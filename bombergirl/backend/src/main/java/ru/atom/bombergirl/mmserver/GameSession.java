package ru.atom.bombergirl.mmserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.bombergirl.communication.message.EndMessage;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.game.model.model.*;
import ru.atom.bombergirl.communication.message.ObjectMessage;
import ru.atom.bombergirl.communication.message.Topic;
import ru.atom.bombergirl.communication.network.Broker;
import ru.atom.bombergirl.communication.util.JsonHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ikozin on 17.04.17.
 */
public class GameSession implements Tickable, Runnable {
    private static final Logger log = LogManager.getLogger(MatchMaker.class);
    private static AtomicLong idGenerator = new AtomicLong();
    private List<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<Point, GameObject> gameField = new ConcurrentHashMap<>();
    //private LinkedHashMap<Point, GameObject> gameObjects = new LinkedHashMap<>();
    private static AtomicInteger counter = new AtomicInteger(0);
    private List<ObjectMessage> objectMessages = new ArrayList<>();
    private long idGame;
    private List<Temporary> dead = new CopyOnWriteArrayList<>();
    private final Connection[] connections;
    private ConcurrentHashMap<Connection, Boolean> survived = new ConcurrentHashMap<>();
    private final long id = idGenerator.get();

    public static final int PLAYERS_IN_GAME = 4;

    public GameSession(Connection[] connections) {
        this.connections = connections;
        for (Connection conn: connections) {
            survived.put(conn, true);
        }
        idGame = idGenerator.getAndIncrement();
    }

    public long getIdGame() {
        return idGame;
    }


    public void initialField() {
        List<Point> spawnPositions = new ArrayList<>();
        //adding walls and woods
        for (int i = 0; i < GameField.field.length; i++) {
            for (int j = 0; j < GameField.field[i].length; j++) {
                Point point = new Point(i * GameField.GRID_SIZE, j * GameField.GRID_SIZE);
                if (GameField.field[i][j] == 0) {
                    continue;
                }
                else if (GameField.field[i][j] == 1) {
                    gameObjects.add(new Wall(point));
                }
                else if (GameField.field[i][j] == 2) {
                    gameObjects.add(new Wood(point));
                }
                else if (GameField.field[i][j] == 3) {
                    spawnPositions.add(point);
                }
                else if (GameField.field[i][j] == 4) {
                    gameObjects.add(new BonusSpeed(point));
                }
                else if (GameField.field[i][j] == 5) {
                    gameObjects.add(new BonusBomb(point));
                }
                else if (GameField.field[i][j] == 6) {
                    gameObjects.add(new BonusFire(point));
                }
            }
        }
        //adding pawns
        for (int i = 0; i < spawnPositions.size(); i++) {
            Pawn pawn = new Pawn(spawnPositions.get(i), this);
            connections[i].setGirl(pawn);
            log.info("set pawn : " + connections[i].getPawn());
            Broker.getInstance().send(connections[i], Topic.POSSESS, pawn.getId());
            gameObjects.add(pawn);
        }
    }



    public static int nextValue() {
        return counter.getAndIncrement();
    }

    public CopyOnWriteArrayList<GameObject> getGameObjects() {
        return new CopyOnWriteArrayList<>(gameObjects);
    }

    public ConcurrentHashMap<Point, GameObject> getGameField() {
        return gameField;
    }


    @Override
    public void tick(long elapsed) {
        objectMessages.clear();
        gameObjects.forEach(x -> objectMessages.add(
                new ObjectMessage(x.getClass().getSimpleName(), x.getId(),
                        ((Positionable)x).getPosition())));
        gameObjects.removeAll(dead);
        gameField.clear();
        gameObjects.forEach(x -> {
            ((Positionable)x).getPosition().getSmallValues();
            gameField.put(((Positionable)x).getPosition().getSmallValues(), x);
        });
        dead.clear();

        EndMessage endGameWin = new EndMessage(1);
        EndMessage endGameLose = new EndMessage(0);

        for (int i = 0; i < connections.length; i++) {
            if (connections[i].getPawn().isDead() && survived.containsKey(connections[i])) {
                survived.remove(connections[i]);
                Broker.getInstance().send(connections[i], Topic.REPLICA, objectMessages);
                Broker.getInstance().send(connections[i], Topic.END_MATCH, endGameLose);
                connections[i].getSession().close();
            }
            if (survived.get(connections[i]) != null && survived.get(connections[i]))
                Broker.getInstance().send(connections[i], Topic.REPLICA, objectMessages);
        }
        if (survived.size() == 1) {
            Broker.getInstance().send(survived.keySet().iterator().next(), Topic.REPLICA, objectMessages);
            Broker.getInstance().send(survived.keySet().iterator().next(), Topic.END_MATCH, endGameWin);
            survived.keySet().iterator().next().getSession().close();
        }

        //List<Point> changePos = new CopyOnWriteArrayList<>();
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }
            if (gameObject instanceof Temporary && ((Temporary) gameObject).isDead()) {
                dead.add((Temporary)gameObject);
                //changePos.add(gameObject.getKey());
                ((Positionable)gameObject).setPosition(new Point(500, 500));
            }
        }
        //changePos.forEach(x -> gameObjects.put(x, ((Positionable)gameObjects.get(x)).setPosition(new Point(500, 500)));
        //gameObjects.removeAll(dead);
    }

    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void run() {
        initialField();

        //adding objects to message
        gameObjects.forEach(x -> objectMessages.add(
                new ObjectMessage(x.getClass().getSimpleName(), x.getId(), ((Positionable)x).getPosition())));
        log.info(JsonHelper.toJson(objectMessages));

        //sending message to all users for rendering
        for (int i = 0; i < connections.length; i++) {
            Broker.getInstance().send(connections[i], Topic.REPLICA, objectMessages);
        }

        //start changing the world by ticker
        Ticker ticker = new Ticker(this);
        ticker.loop();
        log.info(Thread.currentThread().getName() + " started");
    }

    @Override
    public String toString() {
        return "GameSession{" +
                "connections=" + Arrays.toString(connections) +
                ", id=" + id +
                '}';
    }

    public long getId() {
        return id;
    }
}

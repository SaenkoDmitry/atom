package ru.atom.bombergirl.mmserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.game.model.model.*;
import ru.atom.bombergirl.communication.message.ObjectMessage;
import ru.atom.bombergirl.communication.message.Topic;
import ru.atom.bombergirl.communication.network.Broker;
import ru.atom.bombergirl.communication.util.JsonHelper;

import java.util.*;
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
    private static AtomicInteger counter = new AtomicInteger(0);
    private List<ObjectMessage> objectMessages = new ArrayList<>();
    private long idGame;
    List<Temporary> dead = new CopyOnWriteArrayList<>();
    private final Connection[] connections;
    private final long id = idGenerator.get();
    private List<GameObject> gameField = new ArrayList<>();
    //private HashMap <Point, GameObject> objects = new HashMap<>();

    public static final int PLAYERS_IN_GAME = 4;

    public GameSession(Connection[] connections) {
        this.connections = connections;
        idGame = idGenerator.getAndIncrement();
    }

    public long getIdGame() {
        return idGame;
    }


    public void initialField() {

        //adding walls and woods
        for (int i = 0;i < 17;i++) {
            for (int j = 0;j < 13;j++) {
                Point point = new Point(GameField.GRID_SIZE * i, GameField.GRID_SIZE * j);
                if (i == 1 && j == 1
                        || i == 1 && j == 11
                        || i == 15 && j == 1
                        || i == 15 && j == 11
                        || i == 1 && j == 2
                        || i == 2 && j == 1
                        || i == 14 && j == 1
                        || i == 15 && j == 2
                        || i == 1 && j == 10
                        || i == 2 && j == 11
                        || i == 14 && j == 11
                        || i == 15 && j == 10)
                    continue;
                else if (i % 2 == 0 && j % 2 == 0
                        || i == 0
                        || j == 0
                        || i == 16
                        || j == 12) {
                    gameObjects.add(new Wall(point));
                }
                else {
                    gameObjects.add(new Wood(point));
                }
            }
        }

        // adding pawns
        for (int i = 0; i < connections.length; i++) {
            Pawn pawn = new Pawn(spawnPositions.get(i), this);
            connections[i].setGirl(pawn);
            log.info("set pawn : " + connections[i].getPawn());
            Broker.getInstance().send(connections[i], Topic.POSSESS, pawn.getId());
            gameObjects.add(pawn);
        }
    }

    private List<Point> spawnPositions = new ArrayList<>(Arrays.asList(
            new Point(GameField.GRID_SIZE, GameField.GRID_SIZE),
            new Point(GameField.GRID_SIZE, GameField.GRID_SIZE * 11),
            new Point(GameField.GRID_SIZE * 15, GameField.GRID_SIZE),
            new Point(GameField.GRID_SIZE * 15, GameField.GRID_SIZE * 11)
    ));

    public static int nextValue() {
        return counter.getAndIncrement();
    }

    public List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    @Override
    public void tick(long elapsed) {
        objectMessages.clear();
        gameObjects.forEach(x -> objectMessages.add(
                new ObjectMessage(x.getClass().getSimpleName(), x.getId(),
                        ((Positionable)x).getPosition())));
        gameObjects.removeAll(dead);
        dead.clear();

        for (int i = 0; i < connections.length; i++) {
            Broker.getInstance().send(connections[i], Topic.REPLICA, objectMessages);
        }
        //Broker.getInstance().broadcast(Topic.REPLICA,  objectMessages);

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Tickable) {
                ((Tickable) gameObject).tick(elapsed);
            }
            if (gameObject instanceof Temporary && ((Temporary) gameObject).isDead()) {
                dead.add((Temporary)gameObject);
                ((Positionable)gameObject).setPosition(new Point(500, 500));
            }
        }
        //gameObjects.removeAll(dead);
    }

    public void addGameObject(GameObject gameObject) {
        //objects.put(((Positionable)gameObject).getPosition(), gameObject);
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

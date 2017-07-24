package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Collider;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.mmserver.GameSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.abs;

/**
 * Created by dmitriy on 05.03.17.
 */
public class Bomb implements GameObject, Positionable, Temporary, Tickable, Collider {

    private Point position;
    private Pawn pawn;
    private final long lifetime = 3000;
    private long workTime = 0;
    private int power;
    private boolean isDead = false;
    private final int id;
    private static int idBomb = 0;
    private int length = 1;
    private GameSession session;
    private int pawnId;


    private Bomb(GameSession session, int pawnId, int power, Pawn pawn) {
        this.session = session;
        this.pawnId = pawnId;
        this.power = power;
        this.pawn = pawn;
        id = GameSession.nextValue();
        idBomb++;
    }

    public static void create(Point position, GameSession session, int pawnId, int power, Pawn pawn) {
        Bomb thisBomb = new Bomb(session, pawnId, power, pawn);
        thisBomb.setPosition(position);
        session.addGameObject(thisBomb);
    }

    public int getPawnId() {
        return pawnId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void tick(long elapsed) {
        workTime += elapsed;
        if (workTime >= lifetime) {
            destroy();
        }
    }

    @Override
    public long getLifetimeMillis() {
        return lifetime;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    public void destroy() {
        isDead = true;
        pawn.decreaseActiveBombs();
        CopyOnWriteArrayList<GameObject> gameObjects = session.getGameObjects();
        List<Point> toBurn = new ArrayList<>(Arrays.asList(position));
        int x = position.getX();
        int y = position.getY();
        Point[] points = new Point[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j <= power; j++) {
                points[0] = new Point(x + GameField.GRID_SIZE * j, y);
                points[1] = new Point(x - GameField.GRID_SIZE * j, y);
                points[2] = new Point(x, y + GameField.GRID_SIZE * j);
                points[3] = new Point(x, y - GameField.GRID_SIZE * j);
                if (!(0 <= points[i].getSmallValues().getX()
                        && points[i].getSmallValues().getX() <= 16)
                        || !(0 <= points[i].getSmallValues().getY()
                        && points[i].getSmallValues().getY() <= 12)) {
                    break;
                }
                if (GameField.field[points[i].getSmallValues().getX()]
                        [points[i].getSmallValues().getY()] != 1) {
                    //GameObject o = gameObjects.get(points[i].getSmallValues());
//                    if (o != null)
//                        ((Temporary) o).destroy();
                    toBurn.add(new Fire(points[i], session).getPosition());
                    if (session.getGameField().get(points[i].getSmallValues()) instanceof Wood
                            || session.getGameField().get(points[i].getSmallValues()) instanceof Pawn) {
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }
//        for (GameObject o : gameObjects) {
//            if (o instanceof Temporary) {
//                if (!(o instanceof Bomb)) {
//                    if (o instanceof Positionable) {
//                        if ((abs(((Positionable) o).getPosition().getX() - this.position.getX()) <= GameField.GRID_SIZE * 3 / 2// align explosion
//                                && Math.abs(((Positionable) o).getPosition().getY() - this.position.getY()) <= 10)
//                                || (abs(((Positionable) o).getPosition().getY() - this.position.getY()) <= GameField.GRID_SIZE * 3 / 2
//                                && Math.abs(((Positionable) o).getPosition().getX() - this.position.getX()) <= 10)) {
//                            ((Temporary) o).destroy();
//                        }
//                    }
//                }
//            }
//        }
        for (Point p: toBurn) {
            session.addGameObject(new Fire(p, session));
        }
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public boolean isColliding(Collider other) {
        return false; //DUMMY
    }

}

package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Collider;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.mmserver.GameSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by dmitriy on 05.03.17.
 */
public class Bomb implements GameObject, Positionable, Temporary, Tickable, Collider {

    private Point position;
    private final long lifetime = 3000;
    private long workTime = 0;
    private boolean isDead = false;
    private final int id;
    private static int idBomb = 0;
    private int length = 1;
    private GameSession session;
    private int pawnId;

    /*public Bomb(int x, int y) {
        this.position = new Point(x, y);
        id = GameSession.nextValue();
    }*/

    private Bomb(GameSession session, int pawnId) {
        this.session = session;
        this.pawnId = pawnId;
        id = GameSession.nextValue();
        idBomb++;
    }

    public static void create(Point position, GameSession session, int pawnId) {
        Bomb thisBomb = new Bomb(session, pawnId);
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
        List<GameObject> gameObjects = session.getGameObjects();
        List<Point> toBurn = new ArrayList<>(Arrays.asList(position));
        int x = position.getX();
        int y = position.getY();
        Point[] points = new Point[4];
        points[0] = new Point(x + GameField.GRID_SIZE, y);
        points[1] = new Point(x - GameField.GRID_SIZE, y);
        points[2] = new Point(x, y + GameField.GRID_SIZE);
        points[3] = new Point(x, y - GameField.GRID_SIZE);
        Fire[] fire = new Fire[4];
        fire[0] = new Fire(points[0], session);// * i
        fire[1] = new Fire(points[1], session);
        fire[2] = new Fire(points[2], session);
        fire[3] = new Fire(points[3], session);
        for (int i = 0; i < 4; i++) {
            if (GameField.field[points[i].getSmallValues().getX()]
                    [points[i].getSmallValues().getY()] == 0) {
                toBurn.add(fire[i].getPosition());
            }
        }
        for (GameObject o : gameObjects) {
            if (o instanceof Temporary) {
                if (!(o instanceof Bomb)) {
                    if (o instanceof Positionable) {
                        if ((abs(((Positionable) o).getPosition().getX() - this.position.getX()) <= GameField.GRID_SIZE * 3 / 2// align explosion
                                && Math.abs(((Positionable) o).getPosition().getY() - this.position.getY()) <= 10)
                                || (abs(((Positionable) o).getPosition().getY() - this.position.getY()) <= GameField.GRID_SIZE * 3 / 2
                                && Math.abs(((Positionable) o).getPosition().getX() - this.position.getX()) <= 10)) {
                            ((Temporary) o).destroy();
                        }
                    }
                }
            }
        }
        for (Point p: toBurn) {
            session.addGameObject(new Fire(p.getX(), p.getY(), session));
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

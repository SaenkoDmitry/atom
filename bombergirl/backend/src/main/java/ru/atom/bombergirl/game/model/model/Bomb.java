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
            isDead = true;
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
        List<GameObject> gameObjects = session.getGameObjects();
        List<Point> toBurn = new ArrayList<>(Arrays.asList(position));
        int x = position.getX();
        int y = position.getY();
        //session.addGameObject(new Fire(position.getX(), position.getY(), session));
//        for (int i = 1; i <= length; i++) {
            Fire[] fire = new Fire[4];
            fire[0] = new Fire(x + GameField.GRID_SIZE, y, session);// * i
            fire[1] = new Fire(x - GameField.GRID_SIZE, y, session);// * i
            fire[2] = new Fire(x, y + GameField.GRID_SIZE, session);// * i
            fire[3] = new Fire(x, y - GameField.GRID_SIZE, session);// * i
        toBurn.add(fire[0].getPosition());
        toBurn.add(fire[1].getPosition());
        toBurn.add(fire[2].getPosition());
        toBurn.add(fire[3].getPosition());
//            for (int j = 0; j < 4; j++) {
//                int flag = 0;
//                for (GameObject o : gameObjects) {
//                    if (fire[j].isColliding((Collider) o)
//                            && this != o) {
//                        flag = 1;
//                        if (((o instanceof Wood) || (o instanceof Pawn))) {
//                            //((Temporary) o).destroy();
//                            //session.addGameObject(fire[j]);
//                            toBurn.add(fire[j].getPosition());
//                            break;
//                        }
//                    }
//                }
//                if (flag == 0) {
//                    session.addGameObject(fire[j]);
//                }
//            }
//        }
        for (GameObject o : gameObjects) {
            if (o instanceof Temporary) {
                if (!(o instanceof Bomb)) {
                    if (o instanceof Positionable) {
                        if ((abs(((Positionable) o).getPosition().getX() - this.position.getX()) <= GameField.GRID_SIZE * 3 / 2// align explosion
                                && Math.abs(((Positionable) o).getPosition().getY() - this.position.getY()) <= 10)
                                || (abs(((Positionable) o).getPosition().getY() - this.position.getY()) <= GameField.GRID_SIZE * 3 / 2
                                && Math.abs(((Positionable) o).getPosition().getX() - this.position.getX()) <= 10)) {
                            ((Temporary) o).destroy();
                            //toBurn.add(((Positionable)o).getPosition());
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

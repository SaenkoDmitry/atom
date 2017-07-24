package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Collider;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.mmserver.GameSession;

import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.abs;

/**
 * Created by dmitriy on 11.03.17.
 */
public class Fire implements GameObject, Positionable, Temporary, Tickable {

    private Point position;
    private final long lifetime = 300;
    private long workTime = 0;
    private boolean isDead = false;
    private GameSession session;
    private final int id;

    public Fire(int x, int y) {
        this.position = new Point(x, y);
        this.id = GameSession.nextValue();
    }

    public Fire(Point position, GameSession session) {
        this.session = session;
        this.position = position;
        this.id = GameSession.nextValue();
    }

    @Override
    public int getId() {
        return id;
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
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void destroy() {
        CopyOnWriteArrayList<GameObject> gameObjects = session.getGameObjects();
        for (GameObject o : gameObjects) {
            if (o instanceof Temporary) {
                if (!(o instanceof Bomb)
                        && !(o instanceof BonusBomb)
                        && !(o instanceof BonusFire)
                        && !(o instanceof BonusSpeed)
                        && !(o instanceof Fire)) {
                    if (o instanceof Positionable) {
                        if (Math.abs(this.getPosition().getX() - ((Positionable)o).getPosition().getX()) < GameField.GRID_SIZE - 7
                                && Math.abs(this.getPosition().getY() - ((Positionable)o).getPosition().getY()) < GameField.GRID_SIZE - 7) {
                            ((Temporary) o).destroy();
                        }
                    }
                }
            }
        }
        isDead = true;
    }

}

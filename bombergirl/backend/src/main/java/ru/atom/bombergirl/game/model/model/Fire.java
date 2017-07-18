package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Collider;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.mmserver.GameSession;

/**
 * Created by dmitriy on 11.03.17.
 */
public class Fire implements GameObject, Positionable, Temporary, Tickable {

    private Point position;
    private final long lifetime = 500;
    private long workTime = 0;
    private boolean isDead = false;
    private final int id;
    private GameSession session;

    public Fire(int x, int y, GameSession session) {
        this.session = session;
        this.position = new Point(x, y);
        this.id = GameSession.nextValue();
    }

    public Fire(Point position, GameSession session) {
        this.position = position;
        this.session = session;
        this.id = GameSession.nextValue();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void tick(long elapsed) {
        workTime += elapsed;
        if (elapsed >= lifetime) {
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
        isDead = true;
    }

}

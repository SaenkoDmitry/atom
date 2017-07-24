package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Collider;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.mmserver.GameSession;

public class BonusFire implements GameObject, Positionable, Temporary, Collider {

    private Point position;
    private final int id;
    private boolean isDead = false;

    public BonusFire(Point position) {
        this.position = position;
        id = GameSession.nextValue();
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
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public long getLifetimeMillis() {
        return 0;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void destroy() {
        isDead = true;
    }

    @Override
    public void tick(long elapsed) {

    }

    @Override
    public boolean isColliding(Collider other) {
        return false;
    }
}

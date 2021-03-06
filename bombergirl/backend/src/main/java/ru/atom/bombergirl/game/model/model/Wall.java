package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Collider;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.mmserver.GameSession;

/**
 * Created by dmitriy on 05.03.17.
 */
public class Wall implements Block, GameObject, Positionable {

    private Point position;
    private final int id;

    public Wall(int x, int y) {
        this.position = new Point(x, y);
        id = GameSession.nextValue();
    }

    public Wall(Point position) {
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
    public boolean isColliding(Collider other) {
        return false; //DUMMY
    }
}

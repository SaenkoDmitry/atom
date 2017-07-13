package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.mmserver.GameSession;

/**
 * Created by dmitriy on 05.03.17.
 */
public class Bonus implements GameObject, Positionable {

    private Point position;
    private final int id;
    private GameSession session;

    public Bonus(Point position, GameSession session) {
        this.session = session;
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
}

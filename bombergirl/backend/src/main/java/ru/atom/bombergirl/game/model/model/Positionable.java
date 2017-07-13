package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Point;

/**
 * GameObject that has coordinates
 * ^ Y
 * |
 * |
 * |
 * |          X
 * .---------->
 */
public interface Positionable extends GameObject {
    /**
     * @return Current position
     */
    Point getPosition();
    void setPosition(Point position);
}

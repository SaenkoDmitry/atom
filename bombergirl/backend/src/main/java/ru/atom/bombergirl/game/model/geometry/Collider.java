package ru.atom.bombergirl.game.model.geometry;

import ru.atom.bombergirl.game.model.model.Positionable;

/**
 * Entity that can physically intersect, like flame and player
 */
public interface Collider extends Positionable {
    /**
     * @return true if Colliders geometrically intersect
     */
    boolean isColliding(Collider other);
}
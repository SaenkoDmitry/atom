package ru.atom.bombergirl.game.model.model;

import ru.atom.bombergirl.game.model.geometry.Collider;
import ru.atom.bombergirl.game.model.geometry.Point;
import ru.atom.bombergirl.mmserver.GameSession;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by dmitriy on 05.03.17.
 */
public class Pawn implements GameObject, Positionable, Movable, Tickable, Collider, Temporary {

    private boolean isDead = false;
    private Point position;
    private int step = 1;
    private int numberOfBombs = 1;
    private int activeBombs = 0;
    private int powerOfBombs = 1;
    private final int id;
    private boolean toPlantBomb = false;
    private List<Action> actions = new CopyOnWriteArrayList<>();
    Point preChangePosition = position;
    private GameSession session;

    public Pawn(Point p, GameSession s) {
        this.position = new Point(p.getX(), p.getY());
        session = s;
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

    public void plantBomb() {
        if (!toPlantBomb) {
            return;
        }
        activeBombs++;
        Bomb.create(this.position, session, id, powerOfBombs, this);
        toPlantBomb = false;
    }

    public void decreaseActiveBombs() {
        activeBombs--;
    }

    public void makePlantBomb() {
        if (activeBombs < numberOfBombs)
            toPlantBomb = true;
    }

    public void enhanceSpeed() {
        if (step < 5)
            step++;
    }

    public void enhancePowerOfBombs() {
        if (powerOfBombs < 5)
            powerOfBombs++;
    }

    public void enhanceNumberOfBombs() {
        if (numberOfBombs < 5)
            numberOfBombs++;
    }

    @Override
    public Point move(Direction direction) {
        List<GameObject> objects = session
                .getGameObjects()
                .stream()
                .filter(o -> o instanceof Collider)
                .collect(Collectors.toList());
        preChangePosition = position;
        switch (direction) {
            case DOWN:
                position = new Point(position.getX(), position.getY() - step);
                break;
            case UP:
                position = new Point(position.getX(), position.getY() + step);
                break;
            case LEFT:
                position = new Point(position.getX() - step, position.getY());
                break;
            case RIGHT:
                position = new Point(position.getX() + step, position.getY());
                break;
            default:
                break;
        }
        for (GameObject o : objects) {
            if (this.isColliding((Collider) o)
                    && this != o
                    && !(o instanceof Pawn)) {
                if (o instanceof BonusSpeed && !((BonusSpeed) o).isDead()) {
                    enhanceSpeed();
                    ((Temporary)o).destroy();
                    return position;
                }
                else if (o instanceof BonusBomb && !((BonusBomb) o).isDead()) {
                    enhanceNumberOfBombs();
                    ((Temporary)o).destroy();
                    return position;
                }
                else if (o instanceof BonusFire && !((BonusFire) o).isDead()) {
                    enhancePowerOfBombs();
                    ((Temporary)o).destroy();
                    return position;
                }
                return preChangePosition;
            }
        }
        return position;
    }

    public boolean isColliding(Collider c) {
        if (c instanceof Bomb) {
            if (Math.abs(preChangePosition.getX() - c.getPosition().getX()) +
                    Math.abs(preChangePosition.getY() - c.getPosition().getY()) <
                    Math.abs(this.getPosition().getX() - c.getPosition().getX()) +
                            Math.abs(this.getPosition().getY() - c.getPosition().getY())) {
                return false;
            }
        }
        if (Math.abs(this.getPosition().getX() - c.getPosition().getX()) < GameField.GRID_SIZE - 7
                && Math.abs(this.getPosition().getY() - c.getPosition().getY()) < GameField.GRID_SIZE - 7) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void tick(long elapsed) {
//        Action a;
        Iterator<Action> it = actions.iterator();
        /*Map<Direction, Boolean> isDirectionDone = new HashMap<>();
        for (Direction d: Movable.Direction.values()) {
            isDirectionDone.put(d, false);
        }*/
        while (it.hasNext()) {
            it.next().act(this);
        }
        plantBomb();
        actions.clear();
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public long getLifetimeMillis() {
        return 0; //dummy, we don't need to record Pawn's lifetime
    }

    @Override
    public void destroy() {
        isDead = true;
    }
}

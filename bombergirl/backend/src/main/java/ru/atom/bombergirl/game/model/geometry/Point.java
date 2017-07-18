package ru.atom.bombergirl.game.model.geometry;

import ru.atom.bombergirl.game.model.model.GameField;

public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point getSmallValues() {
        return new Point((int)Math.round((double)x / (double)GameField.GRID_SIZE),
                (int)Math.round((double)y / (double)GameField.GRID_SIZE));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "{\n" +
                "    \"x\" : " + getX() + "\n" +
                "    \"y\" : " + getY() + "\n" +
                "  }";
    }
}

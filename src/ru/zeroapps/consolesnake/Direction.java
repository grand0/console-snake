package ru.zeroapps.consolesnake;

public enum Direction {
    UP(0),
    RIGHT(1),
    DOWN(2),
    LEFT(3),
    NONE(Integer.MAX_VALUE);

    int num;

    Direction(int n) {
        num = n;
    }

    public boolean isIllegalMove(Direction dir) {
        return Math.abs(num - dir.num) == 2 || Math.abs(num - dir.num) == 0;
    }
}

package net.lyxodius.lyxGame.main;

public enum Direction {
    UP(0),
    RIGHT(1),
    DOWN(2),
    LEFT(3);

    public final int value;

    Direction(int value) {
        this.value = value;
    }

    public static Direction getByValue(int value) {
        for (Direction direction : values()) {
            if (direction.value == value) {
                return direction;
            }
        }
        return null;
    }

    public Direction getOpposite() {
        if (this == UP) {
            return DOWN;
        } else if (this == RIGHT) {
            return LEFT;
        } else if (this == DOWN) {
            return UP;
        } else if (this == LEFT) {
            return RIGHT;
        }
        return null;
    }
}

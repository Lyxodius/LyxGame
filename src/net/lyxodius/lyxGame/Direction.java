package net.lyxodius.lyxGame;

class Direction {
    static final int UP = 0;
    static final int RIGHT = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;

    static int getOpposite(int direction) {
        if (direction == UP) {
            return DOWN;
        } else if (direction == RIGHT) {
            return LEFT;
        } else if (direction == DOWN) {
            return UP;
        } else if (direction == LEFT) {
            return RIGHT;
        }
        return -1;
    }
}

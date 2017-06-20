package net.lyxodius.lyxGame;

import java.util.Random;

/**
 * Created by Lyxodius on 17.06.2017.
 */
class Timer {
    private final int minimum;
    private final int maximum;
    private final Random random;
    private long lastExecutionFrame;
    private int currentInterval;

    Timer(int minimum, int maximum) {
        this.minimum = minimum;
        currentInterval = minimum;
        this.lastExecutionFrame = LyxGame.getFrame();
        this.maximum = maximum;

        random = new Random();

        calculateCurrentInterval();
    }

    private void calculateCurrentInterval() {
        if (maximum > minimum) {
            currentInterval = minimum + random.nextInt(maximum - minimum);
        }
    }

    boolean toBeExecuted() {
        boolean result = LyxGame.getFrame() - lastExecutionFrame > currentInterval / LyxGame.FRAME_MS;
        if (result) {
            lastExecutionFrame = LyxGame.getFrame();
            calculateCurrentInterval();
        }
        return result;
    }
}

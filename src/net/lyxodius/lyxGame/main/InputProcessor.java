package net.lyxodius.lyxGame.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lyxodius on 19.06.2017.
 */
class InputProcessor implements KeyListener {
    private static final int ENTER = 10;
    private static final int ESCAPE = 27;
    private static final int SPACE = 32;
    private static final int LEFT = 37;
    private static final int UP = 38;
    private static final int RIGHT = 39;
    private static final int DOWN = 40;
    private static final int F1 = 112;

    private final HashMap<Integer, Boolean> keys;
    private final HashMap<Integer, Direction> directions;
    private final LyxGame lyxGame;
    private final ArrayList<Direction> directionQueue;
    private HashMap<Integer, Boolean> oldKeys;
    private Player player;

    InputProcessor(LyxGame lyxGame) {
        this.lyxGame = lyxGame;

        keys = new HashMap<>();
        keys.put(ENTER, false);
        keys.put(ESCAPE, false);
        keys.put(SPACE, false);
        keys.put(LEFT, false);
        keys.put(UP, false);
        keys.put(RIGHT, false);
        keys.put(DOWN, false);
        keys.put(F1, false);

        oldKeys = copy(keys);

        directions = new HashMap<>();
        directions.put(LEFT, Direction.LEFT);
        directions.put(UP, Direction.UP);
        directions.put(RIGHT, Direction.RIGHT);
        directions.put(DOWN, Direction.DOWN);

        directionQueue = new ArrayList<>();
    }

    private HashMap<Integer, Boolean> copy(HashMap<Integer, Boolean> source) {
        HashMap<Integer, Boolean> copy = new HashMap<>();

        for (Map.Entry<Integer, Boolean> key : source.entrySet()) {
            copy.put(key.getKey(), key.getValue());
        }

        return copy;
    }

    void processInput() {
        player = lyxGame.getPlayer();

        if (keys.get(ESCAPE)) {
            System.exit(0);
        }

        if ((keys.get(ENTER) || keys.get(SPACE)) && (!oldKeys.get(ENTER) && !oldKeys.get(SPACE))) {
            if (lyxGame.getMessageBox().isPaused()) {
                if (!lyxGame.getMessageBox().continueMessage()) {
                    lyxGame.movementStopped = false;
                }
            } else if (!lyxGame.movementStopped) {
                player.interact(lyxGame.getMap(), lyxGame.getScriptExecutor());
            }
        }

        if (keys.get(F1) && !oldKeys.get(F1)) {
            lyxGame.toggleFullscreen();
        }

        processDirections();

        oldKeys = copy(keys);
    }

    private void processDirections() {
        for (Map.Entry<Integer, Direction> direction : directions.entrySet()) {
            processDirection(direction);
        }

        if (directionQueue.size() > 0) {
            player.queueMove(directionQueue.get(directionQueue.size() - 1));
        }
    }

    private void processDirection(Map.Entry<Integer, Direction> direction) {
        if (keys.get(direction.getKey())) {
            if (!oldKeys.get(direction.getKey())) {
                directionQueue.add(direction.getValue());
            }
        } else {
            directionQueue.remove(direction.getValue());
            player.stopMove(direction.getValue());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.put(e.getKeyCode(), false);
    }
}

package net.lyxodius.lyxGame;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Entity {
    static final int WIDTH = LyxGame.TILE_SIZE + LyxGame.TILE_SIZE / 2;
    static final int HEIGHT = LyxGame.TILE_SIZE * 2;
    static final int RENDERED_WIDTH = WIDTH * LyxGame.MAGNIFICATION;
    private final ArrayList<Integer> moveQueue;
    private final Random random;
    public Vector3D position;
    public String name;
    public BufferedImage image;
    public String imageName;
    public Behavior behavior;
    public Script onInteract;
    int targetX;
    int targetY;
    int speed;
    private int step;
    private int direction;
    private boolean moving;
    private int xOffset;
    private int yOffset;
    private int pixelsRemaining;

    public Entity(String name, Vector3D position) {
        this(name, position, null);
    }

    Entity(String name, Vector3D position, String imageName) {
        this.name = name;
        this.position = position;
        setImage(imageName);

        moveQueue = new ArrayList<>();

        step = 0;
        direction = Direction.DOWN;
        speed = Speed.MEDIUM;

        random = new Random();

        behavior = Behavior.NOTHING;
    }

    void interact(Map map, ScriptExecutor scriptExecutor) {
        Entity target = null;

        if (direction == Direction.UP) {
            target = map.getEntityAt(position.x, position.y - 1);
        } else if (direction == Direction.RIGHT) {
            target = map.getEntityAt(position.x + 1, position.y);
        } else if (direction == Direction.DOWN) {
            target = map.getEntityAt(position.x, position.y + 1);
        } else if (direction == Direction.LEFT) {
            target = map.getEntityAt(position.x - 1, position.y);
        }

        if (target != null && !target.moving) {
            target.direction = Direction.getOpposite(direction);
            if (target.onInteract != null) {
                scriptExecutor.executeScript(target.onInteract.getScript());
            }
        }
    }

    Vector3D getPosition() {
        return position;
    }

    BufferedImage getImage() {
        return image;
    }

    public void setImage(String name) {
        if (name != null && !name.equals("null")) {
            image = AssetManager.getImageByName(name);
            imageName = name;
        }
    }

    int getStep() {
        return step;
    }

    int getDirection() {
        return direction;
    }

    int getXOffset() {
        return xOffset;
    }

    int getYOffset() {
        return yOffset;
    }

    void queueMove(int direction) {
        if (moveQueue.contains(direction)) {
            moveQueue.remove((Integer) direction);
        }
        moveQueue.add(direction);
    }

    boolean attemptMove(int direction, Map map) {
        boolean result = false;

        targetX = position.x;
        targetY = position.y;

        if (direction == Direction.UP) {
            targetY--;
        } else if (direction == Direction.RIGHT) {
            targetX++;
        } else if (direction == Direction.DOWN) {
            targetY++;
        } else if (direction == Direction.LEFT) {
            targetX--;
        }

        if (!moving) {
            this.direction = direction;
            if (targetX >= 0
                    && targetX < map.getWidth()
                    && targetY >= 0
                    && targetY < map.getHeight()
                    && !map.collisionTiles[targetY][targetX]) {
                result = true;
            } else {
                step = 0;
            }
        }

        for (Entity entity : map.getEntityList()) {
            if (entity.position.x == targetX && entity.position.y == targetY && entity.position.z == position.z) {
                result = false;
                step = 0;
            }
        }

        if (result) {
            position.x = targetX;
            position.y = targetY;
            step++;
            moving = true;
            pixelsRemaining = LyxGame.TILE_SIZE;
        }

        return result;
    }

    private void randomMove(Map map) {
        attemptMove(random.nextInt(4), map);
    }

    void update(Map map) {
        if (behavior == Behavior.RANDOM_MOVEMENT && behavior.timer.toBeExecuted()) {
            randomMove(map);
        }

        if (moveQueue.size() > 0) {
            attemptMove(moveQueue.get(moveQueue.size() - 1), map);
        }

        if (moving) {
            pixelsRemaining -= speed;

            if (pixelsRemaining <= 0) {
                xOffset = 0;
                yOffset = 0;
                moving = false;
                if (moveQueue.size() > 0) {
                    this.attemptMove(moveQueue.get(moveQueue.size() - 1), map);
                } else if (pixelsRemaining < LyxGame.TILE_SIZE / 2 && step % 2 > 0) {
                    step++;
                }
            }

            if (direction == Direction.UP) {
                yOffset = pixelsRemaining * LyxGame.MAGNIFICATION;
            } else if (direction == Direction.RIGHT) {
                xOffset = -1 * pixelsRemaining * LyxGame.MAGNIFICATION;
            } else if (direction == Direction.DOWN) {
                yOffset = -1 * pixelsRemaining * LyxGame.MAGNIFICATION;
            } else if (direction == Direction.LEFT) {
                xOffset = pixelsRemaining * LyxGame.MAGNIFICATION;
            }
        }
    }

    void stopMove(int direction) {
        moveQueue.remove((Integer) direction);
    }

    public String toString() {
        return name;
    }
}

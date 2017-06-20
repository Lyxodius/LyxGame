package net.lyxodius.lyxGame;

class Camera {
    private final Vector2D position;
    private final LyxGame lyxGame;
    private Direction direction;
    private boolean panning;
    private int pixelsMoved;

    Camera(LyxGame lyxGame) {
        this.lyxGame = lyxGame;
        position = new Vector2D(0, 0);
    }

    void attemptPan(Direction direction) {
        if (pixelsMoved == 0 || pixelsMoved == LyxGame.RENDERED_TILE_SIZE) {
            if ((direction == Direction.UP && position.y >= LyxGame.RENDERED_TILE_SIZE)
                    || (direction == Direction.RIGHT && position.x <= getCameraBorderX())
                    || (direction == Direction.DOWN && position.y <= getCameraBorderY())
                    || (direction == Direction.LEFT && position.x >= LyxGame.RENDERED_TILE_SIZE)) {
                pan(direction);
            }
        }
    }

    private void pan(Direction direction) {
        this.direction = direction;
        pixelsMoved = 0;
        panning = true;
    }

    void adjustToPosition(Vector3D position) {
        int targetX = this.position.x / LyxGame.RENDERED_TILE_SIZE + position.x - 8;
        int targetY = this.position.y / LyxGame.RENDERED_TILE_SIZE + position.y - 4;

        int cameraBorderX = getCameraBorderX();
        int cameraBorderY = getCameraBorderY();

        int tileCameraBorderX = cameraBorderX / LyxGame.RENDERED_TILE_SIZE + 1;
        int tileCameraBorderY = cameraBorderY / LyxGame.RENDERED_TILE_SIZE + 1;

        if (targetX > 0 && tileCameraBorderX > 0) {
            if (targetX < tileCameraBorderX) {
                this.position.x = targetX * LyxGame.RENDERED_TILE_SIZE;
            } else {
                this.position.x = cameraBorderX + LyxGame.RENDERED_TILE_SIZE;
            }
        } else {
            this.position.x = 0;
        }

        if (targetY > 0 && tileCameraBorderY > 0) {
            if (targetY < tileCameraBorderY) {
                this.position.y = targetY * LyxGame.RENDERED_TILE_SIZE;
            } else {
                this.position.y = cameraBorderY + LyxGame.RENDERED_TILE_SIZE;
            }
        } else {
            this.position.y = 0;
        }
    }

    private int getCameraBorderX() {
        int mapWidth = lyxGame.map.getWidth();
        return (mapWidth - 17) * LyxGame.RENDERED_TILE_SIZE;
    }

    private int getCameraBorderY() {
        int mapHeight = lyxGame.map.getHeight();
        return (mapHeight - 10) * LyxGame.RENDERED_TILE_SIZE;
    }

    void update() {
        if (panning && pixelsMoved < LyxGame.RENDERED_TILE_SIZE) {
            int pixelsToMove = 2 * LyxGame.MAGNIFICATION;
            if (direction == Direction.UP) {
                position.y -= pixelsToMove;
            } else if (direction == Direction.RIGHT) {
                position.x += pixelsToMove;
            } else if (direction == Direction.DOWN) {
                position.y += pixelsToMove;
            } else if (direction == Direction.LEFT) {
                position.x -= pixelsToMove;
            }
            pixelsMoved += pixelsToMove;
        } else {
            panning = false;
        }
    }

    Vector2D getPosition() {
        return position;
    }
}

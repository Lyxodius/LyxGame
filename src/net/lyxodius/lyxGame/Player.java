package net.lyxodius.lyxGame;

class Player extends Entity {
    private final Camera camera;

    Player(Camera camera) {
        super("hero", null, "hero");
        this.camera = camera;
        this.name = "player";
        speed = Speed.FAST;
    }

    protected boolean attemptMove(int direction, Map map) {
        // If you try to make this more efficient it gets unreadable!
        boolean attemptMove = super.attemptMove(direction, map);
        if (attemptMove) {
            if (direction == Direction.UP) {
                if (targetY * LyxGame.RENDERED_TILE_SIZE < camera.getPosition().y + (4 * LyxGame.RENDERED_TILE_SIZE)) {
                    camera.attemptPan(direction);
                }
            } else if (direction == Direction.RIGHT) {
                if (targetX * LyxGame.RENDERED_TILE_SIZE > camera.getPosition().x + (8 * LyxGame.RENDERED_TILE_SIZE)) {
                    camera.attemptPan(direction);
                }
            } else if (direction == Direction.DOWN) {
                if (targetY * LyxGame.RENDERED_TILE_SIZE > camera.getPosition().y + (4 * LyxGame.RENDERED_TILE_SIZE)) {
                    camera.attemptPan(direction);
                }
            } else if (direction == Direction.LEFT) {
                if (targetX * LyxGame.RENDERED_TILE_SIZE < camera.getPosition().x + (6 * LyxGame.RENDERED_TILE_SIZE)) {
                    camera.attemptPan(direction);
                }
            }
        }

        return attemptMove;
    }
}

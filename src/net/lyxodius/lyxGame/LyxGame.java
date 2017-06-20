package net.lyxodius.lyxGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LyxGame extends JFrame implements Runnable {
    static final int TILE_SIZE = 16;
    static final int MAGNIFICATION = 5;
    static final int RENDERED_TILE_SIZE = TILE_SIZE * MAGNIFICATION;

    static final int FRAME_MS = 16;
    private static final boolean RENDER_FPS = false;
    static ScriptExecutor scriptExecutor;
    private static int frame;
    private final Player player;
    private final ArrayList<BufferedImage> tileSets;
    private final Camera camera;
    private final boolean running;
    private final InputProcessor inputProcessor;
    Map map;
    private int averageFps;
    private long lastLoopTime;
    private long lastFpsUpdate;
    private ArrayList<Integer> fpsCollection;

    private LyxGame() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        camera = new Camera(this);
        tileSets = loadTileSets();

        player = new Player(camera);
        teleportPlayer("0", 2, 2);

        setSize(256 * MAGNIFICATION, 144 * MAGNIFICATION);
        setUndecorated(true);
        setResizable(false);
        setVisible(true);

        lastLoopTime = System.currentTimeMillis();
        lastFpsUpdate = System.currentTimeMillis();

        fpsCollection = new ArrayList<>();

        scriptExecutor = new ScriptExecutor(this);
        inputProcessor = new InputProcessor(this);
        addKeyListener(inputProcessor);

        running = true;
        new Thread(this).start();
    }

    public static void main(String args[]) {
        new LyxGame();
    }

    public static ArrayList<BufferedImage> loadTileSets() {
        ArrayList<BufferedImage> tileSets = new ArrayList<>();

        final File[] files = new File("img/tile").listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    tileSets.add(ImageIO.read(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return tileSets;
    }

    static int getFrame() {
        return frame;
    }

    void teleportPlayer(String name, int x, int y) {
        this.map = Map.readFromFile(name);
        map.getEntityList().add(player);
        player.position = new Vector3D(x, y, 1);
        camera.adjustToPosition(player.position);
    }

    private void render(Graphics2D graphics2D) {
        graphics2D.fillRect(0, 0, getWidth(), getHeight());

        for (int z = 0; z < 3; z++) {
            if (z == 2) {
                map.getEntityList().sort(new EntityComparator());
                for (Entity entity : map.getEntityList()) {
                    renderEntity(entity, graphics2D);
                }
            }
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    int value = map.tiles[z][y][x];

                    int tileSetId = value / 81;
                    int tileY = value % 81 / 9;
                    int tileX = value % 81 % 9;

                    graphics2D.drawImage(tileSets.get(tileSetId),
                            x * RENDERED_TILE_SIZE - camera.getPosition().x,
                            y * RENDERED_TILE_SIZE - camera.getPosition().y,
                            (x + 1) * RENDERED_TILE_SIZE - camera.getPosition().x,
                            (y + 1) * RENDERED_TILE_SIZE - camera.getPosition().y,
                            tileX * TILE_SIZE,
                            tileY * TILE_SIZE,
                            (tileX + 1) * TILE_SIZE,
                            (tileY + 1) * TILE_SIZE, null);
                }
            }
        }

        if (RENDER_FPS) {
            graphics2D.setColor(Color.RED);
            graphics2D.setFont(new Font("Courier New", Font.PLAIN, 72));

            long difference = System.currentTimeMillis() - lastFpsUpdate;
            fpsCollection.add((int) (1000 / (System.currentTimeMillis() - lastLoopTime)));

            if (difference >= 1000) {
                int fpsSum = 0;

                for (int fps : fpsCollection) {
                    fpsSum += fps;
                }

                averageFps = fpsSum / fpsCollection.size();
                fpsCollection = new ArrayList<>();
                lastFpsUpdate = System.currentTimeMillis();
            }

            graphics2D.drawString("FPS: " + averageFps, 20, 60);

            lastLoopTime = System.currentTimeMillis();
        }
    }

    private void renderEntity(Entity entity, Graphics2D graphics2D) {
        if (entity.getImage() != null) {
            int stepOffset = Entity.WIDTH;
            if (entity.getStep() % 4 == 1) {
                stepOffset = 0;
            } else if (entity.getStep() % 4 == 3) {
                stepOffset = 2 * Entity.WIDTH;
            }

            int directionOffset = entity.direction.value * LyxGame.TILE_SIZE * 2;

            int dx1 = entity.getPosition().x * LyxGame.RENDERED_TILE_SIZE - Entity.RENDERED_WIDTH / 6 + entity.getXOffset() - camera.getPosition().x;
            int dy1 = (entity.getPosition().y - 1) * LyxGame.RENDERED_TILE_SIZE + entity.getYOffset() - camera.getPosition().y;
            int dx2 = (entity.getPosition().x + 1) * LyxGame.RENDERED_TILE_SIZE + Entity.RENDERED_WIDTH / 6 + entity.getXOffset() - camera.getPosition().x;
            int dy2 = (entity.getPosition().y + 1) * LyxGame.RENDERED_TILE_SIZE + entity.getYOffset() - camera.getPosition().y;
            int sx1 = stepOffset;
            int sx2 = stepOffset + Entity.WIDTH;
            int sy2 = directionOffset + Entity.HEIGHT;

            graphics2D.drawImage(entity.getImage(), dx1, dy1, dx2, dy2, sx1, directionOffset, sx2, sy2, null);
        }
    }

    @Override
    public void run() {
        createBufferStrategy(2);
        BufferStrategy myStrategy = getBufferStrategy();

        while (running) {
            inputProcessor.processInput();
            for (Entity entity : map.getEntityList()) {
                entity.update(map);
            }
            camera.update();
            Graphics g = myStrategy.getDrawGraphics();
            try {
                render((Graphics2D) g);
            } finally {
                g.dispose();
            }
            myStrategy.show();
            try {
                Thread.sleep(FRAME_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            frame++;
        }
    }

    Player getPlayer() {
        return player;
    }

    public Map getMap() {
        return map;
    }

    ScriptExecutor getScriptExecutor() {
        return scriptExecutor;
    }
}

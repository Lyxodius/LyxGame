package net.lyxodius.lyxGameEditor;

import net.lyxodius.lyxGame.Entity;
import net.lyxodius.lyxGame.Vector2D;
import net.lyxodius.lyxGame.Vector3D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class MapDialog extends JDialog implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    static final int WIDTH = 16 * LyxGameEditor.RENDERED_TILE_SIZE;
    private static final int HEIGHT = 9 * LyxGameEditor.RENDERED_TILE_SIZE;
    private final boolean running;
    private final ArrayList<BufferedImage> tileSets;
    private final TileDialog tileDialog;
    private final EditorFrame editorFrame;
    private BufferedImage collisionsImage;
    private boolean mouseIsPressed;
    private boolean collisionToDraw;
    private Vector2D camera;
    private Vector2D oldCamera;
    private Vector2D mouseStart;
    private int mouseButton;

    MapDialog(EditorFrame editorFrame, ArrayList<BufferedImage> tileSets) {
        super(editorFrame);

        setSize(WIDTH, HEIGHT);
        setResizable(false);

        try {
            collisionsImage = ImageIO.read(new File("img/editor/collisions.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.editorFrame = editorFrame;
        this.tileSets = tileSets;
        this.tileDialog = new TileDialog(tileSets, editorFrame);

        setUndecorated(true);
        setVisible(true);

        running = true;
        new Thread(this).start();

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        camera = new Vector2D(0, 0);
    }

    @Override
    public void run() {
        createBufferStrategy(2);
        BufferStrategy myStrategy = getBufferStrategy();

        while (running) {
            Graphics g = myStrategy.getDrawGraphics();
            try {
                render(g);
            } finally {
                g.dispose();
            }
            try {
                myStrategy.show();
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this,
                        "Buffers have not been created. Please restart the application.");
                System.exit(1);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(-1 * camera.x, -1 * camera.y,
                editorFrame.getCurrentMap().getWidth() * LyxGameEditor.RENDERED_TILE_SIZE,
                editorFrame.getCurrentMap().getHeight() * LyxGameEditor.RENDERED_TILE_SIZE);

        for (int z = 0; z < 4; z++) {
            if (z == 3) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
            } else if (z > editorFrame.getSelectedLayer()) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            for (int y = 0; y < editorFrame.getCurrentMap().getHeight(); y++) {
                for (int x = 0; x < editorFrame.getCurrentMap().getWidth(); x++) {
                    if (z < 3) {
                        renderVisibleTile(g2d, z, y, x);
                    } else if (editorFrame.getSelectedLayer() == 3) {
                        renderCollisionTile(g2d, y, x);
                    }
                }
            }
        }

        if (editorFrame.getSelectedLayer() == 4) {
            for (Entity entity : editorFrame.getCurrentMap().getEntityList()) {
                renderEntity(g2d, entity);
            }
        }
    }

    private void renderEntity(Graphics2D g2d, Entity entity) {
        int x = entity.position.x * LyxGameEditor.RENDERED_TILE_SIZE - camera.x;
        int y = entity.position.y * LyxGameEditor.RENDERED_TILE_SIZE - camera.y;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, LyxGameEditor.RENDERED_TILE_SIZE, LyxGameEditor.RENDERED_TILE_SIZE);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y, LyxGameEditor.RENDERED_TILE_SIZE, LyxGameEditor.RENDERED_TILE_SIZE);

        Rectangle rectangle = new Rectangle(x, y, LyxGameEditor.RENDERED_TILE_SIZE, LyxGameEditor.RENDERED_TILE_SIZE);
        Font font = new Font(g2d.getFont().getName(), Font.PLAIN, 11 + LyxGameEditor.MAGNIFICATION);
        drawCenteredString(g2d, entity.name, rectangle, font);
    }

    private void drawCenteredString(Graphics g, String text, Rectangle rectangle, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);

        int x = rectangle.x + (rectangle.width - metrics.stringWidth(text)) / 2;
        int y = rectangle.y + ((rectangle.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setFont(font);
        g.drawString(text, x, y);
    }

    private void renderVisibleTile(Graphics2D g2d, int z, int y, int x) {
        int value = 0;
        try {
            value = editorFrame.getCurrentMap().tiles[z][y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(
                    "Could not get value for (" + z + "|" + y + "|" + x + ") of current map. Did the size change?");
        }

        int tileSetId = value / 81;
        int tileY = value % 81 / 9;
        int tileX = value % 81 % 9;

        renderTile(g2d, tileSets.get(tileSetId), x, y,
                tileX * LyxGameEditor.TILE_SIZE,
                tileY * LyxGameEditor.TILE_SIZE,
                (tileX + 1) * LyxGameEditor.TILE_SIZE,
                (tileY + 1) * LyxGameEditor.TILE_SIZE);
    }

    private void renderCollisionTile(Graphics2D g2d, int y, int x) {
        int offset = 0;

        if (editorFrame.getCurrentMap().collisionTiles[y][x]) {
            offset = LyxGameEditor.TILE_SIZE;
        }

        renderTile(g2d, collisionsImage, x, y,
                offset,
                0,
                offset + LyxGameEditor.TILE_SIZE,
                LyxGameEditor.TILE_SIZE);
    }

    private void renderTile(Graphics2D g2d, BufferedImage image, int x, int y, int sx1, int sy1, int sx2, int sy2) {
        g2d.drawImage(image,
                x * LyxGameEditor.RENDERED_TILE_SIZE - camera.x,
                y * LyxGameEditor.RENDERED_TILE_SIZE - camera.y,
                (x + 1) * LyxGameEditor.RENDERED_TILE_SIZE - camera.x,
                (y + 1) * LyxGameEditor.RENDERED_TILE_SIZE - camera.y,
                sx1, sy1, sx2, sy2, this);
    }

    private void handleMouseClick(MouseEvent e) {
        if (mouseButton == MouseEvent.BUTTON1) {
            drawTile(e);
        } else if (mouseButton == MouseEvent.BUTTON3) {
            moveCamera(e);
        }
    }

    private void drawTile(MouseEvent e) {
        int x = (e.getX() + camera.x) / LyxGameEditor.RENDERED_TILE_SIZE;
        int y = (e.getY() + camera.y) / LyxGameEditor.RENDERED_TILE_SIZE;
        int z = editorFrame.getSelectedLayer();

        if (y >= 0 && y < editorFrame.getCurrentMap().getHeight()
                && x >= 0 && x < editorFrame.getCurrentMap().getWidth()) {
            if (z < 3) {
                editorFrame.getCurrentMap().tiles[z][y][x] = tileDialog.getTileId();
            } else if (z == 3) {
                if (!mouseIsPressed) {
                    collisionToDraw = !editorFrame.getCurrentMap().collisionTiles[y][x];
                }
                mouseIsPressed = true;
                editorFrame.getCurrentMap().collisionTiles[y][x] = collisionToDraw;
            } else if (z == 4) {
                Entity entity = editorFrame.getCurrentMap().getEntityAt(x, y);
                if (entity == null) {
                    int count = 0;
                    while (editorFrame.getCurrentMap().getEntity(Integer.toString(count)) != null) {
                        count++;
                    }
                    entity = new Entity(Integer.toString(count), new Vector3D(x, y, 0));
                    editorFrame.getCurrentMap().getEntityList().add(entity);
                }
                new EntityDialog(editorFrame, entity);
            }
        }
    }

    private void moveCamera(MouseEvent e) {
        camera = new Vector2D(oldCamera.x + (mouseStart.x - e.getX()),
                oldCamera.y + (mouseStart.y - e.getY()));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 27) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseButton = e.getButton();
        oldCamera = camera;
        mouseStart = new Vector2D(e.getX(), e.getY());
        handleMouseClick(e);
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        mouseIsPressed = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseClick(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    TileDialog getTileDialog() {
        return tileDialog;
    }
}

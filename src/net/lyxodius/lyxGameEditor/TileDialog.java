package net.lyxodius.lyxGameEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class TileDialog extends JDialog implements Runnable, MouseListener, MouseMotionListener {
    private static final int WIDTH = 9 * LyxGameEditor.RENDERED_TILE_SIZE;
    private static final int HEIGHT = 9 * LyxGameEditor.RENDERED_TILE_SIZE;
    private final EditorFrame editorFrame;
    private final ArrayList<BufferedImage> tileSets;
    private final boolean running;
    private int tileId;

    TileDialog(ArrayList<BufferedImage> tileSets, EditorFrame editorFrame) {
        super(editorFrame);

        setSize(WIDTH, HEIGHT);
        setResizable(false);

        this.editorFrame = editorFrame;
        this.tileSets = tileSets;

        setUndecorated(true);
        setLocation(MapDialog.WIDTH + 5, 0);

        addMouseListener(this);
        addMouseMotionListener(this);

        setVisible(true);

        running = true;
        new Thread(this).start();
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
            myStrategy.show();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.drawImage(tileSets.get(editorFrame.getCurrentTileSetId()), 0, 0, WIDTH, HEIGHT, this);

        g2d.setColor(Color.CYAN);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        g2d.fillRect(tileId % 9 * LyxGameEditor.RENDERED_TILE_SIZE,
                tileId / 9 * LyxGameEditor.RENDERED_TILE_SIZE
                        - editorFrame.getCurrentTileSetId() * 9 * LyxGameEditor.RENDERED_TILE_SIZE,
                LyxGameEditor.RENDERED_TILE_SIZE,
                LyxGameEditor.RENDERED_TILE_SIZE);
    }

    private void selectTile(MouseEvent e) {
        int x = e.getX() / LyxGameEditor.RENDERED_TILE_SIZE;
        int y = e.getY() / LyxGameEditor.RENDERED_TILE_SIZE;

        if (y >= 0 && y < 9 && x >= 0 && x < 9) {
            tileId = editorFrame.getCurrentTileSetId() * 81 + y * 9 + x;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        selectTile(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        selectTile(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    int getTileId() {
        return tileId;
    }
}

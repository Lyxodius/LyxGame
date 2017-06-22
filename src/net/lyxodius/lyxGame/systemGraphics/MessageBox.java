package net.lyxodius.lyxGame.systemGraphics;

import net.lyxodius.lyxGame.main.AssetManager;
import net.lyxodius.lyxGame.main.LyxGame;
import net.lyxodius.lyxGame.main.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Lyxodius on 20.06.2017.
 */
public class MessageBox extends SystemGraphic {

    private final BufferedImage boxImage;
    private final BufferedImage gradientImage;
    private final BufferedImage font;

    private ArrayList<CharLine> charLines;

    private int currentLine;
    private boolean paused;
    private boolean visible;
    private int movedPixels;
    private long lastPauseBlink;
    private boolean showingPause;
    private boolean movingUp;

    public MessageBox() {
        super(new Vector2D(0, 440));

        boxImage = AssetManager.getImageByPath("system/box");
        gradientImage = AssetManager.getImageByPath("system/gradient");
        font = AssetManager.getImageByPath("system/font");
    }

    public void showMessage(String message) {
        visible = true;

        charLines = new ArrayList<>();

        String[] words = message.split(" ");
        StringBuilder sb = new StringBuilder();
        int currentLine = 0;

        for (String word : words) {
            if (sb.length() + word.length() < 40) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(word);
            } else {
                charLines.add(new CharLine(new Vector2D(position.x + 40, position.y + 40 + currentLine * 55), sb.toString(), font));
                sb = new StringBuilder();
                currentLine++;
                sb.append(word);
            }
        }
        charLines.add(new CharLine(new Vector2D(position.x + 40, position.y + 40 + currentLine * 55), sb.toString(), font));

        CharLine charLine = charLines.get(0);
        charLine.opacity = 1;
        charLine.start();
    }

    public void update() {
        if (!visible) {
            return;
        }

        for (CharLine charLine : charLines) {
            charLine.update();
        }

        if (!movingUp && !paused) {
            if (charLines.get(currentLine).isComplete()) {
                nextLine();
            }
        }

        if (movingUp) {
            if (movedPixels < 11) {
                for (CharLine charLine : charLines) {
                    charLine.moveUp();
                }
                movedPixels++;
            } else {
                movingUp = false;
                movedPixels = 0;
            }
        }

        if (paused && LyxGame.getFrame() - lastPauseBlink > 10) {
            showingPause = !showingPause;
            lastPauseBlink = LyxGame.getFrame();
        }
    }

    private void nextLine() {
        nextLine(false);
    }

    private void nextLine(boolean override) {
        if ((currentLine + 1) % 4 == 0 && !override) {
            paused = true;
        } else {
            if (currentLine < charLines.size() - 1) {
                currentLine++;

                if (currentLine > 3) {
                    movingUp = true;
                    charLines.get(currentLine).fadeIn();
                    charLines.get(currentLine - 4).fadeOut();
                } else {
                    charLines.get(currentLine).opacity = 1;
                }

                charLines.get(currentLine).start();
            } else {
                paused = true;
            }
        }
    }

    public void draw(Graphics2D graphics2D) {
        if (!visible) {
            return;
        }

        int BORDER_WIDTH = 8;
        final int RENDERED_BORDER_WIDTH = BORDER_WIDTH * LyxGame.MAGNIFICATION;
        int HEIGHT = 56;
        final int RENDERED_HEIGHT = HEIGHT * LyxGame.MAGNIFICATION;
        int WIDTH = 256;
        final int RENDERED_WIDTH = WIDTH * LyxGame.MAGNIFICATION;
        int LINE_LENGTH = 16;
        final int RENDERED_LINE_LENGTH = LINE_LENGTH * LyxGame.MAGNIFICATION;
        int SHORTENED_LINE_LENGTH = 14;
        final int RENDERED_SHORTENED_LINE_LENGTH = SHORTENED_LINE_LENGTH * LyxGame.MAGNIFICATION;
        final int RENDERED_PAUSE_WIDTH = 16 * LyxGame.MAGNIFICATION;
        final int RENDERED_PAUSE_HEIGHT = 8 * LyxGame.MAGNIFICATION;

        // Gradient
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        graphics2D.drawImage(gradientImage,
                position.x, position.y,
                position.x + RENDERED_WIDTH, position.y + RENDERED_HEIGHT,
                0, 0, 32, 32, null);
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        // Top line
        for (int i = 0; i < 15; i++) {
            graphics2D.drawImage(boxImage,
                    position.x + RENDERED_BORDER_WIDTH + i * RENDERED_LINE_LENGTH,
                    position.y,
                    position.x + RENDERED_BORDER_WIDTH + (i + 1) * RENDERED_LINE_LENGTH,
                    position.y + RENDERED_BORDER_WIDTH,
                    8, 0, 24, 8, null);
        }

        // Left line
        for (int i = 0; i < 3; i++) {
            graphics2D.drawImage(boxImage,
                    position.x,
                    position.y + RENDERED_BORDER_WIDTH + i * RENDERED_SHORTENED_LINE_LENGTH - 5,
                    position.x + RENDERED_BORDER_WIDTH,
                    position.y + RENDERED_BORDER_WIDTH + (i + 1) * RENDERED_SHORTENED_LINE_LENGTH - 5,
                    0, 9, 8, 23, null);
        }

        // Right line
        for (int i = 0; i < 3; i++) {
            graphics2D.drawImage(boxImage,
                    RENDERED_WIDTH - RENDERED_BORDER_WIDTH,
                    position.y + RENDERED_BORDER_WIDTH + i * RENDERED_SHORTENED_LINE_LENGTH - 5,
                    RENDERED_WIDTH,
                    position.y + RENDERED_BORDER_WIDTH + (i + 1) * RENDERED_SHORTENED_LINE_LENGTH - 5,
                    24, 9, 32, 23, null);
        }

        // Bottom line
        for (int i = 0; i < 15; i++) {
            graphics2D.drawImage(boxImage,
                    position.x + RENDERED_BORDER_WIDTH + i * RENDERED_LINE_LENGTH,
                    position.y + RENDERED_HEIGHT - RENDERED_BORDER_WIDTH,
                    position.x + RENDERED_BORDER_WIDTH + (i + 1) * RENDERED_LINE_LENGTH,
                    position.y + RENDERED_HEIGHT,
                    8, 24, 24, 32, null);
        }

        // Top-left corner
        graphics2D.drawImage(boxImage,
                position.x, position.y,
                position.x + RENDERED_BORDER_WIDTH, position.y + RENDERED_BORDER_WIDTH,
                0, 0, 8, 8, null);

        // Top-right-corner
        graphics2D.drawImage(boxImage,
                RENDERED_WIDTH - RENDERED_BORDER_WIDTH, position.y,
                RENDERED_WIDTH, position.y + RENDERED_BORDER_WIDTH,
                24, 0, 32, 8, null);

        // Bottom-left corner
        graphics2D.drawImage(boxImage,
                position.x, position.y + RENDERED_HEIGHT - RENDERED_BORDER_WIDTH,
                position.x + RENDERED_BORDER_WIDTH, position.y + RENDERED_HEIGHT,
                0, 24, 8, 32, null);

        // Bottom-right corner
        graphics2D.drawImage(boxImage,
                RENDERED_WIDTH - RENDERED_BORDER_WIDTH, position.y + RENDERED_HEIGHT - RENDERED_BORDER_WIDTH,
                RENDERED_WIDTH, position.y + RENDERED_HEIGHT,
                24, 24, 32, 32, null);

        for (CharLine charLine : charLines) {
            charLine.draw(graphics2D);
        }

        if (showingPause) {
            graphics2D.drawImage(boxImage,
                    position.x + RENDERED_WIDTH / 2 - RENDERED_BORDER_WIDTH / 2,
                    position.y + RENDERED_HEIGHT - RENDERED_PAUSE_HEIGHT,
                    position.x + RENDERED_WIDTH / 2 + RENDERED_PAUSE_WIDTH / 2,
                    position.y + RENDERED_HEIGHT,
                    BORDER_WIDTH,
                    BORDER_WIDTH * 2,
                    BORDER_WIDTH + LINE_LENGTH,
                    BORDER_WIDTH * 3,
                    null);
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean continueMessage() {
        paused = false;
        showingPause = false;
        if (currentLine < charLines.size() - 1) {
            movingUp = true;
            nextLine(true);
            return true;
        } else {
            visible = false;
            currentLine = 0;
            return false;
        }
    }
}

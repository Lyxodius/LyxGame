package net.lyxodius.lyxGame.systemGraphics;

import net.lyxodius.lyxGame.main.LyxGame;
import net.lyxodius.lyxGame.main.SfxPlayer;
import net.lyxodius.lyxGame.main.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Lyxodius on 20.06.2017.
 */
public class CharLine extends SystemGraphic {

    private final Vector2D position;
    private final ArrayList<CharGraphic> charGraphics;
    private final String line;
    private final BufferedImage font;
    float opacity;
    private int currentChar;
    private boolean running;
    private boolean complete;
    private boolean fadingIn;
    private boolean fadingOut;
    private long lastSound;

    CharLine(Vector2D position, String line, BufferedImage font) {
        super(position);

        this.position = position;
        this.line = line;
        this.font = font;

        this.charGraphics = new ArrayList<>();
    }

    @Override
    public void update() {
        if (running) {
            if (currentChar < line.length()) {
                charGraphics.add(new CharGraphic(line.charAt(currentChar),
                        new Vector2D(position.x + currentChar * CharGraphic.WIDTH * LyxGame.MAGNIFICATION, position.y),
                        font));
                currentChar++;

                if (LyxGame.getFrame() - lastSound > 3) {
                    SfxPlayer.playSound("Cursor1");
                    lastSound = LyxGame.getFrame();
                }
            } else {
                complete = true;
                running = false;
            }
        }

        if (fadingIn) {
            opacity += 0.1f;
            if (opacity > 1) {
                opacity = 1;
                fadingIn = false;
            }

        } else if (fadingOut) {
            opacity -= 0.1f;
            if (opacity < 0) {
                opacity = 0;
                fadingOut = false;
            }
        }
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        for (CharGraphic characterGraphic : charGraphics) {
            characterGraphic.draw(graphics2D);
        }
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    boolean isComplete() {
        return complete;
    }

    void start() {
        if (!isComplete()) {
            running = true;
        }
    }

    void moveUp() {
        position.y -= LyxGame.MAGNIFICATION;
        for (CharGraphic charGraphic : charGraphics) {
            charGraphic.position.y -= LyxGame.MAGNIFICATION;
        }
    }

    void fadeIn() {
        fadingIn = true;
    }

    void fadeOut() {
        fadingOut = true;
    }
}

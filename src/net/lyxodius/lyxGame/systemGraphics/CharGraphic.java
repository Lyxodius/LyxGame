package net.lyxodius.lyxGame.systemGraphics;

import net.lyxodius.lyxGame.main.LyxGame;
import net.lyxodius.lyxGame.main.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Lyxodius on 20.06.2017.
 */
public class CharGraphic extends SystemGraphic {
    static final int WIDTH = 6;
    private static final int HEIGHT = 8;

    private static final int ROW_WIDTH = 32;

    private final int sx1;
    private final int sy1;
    private final int sx2;
    private final int sy2;

    private final BufferedImage font;

    CharGraphic(char c, Vector2D position, BufferedImage font) {
        super(position);

        int charId = LyxChar.getId(c);

        this.position = position;
        this.font = font;

        if (charId >= 0 && charId < ROW_WIDTH) {
            this.sx1 = WIDTH * (charId);
            this.sy1 = 0;
        } else if (charId >= ROW_WIDTH && charId < ROW_WIDTH * 2) {
            this.sx1 = WIDTH * (charId - ROW_WIDTH);
            this.sy1 = HEIGHT;
        } else if (charId >= ROW_WIDTH * 2 && charId < ROW_WIDTH * 3) {
            this.sx1 = WIDTH * (charId - ROW_WIDTH * 2);
            this.sy1 = HEIGHT * 2;
        } else {
            this.sx1 = WIDTH * 31;
            this.sy1 = HEIGHT;
        }

        sx2 = sx1 + WIDTH;
        sy2 = sy1 + HEIGHT;
    }

    @Override
    public void update() {

    }

    public void draw(Graphics2D graphics2D) {
        int dx2 = position.x + WIDTH * LyxGame.MAGNIFICATION;
        int dy2 = position.y + HEIGHT * LyxGame.MAGNIFICATION;

        graphics2D.drawImage(font,
                position.x, position.y,
                dx2, dy2,
                sx1, sy1,
                sx2, sy2,
                null);
    }
}

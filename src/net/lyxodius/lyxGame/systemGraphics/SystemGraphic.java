package net.lyxodius.lyxGame.systemGraphics;

import net.lyxodius.lyxGame.main.Vector2D;

import java.awt.*;

/**
 * Created by Lyxodius on 21.06.2017.
 */
public abstract class SystemGraphic {
    Vector2D position;

    SystemGraphic(Vector2D position) {
        this.position = position;
    }

    public abstract void update();

    public abstract void draw(Graphics2D graphics2D);
}

package net.lyxodius.lyxGame.main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AssetManager {
    private static final HashMap<String, BufferedImage> IMAGES = new HashMap<>();

    public static BufferedImage getImageByPath(String path) {
        BufferedImage image = null;

        if (path != null) {
            if (IMAGES.containsKey(path)) {
                image = IMAGES.get(path);
            } else {
                File file = new File("img/" + path + ".png");
                if (file.exists()) {
                    try {
                        image = ImageIO.read(file);
                        IMAGES.put(path, image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("File '" + file.getPath() + "' does not exist.");
                }
            }
        }

        return image;
    }
}

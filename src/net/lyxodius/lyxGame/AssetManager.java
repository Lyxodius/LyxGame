package net.lyxodius.lyxGame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

class AssetManager {
    private static final HashMap<String, BufferedImage> IMAGES = new HashMap<>();

    static BufferedImage getImageByName(String name) {
        BufferedImage image = null;

        if (name != null) {
            if (IMAGES.containsKey(name)) {
                image = IMAGES.get(name);
            } else {
                File file = new File("img/entity/" + name + ".png");
                if (file.exists()) {
                    try {
                        image = ImageIO.read(file);
                        IMAGES.put(name, image);
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

package net.lyxodius.lyxGame.main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Lyxodius on 22.06.2017.
 */
public class SfxPlayer implements Runnable {
    private final Clip clip;

    private SfxPlayer(Clip clip) {
        this.clip = clip;
        new Thread(this).start();
    }

    public static void playSound(String name) {
        try {
            File file = new File("sfx/" + name + ".wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            new SfxPlayer(clip);

            FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            floatControl.setValue(-15.0f);

        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        clip.start();
    }
}

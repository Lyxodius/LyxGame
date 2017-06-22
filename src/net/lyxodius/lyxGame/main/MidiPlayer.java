package net.lyxodius.lyxGame.main;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Lyxodius on 24.05.2017.
 */
class MidiPlayer implements Runnable {
    private Sequencer sequencer;

    MidiPlayer() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    void playMidi(String name) {
        if (name == null || name.isEmpty() || name.equals("null")) {
            return;
        }

        File file = new File("bgm/" + name + ".mid");
        try (FileInputStream fis = new FileInputStream(file)) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                try {
                    sequencer.setSequence(bis);
                } catch (IOException | InvalidMidiDataException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sequencer.addMetaEventListener(meta -> {
            if (meta.getType() == 0x2F) {
                sequencer.setTickPosition(0);
                sequencer.start();
            }
        });

        new Thread(this).start();
    }

    void stopMidi() {
        sequencer.stop();
    }

    @Override
    public void run() {
        sequencer.start();
    }
}

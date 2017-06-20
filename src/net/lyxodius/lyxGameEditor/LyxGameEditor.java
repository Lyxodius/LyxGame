package net.lyxodius.lyxGameEditor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class LyxGameEditor {
    static final int TILE_SIZE = 16;
    static int MAGNIFICATION = 5;
    static int RENDERED_TILE_SIZE;

    private LyxGameEditor() {
        mkDir("editor");
        mkDir("map");

        double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        if (screenHeight <= 720) {
            MAGNIFICATION = 2;
        } else if (screenHeight <= 1080) {
            MAGNIFICATION = 3;
        }

        RENDERED_TILE_SIZE = TILE_SIZE * MAGNIFICATION;

        new EditorFrame();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException
                | ClassNotFoundException
                | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
        }

        new LyxGameEditor();
    }

    static void openExplorer(String path) {
        try {
            Runtime.getRuntime().exec("explorer.exe \"" + path + "\"");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static void openFile(String path, Component parent) {
        File file = new File(path);
        if (fileCheck(file, parent)) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static boolean fileCheck(File file, Component parent) {
        if (!file.isDirectory() && !file.exists()) {
            int result = JOptionPane.showConfirmDialog(parent, String.format("File '%s' does not exist. Create?",
                    file.getName()), "File does not exist", JOptionPane.YES_NO_OPTION);

            if (result == 0) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private void mkDir(String name) {
        File dir = new File(name);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                System.out.println("Could not create '" + name + "' directory. Exiting editor.");
                System.exit(1);
            }
        }
    }
}

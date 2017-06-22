package net.lyxodius.lyxGameEditor;

import net.lyxodius.lyxGame.main.LyxGame;
import net.lyxodius.lyxGame.main.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Lyxodius on 16.06.2017.
 */
class EditorFrame extends JFrame {
    private final JSpinner tileSetSpinner;
    private final LyxPanel editorPanel;
    private Map currentMap;
    private int selectedLayer;
    private MapPanel mapPanel;

    EditorFrame() {
        super("LyxGameEditor");

        editorPanel = new LyxPanel(this);
        editorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(editorPanel);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        LyxPanel fileButtonPanel = new LyxPanel(editorPanel, false, true);

        JButton newButton = new JButton("New");
        newButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Discard changes and create new map?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                setMap(new Map());
            }
        });
        fileButtonPanel.add(newButton);

        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser("map");
            int result = jFileChooser.showDialog(this, "Open");

            if (result == 0) {
                setMap(Map.readFromFile(jFileChooser.getSelectedFile()));
            }
        });
        fileButtonPanel.add(openButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            currentMap.saveToFile();
            JOptionPane.showMessageDialog(this, String.format("Saved map '%s'.", currentMap.name));
        });
        fileButtonPanel.add(saveButton);

        JButton explorerButton = new JButton("Explorer");
        explorerButton.addActionListener(e -> LyxGameEditor.openExplorer("map"));
        fileButtonPanel.add(explorerButton);

        LyxPanel tileSetPanel = new LyxPanel(editorPanel);

        tileSetPanel.add(new JLabel("TileSet:"));

        ButtonGroup buttonGroup = new ButtonGroup();
        LyxPanel layerButtonPanel = new LyxPanel(editorPanel, false, true);

        JToggleButton layerZeroButton = new JToggleButton("0");
        layerZeroButton.addActionListener(e -> selectedLayer = 0);
        buttonGroup.add(layerZeroButton);
        layerButtonPanel.add(layerZeroButton);

        JToggleButton layerOneButton = new JToggleButton("1");
        layerOneButton.addActionListener(e -> selectedLayer = 1);
        buttonGroup.add(layerOneButton);
        layerButtonPanel.add(layerOneButton);

        JToggleButton layerTwoButton = new JToggleButton("2");
        layerTwoButton.addActionListener(e -> selectedLayer = 2);
        buttonGroup.add(layerTwoButton);
        layerButtonPanel.add(layerTwoButton);

        JToggleButton collisionLayerButton = new JToggleButton("x");
        collisionLayerButton.addActionListener(e -> selectedLayer = 3);
        buttonGroup.add(collisionLayerButton);
        layerButtonPanel.add(collisionLayerButton);

        JToggleButton entityLayerButton = new JToggleButton("e");
        entityLayerButton.addActionListener(e -> selectedLayer = 4);
        buttonGroup.add(entityLayerButton);
        layerButtonPanel.add(entityLayerButton);

        layerZeroButton.doClick();

        ArrayList<BufferedImage> tileSets = LyxGame.loadTileSets();
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, tileSets.size() - 1, 1);
        tileSetSpinner = new JSpinner(spinnerModel);
        tileSetPanel.add(tileSetSpinner);

        this.setMap(Map.readFromFile());

        MapDialog mapDialog = new MapDialog(this, tileSets);

        setResizable(false);

        addWindowStateListener(e -> {
            if (e.getNewState() == 0) {
                for (Window window : getOwnedWindows()) {
                    window.setVisible(true);
                }
            } else if (e.getNewState() == 1) {
                for (Window window : getOwnedWindows()) {
                    window.setVisible(false);
                }
            }
        });

        setLocation(mapDialog.getTileDialog().getLocation().x + mapDialog.getTileDialog().getWidth() + 5, 0);
        pack();
        setVisible(true);
    }

    private void setMap(Map map) {
        currentMap = map;
        if (mapPanel != null) {
            editorPanel.remove(mapPanel);
            editorPanel.remove(editorPanel.getComponentCount() - 1);
        }
        mapPanel = new MapPanel(map, editorPanel);
        this.revalidate();
    }

    Map getCurrentMap() {
        return currentMap;
    }

    int getSelectedLayer() {
        return selectedLayer;
    }

    int getCurrentTileSetId() {
        return (int) tileSetSpinner.getValue();
    }
}

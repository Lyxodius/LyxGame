package net.lyxodius.lyxGameEditor;

import net.lyxodius.lyxGame.Map;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Created by Lyxodius on 16.06.2017.
 */
class MapPanel extends LyxPanel {
    MapPanel(Map map, Container parent) {
        super(parent, true, false);

        LyxPanel namePanel = new LyxPanel(this);

        namePanel.add(new JLabel("name:"));
        JTextField nameTextField = new JTextField(map.name, 8);
        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                map.name = nameTextField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                map.name = nameTextField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        namePanel.add(nameTextField);

        LyxPanel resizePanel = new LyxPanel(this);

        resizePanel.add(new JLabel("width:"));
        SpinnerModel widthSpinnerModel = new SpinnerNumberModel(map.getWidth(), 16, 64, 1);
        JSpinner widthSpinner = new JSpinner(widthSpinnerModel);
        resizePanel.add(widthSpinner);

        resizePanel.add(new JLabel("height:"));
        SpinnerModel heightSpinnerModel = new SpinnerNumberModel(map.getHeight(), 9, 36, 1);
        JSpinner heightSpinner = new JSpinner(heightSpinnerModel);
        resizePanel.add(heightSpinner);

        JButton resizeButton = new JButton("Resize");
        resizeButton.addActionListener(e -> {
            int width = (int) widthSpinner.getValue();
            int height = (int) heightSpinner.getValue();
            map.resize(width, height);
            JOptionPane.showMessageDialog(this,
                    String.format("Resized map '%s'.\nWidth: %d\nHeight: %d", map.name, width, height));
        });
        resizePanel.add(resizeButton);
    }
}

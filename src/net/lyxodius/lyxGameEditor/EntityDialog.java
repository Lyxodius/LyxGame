package net.lyxodius.lyxGameEditor;

import net.lyxodius.lyxGame.Behavior;
import net.lyxodius.lyxGame.Entity;
import net.lyxodius.lyxGame.Script;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Lyxodius on 17.06.2017.
 */
class EntityDialog extends JDialog {
    private final EditorFrame editorFrame;
    private final Entity entity;
    private JPanel imagePanel;

    EntityDialog(EditorFrame editorFrame, Entity entity) {
        super(editorFrame);
        setModal(true);
        setTitle(entity.name);

        this.editorFrame = editorFrame;
        this.entity = entity;

        LyxPanel panel = new LyxPanel(this, BoxLayout.X_AXIS);

        createPropertyPanel(panel);
        createImagePanel(panel);
        createCodePanel(panel);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void createCodePanel(LyxPanel parent) {
        LyxPanel codePanel = new LyxPanel(parent);

        LyxPanel behaviourPanel = new LyxPanel(codePanel);

        behaviourPanel.add(new JLabel("Behavior:"));
        JComboBox<Behavior> behaviorComboBox = new JComboBox<>(Behavior.values());
        behaviourPanel.add(behaviorComboBox);
        behaviorComboBox.setSelectedItem(entity.behavior);
        behaviorComboBox.addActionListener(e -> entity.behavior = (Behavior) behaviorComboBox.getSelectedItem());

        LyxPanel onInteractPanel = new LyxPanel(codePanel);

        JButton editOnInteractButton = new JButton("Edit");

        onInteractPanel.add(new JLabel("onInteract:"));
        String onInteract = null;
        if (entity.onInteract != null) {
            onInteract = entity.onInteract.name;
        } else {
            editOnInteractButton.setEnabled(false);
        }
        JTextField onInteractTextField = new JTextField(onInteract, 8);
        onInteractPanel.add(onInteractTextField);
        onInteractTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                editOnInteractButton.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (onInteractTextField.getText().isEmpty()) {
                    editOnInteractButton.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        JButton setOnInteractButton = new JButton("Set");
        onInteractPanel.add(setOnInteractButton);
        setOnInteractButton.addActionListener(e -> {
            String name = onInteractTextField.getText();
            if (!name.isEmpty()) {
                File file = new File("script/" + name + ".script");
                if (LyxGameEditor.fileCheck(file, this)) {
                    entity.onInteract = Script.loadFromFile(name);
                    JOptionPane.showMessageDialog(this,
                            String.format("Assigned script '%s' to onInteract.", name));
                }
            } else {
                entity.onInteract = null;
                JOptionPane.showMessageDialog(this, "Cleared onInteract.");
            }
        });

        JButton onInteractBrowseButton = new JButton("Browse");
        onInteractPanel.add(onInteractBrowseButton);
        onInteractBrowseButton.addActionListener(e -> {
            JFileChooser onInteractFileChooser = new JFileChooser("script");
            int result = onInteractFileChooser.showDialog(this, "Assign script");

            if (result == 0) {
                String name = onInteractFileChooser.getSelectedFile().getName();
                name = name.substring(0, name.lastIndexOf('.'));
                onInteractTextField.setText(name);
                entity.onInteract = Script.loadFromFile(name);
                JOptionPane.showMessageDialog(this,
                        String.format("Assigned script '%s' to onInteract.", name));
            }
        });

        JButton clearOnInteractButton = new JButton("Clear");
        onInteractPanel.add(clearOnInteractButton);
        clearOnInteractButton.addActionListener(e -> {
            onInteractTextField.setText(null);
            entity.onInteract = null;
            JOptionPane.showMessageDialog(this, "Cleared onInteract.");
        });

        onInteractPanel.add(editOnInteractButton);
        editOnInteractButton.addActionListener(
                e -> LyxGameEditor.openFile("script/" + onInteractTextField.getText() + ".script", this));

        codePanel.add(Box.createVerticalGlue(), true);
    }

    private void createImagePanel(LyxPanel parent) {
        imagePanel = new JPanel();
        parent.add(imagePanel);
        setImage();
    }

    private void setImage() {
        imagePanel.removeAll();

        BufferedImage image = entity.image;
        if (image == null) {
            try {
                image = ImageIO.read(new File("img/entity/nothing.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (image != null) {
            ImageIcon imageIcon = new ImageIcon(image);
            imagePanel.add(new JLabel(imageIcon));
        }

        revalidate();
    }

    private void createPropertyPanel(LyxPanel parent) {
        LyxPanel propertyPanel = new LyxPanel(parent);

        LyxPanel namePanel = new LyxPanel(propertyPanel);

        namePanel.add(new JLabel("name:"));
        JTextField nameTextField = new JTextField(entity.name, 5);
        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void setName() {
                String name = nameTextField.getText();
                entity.name = name;
                setTitle(name);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                setName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        namePanel.add(nameTextField);

        LyxPanel imagePanel = new LyxPanel(propertyPanel);
        imagePanel.add(new JLabel("image:"));
        JTextField imageTextField = new JTextField(entity.imageName, 8);
        imageTextField.setEnabled(false);
        imagePanel.add(imageTextField);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("img/entity");
            int result = fileChooser.showDialog(this, "Assign image");

            if (result == 0) {
                String name = fileChooser.getSelectedFile().getName();
                name = name.substring(0, name.lastIndexOf('.'));
                imageTextField.setText(name);
                entity.setImage(name);
                setImage();
            }
        });
        imagePanel.add(browseButton);

        LyxPanel positionPanel = new LyxPanel(propertyPanel);

        int mapWidth = editorFrame.getCurrentMap().getWidth();
        int mapHeight = editorFrame.getCurrentMap().getHeight();

        positionPanel.add(new JLabel("x:"));
        SpinnerModel xSpinnerModel = new SpinnerNumberModel(entity.position.x, 0, mapWidth, 1);
        JSpinner xSpinner = new JSpinner(xSpinnerModel);
        xSpinner.addChangeListener(e -> entity.position.x = (int) xSpinner.getValue());
        positionPanel.add(xSpinner);

        positionPanel.add(new JLabel("y:"));
        SpinnerModel ySpinnerModel = new SpinnerNumberModel(entity.position.y, 0, mapHeight, 1);
        JSpinner ySpinner = new JSpinner(ySpinnerModel);
        ySpinner.addChangeListener(e -> entity.position.y = (int) ySpinner.getValue());
        positionPanel.add(ySpinner);

        positionPanel.add(new JLabel("z:"));
        SpinnerModel zSpinnerModel = new SpinnerNumberModel(entity.position.z, 0, 2, 1);
        JSpinner zSpinner = new JSpinner(zSpinnerModel);
        zSpinner.addChangeListener(e -> entity.position.z = (int) zSpinner.getValue());
        positionPanel.add(zSpinner);

        try {
            final ImageIcon imageIcon = new ImageIcon(ImageIO.read(new File("img/editor/angery.png")));

            JButton deleteButton = new JButton("delet dis");
            deleteButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(this,
                        String.format("Do you really want to delet dis (%s)?", entity.name),
                        "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, imageIcon);
                if (result == 0) {
                    editorFrame.getCurrentMap().getEntityList().remove(entity);
                    dispose();
                }
            });
            deleteButton.setIcon(imageIcon);
            propertyPanel.add(deleteButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

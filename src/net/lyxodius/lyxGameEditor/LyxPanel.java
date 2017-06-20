package net.lyxodius.lyxGameEditor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lyxodius on 17.06.2017.
 */
class LyxPanel extends JPanel {
    private int axis;
    private boolean noSpacing;

    LyxPanel(Container parent, int axis) {
        setLayout(new BoxLayout(this, axis));
        parent.add(this);
    }

    LyxPanel(Container parent) {
        this(parent, false, false);
    }

    LyxPanel(Container parent, boolean parentAxis, boolean noSpacing) {
        axis = BoxLayout.Y_AXIS;
        this.noSpacing = noSpacing;

        if (parent instanceof LyxPanel) {
            BoxLayout boxLayout = (BoxLayout) parent.getLayout();
            if (boxLayout.getAxis() == BoxLayout.Y_AXIS) {
                axis = BoxLayout.X_AXIS;
            }
            if (parentAxis) {
                axis = boxLayout.getAxis();
            }
        }

        setLayout(new BoxLayout(this, axis));

        parent.add(this);
    }

    public Component add(Component component) {
        return add(component, false);
    }

    Component add(Component component, boolean noSpacing) {
        if (getComponentCount() > 0 && !this.noSpacing && !noSpacing) {
            if (axis == BoxLayout.Y_AXIS) {
                super.add(Box.createRigidArea(new Dimension(0, 10)));
            } else if (axis == BoxLayout.X_AXIS) {
                super.add(Box.createRigidArea(new Dimension(5, 0)));
            }
        }
        super.add(component);
        return component;
    }
}

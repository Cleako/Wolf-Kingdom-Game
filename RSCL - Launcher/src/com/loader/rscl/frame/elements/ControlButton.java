package com.loader.rscl.frame.elements;

import javax.swing.JButton;

import com.loader.rscl.frame.listeners.ButtonListener;
import com.loader.rscl.util.Utils;

@SuppressWarnings("serial")
public class ControlButton extends JButton
{
    public ControlButton(final int buttonType, final int x, final int y, final int width, final int height) {
        final String image = (buttonType == 1) ? "minimize" : "close";
        if (buttonType != 3) {
            this.setIcon(Utils.getImage(String.valueOf(image) + ".png"));
            this.setRolloverIcon(Utils.getImage(String.valueOf(image) + "_hover.png"));
        }
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setHorizontalTextPosition(0);
        this.setActionCommand(image);
        if (buttonType != 3) {
            this.addActionListener(new ButtonListener());
        }
        this.setBounds(x, y, width, height);
    }
}

package com.loader.rscl.frame.elements;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.Color;

import java.awt.event.MouseListener;
import javax.swing.JButton;

import com.loader.rscl.frame.listeners.ButtonListener;
import com.loader.rscl.util.Utils;

public class LaunchButton extends JButton implements MouseListener
{
    private static final long serialVersionUID = -3245141651685683983L;
    
    public LaunchButton() {
        super("");
        this.setIcon(Utils.getImage("launch.png"));
        this.setRolloverIcon(Utils.getImage("launch_hover.png"));
        this.setHorizontalTextPosition(0);
        this.setFont(Utils.getFont("OpenSans-Regular.ttf", 1, 20.0f));
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setForeground(Color.WHITE);
        this.setActionCommand("launch");
        this.addActionListener(new ButtonListener());
        this.addMouseListener(this);
    }
    
    @Override
    public void mouseClicked(final MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(12));
    }
    
    @Override
    public void mouseExited(final MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(0));
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
    }
}

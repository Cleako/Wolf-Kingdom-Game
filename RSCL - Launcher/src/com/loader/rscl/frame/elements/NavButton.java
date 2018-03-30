package com.loader.rscl.frame.elements;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import javax.swing.JButton;

import com.loader.rscl.frame.listeners.ButtonListener;
import com.loader.rscl.util.Utils;

@SuppressWarnings("serial")
public class NavButton extends JButton implements MouseListener
{
    public NavButton(final String text, final Rectangle bounds) {
        super(text.toUpperCase());
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setForeground(Color.WHITE);
        this.setHorizontalTextPosition(0);
        this.setFont(Utils.getFont("OpenSans-Regular.ttf", 0, 10.0f));
        this.addMouseListener(this);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setFocusable(false);
        this.setBounds(bounds);
        this.addActionListener(new ButtonListener());
    }
    
    @Override
    public void mouseClicked(final MouseEvent arg0) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent arg0) {
        this.setCursor(Cursor.getPredefinedCursor(12));
        this.setForeground(this.getForeground().darker());
    }
    
    @Override
    public void mouseExited(final MouseEvent arg0) {
        this.setCursor(Cursor.getPredefinedCursor(0));
        this.setForeground(this.getForeground().brighter());
    }
    
    @Override
    public void mousePressed(final MouseEvent arg0) {
    }
    
    @Override
    public void mouseReleased(final MouseEvent arg0) {
    }
}

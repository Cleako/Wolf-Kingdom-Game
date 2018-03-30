package com.loader.rscl.util;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import javax.swing.ImageIcon;
import java.net.URL;
import java.awt.Desktop;
import java.io.IOException;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.text.DateFormat;

public class Utils
{
    public static DateFormat df;
    private static long timeCorrection;
    private static long lastTimeUpdate;
    
    public static Font getFont(final String fontName, final int type, final float size) {
        try {
            Font font = Font.createFont(0, Utils.class.getResource("/data/fonts/" + fontName).openStream());
            final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            genv.registerFont(font);
            font = font.deriveFont(type, size);
            return font;
        }
        catch (FontFormatException | IOException ex2) {
            final Exception ex = null;
            final Exception e = ex;
            e.printStackTrace();
            return null;
        }
    }
    
    public static void openWebpage(final String url) {
        final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URL(url).toURI());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static synchronized long currentTimeMillis() {
        final long l = System.currentTimeMillis();
        if (l < Utils.lastTimeUpdate) {
            Utils.timeCorrection += Utils.lastTimeUpdate - l;
        }
        Utils.lastTimeUpdate = l;
        return l + Utils.timeCorrection;
    }
    
    public static ImageIcon getImage(final String name) {
        return new ImageIcon(Utils.class.getResource("/data/images/" + name));
    }
    
    public static String getServerTime() {
        if (Utils.df == null) {
            (Utils.df = new SimpleDateFormat("h:mm:ss a")).setTimeZone(TimeZone.getTimeZone("America/Chicago"));
        }
        return Utils.df.format(new Date());
    }
    
    public static String stripHtml(final String text) {
        return text.replaceAll("\\<.*?\\>", "");
    }
}

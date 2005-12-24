package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUIPicture extends GUIElement
{
    public GUIPicture
            (String nn, int nx, int ny, int nw, int nh, String picture)  throws IOException
    {
        BufferedImage img = javax.imageio.ImageIO.read(new File(picture));
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myname = nn;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        Graphics2D g = mybuffer.createGraphics();
        g.drawImage(img, 0, 0, nw, nh, 0, 0, nw, nh, null);
        g.dispose();

    }
}

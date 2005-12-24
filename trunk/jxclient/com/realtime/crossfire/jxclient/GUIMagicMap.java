package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUIMagicMap extends GUIElement implements CrossfireMagicmapListener,
                                                       CrossfireNewmapListener
{
    Color[] mycolors = new Color[]{Color.BLACK, Color.WHITE, Color.BLUE,
                                   Color.RED, Color.GREEN, Color.YELLOW,
                                   Color.PINK, Color.GRAY, Color.ORANGE,
                                   Color.CYAN, Color.MAGENTA};
    public GUIMagicMap
            (String nn, int nx, int ny, int nw, int nh)  throws IOException
    {
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myname = nn;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
    }
    public void CommandMagicmapReceived(CrossfireCommandMagicmapEvent evt)
    {
        int datapos = 0;
        byte[] data = evt.getData();
        byte square;
        Color scolor;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        BufferedImage buf = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D g = buf.createGraphics();
        g.setBackground(new Color(0,0,0,0.0f));
        g.clearRect(0,0,w,h);
        while(data[datapos]==' ')
            datapos++;
        for (int y=0;y<evt.getHeight();y++)
        {
            for (int x=0;x<evt.getWidth();x++)
            {
                square = data[datapos];
                if (square > 10)
                    scolor = Color.DARK_GRAY;
                else
                    scolor = mycolors[square];

                g.setColor(scolor);
                g.fillRect(x*4,y*4,(x*4)+4,(y*4)+4);
                datapos++;
            }
        }
        g.dispose();
        mybuffer = buf;
    }
    public void CommandNewmapReceived(CrossfireCommandNewmapEvent evt)
    {
        Graphics2D g = mybuffer.createGraphics();
        g.setBackground(new Color(0,0,0,0.0f));
        g.clearRect(0,0,w,h);
        g.dispose();
    }
}

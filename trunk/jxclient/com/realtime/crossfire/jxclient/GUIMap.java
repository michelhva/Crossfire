package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUIMap extends GUIElement implements CrossfireMap1Listener,
                                                  CrossfireNewmapListener,
                                                  CrossfireMapscrollListener
{
    private boolean need_update = false;
    private boolean new_map_happened = true;
    private BufferedImage myblacktile = null;
    public GUIMap
            (String nn, int nx, int ny, int nw, int nh)  throws IOException
    {
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myname = nn;

        myblacktile = javax.imageio.ImageIO.read(new File("cache/black.png"));

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        Graphics2D g = mybuffer.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,mybuffer.getWidth(), mybuffer.getHeight());
        g.dispose();
    }
    public void redraw(Graphics g)
    {
        synchronized(mybuffer)
        {
            if (new_map_happened == true)
            {
                g.setColor(Color.BLACK);
                g.fillRect(x,y,w,h);
                new_map_happened = false;
            }
            if (need_update == true)
            {
                need_update = false;
                MapSquare[][] map = com.realtime.crossfire.jxclient.Map.getMap();
                for (int nz=0; nz<ServerConnection.NUM_LAYERS; nz++)
                {
                    for (int ny=10; ny<ServerConnection.MAP_HEIGHT+10; ny++)
                    {
                        for (int nx=10; nx<ServerConnection.MAP_WIDTH+10; nx++)
                        {
                            redrawSquare(g, map[nx][ny], nz);
                        }
                    }
                }
            }
        }
    }
    protected void cleanSquare(Graphics g, MapSquare square)
    {
        g.setColor(Color.BLACK);
        g.fillRect(((square.getXPos()-10)*ServerConnection.SQUARE_SIZE),
                     ((square.getYPos()-10)*ServerConnection.SQUARE_SIZE),
                   ServerConnection.SQUARE_SIZE,
                   ServerConnection.SQUARE_SIZE);
    }
    protected void redrawSquare(Graphics g, MapSquare square, int nz)
    {
        if (square.isDirty()==false)
            return;
        if (nz == 0)
        {
            cleanSquare(g, square);
        }
        if (square.getHead(nz) == square)
        {
            Face f = square.getFace(nz);
            if (f != null)
            {
                BufferedImage img = f.getPicture();
                int px = (square.getXPos()-10)*ServerConnection.SQUARE_SIZE;
                int py = (square.getYPos()-10)*ServerConnection.SQUARE_SIZE;
                int psx = px - (img.getWidth()-ServerConnection.SQUARE_SIZE);
                int psy = py - (img.getHeight()-ServerConnection.SQUARE_SIZE);
                g.drawImage(img, psx, psy, img.getWidth(), img.getHeight(), null);
            }
        }
        if (nz == ServerConnection.NUM_LAYERS-1)
        {
            /*if (square.getDarkness() >= 0)
            {
                g.setColor(new Color(0,0,0,square.getDarkness()));
                g.fillRect(((square.getXPos()-10)*ServerConnection.SQUARE_SIZE),
                             ((square.getYPos()-10)*ServerConnection.SQUARE_SIZE),
                             ServerConnection.SQUARE_SIZE,
                             ServerConnection.SQUARE_SIZE);
                System.out.println("Darkness:"+square+" D:" +square.getDarkness());
            }
            else
            {
                System.out.println("Darkness:"+square+" I:" +square.getDarkness());
            }*/
            square.clean();
        }
    }
    public void CommandMapscrollReceived(CrossfireCommandMapscrollEvent evt)
    {
        synchronized(mybuffer)
        {
            need_update = true;
        }
    }
    public void CommandNewmapReceived(CrossfireCommandNewmapEvent evt)
    {
        /*Graphics2D g = mybuffer.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,mybuffer.getWidth(), mybuffer.getHeight());
        g.dispose();*/
        synchronized(mybuffer)
        {
            need_update = true;
            new_map_happened = true;
        }
    }
    public void CommandMap1Received(CrossfireCommandMap1Event evt)
    {
        synchronized(mybuffer)
        {
            need_update = true;
        }
    }
    public void refresh()
    {
        synchronized(mybuffer)
        {
            need_update = true;
            new_map_happened = true;
        }
    }
}

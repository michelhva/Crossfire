//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIMap extends GUIElement implements CrossfireMap1Listener, CrossfireNewmapListener, CrossfireMapscrollListener
{
    private boolean need_update = false;

    private int need_update_cnt = 0;

    private boolean new_map_happened = true;

    private BufferedImage myblacktile = null;

    private boolean use_big_images = true;

    private int mysquaresize;

    public GUIMap(final JXCWindow jxcWindow, String nn, int nx, int ny, int nw, int nh)  throws IOException
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        myblacktile = ImageIO.read(this.getClass().getClassLoader().getResource("black_big.png"));

        createBuffer();
        mysquaresize = CrossfireServerConnection.SQUARE_SIZE;
    }

    public GUIMap(final JXCWindow jxcWindow, String nn, int nx, int ny, int nw, int nh, boolean big)  throws IOException
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        use_big_images = big;

        if (big)
        {
            myblacktile = ImageIO.read(this.getClass().getClassLoader().getResource("black_big.png"));
            mysquaresize = CrossfireServerConnection.SQUARE_SIZE;
        }
        else
        {
            myblacktile = ImageIO.read(this.getClass().getClassLoader().getResource("black.png"));
            mysquaresize = 32;
        }

        createBuffer();
    }

    public void redraw(Graphics g)
    {
        synchronized(mybuffer)
        {
            if (new_map_happened)
            {
                g.setColor(Color.BLACK);
                g.fillRect(x, y, w, h);
                new_map_happened = false;
            }

            if (need_update)
            {
                need_update_cnt--;
                if (need_update_cnt <= 0)
                    need_update = false;
                CfMapSquare[][] map = CfMap.getMap();
                for (int nz = 0; nz < CrossfireServerConnection.NUM_LAYERS; nz++)
                {
                    for (int ny = 10; ny < CrossfireServerConnection.MAP_HEIGHT+10; ny++)
                    {
                        for (int nx = 10; nx < CrossfireServerConnection.MAP_WIDTH+10; nx++)
                        {
                            redrawSquare(g, map[nx][ny], nz);
                        }
                    }
                }
            }
        }
    }

    protected void cleanSquare(Graphics g, CfMapSquare square)
    {
        g.setColor(Color.BLACK);
        g.fillRect(((square.getXPos()-10)*mysquaresize),
                   ((square.getYPos()-10)*mysquaresize),
                   mysquaresize, mysquaresize);
    }

    protected void redrawSquare(Graphics g, CfMapSquare square, int nz)
    {
        if (square == null) //Sometimes happen. Not sure of the origin, but I think
                            //it is related to a scrolling faster than a non-cached
                            //image happening. Seems harmless to simply ignore the
                            //null square here.
        {
            System.err.println("Warning ! Null square detected");
            return;
        }

        if (!square.isDirty())
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
                final ImageIcon img;
                if (use_big_images)
                {
                    img = f.getImageIcon();
                }
                else
                {
                    img = f.getOriginalImageIcon();
                }

                int px = (square.getXPos()-10)*mysquaresize;
                int py = (square.getYPos()-10)*mysquaresize;
                int psx = px-(img.getIconWidth()-mysquaresize);
                int psy = py-(img.getIconHeight()-mysquaresize);
                g.drawImage(img.getImage(), psx, psy, img.getIconWidth(), img.getIconHeight(), null);
            }
        }

        if (nz == CrossfireServerConnection.NUM_LAYERS-1)
        {
            /*if (square.getDarkness() >= 0)
            {
                g.setColor(new Color(0, 0, 0, square.getDarkness()));
                g.fillRect(((square.getXPos()-10)*CrossfireServerConnection.SQUARE_SIZE),
                             ((square.getYPos()-10)*CrossfireServerConnection.SQUARE_SIZE),
                             CrossfireServerConnection.SQUARE_SIZE,
                             CrossfireServerConnection.SQUARE_SIZE);
                System.out.println("Darkness:"+square+" D:" +square.getDarkness());
            }
            else
            {
                System.out.println("Darkness:"+square+" I:" +square.getDarkness());
            }*/
            square.clean();
        }
    }

    public void commandMapscrollReceived(CrossfireCommandMapscrollEvent evt)
    {
        synchronized(mybuffer)
        {
            need_update = true;
            need_update_cnt = 2;
        }
        setChanged();
    }

    public void commandNewmapReceived(CrossfireCommandNewmapEvent evt)
    {
        /*Graphics2D g = mybuffer.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, mybuffer.getWidth(), mybuffer.getHeight());
        g.dispose();*/
        synchronized(mybuffer)
        {
            need_update = true;
            need_update_cnt = 2;
            new_map_happened = true;
        }
        setChanged();
    }

    public void commandMap1Received(CrossfireCommandMap1Event evt)
    {
        synchronized(mybuffer)
        {
            need_update = true;
            need_update_cnt = 2;
        }
        setChanged();
    }

    public void refresh()
    {
        synchronized(mybuffer)
        {
            need_update = true;
            need_update_cnt = 2;
            new_map_happened = true;
        }
        setChanged();
    }

    /** {@inheritDoc} */
    public void mouseClicked(final MouseEvent e)
    {
        switch (e.getButton())
        {
        case MouseEvent.BUTTON1:
            final int dx = (e.getX()+getX())/CrossfireServerConnection.SQUARE_SIZE-CrossfireServerConnection.MAP_WIDTH/2;
            final int dy = (e.getY()+getY())/CrossfireServerConnection.SQUARE_SIZE-CrossfireServerConnection.MAP_HEIGHT/2;
            try
            {
                final JXCWindow jxcw = (JXCWindow)e.getSource();
                jxcw.getCrossfireServerConnection().sendLookat(dx, dy);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(0);
            }
            break;

        case MouseEvent.BUTTON2:
        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        final Graphics2D g = mybuffer.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.dispose();
        setChanged();
    }
}

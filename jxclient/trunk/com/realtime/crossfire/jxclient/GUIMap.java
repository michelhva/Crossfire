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
    private final BufferedImage myblacktile;

    private final boolean use_big_images;

    private final int mysquaresize;

    public GUIMap(final JXCWindow jxcWindow, final String nn, final int nx, final int ny, final int nw, final int nh) throws IOException
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        use_big_images = true;
        myblacktile = ImageIO.read(this.getClass().getClassLoader().getResource("black_big.png"));

        createBuffer();
        mysquaresize = CrossfireServerConnection.SQUARE_SIZE;
    }

    public GUIMap(final JXCWindow jxcWindow, final String nn, final int nx, final int ny, final int nw, final int nh, final boolean big) throws IOException
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

    public void render()
    {
        synchronized(mybuffer)
        {
            final Graphics2D g = mybuffer.createGraphics();
            try
            {
                for (int layer = 0; layer < CrossfireServerConnection.NUM_LAYERS; layer++)
                {
                    for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT; y++)
                    {
                        redrawSquare(g, x, y);
                    }
                }
            }
            finally
            {
                g.dispose();
            }
        }
    }

    protected void cleanSquare(final Graphics g, final CfMapSquare square)
    {
        g.setColor(Color.BLACK);
        g.fillRect(((square.getXPos()-10)*mysquaresize-mysquaresize/2),
                   ((square.getYPos()-10)*mysquaresize-mysquaresize/2),
                   mysquaresize, mysquaresize);
    }

    /**
     * Redraw one square if it has been changed. If it is unchanged ({@link
     * CfMapSquare#isDirty()} is unset), nothing is drawn.
     *
     * @param g The graphics to draw into.
     *
     * @param x The x-coordinate of the map tile to redraw.
     *
     * @param y The y-coordinate of the map tile to redraw.
     */
    private void redrawSquare(final Graphics g, final int x, final int y)
    {
        final CfMapSquare square = CfMap.getMap()[x+10][y+10];
        if (!square.isDirty())
        {
            return;
        }

        for (int layer = 0; layer < CrossfireServerConnection.NUM_LAYERS; layer++)
        {
            redrawSquare(g, square, layer);
        }
    }

    protected void redrawSquare(final Graphics g, final CfMapSquare square, final int nz)
    {
        if (square == null) //Sometimes happen. Not sure of the origin, but I think
                            //it is related to a scrolling faster than a non-cached
                            //image happening. Seems harmless to simply ignore the
                            //null square here.
        {
            System.err.println("Warning ! Null square detected");
            return;
        }

        if (nz == 0)
        {
            cleanSquare(g, square);
        }

        if (square.getHead(nz) == square)
        {
            final Face f = square.getFace(nz);
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

                final int px = (square.getXPos()-10)*mysquaresize-mysquaresize/2;
                final int py = (square.getYPos()-10)*mysquaresize-mysquaresize/2;
                final int psx = px-(img.getIconWidth()-mysquaresize);
                final int psy = py-(img.getIconHeight()-mysquaresize);
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

    public void commandMapscrollReceived(final CrossfireCommandMapscrollEvent evt)
    {
        synchronized(mybuffer)
        {
            final int dx = -evt.getDX()*mysquaresize;
            final int dy = -evt.getDY()*mysquaresize;
            if (Math.abs(dx) >= CrossfireServerConnection.MAP_WIDTH || Math.abs(dy) >= CrossfireServerConnection.MAP_HEIGHT)
            {
                render();
                return;
            }

            final int x;
            final int w;
            if (dx < 0)
            {
                x = -dx;
                w = getWidth()+dx;
            }
            else
            {
                x = 0;
                w = getWidth()-dx;
            }
            final int y;
            final int h;
            if (dy < 0)
            {
                y = -dy;
                h = getHeight()+dy;
            }
            else
            {
                y = 0;
                h = getHeight()-dy;
            }

            final Graphics2D g = mybuffer.createGraphics();
            try
            {
                g.copyArea(x, y, w, h, dx, dy);

                for (int yy = 0; yy < y; yy++)
                {
                    for (int xx = 0; xx < CrossfireServerConnection.MAP_WIDTH; xx++)
                    {
                        redrawSquare(g, xx, yy);
                    }
                }

                for (int yy = y+h; yy < CrossfireServerConnection.MAP_HEIGHT; yy++)
                {
                    for (int xx = 0; xx < CrossfireServerConnection.MAP_WIDTH; xx++)
                    {
                        redrawSquare(g, xx, yy);
                    }
                }

                for (int yy = y; yy < y+h; yy++)
                {
                    for (int xx = 0; xx < x; xx++)
                    {
                        redrawSquare(g, xx, yy);
                    }

                    for (int xx = x+w; xx < CrossfireServerConnection.MAP_WIDTH; xx++)
                    {
                        redrawSquare(g, xx, yy);
                    }
                }
            }
            finally
            {
                g.dispose();
            }
        }
        setChanged();
    }

    public void commandNewmapReceived(final CrossfireCommandNewmapEvent evt)
    {
        synchronized(mybuffer)
        {
            render();
        }
        setChanged();
    }

    public void commandMap1Received(final CrossfireCommandMap1Event evt)
    {
        synchronized(mybuffer)
        {
            final Graphics2D g = mybuffer.createGraphics();
            for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT; y++)
            {
                for (int x = 0; x < CrossfireServerConnection.MAP_WIDTH; x++)
                {
                    redrawSquare(g, x, y);
                }
            }
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
            catch (final Exception ex)
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

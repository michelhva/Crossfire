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
public class GUIMap extends GUIElement implements CrossfireMapListener, CrossfireNewmapListener, CrossfireMapscrollListener
{
    /**
     * The color to use for overlaying fog-of-war tiles.
     */
    private static final Color fogOfWarColor = new Color(0, 0, 0.5F, 0.5F);

    /**
     * The minimum darkness alpha value; it is used for completely black tiles.
     * The maximum is 0.0F for full bright tiles.
     */
    private static final float MAX_DARKNESS_ALPHA = 0.7F;

    private final BufferedImage myblacktile;

    private final boolean use_big_images;

    private final int mysquaresize;

    /**
     * Cache to lookup darkness overlay colors. Maps darkness value to overlay
     * color. Not yet allocated entries are set to <code>null</code>.
     */
    private Color[] darknessColors = new Color[256];

    public GUIMap(final JXCWindow jxcWindow, final String nn, final int nx, final int ny, final int nw, final int nh) throws IOException
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        use_big_images = true;

        myblacktile = ImageIO.read(this.getClass().getClassLoader().getResource("black_big.png"));
        mysquaresize = CrossfireServerConnection.SQUARE_SIZE;
        if (nw != CrossfireServerConnection.MAP_WIDTH*mysquaresize) throw new IllegalArgumentException();
        if (nh != CrossfireServerConnection.MAP_HEIGHT*mysquaresize) throw new IllegalArgumentException();

        createBuffer();
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
        if (nw != CrossfireServerConnection.MAP_WIDTH*mysquaresize) throw new IllegalArgumentException();
        if (nh != CrossfireServerConnection.MAP_HEIGHT*mysquaresize) throw new IllegalArgumentException();

        createBuffer();
    }

    private void render()
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

    /**
     * Redraw one square completely black.
     *
     * @param g The graphics to draw into.
     *
     * @param x The x-coordinate of the square to clear.
     *
     * @param y The y-coordinate of the square to clear.
     */
    protected void cleanSquare(final Graphics g, final int x, final int y)
    {
        g.setColor(Color.BLACK);
        g.fillRect(x*mysquaresize, y*mysquaresize, mysquaresize, mysquaresize);
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
        if (!CfMapUpdater.getMap().resetDirty(x, y))
        {
            return;
        }

        cleanSquare(g, x, y);
        for (int layer = 0; layer < CrossfireServerConnection.NUM_LAYERS; layer++)
        {
            redrawSquare(g, x, y, layer);
        }
        if (CfMapUpdater.getMap().isFogOfWar(x, y))
        {
            g.setColor(fogOfWarColor);
            g.fillRect(x*mysquaresize, y*mysquaresize, mysquaresize, mysquaresize);
        }
        final int darkness = CfMapUpdater.getMap().getDarkness(x, y);
        if (darkness < 255)
        {
            g.setColor(getDarknessColor(darkness));
            g.fillRect(x*mysquaresize, y*mysquaresize, mysquaresize, mysquaresize);
        }
    }

    /**
     * Redraw one layer of a square.
     *
     * @param g The graphics to draw into.
     *
     * @param square The square to redraw.
     *
     * @param layer The layer to redraw.
     */
    private void redrawSquare(final Graphics g, final int x, final int y, final int layer)
    {
        final int px = x*mysquaresize;
        final int py = y*mysquaresize;

        final CfMapSquare headMapSquare = CfMapUpdater.getMap().getHeadMapSquare(x, y, layer);
        if (headMapSquare != null)
        {
            final Face headFace = headMapSquare.getFace(layer);
            assert headFace != null; // getHeadMapSquare() would have been cleared in this case
            final ImageIcon img = headFace.getImageIcon(use_big_images);
            final int dx = headMapSquare.getX()-CfMapUpdater.getMap().getMapSquare(x, y).getX();
            final int dy = headMapSquare.getY()-CfMapUpdater.getMap().getMapSquare(x, y).getY();
            assert dx > 0 || dy > 0;
            final int sx = img.getIconWidth()-mysquaresize*(dx+1);
            final int sy = img.getIconHeight()-mysquaresize*(dy+1);
            g.drawImage(img.getImage(),
                px, py, px+mysquaresize, py+mysquaresize,
                sx, sy, sx+mysquaresize, sy+mysquaresize,
                null);
        }

        final Face f = CfMapUpdater.getMap().getFace(x, y, layer);
        if (f != null)
        {
            final ImageIcon img = f.getImageIcon(use_big_images);
            final int sx = img.getIconWidth();
            final int sy = img.getIconHeight();
            g.drawImage(img.getImage(),
                px, py, px+mysquaresize, py+mysquaresize,
                sx-mysquaresize, sy-mysquaresize, sx, sy,
                null);
        }
    }

    public void commandMapscrollReceived(final CrossfireCommandMapscrollEvent evt)
    {
        synchronized(mybuffer)
        {
            final int dx = -evt.getDX();
            final int dy = -evt.getDY();
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
                w = CrossfireServerConnection.MAP_WIDTH+dx;
            }
            else
            {
                x = 0;
                w = CrossfireServerConnection.MAP_WIDTH-dx;
            }
            final int y;
            final int h;
            if (dy < 0)
            {
                y = -dy;
                h = CrossfireServerConnection.MAP_HEIGHT+dy;
            }
            else
            {
                y = 0;
                h = CrossfireServerConnection.MAP_HEIGHT-dy;
            }

            final Graphics2D g = mybuffer.createGraphics();
            try
            {
                g.copyArea(x*mysquaresize, y*mysquaresize, w*mysquaresize, h*mysquaresize, dx*mysquaresize, dy*mysquaresize);

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

    public void commandMapReceived(final CrossfireCommandMapEvent evt)
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
            final int dx = e.getX()/CrossfireServerConnection.SQUARE_SIZE-CrossfireServerConnection.MAP_WIDTH/2;
            final int dy = e.getY()/CrossfireServerConnection.SQUARE_SIZE-CrossfireServerConnection.MAP_HEIGHT/2;
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

    /**
     * Return an overlay color for a darkness value.
     *
     * @param darkness The darkness value between 0 and 255.
     *
     * @return The overlay color.
     */
    private Color getDarknessColor(final int darkness)
    {
        if (darknessColors[darkness] == null)
        {
            final float alpha = MAX_DARKNESS_ALPHA*(255-darkness)/255F;
            darknessColors[darkness] = new Color(0, 0, 0, alpha);
        }

        return darknessColors[darkness];
    }
}

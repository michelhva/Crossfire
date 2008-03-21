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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.magicmap.CfMagicMap;
import com.realtime.crossfire.jxclient.magicmap.CrossfireCommandMagicmapEvent;
import com.realtime.crossfire.jxclient.magicmap.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireCommandMapEvent;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireCommandMapscrollEvent;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireCommandNewmapEvent;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireMapListener;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireMapscrollListener;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireNewmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import javax.swing.ImageIcon;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIMagicMap extends GUIElement
{
    /**
     * The size of one tile in pixels.
     */
    private final int TILE_SIZE = 4;

    /**
     * The x offset for the visible map area.
     */
    private final int offsetX;

    /**
     * The y offset for the visible map area.
     */
    private final int offsetY;

    private final Color[] mycolors = new Color[]
    {
        Color.BLACK, Color.WHITE, Color.BLUE,
        Color.RED, Color.GREEN, Color.YELLOW,
        Color.PINK, Color.GRAY, Color.ORANGE,
        Color.CYAN, Color.MAGENTA,
    };

    /**
     * The {@link CrossfireMagicmapListener} registered to receive mapgicmap
     * commands.
     */
    private final CrossfireMagicmapListener crossfireMagicmapListener = new CrossfireMagicmapListener()
    {
        /** {@inheritDoc} */
        public void commandMagicmapReceived(final CrossfireCommandMagicmapEvent evt)
        {
            synchronized (mybuffer)
            {
                int datapos = 0;
                final byte[] data = evt.getData();
                final Graphics2D g = mybuffer.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, w, h);
                while (data[datapos] == ' ')
                {
                    datapos++;
                }
                final int offsetX = GUIMagicMap.this.offsetX+((CrossfireServerConnection.MAP_WIDTH-1)/2)*TILE_SIZE-evt.getPX()*TILE_SIZE;
                final int offsetY = GUIMagicMap.this.offsetY+((CrossfireServerConnection.MAP_HEIGHT-1)/2)*TILE_SIZE-evt.getPY()*TILE_SIZE;
                for (int y = 0; y < evt.getHeight(); y++)
                {
                    for (int x = 0; x < evt.getWidth(); x++)
                    {
                        final byte square = data[datapos];
                        final Color scolor = square >= mycolors.length ? Color.DARK_GRAY : mycolors[square];
                        g.setColor(scolor);
                        final int sx = offsetX+x*TILE_SIZE;
                        final int sy = offsetY+y*TILE_SIZE;
                        g.fillRect(sx, sy, sx+TILE_SIZE, sy+TILE_SIZE);
                        datapos++;
                    }
                }
                markPlayer(g);
                g.dispose();
            }
            setChanged();
        }
    };

    /**
     * The {@link CrossfireMapscrollListener} used to track player position
     * changes into the magic map.
     */
    private final CrossfireMapscrollListener crossfireMapscrollListener = new CrossfireMapscrollListener()
    {
        /** {@inheritDoc} */
        public void commandMapscrollReceived(final CrossfireCommandMapscrollEvent evt)
        {
            synchronized (mybuffer)
            {
                final Graphics2D g = mybuffer.createGraphics();
                final CfMap map = CfMapUpdater.getMap();
                final int dx = evt.getDX()*TILE_SIZE;
                final int dy = evt.getDY()*TILE_SIZE;
                if (Math.abs(dx) >= w || Math.abs(dy) >= h)
                {
                    redrawTiles(g, map, 0, 0, w/TILE_SIZE, h/TILE_SIZE);
                }
                else
                {
                    g.copyArea(dx <= 0 ? 0 : dx, dy <= 0 ? 0 : dy, dx == 0 ? w : w-Math.abs(dx), dy == 0 ? h : h-Math.abs(dy), -dx, -dy);
                    g.setColor(Color.BLACK);
                    if (dx < 0)
                    {
                        redrawTiles(g, map, 0, 0, -dx/TILE_SIZE, h/TILE_SIZE);
                    }
                    else if (dx > 0)
                    {
                        redrawTiles(g, map, w/TILE_SIZE-dx/TILE_SIZE, 0, w/TILE_SIZE, h/TILE_SIZE);
                    }
                    if (dy < 0)
                    {
                        redrawTiles(g, map, 0, 0, w/TILE_SIZE, -dy/TILE_SIZE);
                    }
                    else if (dy > 0)
                    {
                        redrawTiles(g, map, 0, h/TILE_SIZE-dy/TILE_SIZE, w/TILE_SIZE, h/TILE_SIZE);
                    }
                }
                redrawSquare(g, map, (CrossfireServerConnection.MAP_WIDTH-1)/2-evt.getDX(), (CrossfireServerConnection.MAP_HEIGHT-1)/2-evt.getDY());
                markPlayer(g);
                g.dispose();
            }
            setChanged();
        }
    };

    /**
     * The {@link CrossfireMapListener} registered to receive map updates.
     */
    private final CrossfireMapListener crossfireMapListener = new CrossfireMapListener()
    {
        /** {@inheritDoc} */
        public void commandMapReceived(final CrossfireCommandMapEvent evt)
        {
            synchronized(mybuffer)
            {
                final CfMap map = evt.getMap();
                final int x0 = map.getOffsetX();
                final int y0 = map.getOffsetY();
                final Graphics2D g = mybuffer.createGraphics();
                for (final CfMapSquare mapSquare : evt.getChangedSquares())
                {
                    final int x = mapSquare.getX()+x0;
                    final int y = mapSquare.getY()+y0;
                    redrawSquare(g, map, x, y);
                }
                markPlayer(g);
                g.dispose();
            }
            setChanged();
        }
    };

    /**
     * The {@link CrossfireNewmapListener} registered to receive newmap
     * commands.
     */
    private final CrossfireNewmapListener crossfireNewmapListener = new CrossfireNewmapListener()
    {
        /** {@inheritDoc} */
        public void commandNewmapReceived(final CrossfireCommandNewmapEvent evt)
        {
            synchronized (mybuffer)
            {
                final Graphics2D g = mybuffer.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, w, h);
                markPlayer(g);
                g.dispose();
            }
            setChanged();
        }
    };

    public GUIMagicMap(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h)
    {
        super(jxcWindow, name, x, y, w, h);
        if (w <= 0 || h <= 0) throw new IllegalArgumentException("area must be non-empty");
        if (w%TILE_SIZE != 0) throw new IllegalArgumentException("width is not a multiple of "+TILE_SIZE);
        if (h%TILE_SIZE != 0) throw new IllegalArgumentException("height is not a multiple of "+TILE_SIZE);
        if ((w/TILE_SIZE)%2 != 1) throw new IllegalArgumentException("width is not an odd number of tiles");
        if ((h/TILE_SIZE)%2 != 1) throw new IllegalArgumentException("height is not an odd number of tiles");
        offsetX = w/2-TILE_SIZE/2-((CrossfireServerConnection.MAP_WIDTH-1)/2)*TILE_SIZE;
        offsetY = h/2-TILE_SIZE/2-((CrossfireServerConnection.MAP_HEIGHT-1)/2)*TILE_SIZE;
        createBuffer();
        CfMagicMap.addCrossfireMagicmapListener(crossfireMagicmapListener);
        CfMapUpdater.addCrossfireNewmapListener(crossfireNewmapListener);
        CfMapUpdater.addCrossfireMapscrollListener(crossfireMapscrollListener);
        CfMapUpdater.addCrossfireMapListener(crossfireMapListener);
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        setChanged();
    }

    /**
     * Redraw a rectangular area of tiles.
     *
     * @param g The graphics to draw into.
     *
     * @param map The map to draw.
     *
     * @param x0 The left edge to redraw (inclusive).
     *
     * @param y0 The top edge to redraw (inclusive).
     *
     * @param x1 The right edge to redraw (exclusive).
     *
     * @param y1 The bottom edge to redraw (exclusive).
     */
    private void redrawTiles(final Graphics2D g, final CfMap map, final int x0, final int y0, final int x1, final int y1)
    {
        for (int x = x0; x < x1; x++)
        {
            for (int y = y0; y < y1; y++)
            {
                final int xx = x-offsetX/TILE_SIZE;
                final int yy = y-offsetY/TILE_SIZE;
                redrawSquare(g, map, xx, yy);
            }
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
    private void cleanSquare(final Graphics2D g, final int x, final int y)
    {
        g.setColor(Color.BLACK);
        g.fillRect(x*TILE_SIZE+offsetX, y*TILE_SIZE+offsetY, TILE_SIZE, TILE_SIZE);
    }

    /**
     * Redraw one square.
     *
     * @param g The graphics to draw into.
     *
     * @param map The map to draw.
     *
     * @param x The x-coordinate of the map tile to redraw.
     *
     * @param y The y-coordinate of the map tile to redraw.
     */
    private void redrawSquare(final Graphics2D g, final CfMap map, final int x, final int y)
    {
        cleanSquare(g, x, y);
        for (int layer = 0; layer < CrossfireServerConnection.NUM_LAYERS; layer++)
        {
            redrawSquare(g, map, x, y, layer);
        }
        if (map.isFogOfWar(x, y) || x < 0 || y < 0 || x >= CrossfireServerConnection.MAP_WIDTH || y >= CrossfireServerConnection.MAP_HEIGHT)
        {
            g.setColor(GUIMap.fogOfWarColor);
            g.fillRect(x*TILE_SIZE+offsetX, y*TILE_SIZE+offsetY, TILE_SIZE, TILE_SIZE);
        }
        final int darkness = map.getDarkness(x, y);
        if (darkness < 255)
        {
            g.setColor(GUIMap.getDarknessColor(darkness));
            g.fillRect(x*TILE_SIZE+offsetX, y*TILE_SIZE+offsetY, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Redraw one layer of a square.
     *
     * @param g The graphics to draw into.
     *
     * @param map The map to draw.
     *
     * @param x The x coordinate of the square to redraw.
     *
     * @param y The y coordinate of the square to redraw.
     *
     * @param layer The layer to redraw.
     */
    private void redrawSquare(final Graphics2D g, final CfMap map, final int x, final int y, final int layer)
    {
        final int px = x*TILE_SIZE+offsetX;
        final int py = y*TILE_SIZE+offsetY;

        final CfMapSquare headMapSquare = map.getHeadMapSquare(x, y, layer);
        if (headMapSquare != null)
        {
            final Face headFace = headMapSquare.getFace(layer);
            assert headFace != null; // getHeadMapSquare() would have been cleared in this case
            final ImageIcon img = headFace.getMagicMapImageIcon();
            final int dx = headMapSquare.getX()-map.getMapSquare(x, y).getX();
            final int dy = headMapSquare.getY()-map.getMapSquare(x, y).getY();
            assert dx > 0 || dy > 0;
            final int sx = img.getIconWidth()-TILE_SIZE*(dx+1);
            final int sy = img.getIconHeight()-TILE_SIZE*(dy+1);
            g.drawImage(img.getImage(),
                px, py, px+TILE_SIZE, py+TILE_SIZE,
                sx, sy, sx+TILE_SIZE, sy+TILE_SIZE,
                null);
        }

        final Face f = map.getFace(x, y, layer);
        if (f != null)
        {
            final ImageIcon img = f.getMagicMapImageIcon();
            final int sx = img.getIconWidth();
            final int sy = img.getIconHeight();
            g.drawImage(img.getImage(),
                px, py, px+TILE_SIZE, py+TILE_SIZE,
                sx-TILE_SIZE, sy-TILE_SIZE, sx, sy,
                null);
        }
    }

    /**
     * Paint the player location.
     *
     * @param g The graphics to paint to.
     */
    private void markPlayer(final Graphics2D g)
    {
        g.setColor(Color.RED);
        g.fillRect(offsetX+((CrossfireServerConnection.MAP_WIDTH-1)/2)*TILE_SIZE, offsetY+((CrossfireServerConnection.MAP_HEIGHT-1)/2)*TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
}

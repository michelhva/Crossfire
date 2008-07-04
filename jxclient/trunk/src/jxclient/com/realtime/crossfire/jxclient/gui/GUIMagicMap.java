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
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.mapupdater.MapListener;
import com.realtime.crossfire.jxclient.mapupdater.MapscrollListener;
import com.realtime.crossfire.jxclient.mapupdater.NewmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireCommandMagicmapEvent;
import com.realtime.crossfire.jxclient.server.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireMap2Command;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MapSizeListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.util.Set;
import javax.swing.ImageIcon;

/**
 * Displays magic map results. Fallback for unknown tiles is the normal map
 * contents.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIMagicMap extends GUIElement
{
    /**
     * The size of one tile in pixels.
     */
    private static final int TILE_SIZE = 4;

    /**
     * The {@link CfMapUpdater} instance to use.
     */
    private final CfMapUpdater mapUpdater;

    /**
     * The {@link FacesManager} instance for looking up faces.
     */
    private final FacesManager facesManager;

    /**
     * The map width in tiles.
     */
    private int mapWidth;

    /**
     * The map height in tiles.
     */
    private int mapHeight;

    /**
     * The x offset of the tile representing the player.
     */
    private final int playerX;

    /**
     * The y offset of the tile representing the player.
     */
    private final int playerY;

    /**
     * The x offset for the visible map area.
     */
    private int offsetX;

    /**
     * The y offset for the visible map area.
     */
    private int offsetY;

    /**
     * The colors for displaying magic map data.
     */
    private static final Color[] tileColors = new Color[]
    {
        Color.BLACK,
        Color.WHITE,
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.PINK,
        Color.GRAY,
        Color.ORANGE,
        Color.CYAN,
        Color.MAGENTA,
        Color.DARK_GRAY,
        Color.DARK_GRAY,
        Color.DARK_GRAY,
        Color.DARK_GRAY,
        Color.DARK_GRAY,
    };
    static
    {
        assert CrossfireCommandMagicmapEvent.FACE_COLOR_MASK+1 == tileColors.length;
    }

    /**
     * The {@link CrossfireMagicmapListener} registered to receive mapgicmap
     * commands.
     */
    private final CrossfireMagicmapListener crossfireMagicmapListener = new CrossfireMagicmapListener()
    {
        /** {@inheritDoc} */
        public void commandMagicmapReceived(final CrossfireCommandMagicmapEvent evt)
        {
            synchronized (bufferedImageSync)
            {
                int datapos = evt.getPos();
                final byte[] data = evt.getData();
                final Graphics2D g = bufferedImage.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                final int offsetX = playerX-evt.getPX()*TILE_SIZE;
                final int offsetY = playerY-evt.getPY()*TILE_SIZE;
                for (int y = 0; y < evt.getHeight(); y++)
                {
                    for (int x = 0; x < evt.getWidth(); x++)
                    {
                        g.setColor(tileColors[data[datapos]&CrossfireCommandMagicmapEvent.FACE_COLOR_MASK]);
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
     * The {@link MapscrollListener} used to track player position
     * changes into the magic map.
     */
    private final MapscrollListener mapscrollListener = new MapscrollListener()
    {
        /** {@inheritDoc} */
        public void mapScrolled(final int dx, final int dy)
        {
            synchronized (bufferedImageSync)
            {
                final Graphics2D g = bufferedImage.createGraphics();
                final CfMap map = mapUpdater.getMap();
                final int dxPixels = dx*TILE_SIZE;
                final int dyPixels = dy*TILE_SIZE;
                if (Math.abs(dxPixels) >= getWidth() || Math.abs(dyPixels) >= getHeight())
                {
                    redrawTiles(g, map, 0, 0, getWidth()/TILE_SIZE, getHeight()/TILE_SIZE);
                }
                else
                {
                    g.copyArea(dxPixels <= 0 ? 0 : dxPixels, dyPixels <= 0 ? 0 : dyPixels, dxPixels == 0 ? getWidth() : getWidth()-Math.abs(dxPixels), dyPixels == 0 ? getHeight() : getHeight()-Math.abs(dyPixels), -dxPixels, -dyPixels);
                    g.setColor(Color.BLACK);
                    if (dxPixels < 0)
                    {
                        redrawTiles(g, map, 0, 0, -dxPixels/TILE_SIZE, getHeight()/TILE_SIZE);
                    }
                    else if (dxPixels > 0)
                    {
                        redrawTiles(g, map, getWidth()/TILE_SIZE-dxPixels/TILE_SIZE, 0, getWidth()/TILE_SIZE, getHeight()/TILE_SIZE);
                    }
                    if (dyPixels < 0)
                    {
                        redrawTiles(g, map, 0, 0, getWidth()/TILE_SIZE, -dyPixels/TILE_SIZE);
                    }
                    else if (dyPixels > 0)
                    {
                        redrawTiles(g, map, 0, getHeight()/TILE_SIZE-dyPixels/TILE_SIZE, getWidth()/TILE_SIZE, getHeight()/TILE_SIZE);
                    }
                }
                redrawSquare(g, map, (mapWidth-1)/2-dx, (mapHeight-1)/2-dy);
                markPlayer(g);
                g.dispose();
            }
            setChanged();
        }
    };

    /**
     * The {@link MapListener} registered to receive map updates.
     */
    private final MapListener mapListener = new MapListener()
    {
        /** {@inheritDoc} */
        public void mapChanged(final CfMap map, final Set<CfMapSquare> changedSquares)
        {
            synchronized (bufferedImageSync)
            {
                final int x0 = map.getOffsetX();
                final int y0 = map.getOffsetY();
                final Graphics2D g = bufferedImage.createGraphics();
                for (final CfMapSquare mapSquare : changedSquares)
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
     * The {@link NewmapListener} registered to receive newmap
     * commands.
     */
    private final NewmapListener newmapListener = new NewmapListener()
    {
        /** {@inheritDoc} */
        public void commandNewmapReceived()
        {
            synchronized (bufferedImageSync)
            {
                final Graphics2D g = bufferedImage.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                markPlayer(g);
                g.dispose();
            }
            setChanged();
        }
    };

    /**
     * The {@link MapSizeListener} registered to receive changes of the map
     * view size.
     */
    private final MapSizeListener mapSizeListener = new MapSizeListener()
    {
        /** {@inheritDoc} */
        public void mapSizeChanged(final int mapWidth, final int mapHeight)
        {
            GUIMagicMap.this.mapWidth = mapWidth;
            GUIMagicMap.this.mapHeight = mapHeight;
            offsetX = playerX-((mapWidth-1)/2)*TILE_SIZE;
            offsetY = playerY-((mapHeight-1)/2)*TILE_SIZE;
            synchronized (bufferedImageSync)
            {
                final Graphics2D g = bufferedImage.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                redrawTiles(g, mapUpdater.getMap(), 0, 0, getWidth()/TILE_SIZE, getHeight()/TILE_SIZE);
                g.dispose();
            }
        }
    };

    /**
     * Creates a new instance.
     * @param window the <code>JXCWindow</code> this element belongs to
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen
     * @param y the y-coordinate for drawing this element to screen
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param crossfireServerConnection the server connection to monitor
     * @param mapUpdater the map updater instance to use
     * @param facesManager the faces manager instance to use
     */
    public GUIMagicMap(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final CrossfireServerConnection crossfireServerConnection, final CfMapUpdater mapUpdater, final FacesManager facesManager)
    {
        super(window, name, x, y, w, h, Transparency.TRANSLUCENT);
        if (w <= 0 || h <= 0) throw new IllegalArgumentException("area must be non-empty");
        if (w%TILE_SIZE != 0) throw new IllegalArgumentException("width is not a multiple of "+TILE_SIZE);
        if (h%TILE_SIZE != 0) throw new IllegalArgumentException("height is not a multiple of "+TILE_SIZE);
        if ((w/TILE_SIZE)%2 != 1) throw new IllegalArgumentException("width is not an odd number of tiles");
        if ((h/TILE_SIZE)%2 != 1) throw new IllegalArgumentException("height is not an odd number of tiles");
        this.mapUpdater = mapUpdater;
        this.facesManager = facesManager;
        playerX = w/2-TILE_SIZE/2;
        playerY = h/2-TILE_SIZE/2;

        crossfireServerConnection.addMapSizeListener(mapSizeListener);
        mapSizeListener.mapSizeChanged(crossfireServerConnection.getMapWidth(), crossfireServerConnection.getMapHeight());

        crossfireServerConnection.addCrossfireMagicmapListener(crossfireMagicmapListener);
        mapUpdater.addCrossfireNewmapListener(newmapListener);
        mapUpdater.addCrossfireMapscrollListener(mapscrollListener);
        mapUpdater.addCrossfireMapListener(mapListener);
    }

    /**
     * Redraws a rectangular area of tiles.
     * @param g the graphics to draw into
     * @param map the map to draw
     * @param x0 the left edge to redraw (inclusive)
     * @param y0 the top edge to redraw (inclusive)
     * @param x1 the right edge to redraw (exclusive)
     * @param y1 the bottom edge to redraw (exclusive)
     */
    private void redrawTiles(final Graphics2D g, final CfMap map, final int x0, final int y0, final int x1, final int y1)
    {
        for (int x = x0; x < x1; x++)
        {
            for (int y = y0; y < y1; y++)
            {
                redrawSquare(g, map, x-offsetX/TILE_SIZE, y-offsetY/TILE_SIZE);
            }
        }
    }

    /**
     * Redraws one square completely black.
     * @param g the graphics to draw into
     * @param x the x-coordinate of the square to clear
     * @param y the y-coordinate of the square to clear
     */
    private void cleanSquare(final Graphics2D g, final int x, final int y)
    {
        g.setColor(Color.BLACK);
        g.fillRect(x*TILE_SIZE+offsetX, y*TILE_SIZE+offsetY, TILE_SIZE, TILE_SIZE);
    }

    /**
     * Redraws one square.
     * @param g the graphics to draw into
     * @param map the map to draw
     * @param x the x-coordinate of the map tile to redraw
     * @param y the y-coordinate of the map tile to redraw
     */
    private void redrawSquare(final Graphics2D g, final CfMap map, final int x, final int y)
    {
        cleanSquare(g, x, y);
        for (int layer = 0; layer < CrossfireMap2Command.NUM_LAYERS; layer++)
        {
            redrawSquare(g, map, x, y, layer);
        }
        if (map.isFogOfWar(x, y) || x < 0 || y < 0 || x >= mapWidth || y >= mapHeight)
        {
            g.setColor(GUIMap.FOG_OF_WAR_COLOR);
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
     * Redraws one layer of a square.
     * @param g the graphics to draw into
     * @param map the map to draw
     * @param x the x coordinate of the square to redraw
     * @param y the y coordinate of the square to redraw
     * @param layer the layer to redraw
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
            final ImageIcon img = facesManager.getMagicMapImageIcon(headFace.getFaceNum());
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

        final Face face = map.getFace(x, y, layer);
        if (face != null)
        {
            final ImageIcon img = facesManager.getMagicMapImageIcon(face.getFaceNum());
            final int sx = img.getIconWidth();
            final int sy = img.getIconHeight();
            g.drawImage(img.getImage(),
                px, py, px+TILE_SIZE, py+TILE_SIZE,
                sx-TILE_SIZE, sy-TILE_SIZE, sx, sy,
                null);
        }
    }

    /**
     * Paints the player location.
     * @param g the graphics to paint to
     */
    private void markPlayer(final Graphics2D g)
    {
        g.setColor(Color.RED);
        g.fillRect(playerX, playerY, TILE_SIZE, TILE_SIZE);
    }

    /** {@inheritDoc} */
    @Override
    protected void render(final Graphics g)
    {
    }
}

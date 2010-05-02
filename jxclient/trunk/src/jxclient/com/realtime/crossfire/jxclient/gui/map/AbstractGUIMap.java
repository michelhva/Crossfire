/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.map;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesProvider;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.mapupdater.MapListener;
import com.realtime.crossfire.jxclient.mapupdater.MapscrollListener;
import com.realtime.crossfire.jxclient.mapupdater.NewmapListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.MapSizeListener;
import com.realtime.crossfire.jxclient.server.crossfire.messages.Map2;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import com.realtime.crossfire.jxclient.util.MathUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.util.Set;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for {@link GUIElement}s that display map views.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractGUIMap extends GUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link CfMapUpdater} instance to use.
     */
    @NotNull
    private final CfMapUpdater mapUpdater;

    /**
     * The {@link FacesProvider} for looking up faces.
     */
    @NotNull
    private final FacesProvider facesProvider;

    /**
     * The {@link CrossfireServerConnection} to monitor.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The map width in squares.
     */
    private int mapWidth = 0;

    /**
     * The map height in squares.
     */
    private int mapHeight = 0;

    /**
     * The size of one tile.
     */
    private final int tileSize;

    /**
     * The x offset of the tile representing the player.
     */
    private int playerX = 0;

    /**
     * The y offset of the tile representing the player.
     */
    private int playerY = 0;

    /**
     * The x offset for drawing the square at coordinate 0 of the map.
     */
    private int offsetX = 0;

    /**
     * The y offset for drawing the square at coordinate 0 of the map.
     */
    private int offsetY = 0;

    /**
     * The tile x coordinate where map drawing starts. May be positive if the
     * map view is larger than the gui's area.
     */
    private int displayMinX = 0;

    /**
     * The tile x coordinate where map drawing ends. May be less than {@link
     * #mapWidth} if the map view is larger than the gui's area.
     */
    private int displayMaxX = 0;

    /**
     * The tile y coordinate where map drawing starts. May be positive if the
     * map view is larger than the gui's area.
     */
    private int displayMinY = 0;

    /**
     * The tile y coordinate where map drawing ends. May be less than {@link
     * #mapWidth} if the map view is larger than the gui's area.
     */
    private int displayMaxY = 0;

    /**
     * The distance the leftmost visible tile reaches outside the map window.
     * <code>-{@link #tileSize}<displayMinOffsetX<=0</code>.
     */
    private int displayMinOffsetX = 0;

    /**
     * The number of pixels that are visible in the rightmost visible tile.
     * <code>0<=displayMaxOffsetX<{@link #tileSize}</code>.
     */
    private int displayMaxOffsetX = 0;

    /**
     * The distance the topmost visible tile reaches outside the map window.
     * <code>-{@link #tileSize}<displayMinOffsetX<=0</code>.
     */
    private int displayMinOffsetY = 0;

    /**
     * The number of pixels that are visible in the bottommost visible tile.
     * <code>0<=displayMaxOffsetY<{@link #tileSize}</code>.
     */
    private int displayMaxOffsetY = 0;

    /**
     * The {@link MapListener} registered to receive map updates.
     */
    @NotNull
    private final MapListener mapListener = new MapListener() {
        /** {@inheritDoc} */
        @Override
        public void mapChanged(@NotNull final CfMap map, @NotNull final Set<CfMapSquare> changedSquares) {
            final int x0 = map.getOffsetX();
            final int y0 = map.getOffsetY();
            synchronized (bufferedImageSync) {
                final Graphics g = createBufferGraphics();
                try {
                    for (final CfMapSquare mapSquare : changedSquares) {
                        final int x = mapSquare.getX()+x0;
                        if (displayMinX <= x && x < displayMaxX) {
                            final int y = mapSquare.getY()+y0;
                            if (displayMinY <= y && y < displayMaxY) {
                                redrawSquare(g, map, x, y);
                            }
                        }
                    }
                    markPlayer(g, 0, 0);
                } finally {
                    g.dispose();
                }
            }
            setChanged();
        }
    };

    /**
     * The {@link NewmapListener} registered to receive newmap commands.
     */
    @NotNull
    private final NewmapListener newmapListener = new NewmapListener() {
        /** {@inheritDoc} */
        @Override
        public void commandNewmapReceived() {
            synchronized (bufferedImageSync) {
                final Graphics g = createBufferGraphics();
                try {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(DarknessColors.FOG_OF_WAR_COLOR);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    markPlayer(g, 0, 0);
                } finally {
                    g.dispose();
                }
            }
            setChanged();
        }
    };

    /**
     * The {@link MapscrollListener} registered to receive map_scroll commands.
     */
    @NotNull
    private final MapscrollListener mapscrollListener = new MapscrollListener() {
        /** {@inheritDoc} */
        @Override
        public void mapScrolled(final int dx, final int dy) {
            synchronized (bufferedImageSync) {
                final Graphics g = createBufferGraphics();
                try {
                    updateScrolledMap(g, dx, dy);
                } finally {
                    g.dispose();
                }
            }
            setChanged();
        }
    };

    /**
     * The {@link MapSizeListener} registered to detect map size changes.
     */
    @NotNull
    private final MapSizeListener mapSizeListener = new MapSizeListener() {
        /** {@inheritDoc} */
        @Override
        public void mapSizeChanged(final int mapWidth, final int mapHeight) {
            setMapSize(mapWidth, mapHeight);
        }
    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param extent the extent of this element
     * @param mapUpdater the map updater instance to use
     * @param facesProvider the faces provider for looking up faces
     * @param crossfireServerConnection the server connection to monitor
     */
    protected AbstractGUIMap(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Extent extent, @NotNull final CfMapUpdater mapUpdater, @NotNull final FacesProvider facesProvider, @NotNull final CrossfireServerConnection crossfireServerConnection) {
        super(tooltipManager, elementListener, name, extent, Transparency.OPAQUE);
        tileSize = facesProvider.getSize();
        this.mapUpdater = mapUpdater;
        this.facesProvider = facesProvider;
        this.crossfireServerConnection = crossfireServerConnection;
        this.crossfireServerConnection.addMapSizeListener(mapSizeListener);
        this.mapUpdater.addCrossfireMapListener(mapListener);
        this.mapUpdater.addCrossfireNewmapListener(newmapListener);
        this.mapUpdater.addCrossfireMapscrollListener(mapscrollListener);
        setMapSize(crossfireServerConnection.getMapWidth(), crossfireServerConnection.getMapHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        crossfireServerConnection.removeMapSizeListener(mapSizeListener);
        mapUpdater.removeCrossfireNewmapListener(newmapListener);
        mapUpdater.removeCrossfireMapscrollListener(mapscrollListener);
        mapUpdater.removeCrossfireMapListener(mapListener);
    }

    /**
     * Redraws the complete map view.
     * @param g the graphics to draw into
     */
    private void redrawAll(@NotNull final Graphics g) {
        redrawTiles(g, mapUpdater.getMap(), displayMinX, displayMinY, displayMaxX, displayMaxY);
    }

    /**
     * Redraws all non-dirty tiles.
     * @param g the graphics to draw into
     * @param map the map
     */
    private void redrawAllUnlessDirty(@NotNull final Graphics g, @NotNull final CfMap map) {
        redrawTilesUnlessDirty(g, map, displayMinX-offsetX/tileSize, displayMinY-offsetY/tileSize, displayMaxX-offsetX/tileSize, displayMaxY-offsetY/tileSize);
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
    private void redrawTiles(@NotNull final Graphics g, @NotNull final CfMap map, final int x0, final int y0, final int x1, final int y1) {
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                redrawSquare(g, map, x-offsetX/tileSize, y-offsetY/tileSize);
            }
        }
    }

    /**
     * Redraws a rectangular area of non-dirty tiles.
     * @param g the graphics to draw into
     * @param map the map to draw
     * @param x0 the left edge to redraw (inclusive)
     * @param y0 the top edge to redraw (inclusive)
     * @param x1 the right edge to redraw (exclusive)
     * @param y1 the bottom edge to redraw (exclusive)
     */
    private void redrawTilesUnlessDirty(@NotNull final Graphics g, @NotNull final CfMap map, final int x0, final int y0, final int x1, final int y1) {
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                redrawSquareUnlessDirty(g, map, x, y);
            }
        }
    }

    /**
     * Redraws one square completely black.
     * @param g the graphics to draw into
     * @param x the x-coordinate of the square to clear
     * @param y the y-coordinate of the square to clear
     */
    private void cleanSquare(@NotNull final Graphics g, final int x, final int y) {
        g.setColor(Color.BLACK);
        g.fillRect(offsetX+x*tileSize, offsetY+y*tileSize, tileSize, tileSize);
    }

    /**
     * Redraws one square if it is not dirty. This function is called for tiles
     * that need to be repainted due to scrolling. Skipping dirty squares
     * prevents multiple repainting.
     * @param g the graphics to draw into
     * @param map the map to draw
     * @param x the x-coordinate of the square to clear
     * @param y the y-coordinate of the square to clear
     */
    protected void redrawSquareUnlessDirty(@NotNull final Graphics g, @NotNull final CfMap map, final int x, final int y) {
        if (!map.getMapSquare(x, y).isDirty()) {
            redrawSquare(g, map, x, y);
        }
    }

    /**
     * Redraws one square.
     * @param g the graphics to draw into
     * @param map the map to draw
     * @param x the x-coordinate of the map tile to redraw
     * @param y the y-coordinate of the map tile to redraw
     */
    protected void redrawSquare(@NotNull final Graphics g, @NotNull final CfMap map, final int x, final int y) {
        cleanSquare(g, x, y);
        final CfMapSquare mapSquare = map.getMapSquare(x, y);
        redrawSquare(g, x, y, mapSquare);
        if (map.isFogOfWar(x, y) || x < 0 || y < 0 || x >= mapWidth || y >= mapHeight) {
            g.setColor(DarknessColors.FOG_OF_WAR_COLOR);
            g.fillRect(offsetX+x*tileSize, offsetY+y*tileSize, tileSize, tileSize);
        }
        final int darkness = map.getDarkness(x, y);
        if (darkness < CfMapSquare.DARKNESS_FULL_BRIGHT) {
            g.setColor(DarknessColors.getDarknessColor(darkness));
            g.fillRect(offsetX+x*tileSize, offsetY+y*tileSize, tileSize, tileSize);
        }
    }

    /**
     * Redraws one layer of a square.
     * @param g the graphics to draw into
     * @param x the x coordinate of the square to redraw
     * @param y the y coordinate of the square to redraw
     * @param mapSquare the map square
     */
    private void redrawSquare(@NotNull final Graphics g, final int x, final int y, @NotNull final CfMapSquare mapSquare) {
        final int px = offsetX+x*tileSize;
        final int py = offsetY+y*tileSize;
        final int mapSquareX = mapSquare.getX();
        final int mapSquareY = mapSquare.getY();
        for (int layer = 0; layer < Map2.NUM_LAYERS; layer++) {
            final CfMapSquare headMapSquare = mapSquare.getHeadMapSquare(layer);
            if (headMapSquare != null) {
                final Face headFace = headMapSquare.getFace(layer);
                assert headFace != null; // getHeadMapSquare() would have been cleared in this case
                final int dx = headMapSquare.getX()-mapSquareX;
                final int dy = headMapSquare.getY()-mapSquareY;
                assert dx > 0 || dy > 0;
                paintImage(g, headFace, px, py, tileSize*dx, tileSize*dy);
            }

            final Face face = mapSquare.getFace(layer);
            if (face != null) {
                paintImage(g, face, px, py, 0, 0);
            }
        }
    }

    /**
     * Paints a face into a tile.
     * @param g the graphics to draw into
     * @param face the face to draw
     * @param px the x coordinate of the square to redraw
     * @param py the y coordinate of the square to redraw
     * @param offsetX the x-offset for shifting the original face
     * @param offsetY the y-offset for shifting the original face
     */
    private void paintImage(@NotNull final Graphics g, @NotNull final Face face, final int px, final int py, final int offsetX, final int offsetY) {
        final ImageIcon imageIcon = facesProvider.getImageIcon(face.getFaceNum());
        final int sx = imageIcon.getIconWidth()-offsetX;
        final int sy = imageIcon.getIconHeight()-offsetY;
        g.drawImage(imageIcon.getImage(), px, py, px+tileSize, py+tileSize, sx-tileSize, sy-tileSize, sx, sy, null);
    }

    /**
     * Paints the player location.
     * @param g the graphics to paint to
     * @param dx the x distance to map has just scrolled
     * @param dy the y distance to map has just scrolled
     */
    protected abstract void markPlayer(@NotNull final Graphics g, final int dx, final int dy);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void render(@NotNull final Graphics2D g) {
    }

    /**
     * Sets the map size.
     * @param mapWidth the map width in squares
     * @param mapHeight the map height in squares
     */
    protected void setMapSize(final int mapWidth, final int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        final int nX = MathUtils.divRoundUp(playerX, tileSize);
        displayMinOffsetX = playerX-nX*tileSize;
        assert -tileSize < displayMinOffsetX && displayMinOffsetX <= 0;
        assert (playerX-displayMinOffsetX)%tileSize == 0;
        displayMinX = (mapWidth-1)/2-nX;
        final int tilesX = MathUtils.divRoundUp(getWidth()-displayMinOffsetX, tileSize);
        displayMaxX = displayMinX+tilesX;
        assert (displayMaxX-displayMinX)*tileSize >= getWidth();
        assert (displayMaxX-displayMinX)*tileSize-getWidth() < 2*tileSize;
        displayMaxOffsetX = MathUtils.mod(-displayMinOffsetX-getWidth(), tileSize);
        offsetX = displayMinOffsetX-displayMinX*tileSize;

        final int nY = MathUtils.divRoundUp(playerY, tileSize);
        displayMinOffsetY = playerY-nY*tileSize;
        assert -tileSize < displayMinOffsetY && displayMinOffsetY <= 0;
        assert (playerY-displayMinOffsetY)%tileSize == 0;
        displayMinY = (mapHeight-1)/2-nY;
        final int tilesY = MathUtils.divRoundUp(getHeight()-displayMinOffsetY, tileSize);
        displayMaxY = displayMinY+tilesY;
        assert (displayMaxY-displayMinY)*tileSize >= getHeight();
        assert (displayMaxY-displayMinY)*tileSize-getHeight() < 2*tileSize;
        displayMaxOffsetY = MathUtils.mod(-displayMinOffsetY-getHeight(), tileSize);
        offsetY = displayMinOffsetY-displayMinY*tileSize;

        synchronized (bufferedImageSync) {
            if (hasBufferedImage()) {
                final Graphics g = createBufferGraphics();
                try {
                    redrawAll(g);
                } finally {
                    g.dispose();
                }
            }
        }
    }

    /**
     * Returns the x offset of the tile representing the player.
     * @return the x offset
     */
    public int getPlayerX() {
        return playerX;
    }

    /**
     * Returns the y offset of the tile representing the player.
     * @return the y offset
     */
    public int getPlayerY() {
        return playerY;
    }

    /**
     * Returns the x offset for drawing the square at coordinate 0 of the map.
     * @return the x offset
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Returns the y offset for drawing the square at coordinate 0 of the map.
     * @return the y offset
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateResolution(final int screenWidth, final int screenHeight) {
        super.updateResolution(screenWidth, screenHeight);
        playerX = getWidth()/2-tileSize/2;
        playerY = getHeight()/2-tileSize/2;
        setMapSize(mapWidth, mapHeight);
    }

    /**
     * Updates the map display after the map contents have scrolled.
     * @param g the graphics to update
     * @param dx the x-distance
     * @param dy the y-distance
     */
    private void updateScrolledMap(@NotNull final Graphics g, final int dx, final int dy) {
        final CfMap map = mapUpdater.getMap();
        if (Math.abs(dx)*tileSize >= getWidth() || Math.abs(dy)*tileSize >= getHeight()) {
            redrawAllUnlessDirty(g, map);
        } else {
            final int x = dx > 0 ? dx : 0;
            final int w = dx > 0 ? -dx : dx;
            final int y = dy > 0 ? dy : 0;
            final int h = dy > 0 ? -dy : dy;
            g.copyArea(x*tileSize, y*tileSize, getWidth()+w*tileSize, getHeight()+h*tileSize, -dx*tileSize, -dy*tileSize);

            if (dx > 0) {
                final int ww = (displayMaxOffsetX == 0 ? 0 : 1)+dx;
                redrawTilesUnlessDirty(g, map, displayMaxX-ww, displayMinY, displayMaxX, displayMaxY);
            } else if (dx < 0) {
                final int ww = (displayMinOffsetX == 0 ? 0 : 1)-dx;
                redrawTilesUnlessDirty(g, map, displayMinX, displayMinY, displayMinX+ww, displayMaxY);
            }
            if (dy > 0) {
                final int hh = (displayMaxOffsetY == 0 ? 0 : 1)+dy;
                redrawTilesUnlessDirty(g, map, displayMinX, displayMaxY-hh, displayMaxX, displayMaxY);
            } else if (dy < 0) {
                final int hh = (displayMinOffsetY == 0 ? 0 : 1)-dy;
                redrawTilesUnlessDirty(g, map, displayMinX, displayMinY, displayMaxX, displayMinY+hh);
            }
            markPlayer(g, dx, dy);
        }
    }

}

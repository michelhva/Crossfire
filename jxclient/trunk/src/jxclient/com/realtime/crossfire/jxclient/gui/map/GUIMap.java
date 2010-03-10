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
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireMap2Command;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.MapSizeListener;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.util.Set;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

/**
 * Display the map view. It supports both normal sized (32x32 pixel) and double
 * sized (64x64 pixel) sized tiles.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIMap extends GUIElement {

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
     * The map width in tiles.
     */
    private int mapWidth = 0;

    /**
     * The map height in tiles.
     */
    private int mapHeight = 0;

    /**
     * The size of one tile.
     */
    private final int tileSize;

    /**
     * The tile x-coordinate where map drawing starts. May be positive if the
     * map view is larger than the gui's area.
     */
    private int displayMinX = 0;

    /**
     * The tile x-coordinate where map drawing ends. May be less than {@link
     * #mapWidth} if the map view is larger than the gui's area.
     */
    private int displayMaxX = 0;

    /**
     * The tile y-coordinate where map drawing starts. May be positive if the
     * map view is larger than the gui's area.
     */
    private int displayMinY = 0;

    /**
     * The tile y-coordinate where map drawing ends. May be less than {@link
     * #mapWidth} if the map view is larger than the gui's area.
     */
    private int displayMaxY = 0;

    /**
     * The x-offset for drawing the left-most tile. Positive if the gui's area
     * is larger than the map view; negative otherwise.
     */
    private int offsetX = 0;

    /**
     * The y-offset for drawing the left-most tile. Positive if the gui's area
     * is larger than the map view; negative otherwise.
     */
    private int offsetY = 0;

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
                if (Math.abs(dx) >= mapWidth || Math.abs(dy) >= mapHeight) {
                    setChanged();
                    return;
                }

                final int x;
                final int w;
                if (dx > 0) {
                    x = 0;
                    w = mapWidth-dx;
                } else {
                    x = -dx;
                    w = mapWidth+dx;
                }
                final int y;
                final int h;
                if (dy > 0) {
                    y = 0;
                    h = mapHeight-dy;
                } else {
                    y = -dy;
                    h = mapHeight+dy;
                }

                final Graphics g = createBufferGraphics();
                try {
                    g.copyArea(offsetX+(x+dx)*tileSize, offsetY+(y+dy)*tileSize, w*tileSize, h*tileSize, -dx*tileSize, -dy*tileSize);

                    for (int yy = displayMinY; yy < Math.min(y, displayMaxY); yy++) {
                        for (int xx = displayMinX; xx < displayMaxX; xx++) {
                            redrawSquare(g, mapUpdater.getMap(), xx, yy);
                        }
                    }

                    for (int yy = Math.max(y+h, displayMinY); yy < displayMaxY; yy++) {
                        for (int xx = displayMinX; xx < displayMaxX; xx++) {
                            redrawSquare(g, mapUpdater.getMap(), xx, yy);
                        }
                    }

                    for (int yy = Math.max(y, displayMinY); yy < Math.min(y+h, displayMaxY); yy++) {
                        for (int xx = displayMinX; xx < Math.min(x, displayMaxX); xx++) {
                            redrawSquare(g, mapUpdater.getMap(), xx, yy);
                        }

                        for (int xx = Math.max(x+w, displayMinX); xx < displayMaxX; xx++) {
                            redrawSquare(g, mapUpdater.getMap(), xx, yy);
                        }
                    }
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
     * @param x the x-coordinate for drawing this element to screen
     * @param y the y-coordinate for drawing this element to screen
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param mapUpdater the map updater instance to use
     * @param facesProvider the faces provider for looking up faces
     * @param crossfireServerConnection the server connection to monitor
     */
    public GUIMap(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final CfMapUpdater mapUpdater, @NotNull final FacesProvider facesProvider, @NotNull final CrossfireServerConnection crossfireServerConnection) {
        super(tooltipManager, elementListener, name, x, y, w, h, Transparency.OPAQUE);
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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        redrawTiles(g, mapUpdater.getMap(), displayMinX, displayMinY, displayMaxX, displayMaxY);
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
                redrawSquare(g, map, x, y);
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
     * Redraws one square if it has been changed. If it is unchanged ({@link
     * CfMapSquare#dirty} is unset), nothing is drawn.
     * @param g the graphics to draw into
     * @param map the map to draw
     * @param x the x-coordinate of the map tile to redraw
     * @param y the y-coordinate of the map tile to redraw
     */
    private void redrawSquare(@NotNull final Graphics g, @NotNull final CfMap map, final int x, final int y) {
        cleanSquare(g, x, y);
        final CfMapSquare mapSquare = map.getMapSquare(x, y);
        redrawSquare(g, x, y, mapSquare);
        if (map.isFogOfWar(x, y) || x < 0 || y < 0 || x >= mapWidth || y >= mapHeight) {
            g.setColor(DarknessColors.FOG_OF_WAR_COLOR);
            g.fillRect(offsetX+x*tileSize, offsetY+y*tileSize, tileSize, tileSize);
        }
        final int darkness = map.getDarkness(x, y);
        if (darkness < 255) {
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
        for (int layer = 0; layer < CrossfireMap2Command.NUM_LAYERS; layer++) {
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
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            if (e.getX() >= offsetX && e.getY() >= offsetY) {
                final int dx = (e.getX()-offsetX)/tileSize-mapWidth/2;
                final int dy = (e.getY()-offsetY)/tileSize-mapHeight/2;
                if (dx < mapWidth && dy < mapHeight) {
                    crossfireServerConnection.sendLookat(dx, dy);
                }
            }
            break;

        case MouseEvent.BUTTON2:
        case MouseEvent.BUTTON3:
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void render(@NotNull final Graphics g) {
    }

    /**
     * Sets the map size.
     * @param mapWidth the map width in tiles
     * @param mapHeight the map height in tiles
     */
    private void setMapSize(final int mapWidth, final int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        if (mapWidth*tileSize < getWidth()) {
            displayMinX = 0;
            displayMaxX = mapWidth;
            offsetX = (getWidth()-mapWidth*tileSize)/2;
        } else {
            final int n = (getWidth()+tileSize-1)/(2*tileSize);
            final int effectiveW = (1+2*n)*tileSize;
            displayMinX = (mapWidth-(2*n+1))/2;
            displayMaxX = displayMinX+(1+2*n);
            offsetX = (getWidth()-effectiveW)/2;
        }

        if (mapHeight*tileSize < getHeight()) {
            displayMinY = 0;
            displayMaxY = mapHeight;
            offsetY = (getHeight()-mapHeight*tileSize)/2;
        } else {
            final int n = (getHeight()+tileSize-1)/(2*tileSize);
            final int effectiveH = (1+2*n)*tileSize;
            displayMinY = (mapHeight-(2*n+1))/2;
            displayMaxY = displayMinY+(1+2*n);
            offsetY = (getHeight()-effectiveH)/2;
        }

        synchronized (bufferedImageSync) {
            final Graphics g = createBufferGraphics();
            try {
                redrawAll(g);
            } finally {
                g.dispose();
            }
        }
    }

}

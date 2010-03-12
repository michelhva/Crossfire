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

import com.realtime.crossfire.jxclient.faces.FacesProvider;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Graphics;
import org.jetbrains.annotations.NotNull;

/**
 * Displays magic map results. Fallback for unknown tiles is the normal map
 * contents.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIMagicMap extends AbstractGUIMap {

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
     * The size of one tile.
     */
    private final int tileSize;

    /**
     * The {@link CrossfireServerConnection} to monitor.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The x square coordinate of the player tile.
     */
    private int playerOffsetX;

    /**
     * The y square coordinate of the player tile.
     */
    private int playerOffsetY;

    /**
     * The colors for displaying magic map data.
     */
    @NotNull
    private static final Color[] tileColors = {
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

    static {
        assert CrossfireMagicmapListener.FACE_COLOR_MASK+1 == tileColors.length;
    }

    /**
     * The {@link CrossfireMagicmapListener} registered to receive mapgicmap
     * commands.
     */
    @NotNull
    private final CrossfireMagicmapListener crossfireMagicmapListener = new CrossfireMagicmapListener() {
        /** {@inheritDoc} */
        @Override
        public void commandMagicmapReceived(final int width, final int height, final int px, final int py, @NotNull final byte[] data, final int pos) {
            int datapos = pos;
            synchronized (bufferedImageSync) {
                final Graphics g = createBufferGraphics();
                try {
                    final int offsetX = getPlayerX()-px*tileSize;
                    final int offsetY = getPlayerY()-py*tileSize;
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            if (data[datapos] != 0) {
                                g.setColor(tileColors[data[datapos]&FACE_COLOR_MASK]);
                                final int sx = offsetX+x*tileSize;
                                final int sy = offsetY+y*tileSize;
                                g.fillRect(sx, sy, tileSize, tileSize);
                            }
                            datapos++;
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
    public GUIMagicMap(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final CfMapUpdater mapUpdater, @NotNull final FacesProvider facesProvider, @NotNull final CrossfireServerConnection crossfireServerConnection) {
        super(tooltipManager, elementListener, name, x, y, w, h, mapUpdater, facesProvider, crossfireServerConnection);
        this.mapUpdater = mapUpdater;
        tileSize = facesProvider.getSize();
        this.crossfireServerConnection = crossfireServerConnection;
        this.crossfireServerConnection.addCrossfireMagicmapListener(crossfireMagicmapListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        crossfireServerConnection.removeCrossfireMagicmapListener(crossfireMagicmapListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void markPlayer(@NotNull final Graphics g, final int dx, final int dy) {
        if (dx != 0 || dy != 0) {
            redrawSquare(g, mapUpdater.getMap(), playerOffsetX-dx, playerOffsetY-dy);
        }
        g.setColor(Color.RED);
        g.fillRect(getPlayerX(), getPlayerY(), tileSize, tileSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setMapSize(final int mapWidth, final int mapHeight) {
        super.setMapSize(mapWidth, mapHeight);
        playerOffsetX = (mapWidth-1)/2;
        playerOffsetY = (mapHeight-1)/2;
    }

}

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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.map;

import com.realtime.crossfire.jxclient.faces.FacesProvider;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.map.MapUpdaterState;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Displays a small map view.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIMiniMap extends AbstractGUIMap {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link MapUpdaterState} instance to use.
     */
    @NotNull
    private final MapUpdaterState mapUpdaterState;

    /**
     * The map width in squares.
     */
    private final int width;

    /**
     * The map height in squares.
     */
    private final int height;

    /**
     * The size of one tile.
     */
    private final int tileSize;

    /**
     * The colors for displaying magic map data.
     */
    @NotNull
    private static final Color[] TILE_COLORS = {
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

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param mapUpdaterState the map updater state instance to use
     * @param facesProvider the faces provider for looking up faces
     * @param width the map width in squares
     * @param height the map height in squares
     */
    public GUIMiniMap(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final MapUpdaterState mapUpdaterState, @NotNull final FacesProvider facesProvider, final int width, final int height) {
        super(tooltipManager, elementListener, name, mapUpdaterState, facesProvider, null);
        this.mapUpdaterState = mapUpdaterState;
        this.width = width;
        this.height = height;
        tileSize = facesProvider.getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintSquareBackground(@NotNull final Graphics g, final int px, final int py, final boolean hasImage, @NotNull final CfMapSquare mapSquare) {
        final Color color;
        if (hasImage) {
            color = Color.BLACK;
        } else {
            final int colorIndex = mapSquare.getColor();
            color = 0 <= colorIndex && colorIndex < TILE_COLORS.length ? TILE_COLORS[colorIndex] : Color.BLACK;
        }
        paintColoredSquare(g, color, px, py);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void markPlayer(@NotNull final Graphics g, final int dx, final int dy) {
        if (dx != 0 || dy != 0) {
            final int playerOffsetX = (getMapWidth()-1)/2;
            final int playerOffsetY = (getMapHeight()-1)/2;
            final int mapSquareX = playerOffsetX-dx;
            final int mapSquareY = playerOffsetY-dy;
            final CfMap map = mapUpdaterState.getMap();
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (map) {
                redrawSquare(g, map.getMapSquare(mapSquareX, mapSquareY), map, mapSquareX, mapSquareY);
            }
        }
        g.setColor(Color.RED);
        g.fillRect(getPlayerX(), getPlayerY(), tileSize, tileSize);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width*tileSize, height*tileSize);
    }

}

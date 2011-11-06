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
import com.realtime.crossfire.jxclient.faces.SmoothFaces;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.map.MapUpdaterState;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.util.MathUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Display the map view. It supports both normal sized (32x32 pixel) and double
 * sized (64x64 pixel) sized tiles.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIMap extends AbstractGUIMap {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link CrossfireServerConnection} to monitor.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The size of one tile.
     */
    private final int tileSize;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param mapUpdaterState the map updater state instance to use
     * @param facesProvider the faces provider for looking up faces
     * @param crossfireServerConnection the server connection to monitor
     * @param smoothFaces the smooth faces to use
     */
    public GUIMap(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final MapUpdaterState mapUpdaterState, @NotNull final FacesProvider facesProvider, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final SmoothFaces smoothFaces) {
        super(tooltipManager, elementListener, name, mapUpdaterState, facesProvider, new SmoothingRenderer(smoothFaces, facesProvider));
        this.crossfireServerConnection = crossfireServerConnection;
        tileSize = facesProvider.getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintSquareBackground(@NotNull final Graphics g, final int px, final int py, final boolean hasImage, @NotNull final CfMapSquare mapSquare) {
        paintColoredSquare(g, Color.BLACK, px, py);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void markPlayer(@NotNull final Graphics g, final int dx, final int dy) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            final int dx1 = e.getX()-getOffsetX();
            final int dy1 = e.getY()-getOffsetY();
            if (dx1 >= 0 && dy1 >= 0) {
                final int mapWidth = getMapWidth();
                final int mapHeight = getMapHeight();
                final int dx2 = dx1/tileSize-mapWidth/2;
                final int dy2 = dy1/tileSize-mapHeight/2;
                if (dx2 < mapWidth && dy2 < mapHeight) {
                    crossfireServerConnection.sendLookat(dx2, dy2);
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
    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getMapWidth()*tileSize, getMapHeight()*tileSize);
    }

    /**
     * Returns the minimal map width in squares needed to fill the map area.
     * @return the map width in squares
     */
    public int getPreferredMapWidth() {
        return MathUtils.divRoundUp(getWidth(), tileSize);
    }

    /**
     * Returns the minimal map height in squares needed to fill the map area.
     * @return the map height in squares
     */
    public int getPreferredMapHeight() {
        return MathUtils.divRoundUp(getHeight(), tileSize);
    }

}

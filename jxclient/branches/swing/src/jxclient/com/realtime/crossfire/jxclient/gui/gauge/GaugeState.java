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

package com.realtime.crossfire.jxclient.gui.gauge;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The state of a gauge.
 * @author Andreas Kirschbaum
 */
public class GaugeState {

    /**
     * The image representing a full gauge.
     */
    @Nullable
    private final Image fullImage;

    /**
     * The image representing a more-than-empty gauge.
     */
    @Nullable
    private final Image negativeImage;

    /**
     * The preferred size of this component.
     */
    @NotNull
    private final Dimension preferredSize;

    /**
     * The x-offset for drawing.
     */
    private final int dx;

    /**
     * The y-offset for drawing.
     */
    private int dy;

    /**
     * The width of the "filled" area.
     */
    private int filledW = 0;

    /**
     * The height of the "filled" area.
     */
    private int filledH = 0;

    /**
     * The x-coordinate of the "filled" area.
     */
    private int filledX = 0;

    /**
     * The y-coordinate of the "filled" area.
     */
    private int filledY = 0;

    /**
     * The image for painting the "filled" area.
     */
    @Nullable
    private Image filledPicture = null;

    /**
     * Creates a new instance.
     * @param fullImage the image representing a full gauge
     * @param negativeImage the image representing a more-than-empty gauge; if
     * set to <code>null</code> the gauge remains in empty state
     * @param dx the x-offset for drawing
     * @param dy the y-offset for drawing
     */
    public GaugeState(@Nullable final Image fullImage, @Nullable final Image negativeImage, final int dx, final int dy) {
        this.fullImage = fullImage;
        this.negativeImage = negativeImage;
        final int preferredWidth = Math.max(Math.max(fullImage == null ? 1 : fullImage.getWidth(null), negativeImage == null ? 1 : negativeImage.getWidth(null)), 1);
        final int preferredHeight = Math.max(Math.max(fullImage == null ? 1 : fullImage.getHeight(null), negativeImage == null ? 1 : negativeImage.getHeight(null)), 1);
        preferredSize = new Dimension(preferredWidth, preferredHeight);
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Sets the y-offset for drawing.
     * @param dy the y-offset for drawing
     */
    public void setDy(final int dy) {
        this.dy = dy;
    }

    /**
     * Updates the values from a {@link Orientation} state.
     * @param orientation the state
     * @return whether the state has changed
     */
    public boolean setValues(@NotNull final Orientation orientation) {
        final int newFilledX = orientation.getX();
        final int newFilledY = orientation.getY();
        final int newFilledW = orientation.getW();
        final int newFilledH = orientation.getH();
        final Image newFilledPicture = orientation.isValid() ? orientation.isNegativeImage() ? negativeImage : fullImage : null;

        if (filledX == newFilledX && filledY == newFilledY && filledW == newFilledW && filledH == newFilledH && filledPicture == newFilledPicture) {
            return false;
        }

        filledX = newFilledX;
        filledY = newFilledY;
        filledW = newFilledW;
        filledH = newFilledH;
        filledPicture = newFilledPicture;
        return true;
    }

    /**
     * Draws the gauge image into the given graphics context.
     * @param g the graphics context
     */
    public void draw(@NotNull final Graphics g) {
        if (filledPicture != null) {
            g.drawImage(filledPicture, filledX+dx, filledY+dy, filledX+dx+filledW, filledY+dy+filledH, filledX, filledY, filledX+filledW, filledY+filledH, null);
        }
    }

    /**
     * Returns the preferred size.
     * @return the preferred size
     */
    @NotNull
    public Dimension getPreferredSize() {
        return new Dimension(preferredSize);
    }

}

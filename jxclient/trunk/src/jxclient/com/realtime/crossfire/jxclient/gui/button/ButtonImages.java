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

package com.realtime.crossfire.jxclient.gui.button;

import java.awt.Graphics;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 * A set of images to form a button image. The button image consists of a left,
 * a middle, and a right part. Left and right parts have fixed size, the middle
 * part is clipped or repeated to the button's width.
 * @author Andreas Kirschbaum
 */
public class ButtonImages {

    /**
     * The left border of the button.
     */
    @NotNull
    private final Image imageLeft;

    /**
     * The background of the middle part of the button.
     */
    @NotNull
    private final Image imageMiddle;

    /**
     * The right border of the button.
     */
    @NotNull
    private final Image imageRight;

    /**
     * The button height.
     */
    private final int height;

    /**
     * Creates a new instance.
     * @param imageLeft the left image
     * @param imageMiddle the middle image
     * @param imageRight the right image
     */
    public ButtonImages(@NotNull final Image imageLeft, @NotNull final Image imageMiddle, @NotNull final Image imageRight) {
        if (imageLeft.getHeight(null) != imageMiddle.getHeight(null)) {
            throw new IllegalArgumentException("left image height is "+imageLeft.getHeight(null)+" but middle image height is "+imageMiddle.getHeight(null));
        }
        if (imageMiddle.getHeight(null) != imageRight.getHeight(null)) {
            throw new IllegalArgumentException("middle image height is "+imageMiddle.getHeight(null)+" but right image height is "+imageRight.getHeight(null));
        }

        this.imageLeft = imageLeft;
        this.imageMiddle = imageMiddle;
        this.imageRight = imageRight;
        height = imageMiddle.getHeight(null);
    }

    /**
     * Returns the button's height.
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Draws the button.
     * @param g the graphics to paint into
     * @param w the button width
     */
    public void render(@NotNull final Graphics g, final int w) {
        g.drawImage(imageLeft, 0, 0, null);
        g.drawImage(imageRight, w-imageRight.getWidth(null), 0, null);

        final int middleWidth = imageMiddle.getWidth(null);
        int tmpWidth = w-imageLeft.getWidth(null)-imageRight.getWidth(null);
        int tmpX = imageLeft.getWidth(null);
        while (tmpWidth > 0) {
            final int thisWidth = Math.min(tmpWidth, middleWidth);
            g.drawImage(imageMiddle, tmpX, 0, tmpX+thisWidth, height, 0, 0, thisWidth, height, null);
            tmpX += thisWidth;
            tmpWidth -= thisWidth;
        }
    }

}

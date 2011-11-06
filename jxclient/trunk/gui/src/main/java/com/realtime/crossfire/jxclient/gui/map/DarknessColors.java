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

import com.realtime.crossfire.jxclient.map.CfMapSquare;
import java.awt.Color;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for converting darkness values into colors.
 * @author Andreas Kirschbaum
 */
public class DarknessColors {

    /**
     * The minimum darkness alpha value; it is used for completely black tiles.
     * The maximum is 0.0F for full bright tiles.
     */
    private static final float MAX_DARKNESS_ALPHA = 0.7F;

    /**
     * The color to use for overlaying fog-of-war tiles.
     */
    @NotNull
    public static final Color FOG_OF_WAR_COLOR = new Color(0, 0, 0.5F, 0.5F);

    /**
     * Cache to lookup darkness overlay colors. Maps darkness value to overlay
     * color. Not yet allocated entries are set to <code>null</code>.
     */
    @NotNull
    private static final Color[] DARKNESS_COLORS = new Color[256];

    /**
     * Private constructor to prevent instantiation.
     */
    private DarknessColors() {
    }

    /**
     * Returns an overlay color for a darkness value.
     * @param darkness the darkness value between 0 and 255
     * @return the overlay color
     */
    @NotNull
    public static Color getDarknessColor(final int darkness) {
        synchronized (DARKNESS_COLORS) {
            if (DARKNESS_COLORS[darkness] == null) {
                final float alpha = MAX_DARKNESS_ALPHA*(CfMapSquare.DARKNESS_FULL_BRIGHT-darkness)/(float)CfMapSquare.DARKNESS_FULL_BRIGHT;
                DARKNESS_COLORS[darkness] = new Color(0, 0, 0, alpha);
            }

            return DARKNESS_COLORS[darkness];
        }
    }

}

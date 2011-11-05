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

package com.realtime.crossfire.jxclient.util;

import java.awt.Dimension;
import java.awt.DisplayMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Information about JXClient's screen/window resolution. It consists of a size
 * (width and height) and whether the exact resolution should be used, or if a
 * similar resolution is allowed.
 * @author Andreas Kirschbaum
 */
public class Resolution {

    /**
     * The width in pixel.
     */
    private final int width;

    /**
     * The height in pixel.
     */
    private final int height;

    /**
     * Creates a new instance.
     * @param width the width in pixels
     * @param height the height in pixels
     */
    public Resolution(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Creates a new instance from string representation. The string
     * representation is of the format "1024x768"; it is the format returned
     * from {@link #toString()}.
     * @param str the string representation
     * @return the <code>Resolution instance</code>, or <code>null</code> if the
     *         string representation is invalid
     */
    @Nullable
    public static Resolution parse(@NotNull final String str) {
        final String[] tmp = str.split("x", -1);
        if (tmp.length != 2) {
            return null;
        }
        final int width;
        final int height;
        try {
            width = Integer.parseInt(tmp[0]);
            height = Integer.parseInt(tmp[1]);
        } catch (final NumberFormatException ignored) {
            return null;
        }

        return new Resolution(width, height);
    }

    /**
     * Returns the width in pixels.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height in pixels.
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns whether this resolution matches a {@link DisplayMode}'s
     * resolution.
     * @param displayMode the display mode
     * @return if the resolutions match
     */
    public boolean equalsDisplayMode(@NotNull final DisplayMode displayMode) {
        return width == displayMode.getWidth() && height == displayMode.getHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != Resolution.class) {
            return false;
        }
        final Resolution resolution = (Resolution)obj;
        return resolution.width == width && resolution.height == height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return width^(height<<16)^(height>>16);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return width+"x"+height;
    }

    /**
     * Returns the resolution as a {@link Dimension} instance.
     * @return the dimension instance
     */
    @NotNull
    public Dimension asDimension() {
        return new Dimension(width, height);
    }

}

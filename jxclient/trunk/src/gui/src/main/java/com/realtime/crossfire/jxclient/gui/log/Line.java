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

package com.realtime.crossfire.jxclient.gui.log;

import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the contents of one text line. A text line consists of a sequence of
 * {@link Segment Segments}.
 * @author Andreas Kirschbaum
 */
public class Line implements Iterable<Segment> {

    /**
     * The segments this line consists of. The first segment should be displayed
     * first; subsequent segments are to be displayed without padding.
     */
    @NotNull
    private final List<Segment> segments = new ArrayList<>();

    /**
     * The total height of this line. Set to {@code -1} if unknown.
     */
    private int height = -1;

    /**
     * Appends a {@link Segment} to the end of the line.
     * @param text the text to display
     * @param bold whether bold face is enabled
     * @param italic whether italic face is enabled
     * @param underline whether underlining is enabled
     * @param font the font to use
     * @param color the color to use; {@code null} means "default color"
     */
    public void addSegment(@NotNull final String text, final boolean bold, final boolean italic, final boolean underline, @NotNull final FontID font, @Nullable final Color color) {
        segments.add(new TextSegment(text, bold, italic, underline, font, color));
    }

    @NotNull
    @Override
    public Iterator<Segment> iterator() {
        return Collections.unmodifiableList(segments).iterator();
    }

    /**
     * Returns the last segment.
     * @return the last segment or {@code null} if the line is empty
     */
    @Nullable
    public Segment getLastSegment() {
        return segments.isEmpty() ? null : segments.get(segments.size()-1);
    }

    /**
     * Removes the last segment. The line must not be empty.
     */
    public void removeLastSegment() {
        segments.remove(segments.size()-1);
    }

    /**
     * Returns the height of this line. Returns {@code -1} until {@link
     * #setHeight(int)} was called.
     * @return the height of this line
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the height of this line.
     * @param height the height
     */
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * Updates the cached attributes of some {@link Segment Segments}.
     * @param begin the index of the first segment to update
     * @param end the index of the first segment not to update
     * @param y the top border of the line's bounding box
     * @param fonts the fonts instance to use
     * @param context the font render context to use
     */
    public void updateAttributes(final int begin, final int end, final int y, @NotNull final Fonts fonts, @NotNull final FontRenderContext context) {
        for (int i = begin; i < end; i++) {
            final Segment segment = segments.get(i);
            segment.updateAttributes(fonts, context);
            segment.setY(y);
        }
    }

}

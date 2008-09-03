//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.gui.log;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Manages the contents of one text line. A text line consists of a sequence of
 * {@link Segment}s.
 *
 * @author Andreas Kirschbaum
 */
public class Line implements Iterable<Segment>
{
    /**
     * The segments this line consists of. The first segment should be
     * displayed first; subsequent segments are to be displyed without padding.
     */
    private final List<Segment> segments = new ArrayList<Segment>();

    /**
     * The total height of this line. Set to <code>-1</code> if unknown.
     */
    private int height = -1;

    /**
     * Append a {@link Segment} to the end of the line.
     *
     * @param segment The segment to append.
     */
    public void addSegment(final Segment segment)
    {
        segments.add(segment);
    }

    /** {@inheritDoc} */
    public Iterator<Segment> iterator()
    {
        return Collections.unmodifiableList(segments).iterator();
    }

    /**
     * Return whether the line contains no segments.
     *
     * @return Whether the line contains no segments.
     */
    public boolean isEmpty()
    {
        return segments.isEmpty();
    }

    /**
     * Return the last segment. The line must not be empty.
     *
     * @return The last segment.
     */
    public Segment getLastSegment()
    {
        return segments.get(segments.size()-1);
    }

    /**
     * Remove the last segment. The line must not be empty.
     */
    public void removeLastSegment()
    {
        segments.remove(segments.size()-1);
    }

    /**
     * Return the height of this line. Returns <code>-1</code> until {@link
     * #setHeight(int)} was called.
     *
     * @return The height of this line.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Return the height of this line.
     *
     * @param height The height.
     */
    public void setHeight(final int height)
    {
        this.height = height;
    }

    /**
     * Update the cached attributes of some {@link Segment}s.
     *
     * @param begin The index of the first segment to update.
     * @param end The index of the first segment not to update.
     * @param y The top border of the line's bounding box.
     * @param minY The minimum bottom offset of all segments' bounding boxes.
     * @param fonts the fonts instance to use
     * @param context the font render context to use
     */
    public void updateAttributes(final int begin, final int end, final int y, final int minY, final Fonts fonts, final FontRenderContext context)
    {
        for (int i = begin; i < end; i++)
        {
            final Segment segment = segments.get(i);
            final String text = segment.getText();
            final Font font = segment.getFont(fonts);
            final LineMetrics lineMetrics = font.getLineMetrics(text, context);
            segment.setY(y-minY);
            segment.setUnderlineOffset(Math.round(lineMetrics.getUnderlineOffset()));
        }
    }
}

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
     * Return the number of segments of this line.
     *
     * @return The number of segments.
     */
    public int size()
    {
        return segments.size();
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
     * Return one {@link Segment} by segment index. The first segment has the
     * index <code>0</code>.
     *
     * @param index The segment index.
     *
     * @return The segment.
     */
    public Segment getSegment(final int index)
    {
        return segments.get(index);
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
}

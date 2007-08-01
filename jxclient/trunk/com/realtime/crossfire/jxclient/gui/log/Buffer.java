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
import java.util.ListIterator;

/**
 * Manages the contents of the contents of a log window. It consists of a list
 * of {@link Line}s.
 *
 * @author Andreas Kirschbaum
 */
public class Buffer implements Iterable<Line>
{
    /**
     * The lines in display order.
     */
    private final List<Line> lines = new ArrayList<Line>();

    /**
     * Append a {@link Line} to the end of the buffer.
     *
     * @param line The line to append.
     */
    public void addLine(final Line line)
    {
        lines.add(line);
    }

    /**
     * Return one {@link Line} by line index. The first line has the index
     * <code>0</code>.
     *
     * @param line The line index.
     *
     * @return The line.
     */
    public Line getLine(final int line)
    {
        return lines.get(line);
    }

    /** {@inheritDoc} */
    public Iterator<Line> iterator()
    {
        return Collections.unmodifiableList(lines).iterator();
    }

    /**
     * Return a {@link ListIterator} for the lines in this buffer.
     *
     * @param line The initial line index of the list iterator.
     *
     * @return The list iterator.
     */
    public ListIterator<Line> listIterator(final int line)
    {
        return Collections.unmodifiableList(lines).listIterator(line);
    }

    /**
     * Return the number of lines.
     *
     * @return The number of lines.
     */
    public int size()
    {
        return lines.size();
    }
}

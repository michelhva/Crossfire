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

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
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
     * The maximum number of lines the buffer can hold.
     */
    public static final int MAX_LINES = 250;

    /**
     * The listeners to notify about changes.
     */
    private final List<BufferListener> listeners = new ArrayList<BufferListener>();

    /**
     * The {@link Fonts} instance for looking up fonts.
     */
    private final Fonts fonts;

    /**
     * The {@link FontRenderContext} associated to {@link #buffer}.
     */
    private final FontRenderContext context;

    /**
     * The width to render.
     */
    private final int renderWidth;

    /**
     * The lines in display order.
     */
    private final List<Line> lines = new ArrayList<Line>();

    /**
     * Create a new instance.
     *
     * @param fonts The <code>Fonts</code> instance for looking up fonts.
     *
     * @param context The <code>FontRenderContext</code> to use.
     *
     * @param renderWidth The width to render.
     */
    public Buffer(final Fonts fonts, final FontRenderContext context, final int renderWidth)
    {
        this.fonts = fonts;
        this.context = context;
        this.renderWidth = renderWidth;
    }

    /**
     * Append a {@link Line} to the end of the buffer.
     *
     * @param line The line to append.
     */
    public void addLine(final Line line)
    {
        line.setHeight(calculateHeight(line));
        lines.add(line);
        fireChangedEvent();
    }

    /**
     * Prune excess lines.
     */
    public void prune()
    {
        while (lines.size() > MAX_LINES)
        {
            lines.remove(0);
        }
        fireChangedEvent();
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

    /**
     * Determine the height of a {@link Line} in pixels.
     *
     * @param line The line to process.
     *
     * @return The height in pixels.
     */
    private int calculateHeight(final Line line)
    {
        int height = 0;
        int x = 0;
        int minY = 0;
        int maxY = 0;
        int beginIndex = 0;
        final int imax = line.size();
        for (int i = 0; i < imax; i++)
        {
            final Segment segment = line.getSegment(i);
            final String text = segment.getText();
            final Font font = segment.getFont(fonts);
            final Rectangle2D rect = font.getStringBounds(text, context);
            final int width = (int)Math.round(rect.getWidth());
            if (x != 0 && x+width > renderWidth)
            {
                updateAttributes(line, beginIndex, i, height, minY, maxY);

                height += maxY-minY;
                x = 0;
                minY = 0;
                maxY = 0;
                beginIndex = i;
            }

            segment.setX(x);
            segment.setY(height);
            segment.setWidth(width);

            x += width;
            minY = (int)Math.min(minY, Math.round(rect.getY()));
            maxY = (int)Math.max(maxY, Math.round(rect.getY()+rect.getHeight()));
        }

        updateAttributes(line, beginIndex, imax, height, minY, maxY);
        height += maxY-minY;

        return height;
    }

    /**
     * Update the cached attributes of some {@link Segment}s of a {@link Line}.
     *
     * @param line The line to process.
     *
     * @param begin The index of the first segment to update.
     *
     * @param end The index of the first segment not to update.
     *
     * @param y The top border of the line's bounding box.
     *
     * @param minY The minimum bottom offset of all segments' bounding boxes.
     *
     * @param maxY The maximum top offset of all segments' bounding boxes.
     */
    private void updateAttributes(final Line line, final int begin, final int end, final int y, final int minY, final int maxY)
    {
        for (int i = begin; i < end; i++)
        {
            final Segment segment = line.getSegment(i);
            final String text = segment.getText();
            final Font font = segment.getFont(fonts);
            final LineMetrics lineMetrics = font.getLineMetrics(text, context);
            segment.setHeight(maxY-minY);
            segment.setY(y-minY);
            segment.setUnderlineOffset(Math.round(lineMetrics.getUnderlineOffset()));
        }
    }

    /**
     * Add a listener to notify of changes.
     *
     * @param listener The listener.
     */
    public void addBufferListener(final BufferListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Notify all listeners about changed lines.
     */
    private void fireChangedEvent()
    {
        for (final BufferListener listener : listeners)
        {
            listener.linesChanged();
        }
    }
}

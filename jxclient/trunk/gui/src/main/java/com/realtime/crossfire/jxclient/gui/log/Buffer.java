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

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the contents of the contents of a log window. It consists of a list
 * of {@link Line Lines}.
 * @author Andreas Kirschbaum
 */
public class Buffer {

    /**
     * The maximum number of lines the buffer can hold.
     */
    public static final int MAX_LINES = 250;

    /**
     * The minimal height of a line in pixels. Normally applies to empty lines.
     */
    public static final int MIN_LINE_HEIGHT = 8;

    /**
     * The listeners to notify about changes.
     */
    @NotNull
    private final EventListenerList2<BufferListener> listeners = new EventListenerList2<BufferListener>(BufferListener.class);

    /**
     * The {@link Fonts} instance for looking up fonts.
     */
    @NotNull
    private final Fonts fonts;

    /**
     * The {@link FontRenderContext} to use.
     */
    @NotNull
    private final FontRenderContext context;

    /**
     * The width to render.
     */
    private int renderWidth;

    /**
     * The lines in display order.
     */
    @NotNull
    private final List<Line> lines = new CopyOnWriteArrayList<Line>();

    /**
     * The total height of all {@link #lines}.
     */
    private int totalHeight = 0;

    /**
     * Object to synchronized access to {@link #lines} and {@link
     * #totalHeight}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The number of repetitions of the previously added line of text.
     */
    private int lastCount = 0;

    /**
     * The color of the previously added line of text.
     */
    @Nullable
    private Color lastColor = null;

    /**
     * The contents of the previously added line of text.
     */
    @NotNull
    private String lastText = "";

    /**
     * Creates a new instance.
     * @param fonts the <code>Fonts</code> instance for looking up fonts
     * @param context the <code>FontRenderContext</code> to use
     * @param renderWidth the width to render
     */
    public Buffer(@NotNull final Fonts fonts, @NotNull final FontRenderContext context, final int renderWidth) {
        this.fonts = fonts;
        this.context = context;
        this.renderWidth = renderWidth;
    }

    /**
     * Updates the width to render.
     * @param renderWidth the width to render
     */
    public void setRenderWidth(final int renderWidth) {
        if (this.renderWidth == renderWidth) {
            return;
        }

        this.renderWidth = renderWidth;

        synchronized (sync) {
            totalHeight = 0;
            for (final Line line : lines) {
                final int height = calculateHeight(line);
                line.setHeight(height);
                totalHeight += height;
            }
        }
    }

    /**
     * Clears all lines from the buffer.
     */
    public void clear() {
        final List<Line> removedLines;
        synchronized (sync) {
            removedLines = new ArrayList<Line>(lines);
            totalHeight = 0;
            lines.clear();
        }
        for (final BufferListener listener : listeners.getListeners()) {
            listener.linesRemoved(removedLines);
        }
        lastCount = 0;
        lastText = "";
        lastColor = null;
    }

    /**
     * Appends a {@link Line} to the end of the buffer.
     * @param line the line to append
     */
    public void addLine(@NotNull final Line line) {
        final int height = calculateHeight(line);
        line.setHeight(height);
        synchronized (sync) {
            totalHeight += height;
            lines.add(line);
        }

        for (final BufferListener listener : listeners.getListeners()) {
            listener.lineAdded();
        }
    }

    /**
     * Replaces the last {@link Line} of this buffer.
     * @param line the replacing line
     */
    public void replaceLine(@NotNull final Line line) {
        final int height = calculateHeight(line);
        line.setHeight(height);
        synchronized (sync) {
            totalHeight += height;
            final int lastIndex = lines.size()-1;
            totalHeight -= lines.get(lastIndex).getHeight();
            lines.set(lastIndex, line);
        }

        for (final BufferListener listener : listeners.getListeners()) {
            listener.lineReplaced();
        }
    }

    /**
     * Prunes excess lines.
     */
    public void prune() {
        final List<Line> removedLines;
        synchronized (sync) {
            if (lines.size() <= MAX_LINES) {
                return;
            }

            removedLines = new ArrayList<Line>(lines.size()-MAX_LINES);
            while (lines.size() > MAX_LINES) {
                final Line line = lines.remove(0);
                removedLines.add(line);
                totalHeight -= line.getHeight();
            }
        }
        for (final BufferListener listener : listeners.getListeners()) {
            listener.linesRemoved(removedLines);
        }
    }

    /**
     * Returns one {@link Line} by line index. The first line has the index
     * <code>0</code>.
     * @param line the line index
     * @return the line
     */
    @NotNull
    public Line getLine(final int line) {
        synchronized (sync) {
            return lines.get(line);
        }
    }

    /**
     * Returns the total height of all lines.
     * @return the total height
     */
    public int getTotalHeight() {
        synchronized (sync) {
            return Math.max(totalHeight, 1);
        }
    }

    /**
     * Returns an {@link Iterator} for the lines in this buffer. The caller must
     * hold {@link #sync}'s lock.
     * @return the iterator
     */
    @NotNull
    public Iterator<Line> iterator() {
        assert Thread.holdsLock(sync);
        return Collections.unmodifiableList(lines).iterator();
    }

    /**
     * Returns a {@link ListIterator} for the lines in this buffer.
     * @param line the initial line index of the list iterator
     * @return the list iterator
     */
    @NotNull
    public ListIterator<Line> listIterator(final int line) {
        assert Thread.holdsLock(sync);
        return Collections.unmodifiableList(lines).listIterator(line);
    }

    /**
     * Returns the number of lines.
     * @return the number of lines
     */
    public int size() {
        synchronized (sync) {
            return lines.size();
        }
    }

    /**
     * Determines the height of a {@link Line} in pixels.
     * @param line the line to process
     * @return the height in pixels
     */
    private int calculateHeight(@NotNull final Line line) {
        int height = 0;
        int x = 0;
        int minY = 0;
        int maxY = 0;
        int beginIndex = 0;
        int i = 0;
        for (final Segment segment : line) {
            final RectangularShape rectangle = segment.getSize(fonts, context);
            final int width = (int)Math.round(rectangle.getWidth());
            if (x != 0 && x+width > renderWidth) {
                line.updateAttributes(beginIndex, i, height-minY, fonts, context);

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
            minY = (int)Math.min(minY, Math.round(rectangle.getY()));
            maxY = (int)Math.max(maxY, Math.round(rectangle.getY()+rectangle.getHeight()));

            i++;
        }

        line.updateAttributes(beginIndex, i, height-minY, fonts, context);
        height += maxY-minY;

        return Math.max(MIN_LINE_HEIGHT, height);
    }

    /**
     * Adds a listener to notify of changes.
     * @param listener the listener
     */
    public void addBufferListener(@NotNull final BufferListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener to be notified of changes.
     * @param listener the listener
     */
    public void removeBufferListener(@NotNull final BufferListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns the object to synchronize on when calling {@link #iterator()} or
     * {@link #listIterator(int)}.
     * @return the object
     */
    @NotNull
    public Object getSyncObject() {
        return sync;
    }

    /**
     * Checks whether a new text line should be merged with a preceding line.
     * @param text the text line contents
     * @param color the text line color
     * @return whether the line should be merged
     */
    public boolean mergeLines(@NotNull final String text, @Nullable final Color color) {
        if (lastCount > 0 && text.equals(lastText) && (lastColor == null ? color == null : lastColor.equals(color))) {
            lastCount++;
            return true;
        }

        lastCount = 1;
        lastText = text;
        lastColor = color;
        return false;

    }

    /**
     * Returns the number of merged lines. Should not be called unless directly
     * after {@link #mergeLines(String, Color)} did freturn <code>true</code>.
     * @return the number of merged lines
     */
    public int getLastCount() {
        return lastCount;
    }

}

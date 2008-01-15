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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Encapsulates the state for rendering a {@link #Buffer} instance.
 *
 * @author Andreas Kirschbaum
 */
public class RenderState
{
    /**
     * The listener to notify about state changes.
     */
    private final RenderStateListener renderStateListener;

    /**
     * The rendered buffer.
     */
    private final Buffer buffer;

    /**
     * Records whether scrolling down is possible. It is updated when the
     * buffer is rendered.
     */
    private boolean canScrollDown = false;

    /**
     * Whether to display the bottom most text messages or lines starting at
     * {@link #topIndex}.
     */
    private boolean displayBottom = true;

    /**
     * The first line of {@link #buffer} to display.
     */
    private int topIndex = 0;

    /**
     * The number of pixels to shift the first displayed line.
     */
    private int topOffset = 0;

    /**
     * The listener to re-render the window contents after changes.
     */
    private BufferListener bufferListener = new BufferListener()
    {
        /** {@inheritDoc} */
        public void linesAdded(final Buffer buffer, final int lines)
        {
            assert lines > 0;

            if (displayBottom)
            {
                int dy = 0;
                final ListIterator<Line> it = buffer.listIterator(topIndex);
                for (int i = 0; i < lines; i++)
                {
                    dy += it.next().getHeight();
                }
                scrollDown(dy);
            }
            else
            {
                canScrollDown = true;
                renderStateListener.stateChanged();
            }
        }

        /** {@inheritDoc} */
        public void linesRemoved(final Buffer buffer, final List<Line> lines)
        {
            assert lines.size() > 0;

            int i = lines.size();
            final int j = Math.min(topIndex, i);
            i -= j;
            topIndex -= j;
            assert i <= 0 || topIndex <= 0;

            if (i > 0)
            {
                if (displayBottom)
                {
                    final Iterator<Line> it = lines.iterator();
                    while (i > 0)
                    {
                        topOffset -= it.next().getHeight();
                        i--;
                    }
                }
                else
                {
                    topOffset = 0;
                    recalc();
                }
            }

            renderStateListener.stateChanged();
        }
    };

    /**
     * Create a new instance.
     *
     * @param renderStateListener The listener to notify about state changes.
     *
     * @param buffer The rendered buffer.
     */
    public RenderState(final RenderStateListener renderStateListener, final Buffer buffer)
    {
        this.renderStateListener = renderStateListener;
        this.buffer = buffer;
        buffer.addBufferListener(bufferListener);
        recalc();
    }

    /**
     * Return the first line to render.
     *
     * @return The line index.
     */
    public int getTopIndex()
    {
        return topIndex;
    }

    /**
     * Return the pixel offset for the first line to render.
     *
     * @return The pixel offset.
     */
    public int getTopOffset()
    {
        return topOffset;
    }

    /**
     * Return whether the bottom most lines are displayed.
     *
     * @return Whether the bottom most lines are displayed.
     */
    public boolean isDisplayBottom()
    {
        return displayBottom;
    }

    /**
     * Reset the scrolling range to default values.
     */
    public void resetScroll()
    {
        displayBottom = true;
        recalc();
        renderStateListener.stateChanged();
    }

    /**
     * Scroll up by pixels.
     *
     * @param dy The number of pixels to scroll.
     */
    public void scrollUp(final int dy)
    {
        assert dy > 0;

        topOffset -= dy;
        while (topOffset < 0)
        {
            if (topIndex <= 0)
            {
                topOffset = 0;
                break;
            }

            topIndex--;
            topOffset += buffer.getLine(topIndex).getHeight();
        }
        displayBottom = false;
        recalc();
        renderStateListener.stateChanged();
    }

    /**
     * Scroll down by pixels.
     *
     * @param dy The number of pixels to scroll.
     */
    public void scrollDown(final int dy)
    {
        assert dy > 0;

        topOffset += dy;
        while (topIndex < buffer.size() && topOffset >= buffer.getLine(topIndex).getHeight())
        {
            topOffset -= buffer.getLine(topIndex).getHeight();
            topIndex++;
        }

        recalc();
        renderStateListener.stateChanged();
    }

    /**
     * Whether scrolling up is possible.
     */
    public boolean canScrollUp()
    {
        return topIndex > 0 || topOffset > 0;
    }

    /**
     * Whether scrolling down is possible.
     */
    public boolean canScrollDown()
    {
        return canScrollDown;
    }

    /**
     * Validate the rendering state.
     */
    private void recalc()
    {
        if (!displayBottom)
        {
            int y = -topOffset;
            final int h = renderStateListener.getHeight();
            final ListIterator<Line> it = buffer.listIterator(topIndex);
            while (y < h && it.hasNext())
            {
                y += it.next().getHeight();
            }
            if (it.hasNext() || y > h)
            {
                canScrollDown = y > h || it.hasNext();
            }
            else
            {
                displayBottom = true;
            }
        }

        if (displayBottom)
        {
            int y = renderStateListener.getHeight();
            topIndex = buffer.size();
            final ListIterator<Line> it = buffer.listIterator(topIndex);
            while (y > 0 && it.hasPrevious())
            {
                topIndex--;
                y -= it.previous().getHeight();
            }
            canScrollDown = false;
            topOffset = -y;
        }
    }
}

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

import java.util.List;

/**
 * Encapsulates the state for rendering a {@link Buffer} instance.
 *
 * @author Andreas Kirschbaum
 */
public class RenderStateManager
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
     * The first line of {@link #buffer} to display.
     */
    private int lastTopIndex = -1;

    /**
     * The number of pixels to shift the first displayed line.
     */
    private int lastTopOffset = -1;

    /**
     * The position in pixels of the viewable window. May be negative if not
     * enough lines are present to fill the whole window.
     */
    private int lastScrollPos = -1;

    private boolean lastCanScrollDown = false;

    private boolean lastCanScrollUp = false;

    private final RenderState renderState = new RenderState();

    /**
     * The listener to re-render the window contents after changes.
     */
    private final BufferListener bufferListener = new BufferListener()
    {
        /** {@inheritDoc} */
        public void linesAdded(final Buffer buffer, final int lines)
        {
            renderState.linesAdded(buffer, lines);
            fireChanges();
        }

        /** {@inheritDoc} */
        public void linesReplaced(final Buffer buffer, final int lines)
        {
            renderState.linesReplaced(buffer, lines);
            fireChanges();
        }

        /** {@inheritDoc} */
        public void linesRemoved(final Buffer buffer, final List<Line> lines)
        {
            renderState.linesRemoved(buffer, lines);
            fireChanges();
        }
    };

    /**
     * Create a new instance.
     *
     * @param renderStateListener The listener to notify about state changes.
     *
     * @param buffer The rendered buffer.
     */
    public RenderStateManager(final RenderStateListener renderStateListener, final Buffer buffer)
    {
        this.renderStateListener = renderStateListener;
        this.buffer = buffer;
        buffer.addBufferListener(bufferListener);
        renderState.setHeight(buffer, renderStateListener.getHeight());
        fireChanges();
    }

    /**
     * Return the first line to render.
     *
     * @return The line index.
     */
    public int getTopIndex()
    {
        return renderState.getTopIndex();
    }

    /**
     * Return the pixel offset for the first line to render.
     *
     * @return The pixel offset.
     */
    public int getTopOffset()
    {
        return renderState.getTopOffset();
    }

    public int getScrollPos()
    {
        return renderState.getScrollPos();
    }

    /**
     * Reset the scrolling range to default values.
     */
    public void resetScroll()
    {
        renderState.scrollToBottom(buffer);
        fireChanges();
    }

    /**
     * Scroll up by pixels.
     *
     * @param dy The number of pixels to scroll.
     */
    public void scrollUp(final int dy)
    {
        assert dy > 0;
        renderState.scrollTo(buffer, renderState.getScrollPos()-dy);
        fireChanges();
    }

    /**
     * Scroll down by pixels.
     *
     * @param dy The number of pixels to scroll.
     */
    public void scrollDown(final int dy)
    {
        assert dy > 0;
        renderState.scrollTo(buffer, renderState.getScrollPos()+dy);
        fireChanges();
    }

    /**
     * Scroll to a location.
     *
     * @param y The location.
     */
    public void scrollTo(final int y)
    {
        renderState.scrollTo(buffer, y);
        fireChanges();
    }

    /**
     * Whether scrolling up is possible.
     */
    public boolean canScrollUp()
    {
        return renderState.canScrollUp();
    }

    /**
     * Whether scrolling down is possible.
     */
    public boolean canScrollDown()
    {
        return renderState.canScrollDown();
    }

    private void fireChanges()
    {
        boolean fireChanges = false;

        if (lastTopIndex != renderState.getTopIndex())
        {
            lastTopIndex = renderState.getTopIndex();
            fireChanges = true;
        }

        if (lastTopOffset != renderState.getTopOffset())
        {
            lastTopOffset = renderState.getTopOffset();
            fireChanges = true;
        }

        if (lastScrollPos != renderState.getScrollPos())
        {
            lastScrollPos = renderState.getScrollPos();
            fireChanges = true;
        }

        if (lastCanScrollDown != renderState.canScrollDown())
        {
            lastCanScrollDown = renderState.canScrollDown();
            fireChanges = true;
        }

        if (lastCanScrollUp != renderState.canScrollUp())
        {
            lastCanScrollUp = renderState.canScrollUp();
            fireChanges = true;
        }

        if (renderState.mustRepaint() || fireChanges)
        {
            renderStateListener.stateChanged();
        }
    }
}

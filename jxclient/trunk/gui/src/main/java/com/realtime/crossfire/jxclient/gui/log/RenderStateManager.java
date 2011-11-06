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

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates the state for rendering a {@link Buffer} instance.
 * @author Andreas Kirschbaum
 */
public class RenderStateManager {

    /**
     * The listener to notify about state changes.
     */
    @NotNull
    private final RenderStateListener renderStateListener;

    /**
     * The rendered buffer.
     */
    @NotNull
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

    /**
     * The last known result of {@link RenderState#canScrollDown} for {@link
     * #renderState}. Used to detect changes.
     */
    private boolean lastCanScrollDown = false;

    /**
     * The last known result of {@link RenderState#canScrollUp} for {@link
     * #renderState}. Used to detect changes.
     */
    private boolean lastCanScrollUp = false;

    /**
     * The underlying {@link RenderState} instance.
     */
    @NotNull
    private final RenderState renderState = new RenderState();

    /**
     * The listener to re-render the window contents after changes.
     */
    @NotNull
    private final BufferListener bufferListener = new BufferListener() {

        @Override
        public void lineAdded() {
            renderState.linesAdded(buffer);
            fireChanges();
        }

        @Override
        public void lineReplaced() {
            renderState.linesReplaced(buffer);
            fireChanges();
        }

        @Override
        public void linesRemoved(@NotNull final List<Line> lines) {
            renderState.linesRemoved(buffer, lines);
            fireChanges();
        }

    };

    /**
     * Creates a new instance.
     * @param renderStateListener the listener to notify about state changes
     * @param buffer the rendered buffer
     */
    public RenderStateManager(@NotNull final RenderStateListener renderStateListener, @NotNull final Buffer buffer) {
        this.renderStateListener = renderStateListener;
        this.buffer = buffer;
        this.buffer.addBufferListener(bufferListener);
        fireChanges();
    }

    /**
     * Sets the viewable height in pixel.
     * @param height the viewable height
     */
    public void setHeight(final int height) {
        renderState.setHeight(buffer, height);
    }

    /**
     * Destroys this instance. Must be called when the instance is not needed
     * anymore.
     */
    public void dispose() {
        buffer.removeBufferListener(bufferListener);
    }

    /**
     * Returns the first line to render.
     * @return the line index
     */
    public int getTopIndex() {
        return renderState.getTopIndex();
    }

    /**
     * Returns the number of pixels to shift the first displayed line.
     * @return the pixel offset
     */
    public int getTopOffset() {
        return renderState.getTopOffset();
    }

    /**
     * Returns the location of the view area in pixels.
     * @return the location
     */
    public int getScrollPos() {
        return renderState.getScrollPos();
    }

    /**
     * Resets the scrolling range to default values.
     */
    public void resetScroll() {
        renderState.scrollToBottom(buffer);
        fireChanges();
    }

    /**
     * Scrolls up by pixels.
     * @param dy the number of pixels to scroll
     */
    public void scrollUp(final int dy) {
        assert dy > 0;
        renderState.scrollTo(buffer, renderState.getScrollPos()-dy);
        fireChanges();
    }

    /**
     * Scrolls down by pixels.
     * @param dy the number of pixels to scroll
     */
    public void scrollDown(final int dy) {
        assert dy > 0;
        renderState.scrollTo(buffer, renderState.getScrollPos()+dy);
        fireChanges();
    }

    /**
     * Scrolls to a location.
     * @param y the location
     */
    public void scrollTo(final int y) {
        renderState.scrollTo(buffer, y);
        fireChanges();
    }

    /**
     * Returns whether scrolling up is possible.
     * @return whether scrolling up is possible
     */
    public boolean canScrollUp() {
        return renderState.canScrollUp();
    }

    /**
     * Returns whether scrolling down is possible.
     * @return whether scrolling down is possible
     */
    public boolean canScrollDown() {
        return renderState.canScrollDown();
    }

    /**
     * Notifies listeners of changes.
     */
    private void fireChanges() {
        boolean fireChanges = false;

        if (lastTopIndex != renderState.getTopIndex()) {
            lastTopIndex = renderState.getTopIndex();
            fireChanges = true;
        }

        if (lastTopOffset != renderState.getTopOffset()) {
            lastTopOffset = renderState.getTopOffset();
            fireChanges = true;
        }

        if (lastScrollPos != renderState.getScrollPos()) {
            lastScrollPos = renderState.getScrollPos();
            fireChanges = true;
        }

        if (lastCanScrollDown != renderState.canScrollDown()) {
            lastCanScrollDown = renderState.canScrollDown();
            fireChanges = true;
        }

        if (lastCanScrollUp != renderState.canScrollUp()) {
            lastCanScrollUp = renderState.canScrollUp();
            fireChanges = true;
        }

        if (renderState.mustRepaint() || fireChanges) {
            renderStateListener.stateChanged();
        }
    }

}

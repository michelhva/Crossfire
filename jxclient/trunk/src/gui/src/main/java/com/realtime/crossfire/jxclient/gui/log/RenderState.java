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

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates the state for a scroll bar. The first line shown is the line
 * index {@link #topIndex}. If {@link #topOffset} is non-zero, it should be
 * shifted by this number of pixels. The current scrolling position is {@link
 * #scrollPos}.
 * @author Andreas Kirschbaum
 */
public class RenderState {

    /**
     * Synchronization object for accesses to all fields.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The height of the viewable area.
     */
    private int height = 1;

    /**
     * The first line to display in the viewable area. It is shifted by {@link
     * #topOffset} pixels.
     */
    private int topIndex;

    /**
     * The number of pixels to shift the first displayed line ({@link
     * #topIndex}. Positive values shift up, negative shift down. It's value is
     * either between zero and less than the height of line {@link #topIndex},
     * or it is negative if the rendered buffer does not contain enough lines to
     * fill the view area.
     */
    private int topOffset = -height;

    /**
     * The location of the view area relative to the buffer's total height.
     */
    private int scrollPos;

    /**
     * Whether scrolling up is possible.
     */
    private boolean canScrollUp;

    /**
     * Whether scrolling down is possible.
     */
    private boolean canScrollDown;

    /**
     * Whether the view has to be repainted even if no other value has changed.
     */
    private boolean mustRepaint = true;

    /**
     * Sets the the viewable height in pixel.
     * @param buffer the displayed buffer
     * @param h the viewable height
     */
    public void setHeight(@NotNull final Buffer buffer, final int h) {
        final int bufferHeight = buffer.getTotalHeight();
        synchronized (sync) {
            final int oldHeight = height;
            height = h;
            if (bufferHeight <= height) {
                scrollPos = 0;
                topIndex = 0;
                topOffset = 0;
                canScrollUp = false;
                canScrollDown = false;
            } else if (topOffset < 0 || scrollPos > bufferHeight-height || scrollPos == bufferHeight-oldHeight) {
                scrollToBottom(buffer);
            }
        }
    }

    /**
     * Some lines have been added to the buffer.
     * @param buffer the displayed buffer
     */
    public void linesAdded(@NotNull final Buffer buffer) {
        synchronized (sync) {
            if (topOffset < 0) {
                scrollToBottom(buffer);
            } else if (!canScrollDown) {
                scrollToBottom(buffer);
                mustRepaint = true;
            } else {
                mustRepaint = true;
            }
        }
    }

    /**
     * Some lines have been replaced at the end of the buffer.
     * @param buffer the displayed buffer
     */
    public void linesReplaced(@NotNull final Buffer buffer) {
        synchronized (sync) {
            if (topOffset < 0 || !canScrollDown) {
                scrollToBottom(buffer);
            }
            mustRepaint = true;
        }
    }

    /**
     * Some lines have been removed from the buffer.
     * @param buffer the displayed buffer
     * @param lines the number of lines that have been remove
     */
    public void linesRemoved(@NotNull final Buffer buffer, @NotNull final Collection<Line> lines) {
        final int bufferHeight = buffer.getTotalHeight();
        synchronized (sync) {
            if (bufferHeight <= height) {
                scrollPos = 0;
                topIndex = 0;
                topOffset = 0;
                canScrollUp = false;
                canScrollDown = false;
                mustRepaint = true;
            } else {
                for (Line line : lines) {
                    scrollPos -= line.getHeight();
                }
                topIndex -= lines.size();
                if (scrollPos < 0) {
                    scrollPos = 0;
                    topIndex = 0;
                    topOffset = 0;
                    canScrollUp = false;
                    mustRepaint = true;
                } else {
                    assert topIndex >= 0;
                    // canScrollUp is unaffected
                }
                // canScrollDown is unaffected
            }
        }
    }

    /**
     * Returns the index of the first line to display.
     * @return the line index
     */
    public int getTopIndex() {
        synchronized (sync) {
            return topIndex;
        }
    }

    /**
     * Returns the number of pixels to shift the first displayed line.
     * @return the pixel offset
     */
    public int getTopOffset() {
        synchronized (sync) {
            return topOffset;
        }
    }

    /**
     * Returns the location of the view area in pixels.
     * @return the location
     */
    public int getScrollPos() {
        synchronized (sync) {
            return scrollPos;
        }
    }

    /**
     * Returns whether scrolling up is possible.
     * @return whether scrolling up is possible
     */
    public boolean canScrollUp() {
        synchronized (sync) {
            return canScrollUp;
        }
    }

    /**
     * Returns whether scrolling down is possible.
     * @return whether scrolling down is possible
     */
    public boolean canScrollDown() {
        synchronized (sync) {
            return canScrollDown;
        }
    }

    /**
     * Returns whether the view should be repainted even if no other values have
     * changed. This function resets the flag; calling the function twice
     * returns {@code false} in the second call (if no other changes happen
     * concurrently).
     * @return whether the view should be repainted
     */
    public boolean mustRepaint() {
        synchronized (sync) {
            final boolean result = mustRepaint;
            mustRepaint = false;
            return result;
        }
    }

    /**
     * Scrolls to the given pixel location.
     * @param buffer the displayed buffer
     * @param y the new location
     */
    public void scrollTo(@NotNull final Buffer buffer, final int y) {
        final int bufferHeight = buffer.getTotalHeight();
        synchronized (sync) {
            if (bufferHeight > height) {
                scrollPos = Math.max(Math.min(y, bufferHeight-height), 0);
                topIndex = 0;
                int yPos = scrollPos;
                while (yPos > 0) {
                    final int lineHeight = buffer.getLine(topIndex).getHeight();
                    if (yPos < lineHeight) {
                        break;
                    }

                    yPos -= lineHeight;
                    topIndex++;
                }
                assert yPos >= 0;
                topOffset = yPos;
                canScrollUp = topIndex > 0 || topOffset > 0;
                canScrollDown = y+height < bufferHeight;
                //} else {
                // ignore
            }
        }
    }

    /**
     * Sets the view area to the bottom-most value.
     * @param buffer the displayed buffer
     */
    public void scrollToBottom(@NotNull final Buffer buffer) {
        final int bufferHeight = buffer.getTotalHeight();
        synchronized (sync) {
            if (bufferHeight <= height) {
                scrollPos = 0;
                topIndex = 0;
                topOffset = 0;
                canScrollUp = false;
                canScrollDown = false;
            } else {
                scrollPos = Math.max(bufferHeight-height, 0);
                topIndex = buffer.size();
                int y = height;
                while (y > 0) {
                    topIndex--;
                    assert topIndex >= 0;
                    y -= buffer.getLine(topIndex).getHeight();
                }
                topOffset = -y;
                canScrollUp = topIndex > 0 || topOffset > 0;
                canScrollDown = false;
            }
        }
    }

}

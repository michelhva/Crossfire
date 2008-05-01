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

import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIScrollable2;
import com.realtime.crossfire.jxclient.gui.ScrollableListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public abstract class GUILog extends GUIElement implements GUIScrollable2
{
    /**
     * The number of pixels to scroll.
     */
    public static final int SCROLL_PIXEL = 12;

    /**
     * All listeners.
     */
    private final List<ScrollableListener> listeners = new ArrayList<ScrollableListener>();

    /**
     * The {@link Buffer} containing all received text messages.
     */
    protected final Buffer buffer;

    private final BufferedImage backgroundImage;

    /**
     * The {@link Fonts} instance for looking up fonts.
     */
    private final Fonts fonts;

    /**
     * The rendering state.
     */
    private final RenderStateManager renderStateManager;

    private final RenderStateListener renderStateListener = new RenderStateListener()
    {
        /** {@inheritDoc} */
        public void stateChanged()
        {
            render();

            for (final ScrollableListener listener : listeners)
            {
                listener.setRange(0, buffer.getTotalHeight(), renderStateManager.getScrollPos(), GUILog.super.getHeight());
            }
        }

        /** {@inheritDoc} */
        public int getHeight()
        {
            return GUILog.super.getHeight();
        }
    };

    /**
     * Create a new instance.
     *
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen.
     *
     * @param y The y-coordinate for drawing this element to screen.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param backgroundImage The background image; may be <code>null</code> if
     * unused.
     *
     * @param fonts The <code>Fonts</code> instance for looking up fonts.
     */
    public GUILog(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage backgroundImage, final Fonts fonts)
    {
        super(jxcWindow, name, x, y, w, h, Transparency.TRANSLUCENT);
        this.backgroundImage = backgroundImage;
        this.fonts = fonts;
        final Graphics2D g = super.buffer.createGraphics();
        final FontRenderContext context;
        try
        {
            context = g.getFontRenderContext();
        }
        finally
        {
            g.dispose();
        }
        buffer = new Buffer(fonts, context, w);
        renderStateManager = new RenderStateManager(renderStateListener, buffer);
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        if (renderStateManager == null)
        {
            return;
        }

        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, w, h);
        if (backgroundImage != null)
        {
            g.drawImage(backgroundImage, 0, 0, null);
        }

        int y = -renderStateManager.getTopOffset();
        final ListIterator<Line> it = buffer.listIterator(renderStateManager.getTopIndex());
        while (y < getHeight() && it.hasNext())
        {
            final Line line = it.next();
            drawLine(g, y, line);
            y += line.getHeight();
        }
    }

    /**
     * Draw one {@link Line} to a {@link Graphics2D} instance.
     *
     * @param g The graphics to draw to.
     *
     * @param y The y-coordinate to start drawing.
     *
     * @param line The line to draw.
     */
    private void drawLine(final Graphics2D g, final int y, final Line line)
    {
        for (final Segment segment : line)
        {
            g.setColor(segment.getColor());
            g.setFont(segment.getFont(fonts));
            g.drawString(segment.getText(), segment.getX(), y+segment.getY());
            if (segment.isUnderline())
            {
                g.drawLine(segment.getX(), y+segment.getY()+segment.getUnderlineOffset(), segment.getX()+segment.getWidth()-1, y+segment.getY()+segment.getUnderlineOffset());
            }
        }
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance) // XXX: implement |distance|>1
    {
        if (distance < 0)
        {
            return renderStateManager.canScrollUp();
        }
        else if (distance > 0)
        {
            return renderStateManager.canScrollDown();
        }
        else
        {
            return false;
        }
    }

    /** {@inheritDoc} */
    public void scroll(final int distance)
    {
        if (distance < 0)
        {
            renderStateManager.scrollUp(-distance*SCROLL_PIXEL);
        }
        else if (distance > 0)
        {
            renderStateManager.scrollDown(distance*SCROLL_PIXEL);
        }
        else
        {
            assert false;
        }
    }

    /** {@inheritDoc} */
    public void resetScroll()
    {
        renderStateManager.resetScroll();
    }

    /** {@inheritDoc} */
    public void scrollTo(final int pos)
    {
        renderStateManager.scrollTo(pos);
    }

    /** {@inheritDoc} */
    public void addScrollableListener(final ScrollableListener listener)
    {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    public void removeScrollableListener(final ScrollableListener listener)
    {
        listeners.remove(listener);
    }
}

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

import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable2;
import com.realtime.crossfire.jxclient.gui.scrollable.ScrollableListener;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Abstract base class for gui elements implementing text fields.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class GUILog extends GUIElement implements GUIScrollable2
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

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

    /**
     * The background image drawn below the text contents. Set to
     * <code>null</code> if unused.
     */
    private final BufferedImage backgroundImage;

    /**
     * The {@link Fonts} instance for looking up fonts.
     */
    private final Fonts fonts;

    /**
     * The rendering state.
     */
    private final RenderStateManager renderStateManager;

    /**
     * The {@link RenderStateListener} attached to {@link #renderStateManager}.
     */
    private final RenderStateListener renderStateListener = new RenderStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void stateChanged()
        {
            setChanged();
            for (final ScrollableListener listener : listeners)
            {
                listener.setRange(0, buffer.getTotalHeight(), renderStateManager.getScrollPos(), GUILog.super.getHeight());
            }
        }

        /** {@inheritDoc} */
        @Override
        public int getHeight()
        {
            return GUILog.super.getHeight();
        }
    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer to notify
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen
     * @param y the y-coordinate for drawing this element to screen
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param backgroundImage the background image; may be <code>null</code> if
     * unused
     * @param fonts the <code>Fonts</code> instance for looking up fonts
     */
    protected GUILog(final TooltipManager tooltipManager, final JXCWindowRenderer windowRenderer, final String name, final int x, final int y, final int w, final int h, final BufferedImage backgroundImage, final Fonts fonts)
    {
        super(tooltipManager, windowRenderer, name, x, y, w, h, Transparency.TRANSLUCENT);
        this.backgroundImage = backgroundImage;
        this.fonts = fonts;
        final FontRenderContext context;
        synchronized (bufferedImageSync)
        {
            final Graphics2D g = createBufferGraphics();
            try
            {
                context = g.getFontRenderContext();
            }
            finally
            {
                g.dispose();
            }
        }
        buffer = new Buffer(fonts, context, w);
        renderStateManager = new RenderStateManager(renderStateListener, buffer);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
        renderStateManager.dispose();
    }

    /** {@inheritDoc} */
    @Override
    protected void render(final Graphics g)
    {
        if (renderStateManager == null)
        {
            return;
        }

        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, getWidth(), getHeight());
        if (backgroundImage != null)
        {
            g.drawImage(backgroundImage, 0, 0, null);
        }

        int y = -renderStateManager.getTopOffset();
        synchronized (buffer.getSyncObject())
        {
            final ListIterator<Line> it = buffer.listIterator(renderStateManager.getTopIndex());
            while (y < getHeight() && it.hasNext())
            {
                final Line line = it.next();
                drawLine(g, y, line);
                y += line.getHeight();
            }
        }
    }

    /**
     * Draws one {@link Line} to a {@link Graphics2D} instance.
     * @param g the graphics to draw to
     * @param y the y-coordinate to start drawing
     * @param line the line to draw
     */
    private void drawLine(final Graphics g, final int y, final Line line)
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
    @Override
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
    @Override
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
    @Override
    public void resetScroll()
    {
        renderStateManager.resetScroll();
    }

    /** {@inheritDoc} */
    @Override
    public void scrollTo(final int pos)
    {
        renderStateManager.scrollTo(pos);
    }

    /** {@inheritDoc} */
    @Override
    public void addScrollableListener(final ScrollableListener listener)
    {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeScrollableListener(final ScrollableListener listener)
    {
        listeners.remove(listener);
    }
}

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
import com.realtime.crossfire.jxclient.gui.GUIScrollable;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawextinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandQueryEvent;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUILog extends GUIElement implements GUIScrollable
{
    /**
     * The number of pixels to scroll.
     */
    public static final int SCROLL_PIXEL = 12;

    /**
     * Maps color index to color.
     */
    private final Map<Integer, Color> colors = new HashMap<Integer, Color>();
    {
        colors.put(0, Color.BLACK); //black
        colors.put(1, Color.WHITE); //white
        colors.put(2, Color.BLUE); //navy blue
        colors.put(3, Color.RED); //red
        colors.put(4, Color.ORANGE); //orange
        colors.put(5, Color.CYAN); //dodger blue
        colors.put(6, new Color(0xFFC000)); //dark orange
        colors.put(7, Color.GREEN); //sea green
        colors.put(8, new Color(0x008000)); //dark sea green
        colors.put(9, Color.GRAY); //grey
        colors.put(10, new Color(0x806000)); //brown sienna
        colors.put(11, Color.YELLOW); //gold
        colors.put(12, new Color(0xBDB76B)); //khaki
    }

    /**
     * The {@link Parser} instance for parsing drawextinfo messages.
     */
    private final Parser parser = new Parser();

    /**
     * The {@link Buffer} containing all received text messages.
     */
    private final Buffer buffer;

    private final BufferedImage backgroundImage;

    /**
     * The {@link Fonts} instance for looking up fonts.
     */
    private final Fonts fonts;

    /**
     * The default color to use for text message not specifying a color.
     */
    private final Color defaultColor;

    /**
     * The {@link FontRenderContext} associated to {@link #buffer}.
     */
    private FontRenderContext context = null;

    /**
     * The rendering state.
     */
    private final RenderState renderState;

    /**
     * The {@link CrossfireQueryListener} registered to receive query commands.
     */
    private final CrossfireQueryListener crossfireQueryListener = new CrossfireQueryListener()
    {
        /** {@inheritDoc} */
        public void commandQueryReceived(final CrossfireCommandQueryEvent evt)
        {
            parser.parseWithoutMediaTags(evt.getPrompt(), Color.RED, buffer);
        }
    };

    /**
     * The {@link CrossfireDrawextinfoListener} registered to receive
     * drawextinfo commands.
     */
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener()
    {
        /** {@inheritDoc} */
        public void commandDrawextinfoReceived(final CrossfireCommandDrawextinfoEvent evt)
        {
            parser.parse(evt.getMessage(), findColor(evt.getColor()), buffer);
        }
    };

    /**
     * The {@link CrossfireDrawinfoListener} registered to receive drawinfo
     * commands.
     */
    private final CrossfireDrawinfoListener crossfireDrawinfoListener = new CrossfireDrawinfoListener()
    {
        /** {@inheritDoc} */
        public void commandDrawinfoReceived(final CrossfireCommandDrawinfoEvent evt)
        {
            parser.parseWithoutMediaTags(evt.getText(), findColor(evt.getTextType()), buffer);
        }
    };

    private final RenderStateListener renderStateListener = new RenderStateListener()
    {
        /** {@inheritDoc} */
        public void stateChanged()
        {
            render();
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
     *
     * @param defaultColor The default color to use for text message not
     * specifying a color.
     *
     * @param The color to replace with <code>defaultColor</code>.
     */
    public GUILog(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage backgroundImage, final Fonts fonts, final Color defaultColor)
    {
        super(jxcWindow, name, x, y, w, h);
        this.backgroundImage = backgroundImage;
        this.fonts = fonts;
        this.defaultColor = defaultColor;
        createBuffer();
        buffer = new Buffer(fonts, context, w);
        renderState = new RenderState(renderStateListener, buffer);
        jxcWindow.getCrossfireServerConnection().addCrossfireQueryListener(crossfireQueryListener);
        jxcWindow.getCrossfireServerConnection().addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        jxcWindow.getCrossfireServerConnection().addCrossfireDrawinfoListener(crossfireDrawinfoListener);
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, w, h);
        if (backgroundImage != null)
        {
            g.drawImage(backgroundImage, 0, 0, null);
        }

        int y = -renderState.getTopOffset();
        final ListIterator<Line> it = buffer.listIterator(renderState.getTopIndex());
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

    /**
     * Convert a Crossfire color index to a {@link Color} instance.
     *
     * @param index The color index to look up.
     *
     * @return The color.
     */
    private Color findColor(final int index)
    {
        final Color color = colors.get(index);
        return color == null ? defaultColor : color;
    }

    /**
     * Set a color mapping.
     *
     * @param index The color index to change.
     *
     * @param color The color to map to.
     */
    public void setColor(final int index, final Color color)
    {
        colors.put(index, color);
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance) // XXX: implement |distance|>1
    {
        if (distance < 0)
        {
            return renderState.canScrollUp();
        }
        else if (distance > 0)
        {
            return renderState.canScrollDown();
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
            renderState.scrollUp(-distance*SCROLL_PIXEL);
        }
        else if (distance > 0)
        {
            renderState.scrollDown(distance*SCROLL_PIXEL);
        }
        else
        {
            assert false;
        }
    }

    /** {@inheritDoc} */
    public void resetScroll()
    {
        renderState.resetScroll();
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        final Graphics2D g = mybuffer.createGraphics();
        if (backgroundImage != null)
        {
            g.drawImage(backgroundImage, 0, 0, null);
        }
        context = g.getFontRenderContext();
        g.dispose();
        setChanged();
    }
}

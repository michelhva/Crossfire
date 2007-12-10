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

import com.realtime.crossfire.jxclient.CrossfireCommandDrawextinfoEvent;
import com.realtime.crossfire.jxclient.CrossfireCommandDrawinfoEvent;
import com.realtime.crossfire.jxclient.CrossfireCommandQueryEvent;
import com.realtime.crossfire.jxclient.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIScrollable;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
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
    private final Buffer buffer = new Buffer();

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
     * Records whether scrolling up is possible. It is updated when the buffer
     * is rendered.
     */
    private boolean canScrollUp = false;

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
     * The first line of {@link #buffer} to display. Only used if {@link
     * #displayBottom} is unset.
     */
    private int topIndex = 0;

    /**
     * The number of pixels to shift the first displayed line. Only used if
     * {@link #displayBottom} is unset.
     */
    private int topOffset = 0;

    /**
     * The {@link CrossfireQueryListener} registered to receive query commands.
     */
    private final CrossfireQueryListener crossfireQueryListener = new CrossfireQueryListener()
    {
        /** {@inheritDoc} */
        public void commandQueryReceived(final CrossfireCommandQueryEvent evt)
        {
            parser.parseWithoutMediaTags(evt.getPrompt(), Color.RED, buffer);
            render();
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
            render();
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
            render();
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
     * @param Fonts The <code>Fonts</code> instance for looking up fonts.
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
        jxcWindow.getCrossfireServerConnection().addCrossfireQueryListener(crossfireQueryListener);
        jxcWindow.getCrossfireServerConnection().addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        jxcWindow.getCrossfireServerConnection().addCrossfireDrawinfoListener(crossfireDrawinfoListener);
    }

    protected void render()
    {
        final Graphics2D g = mybuffer.createGraphics();
        try
        {
            g.setBackground(new Color(0, 0, 0, 0.0f));
            g.clearRect(0, 0, w, h);
            if (backgroundImage != null)
            {
                g.drawImage(backgroundImage, 0, 0, null);
            }

            if (displayBottom)
            {
                int y = getHeight();
                topIndex = buffer.size();
                final ListIterator<Line> it = buffer.listIterator(topIndex);
                while (y > 0 && it.hasPrevious())
                {
                    final Line line = it.previous();
                    topIndex--;
                    y -= calculateHeight(line);
                    drawLine(g, y, line);
                }
                canScrollUp = y < 0 || it.hasPrevious();
                canScrollDown = false;
                topOffset = -y;
            }
            else
            {
                int y = -topOffset;
                final ListIterator<Line> it = buffer.listIterator(topIndex);
                while (y < getHeight() && it.hasNext())
                {
                    final Line line = it.next();
                    final int height = calculateHeight(line);
                    drawLine(g, y, line);
                    y += height;
                }
                canScrollUp = topIndex > 0 || topOffset > 0;
                canScrollDown = y > getHeight() || it.hasNext();
            }
        }
        finally
        {
            g.dispose();
        }
        setChanged();
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
        final int cachedHeight = line.getHeight();
        if (cachedHeight != -1)
        {
            return cachedHeight;
        }

        int totalHeight = 0;
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
            if (x != 0 && x+width > getWidth())
            {
                updateAttributes(line, beginIndex, i, totalHeight, minY, maxY);

                totalHeight += maxY-minY;
                x = 0;
                minY = 0;
                maxY = 0;
                beginIndex = i;
            }

            segment.setX(x);
            segment.setY(totalHeight);
            segment.setWidth(width);

            x += width;
            minY = (int)Math.min(minY, Math.round(rect.getY()));
            maxY = (int)Math.max(maxY, Math.round(rect.getY()+rect.getHeight()));
        }

        updateAttributes(line, beginIndex, imax, totalHeight, minY, maxY);
        totalHeight += maxY-minY;
        line.setHeight(totalHeight);

        return totalHeight;
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
    public boolean canScrollUp()
    {
        return canScrollUp;
    }

    /** {@inheritDoc} */
    public void scrollUp()
    {
        assert canScrollUp;

        topOffset -= SCROLL_PIXEL;
        while (topOffset < 0)
        {
            if (topIndex > 0)
            {
                topIndex--;
                topOffset += calculateHeight(buffer.getLine(topIndex));
            }
            else
            {
                topOffset = 0;
            }
        }
        displayBottom = false;
        render();
    }

    /** {@inheritDoc} */
    public boolean canScrollDown()
    {
        return canScrollDown;
    }

    /** {@inheritDoc} */
    public void scrollDown()
    {
        assert canScrollDown;

        topOffset += SCROLL_PIXEL;
        while (topOffset >= calculateHeight(buffer.getLine(topIndex)))
        {
            topOffset -= calculateHeight(buffer.getLine(topIndex));
            topIndex++;
        }
        int y = -topOffset;
        final ListIterator<Line> it = buffer.listIterator(topIndex);
        while (y < getHeight() && it.hasNext())
        {
            final Line line = it.next();
            y += calculateHeight(line);
        }
        displayBottom = y <= getHeight() && !it.hasNext();
        render();
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

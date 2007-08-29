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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUILog extends GUIElement implements GUIScrollable, CrossfireDrawinfoListener, CrossfireDrawextinfoListener
{
    /**
     * The number of pixels to scroll.
     */
    public static final int SCROLL_PIXEL = 12;

    /**
     * The {@link Parser} instance for parsing drawextinfo messages.
     */
    private final Parser parser = new Parser();

    /**
     * The {@link Buffer} containing all received text messages.
     */
    private final Buffer buffer = new Buffer();

    private final BufferedImage mybackground;

    /**
     * The font to use for {@link Segment.Font.PRINT}, {@link
     * Segment.Font.HAND}, and {@link Segment.Font.STANGE} text.
     */
    private final Font fontPrint;

    /**
     * The font to use for {@link Segment.Font.FIXED} text.
     */
    private final Font fontFixed;

    /**
     * The font to use for {@link Segment.Font.FIXED} text which has bold
     * enabled.
     */
    private final Font fontFixedBold;

    /**
     * The font to use for {@link Segment.Font.ARCANE} text.
     */
    private final Font fontArcane;

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
     * Create a new instance.
     *
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
     *
     * @param nn The name of this element.
     *
     * @param nx The x-coordinate for drawing this element to screen.
     *
     * @param ny The y-coordinate for drawing this element to screen.
     *
     * @param nw The width for drawing this element to screen.
     *
     * @param nh The height for drawing this element to screen.
     *
     * @param picture The background image; may be <code>null</code> if unused.
     *
     * @param fontPrint The font to use for <code>Segment.Font.PRINT</code>,
     * <code>Segment.Font.HAND</code>, and <code>Segment.Font.STANGE</code>
     * text.
     *
     * @param fontFixed The font to use for <code>Segment.Font.FIXED</code>
     * text.
     *
     * @param fontFixedBold The font to use for <code>Segment.Font.FIXED</code>
     * text which has bold enabled.
     *
     * @param fontArcane The font to use for <code>Segment.Font.ARCANE</code>
     * text.
     */
    public GUILog(final JXCWindow jxcWindow, final String nn, final int nx, final int ny, final int nw, final int nh, final BufferedImage picture, final Font fontPrint, final Font fontFixed, final Font fontFixedBold, final Font fontArcane) throws IOException
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        mybackground = picture;
        this.fontPrint = fontPrint;
        this.fontFixed = fontFixed;
        this.fontFixedBold = fontFixedBold;
        this.fontArcane = fontArcane;
        createBuffer();
        jxcWindow.getCrossfireServerConnection().addCrossfireQueryListener(crossfireQueryListener);
    }

    protected void render()
    {
        final Graphics2D g = mybuffer.createGraphics();
        try
        {
            g.setBackground(new Color(0, 0, 0, 0.0f));
            g.clearRect(0, 0, w, h);
            if (mybackground != null)
            {
                g.drawImage(mybackground, x, y, null);
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
                    final int height = calculateHeight(line);
                    y -= height;
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
            final Font font = findFont(segment);
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
     * Update the cached attributes of some {@link Segments} of a {@link Line}.
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
            final Font font = findFont(segment);
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
            final Color color = segment.getColor();
            g.setColor(color == null || color == Color.BLACK ? Color.WHITE : color);
            final Font font = findFont(segment);
            g.setFont(font);
            g.drawString(segment.getText(), segment.getX(), y+segment.getY());
            if (segment.isUnderline())
            {
                g.drawLine(segment.getX(), y+segment.getY()+segment.getUnderlineOffset(), segment.getX()+segment.getWidth()-1, y+segment.getY()+segment.getUnderlineOffset());
            }
        }
    }

    /**
     * Return the {@link Font} to use for a given {@link Segment}.
     *
     * @param segment The segment.
     *
     * @return The font.
     */
    private Font findFont(final Segment segment)
    {
        switch (segment.getFont())
        {
        case PRINT:
            return fontPrint;

        case FIXED:
            return segment.isBold() ? fontFixedBold : fontFixed;

        case ARCANE:
            return fontArcane;

        case HAND:
            return fontPrint;

        case STRANGE:
            return fontPrint;
        }

        throw new AssertionError();
    }

    /** {@inheritDoc} */
    public void commandDrawextinfoReceived(final CrossfireCommandDrawextinfoEvent evt)
    {
        parser.parse(evt.getMessage(), findColor(evt.getColor()), buffer);
        render();
    }

    /** {@inheritDoc} */
    public void commandDrawinfoReceived(final CrossfireCommandDrawinfoEvent evt)
    {
        parser.parseWithoutMediaTags(evt.getText(), findColor(evt.getTextType()), buffer);
        render();
    }

    /**
     * Convert a Crossfire color index to a {@link Color} instance.
     *
     * @return The color, or <code>null</code> to use the default color.
     */
    private Color findColor(final int color)
    {
        switch (color)
        {
        case 0: //black
            return null;
        case 1: //white
            return Color.WHITE;
        case 2: //navy blue
            return Color.BLUE;
        case 3: //red
            return Color.RED;
        case 4: //orange
            return Color.ORANGE;
        case 5: //dodger blue
            return Color.CYAN;
        case 6: //dark orange
            return Color.MAGENTA;
        case 7: //sea green
            return Color.GREEN;
        case 8: //dark sea green
            return Color.GREEN;
        case 9: //grey
            return Color.GRAY;
        case 10: //brown sienna
            return Color.PINK;
        case 11: //gold
            return Color.YELLOW;
        case 12: //khaki
            return Color.WHITE;
        default:
            return null;
        }
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
        if (mybackground != null)
        {
            g.drawImage(mybackground, x, y, null);
        }
        context = g.getFontRenderContext();
        g.dispose();
        setChanged();
    }
}

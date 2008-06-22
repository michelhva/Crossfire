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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIHTMLLabel extends AbstractLabel
{
    /**
     * Size of border around text in auto-resize mode.
     */
    public static final int AUTO_BORDER_SIZE = 2;

    /**
     * The pattern used to split a string into lines.
     */
    private static final Pattern patternLineBreak = Pattern.compile("<br>");

    private final Font font;

    private final Color color;

    /**
     * If set, auto-resize this element to the extent of {@link #text}.
     */
    private boolean autoResize = false;

    public GUIHTMLLabel(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final Font font, final Color color, final Color backgroundColor, final String text)
    {
        super(window, name, x, y, w, h, picture, backgroundColor);
        this.font = font;
        this.color = color;
        setText(text);
    }

    /** {@inheritDoc} */
    @Override protected void textChanged()
    {
        autoResize();
        setChanged();
    }

    /**
     * Enable or disable auto-resizing. If enabled, the gui element's size
     * changes to the displayed text's size.
     *
     * @param autoResize If set, enable auto-resizing; if unset, disable
     * auto-resizing.
     */
    public void setAutoResize(final boolean autoResize)
    {
        if (this.autoResize != autoResize)
        {
            this.autoResize = autoResize;
            autoResize();
        }
    }

    /** {@inheritDoc} */
    @Override public void paintComponent(final Graphics2D g)
    {
        super.paintComponent(g);
        if (font == null)
        {
            return;
        }

        g.setFont(font);
        g.setColor(color);

        final Reader reader = new StringReader(getText());
        final InternalHTMLRenderer renderer = new InternalHTMLRenderer(font, color, g, 0, font.getSize(), autoResize ? AUTO_BORDER_SIZE : 0);
        final ParserDelegator parserDelegator = new ParserDelegator();
        try
        {
            parserDelegator.parse(reader, renderer, false);
        }
        catch (final IOException ex)
        {
            // XXX: handle exception
        }
    }

    /**
     * If auto-resizing is enabled, calculate the new width and height.
     */
    private void autoResize()
    {
        if (!autoResize)
        {
            return;
        }

        final Graphics2D g = bufferedImage.createGraphics();
        try
        {
            final FontRenderContext context = g.getFontRenderContext();
            int width = 0;
            int height = 0;
            for (final String str : patternLineBreak.split(getText(), -1))
            {
                final Rectangle2D size = font.getStringBounds(str, context);
                width = Math.max(width, (int)size.getWidth());
                height += (int)size.getHeight();
            }
            setElementSize(Math.max(1, width+2*AUTO_BORDER_SIZE), Math.max(1, height+2*AUTO_BORDER_SIZE));
        }
        finally
        {
            g.dispose();
        }
    }
}

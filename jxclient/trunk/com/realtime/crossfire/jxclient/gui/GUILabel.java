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

import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUILabel extends GUIElement
{
    /**
     * Size of border around text in auto-resize mode.
     */
    public static final int AUTO_BORDER_SIZE = 2;

    /**
     * The pattern used to split a string into lines.
     */
    private static final Pattern patternLineBreak = Pattern.compile("\n");

    private ImageIcon mybackground = null;

    private final Font myfont;

    private String mycaption = "";

    private final Color mycolor;

    /**
     * If set, the opaque background color; if <code>null</code>, the
     * background is transparent. This field is ignored if {@link
     * #mybackground} is set.
     */
    private Color backgroundColor = null;

    /**
     * If set, auto-resize this element to the extent of {@link #mycaption}.
     */
    private boolean autoResize = false;

    private void commonInit(final BufferedImage picture) throws IOException
    {
        mybackground = picture == null ? null : new ImageIcon(picture);
        createBuffer();
    }

    public GUILabel(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final Font font, final Color color, final String text) throws IOException
    {
        super(jxcWindow, name, x, y, w, h);
        myfont = font;
        commonInit(picture);
        mycolor = color;
        mycaption = text;
        render();
    }

    public GUILabel(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final Font font, final String text) throws IOException
    {
        super(jxcWindow, name, x, y, w, h);
        myfont = font;
        commonInit(picture);
        mycolor = Color.WHITE;
        mycaption = text;
        render();
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

    public void setText(final String ntxt)
    {
        if (ntxt == null) throw new IllegalArgumentException();
        if (!mycaption.equals(ntxt))
        {
            mycaption = ntxt;
            autoResize();
            render();
        }
    }

    /**
     * Return the label text.
     *
     * @return The label text.
     */
    public String getText()
    {
        return mycaption;
    }

    /**
     * Set the background color.
     *
     * @param backgroundColor The background color, or <code>null</code> for
     * transparent background.
     */
    public void setBackgroundColor(final Color backgroundColor)
    {
        if (this.backgroundColor != backgroundColor)
        {
            this.backgroundColor = backgroundColor;
            createBuffer();
        }
    }

    protected void render()
    {
        try
        {
            final Graphics2D g = mybuffer.createGraphics();
            g.setBackground(new Color(0, 0, 0, 0.0f));
            g.clearRect(0, 0, w, h);
            if (mybackground != null)
            {
                g.drawImage(mybackground.getImage(), x, y, null);
            }
            else if (backgroundColor != null)
            {
                g.setBackground(backgroundColor);
                g.clearRect(0, 0, w-1, h-1);
            }
            g.setFont(myfont);
            g.setColor(mycolor);

            mycaption = mycaption.replaceAll("\n", "<br>");
            final Reader reader = new StringReader(mycaption);
            try
            {
                new ParserDelegator().parse(reader, new InternalHTMLRenderer(myfont, mycolor, g, 0, myfont.getSize(), autoResize ? AUTO_BORDER_SIZE : 0), false);
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
            g.dispose();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        setChanged();
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

        final Graphics2D g = mybuffer.createGraphics();
        try
        {
            final FontRenderContext context = g.getFontRenderContext();
            int width = 0;
            int height = 0;
            for (final String str : patternLineBreak.split(mycaption, -1))
            {
                final Rectangle2D size = myfont.getStringBounds(mycaption, context);
                width = Math.max(width, (int)size.getWidth());
                height += (int)size.getHeight();
            }
            setSize(width+2*AUTO_BORDER_SIZE, height+2*AUTO_BORDER_SIZE);
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Set the background image.
     *
     * @param background The new background image.
     */
    protected void setBackground(final ImageIcon background)
    {
        mybackground = background;
        createBuffer();
        render();
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, backgroundColor == null ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        final Graphics2D g = mybuffer.createGraphics();
        if (mybackground != null)
        {
            g.drawImage(mybackground.getImage(), x, y, null);
        }
        g.dispose();
        setChanged();
    }
}

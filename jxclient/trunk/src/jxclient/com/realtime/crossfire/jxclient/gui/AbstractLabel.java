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
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Abstract base class for all label classes. It manages the label text, and
 * renders the label's background.
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class AbstractLabel extends GUIElement
{
    /**
     * The label contents.
     */
    private String text = "";

    private ImageIcon backgroundImage = null;

    /**
     * If set, the opaque background color; if <code>null</code>, the
     * background is transparent. This field is ignored if {@link
     * #mybackground} is set.
     */
    private Color backgroundColor = null;

    private void commonInit(final BufferedImage picture)
    {
        backgroundImage = picture == null ? null : new ImageIcon(picture);
        createBuffer();
    }

    public AbstractLabel(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final String text)
    {
        super(jxcWindow, name, x, y, w, h);
        commonInit(picture);
        setText(text);
    }

    public void setText(final String text)
    {
        if (text == null) throw new IllegalArgumentException();

        if (!this.text.equals(text))
        {
            this.text = text;
            textChanged();
        }
    }

    /**
     * Will be called whenever {@link #text} has changed.
     */
    protected abstract void textChanged();

    /**
     * Return the label text.
     *
     * @return The label text.
     */
    public String getText()
    {
        return text;
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
            if (backgroundImage != null)
            {
                g.drawImage(backgroundImage.getImage(), 0, 0, null);
            }
            else if (backgroundColor != null)
            {
                g.setBackground(backgroundColor);
                g.clearRect(0, 0, w-1, h-1);
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
     * Set the background image.
     *
     * @param backgroundImage The new background image.
     */
    protected void setBackground(final ImageIcon backgroundImage)
    {
        this.backgroundImage = backgroundImage;
        createBuffer();
        render();
    }

    /** {@inheritDoc} */
    @Override protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, backgroundColor == null ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        final Graphics2D g = mybuffer.createGraphics();
        if (backgroundImage != null)
        {
            g.drawImage(backgroundImage.getImage(), 0, 0, null);
        }
        g.dispose();
        setChanged();
    }
}

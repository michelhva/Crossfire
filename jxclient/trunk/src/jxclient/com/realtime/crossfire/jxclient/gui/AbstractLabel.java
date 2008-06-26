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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

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

    private ImageIcon backgroundImage;

    /**
     * If set, the opaque background color. This field is ignored if {@link
     * #backgroundImage} is set.
     */
    private final Color backgroundColor;

    protected AbstractLabel(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final Color backgroundColor)
    {
        super(window, name, x, y, w, h, Transparency.TRANSLUCENT);
        backgroundImage = picture == null ? null : new ImageIcon(picture);
        this.backgroundColor = backgroundColor;
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

    /** {@inheritDoc} */
    @Override
    protected void render(final Graphics g)
    {
        if (backgroundImage != null)
        {
            g.drawImage(backgroundImage.getImage(), 0, 0, null);
        }
        else
        {
            final Graphics2D g2 = (Graphics2D)g;
            g2.setBackground(backgroundColor);
            g.clearRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Set the background image.
     *
     * @param backgroundImage The new background image.
     */
    protected void setBackground(final ImageIcon backgroundImage)
    {
        this.backgroundImage = backgroundImage;
        setChanged();
    }
}

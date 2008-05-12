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
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public abstract class GUIItem extends ActivatableGUIElement implements GUIScrollable
{
    protected final BufferedImage cursedImage;

    protected final BufferedImage appliedImage;

    protected final BufferedImage selectorImage;

    protected final BufferedImage lockedImage;

    protected final Font font;

    /**
     * The background color of this item.
     */
    private static final Color backgroundColor = new Color(0, 0, 0, 0.0f);

    protected GUIItem(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final Font font)
    {
        super(jxcWindow, name, x, y, w, h, Transparency.TRANSLUCENT);
        this.cursedImage = cursedImage;
        this.appliedImage = appliedImage;
        this.selectorImage = selectorImage;
        this.lockedImage = lockedImage;
        this.font = font;
    }

    /** {@inheritDoc} */
    @Override public void mouseClicked(final MouseEvent e)
    {
        super.mouseClicked(e);
        final JXCWindow jxcw = (JXCWindow)e.getSource();
        switch (e.getButton())
        {
        case MouseEvent.BUTTON1:
            setActive(true);
            button1Clicked(jxcw);
            break;

        case MouseEvent.BUTTON2:
            button2Clicked(jxcw);
            break;

        case MouseEvent.BUTTON3:
            button3Clicked(jxcw);
            break;
        }
    }

    public abstract void button1Clicked(final JXCWindow jxcWindow);

    public abstract void button2Clicked(final JXCWindow jxcWindow);

    public abstract void button3Clicked(final JXCWindow jxcWindow);

    /** {@inheritDoc} */
    @Override public void activeChanged()
    {
        setChanged();
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);
        g.setBackground(backgroundColor);
        g.clearRect(0, 0, w, h);
    }

    @Override public void setVisible(final boolean visible)
    {
        super.setVisible(visible);
        setChanged();
    }
}

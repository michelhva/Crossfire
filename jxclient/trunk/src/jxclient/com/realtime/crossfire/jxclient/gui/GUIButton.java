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

import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIButton extends AbstractButton
{
    private final BufferedImage imageUp;

    private final BufferedImage imageDown;

    private final String text;

    private final Font font;

    private final int textX;

    private final int textY;

    private final Color color;

    public GUIButton(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage imageUp, final BufferedImage imageDown, final String text, final Font font, final Color color, final int textX, final int textY, final boolean autoRepeat, final GUICommandList commandList)
    {
        super(jxcWindow, name, x, y, w, h, Transparency.TRANSLUCENT, autoRepeat, commandList);
        if (imageUp == null) throw new IllegalArgumentException();
        if (imageDown == null) throw new IllegalArgumentException();
        if (imageUp.getWidth() != imageDown.getWidth()) throw new IllegalArgumentException();
        if (imageUp.getHeight() != imageDown.getHeight()) throw new IllegalArgumentException();
        this.imageUp = imageUp;
        this.imageDown = imageDown;
        this.text = text;
        this.font = font;
        this.color = color;
        this.textX = textX;
        this.textY = textY;
    }

    /** {@inheritDoc} */
    @Override public void activeChanged()
    {
        setChanged();
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);
        g.setFont(font);
        g.setColor(color);
        g.drawImage(isActive() ? imageDown : imageUp, 0, 0, null);
        if (text != null)
        {
            g.drawString(text, textX, textY);
        }
    }
}

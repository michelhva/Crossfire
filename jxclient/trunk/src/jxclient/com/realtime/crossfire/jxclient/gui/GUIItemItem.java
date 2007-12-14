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

import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class GUIItemItem extends GUIItem
{
    /**
     * The color for the "nrof" text.
     */
    private final Color nrofColor;

    public GUIItemItem(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage image, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final CrossfireServerConnection crossfireServerConnection, final Font font, final Color nrofColor)
    {
        super(jxcWindow, name, x, y, w, h, image, cursedImage, appliedImage, selectorImage, lockedImage, crossfireServerConnection, font);
        if (nrofColor == null) throw new IllegalArgumentException();
        this.nrofColor = nrofColor;
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        g.drawImage(item.getFace().getOriginalImageIcon().getImage(), 0, 0, null);
        if (item.isApplied())
        {
            g.drawImage(appliedImage, 0, 0, null);
        }
        if (item.isCursed())
        {
            g.drawImage(cursedImage, 0, 0, null);
        }
        if (item.isLocked())
        {
            g.drawImage(lockedImage, 0, 0, null);
        }
        if (isActive())
        {
            g.drawImage(selectorImage, 0, 0, null);
        }
        if (item.getNrOf() > 0)
        {
            g.setFont(font);
            g.setColor(nrofColor);
            g.drawString(String.valueOf(item.getNrOf()), 1, 1+font.getSize());
        }
    }

    /* {@inheritDoc} */
    @Override protected void button2Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        try
        {
            jxcw.getCrossfireServerConnection().sendApply(item.getTag());
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }
}

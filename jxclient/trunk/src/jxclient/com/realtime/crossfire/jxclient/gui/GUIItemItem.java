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

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CfItemModifiedListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class GUIItemItem extends GUIItem
{
    /**
     * The connection instance.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    private final FacesManager facesManager;

    /**
     * The color for the "nrof" text.
     */
    private final Color nrofColor;

    private CfItem item = null;

    /**
     * The {@link CfItemModifiedListener} used to detect attribute changes of
     * the displayed item.
     */
    private final CfItemModifiedListener itemModifiedListener = new CfItemModifiedListener()
    {
        /** {@inheritDoc} */
        public void itemModified()
        {
            setChanged();
        }
    };

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    private final FacesManagerListener facesManagerListener = new FacesManagerListener()
    {
        /** {@inheritDoc} */
        public void faceUpdated(final Face face)
        {
            if (item != null && face.equals(item.getFace()))
            {
                setChanged();
            }
        }
    };

    public GUIItemItem(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final CrossfireServerConnection crossfireServerConnection, final FacesManager facesManager, final Font font, final Color nrofColor)
    {
        super(jxcWindow, name, x, y, w, h, cursedImage, appliedImage, selectorImage, lockedImage, crossfireServerConnection, font);
        if (nrofColor == null) throw new IllegalArgumentException();
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.nrofColor = nrofColor;
        facesManager.addFacesManagerListener(facesManagerListener);
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        final CfItem tmpItem = getItem();
        if (tmpItem == null)
        {
            return;
        }

        g.drawImage(facesManager.getOriginalImageIcon(tmpItem.getFace().getFaceNum()).getImage(), 0, 0, null);
        if (tmpItem.isApplied())
        {
            g.drawImage(appliedImage, 0, 0, null);
        }
        if (tmpItem.isCursed() || tmpItem.isDamned())
        {
            g.drawImage(cursedImage, 0, 0, null);
        }
        if (tmpItem.isLocked())
        {
            g.drawImage(lockedImage, 0, 0, null);
        }
        if (isActive())
        {
            g.drawImage(selectorImage, 0, 0, null);
        }
        if (tmpItem.getNrOf() > 0)
        {
            g.setFont(font);
            g.setColor(nrofColor);
            g.drawString(String.valueOf(tmpItem.getNrOf()), 1, 1+font.getSize());
        }
    }

    /* {@inheritDoc} */
    @Override public void button2Clicked(final JXCWindow jxcWindow)
    {
        final CfItem tmpItem = getItem();
        if (tmpItem == null)
        {
            return;
        }

        crossfireServerConnection.sendApply(tmpItem.getTag());
    }

    protected CfItem getItem()
    {
        return item;
    }

    protected void setItem(final CfItem item)
    {
        if (this.item == item)
        {
            return;
        }

        if (this.item != null)
        {
            this.item.removeCfItemModifiedListener(itemModifiedListener);
        }
        this.item = item;
        if (this.item != null)
        {
            this.item.addCfItemModifiedListener(itemModifiedListener);
        }

        setChanged();

        setTooltipText(item == null ? null : item.getName());
    }
}

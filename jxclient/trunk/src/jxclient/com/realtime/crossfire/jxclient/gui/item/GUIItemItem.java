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

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CfItemListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * A {@link GUIElement} instance representing an in-game item.
 * @author Andreas Kirschbaum
 */
public abstract class GUIItemItem extends GUIItem
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The background color of this item.
     */
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0.0f);

    /**
     * The connection instance.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link FacesManager} instance to use.
     */
    private final FacesManager facesManager;

    /**
     * The {@link ItemPainter} for painting the icon.
     */
    private final ItemPainter itemPainter;

    /**
     * The current item instance.
     */
    private CfItem item = null;

    /**
     * The {@link CfItemListener} used to detect attribute changes of
     * the displayed item.
     */
    private final CfItemListener itemListener = new CfItemListener()
    {
        /** {@inheritDoc} */
        @Override
        public void itemModified()
        {
            setChanged();
            updateTooltipText();
        }
    };

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    private final FacesManagerListener facesManagerListener = new FacesManagerListener()
    {
        /** {@inheritDoc} */
        @Override
        public void faceUpdated(final Face face)
        {
            if (item != null && face.equals(item.getFace()))
            {
                setChanged();
            }
        }
    };

    /**
     * Creates a new instance.
     * @param window the window instance this element belongs to
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param y the y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param crossfireServerConnection the connection instance
     * @param itemPainter the item painter for painting the icon
     * @param facesManager the faces manager instance to use
     */
    protected GUIItemItem(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final CrossfireServerConnection crossfireServerConnection, final ItemPainter itemPainter, final FacesManager facesManager)
    {
        super(window, name, x, y, w, h);
        this.itemPainter = itemPainter;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        facesManager.addFacesManagerListener(facesManagerListener);
    }

    /**
     * Unregisters listeners. Must be called when this item not used anymore.
     */
    public void destroy()
    {
        facesManager.removeFacesManagerListener(facesManagerListener);
    }

    /** {@inheritDoc} */
    @Override
    protected void render(final Graphics g)
    {
        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(BACKGROUND_COLOR);
        g.clearRect(0, 0, getWidth(), getHeight());

        final CfItem tmpItem = item;
        if (tmpItem == null)
        {
            return;
        }

        itemPainter.paint(g2, tmpItem, isSelected(), getFace(tmpItem));
    }

    /**
     * Returns the face for a {@link CfItem} instance.
     * @param item the item instance
     * @return the face
     */
    protected abstract Image getFace(final CfItem item);

    /* {@inheritDoc} */
    @Override
    public void button2Clicked(final JXCWindow window)
    {
        final CfItem tmpItem = item;
        if (tmpItem == null)
        {
            return;
        }

        crossfireServerConnection.sendApply(tmpItem.getTag());
    }

    /**
     * Returns the current item instance.
     * @return the current item instance
     */
    public CfItem getItem()
    {
        return item;
    }

    /**
     * Sets the current item instance.
     * @param item the new current item instance
     */
    protected void setItem(final CfItem item)
    {
        if (this.item == item)
        {
            return;
        }

        if (this.item != null)
        {
            this.item.removeCfItemModifiedListener(itemListener);
        }
        this.item = item;
        if (this.item != null)
        {
            this.item.addCfItemModifiedListener(itemListener);
        }

        setChanged();
        updateTooltipText();
    }

    /**
     * Updates the tooltip text for the current {@link #item}.
     */
    private void updateTooltipText()
    {
        if (item == null)
        {
            setTooltipText(null);
            return;
        }

        setTooltipText(item.getTooltipText());
    }

    /**
     * Sets the selected state.
     * @param selected whether this element should drawn as "selected"
     */
    public abstract void setSelected(final boolean selected);

    /**
     * Returns whether this element should drawn as "selected".
     * @return whether this element is selected
     */
    protected abstract boolean isSelected(); 
}

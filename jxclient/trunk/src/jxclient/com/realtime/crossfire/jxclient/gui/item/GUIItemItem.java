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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

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
     * The overlay image for cursed objects.
     */
    private final BufferedImage cursedImage;

    /**
     * The overlay image for damned objects.
     */
    private final BufferedImage damnedImage;

    /**
     * The overlay image for magical objects.
     */
    private final BufferedImage magicImage;

    /**
     * The overlay image for blessed objects.
     */
    private final BufferedImage blessedImage;

    /**
     * The overlay image for applied objects.
     */
    private final BufferedImage appliedImage;

    /**
     * The overlay image for selected objects.
     */
    private final BufferedImage selectorImage;

    /**
     * The overlay image for locked objects.
     */
    private final BufferedImage lockedImage;

    /**
     * The overlay image for unpaid objects.
     */
    private final BufferedImage unpaidImage;

    /**
     * The background color for cursed objects.
     */
    private final Color cursedColor;

    /**
     * The background color for damned objects.
     */
    private final Color damnedColor;

    /**
     * The background color for magical objects.
     */
    private final Color magicColor;

    /**
     * The background color for blessed objects.
     */
    private final Color blessedColor;

    /**
     * The background color for applied objects.
     */
    private final Color appliedColor;

    /**
     * The background color for selected objects.
     */
    private final Color selectorColor;

    /**
     * The background color for locked objects.
     */
    private final Color lockedColor;

    /**
     * The background color for unpaid objects.
     */
    private final Color unpaidColor;

    /**
     * The font for the "nrof" text.
     */
    private final Font font;

    /**
     * The color for the "nrof" text.
     */
    private final Color nrofColor;

    /**
     * The current item instance.
     */
    private CfItem item = null;

    /**
     * If set, paint the element in "selected" state.
     */
    private boolean selected = false;

    /**
     * The {@link CfItemListener} used to detect attribute changes of
     * the displayed item.
     */
    private final CfItemListener itemListener = new CfItemListener()
    {
        /** {@inheritDoc} */
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
     * @param cursedImage the overlay image for cursed objects
     * @param damnedImage the overlay image for damned objects
     * @param magicImage the overlay image for magical objects
     * @param blessedImage the overlay image for blessed objects
     * @param appliedImage the overlay image for applied objects
     * @param selectorImage the overlay image for selected objects
     * @param lockedImage the overlay image for locked objects
     * @param unpaidImage the overlay image for unpaid objects
     * @param cursedColor the background color for cursed objects
     * @param damnedColor the background color for damned objects
     * @param magicColor the background color for magical objects
     * @param blessedColor the background color for blessed objects
     * @param appliedColor the background color for applied objects
     * @param selectorColor the background color for selected objects
     * @param lockedColor the background color for locked objects
     * @param unpaidColor the background color for unpaid objects
     * @param crossfireServerConnection the connection instance
     * @param facesManager the faces manager instance to use
     * @param font the font for nrof information
     * @param nrofColor the color for nrof information
     */
    protected GUIItemItem(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final BufferedImage cursedImage, final BufferedImage damnedImage, final BufferedImage magicImage, final BufferedImage blessedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final BufferedImage unpaidImage, final Color cursedColor, final Color damnedColor, final Color magicColor, final Color blessedColor, final Color appliedColor, final Color selectorColor, final Color lockedColor, final Color unpaidColor, final CrossfireServerConnection crossfireServerConnection, final FacesManager facesManager, final Font font, final Color nrofColor)
    {
        super(window, name, x, y, w, h);
        if (nrofColor == null) throw new IllegalArgumentException();
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.cursedImage = cursedImage;
        this.damnedImage = damnedImage;
        this.magicImage = magicImage;
        this.blessedImage = blessedImage;
        this.appliedImage = appliedImage;
        this.selectorImage = selectorImage;
        this.lockedImage = lockedImage;
        this.unpaidImage = unpaidImage;
        this.cursedColor = cursedColor;
        this.damnedColor = damnedColor;
        this.magicColor = magicColor;
        this.blessedColor = blessedColor;
        this.appliedColor = appliedColor;
        this.selectorColor = selectorColor;
        this.lockedColor = lockedColor;
        this.unpaidColor = unpaidColor;
        this.font = font;
        this.nrofColor = nrofColor;
        facesManager.addFacesManagerListener(facesManagerListener);
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

        paintColor(g, appliedColor, tmpItem.isApplied());
        paintColor(g, cursedColor, tmpItem.isCursed());
        paintColor(g, damnedColor, tmpItem.isDamned());
        paintColor(g, magicColor, tmpItem.isMagic());
        paintColor(g, blessedColor, tmpItem.isBlessed());
        paintColor(g, lockedColor, tmpItem.isLocked());
        paintColor(g, selectorColor, selected || isActive());
        paintColor(g, unpaidColor, tmpItem.isUnpaid());
        g.drawImage(getFace(tmpItem), 0, 0, null);
        paintImage(g, appliedImage, tmpItem.isApplied());
        paintImage(g, cursedImage, tmpItem.isCursed());
        paintImage(g, damnedImage, tmpItem.isDamned());
        paintImage(g, magicImage, tmpItem.isMagic());
        paintImage(g, blessedImage, tmpItem.isBlessed());
        paintImage(g, lockedImage, tmpItem.isLocked());
        paintImage(g, selectorImage, selected || isActive());
        paintImage(g, unpaidImage, tmpItem.isUnpaid());
        if (getWidth() <= getHeight())
        {
            if (tmpItem.getNrOf() > 0)
            {
                g.setFont(font);
                g.setColor(nrofColor);
                g.drawString(String.valueOf(tmpItem.getNrOf()), 1, 1+font.getSize());
            }
        }
        else
        {
            g.setFont(font);
            g.setColor(nrofColor);
            g2.setBackground(new Color(0, 0, 0, 0.0f));
            renderText(g2, getHeight(), 0, getHeight()/2, tmpItem.getTooltipText1());
            renderText(g2, getHeight(), getHeight()/2, getHeight()/2, tmpItem.getTooltipText2());
        }
    }

    /**
     * Returns the face for a {@link CfItem} instance.
     * @param item the item instance
     * @return the face
     */
    protected Image getFace(final CfItem item)
    {
        return facesManager.getOriginalImageIcon(item.getFace().getFaceNum()).getImage();
    }

    /**
     * Renders a text string.
     * @param g the graphics context to paint into
     * @param dx the x-coordinate
     * @param dy the y-coordinate
     * @param height the text height
     * @param text the text
     */
    private void renderText(final Graphics2D g, final int dx, final int dy, final int height, final String text)
    {
        final Rectangle2D rect = font.getStringBounds(text, g.getFontRenderContext());
        final int y = dy+(int)Math.round((height-rect.getMaxY()-rect.getMinY()))/2;
        g.drawString(text, dx, y);
    }

    /**
     * Conditionally paints the background with a solid color.
     * @param g the context to paint into
     * @param color the color to use
     * @param isActive whether painting should be done at all
     */
    private void paintColor(final Graphics g, final Color color, final boolean isActive)
    {
        if (isActive && color != null)
        {
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Conditionally paints an image.
     * @param g the context to paint into
     * @param image the image to paint
     * @param isActive whether painting should be done at all
     */
    private static void paintImage(final Graphics g, final BufferedImage image, final boolean isActive)
    {
        if (isActive)
        {
            g.drawImage(image, 0, 0, null);
        }
    }

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
    public void setSelected(final boolean selected)
    {
        if (this.selected == selected)
        {
            return;
        }

        this.selected = selected;
        setChanged();
    }
}

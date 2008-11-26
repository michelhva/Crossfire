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

import com.realtime.crossfire.jxclient.items.CfItem;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Paints Crossfire item images.
 * @author Andreas Kirschbaum
 */
public class ItemPainter
{
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
     * The item's width in pixel.
     */
    private final int w;

    /**
     * The item's height in pixel.
     */
    private final int h;

    /**
     * Creates a new instance.
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
     * @param font the font for nrof information
     * @param nrofColor the color for nrof information
     * @param w the item's width in pixel
     * @param h the item's height in pixel
     */
    public ItemPainter(final BufferedImage cursedImage, final BufferedImage damnedImage, final BufferedImage magicImage, final BufferedImage blessedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final BufferedImage unpaidImage, final Color cursedColor, final Color damnedColor, final Color magicColor, final Color blessedColor, final Color appliedColor, final Color selectorColor, final Color lockedColor, final Color unpaidColor, final Font font, final Color nrofColor, final int w, final int h)
    {
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
        this.w = w;
        this.h = h;
    }

    /**
     * Paints an {@link CfItem}.
     * @param g the graphics instance to paint into
     * @param item the item
     * @param selected whether the item is selected
     * @param face the item's face
     */
    public void paint(final Graphics2D g, final CfItem item, final boolean selected, final Image face)
    {
        paintColor(g, appliedColor, item.isApplied());
        paintColor(g, cursedColor, item.isCursed());
        paintColor(g, damnedColor, item.isDamned());
        paintColor(g, magicColor, item.isMagic());
        paintColor(g, blessedColor, item.isBlessed());
        paintColor(g, lockedColor, item.isLocked());
        paintColor(g, selectorColor, selected);
        paintColor(g, unpaidColor, item.isUnpaid());
        g.drawImage(face, 0, 0, null);
        paintImage(g, appliedImage, item.isApplied());
        paintImage(g, cursedImage, item.isCursed());
        paintImage(g, damnedImage, item.isDamned());
        paintImage(g, magicImage, item.isMagic());
        paintImage(g, blessedImage, item.isBlessed());
        paintImage(g, lockedImage, item.isLocked());
        paintImage(g, selectorImage, selected);
        paintImage(g, unpaidImage, item.isUnpaid());
        if (w <= h)
        {
            if (item.getNrOf() > 0)
            {
                g.setFont(font);
                g.setColor(nrofColor);
                g.drawString(String.valueOf(item.getNrOf()), 1, 1+font.getSize());
            }
        }
        else
        {
            g.setFont(font);
            g.setColor(nrofColor);
            g.setBackground(new Color(0, 0, 0, 0.0f));
            renderText(g, h, 0, h/2, item.getTooltipText1());
            renderText(g, h, h/2, h/2, item.getTooltipText2());
        }
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
            g.fillRect(0, 0, w, h);
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
}

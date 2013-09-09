/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.util.MathUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.RectangularShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Paints Crossfire item images.
 * @author Andreas Kirschbaum
 */
public class ItemPainter {

    /**
     * The indentation of the item's text from the icon.
     */
    private static final int TEXT_OFFSET = 3;

    /**
     * The overlay image for cursed objects.
     */
    @Nullable
    private final Image cursedImage;

    /**
     * The overlay image for damned objects.
     */
    @Nullable
    private final Image damnedImage;

    /**
     * The overlay image for magical objects.
     */
    @Nullable
    private final Image magicImage;

    /**
     * The overlay image for blessed objects.
     */
    @Nullable
    private final Image blessedImage;

    /**
     * The overlay image for applied objects.
     */
    @Nullable
    private final Image appliedImage;

    /**
     * The overlay image for unidentified objects.
     */
    @Nullable
    private final Image unidentifiedImage;

    /**
     * The overlay image for selected objects.
     */
    @Nullable
    private final Image selectorImage;

    /**
     * The overlay image for locked objects.
     */
    @Nullable
    private final Image lockedImage;

    /**
     * The overlay image for unpaid objects.
     */
    @Nullable
    private final Image unpaidImage;

    /**
     * The background color for cursed objects.
     */
    @Nullable
    private final Color cursedColor;

    /**
     * The background color for damned objects.
     */
    @Nullable
    private final Color damnedColor;

    /**
     * The background color for magical objects.
     */
    @Nullable
    private final Color magicColor;

    /**
     * The background color for blessed objects.
     */
    @Nullable
    private final Color blessedColor;

    /**
     * The background color for applied objects.
     */
    @Nullable
    private final Color appliedColor;

    /**
     * The background color for unidentified objects.
     */
    @Nullable
    private final Color unidentifiedColor;

    /**
     * The background color for selected objects.
     */
    @Nullable
    private final Color selectorColor;

    /**
     * The background color for locked objects.
     */
    @Nullable
    private final Color lockedColor;

    /**
     * The background color for unpaid objects.
     */
    @Nullable
    private final Color unpaidColor;

    /**
     * The font for the "nrof" text.
     */
    @NotNull
    private final Font font;

    /**
     * The color for the "nrof" text.
     */
    @NotNull
    private final Color nrofColor;

    /**
     * Creates a new instance.
     * @param cursedImage the overlay image for cursed objects
     * @param damnedImage the overlay image for damned objects
     * @param magicImage the overlay image for magical objects
     * @param blessedImage the overlay image for blessed objects
     * @param appliedImage the overlay image for applied objects
     * @param unidentifiedImage the overlay image for unidentified objects
     * @param selectorImage the overlay image for selected objects
     * @param lockedImage the overlay image for locked objects
     * @param unpaidImage the overlay image for unpaid objects
     * @param cursedColor the background color for cursed objects
     * @param damnedColor the background color for damned objects
     * @param magicColor the background color for magical objects
     * @param blessedColor the background color for blessed objects
     * @param appliedColor the background color for applied objects
     * @param unidentifiedColor the background color for unidentified objects
     * @param selectorColor the background color for selected objects
     * @param lockedColor the background color for locked objects
     * @param unpaidColor the background color for unpaid objects
     * @param font the font for nrof information
     * @param nrofColor the color for nrof information
     */
    public ItemPainter(@Nullable final Image cursedImage, @Nullable final Image damnedImage, @Nullable final Image magicImage, @Nullable final Image blessedImage, @Nullable final Image appliedImage, @Nullable final Image unidentifiedImage, @Nullable final Image selectorImage, @Nullable final Image lockedImage, @Nullable final Image unpaidImage, @Nullable final Color cursedColor, @Nullable final Color damnedColor, @Nullable final Color magicColor, @Nullable final Color blessedColor, @Nullable final Color appliedColor, @Nullable final Color unidentifiedColor, @Nullable final Color selectorColor, @Nullable final Color lockedColor, @Nullable final Color unpaidColor, @NotNull final Font font, @NotNull final Color nrofColor) {
        this.cursedImage = cursedImage;
        this.damnedImage = damnedImage;
        this.magicImage = magicImage;
        this.blessedImage = blessedImage;
        this.appliedImage = appliedImage;
        this.unidentifiedImage = unidentifiedImage;
        this.selectorImage = selectorImage;
        this.lockedImage = lockedImage;
        this.unpaidImage = unpaidImage;
        this.cursedColor = cursedColor;
        this.damnedColor = damnedColor;
        this.magicColor = magicColor;
        this.blessedColor = blessedColor;
        this.appliedColor = appliedColor;
        this.unidentifiedColor = unidentifiedColor;
        this.selectorColor = selectorColor;
        this.lockedColor = lockedColor;
        this.unpaidColor = unpaidColor;
        this.font = font;
        this.nrofColor = nrofColor;
    }

    /**
     * Creates a new instance having the same parameters as this instance except
     * for the item's size.
     * @return the new instance
     */
    @NotNull
    public ItemPainter newItemPainter() {
        return new ItemPainter(cursedImage, damnedImage, magicImage, blessedImage, appliedImage, unidentifiedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, unidentifiedColor, selectorColor, lockedColor, unpaidColor, font, nrofColor);
    }

    /**
     * Returns the minimal size needed to display this item.
     * @return the minimal size
     */
    @NotNull
    public Dimension getMinimumSize() {
        final Dimension dimension = new Dimension(32, 32);
        updateMinimumSize(dimension, appliedImage);
        updateMinimumSize(dimension, unidentifiedImage);
        updateMinimumSize(dimension, cursedImage);
        updateMinimumSize(dimension, magicImage);
        updateMinimumSize(dimension, blessedImage);
        updateMinimumSize(dimension, lockedImage);
        updateMinimumSize(dimension, unpaidImage);
        return dimension;
    }

    /**
     * Updates the minimum size to contain an image.
     * @param minimumSize the minimum size
     * @param image the image
     */
    private static void updateMinimumSize(@NotNull final Dimension minimumSize, @Nullable final Image image) {
        if (image == null) {
            return;
        }

        final int width = image.getWidth(null);
        if (minimumSize.width < width) {
            minimumSize.width = width;
        }

        final int height = image.getWidth(null);
        if (minimumSize.height < height) {
            minimumSize.height = height;
        }
    }

    /**
     * Paints an {@link CfItem}.
     * @param g the graphics instance to paint into
     * @param item the item
     * @param selected whether the item is selected
     * @param face the item's face
     * @param w the item's width in pixel
     * @param h the item's height in pixel
     */
    public void paint(@NotNull final Graphics2D g, @NotNull final CfItem item, final boolean selected, @NotNull final Image face, final int w, final int h) {
        paintColor(g, appliedColor, item.isApplied(), w, h);
        paintColor(g, unidentifiedColor, item.isUnidentified(), w, h);
        paintColor(g, cursedColor, item.isCursed(), w, h);
        paintColor(g, damnedColor, item.isDamned(), w, h);
        paintColor(g, magicColor, item.isMagic(), w, h);
        paintColor(g, blessedColor, item.isBlessed(), w, h);
        paintColor(g, lockedColor, item.isLocked(), w, h);
        paintColor(g, selectorColor, selected, w, h);
        paintColor(g, unpaidColor, item.isUnpaid(), w, h);
        final int imageW = Math.max(0, face.getWidth(null));
        final int imageH = Math.max(0, face.getHeight(null));
        final int scaledW;
        final int scaledH;
        final int offsetX;
        final int offsetY;
        if (imageW > imageH) {
            scaledW = h;
            scaledH = MathUtils.divRound(imageH*h, imageW);
            offsetX = 0;
            offsetY = (h-scaledH)/2;
        } else {
            scaledW = MathUtils.divRound(imageW*h, imageH);
            scaledH = h;
            offsetX = (h-scaledW)/2;
            offsetY = 0;
        }
        g.drawImage(face, offsetX, offsetY, scaledW, scaledH, null);
        paintImage(g, appliedImage, item.isApplied());
        paintImage(g, unidentifiedImage, item.isUnidentified());
        paintImage(g, cursedImage, item.isCursed());
        paintImage(g, damnedImage, item.isDamned());
        paintImage(g, magicImage, item.isMagic());
        paintImage(g, blessedImage, item.isBlessed());
        paintImage(g, lockedImage, item.isLocked());
        paintImage(g, selectorImage, selected);
        paintImage(g, unpaidImage, item.isUnpaid());
        if (w <= h) {
            if (item.getNrOf() > 1) {
                g.setFont(font);
                g.setColor(nrofColor);
                g.drawString(String.valueOf(item.getNrOf()), 1, 1+font.getSize());
            }
        } else {
            g.setFont(font);
            g.setColor(nrofColor);
            g.setBackground(new Color(0, 0, 0, 0.0f));
            renderText(g, TEXT_OFFSET+h, 0, h/2, item.getTooltipText1());
            renderText(g, TEXT_OFFSET+h, h/2, h/2, item.getTooltipText2());
        }
    }

    /**
     * Conditionally paints the background with a solid color.
     * @param g the context to paint into
     * @param color the color to use
     * @param isActive whether painting should be done at all
     * @param w the item's width in pixel
     * @param h the item's height in pixel
     */
    private static void paintColor(@NotNull final Graphics g, @Nullable final Color color, final boolean isActive, final int w, final int h) {
        if (isActive && color != null) {
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
    private static void paintImage(@NotNull final Graphics g, @Nullable final Image image, final boolean isActive) {
        if (isActive) {
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
    private void renderText(@NotNull final Graphics2D g, final int dx, final int dy, final int height, @NotNull final String text) {
        final RectangularShape rectangle = font.getStringBounds(text, g.getFontRenderContext());
        final int y = dy+(int)Math.round(height-rectangle.getMaxY()-rectangle.getMinY())/2;
        g.drawString(text, dx, y);
    }

}

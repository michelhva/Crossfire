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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;

/**
 * A button which displays a string. The button width does not depend on the
 * underlying images.
 *
 * @author Andreas Kirschbaum
 */
public class GUITextButton extends AbstractButton
{
    /**
     * The images comprising the "up" button state.
     */
    private final ButtonImages up;

    /**
     * The images comprising the "down" button state.
     */
    private final ButtonImages down;

    /**
     * The button text.
     */
    private final String text;

    /**
     * The font to use.
     */
    private final Font font;

    /**
     * The text color.
     */
    private final Color color;

    /**
     * Create a new instance.
     *
     * @param window The <code>JXCWindow</code> this element belongs to.
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param y The y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param up The images comprising the "up" button state.
     *
     * @param down The images comprising the "down" button state.
     *
     * @param text The button text.
     *
     * @param font The font to use.
     *
     * @param color The text color.
     *
     * @param autoRepeat Whether the button should autorepeat while being
     * pressed.
     *
     * @param commandList The commands to execute when the button is selected.
     */
    public GUITextButton(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final ButtonImages up, final ButtonImages down, final String text, final Font font, final Color color, final boolean autoRepeat, final GUICommandList commandList)
    {
        super(window, name, x, y, w, h, Transparency.TRANSLUCENT, autoRepeat, commandList);
        if (up == null) throw new IllegalArgumentException();
        if (down == null) throw new IllegalArgumentException();
        if (up.getHeight() != h) throw new IllegalArgumentException("'up' state is height "+up.getHeight()+" but button height is "+h);
        if (down.getHeight() != h) throw new IllegalArgumentException("'down' state is height "+up.getHeight()+" but button height is "+h);
        if (up.getMinimumWidth() > w) throw new IllegalArgumentException("minimum width in 'up' state is "+up.getMinimumWidth()+" but button width is "+w);
        if (down.getMinimumWidth() > w) throw new IllegalArgumentException("minimum width in 'down' state is "+down.getMinimumWidth()+" but button width is "+w);
        if (text == null) throw new IllegalArgumentException();
        if (font == null) throw new IllegalArgumentException();
        if (color == null) throw new IllegalArgumentException();

        this.up = up;
        this.down = down;
        this.text = text;
        this.font = font;
        this.color = color;
    }

    /** {@inheritDoc} */
    @Override public void activeChanged()
    {
        setChanged();
    }

    /** {@inheritDoc} */
    @Override public void paintComponent(final Graphics2D g)
    {
        super.paintComponent(g);
        g.setFont(font);
        g.setColor(color);
        (isActive() ? down : up).render(g, getWidth());
        final Rectangle2D rect = font.getStringBounds(text, g.getFontRenderContext());
        final int y = (int)Math.round((h-rect.getMaxY()-rect.getMinY()))/2;
        g.drawString(text, (int)Math.round((w-rect.getWidth())/2), y);
    }

    /**
     * A set of images to form a button image.
     */
    public static class ButtonImages
    {
        public static final int OFFSET = 3;

        /**
         * The left border of the button.
         */
        private final Image imageLeft;

        /**
         * The background of the middle part of the button.
         */
        private final Image imageMiddle;

        /**
         * The right border of the button.
         */
        private final Image imageRight;

        /**
         * The button height.
         */
        private final int height;

        public ButtonImages(final Image imageLeft, final Image imageMiddle, final Image imageRight)
        {
            if (imageLeft == null) throw new IllegalArgumentException();
            if (imageMiddle == null) throw new IllegalArgumentException();
            if (imageRight == null) throw new IllegalArgumentException();
            if (imageLeft.getHeight(null) != imageMiddle.getHeight(null)) throw new IllegalArgumentException("left image height is "+imageLeft.getHeight(null)+" but middle image height is "+imageMiddle.getHeight(null));
            if (imageMiddle.getHeight(null) != imageRight.getHeight(null)) throw new IllegalArgumentException("middle image height is "+imageMiddle.getHeight(null)+" but right image height is "+imageRight.getHeight(null));

            this.imageLeft = imageLeft;
            this.imageMiddle = imageMiddle;
            this.imageRight = imageRight;
            height = imageMiddle.getHeight(null);
        }

        /**
         * Return the button height.
         * @return the height
         */
        public int getHeight()
        {
            return height;
        }

        /**
         * Return the minimal possible button width.
         * @return the minimal button width
         */
        public int getMinimumWidth()
        {
            return imageLeft.getWidth(null)+2*OFFSET+imageRight.getWidth(null);
        }

        /**
         * Draw the button.
         *
         * @param g The graphics to paint into.
         *
         * @param w The button width.
         */
        private void render(final Graphics g, final int w)
        {
            g.drawImage(imageLeft, 0, 0, null);
            g.drawImage(imageRight, w-imageRight.getWidth(null), 0, null);

            final int middleWidth = imageMiddle.getWidth(null);
            int tmpWidth = w-imageLeft.getWidth(null)-imageRight.getWidth(null);
            int tmpX = imageLeft.getWidth(null);
            while (tmpWidth > 0)
            {
                final int thisWidth = Math.min(tmpWidth, middleWidth);
                g.drawImage(imageMiddle, tmpX, 0, tmpX+thisWidth, height, 0, 0, thisWidth, height, null);
                tmpX += thisWidth;
                tmpWidth -= thisWidth;
            }
        }
    }
}

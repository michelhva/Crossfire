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

package com.realtime.crossfire.jxclient.gui.button;

import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.geom.RectangularShape;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link com.realtime.crossfire.jxclient.gui.gui.GUIElement GUIElement} that
 * implements a button. The button shows a background image and a text. The
 * background image consists of three parts: left, middle, and right. Left and
 * right are fixed size image, the middle part is clipped or repeated to the
 * actual with of the button. underlying images.
 * @author Andreas Kirschbaum
 */
public class GUITextButton extends AbstractButton {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The images comprising the "up" button state.
     */
    @NotNull
    private final ButtonImages up;

    /**
     * The images comprising the "down" button state.
     */
    @NotNull
    private final ButtonImages down;

    /**
     * The button text.
     */
    @NotNull
    private final String text;

    /**
     * The font to use.
     */
    @NotNull
    private final Font font;

    /**
     * The text color.
     */
    @NotNull
    private final Color color;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param extent the extent of this element
     * @param up the images comprising the "up" button state
     * @param down the images comprising the "down" button state
     * @param text the button text
     * @param font the font to use
     * @param color the text color
     * @param autoRepeat whether the button should autorepeat while being
     * pressed
     * @param commandList the commands to execute when the button is selected
     */
    public GUITextButton(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Extent extent, @NotNull final ButtonImages up, @NotNull final ButtonImages down, @NotNull final String text, @NotNull final Font font, @NotNull final Color color, final boolean autoRepeat, @NotNull final CommandList commandList) {
        super(tooltipManager, elementListener, name, extent, Transparency.TRANSLUCENT, autoRepeat, commandList);
        final int w = extent.getConstantW();
        final int h = extent.getConstantH();
        if (up.getHeight() != h) {
            throw new IllegalArgumentException("'up' state is height "+up.getHeight()+" but button height is "+h);
        }
        if (down.getHeight() != h) {
            throw new IllegalArgumentException("'down' state is height "+up.getHeight()+" but button height is "+h);
        }
        if (up.getMinimumWidth() > w) {
            throw new IllegalArgumentException("minimum width in 'up' state is "+up.getMinimumWidth()+" but button width is "+w);
        }
        if (down.getMinimumWidth() > w) {
            throw new IllegalArgumentException("minimum width in 'down' state is "+down.getMinimumWidth()+" but button width is "+w);
        }

        this.up = up;
        this.down = down;
        this.text = text;
        this.font = font;
        this.color = color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activeChanged() {
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void render(@NotNull final Graphics2D g2) {
        g2.setFont(font);
        g2.setColor(color);
        (isActive() ? down : up).render(g2, getWidth());
        final RectangularShape rectangle = font.getStringBounds(text, g2.getFontRenderContext());
        final int y = (int)Math.round(getHeight()-rectangle.getMaxY()-rectangle.getMinY())/2;
        g2.drawString(text, (int)Math.round((getWidth()-rectangle.getWidth())/2), y);
    }

}

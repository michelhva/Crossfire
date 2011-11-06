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

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.GUISelectable;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.geom.RectangularShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link com.realtime.crossfire.jxclient.gui.gui.GUIElement GUIElement} that
 * implements a button. The button shows a background image and a text. The
 * background image consists of three parts: left, middle, and right. Left and
 * right are fixed size image, the middle part is clipped or repeated to the
 * actual with of the button. underlying images.
 * @author Andreas Kirschbaum
 */
public class GUITextButton extends AbstractButton implements GUISelectable {

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
     * The text color when selected.
     */
    @NotNull
    private final Color colorSelected;

    /**
     * The preferred size of this component.
     */
    @NotNull
    private final Dimension preferredSize;

    /**
     * Whether the element is currently selected.
     */
    private boolean selected;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param up the images comprising the "up" button state
     * @param down the images comprising the "down" button state
     * @param text the button text
     * @param font the font to use
     * @param color the text color
     * @param colorSelected the text color when selected
     * @param autoRepeat whether the button should autorepeat while being
     * pressed
     * @param commandList the commands to execute when the button is selected
     */
    public GUITextButton(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final ButtonImages up, @NotNull final ButtonImages down, @NotNull final String text, @NotNull final Font font, @NotNull final Color color, @NotNull final Color colorSelected, final boolean autoRepeat, @NotNull final CommandList commandList) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT, autoRepeat, commandList);
        this.colorSelected = colorSelected;
        final int preferredHeight = up.getHeight();
        if (preferredHeight != down.getHeight()) {
            throw new IllegalArgumentException("'up' state height is "+preferredHeight+" but 'down' state height is "+down.getHeight());
        }
        this.up = up;
        this.down = down;
        this.text = text;
        this.font = font;
        this.color = color;
        preferredSize = GuiUtils.getTextDimension(text, getFontMetrics(font));
        if (preferredSize.height < preferredHeight) {
            preferredSize.height = preferredHeight;
        }
        preferredSize.width += 12;
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
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D)g;
        g2.setFont(font);
        g2.setColor(selected ? colorSelected : color);
        final int width = getWidth();
        (GuiUtils.isActive(this) ? down : up).render(g2, width);
        final FontRenderContext fontRenderContext = g2.getFontRenderContext();
        final RectangularShape rectangle = font.getStringBounds(text, fontRenderContext);
        final int x = (int)Math.round((width-rectangle.getWidth())/2);
        final FontMetrics fontMetrics = g2.getFontMetrics();
        final int y = (int)Math.round(preferredSize.height-rectangle.getHeight())/2+fontMetrics.getAscent();
        g2.drawString(text, x, y);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected Dimension getMinimumSizeInt() {
        return new Dimension(preferredSize);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, preferredSize.height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(final boolean selected) {
        this.selected = selected;
    }

}

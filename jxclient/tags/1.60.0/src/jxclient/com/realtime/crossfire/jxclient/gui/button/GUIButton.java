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
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link com.realtime.crossfire.jxclient.gui.gui.GUIElement GUIElement} that
 * implements a button. The button shows an image and optionally overlays a text
 * string.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIButton extends AbstractButton {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The image in unselected state.
     */
    @NotNull
    private final Image imageUp;

    /**
     * The image in selected state.
     */
    @NotNull
    private final Image imageDown;

    /**
     * The overlay text or <code>null</code> to display only the image. The text
     * is rendered using {@link #font}.
     */
    @Nullable
    private final String text;

    /**
     * The {@link Font} for the overlay {@link #text} or <code>null</code> to
     * display only the image.
     */
    @Nullable
    private final Font font;

    /**
     * The x coordinate of the overlay text.
     */
    private final int textX;

    /**
     * The y coordinate of the overlay text. This is the base line's y
     * coordinate.
     */
    private final int textY;

    /**
     * The {@link Color} of the overlay text or <code>null</code> to display
     * only the image.
     */
    @Nullable
    private final Color color;

    /**
     * Creates a new instance. Both <code>imageUp</code> and
     * <code>imageDown</code> must have the same size. The x/y coordinates
     * specify the base line of the first character of the overlay text.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param extent the extent of this element
     * @param imageUp the image in unselected state
     * @param imageDown the image in selected state
     * @param text the overlay text or <code>null</code> to display only the
     * image
     * @param font the font for the overlay text or <code>null</code> to display
     * only the image
     * @param color the color of the overlay text or <code>null</code> to
     * display only the image
     * @param textX the x coordinate of the overlay text
     * @param textY the y coordinate of the overlay text
     * @param autoRepeat whether the button should autorepeat while being
     * pressed
     * @param commandList the commands to execute when the button is elected
     */
    public GUIButton(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Extent extent, @NotNull final BufferedImage imageUp, @NotNull final BufferedImage imageDown, @Nullable final String text, @Nullable final Font font, @Nullable final Color color, final int textX, final int textY, final boolean autoRepeat, @NotNull final CommandList commandList) {
        super(tooltipManager, elementListener, name, extent, Transparency.TRANSLUCENT, autoRepeat, commandList);
        if (imageUp.getWidth() != imageDown.getWidth()) {
            throw new IllegalArgumentException();
        }
        if (imageUp.getHeight() != imageDown.getHeight()) {
            throw new IllegalArgumentException();
        }
        this.imageUp = imageUp;
        this.imageDown = imageDown;
        this.text = text;
        this.font = font;
        this.color = color;
        this.textX = textX;
        this.textY = textY;
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
        g2.drawImage(isActive() ? imageDown : imageUp, 0, 0, null);
        if (text != null && font != null && color != null) {
            g2.setFont(font);
            g2.setColor(color);
            g2.drawString(text, textX, textY);
        }
    }

}

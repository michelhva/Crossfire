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

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for all label classes. It manages the label text, and
 * renders the label's background.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class AbstractLabel extends AbstractGUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The label text.
     */
    @NotNull
    private String text;

    /**
     * The font for rendering the label text.
     */
    @NotNull
    private final Font textFont;

    /**
     * The text color.
     */
    @NotNull
    private final Color textColor;

    /**
     * The background image. It takes precedence over {@link #backgroundColor}.
     */
    @Nullable
    private ImageIcon backgroundImage;

    /**
     * If set, the opaque background color. This field is ignored if {@link
     * #backgroundImage} is set.
     */
    @Nullable
    private final Color backgroundColor;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param text the text
     * @param textFont the text font
     * @param textColor the text color
     * @param backgroundPicture the optional background picture
     * @param backgroundColor the background color; ignored if background
     * picture is set
     */
    protected AbstractLabel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final String text, @NotNull final Font textFont, @NotNull final Color textColor, @Nullable final BufferedImage backgroundPicture, @Nullable final Color backgroundColor) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT);
        this.text = text;
        this.textFont = textFont;
        this.textColor = textColor;
        backgroundImage = backgroundPicture == null ? null : new ImageIcon(backgroundPicture);
        this.backgroundColor = backgroundColor;
    }

    /**
     * The label text.
     * @param text the text
     */
    public void setText(@NotNull final String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            textChanged();
        }
    }

    /**
     * Will be called whenever {@link #text} has changed.
     */
    protected abstract void textChanged();

    /**
     * Returns the label text.
     * @return the label text
     */
    @NotNull
    protected String getText() {
        return text;
    }

    /**
     * Returns the font.
     * @return the font
     */
    @NotNull
    protected Font getTextFont() {
        return textFont;
    }

    /**
     * Returns the text color.
     * @return the text color
     */
    @NotNull
    protected Color getTextColor() {
        return textColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, null);
        } else if (backgroundColor != null) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Sets the background image. If both background color and background image
     * are set, the color is ignored.
     * @param backgroundImage the background image
     */
    protected void setBackgroundImage(@Nullable final ImageIcon backgroundImage) {
        this.backgroundImage = backgroundImage;
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // ignore
    }

}

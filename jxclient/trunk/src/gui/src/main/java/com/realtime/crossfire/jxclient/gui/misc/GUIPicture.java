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

package com.realtime.crossfire.jxclient.gui.misc;

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link AbstractGUIElement} that displays a picture.
 * @author Lauwenmark
 */
public class GUIPicture extends AbstractGUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The picture to paint.
     */
    @NotNull
    private final Image image;

    /**
     * The tooltip text to show.
     */
    @NotNull
    private final String tooltipText;

    /**
     * The preferred size of this component.
     */
    @NotNull
    private final Dimension preferredSize;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param image the picture to paint
     * @param alpha the transparency value
     * @param preferredWidth the preferred width of this picture
     * @param preferredHeight the preferred height of this picture
     * @param guiFactory the global GUI factory instance
     * @param tooltipText the text to show as the tooltip
     */
    public GUIPicture(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final BufferedImage image, final float alpha, final int preferredWidth, final int preferredHeight, @NotNull final GuiFactory guiFactory, @NotNull final String tooltipText) {
        super(tooltipManager, elementListener, name, alpha < 1.0F ? Transparency.TRANSLUCENT : image.getTransparency(), guiFactory);
        this.image = image;
        this.tooltipText = tooltipText;
        preferredSize = new Dimension(preferredWidth, preferredHeight);
    }

    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(preferredSize);
    }

    @NotNull
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(preferredSize);
    }

    @NotNull
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(preferredSize);
    }

    @Override
    public void notifyOpen() {
        setTooltipText(tooltipText);
    }

}

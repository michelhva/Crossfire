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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;

/**
 * @author Lauwenmark
 * @version 1.0
 * @since 1.0
 */
public class GUIPicture extends GUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The picture to paint.
     */
    @NotNull
    private final BufferedImage image;

    /**
     * The transparency value.
     */
    private final float alpha;

    /**
     * Create a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name The name of this element.
     * @param extent the extent of this element
     * @param image The picture to paint.
     * @param alpha The transparency value.
     */
    public GUIPicture(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Extent extent, @NotNull final BufferedImage image, final float alpha) {
        super(tooltipManager, elementListener, name, extent, alpha < 1F ? Transparency.TRANSLUCENT : image.getTransparency());
        this.image = image;
        this.alpha = alpha;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void render(@NotNull final Graphics2D g) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateResolution(final int screenWidth, final int screenHeight) {
        super.updateResolution(screenWidth, screenHeight);
        synchronized (bufferedImageSync) {
            final Graphics2D g = createBufferGraphics();
            try {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            } finally {
                g.dispose();
            }
        }
    }

}

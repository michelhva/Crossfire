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

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A background image for dialog windows.
 * @author Andreas Kirschbaum
 */
public class GUIDialogBackground extends AbstractGUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The north-west frame picture.
     */
    @NotNull
    private final Image frameNW;

    /**
     * The north frame picture.
     */
    @NotNull
    private final Image frameN;

    /**
     * The north-east frame picture.
     */
    @NotNull
    private final Image frameNE;

    /**
     * The west frame picture.
     */
    @NotNull
    private final Image frameW;

    /**
     * The center frame picture.
     */
    @NotNull
    private final Image frameC;

    /**
     * The east frame picture.
     */
    @NotNull
    private final Image frameE;

    /**
     * The south-west frame picture.
     */
    @NotNull
    private final Image frameSW;

    /**
     * The south frame picture.
     */
    @NotNull
    private final Image frameS;

    /**
     * The south-east frame picture.
     */
    @NotNull
    private final Image frameSE;

    /**
     * The width of the north border in pixel.
     */
    private final int heightN;

    /**
     * The width of the south border in pixel.
     */
    private final int heightS;

    /**
     * The width of the west border in pixel.
     */
    private final int widthW;

    /**
     * The width of the east border in pixel.
     */
    private final int widthE;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param transparency the transparency value for the backing buffer
     * @param frameNW The north-west frame picture.
     * @param frameN The north frame picture.
     * @param frameNE The north-east frame picture.
     * @param frameW The west frame picture.
     * @param frameC The center frame picture.
     * @param frameE The east frame picture.
     * @param frameSW The south-west frame picture.
     * @param frameS The south frame picture.
     * @param frameSE The south-east frame picture.
     */
    public GUIDialogBackground(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int transparency, @NotNull final BufferedImage frameNW, @NotNull final BufferedImage frameN, @NotNull final BufferedImage frameNE, @NotNull final BufferedImage frameW, @NotNull final Image frameC, @NotNull final BufferedImage frameE, @NotNull final BufferedImage frameSW, @NotNull final BufferedImage frameS, @NotNull final BufferedImage frameSE) {
        super(tooltipManager, elementListener, name, transparency);
        this.frameNW = frameNW;
        this.frameN = frameN;
        this.frameNE = frameNE;
        this.frameW = frameW;
        this.frameC = frameC;
        this.frameE = frameE;
        this.frameSW = frameSW;
        this.frameS = frameS;
        this.frameSE = frameSE;
        heightN = frameN.getHeight();
        heightS = frameS.getHeight();
        widthW = frameW.getWidth();
        widthE = frameE.getWidth();
        if (frameNW.getWidth() != widthW) {
            throw new IllegalArgumentException();
        }
        if (frameSW.getWidth() != widthW) {
            throw new IllegalArgumentException();
        }
        if (frameNE.getWidth() != widthE) {
            throw new IllegalArgumentException();
        }
        if (frameSE.getWidth() != widthE) {
            throw new IllegalArgumentException();
        }
        if (frameNW.getHeight() != heightN) {
            throw new IllegalArgumentException();
        }
        if (frameSW.getHeight() != heightS) {
            throw new IllegalArgumentException();
        }
        if (frameNE.getHeight() != heightN) {
            throw new IllegalArgumentException();
        }
        if (frameSE.getHeight() != heightS) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        final int w = Math.max(1, getWidth()-widthW-widthE);
        final int h = Math.max(1, getHeight()-heightN-heightS);
        g.drawImage(frameNW, 0, 0, null);
        g.drawImage(frameN, widthW, 0, widthW+w, heightN, 0, 0, w, heightN, null);
        g.drawImage(frameNE, widthW+w, 0, null);
        g.drawImage(frameW, 0, heightN, widthW, heightN+h, 0, 0, widthW, h, null);
        g.drawImage(frameC, widthW, heightN, widthW+w, heightN+h, 0, 0, w, h, null);
        g.drawImage(frameE, widthW+w, heightN, widthW+w+widthE, heightN+h, 0, 0, widthE, h, null);
        g.drawImage(frameSW, 0, heightN+h, null);
        g.drawImage(frameS, widthW, heightN+h, widthW+w, heightN+h+heightS, 0, 0, w, heightS, null);
        g.drawImage(frameSE, widthW+w, heightN+h, null);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(widthW+1+widthE, heightN+1+heightS);
    }

}

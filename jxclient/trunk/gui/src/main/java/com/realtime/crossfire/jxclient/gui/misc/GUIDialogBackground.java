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
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
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
     * The alpha value for the background, 1 opaque 0 full transparent.
     */
    private final float alpha;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param alpha the alpha value for the background, 1 is opaque, 0 full
     * transparent
     * @param frameNW the north-west frame picture
     * @param frameN the north frame picture
     * @param frameNE the north-east frame picture
     * @param frameW the west frame picture
     * @param frameC the center frame picture
     * @param frameE the east frame picture
     * @param frameSW the south-west frame picture
     * @param frameS the south frame picture
     * @param frameSE the south-east frame picture
     */
    public GUIDialogBackground(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final float alpha, @NotNull final Image frameNW, @NotNull final Image frameN, @NotNull final Image frameNE, @NotNull final Image frameW, @NotNull final Image frameC, @NotNull final Image frameE, @NotNull final Image frameSW, @NotNull final Image frameS, @NotNull final Image frameSE) {
        super(tooltipManager, elementListener, name, alpha < 1F ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        this.frameNW = frameNW;
        this.frameN = frameN;
        this.frameNE = frameNE;
        this.frameW = frameW;
        this.frameC = frameC;
        this.frameE = frameE;
        this.frameSW = frameSW;
        this.frameS = frameS;
        this.frameSE = frameSE;
        this.alpha = alpha;
        heightN = frameN.getHeight(null);
        heightS = frameS.getHeight(null);
        widthW = frameW.getWidth(null);
        widthE = frameE.getWidth(null);
        if (frameNW.getWidth(null) != widthW) {
            throw new IllegalArgumentException();
        }
        if (frameSW.getWidth(null) != widthW) {
            throw new IllegalArgumentException();
        }
        if (frameNE.getWidth(null) != widthE) {
            throw new IllegalArgumentException();
        }
        if (frameSE.getWidth(null) != widthE) {
            throw new IllegalArgumentException();
        }
        if (frameNW.getHeight(null) != heightN) {
            throw new IllegalArgumentException();
        }
        if (frameSW.getHeight(null) != heightS) {
            throw new IllegalArgumentException();
        }
        if (frameNE.getHeight(null) != heightN) {
            throw new IllegalArgumentException();
        }
        if (frameSE.getHeight(null) != heightS) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        final Graphics paint;
        if (alpha < 1F) {
            final Graphics2D g2d = (Graphics2D)g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            paint = g2d;
        } else {
            paint = g;
        }

        super.paintComponent(paint);
        final int w = Math.max(1, getWidth()-widthW-widthE);
        final int h = Math.max(1, getHeight()-heightN-heightS);
        paint.drawImage(frameNW, 0, 0, null);
        paint.drawImage(frameN, widthW, 0, widthW+w, heightN, 0, 0, w, heightN, null);
        paint.drawImage(frameNE, widthW+w, 0, null);
        paint.drawImage(frameW, 0, heightN, widthW, heightN+h, 0, 0, widthW, h, null);
        paint.drawImage(frameC, widthW, heightN, widthW+w, heightN+h, 0, 0, w, h, null);
        paint.drawImage(frameE, widthW+w, heightN, widthW+w+widthE, heightN+h, 0, 0, widthE, h, null);
        paint.drawImage(frameSW, 0, heightN+h, null);
        paint.drawImage(frameS, widthW, heightN+h, widthW+w, heightN+h+heightS, 0, 0, w, heightS, null);
        paint.drawImage(frameSE, widthW+w, heightN+h, null);
        if (paint != g) {
            paint.dispose();
        }
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

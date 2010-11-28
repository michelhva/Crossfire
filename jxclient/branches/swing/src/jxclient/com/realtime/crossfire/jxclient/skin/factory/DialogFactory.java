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

package com.realtime.crossfire.jxclient.skin.factory;

import com.realtime.crossfire.jxclient.gui.GUIDialogTitle;
import com.realtime.crossfire.jxclient.gui.GUIPicture;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.label.Alignment;
import com.realtime.crossfire.jxclient.gui.label.GUIOneLineLabel;
import com.realtime.crossfire.jxclient.skin.skin.Expression;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A factory class to create "textbutton" instances.
 * @author Andreas Kirschbaum
 */
public class DialogFactory {

    /**
     * The north-west frame picture.
     */
    @NotNull
    private final BufferedImage frameNW;

    /**
     * The north frame picture.
     */
    @NotNull
    private final BufferedImage frameN;

    /**
     * The north-east frame picture.
     */
    @NotNull
    private final BufferedImage frameNE;

    /**
     * The west frame picture.
     */
    @NotNull
    private final BufferedImage frameW;

    /**
     * The center frame picture.
     */
    @NotNull
    private final BufferedImage frameC;

    /**
     * The east frame picture.
     */
    @NotNull
    private final BufferedImage frameE;

    /**
     * The south-west frame picture.
     */
    @NotNull
    private final BufferedImage frameSW;

    /**
     * The south frame picture.
     */
    @NotNull
    private final BufferedImage frameS;

    /**
     * The south-east frame picture.
     */
    @NotNull
    private final BufferedImage frameSE;

    /**
     * The size of the north border in pixels.
     */
    private final int sizeN;

    /**
     * The size of the south border in pixels.
     */
    private final int sizeS;

    /**
     * The size of the west border in pixels.
     */
    private final int sizeW;

    /**
     * The size of the east border in pixels.
     */
    private final int sizeE;

    /**
     * The font for the dialog title.
     */
    private final Font titleFont;

    /**
     * The color for the dialog title.
     */
    private final Color titleColor;

    /**
     * The background color for the dialog title.
     */
    @Nullable
    private final Color titleBackgroundColor;

    /**
     * y The alpha value for the dialog background except for the title.
     */
    private final float alpha;

    /**
     * Create a new instance. The border images must have matching sizes.
     * @param frameNW The north-west frame picture.
     * @param frameN The north frame picture.
     * @param frameNE The north-east frame picture.
     * @param frameW The west frame picture.
     * @param frameC The center frame picture.
     * @param frameE The east frame picture.
     * @param frameSW The south-west frame picture.
     * @param frameS The south frame picture.
     * @param frameSE The south-east frame picture.
     * @param titleFont The font for the dialog title.
     * @param titleColor The color for the dialog title.
     * @param titleBackgroundColor The background color for the dialog title.
     * @param alpha The alpha value for the dialog background except for the
     * title.
     */
    public DialogFactory(@NotNull final BufferedImage frameNW, @NotNull final BufferedImage frameN, @NotNull final BufferedImage frameNE, @NotNull final BufferedImage frameW, @NotNull final BufferedImage frameC, @NotNull final BufferedImage frameE, @NotNull final BufferedImage frameSW, @NotNull final BufferedImage frameS, @NotNull final BufferedImage frameSE, @NotNull final Font titleFont, @NotNull final Color titleColor, @NotNull final Color titleBackgroundColor, final float alpha) {
        this.frameNW = frameNW;
        this.frameN = frameN;
        this.frameNE = frameNE;
        this.frameW = frameW;
        this.frameC = frameC;
        this.frameE = frameE;
        this.frameSW = frameSW;
        this.frameS = frameS;
        this.frameSE = frameSE;
        sizeN = frameN.getHeight(null);
        sizeS = frameS.getHeight(null);
        sizeW = frameW.getWidth(null);
        sizeE = frameE.getWidth(null);
        if (frameNW.getHeight(null) != sizeN) {
            throw new IllegalArgumentException("height of NW ("+frameNW.getHeight(null)+") does not match height of N ("+sizeN+")");
        }
        if (frameNE.getHeight(null) != sizeN) {
            throw new IllegalArgumentException("height of NE ("+frameNE.getHeight(null)+") does not match height of N ("+sizeN+")");
        }
        if (frameSW.getHeight(null) != sizeS) {
            throw new IllegalArgumentException("height of SW ("+frameSW.getHeight(null)+") does not match height of N ("+sizeS+")");
        }
        if (frameSE.getHeight(null) != sizeS) {
            throw new IllegalArgumentException("height of SE ("+frameSE.getHeight(null)+") does not match height of N ("+sizeS+")");
        }
        if (frameNW.getWidth(null) != sizeW) {
            throw new IllegalArgumentException("width of NW ("+frameNW.getWidth(null)+") does not match width of W");
        }
        if (frameNE.getWidth(null) != sizeE) {
            throw new IllegalArgumentException("width of NE ("+frameNE.getWidth(null)+") does not match width of E");
        }
        if (frameSW.getWidth(null) != sizeW) {
            throw new IllegalArgumentException("width of SW ("+frameSW.getWidth(null)+") does not match width of W");
        }
        if (frameSE.getWidth(null) != sizeE) {
            throw new IllegalArgumentException("width of SE ("+frameSE.getWidth(null)+") does not match width of E");
        }
        final int contentWidth = frameC.getWidth(null);
        final int contentHeight = frameC.getHeight(null);
        if (frameN.getWidth(null) != contentWidth) {
            throw new IllegalArgumentException("width of N ("+frameN.getWidth(null)+") does not match width of C ("+contentWidth+")");
        }
        if (frameS.getWidth(null) != contentWidth) {
            throw new IllegalArgumentException("width of S ("+frameS.getWidth(null)+") does not match width of C ("+contentWidth+")");
        }
        if (frameW.getHeight(null) != contentHeight) {
            throw new IllegalArgumentException("width of W ("+frameW.getHeight(null)+") does not match height of C ("+contentHeight+")");
        }
        if (frameE.getHeight(null) != contentHeight) {
            throw new IllegalArgumentException("width of E ("+frameE.getHeight(null)+") does not match height of C ("+contentHeight+")");
        }
        this.titleFont = titleFont;
        this.titleColor = titleColor;
        final int intAlpha = (int)(255*alpha);
        this.titleBackgroundColor = intAlpha == 0 ? null : new Color(titleBackgroundColor.getRed(), titleBackgroundColor.getGreen(), titleBackgroundColor.getBlue(), intAlpha);
        this.alpha = alpha;
    }

    /**
     * Create a new dialog.
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer the dialog belongs to
     * @param elementListener the element listener to notify
     * @param name The base name of the dialog's gui elements.
     * @param w The width of the dialog, including the frames.
     * @param h The height of the dialog, including the frames.
     * @param title The dialog's title, or an empty string for no title.
     * @param gui the gui to change into a dialog
     * @return the center component of the dialog
     */
    @NotNull
    public Container newDialog(@NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Expression w, @NotNull final Expression h, @NotNull final String title, @NotNull final Container gui) {
        final int titleHeight = title.length() > 0 ? 18 : 0;
        final int width = w.evaluate(1024, 768)/*XXX*/-sizeW-sizeE;
        final int height = h.evaluate(1024, 768)/*XXX*/-sizeN-sizeS+titleHeight;
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gui.add(new GUIPicture(tooltipManager, elementListener, name+"_nw", frameNW, alpha, sizeW, sizeN), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gui.add(new GUIPicture(tooltipManager, elementListener, name+"_n", frameN, alpha, width, sizeN));
        gbc.gridx = 2;
        gbc.weightx = 0;
        gui.add(new GUIPicture(tooltipManager, elementListener, name+"_ne", frameNE, alpha, sizeE, sizeN), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.gridheight = 2;
        gui.add(new GUIPicture(tooltipManager, elementListener, name+"_w", frameW, alpha, sizeW, height), gbc);
        gbc.gridx = 1;
        gbc.gridy = titleHeight > 0 ? 2 : 1;
        gbc.gridheight = titleHeight > 0 ? 1 : 2;
        gbc.weightx = 1;
        final Container result = new GUIPicture(tooltipManager, elementListener, name+"_c", frameC, alpha, width, height-titleHeight);
        gui.add(result, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gui.add(new GUIPicture(tooltipManager, elementListener, name+"_e", frameE, alpha, sizeE, height), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gui.add(new GUIPicture(tooltipManager, elementListener, name+"_sw", frameSW, alpha, sizeW, sizeS), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gui.add(new GUIPicture(tooltipManager, elementListener, name+"_s", frameS, alpha, width, sizeS), gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gui.add(new GUIPicture(tooltipManager, elementListener, name+"_se", frameSE, alpha, sizeE, sizeS), gbc);

        if (titleHeight > 0) {
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridheight = 1;
            gbc.weightx = 1;
            gbc.weighty = 0;
            final Container dialogTitle = new GUIDialogTitle(tooltipManager, windowRenderer, elementListener, name+"_t", frameC, alpha, width, titleHeight);
            if (!title.equals("_")) {
                final GUIOneLineLabel titleLabel = new GUIOneLineLabel(tooltipManager, elementListener, name+"_title", null, titleFont, titleColor, titleBackgroundColor, Alignment.LEFT, " "+title);
                dialogTitle.setLayout(new BorderLayout());
                dialogTitle.add(titleLabel, BorderLayout.CENTER);
                titleLabel.setIgnore();
            }
            gui.add(dialogTitle, gbc);
        }
        return result;
    }

}

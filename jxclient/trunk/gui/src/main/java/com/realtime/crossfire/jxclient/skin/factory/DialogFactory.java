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

package com.realtime.crossfire.jxclient.skin.factory;

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.label.GUIDialogTitle;
import com.realtime.crossfire.jxclient.gui.misc.GUIDialogBackground;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
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
     * The alpha transparency for the dialog background, 1 is opaque and 0 is
     * transparent.
     */
    private final float frameAlpha;

    /**
     * Creates a new instance. The border images must have matching sizes.
     * @param frameNW the north-west frame picture
     * @param frameN the north frame picture
     * @param frameNE the north-east frame picture
     * @param frameW the west frame picture
     * @param frameC the center frame picture
     * @param frameE the east frame picture
     * @param frameSW the south-west frame picture
     * @param frameS the south frame picture
     * @param frameSE the south-east frame picture
     * @param titleFont the font for the dialog title
     * @param titleColor the color for the dialog title
     * @param titleBackgroundColor the background color for the dialog title
     * @param frameAlpha the alpha value for the dialog background except for
     * the title
     */
    public DialogFactory(@NotNull final Image frameNW, @NotNull final Image frameN, @NotNull final Image frameNE, @NotNull final Image frameW, @NotNull final Image frameC, @NotNull final Image frameE, @NotNull final Image frameSW, @NotNull final Image frameS, @NotNull final Image frameSE, @NotNull final Font titleFont, @NotNull final Color titleColor, @NotNull final Color titleBackgroundColor, final float frameAlpha) {
        this.frameNW = frameNW;
        this.frameN = frameN;
        this.frameNE = frameNE;
        this.frameW = frameW;
        this.frameC = frameC;
        this.frameE = frameE;
        this.frameSW = frameSW;
        this.frameS = frameS;
        this.frameSE = frameSE;
        this.frameAlpha = frameAlpha;
        if (frameAlpha < 0F || frameAlpha > 1F) {
            throw new IllegalArgumentException("alpha transparency should be between 0 and 1 inclusive");
        }
        final int sizeN = frameN.getHeight(null);
        final int sizeS = frameS.getHeight(null);
        final int sizeW = frameW.getWidth(null);
        final int sizeE = frameE.getWidth(null);
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
        final int intAlpha = (int)(255*frameAlpha);
        this.titleBackgroundColor = intAlpha == 0 ? null : new Color(titleBackgroundColor.getRed(), titleBackgroundColor.getGreen(), titleBackgroundColor.getBlue(), intAlpha);
    }

    /**
     * Creates a new dialog.
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer the dialog belongs to
     * @param elementListener the element listener to notify
     * @param title the dialog's title, or an empty string for no title
     * @return the newly created GUI elements
     */
    @NotNull
    public Iterable<AbstractGUIElement> newDialog(@NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final String title) {
        final Collection<AbstractGUIElement> result = new ArrayList<AbstractGUIElement>();
        result.add(new GUIDialogBackground(tooltipManager, elementListener, "dialog_background", frameAlpha, frameNW, frameN, frameNE, frameW, frameC, frameE, frameSW, frameS, frameSE));

        if (title.length() > 0) {
            result.add(new GUIDialogTitle(tooltipManager, windowRenderer, elementListener, "dialog_title", titleFont, titleColor, titleBackgroundColor, title));
        }

        return result;
    }

    /**
     * Returns the alpha value for the frame background.
     * @return alpha value, 1 is opaque and 0 totally transparent
     */
    public float getFrameAlpha() {
        return frameAlpha;
    }
}

//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.skin;

import com.realtime.crossfire.jxclient.gui.GUIDialogTitle;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUILabel;
import com.realtime.crossfire.jxclient.gui.GUIOneLineLabel;
import com.realtime.crossfire.jxclient.gui.GUIPicture;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.ArrayList;

/**
 * A factory class to create "textbutton" instances.
 *
 * @author Andreas Kirschbaum
 */
public class DialogFactory
{
    /** The north-west frame picture. */
    private final BufferedImage frameNW;

    /** The north frame picture. */
    private final BufferedImage frameN;

    /** The north-east frame picture. */
    private final BufferedImage frameNE;

    /** The west frame picture. */
    private final BufferedImage frameW;

    /** The center frame picture. */
    private final BufferedImage frameC;

    /** The east frame picture. */
    private final BufferedImage frameE;

    /** The south-west frame picture. */
    private final BufferedImage frameSW;

    /** The south frame picture. */
    private final BufferedImage frameS;

    /** The south-east frame picture. */
    private final BufferedImage frameSE;

    /** The size of the north border in pixels. */
    private final int sizeN;

    /** The size of the south border in pixels. */
    private final int sizeS;

    /** The size of the west border in pixels. */
    private final int sizeW;

    /** The size of the east border in pixels. */
    private final int sizeE;

    /** The width of the center area in pixels. */
    private final int contentWidth;

    /** The height of the center area in pixels. */
    private final int contentHeight;

    /** The font for the dialog title. */
    private final Font titleFont;

    /** The color for the dialog title. */
    private final Color titleColor;

    /**y
     * The alpha value for the dialog background except for the title.
     */
    private final float alpha;

    /**
     * Create a new instance. The border images must have matching sizes.
     *
     * @param frameNW The north-west frame picture.
     *
     * @param frameN The north frame picture.
     *
     * @param frameNE The north-east frame picture.
     *
     * @param frameW The west frame picture.
     *
     * @param frameC The center frame picture.
     *
     * @param frameE The east frame picture.
     *
     * @param frameSW The south-west frame picture.
     *
     * @param frameS The south frame picture.
     *
     * @param frameSE The south-east frame picture.
     *
     * @param titleFont The font for the dialog title.
     *
     * @param titleColor The color for the dialog title.
     *
     * @param alpha The alpha value for the dialog background except for the
     * title.
     */
    public DialogFactory(final BufferedImage frameNW, final BufferedImage frameN, final BufferedImage frameNE, final BufferedImage frameW, final BufferedImage frameC, final BufferedImage frameE, final BufferedImage frameSW, final BufferedImage frameS, final BufferedImage frameSE, final Font titleFont, final Color titleColor, final float alpha)
    {
        if (frameNW == null) throw new IllegalArgumentException();
        if (frameN == null) throw new IllegalArgumentException();
        if (frameNE == null) throw new IllegalArgumentException();
        if (frameW == null) throw new IllegalArgumentException();
        if (frameC == null) throw new IllegalArgumentException();
        if (frameE == null) throw new IllegalArgumentException();
        if (frameSW == null) throw new IllegalArgumentException();
        if (frameS == null) throw new IllegalArgumentException();
        if (frameSE == null) throw new IllegalArgumentException();
        if (titleFont == null) throw new IllegalArgumentException();
        if (titleColor == null) throw new IllegalArgumentException();
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
        if (frameNW.getHeight(null) != sizeN) throw new IllegalArgumentException("height of NW does not match height of N");
        if (frameNE.getHeight(null) != sizeN) throw new IllegalArgumentException("height of NE does not match height of N");
        if (frameSW.getHeight(null) != sizeS) throw new IllegalArgumentException("height of SW does not match height of N");
        if (frameSE.getHeight(null) != sizeS) throw new IllegalArgumentException("height of SE does not match height of N");
        if (frameNW.getWidth(null) != sizeW) throw new IllegalArgumentException("width of NW does not match width of W");
        if (frameNE.getWidth(null) != sizeE) throw new IllegalArgumentException("width of NE does not match width of E");
        if (frameSW.getWidth(null) != sizeW) throw new IllegalArgumentException("width of SW does not match width of W");
        if (frameSE.getWidth(null) != sizeE) throw new IllegalArgumentException("width of SE does not match width of E");
        contentWidth = frameC.getWidth(null);
        contentHeight = frameC.getHeight(null);
        if (frameN.getWidth(null) != contentWidth) throw new IllegalArgumentException("width of N does not match width of C");
        if (frameS.getWidth(null) != contentWidth) throw new IllegalArgumentException("width of S does not match width of C");
        if (frameW.getHeight(null) != contentHeight) throw new IllegalArgumentException("width of W does not match height of C");
        if (frameE.getHeight(null) != contentHeight) throw new IllegalArgumentException("width of E does not match height of C");
        this.titleFont = titleFont;
        this.titleColor = titleColor;
        this.alpha = alpha;
    }

    /**
     * Create a new dialog.
     *
     * @param jxcWindow The JXCWindow instance.
     *
     * @param name The base name of the dialog's gui elements.
     *
     * @param w The width of the dialog, including the frames.
     *
     * @param h The height of the dialog, including the frames.
     *
     * @param title The dialog's title, or an empty string for no title.
     *
     * @return The gui elements comprising the new dialog.
     */
    public Collection<GUIElement> newDialog(final JXCWindow jxcWindow, final String name, final int w, final int h, final String title)
    {
        if (w <= sizeW+sizeE) throw new IllegalArgumentException("dialog height is smaller than heights of N and S");
        if (h <= sizeN+sizeS) throw new IllegalArgumentException("dialog width is smaller than heights of W and E");
        if (w > sizeW+contentWidth+sizeE) throw new IllegalArgumentException("dialog width is wider than W+C+E");
        if (h > sizeN+contentHeight+sizeS) throw new IllegalArgumentException("dialog height is taller than N+C+S");

        final int titleHeight = title.length() > 0 ? 18 : 0;
        final Collection<GUIElement> result = new ArrayList<GUIElement>();
        result.add(new GUIPicture(jxcWindow, name+"_nw", 0, 0, sizeW, sizeN, frameNW, alpha));
        result.add(new GUIPicture(jxcWindow, name+"_n", sizeW, 0, w-sizeW-sizeE, sizeN, frameN, alpha));
        result.add(new GUIPicture(jxcWindow, name+"_ne", w-sizeE, 0, sizeE, sizeN, frameNE, alpha));
        result.add(new GUIPicture(jxcWindow, name+"_w", 0, sizeN, sizeW, h-sizeN-sizeS, frameW, alpha));
        result.add(new GUIPicture(jxcWindow, name+"_c", sizeW, sizeN+titleHeight, w-sizeW-sizeE, h-sizeN-sizeS-titleHeight, frameC, alpha));
        result.add(new GUIPicture(jxcWindow, name+"_e", w-sizeE, sizeN, sizeE, h-sizeN-sizeS, frameE, alpha));
        result.add(new GUIPicture(jxcWindow, name+"_sw", 0, h-sizeS, sizeW, sizeS, frameSW, alpha));
        result.add(new GUIPicture(jxcWindow, name+"_s", sizeW, h-sizeS, w-sizeW-sizeE, sizeS, frameS, alpha));
        result.add(new GUIPicture(jxcWindow, name+"_se", w-sizeE, h-sizeS, sizeE, sizeS, frameSE, alpha));
        if (titleHeight > 0)
        {
            result.add(new GUIDialogTitle(jxcWindow, name+"_t", sizeW, sizeN, w-sizeW-sizeE, titleHeight, frameC));
            if (!title.equals("_"))
            {
                final GUIOneLineLabel titleLabel = new GUIOneLineLabel(jxcWindow, name+"_title", sizeW, sizeN, w-sizeW-sizeE, titleHeight, null, titleFont, titleColor, GUILabel.Alignment.LEFT, title);
                result.add(titleLabel);
                titleLabel.setIgnore();
            }
        }
        return result;
    }
}

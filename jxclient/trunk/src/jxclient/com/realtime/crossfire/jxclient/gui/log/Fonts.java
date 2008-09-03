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
package com.realtime.crossfire.jxclient.gui.log;

import java.awt.Font;

/**
 * Manage a set of fonts.
 *
 * @author Andreas Kirschbaum
 */
public class Fonts
{
    /**
     * The font to use for {@link FontID#PRINT}, {@link FontID#HAND}, and
     * {@link FontID#STRANGE} text.
     */
    private final Font fontPrint;

    /**
     * The font to use for {@link FontID#FIXED} text.
     */
    private final Font fontFixed;

    /**
     * The font to use for {@link FontID#FIXED} text which has bold enabled.
     */
    private final Font fontFixedBold;

    /**
     * The font to use for {@link FontID#ARCANE} text.
     */
    private final Font fontArcane;

    /**
     * Create a new instance.
     *
     * @param fontPrint The font to use for <code>FontID.PRINT</code>,
     * <code>FontID.HAND</code>, and <code>FontID.STANGE</code> text.
     *
     * @param fontFixed The font to use for <code>FontID.FIXED</code> text.
     *
     * @param fontFixedBold The font to use for <code>FontID.FIXED</code> text
     * which has bold enabled.
     *
     * @param fontArcane The font to use for <code>FontID.ARCANE</code> text.
     */
    public Fonts(final Font fontPrint, final Font fontFixed, final Font fontFixedBold, final Font fontArcane)
    {
        this.fontPrint = fontPrint;
        this.fontFixed = fontFixed;
        this.fontFixedBold = fontFixedBold;
        this.fontArcane = fontArcane;
    }

    /**
     * Return the font to use for {@link FontID#PRINT}, {@link FontID#HAND},
     * and {@link FontID#STRANGE} text.
     *
     * @return The font.
     */
    public Font getFontPrint()
    {
        return fontPrint;
    }

    /**
     * Return the font to use for {@link FontID#FIXED} text.
     *
     * @return The font.
     */
    public Font getFontFixed()
    {
        return fontFixed;
    }

    /**
     * Return the font to use for {@link FontID#FIXED} text which has bold
     * enabled.
     *
     * @return The font.
     */
    public Font getFontFixedBold()
    {
        return fontFixedBold;
    }

    /**
     * Return the font to use for {@link FontID#ARCANE} text.
     *
     * @return The font.
     */
    public Font getFontArcane()
    {
        return fontArcane;
    }
}

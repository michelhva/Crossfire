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

import com.realtime.crossfire.jxclient.gui.GUICheckBox;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 * A factory class to create "checkbox" instances.
 *
 * @author Andreas Kirschbaum
 */
public class CheckBoxFactory
{
    /**
     * The image for the "checked" state.
     */
    private final BufferedImage checked;

    /**
     * The image for the "unchecked" state.
     */
    private final BufferedImage unchecked;

    /**
     * The font to use.
     */
    private final Font font;

    /**
     * The text color.
     */
    private final Color color;

    /**
     * Create a new instance.
     *
     * @param checked The image for the "checked" state.
     *
     * @param unchecked The image for the "unchecked" state.
     *
     * @param font The font to use.
     *
     * @param color The text color.
     */
    public CheckBoxFactory(final BufferedImage checked, final BufferedImage unchecked, final Font font, final Color color)
    {
        if (checked == null) throw new IllegalArgumentException();
        if (unchecked == null) throw new IllegalArgumentException();
        if (font == null) throw new IllegalArgumentException();
        if (color == null) throw new IllegalArgumentException();

        this.checked = checked;
        this.unchecked = unchecked;
        this.font = font;
        this.color = color;
    }

    /**
     * Create a new checkbox.
     *
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param y The y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param option The option to affect.
     *
     * @param text The button text.
     *
     * @return The new checkbox.
     */
    public GUICheckBox newCheckBox(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final CheckBoxOption option, final String text)
    {
            return new GUICheckBox(jxcWindow, name, x, y, w, h, checked, unchecked, font, color, option, text);
    }
}

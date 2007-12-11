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

import com.realtime.crossfire.jxclient.gui.GUITextButton;
import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.Font;

/**
 * A factory class to create "textbutton" instances.
 *
 * @author Andreas Kirschbaum
 */
public class TextButtonFactory
{
    /**
     * The images comprising the "up" button state.
     */
    private final GUITextButton.ButtonImages up;

    /**
     * The images comprising the "down" button state.
     */
    private final GUITextButton.ButtonImages down;

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
     * @param up The images comprising the "up" button state.
     *
     * @param down The images comprising the "down" button state.
     *
     * @param font The font to use.
     *
     * @param color The text color.
     */
    public TextButtonFactory(final GUITextButton.ButtonImages up, final GUITextButton.ButtonImages down, final Font font, final Color color)
    {
        if (up == null) throw new IllegalArgumentException();
        if (down == null) throw new IllegalArgumentException();
        if (font == null) throw new IllegalArgumentException();
        if (color == null) throw new IllegalArgumentException();

        this.up = up;
        this.down = down;
        this.font = font;
        this.color = color;
    }

    /**
     * Create a new text button.
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
     * @param text The button text.
     *
     * @param autoRepeat Whether the button should autorepeat while being
     * pressed.
     *
     * @param commandList The commands to execute when the button is elected.
     */
    public GUITextButton newTextButton(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final String text, final boolean autoRepeat, final GUICommandList commandList)
    {
        return new GUITextButton(jxcWindow, name, x, y, w, h, up, down, text, font, color, autoRepeat, commandList);
    }
}

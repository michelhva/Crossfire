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

import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import java.awt.Color;
import java.awt.Image;

/**
 * A gui element implementing a static text field which may contain media tags.
 * @author Andreas Kirschbaum
 */
public class GUILabelLog extends GUILog
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link Parser} instance for parsing drawextinfo messages.
     */
    private final Parser parser = new Parser();

    /**
     * The default color to use for text message not specifying a color.
     */
    private final Color defaultColor;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer to notify
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen
     * @param y the y-coordinate for drawing this element to screen
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param backgroundImage the background image; may be <code>null</code> if
     * unused
     * @param fonts the <code>Fonts</code> instance for looking up fonts
     * @param defaultColor the default color to use for text message not
     * specifying a color
     */
    public GUILabelLog(final TooltipManager tooltipManager, final JXCWindowRenderer windowRenderer, final String name, final int x, final int y, final int w, final int h, final Image backgroundImage, final Fonts fonts, final Color defaultColor)
    {
        super(tooltipManager, windowRenderer, name, x, y, w, h, backgroundImage, fonts);
        this.defaultColor = defaultColor;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /**
     * Sets the displayed text by parsing a string.
     * @param string the string to parse
     */
    public void updateText(final CharSequence string)
    {
        buffer.clear();
        parser.parse(string, defaultColor, buffer);
        scrollTo(0);
    }
}

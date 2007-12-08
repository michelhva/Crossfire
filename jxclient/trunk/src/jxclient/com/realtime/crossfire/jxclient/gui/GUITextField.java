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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 * A text input field which executes a {@link GUICommandList} when ENTER is
 * pressed.
 *
 * @author Andreas Kirschbaum
 */
public class GUITextField extends GUIText
{
    private final GUICommandList commandList;

    public GUITextField(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage activePicture, final BufferedImage inactivePicture, final Font font, final Color inactiveColor, final Color activeColor, final String text, final GUICommandList commandList)
    {
        super(jxcWindow, name, x, y, w, h, activePicture, inactivePicture, font, inactiveColor, activeColor, text);
        if (commandList == null) throw new IllegalArgumentException();
        this.commandList = commandList;
    }

    /** {@inheritDoc} */
    protected void execute(final JXCWindow jxcWindow, final String command)
    {
        commandList.execute();
    }
}

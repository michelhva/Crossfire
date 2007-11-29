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
import java.awt.event.MouseEvent;

/**
 * Abstract base class for button classes.
 *
 * @author Andreas Kirschbaum
 */
public abstract class AbstractButton extends GUIElement
{
    /**
     * The commands to execute when the button is elected.
     */
    private final GUICommandList commandList;

    /**
     * Create a new instance.
     *
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen.
     *
     * @param y The y-coordinate for drawing this element to screen.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param commandList The commands to execute when the button is elected.
     */
    public AbstractButton(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final GUICommandList commandList)
    {
        super(jxcWindow, name, x, y, w, h);
        if (commandList == null) throw new IllegalArgumentException();
        this.commandList = commandList;
    }

    /** {@inheritDoc} */
    public void mouseReleased(final MouseEvent e)
    {
        final int b = e.getButton();
        switch(b)
        {
        case MouseEvent.BUTTON1:
            commandList.execute();
            setActive(false);
            final JXCWindow jxcWindow = (JXCWindow)e.getSource();
            jxcWindow.deactivateCurrentElement();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    public void mousePressed(final MouseEvent e)
    {
        final int b = e.getButton();
        switch(b)
        {
        case MouseEvent.BUTTON1:
            setActive(true);
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }
}

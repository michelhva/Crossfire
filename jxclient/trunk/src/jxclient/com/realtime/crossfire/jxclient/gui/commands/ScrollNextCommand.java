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
package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.gui.ActivatableGUIElement;

/**
 * A {@link GUIElement} which transfers the focus between two gui elements.
 * @author Andreas Kirschbaum
 */
public class ScrollNextCommand implements GUICommand
{
    /**
     * The element to activate.
     */
    private final ActivatableGUIElement nextElement;

    /**
     * The element to deactivate.
     */
    private final ActivatableGUIElement prevElement;

    /**
     * Creates a new instance.
     * @param nextElement the element to activate
     * @param prevElement the element to deactivate
     */
    public ScrollNextCommand(final ActivatableGUIElement nextElement, final ActivatableGUIElement prevElement)
    {
	this.nextElement = nextElement;
	this.prevElement = prevElement;
    }

    /** {@inheritDoc} */
    public boolean canExecute()
    {
	return true;
    }

    /** {@inheritDoc} */
    public void execute()
    {
	if (prevElement.isActive())
	{
	    nextElement.setActive(true);
	}
    }
}

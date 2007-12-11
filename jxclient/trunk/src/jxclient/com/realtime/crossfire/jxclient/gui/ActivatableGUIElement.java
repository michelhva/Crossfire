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

import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.event.MouseEvent;

/**
 *
 *
 * @author Andreas Kirschbaum
 */
public abstract class ActivatableGUIElement extends GUIElement
{
    /**
     * Whether the element is active.
     */
    private boolean active = false;

    /**
     * Create a new instance.
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
     */
    protected ActivatableGUIElement(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h)
    {
        super(jxcWindow, name, x, y, w, h);
    }

    /**
     * Set the active state.
     *
     * @param active The new active state.
     *
     * @return Whether the active state was changed.
     */
    public boolean setActive(final boolean active)
    {
        if (this.active == active)
        {
            return false;
        }

        this.active = active;
        return true;
    }

    /* {@inheritDoc} */
    @Override public boolean isActive()
    {
        return active;
    }

    /** {@inheritDoc} */
    @Override public void mousePressed(final MouseEvent e)
    {
        setActive(true);
    }
}

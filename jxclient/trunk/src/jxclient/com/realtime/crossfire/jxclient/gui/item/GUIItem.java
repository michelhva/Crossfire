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
package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIScrollable;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

/**
 * A {@link GUIElement} representing an in-game object.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class GUIItem extends ActivatableGUIElement implements GUIScrollable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * Creates a new instance.
     * @param window the window instance this element belongs to
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param y the y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     */
    protected GUIItem(final JXCWindow window, final String name, final int x, final int y, final int w, final int h)
    {
        super(window, name, x, y, w, h, Transparency.TRANSLUCENT);
    }

    /** {@inheritDoc} */
    @Override
    public void mouseClicked(final MouseEvent e)
    {
        super.mouseClicked(e);
        final JXCWindow window = (JXCWindow)e.getSource();
        switch (e.getButton())
        {
        case MouseEvent.BUTTON1:
            setActive(true);
            button1Clicked(window);
            break;

        case MouseEvent.BUTTON2:
            button2Clicked(window);
            break;

        case MouseEvent.BUTTON3:
            button3Clicked(window);
            break;
        }
    }

    /**
     * Called when the left mouse button was pressed.
     * @param window the window instance this element belongs to
     */
    public abstract void button1Clicked(final JXCWindow window);

    /**
     * Called when the middle mouse button was pressed.
     * @param window the window instance this element belongs to
     */
    public abstract void button2Clicked(final JXCWindow window);

    /**
     * Called when the right mouse button was pressed.
     * @param window the window instance this element belongs to
     */
    public abstract void button3Clicked(final JXCWindow window);

    /** {@inheritDoc} */
    @Override
    public void activeChanged()
    {
        setChanged();
    }

    @Override
    public void setElementVisible(final boolean visible)
    {
        super.setElementVisible(visible);
        setChanged();
    }
}

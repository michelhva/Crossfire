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
import com.realtime.crossfire.jxclient.gui.GUIScrollable;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public abstract class GUIItem extends ActivatableGUIElement implements GUIScrollable
{
    protected GUIItem(final JXCWindow window, final String name, final int x, final int y, final int w, final int h)
    {
        super(window, name, x, y, w, h, Transparency.TRANSLUCENT);
    }

    /** {@inheritDoc} */
    @Override public void mouseClicked(final MouseEvent e)
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

    public abstract void button1Clicked(final JXCWindow window);

    public abstract void button2Clicked(final JXCWindow window);

    public abstract void button3Clicked(final JXCWindow window);

    /** {@inheritDoc} */
    @Override public void activeChanged()
    {
        setChanged();
    }

    @Override public void setElementVisible(final boolean visible)
    {
        super.setElementVisible(visible);
        setChanged();
    }
}

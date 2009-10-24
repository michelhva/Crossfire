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
package com.realtime.crossfire.jxclient.gui.gui;

import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIElement} that can be set to active or inactive.
 *
 * @author Andreas Kirschbaum
 */
public abstract class ActivatableGUIElement extends GUIElement
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * Create a new instance.
     *
     * @param tooltipManager the tooltip manager to update
     *
     * @param windowRenderer the window renderer to notify
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
     * @param transparency The transparency value for the backing buffer
     */
    protected ActivatableGUIElement(@NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final String name, final int x, final int y, final int w, final int h, final int transparency)
    {
        super(tooltipManager, windowRenderer, name, x, y, w, h, transparency);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /**
     * Set the active state.
     *
     * @param active The new active state.
     */
    public void setActive(final boolean active)
    {
        final Gui gui = getGui();
        if (gui != null)
        {
            gui.setActiveElement(this, active);
        }
    }

    /**
     * Will be called whenever the active state has changed.
     */
    protected abstract void activeChanged();

    /* {@inheritDoc} */
    public boolean isActive()
    {
        final Gui gui = getGui();
        return gui != null && gui.getActiveElement() == this;
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(@NotNull final MouseEvent e)
    {
        setActive(true);
    }
}

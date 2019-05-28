/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIElement} representing an in-game object.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class GUIItem extends ActivatableGUIElement implements GUIScrollable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param guiFactory the global GUI factory instance
     */
    protected GUIItem(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT, guiFactory);
    }

    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            setActive(true);
            button1Clicked(e.getModifiersEx());
            break;

        case MouseEvent.BUTTON2:
            button2Clicked(e.getModifiersEx());
            break;

        case MouseEvent.BUTTON3:
            button3Clicked(e.getModifiersEx());
            break;
        }
    }

    @Override
    public void execute() {
        // ignore
    }

    /**
     * Called when the left mouse button was pressed.
     * @param modifiers the input event modifiers
     */
    public abstract void button1Clicked(final int modifiers);

    /**
     * Called when the middle mouse button was pressed.
     * @param modifiers the input event modifiers
     */
    public abstract void button2Clicked(final int modifiers);

    /**
     * Called when the right mouse button was pressed.
     * @param modifiers the input event modifiers
     */
    public abstract void button3Clicked(final int modifiers);

    @Override
    public void activeChanged() {
        setChanged();
    }

    @Override
    public void setVisible(final boolean aFlag) {
        super.setVisible(aFlag);
        setChanged();
    }

}

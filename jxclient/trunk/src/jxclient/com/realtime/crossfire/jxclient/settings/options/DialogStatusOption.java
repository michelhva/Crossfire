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
package com.realtime.crossfire.jxclient.settings.options;

import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.JXCWindow;

/**
 * An {@link Option} that affects the visibility of a dialog.
 *
 * @author Andreas Kirschbaum
 */
public class DialogStatusOption extends Option
{
    /**
     * The window to use.
     */
    private final JXCWindow window;

    /**
     * The tracked dialog.
     */
    private final Gui dialog;

    /**
     * The default state.
     */
    private final boolean defaultOpen;

    /**
     * Create a new instance.
     *
     * @param window The window to use.
     *
     * @param The tracked dialog.
     *
     * @param defaultOpen Whether the dialog is opened by default.
     */
    public DialogStatusOption(final JXCWindow window, final Gui dialog, final boolean defaultOpen)
    {
        this.window = window;
        this.dialog = dialog;
        this.defaultOpen = defaultOpen;
    }

    /**
     * Return the current state.
     *
     * @return The current state.
     */
    public boolean isOpen()
    {
        return window.isDialogOpen(dialog);
    }

    /**
     * Return the default state.
     *
     * @return The default state.
     */
    public boolean isDefaultOpen()
    {
        return defaultOpen;
    }

    /**
     * Set the current state.
     *
     * @param checked The new state.
     */
    public void setOpen(final boolean open)
    {
        if (isOpen() == open)
        {
            return;
        }

        window.toggleDialog(dialog);
        fireStateChangedEvent();
    }

    /** {@inheritDoc} */
    @Override protected void fireStateChangedEvent()
    {
        super.fireStateChangedEvent();
    }
}

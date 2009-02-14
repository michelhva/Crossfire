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

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.MouseTracker;

/**
 * Factory for creating {@link Gui} instances.
 * @author Andreas Kirschbaum
 */
public class GuiFactory
{
    /**
     * The mouse tracker when in debug GUI mode or <code>null</code> otherwise.
     */
    private final MouseTracker mouseTracker;

    /**
     * The commands instance for executing commands.
     */
    private final Commands commands;

    /**
     * The gui manager to use.
     */
    private final GuiManager guiManager;

    /**
     * Creates a new instance.
     * @param mouseTracker the mouse tracker when in debug GUI mode or
     * <code>null</code> otherwise
     * @param commands the commands instance for executing commands
     * @param guiManager the gui manager to use
     */
    public GuiFactory(final MouseTracker mouseTracker, final Commands commands, final GuiManager guiManager)
    {
        this.mouseTracker = mouseTracker;
        this.commands = commands;
        this.guiManager = guiManager;
    }

    /**
     * Creates a new {@link Gui} instance.
     * @return the new gui instance
     */
    public Gui newGui()
    {
        return new Gui(mouseTracker, commands, guiManager);
    }
}

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

import com.realtime.crossfire.jxclient.window.GuiManager;

/**
 * A {@link GUICommand} which activates the command input field.
 * @author Andreas Kirschbaum
 */
public class ActivateCommandInputCommand implements GUICommand
{
    /**
     * The {@link GuiManager} to affect.
     */
    private final GuiManager guiManager;

    /** The command text to set. */
    private final String commandText;

    /**
     * Creates a new instance.
     * @param commandText the command text to set
     * @param guiManager the gui manager to affect
     */
    public ActivateCommandInputCommand(final String commandText, final GuiManager guiManager)
    {
        this.commandText = commandText;
        this.guiManager = guiManager;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canExecute()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void execute()
    {
        guiManager.activateCommandInput(commandText);
    }

    /**
     * Returns the command text to set.
     * @return the command text
     */
    public String getCommandText()
    {
        return commandText;
    }
}

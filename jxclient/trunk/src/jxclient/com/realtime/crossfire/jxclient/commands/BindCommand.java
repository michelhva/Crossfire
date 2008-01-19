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
package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;

/**
 * Implements a "bind" command. It associates a key with a command.
 *
 * @author Andreas Kirschbaum
 */
public class BindCommand extends AbstractCommand
{
    /**
     * Create a new instance.
     *
     * @param window The window to execute in.
     */
    protected BindCommand(final JXCWindow window)
    {
        super(window);
    }

    /** {@inheritDoc} */
    public boolean allArguments()
    {
        return true;
    }

    /** {@inheritDoc} */
    public void execute(final String args)
    {
        final String commands;
        final boolean perCharacterBinding;
        if (args.equals("-c"))
        {
            perCharacterBinding = true;
            commands = "";
        }
        else if (args.startsWith("-c "))
        {
            perCharacterBinding = true;
            commands = args.substring(3).trim();
        }
        else
        {
            perCharacterBinding = false;
            commands = args;
        }

        if (commands.length() == 0)
        {
            drawInfoError("Which command to you want to bind?");
            return;
        }

        if (!getWindow().createKeyBinding(perCharacterBinding, new GUICommandList(GUICommandList.Type.AND, commands, getWindow())))
        {
            drawInfoError("Cannot use bind -c since no character is logged in.");
            return;
        }
    }
}

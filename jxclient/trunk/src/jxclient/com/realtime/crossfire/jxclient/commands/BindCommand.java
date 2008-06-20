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

import com.realtime.crossfire.jxclient.gui.commands.GUICommandFactory;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.util.StringUtils;
import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.JXCWindow;

/**
 * Implements a "bind" command. It associates a key with a command.
 *
 * @author Andreas Kirschbaum
 */
public class BindCommand extends AbstractCommand
{
    /**
     * The windows to execute in.
     */
    private final JXCWindow window;

    /**
     * The commands instance for executing commands.
     */
    private final Commands commands;

    /**
     * Create a new instance.
     *
     * @param window The window to execute in.
     *
     * @param crossfireServerConnection the connection instance
     *
     * @param commands the commands instance for executing commands
     */
    protected BindCommand(final JXCWindow window, final CrossfireServerConnection crossfireServerConnection, final Commands commands)
    {
        super(crossfireServerConnection);
        this.window = window;
        this.commands = commands;
    }

    /** {@inheritDoc} */
    public boolean allArguments()
    {
        return true;
    }

    /** {@inheritDoc} */
    public void execute(final String args)
    {
        final String commandList;
        final boolean perCharacterBinding;
        if (args.equals("-c"))
        {
            perCharacterBinding = true;
            commandList = "";
        }
        else if (args.startsWith("-c "))
        {
            perCharacterBinding = true;
            commandList = StringUtils.trimLeading(args.substring(3));
        }
        else
        {
            perCharacterBinding = false;
            commandList = args;
        }

        if (commandList.length() == 0)
        {
            drawInfoError("Which command do you want to bind?");
            return;
        }

        final GUICommandList commandList2 = new GUICommandList(GUICommandList.CommandType.AND);
        commandList2.add(GUICommandFactory.createCommand(commandList, window, commands));
        if (!window.createKeyBinding(perCharacterBinding, commandList2))
        {
            drawInfoError("Cannot use bind -c since no character is logged in.");
            return;
        }
    }
}

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

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parses and executes client-side commands.
 *
 * @author Andreas Kirschbaum
 */
public class Commands
{
    /**
     * The pattern to split a command from argments, and to split arguments.
     */
    public static final Pattern patternWhitespace = Pattern.compile(" +");

    /**
     * Maps command name to {@link Command} instance.
     */
    private final Map<String, Command> commands = new HashMap<String, Command>();

    /**
     * Create a new instance.
     *
     * @param window The window to execute the commands in.
     *
     * @param crossfireServerConnection the connection instance
     */
    public Commands(final JXCWindow window, final CrossfireServerConnection crossfireServerConnection)
    {
        commands.put("bind", new BindCommand(window, crossfireServerConnection));
        commands.put("unbind", new UnbindCommand(window, crossfireServerConnection));
        commands.put("screenshot", new ScreenshotCommand(window, crossfireServerConnection));
        commands.put("script", new ScriptCommand(window, crossfireServerConnection));
        commands.put("exec", new ExecCommand(window, crossfireServerConnection));
        commands.put("set", new SetCommand(window, crossfireServerConnection));
    }

    /**
     * Execute a client-side command.
     *
     * @param command The command.
     *
     * @param commandList The command and all remaining commands of the command
     * list.
     *
     * @return 0=unknown command, 1=command was executed, 2=whole command list
     * was executed.
     */
    public int execute(final String command, final String commandList)
    {
        final String[] args = patternWhitespace.split(command.trim(), 2);
        final Command cmd = commands.get(args[0]);
        if (cmd == null)
        {
            return 0;
        }

        if (!cmd.allArguments())
        {
            cmd.execute(args.length >= 2 ? args[1] : "");
            return 1;
        }

        assert commandList.startsWith(command);
        final String[] argsList = patternWhitespace.split(commandList.trim(), 2);
        assert argsList[0].equals(args[0]);

        cmd.execute(argsList.length >= 2 ? argsList[1] : "");
        return 2;
    }
}

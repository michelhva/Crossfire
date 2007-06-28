/* $Id$ */

package com.realtime.crossfire.jxclient;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of {@link GUICommand} instances.
 *
 * @author Andreas Kirschbaum
 */
public class GUICommandList
{
    /**
     * The list of {@link GUICommands} in execution order.
     */
    private final List<GUICommand> commands = new ArrayList<GUICommand>();

    /**
     * Create a new instance as an empty command list.
     */
    public GUICommandList()
    {
    }

    /**
     * Create a new instance from a string. The passed string contains a list
     * of commands to be sent to the server. The commands are separated by ';'
     * characters.
     *
     * @param commands The commands.
     *
     * @param jxcWindow The window to execute the commands in.
     */
    public GUICommandList(final String commands, final JXCWindow jxcWindow)
    {
        final String[] cmds = commands.split(";");
        for (final String command : commands.trim().split(" *; *"))
        {
            this.commands.add(new GUICommand(null, GUICommand.Command.GUI_SEND_COMMAND, new GUICommand.SendCommandParameter(jxcWindow, command)));
        }
    }

    /**
     * Add a command to the end of this command list.
     *
     * @param guiCommand The command to add.
     */
    public void add(final GUICommand guiCommand)
    {
        commands.add(guiCommand);
    }

    /**
     * Execute the command list by calling {@link GUICommand.execute()} for
     * each command in order.
     */
    public void execute()
    {
        for (final GUICommand command : commands)
        {
            if (!command.canExecute())
            {
                return;
            }
        }

        for (final GUICommand command : commands)
        {
            command.execute();
        }
    }

    /**
     * Return the commands as a string.
     *
     * @return The commands as a string.
     */
    public String getCommandString()
    {
        final StringBuilder sb = new StringBuilder();
        boolean firstCommand = true;
        for (final GUICommand guiCommand : commands)
        {
            if (guiCommand.getOrder() != GUICommand.Command.GUI_SEND_COMMAND)
            {
                throw new AssertionError("Cannot encode command of type "+guiCommand.getOrder());
            }

            if (firstCommand)
            {
                firstCommand = false;
            }
            else
            {
                sb.append(';');
            }
            final GUICommand.SendCommandParameter parameter = (GUICommand.SendCommandParameter)guiCommand.getParams();
            sb.append(parameter.getCommand());
        }
        return sb.toString();
    }
}

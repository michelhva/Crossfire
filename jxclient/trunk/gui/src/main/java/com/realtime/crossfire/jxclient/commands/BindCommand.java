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

package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.commandlist.CommandListType;
import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.commands.GUICommandFactory;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the "bind" command. It associates a key with a command.
 * @author Andreas Kirschbaum
 */
public class BindCommand extends AbstractCommand {

    /**
     * The {@link CommandCallback} to use.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * The {@link GUICommandFactory} for creating commands.
     */
    @NotNull
    private final GUICommandFactory guiCommandFactory;

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection instance
     * @param commandCallback the command callback to use
     * @param guiCommandFactory the gui command factory for creating commands
     */
    public BindCommand(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final CommandCallback commandCallback, @NotNull final GUICommandFactory guiCommandFactory) {
        super("bind", crossfireServerConnection);
        this.commandCallback = commandCallback;
        this.guiCommandFactory = guiCommandFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allArguments() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NotNull final String args) {
        final String commandList;
        final boolean perCharacterBinding;
        if (args.equals("-c")) {
            perCharacterBinding = true;
            commandList = "";
        } else if (args.startsWith("-c ")) {
            perCharacterBinding = true;
            commandList = StringUtils.trimLeading(args.substring(3));
        } else {
            perCharacterBinding = false;
            commandList = args;
        }

        if (commandList.length() == 0) {
            drawInfoError("Which command do you want to bind?");
            return;
        }

        final CommandList commandList2 = new CommandList(CommandListType.AND);
        commandList2.add(guiCommandFactory.createCommand(commandList));
        if (!commandCallback.createKeyBinding(perCharacterBinding, commandList2)) {
            drawInfoError("Cannot use bind -c since no character is logged in.");
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

}

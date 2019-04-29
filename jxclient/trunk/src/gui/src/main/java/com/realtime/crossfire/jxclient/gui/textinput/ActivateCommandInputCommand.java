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

package com.realtime.crossfire.jxclient.gui.textinput;

import com.realtime.crossfire.jxclient.gui.commandlist.GUICommand;
import com.realtime.crossfire.jxclient.gui.commandlist.GUICommand2;
import com.realtime.crossfire.jxclient.settings.Macros;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} which activates the command input field.
 * @author Andreas Kirschbaum
 */
public class ActivateCommandInputCommand implements GUICommand2 {

    /**
     * The {@link CommandCallback} to affect.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * The command text to set.
     */
    @NotNull
    private final String commandText;

    /**
     * The {@link Macros} instance to use.
     */
    @NotNull
    private final Macros macros;

    /**
     * Creates a new instance.
     * @param commandText the command text to set
     * @param commandCallback the command callback to affect
     * @param macros the macros instance to use
     */
    public ActivateCommandInputCommand(@NotNull final String commandText, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) {
        this.commandText = commandText;
        this.commandCallback = commandCallback;
        this.macros = macros;
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        commandCallback.activateCommandInput(macros.expandMacros(commandText));
    }

    @NotNull
    @Override
    public String getCommandString() {
        return commandText.isEmpty() ? "-e" : "-e "+commandText;
    }

}

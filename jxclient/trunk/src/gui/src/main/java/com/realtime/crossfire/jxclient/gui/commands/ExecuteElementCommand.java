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

package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.gui.commandlist.GUICommand;
import com.realtime.crossfire.jxclient.gui.item.GUIItem;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} which executes (i.e., simulates a left-button mouse
 * click on) an {@link GUIItem}.
 * @author Andreas Kirschbaum
 */
public class ExecuteElementCommand implements GUICommand {

    /**
     * The item element to execute.
     */
    @NotNull
    private final GUIItem item;

    /**
     * Creates a new instance.
     * @param item the item element to execute
     */
    public ExecuteElementCommand(@NotNull final GUIItem item) {
        this.item = item;
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        item.button1Clicked(0);
    }

}

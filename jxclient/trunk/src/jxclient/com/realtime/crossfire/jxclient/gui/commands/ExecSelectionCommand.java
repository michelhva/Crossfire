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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.gui.item.GUIItemItem;
import com.realtime.crossfire.jxclient.gui.list.GUIItemList;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CurrentFloorManager;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUICommand} that executes a command on the selected item of a
 * {@link GUIItemList}.
 * @author Andreas Kirschbaum
 */
public class ExecSelectionCommand implements GUICommand
{
    /**
     * The executable commands.
     */
    public enum CommandType
    {
        /**
         * Apply an item.
         */
        APPLY
            {
                /** {@inheritDoc} */
                @Override
                protected void doExecute(@NotNull final CfItem item, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue)
                {
                    crossfireServerConnection.sendApply(item.getTag());
                }
            },

        /**
         * Drop an item (to the ground of into an opened container).
         */
        DROP
            {
                /** {@inheritDoc} */
                @Override
                protected void doExecute(@NotNull final CfItem item, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue)
                {
                    if (item.isLocked())
                    {
                        crossfireServerConnection.drawInfo("This item is locked. To drop it, first unlock by SHIFT+leftclicking on it.", 3);
                    }
                    else
                    {
                        commandQueue.sendMove(floor, item.getTag());
                    }
                }
            },

        /**
         * Examine an item.
         */
        EXAMINE
            {
                /** {@inheritDoc} */
                @Override
                protected void doExecute(@NotNull final CfItem item, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue)
                {
                    crossfireServerConnection.sendExamine(item.getTag());
                }
            },

        /**
         * Lock an item.
         */
        LOCK
            {
                /** {@inheritDoc} */
                @Override
                protected void doExecute(@NotNull final CfItem item, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue)
                {
                    crossfireServerConnection.sendLock(true, item.getTag());
                }
            },

        /**
         * Toggle the lock of an item.
         */
        LOCK_TOGGLE
            {
                /** {@inheritDoc} */
                @Override
                protected void doExecute(@NotNull final CfItem item, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue)
                {
                    crossfireServerConnection.sendLock(!item.isLocked(), item.getTag());
                }
            },

        /**
         * Mark an item.
         */
        MARK
            {
                /** {@inheritDoc} */
                @Override
                protected void doExecute(@NotNull final CfItem item, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue)
                {
                    crossfireServerConnection.sendMark(item.getTag());
                }
            },

        /**
         * Unlock an item.
         */
        UNLOCK
            {
                /** {@inheritDoc} */
                @Override
                protected void doExecute(@NotNull final CfItem item, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue)
                {
                    crossfireServerConnection.sendLock(false, item.getTag());
                }
            },
        ;

        /**
         * Returns whether the action can be executed.
         * @param guiItem the item to check for
         * @return whether the action can be executed
         */
        public static boolean canExecute(@Nullable final GUIItemItem guiItem)
        {
            return guiItem != null && guiItem.getItem() != null;
        }

        /**
         * Executes the action.
         * @param guiItem the item to execute on
         * @param crossfireServerConnection the server connection to use
         * @param floor the current floor index
         * @param commandQueue the command queue to use
         */
        public void execute(@Nullable final GUIItemItem guiItem, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue)
        {
            if (guiItem == null)
            {
                return;
            }

            final CfItem item = guiItem.getItem();
            if (item == null)
            {
                return;
            }

            doExecute(item, crossfireServerConnection, floor, commandQueue);
        }

        /**
         * Executes the action.
         * @param item the item to execute on
         * @param crossfireServerConnection the server connection to use
         * @param floor the current floor index
         * @param commandQueue the command queue to use
         */
        protected abstract void doExecute(@NotNull final CfItem item, @NotNull final CrossfireServerConnection crossfireServerConnection, final int floor, @NotNull final CommandQueue commandQueue);
    }

    /**
     * The list to execute in.
     */
    @NotNull
    private final GUIItemList list;

    /**
     * The command to execute.
     */
    @NotNull
    private final CommandType command;

    /**
     * The connection to execute commands on.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The floor manager to use.
     */
    @NotNull
    private final CurrentFloorManager floorManager;

    /**
     * The command queue to use.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * Creates a new instance.
     * @param list the list to execute in
     * @param command the command to execute
     * @param crossfireServerConnection the connection to execute commands on
     * @param floorManager the floor manager to use
     * @param commandQueue the command queue to use
     */
    public ExecSelectionCommand(@NotNull final GUIItemList list, @NotNull final CommandType command, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final CurrentFloorManager floorManager, @NotNull final CommandQueue commandQueue)
    {
        this.list = list;
        this.command = command;
        this.crossfireServerConnection = crossfireServerConnection;
        this.floorManager = floorManager;
        this.commandQueue = commandQueue;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canExecute()
    {
        return CommandType.canExecute(list.getSelectedItem());
    }

    /** {@inheritDoc} */
    @Override
    public void execute()
    {
        command.execute(list.getSelectedItem(), crossfireServerConnection, floorManager.getCurrentFloor(), commandQueue);
    }
}

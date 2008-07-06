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

import com.realtime.crossfire.jxclient.gui.item.GUIItemItem;
import com.realtime.crossfire.jxclient.gui.list.GUIItemList;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CurrentFloorManager;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;

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
                protected void doExecute(final CfItem item, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue)
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
                protected void doExecute(final CfItem item, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue)
                {
                    if (item.isLocked())
                    {
                        crossfireServerConnection.drawInfo("This item is locked. To drop it, first unlock by SHIFT+leftclicking on it.", 3);
                    }
                    else
                    {
                        crossfireServerConnection.sendMove(floor, item.getTag(), commandQueue.getRepeatCount());
                    }
                }
            },

        /**
         * Examine an item.
         */
        EXAMINE
            {
                /** {@inheritDoc} */
                protected void doExecute(final CfItem item, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue)
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
                protected void doExecute(final CfItem item, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue)
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
                protected void doExecute(final CfItem item, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue)
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
                protected void doExecute(final CfItem item, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue)
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
                protected void doExecute(final CfItem item, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue)
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
        public static boolean canExecute(final GUIItemItem guiItem)
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
        public void execute(final GUIItemItem guiItem, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue)
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
        protected abstract void doExecute(final CfItem item, final CrossfireServerConnection crossfireServerConnection, final int floor, final CommandQueue commandQueue);
    }

    /**
     * The list to execute in.
     */
    private final GUIItemList list;

    /**
     * The command to execute.
     */
    private final CommandType command;

    /**
     * The connection to execute commands on.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The floor manager to use.
     */
    private final CurrentFloorManager floorManager;

    /**
     * The command queue to use.
     */
    private final CommandQueue commandQueue;

    /**
     * Creates a new instance.
     * @param list the list to execute in
     * @param command the command to execute
     * @param crossfireServerConnection the connection to execute commands on
     * @param floorManager the floor manager to use
     * @param commandQueue the command queue to use
     */
    public ExecSelectionCommand(final GUIItemList list, final CommandType command, final CrossfireServerConnection crossfireServerConnection, final CurrentFloorManager floorManager, final CommandQueue commandQueue)
    {
        this.list = list;
        this.command = command;
        this.crossfireServerConnection = crossfireServerConnection;
        this.floorManager = floorManager;
        this.commandQueue = commandQueue;
    }

    /** {@inheritDoc} */
    public boolean canExecute()
    {
        return CommandType.canExecute(list.getSelectedItem());
    }

    /** {@inheritDoc} */
    public void execute()
    {
        command.execute(list.getSelectedItem(), crossfireServerConnection, floorManager.getCurrentFloor(), commandQueue);
    }
}

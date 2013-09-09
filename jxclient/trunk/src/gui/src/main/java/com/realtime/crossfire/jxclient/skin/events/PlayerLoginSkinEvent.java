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

package com.realtime.crossfire.jxclient.skin.events;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemSetListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link SkinEvent} that executes a {@link CommandList} at connection setup.
 * @author Andreas Kirschbaum
 */
public class PlayerLoginSkinEvent implements SkinEvent {

    /**
     * Whether to generate login events (<code>true</code>) or logout events
     * (<code>false</code>).
     */
    private final boolean login;

    /**
     * The {@link CommandList} to execute.
     */
    @NotNull
    private final CommandList commandList;

    /**
     * The {@link ItemSet} to watch.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The {@link ItemSetListener} attached to {@link #itemSet}.
     */
    @NotNull
    private final ItemSetListener itemSetListener = new ItemSetListener() {

        @Override
        public void itemAdded(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemMoved(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemChanged(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemRemoved(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void playerChanged(@Nullable final CfItem player) {
            //noinspection VariableNotUsedInsideIf
            if (player == null) {
                if (!login) {
                    commandList.execute();
                }
            } else {
                if (login) {
                    commandList.execute();
                }
            }
        }

        @Override
        public void openContainerChanged(final int tag) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param login whether to generate login events (<code>true</code>) or
     * logout events (<code>false</code>)
     * @param commandList the command list to execute
     * @param itemSet the item set to watch
     */
    public PlayerLoginSkinEvent(final boolean login, @NotNull final CommandList commandList, @NotNull final ItemSet itemSet) {
        this.login = login;
        this.commandList = commandList;
        this.itemSet = itemSet;
        this.itemSet.addItemSetListener(itemSetListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        itemSet.removeItemSetListener(itemSetListener);
    }

}

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

package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.crossfire.messages.UpdItem;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.stats.Stats;
import org.jetbrains.annotations.NotNull;

/**
 * Manages items known to the character. This includes items on the floor, in
 * the character's inventory, the character object itself, and items within
 * containers known to the character.
 * @author Andreas Kirschbaum
 */
public class ItemsManager {

    /**
     * The {@link FacesManager} instance for looking up faces.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The {@link Stats} instance to update.
     */
    @NotNull
    private final Stats stats;

    /**
     * The {@link SkillSet} instance to update.
     */
    @NotNull
    private final SkillSet skillSet;

    /**
     * The known {@link CfItem CfItems}.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        @Override
        public void start() {
            itemSet.reset();
        }

        @Override
        public void metaserver() {
            itemSet.reset();
        }

        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        @Override
        public void connecting(@NotNull final String serverInfo) {
            itemSet.reset();
        }

        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        @Override
        public void connected() {
            // ignore
        }

        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param facesManager the faces manager for looking up faces
     * @param stats the instance to update
     * @param skillSet the skill set instance to update
     * @param guiStateManager the gui state manager to watch
     * @param itemSet the item set to use
     */
    public ItemsManager(@NotNull final FacesManager facesManager, @NotNull final Stats stats, @NotNull final SkillSet skillSet, @NotNull final GuiStateManager guiStateManager, @NotNull final ItemSet itemSet) {
        this.facesManager = facesManager;
        this.stats = stats;
        this.skillSet = skillSet;
        this.itemSet = itemSet;
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /**
     * A "delinv" command has been received.
     * @param tag the item tag
     */
    public void delinvReceived(final int tag) {
        itemSet.cleanInventory(tag);
    }

    /**
     * A "delitem" command has been received.
     * @param tags the item tags
     */
    public void delitemReceived(@NotNull final int[] tags) {
        itemSet.removeItems(tags);
    }

    /**
     * An "additem" has been received.
     * @param location the item's location
     * @param tag the item tag
     * @param flags the item's flags
     * @param weight the item's weight
     * @param faceNum the item's face ID
     * @param name the item's singular name
     * @param namePl the item's plural name
     * @param anim the item's animation ID
     * @param animSpeed the item's animation speed
     * @param nrof the number of items
     * @param type the item's type
     */
    public void addItemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
        itemSet.addItem(new CfItem(location, tag, flags, weight, facesManager.getFace(faceNum), name, namePl, anim, animSpeed, nrof, type));
    }

    /**
     * A "player" command has been received.
     * @param tag the player's taq
     * @param weight the player's weight
     * @param faceNum the player's face ID
     * @param name the player's name
     */
    public void playerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name) {
        stats.setActiveSkill("");
        skillSet.clearNumberedSkills();
        itemSet.setPlayer(new CfPlayer(tag, weight, facesManager.getFace(faceNum), name));
        stats.setStat(Stats.C_STAT_WEIGHT, weight);
    }

    /**
     * An "upditem" command has been received.
     * @param flags the changed values
     * @param tag the item's tag
     * @param valLocation the item's location
     * @param valFlags the item's flags
     * @param valWeight the item's weight
     * @param valFaceNum the item's face ID
     * @param valName the item's singular name
     * @param valNamePl the item's plural name
     * @param valAnim the item's animation ID
     * @param valAnimSpeed the item's animation speed
     * @param valNrof the number of items
     */
    public void upditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
        itemSet.updateItem(flags, tag, valLocation, valFlags, valWeight, facesManager.getFace(valFaceNum), valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        if ((flags&UpdItem.UPD_WEIGHT) != 0) {
            final CfItem player = itemSet.getPlayer();
            if (player != null && player.getTag() == tag) {
                stats.setStat(Stats.C_STAT_WEIGHT, valWeight);
            }
        }
    }

}

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
 * Copyright (C) 2011 Nicolas Weeger
 */

package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.quests.Quest;
import com.realtime.crossfire.jxclient.quests.QuestsManager;
import com.realtime.crossfire.jxclient.quests.QuestsManagerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a view of all quests a character is doing.
 * @author Nicolas Weeger
 */
public class QuestsView extends AbstractItemView {

    /**
     * The quests to display.
     */
    @NotNull
    private final QuestsManager questsManager;

    /**
     * The {@link FacesManager} for retrieving face information.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * Creates a new instance.
     * @param questsManager the quests to display
     * @param facesManager the faces manager for retrieving face information
     */
    public QuestsView(@NotNull final QuestsManager questsManager, @NotNull final FacesManager facesManager) {
        this.questsManager = questsManager;
        this.facesManager = facesManager;
        questsManager.addCrossfireQuestChangedListener(new QuestsManagerListener() {
            @Override
            public void questAdded(final int index) {
                addModifiedRange(index, questsManager.getQuests());
            }
        }) ;
        facesManager.addFacesManagerListener(new FacesManagerListener() {

            @Override
            public void faceUpdated(@NotNull final Face face) {
                if (questsManager.displaysFace(face.getFaceNum())) {
                    addModifiedRange(0, questsManager.getQuests());
                }
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return questsManager.getQuests();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public CfItem getItem(final int index) {
        final Quest quest = questsManager.getQuest(index);
        if (quest == null) {
            return null;
        }
        final Face face = facesManager.getFace(quest.getFace());
        return new CfItem(0, quest.getCode(), 0, 0, face, quest.getTitle(), quest.getTitle(), 0, 0, 0, 0);
    }

}

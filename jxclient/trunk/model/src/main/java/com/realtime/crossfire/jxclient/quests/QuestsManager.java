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
 * Copyright (C) 2011 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.quests;

import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all quests for a player.
 * @author nicolas
 */
public class QuestsManager {

    /**
     * All quests.
     */
    @NotNull
    private final List<Quest> quests = new CopyOnWriteArrayList<Quest>();

    /**
     * The {@link QuestsManagerListener QuestsManagerListeners} to notify about
     * changes.
     */
    @NotNull
    private final EventListenerList2<QuestsManagerListener> listeners = new EventListenerList2<QuestsManagerListener>(QuestsManagerListener.class);

    /**
     * A {@link Comparator} to compare {@link Quest} instances by title and code.
     */
    @NotNull
    private final Comparator<Quest> questComparator = new QuestComparator();

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        @Override
        public void start() {
            // ignore
        }

        @Override
        public void metaserver() {
            // ignore
        }

        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        @Override
        public void connecting(@NotNull final String serverInfo) {
            quests.clear();
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
     * @param guiStateManager the gui state manager to watch
     */
    public QuestsManager(@NotNull final GuiStateManager guiStateManager) {
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /**
     * Adds a {@link QuestsManagerListener} to notify about changes.
     * @param listener the listener to add
     */
    public void addCrossfireQuestChangedListener(@NotNull final QuestsManagerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link QuestsManagerListener} to notify about changes.
     * @param listener the listener to remove
     */
    public void removeCrossfireQuestChangedListener(@NotNull final QuestsManagerListener listener) {
        listeners.remove(listener);
    }

    /**
     * Adds a new quest.
     * @param code the quest's code
     * @param title the quest's title
     * @param face the quest's face
     * @param replay whether the quest can be replayed or not
     * @param parent quest internal code of this quest's parent, 0 if no parent.
     * @param end whether the quest is ended or not
     * @param description the quest's current step
     */
    public void addQuest(final int code, @NotNull final String title, final int face, final boolean replay, final int parent, final boolean end, @NotNull final String description) {
        final Quest key = new Quest(code, title, face, replay, parent, end, description);

        int index = Collections.binarySearch(quests, key, questComparator);
        if (index < 0) {
            index = -index-1;
            quests.add(index, key);
        } else {
            final Quest quest = quests.get(index);
            quest.setStep(end, description);
        }

        for (final QuestsManagerListener listener : listeners.getListeners()) {
            listener.questAdded(index);
        }
    }

    /**
     * Updates quest information.
     * @param code the quest's code
     * @param end whether the quest is ended or not
     * @param step the quest's current step
     */
    public void updateQuest(final int code, final boolean end, @NotNull final String step) {
        for (final Quest quest : quests) {
            if (quest.getCode() == code) {
                quest.setStep(end, step);
                break;
            }
        }
    }

    /**
     * Returns the number of current quests.
     * @return the number of quests
     */
    public int getQuests() {
        return quests.size();
    }

    /**
     * Returns a {@link Quest} instance by index.
     * @param index the index
     * @return the quest or <code>null</code> if the index is invalid
     */
    @Nullable
    public Quest getQuest(final int index) {
        return 0 <= index && index < quests.size() ? quests.get(index) : null;
    }

    /**
     * Returns whether any quest has the given face.
     * @param faceNum the face
     * @return whether the face was found
     */
    public boolean displaysFace(final int faceNum) {
        for (final Quest quest : quests) {
            if (quest.getFace() == faceNum) {
                return true;
            }
        }

        return false;
    }

    /**
     * A character name was sent to the server.
     */
    public void selectCharacter() {
        quests.clear();
    }

}

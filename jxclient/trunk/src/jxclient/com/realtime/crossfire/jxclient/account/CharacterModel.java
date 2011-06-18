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
 * Copyright (C) 2010 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.account;

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains the character list for an account. Only meaningful if the server
 * supports the new "loginmethod".
 * @author Nicolas Weeger
 */
public class CharacterModel {

    /**
     * The current entries.
     */
    @NotNull
    private final List<CharacterInformation> characters = new ArrayList<CharacterInformation>();

    /**
     * The pending entries. Only valid between {@link #begin()} and {@link
     * #commit()}.
     */
    @NotNull
    private final Collection<CharacterInformation> charactersPending = new ArrayList<CharacterInformation>();

    /**
     * Object used for synchronization.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * All registered character listeners.
     */
    @NotNull
    private final EventListenerList2<CharacterListener> characterListeners = new EventListenerList2<CharacterListener>(CharacterListener.class);

    /**
     * All registered character entry listeners. Maps entry index to list of
     * listeners.
     */
    @NotNull
    private final Map<Integer, EventListenerList2<CharacterInformationListener>> characterInformationListeners = new HashMap<Integer, EventListenerList2<CharacterInformationListener>>();

    /**
     * Returns a character entry by index.
     * @param index the index
     * @return the character entry, or <code>null</code> if the index is
     *         invalid
     */
    @Nullable
    public CharacterInformation getEntry(final int index) {
        try {
            synchronized (sync) {
                return characters.get(index);
            }
        } catch (final IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    /**
     * Returns the index of an entry by character name.
     * @param characterName the character name
     * @return the index, or <code>-1</code> if not found
     */
    public int getCharacterIndex(@NotNull final String characterName) {
        synchronized (sync) {
            int index = 0;
            for (final CharacterInformation characterInformation : characters) {
                if (characterInformation.getName().equals(characterName)) {
                    return index;
                }

                index++;
            }
        }

        return -1;
    }

    /**
     * Returns the number of character entries.
     * @return the number of character entries
     */
    public int size() {
        synchronized (sync) {
            return characters.size();
        }
    }

    /**
     * Adds an entry.
     * @param characterInformation the entry to add
     */
    public void add(@NotNull final CharacterInformation characterInformation) {
        synchronized (sync) {
            charactersPending.add(characterInformation);
        }
    }

    /**
     * Starts an update transaction.
     */
    public void begin() {
        charactersPending.clear();
    }

    /**
     * Finishes an update transaction.
     */
    public void commit() {
        final int oldMetaListSize;
        final int newMetaListSize;
        synchronized (sync) {
            oldMetaListSize = characters.size();
            characters.clear();
            characters.addAll(charactersPending);
            Collections.sort(characters);
            newMetaListSize = characters.size();
        }
        charactersPending.clear();

        for (final CharacterListener characterListener : characterListeners.getListeners()) {
            characterListener.numberOfItemsChanged();
        }

        for (int i = 0, iMax = Math.max(oldMetaListSize, newMetaListSize); i < iMax; i++) {
            for (final CharacterInformationListener characterInformationListener : getCharacterInformationListeners(i).getListeners()) {
                characterInformationListener.informationChanged();
            }
        }
    }

    /**
     * Adds a character listener.
     * @param listener the listener to add
     */
    public void addCharacterListener(@NotNull final CharacterListener listener) {
        characterListeners.add(listener);
    }

    /**
     * Removes a character listener.
     * @param listener the listener to remove
     */
    public void removeCharacterListener(@NotNull final CharacterListener listener) {
        characterListeners.remove(listener);
    }

    /**
     * Adds a character entry listener for one entry.
     * @param index the entry index to monitor
     * @param listener the listener to add
     */
    public void addCharacterInformationListener(final int index, @NotNull final CharacterInformationListener listener) {
        getCharacterInformationListeners(index).add(listener);
    }

    /**
     * Removes a character entry listener for one entry.
     * @param index the entry index to monitor
     * @param listener the listener to remove
     */
    public void removeCharacterInformationListener(final int index, @NotNull final CharacterInformationListener listener) {
        getCharacterInformationListeners(index).remove(listener);
    }

    /**
     * Returns the character entry listeners for one entry index.
     * @param index the entry index
     * @return the listeners list
     */
    @NotNull
    private EventListenerList2<CharacterInformationListener> getCharacterInformationListeners(final int index) {
        synchronized (characterInformationListeners) {
            final EventListenerList2<CharacterInformationListener> existingListeners = characterInformationListeners.get(index);
            if (existingListeners != null) {
                return existingListeners;
            }

            final EventListenerList2<CharacterInformationListener> newListeners = new EventListenerList2<CharacterInformationListener>(CharacterInformationListener.class);
            characterInformationListeners.put(index, newListeners);
            return newListeners;
        }
    }

    /**
     * Returns whether any character has the given face.
     * @param faceNum the face
     * @return whether the face was found
     */
    public boolean displaysFace(final int faceNum) {
        for (final CharacterInformation character : characters) {
            if (character.getFaceNumber() == faceNum) {
                return true;
            }
        }

        return false;
    }

}

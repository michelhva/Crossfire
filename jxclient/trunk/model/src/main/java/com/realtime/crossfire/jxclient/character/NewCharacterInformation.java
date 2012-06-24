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

package com.realtime.crossfire.jxclient.character;

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains information for creating new characters.
 * @author Andreas Kirschbaum
 */
public class NewCharacterInformation {

    /**
     * The {@link StartingMap} entries available for character creation.
     */
    @NotNull
    private final List<StartingMap> startingMaps = new ArrayList<StartingMap>();

    /**
     * The races available for character creation.
     */
    @NotNull
    private final List<String> raceList = new ArrayList<String>();

    /**
     * The classes available for character creation.
     */
    @NotNull
    private final List<String> classList = new ArrayList<String>();

    /**
     * The defined races for character creation.
     */
    @NotNull
    private final Map<String, ClassRaceInfo> raceInfo = new HashMap<String, ClassRaceInfo>();

    /**
     * The defined classes for character creation.
     */
    @NotNull
    private final Map<String, ClassRaceInfo> classInfo = new HashMap<String, ClassRaceInfo>();

    /**
     * The {@link NewCharInfo} for creating new characters.
     */
    @NotNull
    private NewCharInfo newCharInfo = new NewCharInfo(0, 0, 0, Collections.<String>emptyList(), false, false, false);

    /**
     * All registered character listeners.
     */
    @NotNull
    private final EventListenerList2<NewCharacterInformationListener> newCharacterInformationListeners = new EventListenerList2<NewCharacterInformationListener>(NewCharacterInformationListener.class);

    /**
     * Sets the {@link StartingMap} entries available for character creation.
     * @param startingMaps the starting map entries
     */
    public void setStartingMaps(@NotNull final Collection<StartingMap> startingMaps) {
        this.startingMaps.clear();
        this.startingMaps.addAll(startingMaps);
    }

    /**
     * Registers a {@link NewCharacterInformationListener} to be notified of
     * changes.
     * @param newCharacterInformationListener the listener to register
     */
    public void addNewCharacterInformationListener(@NotNull final NewCharacterInformationListener newCharacterInformationListener) {
        newCharacterInformationListeners.add(newCharacterInformationListener);
    }

    /**
     * Unregisters a {@link NewCharacterInformationListener} not to be notified
     * of changes.
     * @param newCharacterInformationListener the listener to unregister
     */
    public void removeNewCharacterInformationListener(@NotNull final NewCharacterInformationListener newCharacterInformationListener) {
        newCharacterInformationListeners.remove(newCharacterInformationListener);
    }

    /**
     * Returns all defined {@link StartingMap} entries available for character
     * creation.
     * @return the starting map entries
     */
    @NotNull
    public List<StartingMap> getStartingMaps() {
        return Collections.unmodifiableList(startingMaps);
    }

    /**
     * Sets the races available for character creation.
     * @param raceList the races
     */
    public void setRaceList(@NotNull final String[] raceList) {
        this.raceList.clear();
        this.raceList.addAll(Arrays.asList(raceList));
        for (final NewCharacterInformationListener newCharacterInformationListener : newCharacterInformationListeners.getListeners()) {
            newCharacterInformationListener.raceListChanged();
        }
    }

    /**
     * Returns all defined races available for character creation.
     * @return the races
     */
    @NotNull
    public List<String> getRaceList() {
        return Collections.unmodifiableList(raceList);
    }

    /**
     * Sets the classes available for character creation.
     * @param classList the classes
     */
    public void setClassList(@NotNull final String[] classList) {
        this.classList.clear();
        this.classList.addAll(Arrays.asList(classList));
        for (final NewCharacterInformationListener newCharacterInformationListener : newCharacterInformationListeners.getListeners()) {
            newCharacterInformationListener.classListChanged();
        }
    }

    /**
     * Returns all defined classes available for character creation.
     * @return the classes
     */
    @NotNull
    public List<String> getClassesList() {
        return Collections.unmodifiableList(classList);
    }

    /**
     * Sets or updates a {@link ClassRaceInfo}.
     * @param classRaceInfo the race info to set
     */
    public void addRaceInfo(@NotNull final ClassRaceInfo classRaceInfo) {
        raceInfo.put(classRaceInfo.getArchName(), classRaceInfo);
    }

    /**
     * Returns a {@link ClassRaceInfo} by race name.
     * @param race the race name
     * @return the race info or <code>null</code> if no race info is defined
     */
    @Nullable
    public ClassRaceInfo getRaceInfo(@NotNull final String race) {
        return raceInfo.get(race);
    }

    /**
     * Sets or updates a {@link ClassRaceInfo class info}.
     * @param classInfo the class info to set
     */
    public void addClassInfo(@NotNull final ClassRaceInfo classInfo) {
        this.classInfo.put(classInfo.getArchName(), classInfo);
    }

    /**
     * Returns a {@link ClassRaceInfo class info} by class name.
     * @param className the class name
     * @return the class race info or <code>null</code> if no such class info is
     *         defined
     */
    @Nullable
    public ClassRaceInfo getClassInfo(@NotNull final String className) {
        return classInfo.get(className);
    }

    /**
     * Returns the {@link NewCharInfo} instance for character creation.
     * @return the new char info instance
     */
    @NotNull
    public NewCharInfo getNewCharInfo() {
        return newCharInfo;
    }

    /**
     * Sets the {@link NewCharInfo} instance for character creation.
     * @param newCharInfo the new char info instance
     */
    public void setNewCharInfo(@NotNull final NewCharInfo newCharInfo) {
        this.newCharInfo = newCharInfo;
    }

}

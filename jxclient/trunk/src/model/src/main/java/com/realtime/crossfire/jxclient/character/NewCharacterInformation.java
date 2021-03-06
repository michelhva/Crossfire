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
     * The starting maps available for character creation.
     */
    @NotNull
    private final List<String> startingMapList = new ArrayList<>();

    /**
     * The races available for character creation.
     */
    @NotNull
    private final List<String> raceList = new ArrayList<>();

    /**
     * The classes available for character creation.
     */
    @NotNull
    private final List<String> classList = new ArrayList<>();

    /**
     * The defined starting maps for character creation.
     */
    @NotNull
    private final Map<String, StartingMap> startingMapInfo = new HashMap<>();

    /**
     * The defined races for character creation.
     */
    @NotNull
    private final Map<String, ClassRaceInfo> raceInfo = new HashMap<>();

    /**
     * The defined classes for character creation.
     */
    @NotNull
    private final Map<String, ClassRaceInfo> classInfo = new HashMap<>();

    /**
     * The {@link NewCharInfo} for creating new characters.
     */
    @NotNull
    private NewCharInfo newCharInfo = new NewCharInfo(0, 0, 0, Collections.emptyList(), false, false, false);

    /**
     * All registered character listeners.
     */
    @NotNull
    private final EventListenerList2<NewCharacterInformationListener> newCharacterInformationListeners = new EventListenerList2<>();

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
     * Sets the {@link StartingMap} entries available for character creation.
     * @param startingMaps the starting map entries
     */
    public void setStartingMapList(@NotNull final Iterable<StartingMap> startingMaps) {
        startingMapList.clear();
        for (StartingMap startingMap : startingMaps) {
            startingMapList.add(startingMap.getArchName());
        }
        for (NewCharacterInformationListener newCharacterInformationListener : newCharacterInformationListeners) {
            newCharacterInformationListener.startingMapListChanged();
        }
        for (StartingMap startingMap : startingMaps) {
            addStartingMapInfo(startingMap);
        }
    }

    /**
     * Returns all defined starting maps for character creation.
     * @return the starting map entries
     */
    @NotNull
    public List<String> getStartingMapList() {
        return Collections.unmodifiableList(startingMapList);
    }

    /**
     * Sets or updates a {@link StartingMap}.
     * @param startingMapInfo the starting map to set
     */
    private void addStartingMapInfo(@NotNull final StartingMap startingMapInfo) {
        this.startingMapInfo.put(startingMapInfo.getArchName(), startingMapInfo);
        for (NewCharacterInformationListener newCharacterInformationListener : newCharacterInformationListeners) {
            newCharacterInformationListener.startingMapInfoChanged(startingMapInfo.getArchName());
        }
    }

    /**
     * Returns a {@link StartingMap} by map name.
     * @param name the map name
     * @return the starting map info or {@code null} if no starting map info is
     * defined
     */
    @Nullable
    public StartingMap getStartingMapInfo(@NotNull final String name) {
        return startingMapInfo.get(name);
    }

    /**
     * Sets the races available for character creation.
     * @param raceList the races
     */
    public void setRaceList(@NotNull final String[] raceList) {
        this.raceList.clear();
        this.raceList.addAll(Arrays.asList(raceList));
        for (NewCharacterInformationListener newCharacterInformationListener : newCharacterInformationListeners) {
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
     * Sets or updates a {@link ClassRaceInfo}.
     * @param classRaceInfo the race info to set
     */
    public void addRaceInfo(@NotNull final ClassRaceInfo classRaceInfo) {
        raceInfo.put(classRaceInfo.getArchName(), classRaceInfo);
        for (NewCharacterInformationListener newCharacterInformationListener : newCharacterInformationListeners) {
            newCharacterInformationListener.raceInfoChanged(classRaceInfo.getArchName());
        }
    }

    /**
     * Returns a {@link ClassRaceInfo} by race name.
     * @param race the race name
     * @return the race info or {@code null} if no race info is defined
     */
    @Nullable
    public ClassRaceInfo getRaceInfo(@NotNull final String race) {
        return raceInfo.get(race);
    }

    /**
     * Sets the classes available for character creation.
     * @param classList the classes
     */
    public void setClassList(@NotNull final String[] classList) {
        this.classList.clear();
        this.classList.addAll(Arrays.asList(classList));
        for (NewCharacterInformationListener newCharacterInformationListener : newCharacterInformationListeners) {
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
     * Sets or updates a {@link ClassRaceInfo class info}.
     * @param classInfo the class info to set
     */
    public void addClassInfo(@NotNull final ClassRaceInfo classInfo) {
        this.classInfo.put(classInfo.getArchName(), classInfo);
        for (NewCharacterInformationListener newCharacterInformationListener : newCharacterInformationListeners) {
            newCharacterInformationListener.classInfoChanged(classInfo.getArchName());
        }
    }

    /**
     * Returns a {@link ClassRaceInfo class info} by class name.
     * @param className the class name
     * @return the class race info or {@code null} if no such class info is
     * defined
     */
    @Nullable
    public ClassRaceInfo getClassInfo(@NotNull final String className) {
        return classInfo.get(className);
    }

    /**
     * Sets the {@link NewCharInfo} instance for character creation.
     * @param newCharInfo the new char info instance
     */
    public void setNewCharInfo(@NotNull final NewCharInfo newCharInfo) {
        this.newCharInfo = newCharInfo;
    }

    /**
     * Returns the {@link NewCharInfo} instance for character creation.
     * @return the new char info instance
     */
    @NotNull
    public NewCharInfo getNewCharInfo() {
        return newCharInfo;
    }

}

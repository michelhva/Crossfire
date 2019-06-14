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

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.character.Choice;
import com.realtime.crossfire.jxclient.character.ClassRaceInfo;
import com.realtime.crossfire.jxclient.character.NewCharacterInformation;
import com.realtime.crossfire.jxclient.character.NewCharacterInformationListener;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * General information for creating new characters.
 * @author Andreas Kirschbaum
 */
public class NewCharModel {

    /**
     * The class or race name to use if no (or no valid) class or race exists.
     */
    @NotNull
    private static final String UNKNOWN = "unknown";

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_INVALID_STAT_STR = 1;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_INVALID_STAT_DEX = 2;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_INVALID_STAT_CON = 3;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_INVALID_STAT_INT = 4;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_INVALID_STAT_WIS = 5;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_INVALID_STAT_POW = 6;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_INVALID_STAT_CHA = 7;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_UNUSED_POINTS = 8;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_CHARACTER_NAME = 9;

    /**
     * A priority value for {@link #setErrorText(int, String)}.
     */
    public static final int PRIORITY_SERVER_FAILURE = Integer.MAX_VALUE;

    /**
     * The options received from the server.
     */
    @NotNull
    private final NewCharacterInformation newCharacterInformation;

    /**
     * The listeners to notify about changes.
     */
    @NotNull
    private final EventListenerList2<NewCharModelListener> listeners = new EventListenerList2<>();

    /**
     * The base attribute values. Maps stat to points.
     */
    @NotNull
    private final Map<NewcharStat, Integer> values = new EnumMap<>(NewcharStat.class);

    /**
     * The selected race.
     */
    @NotNull
    private String race = UNKNOWN;

    /**
     * The selected class.
     */
    @NotNull
    private String class_ = UNKNOWN;

    /**
     * The selected starting map.
     */
    @NotNull
    private String startingMap = UNKNOWN;

    /**
     * The shown choice. Set to {@code null} if no choice is currently
     * selected.
     */
    @Nullable
    private Choice option;

    /**
     * The selected index in {@link #option}.
     */
    private int optionIndex;

    /**
     * The error text(s) to show. Maps priority to error text. The first error
     * text is displayed.
     */
    @NotNull
    private final Map<Integer, String> errorTexts = new TreeMap<>();

    /**
     * Creates a new instance.
     * @param newCharacterInformation the new character information to use
     */
    public NewCharModel(@NotNull final NewCharacterInformation newCharacterInformation) {
        this.newCharacterInformation = newCharacterInformation;

        this.newCharacterInformation.addNewCharacterInformationListener(new NewCharacterInformationListener() {

            @Override
            public void classListChanged() {
                setClass(class_);
                notifyListeners();
            }

            @Override
            public void classInfoChanged(@NotNull final String className) {
                notifyListeners();
            }

            @Override
            public void raceListChanged() {
                setRace(race);
                notifyListeners();
            }

            @Override
            public void raceInfoChanged(@NotNull final String raceName) {
                notifyListeners();
            }

            @Override
            public void startingMapListChanged() {
                setStartingMap(startingMap);
                notifyListeners();
            }

            @Override
            public void startingMapInfoChanged(@NotNull final String startingMapName) {
                notifyListeners();
            }

        });
        setClass(UNKNOWN);
        setRace(UNKNOWN);
        setStartingMap(UNKNOWN);
    }

    /**
     * Adds a listener to notify of changes.
     * @param listener the listener
     */
    public void addListener(@NotNull final NewCharModelListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener to be notified of changes.
     * @param listener the listener
     */
    public void removeListener(@NotNull final NewCharModelListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns the {@link NewCharacterInformation} instance received from the
     * server.
     * @return the instance
     */
    @NotNull
    public NewCharacterInformation getNewCharacterInformation() {
        return newCharacterInformation;
    }

    /**
     * Returns the selected race.
     * @return the race
     */
    @NotNull
    public String getRace() {
        return race;
    }

    /**
     * Sets the selected race.
     * @param race the selected race
     */
    public void setRace(@NotNull final String race) {
        final List<String> list = newCharacterInformation.getRaceList();
        final String newRace;
        if (list.contains(race)) {
            newRace = race;
        } else if (!list.isEmpty()) {
            newRace = list.get(0);
        } else {
            newRace = UNKNOWN;
        }
        if (this.race.equals(newRace)) {
            return;
        }

        this.race = newRace;
        updateOption();
        notifyListeners();
    }

    /**
     * Returns the selected class.
     * @return the class
     */
    @NotNull
    public String getClass_() {
        return class_;
    }

    /**
     * Sets the selected class.
     * @param class_ the class
     */
    public void setClass(@NotNull final String class_) {
        final List<String> list = newCharacterInformation.getClassesList();
        final String newClass;
        if (list.contains(class_)) {
            newClass = class_;
        } else if (!list.isEmpty()) {
            newClass = list.get(0);
        } else {
            newClass = UNKNOWN;
        }
        if (this.class_.equals(newClass)) {
            return;
        }

        this.class_ = newClass;
        updateOption();
        notifyListeners();
    }

    /**
     * Returns the selected starting map.
     * @return the starting map
     */
    @NotNull
    public String getStartingMap() {
        return startingMap;
    }

    /**
     * Sets the selected starting map.
     * @param startingMap the starting map
     */
    public void setStartingMap(@NotNull final String startingMap) {
        final List<String> list = newCharacterInformation.getStartingMapList();
        final String newStartingMap;
        if (list.contains(startingMap)) {
            newStartingMap = startingMap;
        } else if (!list.isEmpty()) {
            newStartingMap = list.get(0);
        } else {
            newStartingMap = UNKNOWN;
        }
        if (this.startingMap.equals(newStartingMap)) {
            return;
        }

        this.startingMap = newStartingMap;
        updateOption();
        notifyListeners();
    }

    /**
     * Returns the total points (sum of base, race, and class) for a stat.
     * @param stat the stat
     * @return the points
     */
    public int getTotal(@NotNull final NewcharStat stat) {
        return getValue(stat)+getRaceStatAdjustment(stat)+getClassStatAdjustment(stat);
    }

    /**
     * Sets the base points for a stat.
     * @param stat the stat
     * @param value the points
     */
    public void setValue(@NotNull final NewcharStat stat, final int value) {
        final Integer prevValue = values.put(stat, value);
        //noinspection EqualsReplaceableByObjectsCall
        if (prevValue == null || !prevValue.equals(value)) {
            notifyListeners();
        }
    }

    /**
     * Returns the base points for a stat.
     * @param stat the stat
     * @return the points
     */
    public int getValue(@NotNull final NewcharStat stat) {
        final Integer tmp = values.get(stat);
        return tmp == null ? 0 : tmp;
    }

    /**
     * Returns the race points for a stat.
     * @param stat the stat
     * @return the points
     */
    public int getRaceStatAdjustment(@NotNull final NewcharStat stat) {
        final ClassRaceInfo info = newCharacterInformation.getRaceInfo(race);
        return info == null ? 0 : (int)info.getStatAdjustment(stat.getStat()); // XXX: long->int cast
    }

    /**
     * Returns the class points for a stat.
     * @param stat the stat
     * @return the points
     */
    public int getClassStatAdjustment(@NotNull final NewcharStat stat) {
        final ClassRaceInfo info = newCharacterInformation.getClassInfo(class_);
        return info == null ? 0 : (int)info.getStatAdjustment(stat.getStat()); // XXX: long->int cast
    }

    /**
     * Sets an error text.
     * @param priority the priority for deciding which error text to show
     * @param text the error text or {@code null} to remove the error
     */
    public void setErrorText(final int priority, @Nullable final String text) {
        final String prevText = errorTexts.get(priority);
        errorTexts.compute(priority, (p, s) -> text);
        if (!Objects.equals(text, prevText)) {
            notifyListeners();
        }
    }

    /**
     * Returns the error text to show.
     * @return the error text or an empty string to show none
     */
    @NotNull
    public String getErrorText() {
        final Iterator<String> it = errorTexts.values().iterator();
        return it.hasNext() ? it.next() : "";
    }

    /**
     * Returns the error text to show.
     * @return the error text or an empty string to show none
     */
    public boolean hasNonServerFailureErrorText() {
        final Iterator<Integer> it = errorTexts.keySet().iterator();
        return it.hasNext() && it.next() != PRIORITY_SERVER_FAILURE;
    }

    /**
     * Notifies all listeners about a change. Calls {@link
     * NewCharModelListener#changed()} for all elements in {@link #listeners}.
     */
    private void notifyListeners() {
        for (final NewCharModelListener listener : listeners) {
            listener.changed();
        }
    }

    /**
     * Returns the number of unused attribute points.
     * @return the number of unused attribute points
     */
    public int getUnusedPoints() {
        int result = newCharacterInformation.getNewCharInfo().getPoints();
        for (int value : values.values()) {
            result -= value;
        }
        return result;
    }

    /**
     * Returns the character options.
     * @return the character options or {@code null} if no options are available
     */
    @Nullable
    public Choice getOption() {
        return option;
    }

    /**
     * Return the selected character option.
     * @return the option or {@code -1} if no option is available
     */
    public int getOptionIndex() {
        return optionIndex;
    }

    /**
     * Sets the selected character option.
     * @param optionIndex the option; invalid values are handled
     */
    public void setOptionIndex(final int optionIndex) {
        final int newOptionIndex = optionIndex < 0 || option == null || optionIndex >= option.getChoices().size() ? 0 : optionIndex;
        if (this.optionIndex == newOptionIndex) {
            return;
        }
        this.optionIndex = newOptionIndex;
        notifyListeners();
    }

    /**
     * Updates {@link #option} if necessary.
     */
    private void updateOption() {
        @Nullable final Choice newOption;
        final ClassRaceInfo raceInfo = newCharacterInformation.getRaceInfo(race);
        if (raceInfo != null && !raceInfo.getChoices().isEmpty()) {
            newOption = raceInfo.getChoices().get(0);
        } else {
            final ClassRaceInfo classInfo = newCharacterInformation.getClassInfo(class_);
            if (classInfo != null && !classInfo.getChoices().isEmpty()) {
                newOption = classInfo.getChoices().get(0);
            } else {
                newOption = null;
            }
        }
        if (!Objects.equals(option, newOption)) {
            option = newOption;
            optionIndex = 0;
        }
    }

}

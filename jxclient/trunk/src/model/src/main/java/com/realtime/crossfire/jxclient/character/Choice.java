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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * A choice for character creation.
 * @author Andreas Kirschbaum
 */
public class Choice {

    /**
     * Identifies the choice.
     */
    @NotNull
    private final String choiceName;

    /**
     * The human readable choice name.
     */
    @NotNull
    private final String choiceDescription;

    /**
     * The choices.
     */
    @NotNull
    private final Map<String, String> choices;

    /**
     * Creates a new instance.
     * @param choiceName identifies the choice
     * @param choiceDescription the human readable choice name
     * @param choices the choices
     */
    public Choice(@NotNull final String choiceName, @NotNull final String choiceDescription, @NotNull final Map<String, String> choices) {
        this.choiceName = choiceName;
        this.choiceDescription = choiceDescription;
        this.choices = new LinkedHashMap<String, String>(choices);
    }

    /**
     * Returns the choice identification.
     * @return the choice identification
     */
    @NotNull
    public String getChoiceName() {
        return choiceName;
    }

    /**
     * Returns the human readable choice name.
     * @return the human readable choice name
     */
    @NotNull
    public String getChoiceDescription() {
        return choiceDescription;
    }

    /**
     * Returns the choices.
     * @return the choices
     */
    @NotNull
    public Map<String, String> getChoices() {
        return Collections.unmodifiableMap(choices);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "name="+choiceName+", description="+choiceDescription+", choices="+choices;
    }

}

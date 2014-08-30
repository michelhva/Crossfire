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

package com.realtime.crossfire.jxclient.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An entry in the settings file. It consists of a key/value pair and a comment
 * that explains the entry.
 * @author Andreas Kirschbaum
 */
public class SettingsEntry<T> {

    /**
     * The key in the settings file.
     */
    @NotNull
    private final String key;

    /**
     * The default value if the key is missing from the settings file.
     */
    @NotNull
    private final T defaultValue;

    /**
     * The comment to add to the settings file. Set to {@code null} if unknown.
     */
    @Nullable
    private final String comment;

    /**
     * Creates a new instance.
     * @param key the key in the settings file
     * @param defaultValue the default value if the key is missing from the
     * settings file
     * @param comment the comment to add to the settings file or {@code null} if
     * unknown
     */
    public SettingsEntry(@NotNull final String key, @NotNull final T defaultValue, @Nullable final String comment) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.comment = comment;
    }

    /**
     * Returns the key in the settings file.
     * @return the key
     */
    @NotNull
    public String getKey() {
        return key;
    }

    /**
     * Returns the default value if the key is missing from the settings file.
     * @return the default value
     */
    @NotNull
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the command to add to the settings file.
     * @return the comment or {@code null} if unknown
     */
    @Nullable
    public String getComment() {
        return comment;
    }

}

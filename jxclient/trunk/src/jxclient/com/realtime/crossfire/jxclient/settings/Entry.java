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
 * The value part of an entry of a settings file.
 * @author Andreas Kirschbaum
 */
public class Entry {

    /**
     * The value.
     */
    @NotNull
    private String value;

    /**
     * The documentation string or <code>null</code> if unknown.
     */
    @Nullable
    private String documentation;

    /**
     * Creates a new instance.
     * @param value the value
     * @param documentation the documentation string or <code>null</code> if
     * unknown
     */
    public Entry(@NotNull final String value, @Nullable final String documentation) {
        this.value = value;
        this.documentation = documentation;
    }

    /**
     * Returns the value.
     * @return the value
     */
    @NotNull
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * @param value the value
     */
    public void setValue(@NotNull final String value) {
        this.value = value;
    }

    /**
     * Returns the documentation string.
     * @return the documentation string or <code>null</code> if unknown
     */
    @Nullable
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the documentation string.
     * @param documentation the documentation string or <code>null</code> if
     * unknown
     */
    public void setDocumentation(@Nullable final String documentation) {
        if (this.documentation == null) {
            this.documentation = documentation;
        }
    }

}

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

import java.util.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners interested in {@link NewCharacterInformation} related
 * events.
 * @author Andreas Kirschbaum
 */
public interface NewCharacterInformationListener extends EventListener {

    /**
     * Called whenever the class list may have changed.
     */
    void classListChanged();

    /**
     * Called whenever information of a class may have changed.
     * @param className the name of the class that may have changed
     */
    void classInfoChanged(@NotNull String className);

    /**
     * Called whenever the race list may have changed.
     */
    void raceListChanged();

    /**
     * Called whenever information of a race may have changed.
     * @param raceName the name of the race that may have changed
     */
    void raceInfoChanged(@NotNull String raceName);

    /**
     * Called whenever the starting map list may have changed.
     */
    void startingMapListChanged();

    /**
     * Called whenever information of a starting map may have changed.
     * @param startingMapName the name of the starting map that may have
     * changed
     */
    void startingMapInfoChanged(@NotNull String startingMapName);

}

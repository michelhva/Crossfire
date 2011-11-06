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

package com.realtime.crossfire.jxclient.server.crossfire;

import java.util.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners interested in quest information related messages
 * received from the Crossfire server.
 * @author Nicolas Weeger
 */
public interface CrossfireQuestListener extends EventListener {

    /**
     * An "addquest" command was received.
     * @param code the quest internal code.
     * @param title the quest title.
     * @param face the quest face.
     * @param replay whether the quest can be replayed or not.
     * @param parent quest internal code of this quest's parent, 0 if no parent.
     * @param end whether the quest is finished or not.
     * @param step the current quest step.
     */
    void addQuest(int code, @NotNull String title, int face, boolean replay, int parent, boolean end, @NotNull String step);

    /**
     * An "updquest" command was received.
     * @param code the quest internal code.
     * @param end whether the quest is finished or not.
     * @param step the current quest step.
     */
    void updateQuest(int code, boolean end, @NotNull String step);
}

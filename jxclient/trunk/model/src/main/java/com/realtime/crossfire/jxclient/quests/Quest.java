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

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import org.jetbrains.annotations.NotNull;

/**
 * Describes an ingame quest.
 * @author Nicolas Weeger
 */
public class Quest {

    /**
     * The {@link QuestListener QuestListeners} to be notified of changes.
     */
    @NotNull
    private final EventListenerList2<QuestListener> listeners = new EventListenerList2<QuestListener>(QuestListener.class);

    /**
     * Quest internal code.
     */
    private final int code;

    /**
     * Quest title.
     */
    @NotNull
    private final String title;

    /**
     * Quest face.
     */
    private final int face;

    /**
     * If true, the quest can be replayed.
     */
    private final boolean replay;

    /**
     * Quest internal code of this quest's parent, 0 if no parent.
     */
    private final int parent;

    /**
     * If true, the quest is completed.
     */
    private boolean end;

    /**
     * Description of the current step.
     */
    @NotNull
    private String step;

    /**
     * Create a new quest.
     * @param code the quest internal code.
     * @param title the quest's title.
     * @param face the quest's face.
     * @param replay if 1 the quest can be replayed.
     * @param parent quest internal code of this quest's parent, 0 if no
     * parent.
     * @param end if 1 the quest is completed.
     * @param step current quest's step.
     */
    public Quest(final int code, @NotNull final String title, final int face, final boolean replay, final int parent, final boolean end, @NotNull final String step) {
        this.code = code;
        this.title = title;
        this.face = face;
        this.replay = replay;
        this.parent = parent;
        this.end = end;
        this.step = step;
    }

    /**
     * Get the quest's code.
     * @return code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Get the quest's title.
     * @return title.
     */
    @NotNull
    public String getTitle() {
        return title;
    }

    /**
     * Get the quest's face.
     * @return face index.
     */
    public int getFace() {
        return face;
    }

    /**
     * Update the quest's state.
     * @param end if true the quest is completed.
     * @param step step description.
     */
    public void setStep(final boolean end, @NotNull final String step) {
        boolean changed = false;

        if (this.end != end) {
            this.end = end;
            changed = true;
        }

        if (this.step.compareTo(step) != 0) {
            this.step = step;
            changed = true;
        }

        if (changed) {
            fireChanged();
        }
    }

    /**
     * Returns a description for this spell to be used in tooltips.
     * @return the tooltip text
     */
    @NotNull
    public String getTooltipText() {
        final StringBuilder sb = new StringBuilder("<b>");
        sb.append(title);
        sb.append("</b>");
        if (end) {
            sb.append(" (finished");
            if (replay) {
                sb.append(", can be replayed");
            }
            sb.append(")");
        }
        sb.append("<br>");
        sb.append(step);
        return sb.toString();
    }

    /**
     * Notifies all listeners.
     */
    private void fireChanged() {
        for (final QuestListener listener : listeners.getListeners()) {
            listener.questChanged();
        }
    }

    /**
     * Adds a {@link QuestListener} to be notified of changes.
     * @param listener the listener to add
     */
    public void addQuestListener(@NotNull final QuestListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link QuestListener} to be notified of changes.
     * @param listener the listener to remove
     */
    public void removeQuestListener(@NotNull final QuestListener listener) {
        listeners.remove(listener);
    }

}

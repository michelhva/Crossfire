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

package com.realtime.crossfire.jxclient.knowledge;

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import org.jetbrains.annotations.NotNull;

/**
 * Describes a Crossfire spell.
 * @author Lauwenmark
 */
public class KnowledgeItem {

    /**
     * The {@link KnowledgeListener KnowledgeListeners} to be notified of
     * changes.
     */
    @NotNull
    private final EventListenerList2<KnowledgeListener> listeners = new EventListenerList2<KnowledgeListener>(KnowledgeListener.class);

    /**
     * The knowledge identifier.
     */
    private final int knowledgeIndex;

    /**
     * The knowledge's type.
     */
    @NotNull
    private final String type;

    /**
     * The description.
     */
    @NotNull
    private String title = "";

    /**
     * The face number.
     */
    private final int faceNum;

    /**
     * Creates a new instance.
     * @param knowledgeIndex the knowledge identifier
     * @param type the knowledge's type
     * @param title the description
     * @param faceNum the face number
     */
    public KnowledgeItem(final int knowledgeIndex, @NotNull final String type, @NotNull final String title, final int faceNum) {
        this.knowledgeIndex = knowledgeIndex;
        this.type = type;
        this.title = title;
        this.faceNum = faceNum;
    }

    /**
     * Returns the tag ID.
     * @return the tag ID
     */
    public int getKnowledgeIndex() {
        return knowledgeIndex;
    }

    /**
     * Returns the description.
     * @return the description
     */
    @NotNull
    public String getKnowledgeTitle() {
        return title;
    }

    /**
     * Returns the face number.
     * @return the face number
     */
    public int getFaceNum() {
        return faceNum;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "Tag:"+knowledgeIndex+" Type:"+type+" Title:"+title+" Face:"+faceNum;
    }

    /**
     * Returns a description for this spell to be used in tooltips.
     * @return the tooltip text
     */
    @NotNull
    public String getTooltipText() {
        return title;
    }

    /**
     * Notifies all listeners.
     */
    private void fireChanged() {
        for (final KnowledgeListener listener : listeners.getListeners()) {
            listener.knowledgeChanged();
        }
    }

    /**
     * Adds a {@link KnowledgeListener} to be notified of changes.
     * @param listener the listener to add
     */
    public void addKnowledgeListener(@NotNull final KnowledgeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link KnowledgeListener} to be notified of changes.
     * @param listener the listener to remove
     */
    public void removeKnowledgeListener(@NotNull final KnowledgeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns the knowledge's type.
     * @return the type
     */
    public String getType() {
        return type;
    }

}

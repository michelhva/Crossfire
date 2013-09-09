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

package com.realtime.crossfire.jxclient.faces;

import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains a mapping of face numbers to face data. Face data can be queried
 * for {@link #getOriginalImageIcon(int, boolean[]) original size}, {@link
 * #getScaledImageIcon(int, boolean[]) scaled data for map views}, or {@link
 * #getMagicMapImageIcon(int, boolean[]) reduced size for minimap views}. {@link
 * #addFacesManagerListener(FacesManagerListener) Listeners can be attached} to
 * be notified when face data has been loaded.
 * @author Andreas Kirschbaum
 */
public interface FacesManager {

    /**
     * Returns the "original" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @param isUnknownImage returns whether the returned face is the "unknown"
     * face; ignored if <code>null</code>
     * @return the face, or the "unknown" face if the face is not loaded
     */
    @NotNull
    ImageIcon getOriginalImageIcon(int faceNum, @Nullable boolean[] isUnknownImage);

    /**
     * Returns the "scaled" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @param isUnknownImage returns whether the returned face is the "unknown"
     * face; ignored if <code>null</code>
     * @return the face, or the "unknown" face if the face is not loaded
     */
    @NotNull
    ImageIcon getScaledImageIcon(int faceNum, @Nullable boolean[] isUnknownImage);

    /**
     * Returns the "magic map" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @param isUnknownImage returns whether the returned face is the "unknown"
     * face; ignored if <code>null</code>
     * @return the face, or the "unknown" face if the face is not loaded
     */
    @NotNull
    ImageIcon getMagicMapImageIcon(int faceNum, @Nullable boolean[] isUnknownImage);

    /**
     * Returns the {@link Face} instance for a given face ID. Requests the face
     * face from the server if necessary.
     * @param faceNum the face ID to request
     * @return the face
     */
    @NotNull
    Face getFace(int faceNum);

    /**
     * Returns the {@link Face} instance for a given face ID. Requests the face
     * from the server if necessary.
     * @param faceNum the face ID to request
     * @return the face or <code>null</code> for the empty face
     */
    @Nullable
    Face getFace2(int faceNum);

    /**
     * Adds a {@link FacesManagerListener} to be notified about updated faces.
     * @param facesManagerListener the listener
     */
    void addFacesManagerListener(@NotNull FacesManagerListener facesManagerListener);

    /**
     * Removes a {@link FacesManagerListener} to be notified about updated
     * faces.
     * @param facesManagerListener the listener
     */
    void removeFacesManagerListener(@NotNull FacesManagerListener facesManagerListener);

    /**
     * Forgets about pending faces.
     */
    void reset();

}

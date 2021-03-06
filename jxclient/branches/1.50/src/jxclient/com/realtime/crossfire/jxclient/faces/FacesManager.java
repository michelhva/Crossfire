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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.faces;

import com.realtime.crossfire.jxclient.util.ResourceUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Retrieves {@link Face} information by face ID. If a face is not available
 * in-memory, an "unknown" (question mark) face is returned immediately.
 * Asynchonously, the face is loaded from the file cache. If loading fails, the
 * face is requested from the server (and later stored into the file cache). As
 * soon as the face becomes available, all registered {@link
 * FacesManagerListener}s are notified.
 * @author Andreas Kirschbaum
 */
public class FacesManager {

    /**
     * The {@link FaceCache} instance used to look up in-memory faces.
     */
    @NotNull
    private final FaceCache faceCache;

    /**
     * The {@link FaceQueue} instance used to load faces not present in-memory.
     */
    @NotNull
    private final FaceQueue faceQueue;

    /**
     * The unknown face.
     */
    @NotNull
    private final FaceImages unknownFaceImages;

    /**
     * The {@link FacesManagerListener}s to notify about changed faces.
     */
    @NotNull
    private final Collection<FacesManagerListener> facesManagerListeners = new CopyOnWriteArrayList<FacesManagerListener>();

    /**
     * The empty face; returned for face ID 0.
     */
    @NotNull
    private final FaceImages emptyFaceImages;

    /**
     * The {@link FaceQueueListener} registered to {@link #faceQueue}.
     */
    @NotNull
    private final FaceQueueListener faceQueueListener = new FaceQueueListener() {
        /** {@inheritDoc} */
        @Override
        public void faceLoaded(@NotNull final Face face, @NotNull final FaceImages faceImages) {
            face.setFaceImages(faceImages);
            fireFaceUpdated(face);
        }

        /** {@inheritDoc} */
        @Override
        public void faceFailed(@NotNull final Face face) {
            face.setFaceImages(unknownFaceImages);
            fireFaceUpdated(face);
        }
    };

    /**
     * Creates a new instance.
     * @param faceCache the face cache instance for storing in-memory faces
     * @param faceQueue the face queue to use
     * @throws IOException if the unknown image resource cannot be loaded
     */
    public FacesManager(@NotNull final FaceCache faceCache, @NotNull final FaceQueue faceQueue) throws IOException {
        this.faceCache = faceCache;
        this.faceQueue = faceQueue;
        faceQueue.addFaceQueueListener(faceQueueListener);

        emptyFaceImages = FaceImagesUtils.newEmptyFaceImages();
        unknownFaceImages = FaceImagesUtils.newFaceImages(ResourceUtils.loadImage(ResourceUtils.UNKNOWN_PNG));
    }

    /**
     * Returns the "orignal" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face, or the "unknown" face if the face is not loaded
     */
    @NotNull
    public ImageIcon getOriginalImageIcon(final int faceNum) {
        return getFaceImages(faceNum).getOriginalImageIcon();
    }

    /**
     * Returns the "scaled" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face, or the "unknown" face if the face is not loaded
     */
    @NotNull
    public ImageIcon getScaledImageIcon(final int faceNum) {
        return getFaceImages(faceNum).getScaledImageIcon();
    }

    /**
     * Returns the "magic map" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face, or the "unknown" face if the face is not loaded
     */
    @NotNull
    public ImageIcon getMagicMapImageIcon(final int faceNum) {
        return getFaceImages(faceNum).getMagicMapImageIcon();
    }

    /**
     * Returns the {@link Face} instance for a given face ID. Requests the face
     * face from the server if necessary.
     * @param faceNum the face ID to request
     * @return the face
     */
    @NotNull
    public Face getFace(final int faceNum) {
        getFaceImages(faceNum);
        return faceCache.getFace(faceNum);
    }

    /**
     * Returns the {@link Face} instance for a given face ID. Requests the face
     * from the server if necessary.
     * @param faceNum the face ID to request
     * @return the face or <code>null</code> for the empty face
     */
    @Nullable
    public Face getFace2(final int faceNum) {
        if (faceNum == 0) {
            return null;
        }

        getFaceImages(faceNum);
        return faceCache.getFace(faceNum);
    }

    /**
     * Returns the {@link FaceImages} information for a face ID. This function
     * returns immediately even if the face is not loaded. A not loaded face
     * will be updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face images information
     */
    @NotNull
    private FaceImages getFaceImages(final int faceNum) {
        if (faceNum == 0) {
            return emptyFaceImages;
        }

        final Face face = faceCache.getFace(faceNum);
        final FaceImages faceImages = face.getFaceImages();
        if (faceImages != null) {
            return faceImages;
        }

        faceQueue.loadFace(face);
        return unknownFaceImages;
    }

    /**
     * Notifies all {@link FacesManagerListener}s that a face has been updated.
     * @param face the face
     */
    private void fireFaceUpdated(@NotNull final Face face) {
        for (final FacesManagerListener facesManagerListener : facesManagerListeners) {
            facesManagerListener.faceUpdated(face);
        }
    }

    /**
     * Adds a {@link FacesManagerListener} to be notified about updated faces.
     * @param facesManagerListener the listener
     */
    public void addFacesManagerListener(@NotNull final FacesManagerListener facesManagerListener) {
        facesManagerListeners.add(facesManagerListener);
    }

    /**
     * Removes a {@link FacesManagerListener} to be notified about updated
     * faces.
     * @param facesManagerListener the listener
     */
    public void removeFacesManagerListener(@NotNull final FacesManagerListener facesManagerListener) {
        facesManagerListeners.remove(facesManagerListener);
    }

    /**
     * Forgets about pending faces.
     */
    public void reset() {
        faceCache.reset();
        faceQueue.reset();
    }

}

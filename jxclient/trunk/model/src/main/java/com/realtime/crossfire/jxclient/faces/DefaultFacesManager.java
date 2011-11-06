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

import com.realtime.crossfire.jxclient.util.ResourceUtils;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Retrieves {@link Face} information by face ID. If a face is not available
 * in-memory, an "unknown" (question mark) face is returned immediately.
 * Asynchronously, the face is loaded from the file cache. If loading fails, the
 * face is requested from the server (and later stored into the file cache). As
 * soon as the face becomes available, all registered {@link
 * FacesManagerListener FacesManagerListeners} are notified.
 * @author Andreas Kirschbaum
 */
public class DefaultFacesManager extends AbstractFacesManager {

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
     * The empty face; returned for face ID 0.
     */
    @NotNull
    private final FaceImages emptyFaceImages;

    /**
     * The {@link FaceQueueListener} registered to {@link #faceQueue}.
     */
    @NotNull
    private final FaceQueueListener faceQueueListener = new FaceQueueListener() {

        @Override
        public void faceLoaded(@NotNull final Face face, @NotNull final FaceImages faceImages) {
            face.setFaceImages(faceImages);
            fireFaceUpdated(face);
        }

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
    public DefaultFacesManager(@NotNull final FaceCache faceCache, @NotNull final FaceQueue faceQueue) throws IOException {
        super(faceCache);
        this.faceQueue = faceQueue;
        faceQueue.addFaceQueueListener(faceQueueListener);

        emptyFaceImages = FaceImagesUtils.newEmptyFaceImages();
        unknownFaceImages = FaceImagesUtils.newFaceImages(ResourceUtils.loadImage(ResourceUtils.UNKNOWN_PNG));
    }

    /**
     * Returns the {@link FaceImages} information for a face ID. This function
     * returns immediately even if the face is not loaded. A not loaded face
     * will be updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face images information
     */
    @NotNull
    @Override
    protected FaceImages getFaceImages(final int faceNum, @Nullable final boolean[] isUnknownImage) {
        if (faceNum == 0) {
            if (isUnknownImage != null) {
                isUnknownImage[0] = false;
            }
            return emptyFaceImages;
        }

        final Face face = lookupFace(faceNum);
        final FaceImages faceImages = face.getFaceImages();
        if (faceImages != null) {
            if (isUnknownImage != null) {
                isUnknownImage[0] = false;
            }
            return faceImages;
        }

        faceQueue.loadFace(face);
        if (isUnknownImage != null) {
            isUnknownImage[0] = true;
        }
        return unknownFaceImages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        super.reset();
        faceQueue.reset();
    }

}

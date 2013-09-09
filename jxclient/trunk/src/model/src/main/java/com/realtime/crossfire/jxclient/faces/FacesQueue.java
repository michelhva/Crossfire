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

import org.jetbrains.annotations.NotNull;

/**
 * The main {@link FaceQueue} for loading faces. It first delegates to a {@link
 * FileCacheFaceQueue} to load the face from the disk cache. If this fails, the
 * face is requested through a {@link AskfaceFaceQueue}.
 * @author Andreas Kirschbaum
 */
public class FacesQueue extends AbstractFaceQueue {

    /**
     * The {@link FileCacheFaceQueue} instance used to load faces from the file
     * cache.
     */
    @NotNull
    private final FileCacheFaceQueue fileCacheFaceQueue;

    /**
     * The {@link FaceQueue} instance used to query faces from the Crossfire
     * server.
     */
    @NotNull
    private final FaceQueue faceQueue;

    /**
     * The {@link FaceQueueListener} attached to {@link #fileCacheFaceQueue}.
     */
    @NotNull
    private final FaceQueueListener fileCacheFaceQueueListener = new FaceQueueListener() {

        @Override
        public void faceLoaded(@NotNull final Face face, @NotNull final FaceImages faceImages) {
            fireFaceLoaded(face, faceImages);
        }

        @Override
        public void faceFailed(@NotNull final Face face) {
            faceQueue.loadFace(face);
        }

    };

    /**
     * The {@link FaceQueueListener} attached to {@link #faceQueue}.
     */
    @NotNull
    private final FaceQueueListener askfaceFaceQueueListener = new FaceQueueListener() {

        @Override
        public void faceLoaded(@NotNull final Face face, @NotNull final FaceImages faceImages) {
            fireFaceLoaded(face, faceImages);
            fileCacheFaceQueue.saveFace(face, faceImages);
        }

        @Override
        public void faceFailed(@NotNull final Face face) {
            fireFaceFailed(face);
        }

    };

    /**
     * Creates a new instance.
     * @param faceQueue the face queue for sending askface commands
     * @param imageCacheOriginal the image cache used for loading original image
     * files
     * @param imageCacheScaled the image cache used for loading scaled image
     * files
     * @param imageCacheMagicMap the image cache used for loading magic map
     * image files
     */
    public FacesQueue(@NotNull final FaceQueue faceQueue, @NotNull final ImageCache imageCacheOriginal, @NotNull final ImageCache imageCacheScaled, @NotNull final ImageCache imageCacheMagicMap) {
        fileCacheFaceQueue = new FileCacheFaceQueue(imageCacheOriginal, imageCacheScaled, imageCacheMagicMap);
        this.faceQueue = faceQueue;
        fileCacheFaceQueue.addFaceQueueListener(fileCacheFaceQueueListener);
        faceQueue.addFaceQueueListener(askfaceFaceQueueListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        fileCacheFaceQueue.reset();
        faceQueue.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFace(@NotNull final Face face) {
        fileCacheFaceQueue.loadFace(face);
    }

}

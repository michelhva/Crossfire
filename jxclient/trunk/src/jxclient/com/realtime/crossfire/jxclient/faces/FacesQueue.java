//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.faces;

import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main {@link FaceQueue} for loading faces. It first delegates to a
 * {@link FileCacheFaceQueue} to load the face from the disk cache. If this
 * fails, the face is requested through a {@link AskfaceFaceQueue}.
 * @author Andreas Kirschbaum
 */
public class FacesQueue extends AbstractFaceQueue
{
    /**
     * The {@link FileCacheFaceQueue} instance used to load faces from the file
     * cache.
     */
    @NotNull
    private final FileCacheFaceQueue fileCacheFaceQueue;

    /**
     * The {@link AskfaceFaceQueue} instance used to query faces from the
     * Crossfire server.
     */
    @NotNull
    private final AskfaceFaceQueue askfaceFaceQueue;

    /**
     * The {@link FaceQueueListener} attached to {@link #fileCacheFaceQueue}.
     */
    @NotNull
    private final FaceQueueListener fileCacheFaceQueueListener = new FaceQueueListener()
    {
        /** {@inheritDoc} */
        @Override
        public void faceLoaded(@NotNull final Face face, @NotNull final FaceImages faceImages)
        {
            fireFaceLoaded(face, faceImages);
        }

        /** {@inheritDoc} */
        @Override
        public void faceFailed(@NotNull final Face face)
        {
            askfaceFaceQueue.loadFace(face);
        }
    };

    /**
     * The {@link FaceQueueListener} attached to {@link #askfaceFaceQueue}.
     */
    @NotNull
    private final FaceQueueListener askfaceFaceQueueListener = new FaceQueueListener()
    {
        /** {@inheritDoc} */
        @Override
        public void faceLoaded(@NotNull final Face face, @NotNull final FaceImages faceImages)
        {
            fireFaceLoaded(face, faceImages);
            fileCacheFaceQueue.saveFace(face, faceImages);
        }

        /** {@inheritDoc} */
        @Override
        public void faceFailed(@NotNull final Face face)
        {
            fireFaceFailed(face);
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the server connection for sending
     * askface commands
     * @param imageCacheOriginal the image cache used for loading original
     * image files
     * @param imageCacheScaled the image cache used for loading scaled image
     * files
     * @param imageCacheMagicMap the image cache used for loading magic map
     * image files
     */
    public FacesQueue(@Nullable final CrossfireServerConnection crossfireServerConnection, @NotNull final ImageCache imageCacheOriginal, @NotNull final ImageCache imageCacheScaled, @NotNull final ImageCache imageCacheMagicMap)
    {
        fileCacheFaceQueue = new FileCacheFaceQueue(imageCacheOriginal, imageCacheScaled, imageCacheMagicMap);
        askfaceFaceQueue = new AskfaceFaceQueue(crossfireServerConnection);
        fileCacheFaceQueue.addFaceQueueListener(fileCacheFaceQueueListener);
        askfaceFaceQueue.addFaceQueueListener(askfaceFaceQueueListener);
    }

    /** {@inheritDoc} */
    @Override
    public void reset()
    {
        fileCacheFaceQueue.reset();
        askfaceFaceQueue.reset();
    }

    /** {@inheritDoc} */
    @Override
    public void loadFace(@NotNull final Face face)
    {
        fileCacheFaceQueue.loadFace(face);
    }

    /**
     * Returns the {@link AskfaceFaceQueue} instance.
     * @return the askface queue
     */
    @NotNull
    public AskfaceFaceQueue getAskfaceQueue()
    {
        return askfaceFaceQueue;
    }
}

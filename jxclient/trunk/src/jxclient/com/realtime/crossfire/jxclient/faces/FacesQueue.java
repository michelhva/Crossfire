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

/**
 * The main {@link FaceQueue} for loading faces. It first delegates to a
 * {@link FileCacheFaceQueue} to load the face from the disk cache. If this
 * fails, the face is requested through a {@link AskfaceFaceQueue}.
 * @author Andreas Kirschbaum
 */
public class FacesQueue extends DefaultFaceQueue
{
    /**
     * The {@link FileCacheFaceQueue} instance used to load faces from the file
     * cache.
     */
    private final FileCacheFaceQueue fileCacheFaceQueue;

    /**
     * The {@link AskfaceFaceQueue} instance used to query faces from the
     * Crossfire server.
     */
    private final AskfaceFaceQueue askfaceFaceQueue;

    /**
     * The {@link FaceQueueListener} attached to {@link #fileCacheFaceQueue}.
     */
    private final FaceQueueListener fileCacheFaceQueueListener = new FaceQueueListener()
    {
        /** {@inheritDoc} */
        public void faceLoaded(final Face face, final FaceImages faceImages)
        {
            fireFaceLoaded(face, faceImages);
        }

        /** {@inheritDoc} */
        public void faceFailed(final Face face)
        {
            askfaceFaceQueue.loadFace(face);
        }
    };

    /**
     * The {@link FaceQueueListener} attached to {@link #askfaceFaceQueue}.
     */
    private final FaceQueueListener askfaceFaceQueueListener = new FaceQueueListener()
    {
        /** {@inheritDoc} */
        public void faceLoaded(final Face face, final FaceImages faceImages)
        {
            fireFaceLoaded(face, faceImages);
            fileCacheFaceQueue.saveFace(face, faceImages);
        }

        /** {@inheritDoc} */
        public void faceFailed(final Face face)
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
    public FacesQueue(final CrossfireServerConnection crossfireServerConnection, final ImageCache imageCacheOriginal, final ImageCache imageCacheScaled, final ImageCache imageCacheMagicMap)
    {
        fileCacheFaceQueue = new FileCacheFaceQueue(imageCacheOriginal, imageCacheScaled, imageCacheMagicMap);
        askfaceFaceQueue = new AskfaceFaceQueue(crossfireServerConnection);
        fileCacheFaceQueue.addFaceQueueListener(fileCacheFaceQueueListener);
        askfaceFaceQueue.addFaceQueueListener(askfaceFaceQueueListener);
    }

    /** {@inheritDoc} */
    public void reset()
    {
        fileCacheFaceQueue.reset();
        askfaceFaceQueue.reset();
    }

    /** {@inheritDoc} */
    public void loadFace(final Face face)
    {
        fileCacheFaceQueue.loadFace(face);
    }

    /**
     * Returns the {@link AskfaceFaceQueue} instance.
     * @return the askface queue
     */
    public AskfaceFaceQueue getAskfaceQueue()
    {
        return askfaceFaceQueue;
    }
}

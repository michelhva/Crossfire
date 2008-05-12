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

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract base class for classes implementing {@link FaceQueue}. This class
 * maintains the {@link FaceQueueListener}s; implementing classes need to
 * implement only the actual face loading code.
 * @author Andreas Kirschbaum
 */
public abstract class DefaultFaceQueue implements FaceQueue
{
    /**
     * The registered {@link FaceQueueListener}s.
     */
    private final List<FaceQueueListener> faceQueueListeners = new LinkedList<FaceQueueListener>();

    /** {@inheritDoc} */
    public void addFaceQueueListener(final FaceQueueListener faceQueueListener)
    {
        faceQueueListeners.add(faceQueueListener);
    }

    /** {@inheritDoc} */
    public void removeFaceQueueListener(final FaceQueueListener faceQueueListener)
    {
        faceQueueListeners.remove(faceQueueListener);
    }

    /**
     * Notify all listener with {@link
     * FaceQueueListener#faceLoaded(Face, FaceImages)}.
     * @param face the face that has been loaded
     * @param faceImages the face images instance that has been loaded
     */
    protected void fireFaceLoaded(final Face face, final FaceImages faceImages)
    {
        for (final FaceQueueListener faceQueueListener : faceQueueListeners)
        {
            faceQueueListener.faceLoaded(face, faceImages);
        }
    }

    /**
     * Notify all listener with {@link FaceQueueListener#faceFailed(Face)}.
     * @param face the face that has failed to load
     */
    protected void fireFaceFailed(final Face face)
    {
        for (final FaceQueueListener faceQueueListener : faceQueueListeners)
        {
            faceQueueListener.faceFailed(face);
        }
    }
}

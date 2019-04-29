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

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for classes implementing {@link FaceQueue}. This class
 * maintains the {@link FaceQueueListener FaceQueueListeners}; implementing
 * classes need to implement only the actual face loading code.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractFaceQueue implements FaceQueue {

    /**
     * The registered {@link FaceQueueListener FaceQueueListeners}.
     */
    @NotNull
    private final EventListenerList2<FaceQueueListener> faceQueueListeners = new EventListenerList2<>();

    @Override
    public void addFaceQueueListener(@NotNull final FaceQueueListener faceQueueListener) {
        faceQueueListeners.add(faceQueueListener);
    }

    @Override
    public void removeFaceQueueListener(@NotNull final FaceQueueListener faceQueueListener) {
        faceQueueListeners.remove(faceQueueListener);
    }

    /**
     * Notify all listener with {@link FaceQueueListener#faceLoaded(Face,
     * FaceImages)}.
     * @param face the face that has been loaded
     * @param faceImages the face images instance that has been loaded
     */
    protected void fireFaceLoaded(@NotNull final Face face, @NotNull final FaceImages faceImages) {
        for (final FaceQueueListener faceQueueListener : faceQueueListeners) {
            faceQueueListener.faceLoaded(face, faceImages);
        }
    }

    /**
     * Notify all listener with {@link FaceQueueListener#faceFailed(Face)}.
     * @param face the face that has failed to load
     */
    protected void fireFaceFailed(@NotNull final Face face) {
        for (final FaceQueueListener faceQueueListener : faceQueueListeners) {
            faceQueueListener.faceFailed(face);
        }
    }

}

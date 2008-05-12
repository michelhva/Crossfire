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

/**
 * Interface for classes implementing a means to load {@link Face}s.
 * @author Andreas Kirschbaum
 */
public interface FaceQueue
{
    /**
     * Reset the processing: forget about pending faces. This function is
     * called whenever the server socket breaks, or when a new connection has
     * been established.
     */
    void reset();

    /**
     * Request a face. Must eventually call either {@link
     * FaceQueueListener#faceLoaded(Face, FaceImages)} or {@link
     * FaceQueueListener#faceFailed(Face)} for the face. Faces re-requested
     * while still processing may be notified only once.
     * @param face the requested face
     */
    void loadFace(Face face);

    /**
     * Adds a {@link FaceQueueListener} to be notified about processed faces.
     * @param faceQueueListener the listener to add
     */
    void addFaceQueueListener(FaceQueueListener faceQueueListener);

    /**
     * Removes a {@link FaceQueueListener} to be notified about processed
     * faces.
     * @param faceQueueListener the listener to remove
     */
    void removeFaceQueueListener(FaceQueueListener faceQueueListener);
}

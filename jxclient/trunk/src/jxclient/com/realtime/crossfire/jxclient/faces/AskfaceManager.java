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

import com.realtime.crossfire.jxclient.server.CrossfireUpdateFaceListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements a queue to process "askface" commands. Faces to query are added
 * with {@link #queryFace(int)}. When the image is available the client code
 * will be notified though the callback function {@link
 * FacesCallback#sendAskface(int)}.
 *
 * <p>The askface manager does not send more than {@link
 * #CONCURRENT_ASKFACE_COMMANDS} concurrent "askface" commands to the server.
 * This is to keep the client responsive even if many faces have to be fetched.
 *
 * @author Andreas Kirschbaum
 */
public class AskfaceManager
{
    /**
     * The maximum number of concurrently sent "askface" commands.
     */
    public static final int CONCURRENT_ASKFACE_COMMANDS = 8;

    /**
     * The {@link FacesCallback} instance to be notified about available face
     * images.
     */
    private final FacesCallback facesCallback;

    /**
     * Face numbers for which "askface" commands have been sent without having
     * received a response from the server.
     */
    private final Set<Integer> pendingAskfaces = new HashSet<Integer>();

    /**
     * Face numbers for which an "askface" command should be sent. It includes
     * all elements of {@link #pendingAskfaces}.
     */
    private final Set<Integer> pendingFaces = new HashSet<Integer>();

    /**
     * The {@link CrossfireUpdateFaceListener} registered to detect updated
     * faces.
     */
    private final CrossfireUpdateFaceListener crossfireUpdateFaceListener = new CrossfireUpdateFaceListener()
    {
        /** {@inheritDoc} */
        public void updateFace(final int faceID)
        {
            faceReceived(faceID);
        }
    };

    /**
     * Create a new instance.
     *
     * @param facesCallback The faces callback to be notified.
     */
    public AskfaceManager(final FacesCallback facesCallback)
    {
        if (facesCallback == null) throw new IllegalArgumentException();

        this.facesCallback = facesCallback;
        facesCallback.addCrossfireUpdateFaceListener(crossfireUpdateFaceListener);
    }

    /**
     * Reset the askface queue.
     */
    public void reset()
    {
        pendingAskfaces.clear();
    }

    /**
     * Ask the server to send image info.
     *
     * @param face the face to query
     */
    public void queryFace(final int face)
    {
        assert face > 0;

        if (!pendingFaces.add(face))
        {
            return;
        }

        sendAskface();
    }

    /**
     * Send some pending "askface" commands.
     */
    private void sendAskface()
    {
        for (final int face : pendingFaces)
        {
            if (pendingAskfaces.size() >= CONCURRENT_ASKFACE_COMMANDS)
            {
                break;
            }

            if (pendingAskfaces.add(face))
            {
                facesCallback.sendAskface(face);
            }
        }
    }

    /**
     * Notify the askface manager that image information have been received
     * from the server.
     *
     * @param face The modified face.
     */
    private void faceReceived(final int face)
    {
        if (!pendingAskfaces.remove(face))
        {
            System.err.println("received unexpected image for "+face);
        }
        else if (!pendingFaces.remove(face))
        {
            assert false;
        }
        sendAskface();
    }
}

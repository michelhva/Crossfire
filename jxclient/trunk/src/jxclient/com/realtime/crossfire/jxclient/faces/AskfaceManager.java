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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Implements a queue to retrieve missing images from a Crossfire server. Faces
 * to query are added to the queue with {@link #queryFace(int)}. When the image
 * is received from the server, client code will be notified though the
 * callback function {@link FacesCallback#sendAskface(int)}.
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
     * The same elements as {@link #pendingFaces} in query order.
     */
    private final List<Integer> pendingFacesQueue = new LinkedList<Integer>();

    /**
     * The {@link CrossfireUpdateFaceListener} registered to detect updated
     * faces.
     */
    private final CrossfireUpdateFaceListener crossfireUpdateFaceListener = new CrossfireUpdateFaceListener()
    {
        /** {@inheritDoc} */
        public void updateFace(final int faceNum)
        {
            faceReceived(faceNum);
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
        pendingFaces.clear();
        pendingFacesQueue.clear();
    }

    /**
     * Ask the server to send image info.
     *
     * @param faceNum the face to query
     */
    public void queryFace(final int faceNum)
    {
        assert faceNum > 0;

        final Integer faceObject = faceNum;
        if (!pendingFaces.add(faceObject))
        {
            // move image to front of queue
            pendingFacesQueue.remove(faceObject);
            pendingFacesQueue.add(0, faceObject);
            return;
        }
        pendingFacesQueue.add(0, faceObject);

        sendAskface();
    }

    /**
     * Send some pending "askface" commands.
     */
    private void sendAskface()
    {
        for (final int faceNum : pendingFacesQueue)
        {
            if (pendingAskfaces.size() >= CONCURRENT_ASKFACE_COMMANDS)
            {
                break;
            }

            if (pendingAskfaces.add(faceNum))
            {
                facesCallback.sendAskface(faceNum);
            }
        }
    }

    /**
     * Notify the askface manager that image information have been received
     * from the server.
     *
     * @param faceNum The modified face.
     */
    private void faceReceived(final int faceNum)
    {
        final Integer faceObject = faceNum;
        if (!pendingAskfaces.remove(faceObject))
        {
            System.err.println("received unexpected image for "+faceNum);
        }
        else
        {
            if (!pendingFaces.remove(faceObject))
            {
                assert false;
            }
            if (!pendingFacesQueue.remove(faceObject))
            {
                assert false;
            }
        }
        sendAskface();
    }
}

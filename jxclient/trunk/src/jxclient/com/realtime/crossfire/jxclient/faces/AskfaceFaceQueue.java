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
import com.realtime.crossfire.jxclient.server.CrossfireUpdateFaceListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * A {@link FaceQueue} requesting faces by "askface" commands sent to the
 * Crossfire server.
 * @author Andreas Kirschbaum
 */
public class AskfaceFaceQueue extends DefaultFaceQueue
{
    /**
     * The maximum number of concurrently sent "askface" commands. If more are
     * requested, the excess ones are put on hold until some face information
     * is received.
     */
    public static final int CONCURRENT_ASKFACE_COMMANDS = 8;

    /**
     * The object use for synchronization.
     */
    private final Object sync = new Object();

    /**
     * The connection to use.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * Face numbers for which "askface" commands have been sent without having
     * received a response from the server. Maps face ID to {@link Face}
     * instance.
     */
    private final Map<Integer, Face> pendingAskfaces = new HashMap<Integer, Face>();

    /**
     * Face numbers for which an "askface" command should be sent. It includes
     * all elements of {@link #pendingAskfaces}.
     */
    private final Map<Integer, Face> pendingFaces = new HashMap<Integer, Face>();

    /**
     * The same elements as {@link #pendingFaces} in query order.
     */
    private final List<Face> pendingFacesQueue = new LinkedList<Face>();

    /**
     * The {@link CrossfireUpdateFaceListener} registered to {@link
     * #crossfireServerConnection} receive face commands.
     */
    private final CrossfireUpdateFaceListener crossfireUpdateFaceListener = new CrossfireUpdateFaceListener()
    {
        /** {@inheritDoc} */
        public void updateFace(final int faceNum, final int faceSetNum, final byte[] packet, final int pos, final int len)
        {
            faceReceived(faceNum, faceSetNum, packet, pos, len);
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection instance for sending
     * askface commands
     */
    public AskfaceFaceQueue(final CrossfireServerConnection crossfireServerConnection)
    {
        this.crossfireServerConnection = crossfireServerConnection;
        if (crossfireServerConnection != null)
        {
            crossfireServerConnection.addCrossfireUpdateFaceListener(crossfireUpdateFaceListener);
        }
    }

    /** {@inheritDoc} */
    public void reset()
    {
        synchronized (sync)
        {
            pendingAskfaces.clear();
            pendingFaces.clear();
            pendingFacesQueue.clear();
        }
    }

    /** {@inheritDoc} */
    public void loadFace(final Face face)
    {
        final int faceNum = face.getFaceNum();
        if (faceNum <= 0 || faceNum > 65535)
        {
            fireFaceFailed(face);
            return;
        }

        final Integer faceObject = faceNum;
        synchronized (sync)
        {
            if (pendingFaces.put(faceObject, face) != null)
            {
                // move image to front of queue
                pendingFacesQueue.remove(face);
                pendingFacesQueue.add(0, face);
                return;
            }
            pendingFacesQueue.add(0, face);

            sendAskface();
        }
    }
    /**
     * Sends some pending "askface" commands.
     */
    private void sendAskface()
    {
        for (final Face face : pendingFacesQueue)
        {
            if (pendingAskfaces.size() >= CONCURRENT_ASKFACE_COMMANDS)
            {
                break;
            }

            final int faceNum = face.getFaceNum();
            if (pendingAskfaces.put(faceNum, face) == null && crossfireServerConnection != null)
            {
                crossfireServerConnection.sendAskface(faceNum);
            }
        }
    }
    /**
     * Notifies the askface manager that image information have been received
     * from the server.
     * @param faceNum the face ID
     * @param faceSetNum the face set
     * @param packet the face data
     * @param pos the starting position into <code>packet</code>
     * @param len the length in bytes
     */
    private void faceReceived(final int faceNum, final int faceSetNum, final byte[] packet, final int pos, final int len)
    {
        final Integer faceObject = faceNum;
        synchronized (sync)
        {
            final Face face = pendingAskfaces.remove(faceObject);
            if (face == null)
            {
                System.err.println("received unexpected image for face "+faceNum);
            }
            else
            {
                if (pendingFaces.remove(faceObject) != face)
                {
                    assert false;
                }
                if (!pendingFacesQueue.remove(face))
                {
                    assert false;
                }

                processFaceData(face, Arrays.copyOfRange(packet, pos, pos+len));
            }
            sendAskface();
        }
    }

    /**
     * Processes face information received from the server.
     * @param face the face
     * @param data the face information; it is supposed to be a .png file
     */
    public void processFaceData(final Face face, final byte[] data)
    {
        final ImageIcon originalImageIcon;
        try
        {
            originalImageIcon = new ImageIcon(data);
        }
        catch (final IllegalArgumentException ex)
        {
            System.err.println("Invalid .png data for face "+face+": "+ex.getMessage());
            return;
        }

        if (originalImageIcon.getIconWidth() <= 0 || originalImageIcon.getIconHeight() <= 0)
        {
            fireFaceFailed(face);
            return;
        }

        fireFaceLoaded(face, FaceImagesUtils.newFaceImages(originalImageIcon));
    }
}

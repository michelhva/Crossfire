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

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link FaceQueue} requesting faces by "askface" commands sent to the
 * Crossfire server.
 * @author Andreas Kirschbaum
 */
public class AskfaceFaceQueue extends AbstractFaceQueue {

    /**
     * The maximum number of concurrently sent "askface" commands. If more are
     * requested, the excess ones are put on hold until some face information is
     * received.
     */
    private static final int CONCURRENT_ASKFACE_COMMANDS = 8;

    /**
     * The object use for synchronization.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The connection to use.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * Face numbers for which "askface" commands have been sent without having
     * received a response from the server. Maps face ID to {@link Face}
     * instance.
     */
    @NotNull
    private final Map<Integer, Face> pendingAskfaces = new HashMap<Integer, Face>();

    /**
     * Face numbers for which an "askface" command should be sent. It includes
     * all elements of {@link #pendingAskfaces}.
     */
    @NotNull
    private final Map<Integer, Face> pendingFaces = new HashMap<Integer, Face>();

    /**
     * The same elements as {@link #pendingFaces} in query order.
     */
    @NotNull
    private final List<Face> pendingFacesQueue = new LinkedList<Face>();

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection instance for sending
     * askface commands
     */
    public AskfaceFaceQueue(@NotNull final CrossfireServerConnection crossfireServerConnection) {
        this.crossfireServerConnection = crossfireServerConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        synchronized (sync) {
            pendingAskfaces.clear();
            pendingFaces.clear();
            pendingFacesQueue.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFace(@NotNull final Face face) {
        final int faceNum = face.getFaceNum();
        if (faceNum <= 0 || faceNum > 65535) {
            fireFaceFailed(face);
            return;
        }

        final Integer faceObject = faceNum;
        synchronized (sync) {
            if (pendingFaces.put(faceObject, face) != null) {
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
    private void sendAskface() {
        for (final Face face : pendingFacesQueue) {
            if (pendingAskfaces.size() >= CONCURRENT_ASKFACE_COMMANDS) {
                break;
            }

            final int faceNum = face.getFaceNum();
            if (pendingAskfaces.put(faceNum, face) == null) {
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
     */
    public void faceReceived(final int faceNum, final int faceSetNum, @NotNull final ByteBuffer packet) {
        final Integer faceObject = faceNum;
        synchronized (sync) {
            final Face face = pendingAskfaces.remove(faceObject);
            if (face == null) {
                System.err.println("received unexpected image for face "+faceNum);
            } else {
                if (pendingFaces.remove(faceObject) != face) {
                    assert false;
                }
                if (!pendingFacesQueue.remove(face)) {
                    assert false;
                }

                final byte[] data = new byte[packet.remaining()];
                packet.get(data);
                processFaceData(face, data);
            }
            sendAskface();
        }
    }

    /**
     * Processes face information received from the server.
     * @param face the face
     * @param data the face information; it is supposed to be a .png file
     */
    private void processFaceData(@NotNull final Face face, @NotNull final byte[] data) {
        final ImageIcon originalImageIcon;
        try {
            originalImageIcon = new ImageIcon(data);
        } catch (final IllegalArgumentException ex) {
            System.err.println("Invalid .png data for face "+face+": "+ex.getMessage());
            return;
        }

        if (originalImageIcon.getIconWidth() <= 0 || originalImageIcon.getIconHeight() <= 0) {
            fireFaceFailed(face);
            return;
        }

        fireFaceLoaded(face, FaceImagesUtils.newFaceImages(originalImageIcon));
    }

}

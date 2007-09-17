/* $Id$ */

package com.realtime.crossfire.jxclient.faces;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements a queue to process "askface" commands. Faces to query are added
 * with {@link #queryFace(int)}. When the image is available the client code
 * will be notified though the callback function {@link
 * FacesCallback#sendAskFace(int)}.
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
    private static final Set<Integer> pendingAskfaces = new HashSet<Integer>();

    /**
     * Face numbers for which an "askface" command should be sent. It includes
     * all elements of {@link #pendingAskfaces}.
     */
    private static final Set<Integer> pendingFaces = new HashSet<Integer>();

    /**
     * Create a new instance.
     *
     * @param facesCallback The faces callback to be notified.
     */
    public AskfaceManager(final FacesCallback facesCallback)
    {
        if (facesCallback == null) throw new IllegalArgumentException();

        this.facesCallback = facesCallback;
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

            if (!pendingAskfaces.contains(face))
            {
                try
                {
                    facesCallback.sendAskface(face);
                }
                catch (final IOException ex)
                {
                    pendingFaces.clear();
                    break;
                }
                pendingAskfaces.add(face);
            }
        }
    }

    /**
     * Notify the askface manager that image information have been received
     * from the server.
     *
     * @param face The modified face.
     */
    public void faceReceived(final int face)
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

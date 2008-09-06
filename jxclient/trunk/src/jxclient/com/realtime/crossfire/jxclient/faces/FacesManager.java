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
import com.realtime.crossfire.jxclient.util.ResourceUtils;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.Image;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Retrieves {@link Face} information by face ID. If a face is not available
 * in-memory, an "unknown" (question mark) face is returned immediately.
 * Asynchonously, the face is loaded from the file cache. If loading fails, the
 * face is requested from the server (and later stored into the file cache). As
 * soon as the face becomes available, all registered {@link
 * FacesManagerListener}s are notified.
 * @author Andreas Kirschbaum
 */
public class FacesManager
{
    /**
     * The resource name of the "unknown" face.
     */
    private static final String UNKNOWN_PNG = "resource/unknown.png";

    /**
     * The resource for "Click here for next group of items" buttons.
     */
    private static final String NEXT_GROUP_FACE = "resource/next_group.png";

    /**
     * The resource for "Click here for previous group of items" buttons.
     */
    private static final String PREV_GROUP_FACE = "resource/prev_group.png";

    /**
     * The {@link FaceCache} instance used to look up in-memory faces.
     */
    private final FaceCache faceCache;

    /**
     * The {@link FacesQueue} instance used to load faces not present
     * in-memory.
     */
    private final FacesQueue facesQueue;

    /**
     * The unknown face.
     */
    private final FaceImages unknownFaceImages;

    /**
     * The {@link FacesManagerListener}s to notify about changed faces.
     */
    private final List<FacesManagerListener> facesManagerListeners = new LinkedList<FacesManagerListener>();

    /**
     * The empty face; returned for face ID 0.
     */
    private final FaceImages emptyFaceImages;

    /**
     * The face to substitute into "Click here for next group of items".
     */
    private final Image nextGroupFace;

    /**
     * The face to substitute into "Click here for previous group of items".
     */
    private final Image prevGroupFace;

    /**
     * The {@link FaceQueueListener} registered to {@link #facesQueue}.
     */
    private final FaceQueueListener faceQueueListener = new FaceQueueListener()
    {
        /** {@inheritDoc} */
        public void faceLoaded(final Face face, final FaceImages faceImages)
        {
            face.setFaceImages(faceImages);
            fireFaceUpdated(face);
        }

        /** {@inheritDoc} */
        public void faceFailed(final Face face)
        {
            face.setFaceImages(unknownFaceImages);
            fireFaceUpdated(face);
        }
    };

    /**
     * Creates a new instance.
     * @param faceCache the face cache instance for storing in-memory faces
     * @param crossfireServerConnection the server connection for sending
     * askface commands
     * @param imageCacheOriginal the image cache used for loading original
     * image files
     * @param imageCacheScaled the image cache used for loading scaled image
     * files
     * @param imageCacheMagicMap the image cache used for loading magic map
     * image files
     * @throws IOException if the unknown image resource cannot be loaded
     */
    public FacesManager(final CrossfireServerConnection crossfireServerConnection, final ImageCache imageCacheOriginal, final ImageCache imageCacheScaled, final ImageCache imageCacheMagicMap, final FaceCache faceCache) throws IOException
    {
        this.faceCache = faceCache;
        facesQueue = new FacesQueue(crossfireServerConnection, imageCacheOriginal, imageCacheScaled, imageCacheMagicMap);
        facesQueue.addFaceQueueListener(faceQueueListener);

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();

        final ImageIcon emptyOriginalImageIcon = new ImageIcon(gconf.createCompatibleImage(Face.SQUARE_SIZE, Face.SQUARE_SIZE, Transparency.OPAQUE));
        final ImageIcon emptyScaledImageIcon = new ImageIcon(gconf.createCompatibleImage(Face.SQUARE_SIZE*2, Face.SQUARE_SIZE*2, Transparency.OPAQUE));
        final ImageIcon emptyMagicMapImageIcon = new ImageIcon(gconf.createCompatibleImage(Face.SQUARE_SIZE/8, Face.SQUARE_SIZE/8, Transparency.OPAQUE));
        emptyFaceImages = new FaceImages(emptyOriginalImageIcon, emptyScaledImageIcon, emptyMagicMapImageIcon);

        unknownFaceImages = FaceImagesUtils.newFaceImages(ResourceUtils.loadImage(UNKNOWN_PNG));
        nextGroupFace = ResourceUtils.loadImage(NEXT_GROUP_FACE).getImage();
        prevGroupFace = ResourceUtils.loadImage(PREV_GROUP_FACE).getImage();
    }

    /**
     * Returns the "orignal" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face, or the "unknown" face if the face is not loaded
     */
    public ImageIcon getOriginalImageIcon(final int faceNum)
    {
        return getFaceImages(faceNum).getOriginalImageIcon();
    }

    /**
     * Returns the "scaled" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face, or the "unknown" face if the face is not loaded
     */
    public ImageIcon getScaledImageIcon(final int faceNum)
    {
        return getFaceImages(faceNum).getScaledImageIcon();
    }

    /**
     * Returns the "magic map" face for a face ID. This function returns
     * immediately even if the face is not loaded. A not loaded face will be
     * updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face, or the "unknown" face if the face is not loaded
     */
    public ImageIcon getMagicMapImageIcon(final int faceNum)
    {
        return getFaceImages(faceNum).getMagicMapImageIcon();
    }

    /**
     * Returns the {@link FaceImages} information for a face ID. This function
     * returns immediately even if the face is not loaded. A not loaded face
     * will be updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face images information
     */
    public FaceImages getFaceImages(final int faceNum)
    {
        if (faceNum == 0)
        {
            return emptyFaceImages;
        }

        final Face face = faceCache.getFace(faceNum);
        final FaceImages faceImages = face.getFaceImages();
        if (faceImages != null)
        {
            return faceImages;
        }

        facesQueue.loadFace(face);
        return unknownFaceImages;
    }

    /**
     * Notifies all {@link FacesManagerListener}s that a face has been updated.
     * @param face the face
     */
    private void fireFaceUpdated(final Face face)
    {
        for (final FacesManagerListener facesManagerListener : facesManagerListeners)
        {
            facesManagerListener.faceUpdated(face);
        }
    }

    /**
     * Adds a {@link FacesManagerListener} to be notified about updated faces.
     * @param facesManagerListener the listener
     */
    public void addFacesManagerListener(final FacesManagerListener facesManagerListener)
    {
        facesManagerListeners.add(facesManagerListener);
    }

    /**
     * Removes a {@link FacesManagerListener} to be notified about updated
     * faces.
     * @param facesManagerListener the listener
     */
    public void removeFacesManagerListener(final FacesManagerListener facesManagerListener)
    {
        facesManagerListeners.remove(facesManagerListener);
    }

    /**
     * Forgets about pending faces.
     */
    public void reset()
    {
        faceCache.reset();
        facesQueue.reset();
    }

    /**
     * Returns the {@link FacesQueue} instance.
     * @return the faces queue instance
     */
    public FacesQueue getFacesQueue()
    {
        return facesQueue;
    }

    /**
     * Returns the face to substitute into "Click here for next group of
     * items".
     * @return the image
     */
    public Image getNextGroupFace()
    {
        return nextGroupFace;
    }

    /**
     * Returns the face to substitute into "Click here for previous group of
     * items".
     * @return the image
     */
    public Image getPrevGroupFace()
    {
        return prevGroupFace;
    }
}

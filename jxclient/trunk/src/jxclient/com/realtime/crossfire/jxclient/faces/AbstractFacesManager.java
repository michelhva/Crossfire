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

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for {@link FacesManager} implementations.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractFacesManager implements FacesManager {

    /**
     * The {@link FacesManagerListener}s to notify about changed faces.
     */
    @NotNull
    private final Collection<FacesManagerListener> facesManagerListeners = new CopyOnWriteArrayList<FacesManagerListener>();

    /**
     * The {@link FaceCache} instance used to look up in-memory faces.
     */
    @NotNull
    private final FaceCache faceCache;

    /**
     * Creates a new instance.
     * @param faceCache the face cache instance for storing in-memory faces
     */
    protected AbstractFacesManager(@NotNull final FaceCache faceCache) {
        this.faceCache = faceCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFacesManagerListener(@NotNull final FacesManagerListener facesManagerListener) {
        facesManagerListeners.add(facesManagerListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFacesManagerListener(@NotNull final FacesManagerListener facesManagerListener) {
        facesManagerListeners.remove(facesManagerListener);
    }

    /**
     * Notifies all {@link FacesManagerListener}s that a face has been updated.
     * @param face the face
     */
    protected void fireFaceUpdated(@NotNull final Face face) {
        for (final FacesManagerListener facesManagerListener : facesManagerListeners) {
            facesManagerListener.faceUpdated(face);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ImageIcon getOriginalImageIcon(final int faceNum) {
        return getFaceImages(faceNum).getOriginalImageIcon();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ImageIcon getScaledImageIcon(final int faceNum) {
        return getFaceImages(faceNum).getScaledImageIcon();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ImageIcon getMagicMapImageIcon(final int faceNum) {
        return getFaceImages(faceNum).getMagicMapImageIcon();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Face getFace(final int faceNum) {
        getFaceImages(faceNum);
        return faceCache.getFace(faceNum);
    }

    /**
     * Returns the {@link FaceImages} information for a face ID. This function
     * returns immediately even if the face is not loaded. A not loaded face
     * will be updated as soon as loading has finished.
     * @param faceNum the face ID
     * @return the face images information
     */
    @NotNull
    protected abstract FaceImages getFaceImages(final int faceNum);

    /**
     * Returns the {@link Face} instance for a given face ID. Other than {@link
     * #getFace(int)}, does not request the face face from the server if
     * unknown.
     * @param faceNum the face ID to look up
     * @return the face
     */
    @NotNull
    protected Face lookupFace(final int faceNum) {
        return faceCache.getFace(faceNum);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Face getFace2(final int faceNum) {
        if (faceNum == 0) {
            return null;
        }

        getFaceImages(faceNum);
        return faceCache.getFace(faceNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        faceCache.reset();
    }

}

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

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link FaceQueue} loading faces from {@link ImageCache} instances.
 * @author Andreas Kirschbaum
 */
public class FileCacheFaceQueue extends AbstractFaceQueue
{
    /**
     * The object used for synchronization.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The image cache used for loading orignal images.
     */
    @NotNull
    private final ImageCache imageCacheOriginal;

    /**
     * The image cache used for loading scaled images.
     */
    @NotNull
    private final ImageCache imageCacheScaled;

    /**
     * The image cache used for loading magic map images.
     */
    @NotNull
    private final ImageCache imageCacheMagicMap;

    /**
     * The {@link ExecutorService} used to execute face loading. The pool
     * consists of one thread; this means all requests are serialized.
     */
    @NotNull
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * The faces for which {@link #loadFace(Face)} has been called but that are
     * not yet processed.
     */
    @NotNull
    private final Collection<Face> pendingLoadFaces = new HashSet<Face>();

    /**
     * Counts the number of calls to {@link #reset()}. An {@link
     * ExecutorService} does not allow to cancel scheduled but still pending
     * requests. Therefore the thread checks if this ID matches the ID value at
     * creation time. If the IDs do not match it exits.
     */
    private int id = 0;

    /**
     * Creates a new instance.
     * @param imageCacheOriginal the image cache used for loading original
     * image files
     * @param imageCacheScaled the image cache used for loading scaled image
     * files
     * @param imageCacheMagicMap the image cache used for loading magic map
     * image files
     */
    public FileCacheFaceQueue(@NotNull final ImageCache imageCacheOriginal, @NotNull final ImageCache imageCacheScaled, @NotNull final ImageCache imageCacheMagicMap)
    {
        this.imageCacheOriginal = imageCacheOriginal;
        this.imageCacheScaled = imageCacheScaled;
        this.imageCacheMagicMap = imageCacheMagicMap;
    }

    /** {@inheritDoc} */
    @Override
    public void reset()
    {
        synchronized (sync)
        {
            id++;
            pendingLoadFaces.clear();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void loadFace(@NotNull final Face face)
    {
        final boolean doAdd;
        synchronized (sync)
        {
            doAdd = pendingLoadFaces.add(face);
        }
        if (doAdd)
        {
            executorService.submit(new LoadTask(face));
        }
    }

    /**
     * Saves a face to the cacches. This function returns immediately; the
     * faces are written asynchronously.
     * @param face the face to write
     * @param faceImages the image information to write
     */
    public void saveFace(@NotNull final Face face, @NotNull final FaceImages faceImages)
    {
        executorService.submit(new SaveTask(face, faceImages));
    }

    /**
     * A thread which loads one face from the caches.
     * @author Andreas Kirschbaum
     */
    private class LoadTask implements Runnable
    {
        private final int taskId = id;

        /**
         * The face to load.
         */
        @NotNull
        private final Face face;

        /**
         * Creates a new instance.
         * @param face the face to load
         */
        public LoadTask(@NotNull final Face face)
        {
            this.face = face;
        }

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            try
            {
                if (taskId != id)
                {
                    return;
                }

                final ImageIcon originalImageIcon = imageCacheOriginal.load(face);
                if (originalImageIcon == null)
                {
                    fireFaceFailed(face);
                    return;
                }

                final ImageIcon scaledImageIcon = imageCacheScaled.load(face);
                if (scaledImageIcon == null)
                {
                    fireFaceFailed(face);
                    return;
                }

                final ImageIcon magicMapImageIcon = imageCacheMagicMap.load(face);
                if (magicMapImageIcon == null)
                {
                    fireFaceFailed(face);
                    return;
                }

                fireFaceLoaded(face, new FaceImages(originalImageIcon, scaledImageIcon, magicMapImageIcon));
            }
            finally
            {
                synchronized (sync)
                {
                    pendingLoadFaces.remove(face);
                }
            }
        }
    }

    /**
     * A thread which saves one face to the caches.
     * @author Andreas Kirschbaum
     */
    private class SaveTask implements Runnable
    {
        /**
         * The face to save.
         */
        @NotNull
        private final Face face;

        /**
         * The images to save.
         */
        @NotNull
        private final FaceImages faceImages;

        /**
         * Creates a new instance.
         * @param face the face to save
         * @param faceImages the images to save
         */
        public SaveTask(@NotNull final Face face, @NotNull final FaceImages faceImages)
        {
            this.face = face;
            this.faceImages = faceImages;
        }

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            imageCacheOriginal.save(face, faceImages.getOriginalImageIcon());
            imageCacheScaled.save(face, faceImages.getScaledImageIcon());
            imageCacheMagicMap.save(face, faceImages.getMagicMapImageIcon());
        }
    }
}

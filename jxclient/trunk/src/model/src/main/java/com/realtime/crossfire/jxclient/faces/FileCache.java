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

import com.realtime.crossfire.jxclient.util.FilenameUtils;
import com.realtime.crossfire.jxclient.util.Images;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A disk based cache for image files.
 * @author Andreas Kirschbaum
 */
public class FileCache implements ImageCache {

    /**
     * The directory where the images are saved.
     */
    @NotNull
    private final File cacheDir;

    /**
     * Creates a new instance.
     * @param cacheDir the directory where the images are saved
     */
    public FileCache(@NotNull final File cacheDir) {
        this.cacheDir = cacheDir;
        if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) {
            System.err.println(cacheDir+": cannot create directory");
        }
    }

    @Nullable
    @Override
    public ImageIcon load(@NotNull final Face face) {
        return load(face.getFaceName(), face.getFaceChecksum());
    }

    /**
     * Retrieves an image from the cache.
     * @param faceName the image name to retrieve
     * @param faceChecksum the checksum to retrieve
     * @return the image icon, or {@code null} if the cache does not contain the
     * image
     */
    @Nullable
    private ImageIcon load(@NotNull final String faceName, final int faceChecksum) {
        final File file = getImageFileName(faceName, faceChecksum);
        final long len = file.length();
        if (len >= 0x10000 || len <= 0) {
            return null;
        }
        final byte[] data = new byte[(int)len];
        try {
            try (final FileInputStream fis = new FileInputStream(file)) {
                if (fis.read(data) != data.length) {
                    return null;
                }
            }
        } catch (final IOException ignored) {
            return null;
        }
        final ImageIcon imageIcon = new ImageIcon(data); // cannot use ImageIcon(String) since this caches "file not found"
        return imageIcon.getIconWidth() <= 0 && imageIcon.getIconHeight() <= 0 ? null : imageIcon;
    }

    @Override
    public void save(@NotNull final Face face, @NotNull final ImageIcon imageIcon) {
        save(face.getFaceName(), face.getFaceChecksum(), imageIcon);
    }

    /**
     * Stores an {@link ImageIcon} into the cache.
     * @param faceName the image name to save
     * @param faceChecksum the checksum to save
     * @param imageIcon the image icon to store
     */
    public void save(@NotNull final String faceName, final int faceChecksum, @NotNull final Icon imageIcon) {
        Images.saveImageIcon(getImageFileName(faceName, faceChecksum), imageIcon);
    }

    /**
     * Calculates a hashed image name to be used as a file name.
     * @param faceName the image name to hash
     * @param faceChecksum the checksum to hash
     * @return the hashed image name
     */
    @NotNull
    private File getImageFileName(@NotNull final String faceName, final int faceChecksum) {
        final String quotedFaceName = FilenameUtils.quoteName(faceName);
        final String dirName = quotedFaceName.substring(0, Math.min(2, quotedFaceName.length()));
        final File dir = new File(new File(cacheDir, dirName), quotedFaceName);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Cannot create directory: "+dir);
        }
        return new File(dir, Integer.toString(faceChecksum));
    }

}

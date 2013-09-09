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

import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link FacesManager} for regression tests.
 * @author Andreas Kirschbaum
 */
public class TestFacesManager extends AbstractFacesManager {

    /**
     * Creates a new instance.
     * @param faceCache the face cache instance for storing in-memory faces
     */
    public TestFacesManager(@NotNull final FaceCache faceCache) {
        super(faceCache);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected FaceImages getFaceImages(final int faceNum, @Nullable final boolean[] isUnknownImage) {
        final Face face = lookupFace(faceNum);
        final FaceImages faceImages = face.getFaceImages();
        if (faceImages != null) {
            return faceImages;
        }

        final FaceImages faceImages2 = new FaceImages(new ImageIcon(), new ImageIcon(), new ImageIcon());
        face.setFaceImages(faceImages2);
        return faceImages2;
    }

}

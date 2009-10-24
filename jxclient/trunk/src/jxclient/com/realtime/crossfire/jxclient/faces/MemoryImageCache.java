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

import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple in-memory cache implementing the {@link ImageCache} interface.
 * Primary use is from regression tests.
 * @author Andreas Kirschbaum
 */
public class MemoryImageCache implements ImageCache
{
    /**
     * The cache contents. Maps face to image associated with the face.
     */
    @NotNull
    private final Map<Face, ImageIcon> faces = new HashMap<Face, ImageIcon>();

    /** {@inheritDoc} */
    @Nullable
    @Override
    public ImageIcon load(@NotNull final Face face)
    {
        return faces.get(face);
    }

    /** {@inheritDoc} */
    @Override
    public void save(@NotNull final Face face, @NotNull final ImageIcon imageIcon)
    {
        faces.put(face, imageIcon);
    }
}

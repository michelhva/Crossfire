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

/**
 * A cache for {@link Face} instances.
 *
 * @author Andreas Kirschbaum
 */
public class FaceCache
{
    /**
     * The cached faces. Empty slots are set to <code>null</code>.
     */
    private Face[] faces = new Face[65536];

    /**
     * Add a new face to the cache.
     */
    public void addFace(final Face face)
    {
        faces[face.getID()] = face;
    }

    /**
     * Return a face by face id.
     *
     * @return The face, or <code>null</code> if the face is not cached.
     */
    public Face getFace(final int id)
    {
        return faces[index];
    }
}

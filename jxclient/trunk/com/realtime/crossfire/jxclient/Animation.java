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
package com.realtime.crossfire.jxclient;

/**
 * Manages animations frecevied from the server. Animations are uniquely
 * identified by an animation id. Each animation consists of a list of faces.
 *
 * @author Andreas Kirschbaum
 */
public class Animation
{
    /**
     * Flags for the animation; currently unused.
     */
    private final int flags;

    /**
     * @The faces list of the animation.
     */
    private final int[] faces;

    /**
     * Create a new instance.
     *
     * @param flags Flags for the animation; currently unused.
     *
     * @param faces The faces list of the animation.
     */
    public Animation(final int flags, final int[] faces)
    {
        assert faces.length > 0;

        this.flags = flags;
        this.faces = new int[faces.length];
        System.arraycopy(faces, 0, this.faces, 0, faces.length);
    }

    /**
     * Return the number of faces of this animation.
     *
     * @return The number of faces.
     */
    public int getFaces()
    {
        return faces.length;
    }

    /**
     * Return one face of this animation.
     *
     * @param index The face index.
     *
     * @return The face.
     */
    public int getFace(final int index)
    {
        return faces[index];
    }
}

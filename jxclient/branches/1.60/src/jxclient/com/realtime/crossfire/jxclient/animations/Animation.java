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

package com.realtime.crossfire.jxclient.animations;

import org.jetbrains.annotations.NotNull;

/**
 * Manages animations received from the server. Animations are uniquely
 * identified by an animation id. Each animation consists of a list of faces.
 * @author Andreas Kirschbaum
 */
public class Animation {

    /**
     * The animation ID.
     */
    private final int animationId;

    /**
     * Flags for the animation; currently unused by the server.
     */
    private final int flags;

    /**
     * The faces list of the animation.
     */
    @NotNull
    private final int[] faces;

    /**
     * Creates a new instance.
     * @param animationId the animation ID
     * @param flags flags for the animation; currently unused
     * @param faces the faces list of the animation
     */
    public Animation(final int animationId, final int flags, @NotNull final int[] faces) {
        assert faces.length > 0;

        this.animationId = animationId;
        this.flags = flags;
        this.faces = new int[faces.length];
        System.arraycopy(faces, 0, this.faces, 0, faces.length);
    }

    /**
     * Returns the animation ID.
     * @return the animation ID
     */
    public int getAnimationId() {
        return animationId;
    }

    /**
     * Returns the number of faces of this animation.
     * @return the number of faces
     */
    public int getFaces() {
        return faces.length;
    }

    /**
     * Returns one face of this animation.
     * @param index the face index
     * @return the face
     */
    public int getFace(final int index) {
        return faces[index];
    }

}

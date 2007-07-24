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

import java.util.HashMap;
import java.util.Map;

/**
 * Manages animations frecevied from the server. Animations are uniquely
 * identified by an animation id. Each animation consists of a list of faces.
 *
 * @author Andreas Kirschbaum
 */
public class Animations
{
    /**
     * All defined animations. Maps animation id to animation instance.
     */
    private final Map<Integer, Animation> animations = new HashMap<Integer, Animation>();

    /**
     * Define a new animation.
     *
     * @param animationId The animation id.
     *
     * @param flags Flags for the animation; currently unused.
     *
     * @param faces The faces list of the animation.
     */
    public void addAnimation(final int animationId, final int flags, final int[] faces)
    {
        if (faces.length == 1)
        {
            System.err.println("Warning: animation id "+animationId+" has only one face");
        }

        final Animation animation = new Animation(flags, faces);
        if (animations.put(animationId, animation) != null) {
            System.err.println("Warning: duplicate animation id "+animationId);
        }
    }

    /**
     * Return the animation for an animation id.
     *
     * @param animationId The animation id.
     *
     * @return The animation instance, or <code>null</code> if the animation id
     * does not exist.
     */
    public Animation get(final int animationId)
    {
        return animations.get(animationId);
    }
}

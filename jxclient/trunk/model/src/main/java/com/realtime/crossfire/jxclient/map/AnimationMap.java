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

package com.realtime.crossfire.jxclient.map;

import com.realtime.crossfire.jxclient.mapupdater.MapUpdaterState;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains {@link AnimationState} instances for map {@link Location
 * locations}.
 * @author Andreas Kirschbaum
 */
public class AnimationMap {

    /**
     * The active {@link AnimationState} instances. Maps map {@link Location} to
     * <code>AnimationState</code> instance.
     */
    @NotNull
    private final Map<Location, AnimationState> animations = new HashMap<Location, AnimationState>();

    /**
     * Forgets all state.
     */
    public void clear() {
        animations.clear();
    }

    /**
     * Adds a new {@link AnimationState} to a {@link Location}. If the location
     * was not empty, the previous animation state is freed.
     * @param mapUpdaterState the map updater state instance to use
     * @param location the location to add to
     * @param animationState the animation state to add
     */
    public void add(@NotNull final MapUpdaterState mapUpdaterState, @NotNull final Location location, @NotNull final AnimationState animationState) {
        freeAnimationState(animations.put(location, animationState), location);
        animationState.allocate(mapUpdaterState, location);
    }

    /**
     * Clears a {@link Location}.
     * @param location the location to clear
     */
    public void remove(@NotNull final Location location) {
        freeAnimationState(animations.remove(location), location);
    }

    /**
     * Updates the animation speed value of a {@link Location}.
     * @param mapUpdaterState the map updater state instance to use
     * @param location the location to update
     * @param speed the new animation speed
     */
    public void updateSpeed(@NotNull final MapUpdaterState mapUpdaterState, @NotNull final Location location, final int speed) {
        final AnimationState animationState = animations.get(location);
        if (animationState == null) {
            System.err.println("No animation at "+location+" to update animation speed.");
            return;
        }

        animationState.setSpeed(mapUpdaterState, speed);
    }

    /**
     * Scrolls all locations.
     * @param dx the x distance to scroll
     * @param dy the y distance to scroll
     * @param width the map width
     * @param height the map height
     */
    public void scroll(final int dx, final int dy, final int width, final int height) {
        final Iterable<AnimationState> tmp = new HashSet<AnimationState>(animations.values());
        animations.clear();
        for (final AnimationState animationState : tmp) {
            animationState.scroll(dx, dy, width, height);
            for (final Location location : animationState) {
                animations.put(location, animationState);
            }
        }
    }

    /**
     * Calls {@link AnimationState#free(Location)}.
     * @param animationState the instance to call <code>free()</code> on or
     * <code>null</code> to do nothing
     * @param location the location to pass
     */
    private static void freeAnimationState(@Nullable final AnimationState animationState, @NotNull final Location location) {
        if (animationState != null) {
            animationState.free(location);
        }
    }

}

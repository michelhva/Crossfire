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

import com.realtime.crossfire.jxclient.animations.Animation;
import com.realtime.crossfire.jxclient.mapupdater.MapUpdaterState;
import com.realtime.crossfire.jxclient.server.crossfire.messages.Map2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * Manages a set of animated map squares.
 * @author Andreas Kirschbaum
 */
public class CfMapAnimations {

    /**
     * Synchronization object.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The random number generator for {@link Map2#ANIM_RANDOM} type
     * animations.
     */
    @NotNull
    private final Random random = new Random();

    /**
     * The width of the visible map area.
     */
    private int width = 0;

    /**
     * The height of the visible map area.
     */
    private int height = 0;

    /**
     * The {@link MapUpdaterState} instance to update.
     */
    @NotNull
    private final MapUpdaterState mapUpdaterState;

    /**
     * The animations in the visible map area.
     */
    @NotNull
    private final AnimationMap animations = new AnimationMap();

    /**
     * All {@link AnimationState} instances referenced by {@link #animations}.
     */
    @NotNull
    private final Map<AnimationState, Void> animationStates = new WeakHashMap<AnimationState, Void>();

    /**
     * All {@link AnimationState} for {@link Map2#ANIM_SYNC} animations.
     */
    @NotNull
    private final Map<Integer, AnimationState> syncAnimationStates = new HashMap<Integer, AnimationState>();

    /**
     * The {@link AnimationState} instances that have been added but not yet
     * received a "tick" value.
     */
    @NotNull
    private final Collection<AnimationState> pendingTickUpdates = new ArrayList<AnimationState>();

    /**
     * Creates a new instance.
     * @param mapUpdaterState the instance to update
     */
    public CfMapAnimations(@NotNull final MapUpdaterState mapUpdaterState) {
        this.mapUpdaterState = mapUpdaterState;
    }

    /**
     * Forgets all animations.
     */
    public void clear() {
        synchronized (sync) {
            animations.clear();
            animationStates.clear();
            syncAnimationStates.clear();
            pendingTickUpdates.clear();
        }
    }

    /**
     * Adds a visible animation.
     * @param location the location to animate
     * @param animation the animation to display
     * @param type the animation type
     */
    public void add(@NotNull final Location location, @NotNull final Animation animation, final int type) {
        assert 0 <= location.getX();
        assert 0 <= location.getY();
        assert 0 <= type && type < 4;

        final AnimationState animationState;
        final boolean addToPendingTickUpdates;
        switch (type) {
        default: // invalid; treated as "normal"
        case Map2.ANIM_NORMAL: // animation starts at index 0
            animationState = new AnimationState(mapUpdaterState, animation, 0);
            addToPendingTickUpdates = true;
            break;

        case Map2.ANIM_RANDOM: // animation starts at random index
            animationState = new AnimationState(mapUpdaterState, animation, random.nextInt(animation.getFaces()));
            addToPendingTickUpdates = true;
            break;

        case Map2.ANIM_SYNC: // animation is synchronized with other animations
            final int animationId = animation.getAnimationId();
            final AnimationState tmp = syncAnimationStates.get(animationId);
            if (tmp != null) {
                animationState = tmp;
                addToPendingTickUpdates = false;
            } else {
                animationState = new AnimationState(mapUpdaterState, animation, 0);
                syncAnimationStates.put(animationId, animationState);
                addToPendingTickUpdates = true;
            }
            break;
        }

        synchronized (sync) {
            animationStates.put(animationState, null);
            animations.add(location, animationState);
            if (addToPendingTickUpdates) {
                pendingTickUpdates.add(animationState);
            }
        }
    }

    /**
     * Removes all visible animations for a tile.
     * @param x the x-coordinate to un-animate
     * @param y the y-coordinate to un-animate
     */
    public void remove(final int x, final int y) {
        synchronized (sync) {
            for (int layer = 0; layer < Map2.NUM_LAYERS; layer++) {
                animations.remove(new Location(x, y, layer));
            }
        }
    }

    /**
     * Removes a visible animation.
     * @param location the location to un-animate
     */
    public void remove(@NotNull final Location location) {
        synchronized (sync) {
            animations.remove(location);
        }
    }

    /**
     * Updates the animation speed value.
     * @param location the location to update
     * @param speed the new animation speed
     */
    public void updateSpeed(@NotNull final Location location, final int speed) {
        synchronized (sync) {
            animations.updateSpeed(location, speed);
        }
    }

    /**
     * Scrolls the animations. Animations scrolled off the visible are are
     * dropped.
     * @param dx the x-distance to scroll
     * @param dy the y-distance to scroll
     */
    public void scroll(final int dx, final int dy) {
        synchronized (sync) {
            animations.scroll(dx, dy, width, height);
        }
    }

    /**
     * Processes a tick command.
     * @param tickNo the current tick number
     */
    public void tick(final int tickNo) {
        final Iterable<AnimationState> animationStatesToUpdate;
        synchronized (sync) {
            for (final AnimationState animationState : pendingTickUpdates) {
                animationState.setTickNo(tickNo);
            }
            pendingTickUpdates.clear();
            animationStatesToUpdate = new ArrayList<AnimationState>(animationStates.keySet());
        }
        synchronized (mapUpdaterState.mapBegin()) {
            for (final AnimationState animationState : animationStatesToUpdate) {
                animationState.updateTickNo(tickNo);
            }
            mapUpdaterState.mapEnd(false);
        }
    }

    /**
     * Updates the map size.
     * @param width the map width
     * @param height the map height
     */
    public void setMapSize(final int width, final int height) {
        synchronized (sync) {
            this.width = width;
            this.height = height;
            clear();
        }
    }

}

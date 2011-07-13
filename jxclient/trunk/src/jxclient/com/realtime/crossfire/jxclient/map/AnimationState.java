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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * The state of an {@link Animation} on a map.
 * @author Andreas Kirschbaum
 */
public class AnimationState implements Iterable<Location> {

    /**
     * The {@link Animation} to display.
     */
    @NotNull
    private final Animation animation;

    /**
     * The animation speed.
     */
    private int speed = 1;

    /**
     * The face was updated last in this tick number.
     */
    private int tickNo = 0;

    /**
     * The face index currently shown. May contain values between 0 and {@link
     * #speed}*{@link #animation}.getFaces() (exclusive).
     */
    private int index;

    /**
     * Records the last known face. Used to suppress redundant map updates.
     */
    private int lastFace = -1;

    /**
     * All map {@link Location locations} this animation is displayed at.
     */
    @NotNull
    private final Set<Location> locations = new HashSet<Location>();

    /**
     * Creates a new instance.
     * @param animation the animation to display
     * @param index the initial face index
     */
    public AnimationState(@NotNull final Animation animation, final int index) {
        this.animation = animation;
        this.index = index%animation.getFaces();
    }

    /**
     * Sets the animation speed.
     * @param mapUpdaterState the map updater state instance to use
     * @param speed the new animation speed to set
     */
    public void setSpeed(@NotNull final MapUpdaterState mapUpdaterState, final int speed) {
        assert speed > 0;
        if (this.speed == speed) {
            return;
        }
        final int tmpIndex = index/this.speed;
        final int tmpDelay = Math.min(index%this.speed, speed-1);
        this.speed = speed;
        index = tmpIndex*speed+tmpDelay;
        updateFace(mapUpdaterState);
    }

    /**
     * Sets the tick number. This function does not update the displayed face.
     * @param tickNo the current tick number
     */
    public void setTickNo(final int tickNo) {
        this.tickNo = tickNo;
    }

    /**
     * Sets the tick number and update affected faces.
     * @param mapUpdaterState the map updater state instance to use
     * @param tickNo the tick number
     */
    public void updateTickNo(@NotNull final MapUpdaterState mapUpdaterState, final int tickNo) {
        final int diff = tickNo-this.tickNo;
        if (tickNo < this.tickNo) {
            System.err.println("Ignoring inconsistent tick value: current tick number is "+tickNo+", previous tick number was "+this.tickNo+".");
        } else {
            index = (index+diff)%(speed*animation.getFaces());
        }
        this.tickNo = tickNo;
        updateFace(mapUpdaterState);
    }

    /**
     * Updates the map face from the state.
     * @param mapUpdaterState the map updater state instance to use
     */
    private void updateFace(@NotNull final MapUpdaterState mapUpdaterState) {
        final int face = animation.getFace(index/speed);
        if (face == lastFace) {
            return;
        }
        lastFace = face;
        for (final Location location : locations) {
            mapUpdaterState.mapFace(location, face, false);
        }
    }

    /**
     * Adds this animation state to a map {@link Location}.
     * @param mapUpdaterState the map updater state instance to use
     * @param location the map location
     */
    public void allocate(@NotNull final MapUpdaterState mapUpdaterState, @NotNull final Location location) {
        if (!locations.add(location)) {
            throw new IllegalArgumentException();
        }
        if (lastFace != -1) {
            mapUpdaterState.mapFace(location, lastFace, false);
        }
    }

    /**
     * Removes this animation state from a map {@link Location}.
     * @param location the location to free
     */
    public void free(@NotNull final Location location) {
        if (!locations.remove(location)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Iterator<Location> iterator() {
        return Collections.unmodifiableSet(locations).iterator();
    }

    /**
     * Scrolls all map locations.
     * @param dx the x distance to scroll
     * @param dy the y distance to scroll
     * @param width the map width
     * @param height the map height
     */
    public void scroll(final int dx, final int dy, final int width, final int height) {
        final Collection<Location> tmp = new HashSet<Location>();
        for (final Location location : locations) {
            if (0 <= location.getX() && location.getX() < width && 0 <= location.getY() && location.getY() < height) { // out-of-map bounds animations are dropped, not scrolled
                final int newX = location.getX()-dx;
                final int newY = location.getY()-dy;
                if (0 <= newX && newX < width && 0 <= newY && newY < height) { // in-map bounds animations are dropped if scrolled off the visible area
                    final Location newLocation = new Location(newX, newY, location.getLayer());
                    tmp.add(newLocation);
                }
            }
        }
        locations.clear();
        locations.addAll(tmp);
    }

}

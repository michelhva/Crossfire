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
package com.realtime.crossfire.jxclient.map;

import com.realtime.crossfire.jxclient.animations.Animation;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.server.CrossfireMap2Command;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireTickListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages a set of animated map squares.
 * @author Andreas Kirschbaum
 */
public class CfMapAnimations
{
    /**
     * Synchronization object.
     */
    private final Object sync = new Object();

    /**
     * The width of the visible map area.
     */
    private int width = 0;

    /**
     * The height of the visible map area.
     */
    private int height = 0;

    /**
     * The {@link CfMapUpdater} instance to update.
     */
    private final CfMapUpdater mapUpdater;

    /**
     * The animations in the visible map area.
     */
    private final Map<Location, AnimationState> animations = new HashMap<Location, AnimationState>();

    /**
     * The {@link AnimationState} instances that have been added but not yet
     * received a "tick" value.
     */
    private final Set<AnimationState> pendingTickUpdates = new HashSet<AnimationState>();

    /**
     * The listener for receiving "tick" commands.
     */
    private final CrossfireTickListener crossfireTickListener = new CrossfireTickListener()
    {
        /** {@inheritDoc} */
        @Override
        public void tick(final int tickNo)
        {
            CfMapAnimations.this.tick(tickNo);
        }
    };

    /**
     * Creates a new instance.
     * @param mapUpdater the instance to update
     */
    public CfMapAnimations(final CfMapUpdater mapUpdater)
    {
        this.mapUpdater = mapUpdater;
    }

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection to watch
     * @param mapUpdater the instance to update
     */
    public CfMapAnimations(final CrossfireServerConnection crossfireServerConnection, final CfMapUpdater mapUpdater)
    {
        this(mapUpdater);
        crossfireServerConnection.addCrossfireTickListener(crossfireTickListener);
    }

    /**
     * Forgets all animations.
     */
    public void clear()
    {
        synchronized (sync)
        {
            animations.clear();
            pendingTickUpdates.clear();
        }
    }

    /**
     * Adds a visible animation.
     * @param location the location to animate
     * @param animation the animation to display
     * @param type the animation type
     */
    public void add(final Location location, final Animation animation, final int type)
    {
        assert 0 <= location.getX();
        assert 0 <= location.getY();
        assert 0 <= type && type < 4;

        final AnimationState animationState = new AnimationState(animation, type, mapUpdater);
        synchronized (sync)
        {
            animations.put(location, animationState);
        }
        animationState.draw(location, -1);
    }

    /**
     * Removes all visible animations for a tile.
     * @param x the x-coordinate to un-animate
     * @param y the y-coordinate to un-animate
     */
    public void remove(final int x, final int y)
    {
        synchronized (sync)
        {
            for (int layer = 0; layer < CrossfireMap2Command.NUM_LAYERS; layer++)
            {
                animations.remove(new Location(x, y, layer));
            }
        }
    }

    /**
     * Removes a visible animation.
     * @param location the location to un-animate
     */
    public void remove(final Location location)
    {
        assert 0 <= location.getX();
        assert 0 <= location.getY();

        synchronized (sync)
        {
            animations.remove(location);
        }
    }

    /**
     * Updates the animation speed value.
     * @param location the location to update
     * @param speed the new animation speed
     */
    public void updateSpeed(final Location location, final int speed)
    {
        assert 0 <= location.getX();
        assert 0 <= location.getY();

        final AnimationState animationState;
        synchronized (sync)
        {
            animationState = animations.get(location);
        }
        if (animationState == null)
        {
            System.err.println("No animation at "+location+" to update animation speed.");
            return;
        }

        animationState.setSpeed(speed);
    }

    /**
     * Scrolls the animations. Animations scrolled off the visible are are
     * dropped.
     * @param dx the x-distance to scroll
     * @param dy the y-distance to scroll
     */
    public void scroll(final int dx, final int dy)
    {
        synchronized (sync)
        {
            final Map<Location, AnimationState> tmp = new HashMap<Location, AnimationState>(animations);
            animations.clear();

            for (final Map.Entry<Location, AnimationState> e : tmp.entrySet())
            {
                final Location location = e.getKey();
                if (0 <= location.getX() && location.getX() < width && 0 <= location.getY() && location.getY() < height) // out-of-map bounds animations are dropped not scrolled
                {
                    final int newX = location.getX()-dx;
                    final int newY = location.getY()-dy;
                    if (0 <= newX && newX < width && 0 <= newY && newY < height) // in-map bounds animations are dropped if scrolled off the visible area
                    {
                        animations.put(new Location(newX, newY, location.getLayer()), e.getValue());
                    }
                }
            }
        }
    }

    /**
     * Processes a tick command.
     * @param tickNo the current tick number
     */
    public void tick(final int tickNo)
    {
        final List<Map.Entry<Location, AnimationState>> animationsToUpdate;
        synchronized (sync)
        {
            for (final AnimationState animationState : pendingTickUpdates)
            {
                animationState.setTickno(tickNo);
            }
            pendingTickUpdates.clear();

            if (animations.isEmpty())
            {
                return;
            }

            animationsToUpdate = new ArrayList<Map.Entry<Location, AnimationState>>();
            for (final Map.Entry<Location, AnimationState> e : animations.entrySet())
            {
                animationsToUpdate.add(e);
            }
        }
        mapUpdater.processMapBegin();
        for (final Map.Entry<Location, AnimationState> e : animationsToUpdate)
        {
            final Location location = e.getKey();
            final AnimationState animationState = e.getValue();
            animationState.updateTickno(tickNo, location);
        }
        mapUpdater.processMapEnd(false);
    }

    /**
     * Updates the map size.
     * @param width the map width
     * @param height the map height
     */
    public void setMapSize(final int width, final int height)
    {
        synchronized (sync)
        {
            this.width = width;
            this.height = height;
            animations.clear();
            pendingTickUpdates.clear();
        }
    }
}

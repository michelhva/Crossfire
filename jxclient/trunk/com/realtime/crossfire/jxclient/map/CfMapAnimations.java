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

import com.realtime.crossfire.jxclient.Animation;
import com.realtime.crossfire.jxclient.CfMapUpdater;
import com.realtime.crossfire.jxclient.CrossfireServerConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages a set of animated map squares.
 *
 * @author Andreas Kirschbaum
 */
public class CfMapAnimations
{
    /**
     * The width of the visible map area.
     */
    private final int width;

    /**
     * The height of the visible map area.
     */
    private final int height;

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
     * Create a new instance.
     *
     * @param width The visible map width.
     *
     * @param height The visible map height.
     */
    public CfMapAnimations(final int width, final int height)
    {
        this.width = width;
        this.height = height;
    }

    /**
     * Forget all animations.
     */
    public void clear()
    {
        animations.clear();
        pendingTickUpdates.clear();
    }

    /**
     * Add a visible animation.
     *
     * @param x The x-coordinate to animate.
     *
     * @param y The y-coordinate to animate.
     *
     * @param layer The layer to animate.
     *
     * @param animation The animation to display.
     *
     * @param type The animation type.
     */
    public void add(final int x, final int y, final int layer, final Animation animation, final int type)
    {
        assert 0 <= x;
        assert 0 <= y;
        assert 0 <= type && type < 4;

        final Location location = new Location(x, y, layer);
        final AnimationState animationState = new AnimationState(animation, type);
        animations.put(location, animationState);
        animationState.draw(location, -1);
    }

    /**
     * Remove all visible animations for a tile.
     *
     * @param x The x-coordinate to un-animate.
     *
     * @param y The y-coordinate to un-animate.
     */
    public void remove(final int x, final int y)
    {
        for (int layer = 0; layer < CrossfireServerConnection.NUM_LAYERS; layer++)
        {
            animations.remove(new Location(x, y, layer));
        }
    }

    /**
     * Remove a visible animation.
     *
     * @param x The x-coordinate to un-animate.
     *
     * @param y The y-coordinate to un-animate.
     *
     * @param layer The layer to un-animate.
     */
    public void remove(final int x, final int y, final int layer)
    {
        assert 0 <= x;
        assert 0 <= y;

        animations.remove(new Location(x, y, layer));
    }

    /**
     * Update the animation speed value.
     *
     * @param x The x-coordinate to update.
     *
     * @param y The y-coordinate to update.
     *
     * @param layer The layer to update.
     *
     * @param speed The new animation speed.
     */
    public void updateSpeed(final int x, final int y, final int layer, final int speed)
    {
        assert 0 <= x;
        assert 0 <= y;

        final Location location = new Location(x, y, layer);
        final AnimationState animationState = animations.get(location);
        if (animationState == null)
        {
            System.err.println("No animation at "+x+"/"+y+"/"+layer+" to update animation speed.");
            return;
        }

        animationState.setSpeed(speed);
    }

    /**
     * Scroll the animations. Animations scrolled off the visible are are
     * dropped.
     *
     * @param dx The x-distance to scroll.
     *
     * @param dy The y-distance to scroll.
     */
    public void scroll(final int dx, final int dy)
    {
        final Map<Location, AnimationState> tmp = new HashMap<Location, AnimationState>(animations);
        animations.clear();

        for (final Map.Entry<Location, AnimationState> e : tmp.entrySet())
        {
            final Location location = e.getKey();
            if (0 <= location.x && location.x < width && 0 <= location.y && location.y < height) // out-of-map bounds animations are dropped not scrolled
            {
                final int newX = location.x-dx;
                final int newY = location.y-dy;
                if (0 <= newX && newX < width && 0 <= newY && newY < height) // in-map bounds animations are dropped if scrolled off the visible area
                {
                    animations.put(new Location(newX, newY, location.layer), e.getValue());
                }
            }
        }
    }

    /**
     * Process a tick command.
     *
     * @param tickno The current tick number.
     */
    public void tick(final int tickno)
    {
        for (final AnimationState animationState : pendingTickUpdates)
        {
            animationState.setTickno(tickno);
        }
        pendingTickUpdates.clear();

        if (animations.isEmpty())
        {
            return;
        }

        CfMapUpdater.processMapBegin();
        for (final Map.Entry<Location, AnimationState> e : animations.entrySet())
        {
            final Location location = e.getKey();
            final AnimationState animationState = e.getValue();
            animationState.updateTickno(tickno, location);
        }
        CfMapUpdater.processMapEnd(false);
    }

    /**
     * A location on the map.
     */
    private static class Location
    {
        /**
         * The x-coordinate.
         */
        private final int x;

        /**
         * The y-coordinate.
         */
        private final int y;

        /**
         * The layer.
         */
        private final int layer;

        /**
         * Create a new location.
         *
         * @param x The x-coordinate.
         *
         * @param y The y-coordinate.
         *
         * @param layer The layer.
         */
        public Location(final int x, final int y, final int layer)
        {
            this.x = x;
            this.y = y;
            this.layer = layer;
        }

        /** {@inheritDoc} */
        public boolean equals(final Object obj)
        {
            if (obj == null) return false;
            if (obj.getClass() != Location.class) return false;
            final Location loc = (Location) obj;
            return loc.x == x && loc.y == y && loc.layer == layer;
        }

        /** {@inheritDoc} */
        public int hashCode()
        {
            return x^y*0x1000^layer*0x1000000;
        }

        /** {@inheritDoc} */
        public String toString()
        {
            return x+"/"+y+"/"+layer;
        }
    }

    /**
     * Animation state information.
     */
    private static class AnimationState
    {
        /**
         * The animation to display.
         */
        private final Animation animation;

        /**
         * The animation type.
         */
        private final int type;

        /**
         * The animation speed.
         */
        private int speed = 1;

        /**
         * The face was updated last in this tick number.
         */
        private int tickno = 0;

        /**
         * The face index currently shown.
         */
        private int index = 0;

        /**
         * Create a new instance.
         *
         * @param animation The animation to display.
         *
         * @param type The animation type.
         */
        public AnimationState(final Animation animation, final int type)
        {
            this.animation = animation;
            this.type = type;
        }

        /**
         * Set the animation speed.
         *
         * @param speed The new animation speed to set.
         */
        public void setSpeed(final int speed)
        {
            assert speed > 0;
            final int tmpIndex = this.index/this.speed;
            final int tmpDelay = Math.min(this.index%this.speed, speed-1);
            this.speed = speed;
            this.index = tmpIndex*speed+tmpDelay;
        }

        /**
         * Set the tick number. This function does not update the displayed
         * face.
         */
        public void setTickno(final int tickno)
        {
            this.tickno = tickno;
        }

        /**
         * Set the tick number and update affected faces.
         *
         * @param tickno The tick number.
         *
         * @param location The location to update.
         */
        public void updateTickno(final int tickno, final Location location)
        {
            final int oldFaceIndex = index/speed;
            final int diff = tickno-this.tickno;
            if (tickno < this.tickno)
            {
                System.err.println("Ignoring inconsistent tick value: current tick number is "+tickno+", previous tick number was "+this.tickno+".");
            }
            else
            {
                index = (index+diff)%(speed*animation.getFaces());
            }
            this.tickno = tickno;

            draw(location, oldFaceIndex);
        }

        /**
         * Update the map face at the given location.
         *
         * @param location The map location to update.
         *
         * @param oldFaceIndex Suppress the map face update if the new face
         * index equals this value.
         */
        public void draw(final Location location, final int oldFaceIndex)
        {
            final int faceIndex = index/speed;
            if(faceIndex == oldFaceIndex)
            {
                return;
            }

            final int face = animation.getFace(faceIndex);
            CfMapUpdater.processMapFace(location.x, location.y, location.layer, face);
        }
    }
}

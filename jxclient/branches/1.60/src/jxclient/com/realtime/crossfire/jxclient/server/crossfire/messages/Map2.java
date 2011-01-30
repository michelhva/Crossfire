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

package com.realtime.crossfire.jxclient.server.crossfire.messages;

/**
 * Interface defining constants for the "map2" Crossfire protocol message.
 * @author Andreas Kirschbaum
 */
public interface Map2 {

    /**
     * The total number of map layers to display.
     */
    int NUM_LAYERS = 10;

    /**
     * Offset for coordinate values in map2 command.
     */
    int COORD_OFFSET = 15;

    /**
     * Normal coordinate.
     */
    int TYPE_COORDINATE = 0;

    /**
     * Scroll information.
     */
    int TYPE_SCROLL = 1;

    /**
     * Clear a square.
     */
    int COORD_CLEAR_SPACE = 0;

    /**
     * Darkness information.
     */
    int COORD_DARKNESS = 1;

    /**
     * Face information for layer 0.
     */
    int COORD_LAYER0 = 0x10;

    /**
     * Face information for layer 1.
     */
    int COORD_LAYER1 = 0x11;

    /**
     * Face information for layer 2.
     */
    int COORD_LAYER2 = 0x12;

    /**
     * Face information for layer 3.
     */
    int COORD_LAYER3 = 0x13;

    /**
     * Face information for layer 4.
     */
    int COORD_LAYER4 = 0x14;

    /**
     * Face information for layer 5.
     */
    int COORD_LAYER5 = 0x15;

    /**
     * Face information for layer 6.
     */
    int COORD_LAYER6 = 0x16;

    /**
     * Face information for layer 7.
     */
    int COORD_LAYER7 = 0x17;

    /**
     * Face information for layer 8.
     */
    int COORD_LAYER8 = 0x18;

    /**
     * Face information for layer 9.
     */
    int COORD_LAYER9 = 0x19;

    /**
     * Bit value whether this is a face or an animation.
     */
    int FACE_ANIMATION = 0x8000;

    /**
     * Animation type: normal animation. Starts at index zero.
     */
    int ANIM_NORMAL = 0;

    /**
     * Animation type: randomized animation. Starts at random index.
     */
    int ANIM_RANDOM = 1;

    /**
     * Animation type: synchronized animation. Starts at same state as similar
     * animations.
     */
    int ANIM_SYNC = 2;

    /**
     * The mask for extracting the animation ID.
     */
    int ANIM_MASK = 0x1FFF;

    /**
     * The lowest bit of the animation type.
     */
    int ANIM_TYPE_SHIFT = 13;

    /**
     * The mask for extracting the animation type. To be applied after
     * shifting.
     */
    int ANIM_TYPE_MASK = 3;

}

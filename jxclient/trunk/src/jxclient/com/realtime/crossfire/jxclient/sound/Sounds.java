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
package com.realtime.crossfire.jxclient.sound;

/**
 * Manages all sounds.
 *
 * @author Andreas Kirschbaum
 */
public enum Sounds
{
    /** A character related sound event. */
    CHARACTER;

    /**
     * The sound to play when the character get poisoned.
     */
    public static final String POISON_ON = "poison_on";

    /**
     * The sound to play when the character gets unpoisoned.
     */
    public static final String POISON_OFF = "poison_off";

    /**
     * The sound to play when the character gains a new level.
     */
    public static final String LEVEL_UP = "level_up";
}

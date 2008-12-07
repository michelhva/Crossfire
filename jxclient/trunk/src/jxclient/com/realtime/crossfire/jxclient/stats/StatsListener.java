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
package com.realtime.crossfire.jxclient.stats;

/**
 * Interface for listeners interested in changes of {@link Stats} instances.
 * @author Andreas Kirschbaum
 */
public interface StatsListener
{
    /**
     * The stats instance is about to be reset.
     */
    void reset();

    /**
     * A stat value has changed.
     * @param statnr the stat number
     * @param value the new stat value
     */
    void statChanged(int statnr, int value);

    /**
     * The "simple weapon speed" value has changed.
     * @param simpleWeaponSpeed the new stat value
     */
    void simpleWeaponSpeedChanged(boolean simpleWeaponSpeed);

    /**
     * The player's title has changed.
     * @param title the new title
     */
    void titleChanged(String title);

    /**
     * The player's range type has changed.
     * @param range the new range type
     */
    void rangeChanged(String range);

    /**
     * The player's active skill has changed.
     * @param activeSkill the new active skill
     */
    void activeSkillChanged(String activeSkill);

    /**
     * The player's experience has changed.
     * @param exp the new experience
     */
    void experienceChanged(long exp);

    /**
     * The player's experience to reach the next level has changed.
     * @param expNextLevel the new experience
     */
    void experienceNextLevelChanged(long expNextLevel);
}

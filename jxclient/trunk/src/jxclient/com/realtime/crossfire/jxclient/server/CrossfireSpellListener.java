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
package com.realtime.crossfire.jxclient.server;

/**
 * Interface for listeners interested in spell information related messages
 * received from the Crossfire server.
 * @author Andreas Kirschbaum
 */
public interface CrossfireSpellListener
{
    /** Flag for updspell command: mana is present. */
    int UPD_SP_MANA = 1;

    /** Flag for updspell command: grace is present. */
    int UPD_SP_GRACE = 2;

    /** Flag for updspell command: damage is present. */
    int UPD_SP_DAMAGE = 4;

    /**
     * An "addspell" command has been received.
     * @param tag the spell tag
     * @param level the spell level
     * @param castingTime the casting time
     * @param mana the mana needed to cast the spell
     * @param grace the grace needed to case the spell
     * @param damage the damage done
     * @param skill the skill needed to cast the spell
     * @param path the spell path
     * @param face the face of the spell icon
     * @param name the spell name
     * @param message the spell description
     */
    void addSpell(int tag, int level, int castingTime, int mana, int grace, int damage, int skill, int path, int face, String name, String message);

    /**
     * A "delspell" command has been received.
     * @param tag the spell tag
     */
    void deleteSpell(int tag);

    /**
     * And "updspell" command has been received.
     * @param flags the changed information
     * @param tag the spell tag
     * @param mana the mana needed to cast the spell
     * @param grace the grace needed to cast the spell
     * @param damage the damage done
     */
    void updateSpell(int flags, int tag, int mana, int grace, int damage);
}

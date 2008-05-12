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
 * Interface for listeners interested in item related commands.
 * @author Andreas Kirschbaum
 */
public interface CrossfireUpdateItemListener
{
    /**
     * A "delinv" command has been received.
     * @param tag the item tag
     */
    void delinvReceived(int tag);

    /**
     * A "delitem" command has been received.
     * @param tags the item tags
     */
    void delitemReceived(int[] tags);

    /**
     * An "additem" has been received.
     * @param location the item's location
     * @param tag the item tag
     * @param flags the item's flags
     * @param weight the item's weight
     * @param faceNum the item's face ID
     * @param name the item's singular name
     * @param namePl the item's plural name
     * @param anim the item's animation ID
     * @param animSpeed the item's animation speed
     * @param nrof the number of items
     * @param type the item's type
     */
    void additemReceived(int location, int tag, int flags, int weight, int faceNum, String name, String namePl, int anim, int animSpeed, int nrof, int type);

    /**
     * A command has been parsed.
     */
    void commandComplete();

    /**
     * A "player" command has been received.
     * @param tag the player's taqg
     * @param weight the player's weight
     * @param faceNum the player's face ID
     * @param name the player's name
     */
    void playerReceived(int tag, int weight, int faceNum, String name);

    /**
     * An "upditem" command has been received.
     * @param flags the changed values
     * @param tag the item's tag
     * @param valLocation the item's location
     * @param valFlags the item's flags
     * @param valWeight the item's weight
     * @param valFaceNum the item's face ID
     * @param valName the item's singular name
     * @param valNamePl the item's plural name
     * @param valAnim the item's animation ID
     * @param valAnimSpeed the item's animation speed
     * @param valNrof the number of items
     */
    void upditemReceived(int flags, int tag, int valLocation, int valFlags, int valWeight, int valFaceNum, String valName, String valNamePl, int valAnim, int valAnimSpeed, int valNrof);
}

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
 * Adds encoding/decoding of crossfire protocol packets to a {@link
 * ServerConnection}.
 * @author Andreas Kirschbaum
 */
public interface CrossfireServerConnection extends ServerConnection
{
    /**
     * Starts operation.
     */
    void start();

    /**
     * Stops operation.
     * @throws InterruptedException if stopping was interrupted
     */
    void stop() throws InterruptedException;

    /**
     * Adds a listener to be notified about connection progress.
     * @param listener the listener to add
     */
    void addCrossfireServerConnectionListener(CrossfireServerConnectionListener listener);

    /**
     * Adds a listener to be notified about map size changes.
     * @param listener the listener to add
     */
    void addMapSizeListener(MapSizeListener listener);

    /**
     * Removes a listener to be notified about map size changes.
     * @param listener the listener to remove
     */
    void removeMapSizeListener(MapSizeListener listener);

    /**
     * Adds a new listener monitoring the drawinfo S-&gt;C messages.
     * @param listener the listener to remove
     */
    void addCrossfireDrawinfoListener(CrossfireDrawinfoListener listener);

    /**
     * Removes the given listener from the list of objects listening to the
     * drawinfo S-&gt;C messages.
     * @param listener the listener to remove
     */
    void removeCrossfireDrawinfoListener(CrossfireDrawinfoListener listener);

    /**
     * Adds a new listener monitoring the drawextinfo S-&gt;C messages.
     * @param listener the listener to remove
     */
    void addCrossfireDrawextinfoListener(CrossfireDrawextinfoListener listener);

    /**
     * Removes the given listener from the list of objects listening to the
     * drawextinfo S-&gt;C messages.
     * @param listener the listener to remove
     */
    void removeCrossfireDrawextinfoListener(CrossfireDrawextinfoListener listener);

    /**
     * Adds a new listener monitoring the query S-&gt;C messages.
     * @param listener the listener to remove
     */
    void addCrossfireQueryListener(CrossfireQueryListener listener);

    /**
     * Removes the given listener from the list of objects listening to the
     * query S-&gt;C messages.
     * @param listener the listener to remove
     */
    void removeCrossfireQueryListener(CrossfireQueryListener listener);

    /**
     * Adds a listener from the list of objects listening to magicmap messages.
     * @param listener the listener to add
     */
    void addCrossfireMagicmapListener(CrossfireMagicmapListener listener);

    /**
     * Removes a listener from the list of objects listening to magicmap
     * messages.
     * @param listener the listener to remove
     */
    void removeCrossfireMagicmapListener(CrossfireMagicmapListener listener);

    /**
     * Add a listener to be notified about face image changes.
     * @param listener the listener to add
     */
    void addCrossfireUpdateFaceListener(CrossfireUpdateFaceListener listener);

    /**
     * Adds a listener to be notified about stats changes.
     * @param crossfireStatsListener the listener to add
     */
    void addCrossfireStatsListener(CrossfireStatsListener crossfireStatsListener);

    /**
     * Adds a listener to be notified about item changes.
     * @param crossfireUpdateItemListener the listener to add
     */
    void addCrossfireUpdateItemListener(CrossfireUpdateItemListener crossfireUpdateItemListener);

    /**
     * Adds a listener to be notified about map changes.
     * @param listener the listener to add
     */
    void addCrossfireUpdateMapListener(CrossfireUpdateMapListener listener);

    /**
     * Adds a listener to be notified about tick changes.
     * @param listener the listener to add
     */
    void addCrossfireTickListener(CrossfireTickListener listener);

    /**
     * Adds a listener to be notified about received sound commands.
     * @param listener the listener to add
     */
    void addCrossfireSoundListener(CrossfireSoundListener listener);

    /**
     * Adds a listener to be notified about received music commands.
     * @param listener the listener to add
     */
    void addCrossfireMusicListener(CrossfireMusicListener listener);

    /**
     * Adds a listener to be notified about received comc commands.
     * @param listener the listener to add
     */
    void addCrossfireComcListener(CrossfireComcListener listener);

    /**
     * Adds a listener to be notified about received face commands.
     * @param listener the listener to add
     */
    void addCrossfireFaceListener(final CrossfireFaceListener listener);

    /**
     * Adds a listener to be notified about received spell commands.
     * @param listener the listener to add
     */
    void addCrossfireSpellListener(CrossfireSpellListener listener);

    /**
     * Adds a listener to be notified about received experience table changes.
     * @param crossfireExpTableListener the listener to add
     */
    void addCrossfireExpTableListener(CrossfireExpTableListener crossfireExpTableListener);

    /**
     * Adds a listener to be notified about received skill info changes.
     * @param listener the listener to add
     */
    void addCrossfireSkillInfoListener(CrossfireSkillInfoListener listener);

    /**
     * Removes a listener to be notified about received skill info changes.
     * @param listener the listener to remove
     */
    void removeCrossfireSkillInfoListener(CrossfireSkillInfoListener listener);

    /**
     * Adds a listener to be notified about received packets.
     * @param listener the listener to add
     */
    void addPacketWatcherListener(ReceivedPacketListener listener);

    /**
     * Removes a listener to be notified about received packets.
     * @param listener the listener to add
     */
    void removePacketWatcherListener(ReceivedPacketListener listener);

    /**
     * Pretends that a drawinfo message has been received.
     * @param message the message
     * @param color the color
     */
    void drawInfo(String message, int color);

    /**
     * Sends an "addme" command to the server.
     */
    void sendAddme();

    /**
     * Sends an "apply" command to the server.
     * @param tag the item to apply
     */
    void sendApply(int tag);

    /**
     * Sends an "askface" command to the server.
     * @param num the face to query
     */
    void sendAskface(int num);

    /**
     * Sends an "examine" command to the server.
     * @param tag the item to examine
     */
    void sendExamine(int tag);

    /**
     * Sends a "lock" command to the server.
     * @param val whether to lock the item
     * @param tag the item to lock
     */
    void sendLock(boolean val, int tag);

    /**
     * Sends a "lookat" command to the server.
     * @param dx the x-coordinate in tiles, relative to the player
     * @param dy the y-coordinate in tiles, relative to the player
     */
    void sendLookat(final int dx, int dy);

    /**
     * Sends a "mark" command to the server.
     * @param tag the item to mark
     */
    void sendMark(int tag);

    /**
     * Sends a "move" command to the server.
     * @param to the destination location
     * @param tag the item to move
     * @param nrof the number of items to move
     */
    void sendMove(int to, int tag, int nrof);

    /**
     * Sends a "ncom" command to the server.
     * @param repeat the repeat count
     * @param command the command
     * @return the packet id
     */
    int sendNcom(int repeat, String command);

    /**
     * Sends a "reply" command to the server.
     * @param text the text to reply
     */
    void sendReply(String text);

    /**
     * Sends a "requestinfo" command to the server.
     * @param infoType the info type to request
     */
    void sendRequestinfo(String infoType);

    /**
     * Sends a "setup" command to the server.
     * @param options the option/value pairs to send
     */
    void sendSetup(String... options);

    /**
     * Sends a "toggleextendedtext" command to the server.
     * @param types the types to request
     */
    void sendToggleextendedtext(int... types);

    /**
     * Sends a "version" command to the server.
     * @param csval the client version number
     * @param scval the server version number
     * @param vinfo the client identification string
     */
    void sendVersion(int csval, int scval, String vinfo);

    /**
     * Sets the map size. Must not be called in connected state.
     * @param mapWidth the map width in tiles; must be odd and between 3 and 63
     * @param mapHeight the map height in tiles; must be odd and between 3 and
     * 63
     * @throws IllegalArgumentException if the map size if invalid
     */
    void setMapSize(int mapWidth, int mapHeight);

    /**
     * Sets the maximum number of objects in the ground view. Must not be
     * called in connected state.
     * @param numLookObjects the number of objects
     * @throws IllegalArgumentException if the number of objects is invalid
     */
    void setNumLookObjects(int numLookObjects);

    /**
     * Returns the map width in tiles.
     * @return the map width
     */
    int getMapWidth();

    /**
     * Returns the map height in tiles.
     * @return the map height
     */
    int getMapHeight();
}

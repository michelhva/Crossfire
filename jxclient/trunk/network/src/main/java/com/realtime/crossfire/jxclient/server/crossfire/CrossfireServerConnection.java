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

package com.realtime.crossfire.jxclient.server.crossfire;

import com.realtime.crossfire.jxclient.server.server.ReceivedPacketListener;
import com.realtime.crossfire.jxclient.server.server.ServerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds encoding/decoding of crossfire protocol packets to a {@link
 * ServerConnection}.
 * @author Andreas Kirschbaum
 */
public interface CrossfireServerConnection extends ServerConnection {

    /**
     * Starts operation.
     */
    void start();

    /**
     * Stops operation.
     * @throws InterruptedException if stopping was interrupted
     */
    @SuppressWarnings("RedundantThrows")
    void stop() throws InterruptedException;

    /**
     * Adds a new listener monitoring the drawinfo S-&gt;C messages.
     * @param listener the listener to remove
     */
    void addCrossfireDrawinfoListener(@NotNull CrossfireDrawinfoListener listener);

    /**
     * Removes the given listener from the list of objects listening to the
     * drawinfo S-&gt;C messages.
     * @param listener the listener to remove
     */
    void removeCrossfireDrawinfoListener(@NotNull CrossfireDrawinfoListener listener);

    /**
     * Adds a new listener monitoring the drawextinfo S-&gt;C messages.
     * @param listener the listener to remove
     */
    void addCrossfireDrawextinfoListener(@NotNull CrossfireDrawextinfoListener listener);

    /**
     * Removes the given listener from the list of objects listening to the
     * drawextinfo S-&gt;C messages.
     * @param listener the listener to remove
     */
    void removeCrossfireDrawextinfoListener(@NotNull CrossfireDrawextinfoListener listener);

    /**
     * Adds a new listener monitoring the query S-&gt;C messages.
     * @param listener the listener to remove
     */
    void addCrossfireQueryListener(@NotNull CrossfireQueryListener listener);

    /**
     * Removes the given listener from the list of objects listening to the
     * query S-&gt;C messages.
     * @param listener the listener to remove
     */
    void removeCrossfireQueryListener(@NotNull CrossfireQueryListener listener);

    /**
     * Adds a listener from the list of objects listening to magicmap messages.
     * @param listener the listener to add
     */
    void addCrossfireMagicmapListener(@NotNull CrossfireMagicmapListener listener);

    /**
     * Removes a listener from the list of objects listening to magicmap
     * messages.
     * @param listener the listener to remove
     */
    void removeCrossfireMagicmapListener(@NotNull CrossfireMagicmapListener listener);

    /**
     * Add a listener to be notified about face image changes.
     * @param listener the listener to add
     */
    void addCrossfireUpdateFaceListener(@NotNull CrossfireUpdateFaceListener listener);

    /**
     * Adds a listener to be notified about item changes.
     * @param crossfireUpdateItemListener the listener to add
     */
    void addCrossfireUpdateItemListener(@NotNull CrossfireUpdateItemListener crossfireUpdateItemListener);

    /**
     * Removes a listener to be notified about item changes.
     * @param crossfireUpdateItemListener the listener to remove
     */
    void removeCrossfireUpdateItemListener(@NotNull CrossfireUpdateItemListener crossfireUpdateItemListener);

    /**
     * Sets a listener to be notified about map changes. At most one such
     * listener may be set.
     * @param listener the listener to set or <code>null</code> to unset it
     */
    void setCrossfireUpdateMapListener(@Nullable CrossfireUpdateMapListener listener);

    /**
     * Adds a listener to be notified about tick changes.
     * @param listener the listener to add
     */
    void addCrossfireTickListener(@NotNull CrossfireTickListener listener);

    /**
     * Adds a listener to be notified about received sound commands.
     * @param listener the listener to add
     */
    void addCrossfireSoundListener(@NotNull CrossfireSoundListener listener);

    /**
     * Adds a listener to be notified about received music commands.
     * @param listener the listener to add
     */
    void addCrossfireMusicListener(@NotNull CrossfireMusicListener listener);

    /**
     * Adds a listener to be notified about received comc commands.
     * @param listener the listener to add
     */
    void addCrossfireComcListener(@NotNull CrossfireComcListener listener);

    /**
     * Adds a listener to be notified about received face commands.
     * @param listener the listener to add
     */
    void addCrossfireFaceListener(@NotNull CrossfireFaceListener listener);

    /**
     * Adds a listener to be notified about received "pickup" messages.
     * @param listener the listener to add
     */
    void addCrossfirePickupListener(@NotNull CrossfirePickupListener listener);

    /**
     * Removes a listener to be notified about received "pickup" messages.
     * @param listener the listener to remove
     */
    void removeCrossfirePickupListener(@NotNull CrossfirePickupListener listener);

    /**
     * Adds a listener to be notified about received packets.
     * @param listener the listener to add
     */
    void addPacketWatcherListener(@NotNull ReceivedPacketListener listener);

    /**
     * Removes a listener to be notified about received packets.
     * @param listener the listener to add
     */
    void removePacketWatcherListener(@NotNull ReceivedPacketListener listener);

    /**
     * Adds a listener to be notified about sent reply packets.
     * @param listener the listener to add
     */
    void addSentReplyListener(@NotNull SentReplyListener listener);

    /**
     * Removes a listener to be notified about sent reply packets.
     * @param listener the listener to add
     */
    void removeSentReplyListener(@NotNull SentReplyListener listener);

    /**
     * Adds a listener to be notified about account events.
     * @param listener the listener to add
     */
    void addCrossfireAccountListener(@NotNull CrossfireAccountListener listener);

    /**
     * Removes a listener to be notified about account events.
     * @param listener the listener to remove
     */
    void removeCrossfireAccountListener(@NotNull CrossfireAccountListener listener);

    /**
     * Adds a listener to be notified of failure messages.
     * @param listener the listener to add
     */
    void addCrossfireFailureListener(@NotNull CrossfireFailureListener listener);

    /**
     * Removes a listener to be notified of failure messages.
     * @param listener the listener to remove
     */
    void removeCrossfireFailureListener(@NotNull CrossfireFailureListener listener);

    /**
     * Pretends that a drawinfo message has been received.
     * @param message the message
     * @param color the color
     */
    void drawInfo(@NotNull String message, int color);

    /**
     * Pretends that a drawextinfo message has been received.
     * @param type the message type
     * @param subtype the message subtype
     * @param message the message
     * @param color the color
     */
    void drawextinfo(int color, int type, int subtype, String message);

    /**
     * Enables or disables printing of message types.
     * @param printMessageTypes whether to enable or disable message types
     */
    void drawInfoSetDebugMode(boolean printMessageTypes);

    /**
     * Asks for an account login.
     * @param login the account login
     * @param password the account password
     */
    void sendAccountLogin(@NotNull String login, @NotNull String password);

    /**
     * Sends a request to play a character from an account.
     * @param name the character's name to play
     */
    void sendAccountPlay(@NotNull String name);

    /**
     * Sends a request to add an existing character to an account.
     * @param force 0 to allow failure, 1 to force in certain situations
     * @param login the character's login
     * @param password the character's password
     */
    void sendAccountLink(int force, @NotNull String login, @NotNull String password);

    /**
     * Sends a request to create a new account.
     * @param login the account login
     * @param password the account password
     */
    void sendAccountCreate(@NotNull String login, @NotNull String password);

    /**
     * Sends a request to create a new character associated to the account.
     * @param login the character's name
     * @param password the character's password
     */
    void sendAccountCharacterCreate(@NotNull String login, @NotNull String password);

    /**
     * Sends a request to change the account's password.
     * @param currentPassword current account password
     * @param newPassword new account password
     */
    void sendAccountPassword(@NotNull String currentPassword, @NotNull String newPassword);

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
    int sendNcom(int repeat, @NotNull String command);

    /**
     * Sends a "reply" command to the server.
     * @param text the text to reply
     */
    void sendReply(@NotNull String text);

    /**
     * Sends a "requestinfo" command to the server.
     * @param infoType the info type to request
     */
    void sendRequestinfo(@NotNull String infoType);

    /**
     * Sends a "setup" command to the server.
     * @param options the option/value pairs to send
     */
    void sendSetup(@NotNull String... options);

    /**
     * Sends a "toggleextendedtext" command to the server.
     * @param types the types to request
     */
    void sendToggleextendedtext(@NotNull int... types);

    /**
     * Sends a "version" command to the server.
     * @param csval the client version number
     * @param scval the server version number
     * @param vinfo the client identification string
     */
    void sendVersion(int csval, int scval, @NotNull String vinfo);

    /**
     * Sets the preferred map size.
     * @param preferredMapWidth the preferred map width in tiles; must be odd
     * @param preferredMapHeight the preferred map height in tiles; must be odd
     */
    void setPreferredMapSize(int preferredMapWidth, int preferredMapHeight);

    /**
     * Sets the maximum number of objects in the ground view. Must not be called
     * in connected state.
     * @param preferredNumLookObjects the number of objects
     * @throws IllegalArgumentException if the number of objects is invalid
     */
    void setPreferredNumLookObjects(int preferredNumLookObjects);

    /**
     * Returns the current account name.
     * @return the current account name or <code>null</code>
     */
    @Nullable
    String getAccountName();

}

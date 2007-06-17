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
package com.realtime.crossfire.jxclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Adds encoding/decoding of crossfire protocol packets to a {@link
 * ServerConnection}.
 *
 * @author Andreas Kirschbaum
 */
public class CrossfireServerConnection extends ServerConnection
{
    /**
     * The total number of map layers to display.
     */
    public static final int NUM_LAYERS = 3;

    /**
     * The pixel size of the gaming squares. Notice that they are supposed to
     * be *squares*, so only a single value is needed :)
     */
    public static final int SQUARE_SIZE = 64;

    /**
     * The map width in tiles that is negotiated with the server.
     */
    public static final int MAP_WIDTH = 17;

    /**
     * The map height in tiles that is negotiated with the server.
     */
    public static final int MAP_HEIGHT = 13;

    private List<CrossfireGoodbyeListener> mylisteners_goodbye =
            new ArrayList<CrossfireGoodbyeListener>();
    private List<CrossfireAddmeSuccessListener> mylisteners_addme_success =
            new ArrayList<CrossfireAddmeSuccessListener>();
    private List<CrossfireDrawinfoListener> mylisteners_drawinfo =
            new ArrayList<CrossfireDrawinfoListener>();
    private List<CrossfireDrawextinfoListener> mylisteners_drawextinfo =
            new ArrayList<CrossfireDrawextinfoListener>();
    private List<CrossfireAddmeFailedListener> mylisteners_addme_failed =
            new ArrayList<CrossfireAddmeFailedListener>();
    private List<CrossfireQueryListener> mylisteners_query =
            new ArrayList<CrossfireQueryListener>();

    public static final int MSG_TYPE_BOOK_CLASP_1 = 1;
    public static final int MSG_TYPE_BOOK_CLASP_2 = 2;
    public static final int MSG_TYPE_BOOK_ELEGANT_1 = 3;
    public static final int MSG_TYPE_BOOK_ELEGANT_2 = 4;
    public static final int MSG_TYPE_BOOK_QUARTO_1 = 5;
    public static final int MSG_TYPE_BOOK_QUARTO_2 = 6;
    public static final int MSG_TYPE_BOOK_SPELL_EVOKER = 8;
    public static final int MSG_TYPE_BOOK_SPELL_PRAYER = 9;
    public static final int MSG_TYPE_BOOK_SPELL_PYRO = 10;
    public static final int MSG_TYPE_BOOK_SPELL_SORCERER = 11;
    public static final int MSG_TYPE_BOOK_SPELL_SUMMONER = 12;

    /** Flag for updspell command: mana is present. */
    public static final int UPD_SP_MANA = 1;

    /** Flag for updspell command: grace is present. */
    public static final int UPD_SP_GRACE = 2;

    /** Flag for updspell command: damage is present. */
    public static final int UPD_SP_DAMAGE = 4;

    /**
     * Buffer to build commands to send. It is shared between all sendXxx()
     * functions. It is used to synchronize these functions.
     */
    private final byte[] writeBuffer = new byte[65536];

    /**
     * A byte buffer using {@link #writeBuffer} to store the data.
     */
    private final ByteBuffer byteBuffer = ByteBuffer.wrap(writeBuffer);

    /**
     * The packet id for the next "ncom" command to send.
     */
    private int packet = 1;

    /** The command prefix for the "addme" command. */
    private static final byte[] addmePrefix = { 'a', 'd', 'd', 'm', 'e', ' ', };

    /** The command prefix for the "apply" command. */
    private static final byte[] applyPrefix = { 'a', 'p', 'p', 'l', 'y', ' ', };

    /** The command prefix for the "askface" command. */
    private static final byte[] askfacePrefix = { 'a', 's', 'k', 'f', 'a', 'c', 'e', ' ', };

    /** The command prefix for the "examine" command. */
    private static final byte[] examinePrefix = { 'e', 'x', 'a', 'm', 'i', 'n', 'e', ' ', };

    /** The command prefix for the "lock" command. */
    private static final byte[] lockPrefix = { 'l', 'o', 'c', 'k', ' ', };

    /** The command prefix for the "lookat" command. */
    private static final byte[] lookatPrefix = { 'l', 'o', 'o', 'k', 'a', 't', ' ', };

    /** The command prefix for the "mapredraw" command. */
    private static final byte[] mapredrawPrefix = { 'm', 'a', 'p', 'r', 'e', 'd', 'r', 'a', 'w', ' ', };

    /** The command prefix for the "mark" command. */
    private static final byte[] markPrefix = { 'm', 'a', 'r', 'k', ' ', };

    /** The command prefix for the "move" command. */
    private static final byte[] movePrefix = { 'm', 'o', 'v', 'e', ' ', };

    /** The command prefix for the "ncom" command. */
    private static final byte[] ncomPrefix = { 'n', 'c', 'o', 'm', ' ', };

    /** The command prefix for the "reply" command. */
    private static final byte[] replyPrefix = { 'r', 'e', 'p', 'l', 'y', ' ', };

    /** The command prefix for the "requestinfo" command. */
    private static final byte[] requestinfoPrefix = { 'r', 'e', 'q', 'u', 'e', 's', 't', 'i', 'n', 'f', 'o', ' ', };

    /** The command prefix for the "setup" command. */
    private static final byte[] setupPrefix = { 's', 'e', 't', 'u', 'p', }; // note that this command does not have a trailing space

    /** The command prefix for the "toggleextendedtext" command. */
    private static final byte[] toggleextendedtextPrefix = { 't', 'o', 'g', 'g', 'l', 'e', 'e', 'x', 't', 'e', 'n', 'd', 'e', 'd', 't', 'e', 'x', 't', }; // note that this command does not have a trailing space

    /** The command prefix for the "version" command. */
    private static final byte[] versionPrefix = { 'v', 'e', 'r', 's', 'i', 'o', 'n', ' ', };

    public CrossfireServerConnection(String hostname, int port)
    {
        super(hostname, port);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
    }

    /**
     * Adds a new listener monitoring the
     * goodbye S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireGoodbyeListener(CrossfireGoodbyeListener listener)
    {
        mylisteners_goodbye.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * goodbye S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireGoodbyeListener(CrossfireGoodbyeListener listener)
    {
        mylisteners_goodbye.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * addme_success S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireAddmeSuccessListener(CrossfireAddmeSuccessListener listener)
    {
        mylisteners_addme_success.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * addme_success S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireAddmeSuccessListener(CrossfireAddmeSuccessListener listener)
    {
        mylisteners_addme_success.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * map1 S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireMap1Listener(CrossfireMap1Listener listener)
    {
        CfMap.addCrossfireMap1Listeners(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * map1 S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireMap1Listener(CrossfireMap1Listener listener)
    {
        CfMap.removeCrossfireMap1Listeners(listener);
    }

    /**
     * Adds a new listener monitoring the
     * newmap S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireNewmapListener(CrossfireNewmapListener listener)
    {
        CfMap.addCrossfireNewmapListeners(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * newmap S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireNewmapListener(CrossfireNewmapListener listener)
    {
        CfMap.removeCrossfireNewmapListeners(listener);
    }

    /**
     * Adds a new listener monitoring the
     * player S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfirePlayerListener(CrossfirePlayerListener listener)
    {
        CfPlayer.addCrossfirePlayerListeners(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * player S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfirePlayerListener(CrossfirePlayerListener listener)
    {
        CfPlayer.removeCrossfirePlayerListeners(listener);
    }

    /**
     * Adds a new listener monitoring the
     * stats S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireStatsListener(CrossfireStatsListener listener)
    {
        CfPlayer.addCrossfireStatsListeners(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * stats S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireStatsListener(CrossfireStatsListener listener)
    {
        CfPlayer.removeCrossfireStatsListeners(listener);
    }

    /**
     * Adds a new listener monitoring the
     * mapscroll S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireMapscrollListener(CrossfireMapscrollListener listener)
    {
        CfMap.addCrossfireMapscrollListeners(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * mapscroll S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireMapscrollListener(CrossfireMapscrollListener listener)
    {
        CfMap.removeCrossfireMapscrollListeners(listener);
    }

    /**
     * Adds a new listener monitoring the
     * drawinfo S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireDrawinfoListener(CrossfireDrawinfoListener listener)
    {
        mylisteners_drawinfo.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * drawinfo S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireDrawinfoListener(CrossfireDrawinfoListener listener)
    {
        mylisteners_drawinfo.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * drawextinfo S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireDrawextinfoListener(CrossfireDrawextinfoListener listener)
    {
        mylisteners_drawextinfo.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * drawextinfo S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireDrawextinfoListener(CrossfireDrawextinfoListener listener)
    {
        mylisteners_drawextinfo.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * magicmap S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireMagicmapListener(CrossfireMagicmapListener listener)
    {
        CfMagicMap.addCrossfireMagicmapListeners(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * magicmap S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireMagicmapListener(CrossfireMagicmapListener listener)
    {
        CfMagicMap.removeCrossfireMagicmapListeners(listener);
    }

    /**
     * Adds a new listener monitoring the
     * addme_failed S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireAddmeFailedListener(CrossfireAddmeFailedListener listener)
    {
        mylisteners_addme_failed.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * addme_failed S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireAddmeFailedListener(CrossfireAddmeFailedListener listener)
    {
        mylisteners_addme_failed.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * query S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireQueryListener(CrossfireQueryListener listener)
    {
        mylisteners_query.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * query S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireQueryListener(CrossfireQueryListener listener)
    {
        mylisteners_query.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * addspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireSpellAddedListener(CrossfireSpellAddedListener listener)
    {
        ItemsList.getCrossfireSpellAddedListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * addspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireSpellAddedListener(CrossfireSpellAddedListener listener)
    {
        ItemsList.getCrossfireSpellAddedListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * updspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireSpellUpdatedListener(CrossfireSpellUpdatedListener listener)
    {
        ItemsList.getCrossfireSpellUpdatedListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * updspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireSpellUpdatedListener(CrossfireSpellUpdatedListener listener)
    {
        ItemsList.getCrossfireSpellUpdatedListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * delspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireSpellRemovedListener(CrossfireSpellRemovedListener listener)
    {
        ItemsList.getCrossfireSpellRemovedListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * delspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireSpellRemovedListener(CrossfireSpellRemovedListener listener)
    {
        ItemsList.getCrossfireSpellRemovedListeners().remove(listener);
    }

    /** {@inheritDoc} */
    // This function does not avoid index out of bounds accesses to the array
    // <code>packet</code>; instead, a try...catch clause is used to detect
    // invalid packets.
    protected void command(final byte[] packet) throws IOException, UnknownCommandException
    {
        try
        {
            final DataInputStream dis;
            int pos = 0;
            switch (packet[pos++])
            {
            case 'a':
                switch (packet[pos++])
                {
                case 'd':
                    if (packet[pos++] != 'd') break;
                    switch (packet[pos++])
                    {
                    case 'm':
                        if (packet[pos++] != 'e') break;
                        if (packet[pos++] != '_') break;
                        switch (packet[pos++])
                        {
                        case 'f':
                            if (packet[pos++] != 'a') break;
                            if (packet[pos++] != 'i') break;
                            if (packet[pos++] != 'l') break;
                            if (packet[pos++] != 'e') break;
                            if (packet[pos++] != 'd') break;
                            if (pos != packet.length) break;
                            // XXX: addme_failed command not implemented
                            return;

                        case 's':
                            if (packet[pos++] != 'u') break;
                            if (packet[pos++] != 'c') break;
                            if (packet[pos++] != 'c') break;
                            if (packet[pos++] != 'e') break;
                            if (packet[pos++] != 's') break;
                            if (packet[pos++] != 's') break;
                            if (pos != packet.length) break;
                            // XXX: addme_success command not implemented
                            return;
                        }
                        break;

                    case 's':
                        if (packet[pos++] != 'p') break;
                        if (packet[pos++] != 'e') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != ' ') break;
                        while (pos < packet.length)
                        {
                            final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int level = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int castingTime = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int mana = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int grace = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int damage = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int skill = packet[pos++]&0xFF;
                            final int path = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int face = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int nameLength = packet[pos++]&0xFF;
                            final String name = new String(packet, pos, nameLength, "UTF-8");
                            pos += nameLength;
                            final int messageLength = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final String message = new String(packet, pos, messageLength, "UTF-8");
                            pos += messageLength;
                            if (pos > packet.length) break;
                            ItemsList.addSpell(tag, level, castingTime, mana, grace, damage, skill, path, face, name, message);
                        }
                        if (pos != packet.length) break;
                        return;
                    }
                    break;

                case 'n':
                    if (packet[pos++] != 'i') break;
                    if (packet[pos++] != 'm') break;
                    if (packet[pos++] != ' ') break;
                    {
                        final int num = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int flags = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int[] faces = new int[(packet.length-pos)/2];
                        for (int i = 0; i < faces.length; i++)
                        {
                            faces[i] = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        }
                        if (pos != packet.length) break;
                        // XXX: anim command not implemented
                    }
                    return;
                }
                break;

            case 'c':
                if (packet[pos++] != 'o') break;
                if (packet[pos++] != 'm') break;
                if (packet[pos++] != 'c') break;
                if (packet[pos++] != ' ') break;
                {
                    final int packetNo = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final int time = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    if (pos != packet.length) break;
                    // XXX: comc command not implemented
                }
                return;

            case 'd':
                switch (packet[pos++])
                {
                case 'e':
                    if (packet[pos++] != 'l') break;
                    switch (packet[pos++])
                    {
                    case 'i':
                        switch (packet[pos++])
                        {
                        case 'n':
                            if (packet[pos++] != 'v') break;
                            if (packet[pos++] != ' ') break;
                            {
                                int tag = 0;
                                do
                                {
                                    tag = tag*10+parseDigit(packet[pos++]);
                                }
                                while (pos < packet.length);
                                if (pos != packet.length) break;
                                ItemsList.cleanInventory(tag);
                            }
                            return;

                        case 't':
                            if (packet[pos++] != 'e') break;
                            if (packet[pos++] != 'm') break;
                            if (packet[pos++] != ' ') break;
                            {
                                final int[] tags = new int[(packet.length-pos)/4];
                                for (int i = 0; i < tags.length; i++)
                                {
                                    tags[i] = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                }
                                if (pos != packet.length) break;
                                ItemsList.removeItems(tags);
                            }
                            return;
                        }
                        break;

                    case 's':
                        if (packet[pos++] != 'p') break;
                        if (packet[pos++] != 'e') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != ' ') break;
                        {
                            final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (pos != packet.length) break;
                            ItemsList.deleteSpell(tag);
                        }
                        return;
                    }
                    break;

                case 'r':
                    if (packet[pos++] != 'a') break;
                    if (packet[pos++] != 'w') break;
                    switch (packet[pos++])
                    {
                    case 'e':
                        if (packet[pos++] != 'x') break;
                        if (packet[pos++] != 't') break;
                        if (packet[pos++] != 'i') break;
                        if (packet[pos++] != 'n') break;
                        if (packet[pos++] != 'f') break;
                        if (packet[pos++] != 'o') break;
                        if (packet[pos++] != ' ') break;
                        {
                            int color = 0;
                            do
                            {
                                color = color*10+parseDigit(packet[pos++]);
                            }
                            while (packet[pos] != ' ');
                            pos++;

                            int type = 0;
                            do
                            {
                                type = type*10+parseDigit(packet[pos++]);
                            }
                            while (packet[pos] != ' ');
                            pos++;

                            int subtype = 0;
                            do
                            {
                                subtype = subtype*10+parseDigit(packet[pos++]);
                            }
                            while (packet[pos] != ' ');
                            pos++;

                            final String message = new String(packet, pos, packet.length-pos, "UTF-8");

                            final CrossfireCommandDrawextinfoEvent evt = new CrossfireCommandDrawextinfoEvent(this, color, type, subtype, message);
                            for (final CrossfireDrawextinfoListener listener : mylisteners_drawextinfo)
                            {
                                listener.commandDrawextinfoReceived(evt);
                            }
                        }
                        return;

                    case 'i':
                        if (packet[pos++] != 'n') break;
                        if (packet[pos++] != 'f') break;
                        if (packet[pos++] != 'o') break;
                        if (packet[pos++] != ' ') break;
                        {
                            int color = 0;
                            do
                            {
                                color = color*10+parseDigit(packet[pos++]);
                            }
                            while (packet[pos] != ' ');
                            pos++;

                            final String message = new String(packet, pos, packet.length-pos, "UTF-8");

                            final CrossfireCommandDrawinfoEvent evt = new CrossfireCommandDrawinfoEvent(this, message, color);
                            for (final CrossfireDrawinfoListener listener : mylisteners_drawinfo)
                            {
                                listener.commandDrawinfoReceived(evt);
                            }
                        }
                        return;
                    }
                    break;
                }
                break;

            case 'E':
                if (packet[pos++] != 'x') break;
                if (packet[pos++] != 't') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'n') break;
                if (packet[pos++] != 'd') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'd') break;
                switch (packet[pos++])
                {
                case 'I':
                    if (packet[pos++] != 'n') break;
                    if (packet[pos++] != 'f') break;
                    if (packet[pos++] != 'o') break;
                    if (packet[pos++] != 'S') break;
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != ' ') break;
                    do
                    {
                        final int startPos = pos;
                        while (pos < packet.length && packet[pos] != ' ')
                        {
                            pos++;
                        }
                        final String string = new String(packet, startPos, pos-startPos, "UTF-8");
                        // XXX: ExtendedInfoSet command not implemented
                    }
                    while (pos < packet.length);
                    return;

                case 'T':
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 'x') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 'S') break;
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != ' ') break;
                    do
                    {
                        final int startPos = pos;
                        while (pos < packet.length && packet[pos] != ' ')
                        {
                            pos++;
                        }
                        final String type = new String(packet, startPos, pos-startPos, "UTF-8");
                        pos++;
                        // XXX: ExtendedTextSet command not implemented
                    }
                    while (pos < packet.length);
                    return;
                }
                break;

            case 'f':
                if (packet[pos++] != 'a') break;
                if (packet[pos++] != 'c') break;
                if (packet[pos++] != 'e') break;
                switch (packet[pos++])
                {
                case '1':
                    if (packet[pos++] != ' ') break;
                    {
                        final int num = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int checksum = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final String name = new String(packet, pos, packet.length-pos, "UTF-8");
                        Faces.setFace(num, 0, checksum, name);
                    }
                    return;

                case '2':
                    if (packet[pos++] != ' ') break;
                    {
                        final int num = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int setnum = packet[pos++]&0xFF;
                        final int checksum = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final String name = new String(packet, pos, packet.length-pos, "UTF-8");
                        Faces.setFace(num, setnum, checksum, name);
                    }
                    return;
                }
                break;

            case 'g':
                if (packet[pos++] != 'o') break;
                if (packet[pos++] != 'o') break;
                if (packet[pos++] != 'd') break;
                if (packet[pos++] != 'b') break;
                if (packet[pos++] != 'y') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != ' ') break;
                // XXX: goodbye command not implemented
                return;

            case 'i':
                switch (packet[pos++])
                {
                case 'm':
                    if (packet[pos++] != 'a') break;
                    if (packet[pos++] != 'g') break;
                    if (packet[pos++] != 'e') break;
                    switch (packet[pos++])
                    {
                    case ' ':
                        {
                            final int face = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int len = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (pos+len != packet.length) break;
                            final int pixmap = Faces.setImage(face, 0, packet, pos, len);
                            CfMap.updateFace(pixmap);
                        }
                        return;

                    case '2':
                        if (packet[pos++] != ' ') break;
                        {
                            final int face = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int set = packet[pos++]&0xFF;
                            final int len = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (pos+len != packet.length) break;
                            final int pixmap = Faces.setImage(face, set, packet, pos, len);
                            CfMap.updateFace(pixmap);
                        }
                        return;
                    }
                    break;

                case 't':
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 'm') break;
                    switch (packet[pos++])
                    {
                    case ' ':
                        dis = new DataInputStream(new ByteArrayInputStream(packet, pos, packet.length-pos));
                        cmd_item(dis);
                        return;

                    case '1':
                        if (packet[pos++] != ' ') break;
                        dis = new DataInputStream(new ByteArrayInputStream(packet, pos, packet.length-pos));
                        ItemsList.addItems(dis);
                        return;

                    case '2':
                        if (packet[pos++] != ' ') break;
                        {
                            final int location = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            while (pos < packet.length)
                            {
                                final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int flags = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int weight = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int face = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int nameLength = packet[pos++]&0xFF;
                                final String[] names = new String(packet, pos, nameLength, "UTF-8").split("\0", 2);
                                pos += nameLength;
                                final String name = names[0];
                                final String namePl = names[names.length-1];
                                final int anim = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int animSpeed = packet[pos++]&0xFF;
                                final int nrof = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int type = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                ItemsList.addItem2(location, tag, flags, weight, face, name, namePl, anim, animSpeed, nrof, type);
                            }
                            ItemsList.addItems2Processed();
                        }
                        return;
                    }
                    break;
                }
                break;

            case 'm':
                if (packet[pos++] != 'a') break;
                switch (packet[pos++])
                {
                case 'g':
                    if (packet[pos++] != 'i') break;
                    if (packet[pos++] != 'c') break;
                    if (packet[pos++] != 'm') break;
                    if (packet[pos++] != 'a') break;
                    if (packet[pos++] != 'p') break;
                    if (packet[pos++] != ' ') break;
                    dis = new DataInputStream(new ByteArrayInputStream(packet, pos, packet.length-pos));
                    CfMagicMap.magicmap(dis);
                    return;

                case 'p':
                    switch (packet[pos++])
                    {
                    case '1':
                        if (packet[pos++] != ' ') break;
                        CfMap.map1(new DataInputStream(new ByteArrayInputStream(packet, pos, packet.length-pos)));
                        return;

                    case '_':
                        if (packet[pos++] != 's') break;
                        if (packet[pos++] != 'c') break;
                        if (packet[pos++] != 'r') break;
                        if (packet[pos++] != 'o') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != ' ') break;
                        {
                            final boolean negateDx = packet[pos] == '-';
                            if (negateDx)
                            {
                                pos++;
                            }
                            int dx = 0;
                            do
                            {
                                dx = dx*10+parseDigit(packet[pos++]);
                            }
                            while (packet[pos] != ' ');
                            pos++;
                            if (negateDx)
                            {
                                dx = -dx;
                            }

                            final boolean negateDy = packet[pos] == '-';
                            if (negateDy)
                            {
                                pos++;
                            }
                            int dy = 0;
                            do
                            {
                                dy = dy*10+parseDigit(packet[pos++]);
                            }
                            while (pos < packet.length);
                            if (negateDy)
                            {
                                dy = -dy;
                            }

                            CfMap.scroll(dx, dy);
                        }
                        return;

                    case 'e':
                        if (packet[pos++] != 'x') break;
                        if (packet[pos++] != 't') break;
                        if (packet[pos++] != 'e') break;
                        if (packet[pos++] != 'n') break;
                        if (packet[pos++] != 'd') break;
                        if (packet[pos++] != 'e') break;
                        if (packet[pos++] != 'd') break;
                        if (packet[pos++] != ' ') break;
                        dis = new DataInputStream(new ByteArrayInputStream(packet, pos, packet.length-pos));
                        cmd_mapextended(dis);
                        return;
                    }
                    break;
                }
                break;

            case 'n':
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'w') break;
                if (packet[pos++] != 'm') break;
                if (packet[pos++] != 'a') break;
                if (packet[pos++] != 'p') break;
                if (pos != packet.length) break;
                CfMap.newMap(this);
                return;

            case 'p':
                if (packet[pos++] != 'l') break;
                if (packet[pos++] != 'a') break;
                if (packet[pos++] != 'y') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'r') break;
                if (packet[pos++] != ' ') break;
                {
                    final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final int weight = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final int face = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final int nameLength = packet[pos++]&0xFF;
                    final String name = new String(packet, pos, nameLength, "UTF-8");
                    pos += nameLength;
                    if(pos != packet.length) break;
                    ItemsList.createPlayer(tag, weight, face, name);
                }
                return;

            case 'q':
                if (packet[pos++] != 'u') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'r') break;
                if (packet[pos++] != 'y') break;
                if (packet[pos++] != ' ') break;
                {
                    int flags = 0;
                    do
                    {
                        flags = flags*10+parseDigit(packet[pos++]);
                    }
                    while (packet[pos] != ' ');
                    pos++;

                    final String text = new String(packet, pos, packet.length-pos, "UTF-8");

                    setStatus(STATUS_QUERY);
                    final CrossfireCommandQueryEvent evt = new CrossfireCommandQueryEvent(this, text, flags);
                    for (final CrossfireQueryListener listener : mylisteners_query)
                    {
                        listener.commandQueryReceived(evt);
                    }
                }
                return;

            case 'r':
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'p') break;
                if (packet[pos++] != 'l') break;
                if (packet[pos++] != 'y') break;
                if (packet[pos++] != 'i') break;
                if (packet[pos++] != 'n') break;
                if (packet[pos++] != 'f') break;
                if (packet[pos++] != 'o') break;
                if (packet[pos++] != ' ') break;
                {
                    final int startPos = pos;
                    while (packet[pos] != '\n')
                    {
                        pos++;
                    }
                    final String infoType = new String(packet, startPos, pos-startPos, "UTF-8");
                    cmd_replyinfo(infoType, packet, pos+1);
                }
                return;

            case 's':
                switch (packet[pos++])
                {
                case 'e':
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 'u') break;
                    if (packet[pos++] != 'p') break;
                    if (packet[pos++] != ' ') break;
                    final List<String> options = new ArrayList<String>();
                    while (pos < packet.length)
                    {
                        final int startPos = pos;
                        while (pos < packet.length && packet[pos] != ' ')
                        {
                            pos++;
                        }
                        options.add(new String(packet, startPos, pos-startPos, "UTF-8"));
                        if (pos < packet.length)
                        {
                            pos++;
                        }
                    }
                    if (options.size()%2 != 0)
                    {
                        throw new UnknownCommandException("odd number of arguments in setup command");
                    }
                    cmd_setup(options);
                    return;

                case 'm':
                    if (packet[pos++] != 'o') break;
                    if (packet[pos++] != 'o') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 'h') break;
                    if (packet[pos++] != ' ') break;
                    {
                        final int facenbr = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int smoothpic = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        if (pos != packet.length) break;
                        // XXX: smooth command not implemented
                    }
                    return;

                case 'o':
                    if (packet[pos++] != 'u') break;
                    if (packet[pos++] != 'n') break;
                    if (packet[pos++] != 'd') break;
                    if (packet[pos++] != ' ') break;
                    {
                        final int x = packet[pos++];
                        final int y = packet[pos++];
                        final int num = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int type = packet[pos++];
                        if (pos != packet.length) break;
                        // sound command not implemented
                    }
                    return;

                case 't':
                    if (packet[pos++] != 'a') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 's') break;
                    if (packet[pos++] != ' ') break;
                    final Stats stats = CfPlayer.getStats();
                    while (pos < packet.length)
                    {
                        final int stat = packet[pos++]&0xFF;
                        switch (stat)
                        {
                        case Stats.CS_STAT_EXP:
                            final int experience1 = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            stats.setExperience(experience1);
                            break;

                        case Stats.CS_STAT_SPEED:
                            final int speed = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            stats.setStat(stat, speed);
                            break;

                        case Stats.CS_STAT_WEAP_SP:
                            final int weaponSpeed = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            stats.setStat(stat, weaponSpeed);
                            break;

                        case Stats.CS_STAT_RANGE:
                            final int rangeLength = packet[pos++]&0xFF;
                            final String range = new String(packet, pos, rangeLength, "UTF-8");
                            pos += rangeLength;
                            stats.setRange(range);
                            break;

                        case Stats.CS_STAT_TITLE:
                            final int titleLength = packet[pos++]&0xFF;
                            final String title = new String(packet, pos, titleLength, "UTF-8");
                            pos += titleLength;
                            stats.setTitle(title);
                            break;

                        case Stats.CS_STAT_WEIGHT_LIM:
                            final int weightLimit = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            stats.setStat(Stats.CS_STAT_WEIGHT_LIM, weightLimit);
                            break;

                        case Stats.CS_STAT_EXP64:
                            final long experience2 = ((long)(packet[pos++]&0xFF)<<56)|((long)(packet[pos++]&0xFF)<<48)|((long)(packet[pos++]&0xFF)<<40)|(long)((packet[pos++]&0xFF)<<32)|((long)(packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            stats.setExperience(experience2);
                            break;

                        case Stats.CS_STAT_HP:
                        case Stats.CS_STAT_MAXHP:
                        case Stats.CS_STAT_SP:
                        case Stats.CS_STAT_MAXSP:
                        case Stats.CS_STAT_STR:
                        case Stats.CS_STAT_INT:
                        case Stats.CS_STAT_WIS:
                        case Stats.CS_STAT_DEX:
                        case Stats.CS_STAT_CON:
                        case Stats.CS_STAT_CHA:
                        case Stats.CS_STAT_LEVEL:
                        case Stats.CS_STAT_WC:
                        case Stats.CS_STAT_AC:
                        case Stats.CS_STAT_DAM:
                        case Stats.CS_STAT_ARMOUR:
                        case Stats.CS_STAT_FOOD:
                        case Stats.CS_STAT_POW:
                        case Stats.CS_STAT_GRACE:
                        case Stats.CS_STAT_MAXGRACE:
                        case Stats.CS_STAT_FLAGS:
                            final int value = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            stats.setStat(stat, value);
                            break;

                        case Stats.CS_STAT_SPELL_ATTUNE:
                        case Stats.CS_STAT_SPELL_REPEL:
                        case Stats.CS_STAT_SPELL_DENY:
                            final int spellPath = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            // TODO: set spell paths
                            break;

                        default:
                            if (Stats.CS_STAT_RESIST_START <= stat && stat < Stats.CS_STAT_RESIST_START+Stats.RESIST_TYPES)
                            {
                                final int resist = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                stats.setStat(stat, resist);
                            }
                            else if (Stats.CS_STAT_SKILLINFO <= stat && stat < Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS)
                            {
                                final int level = packet[pos++]&0xFF;
                                final long experience3 = ((long)(packet[pos++]&0xFF)<<56)|((long)(packet[pos++]&0xFF)<<48)|((long)(packet[pos++]&0xFF)<<40)|(long)((packet[pos++]&0xFF)<<32)|((long)(packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final Skill sk = Stats.getSkill(stat);
                                if (sk == null)
                                {
                                    System.err.println("ignoring skill value for unknown skill "+stat);
                                }
                                else
                                {
                                    sk.setLevel(level);
                                    sk.setExperience(experience3);
                                }
                            }
                            else
                            {
                                throw new IOException("unknown stat value: "+stat);
                            }
                            break;
                        }
                    }
                    if (pos > packet.length) break;
                    CfPlayer.setStatsProcessed();
                    return;
                }
                break;

            case 'u':
                if (packet[pos++] != 'p') break;
                if (packet[pos++] != 'd') break;
                switch (packet[pos++])
                {
                case 'i':
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 'm') break;
                    if (packet[pos++] != ' ') break;
                    {
                        final int flags = packet[pos++]&0xFF;
                        final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int valFlags = (flags&CfItem.UPD_FLAGS) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valWeight = (flags&CfItem.UPD_WEIGHT) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valFace = (flags&CfItem.UPD_FACE) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final String valName;
                        final String valNamePl;
                        if ((flags&CfItem.UPD_NAME) != 0)
                        {
                            final int nameLength = packet[pos++]&0xFF;
                            int namePlIndex = 0;
                            while (namePlIndex < nameLength && packet[namePlIndex] != 0)
                            {
                                namePlIndex++;
                            }
                            valName = new String(packet, pos, namePlIndex, "UTF-8");
                            valNamePl = namePlIndex+1 < nameLength ? new String(packet, namePlIndex+1, nameLength-(namePlIndex+1), "UTF-8") : valName;
                            pos += nameLength;
                        }
                        else
                        {
                            valName = "";
                            valNamePl = "";
                        }
                        final int valAnim = (flags&CfItem.UPD_ANIM) != 0 ? ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valAnimSpeed = (flags&CfItem.UPD_ANIM) != 0 ? packet[pos++]&0xFF : 0;
                        final int valNrof = (flags&CfItem.UPD_NROF) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        if(pos != packet.length) break;
                        ItemsList.updateItem(flags, tag, valFlags, valWeight, valFace, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
                    }
                    return;

                case 's':
                    if (packet[pos++] != 'p') break;
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 'l') break;
                    if (packet[pos++] != 'l') break;
                    if (packet[pos++] != ' ') break;
                    {
                        final int flags = packet[pos++]&0xFF;
                        final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int mana = (flags&UPD_SP_MANA) != 0 ? ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int grace = (flags&UPD_SP_GRACE) != 0 ? ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int damage = (flags&UPD_SP_DAMAGE) != 0 ? ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        if(pos != packet.length) break;
                        ItemsList.updateSpell(flags, tag, mana, grace, damage);
                    }
                    return;
                }
                break;

            case 'v':
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'r') break;
                if (packet[pos++] != 's') break;
                if (packet[pos++] != 'i') break;
                if (packet[pos++] != 'o') break;
                if (packet[pos++] != 'n') break;
                if (packet[pos++] != ' ') break;
                {
                    int csval = 0;
                    do
                    {
                        csval = csval*10+parseDigit(packet[pos++]);
                    }
                    while (packet[pos] != ' ');
                    pos++;

                    int scval = 0;
                    do
                    {
                        scval = scval*10+parseDigit(packet[pos++]);
                    }
                    while (packet[pos] != ' ');
                    pos++;

                    final String vinfo = new String(packet, pos, packet.length-pos, "UTF-8");

                    cmd_version(csval, scval, vinfo);
                }
                return;
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            // ignore
        }
        catch (final StringIndexOutOfBoundsException ex)
        {
            // ignore
        }

        int cmdlen;
        for (cmdlen = 0; cmdlen < packet.length; cmdlen++)
        {
            if ((packet[cmdlen]&0xFF) <= 0x20 || (packet[cmdlen]&0xFF) >= 0x80)
            {
                break;
            }
        }
        throw new UnknownCommandException("Cannot parse command: "+new String(packet, 0, cmdlen, "UTF-8"));
    }

    /**
     * Handles the item server to client command.
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_item(DataInputStream dis) throws IOException
    {
        // XXX: "item" command not yet implemented
    }

    /**
     * Handles the version server to client command.
     * @param csval The client version.
     * @param scval The server version.
     * @param vinfo The version information string.
     * @since 1.0
     */
    private void cmd_version(final int csval, final int scval, final String vinfo) throws IOException
    {
        sendVersion(1023, 1027, "JXClient Java Client Pegasus 0.1");
        sendToggleextendedtext(1, 2, 3, 4, 5, 6, 7);
        sendSetup(
            "sound 0",
            "exp64 1",
            "map1cmd 1",
            "darkness 1",
            "newmapcmd 1",
            "facecache 1",
            "extendedTextInfos 1",
            "itemcmd 2",
            "spellmon 1",
            "mapsize "+MAP_WIDTH+"x"+MAP_HEIGHT);
        sendRequestinfo("image_info");
        sendRequestinfo("skill_info");
        sendToggleextendedtext(1);
    }

    /**
     * Handles the replyinfo server to client command.
     * @param infoType The info_type parameter.
     * @param packet The packet payload data.
     * @param pos The starting offset into <code>packet</code> where the
     * parameters of <code>infoType</code>'s parameter start.
     * @since 1.0
     */
    void cmd_replyinfo(final String infoType, final byte[] packet, final int pos) throws IOException
    {
        BufferedReader d = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet, pos, packet.length-pos)));
        if (infoType.equals("image_info"))
        {
            int nrpics = Integer.parseInt(d.readLine());
            sendAddme();
        }
        else if (infoType.equals("skill_info"))
        {
            String r = "";
            while(r!=null)
            {
                r = d.readLine();
                if (r!=null)
                {
                    String[] sk = r.split(":");
                    final int skillId = Integer.parseInt(sk[0]);
                    if (skillId < Stats.CS_STAT_SKILLINFO || skillId >= Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS)
                    {
                        System.err.println("Ignoring skill definition for invalid skill id "+skillId+".");
                    }
                    else
                    {
                        Stats.addSkill(skillId, sk[1]);
                    }
                }
            }
        }
        else
        {
            System.err.println("Ignoring unexpected replyinfo type '"+infoType+"'.");
        }
    }

    /**
     * Handles the setup server to client command.
     * @param options The option/value pairs.
     * @since 1.0
     */
    void cmd_setup(final List<String> options) throws IOException
    {
        for (int i = 0; i+1 < options.size(); i += 2)
        {
            final String option = options.get(i);
            final String value = options.get(i+1);
            if (option.equals("spellmon"))
            {
                if (value.equals("1"))
                {
                    ItemsList.setSpellMode(ItemsList.SPELLMODE_SENT);
                }
                else
                {
                    ItemsList.setSpellMode(ItemsList.SPELLMODE_LOCAL);
                }
            }
            else if (option.equals("sound"))
            {
                // XXX: record setting: enabled iff value.equals("1")
            }
            else if (option.equals("exp64"))
            {
                // Ignored since it only enables additional/improved stat
                // commands but the old version is also supported.
            }
            else if (option.equals("newmapcmd"))
            {
                if (!value.equals("1"))
                {
                    System.err.println("Error: the server is too old for this client since it does not support the newmapcmd=1 setup option.");
                    System.exit(1);
                }
            }
            else if (option.equals("facecache"))
            {
                if (!value.equals("1"))
                {
                    System.err.println("Error: the server is too old for this client since it does not support the facecache=1 setup option.");
                    System.exit(1);
                }
            }
            else if (option.equals("extendedTextInfos"))
            {
                if (!value.equals("1"))
                {
                    System.err.println("Error: the server is too old for this client since it does not support the extendedTextInfos=1 setup option.");
                    System.exit(1);
                }
            }
            else if (option.equals("itemcmd"))
            {
                if (!value.equals("2"))
                {
                    System.err.println("Error: the server is too old for this client since it does not support the itemcmd=2 setup option.");
                    System.exit(1);
                }
            }
            else if (option.equals("mapsize"))
            {
                if (!value.equals(MAP_WIDTH+"x"+MAP_HEIGHT))
                {
                    System.err.println("Error: the server is not suitable for this client since it does not support a map size of "+MAP_WIDTH+"x"+MAP_HEIGHT+".");
                    System.exit(1);
                }
            }
            else if (option.equals("map1cmd"))
            {
                if (!value.equals("1"))
                {
                    System.err.println("Error: the server is too old for this client since it does not support the map1cmd=1 setup option.");
                    System.exit(1);
                }
            }
            else if (option.equals("darkness"))
            {
                // do not care
            }
            else
            {
                System.err.println("Warning: got unknown option from server: "+option+"="+value);
            }
        }
    }

    /**
     * Handles the MapExtended server to client command.
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_mapextended(DataInputStream dis) throws IOException
    {
        // XXX: "MapExtended" command not yet implemented
    }

    /**
     * Returns the list of all items at the given location.
     * Usually, this is either an inventory content, or the list of objects on
     * the floor.
     * @param location The object tag identifier of the location to get items from.
     * @return Known items, as a List object.
     * @since 1.0
     */
    public List<CfItem> getItems(int location)
    {
        return ItemsList.getItems(location);
    }

    /**
     * Returns the current player.
     * @return The current player, as a CfPlayer object.
     * @since 1.0
     */
    public CfPlayer getPlayer()
    {
        return ItemsList.getPlayer();
    }
    public void drawInfo(String msg, int col)
    {
        CrossfireCommandDrawinfoEvent evt = new
                CrossfireCommandDrawinfoEvent(this,msg,col);
        Iterator<CrossfireDrawinfoListener> it = mylisteners_drawinfo.iterator();
        while (it.hasNext())
        {
            it.next().commandDrawinfoReceived(evt);
        }
    }

    /**
     * Send an "addme" command to the server.
     */
    public void sendAddme() throws IOException
    {
        writePacket(addmePrefix, addmePrefix.length);
    }

    /**
     * Send an "apply" command to the server.
     *
     * @param tag the item to apply
     */
    public void sendApply(final int tag) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(applyPrefix);
            putDecimal(tag);
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send an "askface" command to the server.
     *
     * @param num the face to query
     */
    public void sendAskface(final int num) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(askfacePrefix);
            putDecimal(num);
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send an "examine" command to the server.
     *
     * @param tag the item to examine
     */
    public void sendExamine(final int tag) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(examinePrefix);
            putDecimal(tag);
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "lock" command to the server.
     *
     * @param val whether to lock the item
     *
     * @param tag the item to lock
     */
    public void sendLock(final boolean val, final int tag) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(lockPrefix);
            byteBuffer.put((byte)(val ? 1 : 0));
            byteBuffer.putInt(tag);
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "lookat" command to the server.
     *
     * @param dx The x-coordinate in tiles, relative to the player.
     *
     * @param dy The y-coordinate in tiles, relative to the player.
     */
    public void sendLookat(final int dx, final int dy) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(lookatPrefix);
            putDecimal(dx);
            byteBuffer.put((byte)' ');
            putDecimal(dy);
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "mapredraw" command to the server.
     */
    public void sendMapredraw() throws IOException
    {
        writePacket(mapredrawPrefix, mapredrawPrefix.length);
    }

    /**
     * Send a "mark" command to the server.
     *
     * @param tag the item to mark
     */
    public void sendMark(final int tag) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(markPrefix);
            byteBuffer.putInt(tag);
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "move" command to the server.
     *
     * @param to the destination location
     *
     * @param tag the item to move
     *
     * @param nrof the number of items to move
     */
    public void sendMove(final int to, final int tag, final int nrof) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(movePrefix);
            putDecimal(to);
            byteBuffer.put((byte)' ');
            putDecimal(tag);
            byteBuffer.put((byte)' ');
            putDecimal(nrof);
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "ncom" command to the server.
     *
     * @param repeat the repeat count
     *
     * @param command the command
     *
     * @return the packet id
     */
    public int sendNcom(final int repeat, final String command) throws IOException
    {
        final int thisPacket;
        synchronized(writeBuffer)
        {
            thisPacket = packet++&0x00FF;
            byteBuffer.clear();
            byteBuffer.put(ncomPrefix);
            byteBuffer.putShort((short)thisPacket);
            byteBuffer.putInt(repeat);
            byteBuffer.put(command.getBytes("UTF-8"));
            writePacket(writeBuffer, byteBuffer.position());
        }
        return thisPacket;
    }

    /**
     * Send a "reply" command to the server.
     *
     * @param text the text to reply
     */
    public void sendReply(final String text) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(replyPrefix);
            byteBuffer.put(text.getBytes("UTF-8"));
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "requestinfo" command to the server.
     *
     * @param infoType the info type to request
     */
    public void sendRequestinfo(final String infoType) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(requestinfoPrefix);
            byteBuffer.put(infoType.getBytes("UTF-8"));
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "setup" command to the server.
     *
     * @param options... the option/value pairs to send
     */
    public void sendSetup(final String... options) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(setupPrefix);
            if (options.length <= 0)
            {
                byteBuffer.put((byte)' ');
            }
            else
            {
                for (final String option : options)
                {
                    byteBuffer.put((byte)' ');
                    byteBuffer.put(option.getBytes("UTF-8"));
                }
            }
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "toggleextendedtext" command to the server.
     *
     * @param types... the types to request
     */
    public void sendToggleextendedtext(final int... types) throws IOException
    {
        if (types.length <= 0)
        {
            return;
        }

        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(toggleextendedtextPrefix);
            for (final int type : types)
            {
                byteBuffer.put((byte)' ');
                putDecimal(type);
            }
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "version" command to the server.
     *
     * @param csval the client version number
     *
     * @param scval the server version number
     *
     * @param vinfo the client identification string
     */
    public void sendVersion(final int csval, final int scval, final String vinfo) throws IOException
    {
        synchronized(writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(versionPrefix);
            putDecimal(csval);
            byteBuffer.put((byte)' ');
            putDecimal(scval);
            byteBuffer.put((byte)' ');
            byteBuffer.put(vinfo.getBytes("UTF-8"));
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Append an integer in decimal ASCII representation to {@link
     * #byteBuffer}.
     *
     * @param value the value to append
     *
     * @throws IOException will never be thrown
     */
    private void putDecimal(final int value) throws IOException
    {
        if (value == 0)
        {
            byteBuffer.put((byte)'0');
        }
        else
        {
            final String str = Integer.toString(value);
            byteBuffer.put(str.getBytes("ISO-8859-1"));
        }
    }

    /**
     * Parse a character into an integer.
     *
     * @param ch the character to parse
     *
     * @return the integer representing the character
     *
     * @throws UnknownCommandException if <code>ch</code> is not a digit
     */
    private static int parseDigit(final byte ch) throws UnknownCommandException
    {
        final int digit = ch-'0';
        if (digit < 0 || digit > 9)
        {
            throw new UnknownCommandException("not a digit: "+ch);
        }
        return digit;
    }
}

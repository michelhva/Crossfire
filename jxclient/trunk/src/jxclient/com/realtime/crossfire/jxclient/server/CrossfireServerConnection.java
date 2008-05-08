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

import com.realtime.crossfire.jxclient.ExperienceTable;
import com.realtime.crossfire.jxclient.animations.Animation;
import com.realtime.crossfire.jxclient.animations.Animations;
import com.realtime.crossfire.jxclient.faces.Faces;
import com.realtime.crossfire.jxclient.faces.FacesCallback;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.HexCodec;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Adds encoding/decoding of crossfire protocol packets to a {@link
 * ServerConnection}.
 *
 * @author Andreas Kirschbaum
 */
public class CrossfireServerConnection extends ServerConnection implements FacesCallback
{
    /**
     * Pattern to split a string by ":".
     */
    private static final Pattern patternDot = Pattern.compile(":");

    private static final Charset utf8 = Charset.forName("UTF-8");

    /**
     * The {@link ItemsManager} instance to update.
     */
    private final ItemsManager itemsManager;

    /**
     * The {@link SpellsManager} instance to update.
     */
    private final SpellsManager spellsManager;

    /**
     * The {@link Stats} instance to update.
     */
    private final Stats stats;

    /**
     * The {@link Faces} instance to update.
     */
    private final Faces faces;

    /**
     * The map width in tiles that is negotiated with the server.
     */
    private int mapWidth = 17;

    /**
     * The map height in tiles that is negotiated with the server.
     */
    private int mapHeight = 13;

    /**
     * The {@link MapSizeListener}s to be notified.
     */
    private final List<MapSizeListener> mapSizeListeners = new ArrayList<MapSizeListener>();

    private final List<CrossfireDrawinfoListener> drawinfoListeners = new ArrayList<CrossfireDrawinfoListener>();

    private final List<CrossfireDrawextinfoListener> drawextinfoListeners = new ArrayList<CrossfireDrawextinfoListener>();

    private final List<CrossfireQueryListener> queryListeners = new ArrayList<CrossfireQueryListener>();

    /**
     * The {@link CrossfireMagicmapListener}s to be notified of received
     * magicmap commands.
     */
    private final List<CrossfireMagicmapListener> magicmapListeners = new ArrayList<CrossfireMagicmapListener>();

    /**
     * The {@link CrossfireUpdateFaceListener}s to be notified.
     */
    private final List<CrossfireUpdateFaceListener> crossfireUpdateFaceListeners = new ArrayList<CrossfireUpdateFaceListener>();

    /**
     * The {@link CrossfireSoundListener}s to be notified.
     */
    private final List<CrossfireSoundListener> crossfireSoundListeners = new ArrayList<CrossfireSoundListener>();

    /**
     * The {@link CrossfireMusicListener}s to be notified.
     */
    private final List<CrossfireMusicListener> crossfireMusicListeners = new ArrayList<CrossfireMusicListener>();

    /**
     * The {@link CrossfireComcListener}s to be notified.
     */
    private final List<CrossfireComcListener> crossfireComcListeners = new ArrayList<CrossfireComcListener>();

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

    /**
     * The semaphore used to synchronized map model updates and map view
     * redraws.
     */
    private final Object redrawSemaphore;

    /** The global experience table. */
    private final ExperienceTable experienceTable;

    /**
     * The defined animations.
     */
    private final Animations animations;

    /**
     * The appender to write protocol commands to. May be <code>null</code> to
     * not write anything.
     */
    private final Appendable debugProtocol;

    /**
     * A formatter for timestamps.
     */
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");

    /**
     * Create a new instance.
     *
     * @param redrawSemaphore The semaphore used to synchronized map model
     * updates and map view redraws.
     *
     * @param experienceTable The experience table instance to update.
     *
     * @param animations The animations to update.
     *
     * @param debugProtocol If non-<code>null</code>, write all protocol
     * commands to this appender.
     *
     * @param itemsManager the instance to update
     *
     * @param spellsManager the instance to update 
     *
     * @param stats the instance to update
     *
     * @param faces the instance to update
     */
    public CrossfireServerConnection(final Object redrawSemaphore, final ExperienceTable experienceTable, final Animations animations, final Appendable debugProtocol, final ItemsManager itemsManager, final SpellsManager spellsManager, final Stats stats, final Faces faces)
    {
        this.itemsManager = itemsManager;
        this.spellsManager = spellsManager;
        this.stats = stats;
        this.faces = faces;
        this.redrawSemaphore = redrawSemaphore;
        this.experienceTable = experienceTable;
        this.animations = animations;
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        this.debugProtocol = debugProtocol;
    }

    /**
     * Adds a listener to be notified about map size changes.
     * @param listener the listener to add
     */
    public synchronized void addMapSizeListener(final MapSizeListener listener)
    {
        mapSizeListeners.add(listener);
    }

    /**
     * Removes a listener to be notified about map size changes.
     * @param listener the listener to remove
     */
    public synchronized void removeMapSizeListener(final MapSizeListener listener)
    {
        mapSizeListeners.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * drawinfo S-&gt;C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireDrawinfoListener(final CrossfireDrawinfoListener listener)
    {
        drawinfoListeners.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * drawinfo S-&gt;C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireDrawinfoListener(final CrossfireDrawinfoListener listener)
    {
        drawinfoListeners.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * drawextinfo S-&gt;C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireDrawextinfoListener(final CrossfireDrawextinfoListener listener)
    {
        drawextinfoListeners.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * drawextinfo S-&gt;C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireDrawextinfoListener(final CrossfireDrawextinfoListener listener)
    {
        drawextinfoListeners.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * query S-&gt;C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireQueryListener(final CrossfireQueryListener listener)
    {
        queryListeners.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * query S-&gt;C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireQueryListener(final CrossfireQueryListener listener)
    {
        queryListeners.remove(listener);
    }

    /**
     * Adds a listener from the list of objects listening to magicmap messages.
     * @param listener the listener to add
     */
    public void addCrossfireMagicmapListener(final CrossfireMagicmapListener listener)
    {
        magicmapListeners.add(listener);
    }

    /**
     * Removes a listener from the list of objects listening to magicmap
     * messages.
     * @param listener the listener to remove
     */
    public void removeCrossfireMagicmapListener(final CrossfireMagicmapListener listener)
    {
        magicmapListeners.remove(listener);
    }

    /**
     * Add a listener to be notified about face image changes.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireUpdateFaceListener(final CrossfireUpdateFaceListener listener)
    {
        crossfireUpdateFaceListeners.add(listener);
    }

    /**
     * Add a listener to be notified about received sound commands.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireSoundListener(final CrossfireSoundListener listener)
    {
        crossfireSoundListeners.add(listener);
    }

    /**
     * Add a listener to be notified about received music commands.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireMusicListener(final CrossfireMusicListener listener)
    {
        crossfireMusicListeners.add(listener);
    }

    /**
     * Add a listener to be notified about received comc commands.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireComcListener(final CrossfireComcListener listener)
    {
        crossfireComcListeners.add(listener);
    }

    /** {@inheritDoc} */
    // This function does not avoid index out of bounds accesses to the array
    // <code>packet</code>; instead, a try...catch clause is used to detect
    // invalid packets.
    public void processPacket(final byte[] packet, final int start, final int end) throws UnknownCommandException
    {
        try
        {
            final DataInputStream dis;
            int pos = start;
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
                            if (pos != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv addme_failed\n");
                            }
                            // XXX: addme_failed command not implemented
                            return;

                        case 's':
                            if (packet[pos++] != 'u') break;
                            if (packet[pos++] != 'c') break;
                            if (packet[pos++] != 'c') break;
                            if (packet[pos++] != 'e') break;
                            if (packet[pos++] != 's') break;
                            if (packet[pos++] != 's') break;
                            if (pos != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv addme_success\n");
                            }
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
                        while (pos < end)
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
                            final String name = new String(packet, pos, nameLength, utf8);
                            pos += nameLength;
                            final int messageLength = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final String message = new String(packet, pos, messageLength, utf8);
                            pos += messageLength;
                            if (pos > end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv addspell tag="+tag+" lvl="+level+" time="+castingTime+" sp="+mana+" gr="+grace+" dam="+damage+" skill="+skill+" path="+path+" face="+face+" name="+name+" msg="+message+"\n");
                            }
                            spellsManager.addSpell(tag, level, castingTime, mana, grace, damage, skill, path, face, name, message);
                        }
                        if (pos != end) break;
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
                        final int[] faces = new int[(end-pos)/2];
                        if (faces.length <= 0) throw new UnknownCommandException("no faces in anim command");
                        for (int i = 0; i < faces.length; i++)
                        {
                            faces[i] = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        }
                        if (pos != end) break;
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv anim num="+num+" flags="+flags+" faces="+Arrays.toString(faces)+"\n");
                        }
                        if ((num&~0x1FFF) != 0) throw new UnknownCommandException("invalid animation id "+num);
                        animations.addAnimation(num&0x1FFF, flags, faces);
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
                    if (pos != end) break;
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv comc no="+packetNo+" time="+time+"\n");
                    }
                    for (final CrossfireComcListener listener : crossfireComcListeners)
                    {
                        listener.commandComcReceived(packetNo, time);
                    }
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
                                while (pos < end);
                                if (pos != end) break;
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv delinv tag="+tag+"\n");
                                }
                                itemsManager.cleanInventory(tag);
                            }
                            return;

                        case 't':
                            if (packet[pos++] != 'e') break;
                            if (packet[pos++] != 'm') break;
                            if (packet[pos++] != ' ') break;
                            {
                                final int[] tags = new int[(end-pos)/4];
                                for (int i = 0; i < tags.length; i++)
                                {
                                    tags[i] = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                }
                                if (pos != end) break;
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv delitem tags="+Arrays.toString(tags)+"\n");
                                }
                                itemsManager.removeItems(tags);
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
                            if (pos != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv delspell tag="+tag+"\n");
                            }
                            spellsManager.deleteSpell(tag);
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

                            final String message = new String(packet, pos, end-pos, utf8);

                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv drawextinfo color="+color+" type="+type+"/"+subtype+" msg="+message+"\n");
                            }

                            final CrossfireCommandDrawextinfoEvent evt = new CrossfireCommandDrawextinfoEvent(this, color, type, subtype, message);
                            for (final CrossfireDrawextinfoListener listener : drawextinfoListeners)
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

                            final String message = new String(packet, pos, end-pos, utf8);

                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv drawinfo color="+color+" msg="+message+"\n");
                            }

                            drawInfo(message, color);
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
                        while (pos < end && packet[pos] != ' ')
                        {
                            pos++;
                        }
                        final String string = new String(packet, startPos, pos-startPos, utf8);
                        pos++;
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv ExtendedInfoSet "+string+"\n");
                        }
                        // XXX: ExtendedInfoSet command not implemented
                    }
                    while (pos < end);
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
                        while (pos < end && packet[pos] != ' ')
                        {
                            pos++;
                        }
                        final String type = new String(packet, startPos, pos-startPos, utf8);
                        pos++;
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv ExtendedTextSet "+type+"\n");
                        }
                        // XXX: ExtendedTextSet command not implemented
                    }
                    while (pos < end);
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
                        final String name = new String(packet, pos, end-pos, utf8);
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv face1 num="+num+" checksum="+checksum+" name="+name+"\n");
                        }
                        faces.setFace(num, 0, name);
                    }
                    return;

                case '2':
                    if (packet[pos++] != ' ') break;
                    {
                        final int num = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int setnum = packet[pos++]&0xFF;
                        final int checksum = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final String name = new String(packet, pos, end-pos, utf8).intern();
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv face2 num="+num+" set="+setnum+" checksum="+checksum+" name="+name+"\n");
                        }
                        faces.setFace(num, setnum, name);
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
                if (debugProtocol != null)
                {
                    debugProtocolWrite("recv goodbye\n");
                }
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
                            if (pos+len != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv image face="+face+" len="+len+"\n");
                            }
                            final int pixmap = faces.setImage(face, packet, pos, len);
                            CfMapUpdater.updateFace(pixmap);
                            for (final CrossfireUpdateFaceListener listener : crossfireUpdateFaceListeners)
                            {
                                listener.updateFace(pixmap);
                            }
                        }
                        return;

                    case '2':
                        if (packet[pos++] != ' ') break;
                        {
                            final int face = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int set = packet[pos++]&0xFF;
                            final int len = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (pos+len != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv image2 face="+face+" set="+set+" len="+len+"\n");
                            }
                            final int pixmap = faces.setImage(face, packet, pos, len);
                            CfMapUpdater.updateFace(pixmap);
                            for (final CrossfireUpdateFaceListener listener : crossfireUpdateFaceListeners)
                            {
                                listener.updateFace(pixmap);
                            }
                        }
                        return;
                    }
                    break;

                case 't':
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 'm') break;
                    switch (packet[pos++])
                    {
                    case '1':
                        if (packet[pos++] != ' ') break;
                        dis = new DataInputStream(new ByteArrayInputStream(packet, pos, end-pos));
                        try
                        {
                            final int len = dis.available();
                            int pos2 = 0;
                            final int location = dis.readInt();
                            pos2 += 4;
                            while (pos2 < len)
                            {
                                final int tag = dis.readInt();
                                final int flags = dis.readInt();
                                final int weight = dis.readInt();
                                final int faceid = dis.readInt();
                                final int namelength = dis.readUnsignedByte();
                                pos2 += 17;
                                final byte[] buf = new byte[namelength];
                                dis.readFully(buf);
                                final String[] names = new String(buf).split("\0", 2);
                                final String name = names[0];
                                final String namePl = names[names.length >= 2 ? 1 : 0];
                                pos2 += namelength;
                                final int anim = dis.readUnsignedShort();
                                final int animSpeed = dis.readUnsignedByte();
                                final int nrof = dis.readInt();
                                pos2 += 7;
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv item1 tag="+tag+" flags="+flags+" weight="+weight+" face="+faceid+" name="+name+" name_pl="+namePl+" anim="+anim+" anim_speed="+animSpeed+" nrof="+nrof+"\n");
                                }
                                final CfItem item = new CfItem(location, tag, flags, weight, faces.getFace(faceid), name, namePl, anim, animSpeed, nrof);
                                itemsManager.addItem(item);
                            }
                            itemsManager.fireEvents();
                        }
                        catch (final IOException ex)
                        {
                            throw new UnknownCommandException("invalid item1 command: "+ex.getMessage());
                        }
                        return;

                    case '2':
                        if (packet[pos++] != ' ') break;
                        {
                            final int location = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            while (pos < end)
                            {
                                final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int flags = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int weight = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int face = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int nameLength = packet[pos++]&0xFF;
                                final String[] names = new String(packet, pos, nameLength, utf8).split("\0", 2);
                                pos += nameLength;
                                final String name = names[0].intern();
                                final String namePl = names.length < 2 ? name : names[1].intern();
                                final int anim = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int animSpeed = packet[pos++]&0xFF;
                                final int nrof = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                final int type = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv item2 tag="+tag+" flags="+flags+" weight="+weight+" face="+face+" name="+name+" name_pl="+namePl+" anim="+anim+" anim_speed="+animSpeed+" nrof="+nrof+" type="+type+"\n");
                                }
                                itemsManager.addItem(new CfItem(location, tag, flags, weight, faces.getFace(face), name, namePl, anim, animSpeed, nrof, type));
                            }
                            itemsManager.fireEvents();
                        }
                        return;
                    }
                    break;
                }
                break;

            case 'm':
                switch (packet[pos++])
                {
                case 'a':
                    switch (packet[pos++])
                    {
                    case 'g':
                        if (packet[pos++] != 'i') break;
                        if (packet[pos++] != 'c') break;
                        if (packet[pos++] != 'm') break;
                        if (packet[pos++] != 'a') break;
                        if (packet[pos++] != 'p') break;
                        if (packet[pos++] != ' ') break;

                        int width = 0;
                        do
                        {
                                width = width*10+parseDigit(packet[pos++]);
                        }
                        while (packet[pos] != ' ');
                        pos++;

                        int height = 0;
                        do
                        {
                                height = height*10+parseDigit(packet[pos++]);
                        }
                        while (packet[pos] != ' ');
                        pos++;

                        int px = 0;
                        do
                        {
                                px = px*10+parseDigit(packet[pos++]);
                        }
                        while (packet[pos] != ' ');
                        pos++;

                        int py = 0;
                        do
                        {
                                py = py*10+parseDigit(packet[pos++]);
                        }
                        while (packet[pos] != ' ');
                        pos++;

                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv magicmap size="+width+"x"+height+" player="+px+"/"+py+" len="+(end-pos)+"\n");
                        }

                        if (end-pos != width*height)
                        {
                            throw new UnknownCommandException("invalid magicmap command");
                        }

                        final CrossfireCommandMagicmapEvent evt = new CrossfireCommandMagicmapEvent(new Object(), width, height, px, py, packet, pos);
                        for (final CrossfireMagicmapListener listener : magicmapListeners)
                        {
                            listener.commandMagicmapReceived(evt);
                        }
                        return;

                    case 'p':
                        switch (packet[pos++])
                        {
                        case '2':
                            if (packet[pos++] != ' ') break;
                            cmd_map2(packet, pos, end);
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
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv mapextended\n");
                            }
                            dis = new DataInputStream(new ByteArrayInputStream(packet, pos, end-pos));
                            cmd_mapextended(dis);
                            return;
                        }
                        break;
                    }
                    break;

                case 'u':
                    if (packet[pos++] != 's') break;
                    if (packet[pos++] != 'i') break;
                    if (packet[pos++] != 'c') break;
                    if (packet[pos++] != ' ') break;
                    final String music = new String(packet, pos, end-pos, utf8);
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv music "+music+"\n");
                    }

                    for (final CrossfireMusicListener listener : crossfireMusicListeners)
                    {
                        listener.commandMusicReceived(music);
                    }
                    return;
                }
                break;

            case 'n':
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'w') break;
                if (packet[pos++] != 'm') break;
                if (packet[pos++] != 'a') break;
                if (packet[pos++] != 'p') break;
                if (pos != end) break;
                if (debugProtocol != null)
                {
                    debugProtocolWrite("recv newmap\n");
                }
                CfMapUpdater.processNewmap(mapWidth, mapHeight);
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
                    final String name = new String(packet, pos, nameLength, utf8);
                    pos += nameLength;
                    if (pos != end) break;
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv player tag="+tag+" weight="+weight+" face="+face+" name="+name+"\n");
                    }
                    stats.resetSkills();
                    itemsManager.setPlayer(new CfPlayer(tag, weight, faces.getFace(face), name));
                    stats.setStat(Stats.C_STAT_WEIGHT, weight);
                    stats.setStatsProcessed(false);
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

                    final String text = new String(packet, pos, end-pos, utf8);

                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv query flags="+flags+" text="+text+"\n");
                    }

                    final CrossfireCommandQueryEvent evt = new CrossfireCommandQueryEvent(this, text, flags);
                    for (final CrossfireQueryListener listener : queryListeners)
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
                    final String infoType = new String(packet, startPos, pos-startPos, utf8);
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv replyinfo type="+infoType+" len="+(end-(pos+1))+"\n");
                    }
                    try
                    {
                        cmd_replyinfo(infoType, packet, pos+1, end);
                    }
                    catch (final IOException ex)
                    {
                        throw new UnknownCommandException("invalid replyinfo command: "+ex.getMessage());
                    }
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
                    while (pos < end)
                    {
                        while (pos < end && packet[pos] == ' ')
                        {
                            pos++;
                        }
                        final int startPos = pos;
                        while (pos < end && packet[pos] != ' ')
                        {
                            pos++;
                        }
                        options.add(new String(packet, startPos, pos-startPos, utf8));
                        if (pos < end)
                        {
                            pos++;
                        }
                    }
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv setup "+options+"\n");
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
                        if (pos != end) break;
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv smooth face="+facenbr+" smoothpic="+smoothpic+"\n");
                        }
                        // XXX: smooth command not implemented
                    }
                    return;

                case 'o':
                    if (packet[pos++] != 'u') break;
                    if (packet[pos++] != 'n') break;
                    if (packet[pos++] != 'd') break;
                    switch (packet[pos++])
                    {
                    case ' ':
                        {
                            final int x = packet[pos++];
                            final int y = packet[pos++];
                            final int num = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int type = packet[pos++];
                            if (pos != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv sound pos="+x+"/"+y+" num="+num+" type="+type+"\n");
                            }

                            for (final CrossfireSoundListener listener : crossfireSoundListeners)
                            {
                                listener.commandSoundReceived(x, y, num, type);
                            }
                        }
                        return;

                    case '2':
                        if (packet[pos++] != ' ') break;
                        {
                            final int x = packet[pos++];
                            final int y = packet[pos++];
                            final int dir = packet[pos++];
                            final int volume = packet[pos++];
                            final int type = packet[pos++];
                            final int actionLength = packet[pos++]&0xFF;
                            final String action = new String(packet, pos, actionLength, utf8);
                            pos += actionLength;
                            final int nameLength = packet[pos++]&0xFF;
                            final String name = new String(packet, pos, nameLength, utf8);
                            pos += nameLength;
                            if (pos != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv sound2 pos="+x+"/"+y+" dir="+dir+" volume="+volume+" type="+type+" action="+action+" name="+name+"\n");
                            }

                            for (final CrossfireSoundListener listener : crossfireSoundListeners)
                            {
                                listener.commandSound2Received(x, y, dir, volume, type, action, name);
                            }
                        }
                        return;
                    }
                    break;

                case 't':
                    if (packet[pos++] != 'a') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 's') break;
                    if (packet[pos++] != ' ') break;
                    while (pos < end)
                    {
                        final int stat = packet[pos++]&0xFF;
                        switch (stat)
                        {
                        case Stats.CS_STAT_EXP:
                            final int experience1 = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF); // XXX: should be 4 byte?
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats exp="+experience1+"\n");
                            }
                            stats.setExperience(experience1);
                            break;

                        case Stats.CS_STAT_SPEED:
                            final int speed = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats speed="+speed+"\n");
                            }
                            stats.setStat(stat, speed);
                            break;

                        case Stats.CS_STAT_WEAP_SP:
                            final int weaponSpeed = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats weapon_speed="+weaponSpeed+"\n");
                            }
                            stats.setStat(stat, weaponSpeed);
                            break;

                        case Stats.CS_STAT_RANGE:
                            final int rangeLength = packet[pos++]&0xFF;
                            final String range = new String(packet, pos, rangeLength, utf8);
                            pos += rangeLength;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats range="+range+"\n");
                            }
                            stats.setRange(range);
                            break;

                        case Stats.CS_STAT_TITLE:
                            final int titleLength = packet[pos++]&0xFF;
                            final String title = new String(packet, pos, titleLength, utf8);
                            pos += titleLength;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats title="+title+"\n");
                            }
                            stats.setTitle(title);
                            break;

                        case Stats.CS_STAT_WEIGHT_LIM:
                            final int weightLimit = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats weight_limit="+weightLimit+"\n");
                            }
                            stats.setStat(Stats.CS_STAT_WEIGHT_LIM, weightLimit);
                            break;

                        case Stats.CS_STAT_EXP64:
                            final long experience2 = ((long)(packet[pos++]&0xFF)<<56)|((long)(packet[pos++]&0xFF)<<48)|((long)(packet[pos++]&0xFF)<<40)|((long)(packet[pos++]&0xFF)<<32)|((long)(packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats exp64="+experience2+"\n");
                            }
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
                            final int statValue = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats stat"+stat+"="+((statValue&0x8000) != 0 ? statValue-0x10000 : statValue)+"\n");
                            }
                            stats.setStat(stat, (statValue&0x8000) != 0 ? statValue-0x10000 : statValue);
                            break;

                        case Stats.CS_STAT_FLAGS:
                            final int value = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats flags="+value+"\n");
                            }
                            stats.setStat(stat, value);
                            break;

                        case Stats.CS_STAT_SPELL_ATTUNE:
                        case Stats.CS_STAT_SPELL_REPEL:
                        case Stats.CS_STAT_SPELL_DENY:
                            final int spellPath = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats spell"+stat+"="+spellPath+"\n");
                            }
                            // TODO: set spell paths
                            break;

                        default:
                            if (Stats.CS_STAT_RESIST_START <= stat && stat < Stats.CS_STAT_RESIST_START+Stats.RESIST_TYPES)
                            {
                                final int resist = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv stats resist"+stat+"="+((resist&0x8000) != 0 ? resist-0x10000 : resist)+"\n");
                                }
                                stats.setStat(stat, (resist&0x8000) != 0 ? resist-0x10000 : resist);
                            }
                            else if (Stats.CS_STAT_SKILLINFO <= stat && stat < Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS)
                            {
                                final int level = packet[pos++]&0xFF;
                                final long experience3 = ((long)(packet[pos++]&0xFF)<<56)|((long)(packet[pos++]&0xFF)<<48)|((long)(packet[pos++]&0xFF)<<40)|((long)(packet[pos++]&0xFF)<<32)|((long)(packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv stats skill"+stat+"="+level+"/"+experience3+"\n");
                                }
                                final Skill sk = Stats.getSkill(stat);
                                if (sk == null)
                                {
                                    System.err.println("ignoring skill value for unknown skill "+stat);
                                }
                                else
                                {
                                    sk.set(level, experience3);
                                }
                            }
                            else
                            {
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv stats <unknown>"+stat+"\n");
                                }
                                throw new UnknownCommandException("unknown stat value: "+stat);
                            }
                            break;
                        }
                    }
                    stats.setStatsProcessed(false);
                    if (pos > end) break;
                    return;
                }
                break;

            case 't':
                if (packet[pos++] != 'i') break;
                if (packet[pos++] != 'c') break;
                if (packet[pos++] != 'k') break;
                if (packet[pos++] != ' ') break;
                {
                    final int tickno = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    if (pos != end) break;
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv tick "+tickno+"\n");
                    }
                    CfMapUpdater.processTick(tickno);
                }
                return;

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
                        final int valLocation = (flags&CfItem.UPD_LOCATION) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valFlags = (flags&CfItem.UPD_FLAGS) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valWeight = (flags&CfItem.UPD_WEIGHT) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valFace = (flags&CfItem.UPD_FACE) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final String valName;
                        final String valNamePl;
                        if ((flags&CfItem.UPD_NAME) != 0)
                        {
                            final int nameLength = packet[pos++]&0xFF;
                            int namePlIndex = 0;
                            while (namePlIndex < nameLength && packet[pos+namePlIndex] != 0)
                            {
                                namePlIndex++;
                            }
                            valName = new String(packet, pos, namePlIndex, utf8);
                            valNamePl = namePlIndex+1 < nameLength ? new String(packet, pos+namePlIndex+1, nameLength-(namePlIndex+1), utf8) : valName;
                            pos += nameLength;
                        }
                        else
                        {
                            valName = "";
                            valNamePl = "";
                        }
                        final int valAnim = (flags&CfItem.UPD_ANIM) != 0 ? ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valAnimSpeed = (flags&CfItem.UPD_ANIMSPEED) != 0 ? packet[pos++]&0xFF : 0;
                        final int valNrof = (flags&CfItem.UPD_NROF) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        if (pos != end) break;
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv upditem flags="+flags+" tag="+tag+" loc="+valLocation+" flags="+valFlags+" weight="+valWeight+" face="+valFace+" name="+valName+" name_pl="+valNamePl+" anim="+valAnim+" anim_speed="+valAnimSpeed+" nrof="+valNrof+"\n");
                        }
                        itemsManager.updateItem(flags, tag, valLocation, valFlags, valWeight, valFace, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
                        if ((flags&CfItem.UPD_WEIGHT) != 0)
                        {
                            final CfPlayer player = itemsManager.getPlayer();
                            if (player != null && player.getTag() == tag)
                            {
                                stats.setStat(Stats.C_STAT_WEIGHT, valWeight);
                                stats.setStatsProcessed(false);
                            }
                        }
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
                        if (pos != end) break;
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv updspell flags="+flags+" tag="+tag+" sp="+mana+" gr="+grace+" dam="+damage+"\n");
                        }
                        spellsManager.updateSpell(flags, tag, mana, grace, damage);
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

                    final String vinfo = new String(packet, pos, end-pos, utf8);

                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv version cs="+csval+" sc="+scval+" info="+vinfo+"\n");
                    }

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
        catch (final UnknownCommandException ex)
        {
            ex.setDetails(packet, start, end);
            throw ex;
        }

        if (debugProtocol != null)
        {
            final StringBuilder sb = new StringBuilder("recv invalid ");
            for (int i = start; i < end; i++)
            {
                if (i > start)
                {
                    sb.append(' ');
                }

                HexCodec.hexEncode2(sb, packet[i]&0xFF);
            }
            debugProtocolWrite(sb.toString());
        }

        int cmdlen;
        for (cmdlen = start; cmdlen < end; cmdlen++)
        {
            if ((packet[cmdlen]&0xFF) <= 0x20 || (packet[cmdlen]&0xFF) >= 0x80)
            {
                break;
            }
        }
        throw new UnknownCommandException("Cannot parse command: "+new String(packet, start, cmdlen-start, utf8));
    }

    /**
     * Process the payload data for a map2 command.
     *
     * @param packet The packet contents.
     *
     * @param start The start of the payload data to process.
     *
     * @param end The end of the payload data to process.
     *
     * @throws UnknownCommandException If the command cannot be parsed.
     */
    private void cmd_map2(final byte[] packet, final int start, final int end) throws UnknownCommandException
    {
        synchronized (redrawSemaphore)
        {
            CfMapUpdater.processMapBegin();
            if (debugProtocol != null)
            {
                debugProtocolWrite("recv map2 begin\n");
            }
            int pos = start;
            while (pos < end)
            {
                final int coord = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                final int x = ((coord>>10)&0x3F)-CrossfireMap2Command.MAP2_COORD_OFFSET;
                final int y = ((coord>>4)&0x3F)-CrossfireMap2Command.MAP2_COORD_OFFSET;
                final int coordType = coord&0xF;

                switch (coordType)
                {
                case 0:         // normal coordinate
                    for (;;)
                    {
                        final int lenType = packet[pos++]&0xFF;
                        if (lenType == 0xFF)
                        {
                            break;
                        }

                        final int len = (lenType>>5)&7;
                        final int type = lenType&31;
                        switch (type)
                        {
                        case 0: // clear space
                            if (len != 0) throw new UnknownCommandException("map2 command contains clear command with length "+len);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv map2 "+x+"/"+y+" clear\n");
                            }
                            CfMapUpdater.processMapClear(x, y);
                            break;

                        case 1: // darkness information
                            if (len != 1) throw new UnknownCommandException("map2 command contains darkness command with length "+len);
                            final int darkness = packet[pos++]&0xFF;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv map2 "+x+"/"+y+" darkness="+darkness+"\n");
                            }
                            CfMapUpdater.processMapDarkness(x, y, darkness);
                            break;

                        case 0x10: // image information
                        case 0x11:
                        case 0x12:
                        case 0x13:
                        case 0x14:
                        case 0x15:
                        case 0x16:
                        case 0x17:
                        case 0x18:
                        case 0x19:
                            if (len < 2) throw new UnknownCommandException("map2 command contains image command with length "+len);
                            final int face = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if ((face&0x8000) == 0)
                            {
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv map2 "+x+"/"+y+"/"+(type-0x10)+" face="+face+"\n");
                                }
                                CfMapUpdater.processMapFace(x, y, type-0x10, face, faces);
                            }
                            else
                            {
                                final Animation animation = animations.get(face&0x1FFF);
                                if (animation == null) throw new UnknownCommandException("map2 command references undefined animation "+(face&0x1FFF));
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv map2 "+x+"/"+y+"/"+(type-0x10)+" anim="+(face&0x1FFF)+" type="+((face>>13)&3)+"\n");
                                }
                                CfMapUpdater.processMapAnimation(x, y, type-0x10, animation, (face>>13)&3, faces);
                            }
                            if (len == 3)
                            {
                                if (face == 0)
                                {
                                    throw new UnknownCommandException("map2 command contains smoothing or animation information for empty face");
                                }

                                if ((face&0x8000) == 0)
                                {
                                    final int smooth = packet[pos++]&0xFF;
                                    if (debugProtocol != null)
                                    {
                                        debugProtocolWrite("recv map2 "+x+"/"+y+"/"+(type-0x10)+" smooth="+smooth+"\n");
                                    }
                                    // XXX: update smoothing information
                                }
                                else
                                {
                                    final int animSpeed = packet[pos++]&0xFF;
                                    if (debugProtocol != null)
                                    {
                                        debugProtocolWrite("recv map2 "+x+"/"+y+"/"+(type-0x10)+" anim_speed="+animSpeed+"\n");
                                    }
                                    CfMapUpdater.processMapAnimationSpeed(x, y, type-0x10, animSpeed);
                                }
                            }
                            else if (len == 4)
                            {
                                if (face == 0)
                                {
                                    throw new UnknownCommandException("map2 command contains smoothing or animation information for empty face");
                                }

                                final int animSpeed = packet[pos++]&0xFF;
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv map2 "+x+"/"+y+"/"+(type-0x10)+" anim_speed="+animSpeed+"\n");
                                }
                                CfMapUpdater.processMapAnimationSpeed(x, y, type-0x10, animSpeed);

                                final int smooth = packet[pos++]&0xFF;
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv map2 "+x+"/"+y+"/"+(type-0x10)+" smooth="+smooth+"\n");
                                }
                                // XXX: update smoothing information
                            }
                            else if (len != 2)
                            {
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv map2 "+x+"/"+y+"/"+(type-0x10)+" <invalid>\n");
                                }
                                throw new UnknownCommandException("map2 command contains image command with length "+len);
                            }
                        }
                    }
                    break;

                case 1:         // scroll information
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv map2 "+x+"/"+y+" scroll\n");
                    }
                    CfMapUpdater.processScroll(x, y);
                    break;

                default:
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv map2 "+x+"/"+y+" <invalid>\n");
                    }
                    throw new UnknownCommandException("map2 command contains unexpected coordinate type "+coordType);
                }
            }
            if (debugProtocol != null)
            {
                debugProtocolWrite("recv map2 end\n");
            }
            CfMapUpdater.processMapEnd(true);
        }
    }

    /**
     * Handles the version server to client command.
     * @param csval The client version.
     * @param scval The server version.
     * @param vinfo The version information string.
     */
    private void cmd_version(final int csval, final int scval, final String vinfo)
    {
        CfMapUpdater.processNewmap(mapWidth, mapHeight);
        sendVersion(1023, 1027, "JXClient Java Client Pegasus 0.1");
        sendToggleextendedtext(MessageTypes.getAllTypes());
        sendSetup(
            "sound 3",
            "sound2 3",
            "exp64 1",
            "map2cmd 1",
            "darkness 1",
            "newmapcmd 1",
            "facecache 1",
            "extendedTextInfos 1",
            "itemcmd 2",
            "spellmon 1",
            "tick 1",
            "mapsize "+mapWidth+"x"+mapHeight);
        sendRequestinfo("image_info");
        sendRequestinfo("skill_info");
        sendRequestinfo("exp_table");
        sendToggleextendedtext(1);
        stats.setSimpleWeaponSpeed(scval >= 1029);
    }

    /**
     * Handles the replyinfo server to client command.
     * @param infoType The info_type parameter.
     * @param packet The packet payload data.
     * @param startPos The starting offset into <code>packet</code> where the
     * parameters of <code>infoType</code>'s parameter start.
     * @param endPos The end offset into <code>packet</code>.
     * @throws IOException If an I/O error occurs.
     */
    private void cmd_replyinfo(final String infoType, final byte[] packet, final int startPos, final int endPos) throws IOException
    {
        if (infoType.equals("image_info"))
        {
            final BufferedReader d = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet, startPos, endPos-startPos)));
            final String info = d.readLine();
            if (info == null)
            {
                throw new IOException("Truncated parameter in image_info");
            }
            final int nrpics = Integer.parseInt(info);
            sendAddme();
        }
        else if (infoType.equals("skill_info"))
        {
            Stats.clearSkills();
            final BufferedReader d = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet, startPos, endPos-startPos)));
            for (;;)
            {
                final String r = d.readLine();
                if (r == null)
                {
                    break;
                }

                final String[] sk = patternDot.split(r, 2);
                if (sk.length != 2)
                {
                    System.err.println("Ignoring skill definition for invalid skill: "+r+".");
                    continue;
                }

                final int skillId;
                try
                {
                    skillId = Integer.parseInt(sk[0]);
                }
                catch (final NumberFormatException ex)
                {
                    System.err.println("Ignoring skill definition for invalid skill: "+r+".");
                    continue;
                }

                if (skillId < Stats.CS_STAT_SKILLINFO || skillId >= Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS)
                {
                    System.err.println("Ignoring skill definition for invalid skill id "+skillId+": "+r+".");
                    continue;
                }

                Stats.addSkill(skillId, sk[1]);
            }
        }
        else if (infoType.equals("exp_table"))
        {
            experienceTable.clear();

            int pos = startPos;
            final int numLevels = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
            for (int i = 1; i < numLevels; i++)
            {
                final long exp = ((long)(packet[pos++]&0xFF)<<56)|((long)(packet[pos++]&0xFF)<<48)|((long)(packet[pos++]&0xFF)<<40)|((long)(packet[pos++]&0xFF)<<32)|((long)(packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                experienceTable.add(i, exp);
            }
            if (pos < endPos)
            {
                System.err.println("Ignoring excess data at end of exp_table");
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
     * @throws UnknownCommandException If a protocol error occurs
     */
    private void cmd_setup(final List<String> options) throws UnknownCommandException
    {
        for (int i = 0; i+1 < options.size(); i += 2)
        {
            final String option = options.get(i);
            final String value = options.get(i+1);
            if (option.equals("spellmon"))
            {
                if (!value.equals("1"))
                {
                    throw new UnknownCommandException("Error: the server is too old for this client since it does not support the spellmon=1 setup option.");
                }
            }
            else if (option.equals("sound"))
            {
                // ignore: if the server sends sound info it is processed
            }
            else if (option.equals("sound2"))
            {
                // ignore: if the server sends sound info it is processed
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
                    throw new UnknownCommandException("Error: the server is too old for this client since it does not support the newmapcmd=1 setup option.");
                }
            }
            else if (option.equals("facecache"))
            {
                if (!value.equals("1"))
                {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the facecache=1 setup option.");
                }
            }
            else if (option.equals("extendedTextInfos"))
            {
                if (!value.equals("1"))
                {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the extendedTextInfos=1 setup option.");
                }
            }
            else if (option.equals("itemcmd"))
            {
                if (!value.equals("2"))
                {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the itemcmd=2 setup option.");
                }
            }
            else if (option.equals("mapsize"))
            {
                if (!value.equals(mapWidth+"x"+mapHeight))
                {
                    throw new UnknownCommandException("the server is not suitable for this client since it does not support a map size of "+mapWidth+"x"+mapHeight+".");
                }
            }
            else if (option.equals("map2cmd"))
            {
                if (!value.equals("1"))
                {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the map2cmd=1 setup option.");
                }
            }
            else if (option.equals("darkness"))
            {
                // do not care
            }
            else if (option.equals("tick"))
            {
                if (!value.equals("1"))
                {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the tick=1 setup option.");
                }
            }
            else
            {
                System.err.println("Warning: ignoring unknown setup option from server: "+option+"="+value);
            }
        }
    }

    /**
     * Handles the MapExtended server to client command.
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    private void cmd_mapextended(final DataInputStream dis)
    {
        // XXX: "MapExtended" command not yet implemented
    }

    public void drawInfo(final String message, final int color)
    {
        final CrossfireCommandDrawinfoEvent evt = new CrossfireCommandDrawinfoEvent(this, message, color);
        for (final CrossfireDrawinfoListener listener : drawinfoListeners)
        {
            listener.commandDrawinfoReceived(evt);
        }
    }

    /**
     * Send an "addme" command to the server.
     */
    public void sendAddme()
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send addme\n");
        }
        writePacket(addmePrefix, addmePrefix.length);
    }

    /**
     * Send an "apply" command to the server.
     *
     * @param tag the item to apply
     */
    public void sendApply(final int tag)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send apply tag="+tag+"\n");
        }
        synchronized (writeBuffer)
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
    public void sendAskface(final int num)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send askface face="+num+"\n");
        }
        synchronized (writeBuffer)
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
    public void sendExamine(final int tag)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send examine tag="+tag+"\n");
        }
        synchronized (writeBuffer)
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
    public void sendLock(final boolean val, final int tag)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send lock tag="+tag+" val="+val+"\n");
        }
        synchronized (writeBuffer)
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
    public void sendLookat(final int dx, final int dy)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send lockat pos="+dx+"/"+dy+"\n");
        }
        synchronized (writeBuffer)
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
     * Send a "mark" command to the server.
     *
     * @param tag the item to mark
     */
    public void sendMark(final int tag)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send mark tag="+tag+"\n");
        }
        synchronized (writeBuffer)
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
    public void sendMove(final int to, final int tag, final int nrof)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send move tag="+tag+" to="+to+" nrof="+nrof+"\n");
        }
        synchronized (writeBuffer)
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
    public int sendNcom(final int repeat, final String command)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send ncom no="+packet+" repeat="+repeat+" cmd="+command+"\n");
        }
        final int thisPacket;
        synchronized (writeBuffer)
        {
            thisPacket = packet++&0x00FF;
            byteBuffer.clear();
            byteBuffer.put(ncomPrefix);
            byteBuffer.putShort((short)thisPacket);
            byteBuffer.putInt(repeat);
            byteBuffer.put(command.getBytes(utf8));
            writePacket(writeBuffer, byteBuffer.position());
        }
        return thisPacket;
    }

    /**
     * Send a "reply" command to the server.
     *
     * @param text the text to reply
     */
    public void sendReply(final String text)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send reply text="+text+"\n");
        }
        synchronized (writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(replyPrefix);
            byteBuffer.put(text.getBytes(utf8));
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "requestinfo" command to the server.
     *
     * @param infoType the info type to request
     */
    public void sendRequestinfo(final String infoType)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send requestinfo type="+infoType+"\n");
        }
        synchronized (writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(requestinfoPrefix);
            byteBuffer.put(infoType.getBytes(utf8));
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "setup" command to the server.
     *
     * @param options the option/value pairs to send
     */
    public void sendSetup(final String... options)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send setup options="+Arrays.toString(options)+"\n");
        }
        synchronized (writeBuffer)
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
                    byteBuffer.put(option.getBytes(utf8));
                }
            }
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Send a "toggleextendedtext" command to the server.
     *
     * @param types the types to request
     */
    public void sendToggleextendedtext(final int... types)
    {
        if (types.length <= 0)
        {
            return;
        }

        if (debugProtocol != null)
        {
            debugProtocolWrite("send toggleextendedtext types="+Arrays.toString(types)+"\n");
        }
        synchronized (writeBuffer)
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
    public void sendVersion(final int csval, final int scval, final String vinfo)
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send version cs="+csval+" sc="+scval+" info="+vinfo+"\n");
        }
        synchronized (writeBuffer)
        {
            byteBuffer.clear();
            byteBuffer.put(versionPrefix);
            putDecimal(csval);
            byteBuffer.put((byte)' ');
            putDecimal(scval);
            byteBuffer.put((byte)' ');
            byteBuffer.put(vinfo.getBytes(utf8));
            writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Append an integer in decimal ASCII representation to {@link
     * #byteBuffer}.
     *
     * @param value the value to append
     */
    private void putDecimal(final int value)
    {
        if (value == 0)
        {
            byteBuffer.put((byte)'0');
        }
        else
        {
            final String str = Integer.toString(value);
            try
            {
                byteBuffer.put(str.getBytes("ISO-8859-1"));
            }
            catch (final UnsupportedEncodingException ex)
            {
                throw new AssertionError(); // every Java implementation must support UTF-8
            }
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

    /**
     * Write a message to the debug protocol.
     *
     * @param str The message to write.
     */
    private void debugProtocolWrite(final String str)
    {
        try
        {
            debugProtocol.append(simpleDateFormat.format(new Date()));
            debugProtocol.append(str);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write debug protocol: "+ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

    /**
     * Validates a map size.
     * @param mapWidth the map width in tiles; must be odd and between 3 and 63
     * @param mapHeight the map height in tiles; must be odd and between 3 and
     * 63
     * @throws IllegalArgumentException if the map size if invalid
     */
    public static void validateMapSize(final int mapWidth, final int mapHeight)
    {
        if (mapWidth%2 == 0) throw new IllegalArgumentException("map width is even");
        if (mapHeight%2 == 0) throw new IllegalArgumentException("map height is even");
        if (mapWidth < 3) throw new IllegalArgumentException("map width is less than 3");
        if (mapWidth > 63) throw new IllegalArgumentException("map width is greater than 63");
        if (mapHeight < 3) throw new IllegalArgumentException("map width is less than 3");
        if (mapHeight > 63) throw new IllegalArgumentException("map width is greater than 63");
    }

    /**
     * Sets the map size. Must not be called in connected state.
     * @param mapWidth the map width in tiles; must be odd and between 3 and 63
     * @param mapHeight the map height in tiles; must be odd and between 3 and
     * 63
     * @throws IllegalArgumentException if the map size if invalid
     */
    public void setMapSize(final int mapWidth, final int mapHeight)
    {
        if (isConnected()) throw new IllegalStateException();

        validateMapSize(mapWidth, mapHeight);
        if (this.mapWidth == mapWidth && this.mapHeight == mapHeight)
        {
            return;
        }

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        for (final MapSizeListener listener : mapSizeListeners)
        {
            listener.mapSizeChanged(mapWidth, mapHeight);
        }
    }

    /**
     * Return the map width in tiles.
     * @return the map width
     */
    public int getMapWidth()
    {
        return mapWidth;
    }

    /**
     * Return the map height in tiles.
     * @return the map height
     */
    public int getMapHeight()
    {
        return mapHeight;
    }
}

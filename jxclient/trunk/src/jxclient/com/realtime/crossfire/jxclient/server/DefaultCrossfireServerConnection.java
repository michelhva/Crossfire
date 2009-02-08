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

import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.util.HexCodec;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link CrossfireServerConnection}.
 * @author Andreas Kirschbaum
 */
public class DefaultCrossfireServerConnection extends DefaultServerConnection implements CrossfireServerConnection
{
    /**
     * Pattern to split a string by ":".
     */
    private static final Pattern patternDot = Pattern.compile(":");

    /**
     * The {@link Charset} used for parsing or encoding strings received from
     * or sent to the Crossfire server.
     */
    private static final Charset utf8 = Charset.forName("UTF-8");

    /**
     * The map width in tiles that is negotiated with the server.
     */
    private int mapWidth = 17;

    /**
     * The map height in tiles that is negotiated with the server.
     */
    private int mapHeight = 13;

    /**
     * The number of ground view objects requested from the server.
     */
    private int numLookObjects = 50;

    /**
     * The {@link MapSizeListener}s to be notified.
     */
    private final List<MapSizeListener> mapSizeListeners = new ArrayList<MapSizeListener>();

    /**
     * The {@link CrossfireDrawinfoListener}s to be notified.
     */
    private final List<CrossfireDrawinfoListener> drawinfoListeners = new ArrayList<CrossfireDrawinfoListener>();

    /**
     * The {@link CrossfireDrawextinfoListener}s to be notified.
     */
    private final List<CrossfireDrawextinfoListener> drawextinfoListeners = new ArrayList<CrossfireDrawextinfoListener>();

    /**
     * The {@link CrossfireQueryListener}s to be notified.
     */
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
     * The {@link CrossfireStatsListener}s to be notified.
     */
    private final List<CrossfireStatsListener> crossfireStatsListeners = new ArrayList<CrossfireStatsListener>();

    /**
     * The {@link CrossfireUpdateItemListener}s to be notified.
     */
    private final List<CrossfireUpdateItemListener> crossfireUpdateItemListeners = new ArrayList<CrossfireUpdateItemListener>();

    /**
     * The {@link CrossfireUpdateMapListener}s to be notified.
     */
    private final List<CrossfireUpdateMapListener> crossfireUpdateMapListeners = new ArrayList<CrossfireUpdateMapListener>();

    /**
     * The {@link CrossfireTickListener}s to be notified.
     */
    private final List<CrossfireTickListener> crossfireTickListeners = new ArrayList<CrossfireTickListener>();

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

    /**
     * The {@link CrossfireFaceListener}s to be notified.
     */
    private final List<CrossfireFaceListener> crossfireFaceListeners = new ArrayList<CrossfireFaceListener>();

    /**
     * The {@link CrossfireSpellListener}s to be notified.
     */
    private final List<CrossfireSpellListener> crossfireSpellListeners = new ArrayList<CrossfireSpellListener>();

    /**
     * The {@link ReceivedPacketListener}s to be notified.
     */
    private final List<ReceivedPacketListener> receivedPacketListeners = new CopyOnWriteArrayList<ReceivedPacketListener>();

    /**
     * The {@link CrossfireExpTableListener}s to be notified.
     */
    private final List<CrossfireExpTableListener> crossfireExpTableListeners = new ArrayList<CrossfireExpTableListener>();

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

    /**
     * The appender to write protocol commands to. May be <code>null</code> to
     * not write anything.
     */
    private final Writer debugProtocol;

    /**
     * A formatter for timestamps.
     */
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");

    /**
     * Creates a new instance.
     * @param redrawSemaphore the semaphore used to synchronized map model
     * updates and map view redraws
     * @param debugProtocol tf non-<code>null</code>, write all protocol
     * commands to this writer
     */
    public DefaultCrossfireServerConnection(final Object redrawSemaphore, final Writer debugProtocol)
    {
        this.redrawSemaphore = redrawSemaphore;
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        this.debugProtocol = debugProtocol;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void addMapSizeListener(final MapSizeListener listener)
    {
        mapSizeListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeMapSizeListener(final MapSizeListener listener)
    {
        mapSizeListeners.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void addCrossfireDrawinfoListener(final CrossfireDrawinfoListener listener)
    {
        drawinfoListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeCrossfireDrawinfoListener(final CrossfireDrawinfoListener listener)
    {
        drawinfoListeners.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void addCrossfireDrawextinfoListener(final CrossfireDrawextinfoListener listener)
    {
        drawextinfoListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeCrossfireDrawextinfoListener(final CrossfireDrawextinfoListener listener)
    {
        drawextinfoListeners.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void addCrossfireQueryListener(final CrossfireQueryListener listener)
    {
        queryListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeCrossfireQueryListener(final CrossfireQueryListener listener)
    {
        queryListeners.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireMagicmapListener(final CrossfireMagicmapListener listener)
    {
        magicmapListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireMagicmapListener(final CrossfireMagicmapListener listener)
    {
        magicmapListeners.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateFaceListener(final CrossfireUpdateFaceListener listener)
    {
        crossfireUpdateFaceListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireStatsListener(final CrossfireStatsListener crossfireStatsListener)
    {
        crossfireStatsListeners.add(crossfireStatsListener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateItemListener(final CrossfireUpdateItemListener crossfireUpdateItemListener)
    {
        crossfireUpdateItemListeners.add(crossfireUpdateItemListener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateMapListener(final CrossfireUpdateMapListener listener)
    {
        crossfireUpdateMapListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireTickListener(final CrossfireTickListener listener)
    {
        crossfireTickListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireSoundListener(final CrossfireSoundListener listener)
    {
        crossfireSoundListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireMusicListener(final CrossfireMusicListener listener)
    {
        crossfireMusicListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireComcListener(final CrossfireComcListener listener)
    {
        crossfireComcListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireFaceListener(final CrossfireFaceListener listener)
    {
        crossfireFaceListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireSpellListener(final CrossfireSpellListener listener)
    {
        crossfireSpellListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addPacketWatcherListener(final ReceivedPacketListener listener)
    {
        receivedPacketListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removePacketWatcherListener(final ReceivedPacketListener listener)
    {
        receivedPacketListeners.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireExpTableListener(final CrossfireExpTableListener crossfireExpTableListener)
    {
        crossfireExpTableListeners.add(crossfireExpTableListener);
    }

    /** {@inheritDoc}
     * Processes a received packet. This function does not avoid index out of
     * bounds accesses to the array <code>packet</code>; instead, a
     * <code>try...catch</code> clause is used to detect invalid packets.
     */
    @Override
    public void processPacket(final byte[] packet, final int start, final int end) throws UnknownCommandException
    {
        try
        {
            int pos = start;
            final int args;
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
                            args = pos;
                            if (pos != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv addme_failed\n");
                            }
                            // XXX: addme_failed command not implemented
                            notifyPacketWatcherListenersNodata(packet, start, args, end);
                            return;

                        case 's':
                            if (packet[pos++] != 'u') break;
                            if (packet[pos++] != 'c') break;
                            if (packet[pos++] != 'c') break;
                            if (packet[pos++] != 'e') break;
                            if (packet[pos++] != 's') break;
                            if (packet[pos++] != 's') break;
                            args = pos;
                            if (pos != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv addme_success\n");
                            }
                            // XXX: addme_success command not implemented
                            notifyPacketWatcherListenersNodata(packet, start, args, end);
                            return;
                        }
                        break;

                    case 's':
                        if (packet[pos++] != 'p') break;
                        if (packet[pos++] != 'e') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != ' ') break;
                        args = pos;
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
                            for (final CrossfireSpellListener crossfireSpellListener : crossfireSpellListeners)
                            {
                                crossfireSpellListener.addSpell(tag, level, castingTime, mana, grace, damage, skill, path, face, name, message);
                            }
                        }
                        if (pos != end) break;
                        notifyPacketWatcherListenersMixed(packet, start, args, end);
                        return;
                    }
                    break;

                case 'n':
                    if (packet[pos++] != 'i') break;
                    if (packet[pos++] != 'm') break;
                    if (packet[pos++] != ' ') break;
                    args = pos;
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
                            debugProtocolWrite("recv addanim num="+num+" flags="+flags+" faces="+Arrays.toString(faces)+"\n");
                        }
                        if ((num&~0x1FFF) != 0) throw new UnknownCommandException("invalid animation id "+num);
                        for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                        {
                            listener.addAnimation(num&0x1FFF, flags, faces);
                        }
                    }
                    notifyPacketWatcherListenersShortArray(packet, start, args, end);
                    return;
                }
                break;

            case 'c':
                if (packet[pos++] != 'o') break;
                if (packet[pos++] != 'm') break;
                if (packet[pos++] != 'c') break;
                if (packet[pos++] != ' ') break;
                args = pos;
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
                notifyPacketWatcherListenersShortInt(packet, start, args, end);
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
                            args = pos;
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
                                for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners)
                                {
                                    crossfireUpdateItemListener.delinvReceived(tag);
                                }
                            }
                            notifyPacketWatcherListenersAscii(packet, start, args, end);
                            return;

                        case 't':
                            if (packet[pos++] != 'e') break;
                            if (packet[pos++] != 'm') break;
                            if (packet[pos++] != ' ') break;
                            args = pos;
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
                                for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners)
                                {
                                    crossfireUpdateItemListener.delitemReceived(tags);
                                }
                            }
                            notifyPacketWatcherListenersIntArray(packet, start, args, end);
                            return;
                        }
                        break;

                    case 's':
                        if (packet[pos++] != 'p') break;
                        if (packet[pos++] != 'e') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != 'l') break;
                        if (packet[pos++] != ' ') break;
                        args = pos;
                        {
                            final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (pos != end) break;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv delspell tag="+tag+"\n");
                            }
                            for (final CrossfireSpellListener crossfireSpellListener : crossfireSpellListeners)
                            {
                                crossfireSpellListener.deleteSpell(tag);
                            }
                        }
                        notifyPacketWatcherListenersIntArray(packet, start, args, end);
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
                        args = pos;
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
                        notifyPacketWatcherListenersAscii(packet, start, args, end);
                        return;

                    case 'i':
                        if (packet[pos++] != 'n') break;
                        if (packet[pos++] != 'f') break;
                        if (packet[pos++] != 'o') break;
                        if (packet[pos++] != ' ') break;
                        args = pos;
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
                        notifyPacketWatcherListenersAscii(packet, start, args, end);
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
                    args = pos;
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
                    notifyPacketWatcherListenersNodata(packet, start, args, end);
                    return;

                case 'T':
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 'x') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 'S') break;
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != ' ') break;
                    args = pos;
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
                    notifyPacketWatcherListenersNodata(packet, start, args, end);
                    return;
                }
                break;

            case 'f':
                if (packet[pos++] != 'a') break;
                if (packet[pos++] != 'c') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != '2') break;
                if (packet[pos++] != ' ') break;
                args = pos;
                {
                    final int faceNum = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final int faceSetNum = packet[pos++]&0xFF;
                    final int faceChecksum = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final String faceName = new String(packet, pos, end-pos, utf8).intern();
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv face2 num="+faceNum+" set="+faceSetNum+" checksum="+faceChecksum+" name="+faceName+"\n");
                    }
                    for (final CrossfireFaceListener crossfireFaceListener : crossfireFaceListeners)
                    {
                        crossfireFaceListener.faceReceived(faceNum, faceSetNum, faceChecksum, faceName);
                    }
                }
                notifyPacketWatcherListenersMixed(packet, start, args, end);
                return;

            case 'g':
                if (packet[pos++] != 'o') break;
                if (packet[pos++] != 'o') break;
                if (packet[pos++] != 'd') break;
                if (packet[pos++] != 'b') break;
                if (packet[pos++] != 'y') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != ' ') break;
                args = pos;
                if (pos != end) break;
                if (debugProtocol != null)
                {
                    debugProtocolWrite("recv goodbye\n");
                }
                // XXX: goodbye command not implemented
                notifyPacketWatcherListenersNodata(packet, start, args, end);
                return;

            case 'i':
                switch (packet[pos++])
                {
                case 'm':
                    if (packet[pos++] != 'a') break;
                    if (packet[pos++] != 'g') break;
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != '2') break;
                    if (packet[pos++] != ' ') break;
                    args = pos;
                    {
                        final int faceNum = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int faceSetNum = packet[pos++]&0xFF;
                        final int len = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        if (pos+len != end) break;
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv image2 face="+faceNum+" set="+faceSetNum+" len="+len+"\n");
                        }
                        for (final CrossfireUpdateFaceListener listener : crossfireUpdateFaceListeners)
                        {
                            listener.updateFace(faceNum, faceSetNum, packet, pos, len);
                        }
                    }
                    notifyPacketWatcherListenersMixed(packet, start, args, end);
                    return;

                case 't':
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 'm') break;
                    if (packet[pos++] != '2') break;
                    if (packet[pos++] != ' ') break;
                    args = pos;
                    {
                        final int location = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        while (pos < end)
                        {
                            final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int flags = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int weight = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            final int faceNum = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
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
                                debugProtocolWrite("recv item2 location="+location+" tag="+tag+" flags="+flags+" weight="+weight+" face="+faceNum+" name="+name+" name_pl="+namePl+" anim="+anim+" anim_speed="+animSpeed+" nrof="+nrof+" type="+type+"\n");
                            }
                            for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners)
                            {
                                crossfireUpdateItemListener.additemReceived(location, tag, flags, weight, faceNum, name, namePl, anim, animSpeed, nrof, type);
                            }
                        }
                        if (pos != end) break;
                        for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners)
                        {
                            crossfireUpdateItemListener.additemFinished();
                        }
                    }
                    notifyPacketWatcherListenersMixed(packet, start, args, end);
                    return;
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
                        args = pos;

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
                        notifyPacketWatcherListenersMixed(packet, start, args, end);
                        return;

                    case 'p':
                        switch (packet[pos++])
                        {
                        case '2':
                            if (packet[pos++] != ' ') break;
                            args = pos;
                            cmdMap2(packet, pos, end);
                            notifyPacketWatcherListenersShortArray(packet, start, args, end);
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
                            args = pos;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv mapextended\n");
                            }
                            cmdMapextended(packet, pos, end-pos);
                            notifyPacketWatcherListenersMixed(packet, start, args, end);
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
                    args = pos;
                    final String music = new String(packet, pos, end-pos, utf8);
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv music "+music+"\n");
                    }

                    for (final CrossfireMusicListener listener : crossfireMusicListeners)
                    {
                        listener.commandMusicReceived(music);
                    }
                    notifyPacketWatcherListenersAscii(packet, start, args, end);
                    return;
                }
                break;

            case 'n':
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'w') break;
                if (packet[pos++] != 'm') break;
                if (packet[pos++] != 'a') break;
                if (packet[pos++] != 'p') break;
                args = pos;
                if (pos != end) break;
                if (debugProtocol != null)
                {
                    debugProtocolWrite("recv newmap\n");
                }
                for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                {
                    listener.newMap(mapWidth, mapHeight);
                }
                notifyPacketWatcherListenersNodata(packet, start, args, end);
                return;

            case 'p':
                if (packet[pos++] != 'l') break;
                if (packet[pos++] != 'a') break;
                if (packet[pos++] != 'y') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'r') break;
                if (packet[pos++] != ' ') break;
                args = pos;
                {
                    final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final int weight = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final int faceNum = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    final int nameLength = packet[pos++]&0xFF;
                    final String name = new String(packet, pos, nameLength, utf8);
                    pos += nameLength;
                    if (pos != end) break;
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv player tag="+tag+" weight="+weight+" face="+faceNum+" name="+name+"\n");
                    }
                    for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners)
                    {
                        crossfireUpdateItemListener.playerReceived(tag, weight, faceNum, name);
                    }
                }
                notifyPacketWatcherListenersMixed(packet, start, args, end);
                return;

            case 'q':
                if (packet[pos++] != 'u') break;
                if (packet[pos++] != 'e') break;
                if (packet[pos++] != 'r') break;
                if (packet[pos++] != 'y') break;
                if (packet[pos++] != ' ') break;
                args = pos;
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
                notifyPacketWatcherListenersAscii(packet, start, args, end);
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
                args = pos;
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
                        cmdReplyinfo(infoType, packet, pos+1, end);
                    }
                    catch (final IOException ex)
                    {
                        throw new UnknownCommandException("invalid replyinfo command: "+ex.getMessage());
                    }
                }
                notifyPacketWatcherListenersAscii(packet, start, args, end);
                return;

            case 's':
                switch (packet[pos++])
                {
                case 'e':
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 'u') break;
                    if (packet[pos++] != 'p') break;
                    if (packet[pos++] != ' ') break;
                    args = pos;
                    final List<String> options = new ArrayList<String>();
                    while (pos < end)
                    {
                        while (packet[pos] == ' ')
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
                    if (pos != end) break;
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv setup "+options+"\n");
                    }
                    if (options.size()%2 != 0)
                    {
                        throw new UnknownCommandException("odd number of arguments in setup command");
                    }
                    cmdSetup(options);
                    notifyPacketWatcherListenersAscii(packet, start, args, end);
                    return;

                case 'm':
                    if (packet[pos++] != 'o') break;
                    if (packet[pos++] != 'o') break;
                    if (packet[pos++] != 't') break;
                    if (packet[pos++] != 'h') break;
                    if (packet[pos++] != ' ') break;
                    args = pos;
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
                    notifyPacketWatcherListenersShortArray(packet, start, args, end);
                    return;

                case 'o':
                    if (packet[pos++] != 'u') break;
                    if (packet[pos++] != 'n') break;
                    if (packet[pos++] != 'd') break;
                    switch (packet[pos++])
                    {
                    case ' ':
                        args = pos;
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
                        notifyPacketWatcherListenersMixed(packet, start, args, end);
                        return;

                    case '2':
                        if (packet[pos++] != ' ') break;
                        args = pos;
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
                        notifyPacketWatcherListenersMixed(packet, start, args, end);
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
                        case CrossfireStatsListener.CS_STAT_HP:
                        case CrossfireStatsListener.CS_STAT_MAXHP:
                        case CrossfireStatsListener.CS_STAT_SP:
                        case CrossfireStatsListener.CS_STAT_MAXSP:
                        case CrossfireStatsListener.CS_STAT_STR:
                        case CrossfireStatsListener.CS_STAT_INT:
                        case CrossfireStatsListener.CS_STAT_WIS:
                        case CrossfireStatsListener.CS_STAT_DEX:
                        case CrossfireStatsListener.CS_STAT_CON:
                        case CrossfireStatsListener.CS_STAT_CHA:
                        case CrossfireStatsListener.CS_STAT_LEVEL:
                        case CrossfireStatsListener.CS_STAT_WC:
                        case CrossfireStatsListener.CS_STAT_AC:
                        case CrossfireStatsListener.CS_STAT_DAM:
                        case CrossfireStatsListener.CS_STAT_ARMOUR:
                        case CrossfireStatsListener.CS_STAT_FOOD:
                        case CrossfireStatsListener.CS_STAT_POW:
                        case CrossfireStatsListener.CS_STAT_GRACE:
                        case CrossfireStatsListener.CS_STAT_MAXGRACE:
                        case CrossfireStatsListener.CS_STAT_FLAGS:
                            final short int2Param = (short)(((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF));
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats stat="+stat+" int2="+int2Param+"="+(int2Param&0xFFFF)+"\n");
                            }
                            for(final CrossfireStatsListener crossfireStatsListener : crossfireStatsListeners)
                            {
                                crossfireStatsListener.statInt2Received(stat, int2Param);
                            }
                            notifyPacketWatcherListenersStats(stat, int2Param);
                            break;

                        case CrossfireStatsListener.CS_STAT_EXP:
                        case CrossfireStatsListener.CS_STAT_SPEED:
                        case CrossfireStatsListener.CS_STAT_WEAP_SP:
                        case CrossfireStatsListener.CS_STAT_WEIGHT_LIM:
                        case CrossfireStatsListener.CS_STAT_SPELL_ATTUNE:
                        case CrossfireStatsListener.CS_STAT_SPELL_REPEL:
                        case CrossfireStatsListener.CS_STAT_SPELL_DENY:
                            final int int4Param = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats stat="+stat+" int4="+int4Param+"\n");
                            }
                            for(final CrossfireStatsListener crossfireStatsListener : crossfireStatsListeners)
                            {
                                crossfireStatsListener.statInt4Received(stat, int4Param);
                            }
                            notifyPacketWatcherListenersStats(stat, int4Param);
                            break;

                        case CrossfireStatsListener.CS_STAT_EXP64:
                            final long int8Param = ((long)(packet[pos++]&0xFF)<<56)|((long)(packet[pos++]&0xFF)<<48)|((long)(packet[pos++]&0xFF)<<40)|((long)(packet[pos++]&0xFF)<<32)|((long)(packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats stat="+stat+" int8="+int8Param+"\n");
                            }
                            for(final CrossfireStatsListener crossfireStatsListener : crossfireStatsListeners)
                            {
                                crossfireStatsListener.statInt8Received(stat, int8Param);
                            }
                            notifyPacketWatcherListenersStats(stat, int8Param);
                            break;

                        case CrossfireStatsListener.CS_STAT_RANGE:
                        case CrossfireStatsListener.CS_STAT_TITLE:
                            final int length = packet[pos++]&0xFF;
                            final String strParam = new String(packet, pos, length, utf8);
                            pos += length;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv stats stat="+stat+" str="+strParam+"\n");
                            }
                            for(final CrossfireStatsListener crossfireStatsListener : crossfireStatsListeners)
                            {
                                crossfireStatsListener.statStringReceived(stat, strParam);
                            }
                            notifyPacketWatcherListenersStats(stat, strParam);
                            break;

                        default:
                            if (CrossfireStatsListener.CS_STAT_RESIST_START <= stat && stat < CrossfireStatsListener.CS_STAT_RESIST_START+CrossfireStatsListener.RESIST_TYPES)
                            {
                                final short int2Param2 = (short)(((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF));
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv stats stat="+stat+" int2="+int2Param2+"\n");
                                }
                                for(final CrossfireStatsListener crossfireStatsListener : crossfireStatsListeners)
                                {
                                    crossfireStatsListener.statInt2Received(stat, int2Param2);
                                }
                                notifyPacketWatcherListenersStats(stat, int2Param2);
                            }
                            else if (CrossfireStatsListener.CS_STAT_SKILLINFO <= stat && stat < CrossfireStatsListener.CS_STAT_SKILLINFO+CrossfireStatsListener.CS_NUM_SKILLS)
                            {
                                final int level = packet[pos++]&0xFF;
                                final long experience = ((long)(packet[pos++]&0xFF)<<56)|((long)(packet[pos++]&0xFF)<<48)|((long)(packet[pos++]&0xFF)<<40)|((long)(packet[pos++]&0xFF)<<32)|((long)(packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv stats stat="+stat+" level="+level+" experience="+experience+"\n");
                                }
                                for(final CrossfireStatsListener crossfireStatsListener : crossfireStatsListeners)
                                {
                                    crossfireStatsListener.statSkillReceived(stat, level, experience);
                                }
                                notifyPacketWatcherListenersStats(stat, level, experience);
                            }
                            else
                            {
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv stats stat="+stat+" <unknown parameter>\n");
                                }
                                throw new UnknownCommandException("unknown stat value: "+stat);
                            }
                            break;
                        }
                    }
                    if (pos != end) break;
                    return;
                }
                break;

            case 't':
                if (packet[pos++] != 'i') break;
                if (packet[pos++] != 'c') break;
                if (packet[pos++] != 'k') break;
                if (packet[pos++] != ' ') break;
                args = pos;
                {
                    final int tickNo = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                    if (pos != end) break;
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv tick "+tickNo+"\n");
                    }
                    for (final CrossfireTickListener listener : crossfireTickListeners)
                    {
                        listener.tick(tickNo);
                    }
                }
                notifyPacketWatcherListenersIntArray(packet, start, args, end);
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
                    args = pos;
                    {
                        final int flags = packet[pos++]&0xFF;
                        final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int valLocation = (flags&CfItem.UPD_LOCATION) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valFlags = (flags&CfItem.UPD_FLAGS) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valWeight = (flags&CfItem.UPD_WEIGHT) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int valFaceNum = (flags&CfItem.UPD_FACE) != 0 ? ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
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
                            debugProtocolWrite("recv upditem flags="+flags+" tag="+tag+" loc="+valLocation+" flags="+valFlags+" weight="+valWeight+" face="+valFaceNum+" name="+valName+" name_pl="+valNamePl+" anim="+valAnim+" anim_speed="+valAnimSpeed+" nrof="+valNrof+"\n");
                        }
                        for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners)
                        {
                            crossfireUpdateItemListener.upditemReceived(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
                        }
                    }
                    notifyPacketWatcherListenersMixed(packet, start, args, end);
                    return;

                case 's':
                    if (packet[pos++] != 'p') break;
                    if (packet[pos++] != 'e') break;
                    if (packet[pos++] != 'l') break;
                    if (packet[pos++] != 'l') break;
                    if (packet[pos++] != ' ') break;
                    args = pos;
                    {
                        final int flags = packet[pos++]&0xFF;
                        final int tag = ((packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
                        final int mana = (flags&CrossfireSpellListener.UPD_SP_MANA) != 0 ? ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int grace = (flags&CrossfireSpellListener.UPD_SP_GRACE) != 0 ? ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        final int damage = (flags&CrossfireSpellListener.UPD_SP_DAMAGE) != 0 ? ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF) : 0;
                        if (pos != end) break;
                        if (debugProtocol != null)
                        {
                            debugProtocolWrite("recv updspell flags="+flags+" tag="+tag+" sp="+mana+" gr="+grace+" dam="+damage+"\n");
                        }
                        for (final CrossfireSpellListener crossfireSpellListener : crossfireSpellListeners)
                        {
                            crossfireSpellListener.updateSpell(flags, tag, mana, grace, damage);
                        }
                    }
                    notifyPacketWatcherListenersMixed(packet, start, args, end);
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
                args = pos;
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

                    cmdVersion(csval, scval, vinfo);
                }
                notifyPacketWatcherListenersAscii(packet, start, args, end);
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

        final String command = extractCommand(packet, start, end);
        throw new UnknownCommandException("Cannot parse command: "+command);
    }

    /**
     * Returns the command string for a received packet.
     * @param packet the packet contents
     * @param start the start index into <code>packet</code>
     * @param end the end index into <code>packet</code>
     * @return the command string
     */
    private static String extractCommand(final byte[] packet, final int start, final int end)
    {
        int cmdlen;
        for (cmdlen = start; cmdlen < end; cmdlen++)
        {
            if ((packet[cmdlen]&0xFF) <= 0x20 || (packet[cmdlen]&0xFF) >= 0x80)
            {
                break;
            }
        }
        return new String(packet, start, cmdlen-start, utf8);
    }

    /**
     * Processes the payload data for a map2 command.
     * @param packet the packet contents
     * @param start the start of the payload data to process
     * @param end the end of the payload data to process
     * @throws UnknownCommandException if the command cannot be parsed
     */
    private void cmdMap2(final byte[] packet, final int start, final int end) throws UnknownCommandException
    {
        synchronized (redrawSemaphore)
        {
            for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
            {
                listener.mapBegin();
            }
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
                            for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                            {
                                listener.mapClear(x, y);
                            }
                            break;

                        case 1: // darkness information
                            if (len != 1) throw new UnknownCommandException("map2 command contains darkness command with length "+len);
                            final int darkness = packet[pos++]&0xFF;
                            if (debugProtocol != null)
                            {
                                debugProtocolWrite("recv map2 "+x+"/"+y+" darkness="+darkness+"\n");
                            }
                            for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                            {
                                listener.mapDarkness(x, y, darkness);
                            }
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
                                for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                                {
                                    listener.mapFace(x, y, type-0x10, face);
                                }
                            }
                            else
                            {
                                if (debugProtocol != null)
                                {
                                    debugProtocolWrite("recv map2 "+x+"/"+y+"/"+(type-0x10)+" anim="+(face&0x1FFF)+" type="+((face>>13)&3)+"\n");
                                }
                                for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                                {
                                    listener.mapAnimation(x, y, type-0x10, face&0x1FFF, (face>>13)&3);
                                }
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
                                    for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                                    {
                                        listener.mapAnimationSpeed(x, y, type-0x10, animSpeed);
                                    }
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
                                for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                                {
                                    listener.mapAnimationSpeed(x, y, type-0x10, animSpeed);
                                }

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
                    for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
                    {
                        listener.scroll(x, y);
                    }
                    break;

                default:
                    if (debugProtocol != null)
                    {
                        debugProtocolWrite("recv map2 "+x+"/"+y+" <invalid>\n");
                    }
                    throw new UnknownCommandException("map2 command contains unexpected coordinate type "+coordType);
                }
            }
            if (pos > end)
            {
                throw new UnknownCommandException("truncated map2 command");
            }
            if (debugProtocol != null)
            {
                debugProtocolWrite("recv map2 end\n");
            }
            for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
            {
                listener.mapEnd();
            }
        }
    }

    /**
     * Handles the version server to client command.
     * @param csval the client version
     * @param scval the server version
     * @param vinfo the version information string
     */
    private void cmdVersion(final int csval, final int scval, final String vinfo)
    {
        for (final CrossfireUpdateMapListener listener : crossfireUpdateMapListeners)
        {
            listener.newMap(mapWidth, mapHeight);
        }
        sendVersion(1023, 1027, "JXClient Java Client Pegasus 0.1");
        sendToggleextendedtext(MessageTypes.getAllTypes());
        sendSetup(
            "faceset 0",
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
            "mapsize "+mapWidth+"x"+mapHeight,
            "num_look_objects "+numLookObjects);
        sendRequestinfo("image_info");
        sendRequestinfo("skill_info");
        sendRequestinfo("exp_table");
        sendToggleextendedtext(1);
        for(final CrossfireStatsListener crossfireStatsListener : crossfireStatsListeners)
        {
            crossfireStatsListener.setSimpleWeaponSpeed(scval >= 1029);
        }
    }

    /**
     * Handles the replyinfo server to client command.
     * @param infoType the info_type parameter
     * @param packet the packet payload data
     * @param startPos the starting offset into <code>packet</code> where the
     * parameters of <code>infoType</code>'s parameter start
     * @param endPos the end offset into <code>packet</code>
     * @throws IOException if an I/O error occurs
     */
    private void cmdReplyinfo(final String infoType, final byte[] packet, final int startPos, final int endPos) throws IOException
    {
        if (infoType.equals("image_info"))
        {
            processImageInfoReplyinfo(packet, startPos, endPos);
        }
        else if (infoType.equals("skill_info"))
        {
            processSkillInfoReplyinfo(packet, startPos, endPos);
        }
        else if (infoType.equals("exp_table"))
        {
            processExpTableReplyinfo(packet, startPos, endPos);
        }
        else
        {
            System.err.println("Ignoring unexpected replyinfo type '"+infoType+"'.");
        }
    }

    /**
     * Processes a "replyinfo image_info" block.
     * @param packet the packet to process
     * @param startPos the starting position into <code>packet</code>
     * @param endPos the end position into <code>packet</code>
     * @throws IOException if the packet cannot be parsed
     */
    private void processImageInfoReplyinfo(final byte[] packet, final int startPos, final int endPos) throws IOException
    {
        final ByteArrayInputStream is = new ByteArrayInputStream(packet, startPos, endPos-startPos);
        try
        {
            final InputStreamReader isr = new InputStreamReader(is);
            try
            {
                final BufferedReader d = new BufferedReader(isr);
                try
                {
                    final String info = d.readLine();
                    if (info == null)
                    {
                        throw new IOException("Truncated parameter in image_info");
                    }
                    final int nrpics = Integer.parseInt(info);
                }
                finally
                {
                    d.close();
                }
            }
            finally
            {
                isr.close();
            }
        }
        finally
        {
            is.close();
        }
        sendAddme();
    }

    /**
     * Processes a "replyinfo skill_info" block.
     * @param packet the packet to process
     * @param startPos the starting position into <code>packet</code>
     * @param endPos the end position into <code>packet</code>
     * @throws IOException if the packet cannot be parsed
     */
    private static void processSkillInfoReplyinfo(final byte[] packet, final int startPos, final int endPos) throws IOException
    {
        SkillSet.clearSkills();
        final ByteArrayInputStream is = new ByteArrayInputStream(packet, startPos, endPos-startPos);
        try
        {
            final InputStreamReader isr = new InputStreamReader(is);
            try
            {
                final BufferedReader d = new BufferedReader(isr);
                try
                {
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

                        if (skillId < CrossfireStatsListener.CS_STAT_SKILLINFO || skillId >= CrossfireStatsListener.CS_STAT_SKILLINFO+CrossfireStatsListener.CS_NUM_SKILLS)
                        {
                            System.err.println("Ignoring skill definition for invalid skill id "+skillId+": "+r+".");
                            continue;
                        }

                        SkillSet.addSkill(skillId, sk[1]);
                    }
                }
                finally
                {
                    d.close();
                }
            }
            finally
            {
                isr.close();
            }
        }
        finally
        {
            is.close();
        }
    }

    /**
     * Processes a "replyinfo exp_table" block.
     * @param packet the packet to process
     * @param startPos the starting position into <code>packet</code>
     * @param endPos the end position into <code>packet</code>
     */
    private void processExpTableReplyinfo(final byte[] packet, final int startPos, final int endPos)
    {
        int pos = startPos;
        final int numLevels = ((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
        final long[] expTable = new long[numLevels];
        for (int level = 1; level < numLevels; level++)
        {
            expTable[level] = ((long)(packet[pos++]&0xFF)<<56)|((long)(packet[pos++]&0xFF)<<48)|((long)(packet[pos++]&0xFF)<<40)|((long)(packet[pos++]&0xFF)<<32)|((long)(packet[pos++]&0xFF)<<24)|((packet[pos++]&0xFF)<<16)|((packet[pos++]&0xFF)<<8)|(packet[pos++]&0xFF);
        }
        if (pos < endPos)
        {
            System.err.println("Ignoring excess data at end of exp_table");
        }

        for (final CrossfireExpTableListener crossfireExpTableListener : crossfireExpTableListeners)
        {
            crossfireExpTableListener.expTableReceived(expTable);
        }
    }

    /**
     * Handles the setup server to client command.
     * @param options the option/value pairs
     * @throws UnknownCommandException if a protocol error occurs
     */
    private void cmdSetup(final List<String> options) throws UnknownCommandException
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
            else if (option.equals("num_look_objects"))
            {
                try
                {
                    if (Integer.parseInt(value) != numLookObjects)
                    {
                        System.err.println("Warning: the server didn't accept the num_look_objects setup option: requested "+numLookObjects+", returned "+value+".");
                        System.err.println("Expect issues with the ground view display.");
                    }
                }
                catch (final NumberFormatException ex)
                {
                    System.err.println("Warning: the server is too old for this client since it does not support the num_look_objects setup option.");
                    System.err.println("Expect issues with the ground view display.");
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
     * @param buf the parameter buffer
     * @param start the starting index into <code>buf</code>
     * @param length the length of the parameter buffer
     */
    private void cmdMapextended(final byte[] buf, final int start, final int length)
    {
        // XXX: "MapExtended" command not yet implemented
    }

    /** {@inheritDoc} */
    @Override
    public void drawInfo(final String message, final int color)
    {
        final CrossfireCommandDrawinfoEvent evt = new CrossfireCommandDrawinfoEvent(this, message, color);
        for (final CrossfireDrawinfoListener listener : drawinfoListeners)
        {
            listener.commandDrawinfoReceived(evt);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sendAddme()
    {
        if (debugProtocol != null)
        {
            debugProtocolWrite("send addme\n");
        }
        writePacket(addmePrefix, addmePrefix.length);
    }

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
    public int sendNcom(final int repeat, final String command)
    {
        if(!isConnected())
        {
            return 0;
        }
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
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
     * Appends an integer in decimal ASCII representation to {@link
     * #byteBuffer}.
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
     * Parses a character into an integer.
     * @param ch the character to parse
     * @return the integer representing the character
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
     * Writes a message to the debug protocol.
     * @param str the message to write
     */
    private void debugProtocolWrite(final String str)
    {
        try
        {
            debugProtocol.append(simpleDateFormat.format(new Date()));
            debugProtocol.append(str);
            debugProtocol.flush();
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

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
    public void setNumLookObjects(final int numLookObjects)
    {
        if (isConnected()) throw new IllegalStateException();
        if (numLookObjects < 1) throw new IllegalArgumentException("num_look_objects is not positive");
        this.numLookObjects = numLookObjects;
    }

    /** {@inheritDoc} */
    @Override
    public int getMapWidth()
    {
        return mapWidth;
    }

    /** {@inheritDoc} */
    @Override
    public int getMapHeight()
    {
        return mapHeight;
    }

    /**
     * Notifies all {@link ReceivedPacketListener}s about an empty packet.
     * @param command the command string
     */
    private void notifyPacketWatcherListenersEmpty(final String command)
    {
        for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners)
        {
            receivedPacketListener.processEmpty(command);
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener}s about a packet having ascii
     * parameters.
     * @param packet the packet contents
     * @param start the start index into <code>packet</code>
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     * @param end the end index into <code>packet</code>
     */
    private void notifyPacketWatcherListenersAscii(final byte[] packet, final int start, final int args, final int end)
    {
        if (!receivedPacketListeners.isEmpty())
        {
            final String command = extractCommand(packet, start, end);
            if (start >= end)
            {
                notifyPacketWatcherListenersEmpty(command);
            }
            else
            {
                for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners)
                {
                    receivedPacketListener.processAscii(command, packet, args, end);
                }
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener}s about a packet having an
     * array of short values as parameters.
     * @param packet the packet contents
     * @param start the start index into <code>packet</code>
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     * @param end the end index into <code>packet</code>
     */
    private void notifyPacketWatcherListenersShortArray(final byte[] packet, final int start, final int args, final int end)
    {
        if (!receivedPacketListeners.isEmpty())
        {
            final String command = extractCommand(packet, start, end);
            if (start >= end)
            {
                notifyPacketWatcherListenersEmpty(command);
            }
            else
            {
                for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners)
                {
                    receivedPacketListener.processShortArray(command, packet, args, end);
                }
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener}s about a packet having an
     * array of int values as parameters.
     * @param packet the packet contents
     * @param start the start index into <code>packet</code>
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     * @param end the end index into <code>packet</code>
     */
    private void notifyPacketWatcherListenersIntArray(final byte[] packet, final int start, final int args, final int end)
    {
        if (!receivedPacketListeners.isEmpty())
        {
            final String command = extractCommand(packet, start, end);
            if (start >= end)
            {
                notifyPacketWatcherListenersEmpty(command);
            }
            else
            {
                for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners)
                {
                    receivedPacketListener.processIntArray(command, packet, args, end);
                }
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener}s about a packet having a
     * short and an in value as parameters.
     * @param packet the packet contents
     * @param start the start index into <code>packet</code>
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     * @param end the end index into <code>packet</code>
     */
    private void notifyPacketWatcherListenersShortInt(final byte[] packet, final int start, final int args, final int end)
    {
        if (!receivedPacketListeners.isEmpty())
        {
            final String command = extractCommand(packet, start, end);
            if (start >= end)
            {
                notifyPacketWatcherListenersEmpty(command);
            }
            else
            {
                for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners)
                {
                    receivedPacketListener.processShortInt(command, packet, args, end);
                }
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener}s about a packet having mixed
     * parameters.
     * @param packet the packet contents
     * @param start the start index into <code>packet</code>
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     * @param end the end index into <code>packet</code>
     */
    private void notifyPacketWatcherListenersMixed(final byte[] packet, final int start, final int args, final int end)
    {
        if (!receivedPacketListeners.isEmpty())
        {
            final String command = extractCommand(packet, start, end);
            if (start >= end)
            {
                notifyPacketWatcherListenersEmpty(command);
            }
            else
            {
                for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners)
                {
                    receivedPacketListener.processMixed(command, packet, args, end);
                }
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener}s about a packet having stat
     * parameters.
     * @param stat the stat value
     * @param args the stat arguments depending on <code>type</code> and
     * <code>stat</code>
     */
    private void notifyPacketWatcherListenersStats(final int stat, final Object... args)
    {
        if (!receivedPacketListeners.isEmpty())
        {
            for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners)
            {
                receivedPacketListener.processStats("stats", stat, args);
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener}s about a packet having
     * unknown parameters.
     * @param packet the packet contents
     * @param start the start index into <code>packet</code>
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     * @param end the end index into <code>packet</code>
     */
    private void notifyPacketWatcherListenersNodata(final byte[] packet, final int start, final int args, final int end)
    {
        if (!receivedPacketListeners.isEmpty())
        {
            final String command = extractCommand(packet, start, end);
            if (start >= end)
            {
                notifyPacketWatcherListenersEmpty(command);
            }
            else
            {
                for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners)
                {
                    receivedPacketListener.processNodata(command, packet, args, end);
                }
            }
        }
    }
}

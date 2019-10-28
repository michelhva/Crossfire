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

import com.realtime.crossfire.jxclient.account.CharacterInformation;
import com.realtime.crossfire.jxclient.server.server.ReceivedPacketListener;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for {@link CrossfireServerConnection} implementing
 * classes.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractCrossfireServerConnection implements CrossfireServerConnection {

    /**
     * The {@link Charset} used for parsing or encoding strings received from or
     * sent to the Crossfire server.
     */
    @NotNull
    protected static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * The {@link Model} instance that is updated.
     */
    @NotNull
    private final Model model;

    /**
     * The {@link CrossfireDrawinfoListener CrossfireDrawinfoListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireDrawinfoListener> drawinfoListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireDrawextinfoListener CrossfireDrawextinfoListeners} to
     * be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireDrawextinfoListener> drawextinfoListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireQueryListener CrossfireQueryListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireQueryListener> queryListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireMagicmapListener CrossfireMagicmapListeners} to be
     * notified of received magicmap commands.
     */
    @NotNull
    private final EventListenerList2<CrossfireMagicmapListener> magicmapListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireUpdateItemListener CrossfireUpdateItemListeners} to
     * be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireUpdateItemListener> crossfireUpdateItemListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireTickListener CrossfireTickListeners} to be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireTickListener> crossfireTickListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireSoundListener CrossfireSoundListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireSoundListener> crossfireSoundListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireMusicListener CrossfireMusicListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireMusicListener> crossfireMusicListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireComcListener CrossfireComcListeners} to be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireComcListener> crossfireComcListeners = new EventListenerList2<>();

    /**
     * The {@link ReceivedPacketListener ReceivedPacketListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<ReceivedPacketListener> receivedPacketListeners = new EventListenerList2<>();

    /**
     * The {@link SentReplyListener SentReplyListeners} to be notified.
     */
    @NotNull
    private final EventListenerList2<SentReplyListener> sentReplyListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfirePickupListener CrossfirePickupListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfirePickupListener> crossfirePickupListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireAccountListener CrossfireAccountListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireAccountListener> crossfireAccountListeners = new EventListenerList2<>();

    /**
     * The {@link CrossfireFailureListener CrossfireFailureListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireFailureListener> crossfireFailureListeners = new EventListenerList2<>();

    /**
     * Creates a new instance.
     * @param model the model instance to update
     */
    protected AbstractCrossfireServerConnection(@NotNull final Model model) {
        this.model = model;
    }

    @Override
    public void addCrossfireDrawinfoListener(@NotNull final CrossfireDrawinfoListener listener) {
        drawinfoListeners.add(listener);
    }

    @Override
    public void removeCrossfireDrawinfoListener(@NotNull final CrossfireDrawinfoListener listener) {
        drawinfoListeners.remove(listener);
    }

    @Override
    public void addCrossfireDrawextinfoListener(@NotNull final CrossfireDrawextinfoListener listener) {
        drawextinfoListeners.add(listener);
    }

    @Override
    public void removeCrossfireDrawextinfoListener(@NotNull final CrossfireDrawextinfoListener listener) {
        drawextinfoListeners.remove(listener);
    }

    @Override
    public void addCrossfireQueryListener(@NotNull final CrossfireQueryListener listener) {
        queryListeners.add(listener);
    }

    @Override
    public void removeCrossfireQueryListener(@NotNull final CrossfireQueryListener listener) {
        queryListeners.remove(listener);
    }

    @Override
    public void addCrossfireMagicmapListener(@NotNull final CrossfireMagicmapListener listener) {
        magicmapListeners.add(listener);
    }

    @Override
    public void removeCrossfireMagicmapListener(@NotNull final CrossfireMagicmapListener listener) {
        magicmapListeners.remove(listener);
    }

    @Override
    public void addCrossfireUpdateItemListener(@NotNull final CrossfireUpdateItemListener crossfireUpdateItemListener) {
        crossfireUpdateItemListeners.add(crossfireUpdateItemListener);
    }

    @Override
    public void removeCrossfireUpdateItemListener(@NotNull final CrossfireUpdateItemListener crossfireUpdateItemListener) {
        crossfireUpdateItemListeners.remove(crossfireUpdateItemListener);
    }

    @Override
    public void addCrossfireTickListener(@NotNull final CrossfireTickListener listener) {
        crossfireTickListeners.add(listener);
    }

    @Override
    public void addCrossfireSoundListener(@NotNull final CrossfireSoundListener listener) {
        crossfireSoundListeners.add(listener);
    }

    @Override
    public void addCrossfireMusicListener(@NotNull final CrossfireMusicListener listener) {
        crossfireMusicListeners.add(listener);
    }

    @Override
    public void addCrossfireComcListener(@NotNull final CrossfireComcListener listener) {
        crossfireComcListeners.add(listener);
    }

    @Override
    public void addPacketWatcherListener(@NotNull final ReceivedPacketListener listener) {
        receivedPacketListeners.add(listener);
    }

    @Override
    public void addCrossfireAccountListener(@NotNull final CrossfireAccountListener listener) {
        crossfireAccountListeners.add(listener);
    }

    @Override
    public void removeCrossfireAccountListener(@NotNull final CrossfireAccountListener listener) {
        crossfireAccountListeners.remove(listener);
    }

    @Override
    public void removePacketWatcherListener(@NotNull final ReceivedPacketListener listener) {
        receivedPacketListeners.remove(listener);
    }

    @Override
    public void addSentReplyListener(@NotNull final SentReplyListener listener) {
        sentReplyListeners.add(listener);
    }

    @Override
    public void removeSentReplyListener(@NotNull final SentReplyListener listener) {
        sentReplyListeners.remove(listener);
    }

    @Override
    public void addCrossfirePickupListener(@NotNull final CrossfirePickupListener listener) {
        crossfirePickupListeners.add(listener);
    }

    @Override
    public void removeCrossfirePickupListener(@NotNull final CrossfirePickupListener listener) {
        crossfirePickupListeners.remove(listener);
    }

    @Override
    public void addCrossfireFailureListener(@NotNull final CrossfireFailureListener listener) {
        crossfireFailureListeners.add(listener);
    }

    @Override
    public void removeCrossfireFailureListener(@NotNull final CrossfireFailureListener listener) {
        crossfireFailureListeners.remove(listener);
    }

    protected void fireManageAccount() {
        for (CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners) {
            crossfireAccountListener.manageAccount();
        }
    }

    protected void fireStartAccountList(@NotNull final String accountName) {
        for (CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners) {
            crossfireAccountListener.startAccountList(accountName);
        }
    }

    protected void fireAddAccount(@NotNull final CharacterInformation characterInformation) {
        for (CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners) {
            crossfireAccountListener.addAccount(characterInformation);
        }
    }

    protected void fireEndAccountList(final int count) {
        for (CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners) {
            crossfireAccountListener.endAccountList(count);
        }
    }

    protected void fireStartPlaying() {
        for (CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners) {
            crossfireAccountListener.startPlaying();
        }
    }

    protected void fireCommandComcReceived(final int packetNo, final int time) {
        for (CrossfireComcListener listener : crossfireComcListeners) {
            listener.commandComcReceived(packetNo, time);
        }
    }

    protected void fireDelinvReceived(final int tag) {
        model.getItemsManager().delinvReceived(tag);
        for (CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners) {
            crossfireUpdateItemListener.delinvReceived(tag);
        }
    }

    protected void fireDelitemReceived(@NotNull final int[] tags) {
        model.getItemsManager().delitemReceived(tags);
        for (CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners) {
            crossfireUpdateItemListener.delitemReceived(tags);
        }
    }

    protected void fireFaceReceived(final int faceNum, final int faceSetNum, final int faceChecksum, @NotNull final String faceName) {
        model.getFaceCache().addFace(faceNum, faceSetNum, faceChecksum, faceName);
    }

    protected void fireFailure(@NotNull final String command, @NotNull final String arguments) {
        for (CrossfireFailureListener crossfireFailureListener : crossfireFailureListeners) {
            crossfireFailureListener.failure(command, arguments);
        }
    }

    /**
     * Inform the various failure listeners that they can clean the last
     * displayed failure.
     */
    protected void clearFailure() {
        for (CrossfireFailureListener crossfireFailureListener : crossfireFailureListeners) {
            crossfireFailureListener.clearFailure();
        }
    }

    protected void fireAddItemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, final String name, final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
        model.getItemsManager().addItemReceived(location, tag, flags, weight, faceNum, name, namePl, anim, animSpeed, nrof, type);
        for (CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners) {
            crossfireUpdateItemListener.addItemReceived(location, tag, flags, weight, faceNum, name, namePl, anim, animSpeed, nrof, type);
        }
    }

    protected void fireMagicMap() {
        for (CrossfireMagicmapListener listener : magicmapListeners) {
            listener.commandMagicmapReceived();
        }
    }

    protected void fireMusicReceived(@NotNull final String music) {
        for (CrossfireMusicListener listener : crossfireMusicListeners) {
            listener.commandMusicReceived(music);
        }
    }

    protected void firePickupChanged(final int pickupOptions) {
        for (CrossfirePickupListener crossfirePickupListener : crossfirePickupListeners) {
            crossfirePickupListener.pickupChanged(pickupOptions);
        }
    }

    protected void firePlayerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name) {
        model.getItemsManager().playerReceived(tag, weight, faceNum, name);
        for (CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners) {
            crossfireUpdateItemListener.playerReceived(tag, weight, faceNum, name);
        }
    }

    protected void fireCommandQueryReceived(@NotNull final String prompt, final int queryType) {
        for (CrossfireQueryListener listener : queryListeners) {
            listener.commandQueryReceived(prompt, queryType);
        }
    }

    protected void fireCommandSoundReceived(final int x, final int y, final int num, final int type) {
        for (CrossfireSoundListener listener : crossfireSoundListeners) {
            listener.commandSoundReceived(x, y, num, type);
        }
    }

    protected void fireCommandSound2Received(final int x, final int y, final int dir, final int volume, final int type, @NotNull final String action, @NotNull final String name) {
        for (CrossfireSoundListener listener : crossfireSoundListeners) {
            listener.commandSound2Received(x, y, dir, volume, type, action, name);
        }
    }

    protected void fireTick(final int tickNo) {
        for (CrossfireTickListener listener : crossfireTickListeners) {
            listener.tick(tickNo);
        }
    }

    protected void fireUpditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
        model.getItemsManager().upditemReceived(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        for (CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners) {
            crossfireUpdateItemListener.upditemReceived(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        }
    }

    @Override
    public void drawInfo(@NotNull final String message, final int color) {
        for (CrossfireDrawinfoListener listener : drawinfoListeners) {
            listener.commandDrawinfoReceived(message, color);
        }
    }

    @Override
    public void drawextinfo(final int color, final int type, final int subtype, @NotNull final String message) {
        for (CrossfireDrawextinfoListener listener : drawextinfoListeners) {
            listener.commandDrawextinfoReceived(color, type, subtype, message);
        }
    }

    @Override
    public void drawInfoSetDebugMode(final boolean printMessageTypes) {
        for (CrossfireDrawextinfoListener listener : drawextinfoListeners) {
            listener.setDebugMode(printMessageTypes);
        }
    }

    protected void fireReplySent() {
        for (SentReplyListener sentReplyListener : sentReplyListeners) {
            sentReplyListener.replySent();
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * an empty packet.
     * @param command the command string
     */
    private void notifyPacketWatcherListenersEmpty(@NotNull final String command) {
        for (ReceivedPacketListener receivedPacketListener : receivedPacketListeners) {
            receivedPacketListener.processEmpty(command);
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having ascii parameters.
     * @param packet the packet contents
     * @param args the start index into {@code packet} of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersAscii(@NotNull final ByteBuffer packet, final int args) {
        final Iterator<ReceivedPacketListener> listeners = receivedPacketListeners.iterator();
        if (listeners.hasNext()) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                while (listeners.hasNext()) {
                    final ReceivedPacketListener receivedPacketListener = listeners.next();
                    ((Buffer)packet).position(args);
                    receivedPacketListener.processAscii(command, packet);
                }
            } else {
                notifyPacketWatcherListenersEmpty(command);
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having an array of short values as parameters.
     * @param packet the packet contents
     * @param args the start index into {@code packet} of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersShortArray(@NotNull final ByteBuffer packet, final int args) {
        final Iterator<ReceivedPacketListener> listeners = receivedPacketListeners.iterator();
        if (listeners.hasNext()) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                while (listeners.hasNext()) {
                    final ReceivedPacketListener receivedPacketListener = listeners.next();
                    ((Buffer)packet).position(args);
                    receivedPacketListener.processShortArray(command, packet);
                }
            } else {
                notifyPacketWatcherListenersEmpty(command);
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having an array of int values as parameters.
     * @param packet the packet contents
     * @param args the start index into {@code packet} of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersIntArray(@NotNull final ByteBuffer packet, final int args) {
        final Iterator<ReceivedPacketListener> listeners = receivedPacketListeners.iterator();
        if (listeners.hasNext()) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                while (listeners.hasNext()) {
                    final ReceivedPacketListener receivedPacketListener = listeners.next();
                    ((Buffer)packet).position(args);
                    receivedPacketListener.processIntArray(command, packet);
                }
            } else {
                notifyPacketWatcherListenersEmpty(command);
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having a short and an in value as parameters.
     * @param packet the packet contents
     * @param args the start index into {@code packet} of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersShortInt(@NotNull final ByteBuffer packet, final int args) {
        final Iterator<ReceivedPacketListener> listeners = receivedPacketListeners.iterator();
        if (listeners.hasNext()) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                while (listeners.hasNext()) {
                    final ReceivedPacketListener receivedPacketListener = listeners.next();
                    ((Buffer)packet).position(args);
                    receivedPacketListener.processShortInt(command, packet);
                }
            } else {
                notifyPacketWatcherListenersEmpty(command);
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having mixed parameters.
     * @param packet the packet contents
     * @param args the start index into {@code packet} of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersMixed(@NotNull final ByteBuffer packet, final int args) {
        final Iterator<ReceivedPacketListener> listeners = receivedPacketListeners.iterator();
        if (listeners.hasNext()) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                while (listeners.hasNext()) {
                    final ReceivedPacketListener receivedPacketListener = listeners.next();
                    ((Buffer)packet).position(args);
                    receivedPacketListener.processMixed(command, packet);
                }
            } else {
                notifyPacketWatcherListenersEmpty(command);
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having stat parameters.
     * @param stat the stat value
     * @param args the stat arguments depending on {@code type} and {@code
     * stat}
     */
    protected void notifyPacketWatcherListenersStats(final int stat, @NotNull final Object... args) {
        for (ReceivedPacketListener receivedPacketListener : receivedPacketListeners) {
            receivedPacketListener.processStats("stats", stat, args);
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having unknown parameters.
     * @param packet the packet contents
     * @param args the start index into {@code packet} of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersNoData(@NotNull final ByteBuffer packet, final int args) {
        final Iterator<ReceivedPacketListener> listeners = receivedPacketListeners.iterator();
        if (listeners.hasNext()) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                while (listeners.hasNext()) {
                    final ReceivedPacketListener receivedPacketListener = listeners.next();
                    ((Buffer)packet).position(args);
                    receivedPacketListener.processNoData(command, packet);
                }
            } else {
                notifyPacketWatcherListenersEmpty(command);
            }
        }
    }

    /**
     * Returns the command string for a received packet.
     * @param packet the packet contents
     * @return the command string
     */
    @NotNull
    protected static String extractCommand(@NotNull final ByteBuffer packet) {
        int cmdLen;
        for (cmdLen = 0; cmdLen < packet.limit(); cmdLen++) {
            final byte ch = packet.get(cmdLen);
            if ((ch&0xFF) <= 0x20 || (ch&0xFF) >= 0x80) {
                break;
            }
        }
        return newString(packet, 0, cmdLen);
    }

    /**
     * Extracts a string from a {@link ByteBuffer} range.
     * @param byteBuffer the byte buffer
     * @param start the start index of the string
     * @param len the length of the string
     * @return the string
     */
    @NotNull
    protected static String newString(@NotNull final ByteBuffer byteBuffer, final int start, final int len) {
        final byte[] tmp = new byte[len];
        for (int i = 0; i < len; i++) {
            tmp[i] = byteBuffer.get(start+i);
        }
        return new String(tmp, UTF8);
    }

    protected void fireSelectCharacter(@NotNull final String accountName, @NotNull final String characterName) {
        model.getSpellsManager().selectCharacter();
        model.getQuestsManager().selectCharacter();
        model.getKnowledgeManager().selectCharacter();
        for (CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners) {
            crossfireAccountListener.selectCharacter(accountName, characterName);
        }
    }

}

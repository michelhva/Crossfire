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

import com.realtime.crossfire.jxclient.map.Location;
import com.realtime.crossfire.jxclient.server.server.ReceivedPacketListener;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final EventListenerList2<CrossfireDrawinfoListener> drawinfoListeners = new EventListenerList2<CrossfireDrawinfoListener>(CrossfireDrawinfoListener.class);

    /**
     * The {@link CrossfireDrawextinfoListener CrossfireDrawextinfoListeners} to
     * be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireDrawextinfoListener> drawextinfoListeners = new EventListenerList2<CrossfireDrawextinfoListener>(CrossfireDrawextinfoListener.class);

    /**
     * The {@link CrossfireQueryListener CrossfireQueryListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireQueryListener> queryListeners = new EventListenerList2<CrossfireQueryListener>(CrossfireQueryListener.class);

    /**
     * The {@link CrossfireMagicmapListener CrossfireMagicmapListeners} to be
     * notified of received magicmap commands.
     */
    @NotNull
    private final EventListenerList2<CrossfireMagicmapListener> magicmapListeners = new EventListenerList2<CrossfireMagicmapListener>(CrossfireMagicmapListener.class);

    /**
     * The {@link CrossfireUpdateItemListener CrossfireUpdateItemListeners} to
     * be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireUpdateItemListener> crossfireUpdateItemListeners = new EventListenerList2<CrossfireUpdateItemListener>(CrossfireUpdateItemListener.class);

    /**
     * The {@link CrossfireUpdateMapListener} to be notified. Set to
     * <code>null</code> if unset.
     */
    @Nullable
    protected CrossfireUpdateMapListener crossfireUpdateMapListener;

    /**
     * The {@link CrossfireTickListener CrossfireTickListeners} to be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireTickListener> crossfireTickListeners = new EventListenerList2<CrossfireTickListener>(CrossfireTickListener.class);

    /**
     * The {@link CrossfireSoundListener CrossfireSoundListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireSoundListener> crossfireSoundListeners = new EventListenerList2<CrossfireSoundListener>(CrossfireSoundListener.class);

    /**
     * The {@link CrossfireMusicListener CrossfireMusicListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireMusicListener> crossfireMusicListeners = new EventListenerList2<CrossfireMusicListener>(CrossfireMusicListener.class);

    /**
     * The {@link CrossfireComcListener CrossfireComcListeners} to be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireComcListener> crossfireComcListeners = new EventListenerList2<CrossfireComcListener>(CrossfireComcListener.class);

    /**
     * The {@link CrossfireFaceListener CrossfireFaceListeners} to be notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireFaceListener> crossfireFaceListeners = new EventListenerList2<CrossfireFaceListener>(CrossfireFaceListener.class);

    /**
     * The {@link ReceivedPacketListener ReceivedPacketListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<ReceivedPacketListener> receivedPacketListeners = new EventListenerList2<ReceivedPacketListener>(ReceivedPacketListener.class);

    /**
     * The {@link SentReplyListener SentReplyListeners} to be notified.
     */
    @NotNull
    private final EventListenerList2<SentReplyListener> sentReplyListeners = new EventListenerList2<SentReplyListener>(SentReplyListener.class);

    /**
     * The {@link CrossfirePickupListener CrossfirePickupListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfirePickupListener> crossfirePickupListeners = new EventListenerList2<CrossfirePickupListener>(CrossfirePickupListener.class);

    /**
     * The {@link CrossfireAccountListener CrossfireAccountListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireAccountListener> crossfireAccountListeners = new EventListenerList2<CrossfireAccountListener>(CrossfireAccountListener.class);

    /**
     * The {@link CrossfireFailureListener CrossfireFailureListeners} to be
     * notified.
     */
    @NotNull
    private final EventListenerList2<CrossfireFailureListener> crossfireFailureListeners = new EventListenerList2<CrossfireFailureListener>(CrossfireFailureListener.class);

    /**
     * Creates a new instance.
     * @param model the model instance to update
     */
    protected AbstractCrossfireServerConnection(@NotNull final Model model) {
        this.model = model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireDrawinfoListener(@NotNull final CrossfireDrawinfoListener listener) {
        drawinfoListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCrossfireDrawinfoListener(@NotNull final CrossfireDrawinfoListener listener) {
        drawinfoListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireDrawextinfoListener(@NotNull final CrossfireDrawextinfoListener listener) {
        drawextinfoListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCrossfireDrawextinfoListener(@NotNull final CrossfireDrawextinfoListener listener) {
        drawextinfoListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireQueryListener(@NotNull final CrossfireQueryListener listener) {
        queryListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCrossfireQueryListener(@NotNull final CrossfireQueryListener listener) {
        queryListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireMagicmapListener(@NotNull final CrossfireMagicmapListener listener) {
        magicmapListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCrossfireMagicmapListener(@NotNull final CrossfireMagicmapListener listener) {
        magicmapListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireUpdateItemListener(@NotNull final CrossfireUpdateItemListener crossfireUpdateItemListener) {
        crossfireUpdateItemListeners.add(crossfireUpdateItemListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCrossfireUpdateItemListener(@NotNull final CrossfireUpdateItemListener crossfireUpdateItemListener) {
        crossfireUpdateItemListeners.remove(crossfireUpdateItemListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCrossfireUpdateMapListener(@Nullable final CrossfireUpdateMapListener listener) {
        if (listener != null && crossfireUpdateMapListener != null) {
            throw new IllegalStateException();
        }
        crossfireUpdateMapListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireTickListener(@NotNull final CrossfireTickListener listener) {
        crossfireTickListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireSoundListener(@NotNull final CrossfireSoundListener listener) {
        crossfireSoundListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireMusicListener(@NotNull final CrossfireMusicListener listener) {
        crossfireMusicListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireComcListener(@NotNull final CrossfireComcListener listener) {
        crossfireComcListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireFaceListener(@NotNull final CrossfireFaceListener listener) {
        crossfireFaceListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPacketWatcherListener(@NotNull final ReceivedPacketListener listener) {
        receivedPacketListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireAccountListener(@NotNull final CrossfireAccountListener listener) {
        crossfireAccountListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCrossfireAccountListener(@NotNull final CrossfireAccountListener listener) {
        crossfireAccountListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePacketWatcherListener(@NotNull final ReceivedPacketListener listener) {
        receivedPacketListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSentReplyListener(@NotNull final SentReplyListener listener) {
        sentReplyListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSentReplyListener(@NotNull final SentReplyListener listener) {
        sentReplyListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfirePickupListener(@NotNull final CrossfirePickupListener listener) {
        crossfirePickupListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCrossfirePickupListener(@NotNull final CrossfirePickupListener listener) {
        crossfirePickupListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCrossfireFailureListener(@NotNull final CrossfireFailureListener listener) {
        crossfireFailureListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCrossfireFailureListener(@NotNull final CrossfireFailureListener listener) {
        crossfireFailureListeners.remove(listener);
    }

    protected void fireMapClear(final int x, final int y) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapClear(x, y);
    }

    protected void fireMapDarkness(final int x, final int y, final int darkness) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapDarkness(x, y, darkness);
    }

    protected void fireMapFace(@NotNull final Location location, final int face) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapFace(location, face);
    }

    protected void fireMapAnimation(@NotNull final Location location, final int animationNum, final int animationType) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapAnimation(location, animationNum, animationType);
    }

    protected void fireMapSmooth(@NotNull final Location location, final int smooth) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapSmooth(location, smooth);
    }

    protected void fireMapAnimationSpeed(@NotNull final Location location, final int animSpeed) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapAnimationSpeed(location, animSpeed);
    }

    protected void fireManageAccount() {
        for (final CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners.getListeners()) {
            crossfireAccountListener.manageAccount();
        }
    }

    protected void fireStartAccountList(@NotNull final String accountName) {
        for (final CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners.getListeners()) {
            crossfireAccountListener.startAccountList(accountName);
        }
    }

    protected void fireAddAccount(@NotNull final String name, @NotNull final String characterClass, @NotNull final String race, @NotNull final String face, @NotNull final String party, @NotNull final String map, final int level, final int faceNumber) {
        for (final CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners.getListeners()) {
            crossfireAccountListener.addAccount(name, characterClass, race, face, party, map, level, faceNumber);
        }
    }

    protected void fireEndAccountList() {
        for (final CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners.getListeners()) {
            crossfireAccountListener.endAccountList();
        }
    }

    protected void fireStartPlaying() {
        for (final CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners.getListeners()) {
            crossfireAccountListener.startPlaying();
        }
    }

    protected void fireAddAnimation(final int animation, final int flags, @NotNull final int[] faces) {
        if (crossfireUpdateMapListener != null) {
            crossfireUpdateMapListener.addAnimation(animation, flags, faces);
        }
    }

    protected void fireCommandComcReceived(final int packetNo, final int time) {
        for (final CrossfireComcListener listener : crossfireComcListeners.getListeners()) {
            listener.commandComcReceived(packetNo, time);
        }
    }

    protected void fireDelinvReceived(final int tag) {
        model.getItemsManager().delinvReceived(tag);
        for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners.getListeners()) {
            crossfireUpdateItemListener.delinvReceived(tag);
        }
    }

    protected void fireDelitemReceived(@NotNull final int[] tags) {
        model.getItemsManager().delitemReceived(tags);
        for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners.getListeners()) {
            crossfireUpdateItemListener.delitemReceived(tags);
        }
    }

    protected void fireFaceReceived(final int faceNum, final int faceSetNum, final int faceChecksum, @NotNull final String faceName) {
        model.getFaceCache().addFace(faceNum, faceSetNum, faceChecksum, faceName);
        for (final CrossfireFaceListener crossfireFaceListener : crossfireFaceListeners.getListeners()) {
            crossfireFaceListener.faceReceived(faceNum, faceSetNum, faceChecksum, faceName);
        }
    }

    protected void fireFailure(@NotNull final String command, @NotNull final String arguments) {
        for (final CrossfireFailureListener crossfireFailureListener : crossfireFailureListeners.getListeners()) {
            crossfireFailureListener.failure(command, arguments);
        }
    }

    /**
     * Inform the various failure listeners that they can clean the last
     * displayed failure.
     */
    protected void clearFailure() {
        for (final CrossfireFailureListener crossfireFailureListener : crossfireFailureListeners.getListeners()) {
            crossfireFailureListener.clearFailure();
        }
    }

    protected void fireAddItemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, final String name, final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
        model.getItemsManager().addItemReceived(location, tag, flags, weight, faceNum, name, namePl, anim, animSpeed, nrof, type);
        for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners.getListeners()) {
            crossfireUpdateItemListener.addItemReceived(location, tag, flags, weight, faceNum, name, namePl, anim, animSpeed, nrof, type);
        }
    }

    protected void fireMagicMap(final int x, final int y, @NotNull final byte[][] data) {
        if (crossfireUpdateMapListener != null) {
            synchronized (crossfireUpdateMapListener.mapBegin()) {
                assert crossfireUpdateMapListener != null;
                crossfireUpdateMapListener.magicMap(x, y, data);
                assert crossfireUpdateMapListener != null;
                crossfireUpdateMapListener.mapEnd();
            }
        }
        for (final CrossfireMagicmapListener listener : magicmapListeners.getListeners()) {
            listener.commandMagicmapReceived();
        }
    }

    protected void fireMusicReceived(@NotNull final String music) {
        for (final CrossfireMusicListener listener : crossfireMusicListeners.getListeners()) {
            listener.commandMusicReceived(music);
        }
    }

    protected void firePickupChanged(final int pickupOptions) {
        for (final CrossfirePickupListener crossfirePickupListener : crossfirePickupListeners.getListeners()) {
            crossfirePickupListener.pickupChanged(pickupOptions);
        }
    }

    protected void firePlayerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name) {
        model.getItemsManager().playerReceived(tag, weight, faceNum, name);
        for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners.getListeners()) {
            crossfireUpdateItemListener.playerReceived(tag, weight, faceNum, name);
        }
    }

    protected void fireCommandQueryReceived(@NotNull final String prompt, final int queryType) {
        for (final CrossfireQueryListener listener : queryListeners.getListeners()) {
            listener.commandQueryReceived(prompt, queryType);
        }
    }

    protected void fireCommandSoundReceived(final int x, final int y, final int num, final int type) {
        for (final CrossfireSoundListener listener : crossfireSoundListeners.getListeners()) {
            listener.commandSoundReceived(x, y, num, type);
        }
    }

    protected void fireCommandSound2Received(final int x, final int y, final int dir, final int volume, final int type, @NotNull final String action, @NotNull final String name) {
        for (final CrossfireSoundListener listener : crossfireSoundListeners.getListeners()) {
            listener.commandSound2Received(x, y, dir, volume, type, action, name);
        }
    }

    protected void fireTick(final int tickNo) {
        for (final CrossfireTickListener listener : crossfireTickListeners.getListeners()) {
            listener.tick(tickNo);
        }
    }

    protected void fireUpditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
        model.getItemsManager().upditemReceived(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        for (final CrossfireUpdateItemListener crossfireUpdateItemListener : crossfireUpdateItemListeners.getListeners()) {
            crossfireUpdateItemListener.upditemReceived(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawInfo(@NotNull final String message, final int color) {
        for (final CrossfireDrawinfoListener listener : drawinfoListeners.getListeners()) {
            listener.commandDrawinfoReceived(message, color);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawextinfo(final int color, final int type, final int subtype, final String message) {
        for (final CrossfireDrawextinfoListener listener : drawextinfoListeners.getListeners()) {
            listener.commandDrawextinfoReceived(color, type, subtype, message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawInfoSetDebugMode(final boolean printMessageTypes) {
        for (final CrossfireDrawextinfoListener listener : drawextinfoListeners.getListeners()) {
            listener.setDebugMode(printMessageTypes);
        }
    }

    protected void fireReplySent() {
        for (final SentReplyListener sentReplyListener : sentReplyListeners.getListeners()) {
            sentReplyListener.replySent();
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * an empty packet.
     * @param command the command string
     */
    protected void notifyPacketWatcherListenersEmpty(@NotNull final String command) {
        for (final ReceivedPacketListener receivedPacketListener : receivedPacketListeners.getListeners()) {
            receivedPacketListener.processEmpty(command);
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having ascii parameters.
     * @param packet the packet contents
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersAscii(@NotNull final ByteBuffer packet, final int args) {
        final ReceivedPacketListener[] listeners = receivedPacketListeners.getListeners();
        if (listeners.length > 0) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                for (final ReceivedPacketListener receivedPacketListener : listeners) {
                    packet.position(args);
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
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersShortArray(@NotNull final ByteBuffer packet, final int args) {
        final ReceivedPacketListener[] listeners = receivedPacketListeners.getListeners();
        if (listeners.length > 0) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                for (final ReceivedPacketListener receivedPacketListener : listeners) {
                    packet.position(args);
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
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersIntArray(@NotNull final ByteBuffer packet, final int args) {
        final ReceivedPacketListener[] listeners = receivedPacketListeners.getListeners();
        if (listeners.length > 0) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                for (final ReceivedPacketListener receivedPacketListener : listeners) {
                    packet.position(args);
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
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersShortInt(@NotNull final ByteBuffer packet, final int args) {
        final ReceivedPacketListener[] listeners = receivedPacketListeners.getListeners();
        if (listeners.length > 0) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                for (final ReceivedPacketListener receivedPacketListener : listeners) {
                    packet.position(args);
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
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersMixed(@NotNull final ByteBuffer packet, final int args) {
        final ReceivedPacketListener[] listeners = receivedPacketListeners.getListeners();
        if (listeners.length > 0) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                for (final ReceivedPacketListener receivedPacketListener : listeners) {
                    packet.position(args);
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
     * @param args the stat arguments depending on <code>type</code> and
     * <code>stat</code>
     */
    protected void notifyPacketWatcherListenersStats(final int stat, @NotNull final Object... args) {
        final ReceivedPacketListener[] listeners = receivedPacketListeners.getListeners();
        if (listeners.length > 0) {
            for (final ReceivedPacketListener receivedPacketListener : listeners) {
                receivedPacketListener.processStats("stats", stat, args);
            }
        }
    }

    /**
     * Notifies all {@link ReceivedPacketListener ReceivedPacketListeners} about
     * a packet having unknown parameters.
     * @param packet the packet contents
     * @param args the start index into <code>packet</code> of the packet's
     * arguments
     */
    protected void notifyPacketWatcherListenersNoData(@NotNull final ByteBuffer packet, final int args) {
        final ReceivedPacketListener[] listeners = receivedPacketListeners.getListeners();
        if (listeners.length > 0) {
            final String command = extractCommand(packet);
            if (packet.hasRemaining()) { // XXX: should check payload, not whole command?
                for (final ReceivedPacketListener receivedPacketListener : listeners) {
                    packet.position(args);
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
    protected static String newString(final ByteBuffer byteBuffer, final int start, final int len) {
        final byte[] tmp = new byte[len];
        for (int i = 0; i < len; i++) {
            tmp[i] = byteBuffer.get(start+i);
        }
        return new String(tmp, UTF8);
    }

    protected void fireSelectCharacter(@NotNull final String accountName, @NotNull final String characterName) {
        model.getSpellsManager().selectCharacter();
        model.getQuestsManager().selectCharacter();
        for (final CrossfireAccountListener crossfireAccountListener : crossfireAccountListeners.getListeners()) {
            crossfireAccountListener.selectCharacter(accountName, characterName);
        }
    }

}

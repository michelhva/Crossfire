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

package com.realtime.crossfire.jxclient.stats;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.timeouts.TimeoutEvent;
import com.realtime.crossfire.jxclient.timeouts.Timeouts;
import org.jetbrains.annotations.NotNull;

/**
 * Helper class to synthesize an "is poisoned" stat value. The Crossfire server
 * currently does not send this information, therefore drawinfo messages are
 * tracked.
 * @author Andreas Kirschbaum
 */
public class PoisonWatcher {

    /**
     * Timeout after that the "poisoned" state is reset. This allow to prevent a
     * stuck poison warning if the de-assertion was missed for any reason.
     */
    private static final int TIMEOUT_DE_ASSERT = 10000;

    /**
     * The text message the server sends in poisoned state.
     */
    @NotNull
    private static final String ASSERT_MESSAGE = "You feel very sick...";

    /**
     * The text message the server sends when the poison wears off.
     */
    @NotNull
    private static final String DE_ASSERT_MESSAGE = "You feel much better now.";

    /**
     * The text message the server sends when the poison is cured via a spell.
     */
    @NotNull
    private static final String CURE_MESSAGE = "Your body feels cleansed";

    /**
     * The object used for synchronization.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The stats instance to notify.
     */
    @NotNull
    private final Stats stats;

    /**
     * Whether poisoning is active.
     */
    private boolean active = true;

    /**
     * The drawinfo listener to receive drawinfo messages.
     */
    @NotNull
    private final CrossfireDrawinfoListener drawinfoListener = new CrossfireDrawinfoListener() {

        @Override
        public void commandDrawinfoReceived(@NotNull final String text, final int type) {
            check(text);
        }

    };

    /**
     * The drawextinfo listener to receive drawextinfo messages.
     */
    @NotNull
    private final CrossfireDrawextinfoListener drawextinfoListener = new CrossfireDrawextinfoListener() {

        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, @NotNull final String message) {
            check(message);
        }

        @Override
        public void setDebugMode(final boolean printMessageTypes) {
            // ignore
        }

    };

    /**
     * The timeout event used to turn off poisoning if the de-assert message was
     * missed.
     */
    @NotNull
    private final TimeoutEvent timeoutEvent = new TimeoutEvent() {

        @Override
        public void timeout() {
            setActive(false);
        }

    };

    /**
     * Creates a new instance.
     * @param stats the stats instance to notify
     * @param crossfireServerConnection the connection to watch
     */
    public PoisonWatcher(@NotNull final Stats stats, @NotNull final CrossfireServerConnection crossfireServerConnection) {
        this.stats = stats;
        crossfireServerConnection.addCrossfireDrawinfoListener(drawinfoListener);
        crossfireServerConnection.addCrossfireDrawextinfoListener(drawextinfoListener);
        setActive(false);
    }

    /**
     * Examines a text message.
     * @param message the text message
     */
    private void check(@NotNull final String message) {
        if (message.equals(ASSERT_MESSAGE)) {
            setActive(true);
        } else if (message.equals(DE_ASSERT_MESSAGE) || message.equals(CURE_MESSAGE)) {
            setActive(false);
        }
    }

    /**
     * Sets the current poisoned state.
     * @param active the new poisoned state
     */
    private void setActive(final boolean active) {
        synchronized (sync) {
            if (active) {
                Timeouts.reset(TIMEOUT_DE_ASSERT, timeoutEvent);
            } else {
                Timeouts.remove(timeoutEvent);
            }

            if (this.active == active) {
                return;
            }

            this.active = active;
            stats.setStat(Stats.C_STAT_POISONED, active ? 1 : 0);
        }
    }

}

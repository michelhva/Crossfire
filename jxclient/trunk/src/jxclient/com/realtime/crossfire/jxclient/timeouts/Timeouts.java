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

package com.realtime.crossfire.jxclient.timeouts;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import org.jetbrains.annotations.NotNull;

/**
 * Manages a list of timeouts. Client code can register timeouts with {@link
 * #add(int, TimeoutEvent)} or {@link #reset(int, TimeoutEvent)}. These timeout
 * events are called after the given timeout has expired.
 * @author Andreas Kirschbaum
 */
public class Timeouts {

    /**
     * Contains all pending timeout events. The head element is the next event
     * to deliver.
     */
    @NotNull
    private static final PriorityQueue<Event> EVENTS = new PriorityQueue<Event>();

    /**
     * Maps {@link TimeoutEvent} instance to {@link Event} instance. This
     * information is necessary for removing active timeouts.
     */
    @NotNull
    private static final Map<TimeoutEvent, Event> TIMEOUT_EVENTS = new IdentityHashMap<TimeoutEvent, Event>();

    /**
     * The thread that delivers timeout events.
     */
    @NotNull
    private static final Runnable DELIVER_PENDING_TIMEOUTS = new Runnable() {

        @Override
        public void run() {
            try {
                boolean doWait = true;
                while (!Thread.currentThread().isInterrupted()) {
                    final Event event;
                    final boolean execute;
                    synchronized (EVENTS) {
                        if (doWait) {
                            doWait = false;
                            final Event tmp = EVENTS.peek();
                            if (tmp == null) {
                                EVENTS.wait();
                            } else {
                                //noinspection CallToNativeMethodWhileLocked
                                EVENTS.wait(tmp.getTimeout()-System.currentTimeMillis());
                            }
                        }

                        event = EVENTS.peek();
                        //noinspection CallToNativeMethodWhileLocked
                        execute = event != null && event.getTimeout() <= System.currentTimeMillis();
                        if (execute) {
                            EVENTS.poll();
                        }
                    }
                    if (execute) {
                        event.getTimeoutEvent().timeout();
                    } else {
                        doWait = true;
                    }
                }
            } catch (final InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

    };

    static {
        new Thread(DELIVER_PENDING_TIMEOUTS, "JXClient:Timeouts").start();
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Timeouts() {
    }

    /**
     * Sets the timeout value for a given event. If the event is not yet
     * pending, it is added.
     * @param timeout the new timeout in milliseconds
     * @param timeoutEvent the timeout event to execute
     */
    public static void reset(final int timeout, @NotNull final TimeoutEvent timeoutEvent) {
        synchronized (EVENTS) {
            remove(timeoutEvent);
            add(timeout, timeoutEvent);
        }
    }

    /**
     * Adds a timeout event.
     * @param timeout the timeout in milliseconds
     * @param timeoutEvent the timeout event to execute
     */
    private static void add(final int timeout, @NotNull final TimeoutEvent timeoutEvent) {
        synchronized (EVENTS) {
            assert !TIMEOUT_EVENTS.containsKey(timeoutEvent);

            final Event event = new Event(timeout, timeoutEvent);
            TIMEOUT_EVENTS.put(timeoutEvent, event);
            EVENTS.add(event);
            EVENTS.notifyAll();
        }
    }

    /**
     * Removes a timeout event. If the timeout event is not active, nothing
     * happens.
     * @param timeoutEvent the timeout event to remove
     */
    public static void remove(@NotNull final TimeoutEvent timeoutEvent) {
        synchronized (EVENTS) {
            final Event event = TIMEOUT_EVENTS.remove(timeoutEvent);
            if (event != null) {
                EVENTS.remove(event);
            }
        }
    }

}

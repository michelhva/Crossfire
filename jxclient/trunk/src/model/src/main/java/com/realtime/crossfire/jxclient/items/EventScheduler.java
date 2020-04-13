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

package com.realtime.crossfire.jxclient.items;

import org.jetbrains.annotations.NotNull;

/**
 * A scheduler for asynchronous event notifications. Notifications are triggered
 * by calling {@link #trigger()}. Notifications are delivered by calling the
 * event scheduler callback of {@link #eventSchedulerCallback}. This callback is
 * called {@link #delay} after the last call to {@link #trigger()} but not
 * faster than once per {@link #eventSchedulerCallback}.
 * @author Andreas Kirschbaum
 */
public class EventScheduler {

    /**
     * The delay between a call to {@link #trigger()} until the {@link
     * #eventSchedulerCallback} is notified.
     */
    private final int delay;

    /**
     * The minimum delay between two {@link #eventSchedulerCallback}
     * notifications.
     */
    private final int afterEventDelay;

    /**
     * The {@link Runnable} to notify.
     */
    @NotNull
    private final Runnable eventSchedulerCallback;

    /**
     * The object used to synchronize access to {@link #nextAction} and {@link
     * #nextActionNotBefore}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The thread running {@link #runnable}.
     */
    @NotNull
    private final Thread thread;

    /**
     * The timestamp for the next notification. Set to {@code 0} when not
     * active.
     */
    private long nextAction;

    /**
     * The minimum timestamp for the next notification.
     */
    private long nextActionNotBefore = System.currentTimeMillis();

    /**
     * The {@link Runnable} delivering notifications through {@link
     * #eventSchedulerCallback}.
     */
    @NotNull
    @SuppressWarnings("FieldCanBeLocal")
    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            while (true) {
                try {
                    final long now = System.currentTimeMillis();
                    final boolean fireEvent;
                    synchronized (sync) {
                        if (nextAction == 0) {
                            //noinspection WaitOrAwaitWithoutTimeout
                            sync.wait();
                            fireEvent = false;
                        } else {
                            final long thisDelay = Math.max(nextAction, nextActionNotBefore)-now;
                            if (thisDelay > 0) {
                                sync.wait(thisDelay);
                                fireEvent = false;
                            } else {
                                fireEvent = true;
                                nextAction = 0;
                                nextActionNotBefore = System.currentTimeMillis()+afterEventDelay;
                            }
                        }
                    }

                    if (fireEvent) {
                        eventSchedulerCallback.run();
                    }
                } catch (final InterruptedException ignored) {
                    thread.interrupt();
                    break;
                }
            }
        }

    };

    /**
     * Creates a new instance.
     * @param delay the initial delay
     * @param afterEventDelay the "after-event" delay
     * @param eventSchedulerCallback the callback to notify
     */
    public EventScheduler(final int delay, final int afterEventDelay, @NotNull final Runnable eventSchedulerCallback) {
        this.delay = delay;
        this.afterEventDelay = afterEventDelay;
        this.eventSchedulerCallback = eventSchedulerCallback;
        thread = new Thread(runnable, "JXClient:EventScheduler");
        thread.setDaemon(true);
    }

    /**
     * Activates this instance.
     */
    public void start() {
        thread.start();
    }

    /**
     * Notifies the callback.
     */
    public void trigger() {
        final long now = System.currentTimeMillis();
        synchronized (sync) {
            nextAction = now+delay;
            sync.notifyAll();
        }
    }

}

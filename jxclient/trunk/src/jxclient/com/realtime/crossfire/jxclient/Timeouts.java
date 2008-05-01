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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Manages a list of timeouts. Client code can register timeouts with {@link
 * #add(int, TimeoutEvent)} or {@link #reset(int, TimeoutEvent)}. These timeout
 * events are called after the given timeout has expired.
 *
 * @author Andreas Kirschbaum
 */
public class Timeouts
{
    /**
     * Contains all pending timeout events. The head element is the next event
     * to deliver.
     */
    private static final PriorityQueue<Event> events = new PriorityQueue<Event>();

    /**
     * Maps {@link TimeoutEvent} instance to {@link Event} instance. This
     * information is necessary for removing active timeouts.
     */
    private static final Map<TimeoutEvent, Event> timeoutEvents = new IdentityHashMap<TimeoutEvent, Event>();

    /**
     * The thread that delivers timeout events.
     */
    private static Thread deliverPendingTimeouts = new Thread()
    {
        /** {@inheritDoc} */
        @Override public void run()
        {
            boolean doWait = true;
            while (!isInterrupted())
            {
                final Event event;
                final boolean execute;
                synchronized (events)
                {
                    if (doWait)
                    {
                        doWait = false;
                        final Event tmp = events.peek();
                        try
                        {
                            if (tmp == null)
                            {
                                events.wait();
                            }
                            else
                            {
                                events.wait(tmp.getTimeout()-System.currentTimeMillis());
                            }
                        }
                        catch (final InterruptedException ex)
                        {
                            break;
                        }
                    }

                    event = events.peek();
                    execute = event != null && event.getTimeout() <= System.currentTimeMillis();
                    if (execute)
                    {
                        events.poll();
                    }
                }
                if (execute)
                {
                    event.getTimeoutEvent().timeout();
                }
                else
                {
                    doWait = true;
                }
            }
        }
    };
    static
    {
        deliverPendingTimeouts.start();
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Timeouts()
    {
    }

    /**
     * Set the timeout value for a given event. If the event is not yet
     * pending, it is added.
     *
     * @param timeout The new timeout in milliseconds.
     *
     * @param timeoutEvent The timeout event to execute.
     */
    public static void reset(final int timeout, final TimeoutEvent timeoutEvent)
    {
        synchronized (events)
        {
            remove(timeoutEvent);
            add(timeout, timeoutEvent);
        }
    }

    /**
     * Add a timeout event.
     *
     * @param timeout The timeout in milliseconds.
     *
     * @param timeoutEvent The timeout event to execute.
     */
    public static void add(final int timeout, final TimeoutEvent timeoutEvent)
    {
        synchronized (events)
        {
            assert !timeoutEvents.containsKey(timeoutEvent);

            final Event event = new Event(timeout, timeoutEvent);
            timeoutEvents.put(timeoutEvent, event);
            events.add(event);
            events.notifyAll();
        }
    }

    /**
     * Remove a timeout event. If the timeout event is not active, nothing
     * happens.
     *
     * @param timeoutEvent The timeout event to remove.
     */
    public static void remove(final TimeoutEvent timeoutEvent)
    {
        synchronized (events)
        {
            final Event event = timeoutEvents.remove(timeoutEvent);
            if (event != null)
            {
                events.remove(event);
            }
        }
    }

    /**
     * Stores information of a timeout event.
     */
    private static class Event implements Comparable<Event>
    {
        /**
         * The timeout. It is an absolue timestamp as returned by {@link
         * System#currentTimeMillis()}.
         */
        private final long timeout;

        /**
         * The timeout event to execute.
         */
        private final TimeoutEvent timeoutEvent;

        /**
         * Create a new instance.
         *
         * @param timeout The timeout in milliseconds; relative to "now".
         *
         * @param timeoutEvent The timeout event to execute.
         */
        public Event(final int timeout, final TimeoutEvent timeoutEvent)
        {
            this.timeout = System.currentTimeMillis()+timeout;
            this.timeoutEvent = timeoutEvent;
        }

        /**
         * Return the timeout.
         *
         * @return The timeout.
         */
        public long getTimeout()
        {
            return timeout;
        }

        /**
         * Return the timeout event.
         *
         * @return The timeout event.
         */
        public TimeoutEvent getTimeoutEvent()
        {
            return timeoutEvent;
        }

        /** {@inheritDoc} */
        public int compareTo(final Event event)
        {
            if (timeout < event.timeout) return -1;
            if (timeout > event.timeout) return +1;
            return 0;
        }
    }
}

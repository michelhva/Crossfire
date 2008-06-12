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
package com.realtime.crossfire.jxclient.timeouts;

/**
 * Stores information of a timeout event.
 * @author Andreas Kirschbaum
 */
public class Event implements Comparable<Event>
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

    /** {@inheritDoc} */
    public int hashCode()
    {
        return (int)timeout;
    }

    /** {@inheritDoc} */
    public boolean equals(final Object o)
    {
        if(o == null) return false;
        if(o.getClass() != getClass()) return false;
        final Event m = (Event)o;
        return m.timeout == timeout;
    }
}

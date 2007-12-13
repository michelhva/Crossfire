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

import java.util.ArrayList;
import java.util.List;

/**
 * Forwards events to a set of listeners.
 *
 * @author Andreas Kirschbaum
 */
public class ScriptMonitorListeners implements CrossfireScriptMonitorListener
{
    /**
     * The listeners to forward to.
     */
    private final List<CrossfireScriptMonitorListener> scriptMonitorListeners = new ArrayList<CrossfireScriptMonitorListener>();

    /** {@inheritDoc} */
    public void commandSent(final byte[] packet, final int length)
    {
        for (final CrossfireScriptMonitorListener listener : scriptMonitorListeners)
        {
            listener.commandSent(packet, length);
        }
    }

    /**
     * Add a script monitor listener.
     *
     * @param listener The listener to add.
     */
    public void addScriptMonitor(final CrossfireScriptMonitorListener listener)
    {
        scriptMonitorListeners.add(listener);
    }

    /**
     * Remove a script monitor listener.
     *
     * @param listener The listener to remove.
     */
    public void removeScriptMonitor(final CrossfireScriptMonitorListener listener)
    {
        scriptMonitorListeners.remove(listener);
    }
}

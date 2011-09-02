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

package com.realtime.crossfire.jxclient.scripts;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.TestCrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.server.ReceivedPacketListener;
import com.realtime.crossfire.jxclient.test.TestScriptProcess;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression tests for {@link PacketWatcher}.
 * @author Andreas Kirschbaum
 */
public class PacketWatcherTest {

    /**
     * Checks that {@link PacketWatcher#addCommand(String)} does work.
     */
    @Test
    public void test1() {
        final Collection<ReceivedPacketListener> listeners = new ArrayList<ReceivedPacketListener>();
        final CrossfireServerConnection connection = new TestCrossfireServerConnection() {

            @Override
            public void addPacketWatcherListener(@NotNull final ReceivedPacketListener listener) {
                listeners.add(listener);
            }

            @Override
            public void removePacketWatcherListener(@NotNull final ReceivedPacketListener listener) {
                listeners.remove(listener);
            }

        };
        final StringBuilder sb = new StringBuilder();
        final ScriptProcess scriptProcess = new TestScriptProcess() {

            @Override
            public void commandSent(@NotNull final String cmd) {
                sb.append(cmd).append('\n');
            }

        };
        final PacketWatcher packetWatcher = new PacketWatcher(connection, scriptProcess);

        sb.setLength(0);
        for (final ReceivedPacketListener listener : listeners) {
            listener.processEmpty("command");
        }
        Assert.assertEquals("", sb.toString());

        packetWatcher.addCommand("command");

        sb.setLength(0);
        for (final ReceivedPacketListener listener : listeners) {
            listener.processEmpty("comman");
            listener.processEmpty("command");
            listener.processEmpty("commandx");
        }
        Assert.assertEquals("watch command\n"+"watch commandx\n", sb.toString());
    }

}

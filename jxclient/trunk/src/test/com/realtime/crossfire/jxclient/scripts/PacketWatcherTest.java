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
package com.realtime.crossfire.jxclient.scripts;

import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.ReceivedPacketListener;
import com.realtime.crossfire.jxclient.test.TestCrossfireServerConnection;
import com.realtime.crossfire.jxclient.test.TestScriptProcess;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Regression tests for {@link PacketWatcher}.
 * @author Andreas Kirschbaum
 */
public class PacketWatcherTest extends TestCase
{
    /**
     * Creates a new instance.
     * @param name the test case name
     */
    public PacketWatcherTest(final String name)
    {
        super(name);
    }

    /**
     * Creates a new test suite.
     * @return the test suite
     */
    public static Test suite()
    {
        return new TestSuite(PacketWatcherTest.class);
    }

    /**
     * Runs the regression tests.
     * @param args the command line arguments (ignored)
     */
    public static void main(final String[] args)
    {
        TestRunner.run(suite());
    }

    /**
     * Checks that {@link PacketWatcher#addCommand(String)} does work.
     */
    public void test1()
    {
        final Collection<ReceivedPacketListener> listeners = new ArrayList<ReceivedPacketListener>();
        final CrossfireServerConnection connection = new TestCrossfireServerConnection()
        {
            /** {@inheritDoc} */
            @Override
            public void addPacketWatcherListener(final ReceivedPacketListener listener)
            {
                listeners.add(listener);
            }

            /** {@inheritDoc} */
            @Override
            public void removePacketWatcherListener(final ReceivedPacketListener listener)
            {
                listeners.remove(listener);
            }
        };
        final StringBuilder sb = new StringBuilder();
        final ScriptProcess scriptProcess = new TestScriptProcess()
        {
            /** {@inheritDoc} */
            @Override
            public void commandSent(final String cmd)
            {
                sb.append(cmd).append('\n');
            }
        };
        final PacketWatcher packetWatcher = new PacketWatcher(connection, scriptProcess);

        sb.setLength(0);
        for (final ReceivedPacketListener listener : listeners)
        {
            listener.processEmpty("command");
        }
        assertEquals("", sb.toString());

        packetWatcher.addCommand("command");

        sb.setLength(0);
        for (final ReceivedPacketListener listener : listeners)
        {
            listener.processEmpty("comman");
            listener.processEmpty("command");
            listener.processEmpty("commandx");
        }
        assertEquals("watch command\nwatch commandx\n", sb.toString());
    }
}

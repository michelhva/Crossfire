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

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression tests for {@link DefaultCrossfireServerConnection}.
 * @author Andreas Kirschbaum
 */
@SuppressWarnings("OverlyBroadThrowsClause")
public class DefaultCrossfireServerConnectionTest {

    /**
     * Checks that {@link DefaultCrossfireServerConnection#setPreferredNumLookObjects(int)}
     * queues multiple updates.
     * @throws InterruptedException if the test fails
     * @throws IOException if the test fails
     */
    @Test(timeout = 30000)
    public void testNegotiateNumLookObjects1() throws Exception {
        final Model model = new Model();
        final DefaultCrossfireServerConnection connection = new DefaultCrossfireServerConnection(model, null, "version");
        final TestCrossfireServer server = new TestCrossfireServer();
        connection.start();
        try {
            server.start();
            try {
                connection.connect("localhost", server.getLocalPort());
                connection.setPreferredNumLookObjects(10);
                server.waitForCharacterLogin();
                connection.waitForCurrentNumLookObjectsValid();
                Assert.assertEquals(10, connection.getCurrentNumLookObjects());
                connection.setPreferredNumLookObjects(11);
                connection.setPreferredNumLookObjects(12);
                connection.setPreferredNumLookObjects(13);
                connection.setPreferredNumLookObjects(14);
                connection.waitForCurrentNumLookObjectsValid();
                Assert.assertEquals(14, connection.getCurrentNumLookObjects());
            } finally {
                //noinspection ThrowFromFinallyBlock
                server.stop();
            }
        } finally {
            //noinspection ThrowFromFinallyBlock
            connection.stop();
        }
    }

}

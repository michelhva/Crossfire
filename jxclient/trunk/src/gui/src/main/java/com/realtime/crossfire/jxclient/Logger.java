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

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.Filenames;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Logs received messages to a file.
 * @author Andreas Kirschbaum
 */
public class Logger {

    /**
     * The format for writing timestamps.
     */
    @NotNull
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

    /**
     * The hostname.
     */
    @Nullable
    private String hostname;

    /**
     * Whether the message logger is enabled.
     */
    private final boolean enabled;

    /**
     * The {@link CrossfireQueryListener} registered to receive query commands.
     */
    private final CrossfireQueryListener crossfireQueryListener = new CrossfireQueryListener() {

        @Override
        public void commandQueryReceived(@NotNull final String prompt, final int queryType) {
            log(prompt);
        }

    };

    /**
     * The {@link CrossfireDrawextinfoListener} registered to receive
     * drawextinfo commands.
     */
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener() {

        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, @NotNull final String message) {
            log(message);
        }

        @Override
        public void setDebugMode(final boolean printMessageTypes) {
            // ignore
        }

    };

    /**
     * The {@link CrossfireDrawinfoListener} registered to receive drawinfo
     * commands.
     */
    private final CrossfireDrawinfoListener crossfireDrawinfoListener = new CrossfireDrawinfoListener() {

        @Override
        public void commandDrawinfoReceived(@NotNull final String text, final int type) {
            log(text);
        }

    };

    /**
     * Create a new instance.
     * @param crossfireServerConnection the server connection to monitor
     * @param hostname the hostname
     * @param enabled whether the message logger is enabled
     */
    public Logger(@NotNull final CrossfireServerConnection crossfireServerConnection, @Nullable final String hostname, final boolean enabled) {
        this.hostname = hostname;
        this.enabled = enabled;
        crossfireServerConnection.addCrossfireQueryListener(crossfireQueryListener);
        crossfireServerConnection.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        crossfireServerConnection.addCrossfireDrawinfoListener(crossfireDrawinfoListener);
    }

    /**
     * Updates the hostname.
     * @param hostname the new hostname
     */
    public void setHostname(@Nullable final String hostname) {
        this.hostname = hostname;
    }

    /**
     * Adds a message to the log file.
     * @param message the message
     */
    private void log(@NotNull final String message) {
        if (!enabled) {
            return;
        }

        final Date now = new Date();
        try {
            final File file = Filenames.getMessageLogFile(hostname);
            final FileOutputStream fos = new FileOutputStream(file, true);
            try {
                final OutputStreamWriter osw = new OutputStreamWriter(fos);
                try {
                    osw.write(FORMAT.format(now)+message+"\n");
                } finally {
                    osw.close();
                }
            } finally {
                fos.close();
            }
        } catch (final IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}

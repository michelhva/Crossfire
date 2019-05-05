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

package com.realtime.crossfire.jxclient.util;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

/**
 * Writer debug information to a log file.
 * @author Andreas Kirschbaum
 */
public class DebugWriter {

    /**
     * The {@link Writer} to write to.
     */
    @NotNull
    private final Writer writer;

    /**
     * A formatter for timestamps.
     */
    @NotNull
    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");

    /**
     * The object for synchronizing messages.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * Creates a new instance.
     * @param writer the writer to write to
     */
    public DebugWriter(@NotNull final Writer writer) {
        this.writer = writer;
    }

    /**
     * Writes a message to the debug protocol.
     * @param str the message to write
     */
    public void debugProtocolWrite(@NotNull final CharSequence str) {
        synchronized (sync) {
            try {
                writer.append(simpleDateFormat.format(new Date()));
                writer.append(str);
                writer.append("\n");
                writer.flush();
            } catch (final IOException ex) {
                System.err.println("Cannot write debug protocol: "+ex.getMessage());
                System.exit(1);
                throw new AssertionError(ex);
            }
        }
    }

    /**
     * Writes a message to the debug protocol including a throwable.
     * @param str the message to write
     * @param throwable the throwable to log
     */
    public void debugProtocolWrite(@NotNull final CharSequence str, @NotNull final Throwable throwable) {
        synchronized (sync) {
            try {
                writer.append(simpleDateFormat.format(new Date()));
                writer.append(str);
                writer.append("\n");
                for (Throwable t = throwable; t != null; t = t.getCause()) {
                    writer.append(t.getClass().getName());
                    writer.append("\n");
                    for (Object stack : t.getStackTrace()) {
                        writer.append(stack.toString());
                        writer.append("\n");
                    }
                }
                writer.flush();
            } catch (final IOException ex) {
                System.err.println("Cannot write debug protocol: "+ex.getMessage());
                System.exit(1);
                throw new AssertionError(ex);
            }
        }
    }

}

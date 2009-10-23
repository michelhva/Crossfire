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
package com.realtime.crossfire.jxclient.util;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Writer debug information to a log file.
 * @author Andreas Kirschbaum
 */
public class DebugWriter
{
    /**
     * The {@link Writer} to write to.
     */
    private final Writer writer;

    /**
     * A formatter for timestamps.
     */
    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");

    /**
     * The object for synchronizing messages.
     */
    private final Object sync = new Object();

    /**
     * Creates a new instance.
     * @param writer the writer to write to
     */
    public DebugWriter(final Writer writer)
    {
        if (writer == null)
        {
            throw new IllegalArgumentException();
        }

        this.writer = writer;
    }

    /**
     * Writes a message to the debug protocol.
     * @param str the message to write
     */
    public void debugProtocolWrite(final CharSequence str)
    {
        synchronized (sync)
        {
            try
            {
                writer.append(simpleDateFormat.format(new Date()));
                writer.append(str);
                writer.append("\n");
                writer.flush();
            }
            catch (final IOException ex)
            {
                System.err.println("Cannot write debug protocol: "+ex.getMessage());
                System.exit(1);
                throw new AssertionError();
            }
        }
    }

    /**
     * Writes a message to the debug protocol including a throwable.
     * @param str the message to write
     * @param throwable the throwable to log
     */
    public void debugProtocolWrite(final CharSequence str, final Throwable throwable)
    {
        synchronized (sync)
        {
            try
            {
                writer.append(simpleDateFormat.format(new Date()));
                writer.append(str);
                writer.append("\n");
                writer.append(throwable.getClass().getName());
                writer.append("\n");
                for (final Object stack : throwable.getStackTrace())
                {
                    writer.append(stack.toString());
                    writer.append("\n");
                }
                writer.flush();
            }
            catch (final IOException ex)
            {
                System.err.println("Cannot write debug protocol: "+ex.getMessage());
                System.exit(1);
                throw new AssertionError();
            }
        }
    }
}

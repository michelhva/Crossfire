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

import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;

/**
 * Maintains the pending (ncom) commands sent to the server.
 *
 * @author Andreas Kirschbaum
 */
public class CommandQueue
{
    /**
     * The server connection for sending ncom commands.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The default repeat counter for ncom commands.
     */
    private int repeatCount = 0;

    /**
     * Create a new instance.
     *
     * @param crossfireServerConnection The server connection for sending
     * ncom commands.
     */
    public CommandQueue(final CrossfireServerConnection crossfireServerConnection)
    {
        this.crossfireServerConnection = crossfireServerConnection;
    }

    /**
     * Return the current repeat count and reset it to zero.
     *
     * @return The current repeat count.
     */
    public int getRepeatCount()
    {
        final int oldRepeatCount = this.repeatCount;
        resetRepeatCount();
        return oldRepeatCount;
    }

    /**
     * Reset the current repeat count to zero.
     */
    public void resetRepeatCount()
    {
        repeatCount = 0;
    }

    /**
     * Add a digit to the current repeat count.
     *
     * @param digit The digit (0-9) to add.
     */
    public void addToRepeatCount(final int digit)
    {
        assert 0 <= digit && digit <= 9;
        this.repeatCount = (10*repeatCount+digit)%100000;
    }

    /**
     * Forget about sent commands.
     */
    public void clear()
    {
        resetRepeatCount();
    }

    /**
     * Send a "ncom" command to the server. This function uses the default
     * repeat count.
     *
     * @param command The command to send.
     *
     * @see #sendNcom(int, String)
     */
    public void sendNcom(final String command)
    {
        sendNcom(getRepeatCount(), command);
    }

    /**
     * Send a "ncom" command to the server.
     *
     * @param repeat The repeat count.
     *
     * @param command The command to send.
     *
     * @see #sendNcom(String)
     */
    public void sendNcom(final int repeat, final String command)
    {
        crossfireServerConnection.sendNcom(repeat, command);
    }
}

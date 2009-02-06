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

/**
 * Interface for listeners interested in received packets.
 * @author Andreas Kirschbaum
 */
public interface ReceivedPacketListener
{
    /**
     * A packet having no parameters has been received.
     * @param command the command string
     */
    void processEmpty(final String command);

    /**
     * A packet having ascii parameters has been received.
     * @param command the command string
     * @param packet the buffer holding the packet
     * @param start the start index of the packet
     * @param end the end of the packet data
     */
    void processAscii(String command, byte[] packet, int start, int end);

    /**
     * A packet having an array of short integer parameters has been received.
     * @param command the command string
     * @param packet the buffer holding the packet
     * @param start the start index of the packet
     * @param end the end of the packet data
     */
    void processShortArray(String command, byte[] packet, int start, int end);

    /**
     * A packet having an array of int parameters has been received.
     * @param command the command string
     * @param packet the buffer holding the packet
     * @param start the start index of the packet
     * @param end the end of the packet data
     */
    void processIntArray(String command, byte[] packet, int start, int end);

    /**
     * A packet having a short and an int parameter has been received.
     * @param command the command string
     * @param packet the buffer holding the packet
     * @param start the start index of the packet
     * @param end the end of the packet data
     */
    void processShortInt(String command, byte[] packet, int start, int end);

    /**
     * A packet having mixed parameters has been received.
     * @param command the command string
     * @param packet the buffer holding the packet
     * @param start the start index of the packet
     * @param end the end of the packet data
     */
    void processMixed(String command, byte[] packet, int start, int end);

    /**
     * A stats packet has been received.
     * @param command the command string
     * @param stat the stat value
     * @param args the stat arguments depending on <code>type</code> and
     * <code>stat</code>
     */
    void processStats(String command, int stat, Object[] args);

    /**
     * A packet having unknown parameters has been received.
     * @param command the command string
     * @param packet the buffer holding the packet
     * @param start the start index of the packet
     * @param end the end of the packet data
     */
    void processNodata(String command, byte[] packet, int start, int end);
}

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
package com.realtime.crossfire.jxclient.gui.log;

import java.util.List;

/**
 * Interface for listeners for changes of {@link Buffer} contents.
 *
 * @author Andreas Kirschbaum
 */
public interface BufferListener
{
    /**
     * Some lines have been added to the buffer.
     *
     * @param lines The number of lines that have been added to the end of the
     * buffer.
     */
    void linesAdded(int lines);

    /**
     * Some lines have been replaced at the end of the buffer.
     *
     * @param lines The number of lines that have been replaced at the end of
     * the buffer.
     */
    void linesReplaced(int lines);

    /**
     * Some lines have been removed from the buffer.
     *
     * @param lines The lines that have been removed from the beginning of the
     * buffer.
     */
    void linesRemoved(List<Line> lines);
}

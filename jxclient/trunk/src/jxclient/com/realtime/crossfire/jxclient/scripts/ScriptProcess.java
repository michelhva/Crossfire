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

import org.jetbrains.annotations.NotNull;

/**
 * An external command executed as a client-sided script.
 * @author Andreas Kirschbaum
 */
public interface ScriptProcess extends Comparable<ScriptProcess>
{
    /**
     * Returns the script ID identifying this script instance.
     * @return the script ID
     */
    int getScriptId();

    /**
     * Returns the script's filename.
     * @return the script's filename
     */
    @NotNull
    String getFilename();

    /**
     * Sends a message to the script process.
     * @param cmd the message to send
     */
    void commandSent(@NotNull String cmd);

    /**
     * Adds a {@link ScriptProcessListener} to be notified.
     * @param scriptProcessListener the listener to add
     */
    void addScriptProcessListener(@NotNull ScriptProcessListener scriptProcessListener);

    /**
     * Kills the script process. Does nothing if the process is not running.
     */
    void killScript();
}

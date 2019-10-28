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
import com.realtime.crossfire.jxclient.server.server.ReceivedPacketListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketMonitorCommand;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implements the "watch" function for client-sided scripts.
 * @author Andreas Kirschbaum
 */
public class PacketWatcher {

    /**
     * The commands to watch for.
     */
    @NotNull
    private final Collection<String> commands = new HashSet<>();

    /**
     * The {@link CrossfireServerConnection} to watch.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link ScriptProcess} for sending commands.
     */
    @NotNull
    private final ScriptProcess scriptProcess;

    /**
     * A {@link Pattern} matching all {@link #commands}. Set to {@code null}
     * when not watching for commands.
     */
    @Nullable
    private Pattern pattern;

    /**
     * The {@link ReceivedPacketListener} attached to {@link
     * #crossfireServerConnection}. It is attached if and only if {@link
     * #pattern} is non-{@code null}.
     */
    @NotNull
    private final ReceivedPacketListener receivedPacketListener = new ReceivedPacketListener() {

        @Override
        public void process(@NotNull final String command, @NotNull final ClientSocketMonitorCommand args) {
            if (matchesCommand(command)) {
                final String args2 = args.getMonitorCommand();
                if (args2.isEmpty()) {
                    scriptProcess.commandSent("watch "+command);
                } else {
                    scriptProcess.commandSent("watch "+command+" "+args2);
                }
            }
        }

    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the server connection to watch
     * @param scriptProcess the script process for sending commands
     */
    public PacketWatcher(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final ScriptProcess scriptProcess) {
        this.crossfireServerConnection = crossfireServerConnection;
        this.scriptProcess = scriptProcess;
        rebuildPattern();
    }

    /**
     * Releases allocated resources. Must be called before this instance is
     * freed.
     */
    public void destroy() {
        if (pattern != null) {
            pattern = null;
            crossfireServerConnection.removePacketWatcherListener(receivedPacketListener);
        }
    }

    /**
     * Rebuilds {@link #pattern} from {@link #commands}.
     */
    private void rebuildPattern() {
        final StringBuilder sb = new StringBuilder();
        for (String command : commands) {
            sb.append(Pattern.quote(command));
            sb.append(".*|");
        }
        final int length = sb.length();
        if (length <= 0) {
            if (pattern != null) {
                pattern = null;
                crossfireServerConnection.removePacketWatcherListener(receivedPacketListener);
            }
        } else {
            if (pattern == null) {
                crossfireServerConnection.addPacketWatcherListener(receivedPacketListener);
            }
            sb.setLength(length-1);
            pattern = Pattern.compile(sb.toString());
        }
    }

    /**
     * Adds a command to watch for.
     * @param command the command
     */
    public void addCommand(@NotNull final String command) {
        if (commands.add(command)) {
            rebuildPattern();
        }
    }

    /**
     * Removes a command to watch for.
     * @param command the command
     */
    public void removeCommand(@NotNull final String command) {
        if (commands.remove(command)) {
            rebuildPattern();
        }
    }

    /**
     * Returns whether a command matches the currently watched commands.
     * @param command the command
     * @return whether the command matches
     */
    private boolean matchesCommand(@NotNull final CharSequence command) {
        return pattern != null && pattern.matcher(command).matches();
    }

}

package com.realtime.crossfire.jxclient.server.socket;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for querying monitor commands for script processes.
 */
public interface ClientSocketMonitorCommand {

    /**
     * Returns the monitor command to send to the script process.
     * @return the monitor command
     */
    @NotNull
    String getMonitorCommand();

}

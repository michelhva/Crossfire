package com.realtime.crossfire.jxclient.server;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners interested in "replyinfo exp_table" responses.
 */
public interface CrossfireExpTableListener
{
    /**
     * An "replyinfo exp_table" command has been received.
     * @param expTable the experience table; the array smust not be modified
     */
    void expTableReceived(@NotNull long[] expTable);
}

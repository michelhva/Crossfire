package com.realtime.crossfire.jxclient.server;

/**
 * Interface for listeners interested in "replyinfo exp_table" responses.
 */
public interface CrossfireExpTableListener
{
    /**
     * An "replyinfo exp_table" command has been received.
     * @param expTable the experience table; the array smust not be modified
     */
    void expTableReceived(long[] expTable);
}

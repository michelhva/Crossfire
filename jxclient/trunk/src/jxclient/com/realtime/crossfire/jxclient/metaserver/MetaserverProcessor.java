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
package com.realtime.crossfire.jxclient.metaserver;

/**
 * Asynchronously queries the metaserver and updates a {@link MetaserverModel}
 * instance. Queries are performed regularly every {@link #UPDATE_INTERVAL} but
 * not faster than {@link #MIN_UPDATE_INTERVAL}. Queries can be stopped stopped
 * when the server selection GUI is left ({@link #disable()}; a call to {@link
 * #query()} automatically re-starts queries.
 * @author Andreas Kirschbaum
 */
public class MetaserverProcessor
{
    /**
     * The regular update-interval in seconds.
     */
    private static final int UPDATE_INTERVAL = 15*60;

    /**
     * The minimal update-interval in seconds.
     */
    private static final int MIN_UPDATE_INTERVAL = 5*60;

    /**
     * The {@link Metaserver} instance to forward to.
     */
    private final Metaserver metaserver;

    /**
     * The object used for synchronization.
     */
    private final Object sync = new Object();

    /**
     * Whether the query {@link #thread} has been started.
     */
    private boolean running = false;

    /**
     * Counter used by the query {@link #thread}. If positive, the value will
     * be decremented once per second; when it reaches zero, it is reset back
     * to {@link #UPDATE_INTERVAL} and a metaserver query is executed.
     * <p>
     * When set to zero, updating is disabled.
     */
    private int counter = UPDATE_INTERVAL;

    /**
     * The timestamp at which a query is allowed. Used to enforce minimum query
     * intervals ({@link #MIN_UPDATE_INTERVAL}).
     */
    private long nextQuery = System.currentTimeMillis();

    /**
     * The query {@link Thread}.
     */
    private final Thread thread = new Thread()
    {
        /** {@inheritDoc} */
        @Override
        public void run()
        {
            try
            {
                while (!isInterrupted())
                {
                    boolean executeProcess = false;
                    synchronized (sync)
                    {
                        sync.wait(1000);
                        if (counter > 0)
                        {
                            counter--;
                            if (counter == 0)
                            {
                                executeProcess = true;
                                counter = UPDATE_INTERVAL;
                            }
                        }
                    }
                    if (executeProcess)
                    {
                        final long now = System.currentTimeMillis();
                        if (nextQuery <= now)
                        {
                            nextQuery = now+MIN_UPDATE_INTERVAL;
                            metaserver.updateMetalist();
                        }
                    }
                }
            }
            catch (final InterruptedException ex)
            {
                // ignore
            }
        }
    };

    /**
     * Creates a new instance.
     * @param metaserver the metaserver instance to forward to
     */
    public MetaserverProcessor(final Metaserver metaserver)
    {
        this.metaserver = metaserver;
    }

    /**
     * Immediately triggers a metaserver query and enables periodic re-queries.
     * The immediate query is skipped if a recent query has been executed.
     */
    public void query()
    {
        synchronized (sync)
        {
            if (!running)
            {
                running = true;
                thread.start();
            }
            counter = 1;
        }
    }

    /**
     * Disables periodic re-queries. Re-enable periodic queries with {@link
     * #query()}.
     */
    public void disable()
    {
        synchronized (sync)
        {
            counter = 0;
        }
    }
}

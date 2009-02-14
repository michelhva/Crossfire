package com.realtime.crossfire.jxclient.skin.events;

import com.realtime.crossfire.jxclient.server.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.GUICommandList;

/**
 * A {@link SkinEvent} that executes a {@link GUICommandList} whenever a
 * magicmap protocol command is received.
 * @author Andreas Kirschbaum
 */
public class CrossfireMagicmapSkinEvent implements SkinEvent
{
    /**
     * The {@link GUICommandList} to execute.
     */
    private final GUICommandList commandList;

    /**
     * The {@link CrossfireServerConnection} for tracking magicmap commands.
     */
    private final CrossfireServerConnection server;

    /**
     * The {@link CrossfireMagicmapListener} attached to {@link #server}.
     */
    private final CrossfireMagicmapListener crossfireMagicmapListener = new CrossfireMagicmapListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandMagicmapReceived(final int width, final int height, final int px, final int py, final byte[] data, final int pos)
        {
            commandList.execute();
        }
    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute
     * @param server the connection to attach to
     */
    public CrossfireMagicmapSkinEvent(final GUICommandList commandList, final CrossfireServerConnection server)
    {
        this.commandList = commandList;
        this.server = server;
        server.addCrossfireMagicmapListener(crossfireMagicmapListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        server.removeCrossfireMagicmapListener(crossfireMagicmapListener);
    }
}

package com.realtime.crossfire.jxclient.skin.events;

import com.realtime.crossfire.jxclient.window.ConnectionStateListener;
import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.JXCWindow;

/**
 * A {@link SkinEvent} that executes a {@link GUICommandList} at connection
 * setup.
 * @author Andreas Kirschbaum
 */
public class ConnectionStateSkinEvent implements SkinEvent
{
    /**
     * The {@link GUICommandList} to execute.
     */
    private final GUICommandList commandList;

    /**
     * The {@link JXCWindow} to attach to.
     */
    private final JXCWindow window;

    /**
     * The {@link ConnectionStateListener} attached to {@link #window}.
     */
    private final ConnectionStateListener connectionStateListener = new ConnectionStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void connect()
        {
            commandList.execute();
        }

        /** {@inheritDoc} */
        @Override
        public void disconnect()
        {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute
     * @param window the window to attach to
     */
    public ConnectionStateSkinEvent(final GUICommandList commandList, final JXCWindow window)
    {
        this.commandList = commandList;
        this.window = window;
        window.addConnectionStateListener(connectionStateListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        window.removeConnectionStateListener(connectionStateListener);
    }
}

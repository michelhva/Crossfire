package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.server.ConnectionListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.Pickup;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.skin.JXCSkin;
import com.realtime.crossfire.jxclient.util.NumberParser;
import java.awt.Frame;

public class JXCConnection
{
    /**
     * The prefix for the window title.
     */
    private static final String TITLE_PREFIX = "jxclient";

    /**
     * The {@link KeybindingsManager} to update.
     */
    private final KeybindingsManager keybindingsManager;

    /**
     * The {@link ShortcutsManager} to update.
     */
    private final ShortcutsManager shortcutsManager;

    /**
     * The settings instance to use.
     */
    private final Settings settings;

    /**
     * The {@link Frame} for updating the title.
     */
    private final Frame frame;

    /**
     * The {@link Pickup} instance to update.
     */
    private final Pickup characterPickup;

    /**
     * The {@link CrossfireServerConnection} instance used to connect to the
     * Crossfire server.
     */
    private final CrossfireServerConnection server;

    /**
     * The {@link ConnectionListener} to use when connecting.
     */
    private final ConnectionListener connectionListener;

    /**
     * The {@link GuiManager} to use when connecting.
     */
    private final GuiManager guiManager;

    /**
     * The currently connected server. Set to <code>null</code> if unconnected.
     */
    private String hostname = null;

    /**
     * The currently connected port. Only valid if {@link #hostname} is set.
     */
    private int port = 0;

    /**
     * The currently logged in character. Set to <code>null</code> if not
     * logged in.
     */
    private String character = null;

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void start()
        {
            disconnect();
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            disconnect();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting()
        {
            connect();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(final ClientSocketState clientSocketState)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param keybindingsManager the keybindings manager to update
     * @param shortcutsManager the shortcuts manager to update
     * @param settings the settings instance to use
     * @param window the frame instance for updating the title
     * @param characterPickup the character pickup instance to update
     * @param server the crossfire server connection instance used to connect
     * @param connectionListener the connection listener to use when connecting
     * @param guiManager the gui manager to use when connecting
     */
    public JXCConnection(final KeybindingsManager keybindingsManager, final ShortcutsManager shortcutsManager, final Settings settings, final JXCWindow window, final Pickup characterPickup, final CrossfireServerConnection server, final ConnectionListener connectionListener, final GuiManager guiManager)
    {
        this.keybindingsManager = keybindingsManager;
        this.shortcutsManager = shortcutsManager;
        this.settings = settings;
        frame = window;
        this.characterPickup = characterPickup;
        this.server = server;
        this.connectionListener = connectionListener;
        this.guiManager = guiManager;
        window.addConnectionStateListener(guiStateListener);
        updateTitle();
    }

    /**
     * Returns the currently connected server.
     * @return the server or <code>null</code> if unconnected
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Update the active character name.
     *
     * @param character The active character; <code>null</code> if not logged
     * in.
     */
    public void setCharacter(final String character)
    {
        if (this.character == null ? character == null : this.character.equals(character))
        {
            return;
        }

        keybindingsManager.unloadPerCharacterBindings();
        shortcutsManager.saveShortcuts();

        if (hostname != null && this.character != null)
        {
            final long pickupMode = characterPickup.getPickupMode();
            if (pickupMode != Pickup.PU_NOTHING)
            {
                settings.putLong("pickup_"+hostname+"_"+this.character, pickupMode);
            }
            else
            {
                settings.remove("pickup_"+hostname+"_"+this.character);
            }
        }

        this.character = character;
        updateTitle();

        if (hostname != null && character != null)
        {
            keybindingsManager.loadPerCharacterBindings(hostname, character);
            shortcutsManager.loadShortcuts(hostname, character);
            characterPickup.setPickupMode(settings.getLong("pickup_"+hostname+"_"+character, Pickup.PU_NOTHING));
        }
    }

    /**
     * Update the window title to reflect the current connection state.
     */
    private void updateTitle()
    {
        if (hostname == null)
        {
            frame.setTitle(TITLE_PREFIX);
        }
        else if (character == null)
        {
            frame.setTitle(TITLE_PREFIX+" - "+hostname);
        }
        else
        {
            frame.setTitle(TITLE_PREFIX+" - "+hostname+" - "+character);
        }
    }

    /**
     * Update information about the connected host.
     *
     * @param serverInfo The hostname; <code>null</code> if not connected.
     */
    public void setHost(final String serverInfo)
    {
        final String newHostname;
        final int newPort;
        if (serverInfo == null)
        {
            newHostname = null;
            newPort = 0;
        }
        else
        {
            final String[] tmp = serverInfo.split(":", 2);
            newHostname = tmp[0];
            newPort = tmp.length < 2 ? 13327 : NumberParser.parseInt(tmp[1], 13327, 1, 65535);
        }

        if ((hostname == null ? newHostname == null : hostname.equals(newHostname))
        && port == newPort)
        {
            return;
        }

        setCharacter(null);
        hostname = newHostname;
        port = newPort;
        updateTitle();
    }

    /**
     * Disconnects from the Crossfire server.
     */
    private void disconnect()
    {
        server.disconnect();
        setHost(null);
    }

    /**
     * Connect to the Crossfire server.
     */
    private void connect()
    {
        final JXCSkin skin = guiManager.getSkin();
        server.setMapSize(skin.getMapWidth(), skin.getMapHeight());
        server.setNumLookObjects(skin.getNumLookObjects());
        server.connect(hostname, port, connectionListener);
    }
}

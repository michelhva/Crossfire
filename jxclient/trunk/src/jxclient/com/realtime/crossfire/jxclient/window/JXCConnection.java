package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.server.ConnectionListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.Pickup;
import com.realtime.crossfire.jxclient.settings.Settings;
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
     * The {@link CrossfireQueryListener} attached to {@link #server}. Set to
     * <code>null</code> when unset.
     */
    private CrossfireQueryListener crossfireQueryListener = null;

    /**
     * The {@link CrossfireDrawextinfoListener} attached to {@link #server}.
     * Set to <code>null</code> when unset.
     */
    private CrossfireDrawextinfoListener crossfireDrawextinfoListener = null;

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
     * Creates a new instance.
     * @param keybindingsManager the keybindings manager to update
     * @param settings the settings instance to use
     * @param frame the frame instance for updating the title
     * @param characterPickup the character pickup instance to update
     * @param server the crossfire server connection instance used to connect
     * to the Crossfire server
     */
    public JXCConnection(final KeybindingsManager keybindingsManager, final Settings settings, final Frame frame, final Pickup characterPickup, final CrossfireServerConnection server)
    {
        this.keybindingsManager = keybindingsManager;
        this.settings = settings;
        this.frame = frame;
        this.characterPickup = characterPickup;
        this.server = server;
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
    public void disconnect()
    {
        if (crossfireDrawextinfoListener != null)
        {
            server.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        }
        if (crossfireQueryListener != null)
        {
            server.removeCrossfireQueryListener(crossfireQueryListener);
        }
        server.disconnect();
        setHost(null);
    }

    /**
     * Connect to the Crossfire server.
     * @param connectionListener the connection listener to attach
     * @param crossfireQueryListener the crossfire query listener to attach
     * @param crossfireDrawextinfoListener the crossfire drawinfo listener to
     * @param mapWidth the map width to request from the Crossfire server
     * @param mapHeight the map height to request from the Crossfire server
     * @param numLookObjects the number of ground view objects to request from
     * the Crossfire server
     */
    public void connect(final ConnectionListener connectionListener, final CrossfireQueryListener crossfireQueryListener, final CrossfireDrawextinfoListener crossfireDrawextinfoListener, final int mapWidth, final int mapHeight, final int numLookObjects)
    {
        this.crossfireQueryListener = crossfireQueryListener;
        this.crossfireDrawextinfoListener = crossfireDrawextinfoListener;
        if (crossfireQueryListener != null)
        {
            server.addCrossfireQueryListener(crossfireQueryListener);
        }
        if (crossfireDrawextinfoListener != null)
        {
            server.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        }
        server.setMapSize(mapWidth, mapHeight);
        server.setNumLookObjects(numLookObjects);
        server.connect(hostname, port, connectionListener);
    }
}

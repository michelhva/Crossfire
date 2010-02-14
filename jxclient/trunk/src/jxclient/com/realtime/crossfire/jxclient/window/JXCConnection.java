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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfirePickupListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.options.Pickup;
import com.realtime.crossfire.jxclient.util.NumberParser;
import java.awt.Frame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JXCConnection
{
    /**
     * The default port number for Crossfire servers.
     */
    private static final int DEFAULT_CROSSFIRE_PORT = 13327;

    /**
     * The prefix for the window title.
     */
    @NotNull
    private static final String TITLE_PREFIX = "jxclient";

    /**
     * The {@link KeybindingsManager} to update.
     */
    @NotNull
    private final KeybindingsManager keybindingsManager;

    /**
     * The {@link ShortcutsManager} to update.
     */
    @NotNull
    private final ShortcutsManager shortcutsManager;

    /**
     * The settings instance to use.
     */
    @NotNull
    private final Settings settings;

    /**
     * The {@link Frame} for updating the title.
     */
    @NotNull
    private final Frame frame;

    /**
     * The {@link Pickup} instance to update.
     */
    @NotNull
    private final Pickup characterPickup;

    /**
     * The {@link CrossfireServerConnection} instance used to connect to the
     * Crossfire server.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * The currently connected server. Set to <code>null</code> if unconnected.
     */
    @Nullable
    private String hostname = null;

    /**
     * The currently connected port. Only valid if {@link #hostname} is set.
     */
    private int port = 0;

    /**
     * The currently logged in character. Set to <code>null</code> if not
     * logged in.
     */
    @Nullable
    private String character = null;

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
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
        public void preConnecting(@NotNull final String serverInfo)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo)
        {
            connect();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason)
        {
            // ignore
        }
    };

    /**
     * The {@link CrossfirePickupListener} for tracking pickup mode changes.
     */
    @NotNull
    private final CrossfirePickupListener crossfirePickupListener = new CrossfirePickupListener()
    {
        /** {@inheritDoc} */
        @Override
        public void pickupChanged(final int pickupOptions)
        {
            characterPickup.setPickupMode(pickupOptions);
        }
    };

    /**
     * Creates a new instance.
     * @param keybindingsManager the keybindings manager to update
     * @param shortcutsManager the shortcuts manager to update
     * @param settings the settings instance to use
     * @param frame the frame instance for updating the title
     * @param characterPickup the character pickup instance to update
     * @param server the crossfire server connection instance used to connect
     * @param guiStateManager the gui state manager to watch
     */
    public JXCConnection(@NotNull final KeybindingsManager keybindingsManager, @NotNull final ShortcutsManager shortcutsManager, @NotNull final Settings settings, @NotNull final Frame frame, @NotNull final Pickup characterPickup, @NotNull final CrossfireServerConnection server, @NotNull final GuiStateManager guiStateManager)
    {
        this.keybindingsManager = keybindingsManager;
        this.shortcutsManager = shortcutsManager;
        this.settings = settings;
        this.frame = frame;
        this.characterPickup = characterPickup;
        this.server = server;
        guiStateManager.addGuiStateListener(guiStateListener);
        updateTitle();
    }

    /**
     * Returns the currently connected server.
     * @return the server or <code>null</code> if unconnected
     */
    @Nullable
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Returns the currently connected port.
     * @return the port or <code>0</code> if unconnected
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Update the active character name.
     *
     * @param character The active character; <code>null</code> if not logged
     * in.
     */
    public void setCharacter(@Nullable final String character)
    {
        if (this.character == null ? character == null : this.character.equals(character))
        {
            return;
        }

        keybindingsManager.unloadPerCharacterBindings();
        shortcutsManager.saveShortcuts();

        if (hostname != null && this.character != null)
        {
            server.removeCrossfirePickupListener(crossfirePickupListener);
            final long pickupMode = characterPickup.getPickupMode();
            if (pickupMode == Pickup.PU_NOTHING)
            {
                settings.remove("pickup_"+hostname+"_"+this.character);
            }
            else
            {
                settings.putLong("pickup_"+hostname+"_"+this.character, pickupMode);
            }
        }

        this.character = character;
        updateTitle();

        if (hostname != null && character != null)
        {
            keybindingsManager.loadPerCharacterBindings(hostname, character);
            shortcutsManager.loadShortcuts(hostname, character);
            characterPickup.setPickupMode(settings.getLong("pickup_"+hostname+"_"+character, Pickup.PU_NOTHING));
            server.addCrossfirePickupListener(crossfirePickupListener);
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
    public void setHost(@Nullable final String serverInfo)
    {
        @Nullable final String newHostname;
        final int newPort;
        if (serverInfo == null)
        {
            newHostname = null;
            newPort = 0;
        }
        else
        {
            settings.putString("server", serverInfo);
            final String[] tmp = serverInfo.split(":", 2);
            newHostname = tmp[0];
            newPort = tmp.length < 2 ? DEFAULT_CROSSFIRE_PORT : NumberParser.parseInt(tmp[1], DEFAULT_CROSSFIRE_PORT, 1, 65535);
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
        server.connect(hostname, port);
    }
}

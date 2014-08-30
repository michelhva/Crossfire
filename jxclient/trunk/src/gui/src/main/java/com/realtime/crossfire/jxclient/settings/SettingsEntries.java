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

package com.realtime.crossfire.jxclient.settings;

import com.realtime.crossfire.jxclient.settings.options.Pickup;
import org.jetbrains.annotations.NotNull;

/**
 * All defined entries in the settings file.
 * @author Andreas Kirschbaum
 */
public class SettingsEntries {

    /**
     * The server to which the previous connection was made.
     */
    @NotNull
    public static final SettingsEntry<String> SERVER = new SettingsEntry<String>("server", "crossfire.metalforge.net", "The server last connected to.");

    /**
     * Whether to record of all text messages received from the server.
     */
    @NotNull
    public static final SettingsEntry<Boolean> MESSAGE_LOG_SETTINGS_ENTRY = new SettingsEntry<Boolean>("messagelog", false, "Whether to record all text messages into a file.");

    /**
     * Private constructor to prevent instantiation.
     */
    private SettingsEntries() {
    }

    /**
     * Returns the {@link SettingsEntry} for the default character name on a
     * server.
     * @param hostname the hostname of the server
     * @return the settings entry
     */
    @NotNull
    public static SettingsEntry<String> getPlayerSettingsEntry(@NotNull final String hostname) {
        return new SettingsEntry<String>("player_"+hostname, "", "The charactername last played on the server.");
    }

    /**
     * Returns the {@link SettingsEntry} for the default account name on a
     * server.
     * @param hostname the hostname of the server
     * @return the settings entry
     */
    @NotNull
    public static SettingsEntry<String> getLoginAccountSettingsEntry(@NotNull final String hostname) {
        return new SettingsEntry<String>("login_account_"+hostname, "", "The account last logged in on the server.");
    }

    /**
     * Returns the {@link SettingsEntry} for the default character name on an
     * account.
     * @param hostname the hostname of the server
     * @param accountName the name of the account
     * @return the settings entry
     */
    @NotNull
    public static SettingsEntry<String> getLoginAccountSettingsEntry(@NotNull final String hostname, @NotNull final String accountName) {
        return new SettingsEntry<String>("login_account_"+hostname+"_"+accountName, "", "The character last selected on the account.");
    }

    /**
     * Returns the {@link SettingsEntry} for the default pickup mode of a
     * character.
     * @param hostname the hostname of the server
     * @param characterName the name of the character
     * @return the settings entry
     */
    @NotNull
    public static SettingsEntry<Long> getPickupSettingsEntry(@NotNull final String hostname, @NotNull final String characterName) {
        return new SettingsEntry<Long>("pickup_"+hostname+"_"+characterName, Pickup.PU_NOTHING, "The character's pickup mode.");
    }

}

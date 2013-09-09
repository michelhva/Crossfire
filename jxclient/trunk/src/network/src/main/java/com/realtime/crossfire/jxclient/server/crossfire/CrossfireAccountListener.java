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
 * Copyright (C) 2010 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.server.crossfire;

import com.realtime.crossfire.jxclient.account.CharacterInformation;
import java.util.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners interested in account information related messages
 * received from the Crossfire server.
 * @author Nicolas Weeger
 */
public interface CrossfireAccountListener extends EventListener {

    /**
     * Client should display the account management dialog.
     */
    void manageAccount();

    /**
     * Starting to receive information for the list of characters in an
     * account.
     * @param accountName the account name
     */
    void startAccountList(@NotNull String accountName);

    /**
     * Information about a character in an account was received.
     * @param characterInformation the account information
     */
    void addAccount(@NotNull CharacterInformation characterInformation);

    /**
     * End of character information for an account.
     * @param count how many characters the account has.
     */
    void endAccountList(final int count);

    /**
     * The client should switch to playing mode.
     */
    void startPlaying();

    /**
     * An character name was sent to the server.
     * @param accountName the account name
     * @param characterName the character name
     */
    void selectCharacter(@NotNull String accountName, @NotNull String characterName);

}

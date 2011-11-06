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

package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.gui.gui.Gui;
import org.jetbrains.annotations.NotNull;

/**
 * Interface that defines callback functions needed by commands.
 * @author Andreas Kirschbaum
 */
public interface CommandCallback {

    /**
     * Terminates the application.
     */
    void quitApplication();

    /**
     * Opens a dialog. Does nothing if the dialog is open.
     * @param dialog the dialog to open
     */
    void openDialog(@NotNull Gui dialog);

    /**
     * Toggles a dialog.
     * @param dialog the dialog to toggle
     */
    void toggleDialog(@NotNull Gui dialog);

    /**
     * Closes a dialog. Does nothing if the dialog is not open.
     * @param dialog the dialog to close
     */
    void closeDialog(@NotNull Gui dialog);

    /**
     * Returns a command list.
     * @param args the command list arguments
     * @return the command list
     * @throws NoSuchCommandException if the command list does not exist
     */
    @NotNull
    CommandList getCommandList(@NotNull String args) throws NoSuchCommandException;

    /**
     * Sets the current player name. Does nothing if not currently in the
     * character name prompt.
     * @param playerName the player name
     */
    void updatePlayerName(@NotNull String playerName);

    /**
     * Activates the command input text field. If the skin defines more than one
     * input field, the first matching one is selected.
     * <p/>
     * If neither the main gui nor any visible dialog has an input text field,
     * invisible GUIs are checked as well. If one is found, it is made visible.
     * @param newText the new command text if non-<code>null</code>
     */
    void activateCommandInput(@NotNull String newText);

    /**
     * Adds a key binding.
     * @param perCharacter whether a per-character key binding should be added
     * @param commandList the command list to execute on key press
     * @return whether the key bindings dialog should be opened
     */
    boolean createKeyBinding(boolean perCharacter, @NotNull CommandList commandList);

    /**
     * Removes a key binding.
     * @param perCharacter whether a per-character key binding should be
     * removed
     * @return whether the key bindings dialog should be opened
     */
    boolean removeKeyBinding(boolean perCharacter);

    /**
     * Login to an account.
     * @param login login
     * @param password password
     */
    void accountLogin(@NotNull String login, @NotNull String password);

    /**
     * Creates an account.
     * @param login the account's name
     * @param password the account's password
     */
    void accountCreate(@NotNull String login, @NotNull String password);

    /**
     * Plays a character from the current account.
     * @param name the character's name
     */
    void accountPlayCharacter(@NotNull String name);

    /**
     * Links a character to the current account.
     * @param force 0 to allow failure, 1 to force in certain situations
     * @param login the character's name
     * @param password the character's password
     */
    void accountLink(int force, @NotNull String login, @NotNull String password);

    /**
     * Creates a character. The password should be the last from {@link
     * #accountLogin} or {@link #accountCreate}.
     * @param login the character's name
     */
    void accountCreateCharacter(@NotNull String login);

    /**
     * Change the account password.
     * @param currentPassword the current account password
     * @param newPassword the new account password
     */
    void accountPassword(@NotNull String currentPassword, @NotNull String newPassword);
}

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

package com.realtime.crossfire.jxclient.main;

import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import com.realtime.crossfire.jxclient.gui.commands.NoSuchCommandException;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.GuiManager;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link CommandCallback} that delegates to a {@link GuiManager}.
 * @author Andreas Kirschbaum
 */
public class GuiManagerCommandCallback implements CommandCallback {

    /**
     * The {@link Exiter} instance.
     */
    @NotNull
    private final Exiter exiter;

    /**
     * The {@link GuiManager} to forward to.
     */
    @NotNull
    private GuiManager guiManager;

    /**
     * The {@link CrossfireServerConnection} for sending commands.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * The last sent account or character password. Set to <code>""</code> if no
     * password has been sent yet.
     */
    @NotNull
    private String lastAccountPassword = "";

    /**
     * Creates a new instance.
     * @param exiter the exiter instance
     * @param server the crossfire server connection for sending commands
     */
    public GuiManagerCommandCallback(@NotNull final Exiter exiter, @NotNull final CrossfireServerConnection server) {
        this.exiter = exiter;
        this.server = server;
    }

    /**
     * Creates a new instance.
     * @param guiManager the gui manager to forward to
     */
    @Deprecated
    public void init(@NotNull final GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void quitApplication() {
        exiter.terminate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openDialog(@NotNull final Gui dialog) {
        guiManager.openDialog(dialog, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleDialog(@NotNull final Gui dialog) {
        guiManager.toggleDialog(dialog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeDialog(@NotNull final Gui dialog) {
        guiManager.closeDialog(dialog);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public CommandList getCommandList(@NotNull final String args) throws NoSuchCommandException {
        return guiManager.getCommandList(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePlayerName(@NotNull final String playerName) {
        guiManager.updatePlayerName(playerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateCommandInput(@NotNull final String newText) {
        guiManager.activateCommandInput(newText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createKeyBinding(final boolean perCharacter, @NotNull final CommandList commandList) {
        return guiManager.createKeyBinding(perCharacter, commandList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeKeyBinding(final boolean perCharacter) {
        return guiManager.removeKeyBinding(perCharacter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accountLogin(@NotNull final String login, @NotNull final String password) {
        server.sendAccountLogin(login, password);
        lastAccountPassword = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accountCreate(@NotNull final String login, @NotNull final String password) {
        server.sendAccountCreate(login, password);
        lastAccountPassword = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accountPlayCharacter(@NotNull final String name) {
        server.sendAccountPlay(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accountLink(final int force, @NotNull final String login, @NotNull final String password) {
        server.sendAccountLink(force, login, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accountCreateCharacter(@NotNull final String login) {
        server.sendAccountCharacterCreate(login, lastAccountPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accountPassword(@NotNull final String currentPassword, @NotNull final String newPassword) {
        server.sendAccountPassword(currentPassword, newPassword);
    }

}

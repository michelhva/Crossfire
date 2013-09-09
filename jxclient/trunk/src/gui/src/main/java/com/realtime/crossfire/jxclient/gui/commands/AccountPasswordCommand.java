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
 * Copyright (C) 2011 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.gui.commandlist.GUICommand;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.label.GUILabelFailure;
import com.realtime.crossfire.jxclient.gui.textinput.CommandCallback;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import java.awt.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} sending an account password change request.
 * @author Nicolas Weeger
 */
public class AccountPasswordCommand implements GUICommand {

    /**
     * The {@link CommandCallback} to use.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * The {@link Component} to find the Gui containing the fields.
     */
    @NotNull
    private final Component element;

    /**
     * Creates a new instance.
     * @param commandCallback what to inform of various changes
     * @param button the item to link to to find the Gui from which to get
     * information
     */
    public AccountPasswordCommand(@NotNull final CommandCallback commandCallback, @NotNull final Component button) {
        this.commandCallback = commandCallback;
        element = button;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        final Gui gui = GuiUtils.getGui(element);
        if (gui == null) {
            return;
        }

        final GUIText currentPasswordField = gui.getFirstElement(GUIText.class, "account_password_current");
        final GUIText newPasswordField = gui.getFirstElement(GUIText.class, "account_password_new");
        final GUIText confirmPasswordField = gui.getFirstElement(GUIText.class, "account_password_confirm");

        if (currentPasswordField == null || newPasswordField == null || confirmPasswordField == null) {
            return;
        }

        final String currentPassword = currentPasswordField.getText();
        final String newPassword = newPasswordField.getText();
        final String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty()) {
            final AbstractLabel error = gui.getFirstElement(GUILabelFailure.class, "account_password_error");
            if (error != null) {
                error.setText("Please enter your current password!");
            }
            return;
        }

        if (newPassword.isEmpty()) {
            final AbstractLabel error = gui.getFirstElement(GUILabelFailure.class, "account_password_error");
            if (error != null) {
                error.setText("Can't have an empty password!");
            }
            return;
        }
        if (!confirmPassword.equals(newPassword)) {
            final AbstractLabel error = gui.getFirstElement(GUILabelFailure.class, "account_password_error");
            if (error != null) {
                error.setText("Passwords don't match!");
            }
            return;
        }

        commandCallback.accountPassword(currentPassword, newPassword);
    }

}

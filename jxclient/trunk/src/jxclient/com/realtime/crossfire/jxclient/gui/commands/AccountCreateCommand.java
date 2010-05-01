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

package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.label.GUILabelFailure;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} sending an account creation request.
 * @author Nicolas Weeger
 */
public class AccountCreateCommand implements GUICommand {
    /**
     * The {@link CommandCallback} to use.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * The {@link GUIElement} to find information for account creation.
     */
    private final GUIElement element;

     /**
      * Creates a new instance.
      * @param commandCallback what to inform of the request.
      * @param button element to find the Gui for the other fields.
      */
    public AccountCreateCommand(@NotNull final CommandCallback commandCallback, @NotNull final GUIElement button) {
        this.commandCallback = commandCallback;
        this.element = button;
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        String login, password, confirm;

        final Gui gui = this.element.getGui();
        if (gui == null) {
            return;
        }

        GUIText l = gui.getFirstElement(GUIText.class, "account_login");
        GUIText p = gui.getFirstElement(GUIText.class, "account_password");
        GUIText c = gui.getFirstElement(GUIText.class, "account_password_confirm");

        if ((l == null) || (p==null) || (c==null)) {
            return;
        }

        login = l.getText();
        password = p.getText();
        confirm = c.getText();

        if (login.isEmpty()) {
            GUILabelFailure error = gui.getFirstElement(GUILabelFailure.class, "account_create_error");
            error.setText("Can't have an empty login!");
            return;
        }

        if (password.isEmpty()) {
            GUILabelFailure error = gui.getFirstElement(GUILabelFailure.class, "account_create_error");
            error.setText("Can't have an empty password!");
            return;
        }
        if (!confirm.equals(password)) {
            GUILabelFailure error = gui.getFirstElement(GUILabelFailure.class, "account_create_error");
            error.setText("Passwords don't match!");
            return;
        }

        this.commandCallback.accountCreate(login, password);
    }
}

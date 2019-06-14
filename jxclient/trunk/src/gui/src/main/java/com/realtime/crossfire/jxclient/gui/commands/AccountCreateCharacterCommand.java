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

import com.realtime.crossfire.jxclient.character.Choice;
import com.realtime.crossfire.jxclient.gui.commandlist.GUICommand;
import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.label.NewCharModel;
import com.realtime.crossfire.jxclient.gui.label.NewcharStat;
import com.realtime.crossfire.jxclient.gui.textinput.CommandCallback;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} sending a character creation request.
 * @author Nicolas Weeger
 */
public class AccountCreateCharacterCommand implements GUICommand {

    /**
     * The {@link CommandCallback} to use.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * The {@link AbstractGUIElement} to find the Gui containing the fields.
     */
    @NotNull
    private final AbstractGUIElement element;

    /**
     * The global {@link GuiFactory} instance.
     */
    @NotNull
    private final GuiFactory guiFactory;

    /**
     * The global {@link NewCharModel} instance.
     */
    @NotNull
    private final NewCharModel newCharModel;

    /**
     * Creates a new instance.
     * @param commandCallback what to inform of various changes
     * @param button the item to link to to find the Gui from which to get
     * information
     * @param guiFactory the global GUI factory instance
     * @param newCharModel the global new char model instance
     */
    public AccountCreateCharacterCommand(@NotNull final CommandCallback commandCallback, @NotNull final AbstractGUIElement button, @NotNull final GuiFactory guiFactory, @NotNull final NewCharModel newCharModel) {
        this.commandCallback = commandCallback;
        element = button;
        this.guiFactory = guiFactory;
        this.newCharModel = newCharModel;
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        final Gui gui = guiFactory.getGui(element);
        if (gui == null) {
            return;
        }

        final GUIText loginField = gui.getFirstElement(GUIText.class, "account_character_create");
        if (loginField == null) {
            return;
        }

        final String login = loginField.getText();
        if (login.isEmpty()) {
            loginField.setActive(true);
            return;
        }

        final Collection<String> attributes = new ArrayList<>();
        attributes.add("race "+newCharModel.getRace());
        attributes.add("class "+newCharModel.getClass_());
        for (final NewcharStat stat : NewcharStat.values()) {
            attributes.add(stat.getStatName()+" "+newCharModel.getValue(stat));
        }
        attributes.add("starting_map "+newCharModel.getStartingMap());
        final Choice option = newCharModel.getOption();
        if (option != null) {
            final int optionIndex = newCharModel.getOptionIndex();
            final Iterator<String> it = option.getChoices().keySet().iterator();
            for (int i = 0; i < optionIndex; i++) {
                it.next();
            }
            attributes.add("choice "+option.getChoiceName()+" "+it.next());
        }
        commandCallback.accountCreateCharacter(login, attributes);
    }

}

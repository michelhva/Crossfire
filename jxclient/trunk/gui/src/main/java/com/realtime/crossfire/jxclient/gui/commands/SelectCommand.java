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
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUISelectable;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} for selecting or deselecting a {@link GUIElement}.
 * @author Andreas Kirschbaum
 */
public class SelectCommand implements GUICommand {

    /**
     * The {@link GUISelectable} to affect.
     */
    @NotNull
    private final GUISelectable element;

    /**
     *
     * Whether to select the {@link #element}.
     */
    private final boolean selected;

    /**
     * Creates a new instance.
     * @param element the element to affect
     * @param selected whether to select the element
     */
    public SelectCommand(@NotNull final GUISelectable element, final boolean selected) {
        this.element = element;
        this.selected = selected;
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
        element.select(selected);
    }

}

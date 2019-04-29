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

import com.realtime.crossfire.jxclient.gui.commandlist.GUICommand;
import com.realtime.crossfire.jxclient.gui.list.GUIList;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} which moves the selected element in a {@link GUIList}
 * element.
 * @author Andreas Kirschbaum
 */
public class MoveSelectionCommand implements GUICommand {

    /**
     * The list to scroll.
     */
    @NotNull
    private final GUIList<?> list;

    /**
     * The distance in lines to scroll.
     */
    private final int diffLines;

    /**
     * The distance in elements to scroll.
     */
    private final int diffElements;

    /**
     * Creates a new instance.
     * @param list the list to scroll
     * @param diffLines the distance in lines to scroll
     * @param diffElements the distance in elements to scroll
     */
    public MoveSelectionCommand(@NotNull final GUIList<?> list, final int diffLines, final int diffElements) {
        this.list = list;
        this.diffLines = diffLines;
        this.diffElements = diffElements;
    }

    @Override
    public boolean canExecute() {
        return list.canMoveSelection(diffLines, diffElements) || list.canScroll(diffLines);
    }

    @Override
    public void execute() {
        if (list.canMoveSelection(diffLines, diffElements)) {
            list.moveSelection(diffLines, diffElements);
        } else {
            list.scroll(diffLines);
        }
    }

}

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

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.gui.GUICharacter;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIList} that tracks a {@link GUICharacter} instance.
 * @author Nicolas Weeger
 */
public class CharacterCellRenderer extends JPanel implements GUIListCellRenderer {

    /**
     * Template to use to display items.
     */
    private final GUICharacter template;

    /**
     * Creates a new instance.
     * @param template {@link GUICharacter} that will be the displaying template.
     */
    public CharacterCellRenderer(@NotNull final GUICharacter template) {
        super(new BorderLayout());
        setOpaque(false);
        this.template = template;
        add(template, BorderLayout.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateResolution(final int screenWidth, final int screenHeight) {
        template.updateResolution(screenWidth, screenHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getListCellRendererComponent(@NotNull final JList list, @NotNull final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        template.setIndex(((GUICharacter)value).getIndex());
        template.setSelected(isSelected);
        return this;
    }

}

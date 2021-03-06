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

package com.realtime.crossfire.jxclient.gui.list;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ListCellRenderer} that renders {@link GUIMetaElement} instances.
 * @author Andreas Kirschbaum
 */
public class MetaElementCellRenderer extends JPanel implements GUIListCellRenderer<GUIMetaElement> {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The template used for painting.
     */
    @NotNull
    private final GUIMetaElement template;

    /**
     * Creates a new instance.
     * @param template the template used for painting
     */
    public MetaElementCellRenderer(@NotNull final GUIMetaElement template) {
        super(new BorderLayout());
        setOpaque(false);
        this.template = template;
        this.template.inhibitListeners();
        add(template, BorderLayout.CENTER);
    }

    @NotNull
    @Override
    public Component getListCellRendererComponent(@NotNull final JList<? extends GUIMetaElement> list, @NotNull final GUIMetaElement value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        template.setIndex(value.getIndex());
        template.setSelected(isSelected);
        return this;
    }

}

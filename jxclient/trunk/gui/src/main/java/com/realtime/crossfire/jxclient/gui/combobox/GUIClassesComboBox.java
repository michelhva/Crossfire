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

package com.realtime.crossfire.jxclient.gui.combobox;

import com.realtime.crossfire.jxclient.character.ClassRaceInfo;
import com.realtime.crossfire.jxclient.character.NewCharacterInformationListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.server.crossfire.Model;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIComboBox} that shows available classes for character creation.
 * @author Andreas Kirschbaum
 */
public class GUIClassesComboBox extends GUIComboBox<String> {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link Model} to track for available classes.
     */
    @NotNull
    private final Model model;

    /**
     * The {@link JLabel} that displays the list values.
     */
    @NotNull
    private final JLabel renderer = new JLabel();

    /**
     * The {@link NewCharacterInformationListener} attached to {@link #model}.
     */
    @NotNull
    private final NewCharacterInformationListener newCharacterInformationListener = new NewCharacterInformationListener() {

        @Override
        public void classListChanged() {
            updateModel();
        }

        @Override
        public void classInfoChanged(@NotNull final String className) {
            updateSelectedItem();
        }

        @Override
        public void raceListChanged() {
            // ignore
        }

        @Override
        public void raceInfoChanged(@NotNull final String raceName) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param model the model to display
     * @param label the label to update or <code>null</code>
     */
    public GUIClassesComboBox(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Model model, @Nullable final GUILabelLog label) {
        super(tooltipManager, elementListener, name, label);
        this.model = model;
        model.getNewCharacterInformation().addNewCharacterInformationListener(newCharacterInformationListener);
        updateModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        model.getNewCharacterInformation().removeNewCharacterInformationListener(newCharacterInformationListener);
    }

    /**
     * Updates the combo box model to reflect the current class list of {@link
     * #model}.
     */
    private void updateModel() {
        updateModel(model.getNewCharacterInformation().getClassesList());
        updateSelectedItem();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected Component getListCellRendererComponent(@NotNull final JList list, @Nullable final String value, final int index, final boolean selected, final boolean cellHasFocus) {
        final ClassRaceInfo classInfo = value == null ? null : model.getNewCharacterInformation().getClassInfo(value);
        renderer.setText(classInfo == null ? value : classInfo.getName());
        return renderer;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected String getDescription(@NotNull final String item) {
        final ClassRaceInfo classInfo = model.getNewCharacterInformation().getClassInfo(item);
        return classInfo == null ? "" : classInfo.getMsg();
    }

}

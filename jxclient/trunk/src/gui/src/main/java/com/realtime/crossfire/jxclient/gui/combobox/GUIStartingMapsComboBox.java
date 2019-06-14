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

import com.realtime.crossfire.jxclient.character.NewCharacterInformationListener;
import com.realtime.crossfire.jxclient.character.StartingMap;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.label.NewCharModel;
import com.realtime.crossfire.jxclient.gui.label.NewCharModelListener;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.server.crossfire.Model;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIComboBox} that shows available starting maps for character
 * creation.
 * @author Andreas Kirschbaum
 */
public class GUIStartingMapsComboBox extends GUIComboBox<String> {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link Model} to track for available races.
     */
    @NotNull
    private final Model model;

    /**
     * The {@link NewCharModel} that is shown.
     */
    @NotNull
    private final NewCharModel newCharModel;

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
            // ignore
        }

        @Override
        public void classInfoChanged(@NotNull final String className) {
            // ignore
        }

        @Override
        public void raceListChanged() {
            // ignore
        }

        @Override
        public void raceInfoChanged(@NotNull final String raceName) {
            // ignore
        }

        @Override
        public void startingMapListChanged() {
            updateModel();
        }

        @Override
        public void startingMapInfoChanged(@NotNull final String startingMapName) {
            updateModel();
            updateSelectedItem();
        }

    };

    /**
     * The listener attached to {@link #newCharModel}.
     */
    @NotNull
    private final NewCharModelListener newCharModelListener = new NewCharModelListener() {

        @Override
        public void changed() {
            setSelectedItem(newCharModel.getStartingMap());
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param model the model to display
     * @param newCharModel the new char model to show
     * @param label the label to update or {@code null}
     * @param guiFactory the global GUI factory instance
     */
    public GUIStartingMapsComboBox(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Model model, @NotNull final NewCharModel newCharModel, @Nullable final GUILabelLog label, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, label, guiFactory);
        this.model = model;
        this.newCharModel = newCharModel;
        model.getNewCharacterInformation().addNewCharacterInformationListener(newCharacterInformationListener);
        newCharModel.addListener(newCharModelListener);
        updateModel();
        setSelectedItem(newCharModel.getStartingMap());
    }

    @Override
    public void dispose() {
        super.dispose();
        model.getNewCharacterInformation().removeNewCharacterInformationListener(newCharacterInformationListener);
        newCharModel.removeListener(newCharModelListener);
    }

    @Override
    public void notifyOpen() {
    }

    /**
     * Updates the combo box model to reflect the current starting map list of
     * {@link #model}.
     */
    private void updateModel() {
        final List<String> tmp = new ArrayList<>(model.getNewCharacterInformation().getStartingMapList());
        tmp.sort((o1, o2) -> {
            final StartingMap info1 = model.getNewCharacterInformation().getStartingMapInfo(o1);
            final StartingMap info2 = model.getNewCharacterInformation().getStartingMapInfo(o2);
            return info1 == null || info2 == null ? 0 : info1.getName().compareTo(info2.getName());
        });
        updateModel(tmp);
        updateSelectedItem();
    }

    @NotNull
    @Override
    protected Component getListCellRendererComponent(@NotNull final JList<? extends String> list, @Nullable final String value, final int index, final boolean selected, final boolean cellHasFocus) {
        final StartingMap startingMapInfo = value == null ? null : model.getNewCharacterInformation().getStartingMapInfo(value);
        renderer.setText(startingMapInfo == null ? value : startingMapInfo.getName());
        return renderer;
    }

    @Override
    protected void updateSelectedItem(@Nullable final String item) {
        if (item != null) {
            newCharModel.setStartingMap(item);
        }
    }

    @NotNull
    @Override
    protected String getDescription(@Nullable final String item) {
        final StartingMap startingMap = item == null ? null : model.getNewCharacterInformation().getStartingMapInfo(item);
        return startingMap == null ? "" : startingMap.getDescription();
    }

}

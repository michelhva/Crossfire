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

import com.realtime.crossfire.jxclient.character.Choice;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.label.NewCharModel;
import com.realtime.crossfire.jxclient.gui.label.NewCharModelListener;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIComboBox} that shows character creation options. If more than one
 * such option should exist, only one is shown.
 * @author Andreas Kirschbaum
 */
public class GUICharOptionsComboBox extends GUIComboBox<Integer> {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

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
     * The listener attached to {@link #newCharModel}.
     */
    @NotNull
    private final NewCharModelListener newCharModelListener = new NewCharModelListener() {

        @Override
        public void changed() {
            updateModel();
            setSelectedItem(newCharModel.getOptionIndex());
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param newCharModel the new char model to show
     * @param label the label to update or {@code null}
     * @param guiFactory the global GUI factory instance
     */
    public GUICharOptionsComboBox(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final NewCharModel newCharModel, @Nullable final GUILabelLog label, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, label, guiFactory);
        this.newCharModel = newCharModel;
        newCharModel.addListener(newCharModelListener);
        updateModel();
        setSelectedItem(newCharModel.getOptionIndex());
    }

    @Override
    public void dispose() {
        super.dispose();
        newCharModel.removeListener(newCharModelListener);
    }

    @Override
    public void notifyOpen() {
    }

    /**
     * Updates the combo box model to reflect the current starting map list of
     * {@link #newCharModel}.
     */
    private void updateModel() {
        final Choice option = newCharModel.getOption();
        @Nullable final List<Integer> model;
        if (option == null) {
            model = null;
        } else {
            model = new ArrayList<>();
            final List<String> choices = new ArrayList<>(option.getChoices().values());
            for (int i = 0; i < choices.size(); i++) {
                model.add(i);
            }
            model.sort(Comparator.comparing(choices::get));
        }
        updateModel(model);
        updateSelectedItem();
    }

    @NotNull
    @Override
    protected Component getListCellRendererComponent(@NotNull final JList<? extends Integer> list, @Nullable final Integer value, final int index, final boolean selected, final boolean cellHasFocus) {
        final Choice option = newCharModel.getOption();
        final String text;
        if (option == null || value == null) {
            text = "";
        } else {
            final Iterator<String> it = option.getChoices().values().iterator();
            for (int i = 0; i < value; i++) {
                if (it.hasNext()) {
                    it.next();
                }
            }
            text = it.hasNext() ? it.next() : "";
        }
        renderer.setText(text);
        return renderer;
    }

    @Override
    protected void updateSelectedItem(@Nullable final Integer item) {
        newCharModel.setOptionIndex(item == null ? -1 : item);
    }

    @NotNull
    @Override
    protected String getDescription(@Nullable final Integer item) {
        return "";
    }

}

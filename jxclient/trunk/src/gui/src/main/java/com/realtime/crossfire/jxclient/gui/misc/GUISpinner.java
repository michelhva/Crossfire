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

package com.realtime.crossfire.jxclient.gui.misc;

import com.realtime.crossfire.jxclient.character.NewCharInfo;
import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.gui.TooltipText;
import com.realtime.crossfire.jxclient.gui.label.NewCharModel;
import com.realtime.crossfire.jxclient.gui.label.NewcharStat;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Transparency;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIElement} that displays a spinner.
 * @author Andreas Kirschbaum
 */
public class GUISpinner extends AbstractGUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The shown stat.
     */
    @NotNull
    private final NewcharStat stat;

    /**
     * The shown model.
     */
    @NotNull
    private final NewCharModel newCharModel;

    /**
     * The model for {@link #spinner}.
     */
    @NotNull
    private final SpinnerNumberModel spinnerModel;

    /**
     * The Swing component that implements the combo box.
     */
    @NotNull
    private final JSpinner spinner;

    /**
     * The {@link ChangeListener} for {@link #spinner}.
     */
    @NotNull
    private final ChangeListener changeListener = e -> updateSelectedItem();

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param stat the stat to show
     * @param newCharModel the model to display
     * @param guiFactory the global GUI factory instance
     */
    public GUISpinner(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final NewcharStat stat, @NotNull final NewCharModel newCharModel, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT, guiFactory);
        this.stat = stat;
        this.newCharModel = newCharModel;
        final NewCharInfo newCharInfo = newCharModel.getNewCharacterInformation().getNewCharInfo();
        spinnerModel = new SpinnerNumberModel(newCharInfo.getMinValue(), newCharInfo.getMinValue(), newCharInfo.getMaxValue(), 1); // XXX: this is too early; must be set each time the new character dialog is opened
        spinner = new JSpinner(spinnerModel);
        final JComponent editor = spinner.getEditor();
        for (int i = 0; i < editor.getComponentCount(); i++) {
            final Component component = editor.getComponent(i);
            component.setFocusable(false);
        }
        spinner.addChangeListener(changeListener);
        add(spinner);
    }

    @Override
    public void dispose() {
        super.dispose();
        spinner.removeChangeListener(changeListener);
    }

    @Nullable
    @Override
    public TooltipText getTooltip() {
        return null;
    }

    @Override
    public void notifyOpen() {
        final NewCharInfo newCharInfo = newCharModel.getNewCharacterInformation().getNewCharInfo();
        final int minValue = newCharInfo.getMinValue();
        spinnerModel.setMinimum(minValue);
        final int maxValue = newCharInfo.getMaxValue();
        spinnerModel.setMaximum(maxValue);
        int value = (Integer)spinnerModel.getNumber();
        if (value > maxValue) {
            value = maxValue;
        }
        if (value < minValue) {
            value = minValue;
        }
        spinnerModel.setValue(value);
        updateSelectedItem();
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension result = spinner.getPreferredSize();
        return result == null ? super.getPreferredSize() : result;
    }

    @Override
    public Dimension getMinimumSize() {
        final Dimension result = spinner.getMinimumSize();
        return result == null ? super.getMinimumSize() : result;
    }

    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        spinner.setSize(width, height);
    }

    /**
     * Called whenever the selected item has changed.
     */
    private void updateSelectedItem() {
        final int value = spinnerModel.getNumber().intValue();
        newCharModel.setValue(stat, value);
    }

}

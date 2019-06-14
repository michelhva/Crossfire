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

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.log.GUILog;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Transparency;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIElement} that displays a combo box.
 * @param <T> the type of the entries of this list
 * @author Andreas Kirschbaum
 */
public abstract class GUIComboBox<T> extends AbstractGUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The model for {@link #comboBox}.
     */
    @NotNull
    private final DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();

    /**
     * The {@link GUILog} to update or {@code null}.
     */
    @Nullable
    private final GUILabelLog label;

    /**
     * The Swing component that implements the combo box.
     */
    @NotNull
    private final JComboBox<T> comboBox = new JComboBox<>(model);

    /**
     * The {@link ListCellRenderer} for {@link #comboBox}.
     */
    @NotNull
    @SuppressWarnings("FieldCanBeLocal")
    private final ListCellRenderer<T> renderer = this::getListCellRendererComponent;

    /**
     * The {@link ActionListener} for {@link #comboBox}.
     */
    @NotNull
    private final ActionListener actionListener = e -> updateSelectedItem();

    /**
     * If set, ignores calls to {@link #actionListener}.
     */
    private boolean inhibitActionListener;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param label the label to update or {@code null}
     * @param guiFactory the global GUI factory instance
     */
    protected GUIComboBox(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @Nullable final GUILabelLog label, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT, guiFactory);
        this.label = label;
        comboBox.setFocusable(false);
        comboBox.setRenderer(renderer);
        comboBox.setMaximumRowCount(18);
        comboBox.addActionListener(actionListener);
        add(comboBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        comboBox.removeActionListener(actionListener);
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension result = comboBox.getPreferredSize();
        return result == null ? super.getPreferredSize() : result;
    }

    @Override
    public Dimension getMinimumSize() {
        final Dimension result = comboBox.getMinimumSize();
        return result == null ? super.getMinimumSize() : result;
    }

    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        comboBox.setSize(width, height);
    }

    /**
     * Updates entries shown in the combo box.
     * @param elements the new entries to show
     */
    protected void updateModel(@NotNull final List<T> elements) {
        final boolean updateModel;
        if (elements == null) {
            updateModel = model.getSize() != 0;
        } else {
            final int size = model.getSize();
            if (size == elements.size()) {
                int i;
                for (i = 0; i < size; i++) {
                    if (!model.getElementAt(i).equals(elements.get(i))) {
                        break;
                    }
                }
                updateModel = i < size;
            } else {
                updateModel = true;
            }
        }

        if (updateModel) {
            try {
                inhibitActionListener = true;
                model.removeAllElements();
                for (T element : elements) {
                    model.addElement(element);
                }
            } finally {
                inhibitActionListener = false;
            }
        }
    }

    /**
     * Returns a {@link Component} that displays the {@code value}.
     * @param list the list that contains the value
     * @param value the value that is displayed or {@code null}
     * @param index the list index
     * @param selected whether the value is selected
     * @param cellHasFocus whether the value has the focus
     * @return the component to render the value
     */
    @NotNull
    protected abstract Component getListCellRendererComponent(@NotNull final JList<? extends T> list, @Nullable final T value, final int index, final boolean selected, final boolean cellHasFocus);

    /**
     * Called whenever the selected item has changed.
     */
    protected void updateSelectedItem() {
        if (inhibitActionListener) {
            return;
        }

        if (label == null) {
            return;
        }

        setChanged();
        @SuppressWarnings("unchecked") final T item = (T)comboBox.getSelectedItem();
        label.updateText(item == null ? "" : getDescription(item));
    }

    /**
     * Returns the description for an item.
     * @param item the item
     * @return the description
     */
    @NotNull
    protected abstract String getDescription(@Nullable T item);

}

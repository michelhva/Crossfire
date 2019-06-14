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
import java.util.regex.Pattern;
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
     * A {@link Pattern} that matches "[b]".
     */
    @NotNull
    private static final Pattern PATTERN_BOLD_BEGIN = Pattern.compile("\\[b]");

    /**
     * A {@link Pattern} that matches "[/b]".
     */
    @NotNull
    private static final Pattern PATTERN_BOLD_END = Pattern.compile("\\[/b]");

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
    protected void updateModel(@Nullable final List<T> elements) {
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

        setVisible(elements != null);
        if (updateModel) {
            try {
                inhibitActionListener = true;
                model.removeAllElements();
                if (elements != null) {
                    for (T element : elements) {
                        model.addElement(element);
                    }
                }
            } finally {
                inhibitActionListener = false;
            }
            updateSelectedItem();
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

        @SuppressWarnings("unchecked") final T item = (T)comboBox.getSelectedItem();
        final String text = item == null ? "" : getDescription(item);

        if (label != null) {
            setChanged();
            label.updateText(text);
        }
        updateSelectedItem(item);

        if (text.isEmpty()) {
            setTooltipText(null);
        } else {
            final StringBuilder sb = new StringBuilder();
            for (final String line0 : text.split("\n")) {
                final String line = line0.trim();

                int index = 0;
                while (line.length() > index+80) {
                    int nextIndex = line.lastIndexOf(' ', index+80);
                    if (nextIndex == -1) {
                        nextIndex = line.indexOf(' ', index+80);
                        if (nextIndex == -1) {
                            nextIndex = line.length();
                        }
                        if (nextIndex > index+140) {
                            nextIndex = index+140;
                        }
                    }
                    sb.append(sb.length() == 0 ? "<html>" : "<br>").append(line, index, nextIndex);

                    index = nextIndex;
                    while (index < line.length() && line.charAt(index) == ' ') {
                        index++;
                    }
                }
                sb.append(sb.length() == 0 ? "<html>" : "<br>").append(line, index, line.length());
            }
            setTooltipText(PATTERN_BOLD_END.matcher(PATTERN_BOLD_BEGIN.matcher(sb.toString()).replaceAll("<b>")).replaceAll("</b>"));
        }
    }

    /**
     * Will be called whenever the selected item has changed.
     * @param item the selected item
     */
    protected abstract void updateSelectedItem(@Nullable final T item);

    /**
     * Updates the selected item.
     * @param item the new selected item
     */
    protected void setSelectedItem(@NotNull final T item) {
        comboBox.setSelectedItem(item);
    }

    /**
     * Returns the description for an item.
     * @param item the item
     * @return the description
     */
    @NotNull
    protected abstract String getDescription(@Nullable T item);

}

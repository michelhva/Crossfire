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

package com.realtime.crossfire.jxclient.skin.skin;

import com.realtime.crossfire.jxclient.gui.commandlist.GUICommandFactory;
import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating {@link Gui} instances.
 * @author Andreas Kirschbaum
 */
public class GuiFactory {

    /**
     * The {@link GUICommandFactory} for creating commands.
     */
    private final GUICommandFactory guiCommandFactory;

    /**
     * All existing {@link Gui} instances.
     */
    @NotNull
    private final Collection<Gui> guis = new ArrayList<>();

    /**
     * Creates a new instance.
     * @param guiCommandFactory the gui command factory for creating commands
     */
    public GuiFactory(@NotNull final GUICommandFactory guiCommandFactory) {
        this.guiCommandFactory = guiCommandFactory;
    }

    /**
     * Creates a new {@link Gui} instance.
     * @return the new gui instance
     */
    @NotNull
    public Gui newGui() {
        final Gui gui = new Gui(new KeyBindings(null, guiCommandFactory));
        final JComponent component = gui.getComponent();
        component.setLayout(new GroupLayout(component));
        guis.add(gui);
        return gui;
    }

    /**
     * Returns an  element's absolute screen coordinate.
     * @param element the element
     * @return the element's absolute x coordinate
     */
    public int getElementX(@NotNull final AbstractGUIElement element) {
        final Gui gui = getGui(element);
        int x = gui == null ? 0 : gui.getComponent().getX();
        for (Component component = element; component != null && getGuiFromComponent(component) == null; component = component.getParent()) {
            x += component.getX();
        }
        return x;
    }

    /**
     * Returns an element's absolute screen coordinate.
     * @param element the element
     * @return the element's absolute y coordinate
     */
    public int getElementY(@NotNull final AbstractGUIElement element) {
        final Gui gui = getGui(element);
        int y = gui == null ? 0 : gui.getComponent().getY();
        for (Component component = element; component != null && getGuiFromComponent(component) == null; component = component.getParent()) {
            y += component.getY();
        }
        return y;
    }

    /**
     * Returns the {@link Gui} an element is part of.
     * @param element the element
     * @return the gui or {@code null}
     */
    @Nullable
    public Gui getGui(@NotNull final AbstractGUIElement element) {
        for (Component component = element; component != null; component = component.getParent()) {
            final Gui gui = getGuiFromComponent(component);
            if (gui != null) {
                return gui;
            }
        }
        return null;
    }

    /**
     * Returns the {@link Gui} instance for a {@link Component}.
     * @param component the component
     * @return the GUI instance or {@code null} if the component is not a GUI
     */
    @Nullable
    private Gui getGuiFromComponent(@NotNull final Component component) {
        for (final Gui gui : guis) {
            //noinspection ObjectEquality
            if (gui.getComponent() == component) {
                return gui;
            }
        }
        return null;
    }

}

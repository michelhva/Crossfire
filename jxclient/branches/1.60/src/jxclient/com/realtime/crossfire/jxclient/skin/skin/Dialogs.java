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

import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains a set of {@link Gui} instances representing dialog windows. These
 * dialogs can be looked up by name. Also maintains a set of dialogs pending
 * loading.
 * @author Andreas Kirschbaum
 */
public class Dialogs implements Iterable<Gui> {

    /**
     * The {@link GuiFactory} for creating new {@link Gui} instances.
     */
    @NotNull
    private final GuiFactory guiFactory;

    /**
     * The existing dialogs.
     */
    @NotNull
    private final JXCSkinCache<Gui> dialogs = new JXCSkinCache<Gui>("dialog");

    /**
     * Names of pending skin files.
     */
    @NotNull
    private final Set<String> dialogsToLoad = new HashSet<String>();

    /**
     * Creates a new instance.
     * @param guiFactory the gui factory for creating gui instances
     */
    public Dialogs(@NotNull final GuiFactory guiFactory) {
        this.guiFactory = guiFactory;
    }

    /**
     * Returns a dialog instance by dialog name.
     * @param name the dialog name
     * @return the dialog instance
     * @throws JXCSkinException if the dialog does not exist
     */
    @NotNull
    public Gui lookup(@NotNull final String name) throws JXCSkinException {
        return dialogs.lookup(name);
    }

    /**
     * Creates a new dialog instance.
     * @param name the dialog's name
     * @return the new dialog instance
     */
    @NotNull
    public Gui addDialog(@NotNull final String name) {
        try {
            return dialogs.lookup(name);
        } catch (final JXCSkinException ignored) {
            final Gui gui = guiFactory.newGui();
            try {
                dialogs.insert(name, gui);
            } catch (final JXCSkinException ex) {
                throw new AssertionError(ex);
            }
            dialogsToLoad.add(name);
            return gui;
        }
    }

    /**
     * Returns one dialog pending loading. Each dialog is returned only once.
     * @return a dialog pending loading or <code>null</code>
     */
    @Nullable
    public String getDialogToLoad() {
        final Iterator<String> it = dialogsToLoad.iterator();
        if (!it.hasNext()) {
            return null;
        }

        final String result = it.next();
        it.remove();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Iterator<Gui> iterator() {
        return dialogs.iterator();
    }

    /**
     * Returns whether the dialog state should be saved.
     * @return whether the dialog state should be saved
     */
    public boolean hasChangedDialog() {
        for (final Gui dialog : dialogs) {
            if (dialog.isChangedFromDefault()) {
                return true;
            }
        }

        return false;
    }

}

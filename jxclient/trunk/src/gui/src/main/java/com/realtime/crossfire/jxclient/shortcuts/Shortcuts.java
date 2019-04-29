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

package com.realtime.crossfire.jxclient.shortcuts;

import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages a list of {@link Shortcut Shortcuts}.
 * @author Andreas Kirschbaum
 */
public class Shortcuts implements Iterable<Shortcut> {

    /**
     * The shortcuts. Maps index to {@link Shortcut}. Unset entries are set to
     * {@code null}.
     */
    @NotNull
    private final List<Shortcut> shortcuts = new ArrayList<>();

    /**
     * Whether the contents of {@link #shortcuts} have been modified from the
     * last saved state.
     */
    private boolean modified;

    /**
     * The backing file.
     */
    @Nullable
    private File file;

    /**
     * The listeners to be notified.
     */
    @NotNull
    private final EventListenerList2<ShortcutsListener> listeners = new EventListenerList2<>();

    /**
     * The command queue for executing commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The {@link SpellsManager} instance to watch.
     */
    @NotNull
    private final SpellsManager spellsManager;

    /**
     * Creates a new instance.
     * @param commandQueue the command queue for executing commands
     * @param spellsManager the instance to watch
     */
    public Shortcuts(@NotNull final CommandQueue commandQueue, @NotNull final SpellsManager spellsManager) {
        this.commandQueue = commandQueue;
        this.spellsManager = spellsManager;
    }

    /**
     * Clears all defined shortcuts.
     */
    public void clearShortcuts() {
        if (shortcuts.isEmpty()) {
            return;
        }

        for (int i = 0; i < shortcuts.size(); i++) {
            final Shortcut shortcut = shortcuts.get(i);
            if (shortcut != null) {
                for (final ShortcutsListener listener : listeners) {
                    listener.shortcutRemoved(i, shortcut);
                }
                shortcut.dispose();
            }
        }
        shortcuts.clear();
        modified = true;
    }

    /**
     * Returns a shortcut.
     * @param index the shortcut index
     * @return the shortcut or {@code null} if the shortcut is unset
     */
    @Nullable
    private Shortcut getShortcut(final int index) {
        try {
            return shortcuts.get(index);
        } catch (final IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    /**
     * Sets a {@link Shortcut}.
     * @param index the shortcut index
     * @param shortcut the shortcut to set or {@code null} to unset
     */
    public void setShortcut(final int index, @Nullable final Shortcut shortcut) {
        while (shortcuts.size() <= index) {
            shortcuts.add(null);
        }

        final Shortcut oldShortcut = shortcuts.get(index);
        if (oldShortcut != null) {
            for (final ShortcutsListener listener : listeners) {
                listener.shortcutRemoved(index, oldShortcut);
            }
            oldShortcut.dispose();
        }
        shortcuts.set(index, shortcut);
        modified = true;
        if (shortcut != null) {
            for (final ShortcutsListener listener : listeners) {
                listener.shortcutAdded(index, shortcut);
            }
        }
    }

    /**
     * Unsets a {@link Shortcut}.
     * @param index the shortcut index
     */
    public void unsetShortcut(final int index) {
        setShortcut(index, null);
    }

    /**
     * Sets a {@link Shortcut} to a spell.
     * @param index the shortcut index
     * @param spellName the spell name to cast or invoke
     * @param cast whether the spell should be cast ({@code true}) or invoked
     * ({@code false})
     */
    public void setSpellShortcut(final int index, @NotNull final String spellName, final boolean cast) {
        final Spell spell = spellsManager.getSpell(spellName);
        setSpellShortcut(index, spell, cast);
    }

    /**
     * Sets a {@link Shortcut} to a spell.
     * @param index the shortcut index
     * @param spell the spell to cast or invoke
     * @param cast whether the spell should be cast ({@code true}) or invoked
     * ({@code false})
     */
    public void setSpellShortcut(final int index, @NotNull final Spell spell, final boolean cast) {
        final ShortcutSpell shortcutSpell = new ShortcutSpell(commandQueue, spell);
        shortcutSpell.setCast(cast);
        setShortcut(index, shortcutSpell);
    }

    /**
     * Sets a {@link Shortcut} to a command.
     * @param index the shortcut index
     * @param command the command to execute
     */
    public void setCommandShortcut(final int index, @NotNull final String command) {
        if (command.length() <= 0) {
            System.err.println("shortcut: ignoring empty command");
            return;
        }

        if (command.contains("\n")) {
            System.err.println("shortcut: ignoring multi-line command");
            return;
        }

        final Shortcut shortcutCommand = new ShortcutCommand(commandQueue, command);
        setShortcut(index, shortcutCommand);
    }

    /**
     * Executes a shortcut. Does nothing if the shortcut is unset.
     * @param index the shortcut index
     */
    public void executeShortcut(final int index) {
        final Shortcut shortcut = getShortcut(index);
        if (shortcut != null) {
            shortcut.execute();
        }
    }

    /**
     * Adds a {@link ShortcutsListener}.
     * @param listener the listener to add
     */
    public void addShortcutsListener(@NotNull final ShortcutsListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link ShortcutsListener}.
     * @param listener the listener to remove
     */
    public void removeShortcutsListener(@NotNull final ShortcutsListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns whether the shortcuts have been modified since creation or last
     * call to {@link #resetModified()}.
     * @return whether anything was modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Resets the modified state.
     */
    public void resetModified() {
        modified = false;
    }

    /**
     * Returns the backing file.
     * @return the backing file or {@code null} if unknown
     */
    @Nullable
    public File getFile() {
        return file;
    }

    /**
     * Sets the backing file.
     * @param file the backing file or {@code null} if unknown
     */
    public void setFile(@Nullable final File file) {
        this.file = file;
    }

    @Override
    public Iterator<Shortcut> iterator() {
        return shortcuts.iterator();
    }

}

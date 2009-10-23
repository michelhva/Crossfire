//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.shortcuts;

import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;

/**
 * Manages a list of {@link Shortcut}s.
 * @author Andreas Kirschbaum
 */
public class Shortcuts
{
    /**
     * The shortcuts. Maps index to {@link Shortcut}. Unset entries are set to
     * <code>null</code>.
     */
    private final ArrayList<Shortcut> shortcuts = new ArrayList<Shortcut>();

    /**
     * Whether the contents of {@link #shortcuts} have been modified from the
     * last saved state.
     */
    private boolean modified = false;

    /**
     * The backing file.
     */
    private File file = null;

    /**
     * The listeners to be notified.
     */
    private final EventListenerList listeners = new EventListenerList();

    /**
     * The command queue for executing commands.
     */
    private final CommandQueue commandQueue;

    /**
     * The {@link SpellsManager} instance to watch.
     */
    private final SpellsManager spellsManager;

    /**
     * Creates a new instance.
     * @param commandQueue the command queue for executing commands
     * @param spellsManager the instance to watch
     */
    public Shortcuts(final CommandQueue commandQueue, final SpellsManager spellsManager)
    {
        this.commandQueue = commandQueue;
        this.spellsManager = spellsManager;
    }

    /**
     * Reads shortcut definitions from a file. Previously set shortcuts are
     * overwritten.
     * @param file the file to read
     * @throws IOException if an I/O error occurs
     */
    public void load(final File file) throws IOException
    {
        modified = false;

        clearShortcuts();
        try
        {
            final FileInputStream fis = new FileInputStream(file);
            try
            {
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                try
                {
                    final BufferedReader br = new BufferedReader(isr);
                    try
                    {
                        int index = 0;
                        for (;;)
                        {
                            final String line = br.readLine();
                            if (line == null)
                            {
                                break;
                            }

                            if (line.equals("empty"))
                            {
                                setShortcut(index, null);
                                index++;
                            }
                            else if (line.startsWith("spell cast "))
                            {
                                setSpellShortcut(index, line.substring(11).trim(), true);
                                index++;
                            }
                            else if (line.startsWith("spell invoke "))
                            {
                                setSpellShortcut(index, line.substring(13).trim(), false);
                                index++;
                            }
                            else if (line.startsWith("command "))
                            {
                                setCommandShortcut(index, line.substring(8).trim());
                                index++;
                            }
                            else
                            {
                                System.err.println("shortcut: ignoring undefined entry '"+line+"'");
                            }
                        }
                    }
                    finally
                    {
                        br.close();
                    }
                }
                finally
                {
                    isr.close();
                }
            }
            finally
            {
                fis.close();
            }
        }
        catch (final IOException ex)
        {
            clearShortcuts();
            modified = false;
            this.file = file;
            throw ex;
        }
        modified = false;
        this.file = file;
    }

    /**
     * Writes the shortcut definitions to a file.
     * @throws IOException if an I/O exception occurs
     */
    public void save() throws IOException
    {
        if (!modified || file == null)
        {
            return;
        }

        final FileOutputStream fos = new FileOutputStream(file);
        try
        {
            final OutputStreamWriter osw = new OutputStreamWriter(fos);
            try
            {
                final BufferedWriter bw = new BufferedWriter(osw);
                try
                {
                    for (final Shortcut shortcut : shortcuts)
                    {
                        if (shortcut == null)
                        {
                            bw.write("empty\n");
                        }
                        else if (shortcut instanceof ShortcutSpell)
                        {
                            final ShortcutSpell shortcutSpell = (ShortcutSpell)shortcut;
                            bw.write("spell ");
                            bw.write(shortcutSpell.isCast() ? "cast " : "invoke ");
                            bw.write(shortcutSpell.getSpell().getName());
                            bw.write("\n");
                        }
                        else if (shortcut instanceof ShortcutCommand)
                        {
                            final ShortcutCommand shortcutCommand = (ShortcutCommand)shortcut;
                            bw.write("command ");
                            bw.write(shortcutCommand.getCommand());
                            bw.write("\n");
                        }
                        else
                        {
                            throw new AssertionError();
                        }
                    }
                }
                finally
                {
                    bw.close();
                }
            }
            finally
            {
                osw.close();
            }
        }
        finally
        {
            fos.close();
        }
    }

    /**
     * Clears all defined shortcuts.
     */
    private void clearShortcuts()
    {
        for (int i = 0; i < shortcuts.size(); i++)
        {
            final Shortcut shortcut = shortcuts.get(i);
            if (shortcut != null)
            {
                for (final ShortcutsListener listener : listeners.getListeners(ShortcutsListener.class))
                {
                    listener.shortcutRemoved(i, shortcut);
                }
                shortcut.dispose();
            }
        }
        shortcuts.clear();
    }

    /**
     * Returns a shortcut.
     * @param index the shortcut index
     * @return the shortcut or <code>null</code> if the shortcut is unset
     */
    private Shortcut getShortcut(final int index)
    {
        try
        {
            return shortcuts.get(index);
        }
        catch (final IndexOutOfBoundsException ex)
        {
            return null;
        }
    }

    /**
     * Sets a {@link Shortcut}.
     * @param index the shortcut index
     * @param shortcut the shortcut to set or <code>null</code> to unset
     */
    private void setShortcut(final int index, final Shortcut shortcut)
    {
        while (shortcuts.size() <= index)
        {
            shortcuts.add(null);
        }

        final Shortcut oldShortcut = shortcuts.get(index);
        if (oldShortcut != null)
        {
            for (final ShortcutsListener listener : listeners.getListeners(ShortcutsListener.class))
            {
                listener.shortcutRemoved(index, oldShortcut);
            }
            oldShortcut.dispose();
        }
        shortcuts.set(index, shortcut);
        modified = true;
        if (shortcut != null)
        {
            for (final ShortcutsListener listener : listeners.getListeners(ShortcutsListener.class))
            {
                listener.shortcutAdded(index, shortcut);
            }
        }
    }

    /**
     * Unsets a {@link Shortcut}.
     * @param index the shortcut index
     */
    public void unsetShortcut(final int index)
    {
        setShortcut(index, null);
    }

    /**
     * Sets a {@link Shortcut} to a spell.
     * @param index the shortcut index
     * @param spellName the spell name to cast or invoke
     * @param cast whether the spell should be cast (<code>true</code>) or
     * invoked (<code>false</code>)
     */
    private void setSpellShortcut(final int index, final String spellName, final boolean cast)
    {
        final Spell spell = spellsManager.getSpell(spellName);
        setSpellShortcut(index, spell, cast);
    }

    /**
     * Sets a {@link Shortcut} to a spell.
     * @param index the shortcut index
     * @param spell the spell to cast or invoke
     * @param cast whether the spell should be cast (<code>true</code>) or
     * invoked (<code>false</code>)
     */
    public void setSpellShortcut(final int index, final Spell spell, final boolean cast)
    {
        final ShortcutSpell shortcutSpell = new ShortcutSpell(commandQueue, spell);
        shortcutSpell.setCast(cast);
        setShortcut(index, shortcutSpell);
    }

    /**
     * Sets a {@link Shortcut} to a command.
     * @param index the shortcut index
     * @param command the command to execute
     */
    private void setCommandShortcut(final int index, final String command)
    {
        if (command.length() <= 0)
        {
            System.err.println("shortcut: ignoring empty command");
            return;
        }

        if (command.contains("\n"))
        {
            System.err.println("shortcut: ignoring multi-line command");
            return;
        }

        final ShortcutCommand shortcutCommand = new ShortcutCommand(commandQueue, command);
        setShortcut(index, shortcutCommand);
    }
    /**
     * Executes a shortcut. Does nothing if the shortcut is unset.
     * @param index the shortcut index
     */
    public void executeShortcut(final int index)
    {
        final Shortcut shortcut = getShortcut(index);
        if (shortcut != null)
        {
            shortcut.execute();
        }
    }

    /**
     * Returns the number of shortcut slots.
     * @return the number of shortcut slots
     */
    public int size()
    {
        return shortcuts.size();
    }

    /**
     * Adds a {@link ShortcutsListener}.
     * @param listener the listener to add
     */
    public void addShortcutsListener(final ShortcutsListener listener)
    {
        listeners.add(ShortcutsListener.class, listener);
    }

    /**
     * Removes a {@link ShortcutsListener}.
     * @param listener the listener to remove
     */
    public void removeShortcutsListener(final ShortcutsListener listener)
    {
        listeners.remove(ShortcutsListener.class, listener);
    }

    /**
     * Returns the backing file.
     * @return the backing file or <code>null</code> if unknown
     */
    public File getFile()
    {
        return file;
    }
}

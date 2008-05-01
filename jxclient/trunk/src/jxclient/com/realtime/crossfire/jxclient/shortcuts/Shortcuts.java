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

import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.spells.Spell;
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
 * Manages a list of shortcut commands.
 *
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
     * The listeners to be notified.
     */
    private final EventListenerList listeners = new EventListenerList();

    /**
     * The window to cast the spell in.
     */
    private final JXCWindow jxcWindow;

    /**
     * Create a new instance.
     *
     * @param jxcWindow The window to cast the spell in.
     */
    public Shortcuts(final JXCWindow jxcWindow)
    {
        this.jxcWindow = jxcWindow;
    }

    /**
     * Read shortcut definitions from a file. Previously set shortcuts are
     * overwritten.
     *
     * @param file The file to read.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void load(final File file) throws IOException
    {
        modified = false;
        shortcuts.clear();
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
            shortcuts.clear();
            modified = false;
            throw ex;
        }
        modified = false;
    }

    /**
     * Write the shortcut definitions to a file.
     *
     * @param file The file to write to.
     *
     * @throws IOException If an I/O exception occurs.
     */
    public void save(final File file) throws IOException
    {
        if (!modified)
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
                            bw.write(shortcutSpell.getSpell().getInternalName());
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
     * Return a shortcut.
     *
     * @param index The shortcut index.
     *
     * @return The shortcut, or <code>null</code> if the shortcut is unset.
     */
    public Shortcut getShortcut(final int index)
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

    public void setShortcut(final int index, final Shortcut shortcut)
    {
        while (shortcuts.size() <= index)
        {
            shortcuts.add(null);
            modified = true;
        }

        final Shortcut oldShortcut = shortcuts.get(index);
        if (oldShortcut != null)
        {
            for (final ShortcutsListener listener : listeners.getListeners(ShortcutsListener.class))
            {
                listener.shortcutRemoved(index, oldShortcut);
            }
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

    public void unsetShortcut(final int index)
    {
        setShortcut(index, null);
    }

    public void setSpellShortcut(final int index, final String spellName, final boolean cast)
    {
        final Spell spell = ItemsList.getSpellsManager().getSpell(spellName);
        if (spell == null)
        {
            System.err.println("shortcut: ignoring undefined spell '"+spellName+"'");
            return;
        }

        setSpellShortcut(index, spell, cast);
    }

    public void setSpellShortcut(final int index, final Spell spell, final boolean cast)
    {
        final ShortcutSpell shortcutSpell = new ShortcutSpell(jxcWindow, spell);
        shortcutSpell.setCast(cast);
        setShortcut(index, shortcutSpell);
    }

    public void setCommandShortcut(final int index, final String command)
    {
        if (command.length() <= 0)
        {
            System.err.println("shortcut: ignoring empty command");
            return;
        }

        if (command.indexOf('\n') != 0)
        {
            System.err.println("shortcut: ignoring multi-line command");
            return;
        }

        final ShortcutCommand shortcutCommand = new ShortcutCommand(jxcWindow, command);
        setShortcut(index, shortcutCommand);
    }
    /**
     * Execute a shortcut. Does nothing if the shortcut is unset.
     *
     * @param index The shortcut index.
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
     * Return the number of shortcut slots.
     *
     * @return The number of shortcut slots.
     */
    public int size()
    {
        return shortcuts.size();
    }

    /**
     * Register a {@link ShortcutsListener}.
     *
     * @param listener The listener to register.
     */
    public void addShortcutsListener(final ShortcutsListener listener)
    {
        listeners.add(ShortcutsListener.class, listener);
    }
}

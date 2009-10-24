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
package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * Manages shortcuts.
 * @author Andreas Kirschbaum
 */
public class ShortcutsManager
{
    /**
     * The shortcuts.
     */
    @NotNull
    private final Shortcuts shortcuts;

    /**
     * Creates a new instance.
     * @param commandQueue the command queue to affect
     * @param spellsManager the spells manager to use
     */
    public ShortcutsManager(@NotNull final CommandQueue commandQueue, @NotNull final SpellsManager spellsManager)
    {
        shortcuts = new Shortcuts(commandQueue, spellsManager);
    }

    /**
     * Load shortcut info from the backing file.
     * @param hostname the current hostname
     * @param character the current character name
     */
    public void loadShortcuts(@NotNull final CharSequence hostname, @NotNull final CharSequence character)
    {
        final File file;
        try
        {
            file = Filenames.getShortcutsFile(hostname, character);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read shortcuts file: "+ex.getMessage());
            return;
        }

        try
        {
            shortcuts.load(file);
        }
        catch (final FileNotFoundException ex)
        {
            return;
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read shortcuts file "+file+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Save all shortcut info to the backing file.
     */
    public void saveShortcuts()
    {
        try
        {
            shortcuts.save();
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write shortcuts file "+shortcuts.getFile()+": "+ex.getMessage());
            return;
        }
    }

    @Deprecated
    @NotNull
    public Shortcuts getShortcuts()
    {
        return shortcuts;
    }
}

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

package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.shortcuts.Shortcut;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutCommand;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutSpell;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.jetbrains.annotations.NotNull;

/**
 * Manages shortcuts.
 * @author Andreas Kirschbaum
 */
public class ShortcutsLoader {

    /**
     * Private constructor to prevent instantiation.
     */
    private ShortcutsLoader() {
    }

    /**
     * Load shortcut info from the backing file.
     * @param shortcuts the shortcuts instance to update
     * @param hostname the current hostname
     * @param character the current character name
     */
    public static void loadShortcuts(@NotNull final Shortcuts shortcuts, @NotNull final CharSequence hostname, @NotNull final CharSequence character) {
        final File file;
        try {
            file = Filenames.getShortcutsFile(hostname, character);
        } catch (final IOException ex) {
            System.err.println("Cannot read shortcuts file: "+ex.getMessage());
            return;
        }

        try {
            shortcuts.clearShortcuts();
            try {
                final FileInputStream fis = new FileInputStream(file);
                try {
                    final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                    try {
                        final BufferedReader br = new BufferedReader(isr);
                        try {
                            int index = 0;
                            while (true) {
                                final String line = br.readLine();
                                if (line == null) {
                                    break;
                                }

                                if (line.equals("empty")) {
                                    shortcuts.setShortcut(index, null);
                                    index++;
                                } else if (line.startsWith("spell cast ")) {
                                    shortcuts.setSpellShortcut(index, line.substring(11).trim(), true);
                                    index++;
                                } else if (line.startsWith("spell invoke ")) {
                                    shortcuts.setSpellShortcut(index, line.substring(13).trim(), false);
                                    index++;
                                } else if (line.startsWith("command ")) {
                                    shortcuts.setCommandShortcut(index, line.substring(8).trim());
                                    index++;
                                } else {
                                    System.err.println("shortcut: ignoring undefined entry '"+line+"'");
                                }
                            }
                        } finally {
                            br.close();
                        }
                    } finally {
                        isr.close();
                    }
                } finally {
                    fis.close();
                }
            } catch (final IOException ex) {
                shortcuts.clearShortcuts();
                shortcuts.resetModified();
                shortcuts.setFile(file);
                throw ex;
            }
            shortcuts.resetModified();
            shortcuts.setFile(file);
        } catch (final FileNotFoundException ignored) {
            //noinspection UnnecessaryReturnStatement
            return;
        } catch (final IOException ex) {
            System.err.println("Cannot read shortcuts file "+file+": "+ex.getMessage());
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    /**
     * Save all shortcut info to the backing file.
     * @param shortcuts the shortcuts instance to save
     */
    public static void saveShortcuts(@NotNull final Shortcuts shortcuts) {
        try {
            if (!shortcuts.isModified()) {
                return;
            }

            final File file = shortcuts.getFile();
            if (file == null) {
                return;
            }

            final FileOutputStream fos = new FileOutputStream(file);
            try {
                final OutputStreamWriter osw = new OutputStreamWriter(fos);
                try {
                    final BufferedWriter bw = new BufferedWriter(osw);
                    try {
                        for (final Shortcut shortcut : shortcuts) {
                            if (shortcut == null) {
                                bw.write("empty\n");
                            } else if (shortcut instanceof ShortcutSpell) {
                                final ShortcutSpell shortcutSpell = (ShortcutSpell)shortcut;
                                bw.write("spell ");
                                bw.write(shortcutSpell.isCast() ? "cast " : "invoke ");
                                bw.write(shortcutSpell.getSpell().getName());
                                bw.write("\n");
                            } else if (shortcut instanceof ShortcutCommand) {
                                final ShortcutCommand shortcutCommand = (ShortcutCommand)shortcut;
                                bw.write("command ");
                                bw.write(shortcutCommand.getCommand());
                                bw.write("\n");
                            } else {
                                throw new AssertionError();
                            }
                        }
                    } finally {
                        bw.close();
                    }
                } finally {
                    osw.close();
                }
            } finally {
                fos.close();
            }
        } catch (final IOException ex) {
            System.err.println("Cannot write shortcuts file "+shortcuts.getFile()+": "+ex.getMessage());
            return;
        }

        shortcuts.resetModified();
    }

}

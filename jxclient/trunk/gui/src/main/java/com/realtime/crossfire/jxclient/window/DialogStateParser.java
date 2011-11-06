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

import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.label.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to store or restore the dialog states to/from a file.
 * @author Andreas Kirschbaum
 */
public class DialogStateParser {

    /**
     * The pattern to split fields in the save file.
     */
    @NotNull
    private static final Pattern PATTERN = Pattern.compile(" ");

    /**
     * Private constructor to prevent instantiation.
     */
    private DialogStateParser() {
    }

    /**
     * Loads the dialogs state from a file.
     * @param skin the skin to update
     * @param windowRenderer the window renderer instance to attach to
     */
    public static void load(@NotNull final JXCSkin skin, @NotNull final JXCWindowRenderer windowRenderer) {
        final String skinName = skin.getSkinName();
        final File dialogsFile;
        try {
            dialogsFile = Filenames.getDialogsFile(skinName);
        } catch (final IOException ex) {
            System.err.println(skin.getSkinName()+": "+ex.getMessage());
            return;
        }

        try {
            final FileInputStream fis = new FileInputStream(dialogsFile);
            try {
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                try {
                    final BufferedReader br = new BufferedReader(isr);
                    try {
                        while (true) {
                            final String line = br.readLine();
                            if (line == null) {
                                break;
                            }

                            final String[] tmp = PATTERN.split(line, -1);
                            if (tmp.length != 6) {
                                throw new IOException("syntax error: "+line);
                            }

                            final boolean open;
                            if (tmp[0].equals("open")) {
                                open = true;
                            } else if (tmp[0].equals("close")) {
                                open = false;
                            } else {
                                throw new IOException("syntax error: "+line);
                            }

                            final Gui dialog;
                            try {
                                dialog = skin.getDialog(tmp[1]);
                            } catch (final JXCSkinException ex) {
                                throw new IOException("no such dialog: "+tmp[1], ex);
                            }

                            if (!dialog.isAutoSize() && dialog.isSaveDialog()) {
                                final int x;
                                final int y;
                                final int w;
                                final int h;
                                try {
                                    x = Integer.parseInt(tmp[2]);
                                    y = Integer.parseInt(tmp[3]);
                                    w = Integer.parseInt(tmp[4]);
                                    h = Integer.parseInt(tmp[5]);
                                } catch (final NumberFormatException ex) {
                                    throw new IOException("syntax error: "+line, ex);
                                }

                                try {
                                    dialog.setSize(w, h);
                                } catch (final IllegalArgumentException ex) {
                                    throw new IOException("invalid dialog size for "+tmp[1]+": "+w+"x"+h, ex);
                                }

                                dialog.setPosition(x, y);
                            }

                            if (open) {
                                windowRenderer.openDialog(dialog, false);
                            } else {
                                windowRenderer.closeDialog(dialog);
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
        } catch (final FileNotFoundException ignored) {
            // ignore
        } catch (final IOException ex) {
            System.err.println(dialogsFile+": "+ex.getMessage());
        }
    }

    /**
     * Saves the dialogs state to a file.
     * @param skin the skin to update
     * @param windowRenderer the window renderer instance to attach to
     */
    public static void save(@NotNull final JXCSkin skin, @NotNull final JXCWindowRenderer windowRenderer) {
        final File dialogsFile;
        try {
            dialogsFile = Filenames.getDialogsFile(skin.getSkinName());
        } catch (final IOException ex) {
            System.err.println(skin.getSkinName()+": "+ex.getMessage());
            return;
        }

        final File dir = dialogsFile.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            System.err.println(skin.getSkinName()+": cannot create directory");
        }

        final List<Gui> openDialogs = new LinkedList<Gui>();
        for (final Gui dialog : windowRenderer.getOpenDialogs()) {
            openDialogs.add(0, dialog);
        }

        try {
            final FileOutputStream fos = new FileOutputStream(dialogsFile);
            try {
                final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                try {
                    final BufferedWriter bw = new BufferedWriter(osw);
                    try {
                        for (final Gui dialog : openDialogs) {
                            saveDialog(dialog, "open", bw);
                        }

                        for (final Gui dialog : skin) {
                            if (!windowRenderer.isDialogOpen(dialog)) {
                                saveDialog(dialog, "close", bw);
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
            System.err.println(dialogsFile+": "+ex.getMessage());
        }
    }

    /**
     * Saves the state of one dialog.
     * @param dialog the dialog to save
     * @param type the dialog state; either "open" or "close"
     * @param bw the writer to use
     * @throws IOException if an I/O error occurs
     */
    private static void saveDialog(@NotNull final Gui dialog, @NotNull final String type, @NotNull final Writer bw) throws IOException {
        if (dialog.isAutoSize()) {
            return;
        }

        if (!dialog.isSaveDialog()) {
            return;
        }

        final int w = dialog.getWidth();
        if (w <= 0) {
            return;
        }

        final int h = dialog.getHeight();
        if (h <= 0) {
            return;
        }

        bw.write(type);
        bw.write(" ");
        bw.write(dialog.getName());
        bw.write(" ");
        bw.write(Integer.toString(dialog.getX()));
        bw.write(" ");
        bw.write(Integer.toString(dialog.getY()));
        bw.write(" ");
        bw.write(Integer.toString(dialog.getWidth()));
        bw.write(" ");
        bw.write(Integer.toString(dialog.getHeight()));
        bw.write("\n");
    }

}

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
package com.realtime.crossfire.jxclient;

import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.skin.JXCSkin;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class to store or restore the dialog states to/from a file.
 *
 * @author Andreas Kirschbaum
 */
public class DialogStateParser
{
    /**
     * The pattern to split fields in the save file.
     */
    private static final Pattern pattern = Pattern.compile(" ");

    /**
     * Private constructor to prevent instantiation.
     */
    private DialogStateParser()
    {
    }

    /**
     * Load the dialogs state from a file.
     * @param skin the skin to update
     * @param windowRenderer the window renderer instance to attach to
     */
    public static void load(final JXCSkin skin, final JXCWindowRenderer windowRenderer)
    {
        final String skinName = skin.getSkinName();
        final File dialogsFile;
        try
        {
            dialogsFile = Filenames.getDialogsFile(skinName);

            // Hack for loading obsolete state files
            if (skinName.endsWith("@1024x768"))
            {
                final File obsoleteDialogsFile = Filenames.getDialogsFile(skinName.replaceAll("@.*", ""));
                if (obsoleteDialogsFile.exists())
                {
                    if (!dialogsFile.exists())
                    {
                        dialogsFile.getParentFile().mkdirs();
                        obsoleteDialogsFile.renameTo(dialogsFile);
                    }
                    else
                    {
                        obsoleteDialogsFile.delete();
                    }
                    obsoleteDialogsFile.getParentFile().delete();
                }
            }
        }
        catch (final IOException ex)
        {
            System.err.println(skin.getSkinName()+": "+ex.getMessage());
            return;
        }

        try
        {
            final FileInputStream fis = new FileInputStream(dialogsFile);
            try
            {
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                try
                {
                    final BufferedReader br = new BufferedReader(isr);
                    try
                    {
                        for (;;)
                        {
                            final String line = br.readLine();
                            if (line == null)
                            {
                                break;
                            }

                            final String[] tmp = pattern.split(line, -1);
                            if (tmp.length != 6)
                            {
                                throw new IOException("syntax error: "+line);
                            }

                            final boolean open;
                            if (tmp[0].equals("open"))
                            {
                                open = true;
                            }
                            else if (tmp[0].equals("close"))
                            {
                                open = false;
                            }
                            else
                            {
                                throw new IOException("syntax error: "+line);
                            }

                            final Gui dialog = skin.getDialog(tmp[1]);
                            if (dialog == null)
                            {
                                throw new IOException("no such dialog: "+tmp[1]);
                            }

                            final int x, y, w, h;
                            try
                            {
                                x = Integer.parseInt(tmp[2]);
                                y = Integer.parseInt(tmp[3]);
                                w = Integer.parseInt(tmp[4]);
                                h = Integer.parseInt(tmp[5]);
                            }
                            catch (final NumberFormatException ex)
                            {
                                throw new IOException("syntax error: "+line);
                            }

                            try
                            {
                                dialog.setSize(w, h);
                            }
                            catch (final IllegalArgumentException ex)
                            {
                                throw new IOException("invalid dialog size for "+tmp[1]+": "+w+"x"+h);
                            }

                            dialog.setPosition(x, y);

                            if (open)
                            {
                                windowRenderer.openDialog(dialog);
                            }
                            else
                            {
                                windowRenderer.closeDialog(dialog);
                            }

                            dialog.setStateChanged(false);
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
        catch (final FileNotFoundException ex)
        {
            // ignore
        }
        catch (final IOException ex)
        {
            System.err.println(dialogsFile+": "+ex.getMessage());
        }
    }

    /**
     * Save the dialogs state to a file.
     * @param skin the skin to update
     * @param windowRenderer the window renderer instance to attach to
     */
    public static void save(final JXCSkin skin, final JXCWindowRenderer windowRenderer)
    {
        if (!hasChangedDialog(skin))
        {
            return;
        }

        final File dialogsFile;
        try
        {
            dialogsFile = Filenames.getDialogsFile(skin.getSkinName());
        }
        catch (final IOException ex)
        {
            System.err.println(skin.getSkinName()+": "+ex.getMessage());
            return;
        }

        final File dir = dialogsFile.getParentFile();
        if (dir != null)
        {
            dir.mkdirs();
        }

        final List<Gui> openDialogs = new LinkedList<Gui>();
        for (final Gui dialog : windowRenderer.getOpenDialogs())
        {
            openDialogs.add(0, dialog);
        }

        try
        {
            final FileOutputStream fos = new FileOutputStream(dialogsFile);
            try
            {
                final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                try
                {
                    final BufferedWriter bw = new BufferedWriter(osw);
                    try
                    {
                        for (final Gui dialog : openDialogs)
                        {
                            saveDialog(dialog, "open", bw);
                            assert !isChangedDialog(dialog);
                        }

                        for (final Gui dialog : skin)
                        {
                            if (!windowRenderer.isDialogOpen(dialog))
                            {
                                saveDialog(dialog, "close", bw);
                            }

                            assert !isChangedDialog(dialog);
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
        catch (final IOException ex)
        {
            System.err.println(dialogsFile+": "+ex.getMessage());
        }

        assert !hasChangedDialog(skin);
    }

    /**
     * Save the state of one dialog.
     *
     * @param dialog The dialog to save.
     *
     * @param type The dialog state; either "open" or "close".
     *
     * @param bw The writer to use.
     *
     * @throws IOException If an I/O error occurs.
     */
    private static void saveDialog(final Gui dialog, final String type, final BufferedWriter bw) throws IOException
    {
        final String dialogName = dialog.getName();
        if (dialogName == null)
        {
            return;
        }

        final int w = dialog.getWidth();
        if (w <= 0)
        {
            return;
        }

        final int h = dialog.getHeight();
        if (h <= 0)
        {
            return;
        }

        bw.write(type);
        bw.write(" ");
        bw.write(dialogName);
        bw.write(" ");
        bw.write(Integer.toString(dialog.getX()));
        bw.write(" ");
        bw.write(Integer.toString(dialog.getY()));
        bw.write(" ");
        bw.write(Integer.toString(dialog.getWidth()));
        bw.write(" ");
        bw.write(Integer.toString(dialog.getHeight()));
        bw.write("\n");

        dialog.setStateChanged(false);
    }

    /**
     * Return whether the dialog state should be saved.
     *
     * @param skin The skin to check.
     *
     * @return Whether the dialog state should be saved.
     */
    private static boolean hasChangedDialog(final JXCSkin skin)
    {
        for (final Gui dialog : skin)
        {
            if (isChangedDialog(dialog))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Return whether one dialog has changed from its default state.
     *
     * @param dialog The dialog to check.
     *
     * @return Whether the state has changed.
     */
    private static boolean isChangedDialog(final Gui dialog)
    {
        if (dialog.getName() == null)
        {
            return false;
        }

        if (dialog.getWidth() <= 0 || dialog.getHeight() <= 0)
        {
            return false;
        }

        if (!dialog.isStateChanged())
        {
            return false;
        }

        return true;
    }
}

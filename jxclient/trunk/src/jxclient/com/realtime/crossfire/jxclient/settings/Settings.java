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
package com.realtime.crossfire.jxclient.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * Maintains a set of key/value pairs. The values are stored in a flat file.
 *
 * @author Andreas Kirschbaum
 */
public class Settings
{
    /**
     * The file for loading/saving values.
     */
    private final File file;

    /**
     * The stored values. Maps key name to value.
     */
    private final Map<String, String> values = new TreeMap<String, String>();

    /**
     * Flag to inhibit saving.
     */
    private boolean noSave = true;

    /**
     * Create a new instance.
     *
     * @param file The file for loading/saving values.
     */
    public Settings(final File file)
    {
        this.file = file;
        loadValues();
        noSave = false;
    }

    /**
     * Return the string associated with the specified key at a node, or
     * <code>defaultValue</code> if there is no association for this key.
     *
     * @param key Key to get value for.
     *
     * @return The value.
     */
    public String getString(final String key, final String defaultValue)
    {
        final String value = values.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Return the boolean associated with the specified key at a node, or
     * <code>defaultValue</code> if there is no association for this key.
     *
     * @param key Key to get value for.
     *
     * @return The value.
     */
    public boolean getBoolean(final String key, final boolean defaultValue)
    {
        final String value = getString(key, Boolean.toString(defaultValue));
        try
        {
            return Boolean.parseBoolean(value);
        }
        catch (final NumberFormatException ex)
        {
            return defaultValue;
        }
    }

    /**
     * Return the integer associated with the specified key at a node, or
     * <code>defaultValue</code> if there is no association for this key.
     *
     * @param key Key to get value for.
     *
     * @return The value.
     */
    public int getInt(final String key, final int defaultValue)
    {
        final String value = getString(key, Integer.toString(defaultValue));
        try
        {
            return Integer.parseInt(value);
        }
        catch (final NumberFormatException ex)
        {
            return defaultValue;
        }
    }

    /**
     * Store a key/value pair.
     *
     * @param key The key to store.
     *
     * @param value The value to store.
     */
    public void putString(final String key, final String value)
    {
        final String oldValue = values.put(key, value);
        if (oldValue == null || !oldValue.equals(value))
        {
            setChanged();
        }
    }

    /**
     * Store a key/value pair.
     *
     * @param key The key to store.
     *
     * @param value The value to store.
     */
    public void putBoolean(final String key, final boolean value)
    {
        putString(key, Boolean.toString(value));
    }

    /**
     * Store a key/value pair.
     *
     * @param key The key to store.
     *
     * @param value The value to store.
     */
    public void putInt(final String key, final int value)
    {
        putString(key, Integer.toString(value));
    }

    /**
     * Remove a key. Does nothing if the key has no associated value.
     *
     * @param key The key to remove.
     */
    public void remove(final String key)
    {
        if (values.remove(key) != null)
        {
            setChanged();
        }
    }

    /**
     * This function is called whenever the contents of {@link #values} has
     * changed.
     */
    private void setChanged()
    {
        if (noSave)
        {
            return;
        }

        try
        {
            saveValues();
        }
        catch (final IOException ex)
        {
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * Load the values from the backing file.
     */
    private void loadValues()
    {
        values.clear();

        try
        {
            final FileInputStream fis = new FileInputStream(file);
            try
            {
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                try
                {
                    final LineNumberReader lnr = new LineNumberReader(isr);
                    try
                    {
                        for (;;)
                        {
                            final String line2 = lnr.readLine();
                            if (line2 == null)
                            {
                                break;
                            }
                            final String line = Codec.decode(line2.trim());
                            if (line == null || line.startsWith("#") || line.length() == 0)
                            {
                                continue;
                            }

                            final String[] tmp = line.split("=", 2);
                            if (tmp.length != 2)
                            {
                                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                                continue;
                            }
                            final String key = tmp[0];
                            final String value = tmp[1];

                            putString(key, value);
                        }
                    }
                    finally
                    {
                        lnr.close();
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
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * Save the values to the backing file.
     *
     * @throws IOException if the values cannot be saved
     */
    private void saveValues() throws IOException
    {
        final File tmpFile = new File(file.getPath() + ".tmp");
        final FileOutputStream fos = new FileOutputStream(tmpFile);
        try
        {
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            try
            {
                final BufferedWriter bw = new BufferedWriter(osw);
                try
                {
                    saveNode(bw, values);
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

        if (!tmpFile.renameTo(file))
        {
            throw new IOException("cannot rename "+tmpFile+" to "+file);
        }
    }

    /**
     * Save one node.
     *
     * @param writer The <code>Writer</code> to write to.
     *
     * @param node The node to save.
     *
     * @throws IOException if the node cannot be saved
     */
    private static void saveNode(final BufferedWriter writer, final Map<String, String> node) throws IOException
    {
        if (node.isEmpty())
        {
            return;
        }

        for (final String key : node.keySet())
        {
            writer.newLine();
            writer.write(Codec.encode(key));
            writer.write("=");
            writer.write(Codec.encode(node.get(key)));
            writer.newLine();
        }
    }
}

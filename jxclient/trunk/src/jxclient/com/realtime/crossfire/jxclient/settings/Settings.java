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

package com.realtime.crossfire.jxclient.settings;

import com.realtime.crossfire.jxclient.util.NumberParser;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains a set of key/value pairs. The values are stored in a flat file.
 * @author Andreas Kirschbaum
 */
public class Settings {

    /**
     * The file for loading/saving values.
     */
    @NotNull
    private final File file;

    /**
     * The stored values. Maps key name to value.
     */
    @NotNull
    private final Map<String, Entry> values = new TreeMap<String, Entry>();

    /**
     * Flag to inhibit saving.
     */
    private boolean noSave = true;

    /**
     * Creates a new instance.
     * @param file the file for loading/saving values
     */
    public Settings(@NotNull final File file) {
        this.file = file;
        loadValues();
        noSave = false;
    }

    /**
     * Returns the string associated with the specified key at a node, or
     * <code>defaultValue</code> if there is no association for this key.
     * @param key the key to get the value for
     * @param defaultValue the default value
     * @return the value
     */
    @NotNull
    public String getString(@NotNull final String key, @NotNull final String defaultValue) {
        final Entry entry = values.get(key);
        return entry != null ? entry.getValue() : defaultValue;
    }

    /**
     * Returns the boolean associated with the specified key at a node or
     * <code>defaultValue</code> if there is no association for this key.
     * @param key the key to get the value for
     * @param defaultValue the default value
     * @return the value
     */
    public boolean getBoolean(@NotNull final String key, final boolean defaultValue) {
        final String value = getString(key, Boolean.toString(defaultValue));
        try {
            return Boolean.parseBoolean(value);
        } catch (final NumberFormatException ignored) {
            return defaultValue;
        }
    }

    /**
     * Returns the integer associated with the specified key at a node or
     * <code>defaultValue</code> if there is no association for this key.
     * @param key the key to get the value for
     * @param defaultValue the default value
     * @return the value
     */
    public int getInt(@NotNull final String key, final int defaultValue) {
        return NumberParser.parseInt(getString(key, Integer.toString(defaultValue)), defaultValue);
    }

    /**
     * Returns the long associated with the specified key at a node or
     * <code>defaultValue</code> if there is no association for this key.
     * @param key the key to get the value for
     * @param defaultValue the default value
     * @return the value
     */
    public long getLong(@NotNull final String key, final long defaultValue) {
        return NumberParser.parseLong(getString(key, Long.toString(defaultValue)), defaultValue);
    }

    /**
     * Stores a key/value pair.
     * @param key the key to store
     * @param value the value to store
     * @param documentation the documentation string of the entry or
     * <code>null</code> if unknown
     */
    public void putString(@NotNull final String key, @NotNull final String value, @Nullable final String documentation) {
        final Entry oldEntry = values.get(key);
        if (oldEntry != null) {
            oldEntry.setDocumentation(documentation);
            if (!oldEntry.getValue().equals(value)) {
                oldEntry.setValue(value);
                setChanged();
            }
        } else {
            values.put(key, new Entry(value, documentation));
        }
    }

    /**
     * Stores a key/value pair.
     * @param key the key to store
     * @param value the value to store
     * @param documentation the documentation string of the entry
     */
    public void putBoolean(@NotNull final String key, final boolean value, @NotNull final String documentation) {
        putString(key, Boolean.toString(value), documentation);
    }

    /**
     * Stores a key/value pair.
     * @param key the key to store
     * @param value the value to store
     * @param documentation the documentation string of the entry
     */
    public void putInt(@NotNull final String key, final int value, @NotNull final String documentation) {
        putString(key, Integer.toString(value), documentation);
    }

    /**
     * Stores a key/value pair.
     * @param key the key to store
     * @param value the value to store
     * @param documentation the documentation string of the entry
     */
    public void putLong(@NotNull final String key, final long value, @NotNull final String documentation) {
        putString(key, Long.toString(value), documentation);
    }

    /**
     * Removes a key. Does nothing if the key has no associated value.
     * @param key the key to remove
     */
    public void remove(@NotNull final String key) {
        if (values.remove(key) != null) {
            setChanged();
        }
    }

    /**
     * This function is called whenever the contents of {@link #values} has
     * changed.
     */
    private void setChanged() {
        if (noSave) {
            return;
        }

        try {
            saveValues();
        } catch (final IOException ex) {
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * Loads the values from the backing file.
     */
    private void loadValues() {
        values.clear();

        try {
            final FileInputStream fis = new FileInputStream(file);
            try {
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                try {
                    final LineNumberReader lnr = new LineNumberReader(isr);
                    try {
                        while (true) {
                            final String line2 = lnr.readLine();
                            if (line2 == null) {
                                break;
                            }
                            final String line = Codec.decode(line2.trim());
                            if (line == null || line.startsWith("#") || line.length() == 0) {
                                continue;
                            }

                            final String[] tmp = line.split("=", 2);
                            if (tmp.length != 2) {
                                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                                continue;
                            }
                            final String key = tmp[0];
                            final String value = tmp[1];

                            putString(key, value, null);
                        }
                    } finally {
                        lnr.close();
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
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * Saves the values to the backing file.
     * @throws IOException if the values cannot be saved
     */
    private void saveValues() throws IOException {
        final File tmpFile = new File(file.getPath()+".tmp");
        final FileOutputStream fos = new FileOutputStream(tmpFile);
        try {
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            try {
                final BufferedWriter bw = new BufferedWriter(osw);
                try {
                    saveNode(bw, values);
                } finally {
                    bw.close();
                }
            } finally {
                osw.close();
            }
        } finally {
            fos.close();
        }

        if (!tmpFile.renameTo(file)) {
            throw new IOException("cannot rename "+tmpFile+" to "+file);
        }
    }

    /**
     * Saves one node.
     * @param writer the <code>Writer</code> to write to
     * @param node the node to save
     * @throws IOException if the node cannot be saved
     */
    private static void saveNode(@NotNull final BufferedWriter writer, @NotNull final Map<String, Entry> node) throws IOException {
        if (node.isEmpty()) {
            return;
        }

        for (final Map.Entry<String, Entry> entry : node.entrySet()) {
            final Entry value = entry.getValue();

            writer.newLine();

            final String documentation = value.getDocumentation();
            if (documentation != null) {
                writer.write("# ");
                writer.write(Codec.encode(documentation));
                writer.newLine();
            }

            writer.write(Codec.encode(entry.getKey()));
            writer.write("=");
            writer.write(Codec.encode(value.getValue()));
            writer.newLine();
        }
    }

}

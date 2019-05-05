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

import com.realtime.crossfire.jxclient.util.Codec;
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
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.NotNull;

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
    private final Map<String, Entry> values = new TreeMap<>();

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
     * Returns the string associated with the specified key at a node, or {@code
     * defaultValue} if there is no association for this key.
     * @param key the key to get the value for
     * @return the value
     */
    @NotNull
    public String getString(@NotNull final SettingsEntry<?> key) {
        final Entry entry = values.get(key.getKey());
        return entry == null ? key.getDefaultValue().toString() : entry.getValue();
    }

    /**
     * Returns the boolean associated with the specified key at a node or {@code
     * defaultValue} if there is no association for this key.
     * @param key the key to get the value for
     * @return the value
     */
    public boolean getBoolean(@NotNull final SettingsEntry<Boolean> key) {
        final String value = getString(key);
        try {
            return Boolean.parseBoolean(value);
        } catch (final NumberFormatException ignored) {
            return key.getDefaultValue();
        }
    }

    /**
     * Returns the integer associated with the specified key at a node or {@code
     * defaultValue} if there is no association for this key.
     * @param key the key to get the value for
     * @return the value
     */
    public int getInt(@NotNull final SettingsEntry<Integer> key) {
        return NumberParser.parseInt(getString(key), key.getDefaultValue());
    }

    /**
     * Returns the long associated with the specified key at a node or {@code
     * defaultValue} if there is no association for this key.
     * @param key the key to get the value for
     * @return the value
     */
    public long getLong(@NotNull final SettingsEntry<Long> key) {
        return NumberParser.parseLong(getString(key), key.getDefaultValue());
    }

    /**
     * Stores a key/value pair.
     * @param key the key to store
     * @param value the value to store
     */
    public void putString(@NotNull final SettingsEntry<?> key, @NotNull final String value) {
        final Entry oldEntry = values.get(key.getKey());
        if (oldEntry == null) {
            values.put(key.getKey(), new Entry(value, key.getComment()));
        } else {
            oldEntry.setDocumentation(key.getComment());
            if (!oldEntry.getValue().equals(value)) {
                oldEntry.setValue(value);
                setChanged();
            }
        }
    }

    /**
     * Stores a key/value pair.
     * @param key the key to store
     * @param value the value to store
     */
    public void putBoolean(@NotNull final SettingsEntry<Boolean> key, final boolean value) {
        putString(key, Boolean.toString(value));
    }

    /**
     * Stores a key/value pair.
     * @param key the key to store
     * @param value the value to store
     */
    public void putInt(@NotNull final SettingsEntry<Integer> key, final int value) {
        putString(key, Integer.toString(value));
    }

    /**
     * Stores a key/value pair.
     * @param key the key to store
     * @param value the value to store
     */
    public void putLong(@NotNull final SettingsEntry<Long> key, final long value) {
        putString(key, Long.toString(value));
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
            try (FileInputStream fis = new FileInputStream(file)) {
                try (InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                    try (LineNumberReader lnr = new LineNumberReader(isr)) {
                        loadValues(lnr);
                    }
                }
            }
        } catch (final FileNotFoundException ignored) {
            // ignore
        } catch (final IOException ex) {
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * Loads the values.
     * @param lnr the line number reader from
     * @throws IOException if an I/O error occurs
     */
    private void loadValues(@NotNull final LineNumberReader lnr) throws IOException {
        while (true) {
            final String line2 = lnr.readLine();
            if (line2 == null) {
                break;
            }
            final String line = Codec.decode(line2.trim());
            if (line.startsWith("#") || line.isEmpty()) {
                continue;
            }

            final String[] tmp = line.split("=", 2);
            if (tmp.length != 2) {
                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                continue;
            }
            final String key = tmp[0];
            final String value = tmp[1];

            putString(new SettingsEntry<>(key, "", null), value);
        }
    }

    /**
     * Saves the values to the backing file.
     * @throws IOException if the values cannot be saved
     */
    private void saveValues() throws IOException {
        final File tmpFile = new File(file.getPath()+".tmp");
        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            try (OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                try (BufferedWriter bw = new BufferedWriter(osw)) {
                    saveNode(bw, values);
                }
            }
        }

        if (!tmpFile.renameTo(file)) {
            throw new IOException("cannot rename "+tmpFile+" to "+file);
        }
    }

    /**
     * Saves one node.
     * @param writer the {@code Writer} to write to
     * @param node the node to save
     * @throws IOException if the node cannot be saved
     */
    private static void saveNode(@NotNull final BufferedWriter writer, @NotNull final Map<String, Entry> node) throws IOException {
        if (node.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Entry> entry : node.entrySet()) {
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

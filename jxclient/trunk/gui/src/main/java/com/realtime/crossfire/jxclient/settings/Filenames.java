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

import com.realtime.crossfire.jxclient.util.HexCodec;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to return references to settings files.
 * @author Andreas Kirschbaum
 */
public class Filenames {

    /**
     * Private constructor to prevent instantiation.
     */
    private Filenames() {
    }

    /**
     * Returns the image cache directory.
     * @return the image cache directory
     */
    @NotNull
    public static File getOriginalImageCacheDir() {
        try {
            return getSettingsFile("cache");
        } catch (final IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

    /**
     * Returns the image cache directory for double size images.
     * @return the image cache directory
     */
    @NotNull
    public static File getScaledImageCacheDir() {
        try {
            return getSettingsFile("cache-x2");
        } catch (final IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

    /**
     * Returns the image cache directory for magic map sized images.
     * @return the image cache directory
     */
    @NotNull
    public static File getMagicMapImageCacheDir() {
        try {
            return getSettingsFile("cache-mm");
        } catch (final IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

    /**
     * Returns the main settings file.
     * @return the main settings file
     * @throws IOException if the file cannot be accessed
     */
    @NotNull
    public static File getSettingsFile() throws IOException {
        return getSettingsFile("jxclient.conf");
    }

    /**
     * Returns the shortcuts file.
     * @param hostname the hostname of the character; <code>null</code>=global
     * key bindings file
     * @param character the character name; <code>null</code>=global key
     * bindings file
     * @return the shortcuts file
     * @throws IOException if the file cannot be accessed
     */
    @NotNull
    public static File getShortcutsFile(@NotNull final CharSequence hostname, @NotNull final CharSequence character) throws IOException {
        return getSettingsFile("shortcuts-"+encode(hostname)+"-"+encode(character)+".txt");
    }

    /**
     * Returns the keybindings file.
     * @param hostname the hostname of the character; <code>null</code>=global
     * key bindings file
     * @param character the character name; <code>null</code>=global key
     * bindings file
     * @return the keybindings file; return <code>null</code> if the file cannot
     *         be accessed
     * @throws IOException if the keybindings file cannot be accessed
     */
    @Nullable
    public static File getKeybindingsFile(@Nullable final CharSequence hostname, @Nullable final CharSequence character) throws IOException {
        return getSettingsFile(hostname == null || character == null ? "keybindings.txt" : "keybindings-"+encode(hostname)+"-"+encode(character)+".txt");
    }

    /**
     * Returns the metaserver cache file.
     * @return the metaserver cache file, or <code>null</code> if the file
     *         cannot be accessed
     */
    @Nullable
    public static File getMetaserverCacheFile() {
        try {
            return getSettingsFile("metaserver.txt");
        } catch (final IOException ex) {
            System.err.println("Cannot access metaserver cache file: "+ex.getMessage());
            return null;
        }
    }

    /**
     * Returns the file for storing dialog related information for a skin.
     * @param skinName identifies the skin
     * @return the file
     * @throws IOException if the file cannot be accessed
     */
    @NotNull
    public static File getDialogsFile(@NotNull final String skinName) throws IOException {
        return new File(getSettingsFile("skin_"+skinName), "dialogs.txt");
    }

    /**
     * Returns a file within the settings directory.
     * @param filename the filename
     * @return the settings file
     * @throws IOException if the file cannot be accessed
     */
    @NotNull
    public static File getSettingsFile(@NotNull final String filename) throws IOException {
        final File settingsDir = new File(getCrossfireFile(), "jxclient");
        if (!settingsDir.exists() && !settingsDir.mkdirs()) {
            throw new IOException("cannot create "+settingsDir);
        }

        return new File(settingsDir, filename);
    }

    /**
     * Returns the crossfire settings directory.
     * @return the settings directory
     * @throws IOException if the settings directory cannot be located
     */
    @NotNull
    private static File getCrossfireFile() throws IOException {
        final String home = System.getProperty("user.home");
        if (home == null) {
            throw new IOException("cannot find home directory");
        }

        return new File(home, ".crossfire");
    }

    /**
     * Encodes a string to make it safe as a file name.
     * @param str the string to encode
     * @return the encoded string
     */
    @NotNull
    private static String encode(@NotNull final CharSequence str) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            final char ch = str.charAt(i);
            if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ('0' <= ch && ch <= '9') || ch == '-' || ch == '_' || ch == '.') {
                sb.append(ch);
            } else {
                sb.append('%');
                HexCodec.hexEncode2(sb, ch);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the log file for text message logging.
     * @param hostname the server hostname or <code>null</code>
     * @return the log file
     * @throws IOException if the log file cannot be determined
     */
    @NotNull
    public static File getMessageLogFile(@Nullable final String hostname) throws IOException {
        return getSettingsFile(hostname == null ? "jxclient.txt" : "jxclient-"+hostname+".txt");
    }

}

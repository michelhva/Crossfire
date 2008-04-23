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

import com.realtime.crossfire.jxclient.util.HexCodec;
import java.io.File;
import java.io.IOException;

/**
 * Utility class to return references to settings files.
 *
 * @author Andreas Kirschbaum
 */
public class Filenames
{
    /**
     * Private constructor to prevent instantiation.
     */
    private Filenames()
    {
    }

    /**
     * Return the image cache directory.
     *
     * @return The image cache directory.
     */
    public static File getOriginalImageCacheDir()
    {
        try
        {
            return getSettingsFile("cache");
        }
        catch (final IOException ex)
        {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

    /**
     * Return the image cache directory for double size images.
     *
     * @return The image cache directory.
     */
    public static File getScaledImageCacheDir()
    {
        try
        {
            return getSettingsFile("cache-x2");
        }
        catch (final IOException ex)
        {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

    /**
     * Return the image cache directory for magic map sized images.
     *
     * @return The image cache directory.
     */
    public static File getMagicMapImageCacheDir()
    {
        try
        {
            return getSettingsFile("cache-mm");
        }
        catch (final IOException ex)
        {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

    /**
     * Return the main settings file.
     *
     * @return The main settings file.
     *
     * @throws IOException If the file cannot be accessed.
     */
    public static File getSettingsFile() throws IOException
    {
        return getSettingsFile("jxclient.conf");
    }

    /**
     * Return the shortcuts file.
     *
     * @return The shortcuts file.
     *
     * @throws IOException If the file cannot be accessed.
     */
    public static File getShortcutsFile() throws IOException
    {
        return getSettingsFile("shortcuts.txt");
    }

    /**
     * Return the keybindings file.
     *
     * @param hostname The hostname of the character; <code>null</code>=global
     * key bindings file.
     *
     * @param character The character name; <code>null</code>=global key
     * bindings file.
     *
     * @return The keybindings file; return <code>null</code> if the file
     * cannot be accessed.
     */
    public static File getKeybindingsFile(final String hostname, final String character)
    {
        try
        {
            return getSettingsFile(hostname == null || character == null ? "keybindings.txt" : "keybindings-"+encode(hostname)+"-"+encode(character)+".txt");
        }
        catch (final IOException ex)
        {
            return null;
        }
    }

    /**
     * Return the metaserver cache file.
     *
     * @return The metaserver cache file, or <code>null</code> if the file
     * cannot be accessed.
     */
    public static File getMetaserverCacheFile()
    {
        try
        {
            return getSettingsFile("metaserver.txt");
        }
        catch (final IOException ex)
        {
            return null;
        }
    }

    /**
     * Return the file for storing dialog related information for a skin.
     *
     * @param skinName Identifies the skin.
     *
     * @return The file.
     *
     * @throws IOException If the file cannot be accessed.
     */
    public static File getDialogsFile(final String skinName) throws IOException
    {
        return new File(getSettingsFile("skin_"+skinName), "dialogs.txt");
    }

    /**
     * Return a file within the settings directory.
     *
     * @param filename The filename.
     *
     * @return The settings file.
     *
     * @throws IOException If the file cannot be accessed.
     */
    public static File getSettingsFile(final String filename) throws IOException
    {
        final File settingsDir = new File(getCrossfireFile(), "jxclient");
        if (!settingsDir.exists() && !settingsDir.mkdirs())
        {
            throw new IOException("cannot create "+settingsDir);
        }

        return new File(settingsDir, filename);
    }

    /**
     * Return the crossfire settings directory.
     *
     * @return The settings directory.
     *
     * @throws IOException If the settings directory cannot be located.
     */
    private static File getCrossfireFile() throws IOException
    {
        final String home = System.getProperty("user.home");
        if (home == null)
        {
            throw new IOException("cannot find home directory");
        }

        return new File(home, ".crossfire");
    }

    /**
     * Encode a string to make it safe as a file name.
     *
     * @param str The string to encode.
     *
     * @return The encoded string.
     */
    private static String encode(final String str)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++)
        {
            final char ch = str.charAt(i);
            if (('a' <= ch && ch <= 'z')
            || ('A' <= ch && ch <= 'Z')
            || ('0' <= ch && ch <= '9')
            || ch == '-'
            || ch == '_'
            || ch == '.')
            {
                sb.append(ch);
            }
            else
            {
                sb.append('%');
                HexCodec.hexEncode2(sb, ch);
            }
        }
        return sb.toString();
    }
}

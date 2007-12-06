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
    public static File getImageCacheDir()
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
    public static File getSizedImageCacheDir()
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
     * Return the spellbelt data file.
     *
     * @return The spellbelt data file.
     *
     * @throws IOException If the file cannot be accessed.
     */
    public static File getSpellbeltDataFile() throws IOException
    {
        return getSettingsFile("spellbelt.data");
    }

    /**
     * Return the keybindings file.
     *
     * @return The keybindings file.
     *
     * @throws IOException If the file cannot be accessed.
     */
    public static File getKeybindingsFile() throws IOException
    {
        return getSettingsFile("keybindings.txt");
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
    private static File getSettingsFile(final String filename) throws IOException
    {
        final File settingsDir = new File(getCrossfireFile(), "jxclient");
        if (!settingsDir.exists() && !settingsDir.mkdir())
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
}

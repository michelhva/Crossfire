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
package com.realtime.crossfire.jxclient.sound;

import java.io.InputStream;
import java.io.IOException;

/**
 * Utility class to locate audio files.
 *
 * @author Andreas Kirschbaum
 */
public class AudioFileLoader
{
    /**
     * Private constructor to prevent instantiation.
     */
    private AudioFileLoader()
    {
    }

    /**
     * Return an input stream for an audio name.
     *
     * @param name The name.
     *
     * @return The input stream.
     *
     * @throws IOException If the file cannot be located.
     */
    public static InputStream getInputStream(final String name) throws IOException
    {
        final String resource = "resource/sounds/"+name+".wav";

        final InputStream inputStream = AudioFileLoader.class.getClassLoader().getResourceAsStream(resource);
        if (inputStream != null)
        {
            return inputStream;
        }

        throw new IOException(name+": resource "+resource+" does not exist");
    }
}

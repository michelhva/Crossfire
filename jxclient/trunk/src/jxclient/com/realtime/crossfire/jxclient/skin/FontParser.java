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
package com.realtime.crossfire.jxclient.skin;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

/**
 * Creates {@link Font} instances from strig representations.
 * @author Andreas Kirschbaum
 */
public class FontParser
{
    /**
     * The {@link JXCSkinSource} for loading resources.
     */
    @NotNull
    private final JXCSkinSource skinSource;

    /**
     * Creates a new instance.
     * @param skinSource the skin source for loading resources
     */
    public FontParser(@NotNull final JXCSkinSource skinSource)
    {
        this.skinSource = skinSource;
    }

    /**
     * Returns a font by font file base name.
     * @param name the file base name of the font file to load
     * @return the font
     * @throws IOException if the font cannot be loaded
     */
    @NotNull
    public Font getFont(@NotNull final String name) throws IOException
    {
        final String filename = "fonts/"+name+".ttf";

        final Font font;
        try
        {
            final InputStream ttf = skinSource.getInputStream(filename);
            try
            {
                try
                {
                    font = Font.createFont(Font.TRUETYPE_FONT, ttf);
                }
                catch (final FontFormatException ex)
                {
                    throw new IOException(filename+": invalid font format: "+ex.getMessage());
                }
            }
            finally
            {
                ttf.close();
            }
        }
        catch (final IOException ex)
        {
            throw new IOException(skinSource.getURI(filename)+": i/o error: "+ex.getMessage());
        }
        return font;
    }
}

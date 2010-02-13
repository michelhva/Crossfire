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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.skin.io;

import com.realtime.crossfire.jxclient.gui.gauge.Orientation;
import com.realtime.crossfire.jxclient.gui.gauge.OrientationParser;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.stats.StatsParser;
import com.realtime.crossfire.jxclient.util.NumberParser;
import java.awt.Color;
import java.io.IOException;
import java.io.LineNumberReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for parsing string parameters into values.
 * @author Andreas Kirschbaum
 */
public class ParseUtils
{
    /**
     * Private constructor to prevent instantiation.
     */
    private ParseUtils()
    {
    }

    /**
     * Parses a stat value.
     * @param name the stat value to parse
     * @return the stat value
     * @throws IOException if the stat value does not exist
     */
    public static int parseStat(@NotNull final String name) throws IOException
    {
        try
        {
            return StatsParser.parseStat(name);
        }
        catch (final IllegalArgumentException ex)
        {
            // ignore
        }

        throw new IOException("invalid stat name: "+name);
    }

    /**
     * Parses an orientation value.
     * @param name the orientation value to parse
     * @return the orientation
     * @throws IOException if the orientation value does not exist
     */
    @NotNull
    public static Orientation parseOrientation(@NotNull final String name) throws IOException
    {
        try
        {
            return OrientationParser.parseOrientation(name);
        }
        catch (final IllegalArgumentException ex)
        {
            // ignore
        }

        throw new IOException("invalid orientation: "+name);
    }

    /**
     * Parses a color name.
     * @param name the color name to parse
     * @return the color
     * @throws IOException if the color name does not exist
     */
    @NotNull
    public static Color parseColor(@NotNull final String name) throws IOException
    {
        final Color color = parseColorNull(name);
        if (color != null)
        {
            return color;
        }
        throw new IOException("unknown color name "+name);
    }

    /**
     * Parses a color name, optionally followed by "/&lt;alpha&gt;".
     * @param name the color name to parse
     * @return the color or <code>null</code> if the color name does not exist
     */
    @Nullable
    public static Color parseColorNull(@NotNull final String name)
    {
        final int pos = name.lastIndexOf('/');
        if (pos == -1)
        {
            return parseColorName(name);
        }

        int alpha = 255;
        try
        {
            alpha = (int)(255*NumberParser.parseFloat(name.substring(pos+1))+0.5);
        }
        catch (final IOException ex)
        {
            /* ignore */
        }
        if (alpha < 0 || alpha > 255)
        {
            return parseColorName(name);
        }

        final String colorName = name.substring(0, pos);
        final Color color = parseColorName(colorName);
        if (alpha == 255)
        {
            return color;
        }

        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * Parses a color name.
     * @param name the color name to parse
     * @return the color or <code>null</code> if the color name does not exist
     */
    @Nullable
    private static Color parseColorName(@NotNull final String name)
    {
        if (name.equals("BLACK"))
        {
            return Color.BLACK;
        }
        if (name.equals("DARK_GRAY"))
        {
            return Color.DARK_GRAY;
        }
        if (name.equals("GRAY"))
        {
            return Color.GRAY;
        }
        if (name.equals("WHITE"))
        {
            return Color.WHITE;
        }
        if (name.length() == 7 && name.charAt(0) == '#' && name.charAt(1) != '-')
        {
            try
            {
                return new Color(Integer.parseInt(name.substring(1), 16));
            }
            catch (final NumberFormatException ex)
            {
                // ignore
            }
        }
        return null;
    }

    /**
     * Concatenates trailing arguments into a string. If the first line is
     * "<<EOF", all text up to the next line containing only "EOF" is appended.
     * Comments starting with "#" are dropped.
     * @param args the args to concatenate
     * @param startIndex the first index to concatenate
     * @param lnr where to read additional lines from
     * @return the concatenated string
     * @throws IOException if reading from <code>lnr</lnr> fails
     */
    @NotNull
    public static String parseText(@NotNull final String[] args, final int startIndex, @NotNull final LineNumberReader lnr) throws IOException
    {
        final StringBuilder text = new StringBuilder();
        for (int i = startIndex; i < args.length; i++)
        {
            if (i > startIndex)
            {
                text.append(' ');
            }
            text.append(args[i]);
        }
        if (text.toString().equals("<<EOF"))
        {
            text.setLength(0);
            for (;;)
            {
                final String line = lnr.readLine();
                if (line == null)
                {
                    throw new IOException();
                }
                if (line.equals("EOF"))
                {
                    break;
                }
                if (line.startsWith("#"))
                {
                    continue;
                }

                text.append(line);
                text.append('\n');
            }
            if (text.length() > 0)
            {
                text.setLength(text.length()-1);
            }
        }

        return text.toString();
    }

    /**
     * Parses a check box option name.
     * @param name the check box option name to parse
     * @param optionManager the option manager to use
     * @return the check box option
     * @throws IOException if the check box option name does not exist
     */
    @NotNull
    public static CheckBoxOption parseCheckBoxOption(@NotNull final String name, @NotNull final OptionManager optionManager) throws IOException
    {
        try
        {
            return optionManager.getCheckBoxOption(name);
        }
        catch (final OptionException ex)
        {
            throw new IOException(ex.getMessage());
        }
    }
}

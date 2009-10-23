package com.realtime.crossfire.jxclient.util;

import java.util.regex.Pattern;

/**
 * Utility class for manipulating filenames.
 * @author Andreas Kirschbaum
 */
public class Filenames
{
    /**
     * Replaces "unsafe" characters in file names (see {@link
     * #UNSAFE_FILENAME_CHARACTERS}).
     */
    private static final String REPLACEMENT_CHARACTER = "_";
    /**
     * Matches all chracters that are considered "unsafe" for file names. These
     * characters will be replaced with {@link #REPLACEMENT_CHARACTER}.
     */
    private static final Pattern UNSAFE_FILENAME_CHARACTERS = Pattern.compile("[^a-zA-Z0-9_.]");

    /**
     * Private constructor to prevent instantiation.
     */
    private Filenames()
    {
    }

    /**
     * Converts a file name to a "safe" form. The returned file name will not
     * contain any "unsafe" characters (see {@link #UNSAFE_FILENAME_CHARACTERS}),
     * and it will not be empty.
     * @param name the file name to convert
     * @return the converted file name
     */
    public static String quoteName(final String name)
    {
        final CharSequence trimmedName = name.endsWith(".png") ? name.substring(0, name.length()-4) : name;
        final String replacedName = UNSAFE_FILENAME_CHARACTERS.matcher(trimmedName).replaceAll(REPLACEMENT_CHARACTER);
        return replacedName.length() > 0 ? replacedName : REPLACEMENT_CHARACTER;
    }
}

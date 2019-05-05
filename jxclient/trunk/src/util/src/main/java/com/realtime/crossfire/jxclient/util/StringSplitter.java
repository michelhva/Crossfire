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

package com.realtime.crossfire.jxclient.util;

import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for splitting strings.
 * @author Andreas Kirschbaum
 */
public class StringSplitter {

    /**
     * The preferred line length in characters.
     */
    private static final int PREFERRED_LINE_LENGTH = 50;

    /**
     * The maximum line length in characters.
     */
    private static final int MAXIMUM_LINE_LENGTH = 80;

    /**
     * Private constructor to prevent instantiation.
     */
    private StringSplitter() {
    }

    /**
     * Splits the given string into lines and returns the lines separated by
     * "&lt;br&gt;".
     * @param message the message to format
     * @return the formatted message
     */
    @NotNull
    public static String splitAsHtml(@NotNull final String message) {
        final StringBuilder sb = new StringBuilder();
        for (String line : split(message)) {
            if (sb.length() > 0) {
                sb.append("<br>");
            }
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Splits the given string into lines.
     * @param message the message to split
     * @return the lines
     */
    @NotNull
    private static Iterable<String> split(@NotNull final String message) {
        final String paddedMessage = message.trim()+" ";

        final Collection<String> result = new ArrayList<>();
        int start = 0;
        while (true) {
            while (start < paddedMessage.length() && paddedMessage.charAt(start) == ' ') {
                start++;
            }
            if (start >= paddedMessage.length()) {
                break;
            }

            final int nextSpace = paddedMessage.indexOf(' ', Math.min(start+PREFERRED_LINE_LENGTH, paddedMessage.length()-1));
            assert nextSpace != -1;
            if (nextSpace-start <= PREFERRED_LINE_LENGTH) {
                result.add(paddedMessage.substring(start, nextSpace));
                start = nextSpace+1;
            } else {
                final int prevSpace = paddedMessage.lastIndexOf(' ', nextSpace-1);
                if (prevSpace != -1 && prevSpace > start) {
                    result.add(paddedMessage.substring(start, prevSpace));
                    start = prevSpace+1;
                } else if (nextSpace-start <= MAXIMUM_LINE_LENGTH) {
                    result.add(paddedMessage.substring(start, nextSpace));
                    start = nextSpace+1;
                } else {
                    final int end = Math.min(start+MAXIMUM_LINE_LENGTH, paddedMessage.length());
                    result.add(paddedMessage.substring(start, end));
                    start = end;
                }
            }
        }
        return result;
    }

}

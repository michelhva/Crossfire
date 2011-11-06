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

package com.realtime.crossfire.jxclient.gui.log;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for parsing drawextinfo messages received from a Crossfire server to
 * update a {@link Buffer} instance.
 * @author Andreas Kirschbaum
 */
public class Parser {

    /**
     * Maps font tag name to font instance.
     */
    @NotNull
    private static final Map<String, FontID> FONTS = new HashMap<String, FontID>();

    static {
        FONTS.put("print", FontID.PRINT);
        FONTS.put("fixed", FontID.FIXED);
        FONTS.put("arcane", FontID.ARCANE);
        FONTS.put("hand", FontID.HAND);
        FONTS.put("strange", FontID.STRANGE);
    }

    /**
     * Maps color tag name to color instance. The keys must be lower case.
     */
    @NotNull
    private static final Map<String, Color> COLORS = new HashMap<String, Color>();

    static {
        COLORS.put("black", Color.BLACK);
        COLORS.put("blue", Color.BLUE);
        COLORS.put("green", Color.GREEN);
        COLORS.put("red", Color.RED);
        COLORS.put("white", Color.WHITE);
    }

    /**
     * The pattern to split a string into words.
     */
    @NotNull
    private static final Pattern WORD_SEPARATOR_PATTERN = Pattern.compile(" ");

    /**
     * Pattern to match line breaks.
     */
    @NotNull
    private static final Pattern END_OF_LINE_PATTERN = Pattern.compile(" *\n");

    /**
     * Whether bold face is enabled.
     */
    private boolean bold = false;

    /**
     * Whether italic face is enabled.
     */
    private boolean italic = false;

    /**
     * Whether underlining is enabled.
     */
    private boolean underline = false;

    /**
     * The font to use.
     */
    private FontID font = FontID.PRINT;

    /**
     * The color to use. <code>null</code> means default color.
     */
    @Nullable
    private Color color = null;

    /**
     * Parses a text message.
     * @param text the text message to parse
     * @param defaultColor the default color to use
     * @param buffer the buffer to update
     */
    public void parse(@NotNull final CharSequence text, @Nullable final Color defaultColor, @NotNull final Buffer buffer) {
        resetAttributes(defaultColor);
        for (final String line : END_OF_LINE_PATTERN.split(text, -1)) {
            parseLine(line, defaultColor, buffer);
        }
        buffer.prune();
    }

    /**
     * Parses a plain text message without media tags.
     * @param text the text message to parse
     * @param color the color to use
     * @param buffer the buffer to update
     */
    public void parseWithoutMediaTags(@NotNull final CharSequence text, @NotNull final Color color, @NotNull final Buffer buffer) {
        if (text.length() == 0) {
            return;
        }

        resetAttributes(color);
        for (final String line : END_OF_LINE_PATTERN.split(text, -1)) {
            parseLineWithoutMediaTags(line, buffer);
        }
        buffer.prune();
    }

    /**
     * Parses one text line.
     * @param text the text to process
     * @param defaultColor the default color to use
     * @param buffer the buffer instance to add to
     */
    private void parseLine(@NotNull final String text, @Nullable final Color defaultColor, @NotNull final Buffer buffer) {
        if (buffer.mergeLines(text, defaultColor)) {
            buffer.replaceLine(parseLine(text+" [["+buffer.getLastCount()+" times]", defaultColor));
        } else {
            buffer.addLine(parseLine(text, defaultColor));
        }
    }

    /**
     * Parses one text line.
     * @param text the text to process
     * @param defaultColor the default color to use
     * @return the <code>Line</code> instance
     */
    @NotNull
    private Line parseLine(@NotNull final String text, @Nullable final Color defaultColor) {
        final Line line = new Line();

        int begin = 0;
        boolean active = false;
        final int iMax = text.length();
        for (int i = 0; i < iMax; i++) {
            final char ch = text.charAt(i);
            if (active) {
                if (ch == ']') {
                    processTag(text.substring(begin, i), defaultColor);
                    begin = i+1;
                    active = false;
                } else if (ch == '[' && i == begin) {
                    processText("[", line);
                    begin = i+1;
                    active = false;
                }
            } else {
                if (ch == '[') {
                    processText(text.substring(begin, i), line);
                    begin = i+1;
                    active = true;
                }
            }
        }
        if (!active) {
            processText(text.substring(begin, iMax), line);
        }

        return line;
    }

    /**
     * Parses one text line of a plain text message without media tags.
     * @param text the text to process
     * @param buffer the buffer instance to add to
     */
    private void parseLineWithoutMediaTags(@NotNull final String text, @NotNull final Buffer buffer) {
        final Line line = new Line();
        if (buffer.mergeLines(text, null)) {
            processText(text+" ["+buffer.getLastCount()+" times]", line);
            buffer.replaceLine(line);
        } else {
            processText(text, line);
            buffer.addLine(line);
        }
    }

    /**
     * Resets all attributes to default values.
     * @param defaultColor the default color to use
     */
    private void resetAttributes(@Nullable final Color defaultColor) {
        bold = false;
        italic = false;
        underline = false;
        font = FontID.PRINT;
        color = defaultColor;
    }

    /**
     * Processes a tag.
     * @param tag the tag name to process; leading and trailing brackets have
     * been removed
     * @param defaultColor the default color to use
     */
    private void processTag(@NotNull final String tag, @Nullable final Color defaultColor) {
        if (tag.length() == 0) {
            return;
        }

        if (tag.equals("b")) {
            bold = true;
        } else if (tag.equals("/b")) {
            bold = false;
        } else if (tag.equals("i")) {
            italic = true;
        } else if (tag.equals("/i")) {
            italic = false;
        } else if (tag.equals("ul")) {
            underline = true;
        } else if (tag.equals("/ul")) {
            underline = false;
        } else if (FONTS.containsKey(tag)) {
            font = FONTS.get(tag);
            assert font != null;
        } else if (tag.startsWith("color=")) {
            final String colorName = tag.substring(6).toLowerCase();
            if (COLORS.containsKey(colorName)) {
                color = COLORS.get(colorName);
                assert color != null;
                //} else {
                // ignore unknown color
            }
        } else if (tag.equals("/color")) {
            color = defaultColor;
            //} else {
            // ignore unknown tag
        }
    }

    /**
     * Processes one text segment.
     * @param text the text segment to process
     * @param line the line to add to
     */
    private void processText(@NotNull final String text, @NotNull final Line line) {
        if (text.length() == 0) {
            return;
        }

        final CharSequence newText;
        final Segment prevSegment = line.getLastSegment();
        if (prevSegment == null || !(prevSegment instanceof TextSegment)) {
            newText = text;
        } else {
            final TextSegment prevTextSegment = (TextSegment)prevSegment;
            if (prevTextSegment.matches(bold, italic, underline, font, color)) {
                newText = prevTextSegment.getText()+text;
                line.removeLastSegment();
            } else {
                newText = text;
            }
        }

        final String[] words = WORD_SEPARATOR_PATTERN.split(newText, -1);
        for (int i = 0; i < words.length-1; i++) {
            line.addSegment(words[i]+" ", bold, italic, underline, font, color);
        }
        if (words[words.length-1].length() > 0) {
            line.addSegment(words[words.length-1], bold, italic, underline, font, color);
        }
    }

    /**
     * Returns the string representation for a color.
     * @param color the color to convert
     * @return the string representation
     */
    @NotNull
    public static String toString(@NotNull final Color color) {
        // function need not be efficient since it is used for regression tests
        // only
        for (final Map.Entry<String, Color> e : COLORS.entrySet()) {
            if (e.getValue() == color) {
                return e.getKey();
            }
        }

        return "unknown";
    }

}

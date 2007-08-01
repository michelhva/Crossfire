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
package com.realtime.crossfire.jxclient.gui.log;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser to parse drawextinfo messages received from a Crossfire server and
 * update a {@link Buffer} instance.
 *
 * @author Andreas Kirschbaum
 */
public class Parser
{
    /**
     * Maps font tag name to font instance.
     */
    private static final Map<String, Segment.Font> fonts = new HashMap<String, Segment.Font>();
    static
    {
        fonts.put("print", Segment.Font.PRINT);
        fonts.put("fixed", Segment.Font.FIXED);
        fonts.put("arcane", Segment.Font.ARCANE);
        fonts.put("hand", Segment.Font.HAND);
        fonts.put("strange", Segment.Font.STRANGE);
    }

    /**
     * Maps color tag name to color instance. The keys must be lower case.
     */
    private static final Map<String, Color> colors = new HashMap<String, Color>();
    static
    {
        colors.put("black", Color.BLACK);
        colors.put("blue", Color.BLUE);
        colors.put("green", Color.GREEN);
        colors.put("red", Color.RED);
        colors.put("white", Color.WHITE);
    }

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
    private Segment.Font font = Segment.Font.PRINT;

    /**
     * The color to use. <code>null</code> means default color.
     */
    private Color color = null;

    /**
     * Parse a text message.
     *
     * @param text The text message to parse.
     *
     * @param defaultColor The default color to use.
     *
     * @param buffer The buffer to update.
     */
    public void parse(final String text, final Color defaultColor, final Buffer buffer)
    {
        if (text == null) throw new IllegalArgumentException();
        if (buffer == null) throw new IllegalArgumentException();

        if (text.isEmpty())
        {
            return;
        }

        resetAttributes(defaultColor);
        for (final String line : text.split("\n", -1))
        {
            parseLine(line, defaultColor, buffer);
        }
    }

    /**
     * Parse one text line.
     *
     * @param text The text to process.
     *
     * @param defaultColor The default color to use.
     *
     * @param buffer The buffer instance to add to.
     */
    private void parseLine(final String text, final Color defaultColor, final Buffer buffer)
    {
        final Line line = new Line();

        int begin = 0;
        boolean active = false;
        final int imax = text.length();
        for (int i = 0; i < imax; i++)
        {
            final char ch = text.charAt(i);
            if (active && ch == ']')
            {
                processTag(text.substring(begin, i), defaultColor);
                begin = i+1;
                active = false;
            }
            else if (ch == '[')
            {
                processText(text.substring(begin, i), line);
                begin = i+1;
                active = true;
            }
        }
        if (!active)
        {
            processText(text.substring(begin, imax), line);
        }

        buffer.addLine(line);
    }

    /**
     * Reset all attributes to default values.
     *
     * @param defaultColor The default color to use.
     */
    private void resetAttributes(final Color defaultColor)
    {
        bold = false;
        italic = false;
        underline = false;
        font = Segment.Font.PRINT;
        color = defaultColor;
    }

    /**
     * Process a tag.
     *
     * @param tag The tag name to process. Leading and trailing brackets have
     * been removed.
     *
     * @param defaultColor The default color to use.
     */
    private void processTag(final String tag, final Color defaultColor)
    {
        if (tag.isEmpty())
        {
            return;
        }

        if (tag.equals("b"))
        {
            bold = true;
        }
        else if (tag.equals("/b"))
        {
            bold = false;
        }
        else if (tag.equals("i"))
        {
            italic = true;
        }
        else if (tag.equals("/i"))
        {
            italic = false;
        }
        else if (tag.equals("ul"))
        {
            underline = true;
        }
        else if (tag.equals("/ul"))
        {
            underline = false;
        }
        else if (fonts.containsKey(tag))
        {
            font = fonts.get(tag);
            assert font != null;
        }
        else if (tag.startsWith("color="))
        {
            final String colorName = tag.substring(6).toLowerCase();
            if (colors.containsKey(colorName))
            {
                color = colors.get(colorName);
                assert color != null;
            }
            else
            {
                // ignore unknown color
            }
        }
        else if (tag.equals("/color"))
        {
            color = defaultColor;
        }
        else
        {
            // ignore unknown tag
        }
    }

    /**
     * Process one text segment.
     *
     * @param text The text segment to process.
     *
     * @param line The line to add to.
     */
    private void processText(final String text, final Line line)
    {
        if (text.isEmpty())
        {
            return;
        }

        if (!line.isEmpty())
        {
            final Segment prevSegment = line.getLastSegment();
            if (prevSegment.isBold() == bold
            && prevSegment.isItalic() == italic
            && prevSegment.isUnderline() == underline
            && prevSegment.getFont() == font
            && prevSegment.getColor() == color)
            {
                line.replaceLastSegment(new Segment(prevSegment.getText()+text, bold, italic, underline, font, color));
                return;
            }
        }

        line.addSegment(new Segment(text, bold, italic, underline, font, color));
    }

    /**
     * Return the string representation for a color.
     *
     * @param color The color to convert.
     *
     * @return The string representation.
     */
    public String toString(final Color color)
    {
        if (color == null)
        {
            throw new IllegalArgumentException();
        }

        // function need not be efficient since it is used for regression tests
        // only
        for (final Map.Entry<String, Color> e : colors.entrySet())
        {
            if (e.getValue() == color)
            {
                return e.getKey();
            }
        }

        return "unknown";
    }
}

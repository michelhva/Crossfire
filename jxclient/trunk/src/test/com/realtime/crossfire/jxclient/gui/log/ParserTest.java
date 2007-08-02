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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Regression tests for class {@link Parser}.
 *
 * @author Andreas Kirschbaum
 */
public class ParserTest extends TestCase
{
    /**
     * The default parser.
     */
    private Parser parser = null;

    /**
     * The default buffer.
     */
    private Buffer buffer = null;

    /**
     * Create a new instance.
     *
     * @param name the test case name
     */
    public ParserTest(final String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ParserTest.class);
    }

    /**
     * Run the regression tests.
     *
     * @param args The command line arguments (ignored).
     */
    public static void main(final String[] args)
    {
        TestRunner.run(suite());
    }

    /**
     * Check that an empty string does not add anything.
     */
    public void testEmpty()
    {
        parser.parse("", null, buffer);
        checkResult(""
            +"buffer:\n"
        );
    }

    /**
     * Check that unknown attributes are ignored.
     */
    public void testAttributesIgnore()
    {
        parser.parse("a[a]b[]c[[]d[]]e", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:abcd]e\n"
        );
    }

    /**
     * Check that attributes are correctly parsed.
     */
    public void testAttributes1()
    {
        parser.parse("a b c", null, buffer);
        parser.parse("[b]a b c", null, buffer);
        parser.parse("[i]a b c", null, buffer);
        parser.parse("[ul]a b c", null, buffer);
        parser.parse("[fixed]a b c", null, buffer);
        parser.parse("[arcane]a b c", null, buffer);
        parser.parse("[hand]a b c", null, buffer);
        parser.parse("[strange]a b c", null, buffer);
        parser.parse("[print]a b c", null, buffer);
        parser.parse("[color=red]a b c", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:a \n"
            +"segment:b \n"
            +"segment:c\n"
            +"line:\n"
            +"segment:(bold)a \n"
            +"segment:(bold)b \n"
            +"segment:(bold)c\n"
            +"line:\n"
            +"segment:(italic)a \n"
            +"segment:(italic)b \n"
            +"segment:(italic)c\n"
            +"line:\n"
            +"segment:(underline)a \n"
            +"segment:(underline)b \n"
            +"segment:(underline)c\n"
            +"line:\n"
            +"segment:(fixed)a \n"
            +"segment:(fixed)b \n"
            +"segment:(fixed)c\n"
            +"line:\n"
            +"segment:(arcane)a \n"
            +"segment:(arcane)b \n"
            +"segment:(arcane)c\n"
            +"line:\n"
            +"segment:(hand)a \n"
            +"segment:(hand)b \n"
            +"segment:(hand)c\n"
            +"line:\n"
            +"segment:(strange)a \n"
            +"segment:(strange)b \n"
            +"segment:(strange)c\n"
            +"line:\n"
            +"segment:a \n"
            +"segment:b \n"
            +"segment:c\n"
            +"line:\n"
            +"segment:(red)a \n"
            +"segment:(red)b \n"
            +"segment:(red)c\n"
        );
    }

    /**
     * Check that attributes are correctly parsed.
     */
    public void testAttributes2()
    {
        parser.parse("a[b]b[i]c[ul]d[/b]e[/i]f[/ul]g", null, buffer);
        parser.parse("Hello [b] all [b]crossfire[/b] members [/b]", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:a\n"
            +"segment:(bold)b\n"
            +"segment:(bold)(italic)c\n"
            +"segment:(bold)(italic)(underline)d\n"
            +"segment:(italic)(underline)e\n"
            +"segment:(underline)f\n"
            +"segment:g\n"
            +"line:\n"
            +"segment:Hello \n"
            +"segment:(bold) \n"
            +"segment:(bold)all \n"
            +"segment:(bold)crossfire\n"
            +"segment: \n"
            +"segment:members \n"
        );
    }

    /**
     * Check that font attributes are correctly parsed.
     */
    public void testAttributesFont1()
    {
        parser.parse("[b]a[fixed]b[arcane]c[hand]d[strange]e[print]f", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:(bold)a\n"
            +"segment:(bold)(fixed)b\n"
            +"segment:(bold)(arcane)c\n"
            +"segment:(bold)(hand)d\n"
            +"segment:(bold)(strange)e\n"
            +"segment:(bold)f\n"
        );
    }

    /**
     * Check that font attributes are correctly parsed: [/fixed] is
     * undefined/does not end [fixed] block.
     */
    public void testAttributesFont2()
    {
        parser.parse("a[fixed]b[/fixed]c", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:a\n"
            +"segment:(fixed)bc\n"
        );
    }

    /**
     * Check that color attributes are correctly parsed.
     */
    public void testAttributesColor()
    {
        parser.parse("[b]a[color=red]b[color=blue]c[color=green]d[/color]e", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:(bold)a\n"
            +"segment:(bold)(red)b\n"
            +"segment:(bold)(blue)c\n"
            +"segment:(bold)(green)d\n"
            +"segment:(bold)e\n"
        );
    }

    /**
     * Check that attributes are reset for each message.
     */
    public void testAttributesReset()
    {
        parser.parse("[b][i][ul][hand][color=red]first", null, buffer);
        parser.parse("second", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:(bold)(italic)(underline)(hand)(red)first\n"
            +"line:\n"
            +"segment:second\n"
        );
    }

    /**
     * Check that multi-line messages are correctly parsed.
     */
    public void testMultiLine()
    {
        parser.parse("first\n[b]second\nth[/b]ird[i]\nfourth", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:first\n"
            +"line:\n"
            +"segment:(bold)second\n"
            +"line:\n"
            +"segment:(bold)th\n"
            +"segment:ird\n"
            +"line:\n"
            +"segment:(italic)fourth\n"
        );
    }

    /**
     * Check that an un-closed tag is dopped.
     */
    public void testDropUnClosedTag()
    {
        parser.parse("abc[fixed", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:abc\n"
        );
    }

    /** {@inheritDoc} */
    public void setUp()
    {
        parser = new Parser();
        buffer = new Buffer();
    }

    /**
     * Check for expected contents of {@link #buffer}.
     *
     * @param expected The expected buffer contents.
     */
    private void checkResult(final String expected)
    {
        assertEquals(expected, dumpBuffer());
    }

    /**
     * Return a string representation of {@link #buffer}.
     *
     * @return The string representation.
     */
    private String dumpBuffer()
    {
        final StringBuilder sb = new StringBuilder();
        dumpBuffer(sb, buffer);
        return sb.toString();
    }

    /**
     * Append the contents of a {@link Buffer} to a {@link StringBuilder}.
     *
     * @param sb The <code>StringBuilder</code> to append to.
     *
     * @param buffer The <code>Buffer</code> to append.
     */
    private void dumpBuffer(final StringBuilder sb, final Buffer buffer)
    {
        sb.append("buffer:\n");
        for (final Line line : buffer)
        {
            dumpLine(sb, line);
        }
    }

    /**
     * Append the contents of a {@link Line} to a {@link StringBuilder}.
     *
     * @param sb The <code>StringBuilder</code> to append to.
     *
     * @param line The <code>Line</code> to append.
     */
    private void dumpLine(final StringBuilder sb, final Line line)
    {
        sb.append("line:\n");
        for (final Segment segment : line)
        {
            dumpSegment(sb, segment);
        }
    }

    /**
     * Append the contents of a {@link Segment} to a {@link StringBuilder}.
     *
     * @param sb The <code>StringBuilder</code> to append to.
     *
     * @param segment The <code>Segment</code> to append.
     */
    private void dumpSegment(final StringBuilder sb, final Segment segment)
    {
        sb.append("segment:");
        if (segment.isBold())
        {
            sb.append("(bold)");
        }
        if (segment.isItalic())
        {
            sb.append("(italic)");
        }
        if (segment.isUnderline())
        {
            sb.append("(underline)");
        }
        dumpFont(segment.getFont(), sb);
        final Color color = segment.getColor();
        if (color != null)
        {
            sb.append('(').append(parser.toString(color)).append(')');
        }
        sb.append(segment.getText());
        sb.append('\n');
    }

    /**
     * Return the string representation for a font.
     *
     * @param font The font to convert.
     *
     * @return The string representation.
     */
    public void dumpFont(final Segment.Font font, final StringBuilder sb)
    {
        if (font == Segment.Font.PRINT)
        {
            // ignore
            return;
        }

        sb.append('(').append(font.toString().toLowerCase()).append(')');
    }
}

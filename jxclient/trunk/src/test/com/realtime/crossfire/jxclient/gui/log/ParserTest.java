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

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Regression tests for class {@link Parser}.
 * @author Andreas Kirschbaum
 */
public class ParserTest extends TestCase
{
    /**
     * The default parser.
     */
    @Nullable
    private Parser parser = null;

    /**
     * The default buffer.
     */
    @Nullable
    private Buffer buffer = null;

    /**
     * Creates a new instance.
     * @param name the test case name
     */
    public ParserTest(@NotNull final String name)
    {
        super(name);
    }

    /**
     * Creates a new test suite.
     * @return the test suite
     */
    @NotNull
    public static Test suite()
    {
        return new TestSuite(ParserTest.class);
    }

    /**
     * Runs the regression tests.
     * @param args the command line arguments (ignored)
     */
    public static void main(@NotNull final String[] args)
    {
        TestRunner.run(suite());
    }

    /**
     * Checks that an empty string does not add anything.
     */
    public void testEmpty()
    {
        parser.parse("", null, buffer);
        checkResult(""
            +"buffer:\n"
        );
    }

    /**
     * Checks that unknown attributes are ignored.
     */
    public void testAttributesIgnore()
    {
        parser.parse("a[a]b[]c[[]d[]]e", null, buffer);
        checkResult(""
            +"buffer:\n"
            +"line:\n"
            +"segment:abc[]d]e\n"
        );
    }

    /**
     * Checks that attributes are correctly parsed.
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
     * Checks that attributes are correctly parsed.
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
     * Checks that font attributes are correctly parsed.
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
     * Checks that font attributes are correctly parsed: [/fixed] is
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
     * Checks that color attributes are correctly parsed.
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
     * Checks that attributes are reset for each message.
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
     * Checks that multi-line messages are correctly parsed.
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
     * Checks that an un-closed tag is dopped.
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
    @Override
    public void setUp() throws FontFormatException, IOException
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        final BufferedImage image = gconf.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
        final Graphics2D g = image.createGraphics();
        parser = new Parser();
        final Font font;
        final FileInputStream fis = new FileInputStream("skins/ragnorok/fonts/regular.ttf");
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, fis);
        }
        finally
        {
            fis.close();
        }
        buffer = new Buffer(new Fonts(font, font, font, font), g.getFontRenderContext(), 100);
        g.dispose();
    }

    /**
     * Checks for expected contents of {@link #buffer}.
     *
     * @param expected The expected buffer contents.
     */
    private void checkResult(@NotNull final String expected)
    {
        assertEquals(expected, dumpBuffer());
    }

    /**
     * Returns a string representation of {@link #buffer}.
     * @return the string representation
     */
    @NotNull
    private String dumpBuffer()
    {
        final StringBuilder sb = new StringBuilder();
        dumpBuffer(sb, buffer);
        return sb.toString();
    }

    /**
     * Appends the contents of a {@link Buffer} to a {@link StringBuilder}.
     * @param sb the <code>StringBuilder</code> to append to
     * @param buffer the <code>Buffer</code> to append
     */
    private static void dumpBuffer(@NotNull final StringBuilder sb, @NotNull final Buffer buffer)
    {
        sb.append("buffer:\n");
        synchronized (buffer.getSyncObject())
        {
            final Iterator<Line> it = buffer.iterator();
            while (it.hasNext())
            {
                final Iterable<Segment> line = it.next();
                dumpLine(sb, line);
            }
        }
    }

    /**
     * Appends the contents of a {@link Line} to a {@link StringBuilder}.
     * @param sb the <code>StringBuilder</code> to append to
     * @param line the <code>Line</code> to append
     */
    private static void dumpLine(@NotNull final StringBuilder sb, @NotNull final Iterable<Segment> line)
    {
        sb.append("line:\n");
        for (final Segment segment : line)
        {
            sb.append(segment);
        }
    }
}

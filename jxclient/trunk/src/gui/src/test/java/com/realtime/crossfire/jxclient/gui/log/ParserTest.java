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

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression tests for {@link Parser}.
 * @author Andreas Kirschbaum
 */
public class ParserTest {

    /**
     * Checks that an empty string does not add anything.
     */
    @Test
    public void testEmpty() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n");
    }

    /**
     * Checks that unknown attributes are ignored.
     */
    @Test
    public void testAttributesIgnore() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("a[a]b[]c[[]d[]]e", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:abc[]d]e\n");
    }

    /**
     * Checks that attributes are correctly parsed.
     */
    @Test
    public void testAttributes1() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("a b c", null, rec.buffer);
        rec.parser.parse("[b]a b c", null, rec.buffer);
        rec.parser.parse("[i]a b c", null, rec.buffer);
        rec.parser.parse("[ul]a b c", null, rec.buffer);
        rec.parser.parse("[fixed]a b c", null, rec.buffer);
        rec.parser.parse("[arcane]a b c", null, rec.buffer);
        rec.parser.parse("[hand]a b c", null, rec.buffer);
        rec.parser.parse("[strange]a b c", null, rec.buffer);
        rec.parser.parse("[print]a b c", null, rec.buffer);
        rec.parser.parse("[color=red]a b c", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:a \n"+"segment:b \n"+"segment:c\n"+"line:\n"+"segment:(bold)a \n"+"segment:(bold)b \n"+"segment:(bold)c\n"+"line:\n"+"segment:(italic)a \n"+"segment:(italic)b \n"+"segment:(italic)c\n"+"line:\n"+"segment:(underline)a \n"+"segment:(underline)b \n"+"segment:(underline)c\n"+"line:\n"+"segment:(fixed)a \n"+"segment:(fixed)b \n"+"segment:(fixed)c\n"+"line:\n"+"segment:(arcane)a \n"+"segment:(arcane)b \n"+"segment:(arcane)c\n"+"line:\n"+"segment:(hand)a \n"+"segment:(hand)b \n"+"segment:(hand)c\n"+"line:\n"+"segment:(strange)a \n"+"segment:(strange)b \n"+"segment:(strange)c\n"+"line:\n"+"segment:a \n"+"segment:b \n"+"segment:c\n"+"line:\n"+"segment:(red)a \n"+"segment:(red)b \n"+"segment:(red)c\n");
    }

    /**
     * Checks that attributes are correctly parsed.
     */
    @Test
    public void testAttributes2() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("a[b]b[i]c[ul]d[/b]e[/i]f[/ul]g", null, rec.buffer);
        rec.parser.parse("Hello [b] all [b]crossfire[/b] members [/b]", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:a\n"+"segment:(bold)b\n"+"segment:(bold)(italic)c\n"+"segment:(bold)(italic)(underline)d\n"+"segment:(italic)(underline)e\n"+"segment:(underline)f\n"+"segment:g\n"+"line:\n"+"segment:Hello \n"+"segment:(bold) \n"+"segment:(bold)all \n"+"segment:(bold)crossfire\n"+"segment: \n"+"segment:members \n");
    }

    /**
     * Checks that font attributes are correctly parsed.
     */
    @Test
    public void testAttributesFont1() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("[b]a[fixed]b[arcane]c[hand]d[strange]e[print]f", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:(bold)a\n"+"segment:(bold)(fixed)b\n"+"segment:(bold)(arcane)c\n"+"segment:(bold)(hand)d\n"+"segment:(bold)(strange)e\n"+"segment:(bold)f\n");
    }

    /**
     * Checks that font attributes are correctly parsed: [/fixed] is
     * undefined/does not end [fixed] block.
     */
    @Test
    public void testAttributesFont2() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("a[fixed]b[/fixed]c", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:a\n"+"segment:(fixed)bc\n");
    }

    /**
     * Checks that color attributes are correctly parsed.
     */
    @Test
    public void testAttributesColor() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("[b]a[color=red]b[color=blue]c[color=green]d[/color]e[color=#01AB4F]f[/color]", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:(bold)a\n"+"segment:(bold)(red)b\n"+"segment:(bold)(blue)c\n"+"segment:(bold)(green)d\n"+"segment:(bold)e\n"+"segment:(bold)(java.awt.Color[r=1,g=171,b=79])f\n");
    }

    /**
     * Checks that attributes are reset for each message.
     */
    @Test
    public void testAttributesReset() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("[b][i][ul][hand][color=red]first", null, rec.buffer);
        rec.parser.parse("second", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:(bold)(italic)(underline)(hand)(red)first\n"+"line:\n"+"segment:second\n");
    }

    /**
     * Checks that multi-line messages are correctly parsed.
     */
    @Test
    public void testMultiLine() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("first\n[b]second\nth[/b]ird[i]\n"+"fourth", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:first\n"+"line:\n"+"segment:(bold)second\n"+"line:\n"+"segment:(bold)th\n"+"segment:ird\n"+"line:\n"+"segment:(italic)fourth\n");
    }

    /**
     * Checks that an un-closed tag is dropped.
     */
    @Test
    public void testDropUnClosedTag() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        rec.parser.parse("abc[fixed", null, rec.buffer);
        rec.checkResult(""+"buffer:\n"+"line:\n"+"segment:abc\n");
    }

    /**
     * Encapsulates the state.
     */
    private static class Rec {

        /**
         * The default parser.
         */
        @NotNull
        private final Parser parser;

        /**
         * The default buffer.
         */
        @NotNull
        private final Buffer buffer;

        /**
         * Creates a new instance.
         */
        private Rec() {
            final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
            final GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
            final BufferedImage image = graphicsConfiguration.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
            final Graphics2D g = image.createGraphics();
            parser = new Parser();
            final Font font;
            try {
                try (InputStream fis = getClass().getClassLoader().getResourceAsStream("com/realtime/crossfire/jxclient/skins/ragnorok/fonts/regular.ttf")) {
                    try {
                        font = Font.createFont(Font.TRUETYPE_FONT, fis);
                    } catch (final FontFormatException ex) {
                        Assert.fail(ex.getMessage());
                        throw new AssertionError(ex);
                    }
                }
            } catch (final IOException ex) {
                Assert.fail(ex.getMessage());
                throw new AssertionError(ex);
            }
            buffer = new Buffer(new Fonts(font, font, font, font), g.getFontRenderContext(), 100);
            g.dispose();
        }

        /**
         * Checks for expected contents of {@link #buffer}.
         * @param expected the expected buffer contents
         */
        private void checkResult(@NotNull final String expected) {
            Assert.assertEquals(expected, dumpBuffer());
        }

        /**
         * Returns a string representation of {@link #buffer}.
         * @return the string representation
         */
        @NotNull
        private String dumpBuffer() {
            final StringBuilder sb = new StringBuilder();
            dumpBuffer(sb, buffer);
            return sb.toString();
        }

        /**
         * Appends the contents of a {@link Buffer} to a {@link StringBuilder}.
         * @param sb the {@code StringBuilder} to append to
         * @param buffer the {@code Buffer} to append
         */
        private static void dumpBuffer(@NotNull final StringBuilder sb, @NotNull final Buffer buffer) {
            sb.append("buffer:\n");
            synchronized (buffer.getSyncObject()) {
                final Iterator<Line> it = buffer.iterator();
                while (it.hasNext()) {
                    final Iterable<Segment> line = it.next();
                    dumpLine(sb, line);
                }
            }
        }

        /**
         * Appends the contents of a {@link Line} to a {@link StringBuilder}.
         * @param sb the {@code StringBuilder} to append to
         * @param line the {@code Line} to append
         */
        private static void dumpLine(@NotNull final StringBuilder sb, @NotNull final Iterable<Segment> line) {
            sb.append("line:\n");
            for (Segment segment : line) {
                sb.append(segment);
            }
        }

    }

}

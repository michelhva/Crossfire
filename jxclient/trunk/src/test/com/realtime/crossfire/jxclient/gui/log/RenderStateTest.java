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
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.jetbrains.annotations.NotNull;

/**
 * Regression tests for class {@link RenderState}.
 * @author Andreas Kirschbaum
 */
public class RenderStateTest extends TestCase {

    /**
     * Assumed height of log window.
     */
    private static final int HEIGHT = 104;

    /**
     * Create a new instance.
     * @param name the test case name
     */
    public RenderStateTest(@NotNull final String name) {
        super(name);
    }

    @NotNull
    public static Test suite() {
        return new TestSuite(RenderStateTest.class);
    }

    /**
     * Run the regression tests.
     * @param args The command line arguments (ignored).
     */
    public static void main(@NotNull final String[] args) {
        TestRunner.run(suite());
    }

    public void test1() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        final Parser parser = new Parser();

        for (int i = 0; i < HEIGHT+10; i++) {
            parser.parse("xxx"+i, null, rec.buffer);
        }
        rec.checkState(101, 0);

        // scroll to valid positions
        rec.scrollTo(0);
        rec.checkState(0, 0);
        rec.scrollTo(5*Buffer.MIN_LINE_HEIGHT+3);
        rec.checkState(5, 3);
        rec.scrollTo(10*Buffer.MIN_LINE_HEIGHT);
        rec.checkState(10, 0);

        // scroll to invalid positions
        rec.scrollTo(-1);
        rec.checkState(0, 0);
        rec.scrollTo(-10);
        rec.checkState(0, 0);

        // scroll to invalid positions
        rec.scrollTo(97*Buffer.MIN_LINE_HEIGHT-1);
        rec.checkState(96, Buffer.MIN_LINE_HEIGHT-1);
        rec.scrollTo(111*Buffer.MIN_LINE_HEIGHT);
        rec.checkState(101, 0);
    }

    public void test2() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final Rec rec = new Rec();
        final Parser parser = new Parser();

        assertEquals(0, HEIGHT%Buffer.MIN_LINE_HEIGHT);

        rec.checkState(0, 0);
        parser.parse("xxx1", null, rec.buffer);
        rec.checkState(0, 0);
        parser.parse("xxx2", null, rec.buffer);
        rec.checkState(0, 0);

        // add lines to completely fill visible area
        for (int i = 2; i < HEIGHT/Buffer.MIN_LINE_HEIGHT; i++) {
            parser.parse("xxx3"+i, null, rec.buffer);
        }
        rec.checkState(0, 0);

        // add one more line ==> buffer sticks at bottom
        parser.parse("xxx4", null, rec.buffer);
        rec.checkState(1, 0);

        // add one more line ==> buffer sticks at bottom
        parser.parse("xxx5", null, rec.buffer);
        rec.checkState(2, 0);

        // scroll up one line
        rec.scrollTo(Buffer.MIN_LINE_HEIGHT);
        rec.checkState(1, 0);

        // add one more line ==> buffer sticks at scroll position
        parser.parse("xxx6", null, rec.buffer);
        rec.checkState(1, 0);

        // scroll back to bottom
        rec.scrollTo(3*Buffer.MIN_LINE_HEIGHT);
        rec.checkState(3, 0);

        // add one more line ==> buffer sticks at bottom
        parser.parse("xxx7", null, rec.buffer);
        rec.checkState(4, 0);

        // completely fill buffer
        for (int i = HEIGHT/Buffer.MIN_LINE_HEIGHT+4; i < Buffer.MAX_LINES; i++) {
            parser.parse("xxx8"+i, null, rec.buffer);
        }
        rec.checkState(Buffer.MAX_LINES-HEIGHT/Buffer.MIN_LINE_HEIGHT, 0);

        // add one more line ==> buffer sticks at bottom
        parser.parse("xxx9", null, rec.buffer);
        rec.checkState(Buffer.MAX_LINES-HEIGHT/Buffer.MIN_LINE_HEIGHT, 0);

        // scroll one line up
        rec.scrollTo(Buffer.MAX_LINES*Buffer.MIN_LINE_HEIGHT-HEIGHT-Buffer.MIN_LINE_HEIGHT);
        rec.checkState(Buffer.MAX_LINES-HEIGHT/Buffer.MIN_LINE_HEIGHT-1, 0);

        // fill more lines ==> scroll position sticks
        for (int i = 0; i < Buffer.MAX_LINES-HEIGHT/Buffer.MIN_LINE_HEIGHT-2; i++) {
            parser.parse("xxx0"+i, null, rec.buffer);
        }
        rec.checkState(1, 0);
        parser.parse("xxx1", null, rec.buffer);
        rec.checkState(0, 0);

        // add one more line ==> scroll position hits top
        parser.parse("xxx2", null, rec.buffer);
        rec.checkState(0, 0);
    }

    /**
     * Encapsulates the state.
     */
    private static class Rec {

        /**
         * The tested {@link RenderState} instance.
         */
        @NotNull
        private final RenderState rs;

        /**
         * The tested {@link Buffer} instance.
         */
        @NotNull
        private final Buffer buffer;

        /**
         * Creates a new instance.
         */
        private Rec() {
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice gd = ge.getDefaultScreenDevice();
            final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
            final BufferedImage image = gconf.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
            final Graphics2D g = image.createGraphics();
            final Font font;
            try {
                final FileInputStream fis = new FileInputStream("skins/ragnorok/fonts/regular.ttf");
                try {
                    try {
                        font = Font.createFont(Font.TRUETYPE_FONT, fis);
                    } catch (final FontFormatException ex) {
                        fail();
                        throw new AssertionError(ex);
                    }
                } finally {
                    fis.close();
                }
            } catch (final IOException ex) {
                fail();
                throw new AssertionError(ex);
            }
            buffer = new Buffer(new Fonts(font, font, font, font), g.getFontRenderContext(), 100);
            g.dispose();
            rs = new RenderState();
            assert buffer != null;
            rs.setHeight(buffer, HEIGHT);

            final BufferListener bufferListener = new BufferListener() {
                /** {@inheritDoc} */
                @Override
                public void linesAdded(final int lines) {
                    assert rs != null;
                    assert buffer != null;
                    rs.linesAdded(buffer);
                }

                /** {@inheritDoc} */
                @Override
                public void linesReplaced(final int lines) {
                    assert rs != null;
                    assert buffer != null;
                    rs.linesReplaced(buffer);
                }

                /** {@inheritDoc} */
                @Override
                public void linesRemoved(@NotNull final List<Line> lines) {
                    assert rs != null;
                    assert buffer != null;
                    rs.linesRemoved(buffer, lines);
                }
            };
            assert buffer != null;
            buffer.addBufferListener(bufferListener);
        }

        /**
         * Calls {@link RenderState#scrollTo(Buffer, int)}.
         * @param y the second argument to pass
         */
        public void scrollTo(final int y) {
            rs.scrollTo(buffer, y);
        }

        /**
         * Checks that the {@link RenderState} instance contains expected
         * values.
         * @param expectedTopIndex the expected top index value
         * @param expectedTopOffset the expected top offset value
         */
        public void checkState(final int expectedTopIndex, final int expectedTopOffset) {
            final int expectedScrollPos = expectedTopIndex*Buffer.MIN_LINE_HEIGHT+expectedTopOffset;
            final int topIndex = rs.getTopIndex();
            final int topOffset = rs.getTopOffset();
            final int scrollPos = rs.getScrollPos();
            assertEquals(formatState(expectedTopIndex, expectedTopOffset, expectedScrollPos), formatState(topIndex, topOffset, scrollPos));
        }

        /**
         * Returns a text representation of the state.
         * @param topIndex the top index value
         * @param topOffset the top offset value
         * @param scrollPos the scroll pos value
         * @return the text representation
         */
        @NotNull
        public String formatState(final int topIndex, final int topOffset, final int scrollPos) {
            return "top="+topIndex+"/"+topOffset+" pos="+scrollPos+"/"+buffer.getTotalHeight();
        }

    }

}

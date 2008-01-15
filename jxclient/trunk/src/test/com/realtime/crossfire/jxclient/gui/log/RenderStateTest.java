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
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.FileInputStream;
import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Regression tests for class {@link RenderState}.
 *
 * @author Andreas Kirschbaum
 */
public class RenderStateTest extends TestCase
{
    /**
     * Assumed height of log window.
     */
    private static final int HEIGHT = 100;

    private RenderState rs = null;

    private Buffer buffer = null;

    private int stateChanged = 0;

    private final RenderStateListener renderStateListener = new RenderStateListener()
    {
        /** {@inheritDoc} */
        public void stateChanged()
        {
            stateChanged++;
        }

        /** {@inheritDoc} */
        public int getHeight()
        {
            return HEIGHT;
        }
    };

    /**
     * Create a new instance.
     *
     * @param name the test case name
     */
    public RenderStateTest(final String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(RenderStateTest.class);
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

    public void test1()
    {
        final Parser parser = new Parser();

        checkState(0, true, 0, -HEIGHT);
        parser.parse("xxx", null, buffer);
        checkState(1, true, 0, -HEIGHT+1);
        parser.parse("xxx", null, buffer);
        checkState(1, true, 0, -HEIGHT+2);

        // scroll up; will not do anything because buffer has too few lines
        rs.scrollUp(1);
        checkState(1/*XXX: should be 0*/, true, 0, -HEIGHT+2);

        // scroll down; will not do anything because display is at bottom
        rs. scrollDown(1);
        checkState(1/*XXX: should be 0*/, true, 0, -HEIGHT+2);

        // fill the buffer
        for(int i = 0; i < 100; i++)
        {
            parser.parse("xxx", null, buffer);
        }
        checkState(100, true, 2, 0);

        // scroll up
        rs.scrollUp(1);
        checkState(1, false, 1, 0);

        // add one more line
        parser.parse("xxx", null, buffer);
        checkState(1, false, 1, 0);

        // scroll down
        rs.scrollDown(1);
        checkState(1, false, 2, 0);

        // scroll down
        rs.scrollDown(1);
        checkState(1, true, 3, 0);

        // completely fill the buffer
        assertEquals(Buffer.MAX_LINES, 250);
        for(int i = 0; i < 200; i++)
        {
            parser.parse("xxx", null, buffer);
        }
        assertEquals(150, Buffer.MAX_LINES-HEIGHT);
        checkState(Buffer.MAX_LINES-50, true, 150, 0);

        // scroll up
        rs.scrollUp(3);
        checkState(1, false, 147, 0);

        // add one more line; this decrements topIndex because one line is pruned
        parser.parse("xxx", null, buffer);
        checkState(1, false, 146, 0);

        // add enough lines to make topIndex decrement to zero
        for(int i = 0; i < 146; i++)
        {
            parser.parse("xxx", null, buffer);
        }
        checkState(146, false, 0, 0);

        // add one more line => topIndex must stay at zero
        parser.parse("xxx", null, buffer);
        checkState(146, false, 0, 0);

        // add one more line => topIndex must stay at zero
        parser.parse("xxx", null, buffer);
        checkState(146, false, 0, 0);

        // scroll down
        rs.scrollDown(1);
        checkState(1, false, 1, 0);
    }

    private void checkState(final int expectedStateChanged, final boolean expectedDisplayBottom, final int expectedTopIndex, final int expectedTopOffset)
    {
        assertEquals(format(expectedStateChanged, expectedDisplayBottom, expectedTopIndex, expectedTopOffset), format(stateChanged, rs.isDisplayBottom(), rs.getTopIndex(), rs.getTopOffset()));
        stateChanged = 0;
    }

    private static String format(final int stateChanged, final boolean displayBottom, final int topIndex, final int topOffset)
    {
        return "ch="+(stateChanged != 0)+" bottom="+displayBottom+" top="+topIndex+"/"+topOffset;
    }

    /** {@inheritDoc} */
    @Override public void setUp() throws FontFormatException, IOException
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        final BufferedImage image = gconf.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
        final Graphics2D g = image.createGraphics();
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
        rs = new RenderState(renderStateListener, buffer);

        stateChanged = 0;
    }
}

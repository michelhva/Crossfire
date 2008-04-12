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
package com.realtime.crossfire.jxclient.gui.gauge;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Regression tests for class {@link Orientation} and implementing classes.
 * @author Andreas Kirschbaum
 */
public class OrientationTest extends TestCase
{
    /**
     * Creates a new instance.
     * @param name the test case name
     */
    public OrientationTest(final String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(OrientationTest.class);
    }

    /**
     * Runs the regression tests.
     * @param args the command line arguments (ignored)
     */
    public static void main(final String[] args)
    {
        TestRunner.run(suite());
    }

    /**
     * Checks that {@link Orientation} instances work correctly.
     */
    public void testOrientations() throws IllegalAccessException, InstantiationException
    {
        check(OrientationWE.class, true, false);
        check(OrientationEW.class, true, true);
        check(OrientationNS.class, false, false);
        check(OrientationSN.class, false, true);
    }

    private void check(final Class<? extends Orientation> class_, final boolean useX, final boolean flip) throws IllegalAccessException, InstantiationException
    {
        checkPositive(class_, useX, flip);
        checkNegative(class_, useX, flip);
    }

    private void checkPositive(final Class<? extends Orientation> class_, final boolean useX, final boolean flip) throws IllegalAccessException, InstantiationException
    {
        final Orientation o = class_.newInstance();
        o.setHasNegativeImage(false);
        o.setExtends(useX ? 100 : 32, useX ? 32 : 100);
        check(o, useX, flip, false, 0, 0, 0, 32, false);

        // 0%
        o.setValues(50, 50, 90);
        check(o, useX, flip, true, 0, 0, 0, 32, false);

        // 25%
        o.setValues(60, 50, 90);
        check(o, useX, flip, true, 0, 0, 25, 32, false);

        // 100%
        o.setValues(90, 50, 90);
        check(o, useX, flip, true, 0, 0, 100, 32, false);

        // 150%
        o.setValues(110, 50, 90);
        check(o, useX, flip, true, 0, 0, 100, 32, false);

        // -50%
        o.setValues(30, 50, 90);
        check(o, useX, flip, true, 0, 0, 0, 32, false);

        // -150%
        o.setValues(-10, 50, 90);
        check(o, useX, flip, true, 0, 0, 0, 32, false);
    }

    private void checkNegative(final Class<? extends Orientation> class_, final boolean useX, final boolean flip) throws IllegalAccessException, InstantiationException
    {
        final Orientation o = class_.newInstance();
        o.setHasNegativeImage(true);
        o.setExtends(useX ? 100 : 32, useX ? 32 : 100);
        check(o, useX, flip, false, 0, 0, 0, 32, false);

        // 0%
        o.setValues(50, 50, 90);
        check(o, useX, flip, true, 0, 0, 0, 32, false);

        // 25%
        o.setValues(60, 50, 90);
        check(o, useX, flip, true, 0, 0, 25, 32, false);

        // 100%
        o.setValues(90, 50, 90);
        check(o, useX, flip, true, 0, 0, 100, 32, false);

        // 150%
        o.setValues(110, 50, 90);
        check(o, useX, flip, true, 0, 0, 100, 32, false);

        // -50%
        o.setValues(30, 50, 90);
        check(o, useX, flip, true, 0, 0, 50, 32, true);

        // -150%
        o.setValues(-10, 50, 90);
        check(o, useX, flip, true, 0, 0, 100, 32, true);
    }

    private void check(final Orientation o, final boolean useX, final boolean flip, final boolean valid, final int x, final int y, final int w, final int h, final boolean negativeImage)
    {
        final int isX = useX ? o.getX() : o.getY();
        final int isY = useX ? o.getY() : o.getX();
        final int isW = useX ? o.getW() : o.getH();
        final int isH = useX ? o.getH() : o.getW();
        assertEquals(valid, o.isValid());
        assertEquals(flip ? 100-x-w : x, isX);
        assertEquals(y, isY);
        assertEquals(w, isW);
        assertEquals(h, isH);
        assertEquals(negativeImage, o.isNegativeImage());
    }
}

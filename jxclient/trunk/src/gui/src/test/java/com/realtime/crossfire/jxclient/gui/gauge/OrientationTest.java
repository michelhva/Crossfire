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

package com.realtime.crossfire.jxclient.gui.gauge;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression tests for class {@link Orientation} and implementing classes.
 * @author Andreas Kirschbaum
 * @noinspection MagicNumber
 */
public class OrientationTest {

    /**
     * Checks that {@link Orientation} instances work correctly.
     * @throws IllegalAccessException if the test failed
     * @throws InstantiationException if the test failed
     */
    @Test
    public void testOrientations() throws IllegalAccessException, InstantiationException {
        check(OrientationWE.class, true, false);
        check(OrientationEW.class, true, true);
        check(OrientationNS.class, false, false);
        check(OrientationSN.class, false, true);
    }

    /**
     * Checks an {@link Orientation} instance.
     * @param class_ the instance's class
     * @param useX if set, swap x and y coordinates
     * @param flip if set, invert the x coordinate
     * @throws IllegalAccessException if the test fails
     * @throws InstantiationException if the test fails
     */
    private static void check(@NotNull final Class<? extends Orientation> class_, final boolean useX, final boolean flip) throws IllegalAccessException, InstantiationException {
        checkPositive(class_, useX, flip);
        checkNegative(class_, useX, flip);
    }

    /**
     * Checks that positive values are correctly handled by an {@link
     * Orientation} instance.
     * @param class_ the instance's class
     * @param useX if set, swap x and y coordinates
     * @param flip if set, invert the x coordinate
     * @throws IllegalAccessException if the test fails
     * @throws InstantiationException if the test fails
     */
    private static void checkPositive(@NotNull final Class<? extends Orientation> class_, final boolean useX, final boolean flip) throws IllegalAccessException, InstantiationException {
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

    /**
     * Checks that negative values are correctly handled by an {@link
     * Orientation} instance.
     * @param class_ the instance's class
     * @param useX if set, swap x and y coordinates
     * @param flip if set, invert the x coordinate
     * @throws IllegalAccessException if the test fails
     * @throws InstantiationException if the test fails
     */
    private static void checkNegative(@NotNull final Class<? extends Orientation> class_, final boolean useX, final boolean flip) throws IllegalAccessException, InstantiationException {
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

    /**
     * Checks that a value is correctly handled.
     * @param o the orientation instance to check
     * @param useX if set, swap x and y coordinates
     * @param flip if set, invert the x coordinate
     * @param valid the expected "valid" value
     * @param x the expected x coordinate
     * @param y the expected y coordinate
     * @param w the expected width
     * @param h the expected height
     * @param negativeImage the expected "negative image" value
     */
    private static void check(@NotNull final Orientation o, final boolean useX, final boolean flip, final boolean valid, final int x, final int y, final int w, final int h, final boolean negativeImage) {
        final int isX = useX ? o.getX() : o.getY();
        final int isY = useX ? o.getY() : o.getX();
        final int isW = useX ? o.getW() : o.getH();
        final int isH = useX ? o.getH() : o.getW();
        Assert.assertEquals(valid, o.isValid());
        Assert.assertEquals(flip ? 100-x-w : x, isX);
        Assert.assertEquals(y, isY);
        Assert.assertEquals(w, isW);
        Assert.assertEquals(h, isH);
        Assert.assertEquals(negativeImage, o.isNegativeImage());
    }

}

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

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression tests for class {@link DoubleMapping}.
 * @author Andreas Kirschbaum
 */
public class DoubleMappingTest {

    /**
     * Checks that basic operations for on {@link DoubleMapping} instances do
     * work.
     */
    @Test
    public void test1() {
        final DoubleMapping doubleMapping = new DoubleMapping();
        doubleMapping.insert(0, 0);
        check(doubleMapping, 0);
        doubleMapping.insert(1, 0);
        check(doubleMapping, 1, 0);
        doubleMapping.insert(1, 1);
        check(doubleMapping, 2, 1, 0);
        doubleMapping.insert(3, 2);
        check(doubleMapping, 3, 1, 0, 2);
        doubleMapping.insert(1, 2);
        check(doubleMapping, 4, 2, 1, 0, 3);
        doubleMapping.remove(0);
        check(doubleMapping, 2, 1, 0, 3);
        doubleMapping.remove(1);
        check(doubleMapping, 1, 0, 2);
    }

    /**
     * Checks that a {@link DoubleMapping} instance contains the expected
     * values.
     * @param doubleMapping the double mapping instance
     * @param values the expected values
     */
    private static void check(@NotNull final DoubleMapping doubleMapping, @NotNull final int... values) {
        for (int i = 0; i < values.length; i++) {
            final int dst = doubleMapping.getDst(i);
            Assert.assertEquals("index "+i, values[i], dst);
            Assert.assertEquals("index "+i, i, doubleMapping.getSrc(dst));
        }
    }

}

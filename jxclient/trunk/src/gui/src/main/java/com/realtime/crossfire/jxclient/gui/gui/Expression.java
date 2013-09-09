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

package com.realtime.crossfire.jxclient.gui.gui;

import org.jetbrains.annotations.NotNull;

/**
 * An expression yielding an integer value derived from a screen resolution.
 * @author Andreas Kirschbaum
 */
public class Expression {

    /**
     * The constant term.
     */
    private final int constant;

    /**
     * The screen width dependent factor.
     */
    private final int widthFactor;

    /**
     * The screen height dependent factor.
     */
    private final int heightFactor;

    /**
     * The preferred width dependent factor.
     */
    private final int prefWidthFactor;

    /**
     * The preferred height dependent factor.
     */
    private final int prefHeightFactor;

    /**
     * Creates a new instance.
     * @param constant the constant term
     * @param widthFactor the screen width dependent factor
     * @param heightFactor the screen height dependent factor
     * @param prefWidthFactor the preferred width dependent factor
     * @param prefHeightFactor the preferred height dependent factor
     */
    public Expression(final int constant, final int widthFactor, final int heightFactor, final int prefWidthFactor, final int prefHeightFactor) {
        this.constant = constant;
        this.widthFactor = widthFactor;
        this.heightFactor = heightFactor;
        this.prefWidthFactor = prefWidthFactor;
        this.prefHeightFactor = prefHeightFactor;
    }

    /**
     * Creates a new instance as the sum or difference of two expressions.
     * @param expression1 the left expression
     * @param negative whether the right expression should be added
     * (<code>false</code>) or subtracted (<code>true</code>)
     * @param expression2 the right expression
     */
    public Expression(@NotNull final Expression expression1, final boolean negative, @NotNull final Expression expression2) {
        final int factor = negative ? -1 : 1;
        constant = expression1.constant+expression2.constant*factor;
        widthFactor = expression1.widthFactor+expression2.widthFactor*factor;
        heightFactor = expression1.heightFactor+expression2.heightFactor*factor;
        prefWidthFactor = expression1.prefWidthFactor+expression2.prefWidthFactor*factor;
        prefHeightFactor = expression1.prefHeightFactor+expression2.prefHeightFactor*factor;
    }

    /**
     * Evaluates the expression into a constant.
     * @param width the screen width
     * @param height the screen height
     * @param prefWidth the preferred width dependent factor
     * @param prefHeight the preferred height dependent factor
     * @return the constant
     */
    public int evaluate(final int width, final int height, final int prefWidth, final int prefHeight) {
        return constant+applyFactor(width, widthFactor)+applyFactor(height, heightFactor)+applyFactor(prefWidth, prefWidthFactor)+applyFactor(prefHeight, prefHeightFactor);
    }

    /**
     * Evaluates the expression into a constant.
     * @return the constant
     */
    public int evaluateConstant() {
        if (widthFactor != 0 || heightFactor != 0) {
            throw new IllegalStateException();
        }

        return constant;
    }

    /**
     * Applies a factor to a value.
     * @param value the value
     * @param factor the factor
     * @return the result
     */
    private static int applyFactor(final int value, final int factor) {
        return (value*factor+1)/2;
    }

}

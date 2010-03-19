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

package com.realtime.crossfire.jxclient.skin.skin;

import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates the extent of a GUI element. It consists of a location and a
 * size.
 * @author Andreas Kirschbaum
 */
public class Extent {

    /**
     * The x coordinate.
     */
    @NotNull
    private Expression x;

    /**
     * The y coordinate.
     */
    @NotNull
    private Expression y;

    /**
     * The width.
     */
    @NotNull
    private Expression w;

    /**
     * The height.
     */
    @NotNull
    private Expression h;

    /**
     * Creates a new instance.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h thr height
     */
    public Extent(@NotNull final Expression x, @NotNull final Expression y, @NotNull final Expression w, @NotNull final Expression h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Creates a new instance.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h thr height
     */
    public Extent(final int x, final int y, final int w, final int h) {
        this.x = new Expression(x, 0, 0);
        this.y = new Expression(y, 0, 0);
        this.w = new Expression(w, 0, 0);
        this.h = new Expression(h, 0, 0);
    }

    /**
     * Srets the location.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setLocation(@NotNull final Expression x, @NotNull final Expression y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Srets the size.
     * @param w the width
     * @param h the height
     */
    public void setSize(@NotNull final Expression w, @NotNull final Expression h) {
        this.w = w;
        this.h = h;
    }

    /**
     * Returns the x coordinate.
     * @param width the screen width
     * @param height the screen height
     * @return the x coordinate
     */
    public int getX(final int width, final int height) {
        return x.evaluate(width, height);
    }

    /**
     * Returns the y coordinate.
     * @param width the screen width
     * @param height the screen height
     * @return the y coordinate
     */
    public int getY(final int width, final int height) {
        return y.evaluate(width, height);
    }

    /**
     * Returns the width.
     * @param width the screen width
     * @param height the screen height
     * @return the width
     */
    public int getW(final int width, final int height) {
        return w.evaluate(width, height);
    }

    /**
     * Returns the height.
     * @param width the screen width
     * @param height the screen height
     * @return the height
     */
    public int getH(final int width, final int height) {
        return h.evaluate(width, height);
    }

    /**
     * Returns the x coordinate. The x coordinate must evaluate to a constant.
     * @return the x coordinate
     */
    public int getConstantX() {
        return x.evaluateConstant();
    }

    /**
     * Returns the y coordinate. The y coordinate must evaluate to a constant.
     * @return the y coordinate
     */
    public int getConstantY() {
        return y.evaluateConstant();
    }

    /**
     * Returns the width. The width must evaluate to a constant.
     * @return the width
     */
    public int getConstantW() {
        return w.evaluateConstant();
    }

    /**
     * Returns the height. The width must evaluate to a constant.
     * @return the height
     */
    public int getConstantH() {
        return h.evaluateConstant();
    }

    /**
     * Returns the width.
     * @return the width
     */
    @NotNull
    public Expression getWExpression() {
        return w;
    }

    /**
     * Returns the height.
     * @return the height
     */
    @NotNull
    public Expression getHExpression() {
        return h;
    }

}

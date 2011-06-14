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

package com.realtime.crossfire.jxclient.skin.io;

import com.realtime.crossfire.jxclient.skin.skin.Expression;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Parser for integer expressions.
 * @author Andreas Kirschbaum
 */
public class ExpressionParser {

    /**
     * The identifier evaluating to the width in pixels of the current
     * resolution.
     */
    @NotNull
    private static final String WIDTH = "WIDTH";

    /**
     * The identifier evaluating to the height in pixels of the current
     * resolution.
     */
    @NotNull
    private static final String HEIGHT = "HEIGHT";

    /**
     * The identifier evaluating to the preferred width in pixels of the current
     * dialog.
     */
    @NotNull
    private static final String PREF_WIDTH = "PREF_WIDTH";

    /**
     * The identifier evaluating to the preferred height in pixels of the
     * current dialog.
     */
    @NotNull
    private static final String PREF_HEIGHT = "PREF_HEIGHT";

    /**
     * Pattern to parse integer constants.
     */
    @NotNull
    private static final Pattern PATTERN_EXPR = Pattern.compile("([0-9]+|"+WIDTH+"|"+HEIGHT+"|"+WIDTH+"/2|"+HEIGHT+"/2|"+PREF_WIDTH+"|"+PREF_HEIGHT+"|"+PREF_WIDTH+"/2|"+PREF_HEIGHT+"/2)([-+])(.+)");

    /**
     * Private constructor to prevent instantiation.
     */
    private ExpressionParser() {
    }

    /**
     * Parses an integer constant. Valid constants are "3", "3+4", and
     * "1+2-3+4".
     * @param str the integer constant string to parse
     * @return the integer value
     * @throws IOException if a parsing error occurs
     */
    public static int parseInt(@NotNull final String str) throws IOException {
        return parseExpression(str).evaluateConstant();
    }

    /**
     * Parses an integer constant. Valid constants are "3", "3+4", and
     * "1+2-3+4".
     * @param str the integer constant string to parse
     * @return the integer expression
     * @throws IOException if a parsing error occurs
     */
    @NotNull
    public static Expression parseExpression(@NotNull final String str) throws IOException {
        try {
            return parseIntegerConstant(str);
        } catch (final NumberFormatException ignored) {
            // ignore
        }

        Matcher matcher = PATTERN_EXPR.matcher(str);
        if (!matcher.matches()) {
            throw new IOException("invalid number: "+str);
        }
        Expression value;
        try {
            value = parseIntegerConstant(matcher.group(1));
            while (true) {
                final boolean negative = matcher.group(2).equals("-");
                final String rest = matcher.group(3);

                matcher = PATTERN_EXPR.matcher(rest);
                if (!matcher.matches()) {
                    final Expression expressionRest = parseIntegerConstant(rest);
                    value = new Expression(value, negative, expressionRest);
                    break;
                }

                final Expression valueRest = parseIntegerConstant(matcher.group(1));
                value = new Expression(value, negative, valueRest);
            }
        } catch (final NumberFormatException ex) {
            throw new IOException("invalid number: "+str, ex);
        }

        return value;
    }

    /**
     * Parses an integer constant string.
     * @param str the string
     * @return the integer expression
     * @throws NumberFormatException if the string cannot be parsed
     */
    @NotNull
    private static Expression parseIntegerConstant(@NotNull final String str) {
        try {
            return new Expression(Integer.parseInt(str), 0, 0, 0, 0);
        } catch (final NumberFormatException ex) {
            if (str.equals(WIDTH)) {
                return new Expression(0, 2, 0, 0, 0);
            }

            if (str.equals(HEIGHT)) {
                return new Expression(0, 0, 2, 0, 0);
            }

            if (str.equals(WIDTH+"/2")) {
                return new Expression(0, 1, 0, 0, 0);
            }

            if (str.equals(HEIGHT+"/2")) {
                return new Expression(0, 0, 1, 0, 0);
            }

            if (str.equals(PREF_WIDTH)) {
                return new Expression(0, 0, 0, 2, 0);
            }

            if (str.equals(PREF_HEIGHT)) {
                return new Expression(0, 0, 0, 0, 2);
            }

            if (str.equals(PREF_WIDTH+"/2")) {
                return new Expression(0, 0, 0, 1, 0);
            }

            if (str.equals(PREF_HEIGHT+"/2")) {
                return new Expression(0, 0, 0, 0, 1);
            }

            throw ex;
        }
    }

}

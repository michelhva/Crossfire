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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.RectangularShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * One segment of a {@link Line} which should be displayed without changing
 * attributes.
 * @author Andreas Kirschbaum
 */
public class TextSegment extends AbstractSegment {

    /**
     * The text to display.
     */
    @NotNull
    private final String text;

    /**
     * Whether bold face is enabled.
     */
    private final boolean bold;

    /**
     * Whether italic face is enabled.
     */
    private final boolean italic;

    /**
     * Whether underlining is enabled.
     */
    private final boolean underline;

    /**
     * The font to use.
     */
    @NotNull
    private final FontID font;

    /**
     * The color to use. {@code null} means "default color".
     */
    @Nullable
    private final Color color;

    /**
     * The distance of the underline to the base line. Set to {@code 0} if
     * unknown.
     */
    private int underlineOffset;

    /**
     * Creates a new segment.
     * @param text the text to display
     * @param bold whether bold face is enabled
     * @param italic whether italic face is enabled
     * @param underline whether underlining is enabled
     * @param font the font to use
     * @param color the color to use; {@code null} means "default color"
     */
    public TextSegment(@NotNull final String text, final boolean bold, final boolean italic, final boolean underline, @NotNull final FontID font, @Nullable final Color color) {
        this.text = text;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.font = font;
        this.color = color;
    }

    /**
     * Returns the text to display.
     * @return the text to display
     */
    @NotNull
    public String getText() {
        return text;
    }

    /**
     * Returns the {@link Font} to use for a given {@link Segment}.
     * @param fonts the {@code Fonts} instance to use
     * @return the font
     */
    @NotNull
    private Font getFont(@NotNull final Fonts fonts) {
        switch (font) {
        case PRINT:
            return fonts.getFontPrint();

        case FIXED:
            return bold ? fonts.getFontFixedBold() : fonts.getFontFixed();

        case ARCANE:
            return fonts.getFontArcane();

        case HAND:
            return fonts.getFontPrint();

        case STRANGE:
            return fonts.getFontPrint();
        }

        throw new AssertionError();
    }

    @Override
    public void draw(@NotNull final Graphics g, final int y, @NotNull final Fonts fonts) {
        g.setColor(color);
        g.setFont(getFont(fonts));
        final int segmentX = getX();
        final int segmentY = getY();
        g.drawString(text, segmentX, y+segmentY);
        if (underline) {
            g.drawLine(segmentX, y+segmentY+underlineOffset, segmentX+getWidth()-1, y+segmentY+underlineOffset);
        }
    }

    @Override
    public void updateAttributes(@NotNull final Fonts fonts, @NotNull final FontRenderContext context) {
        final LineMetrics lineMetrics = getFont(fonts).getLineMetrics(text, context);
        underlineOffset = Math.round(lineMetrics.getUnderlineOffset());
    }

    @NotNull
    @Override
    public RectangularShape getSize(@NotNull final Fonts fonts, @NotNull final FontRenderContext context) {
        return getFont(fonts).getStringBounds(text, context);
    }

    /**
     * Returns whether this segment matches the given attributes.
     * @param bold the bold attribute
     * @param italic the italic attribute
     * @param underline the underline attribute
     * @param font the font attribute
     * @param color the color attribute
     * @return whether all attributes do match
     */
    public boolean matches(final boolean bold, final boolean italic, final boolean underline, @NotNull final FontID font, @Nullable final Color color) {
        return this.bold == bold && this.italic == italic && this.underline == underline && this.font == font && this.color == color;
    }

    @NotNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("segment:");
        if (bold) {
            sb.append("(bold)");
        }
        if (italic) {
            sb.append("(italic)");
        }
        if (underline) {
            sb.append("(underline)");
        }
        if (font != FontID.PRINT) {
            sb.append('(').append(font.toString().toLowerCase()).append(')');
        }
        if (color != null) {
            sb.append('(').append(Parser.toString(color)).append(')');
        }
        sb.append(text);
        sb.append('\n');
        return sb.toString();
    }

}

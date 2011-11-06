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

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link AbstractLabel} that renders the text as a list of plain strings. The
 * lines are separated by newline characters.
 * @author Andreas Kirschbaum
 */
public class GUIMultiLineLabel extends GUILabel {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The pattern to split the text into lines.
     */
    @NotNull
    private static final Pattern LINE_SEPARATOR_PATTERN = Pattern.compile(" *\n");

    /**
     * The text lines to draw.
     */
    @NotNull
    private String[] lines;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param picture the background image; <code>null</code> for no background
     * @param font the font for rendering the label text
     * @param color the font color
     * @param backgroundColor the background color
     * @param alignment the text alignment
     * @param text the label text
     */
    public GUIMultiLineLabel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @Nullable final BufferedImage picture, @NotNull final Font font, @NotNull final Color color, @Nullable final Color backgroundColor, @NotNull final Alignment alignment, @NotNull final String text) {
        super(tooltipManager, elementListener, name, picture, text, font, color, backgroundColor, alignment);
        lines = LINE_SEPARATOR_PATTERN.split(getText(), -1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void textChanged() {
        lines = LINE_SEPARATOR_PATTERN.split(getText(), -1);
        super.textChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);

        if (lines.length <= 0) {
            return;
        }

        final Graphics2D g2 = (Graphics2D)g;
        final Dimension rectangle = GuiUtils.getTextDimension("Xg", getFontMetrics(getTextFont()));
        final int lineHeight = rectangle.height;

        int y = 0;
        for (final String line : lines) {
            drawLine(g2, y, lineHeight, line);
            y += lineHeight;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return getTextSize();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return getTextSize();
    }

    /**
     * Returns the minimal size of this component to display all of {@link
     * #lines}.
     * @return the size
     */
    @NotNull
    private Dimension getTextSize() {
        final FontMetrics fontMetrics = getFontMetrics(getTextFont());

        int width = 0;
        for (final String line : lines) {
            final Dimension dimension = GuiUtils.getTextDimension(line, fontMetrics);
            if (width < dimension.width) {
                width = dimension.width;
            }
        }

        final Dimension rectangle = GuiUtils.getTextDimension("Xg", fontMetrics);
        final int height = lines.length*rectangle.height;

        return new Dimension(width, height);
    }

}

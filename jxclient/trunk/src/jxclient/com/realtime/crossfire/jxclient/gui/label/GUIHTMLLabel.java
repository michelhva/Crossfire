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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implements an {@link AbstractLabel} that displays HTML contents.
 * @author Lauwenmark
 */
public class GUIHTMLLabel extends AbstractLabel {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * Size of border around text in auto-resize mode.
     */
    private static final int AUTO_BORDER_SIZE = 2;

    /**
     * The pattern used to split a string into lines.
     */
    @NotNull
    private static final Pattern PATTERN_LINE_BREAK = Pattern.compile("<br>");

    /**
     * If set, auto-resize this element to the extent of {@link #text}.
     */
    private boolean autoResize = false;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param backgroundPicture the optional background picture
     * @param font the text font
     * @param color the text color
     * @param backgroundColor the background color; ignored if background
     * picture is set
     * @param text the text
     */
    public GUIHTMLLabel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @Nullable final BufferedImage backgroundPicture, @NotNull final Font font, @NotNull final Color color, @Nullable final Color backgroundColor, @NotNull final String text) {
        super(tooltipManager, elementListener, name, text, font, color, backgroundPicture, backgroundColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void textChanged() {
        autoResize();
        setChanged();
    }

    /**
     * Enable or disable auto-resizing. If enabled, the gui element's size
     * changes to the displayed text's size.
     * @param autoResize If set, enable auto-resizing; if unset, disable
     * auto-resizing.
     */
    public void setAutoResize(final boolean autoResize) {
        if (this.autoResize != autoResize) {
            this.autoResize = autoResize;
            autoResize();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);

        final Font font = getTextFont();
        final Color color = getTextColor();
        g.setFont(font);
        g.setColor(color);

        final Reader reader = new StringReader(getText());
        final HTMLEditorKit.ParserCallback renderer = new InternalHTMLRenderer(font, color, g, 0, font.getSize(), autoResize ? AUTO_BORDER_SIZE : 0);
        final ParserDelegator parserDelegator = new ParserDelegator();
        try {
            parserDelegator.parse(reader, renderer, false);
        } catch (final IOException ex) {
            System.err.println("GUIHTMLLabel: cannot render HTML: "+ex.getMessage());
            System.err.println(getText());
        }
    }

    /**
     * If auto-resizing is enabled, calculate the new width and height.
     */
    private void autoResize() {
        if (!autoResize) {
            return;
        }

        final FontMetrics fontMetrics = getFontMetrics(getTextFont());

        int width = 0;
        int height = 0;
        for (final String str : PATTERN_LINE_BREAK.split(getText(), -1)) {
            final Dimension size = GuiUtils.getTextDimension(str, fontMetrics);
            width = Math.max(width, size.width);
            height += size.height;
        }
        setSize(Math.max(1, width+2*AUTO_BORDER_SIZE), Math.max(1, height+2*AUTO_BORDER_SIZE));
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 32); // XXX
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 32); // XXX
    }

}

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

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.RectangularShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIGauge} which displays the current value as a text string on top
 * of the gauge.
 * @author Andreas Kirschbaum
 */
public class GUITextGauge extends GUIGauge {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The text color.
     */
    @NotNull
    private final Color color;

    /**
     * The text font.
     */
    @NotNull
    private final Font font;

    /**
     * The label text.
     */
    @NotNull
    private String labelText = "";

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param pictureFull the image for positive values
     * @param pictureNegative the image for negative values
     * @param pictureEmpty the image for an empty gauge
     * @param orientation the gauge's orientation
     * @param tooltipPrefix the prefix for generating a tooltip
     * @param color the text color
     * @param font the text font
     * @param alpha transparency to draw with
     */
    public GUITextGauge(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Image pictureFull, @Nullable final Image pictureNegative, @NotNull final Image pictureEmpty, @NotNull final Orientation orientation, @Nullable final String tooltipPrefix, @NotNull final Color color, @NotNull final Font font, final float alpha, @NotNull final CommandList commandList) {
        super(tooltipManager, elementListener, name, pictureFull, pictureNegative, pictureEmpty, orientation, tooltipPrefix, alpha, commandList);
        this.color = color;
        this.font = font;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(new Color(0, 0, 0, 0.0f));
        g2.setColor(color);
        g2.setFont(font);
        final String text = labelText;
        final RectangularShape rectangle = font.getStringBounds(text, g2.getFontRenderContext());
        final int x = (int)Math.round((getWidth()-rectangle.getWidth())/2);
        final int y = (int)Math.round(getHeight()-rectangle.getMaxY()-rectangle.getMinY())/2;
        g2.drawString(text, x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final int curValue, final int minValue, final int maxValue, @NotNull final String labelText, @NotNull final String tooltipText) {
        super.setValues(curValue, minValue, maxValue, labelText, tooltipText);
        this.labelText = labelText;
        setChanged();
    }

}

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

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Transparency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Displays a value as a graphical gauge that's filling state depends on the
 * value.
 * @author Andreas Kirschbaum
 */
public class GUIDupGauge extends AbstractGUIElement implements GUIGaugeListener {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The label text.
     */
    @NotNull
    private String labelText = "";

    /**
     * The tooltip prefix. It is prepended to {@link #tooltipText} to form the
     * tooltip.
     */
    @Nullable
    private final String tooltipPrefix;

    /**
     * The tooltip suffix. It is appended to {@link #tooltipPrefix} to form the
     * tooltip.
     */
    @NotNull
    private String tooltipText = "";

    /**
     * The image representing an empty gauge.
     */
    @Nullable
    private final Image emptyImage;

    /**
     * The gauge's orientation.
     */
    @NotNull
    private final Orientation orientationDiv;

    /**
     * The gauge's orientation.
     */
    @NotNull
    private final Orientation orientationMod;

    /**
     * The gauge state.
     */
    @NotNull
    private final GaugeState gaugeStateDiv;

    /**
     * The gauge state.
     */
    @NotNull
    private final GaugeState gaugeStateMod;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param fullImageDiv the top image
     * @param fullImageMod the bottom image
     * @param emptyImage the image representing an empty gauge; if set to
     * <code>null</code> an empty background is used instead
     * @param orientationDiv the gauge's orientation
     * @param orientationMod the gauge's orientation
     * @param tooltipPrefix the prefix for displaying tooltips; if set to
     * <code>null</code> no tooltips are shown
     */
    public GUIDupGauge(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Image fullImageDiv, @NotNull final Image fullImageMod, @Nullable final Image emptyImage, @NotNull final Orientation orientationDiv, @NotNull final Orientation orientationMod, @Nullable final String tooltipPrefix) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT);
        this.emptyImage = emptyImage;
        this.orientationDiv = orientationDiv;
        this.orientationMod = orientationMod;
        this.tooltipPrefix = tooltipPrefix;
        gaugeStateDiv = new GaugeState(fullImageDiv, null, 0, 0);
        final int w = getWidth();
        final int h = getHeight();
        gaugeStateMod = new GaugeState(fullImageMod, null, 0, h/2);
        orientationDiv.setExtends(w, h);
        orientationMod.setExtends(w, h);
        orientationDiv.setValues(0, 0, 9);
        orientationMod.setValues(0, 0, 9);
        gaugeStateDiv.setValues(orientationDiv);
        gaugeStateMod.setValues(orientationMod);
        updateTooltipText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 0.0f));
        g.fillRect(0, 0, getWidth(), getHeight());
        if (emptyImage != null) {
            g.drawImage(emptyImage, 0, 0, null);
        }
        gaugeStateDiv.draw(g);
        gaugeStateMod.draw(g);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return getGaugeStateSize();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getMinimumSize() {
        return getGaugeStateSize();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getMaximumSize() {
        return getGaugeStateSize();
    }

    /**
     * Returns the maximum size of {@link #gaugeStateDiv} and {@link
     * #gaugeStateMod}.
     * @return the maximum size
     */
    @NotNull
    private Dimension getGaugeStateSize() {
        final Dimension div = gaugeStateDiv.getPreferredSize();
        final Dimension mod = gaugeStateMod.getPreferredSize();
        return new Dimension(Math.max(div.width, mod.width), div.height+mod.height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final int curValue, final int minValue, final int maxValue, @NotNull final String labelText, @NotNull final String tooltipText) {
        if (minValue != 0) {
            throw new IllegalArgumentException();
        }
        if (maxValue != 99) {
            throw new IllegalArgumentException();
        }
        if (!orientationDiv.setValues(curValue/10, 0, 9) && !orientationMod.setValues(curValue%10, 0, 9) && this.labelText.equals(labelText) && this.tooltipText.equals(tooltipText)) {
            return;
        }

        this.labelText = labelText;
        this.tooltipText = tooltipText;

        if (gaugeStateDiv.setValues(orientationDiv)) {
            setChanged();
        }
        if (gaugeStateMod.setValues(orientationMod)) {
            setChanged();
        }

        updateTooltipText();
    }

    /**
     * Updates the tooltip's text from {@link #tooltipPrefix} ad {@link
     * #tooltipText}.
     */
    private void updateTooltipText() {
        setTooltipText(tooltipPrefix == null || tooltipText.length() == 0 ? null : tooltipPrefix+tooltipText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        gaugeStateMod.setDy(height/2);
        orientationDiv.setExtends(width, height);
        orientationMod.setExtends(width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHidden(final boolean hidden) {
        // nothing
    }
}

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
import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Displays a value as a graphical gauge that's filling state depends on the
 * value.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIGauge extends AbstractGUIElement implements GUIGaugeListener {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The tooltip prefix. It is prepended to {@link #tooltipText} to form the
     * tooltip.
     */
    @Nullable
    private final String tooltipPrefix;

    /**
     * The {@link CommandList} that is executed on button 2.
     */
    @Nullable
    private final CommandList commandList;

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
    private final Orientation orientation;

    /**
     * The gauge state.
     */
    @NotNull
    private final GaugeState gaugeState;

    /**
     * The gauge alpha value, 1 is opaque and 0 full transparent.
     */
    private final float alpha;

    /**
     * If true, the gauge will not paint itself, whatever its visibility.
     */
    private boolean hidden = false;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param fullImage the image representing a full gauge
     * @param negativeImage the image representing a more-than-empty gauge; if
     * set to <code>null</code> the gauge remains in empty state
     * @param emptyImage the image representing an empty gauge; if set to
     * <code>null</code> an empty background is used instead
     * @param orientation the gauge's orientation
     * @param tooltipPrefix the prefix for displaying tooltips; if set to
     * <code>null</code> no tooltips are shown
     * @param alpha alpha value of the gauge to use
     * @param commandList the command list that is executed on button 2
     */
    public GUIGauge(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @Nullable final Image fullImage, @Nullable final Image negativeImage, @Nullable final Image emptyImage, @NotNull final Orientation orientation, @Nullable final String tooltipPrefix, final float alpha, @Nullable final CommandList commandList) {
        super(tooltipManager, elementListener, name, alpha < 1F ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        this.emptyImage = emptyImage;
        this.orientation = orientation;
        this.tooltipPrefix = tooltipPrefix;
        this.commandList = commandList;
        gaugeState = new GaugeState(fullImage, negativeImage, 0, 0);
        this.alpha = alpha;
        tooltipText = "-";      // make sure the following setValues() does not short-cut
        orientation.setExtends(getWidth(), getHeight());
        orientation.setHasNegativeImage(negativeImage != null);
        orientation.setValues(0, 0, 0);
        gaugeState.setValues(orientation);
        updateTooltipText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        if (orientation.setExtends(width, height)) {
            if (gaugeState.setValues(orientation)) {
                setChanged();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        if (hidden) {
            return;
        }

        final Graphics paint;
        if (alpha < 1F) {
            final Graphics2D g2d = (Graphics2D)g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            paint = g2d;
        } else {
            paint = g;
        }

        super.paintComponent(paint);
        if (emptyImage != null) {
            paint.drawImage(emptyImage, 0, 0, null);
        }
        gaugeState.draw(paint);
        if (paint != g) {
            paint.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final int curValue, final int minValue, final int maxValue, @NotNull final String labelText, @NotNull final String tooltipText) {
        if (!orientation.setValues(curValue, minValue, maxValue) && this.tooltipText.equals(tooltipText)) {
            return;
        }

        this.tooltipText = tooltipText;

        if (gaugeState.setValues(orientation)) {
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
    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return gaugeState.getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return gaugeState.getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getMaximumSize() {
        return gaugeState.getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            break;

        case MouseEvent.BUTTON2:
            if (commandList != null) {
                commandList.execute();
            }
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

}

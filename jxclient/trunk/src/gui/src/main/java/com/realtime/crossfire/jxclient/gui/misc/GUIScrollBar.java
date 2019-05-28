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

package com.realtime.crossfire.jxclient.gui.misc;

import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable2;
import com.realtime.crossfire.jxclient.gui.scrollable.ScrollableListener;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A scroll bar gui element.
 * @author Andreas Kirschbaum
 */
public class GUIScrollBar extends ActivatableGUIElement implements ScrollableListener {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * If set, make the slider size reflect the visible area; if unset, display
     * the slider as a square.
     */
    private final boolean proportionalSlider;

    /**
     * The target element to scroll.
     */
    @NotNull
    private final GUIScrollable2 scrollable;

    /**
     * The background color of the slider.
     */
    @NotNull
    private final Color colorBackground;

    /**
     * The foreground color of the slider.
     */
    @NotNull
    private final Color colorForeground;

    /**
     * The minimum scroll value.
     */
    private int valueMin;

    /**
     * The size of the scroll values.
     */
    private int valueSize = 1;

    /**
     * The height of the slider.
     */
    private int sliderSize = 1;

    /**
     * The scroll location; it need not be within the scroll range.
     */
    private int sliderPos;

    /**
     * The offset between the mouse and the top border of the slider while
     * dragging.
     */
    private int offset;

    /**
     * Set while dragging is active.
     */
    private boolean scrolling;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param proportionalSlider if set, make the slider size reflect the
     * visible area; if unset, display the slider as a square
     * @param scrollable the target element to scroll
     * @param colorBackground the background color of the slider
     * @param colorForeground the foreground color of the slider
     * @param guiFactory the global GUI factory instance
     */
    public GUIScrollBar(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final boolean proportionalSlider, @NotNull final GUIScrollable2 scrollable, @NotNull final Color colorBackground, @NotNull final Color colorForeground, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, Transparency.OPAQUE, guiFactory);
        this.proportionalSlider = proportionalSlider;
        this.scrollable = scrollable;
        this.colorBackground = colorBackground;
        this.colorForeground = colorForeground;
        this.scrollable.addScrollableListener(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        scrollable.removeScrollableListener(this);
    }

    @Override
    public void setRange(final int valueMin, final int valueMax, final int sliderPos, final int sliderSize) {
        if (valueMax <= valueMin) {
            throw new IllegalArgumentException("valueMax="+valueMax+" <= "+valueMin+"="+valueMin);
        }
        if (sliderSize <= 0) {
            throw new IllegalArgumentException("sliderSize="+sliderSize+" <= 0");
        }

        this.valueMin = valueMin;
        valueSize = valueMax-valueMin;
        this.sliderSize = Math.min(sliderSize, valueSize);
        setPosition(sliderPos);
    }

    @Override
    protected void activeChanged() {
    }

    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        super.mousePressed(e);
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            final int sh = getSliderHeightPixels();
            offset = e.getY()-getSliderPosPixels(sh);
            if (offset < 0) {
                scrollable.scrollTo(sliderPos-sliderSize);
            } else if (offset >= sh) {
                scrollable.scrollTo(sliderPos+sliderSize);
            } else {
                scrolling = true;
            }
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    @Override
    public void mouseReleased(@NotNull final MouseEvent e) {
        super.mouseReleased(e);
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            scrolling = false;
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    @Override
    public void mouseDragged(@NotNull final MouseEvent e) {
        super.mouseDragged(e);
        if (scrolling) {
            scrollable.scrollTo(getSliderPos(e.getY()-offset));
        }
    }

    @Override
    public void notifyOpen() {
    }

    @Override
    public void execute() {
        // ignore
    }

    /**
     * Returns the current slider position in slider-coordinates.
     * @param yPixels the y-coordinate within the gui element
     * @return the position in slider-coordinates
     */
    private int getSliderPos(final int yPixels) {
        return (int)Math.round(yPixels*(double)(valueSize-sliderSize)/(getHeight()-getSliderHeightPixels()));
    }

    /**
     * Sets the position of the slider. Invalid values are set to the nearest
     * valid value.
     * @param pos the slider position
     */
    @SuppressWarnings("IfMayBeConditional")
    private void setPosition(final int pos) {
        if (pos < valueMin) {
            sliderPos = valueMin;
        } else if (pos+sliderSize > valueMin+valueSize) {
            sliderPos = valueMin+valueSize-sliderSize;
        } else {
            sliderPos = pos;
        }
        setChanged();
    }

    /**
     * Returns the height of the slider in pixels.
     * @return the height of the slider in pixels
     */
    private int getSliderHeightPixels() {
        return proportionalSlider ? Math.max((int)Math.round(getHeight()*(double)sliderSize/valueSize), getWidth()) : getWidth();
    }

    /**
     * Returns the y-coordinate of the slider.
     * @param sh the height of the slider in pixels
     * @return the y-coordinate
     */
    private int getSliderPosPixels(final int sh) {
        return (int)Math.round(sliderPos*(double)(getHeight()-sh)/(valueSize-sliderSize));
    }

    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        final int sh = getSliderHeightPixels();
        final int sy = getSliderPosPixels(sh);
        g.setColor(colorBackground);
        g.fillRect(0, 0, getWidth(), sy);
        g.fillRect(0, sy+sh, getWidth(), getHeight()-sy-sh);
        g.setColor(colorForeground);
        g.fillRect(0, sy, getWidth(), sh);
    }

    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(16, 64);
    }

    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(16, 16);
    }

}

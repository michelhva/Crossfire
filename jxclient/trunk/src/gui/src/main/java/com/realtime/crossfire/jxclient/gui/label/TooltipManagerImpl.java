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

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.gui.TooltipText;
import com.realtime.crossfire.jxclient.util.SwingUtilities2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the tooltip display. An {@link AbstractLabel} is moved/resized to
 * display a tooltip text for an "active" GUI element.
 * @author Andreas Kirschbaum
 */
public class TooltipManagerImpl implements TooltipManager {

    /**
     * Distance of tooltip from its associated GUI element.
     */
    private static final int TOOLTIP_DISTANCE = 8;

    /**
     * The current window width.
     */
    private int windowWidth;

    /**
     * The current window height.
     */
    private int windowHeight;

    /**
     * The tooltip label. Set to {@code null} if the skin does not use
     * tooltips.
     */
    @Nullable
    private AbstractLabel tooltip;

    /**
     * The last known active gui element. It is used to suppress unnecessary
     * change events to the tooltip label.
     */
    @Nullable
    private AbstractGUIElement activeGuiElement;

    /**
     * Synchronizes accesses to {@link #activeGuiElement}.
     */
    @NotNull
    private final Object activeGuiElementSync = new Object();

    /**
     * The {@link Runnable} that implements the functionality of {@link
     * #reset()}.
     */
    @NotNull
    private final Runnable resetRunnable = new Runnable() {

        @Override
        public void run() {
            synchronized (activeGuiElementSync) {
                removeTooltip();
                activeGuiElement = null;
            }
        }

    };

    /**
     * Updates the current window size.
     * @param windowWidth the window width
     * @param windowHeight the window height
     */
    public void setScreenSize(final int windowWidth, final int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    /**
     * Sets the tooltip label.
     * @param tooltip the tooltip label, or {@code null}
     */
    public void setTooltip(@Nullable final AbstractLabel tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Removes the tooltip. Does nothing if no tooltip is active.
     */
    public void reset() {
        SwingUtilities2.invokeAndWait(resetRunnable);
    }

    @Override
    @SuppressWarnings("ObjectEquality")
    public void setElement(@NotNull final AbstractGUIElement guiElement) {
        SwingUtilities2.invokeAndWait(() -> {
            synchronized (activeGuiElementSync) {
                if (activeGuiElement == null) {
                    activeGuiElement = guiElement;
                    addTooltip();
                } else if (activeGuiElement != guiElement) {
                    removeTooltip();
                    activeGuiElement = guiElement;
                    addTooltip();
                }
            }
        });
    }

    @Override
    public void unsetElement(@NotNull final AbstractGUIElement guiElement) {
        SwingUtilities2.invokeAndWait(() -> {
            synchronized (activeGuiElementSync) {
                //noinspection ObjectEquality
                if (activeGuiElement == guiElement) {
                    removeTooltip();
                    activeGuiElement = null;
                }
            }
        });
    }

    @Override
    public void tooltipTextChanged(@NotNull final GUIElement element) {
        synchronized (activeGuiElementSync) {
            //noinspection ObjectEquality
            if (element != activeGuiElement) {
                return;
            }
        }

        SwingUtilities2.invokeAndWait(this::addTooltip);
    }

    /**
     * Adds or updates the tooltip (text and location) for the {@link
     * #activeGuiElement}.
     */
    private void addTooltip() {
        synchronized (activeGuiElementSync) {
            final AbstractGUIElement tmpActiveGuiElement = activeGuiElement;
            if (tmpActiveGuiElement == null) {
                return;
            }

            final AbstractLabel tmpTooltip = tooltip;
            if (tmpTooltip == null) {
                return;
            }

            final TooltipText tooltipText = tmpActiveGuiElement.getTooltip();
            if (tooltipText == null) {
                tmpTooltip.setVisible(false);
                return;
            }

            tmpTooltip.setVisible(true);
            tmpTooltip.setText(tooltipText.getText());

            final int preferredX = tooltipText.getX()+tooltipText.getW()/2-tmpTooltip.getWidth()/2;
            final int maxX = windowWidth-tmpTooltip.getWidth();
            final int tx = Math.max(0, Math.min(preferredX, maxX));
            final int elementY = tooltipText.getY();
            final int preferredY = elementY+tooltipText.getH()+TOOLTIP_DISTANCE;
            final int ty = preferredY+tmpTooltip.getHeight() <= windowHeight ? preferredY : elementY-tmpTooltip.getHeight()-TOOLTIP_DISTANCE;
            tmpTooltip.setLocation(tx, ty);
        }
    }

    /**
     * Removes the tooltip label. Does nothing if no tooltip is active.
     */
    private void removeTooltip() {
        if (tooltip != null) {
            tooltip.setVisible(false);
        }
    }

}

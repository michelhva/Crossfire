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
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.gui.TooltipText;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.util.SwingUtilities2;
import java.util.Map;
import java.util.WeakHashMap;
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
     * The global {@link GuiFactory} instance.
     */
    @NotNull
    private final GuiFactory guiFactory;

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
    private GUIElement activeGuiElement;

    /**
     * Synchronizes accesses to {@link #activeGuiElement}.
     */
    @NotNull
    private final Object activeGuiElementSync = new Object();

    /**
     * Maps {@link GUIElement} to associated {@link TooltipText}. The tooltip
     * text is shown when the mouse is inside this element. May be {@code null}
     * to show no tooltip.
     */
    @NotNull
    private final Map<GUIElement, TooltipText> tooltipTexts = new WeakHashMap<>();

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
     * Creates a new instance.
     * @param guiFactory the global GUI factory instance
     */
    public TooltipManagerImpl(@NotNull final GuiFactory guiFactory) {
        this.guiFactory = guiFactory;
    }

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
    public void setElement(@NotNull final AbstractGUIElement guiElement) {
        final TooltipText tooltipText = tooltipTexts.get(guiElement);
        if (tooltipText != null) {
            setTooltipText(guiElement, tooltipText.getText());
        }
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
                if (activeGuiElement == guiElement) {
                    removeTooltip();
                    activeGuiElement = null;
                }
            }
        });
    }

    @Override
    public void setTooltipText(@NotNull final AbstractGUIElement element, @Nullable final String tooltipText) {
        final Gui gui = guiFactory.getGui(element);
        if (gui != null) {
            setTooltipText(element, tooltipText, gui.getComponent().getX()+element.getX(), gui.getComponent().getY()+element.getY(), element.getWidth(), element.getHeight());
        }
    }

    @Override
    public void setTooltipText(@NotNull final GUIElement element, @Nullable final String tooltipText, final int x, final int y, final int w, final int h) {
        final TooltipText oldTooltipText = tooltipTexts.get(element);
        if (oldTooltipText == null) {
            if (tooltipText == null) {
                return;
            }
        } else {
            if (tooltipText != null && tooltipText.equals(oldTooltipText.getText()) && x == oldTooltipText.getX() && y == oldTooltipText.getY() && w == oldTooltipText.getW() && h == oldTooltipText.getH()) {
                return;
            }
        }
        tooltipTexts.put(element, tooltipText == null ? null : new TooltipText(tooltipText, x, y, w, h));

        SwingUtilities2.invokeAndWait(() -> {
            synchronized (activeGuiElementSync) {
                if (activeGuiElement == element) {
                    removeTooltip();
                    addTooltip();
                }
            }
        });
    }

    /**
     * Adds or updates the tooltip (text and location) for the {@link
     * #activeGuiElement}.
     */
    private void addTooltip() {
        final GUIElement tmpActiveGuiElement = activeGuiElement;
        assert tmpActiveGuiElement != null;
        assert Thread.holdsLock(activeGuiElementSync);

        final AbstractLabel tmpTooltip = tooltip;
        if (tmpTooltip == null) {
            return;
        }

        final TooltipText tooltipText = tooltipTexts.get(tmpActiveGuiElement);
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

    /**
     * Removes the tooltip label. Does nothing if no tooltip is active.
     */
    private void removeTooltip() {
        if (tooltip != null) {
            tooltip.setVisible(false);
        }
    }

}

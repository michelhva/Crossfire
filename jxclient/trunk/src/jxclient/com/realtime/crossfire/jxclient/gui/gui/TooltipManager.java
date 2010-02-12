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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.gui;

import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the tooltip display. An {@link AbstractLabel} is moved/resized to
 * display a tooltip text for an "active" GUI element.
 * @author Andreas Kirschbaum
 */
public class TooltipManager
{
    /**
     * Distance of tooltip from its associated GUI element.
     */
    private static final int TOOLTIP_DISTANCE = 8;

    /**
     * The {@link JXCWindowRenderer} to manage the tooltips of.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * The tooltip label. Set to <code>null</code> if the skin does not use
     * tooltips.
     */
    @Nullable
    private AbstractLabel tooltip = null;

    /**
     * The last known active gui element. It is used to suppress unnecessary
     * change events to the tooltip label.
     */
    @Nullable
    private GUIElement activeGuiElement = null;

    /**
     * Synchronizes accesses to {@link #activeGuiElement}.
     */
    @NotNull
    private final Object activeGuiElementSync = new Object();

    /**
     * Creates a new instance.
     * @param windowRenderer the window renderer to manager the tooltips of
     */
    public TooltipManager(@NotNull final JXCWindowRenderer windowRenderer)
    {
        this.windowRenderer = windowRenderer;
    }

    /**
     * Sets the tooltip label.
     * @param tooltip the tooltip label, or <code>null</code>
     */
    public void setTooltip(@Nullable final AbstractLabel tooltip)
    {
        this.tooltip = tooltip;
    }

    /**
     * Removes the tooltip. Does nothing if no tooltip is active.
     */
    public void reset()
    {
        synchronized (activeGuiElementSync)
        {
            removeTooltip();
            activeGuiElement = null;
        }
    }

    /**
     * Displays the tooltip for a GUI element.
     * @param guiElement the GUI element to show the tooltip of
     */
    public void setElement(@NotNull final GUIElement guiElement)
    {
        synchronized (activeGuiElementSync)
        {
            if (activeGuiElement == null)
            {
                activeGuiElement = guiElement;
                addTooltip();
            }
            else if (activeGuiElement != guiElement)
            {
                removeTooltip();
                activeGuiElement = guiElement;
                addTooltip();
            }
        }
    }

    /**
     * Removes the tooltip of a GUI element. Does nothing if the given GUI
     * element is not active.
     * @param guiElement the gui element to remove the tooltip of
     */
    public void unsetElement(@NotNull final GUIElement guiElement)
    {
        synchronized (activeGuiElementSync)
        {
            if (activeGuiElement == guiElement)
            {
                removeTooltip();
                activeGuiElement = null;
            }
        }
    }

    /**
     * Updates the tooltip text of a GUI element. Does nothing if the given GUI
     * element is not active.
     * @param guiElement the gui element to process
     */
    public void updateElement(@NotNull final GUIElement guiElement)
    {
        synchronized (activeGuiElementSync)
        {
            if (activeGuiElement == guiElement)
            {
                removeTooltip();
                addTooltip();
            }
        }
    }

    /**
     * Adds or updates the tooltip (text and location) for the {@link
     * #activeGuiElement}.
     */
    private void addTooltip()
    {
        assert activeGuiElement != null;
        assert Thread.holdsLock(activeGuiElementSync);

        if (tooltip == null)
        {
            return;
        }

        final String tooltipText = activeGuiElement.getTooltipText();
        if (tooltipText == null)
        {
            tooltip.setElementVisible(false);
            return;
        }

        tooltip.setElementVisible(true);
        tooltip.setText(tooltipText);

        final int tx = Math.max(0, Math.min(activeGuiElement.getElementX()+activeGuiElement.getWidth()/2-tooltip.getWidth()/2, windowRenderer.getWindowWidth()-tooltip.getWidth()));
        final int ty;
        if (activeGuiElement.getElementY()+activeGuiElement.getHeight()+TOOLTIP_DISTANCE+tooltip.getHeight() <= windowRenderer.getWindowHeight())
        {
            ty = activeGuiElement.getElementY()+activeGuiElement.getHeight()+TOOLTIP_DISTANCE;
        }
        else
        {
            ty = activeGuiElement.getElementY()-tooltip.getHeight()-TOOLTIP_DISTANCE;
        }
        tooltip.setElementLocation(tx, ty);
    }

    /**
     * Removes the tooltip label. Does nothing if no tooltip is active.
     */
    private void removeTooltip()
    {
        if (tooltip != null)
        {
            tooltip.setElementVisible(false);
        }
    }
}

//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient;

import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUILabel;

/**
 * Manages the tooltip display.
 *
 * @author Andreas Kirschbaum
 */
public class TooltipManager
{
    /**
     * Distance of tooltip from this element.
     */
    public static final int TOOLTIP_DISTANCE = 8;

    /**
     * The {@link JXCWindow} to manage to tooltips of.
     */
    private final JXCWindow jxcWindow;

    /**
     * The last known active gui element. It is used to suppress unnecessary
     * change events to the tooltip label.
     */
    private GUIElement activeGuiElement = null;

    /**
     * Create a new instance.
     *
     * @param jxcWindow The window to manage the tooltips of.
     */
    public TooltipManager(final JXCWindow jxcWindow)
    {
        this.jxcWindow = jxcWindow;
    }

    /**
     * Remove the tooltip. If no tooltip is active do nothing.
     */
    public void reset()
    {
        if (activeGuiElement != null)
        {
            removeTooltip();
            activeGuiElement = null;
        }
    }

    /**
     * Display the tooltip for a gui element.
     *
     * @param guiElement The gui element to show the tooltip of.
     */
    public void setElement(final GUIElement guiElement)
    {
        assert guiElement != null;
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

    /**
     * Remove the tooltip of a gui element.
     *
     * @param guiElement The gui element to remove the tooltip of.
     */
    public void unsetElement(final GUIElement guiElement)
    {
        assert guiElement != null;
        if (activeGuiElement == guiElement)
        {
            removeTooltip();
            activeGuiElement = null;
        }
    }

    /**
     * Update the tooltip text of a gui element.
     *
     * @param guiElement The gui element to process.
     */
    public void updateElement(final GUIElement guiElement)
    {
        assert guiElement != null;
        if (activeGuiElement == guiElement)
        {
            removeTooltip();
            addTooltip();
        }
    }

    /**
     * Add or update the tooltip (text and location) for the {@link
     * #activeGuiElement}.
     */
    private void addTooltip()
    {
        assert activeGuiElement != null;

        final GUILabel tooltip = jxcWindow.getTooltip();
        if (tooltip == null)
        {
            return;
        }

        final String tooltipText = activeGuiElement.getTooltipText();
        if (tooltipText == null)
        {
            tooltip.setVisible(false);
            return;
        }

        tooltip.setVisible(true);
        tooltip.setText(tooltipText);

        final int tx = Math.max(0, Math.min(activeGuiElement.getX()+activeGuiElement.getWidth()/2-tooltip.getWidth()/2, jxcWindow.getWindowWidth()-tooltip.getWidth()));
        final int ty;
        if (activeGuiElement.getY()+activeGuiElement.getHeight()+TOOLTIP_DISTANCE+tooltip.getHeight() <= jxcWindow.getWindowHeight())
        {
            ty = activeGuiElement.getY()+activeGuiElement.getHeight()+TOOLTIP_DISTANCE;
        }
        else
        {
            ty = activeGuiElement.getY()-tooltip.getHeight()-TOOLTIP_DISTANCE;
        }
        tooltip.setLocation(tx, ty);
    }

    /**
     * Remove the tooltip label. Do nothing if no tooltip is active.
     */
    private void removeTooltip()
    {
        final GUILabel tooltip = jxcWindow.getTooltip();
        if (tooltip != null)
        {
            tooltip.setVisible(false);
        }
    }
}

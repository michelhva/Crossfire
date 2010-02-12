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

package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A dialog title that allows to move the dialog.
 *
 * @author Andreas Kirschbaum
 */
public class GUIDialogTitle extends GUIPicture
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link JXCWindowRenderer} this element belongs to.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * Set to the distance of the dialog cordinates relative to the mouse
     * position while dragging start. Else set to <code>null</code>.
     */
    @Nullable
    private Point offset = null;

    /**
     * Create a new instance.
     *
     * @param tooltipManager the tooltip manager to update
     *
     * @param windowRenderer the window renderer to notify
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param y The y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param image The picture to paint.
     *
     * @param alpha The transparency value.
     */
    public GUIDialogTitle(@NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final BufferedImage image, final float alpha)
    {
        super(tooltipManager, windowRenderer, name, x, y, w, h, image, alpha);
        this.windowRenderer = windowRenderer;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /* {@inheritDoc} */
    @Override
    public void mousePressed(@NotNull final MouseEvent e)
    {
        super.mousePressed(e);
        final Gui gui = getGui();
        if (gui == null)
        {
            offset = null;
            return;
        }

        final Point point = e.getLocationOnScreen();
        offset = new Point(gui.getX()-point.x, gui.getY()-point.y);
    }

    /* {@inheritDoc} */
    @Override
    public void mouseReleased(@NotNull final MouseEvent e)
    {
        super.mouseReleased(e);
        moveTo(e);
        offset = null;
    }

    /* {@inheritDoc} */
    @Override
    public void mouseDragged(@NotNull final MouseEvent e)
    {
        super.mouseDragged(e);
        moveTo(e);
    }

    /**
     * Move the dialog the given point.
     *
     * @param e The destination point.
     */
    private void moveTo(@NotNull final MouseEvent e)
    {
        if (offset == null)
        {
            return;
        }

        final Gui gui = getGui();
        if (gui == null)
        {
            offset = null;
            return;
        }

        final Point point = e.getLocationOnScreen();
        final int newX = Math.max(Math.min(point.x+offset.x, windowRenderer.getWindowWidth()-gui.getWidth()), 0);
        final int newY = Math.max(Math.min(point.y+offset.y, windowRenderer.getWindowHeight()-gui.getHeight()), 0);
        gui.setPosition(newX, newY);
    }
}

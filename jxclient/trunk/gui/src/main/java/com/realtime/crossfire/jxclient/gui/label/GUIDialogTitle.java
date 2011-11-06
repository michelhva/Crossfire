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
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A dialog title that allows to move the dialog.
 * @author Andreas Kirschbaum
 */
public class GUIDialogTitle extends GUIOneLineLabel {

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
     * Set to the distance of the dialog coordinates relative to the mouse
     * position while dragging start. Else set to <code>null</code>.
     */
    @Nullable
    private Point offset = null;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer this element belongs to
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param textFont the font for rendering the label text
     * @param textColor the font color
     * @param backgroundColor the background color
     * @param title the title text
     */
    public GUIDialogTitle(@NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Font textFont, @NotNull final Color textColor, @Nullable final Color backgroundColor, @NotNull final String title) {
        super(tooltipManager, elementListener, name, null, textFont, textColor, backgroundColor, Alignment.LEFT, title);
        this.windowRenderer = windowRenderer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        super.mousePressed(e);
        final Component gui = GuiUtils.getGui(this);
        if (gui == null) {
            offset = null;
            return;
        }

        final Point point = e.getLocationOnScreen();
        offset = new Point(gui.getX()-point.x, gui.getY()-point.y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(@NotNull final MouseEvent e) {
        super.mouseReleased(e);
        moveTo(e);
        offset = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(@NotNull final MouseEvent e) {
        super.mouseDragged(e);
        moveTo(e);
    }

    /**
     * Moves the dialog the given point.
     * @param e the destination point
     */
    private void moveTo(@NotNull final MouseEvent e) {
        final Point tmpOffset = offset;
        if (tmpOffset == null) {
            return;
        }

        final Gui gui = GuiUtils.getGui(this);
        if (gui == null || gui.isAutoSize()) {
            offset = null;
            return;
        }

        final Point point = e.getLocationOnScreen();
        gui.showDialog(point.x+tmpOffset.x, point.y+tmpOffset.y, windowRenderer.getWindowWidth(), windowRenderer.getWindowHeight());
    }

}

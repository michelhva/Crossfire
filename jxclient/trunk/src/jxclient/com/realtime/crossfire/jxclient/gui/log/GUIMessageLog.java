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

package com.realtime.crossfire.jxclient.gui.log;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 * A gui element implementing the message window.
 * @author Andreas Kirschbaum
 */
public class GUIMessageLog extends GUILog
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The message buffer updater for updating {@link #buffer}.
     */
    @NotNull
    private final MessageBufferUpdater messageBufferUpdater;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen
     * @param y the y-coordinate for drawing this element to screen
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param crossfireServerConnection the connection instance
     * @param backgroundImage the background image; may be <code>null</code> if
     * unused
     * @param fonts the <code>Fonts</code> instance for looking up fonts
     * @param defaultColor the default color to use for text message not
     * specifying a color
     */
    public GUIMessageLog(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final Image backgroundImage, @NotNull final Fonts fonts, @NotNull final Color defaultColor)
    {
        super(tooltipManager, elementListener, name, x, y, w, h, backgroundImage, fonts);
        messageBufferUpdater = new MessageBufferUpdater(crossfireServerConnection, buffer, defaultColor);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
        messageBufferUpdater.dispose();
    }

    /**
     * Sets a color mapping.
     * @param index the color index to change
     * @param color the color to map to
     */
    public void setColor(final int index, @NotNull final Color color)
    {
        messageBufferUpdater.setColor(index, color);
    }

    /**
     * Sets the message types to show.
     * @param types the types to show
     */
    public void setTypes(final int types)
    {
        messageBufferUpdater.setTypes(types);
    }
}

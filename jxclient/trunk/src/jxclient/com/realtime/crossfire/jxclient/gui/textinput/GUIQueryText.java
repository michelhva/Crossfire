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

package com.realtime.crossfire.jxclient.gui.textinput;

import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 * Input field for "query" dialogs.
 *
 * @author Andreas Kirschbaum
 */
public class GUIQueryText extends GUIText
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link CrossfireServerConnection} for sendig reply commands.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * Creates a new instance.
     * @param server the crossfire server connection for sending reply commands
     * @param commandCallback the command callback to use
     * @param elementListener the element listener to notify
     */
    public GUIQueryText(@NotNull final CrossfireServerConnection server, @NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final Image activeImage, @NotNull final Image inactiveImage, @NotNull final Font font, @NotNull final Color inactiveColor, @NotNull final Color activeColor, final int margin, @NotNull final String text, final boolean ignoreUpDown)
    {
        super(commandCallback, tooltipManager, elementListener, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, text, ignoreUpDown);
        this.server = server;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /** {@inheritDoc} */
    @Override
    protected void execute(@NotNull final String command)
    {
        server.sendReply(command);
        setText("");
    }
}

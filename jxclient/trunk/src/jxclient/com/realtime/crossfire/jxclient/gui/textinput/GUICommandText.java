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

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUICommandText extends GUIText
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    @NotNull
    private final Commands commands;

    public GUICommandText(@NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final Image activeImage, @NotNull final Image inactiveImage, @NotNull final Font font, @NotNull final Color inactiveColor, @NotNull final Color activeColor, final int margin, @NotNull final String text, @NotNull final Commands commands, final boolean ignoreUpDown)
    {
        super(commandCallback, tooltipManager, elementListener, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, text, ignoreUpDown);
        this.commands = commands;
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
        commands.executeCommand(command);
        setText("");
    }
}

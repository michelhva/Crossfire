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

package com.realtime.crossfire.jxclient.gui.textinput;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIText} element that executes the entered text as a command.
 * @author Lauwenmark
 */
public class GUICommandText extends GUIText {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link CommandExecutor} for executing entered commands.
     */
    @NotNull
    private final CommandExecutor commandExecutor;

    /**
     * Creates a new instance.
     * @param commandCallback the command callback to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param activeImage the element's background image when it is active
     * @param inactiveImage the element's background image when it is inactive
     * @param font the font for rendering displayed text
     * @param inactiveColor the color for rendering displayed text when the
     * element is active; also cursor color
     * @param activeColor the color for rendering displayed text when the
     * element is active
     * @param margin the left margin in pixels
     * @param text the initially entered text
     * @param commandExecutor the command executor for executing entered
     * commands
     * @param enableHistory if set, enable access to command history
     */
    public GUICommandText(@NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Image activeImage, @NotNull final Image inactiveImage, @NotNull final Font font, @NotNull final Color inactiveColor, @NotNull final Color activeColor, final int margin, @NotNull final String text, @NotNull final CommandExecutor commandExecutor, final boolean enableHistory) {
        super(commandCallback, tooltipManager, elementListener, name, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, text, enableHistory);
        this.commandExecutor = commandExecutor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute(@NotNull final String command) {
        commandExecutor.executeCommand(command);
        setText("");
    }

}

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

package com.realtime.crossfire.jxclient.skin.factory;

import com.realtime.crossfire.jxclient.gui.button.ButtonImages;
import com.realtime.crossfire.jxclient.gui.button.GUITextButton;
import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Font;
import org.jetbrains.annotations.NotNull;

/**
 * A factory class to create "textbutton" instances.
 * @author Andreas Kirschbaum
 */
public class TextButtonFactory {

    /**
     * The images comprising the "up" button state.
     */
    @NotNull
    private final ButtonImages up;

    /**
     * The images comprising the "down" button state.
     */
    @NotNull
    private final ButtonImages down;

    /**
     * The font to use.
     */
    @NotNull
    private final Font font;

    /**
     * The text color.
     */
    @NotNull
    private final Color color;

    /**
     * The text color when selected.
     */
    @NotNull
    private final Color colorSelected;

    /**
     * Creates a new instance.
     * @param up the images comprising the "up" button state
     * @param down the images comprising the "down" button state
     * @param font the font to use
     * @param color the text color
     * @param colorSelected the text color when selected
     */
    public TextButtonFactory(@NotNull final ButtonImages up, @NotNull final ButtonImages down, @NotNull final Font font, @NotNull final Color color, @NotNull final Color colorSelected) {
        this.up = up;
        this.down = down;
        this.font = font;
        this.color = color;
        this.colorSelected = colorSelected;
    }

    /**
     * Creates a new text button.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param text the button text
     * @param autoRepeat whether the button should autorepeat while being
     * pressed
     * @param commandList the commands to execute when the button is elected
     * @return the new text button
     */
    @NotNull
    public AbstractGUIElement newTextButton(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final String text, final boolean autoRepeat, @NotNull final CommandList commandList) {
        return new GUITextButton(tooltipManager, elementListener, name, up, down, text, font, color, colorSelected, autoRepeat, commandList);
    }

}

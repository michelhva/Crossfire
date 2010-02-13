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

package com.realtime.crossfire.jxclient.gui.button;

import com.realtime.crossfire.jxclient.gui.command.GUICommandList;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.timeouts.TimeoutEvent;
import com.realtime.crossfire.jxclient.timeouts.Timeouts;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for button classes.
 *
 * @author Andreas Kirschbaum
 */
public abstract class AbstractButton extends ActivatableGUIElement
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The autorepeat delay initially.
     */
    private static final int TIMEOUT_FIRST = 350;

    /**
     * The autorepeat delay for further repeats.
     */
    private static final int TIMEOUT_SECOND = 80;

    /**
     * Whether this button should autorepeat.
     */
    private final boolean autoRepeat;

    /**
     * The commands to execute when the button is elected.
     */
    @NotNull
    private final GUICommandList commandList;

    @NotNull
    private final TimeoutEvent timeoutEvent = new TimeoutEvent()
    {
        /** {@inheritDoc} */
        @Override
        public void timeout()
        {
            execute();
            Timeouts.reset(TIMEOUT_SECOND, timeoutEvent);
        }
    };

    /**
     * Create a new instance.
     *
     * @param tooltipManager the tooltip manager to update
     *
     * @param elementListener the element listener to notify
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen.
     *
     * @param y The y-coordinate for drawing this element to screen.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param transparency The transparency value for the backing buffer
     *
     * @param autoRepeat Whether the button should autorepeat while being
     * pressed.
     *
     * @param commandList The commands to execute when the button is elected.
     */
    protected AbstractButton(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int x, final int y, final int w, final int h, final int transparency, final boolean autoRepeat, @NotNull final GUICommandList commandList)
    {
        super(tooltipManager, elementListener, name, x, y, w, h, transparency);
        if (commandList == null) throw new IllegalArgumentException();
        this.autoRepeat = autoRepeat;
        this.commandList = commandList;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /** {@inheritDoc} */
    @Override
    public void mouseReleased(@NotNull final MouseEvent e)
    {
        super.mouseReleased(e);
        final int b = e.getButton();
        switch (b)
        {
        case MouseEvent.BUTTON1:
            if (autoRepeat)
            {
                Timeouts.remove(timeoutEvent);
            }
            else
            {
                execute();
            }
            setActive(false);
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(@NotNull final MouseEvent e)
    {
        super.mousePressed(e);
        final int b = e.getButton();
        switch (b)
        {
        case MouseEvent.BUTTON1:
            setActive(true);
            if (autoRepeat)
            {
                execute();
                Timeouts.reset(TIMEOUT_FIRST, timeoutEvent);
            }
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseExited(@NotNull final MouseEvent e)
    {
        if (autoRepeat)
        {
            Timeouts.remove(timeoutEvent);
        }
        setActive(false);
    }

    /**
     * Returns whether the command actions can be executed.
     * @return whether execution is possible
     */
    public boolean canExecute()
    {
        return commandList.canExecute();
    }

    /**
     * Execute the command actions.
     */
    public void execute()
    {
        commandList.execute();
    }
}

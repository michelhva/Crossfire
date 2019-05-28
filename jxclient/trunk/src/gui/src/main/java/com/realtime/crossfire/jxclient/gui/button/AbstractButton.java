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

package com.realtime.crossfire.jxclient.gui.button;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.KeyPressedHandler;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyEvent2;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for button classes.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractButton extends ActivatableGUIElement implements KeyPressedHandler {

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
    private final CommandList commandList;

    /**
     * The {@link ActionListener} for generating autorepeat events.
     */
    @NotNull
    private final ActionListener timeoutEvent = e -> execute();

    /**
     * The {@link Timer} for auto-repeating buttons.
     */
    @NotNull
    private final Timer timer = new Timer(TIMEOUT_FIRST, timeoutEvent);

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param transparency the transparency value for the backing buffer
     * @param autoRepeat whether the button should autorepeat while being
     * pressed
     * @param commandList the commands to execute when the button is elected
     * @param guiFactory the global GUI factory instance
     */
    protected AbstractButton(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int transparency, final boolean autoRepeat, @NotNull final CommandList commandList, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, transparency, guiFactory);
        this.autoRepeat = autoRepeat;
        this.commandList = commandList;
        timer.setDelay(TIMEOUT_SECOND);
    }

    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        final int b = e.getButton();
        switch (b) {
        case MouseEvent.BUTTON1:
            if (!autoRepeat) {
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

    @Override
    public void mouseReleased(@NotNull final MouseEvent e) {
        super.mouseReleased(e);
        final int b = e.getButton();
        switch (b) {
        case MouseEvent.BUTTON1:
            if (autoRepeat) {
                timer.stop();
            }
            setActive(false);
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        super.mousePressed(e);
        final int b = e.getButton();
        switch (b) {
        case MouseEvent.BUTTON1:
            setActive(true);
            if (autoRepeat) {
                execute();
                timer.start();
            }
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
        super.mouseExited(e);
        if (autoRepeat) {
            timer.stop();
        }
        setActive(false);
    }

    @Override
    public void execute() {
        commandList.execute();
    }

    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return getMinimumSizeInt();
    }

    /**
     * Returns the minimal size needed to display this component.
     * @return the minimal size
     */
    @NotNull
    protected abstract Dimension getMinimumSizeInt();

    @Override
    public boolean keyPressed(@NotNull final KeyEvent2 e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_SPACE:
        case KeyEvent.VK_ENTER:
            execute();
            return true;
        }

        return false;
    }

}

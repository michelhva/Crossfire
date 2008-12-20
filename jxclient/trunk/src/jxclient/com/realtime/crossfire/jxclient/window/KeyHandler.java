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
package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Handles keyboard input processing.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class KeyHandler
{
    public static final int KEY_SHIFT_SHIFT = 0;

    public static final int KEY_SHIFT_CTRL = 1;

    public static final int KEY_SHIFT_ALT = 2;

    public static final int KEY_SHIFT_ALTGR = 3;

    /**
     * The {@link Writer} for logging keyboard debug output. Log nothing if
     * <code>null</code>.
     */
    private final Writer debugKeyboard;

    /**
     * The {@link KeybindingsManager} to use.
     */
    private final KeybindingsManager keybindingsManager;

    /**
     * The {@link CommandQueue} to use.
     */
    private final CommandQueue commandQueue;

    /**
     * The {@link JXCWindowRenderer} to use.
     */
    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link KeyHandlerListener} to notify.
     */
    private final KeyHandlerListener keyHandlerListener;

    /**
     * A formatter for timestamps.
     */
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");

    private final boolean[] keyShift = new boolean[] { false, false, false, false };

    private KeyBindings keyBindings = null;

    /**
     * Creates a new instance.
     * @param debugKeyboard the writer for logging keyboard debug output; may
     * @param keybindingsManager the keybindings manager to use
     * @param commandQueue the command queue to use
     * @param windowRenderer the window renderer to use
     * @param keyHandlerListener the key handler listener to notify
     */
    public KeyHandler(final Writer debugKeyboard, final KeybindingsManager keybindingsManager, final CommandQueue commandQueue, final JXCWindowRenderer windowRenderer, final KeyHandlerListener keyHandlerListener)
    {
        this.debugKeyboard = debugKeyboard;
        this.keybindingsManager = keybindingsManager;
        this.commandQueue = commandQueue;
        this.windowRenderer = windowRenderer;
        this.keyHandlerListener = keyHandlerListener;
    }

    public void reset()
    {
        Arrays.fill(keyShift, false);
    }

    public void setKeyBindings(final KeyBindings keyBindings)
    {
        this.keyBindings = keyBindings;
    }

    public boolean getKeyShift(final int keyid)
    {
        return keyShift[keyid];
    }

    public void setKeyShift(final int keyid, final boolean state)
    {
        if (keyShift[keyid] != state)
        {
            debugKeyboardWrite("setKeyShift: "+keyid+"="+state);
        }
        keyShift[keyid] = state;
    }

    private void handleKeyPress(final KeyEvent e, final JXCWindow.Status status)
    {
        if (status != JXCWindow.Status.PLAYING)
        {
            debugKeyboardWrite("handleKeyPress: ignoring key because state is not PLAYING but "+status);
            return;
        }

        if (keybindingsManager.handleKeyPress(e))
        {
            debugKeyboardWrite("keyPressed: keybindingsManager consumed key");
            return;
        }

        if (keyBindings != null && keyBindings.handleKeyPress(e))
        {
            debugKeyboardWrite("keyPressed: skin default key bindings consumed key");
            return;
        }

        if (e.getModifiers() == 0)
        {
            switch (e.getKeyCode())
            {
            case KeyEvent.VK_0:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(0);
                break;

            case KeyEvent.VK_1:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(1);
                break;

            case KeyEvent.VK_2:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(2);
                break;

            case KeyEvent.VK_3:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(3);
                break;

            case KeyEvent.VK_4:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(4);
                break;

            case KeyEvent.VK_5:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(5);
                break;

            case KeyEvent.VK_6:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(6);
                break;

            case KeyEvent.VK_7:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(7);
                break;

            case KeyEvent.VK_8:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(8);
                break;

            case KeyEvent.VK_9:
                debugKeyboardWrite("keyPressed: number key");
                commandQueue.addToRepeatCount(9);
                break;

            default:
                debugKeyboardWrite("keyPressed: ignoring key");
                break;
            }
        }
        else
        {
            debugKeyboardWrite("keyPressed: ignoring key because modifiers != 0");
        }
    }

    private void handleKeyTyped(final KeyEvent e, final JXCWindow.Status status)
    {
        if (status != JXCWindow.Status.PLAYING)
        {
            debugKeyboardWrite("handleKeyTyped: ignoring key because state is not PLAYING but "+status);
            return;
        }

        if (keybindingsManager.handleKeyTyped(e))
        {
            debugKeyboardWrite("keyTyped: keybindingsManager consumed key");
            return;
        }

        if (keyBindings != null && keyBindings.handleKeyTyped(e))
        {
            debugKeyboardWrite("keyTyped: skin default key bindings consumed key");
            return;
        }

        debugKeyboardWrite("keyTyped: ignoring key");
    }

    public void keyPressed(final KeyEvent e, final JXCWindow.Status status)
    {
        debugKeyboardWrite("pressed", e);
        try
        {
            updateModifiers(e);
            switch (e.getKeyCode())
            {
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_ALT_GRAPH:
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
                debugKeyboardWrite("keyPressed: ignoring modifier key");
                break;

            default:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    debugKeyboardWrite("keyPressed: ESC");
                    keyHandlerListener.escPressed();
                }
                else if (keybindingsManager.keyPressed(e.getKeyCode(), e.getModifiers()))
                {
                    debugKeyboardWrite("keyPressed: keybindingsManager consumed key");
                    // done
                }
                else
                {
                    for (final Gui dialog : windowRenderer.getOpenDialogs())
                    {
                        if (!dialog.isHidden(windowRenderer.getGuiState()))
                        {
                            if (dialog.handleKeyPress(e))
                            {
                                debugKeyboardWrite("keyPressed: dialog "+dialog+" consumed key");
                                return;
                            }
                            if (dialog.isModal())
                            {
                                debugKeyboardWrite("keyPressed: dialog "+dialog+" is modal");
                                return;
                            }
                            debugKeyboardWrite("keyPressed: dialog "+dialog+" didn't consume key");
                        }
                    }
                    if (windowRenderer.getCurrentGui().handleKeyPress(e))
                    {
                        debugKeyboardWrite("keyPressed: main gui "+windowRenderer.getCurrentGui()+" consumed key");
                        return;
                    }
                    handleKeyPress(e, status);
                }
                break;
            }
        }
        finally
        {
            debugKeyboardWrite("");
        }
    }

    public void keyReleased(final KeyEvent e)
    {
        debugKeyboardWrite("released", e);
        try
        {
            updateModifiers(e);
            switch (e.getKeyCode())
            {
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_ALT_GRAPH:
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
                debugKeyboardWrite("keyReleased: ignoring modifier key");
                break;

            default:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    debugKeyboardWrite("keyReleased: ignoring ESC");
                    // ignore
                }
                else if (keybindingsManager.keyReleased())
                {
                    debugKeyboardWrite("keyReleased: keybindingsManager consumed key");
                    keyHandlerListener.keyReleased();
                }
                else
                {
                    debugKeyboardWrite("keyReleased: ignoring key");
                }
                break;
            }
        }
        finally
        {
            debugKeyboardWrite("");
        }
    }

    public void keyTyped(final KeyEvent e, final JXCWindow.Status status)
    {
        debugKeyboardWrite("typed", e);
        try
        {
            if (e.getKeyChar() == 27) // ignore ESC key
            {
                debugKeyboardWrite("keyTyped: ignoring ESC");
                return;
            }

            if (keybindingsManager.keyTyped(e.getKeyChar()))
            {
                debugKeyboardWrite("keyTyped: keybindingsManager consumed key");
                commandQueue.resetRepeatCount();
            }
            else
            {
                for (final Gui dialog : windowRenderer.getOpenDialogs())
                {
                    if (!dialog.isHidden(windowRenderer.getGuiState()))
                    {
                        if (dialog.handleKeyTyped(e))
                        {
                            debugKeyboardWrite("keyTyped: dialog "+dialog+" consumed key");
                            return;
                        }
                        if (dialog.isModal())
                        {
                            debugKeyboardWrite("keyTyped: dialog "+dialog+" is modal");
                            return;
                        }
                        debugKeyboardWrite("keyTyped: dialog "+dialog+" didn't consume key");
                    }
                }
                if (windowRenderer.getCurrentGui().handleKeyTyped(e))
                {
                    debugKeyboardWrite("keyTyped: main gui "+windowRenderer.getCurrentGui()+" consumed key");
                    return;
                }
                handleKeyTyped(e, status);
            }
        }
        finally
        {
            debugKeyboardWrite("");
        }
    }

    /**
     * Update the saved modifier state from a key event.
     *
     * @param keyEvent The key event to process.
     */
    private void updateModifiers(final KeyEvent keyEvent)
    {
        final int mask = keyEvent.getModifiersEx();
        setKeyShift(KEY_SHIFT_SHIFT, (mask&InputEvent.SHIFT_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_CTRL, (mask&InputEvent.CTRL_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_ALT, (mask&InputEvent.ALT_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_ALTGR, (mask&InputEvent.ALT_GRAPH_DOWN_MASK) != 0);
        if (!getKeyShift(KEY_SHIFT_CTRL))
        {
            if (commandQueue.stopRunning())
            {
                debugKeyboardWrite("updateModifiers: stopping run");
            }
        }
    }

    /**
     * Writes a {@link KeyEvent} to the keyboard debug.
     * @param type the event type
     * @param e the key event to write
     */
    private void debugKeyboardWrite(final String type, final KeyEvent e)
    {
        if (debugKeyboard == null)
        {
            return;
        }

        debugKeyboardWrite(type+" "+e);
    }

    /**
     * Writes a message to the keyboard debug.
     * @param message the message to write
     */
    private void debugKeyboardWrite(final String message)
    {
        if (debugKeyboard == null)
        {
            return;
        }

        try
        {
            debugKeyboard.append(simpleDateFormat.format(new Date()));
            debugKeyboard.append(message);
            debugKeyboard.append("\n");
            debugKeyboard.flush();
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write keyboard debug: "+ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }
}

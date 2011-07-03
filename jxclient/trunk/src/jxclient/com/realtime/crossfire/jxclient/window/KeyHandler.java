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

package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles keyboard input processing.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class KeyHandler {

    /**
     * The key index for SHIFT.
     */
    private static final int KEY_SHIFT_SHIFT = 0;

    /**
     * The key index for CTRL.
     */
    private static final int KEY_SHIFT_CTRL = 1;

    /**
     * The key index for ALT.
     */
    private static final int KEY_SHIFT_ALT = 2;

    /**
     * The key index for ALT-GR.
     */
    private static final int KEY_SHIFT_ALT_GR = 3;

    /**
     * The {@link Writer} for logging keyboard debug output. Log nothing if
     * <code>null</code>.
     */
    @Nullable
    private final Writer debugKeyboard;

    /**
     * The {@link KeybindingsManager} to use.
     */
    @NotNull
    private final KeybindingsManager keybindingsManager;

    /**
     * The {@link CommandQueue} to use.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The {@link JXCWindowRenderer} to use.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link KeyHandlerListener} to notify.
     */
    @NotNull
    private final KeyHandlerListener keyHandlerListener;

    /**
     * A formatter for timestamps.
     */
    @NotNull
    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");

    /**
     * The current modifier states. Maps key index to state.
     */
    @NotNull
    private final boolean[] keyShift = {
        false,
        false,
        false,
        false
    };

    /**
     * The active {@link KeyBindings}. Set to <code>null</code> when no key
     * bindings are active.
     */
    @Nullable
    private KeyBindings keyBindings = null;

    /**
     * Creates a new instance.
     * @param debugKeyboard the writer for logging keyboard debug output; may
     * @param keybindingsManager the keybindings manager to use
     * @param commandQueue the command queue to use
     * @param windowRenderer the window renderer to use
     * @param keyHandlerListener the key handler listener to notify
     */
    public KeyHandler(@Nullable final Writer debugKeyboard, @NotNull final KeybindingsManager keybindingsManager, @NotNull final CommandQueue commandQueue, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final KeyHandlerListener keyHandlerListener) {
        this.debugKeyboard = debugKeyboard;
        this.keybindingsManager = keybindingsManager;
        this.commandQueue = commandQueue;
        this.windowRenderer = windowRenderer;
        this.keyHandlerListener = keyHandlerListener;
    }

    /**
     * Resets the modifier states.
     */
    public void reset() {
        Arrays.fill(keyShift, false);
    }

    /**
     * Sets the active {@link KeyBindings}.
     * @param keyBindings the key bindings or <code>null</code> to unset
     */
    @SuppressWarnings("NullableProblems")
    public void setKeyBindings(@NotNull final KeyBindings keyBindings) {
        this.keyBindings = keyBindings;
    }

    /**
     * Returns the modifier state.
     * @param keyId the key index
     * @return the modifier state for <code>keyId</code>
     */
    private boolean getKeyShift(final int keyId) {
        return keyShift[keyId];
    }

    /**
     * Sets the modifier state.
     * @param keyId the key index
     * @param state the modifier state for <code>keyId</code>
     */
    private void setKeyShift(final int keyId, final boolean state) {
        if (keyShift[keyId] != state) {
            debugKeyboardWrite("setKeyShift: "+keyId+"="+state);
        }
        keyShift[keyId] = state;
    }

    /**
     * Handles a "key pressed" event.
     * @param e the key event to handle
     */
    private void handleKeyPress(@NotNull final KeyEvent e) {
        updateModifiers(e);

        switch (e.getKeyCode()) {
        case KeyEvent.VK_ALT:
        case KeyEvent.VK_ALT_GRAPH:
        case KeyEvent.VK_SHIFT:
        case KeyEvent.VK_CONTROL:
            debugKeyboardWrite("keyPressed: ignoring modifier key");
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            debugKeyboardWrite("keyPressed: ESC");
            keyHandlerListener.escPressed();
            return;
        }

        if (keybindingsManager.keyPressed(e.getKeyCode(), e.getModifiers())) {
            debugKeyboardWrite("keyPressed: keybindingsManager consumed key");
            return;
        }

        for (final Gui dialog : windowRenderer.getOpenDialogs()) {
            if (!dialog.isHidden(windowRenderer.getGuiState())) {
                if (dialog.handleKeyPress(e)) {
                    debugKeyboardWrite("keyPressed: dialog "+dialog+" consumed key");
                    return;
                }
                if (dialog.isModal()) {
                    debugKeyboardWrite("keyPressed: dialog "+dialog+" is modal");
                    return;
                }
                debugKeyboardWrite("keyPressed: dialog "+dialog+" didn't consume key");
            }
        }

        if (windowRenderer.handleKeyPress(e)) {
            debugKeyboardWrite("keyPressed: main gui consumed key");
            return;
        }

        if (keybindingsManager.handleKeyPress(e)) {
            debugKeyboardWrite("keyPressed: keybindingsManager consumed key");
            return;
        }

        if (keyBindings != null && keyBindings.handleKeyPress(e)) {
            debugKeyboardWrite("keyPressed: skin default key bindings consumed key");
            return;
        }

        if (e.getModifiers() == 0) {
            switch (e.getKeyCode()) {
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
            return;
        }

        debugKeyboardWrite("keyPressed: ignoring key because modifiers != 0");
    }

    /**
     * Handles a "key released" event.
     * @param e the key event to handle
     */
    private void handleKeyRelease(@NotNull final KeyEvent e) {
        updateModifiers(e);

        switch (e.getKeyCode()) {
        case KeyEvent.VK_ALT:
        case KeyEvent.VK_ALT_GRAPH:
        case KeyEvent.VK_SHIFT:
        case KeyEvent.VK_CONTROL:
            debugKeyboardWrite("keyReleased: ignoring modifier key");
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            debugKeyboardWrite("keyReleased: ignoring ESC");
            return;
        }

        if (keybindingsManager.keyReleased()) {
            debugKeyboardWrite("keyReleased: keybindingsManager consumed key");
            keyHandlerListener.keyReleased();
            return;
        }

        debugKeyboardWrite("keyReleased: ignoring key");
    }

    /**
     * Handles a "key pressed" event.
     * @param e the key event to handle
     */
    public void keyPressed(@NotNull final KeyEvent e) {
        debugKeyboardWrite("pressed", e);
        try {
            handleKeyPress(e);
        } finally {
            debugKeyboardWrite("");
        }
    }

    /**
     * Handles a "key released" event.
     * @param e the key event to handle
     */
    public void keyReleased(@NotNull final KeyEvent e) {
        debugKeyboardWrite("released", e);
        try {
            handleKeyRelease(e);
        } finally {
            debugKeyboardWrite("");
        }
    }

    /**
     * Updates the saved modifier state from a key event.
     * @param keyEvent the key event to process
     */
    private void updateModifiers(@NotNull final InputEvent keyEvent) {
        final int mask = keyEvent.getModifiersEx();
        setKeyShift(KEY_SHIFT_SHIFT, (mask&InputEvent.SHIFT_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_CTRL, (mask&InputEvent.CTRL_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_ALT, (mask&InputEvent.ALT_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_ALT_GR, (mask&InputEvent.ALT_GRAPH_DOWN_MASK) != 0);
        if (!getKeyShift(KEY_SHIFT_CTRL) && commandQueue.stopRunning()) {
            debugKeyboardWrite("updateModifiers: stopping run");
        }
    }

    /**
     * Writes a {@link KeyEvent} to the keyboard debug.
     * @param type the event type
     * @param e the key event to write
     */
    private void debugKeyboardWrite(@NotNull final String type, @NotNull final KeyEvent e) {
        if (debugKeyboard == null) {
            return;
        }

        debugKeyboardWrite(type+" "+e);
    }

    /**
     * Writes a message to the keyboard debug.
     * @param message the message to write
     */
    private void debugKeyboardWrite(@NotNull final CharSequence message) {
        if (debugKeyboard == null) {
            return;
        }

        try {
            debugKeyboard.append(simpleDateFormat.format(new Date()));
            debugKeyboard.append(message);
            debugKeyboard.append("\n");
            debugKeyboard.flush();
        } catch (final IOException ex) {
            System.err.println("Cannot write keyboard debug: "+ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

}

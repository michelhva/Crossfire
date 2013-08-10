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
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyEvent2;
import com.realtime.crossfire.jxclient.gui.keybindings.KeybindingsManager;
import com.realtime.crossfire.jxclient.gui.misc.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
     * The active {@link KeyBindings}. Set to <code>null</code> when no key
     * bindings are active.
     */
    @Nullable
    private KeyBindings keyBindings;

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
     * Sets the active {@link KeyBindings}.
     * @param keyBindings the key bindings or <code>null</code> to unset
     */
    @SuppressWarnings("NullableProblems")
    public void setKeyBindings(@NotNull final KeyBindings keyBindings) {
        this.keyBindings = keyBindings;
    }

    /**
     * Handles a "key pressed" event.
     * @param e the key event to handle
     */
    private void handleKeyPress(@NotNull final KeyEvent2 e) {
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

        if (keybindingsManager.keyPressed(e)) {
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

        if (e.getModifiers() == KeyEvent2.NONE) {
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
    private void handleKeyRelease(@NotNull final KeyEvent2 e) {
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
        final KeyEvent2 keyEvent = new KeyEvent2(e.getKeyCode(), e.getKeyChar(), e.getModifiers());
        debugKeyboardWrite("pressed", e, keyEvent);
        try {
            handleKeyPress(keyEvent);
        } finally {
            debugKeyboardWrite("");
        }
    }

    /**
     * Handles a "key released" event.
     * @param e the key event to handle
     */
    public void keyReleased(@NotNull final KeyEvent e) {
        final KeyEvent2 keyEvent = new KeyEvent2(e.getKeyCode(), e.getKeyChar(), e.getModifiers());
        debugKeyboardWrite("released", e, keyEvent);
        try {
            handleKeyRelease(keyEvent);
        } finally {
            debugKeyboardWrite("");
        }
    }

    /**
     * Updates the saved modifier state from a key event.
     * @param keyEvent the key event to process
     */
    private void updateModifiers(@NotNull final KeyEvent2 keyEvent) {
        if ((keyEvent.getModifiers()&KeyEvent2.CTRL) == 0 && commandQueue.stopRunning()) {
            debugKeyboardWrite("updateModifiers: stopping run");
        }
    }

    /**
     * Writes a {@link KeyEvent} to the keyboard debug.
     * @param type the event type
     * @param keyEvent the key event to write
     * @param keyEvent2 the key event to write
     */
    private void debugKeyboardWrite(@NotNull final String type, @NotNull final KeyEvent keyEvent, @NotNull final KeyEvent2 keyEvent2) {
        if (debugKeyboard == null) {
            return;
        }

        debugKeyboardWrite(type+" "+keyEvent2+" "+keyEvent);
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

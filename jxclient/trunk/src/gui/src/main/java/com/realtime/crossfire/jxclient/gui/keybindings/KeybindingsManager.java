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

package com.realtime.crossfire.jxclient.gui.keybindings;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.commandlist.GUICommandFactory;
import com.realtime.crossfire.jxclient.settings.Filenames;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages key bindings.
 * @author Andreas Kirschbaum
 */
public class KeybindingsManager {

    /**
     * The {@link GUICommandFactory} for creating commands.
     */
    @NotNull
    private final GUICommandFactory guiCommandFactory;

    /**
     * The global key bindings.
     */
    @NotNull
    private final KeyBindings keyBindings;

    /**
     * The key bindings for the current user. Set to {@code null} if no user is
     * logged in.
     */
    @Nullable
    private KeyBindings characterKeyBindings;

    /**
     * The current key binding state. Set to {@code null} if no key binding
     * dialog is active.
     */
    @Nullable
    private KeyBindingState keyBindingState;

    /**
     * Creates a new instance.
     * @param keybindingsFile the global keybindings file
     * @param guiCommandFactory the gui command factory for creating commands
     */
    public KeybindingsManager(@NotNull final File keybindingsFile, @NotNull final GUICommandFactory guiCommandFactory) {
        this.guiCommandFactory = guiCommandFactory;
        keyBindings = new KeyBindings(keybindingsFile, guiCommandFactory);
    }

    /**
     * Starts to remove a key binding.
     * @param perCharacter whether a per-character key binding should be
     * removed
     * @return whether the key bindings dialog should be opened
     */
    public boolean removeKeyBinding(final boolean perCharacter) {
        if (perCharacter && characterKeyBindings == null) {
            return false;
        }

        keyBindingState = new KeyBindingState(characterKeyBindings, perCharacter ? null : keyBindings, null);
        return true;
    }

    /**
     * Should be called when the main window is closing.
     * @return whether the key bindings dialog should be closed
     */
    public boolean windowClosing() {
        if (keyBindingState == null) {
            return false;
        }

        keyBindingState = null;
        return true;
    }

    /**
     * Starts creating a new key binding.
     * @param perCharacter whether a per-character key bindings should be
     * created
     * @param cmdList the commands for the key binding
     * @return whether the key bindings dialog should be opened
     */
    public boolean createKeyBinding(final boolean perCharacter, @NotNull final CommandList cmdList) {
        final KeyBindings bindings = getKeyBindings(perCharacter);
        if (bindings == null) {
            return false;
        }

        keyBindingState = new KeyBindingState(bindings, null, cmdList);
        return true;
    }

    /**
     * Returns the active key bindings.
     * @param perCharacter if set, return the per-character key bindings; else
     * return the global bindings
     * @return the key bindings or {@code null} if no per-character bindings
     * exist because no character is logged in
     */
    @Nullable
    private KeyBindings getKeyBindings(final boolean perCharacter) {
        return perCharacter ? characterKeyBindings : keyBindings;
    }

    /**
     * Loads the per-character key bindings. This function should be called when
     * a character logs in.
     * @param hostname the character's hostname
     * @param character the character's name
     */
    public void loadPerCharacterBindings(@NotNull final CharSequence hostname, @NotNull final CharSequence character) {
        try {
            characterKeyBindings = new KeyBindings(Filenames.getKeybindingsFile(hostname, character), guiCommandFactory);
        } catch (final IOException ex) {
            characterKeyBindings = null;
            System.err.println("Cannot read keybindings file for "+character+" on "+hostname+": "+ex.getMessage());
            return;
        }

        try {
            characterKeyBindings.loadKeyBindings();
        } catch (final IOException ex) {
            assert characterKeyBindings != null;
            System.err.println("Cannot read keybindings file "+characterKeyBindings.getFile()+": "+ex.getMessage());
        }
    }

    /**
     * Unloads (clears and saves) the per-character key bindings. This function
     * should be called when a character logs out.
     */
    public void unloadPerCharacterBindings() {
        if (characterKeyBindings != null) {
            try {
                characterKeyBindings.saveKeyBindings();
            } catch (final IOException ex) {
                assert characterKeyBindings != null;
                System.err.println("Cannot write keybindings file "+characterKeyBindings.getFile()+": "+ex.getMessage());
            }
            characterKeyBindings = null;
        }
    }

    /**
     * Saves the key bindings to the backing file.
     */
    public void saveKeybindings() {
        try {
            keyBindings.saveKeyBindings();
        } catch (final IOException ex) {
            System.err.println("Cannot write keybindings file "+keyBindings.getFile()+": "+ex.getMessage());
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    /**
     * Loads the key bindings from the backing file.
     */
    public void loadKeybindings() {
        try {
            keyBindings.loadKeyBindings();
        } catch (final IOException ex) {
            System.err.println("Cannot read keybindings file "+keyBindings.getFile()+": "+ex.getMessage());
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    /**
     * Processes a key released event.
     * @return whether the event has been consumed
     */
    public boolean keyReleased() {
        if (keyBindingState == null) {
            return false;
        }

        if (!keyBindingState.keyReleased()) {
            return false;
        }

        keyBindingState = null;
        return true;
    }

    /**
     * Processes a key pressed event.
     * @param keyEvent the key event
     * @return whether the event has been consumed
     */
    public boolean keyPressed(@NotNull final KeyEvent2 keyEvent) {
        if (keyBindingState == null) {
            return false;
        }

        keyBindingState.keyPressed(keyEvent);
        return true;
    }

    /**
     * Processes a pressed ESC key.
     * @return whether the event has been consumed and the key bindings dialog
     * should be closed
     */
    public boolean escPressed() {
        if (keyBindingState == null) {
            return false;
        }

        keyBindingState = null;
        return true;
    }

    /**
     * Processes a key pressed event.
     * @param e the key event
     * @return whether the event has been consumed
     */
    public boolean handleKeyPress(@NotNull final KeyEvent2 e) {
        //noinspection SimplifiableIfStatement
        if (characterKeyBindings != null && characterKeyBindings.handleKeyPress(e)) {
            return true;
        }

        return keyBindings.handleKeyPress(e);
    }

    /**
     * Searches bindings having a command text starting with the specified
     * value.
     * @param command the text to search for
     * @param startOnly if true only search the text at the start of the
     * command, else anywhere.
     * @return the matching bindings
     */
    public Iterable<KeyBinding> getBindingsForPartialCommand(@NotNull final String command, final boolean startOnly) {
        final Collection<KeyBinding> matches = new HashSet<>();

        /*
         * character-specific bindings override global ones, so need to check for
         * duplicates
         */
        if (characterKeyBindings != null) {
            matches.addAll(characterKeyBindings.getBindingsForPartialCommand(command, startOnly));
        }
        final Iterable<KeyBinding> global = keyBindings.getBindingsForPartialCommand(command, startOnly);
        for (KeyBinding candidate : global) {
            boolean used = false;
            for (KeyBinding check : matches) {
                if (check.equals(candidate)) {
                    used = true;
                    break;
                }
            }
            if (!used) {
                matches.add(candidate);
            }
        }

        return matches;
    }

}

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

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindingState;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.settings.Filenames;
import java.io.IOException;
import java.awt.event.KeyEvent;

/**
 * Manages key bindings.
 * @author Andreas Kirschbaum
 */
public class KeybindingsManager
{
    /**
     * The commands instance to use.
     */
    private final Commands commands;

    /**
     * The associated window.
     */
    private final JXCWindow jxcWindow;

    /**
     * The global key bindings.
     */
    private final KeyBindings keyBindings;

    /**
     * The key bindings for the current user. Set to <code>null</code> if no
     * user is logged in.
     */
    private KeyBindings characterKeyBindings = null;

    /**
     * The current key binding state. Set to <code>null</code> if no key
     * binding dialog is active.
     */
    private KeyBindingState keyBindingState = null;

    /**
     * Creates a new instance.
     * @param commands the commands instance to use
     * @param jxcWindow the associated window
     */
    public KeybindingsManager(final Commands commands, final JXCWindow jxcWindow)
    {
        this.commands = commands;
        this.jxcWindow = jxcWindow;
        keyBindings = new KeyBindings(Filenames.getKeybindingsFile(null, null), commands, jxcWindow);
    }

    /**
     * Starts to remove a key binding.
     * @param perCharacter whether a per-character key binding should be
     * removed
     * @return whether the key bindings dialog should be opened
     */
    public boolean removeKeyBinding(final boolean perCharacter)
    {
        if (perCharacter && characterKeyBindings == null)
        {
            return false;
        }

        keyBindingState = new KeyBindingState(characterKeyBindings, perCharacter ? null : keyBindings, null);
        return true;
    }

    /**
     * Should be called when the main window is closing.
     * @return whether the key bindings dialog should be closed
     */
    public boolean windowClosing()
    {
        if (keyBindingState == null)
        {
            return false;
        }

        keyBindingState = null;
        return true;
    }

    /**
     * Starts creating a new key binding.
     * @param perCharacter whether a per-character key bindings should be
     * created
     * @param cmdlist the commands for the key binding
     * @return whether the key bindings dialog should be opened
     */
    public boolean createKeyBinding(final boolean perCharacter, final GUICommandList cmdlist)
    {
        final KeyBindings bindings = getKeyBindings(perCharacter);
        if (bindings == null)
        {
            return false;
        }

        keyBindingState = new KeyBindingState(bindings, null, cmdlist);
        return true;
    }

    /**
     * Returns the active key bindings.
     * @param perCharacter if set, return the per-character key bindings; else
     * return the global bindings
     * @return the key bindings or <code>null</code> if no per-character
     * bindings exist because no character is logged in
     */
    public KeyBindings getKeyBindings(final boolean perCharacter)
    {
        return perCharacter ? characterKeyBindings : keyBindings;
    }

    /**
     * Loads the per-character key bindings. This function should be called
     * when a character logs in.
     * @param hostname the character's hostname
     * @param character the character's name
     */
    public void loadPerCharacterBindings(final String hostname, final String character)
    {
        characterKeyBindings = new KeyBindings(Filenames.getKeybindingsFile(hostname, character), commands, jxcWindow);
        try
        {
            characterKeyBindings.loadKeyBindings(jxcWindow);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read keybindings file "+characterKeyBindings.getFile()+": "+ex.getMessage());
        }
    }

    /**
     * Unloads (clears and saves) the per-character key bindings. This function
     * should be called when a character logs out.
     */
    public void unloadPerCharacterBindings()
    {
        if (characterKeyBindings != null)
        {
            try
            {
                characterKeyBindings.saveKeyBindings();
            }
            catch (final IOException ex)
            {
                System.err.println("Cannot write keybindings file "+characterKeyBindings.getFile()+": "+ex.getMessage());
            }
            characterKeyBindings = null;
        }
    }

    /**
     * Saves the key bindings to the backing file.
     */
    public void saveKeybindings()
    {
        try
        {
            keyBindings.saveKeyBindings();
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write keybindings file "+keyBindings.getFile()+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Loads the key bindings from the backing file.
     */
    public void loadKeybindings()
    {
        try
        {
            keyBindings.loadKeyBindings(jxcWindow);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read keybindings file "+keyBindings.getFile()+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Processes a key typed event.
     * @param keyChar the character information of the key event
     * @return whether the event has been consumed
     */
    public boolean keyTyped(final char keyChar)
    {
        if (keyBindingState == null)
        {
            return false;
        }

        keyBindingState.keyTyped(keyChar);
        return true;
    }

    /**
     * Processes a key released event.
     * @return whether the event has been consumed
     */
    public boolean keyReleased()
    {
        if (keyBindingState == null)
        {
            return false;
        }

        if (!keyBindingState.keyReleased())
        {
            return false;
        }

        keyBindingState = null;
        return true;
    }

    /**
     * Processes a key pressed event.
     * @param keyCode the character code of the key event
     * @param modifiers the modifiers of the key event
     * @return whether the event has been consumed
     */
    public boolean keyPressed(final int keyCode, final int modifiers)
    {
        if (keyBindingState == null)
        {
            return false;
        }

        keyBindingState.keyPressed(keyCode, modifiers);
        return true;
    }

    /**
     * Processes a pressed ESC key.
     * @return whether the event has been consumed and the key bindings dialog
     * should be closed
     */
    public boolean escPressed()
    {
        if (keyBindingState == null)
        {
            return false;
        }

        keyBindingState = null;
        return true;
    }

    /**
     * Processes a key typed event.
     * @param e the key event
     * @return whether the event has been consumed
     */
    public boolean handleKeyTyped(final KeyEvent e)
    {
        if (characterKeyBindings != null && characterKeyBindings.handleKeyTyped(e))
        {
            return true;
        }

        if (keyBindings.handleKeyTyped(e))
        {
            return true;
        }

        return false;
    }

    /**
     * Processes a key pressed event.
     * @param e the key event
     * @return whether the event has been consumed
     */
    public boolean handleKeyPress(final KeyEvent e)
    {
        if (characterKeyBindings != null && characterKeyBindings.handleKeyPress(e))
        {
            return true;
        }

        if (keyBindings.handleKeyPress(e))
        {
            return true;
        }

        return false;
    }
}

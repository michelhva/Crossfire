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

package com.realtime.crossfire.jxclient.gui.keybindings;

import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a set of key bindings.
 *
 * @author Andreas Kirschbaum
 */
public final class KeyBindings
{
    private final List<KeyBinding> keybindings = new ArrayList<KeyBinding>();

    /**
     * Whether the contents of {@link #keybindings} have been modified from the
     * last saved state.
     */
    private boolean modified = false;

    /**
     * The file for saving the bindings; <code>null</code> to not save.
     */
    private final File file;

    /**
     * The key code map to use. Set to <code>null</code> until first use.
     */
    private KeyCodeMap keyCodeMap = null;

    /**
     * Create a new instance.
     *
     * @param file The file for saving the bindings; <code>null</code> to not
     * save.
     */
    public KeyBindings(final File file)
    {
        this.file = file;
    }

    /**
     * Return the file for saving the bindings; <code>null</code> to not save.
     *
     * @return The file.
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Add a key binding for a key code/modifiers pair.
     *
     * @param keyCode The key code for the key binding.
     *
     * @param modifiers The modifiers for the key binding.
     *
     * @param cmdlist The commands to associate to the key binding.
     *
     * @param isDefault Whether the key binding is a "default" binding which
     * should not be saved.
     */
    public void addKeyBindingAsKeyCode(final int keyCode, final int modifiers, final GUICommandList cmdlist, final boolean isDefault)
    {
        addKeyBinding(new KeyCodeKeyBinding(keyCode, modifiers, cmdlist, isDefault));
    }

    /**
     * Add a key binding for a key character.
     *
     * @param keyChar The key character for the key binding.
     *
     * @param cmdlist The commands to associate to the key binding.
     *
     * @param isDefault Whether the key binding is a "default" binding which
     * should not be saved.
     */
    public void addKeyBindingAsKeyChar(final char keyChar, final GUICommandList cmdlist, final boolean isDefault)
    {
        addKeyBinding(new KeyCharKeyBinding(keyChar, cmdlist, isDefault));
    }

    /**
     * Add (or replace) a key binding.
     * @param keyBinding the key binding
     */
    private void addKeyBinding(final KeyBinding keyBinding)
    {
        KeyBinding elected = null;
        for (final KeyBinding ok : keybindings)
        {
            if (ok.equals(keyBinding))
            {
                elected = ok;
            }
        }
        if (elected != null)
        {
            keybindings.remove(elected);
        }
        keybindings.add(keyBinding);
        modified = true;
    }

    /**
     * Remove a key binding for a key code/modifiers pair.
     *
     * @param keyCode The key code of the key binding.
     *
     * @param modifiers The modifiers of the key binding.
     */
    public void deleteKeyBindingAsKeyCode(final int keyCode, final int modifiers)
    {
        deleteKeyBinding(getKeyBindingAsKeyCode(keyCode, modifiers));
    }

    /**
     * Remove a key binding for a key character.
     *
     * @param keyChar The key character of the key binding.
     */
    public void deleteKeyBindingAsKeyChar(final char keyChar)
    {
        deleteKeyBinding(getKeyBindingAsKeyChar(keyChar));
    }

    /**
     * Removes a key binding.
     * @param keyBinding the key binding; may be <code>null</code>
     */
    private void deleteKeyBinding(final KeyBinding keyBinding)
    {
        if (keyBinding != null)
        {
            keybindings.remove(keyBinding);
            modified = true;
        }
    }

    /**
     * Load the key bindings from the given file.
     *
     * @param jxcWindow The window to execute the commands in.
     *
     * @throws IOException If the file cannot be read.
     */
    public void loadKeyBindings(final JXCWindow jxcWindow) throws IOException
    {
        modified = false;

        if (file == null)
        {
            return;
        }

        try
        {
            final FileInputStream fis = new FileInputStream(file);
            try
            {
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                try
                {
                    final LineNumberReader lnr = new LineNumberReader(isr);
                    try
                    {
                        for (;;)
                        {
                            final String line = lnr.readLine();
                            if (line == null)
                            {
                                break;
                            }

                            try
                            {
                                parseKeyBinding(line, jxcWindow, false);
                            }
                            catch (final InvalidKeyBinding ex)
                            {
                                System.err.println("ignoring invalid key binding ("+ex.getMessage()+"): "+line);
                            }
                        }
                    }
                    finally
                    {
                        lnr.close();
                    }
                }
                finally
                {
                    isr.close();
                }
            }
            finally
            {
                fis.close();
            }
        }
        catch (final FileNotFoundException ex)
        {
            // no error message
            keybindings.clear();
        }
        catch (final IOException ex)
        {
            keybindings.clear();
            modified = false;
            throw ex;
        }

        modified = false;
    }

    /**
     * Save the key bindings to the given file.
     *
     * @throws IOException If the file cannot be written.
     */
    public void saveKeyBindings() throws IOException
    {
        if (file == null || !modified)
        {
            return;
        }

        if (keybindings.size() <= 0)
        {
            file.delete();
            return;
        }

        final FileOutputStream fos = new FileOutputStream(file);
        try
        {
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            try
            {
                final BufferedWriter bw = new BufferedWriter(osw);
                try
                {
                    for (final KeyBinding keyBinding : keybindings)
                    {
                        if (keyBinding.isDefault())
                        {
                            // ignore
                        }
                        else if (keyBinding instanceof KeyCodeKeyBinding)
                        {
                            if (keyCodeMap == null)
                            {
                                keyCodeMap = new KeyCodeMap();
                            }

                            final KeyCodeKeyBinding keyCodeKeyBinding = (KeyCodeKeyBinding)keyBinding;
                            bw.write("code ");
                            bw.write(keyCodeMap.getKeyName(keyCodeKeyBinding.getKeyCode()));
                            bw.write(' ');
                            bw.write(Integer.toString(keyCodeKeyBinding.getModifiers()));
                            bw.write(' ');
                            bw.write(keyCodeKeyBinding.getCommandString());
                            bw.newLine();
                        }
                        else if (keyBinding instanceof KeyCharKeyBinding)
                        {
                            final KeyCharKeyBinding keyCharKeyBinding = (KeyCharKeyBinding)keyBinding;
                            bw.write("char ");
                            bw.write(Integer.toString(keyCharKeyBinding.getKeyChar()));
                            bw.write(' ');
                            bw.write(keyCharKeyBinding.getCommandString());
                            bw.newLine();
                        }
                        else
                        {
                            throw new AssertionError("Cannot encode "+keyBinding.getClass().getName());
                        }
                    }
                }
                finally
                {
                    bw.close();
                }
            }
            finally
            {
                osw.close();
            }
        }
        finally
        {
            fos.close();
        }
    }

    /**
     * Find a key binding associated to a key code/modifiers pair.
     *
     * @param keyCode The key code to look up.
     *
     * @param modifiers The modifiers to look up.
     *
     * @return The key binding, or <code>null</code> if no key binding is
     * associated.
     */
    private KeyBinding getKeyBindingAsKeyCode(final int keyCode, final int modifiers)
    {
        for (final KeyBinding keyBinding : keybindings)
        {
            if (keyBinding.matchesKeyCode(keyCode, modifiers))
            {
                return keyBinding;
            }
        }

        return null;
    }

    /**
     * Find a key binding associated to a key character.
     *
     * @param keyChar The key character to look up.
     *
     * @return The key binding, or <code>null</code> if no key binding is
     * associated.
     */
    private KeyBinding getKeyBindingAsKeyChar(final char keyChar)
    {
        for (final KeyBinding keyBinding : keybindings)
        {
            if (keyBinding.matchesKeyChar(keyChar))
            {
                return keyBinding;
            }
        }

        return null;
    }

    /**
     * Parse and add a key binding.
     *
     * @param line The key binding to parse.
     *
     * @param jxcWindow The window to add the key binding to.
     *
     * @param isDefault Whether the key binding is a "default" binding which
     * should not be saved.
     *
     * @throws InvalidKeyBinding If the key binding is invalid.
     */
    public void parseKeyBinding(final String line, final JXCWindow jxcWindow, final boolean isDefault) throws InvalidKeyBinding
    {
        if (line.startsWith("char "))
        {
            final String[] tmp = line.substring(5).split(" +", 2);
            if (tmp.length != 2)
            {
                throw new InvalidKeyBinding("syntax error");
            }

            try
            {
                final char keyChar = (char)Integer.parseInt(tmp[0]);
                final GUICommandList commands = new GUICommandList(GUICommandList.Type.AND, tmp[1], jxcWindow);
                addKeyBindingAsKeyChar(keyChar, commands, isDefault);
            }
            catch (final NumberFormatException ex)
            {
                throw new InvalidKeyBinding("syntax error");
            }
        }
        else if (line.startsWith("code "))
        {
            final String[] tmp = line.substring(5).split(" +", 3);
            if (tmp.length != 3)
            {
                throw new InvalidKeyBinding("syntax error");
            }

            if (keyCodeMap == null)
            {
                keyCodeMap = new KeyCodeMap();
            }

            final int keyCode;
            try
            {
                keyCode = keyCodeMap.getKeyCode(tmp[0]);
            }
            catch (final NoSuchKeyCode ex)
            {
                throw new InvalidKeyBinding("invalid key code: "+tmp[0]);
            }

            final int modifiers;
            try
            {
                modifiers = Integer.parseInt(tmp[1]);
            }
            catch (final NumberFormatException ex)
            {
                throw new InvalidKeyBinding("invalid modifier: "+tmp[1]);
            }

            final GUICommandList commands = new GUICommandList(GUICommandList.Type.AND, tmp[2], jxcWindow);
            addKeyBindingAsKeyCode(keyCode, modifiers, commands, isDefault);
        }
        else
        {
            throw new InvalidKeyBinding("syntax error");
        }
    }

    /**
     * Execute a "key press" event.
     *
     * @param e The event to execute.
     *
     * @return Whether a matching key binding was found.
     */
    public boolean handleKeyPress(final KeyEvent e)
    {
        return executeKeyBinding(getKeyBindingAsKeyCode(e.getKeyCode(), e.getModifiers()));
    }

    /**
     * Execute a "key typed" event.
     *
     * @param e The event to execute.
     *
     * @return Whether a matching key binding was found.
     */
    public boolean handleKeyTyped(final KeyEvent e)
    {
        return executeKeyBinding(getKeyBindingAsKeyChar(e.getKeyChar()));
    }

    /**
     * Execute a {@link KeyBinding} instance.
     * @param keyBinding the key binding to execute; may be <code>null</code>
     * @return whether <code>keyBinding</code> is not <code>null</code>
     */
    private boolean executeKeyBinding(final KeyBinding keyBinding)
    {
        if (keyBinding == null)
        {
            return false;
        }

        keyBinding.getCommands().execute();
        return true;
    }
}

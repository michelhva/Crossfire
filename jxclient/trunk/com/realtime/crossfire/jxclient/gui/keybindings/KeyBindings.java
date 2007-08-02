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

import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a set of key bindings.
 *
 * @author Andreas Kirschbaum
 */
public final class KeyBindings
{
    private final List<KeyBinding> keybindings = new ArrayList<KeyBinding>();

    public int size()
    {
        return keybindings.size();
    }

    /**
     * Add a key binding for a key code/modifiers pair.
     *
     * @param keyCode The key code for the key binding.
     *
     * @param modifiers The modifiers for the key binding.
     *
     * @param cmdlist The commands to associate to the key binding.
     */
    public void addKeyBindingAsKeyCode(final int keyCode, final int modifiers, final GUICommandList cmdlist)
    {
        final KeyBinding keyBinding = new KeyCodeKeyBinding(keyCode, modifiers, cmdlist);
        KeyBinding elected = null;
        for (final KeyBinding ok : keybindings)
        {
            if (ok.equals(keyBinding))
            {
                elected = ok;
                continue;
            }
        }
        if (elected != null)
        {
            keybindings.remove(elected);
        }
        keybindings.add(keyBinding);
    }

    /**
     * Add a key binding for a key character.
     *
     * @param keyCode The key character for the key binding.
     *
     * @param cmdlist The commands to associate to the key binding.
     */
    public void addKeyBindingAsKeyChar(final char keyChar, final GUICommandList cmdlist)
    {
        final KeyBinding keyBinding = new KeyCharKeyBinding(keyChar, cmdlist);
        KeyBinding elected = null;
        for (final KeyBinding ok : keybindings)
        {
            if (ok.equals(keyBinding))
            {
                elected = ok;
                continue;
            }
        }
        if (elected != null)
        {
            keybindings.remove(elected);
        }
        keybindings.add(keyBinding);
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
        final KeyBinding keyBinding = getKeyBindingAsKeyCode(keyCode, modifiers);
        if (keyBinding != null)
        {
            keybindings.remove(keyBinding);
        }
    }

    /**
     * Remove a key binding for a key character.
     *
     * @param keyChar The key character of the key binding.
     */
    public void deleteKeyBindingAsKeyChar(final char keyChar)
    {
        final KeyBinding keyBinding = getKeyBindingAsKeyChar(keyChar);
        if (keyBinding != null)
        {
            keybindings.remove(keyBinding);
        }
    }

    /**
     * Load the key bindings from the given file.
     *
     * @param filename The file name to save to.
     *
     * @param jxcWindow The window to execute the commands in.
     */
    public void loadKeyBindings(final String filename, final JXCWindow jxcWindow)
    {
        keybindings.clear();
        try
        {
            final FileInputStream fis = new FileInputStream(filename);
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

                            if (line.startsWith("char "))
                            {
                                final String[] tmp = line.substring(5).split(" ", 2);
                                if (tmp.length != 2)
                                {
                                    System.err.println(filename+": ignoring invalid binding: "+line);
                                }
                                else
                                {
                                    try
                                    {
                                        final char keyChar = (char)Integer.parseInt(tmp[0]);
                                        final GUICommandList commands = new GUICommandList(tmp[1], jxcWindow);
                                        addKeyBindingAsKeyChar(keyChar, commands);
                                    }
                                    catch (final NumberFormatException ex)
                                    {
ex.printStackTrace();
                                        System.err.println(filename+": ignoring invalid binding: "+line);
                                    }
                                }
                            }
                            else if (line.startsWith("code "))
                            {
                                final String[] tmp = line.substring(5).split(" ", 3);
                                if (tmp.length != 3)
                                {
                                    System.err.println(filename+": ignoring invalid binding: "+line);
                                }
                                else
                                {
                                    try
                                    {
                                        final int keyCode = Integer.parseInt(tmp[0]);
                                        final int modifiers = Integer.parseInt(tmp[1]);
                                        final GUICommandList commands = new GUICommandList(tmp[2], jxcWindow);
                                        addKeyBindingAsKeyCode(keyCode, modifiers, commands);
                                    }
                                    catch (final NumberFormatException ex)
                                    {
                                        System.err.println(filename+": ignoring invalid binding: "+line);
                                    }
                                }
                            }
                            else
                            {
                                System.err.println(filename+": ignoring invalid binding: "+line);
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
            System.err.println("Cannot load key bindings file "+filename+": "+ex.getMessage());
            keybindings.clear();
        }
    }

    /**
     * Save the key bindings to the given file.
     *
     * @param filename The file name to save to.
     */
    public void saveKeyBindings(final String filename)
    {
        try
        {
            final FileOutputStream fos = new FileOutputStream(filename);
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
                            if (keyBinding instanceof KeyCodeKeyBinding)
                            {
                                final KeyCodeKeyBinding keyCodeKeyBinding = (KeyCodeKeyBinding)keyBinding;
                                bw.write("code ");
                                bw.write(Integer.toString(keyCodeKeyBinding.getKeyCode()));
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
        catch (final IOException e)
        {
            System.err.println("Cannot write keybindings file "+filename+": "+e.getMessage());
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
    public KeyBinding getKeyBindingAsKeyCode(final int keyCode, final int modifiers)
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
    public KeyBinding getKeyBindingAsKeyChar(final char keyChar)
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
}

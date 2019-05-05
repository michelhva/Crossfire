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
import com.realtime.crossfire.jxclient.gui.commandlist.CommandListType;
import com.realtime.crossfire.jxclient.gui.commandlist.GUICommandFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages a set of key bindings.
 * @author Andreas Kirschbaum
 */
public class KeyBindings {

    /**
     * The {@link GUICommandFactory} for creating commands.
     */
    @NotNull
    private final GUICommandFactory guiCommandFactory;

    /**
     * The active key bindings.
     */
    @NotNull
    private final Collection<KeyBinding> keybindings = new HashSet<>();

    /**
     * Whether the contents of {@link #keybindings} have been modified from the
     * last saved state.
     */
    private boolean modified;

    /**
     * The file for saving the bindings; {@code null} to not save.
     */
    @Nullable
    private final File file;

    /**
     * The key code map to use. Set to {@code null} until first use.
     */
    @Nullable
    private KeyCodeMap keyCodeMap;

    /**
     * Creates a new instance.
     * @param file the file for saving the bindings; {@code null} to not save
     * @param guiCommandFactory the gui command factory for creating commands
     */
    public KeyBindings(@Nullable final File file, @NotNull final GUICommandFactory guiCommandFactory) {
        this.file = file;
        this.guiCommandFactory = guiCommandFactory;
    }

    /**
     * Returns the file for saving the bindings; {@code null} to not save.
     * @return the file
     */
    @Nullable
    public File getFile() {
        return file;
    }

    /**
     * Adds a key binding for a key code/modifiers pair.
     * @param keyEvent the key event for the key binding
     * @param cmdList the commands to associate to the key binding
     * @param isDefault whether the key binding is a "default" binding which
     * should not be saved
     */
    public void addKeyBindingAsKeyCode(@NotNull final KeyEvent2 keyEvent, @NotNull final CommandList cmdList, final boolean isDefault) {
        addKeyBinding(new KeyCodeKeyBinding(keyEvent, cmdList, isDefault));
    }

    /**
     * Adds a key binding for a key character.
     * @param keyChar the key character for the key binding
     * @param cmdList the commands to associate to the key binding
     * @param isDefault whether the key binding is a "default" binding which
     * should not be saved
     */
    public void addKeyBindingAsKeyChar(final char keyChar, @NotNull final CommandList cmdList, final boolean isDefault) {
        addKeyBinding(new KeyCharKeyBinding(keyChar, cmdList, isDefault));
    }

    /**
     * Adds (or replace) a key binding.
     * @param keyBinding the key binding
     */
    private void addKeyBinding(@NotNull final KeyBinding keyBinding) {
        keybindings.remove(keyBinding);
        keybindings.add(keyBinding);
        modified = true;
        try {
            saveKeyBindings();
        } catch (final IOException ex) {
            System.err.println("Cannot write keybindings file "+file+": "+ex.getMessage());
        }
    }

    /**
     * Removes a key binding for a key code/modifiers pair.
     * @param keyEvent the key of the key binding
     */
    public void deleteKeyBindingAsKeyCode(@NotNull final KeyEvent2 keyEvent) {
        deleteKeyBinding(getKeyBindingAsKeyCode(keyEvent));
    }

    /**
     * Removes a key binding.
     * @param keyBinding the key binding; may be {@code null}
     */
    private void deleteKeyBinding(@Nullable final KeyBinding keyBinding) {
        if (keyBinding != null) {
            keybindings.remove(keyBinding);
            modified = true;
            try {
                saveKeyBindings();
            } catch (final IOException ex) {
                System.err.println("Cannot write keybindings file "+file+": "+ex.getMessage());
            }
        }
    }

    /**
     * Loads the key bindings from the given file.
     * @throws IOException if the file cannot be read
     */
    public void loadKeyBindings() throws IOException {
        modified = false;

        if (file == null) {
            return;
        }

        try {
            try (final FileInputStream fis = new FileInputStream(file)) {
                try (final InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                    try (final LineNumberReader lnr = new LineNumberReader(isr)) {
                        while (true) {
                            final String line = lnr.readLine();
                            if (line == null) {
                                break;
                            }

                            try {
                                parseKeyBinding(line, false);
                            } catch (final InvalidKeyBindingException ex) {
                                System.err.println("ignoring invalid key binding ("+ex.getMessage()+"): "+line);
                            }
                        }
                    }
                }
            }
        } catch (final FileNotFoundException ignored) {
            // no error message
            keybindings.clear();
        } catch (final IOException ex) {
            keybindings.clear();
            modified = false;
            throw ex;
        }

        modified = false;
    }

    /**
     * Saves the key bindings to the given file.
     * @throws IOException if the file cannot be written
     */
    public void saveKeyBindings() throws IOException {
        if (file == null || !modified) {
            return;
        }

        if (keybindings.size() <= 0) {
            if (!file.delete()) {
                throw new IOException("cannot delete file");
            }
            return;
        }

        try (final FileOutputStream fos = new FileOutputStream(file)) {
            try (final OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                try (final BufferedWriter bw = new BufferedWriter(osw)) {
                    for (final KeyBinding keyBinding : keybindings) {
                        if (keyBinding.isDefault()) {
                            // ignore
                        } else if (keyBinding instanceof KeyCodeKeyBinding) {
                            if (keyCodeMap == null) {
                                keyCodeMap = new KeyCodeMap();
                            }

                            final KeyCodeKeyBinding keyCodeKeyBinding = (KeyCodeKeyBinding)keyBinding;
                            bw.write("code ");
                            final KeyEvent2 keyEvent = keyCodeKeyBinding.getKeyEvent2();
                            bw.write(keyCodeMap.getKeyName(keyEvent.getKeyCode()));
                            bw.write(' ');
                            bw.write(Integer.toString(keyEvent.getModifiers()));
                            bw.write(' ');
                            bw.write(guiCommandFactory.encode(keyCodeKeyBinding.getCommandString()));
                            bw.newLine();
                        } else {
                            throw new AssertionError("Cannot encode "+keyBinding.getClass().getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Finds a key binding associated to a key code/modifiers pair.
     * @param keyEvent the key to look up
     * @return the key binding, or {@code null} if no key binding is associated
     */
    @Nullable
    private KeyBinding getKeyBindingAsKeyCode(final KeyEvent2 keyEvent) {
        for (final KeyBinding keyBinding : keybindings) {
            if (keyBinding.matchesKeyCode(keyEvent)) {
                return keyBinding;
            }
        }

        return null;
    }

    /**
     * Finds a key binding associated to a key character.
     * @param keyChar the key character to look up
     * @return the key binding, or {@code null} if no key binding is associated
     */
    @Nullable
    private KeyBinding getKeyBindingAsKeyChar(final char keyChar) {
        for (final KeyBinding keyBinding : keybindings) {
            if (keyBinding.matchesKeyChar(keyChar)) {
                return keyBinding;
            }
        }

        return null;
    }

    /**
     * Parses and add a key binding.
     * @param line the key binding to parse
     * @param isDefault whether the key binding is a "default" binding which
     * should not be saved
     * @throws InvalidKeyBindingException if the key binding is invalid
     */
    public void parseKeyBinding(@NotNull final String line, final boolean isDefault) throws InvalidKeyBindingException {
        if (line.startsWith("char ")) {
            if (!isDefault) {
                // ignore "key char" definitions in user keybindings files --
                // these are not supported anymore.
                return;
            }

            final String[] tmp = line.substring(5).split(" +", 2);
            if (tmp.length != 2) {
                throw new InvalidKeyBindingException("syntax error");
            }

            try {
                final char keyChar = (char)Integer.parseInt(tmp[0]);
                final CommandList commandList = new CommandList(CommandListType.AND);
                commandList.add(guiCommandFactory.createCommandDecode(tmp[1]));
                addKeyBindingAsKeyChar(keyChar, commandList, isDefault);
            } catch (final NumberFormatException ex) {
                final InvalidKeyBindingException keyBindingException = new InvalidKeyBindingException("syntax error");
                keyBindingException.initCause(ex);
                throw keyBindingException;
            }
        } else if (line.startsWith("code ")) {
            final String[] tmp = line.substring(5).split(" +", 3);
            if (tmp.length != 3) {
                throw new InvalidKeyBindingException("syntax error");
            }

            if (keyCodeMap == null) {
                keyCodeMap = new KeyCodeMap();
            }

            final int keyCode;
            try {
                keyCode = keyCodeMap.getKeyCode(tmp[0]);
            } catch (final NoSuchKeyCodeException ex) {
                final InvalidKeyBindingException keyBindingException = new InvalidKeyBindingException("invalid key code: "+tmp[0]);
                keyBindingException.initCause(ex);
                throw keyBindingException;
            }

            final int modifiers;
            try {
                modifiers = Integer.parseInt(tmp[1]);
            } catch (final NumberFormatException ex) {
                final InvalidKeyBindingException keyBindingException = new InvalidKeyBindingException("invalid modifier: "+tmp[1]);
                keyBindingException.initCause(ex);
                throw keyBindingException;
            }

            final CommandList commandList = new CommandList(CommandListType.AND);
            commandList.add(guiCommandFactory.createCommandDecode(tmp[2]));
            addKeyBindingAsKeyCode(new KeyEvent2(keyCode, (char)0, modifiers), commandList, isDefault);
        } else {
            throw new InvalidKeyBindingException("syntax error");
        }
    }

    /**
     * Executes a "key press" event.
     * @param e the event to execute
     * @return whether a matching key binding was found
     */
    public boolean handleKeyPress(@NotNull final KeyEvent2 e) {
        final KeyBinding keyBindingCode = getKeyBindingAsKeyCode(e);
        if (keyBindingCode != null) {
            executeKeyBinding(keyBindingCode);
            return true;
        }

        final KeyBinding keyBindingChar = getKeyBindingAsKeyChar(e.getKeyChar());
        if (keyBindingChar != null) {
            executeKeyBinding(keyBindingChar);
            return true;
        }

        return false;
    }

    /**
     * Executes a {@link KeyBinding} instance.
     * @param keyBinding the key binding to execute; may be {@code null}
     */
    private static void executeKeyBinding(@NotNull final KeyBinding keyBinding) {
        keyBinding.getCommands().execute();
    }

    /**
     * Search bindings having a command text starting with the specified value.
     * @param command the text to search for
     * @param startOnly if true only search the text at the start of the
     * command, else anywhere.
     * @return the matching bindings
     */
    @NotNull
    public Collection<KeyBinding> getBindingsForPartialCommand(@NotNull final String command, final boolean startOnly) {
        return keybindings.stream().filter(binding -> (startOnly && binding.getCommandString().startsWith(command)) || (!startOnly && binding.getCommandString().contains(command))).collect(Collectors.toSet());
    }

}

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

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Maps between key codes integer constants and string representations.
 * @author Andreas Kirschbaum
 */
public class KeyCodeMap {

    /**
     * Maps key name to key code.
     */
    @NotNull
    private final Map<String, Integer> keyCodes = new HashMap<>();

    /**
     * Maps key code to key name.
     */
    @NotNull
    private final Map<Integer, String> keyNames = new HashMap<>();

    /**
     * Creates a new instance.
     */
    public KeyCodeMap() {
        for (Field field : KeyEvent.class.getDeclaredFields()) {
            if (field.getName().startsWith("VK_")) {
                final int keyCode;
                //noinspection ErrorNotRethrown
                try {
                    keyCode = field.getInt(null);
                } catch (final SecurityException ignored) {
                    continue;
                } catch (final IllegalArgumentException ignored) {
                    continue;
                } catch (final IllegalAccessException ignored) {
                    continue;
                } catch (final NullPointerException ignored) {
                    continue;
                } catch (final ExceptionInInitializerError ignored) {
                    continue;
                }
                final String keyName = field.getName().substring(3);

                keyCodes.put(keyName, keyCode);
                keyNames.put(keyCode, keyName);
            }
        }
    }

    /**
     * Returns the key code for a key name.
     * @param keyName the key name to convert
     * @return the key code
     * @throws NoSuchKeyCodeException if the key name is invalid
     */
    public int getKeyCode(@NotNull final String keyName) throws NoSuchKeyCodeException {
        if (keyCodes.containsKey(keyName)) {
            return keyCodes.get(keyName);
        }

        try {
            return Integer.parseInt(keyName);
        } catch (final NumberFormatException ex) {
            final NoSuchKeyCodeException noSuchKeyCodeException = new NoSuchKeyCodeException();
            noSuchKeyCodeException.initCause(ex);
            throw noSuchKeyCodeException;
        }
    }

    /**
     * Returns the key name for a key code.
     * @param keyCode the key code to convert
     * @return the key name
     */
    @NotNull
    public String getKeyName(final int keyCode) {
        final String keyName = keyNames.get(keyCode);
        if (keyName != null) {
            return keyName;
        }

        return Integer.toString(keyCode);
    }

}

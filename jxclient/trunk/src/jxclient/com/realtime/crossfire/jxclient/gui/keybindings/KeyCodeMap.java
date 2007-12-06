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

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps between key codes integer constants and string representations.
 *
 * @author Andreas Kirschbaum
 */
public class KeyCodeMap
{
    /**
     * Maps key name to key code.
     */
    private final Map<String, Integer> keyCodes = new HashMap<String, Integer>();

    /**
     * Maps key code to key name.
     */
    private final Map<Integer, String> keyNames = new HashMap<Integer, String>();

    /**
     * Create a new instance.
     */
    public KeyCodeMap()
    {
        for (final Field field : KeyEvent.class.getDeclaredFields())
        {
            if (field.getName().startsWith("VK_"))
            {
                final int keyCode;
                try
                {
                    keyCode = field.getInt(null);
                }
                catch (final SecurityException ex)
                {
                    continue;
                }
                catch (final IllegalArgumentException ex)
                {
                    continue;
                }
                catch (final IllegalAccessException ex)
                {
                    continue;
                }
                catch (final NullPointerException ex)
                {
                    continue;
                }
                catch (final ExceptionInInitializerError ex)
                {
                    continue;
                }
                final String keyName = field.getName().substring(3);

                keyCodes.put(keyName, keyCode);
                keyNames.put(keyCode, keyName);
            }
        }
    }

    /**
     * Return the key code for a key name.
     *
     * @param keyName The key name to convert.
     *
     * @return The key code.
     *
     * @throws NoSuchKeyCode If the key name is invalid.
     */
    public int getKeyCode(final String keyName) throws NoSuchKeyCode
    {
        if (keyCodes.containsKey(keyName))
        {
            return keyCodes.get(keyName);
        }

        try
        {
            return Integer.parseInt(keyName);
        }
        catch (final NumberFormatException ex)
        {
            throw new NoSuchKeyCode();
        }
    }

    /**
     * Return the key name for a key code.
     *
     * @param keyCode The key code to convert.
     *
     * @return The key name.
     */
    public String getKeyName(final int keyCode)
    {
        final String keyName = keyNames.get(keyCode);
        if (keyName != null)
        {
            return keyName;
        }

        return Integer.toString(keyCode);
    }
}

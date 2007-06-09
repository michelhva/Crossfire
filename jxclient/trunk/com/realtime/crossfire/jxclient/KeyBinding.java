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
package com.realtime.crossfire.jxclient;

import java.util.List;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class KeyBinding
{
    int                         mycode;
    int                         mymodifiers;
    List<GUICommand>  mycommands;

    public int getKeyCode()
    {
        return mycode;
    }
    public int getKeyModifiers()
    {
        return mymodifiers;
    }
    public List<GUICommand> getCommands()
    {
        return mycommands;
    }
    public KeyBinding(int c, int m, List<GUICommand> l)
    {
        mycode = c;
        mymodifiers = m;
        mycommands = l;
    }
    public boolean equals(Object op)
    {
        if (op instanceof KeyBinding)
        {
            KeyBinding kb = (KeyBinding)op;
            if ((kb.getKeyCode()==mycode)&&(kb.getKeyModifiers()==mymodifiers))
                return true;
            else
                return false;
        }
        else
            return false;
    }
}

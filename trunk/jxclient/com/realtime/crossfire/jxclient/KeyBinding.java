package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;

public class KeyBinding
{
    int                         mycode;
    int                         mymodifiers;
    java.util.List<GUICommand>  mycommands;

    public int getKeyCode()
    {
        return mycode;
    }
    public int getKeyModifiers()
    {
        return mymodifiers;
    }
    public java.util.List<GUICommand> getCommands()
    {
        return mycommands;
    }
    public KeyBinding(int c, int m, java.util.List<GUICommand> l)
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

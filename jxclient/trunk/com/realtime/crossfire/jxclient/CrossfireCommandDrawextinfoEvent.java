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
import java.util.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class CrossfireCommandDrawextinfoEvent extends EventObject
{
    private int mycolor;
    private int mytype;
    private int mysubtype;
    private String mymessage;
    public CrossfireCommandDrawextinfoEvent(Object src, int color, int type,
                                            int subtype, String message)
    {
        super(src);
        mycolor = color;
        mytype = type;
        mysubtype = subtype;
        mymessage = message;
    }
    public int getColor()
    {
        return mycolor;
    }
    public int getType()
    {
        return mytype;
    }
    public int getSubType()
    {
        return mysubtype;
    }
    public String getMessage()
    {
        return mymessage;
    }
}

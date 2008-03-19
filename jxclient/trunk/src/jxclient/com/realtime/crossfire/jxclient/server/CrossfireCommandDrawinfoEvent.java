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
package com.realtime.crossfire.jxclient.server;

import java.util.EventObject;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class CrossfireCommandDrawinfoEvent extends EventObject
{
    /** The serial version UID. */
    private static final long serialVersionUID = 1;

    public static final int NDI_BLACK = 0;

    public static final int NDI_WHITE = 1;

    public static final int NDI_NAVY = 2;

    public static final int NDI_RED = 3;

    public static final int NDI_ORANGE = 4;

    public static final int NDI_BLUE = 5;

    public static final int NDI_DK_ORANGE = 6;

    public static final int NDI_GREEN = 7;

    public static final int NDI_LT_GREEN = 8;

    public static final int NDI_GREY = 9;

    public static final int NDI_BROWN = 10;

    public static final int NDI_GOLD = 11;

    public static final int NDI_TAN = 12;

    private final String mytext;

    private final int mytype;

    public CrossfireCommandDrawinfoEvent(final Object src, final String msg, final int type)
    {
        super(src);
        mytext = msg;
        mytype = type;
    }

    public int getTextType()
    {
        return mytype;
    }

    public String getText()
    {
        return mytext;
    }
}

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
package com.realtime.crossfire.jxclient.magicmap;

import java.util.EventObject;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class CrossfireCommandMagicmapEvent extends EventObject
{
    /** The serial version UID. */
    private static final long serialVersionUID = 1;

    private final int width;

    private final int height;

    private final int px;

    private final int py;

    private final byte[] data;

    public CrossfireCommandMagicmapEvent(final Object src, final int width, final int height, final int px, final int py, final byte[] data)
    {
        super(src);
        this.width = width;
        this.height = height;
        this.px = px;
        this.py = py;
        this.data = data;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getPX()
    {
        return px;
    }

    public int getPY()
    {
        return py;
    }

    public byte[] getData()
    {
        return data;
    }
}

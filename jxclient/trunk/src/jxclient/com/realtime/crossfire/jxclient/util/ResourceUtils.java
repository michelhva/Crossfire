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
package com.realtime.crossfire.jxclient.util;

import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Utility class for loading information from resources.
 * @author Andreas Kirschbaum
 */
public class ResourceUtils
{
    /**
     * Private constructor to prevent instantiation.
     */
    private ResourceUtils()
    {
    }

    /**
     * Loads an image file.
     * @param unknownPng the resource name to load
     * @return the image
     * @throws IOException if the image cannot be loaded
     */
    public static ImageIcon loadImage(final String unknownPng) throws IOException
    {
        final URL url = ResourceUtils.class.getClassLoader().getResource(unknownPng);
        if (url == null)
        {
            throw new IOException("cannot find "+unknownPng);
        }
        final ImageIcon imageIcon = new ImageIcon(url);
        if (imageIcon.getIconWidth() <= 0 || imageIcon.getIconHeight() <= 0)
        {
            throw new IOException("cannot load "+unknownPng);
        }
        return imageIcon;
    }
}

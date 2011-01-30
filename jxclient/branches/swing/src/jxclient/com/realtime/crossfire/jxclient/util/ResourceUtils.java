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

package com.realtime.crossfire.jxclient.util;

import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for loading information from resources.
 * @author Andreas Kirschbaum
 */
public class ResourceUtils {

    /**
     * The resource name of the "unknown" face.
     */
    @NotNull
    public static final String UNKNOWN_PNG = "unknown.png";

    /**
     * The resource for "Click here for next group of items" buttons.
     */
    @NotNull
    public static final String NEXT_GROUP_FACE = "next_group.png";

    /**
     * The resource for "Click here for previous group of items" buttons.
     */
    @NotNull
    public static final String PREV_GROUP_FACE = "prev_group.png";

    /**
     * The resource name for the application icon.
     */
    @NotNull
    public static final String APPLICATION_ICON = "application_icon.png";

    /**
     * Private constructor to prevent instantiation.
     */
    private ResourceUtils() {
    }

    /**
     * Loads an image file.
     * @param name the resource name to load
     * @return the image
     * @throws IOException if the image cannot be loaded
     */
    @NotNull
    public static ImageIcon loadImage(@NotNull final String name) throws IOException {
        final URL url = ResourceUtils.class.getClassLoader().getResource("resource/"+name);
        if (url == null) {
            throw new IOException("cannot find image '"+name+"'");
        }
        final ImageIcon imageIcon = new ImageIcon(url);
        if (imageIcon.getIconWidth() <= 0 || imageIcon.getIconHeight() <= 0) {
            throw new IOException("cannot load image '"+name+"'");
        }
        return imageIcon;
    }

}

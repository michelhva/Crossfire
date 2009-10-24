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
package com.realtime.crossfire.jxclient.faces;

import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

/**
 * Consists of three {@link ImageIcon}s representing a {@link Face}.
 * @author Andreas Kirschbaum
 */
public class FaceImages
{
    /**
     * The {@link ImageIcon} as sent by the Crossfire server.
     */
    @NotNull
    private final ImageIcon originalImageIcon;

    /**
     * The {@link ImageIcon} scaled for the map view.
     */
    @NotNull
    private final ImageIcon scaledImageIcon;

    /**
     * The {@link ImageIcon} scaled for the magic map view.
     */
    @NotNull
    private final ImageIcon magicMapImageIcon;

    public FaceImages(@NotNull final ImageIcon originalImageIcon, @NotNull final ImageIcon scaledImageIcon, @NotNull final ImageIcon magicMapImageIcon)
    {
        this.originalImageIcon = originalImageIcon;
        this.scaledImageIcon = scaledImageIcon;
        this.magicMapImageIcon = magicMapImageIcon;
    }

    /**
     * Returns the {@link ImageIcon} as sent by the Crossfire server.
     * @return the image icon
     */
    @NotNull
    public ImageIcon getOriginalImageIcon()
    {
        return originalImageIcon;
    }

    /**
     * Returns the {@link ImageIcon} scaled for the map view.
     * @return the image icon
     */
    @NotNull
    public ImageIcon getScaledImageIcon()
    {
        return scaledImageIcon;
    }

    /**
     * Returns the {@link ImageIcon} scaled for the magic map view.
     * @return the image icon
     */
    @NotNull
    public ImageIcon getMagicMapImageIcon()
    {
        return magicMapImageIcon;
    }
}

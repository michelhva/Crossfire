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
package com.realtime.crossfire.jxclient.skin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates {@link BufferedImage} instances from strig representations. Each
 * image is loaded only once.
 * @author Andreas Kirschbaum
 */
public class ImageParser
{
    /**
     * All defined images.
     */
    @NotNull
    private final JXCSkinCache<BufferedImage> definedImages = new JXCSkinCache<BufferedImage>("image");

    /**
     * The {@link JXCSkinSource} for loading resources.
     */
    @NotNull
    private final JXCSkinSource skinSource;

    /**
     * Creates a new instance.
     * @param skinSource the skin source for loading resources
     */
    public ImageParser(@NotNull final JXCSkinSource skinSource)
    {
        this.skinSource = skinSource;
    }

    /**
     * Forgets all defined images.
     */
    public void clear()
    {
        definedImages.clear();
    }

    /**
     * Optionally loads an image by base file name.
     * @param color if non-<code>null</code>, return <code>null</code>
     * @param name the base file name
     * @return the image, or <code>null</code> if <code>color!=null</code>
     * @throws IOException if the image cannot be loaded
     */
    @Nullable
    public BufferedImage getImage(@Nullable final Color color, @NotNull final String name) throws IOException
    {
        return color != null ? null : getImage(name);
    }

    /**
     * Loads an image by base file name.
     * @param name the base file name
     * @return the image
     * @throws IOException if the image cannot be loaded
     */
    @NotNull
    public BufferedImage getImage(@NotNull final String name) throws IOException
    {
        try
        {
            return definedImages.lookup(name);
        }
        catch (final JXCSkinException ex)
        {
            // ignore
        }

        final String filename = "pictures/"+name+".png";
        final BufferedImage image;
        final InputStream inputStream = skinSource.getInputStream(filename);
        try
        {
            image = ImageIO.read(inputStream);
        }
        finally
        {
            inputStream.close();
        }
        if (image == null)
        {
            throw new IOException("image '"+skinSource.getURI(filename)+"' does not exist");
        }
        try
        {
            definedImages.insert(name, image);
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError();
        }
        return image;
    }
}

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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.realtime.crossfire.jxclient.*;

/**
 * A utility to perform the scale2x algorithm on a Java Image
 *
 * @author Kevin Glass
 */
public class ImageScale2x
{
    /** The src data from the image */
    private int[] srcData;
    /** The width of the image */
    private int width;
    /** The height of the image */
    private int height;

    /**
     * Create a new scaler that will scale the passed image
     *
     * @param srcImage The image to be scaled
     */
    public ImageScale2x(ImageIcon srcImageIcon)
    {
        width = srcImageIcon.getIconWidth();
        height = srcImageIcon.getIconHeight();

        srcData = new int[width*height];
        final BufferedImage srcBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        srcImageIcon.paintIcon(null, srcBufferedImage.getGraphics(), 0, 0);
        srcBufferedImage.getRGB(0, 0, width, height, srcData, 0, width);
    }

    /**
     * Retrieve the scaled image. Note this is the method that actually
     * does the work so it may take some time to return
     *
     * @return The newly scaled image
     */
    public ImageIcon getScaledImage()
    {
        RawScale2x scaler = new RawScale2x(srcData,width,height);

        BufferedImage image = new BufferedImage(width*2,height*2,BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0,0,width*2,height*2,scaler.getScaledData(),0,width*2);

        return new ImageIcon(image);
    }

    /**
     * An entry point and a bit of test code
     *
     * @param argv The arguments passed in to the test code
     */
    public static void main(String argv[])
    {
        String srcFile = "randam_orig.png";
        try
        {
            System.out.println("Reading: "+srcFile);
            ImageIcon src = new ImageIcon(srcFile);
            ImageScale2x scaler = new ImageScale2x(src);
            ImageIcon out = scaler.getScaledImage();

            String outFile = srcFile.substring(0,srcFile.length()-4);
            outFile += "2x";
            outFile += ".png";
            System.out.println("Writing: "+outFile);
            Face.saveImageIcon(out, new File(outFile));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

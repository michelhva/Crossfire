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

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIPicture extends GUIElement
{
    public GUIPicture
            (String nn, int nx, int ny, int nw, int nh, String picture)  throws IOException
    {
        super(nn, nx, ny, nw, nh);
        BufferedImage img =
            ImageIO.read(this.getClass().getClassLoader().getResource(picture));
        if (img == null)
        {
            throw new IllegalArgumentException("resource '"+picture+"' does not exist");
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        Graphics2D g = mybuffer.createGraphics();
        //System.out.println("nw:"+nw+" nh:"+nh);
        //g.drawImage(img, 0, 0, nw, nh, 0, 0, nw, nh, null);
        g.drawImage(img, 0,0,img.getWidth(),img.getHeight(),null);
        g.dispose();
        setChanged();
    }
}

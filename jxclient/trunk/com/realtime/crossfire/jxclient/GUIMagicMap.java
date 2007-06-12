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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.IOException;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIMagicMap extends GUIElement implements CrossfireMagicmapListener,
                                                       CrossfireNewmapListener
{
    Color[] mycolors = new Color[]{Color.BLACK, Color.WHITE, Color.BLUE,
                                   Color.RED, Color.GREEN, Color.YELLOW,
                                   Color.PINK, Color.GRAY, Color.ORANGE,
                                   Color.CYAN, Color.MAGENTA};
    public GUIMagicMap
            (String nn, int nx, int ny, int nw, int nh)  throws IOException
    {
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myname = nn;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        setChanged();
    }
    public void CommandMagicmapReceived(CrossfireCommandMagicmapEvent evt)
    {
        int datapos = 0;
        byte[] data = evt.getData();
        byte square;
        Color scolor;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        BufferedImage buf = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D g = buf.createGraphics();
        g.setBackground(new Color(0,0,0,0.0f));
        g.clearRect(0,0,w,h);
        while(data[datapos]==' ')
            datapos++;
        for (int y=0;y<evt.getHeight();y++)
        {
            for (int x=0;x<evt.getWidth();x++)
            {
                square = data[datapos];
                if (square > 10)
                    scolor = Color.DARK_GRAY;
                else
                    scolor = mycolors[square];

                g.setColor(scolor);
                g.fillRect(x*4,y*4,(x*4)+4,(y*4)+4);
                datapos++;
            }
        }
        g.dispose();
        mybuffer = buf;
        setChanged();
    }
    public void CommandNewmapReceived(CrossfireCommandNewmapEvent evt)
    {
        Graphics2D g = mybuffer.createGraphics();
        g.setBackground(new Color(0,0,0,0.0f));
        g.clearRect(0,0,w,h);
        g.dispose();
        setChanged();
    }
}

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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.CfMagicMap;
import com.realtime.crossfire.jxclient.CfMapUpdater;
import com.realtime.crossfire.jxclient.CrossfireCommandMagicmapEvent;
import com.realtime.crossfire.jxclient.CrossfireCommandNewmapEvent;
import com.realtime.crossfire.jxclient.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.CrossfireNewmapListener;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIMagicMap extends GUIElement
{
    private final Color[] mycolors = new Color[]
    {
        Color.BLACK, Color.WHITE, Color.BLUE,
        Color.RED, Color.GREEN, Color.YELLOW,
        Color.PINK, Color.GRAY, Color.ORANGE,
        Color.CYAN, Color.MAGENTA,
    };

    /**
     * The {@link CrossfireMagicmapListener} registered to receive mapgicmap
     * commands.
     */
    private final CrossfireMagicmapListener crossfireMagicmapListener = new CrossfireMagicmapListener()
    {
        /** {@inheritDoc} */
        public void commandMagicmapReceived(final CrossfireCommandMagicmapEvent evt)
        {
            int datapos = 0;
            final byte[] data = evt.getData();
            final Graphics2D g = mybuffer.createGraphics();
            g.setBackground(new Color(0, 0, 0, 0.0f));
            g.clearRect(0, 0, w, h);
            while (data[datapos] == ' ')
            {
                datapos++;
            }
            for (int y = 0; y < evt.getHeight(); y++)
            {
                for (int x = 0; x < evt.getWidth(); x++)
                {
                    final byte square = data[datapos];
                    final Color scolor = square >= mycolors.length ? Color.DARK_GRAY : mycolors[square];
                    g.setColor(scolor);
                    g.fillRect(x*4, y*4, (x*4)+4, (y*4)+4);
                    datapos++;
                }
            }
            g.dispose();
            setChanged();
        }
    };

    /**
     * The {@link CrossfireNewmapListener} registered to receive newmap
     * commands.
     */
    private final CrossfireNewmapListener crossfireNewmapListener = new CrossfireNewmapListener()
    {
        /** {@inheritDoc} */
        public void commandNewmapReceived(final CrossfireCommandNewmapEvent evt)
        {
            Graphics2D g = mybuffer.createGraphics();
            g.setBackground(new Color(0, 0, 0, 0.0f));
            g.clearRect(0, 0, w, h);
            g.dispose();
            setChanged();
        }
    };

    public GUIMagicMap(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h)
    {
        super(jxcWindow, name, x, y, w, h);
        createBuffer();
        CfMagicMap.addCrossfireMagicmapListener(crossfireMagicmapListener);
        CfMapUpdater.addCrossfireNewmapListener(crossfireNewmapListener);
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        setChanged();
    }
}

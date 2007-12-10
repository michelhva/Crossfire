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

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.magicmap.CfMagicMap;
import com.realtime.crossfire.jxclient.magicmap.CrossfireCommandMagicmapEvent;
import com.realtime.crossfire.jxclient.magicmap.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireCommandMapscrollEvent;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireCommandNewmapEvent;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireMapscrollListener;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireNewmapListener;
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
    /**
     * The size of one tile in pixels.
     */
    private final int TILE_SIZE = 4;

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
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w, h);
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
                    g.fillRect(x*TILE_SIZE, y*TILE_SIZE, (x+1)*TILE_SIZE, (y+1)*TILE_SIZE);
                    datapos++;
                }
            }
            g.dispose();
            setChanged();
        }
    };

    /**
     * The {@link CrossfireMapscrollListener} used to track player position
     * changes into the magic map.
     */
    private final CrossfireMapscrollListener crossfireMapscrollListener = new CrossfireMapscrollListener()
    {
        /** {@inheritDoc} */
        public void commandMapscrollReceived(final CrossfireCommandMapscrollEvent evt)
        {
            final Graphics2D g = mybuffer.createGraphics();
            final int dx = evt.getDX()*TILE_SIZE;
            final int dy = evt.getDY()*TILE_SIZE;
            if (Math.abs(dx) >= w || Math.abs(dy) >= h)
            {
                g.fillRect(0, 0, w, h);
            }
            else
            {
                g.copyArea(dx <= 0 ? 0 : dx, dy <= 0 ? 0 : dy, dx == 0 ? w : w-Math.abs(dx), dy == 0 ? h : h-Math.abs(dy), -dx, -dy);
                g.setColor(Color.BLACK);
                if (dx < 0)
                {
                    g.fillRect(0, 0, -dx, h);
                }
                else if (dx > 0)
                {
                    g.fillRect(w-dx, 0, dx, h);
                }
                if (dy < 0)
                {
                    g.fillRect(0, 0, w, -dy);
                }
                else if (dy > 0)
                {
                    g.fillRect(0, h-dy, w, dy);
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
            final Graphics2D g = mybuffer.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w, h);
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
        CfMapUpdater.addCrossfireMapscrollListener(crossfireMapscrollListener);
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

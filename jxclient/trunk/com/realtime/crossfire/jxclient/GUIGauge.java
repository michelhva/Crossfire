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
import javax.imageio.ImageIO;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIGauge extends GUIElement implements CrossfireStatsListener
{
    private final int stat;
    private int curValue = 0;
    private int maxValue = 0;
    private int minValue = 0;
    private BufferedImage pictureFull;
    private BufferedImage pictureNegative;
    private BufferedImage pictureEmpty;
    private final int orientation;

    public static final int ORIENTATION_WE = 0;
    public static final int ORIENTATION_EW = 1;
    public static final int ORIENTATION_NS = 2;
    public static final int ORIENTATION_SN = 3;

    public GUIGauge
            (String nn, int nx, int ny,int  nw,int  nh, String picture_full,
             String picture_negative, String picture_empty, int stat, int orientation)
            throws IOException
    {
        pictureFull     =
            ImageIO.read(this.getClass().getClassLoader().getResource(picture_full));
        if (picture_negative != null)
            pictureNegative =
                ImageIO.read(this.getClass().getClassLoader().getResource(picture_negative));
        else
            pictureNegative = null;
        pictureEmpty    =
            ImageIO.read(this.getClass().getClassLoader().getResource(picture_empty));
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        this.stat = stat;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        myname = nn;
        this.orientation = orientation;
        render();
    }
    public void render()
    {
        if (maxValue  <= 0) return;

        int fw = 0;
        int fh = 0;
        int fx = 0;
        int fy = 0;
        Graphics2D g = mybuffer.createGraphics();

        if (curValue >= 0)
        {
            switch (orientation)
            {
                case ORIENTATION_WE:
                    fw = (int)((float)Math.min(curValue, maxValue)*((float)w/(float)maxValue));
                    fh = h;
                    break;
                case ORIENTATION_EW:
                    fw = (int)((float)Math.min(curValue, maxValue)*((float)w/(float)maxValue));
                    fh = h;
                    fx = w - fw;
                    break;
                case ORIENTATION_NS:
                    fh = (int)((float)Math.min(curValue, maxValue)*((float)h/(float)maxValue));
                    fw=w;
                    break;
                case ORIENTATION_SN:
                    fh = (int)((float)Math.min(curValue, maxValue)*((float)h/(float)maxValue));
                    fy = h - fh;
                    fw=w;
                    break;
            }
            g.setBackground(new Color(0,0,0,0.0f));
            g.clearRect(0,0,w,h);
            g.drawImage(pictureEmpty, 0,0,null);
            g.drawImage(pictureFull,fx,fy,fw+fx,fh+fy,fx,fy,fw+fx,fh+fy,null);
            g.dispose();
        }
        else if (pictureNegative != null)
        {
            switch (orientation)
            {
                case ORIENTATION_WE:
                    fw = (int)((float)(-curValue)*((float)w/(float)(-minValue)));
                    fh = h;
                    fx = w - fw;
                    break;
                case ORIENTATION_EW:
                    fw = (int)((float)(-curValue)*((float)w/(float)(-minValue)));
                    fh = h;
                    break;
                case ORIENTATION_NS:
                    fh = (int)((float)(-curValue)*((float)h/(float)(-minValue)));
                    fw = w;
                    fy = h - fh;
                    break;
                case ORIENTATION_SN:
                    fh = (int)((float)(-curValue)*((float)h/(float)(-minValue)));
                    fw = w;
                    break;
            }
            g.setBackground(new Color(0,0,0,0.0f));
            g.clearRect(0,0,w,h);
            g.drawImage(pictureEmpty, 0,0,null);
            g.drawImage(pictureNegative,fx,fy,fw+fx,fh+fy,fx,fy,fw+fx,fh+fy,null);
            g.dispose();
        }
        setChanged();
    }
    public void CommandStatsReceived(CrossfireCommandStatsEvent evt)
    {
        Stats s = evt.getStats();
        switch (stat)
        {
            case Stats.CS_STAT_HP:
                setValues(s.getStat(stat), 0, s.getStat(Stats.CS_STAT_MAXHP));
                break;
            case Stats.CS_STAT_SP:
                setValues(s.getStat(stat), 0, s.getStat(Stats.CS_STAT_MAXSP));
                break;
            case Stats.CS_STAT_FOOD:
                setValues(s.getStat(stat), 0, 999);
                break;
            case Stats.CS_STAT_GRACE:
                setValues(s.getStat(stat), -s.getStat(Stats.CS_STAT_MAXGRACE), s.getStat(Stats.CS_STAT_MAXGRACE));
                break;
            default:
                if (Stats.CS_STAT_RESIST_START <= stat && stat <= Stats.CS_STAT_RESIST_END)
                {
                    setValues(s.getStat(stat), -100, 100);
                }
                break;
        }
        render();
    }

    /**
     * Change the displayed values.
     *
     * @param curValue The values to display.
     *
     * @param minValue The minium possible value.
     *
     * @param maxValue The maximum possible value.
     */
    private void setValues(final int curValue, final int minValue, final int maxValue)
    {
        this.curValue = curValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}

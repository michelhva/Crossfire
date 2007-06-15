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
    private final int mystat;
    private int myvalue = 0;
    private int mymax = 0;
    private int mymin = 0;
    private BufferedImage mypicture_full;
    private BufferedImage mypicture_negative;
    private BufferedImage mypicture_empty;
    private final int myorientation;

    public static final int ORIENTATION_WE = 0;
    public static final int ORIENTATION_EW = 1;
    public static final int ORIENTATION_NS = 2;
    public static final int ORIENTATION_SN = 3;

    public GUIGauge
            (String nn, int nx, int ny,int  nw,int  nh, String picture_full,
             String picture_negative, String picture_empty, int stat, int orientation)
            throws IOException
    {
        mypicture_full     =
            ImageIO.read(this.getClass().getClassLoader().getResource(picture_full));
        if (picture_negative != null)
            mypicture_negative =
                ImageIO.read(this.getClass().getClassLoader().getResource(picture_negative));
        else
            mypicture_negative = null;
        mypicture_empty    =
            ImageIO.read(this.getClass().getClassLoader().getResource(picture_empty));
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        mystat = stat;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        myname = nn;
        myorientation = orientation;
        render();
    }
    public void render()
    {
        if (mymax  <= 0) return;

        int fw = 0;
        int fh = 0;
        int fx = 0;
        int fy = 0;
        Graphics2D g = mybuffer.createGraphics();

        if (myvalue >= 0)
        {
            switch (myorientation)
            {
                case ORIENTATION_WE:
                    fw = (int)((float)Math.min(myvalue, mymax)*((float)w/(float)mymax));
                    fh = h;
                    break;
                case ORIENTATION_EW:
                    fw = (int)((float)Math.min(myvalue, mymax)*((float)w/(float)mymax));
                    fh = h;
                    fx = w - fw;
                    break;
                case ORIENTATION_NS:
                    fh = (int)((float)Math.min(myvalue, mymax)*((float)h/(float)mymax));
                    fw=w;
                    break;
                case ORIENTATION_SN:
                    fh = (int)((float)Math.min(myvalue, mymax)*((float)h/(float)mymax));
                    fy = h - fh;
                    fw=w;
                    break;
            }
            g.setBackground(new Color(0,0,0,0.0f));
            g.clearRect(0,0,w,h);
            g.drawImage(mypicture_empty, 0,0,null);
            g.drawImage(mypicture_full,fx,fy,fw+fx,fh+fy,fx,fy,fw+fx,fh+fy,null);
            g.dispose();
        }
        else if (mypicture_negative != null)
        {
            switch (myorientation)
            {
                case ORIENTATION_WE:
                    fw = (int)((float)(-myvalue)*((float)w/(float)(-mymin)));
                    fh = h;
                    fx = w - fw;
                    break;
                case ORIENTATION_EW:
                    fw = (int)((float)(-myvalue)*((float)w/(float)(-mymin)));
                    fh = h;
                    break;
                case ORIENTATION_NS:
                    fh = (int)((float)(-myvalue)*((float)h/(float)(-mymin)));
                    fw = w;
                    fy = h - fh;
                    break;
                case ORIENTATION_SN:
                    fh = (int)((float)(-myvalue)*((float)h/(float)(-mymin)));
                    fw = w;
                    break;
            }
            g.setBackground(new Color(0,0,0,0.0f));
            g.clearRect(0,0,w,h);
            g.drawImage(mypicture_empty, 0,0,null);
            g.drawImage(mypicture_negative,fx,fy,fw+fx,fh+fy,fx,fy,fw+fx,fh+fy,null);
            g.dispose();
        }
        setChanged();
    }
    public void CommandStatsReceived(CrossfireCommandStatsEvent evt)
    {
        Stats s = evt.getStats();
        switch (mystat)
        {
            case Stats.CS_STAT_HP:
                setValues(s.getStat(mystat), 0, s.getStat(Stats.CS_STAT_MAXHP));
                break;
            case Stats.CS_STAT_SP:
                setValues(s.getStat(mystat), 0, s.getStat(Stats.CS_STAT_MAXSP));
                break;
            case Stats.CS_STAT_FOOD:
                setValues(s.getStat(mystat), 0, 999);
                break;
            case Stats.CS_STAT_GRACE:
                setValues(s.getStat(mystat), -s.getStat(Stats.CS_STAT_MAXGRACE), s.getStat(Stats.CS_STAT_MAXGRACE));
                break;
            default:
                if (Stats.CS_STAT_RESIST_START <= mystat && mystat <= Stats.CS_STAT_RESIST_END)
                {
                    setValues(s.getStat(mystat), -100, 100);
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
        myvalue = curValue;
        mymin = minValue;
        mymax = maxValue;
    }
}

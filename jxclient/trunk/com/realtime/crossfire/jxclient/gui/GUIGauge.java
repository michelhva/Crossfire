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

import com.realtime.crossfire.jxclient.CrossfireCommandStatsEvent;
import com.realtime.crossfire.jxclient.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.Stats;
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
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public class GUIGauge extends GUIElement implements CrossfireStatsListener
{
    private final int stat;

    private int curValue = 0;

    private int maxValue = -1;

    private int minValue = 0;

    private int fw = 0;

    private int fh = 0;

    private int fx = 0;

    private int fy = 0;

    private BufferedImage fPicture = null;

    private final BufferedImage pictureFull;

    private final BufferedImage pictureNegative;

    private final BufferedImage pictureEmpty;

    private final Orientation orientation;

    private final String tooltipPrefix;

    public enum Orientation
    {
        WE,
        EW,
        NS,
        SN,
    }

        public GUIGauge(final JXCWindow jxcWindow, final String nn, final int nx, final int ny, final int nw, final int nh, final BufferedImage picture_full, final BufferedImage picture_negative, final BufferedImage picture_empty, final int stat, final Orientation orientation, final String tooltipPrefix) throws IOException
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        pictureFull = picture_full;
        pictureNegative = picture_negative;
        pictureEmpty = picture_empty;
        this.stat = stat;
        createBuffer();
        this.orientation = orientation;
        this.tooltipPrefix = tooltipPrefix;
        setValues(0, 0, 0);
        setTooltipText(tooltipPrefix == null ? null : tooltipPrefix+curValue);
    }
    public void render()
    {
        if (maxValue <= minValue)
        {
            draw(0, 0, 0, 0, null);
            return;
        }

        int fw = 0;
        int fh = 0;
        int fx = 0;
        int fy = 0;

        if (curValue < 0 && pictureNegative != null)
        {
            switch (orientation)
            {
            case WE:
                fw = (int)((float)(-curValue)*((float)w/(float)-minValue)+0.5);
                fh = h;
                fx = w-fw;
                draw(fx, fy, fw, fh, pictureNegative);
                break;

            case EW:
                fw = (int)((float)-curValue*((float)w/(float)-minValue)+0.5);
                fh = h;
                draw(fx, fy, fw, fh, pictureNegative);
                break;

            case NS:
                fh = (int)((float)-curValue*((float)h/(float)-minValue)+0.5);
                fw = w;
                fy = h-fh;
                draw(fx, fy, fw, fh, pictureNegative);
                break;

            case SN:
                fh = (int)((float)-curValue*((float)h/(float)-minValue)+0.5);
                fw = w;
                draw(fx, fy, fw, fh, pictureNegative);
                break;
            }
        }
        else
        {
            switch (orientation)
            {
            case WE:
                fw = (int)((float)Math.min(curValue, maxValue)*((float)w/(float)maxValue)+0.5);
                fh = h;
                draw(fx, fy, fw, fh, pictureFull);
                break;

            case EW:
                fw = (int)((float)Math.min(curValue, maxValue)*((float)w/(float)maxValue)+0.5);
                fh = h;
                fx = w-fw;
                draw(fx, fy, fw, fh, pictureFull);
                break;

            case NS:
                fh = (int)((float)Math.min(curValue, maxValue)*((float)h/(float)maxValue)+0.5);
                fw = w;
                draw(fx, fy, fw, fh, pictureFull);
                break;

            case SN:
                fh = (int)((float)Math.min(curValue, maxValue)*((float)h/(float)maxValue)+0.5);
                fy = h-fh;
                fw = w;
                draw(fx, fy, fw, fh, pictureFull);
                break;
            }
        }
    }

    /**
     * Draw the given part of a picture to {@link #mybuffer}.
     *
     * @param fx The x-coordinate of the area to draw from
     * <code>fPicture</code>.
     *
     * @param fy The y-coordinate of the area to draw from
     * <code>fPicture</code>.
     *
     * @param fw The width of the area to draw from <code>fPicture</code>.
     *
     * @param fh The height of the area to draw from <code>fPicture</code>.
     *
     * @param fPicture The picture to draw.
     */
    private void draw(int fx, int fy, int fw, int fh, final BufferedImage fPicture)
    {
        assert this.w > 0;
        if (fx > this.w)
        {
            fx = this.w;
            fw = 0;
        }
        else
        {
            if (fx < 0)
            {
                fw -= -fx;
                fx = 0;
            }

            if (fw > this.w)
            {
                fw = this.w;
            }
        }

        assert this.h > 0;
        if (fy > this.h)
        {
            fy = this.h;
            fh = 0;
        }
        else
        {
            if (fy < 0)
            {
                fh -= -fy;
                fy = 0;
            }

            if (fh > this.h)
            {
                fh = this.h;
            }
        }

        if (this.fx == fx && this.fy == fy && this.fw == fw && this.fh == fh && this.fPicture == fPicture)
        {
            return;
        }

        this.fx = fx;
        this.fy = fy;
        this.fw = fw;
        this.fh = fh;
        this.fPicture = fPicture;

        final Graphics2D g = mybuffer.createGraphics();
        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, w, h);
        g.drawImage(pictureEmpty, 0, 0, null);
        if (fPicture != null)
        {
            g.drawImage(fPicture, fx, fy, fw+fx, fh+fy, fx, fy, fw+fx, fh+fy, null);
        }
        g.dispose();
        setChanged();
    }

    public void commandStatsReceived(final CrossfireCommandStatsEvent evt)
    {
        final Stats s = evt.getStats();
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
        if (this.curValue == curValue && this.minValue == minValue && this.maxValue == maxValue)
        {
            return;
        }

        this.curValue = curValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        render();

        setTooltipText(tooltipPrefix == null ? null : tooltipPrefix+curValue);
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

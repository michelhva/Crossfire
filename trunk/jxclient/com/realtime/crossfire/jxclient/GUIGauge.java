package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUIGauge extends GUIElement implements CrossfireStatsListener
{
    private int mystat;
    private int myvalue = 0;
    private int mymax = 0;
    private int mymin = 0;
    private BufferedImage mypicture_full;
    private BufferedImage mypicture_negative;
    private BufferedImage mypicture_empty;
    private int myorientation;

    public static final int ORIENTATION_WE = 0;
    public static final int ORIENTATION_EW = 1;
    public static final int ORIENTATION_NS = 2;
    public static final int ORIENTATION_SN = 3;

    public GUIGauge
            (String nn, int nx, int ny,int  nw,int  nh, String picture_full,
             String picture_negative, String picture_empty, int stat, int orientation)
            throws IOException
    {
        mypicture_full     = javax.imageio.ImageIO.read(new File(picture_full));
        if (picture_negative != null)
            mypicture_negative = javax.imageio.ImageIO.read(new File(picture_negative));
        else
            mypicture_negative = null;
        mypicture_empty    = javax.imageio.ImageIO.read(new File(picture_empty));
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
                    fw = (int)((float)myvalue*((float)w/(float)mymax));
                    fh = h;
                    break;
                case ORIENTATION_EW:
                    fw = (int)((float)myvalue*((float)w/(float)mymax));
                    fh = h;
                    fx = w - fw;
                    break;
                case ORIENTATION_NS:
                    fh = (int)((float)myvalue*((float)h/(float)mymax));
                    fw=w;
                    break;
                case ORIENTATION_SN:
                    fh = (int)((float)myvalue*((float)h/(float)mymax));
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
    }
    public void CommandStatsReceived(CrossfireCommandStatsEvent evt)
    {
        Stats s = evt.getStats();
        switch (mystat)
        {
            case Stats.CS_STAT_HP:
                mymax = s.getStat(Stats.CS_STAT_MAXHP);
                mymin = 0;
                myvalue = s.getStat(mystat);
                break;
            case Stats.CS_STAT_SP:
                mymax = s.getStat(Stats.CS_STAT_MAXSP);
                mymin = 0;
                myvalue = s.getStat(mystat);
                break;
            case Stats.CS_STAT_FOOD:
                mymax = 1000;
                mymin = 0;
                myvalue = s.getStat(mystat);
                break;
            case Stats.CS_STAT_GRACE:
                mymax = s.getStat(Stats.CS_STAT_MAXGRACE);
                mymin = -(s.getStat(Stats.CS_STAT_MAXGRACE));
                myvalue = s.getStat(mystat);
                break;
            default:
                if ((mystat >= Stats.CS_STAT_RESIST_START)&&(mystat <= Stats.CS_STAT_RESIST_END))
                {
                    mymax = 100;
                    mymin = -100;
                    myvalue = s.getStat(mystat);
                    break;
                }
        }
        render();
    }
}

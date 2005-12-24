package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUIButton extends GUIElement
{
    private BufferedImage mypicture_up;
    private BufferedImage mypicture_down;
    private java.util.List<GUICommand> mylist;
    private String mytext = null;
    private Font myfont = null;
    private int mytx = 0;
    private int myty = 0;
    private Color myfontcolor = new Color(255,255,255);

    public GUIButton
            (String nn, int nx, int ny, int nw, int nh, String picup,
                     String picdown, java.util.List<GUICommand> cmd) throws IOException
    {
        mypicture_up   = javax.imageio.ImageIO.read(new File(picup));
        mypicture_down = javax.imageio.ImageIO.read(new File(picdown));
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        mylist = cmd;
        mybuffer   = javax.imageio.ImageIO.read(new File(picup));
        render();
        myname = nn;
    }
    public GUIButton
            (String nn, int nx, int ny, int nw, int nh, String picup,
             String picdown, String txt, Font f, Color mfc,
             int tx, int ty, java.util.List<GUICommand> cmd) throws IOException
    {
        mypicture_up   = javax.imageio.ImageIO.read(new File(picup));
        mypicture_down = javax.imageio.ImageIO.read(new File(picdown));
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        mylist = cmd;
        mybuffer   = javax.imageio.ImageIO.read(new File(picup));
        myname = nn;
        mytext = txt;
        myfont = f;
        myfontcolor = mfc;
        mytx = tx;
        myty = ty;
        render();
    }
    public void mouseReleased(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        int b = e.getButton();
        switch(b)
        {
            case MouseEvent.BUTTON1:
                Iterator it = mylist.iterator();
                while (it.hasNext())
                {
                    ((GUICommand)it.next()).execute();
                }
                active = false;
                JXCWindow jxc = (JXCWindow)(e.getSource());
                jxc.deactivateCurrentElement();
                render();
                break;
            case MouseEvent.BUTTON2:
                break;
            case MouseEvent.BUTTON3:
                break;
        }
    }
    public void mousePressed(MouseEvent e)
    {
        active = true;
        render();
    }
    public void render()
    {
        Graphics2D g = mybuffer.createGraphics();
        g.setFont(myfont);
        g.setColor(myfontcolor);
        if (active)
            g.drawImage(mypicture_down, 0, 0, null);
        else
            g.drawImage(mypicture_up, 0, 0, null);
        if (mytext != null)
        {
            g.drawString(mytext, mytx, myty);
        }
        g.dispose();
    }
}
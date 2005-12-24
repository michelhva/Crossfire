package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUIText extends GUIElement implements KeyListener
{
    private BufferedImage mybackground_active;
    private BufferedImage mybackground_inactive;
    protected Font myfont;
    protected String mytext;
    public GUIText
            (String nn, int nx, int ny, int nw, int nh, String picactive,
                   String picinactive, Font nf, String txt)  throws IOException
    {
        mybackground_active = javax.imageio.ImageIO.read(new File(picactive));
        mybackground_inactive = javax.imageio.ImageIO.read(new File(picinactive));
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myfont = nf;
        mytext = txt;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        render();
        myname = nn;
    }
    public void setText(String nt)
    {
        mytext = nt;
        render();
    }
    public String getText()
    {
        return mytext;
    }
    protected void render()
    {
        synchronized(mybuffer)
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice      gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gconf = gd.getDefaultConfiguration();
            mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);

            Graphics2D g = mybuffer.createGraphics();
            if (active)
            {
                g.drawImage(mybackground_active, 0, 0, null);
                g.setColor(Color.WHITE);
            }
            else
            {
                g.drawImage(mybackground_inactive, 0, 0, null);
                g.setColor(Color.GRAY);
            }
            g.setFont(myfont);
            g.drawString(mytext, 4, myfont.getSize()+2);
            g.dispose();
        }
    }
    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        int b = e.getButton();
        switch(b)
        {
            case MouseEvent.BUTTON1:
                active = true;
                render();
                break;
            case MouseEvent.BUTTON2:
                break;
            case MouseEvent.BUTTON3:
                break;
        }
    }
    public void setActive(boolean act)
    {
        active = act;
        render();
    }
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_BACK_SPACE:
                if (mytext.length()>0)
                {
                    mytext = mytext.substring(0,mytext.length()-1);
                    render();
                }
                break;
            case KeyEvent.VK_DELETE:
                if (mytext.length()>0)
                {
                    mytext = "";
                    render();
                }
                break;
            case KeyEvent.VK_SHIFT:
                break;
            case KeyEvent.VK_ENTER:
                setActive(false);
                break;
            default:
                char chr = e.getKeyChar();
                mytext = mytext+chr;
                render();
                break;
        }
    }
    public void keyReleased(KeyEvent e)
    {
    }
    public void keyTyped(KeyEvent e)
    {

    }
}

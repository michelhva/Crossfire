package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUIElement implements MouseListener
{
    protected int x, y, w, h;
    protected BufferedImage mybuffer;
    protected boolean visible = true;
    protected String myname="Element";
    protected boolean active = false;
    protected boolean visiblechanged = false;

    public String toString()
    {
        return myname;
    }
    public void setActive(boolean act)
    {
        active = act;
    }
    public boolean isActive()
    {
        return active;
    }
    public BufferedImage getBuffer()
    {
        synchronized(mybuffer)
        {
            return mybuffer;
        }
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public int getWidth()
    {
        return w;
    }
    public int getHeight()
    {
        return h;
    }
    public boolean isVisible()
    {
        return visible;
    }
    public void setVisible(boolean v)
    {
        visible = v;
        visiblechanged = true;
    }
    public String getName()
    {
        return myname;
    }

    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        int b = e.getButton();
        switch(b)
        {
            case MouseEvent.BUTTON1:
                System.out.println("Hit element : "+myname);
                System.out.println("Position    : "+x+";"+y);
                break;
            case MouseEvent.BUTTON2:
                break;
            case MouseEvent.BUTTON3:
                break;
        }
    }
    public void mouseEntered(MouseEvent e)
    {
    }
    public void mouseExited(MouseEvent e)
    {
    }
    public void mousePressed(MouseEvent e)
    {
        active = true;
    }
    public void mouseReleased(MouseEvent e)
    {
        mouseClicked(e);
    }
}
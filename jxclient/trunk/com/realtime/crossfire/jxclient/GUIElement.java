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
import java.awt.event.*;
import java.awt.image.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
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
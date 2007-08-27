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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public abstract class GUIElement implements MouseListener
{
    /**
     * The {@link Gui} this element is part of. Set to <code>null</code> if
     * this element is not part of any gui.
     */
    private Gui gui = null;

    /**
     * The x-coordinate for drawing this element to screen.
     */
    protected int x;

    /**
     * The y-coordinate for drawing this element to screen.
     */
    protected int y;

    /**
     * The width for drawing this element to screen.
     */
    protected int w;

    /**
     * The height for drawing this element to screen.
     */
    protected int h;

    protected BufferedImage mybuffer;

    protected boolean visible = true;

    /**
     * Whether this gui element should be ignored for user interaction.
     */
    private boolean ignore = false;

    /**
     * The name of this element.
     */
    protected final String myname;

    protected boolean active = false;

    /**
     * Whether {@link #mybuffer} has changed.
     */
    private boolean changed;

    /**
     * The {@link JXCWindow} this gui element belongs to.
     */
    private final JXCWindow jxcWindow;

    /**
     * The tooltip text to show when the mouse is inside this element. May be
     * <code>null</code> to show no tooltip.
     */
    private String tooltipText = null;

    /**
     * Create a new instance.
     *
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen.
     *
     * @param y The y-coordinate for drawing this element to screen.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     */
    protected GUIElement(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h)
    {
        this.jxcWindow = jxcWindow;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        myname = name;
    }

    /**
     * Return the {@link Gui} this element is part of.
     *
     * @return The gui, or <code>null</code>.
     */
    public Gui getGui()
    {
        return gui;
    }

    /**
     * Set the {@link Gui} this element is part of.
     *
     * @param gui The gui, or <code>null</code>.
     */
    public void setGui(final Gui gui)
    {
        this.gui = gui;
    }

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
        return mybuffer;
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
        if (visible != v)
        {
            visible = v;
            setChanged();
        }
    }

    /**
     * Return whether this gui element should be ignored for user interaction.
     *
     * @return Whether this gui element should be ignored for user interaction.
     */
    public boolean isIgnore()
    {
        return ignore;
    }

    /**
     * Mark this gui element to be ignored for user interaction.
     */
    public void setIgnore()
    {
        ignore = true;
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
        final JXCWindow jxcw = (JXCWindow)e.getSource();
        jxcw.setTooltipElement(this);
    }

    public void mouseExited(MouseEvent e)
    {
        final JXCWindow jxcw = (JXCWindow)e.getSource();
        jxcw.unsetTooltipElement(this);
    }

    public void mousePressed(MouseEvent e)
    {
        active = true;
    }

    public void mouseReleased(MouseEvent e)
    {
        mouseClicked(e);
    }

    /**
     * Return whether {@link #mybuffer} has changed.
     *
     * @return whether <code>mybuffer</code> has changed
     */
    public boolean hasChanged()
    {
        return changed;
    }

    /**
     * Record that {@link #mybuffer} has changed.
     */
    protected void setChanged()
    {
        changed = true;
    }

    /**
     * Clear the flag that {@link #mybuffer} has changed.
     */
    public void resetChanged()
    {
        changed = false;
    }

    /**
     * Set the tooltip text to show when the mouse is inside this element.
     *
     * @param tooltipText The text to show, or <code>null</cod> to disable the
     * tooltip for this element.
     */
    public void setTooltipText(final String tooltipText)
    {
        if (this.tooltipText == null)
        {
            if (tooltipText == null)
            {
                return;
            }
        }
        else
        {
            if (tooltipText != null && tooltipText.equals(this.tooltipText))
            {
                return;
            }
        }
        this.tooltipText = tooltipText;
        jxcWindow.updateTooltipElement(this);
    }

    /**
     * Return the tooltip text to show when the mouse is inside this element.
     *
     * @return The text to show, or <code>null</cod> to disable the tooltip for
     * this element.
     */
    public String getTooltipText()
    {
        return tooltipText;
    }

    /**
     * Change the location of this gui element.
     *
     * @param x The new x-coordinate.
     *
     * @param y The new y-coordinate.
     */
    public void setLocation(final int x, final int y)
    {
        if (this.x != x || this.y != y)
        {
            this.x = x;
            this.y = y;
            setChanged();
        }
    }

    /**
     * Change the size of this gui element.
     *
     * @param w The new width.
     *
     * @param h The new height.
     */
    public void setSize(final int w, final int h)
    {
        if (this.w != w || this.h != h)
        {
            this.w = w;
            this.h = h;
            createBuffer();
        }
    }

    protected abstract void createBuffer();
}

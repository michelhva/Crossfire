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

import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public abstract class GUIElement
{
    /**
     * The {@link Gui} this element is part of. Set to <code>null</code> if
     * this element is not part of any gui.
     */
    private Gui gui = null;

    /**
     * The x-coordinate for drawing this element to screen.
     */
    private int x;

    /**
     * The y-coordinate for drawing this element to screen.
     */
    private int y;

    /**
     * The width for drawing this element to screen.
     */
    protected int w;

    /**
     * The height for drawing this element to screen.
     */
    protected int h;

    protected BufferedImage bufferedImage;

    private boolean visible = true;

    private boolean isDefault = false;

    /**
     * Whether this gui element should be ignored for user interaction.
     */
    private boolean ignore = false;

    /**
     * The transparency for {@link #bufferedImage}.
     */
    private final int transparency;

    /**
     * The name of this element.
     */
    private final String name;

    /**
     * Whether {@link #bufferedImage} has changed.
     */
    private boolean changed;

    /**
     * The {@link JXCWindow} this gui element belongs to.
     */
    private final JXCWindow window;

    /**
     * The tooltip text to show when the mouse is inside this element. May be
     * <code>null</code> to show no tooltip.
     */
    private String tooltipText = null;

    /**
     * Create a new instance.
     *
     * @param window The <code>JXCWindow</code> this element belongs to.
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param y The y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param transparency The transparency value for the backing buffer
     */
    protected GUIElement(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final int transparency)
    {
        this.window = window;
        this.name = name;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.transparency = transparency;
        createBuffer();
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
        if (visible && gui != null)
        {
            gui.setChangedElements();
        }
    }

    @Override public String toString()
    {
        return name;
    }

    public int setElementX()
    {
        return gui != null ? gui.getX()+x : x;
    }

    public int setElementY()
    {
        return gui != null ? gui.getY()+y : y;
    }

    public int getWidth()
    {
        return w;
    }

    public int getHeight()
    {
        return h;
    }

    public boolean isElementVisible()
    {
        return visible;
    }

    public void setElementVisible(final boolean visible)
    {
        if (this.visible != visible)
        {
            this.visible = visible;
            setChanged();
            final Gui tmpGui = gui;
            if (tmpGui != null)
            {
                tmpGui.updateVisibleElement(this);
            }
        }
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefault(final boolean isDefault)
    {
        this.isDefault = isDefault;
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
        return name;
    }

    /**
     * Will be called when the user has clicked (pressed+released) this
     * element. This event will be delivered after {@link
     * #mouseReleased(MouseEvent)}.
     *
     * @param e The mouse event relative to this element.
     */
    public void mouseClicked(final MouseEvent e)
    {
        window.getWindowRenderer().raiseDialog(gui);
    }

    /**
     * Will be called when the mouse has entered the bounding box of this
     * element.
     *
     * @param e The mouse event relative to this element.
     */
    public void mouseEntered(final MouseEvent e)
    {
        ((JXCWindow)e.getSource()).getTooltipManager().setElement(this);
    }

    /**
     * Will be called when the mouse has left the bounding box of this element.
     * This function will not be called unless {@link
     * #mouseEntered(MouseEvent)} has been called before.
     *
     * @param e The mouse event relative to this element.
     */
    public void mouseExited(final MouseEvent e)
    {
        ((JXCWindow)e.getSource()).getTooltipManager().unsetElement(this);
    }

    /**
     * Will be called when the user has pressed the mouse inside this element.
     *
     * @param e The mouse event relative to this element.
     */
    public void mousePressed(final MouseEvent e)
    {
    }

    /**
     * Will be called when the user has released the mouse. This event may be
     * deleivered even if no previous {@link #mousePressed(MouseEvent)} has
     * been delivered before.
     *
     * @param e The mouse event relative to this element.
     */
    public void mouseReleased(final MouseEvent e)
    {
    }

    /**
     * Will be called when the mouse moves within this component.
     * before.
     *
     * @param e The mouse event relative to this element.
     */
    public void mouseMoved(final MouseEvent e)
    {
    }

    /**
     * Will be called when the mouse moves within this component while the
     * button is pressed. This event will be delivered after {@link
     * #mouseMoved(MouseEvent)}.
     *
     * <p>Note: if the mouse leaves this elements's bounding box while the
     * mouse button is still pressed, further <code>mouseDragged</code> (but no
     * <code>mouseMoved</code>) events will be generated.
     *
     * @param e The mouse event relative to this element.
     */
    public void mouseDragged(final MouseEvent e)
    {
    }

    /**
     * Record that {@link #bufferedImage} has changed and must be repainted.
     */
    public void setChanged()
    {
        if (changed)
        {
            return;
        }

        changed = true;
        if (visible && gui != null)
        {
            gui.setChangedElements();
        }
    }

    /**
     * Returns the changed flag.
     * @return the changed flag
     */
    public boolean isChanged()
    {
        return changed;
    }

    /**
     * Clears the changed flag.
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
        window.getTooltipManager().updateElement(this);
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
    public void setElementLocation(final int x, final int y)
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
    public void setElementSize(final int w, final int h)
    {
        if (this.w != w || this.h != h)
        {
            this.w = w;
            this.h = h;
            createBuffer();
        }
    }

    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        bufferedImage = gconf.createCompatibleImage(w, h, transparency);
        setChanged();
    }

    /**
     * Return the {@link JXCWindow} this gui element belongs to.
     *
     * @return The {@link JXCWindow} this gui element belongs to.
     */
    public JXCWindow getWindow()
    {
        return window;
    }

    /**
     * Re-create the contents of {@link #bufferedImage}.
     */
    protected void render()
    {
        synchronized (bufferedImage)
        {
            final Graphics2D g = bufferedImage.createGraphics();
            try
            {
                render(g);
            }
            finally
            {
                g.dispose();
            }
        }
    }

    /**
     * Paint the elements's contents into the passed graphics.
     *
     * @param g The gaphics to paint to.
     */
    protected void render(final Graphics2D g)
    {
    }

    /**
     * Draws this image into the given graphics instance.
     * @param g the graphics instance
     */
    public void drawImage(final Graphics g)
    {
        synchronized (bufferedImage)
        {
            if (changed)
            {
                changed = false;
                render();
            }

            g.drawImage(bufferedImage, setElementX(), setElementY(), window);
        }
    }
}

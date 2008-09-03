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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * A dialog title that allows to move the dialog.
 *
 * @author Andreas Kirschbaum
 */
public class GUIDialogTitle extends GUIPicture
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * Set to the distance of the dialog cordinates relative to the mouse
     * position while dragging start. Else set to <code>null</code>.
     */
    private Point offset = null;

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
     * @param image The picture to paint.
     *
     * @param alpha The transparency value.
     */
    public GUIDialogTitle(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final BufferedImage image, final float alpha)
    {
        super(window, name, x, y, w, h, image, alpha);
    }

    /* {@inheritDoc} */
    @Override
    public void mousePressed(final MouseEvent e)
    {
        super.mousePressed(e);
        final Gui gui = getGui();
        if (gui == null)
        {
            offset = null;
            return;
        }

        final Point point = e.getLocationOnScreen();
        offset = new Point(gui.getX()-point.x, gui.getY()-point.y);
    }

    /* {@inheritDoc} */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
        super.mouseReleased(e);
        moveTo(e);
        offset = null;
    }

    /* {@inheritDoc} */
    @Override
    public void mouseDragged(final MouseEvent e)
    {
        super.mouseDragged(e);
        moveTo(e);
    }

    /**
     * Move the dialog the given point.
     *
     * @param e The destination point.
     */
    private void moveTo(final MouseEvent e)
    {
        if (offset == null)
        {
            return;
        }

        final Gui gui = getGui();
        if (gui == null)
        {
            offset = null;
            return;
        }

        final Point point = e.getLocationOnScreen();
        final int newX = Math.max(Math.min(point.x+offset.x, getWindow().getWindowWidth()-gui.getWidth()), 0);
        final int newY = Math.max(Math.min(point.y+offset.y, getWindow().getWindowHeight()-gui.getHeight()), 0);
        gui.setPosition(newX, newY);
    }
}

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
package com.realtime.crossfire.jxclient.gui.gauge;

import com.realtime.crossfire.jxclient.gui.GUIElement;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The state of a gauge.
 * @author Andreas Kirschbaum
 */
public class GaugeState
{
    /**
     * The owner gui element.
     */
    private final GUIElement owner;

    /**
     * The image representing a full gauge.
     */
    private final BufferedImage fullImage;

    /**
     * The image representing a more-than-empty gauge.
     */
    private final BufferedImage negativeImage;

    /**
     * The x-offset for drawing.
     */
    private final int dx;

    /**
     * The y-offset for drawing.
     */
    private final int dy;

    /**
     * The width of the "filled" area.
     */
    private int filledW = 0;

    /**
     * The height of the "filled" area.
     */
    private int filledH = 0;

    /**
     * The x-coordinate of the "filled" area.
     */
    private int filledX = 0;

    /**
     * The y-coordinate of the "filled" area.
     */
    private int filledY = 0;

    /**
     * The image for painting the "filled" area.
     */
    private BufferedImage filledPicture = null;

    /**
     * Createss a new instance.
     * @param owner the owner gui element
     * @param fullImage the image representing a full gauge
     * @param negativeImage the image representing a more-than-empty gauge; if
     * set to <code>null</code> the gauge remains in empty state
     * @param dx the x-offset for drawing
     * @param dy the y-offset for drawing
     */
    public GaugeState(final GUIElement owner, final BufferedImage fullImage, final BufferedImage negativeImage, final int dx, final int dy)
    {
        this.owner = owner;
        this.fullImage = fullImage;
        this.negativeImage = negativeImage;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Draws the given part of a picture to {@link #buffer}.
     * @param orientation the state
     */
    public void draw(final Orientation orientation)
    {
        final int filledX = orientation.getX();
        final int filledY = orientation.getY();
        final int filledW = orientation.getW();
        final int filledH = orientation.getH();
        final BufferedImage filledPicture = !orientation.isValid() ? null : orientation.isNegativeImage() ? negativeImage : fullImage;

        if (this.filledX == filledX && this.filledY == filledY && this.filledW == filledW && this.filledH == filledH && this.filledPicture == filledPicture)
        {
            return;
        }

        this.filledX = filledX;
        this.filledY = filledY;
        this.filledW = filledW;
        this.filledH = filledH;
        this.filledPicture = filledPicture;
        owner.setChanged();
    }

    /**
     * Draw the gauge image into the given graphics context.
     * @param g the graphics context
     */
    public void draw(final Graphics2D g)
    {
        if (filledPicture != null)
        {
            g.drawImage(filledPicture, filledX+dx, filledY+dy, filledX+dx+filledW, filledY+dy+filledH, filledX, filledY, filledX+filledW, filledY+filledH, null);
        }
    }
}

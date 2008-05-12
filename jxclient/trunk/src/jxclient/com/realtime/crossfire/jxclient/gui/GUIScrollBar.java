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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

/**
 * A scroll bar gui element.
 *
 * @author Andreas Kirschbaum
 */
public class GUIScrollBar extends ActivatableGUIElement implements ScrollableListener
{
    /**
     * If set, make the slider size reflect the visible area; if unset, display
     * the slider as a square.
     */
    private final boolean proportionalSlider;

    /**
     * The target element to scroll.
     */
    private final GUIScrollable2 scrollable;

    /**
     * The background color of the slider.
     */
    private final Color colorBackground;

    /**
     * The foreground color of the slider.
     */
    private final Color colorForeground;

    /**
     * The minimum scroll value.
     */
    private int valueMin = 0;

    /**
     * The size of the scoll values.
     */
    private int valueSize = 1;

    private int sliderSize = 1;

    /**
     * The scroll location; it need not within the scroll range.
     */
    private int sliderPos = 0;

    /**
     * The offset between the mouse and the top border of the slider while
     * dragging.
     */
    private int offset = 0;

    /**
     * Set while dragging is active.
     */
    private boolean scrolling = false;

    /**
     * Create a new instance.
     *
     * @param window The <code>JXCWindow</code> this element belongs to.
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
     *
     * @param proportionalSlider If set, make the slider size reflect the
     * visible area; if unset, display the slider as a square.
     *
     * @param scrollable The target element to scroll.
     *
     * @param colorBackground The background color of the slider.
     *
     * @param colorForeground The foreground color of the slider.
     */
    public GUIScrollBar(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final boolean proportionalSlider, final GUIScrollable2 scrollable, final Color colorBackground, final Color colorForeground)
    {
        super(window, name, x, y, w, h, Transparency.OPAQUE);
        if (scrollable == null) throw new IllegalArgumentException();
        this.proportionalSlider = proportionalSlider;
        this.scrollable = scrollable;
        this.colorBackground = colorBackground;
        this.colorForeground = colorForeground;
        scrollable.addScrollableListener(this);
    }

    /** {@inheritDoc} */
    public void setRange(final int valueMin, final int valueMax, final int sliderPos, final int sliderSize)
    {
        if (valueMax <= valueMin) throw new IllegalArgumentException();
        if (sliderSize <= 0) throw new IllegalArgumentException();

        this.valueMin = valueMin;
        valueSize = valueMax-valueMin;
        this.sliderSize = Math.min(sliderSize, valueSize);
        setPosition(sliderPos);
    }

    /** {@inheritDoc} */
    @Override public void mousePressed(final MouseEvent e)
    {
        super.mousePressed(e);
        switch (e.getButton())
        {
        case MouseEvent.BUTTON1:
            final int sh = getSliderHeightPixels();
            offset = e.getY()-getSliderPosPixels(sh);
            if (offset < 0)
            {
                scrollable.scrollTo(sliderPos-sliderSize);
            }
            else if (offset >= sh)
            {
                scrollable.scrollTo(sliderPos+sliderSize);
            }
            else
            {
                scrolling = true;
            }
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    @Override public void mouseReleased(final MouseEvent e)
    {
        super.mouseReleased(e);
        switch (e.getButton())
        {
        case MouseEvent.BUTTON1:
            scrolling = false;
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    @Override public void mouseDragged(final MouseEvent e)
    {
        super.mouseDragged(e);
        if (scrolling)
        {
            scrollable.scrollTo(getSliderPos(e.getY()-offset));
        }
    }

    /**
     * Return the current slider position in slider-coordinates.
     *
     * @param yPixels The y-coordinate within the gui element.
     *
     * @return The position in slider-coordinates.
     */
    private int getSliderPos(final int yPixels)
    {
        return (int)(yPixels*(double)(valueSize-sliderSize)/(h-getSliderHeightPixels())+0.5);
    }

    public void setPosition(final int pos)
    {
        if (pos < valueMin)
        {
            sliderPos = valueMin;
        }
        else if (pos+sliderSize > valueMin+valueSize)
        {
            sliderPos = valueMin+valueSize-sliderSize;
        }
        else
        {
            sliderPos = pos;
        }
        setChanged();
    }

    /**
     * Return the height of the slider in pixels.
     *
     * @return The height of the slider in pixels.
     */
    private int getSliderHeightPixels()
    {
        return proportionalSlider ? (int)(h*(double)sliderSize/valueSize+0.5) : w;
    }

    /**
     * Return the y-coordinate of the slider.
     *
     * @param sh The height of the slider in pixels.
     *
     * @return The y-coordinate.
     */
    private int getSliderPosPixels(final int sh)
    {
        return (int)(sliderPos*(double)(h-sh)/(valueSize-sliderSize)+0.5);
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        final int sh = getSliderHeightPixels();
        final int sy = getSliderPosPixels(sh);
        g.setColor(colorBackground);
        g.fillRect(0, 0, w, sy);
        g.fillRect(0, sy+sh, w, h-sy-sh);
        g.setColor(colorForeground);
        g.fillRect(0, sy, w, sh);
    }
}

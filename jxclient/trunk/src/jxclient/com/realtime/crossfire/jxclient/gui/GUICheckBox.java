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

import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOptionListener;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Transparency;

/**
 * A check box gui element.
 *
 * @author Andreas Kirschbaum
 */
public class GUICheckBox extends GUIElement
{
    /**
     * The image for the checked [x] state.
     */
    private final BufferedImage checkedImage;

    /**
     * The image for the unchecked [ ] state.
     */
    private final BufferedImage uncheckedImage;

    /**
     * The text.
     */
    private final String text;

    /**
     * The font to use.
     */
    private final Font font;

    /**
     * The text color.
     */
    private final Color color;

    /**
     * The option to display.
     */
    private final CheckBoxOption option;

    /**
     * Create a new instance.
     *
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
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
     * @param checkedImage The image for the checked state.
     *
     * @param uncheckedImage The image for the unchecked state.
     *
     * @param font The font to use.
     *
     * @param color The text color.
     *
     * @param option The option to display.
     *
     * @param text The text to display.
     */
    public GUICheckBox(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage checkedImage, final BufferedImage uncheckedImage, final Font font, final Color color, final CheckBoxOption option, final String text)
    {
        super(jxcWindow, name, x, y, w, h);
        if (checkedImage == null) throw new IllegalArgumentException("missing checked image");
        if (uncheckedImage == null) throw new IllegalArgumentException("missing unchecked image");
        if (checkedImage.getHeight() != h) throw new IllegalArgumentException("'checked' height is "+checkedImage.getHeight()+" but checkbox height is "+h);
        if (uncheckedImage.getHeight() != h) throw new IllegalArgumentException("'unchecked' height is "+uncheckedImage.getHeight()+" but checkbox height is "+h);
        if (checkedImage.getWidth() != uncheckedImage.getWidth()) throw new IllegalArgumentException("'checked' width is "+checkedImage.getWidth()+" but 'unchecked' width is "+uncheckedImage.getWidth());
        if (checkedImage.getWidth() >= w) throw new IllegalArgumentException("'checked' width is "+checkedImage.getWidth()+" but checkbox width is "+w);
        if (uncheckedImage.getWidth() >= w) throw new IllegalArgumentException("'unchecked' width is "+uncheckedImage.getWidth()+" but checkbox width is "+w);
        if (font == null) throw new IllegalArgumentException("missing font");
        if (color == null) throw new IllegalArgumentException("missing color");
        if (option == null) throw new IllegalArgumentException("missing option");
        if (text == null) throw new IllegalArgumentException("missing text");

        this.checkedImage = checkedImage;
        this.uncheckedImage = uncheckedImage;
        this.text = text;
        this.font = font;
        this.color = color;
        this.option = option;

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        option.addCheckBoxOptionListener(new CheckBoxOptionListener()
            {
                /** {@inheritDoc} */
                public void checkedChanged(final CheckBoxOption option)
                {
                    render();
                }
            });
        render();
    }

    /** {@inheritDoc} */
    public void render()
    {
        final Graphics2D g = mybuffer.createGraphics();
        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, w, h);
        g.setFont(font);
        g.setColor(color);
        g.drawImage(option.isChecked() ? checkedImage : uncheckedImage, 0, 0, null);
        final Rectangle2D rect = font.getStringBounds(text, g.getFontRenderContext());
        final int y = (int)Math.round((h-rect.getMaxY()-rect.getMinY()))/2;
        g.drawString(text, checkedImage.getWidth()+4, y);
        g.dispose();
        setChanged();
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override public void mouseReleased(final MouseEvent e)
    {
        super.mouseReleased(e);
        final int b = e.getButton();
        switch (b)
        {
        case MouseEvent.BUTTON1:
            option.toggleChecked();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    @Override public void mousePressed(final MouseEvent e)
    {
        super.mousePressed(e);
        final int b = e.getButton();
        switch (b)
        {
        case MouseEvent.BUTTON1:
            setActive(true);
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }
}

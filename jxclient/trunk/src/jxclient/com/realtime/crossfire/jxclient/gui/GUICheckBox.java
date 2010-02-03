/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionListener;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;

/**
 * A check box gui element.
 *
 * @author Andreas Kirschbaum
 */
public class GUICheckBox extends ActivatableGUIElement
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The image for the checked [x] state.
     */
    @NotNull
    private final BufferedImage checkedImage;

    /**
     * The image for the unchecked [ ] state.
     */
    @NotNull
    private final Image uncheckedImage;

    /**
     * The text.
     */
    @NotNull
    private final String text;

    /**
     * The font to use.
     */
    @NotNull
    private final Font font;

    /**
     * The text color.
     */
    @NotNull
    private final Color color;

    /**
     * The option to display.
     */
    @NotNull
    private final CheckBoxOption option;

    /**
     * The {@link OptionListener} attached to {@link #option}.
     */
    @NotNull
    private final OptionListener optionListener = new OptionListener()
    {
        /** {@inheritDoc} */
        @Override
        public void stateChanged()
        {
            setChanged();
        }
    };

    /**
     * Create a new instance.
     *
     * @param tooltipManager the tooltip manager to update
     *
     * @param windowRenderer the window renderer to notify
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
    public GUICheckBox(@NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final BufferedImage checkedImage, @NotNull final BufferedImage uncheckedImage, @NotNull final Font font, @NotNull final Color color, @NotNull final CheckBoxOption option, @NotNull final String text)
    {
        super(tooltipManager, windowRenderer, name, x, y, w, h, Transparency.TRANSLUCENT);
        if (checkedImage.getHeight() != h) throw new IllegalArgumentException("'checked' height is "+checkedImage.getHeight()+" but checkbox height is "+h);
        if (uncheckedImage.getHeight() != h) throw new IllegalArgumentException("'unchecked' height is "+uncheckedImage.getHeight()+" but checkbox height is "+h);
        if (checkedImage.getWidth() != uncheckedImage.getWidth()) throw new IllegalArgumentException("'checked' width is "+checkedImage.getWidth()+" but 'unchecked' width is "+uncheckedImage.getWidth());
        if (checkedImage.getWidth() >= w) throw new IllegalArgumentException("'checked' width is "+checkedImage.getWidth()+" but checkbox width is "+w);
        if (uncheckedImage.getWidth() >= w) throw new IllegalArgumentException("'unchecked' width is "+uncheckedImage.getWidth()+" but checkbox width is "+w);

        this.checkedImage = checkedImage;
        this.uncheckedImage = uncheckedImage;
        this.text = text;
        this.font = font;
        this.color = color;
        this.option = option;
        this.option.addOptionListener(optionListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
        option.removeOptionListener(optionListener);
    }

    /** {@inheritDoc} */
    @Override
    protected void render(@NotNull final Graphics g)
    {
        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setFont(font);
        g.setColor(color);
        g.drawImage(option.isChecked() ? checkedImage : uncheckedImage, 0, 0, null);
        final RectangularShape rect = font.getStringBounds(text, g2.getFontRenderContext());
        final int y = (int)Math.round((getHeight()-rect.getMaxY()-rect.getMinY()))/2;
        g.drawString(text, checkedImage.getWidth()+4, y);
    }

    /** {@inheritDoc} */
    @Override
    public void mouseReleased(@NotNull final MouseEvent e)
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
    @Override
    protected void activeChanged()
    {
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(@NotNull final MouseEvent e)
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

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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.misc;

import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionListener;
import java.awt.Color;
import java.awt.Dimension;
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
 * @author Andreas Kirschbaum
 */
public class GUICheckBox extends ActivatableGUIElement {

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
    private final OptionListener optionListener = new OptionListener() {

        @Override
        public void stateChanged() {
            setChanged();
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param checkedImage the image for the checked state
     * @param uncheckedImage the image for the unchecked state
     * @param font the font to use
     * @param color the text color
     * @param option the option to display
     * @param text the text to display
     */
    public GUICheckBox(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final BufferedImage checkedImage, @NotNull final BufferedImage uncheckedImage, @NotNull final Font font, @NotNull final Color color, @NotNull final CheckBoxOption option, @NotNull final String text) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT);
        if (checkedImage.getWidth() != uncheckedImage.getWidth()) {
            throw new IllegalArgumentException("'checked' width is "+checkedImage.getWidth()+" but 'unchecked' width is "+uncheckedImage.getWidth());
        }

        this.checkedImage = checkedImage;
        this.uncheckedImage = uncheckedImage;
        this.text = text;
        this.font = font;
        this.color = color;
        this.option = option;
        this.option.addOptionListener(optionListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        option.removeOptionListener(optionListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D)g;
        g2.setFont(font);
        g2.setColor(color);
        g2.drawImage(option.isChecked() ? checkedImage : uncheckedImage, 0, 0, null);
        final RectangularShape rectangle = font.getStringBounds(text, g2.getFontRenderContext());
        final int y = (int)Math.round(getHeight()-rectangle.getMaxY()-rectangle.getMinY())/2;
        g2.drawString(text, checkedImage.getWidth()+4, y);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getMinimumSize() {
        return getMinimumSizeInt();
    }

    /**
     * Returns the minimal size needed to display both icon and text.
     * @return the minimal size
     */
    @NotNull
    private Dimension getMinimumSizeInt() {
        final Dimension result = GuiUtils.getTextDimension(text, getFontMetrics(font));
        result.width += checkedImage.getWidth()+4;
        result.height = checkedImage.getHeight();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void activeChanged() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        final int b = e.getButton();
        switch (b) {
        case MouseEvent.BUTTON1:
            option.toggleChecked();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        super.mousePressed(e);
        final int b = e.getButton();
        switch (b) {
        case MouseEvent.BUTTON1:
            setActive(true);
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(@NotNull final MouseEvent e, final boolean debugGui) {
        if (!hasTooltipText()) { // XXX: properly initialize tooltip text
            setTooltipText(option.getTooltipText());
        }
        super.mouseEntered(e, debugGui);
    }

}

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

package com.realtime.crossfire.jxclient.gui.button;

import com.realtime.crossfire.jxclient.gui.command.GUICommandList;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIButton extends AbstractButton
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    @NotNull
    private final Image imageUp;

    @NotNull
    private final Image imageDown;

    @Nullable
    private final String text;

    @Nullable
    private final Font font;

    private final int textX;

    private final int textY;

    @Nullable
    private final Color color;

    public GUIButton(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final BufferedImage imageUp, @NotNull final BufferedImage imageDown, @Nullable final String text, @Nullable final Font font, @Nullable final Color color, final int textX, final int textY, final boolean autoRepeat, @NotNull final GUICommandList commandList)
    {
        super(tooltipManager, elementListener, name, x, y, w, h, Transparency.TRANSLUCENT, autoRepeat, commandList);
        if (imageUp.getWidth() != imageDown.getWidth())
        {
            throw new IllegalArgumentException();
        }
        if (imageUp.getHeight() != imageDown.getHeight())
        {
            throw new IllegalArgumentException();
        }
        this.imageUp = imageUp;
        this.imageDown = imageDown;
        this.text = text;
        this.font = font;
        this.color = color;
        this.textX = textX;
        this.textY = textY;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /** {@inheritDoc} */
    @Override
    public void activeChanged()
    {
        setChanged();
    }

    /** {@inheritDoc} */
    @Override
    protected void render(@NotNull final Graphics g)
    {
        g.setFont(font);
        g.setColor(color);
        g.drawImage(isActive() ? imageDown : imageUp, 0, 0, null);
        if (text != null)
        {
            g.drawString(text, textX, textY);
        }
    }
}

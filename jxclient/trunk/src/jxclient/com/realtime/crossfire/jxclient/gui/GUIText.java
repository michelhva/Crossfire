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
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;

/**
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class GUIText extends GUIElement implements KeyListener
{
    private final BufferedImage activeImage;

    private final BufferedImage inactiveImage;

    protected final Font font;

    private final Color inactiveColor;

    private final Color activeColor;

    private final StringBuilder text;

    /**
     * If set, hide input; else show input.
     */
    private boolean hideInput = false;

    /**
     * The cursor location.
     */
    private int cursor;

    public GUIText(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage activeImage, final BufferedImage inactiveImage, final Font font, final Color inactiveColor, final Color activeColor, final String text)
    {
        super(jxcWindow, name, x, y, w, h);
        this.activeImage = activeImage;
        this.inactiveImage = inactiveImage;
        this.font = font;
        this.inactiveColor = inactiveColor;
        this.activeColor = activeColor;
        this.text = new StringBuilder(text);
        cursor = this.text.length();
        createBuffer();
        render();
    }

    public void setText(final String text)
    {
        this.text.setLength(0);
        this.text.append(text);
        cursor = this.text.length();
        render();
    }

    public String getText()
    {
        return text.toString();
    }

    protected void render()
    {
        synchronized(mybuffer)
        {
            createBuffer();

            final Graphics2D g = mybuffer.createGraphics();
            g.drawImage(active ? activeImage : inactiveImage, 0, 0, null);
            g.setFont(font);
            final String tmp;
            if (hideInput)
            {
                final String template = "********************";
                final String hiddenText = template.substring(0, Math.min(text.length(), template.length()));
                tmp = hiddenText+" ";
            }
            else
            {
                tmp = text.toString()+" ";
            }

            final Rectangle2D rect = font.getStringBounds(tmp, g.getFontRenderContext());
            final int y = (int)Math.round((h-rect.getMaxY()-rect.getMinY()))/2;
            if (active)
            {
                final String tmpPrefix = tmp.substring(0, cursor);
                final String tmpCursor = tmp.substring(0, cursor+1);
                final Rectangle2D rectPrefix = font.getStringBounds(tmpPrefix, g.getFontRenderContext());
                final Rectangle2D rectCursor = font.getStringBounds(tmpCursor, g.getFontRenderContext());
                final int cursorX1 = (int)(rectPrefix.getWidth()+0.5);
                final int cursorX2 = (int)(rectCursor.getWidth()+0.5);
                g.setColor(inactiveColor);
                g.fillRect(cursorX1, 0, cursorX2-cursorX1, h);
            }
            g.setColor(active ? activeColor : inactiveColor);
            g.drawString(tmp, 0, y);

            g.dispose();
        }
        setChanged();
    }

    /** {@inheritDoc} */
    @Override public void mouseClicked(final MouseEvent e)
    {
        super.mouseClicked(e);
        final int b = e.getButton();
        switch (b)
        {
        case MouseEvent.BUTTON1:
            active = true;
            render();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    public void setActive(final boolean active)
    {
        this.active = active;
        render();
    }

    public void keyPressed(final KeyEvent e)
    {
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_BACK_SPACE:
            if (cursor > 0)
            {
                text.delete(cursor-1, cursor);
                cursor--;
                render();
            }
            break;

        case KeyEvent.VK_DELETE:
            if (cursor < text.length())
            {
                text.delete(cursor, cursor+1);
                render();
            }
            break;

        case KeyEvent.VK_ENTER:
            execute((JXCWindow)e.getSource(), text.toString());
            setActive(false);
            break;

        case KeyEvent.VK_LEFT:
            if (cursor > 0)
            {
                cursor--;
                render();
            }
            break;

        case KeyEvent.VK_RIGHT:
            if (cursor <text.length())
            {
                cursor++;
                render();
            }
            break;

        default:
            final char chr = e.getKeyChar();
            if (chr != KeyEvent.CHAR_UNDEFINED && chr >= ' ')
            {
                text.insert(cursor, chr);
                cursor++;
                render();
            }
            break;
        }
    }

    /** {@inheritDoc} */
    public void keyReleased(final KeyEvent e)
    {
    }

    /** {@inheritDoc} */
    public void keyTyped(final KeyEvent e)
    {
    }

    /**
     * Will be called to execute the entered command.
     *
     * @param jxcWindow The JXCWindow instance.
     *
     * @param command The entered command.
     */
    protected abstract void execute(final JXCWindow jxcWindow, final String command);

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        setChanged();
    }

    /**
     * Enable or disable hidden text.
     *
     * @param hideInput If set, hide input; else show input.
     */
    public void setHideInput(final boolean hideInput)
    {
        if (this.hideInput != hideInput)
        {
            this.hideInput = hideInput;
            render();
        }
    }
}

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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class GUIText extends ActivatableGUIElement implements KeyListener
{
    /**
     * The number of characters to scroll left/right when the cursor would move
     * outside of the visible area.
     */
    private static final int SCROLL_CHARS = 8;

    private final BufferedImage activeImage;

    private final BufferedImage inactiveImage;

    /**
     * The clipboard for cut/copy/paste operations.
     */
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    /**
     * The system selection for cut/copy/paste operations.
     */
    private final Clipboard selection = Toolkit.getDefaultToolkit().getSystemSelection();

    protected final Font font;

    private final Color inactiveColor;

    private final Color activeColor;

    private final int margin;

    private final StringBuilder text;

    /**
     * If set, hide input; else show input.
     */
    private boolean hideInput = false;

    /**
     * The cursor location.
     */
    private int cursor;

    /**
     * The display offset: this many characters are hidden.
     */
    private int offset = 0;

    public GUIText(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage activeImage, final BufferedImage inactiveImage, final Font font, final Color inactiveColor, final Color activeColor, final int margin, final String text)
    {
        super(jxcWindow, name, x, y, w, h, Transparency.TRANSLUCENT);
        if (2*margin >= w) throw new IllegalArgumentException("margin is too large");
        this.activeImage = activeImage;
        this.inactiveImage = inactiveImage;
        this.font = font;
        this.inactiveColor = inactiveColor;
        this.activeColor = activeColor;
        this.margin = margin;
        this.text = new StringBuilder(text);
        setCursor(this.text.length());
    }

    public void setText(final String text)
    {
        this.text.setLength(0);
        this.text.append(text);
        setCursor(this.text.length());
    }

    public String getText()
    {
        return text.toString();
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        g.drawImage(isActive() ? activeImage : inactiveImage, 0, 0, null);
        g.setFont(font);
        final String tmp = getDisplayText(g);
        final Rectangle2D rect = font.getStringBounds(tmp, g.getFontRenderContext());
        final int y = (int)Math.round((h-rect.getMaxY()-rect.getMinY()))/2;
        if (isActive())
        {
            final String tmpPrefix = tmp.substring(0, cursor-offset);
            final String tmpCursor = tmp.substring(0, cursor-offset+1);
            final Rectangle2D rectPrefix = font.getStringBounds(tmpPrefix, g.getFontRenderContext());
            final Rectangle2D rectCursor = font.getStringBounds(tmpCursor, g.getFontRenderContext());
            final int cursorX1 = (int)(rectPrefix.getWidth()+0.5);
            final int cursorX2 = (int)(rectCursor.getWidth()+0.5);
            g.setColor(inactiveColor);
            g.fillRect(margin+cursorX1, 0, cursorX2-cursorX1, h);
        }
        g.setColor(isActive() ? activeColor : inactiveColor);
        g.drawString(tmp, margin, y);
    }

    public String getDisplayText(final Graphics2D g)
    {
        final String tmpText = text.substring(offset);
        if (!hideInput)
        {
            return tmpText+" ";
        }

        final String template = "********************";
        final String hiddenText = template.substring(0, Math.min(tmpText.length(), template.length()));
        return hiddenText+" ";
    }

    /** {@inheritDoc} */
    @Override public void mouseClicked(final MouseEvent e)
    {
        super.mouseClicked(e);
        final int b = e.getButton();
        switch (b)
        {
        case MouseEvent.BUTTON1:
            setActive(true);
            setChanged();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    @Override protected void activeChanged()
    {
        setChanged();
    }

    public void keyPressed(final KeyEvent e)
    {
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_BACK_SPACE:
            if (cursor > 0)
            {
                text.delete(cursor-1, cursor);
                setCursor(cursor-1);
            }
            break;

        case KeyEvent.VK_DELETE:
            if (cursor < text.length())
            {
                text.delete(cursor, cursor+1);
                setChanged();
            }
            break;

        case KeyEvent.VK_KP_LEFT:
        case KeyEvent.VK_LEFT:
            if (cursor > 0)
            {
                setCursor(cursor-1);
            }
            break;

        case KeyEvent.VK_KP_RIGHT:
        case KeyEvent.VK_RIGHT:
            if (cursor < text.length())
            {
                setCursor(cursor+1);
            }
            break;

        case KeyEvent.VK_HOME:
            if (cursor > 0)
            {
                setCursor(0);
            }
            break;

        case KeyEvent.VK_END:
            if (cursor < text.length())
            {
                setCursor(text.length());
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
        final char ch = e.getKeyChar();
        switch (ch)
        {
        case '\r':
        case '\n':
            getJXCWindow().updatePlayerName(text.toString());
            execute((JXCWindow)e.getSource(), text.toString());
            setActive(false);
            break;

        case 0x16:              // CTRL-V
            paste();
            break;

        default:
            if (ch != KeyEvent.CHAR_UNDEFINED && ch >= ' ')
            {
                insertChar(ch);
            }
            break;
        }
    }

    /**
     * Inserts a character at the cursort position.
     * @param ch the character
     */
    public void insertChar(final char ch)
    {
        text.insert(cursor, ch);
        setCursor(cursor+1);
    }

    /**
     * Inserts a string at the cursort position.
     * @param str the string
     */
    public void insertString(final String str)
    {
        text.insert(cursor, str);
        setCursor(cursor+str.length());
    }

    /**
     * Will be called to execute the entered command.
     *
     * @param jxcWindow The JXCWindow instance.
     *
     * @param command The entered command.
     */
    protected abstract void execute(final JXCWindow jxcWindow, final String command);

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
            setChanged();
        }
    }

    /**
     * Set the cursor position. Make sure the cursor position is visible.
     *
     * @param cursor The new cursor position.
     */
    public void setCursor(final int cursor)
    {
        if (this.cursor < cursor)
        {
            // cursor moved right

            final Graphics2D g = buffer.createGraphics();
            for (;;)
            {
                final String tmp = getDisplayText(g);
                final String tmpCursor = tmp.substring(0, cursor-offset+1);
                final Rectangle2D rectCursor = font.getStringBounds(tmpCursor, g.getFontRenderContext());
                final int cursorX = (int)(rectCursor.getWidth()+0.5);
                if (cursorX < w)
                {
                    break;
                }

                if (offset+SCROLL_CHARS <= cursor)
                {
                    offset += SCROLL_CHARS;
                }
                else
                {
                    offset = cursor;
                }
            }
        }
        else if (this.cursor > cursor)
        {
            // cursor moved left

            while (cursor < offset)
            {
                if (offset <= SCROLL_CHARS)
                {
                    offset = 0;
                    break;
                }

                offset -= SCROLL_CHARS;
            }
        }

        this.cursor = cursor;
        setChanged();
    }

    /**
     * Perform a "paste" operation from the system clipboard.
     */
    public void paste()
    {
        Transferable content = null;
        if (selection != null)
        {
            content = selection.getContents(this);
        }
        if (content == null)
        {
            content = clipboard.getContents(this);
        }
        if (content == null)
        {
            return;
        }

        final String str;
        try
        {
            str = (String)content.getTransferData(DataFlavor.stringFlavor);
        }
        catch (final UnsupportedFlavorException ex)
        {
            return;
        }
        catch (final IOException ex)
        {
            return;
        }
        insertString(str);
    }
}

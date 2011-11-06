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

package com.realtime.crossfire.jxclient.gui.textinput;

import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.settings.CommandHistory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for text input fields. It allows entering and editing
 * text. Subclasses define the behavior when {@link #execute(String)} executing
 * the entered text when <code>ENTER</code> is pressed.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class GUIText extends ActivatableGUIElement implements KeyListener {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The number of characters to scroll left/right when the cursor would move
     * outside of the visible area.
     */
    private static final int SCROLL_CHARS = 8;

    /**
     * The {@link CommandCallback} to use.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * The command history for this text field.
     */
    @NotNull
    private final CommandHistory commandHistory;

    /**
     * The element's background image when it is active.
     */
    @NotNull
    private final Image activeImage;

    /**
     * The element's background image when it is inactive.
     */
    @NotNull
    private final Image inactiveImage;

    /**
     * The clipboard for cut/copy/paste operations.
     */
    @NotNull
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    /**
     * The system selection for cut/copy/paste operations.
     */
    @Nullable
    private final Clipboard selection = Toolkit.getDefaultToolkit().getSystemSelection();

    /**
     * The {@link Font font} for rendering displayed text.
     */
    @NotNull
    private final Font font;

    /**
     * The {@link Color color} for rendering displayed text when the element is
     * inactive. Also color of cursor.
     */
    @NotNull
    private final Color inactiveColor;

    /**
     * The {@link Color color} for rendering displayed text when the element is
     * active.
     */
    @NotNull
    private final Color activeColor;

    /**
     * The left margin in pixels.
     */
    private final int margin;

    /**
     * The entered text.
     */
    @NotNull
    private final StringBuilder text;

    /**
     * Whether UP and DOWN keys should be checked. If set, these keys cycle
     * through the history.
     */
    private final boolean enableHistory;

    /**
     * The size of this component.
     */
    private final Dimension preferredSize;

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

    /**
     * Object used to synchronize on access to {@link #text}, {@link #cursor},
     * and {@link #offset}.
     */
    @NotNull
    private final Object syncCursor = new Object();

    /**
     * Creates a new instance.
     * @param commandCallback the command callback to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param activeImage the element's background image when it is active
     * @param inactiveImage the element's background image when it is inactive
     * @param font the font for rendering displayed text
     * @param inactiveColor the color for rendering displayed text when the
     * element is active; also cursor color
     * @param activeColor the color for rendering displayed text when the
     * element is active
     * @param margin the left margin in pixels
     * @param text the initially entered text
     * @param enableHistory if set, enable access to command history
     */
    protected GUIText(@NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Image activeImage, @NotNull final Image inactiveImage, @NotNull final Font font, @NotNull final Color inactiveColor, @NotNull final Color activeColor, final int margin, @NotNull final String text, final boolean enableHistory) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT);
        this.commandCallback = commandCallback;
        commandHistory = new CommandHistory(name);
        this.activeImage = activeImage;
        this.inactiveImage = inactiveImage;
        this.font = font;
        this.inactiveColor = inactiveColor;
        this.activeColor = activeColor;
        this.margin = margin;
        this.text = new StringBuilder(text);
        this.enableHistory = enableHistory;
        preferredSize = new Dimension(activeImage.getWidth(null), activeImage.getHeight(null));
        if (!preferredSize.equals(new Dimension(inactiveImage.getWidth(null), inactiveImage.getHeight(null)))) {
            throw new IllegalArgumentException("active image size differs from inactive image size");
        }
        setCursor(this.text.length());
    }

    /**
     * Sets the entered text.
     * @param text the text
     */
    public void setText(@NotNull final String text) {
        this.text.setLength(0);
        this.text.append(text);
        setCursor(this.text.length());
    }

    /**
     * Returns the entered text.
     * @return the text
     */
    @NotNull
    public String getText() {
        return text.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(isActive() ? activeImage : inactiveImage, 0, 0, null);
        g2.setFont(font);
        final String tmp;
        final int y;
        synchronized (syncCursor) {
            tmp = getDisplayText();
            final FontRenderContext fontRenderContext = g2.getFontRenderContext();
            final RectangularShape rectangle = font.getStringBounds(tmp, fontRenderContext);
            y = (int)Math.round(getHeight()-rectangle.getMaxY()-rectangle.getMinY())/2;
            if (isActive()) {
                final String tmpPrefix = tmp.substring(0, cursor-offset);
                final String tmpCursor = tmp.substring(0, cursor-offset+1);
                final RectangularShape rectanglePrefix = font.getStringBounds(tmpPrefix, fontRenderContext);
                final RectangularShape rectangleCursor = font.getStringBounds(tmpCursor, fontRenderContext);
                final int cursorX1 = (int)Math.round(rectanglePrefix.getWidth());
                final int cursorX2 = (int)Math.round(rectangleCursor.getWidth());
                g2.setColor(inactiveColor);
                g2.fillRect(margin+cursorX1, 0, cursorX2-cursorX1, getHeight());
            }
        }
        g2.setColor(isActive() ? activeColor : inactiveColor);
        g2.drawString(tmp, margin, y);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(preferredSize);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(preferredSize);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(preferredSize);
    }

    /**
     * Returns the displayed text. The displayed text may differ from the {@link
     * #getText() entered text} as it may be clipped left because the input
     * field is scrolled or because the input field is a {@link #hideInput
     * password field that hides input}.
     * @return the displayed text
     */
    @NotNull
    private String getDisplayText() {
        final String tmpText = text.substring(offset);
        if (!hideInput) {
            return tmpText+" ";
        }

        final String template = "****************************************************************************************************************************************************************";
        final String hiddenText = template.substring(0, Math.min(tmpText.length(), template.length()));
        return hiddenText+" ";
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
            setActive(true);
            setChanged();
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
    protected void activeChanged() {
        setChanged();
    }

    @Override
    public boolean keyPressed(@NotNull final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
            final String command = text.toString();
            commandCallback.updatePlayerName(command);
            execute(command);
            if (!hideInput) {
                commandHistory.addCommand(command);
            }
            setActive(false);
            return true;

        case KeyEvent.VK_BACK_SPACE:
            synchronized (syncCursor) {
                if (cursor > 0) {
                    text.delete(cursor-1, cursor);
                    setCursor(cursor-1);
                }
            }
            return true;

        case KeyEvent.VK_DELETE:
            synchronized (syncCursor) {
                if (cursor < text.length()) {
                    text.delete(cursor, cursor+1);
                    setChanged();
                }
            }
            return true;

        case KeyEvent.VK_KP_LEFT:
        case KeyEvent.VK_LEFT:
            synchronized (syncCursor) {
                if (cursor > 0) {
                    setCursor(cursor-1);
                }
            }
            return true;

        case KeyEvent.VK_KP_RIGHT:
        case KeyEvent.VK_RIGHT:
            synchronized (syncCursor) {
                if (cursor < text.length()) {
                    setCursor(cursor+1);
                }
            }
            return true;

        case KeyEvent.VK_KP_UP:
        case KeyEvent.VK_UP:
            if (enableHistory) {
                historyPrev();
                return true;
            }
            break;

        case KeyEvent.VK_KP_DOWN:
        case KeyEvent.VK_DOWN:
            if (enableHistory) {
                historyNext();
                return true;
            }
            break;

        case KeyEvent.VK_HOME:
            synchronized (syncCursor) {
                if (cursor > 0) {
                    setCursor(0);
                }
            }
            return true;

        case KeyEvent.VK_END:
            synchronized (syncCursor) {
                if (cursor < text.length()) {
                    setCursor(text.length());
                }
            }
            return true;

        case KeyEvent.VK_N:              // CTRL-N
            if ((e.getModifiers()&InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
                historyNext();
                return true;
            }
            break;

        case KeyEvent.VK_P:              // CTRL-P
            if ((e.getModifiers()&InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
                historyPrev();
                return true;
            }
            break;

        case KeyEvent.VK_V:              // CTRL-V
            if ((e.getModifiers()&InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
                paste();
                return true;
            }
            break;
        }

        final char ch = e.getKeyChar();
        if (ch != KeyEvent.CHAR_UNDEFINED && ch != (char)127 && ch >= ' ') {
            insertChar(ch);
            return true;
        }

        return false;
    }

    /**
     * Activates the previous command from the command history.
     */
    private void historyPrev() {
        final String commandUp = commandHistory.up();
        if (commandUp != null) {
            setText(commandUp);
        }
    }

    /**
     * Activates the next command from the command history.
     */
    private void historyNext() {
        final String commandDown = commandHistory.down();
        setText(commandDown != null ? commandDown : "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyReleased(@NotNull final KeyEvent e) {
        return false;
    }

    /**
     * Inserts a character at the cursor position.
     * @param ch the character
     */
    private void insertChar(final char ch) {
        synchronized (syncCursor) {
            text.insert(cursor, ch);
            setCursor(cursor+1);
        }
    }

    /**
     * Inserts a string at the cursor position.
     * @param str the string
     */
    private void insertString(@NotNull final String str) {
        synchronized (syncCursor) {
            text.insert(cursor, str);
            setCursor(cursor+str.length());
        }
    }

    /**
     * Will be called to execute the entered command.
     * @param command the entered command
     */
    protected abstract void execute(@NotNull final String command);

    /**
     * Enables or disables hidden text.
     * @param hideInput if set, hide input; else show input
     */
    public void setHideInput(final boolean hideInput) {
        if (this.hideInput != hideInput) {
            this.hideInput = hideInput;
            setChanged();
        }
    }

    /**
     * Sets the cursor position. Make sure the cursor position is visible.
     * @param cursor the new cursor position
     */
    private void setCursor(final int cursor) {
        synchronized (syncCursor) {
            if (getGraphics() == null) { // XXX: hack
                // ignore
            } else if (this.cursor < cursor) {
                // cursor moved right

                while (true) {
                    final String tmp = getDisplayText();
                    final String tmpCursor = tmp.substring(0, cursor-offset+1);
                    //final RectangularShape rectangleCursor = font.getStringBounds(tmpCursor, fontRenderContext);
                    final Dimension dimension = GuiUtils.getTextDimension(tmpCursor, getFontMetrics(font));
                    //final int cursorX = (int)Math.round(rectangleCursor.getWidth());
                    //noinspection NonPrivateFieldAccessedInSynchronizedContext
                    final int cursorX = dimension.width;
                    if (cursorX < getWidth()) {
                        break;
                    }

                    if (offset+SCROLL_CHARS > cursor) {
                        offset = cursor;
                        break;
                    }

                    offset += SCROLL_CHARS;
                }
            } else if (this.cursor > cursor) {
                // cursor moved left

                while (cursor < offset) {
                    if (offset <= SCROLL_CHARS) {
                        offset = 0;
                        break;
                    }

                    offset -= SCROLL_CHARS;
                }
            }

            this.cursor = cursor;
        }
        setChanged();
    }

    /**
     * Performs a "paste" operation from the system clipboard.
     */
    private void paste() {
        Transferable content = null;
        if (selection != null) {
            content = selection.getContents(this);
        }
        if (content == null) {
            content = clipboard.getContents(this);
        }
        if (content == null) {
            return;
        }

        final String str;
        try {
            str = (String)content.getTransferData(DataFlavor.stringFlavor);
        } catch (final UnsupportedFlavorException ignored) {
            return;
        } catch (final IOException ignored) {
            return;
        }
        insertString(str);
    }

}

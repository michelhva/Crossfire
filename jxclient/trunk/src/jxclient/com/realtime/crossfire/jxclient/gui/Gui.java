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

import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.JXCWindowRenderer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Combines a list of {@link GUIElement}s to for a gui.
 *
 * <p>A dialog can be modal. Such dialogs do not propagate key or mouse events
 * to lower dialogs.
 *
 * @author Andreas Kirschbaum
 */
public class Gui
{
    /**
     * The window this gui belongs to.
     */
    private final JXCWindow jxcWindow;

    /**
     * The list of {@link GUIElement}s comprising this gui.
     */
    private final List<GUIElement> elements = new CopyOnWriteArrayList<GUIElement>();

    /**
     * The key bindings for this gui.
     */
    private final KeyBindings keyBindings = new KeyBindings();

    /**
     * Whether this dialog is modal.
     */
    private boolean modal = false;

    /**
     * The gui states that do not show this dialog.
     */
    private final EnumSet<JXCWindowRenderer.GuiState> hideInStates = EnumSet.noneOf(JXCWindowRenderer.GuiState.class);

    /**
     * The gui element which has the focus. Set to <code>null</code> if no such
     * element exists.
     */
    private ActivatableGUIElement activeElement = null;

    /**
     * Records whether at least one gui element has changed since last redraw.
     */
    private boolean hasChangedElements = false;

    /**
     * The x-offset for drawing gui elements inside this gui.
     */
    private int x = 0;

    /**
     * The y-offset for drawing gui elements inside this gui.
     */
    private int y = 0;

    /**
     * The width of the dialog.
     */
    private int w = 1;

    /**
     * The height of the dialog.
     */
    private int h = 1;

    /**
     * If non-<code>null</code>, auto-close this dialog if this gui element
     * becomes inactive.
     */
    private ActivatableGUIElement autoCloseOnDeactivate = null;

    /**
     * Create a new instance.
     *
     * @param jxcWindow The window this gui belongs to.
     */
    public Gui(final JXCWindow jxcWindow)
    {
        if (jxcWindow == null) throw new IllegalArgumentException();

        this.jxcWindow = jxcWindow;
    }

    /**
     * Set the size of this dialog.
     *
     * @param w The width.
     *
     * @param h The height.
     */
    public void setSize(final int w, final int h)
    {
        if (w <= 0 || h <= 0) throw new IllegalArgumentException();

        if (this.w == w && this.h == h)
        {
            return;
        }

        this.w = w;
        this.h = h;
        hasChangedElements = true;
    }

    public void setPosition(final int x, final int y)
    {
        final int newX = Math.max(Math.min(x, jxcWindow.getWindowWidth()-w), 0);
        final int newY = Math.max(Math.min(y, jxcWindow.getWindowHeight()-h), 0);
        if (this.x == newX && this.y == newY)
        {
            return;
        }

        this.x = newX;
        this.y = newY;
        hasChangedElements = true;
    }

    /**
     * Set the modal state.
     *
     * @param modal The new modal state.
     */
    public void setModal(final boolean modal)
    {
        this.modal = modal;
    }

    /**
     * Return the modal state.
     *
     * @return The modal state.
     */
    public boolean isModal()
    {
        return modal;
    }

    /**
     * Add a {@link GUIElement} to this gui. The element must not be added to
     * more than one gui at a time.
     *
     * @param element The <code>GUIElement</code> to add.
     */
    public void add(final GUIElement element)
    {
        if (element.getGui() != null) throw new IllegalArgumentException();
        if (element.getJXCWindow() != jxcWindow) throw new IllegalArgumentException();

        elements.add(element);
        element.setGui(this);
    }

    /**
     * Repaint the gui and clear the changed flags of all repainted elements.
     *
     * @param g The <code>Graphics</code> to paint into.
     *
     * @param jxcWindow The window to deliver change events to.
     */
    public void redraw(final Graphics g, final JXCWindow jxcWindow)
    {
        final boolean debugGui = jxcWindow.isDebugGui();

        final GUIElement mouseElement = debugGui ? jxcWindow.getMouseTracker().getMouseElement() : null;
        final long t0 = debugGui ? System.currentTimeMillis() : 0;

        hasChangedElements = false;
        for (final GUIElement element : elements)
        {
            if (element.isVisible())
            {
                final BufferedImage bufferedImage = element.getBuffer();
                synchronized(bufferedImage)
                {
                    g.drawImage(bufferedImage, element.getX(), element.getY(), jxcWindow);
                    element.resetChanged();
                    if (debugGui)
                    {
                        g.setColor(element == mouseElement ? java.awt.Color.RED : java.awt.Color.WHITE);
                        g.drawRect(element.getX(), element.getY(), element.getWidth()-1, element.getHeight()-1);
                    }
                }
            }
        }

        if (debugGui)
        {
            final long t1 = System.currentTimeMillis();
            g.setColor(java.awt.Color.YELLOW);
            if(mouseElement != null)
            {
                g.drawString(mouseElement.getName(), 16, 16);
            }
            g.drawString((t1-t0)+"ms", 16, 32);
        }
    }

    /**
     * Check whether any visible gui element of this gui has been changed since
     * it was painted last time.
     *
     * @return <code>true</code> if any gui element has changed;
     * <code>false</code> otherwise.
     */
    public boolean needRedraw()
    {
        return hasChangedElements;
    }

    /**
     * Return the first default gui element of this gui.
     *
     * @return The default gui element, or <code>null</code>.
     */
    public GUIElement getDefaultElement()
    {
        for (final GUIElement element : elements)
        {
            if (element.isVisible() && element.isDefault())
            {
                return element;
            }
        }

        return null;
    }

    /**
     * Activate the first default gui element of this gui.
     */
    public void activateDefaultElement()
    {
        final GUIElement defaultElement = getDefaultElement();
        if (defaultElement != null && defaultElement instanceof ActivatableGUIElement)
        {
            final ActivatableGUIElement activatableDefaultElement = (ActivatableGUIElement)defaultElement;
            activatableDefaultElement.setActive(true);
        }
    }

    /**
     * Return the first {@link GUIText} gui element of this gui.
     *
     * @return The <code>GUIText</code> element, or <code>null</code> if this
     * gui does not contain any <code>GUIText</code> gui elements.
     */
    public GUIText getFirstTextArea()
    {
        for (final GUIElement element : elements)
        {
            if ((element instanceof GUIText) && element.isVisible())
            {
                return (GUIText)element;
            }
        }

        return null;
    }

    /**
     * Determine the {@link GUIElement} for a given coordinate.
     *
     * @param x The x-coordinate to check.
     *
     * @param y The y-coordinate to check.
     *
     * @return The <code>GUIElement</code> at the given coordinate, or
     * <code>null</code> if none was found.
     */
    public GUIElement getElementFromPoint(final int x, final int y)
    {
        GUIElement elected = null;
        for (final GUIElement element : elements)
        {
            if (element.isVisible() && !element.isIgnore())
            {
                if (element.getX() <= x && x < element.getX()+element.getWidth())
                {
                    if (element.getY() <= y && y < element.getY()+element.getHeight())
                    {
                        elected = element;
                    }
                }
            }
        }

        return elected;
    }

    /**
     * Set the gui element owning the focus.
     *
     * @param activeElement The gui element, or <code>null</code> if no element
     * should have the focus.
     */
    public void updateActiveElement(final ActivatableGUIElement activeElement)
    {
        if (this.activeElement != null && this.activeElement != activeElement)
        {
            this.activeElement.setActive(false);
        }

        this.activeElement = activeElement.isActive() ? activeElement : null;
        if (autoCloseOnDeactivate != null && autoCloseOnDeactivate == activeElement && !activeElement.isActive())
        {
            autoCloseOnDeactivate = null;
            jxcWindow.getWindowRenderer().closeDialog(this);
        }
    }

    /**
     * Return the gui element owning the focus.
     *
     * @return The gui element owning the focus, or <code>null</code> if no
     * such element exists.
     */
    public ActivatableGUIElement getActiveElement()
    {
        return activeElement;
    }

    /**
     * Dispatch a key press {@link KeyEvent}.
     *
     * @param e The event to dispatch.
     *
     * @return Whether a gui element did handle the event.
     */
    public boolean handleKeyPress(final KeyEvent e)
    {
        if (activeElement != null)
        {
            if (activeElement instanceof KeyListener)
            {
                ((KeyListener)activeElement).keyPressed(e);
                return true;
            }
            else if (activeElement instanceof AbstractButton)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    ((AbstractButton)activeElement).execute();
                    return true;
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            final GUIElement defaultElement = getDefaultElement();
            if (defaultElement != null && defaultElement instanceof AbstractButton)
            {
                ((AbstractButton)defaultElement).execute();
                return true;
            }
        }

        return keyBindings.handleKeyPress(e);
    }

    /**
     * Dispatch a key typed {@link KeyEvent}.
     *
     * @param e The event to dispatch.
     *
     * @return Whether a gui element did handle the event.
     */
    public boolean handleKeyTyped(final KeyEvent e)
    {
        if (activeElement != null)
        {
            if (activeElement instanceof KeyListener)
            {
                ((KeyListener)activeElement).keyTyped(e);
                return true;
            }
        }

        return keyBindings.handleKeyTyped(e);
    }

    /**
     * Return the first {@link GUIText} gui element of this gui and make it
     * active.
     *
     * @return The <code>GUIText</code> element, or <code>null</code> if this
     * gui does not contain any <code>GUIText</code> gui elements.
     */
    private GUIText activateFirstTextArea()
    {
        final GUIText textArea = getFirstTextArea();
        if (textArea != null)
        {
            textArea.setActive(true);
        }
        return textArea;
    }

    /**
     * Return the first command text field of this gui and make it active.
     *
     * @return The comment text field, or <code>null</code> if this gui does
     * not contain any command text fields.
     */
    public GUIText activateCommandInput()
    {
        final GUIText textArea = activateFirstTextArea();
        if (textArea != null && textArea.getName().equals("command"))
        {
            return textArea;
        }

        return null;
    }

    /**
     * Notify that one gui element has changed since last redraw.
     */
    public void setChangedElements()
    {
        hasChangedElements = true;
    }

    /**
     * The x-offset for drawing gui elements inside this gui.
     *
     * @return The x-offset.
     */
    public int getX()
    {
        return x;
    }

    /**
     * The y-offset for drawing gui elements inside this gui.
     *
     * @return The y-offset.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Return the key bindings instance for this gui.
     *
     * @return The key bindings.
     */
    public KeyBindings getKeyBindings()
    {
        return keyBindings;
    }

    /**
     * Hide the dialog in a state.
     *
     * @param state The state.
     */
    public void hideInState(final JXCWindowRenderer.GuiState state)
    {
        hideInStates.add(state);
    }

    /**
     * Return whether this gui is visible in a state.
     *
     * @param state The state.
     *
     * @return Whether this gui is hidden.
     */
    public boolean isHidden(final JXCWindowRenderer.GuiState state)
    {
        return hideInStates.contains(state);
    }

    /**
     * Automatically close this gui element if the given gui element is
     * deactivated.
     *
     * @param activatableGUIElement The gui element to monitor.
     */
    public void setAutoCloseOnDeactivate(final ActivatableGUIElement activatableGUIElement)
    {
        if (activatableGUIElement == null || !activatableGUIElement.isActive())
        {
            autoCloseOnDeactivate = null;
        }
        else
        {
            autoCloseOnDeactivate = activatableGUIElement;
        }
    }
}

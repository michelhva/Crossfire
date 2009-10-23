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
package com.realtime.crossfire.jxclient.gui.gui;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.gui.button.AbstractButton;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.gui.textinput.KeyListener;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.MouseTracker;
import com.realtime.crossfire.jxclient.window.RendererGuiState;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

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
     * The mouse tracker if in GUI debug mode or <code>null</code> otherwise.
     */
    private final MouseTracker mouseTracker;

    /**
     * The list of {@link GUIElement}s comprising this gui.
     */
    private final List<GUIElement> visibleElements = new CopyOnWriteArrayList<GUIElement>();

    /**
     * The key bindings for this gui.
     */
    private final KeyBindings keyBindings;

    /**
     * Whether this dialog is modal.
     */
    private boolean modal = false;

    /**
     * The gui states that do not show this dialog.
     */
    private final Set<RendererGuiState> hideInStates = EnumSet.noneOf(RendererGuiState.class);

    /**
     * If non-<code>null</code>, this element is always active. No other
     * element can become active.
     */
    private ActivatableGUIElement forcedActive = null;

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
    private int w = 0;

    /**
     * The height of the dialog.
     */
    private int h = 0;

    /**
     * The name of the dialog, or <code>null</code>.
     */
    private String name = null;

    /**
     * Whether the state (position or size) has changed.
     */
    private boolean stateChanged = false;

    /**
     * If set, the auto-close listener to notify if this dialog looses the
     * active gui element.
     */
    private GuiAutoCloseListener guiAutoCloseListener = null;

    /**
     * Creates a new instance.
     * @param mouseTracker the mouse tracker when in debug GUI mode or
     * <code>null</code> otherwise
     * @param commands the commands instance for executing commands
     * @param guiManager the gui manager to use
     * @param macros the macros instance to use
     */
    public Gui(final MouseTracker mouseTracker, final Commands commands, final GuiManager guiManager, final Macros macros)
    {
        this.mouseTracker = mouseTracker;
        keyBindings = new KeyBindings(null, commands, guiManager, macros);
    }

    /**
     * Set the name of this dialog.
     *
     * @param name The name of the dialog.
     */
    public void setName(final String name)
    {
        if (name == null) throw new IllegalArgumentException();

        this.name = name;
    }

    /**
     * Mark this gui as a "dialog".
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
        stateChanged = true;
    }

    public void setPosition(final int x, final int y)
    {
        if (w == 0 || h == 0) throw new IllegalStateException();

        if (this.x == x && this.y == y)
        {
            return;
        }

        this.x = x;
        this.y = y;
        hasChangedElements = true;
        stateChanged = true;
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

        updateVisibleElement(element);
        element.setGui(this);
    }

    /**
     * Repaint the gui and clear the changed flags of all repainted elements.
     *
     * @param g The <code>Graphics</code> to paint into.
     */
    public void redraw(final Graphics g)
    {
        if (mouseTracker != null)
        {
            final GUIElement mouseElement = mouseTracker.getMouseElement();
            final long t0 = System.currentTimeMillis();

            hasChangedElements = false;
            for (final GUIElement element : visibleElements)
            {
                element.paintComponent(g);
                g.setColor(element == mouseElement ? Color.RED : Color.WHITE);
                g.drawRect(element.getElementX(), element.getElementY(), element.getWidth()-1, element.getHeight()-1);
            }

            final long t1 = System.currentTimeMillis();
            g.setColor(Color.YELLOW);
            if (mouseElement != null)
            {
                g.drawString(mouseElement.getName(), 16, 16);
            }
            g.drawString((t1-t0)+"ms", 16, 32);
        }
        else
        {
            hasChangedElements = false;
            for (final GUIElement element : visibleElements)
            {
                element.paintComponent(g);
            }
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
    private GUIElement getDefaultElement()
    {
        for (final GUIElement element : visibleElements)
        {
            if (element.isDefault())
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
     * Returns all gui elements of this gui belonging to the given class.
     * @param class_ the class to collect
     * @return the gui elements
     */
    public <T extends GUIElement> Set<T> getElements(final Class<T> class_)
    {
        final Set<T> result = new HashSet<T>(16);
        for (final GUIElement element : visibleElements)
        {
            if (class_.isAssignableFrom(element.getClass()))
            {
                result.add(class_.cast(element));
            }
        }
        return result;
    }

    /**
     * Returns the first gui element of this gui which belongs to the given
     * class and that's name ends with the given ending.
     * @param class_ the class to search for
     * @param ending the ending to search for
     * @return the gui element or <code>null</code> if not found
     */
    public <T extends GUIElement> T getFirstElementEndingWith(final Class<T> class_, final String ending)
    {
        for (final GUIElement element : visibleElements)
        {
            if (class_.isAssignableFrom(element.getClass()) && element.getName().endsWith(ending))
            {
                return class_.cast(element);
            }
        }

        return null;
    }

    /**
     * Returns the first gui element of this gui which belongs to the given
     * class and that's name does not end with the given ending.
     * @param class_ the class to search for
     * @param ending the ending to search for
     * @return the gui element or <code>null</code> if not found
     */
    public <T extends GUIElement> T getFirstElementNotEndingWith(final Class<T> class_, final String ending)
    {
        for (final GUIElement element : visibleElements)
        {
            if (class_.isAssignableFrom(element.getClass()) && !element.getName().endsWith(ending))
            {
                return class_.cast(element);
            }
        }

        return null;
    }

    /**
     * Returns the first gui element of this gui belonging to the given class.
     * @param class_ the class to search for
     * @return the gui element or <code>null</code> if not found
     */
    public <T extends GUIElement> T getFirstElement(final Class<T> class_)
    {
        for (final GUIElement element : visibleElements)
        {
            if (class_.isAssignableFrom(element.getClass()))
            {
                return class_.cast(element);
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
        for (final GUIElement element : visibleElements)
        {
            if (!element.isIgnore())
            {
                if (element.getElementX() <= x && x < element.getElementX()+element.getWidth())
                {
                    if (element.getElementY() <= y && y < element.getElementY()+element.getHeight())
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
     * @param activeElement The gui element.
     *
     * @param active The new active state.
     */
    void setActiveElement(final ActivatableGUIElement activeElement, final boolean active)
    {
        assert activeElement != null;

        final ActivatableGUIElement previousActiveElement = this.activeElement;
        if (active)
        {
            if (forcedActive != null && forcedActive != activeElement)
            {
                return;
            }

            if (isActiveElement(activeElement))
            {
                return;
            }

            this.activeElement = activeElement;
            if (previousActiveElement != null)
            {
                previousActiveElement.activeChanged();
            }
            this.activeElement.activeChanged();

            guiAutoCloseListener = null;
        }
        else
        {
            if (!isActiveElement(activeElement))
            {
                return;
            }

            this.activeElement = null;
            previousActiveElement.activeChanged();

            if (guiAutoCloseListener != null)
            {
                final GuiAutoCloseListener listener = guiAutoCloseListener;
                guiAutoCloseListener = null;
                listener.autoClosed(this);
            }
        }
    }

    /**
     * Returns whether a given gui element is the active element of this
     * dialog.
     * @param activeElement the gui element
     * @return whether the given gui element is active
     */
    private boolean isActiveElement(final ActivatableGUIElement activeElement)
    {
        return this.activeElement != null && this.activeElement == activeElement;
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
                if (((KeyListener)activeElement).keyPressed(e))
                {
                    return true;
                }
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
                if (((KeyListener)activeElement).keyTyped(e))
                {
                    return true;
                }
            }
            else if (activeElement instanceof AbstractButton)
            {
                if (e.getKeyChar() == '\r' || e.getKeyChar() == '\n' || e.getKeyChar() == ' ')
                {
                    ((AbstractButton)activeElement).execute();
                    return true;
                }
            }
        }

        if (e.getKeyChar() == '\r' || e.getKeyChar() == '\n' || e.getKeyChar() == ' ')
        {
            final GUIElement defaultElement = getDefaultElement();
            if (defaultElement != null && defaultElement instanceof AbstractButton)
            {
                ((AbstractButton)defaultElement).execute();
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
        final GUIText textArea = getFirstElement(GUIText.class);
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
     * Deactivates the command text input field of this dialog. Does nothing if
     * the command text input field is not active or if this dialog has no
     * command text input field.
     * @return whether the command text input field has been deactivated
     */
    public boolean deactivateCommandInput()
    {
        if (activeElement == null)
        {
            return false;
        }

        if (!(activeElement instanceof GUIText))
        {
            return false;
        }

        final GUIText textArea = (GUIText)activeElement;
        if (!textArea.getName().equals("command"))
        {
            return false;
        }

        activeElement.setActive(false);
        return true;
    }

    /**
     * Returns the first gui element of this gui belonging to the given class
     * and having the given name.
     * @param class_ the class to search for
     * @param name the button's name
     * @return the button or <code>null</code> if no button matches
     */
    public <T extends GUIElement> T getFirstElement(final Class<T> class_, final String name)
    {
        for (final GUIElement element : visibleElements)
        {
            if (class_.isAssignableFrom(element.getClass())
            && element.getName().equals(name))
            {
                return class_.cast(element);
            }
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
     * The width of the dialog.
     *
     * @return The width, or <code>0</code> if this is not a dlalog.
     */
    public int getWidth()
    {
        return w;
    }

    /**
     * The height of this dialog.
     *
     * @return The height, or <code>0</code> if this is not a dialog.
     */
    public int getHeight()
    {
        return h;
    }

    /**
     * Return the name of the dialog.
     *
     * @return The name, or <code>null</code> if this is not a dialog.
     */
    public String getName()
    {
        return name;
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
    public void hideInState(final RendererGuiState state)
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
    public boolean isHidden(final RendererGuiState state)
    {
        return hideInStates.contains(state);
    }

    /**
     * The {@link GuiAutoCloseListener} to be notified when this dialog becomes
     * inactive.
     * @param guiAutoCloseListener the listener to be notified or
     * <code>null</code>
     */
    public void setGuiAutoCloseListener(final GuiAutoCloseListener guiAutoCloseListener)
    {
        this.guiAutoCloseListener = guiAutoCloseListener;
    }

    /**
     * Set whether the state (position or size) has changed.
     *
     * @param stateChanged Whether the state has changed.
     */
    public void setStateChanged(final boolean stateChanged)
    {
        this.stateChanged = stateChanged;
    }

    /**
     * Enable or disable hidden text in the first input field.
     *
     * @param hideInput If set, hide input; else show input.
     */
    public void setHideInput(final boolean hideInput)
    {
        final GUIText textArea = getFirstElement(GUIText.class);
        if (textArea != null)
        {
            textArea.setHideInput(hideInput);
        }
    }

    public void updateVisibleElement(final GUIElement element)
    {
        if (element.isElementVisible())
        {
            visibleElements.add(element);
        }
        else
        {
            visibleElements.remove(element);
        }
        hasChangedElements = true;
    }

    /**
     * Returns whether a given point is within this dialog's drawing area.
     * @param x the x-coordinate of the the point
     * @param y the y-coordinate of the the point
     * @return whether the coordinate is within the drawing area
     */
    public boolean isWithinDrawingArea(final int x, final int y)
    {
        return this.x <= x && x < this.x+w && this.y <= y && y < this.y+h;
    }

    /**
     * Returns whether this dialog has changed from its default state.
     * @return whether the state has changed
     */
    public boolean isChangedFromDefault()
    {
        return name != null && w > 0 && h > 0 && stateChanged;
    }

    /**
     * Sets an {@link ActivatableGUIElement} that is always active. It prevents
     * any other element from getting active.
     * @param forcedActive the element to set or <code>null</code> to unset
     */
    public void setForcedActive(final ActivatableGUIElement forcedActive)
    {
        this.forcedActive = forcedActive;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return (name == null ? "" : name)+"["+w+"x"+h+"]";
    }
}

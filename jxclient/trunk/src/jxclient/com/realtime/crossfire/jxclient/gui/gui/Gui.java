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

package com.realtime.crossfire.jxclient.gui.gui;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.gui.button.AbstractButton;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.gui.textinput.KeyListener;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.MouseTracker;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Combines a list of {@link GUIElement}s to for a gui.
 * <p>
 * A dialog can be modal. Such dialogs do not propagate key or mouse events to
 * lower dialogs.
 * @author Andreas Kirschbaum
 */
public class Gui
{
    /**
     * The {@link MouseTracker} if in GUI debug mode or <code>null</code>
     * otherwise.
     */
    @Nullable
    private final MouseTracker mouseTracker;

    /**
     * The list of {@link GUIElement}s comprising this gui.
     */
    @NotNull
    private final Collection<GUIElement> visibleElements = new CopyOnWriteArrayList<GUIElement>();

    /**
     * The {@link KeyBindings} for this gui.
     */
    @NotNull
    private final KeyBindings keyBindings;

    /**
     * Whether this dialog is modal. Modal dialogs consume all key presses.
     */
    private boolean modal = false;

    /**
     * The gui states that do not show this dialog.
     */
    @NotNull
    private final Collection<RendererGuiState> hideInStates = EnumSet.noneOf(RendererGuiState.class);

    /**
     * If non-<code>null</code>, this element is always active. No other
     * element can become active.
     */
    @Nullable
    private ActivatableGUIElement forcedActive = null;

    /**
     * The gui element which has the focus. Set to <code>null</code> if no such
     * element exists.
     */
    @Nullable
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
    @Nullable
    private String name = null;

    /**
     * Whether the state (position or size) has changed.
     */
    private boolean stateChanged = false;

    /**
     * If set, the auto-close listener to notify if this dialog looses the
     * active gui element.
     */
    @Nullable
    private GuiAutoCloseListener guiAutoCloseListener = null;

    /**
     * Creates a new instance.
     * @param mouseTracker the mouse tracker when in debug GUI mode or
     * <code>null</code> otherwise
     * @param commands the commands instance for executing commands
     * @param guiManager the gui manager to use
     * @param macros the macros instance to use
     */
    public Gui(@Nullable final MouseTracker mouseTracker, @NotNull final Commands commands, @NotNull final GuiManager guiManager, @NotNull final Macros macros)
    {
        this.mouseTracker = mouseTracker;
        keyBindings = new KeyBindings(null, commands, guiManager, macros);
    }

    /**
     * Sets the name of this dialog.
     * @param name the name of the dialog
     */
    public void setName(@NotNull final String name)
    {
        this.name = name;
    }

    /**
     * Sets the size of this dialog. Marks this gui as a "dialog".
     * @param w the width
     * @param h the height
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

    /**
     * Sets the position of this dialog.
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
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
     * Sets the modal state.
     * @param modal the new modal state
     */
    public void setModal(final boolean modal)
    {
        this.modal = modal;
    }

    /**
     * Returns the modal state.
     * @return the modal state
     */
    public boolean isModal()
    {
        return modal;
    }

    /**
     * Adds a {@link GUIElement} to this gui. The element must not be added to
     * more than one gui at a time.
     * @param element the <code>GUIElement</code> to add
     */
    public void add(@NotNull final GUIElement element)
    {
        if (element.getGui() != null) throw new IllegalArgumentException();

        updateVisibleElement(element);
        element.setGui(this);
    }

    /**
     * Repaints the gui and clear the changed flags of all repainted elements.
     * @param g the <code>Graphics</code> to paint into
     */
    public void redraw(@NotNull final Graphics g)
    {
        if (mouseTracker != null)
        {
            final Component mouseElement = mouseTracker.getMouseElement();
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
     * Checks whether any visible gui element of this gui has been changed
     * since it was painted last time.
     * @return <code>true</code> if any gui element has changed;
     * <code>false</code> otherwise
     */
    public boolean needRedraw()
    {
        return hasChangedElements;
    }

    /**
     * Returns the first default gui element of this gui.
     * @return the default gui element, or <code>null</code>
     */
    @Nullable
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
     * Activates the first default gui element of this gui.
     */
    public void activateDefaultElement()
    {
        final Object defaultElement = getDefaultElement();
        if (defaultElement != null && defaultElement instanceof ActivatableGUIElement)
        {
            final ActivatableGUIElement activatableDefaultElement = (ActivatableGUIElement)defaultElement;
            activatableDefaultElement.setActive(true);
        }
    }

    /**
     * Returnss all gui elements of this gui belonging to the given class.
     * @param class_ the class to collect
     * @return the gui elements
     */
    @NotNull
    public <T extends GUIElement> Set<T> getElements(@NotNull final Class<T> class_)
    {
        final Set<T> result = new HashSet<T>(16);
        for (final Object element : visibleElements)
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
    @Nullable
    public <T extends GUIElement> T getFirstElementEndingWith(@NotNull final Class<T> class_, @NotNull final String ending)
    {
        for (final Component element : visibleElements)
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
    @Nullable
    public <T extends GUIElement> T getFirstElementNotEndingWith(@NotNull final Class<T> class_, @NotNull final String ending)
    {
        for (final Component element : visibleElements)
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
    @Nullable
    public <T extends GUIElement> T getFirstElement(@NotNull final Class<T> class_)
    {
        for (final Object element : visibleElements)
        {
            if (class_.isAssignableFrom(element.getClass()))
            {
                return class_.cast(element);
            }
        }

        return null;
    }

    /**
     * Determines the {@link GUIElement} for a given coordinate.
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return the <code>GUIElement</code> at the given coordinate, or
     * <code>null</code> if none was found
     */
    @Nullable
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
     * Sets the gui element owning the focus.
     * @param activeElement the gui element
     * @param active the new active state
     */
    void setActiveElement(@NotNull final ActivatableGUIElement activeElement, final boolean active)
    {
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
    private boolean isActiveElement(@Nullable final ActivatableGUIElement activeElement)
    {
        return this.activeElement != null && this.activeElement == activeElement;
    }

    /**
     * Returns the gui element owning the focus.
     * @return the gui element owning the focus, or <code>null</code> if no
     * such element exists
     */
    @Nullable
    public ActivatableGUIElement getActiveElement()
    {
        return activeElement;
    }

    /**
     * Dispatches a key press {@link KeyEvent}.
     * @param e the event to dispatch
     * @return whether a gui element did handle the event
     */
    public boolean handleKeyPress(@NotNull final KeyEvent e)
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
     * Dispatches a key typed {@link KeyEvent}.
     * @param e the event to dispatch
     * @return whether a gui element did handle the event
     */
    public boolean handleKeyTyped(@NotNull final KeyEvent e)
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
     * Returns the first {@link GUIText} gui element of this gui and make it
     * active.
     * @return the <code>GUIText</code> element, or <code>null</code> if this
     * gui does not contain any <code>GUIText</code> gui elements
     */
    @Nullable
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
     * Returns the first command text field of this gui and make it active.
     * @return the comment text field, or <code>null</code> if this gui does
     * not contain any command text fields
     */
    @Nullable
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

        final Component textArea = activeElement;
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
    @Nullable
    public <T extends GUIElement> T getFirstElement(@NotNull final Class<T> class_, @NotNull final String name)
    {
        for (final Component element : visibleElements)
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
     * Notifies that one gui element has changed since last redraw.
     */
    public void setChangedElements()
    {
        hasChangedElements = true;
    }

    /**
     * Returns the x-offset for drawing gui elements inside this gui.
     * @return the x-offset
     */
    public int getX()
    {
        return x;
    }

    /**
     * Returns the y-offset for drawing gui elements inside this gui.
     * @return the y-offset
     */
    public int getY()
    {
        return y;
    }

    /**
     * Returns the width of the dialog.
     * @return the width, or <code>0</code> if this is not a dlalog
     */
    public int getWidth()
    {
        return w;
    }

    /**
     * Returns the height of this dialog.
     * @return the height, or <code>0</code> if this is not a dialog
     */
    public int getHeight()
    {
        return h;
    }

    /**
     * Returns the name of the dialog.
     * @return the name, or <code>null</code> if this is not a dialog
     */
    @Nullable
    public String getName()
    {
        return name;
    }

    /**
     * Returns the key bindings instance for this gui.
     * @return the key bindings
     */
    @NotNull
    public KeyBindings getKeyBindings()
    {
        return keyBindings;
    }

    /**
     * Hides the dialog in a state.
     * @param state the state
     */
    public void hideInState(@NotNull final RendererGuiState state)
    {
        hideInStates.add(state);
    }

    /**
     * Returns whether this gui is visible in a state.
     * @param state the state
     * @return whether this gui is hidden
     */
    public boolean isHidden(@NotNull final RendererGuiState state)
    {
        return hideInStates.contains(state);
    }

    /**
     * Sets the {@link GuiAutoCloseListener} to be notified when this dialog
     * becomes inactive.
     * @param guiAutoCloseListener the listener to be notified or
     * <code>null</code>
     */
    public void setGuiAutoCloseListener(@Nullable final GuiAutoCloseListener guiAutoCloseListener)
    {
        this.guiAutoCloseListener = guiAutoCloseListener;
    }

    /**
     * Sets whether the state (position or size) has changed.
     * @param stateChanged whether the state has changed
     */
    public void setStateChanged(final boolean stateChanged)
    {
        this.stateChanged = stateChanged;
    }

    /**
     * Enables or disables hidden text in the first input field.
     * @param hideInput if set, hide input; else show input
     */
    public void setHideInput(final boolean hideInput)
    {
        final GUIText textArea = getFirstElement(GUIText.class);
        if (textArea != null)
        {
            textArea.setHideInput(hideInput);
        }
    }

    /**
     * Adds or removes a {@link GUIElement} from this gui. The gui element is
     * added if it is visible or removed if it is invisible.
     * @param element the gui element
     */
    public void updateVisibleElement(@NotNull final GUIElement element)
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
    public void setForcedActive(@Nullable final ActivatableGUIElement forcedActive)
    {
        this.forcedActive = forcedActive;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String toString()
    {
        return (name == null ? "" : name)+"["+w+"x"+h+"]";
    }
}

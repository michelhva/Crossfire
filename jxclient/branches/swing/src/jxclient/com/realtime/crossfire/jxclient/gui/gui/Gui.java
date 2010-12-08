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
import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.gui.textinput.KeyListener;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.EnumSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Combines a list of {@link GUIElement}s to for a gui.
 * <p/>
 * A dialog can be modal. Such dialogs do not propagate key or mouse events to
 * lower dialogs.
 * @author Andreas Kirschbaum
 */
public class Gui extends Container {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link MouseTracker} if in GUI debug mode or <code>null</code>
     * otherwise.
     */
    @Nullable
    private final MouseTracker mouseTracker;

    /**
     * The {@link KeyBindings} for this gui.
     */
    @NotNull
    private final KeyBindings keyBindings;

    /**
     * The extent of the dialog if it is auto-sizing or <code>null</code>
     * otherwise. Auto-sizing dialogs cannot be moved or resized manually.
     */
    @Nullable
    private Extent autoSize = null;

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
     * If non-<code>null</code>, this element is always active. No other element
     * can become active.
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
     * Whether an initial position has been set.
     */
    private boolean initialPositionSet = false;

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
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     */
    public Gui(@Nullable final MouseTracker mouseTracker, @NotNull final Commands commands, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) {
        this.mouseTracker = mouseTracker;
        keyBindings = new KeyBindings(null, commands, commandCallback, macros);
    }

    /**
     * Sets the size of this dialog. Marks this gui as a "dialog".
     * @param width the width
     * @param height the height
     */
    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        if (getX() == x && getY() == y && getWidth() == width && getHeight() == height) {
            return;
        }

        super.setBounds(x, y, width, height);
        validate();
        hasChangedElements = true;
        stateChanged = true;
    }

    /**
     * Sets the position of this dialog.
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void setPosition(final int x, final int y) {
        if (initialPositionSet && getX() == x && getY() == y) {
            return;
        }

        if ((getWidth() == 0 || getHeight() == 0) && (initialPositionSet || x != 0 || y != 0)) {
            throw new IllegalStateException();
        }

        initialPositionSet = true;
        setLocation(x, y);
        hasChangedElements = true;
        stateChanged = true;
    }

    /**
     * Sets the auto-size state. Auto-size dialogs cannot be moved or resized
     * manually.
     * @param autoSize the new auto-size or <code>null</code>
     */
    public void setAutoSize(@Nullable final Extent autoSize) {
        this.autoSize = autoSize;
    }

    /**
     * Returns the auto-size state. Auto-size dialogs cannot be moved or resized
     * manually.
     * @return the auto-size or <code>null</code>
     */
    @Nullable
    public Extent getAutoSize() {
        return autoSize;
    }

    /**
     * Sets the modal state.
     * @param modal the new modal state
     */
    public void setModal(final boolean modal) {
        this.modal = modal;
    }

    /**
     * Returns the modal state.
     * @return the modal state
     */
    public boolean isModal() {
        return modal;
    }

    /**
     * Repaints the gui and clear the changed flags of all repainted elements.
     * @param g the <code>Graphics</code> to paint into
     */
    public void redraw(@NotNull final Graphics g) {
        if (mouseTracker != null) {
            final GUIElement mouseElement = mouseTracker.getMouseElement();
            final long t0 = System.currentTimeMillis();

            hasChangedElements = false;
            //for (final GUIElement element : visibleElements) {
            //    element.paint(g);
            //    g.setColor(element == mouseElement ? Color.RED : Color.WHITE);
            //    g.drawRect(element.getElementX(), element.getElementY(), element.getWidth()-1, element.getHeight()-1);
            //}

            final long t1 = System.currentTimeMillis();
            g.setColor(Color.black);
            g.fillRect(12, 36, 200, 36);
            g.setColor(Color.YELLOW);
            if (mouseElement != null) {
                g.drawString(mouseElement.getName(), 16, 48);
            }
            g.drawString((t1-t0)+"ms", 16, 64);
        } else {
            hasChangedElements = false;
            paint(g);
            //for (final GUIElement element : visibleElements) {
            //    element.paint(g);
            //}
        }
    }

    /**
     * Checks whether any visible gui element of this gui has been changed since
     * it was painted last time.
     * @return <code>true</code> if any gui element has changed;
     *         <code>false</code> otherwise
     */
    public boolean needRedraw() {
        return hasChangedElements;
    }

    /**
     * Returns the first default gui element of this gui.
     * @return the default gui element, or <code>null</code>
     */
    @Nullable
    private GUIElement getDefaultElement() {
        final int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            final Component component = getComponent(i);
            if (component.isVisible() && component instanceof GUIElement) {
                final GUIElement element = (GUIElement)component;
                if (element.isDefault()) {
                    return element;
                }
            }
        }

        return null;
    }

    /**
     * Activates the first default gui element of this gui.
     */
    public void activateDefaultElement() {
        final Object defaultElement = getDefaultElement();
        if (defaultElement != null && defaultElement instanceof ActivatableGUIElement) {
            final ActivatableGUIElement activatableDefaultElement = (ActivatableGUIElement)defaultElement;
            activatableDefaultElement.setActive(true);
        }
    }

    /**
     * Returns the first gui element of this gui which belongs to the given
     * class and that's name ends with the given ending.
     * @param class_ the class to search for
     * @param ending the ending to search for
     * @return the gui element or <code>null</code> if not found
     * @noinspection TypeMayBeWeakened
     */
    @Nullable
    public <T extends GUIElement> T getFirstElementEndingWith(@NotNull final Class<T> class_, @NotNull final String ending) {
        final int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            final Component component = getComponent(i);
            if (component.isVisible() && component instanceof GUIElement) {
                final GUIElement element = (GUIElement)component;
                if (class_.isAssignableFrom(element.getClass()) && element.getName().endsWith(ending)) {
                    return class_.cast(element);
                }
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
     * @noinspection TypeMayBeWeakened
     */
    @Nullable
    public <T extends GUIElement> T getFirstElementNotEndingWith(@NotNull final Class<T> class_, @NotNull final String ending) {
        final int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            final Component component = getComponent(i);
            if (component.isVisible() && component instanceof GUIElement) {
                final GUIElement element = (GUIElement)component;
                if (class_.isAssignableFrom(element.getClass()) && !element.getName().endsWith(ending)) {
                    return class_.cast(element);
                }
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
    public <T extends GUIElement> T getFirstElement(@NotNull final Class<T> class_) {
        final int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            final Component component = getComponent(i);
            if (component.isVisible() && component instanceof GUIElement) {
                final GUIElement element = (GUIElement)component;
                if (class_.isAssignableFrom(element.getClass())) {
                    return class_.cast(element);
                }
            }
        }

        return null;
    }

    /**
     * Determines the {@link GUIElement} for a given coordinate.
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return the <code>GUIElement</code> at the given coordinate, or
     *         <code>null</code> if none was found
     */
    @Nullable
    public GUIElement getElementFromPoint(final int x, final int y) {
        final Component component = findComponentAt(x, y);
        if (component == null || !(component instanceof GUIElement)) {
            return null;
        }
        return (GUIElement)component;
    }

    /**
     * Sets the gui element owning the focus.
     * @param activeElement the gui element
     * @param active the new active state
     */
    public void setActiveElement(@NotNull final ActivatableGUIElement activeElement, final boolean active) {
        final ActivatableGUIElement previousActiveElement = this.activeElement;
        if (active) {
            if (forcedActive != null && forcedActive != activeElement) {
                return;
            }

            if (isActiveElement(activeElement)) {
                return;
            }

            this.activeElement = activeElement;
            if (previousActiveElement != null) {
                previousActiveElement.activeChanged();
            }
            assert this.activeElement != null;
            this.activeElement.activeChanged();

            guiAutoCloseListener = null;
        } else {
            if (!isActiveElement(activeElement)) {
                return;
            }

            this.activeElement = null;
            assert previousActiveElement != null;
            previousActiveElement.activeChanged();

            if (guiAutoCloseListener != null) {
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
    private boolean isActiveElement(@Nullable final ActivatableGUIElement activeElement) {
        return this.activeElement != null && this.activeElement == activeElement;
    }

    /**
     * Returns the gui element owning the focus.
     * @return the gui element owning the focus, or <code>null</code> if no such
     *         element exists
     */
    @Nullable
    public ActivatableGUIElement getActiveElement() {
        return activeElement;
    }

    /**
     * Dispatches a key press {@link KeyEvent}.
     * @param e the event to dispatch
     * @return whether a gui element did handle the event
     */
    public boolean handleKeyPress(@NotNull final KeyEvent e) {
        if (activeElement != null && activeElement instanceof KeyListener) {
            final KeyListener keyListener = (KeyListener)activeElement;
            if (keyListener.keyPressed(e)) {
                return true;
            }
        }

        return keyBindings.handleKeyPress(e);
    }

    /**
     * Dispatches a key typed {@link KeyEvent}.
     * @param e the event to dispatch
     * @return whether a gui element did handle the event
     */
    public boolean handleKeyTyped(@NotNull final KeyEvent e) {
        if (activeElement != null) {
            if (activeElement instanceof KeyListener) {
                if (((KeyListener)activeElement).keyTyped(e)) {
                    return true;
                }
            } else if (activeElement instanceof AbstractButton) {
                if (e.getKeyChar() == '\r' || e.getKeyChar() == '\n' || e.getKeyChar() == ' ') {
                    assert activeElement != null;
                    ((AbstractButton)activeElement).execute();
                    return true;
                }
            }
        }

        if (e.getKeyChar() == '\r' || e.getKeyChar() == '\n' || e.getKeyChar() == ' ') {
            final GUIElement defaultElement = getDefaultElement();
            if (defaultElement != null && defaultElement instanceof AbstractButton) {
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
     *         gui does not contain any <code>GUIText</code> gui elements
     */
    @Nullable
    private GUIText activateFirstTextArea() {
        final GUIText textArea = getFirstElement(GUIText.class);
        if (textArea != null) {
            textArea.setActive(true);
        }
        return textArea;
    }

    /**
     * Returns the first command text field of this gui and make it active.
     * @return the comment text field, or <code>null</code> if this gui does not
     *         contain any command text fields
     */
    @Nullable
    public GUIText activateCommandInput() {
        final GUIText textArea = activateFirstTextArea();
        if (textArea != null && textArea.getName().equals("command")) {
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
    public boolean deactivateCommandInput() {
        if (activeElement == null) {
            return false;
        }

        if (!(activeElement instanceof GUIText)) {
            return false;
        }

        final GUIElement textArea = activeElement;
        if (!textArea.getName().equals("command")) {
            return false;
        }

        assert activeElement != null;
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
    public <T extends GUIElement> T getFirstElement(@NotNull final Class<T> class_, @NotNull final String name) {
        final int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            final Component component = getComponent(i);
            if (component.isVisible() && component instanceof GUIElement) {
                final GUIElement element = (GUIElement)component;
                if (class_.isAssignableFrom(element.getClass()) && element.getName().equals(name)) {
                    return class_.cast(element);
                }
            }
        }
        return null;
    }

    /**
     * Notifies that one gui element has changed since last redraw.
     */
    public void setChangedElements() {
        hasChangedElements = true;
    }

    /**
     * Returns the key bindings instance for this gui.
     * @return the key bindings
     */
    @NotNull
    public KeyBindings getKeyBindings() {
        return keyBindings;
    }

    /**
     * Hides the dialog in a state.
     * @param state the state
     */
    public void hideInState(@NotNull final RendererGuiState state) {
        hideInStates.add(state);
    }

    /**
     * Returns whether this gui is visible in a state.
     * @param state the state
     * @return whether this gui is hidden
     */
    public boolean isHidden(@NotNull final RendererGuiState state) {
        return hideInStates.contains(state);
    }

    /**
     * Sets the {@link GuiAutoCloseListener} to be notified when this dialog
     * becomes inactive.
     * @param guiAutoCloseListener the listener to be notified or
     * <code>null</code>
     */
    public void setGuiAutoCloseListener(@Nullable final GuiAutoCloseListener guiAutoCloseListener) {
        this.guiAutoCloseListener = guiAutoCloseListener;
    }

    /**
     * Sets whether the state (position or size) has changed.
     * @param stateChanged whether the state has changed
     */
    public void setStateChanged(final boolean stateChanged) {
        this.stateChanged = stateChanged;
    }

    /**
     * Enables or disables hidden text in the first input field.
     * @param hideInput if set, hide input; else show input
     */
    public void setHideInput(final boolean hideInput) {
        final GUIText textArea = getFirstElement(GUIText.class);
        if (textArea != null) {
            textArea.setHideInput(hideInput);
        }
    }

    /**
     * Returns whether a given point is within this dialog's drawing area.
     * @param x the x-coordinate of the the point
     * @param y the y-coordinate of the the point
     * @return whether the coordinate is within the drawing area
     */
    public boolean isWithinDrawingArea(final int x, final int y) {
        return getX() <= x && x < getX()+getWidth() && getY() <= y && y < getY()+getHeight();
    }

    /**
     * Returns whether this dialog has changed from its default state.
     * @return whether the state has changed
     */
    public boolean isChangedFromDefault() {
        return getName() != null && getWidth() > 0 && getHeight() > 0 && stateChanged;
    }

    /**
     * Sets an {@link ActivatableGUIElement} that is always active. It prevents
     * any other element from getting active.
     * @param forcedActive the element to set or <code>null</code> to unset
     */
    public void setForcedActive(@Nullable final ActivatableGUIElement forcedActive) {
        this.forcedActive = forcedActive;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        final String name = getName();
        return (name == null ? "" : name)+"["+getWidth()+"x"+getHeight()+"]";
    }

    /**
     * Auto-resizes the dialog. Does nothing if this dialog is not auto-sizing.
     * @param screenWidth the screen width
     * @param screenHeight the screen height
     */
    public void autoSize(final int screenWidth, final int screenHeight) {
        final Extent extent = autoSize;
        if (extent != null) {
            setBounds(extent.getX(screenWidth, screenHeight), extent.getY(screenWidth, screenHeight), extent.getW(screenWidth, screenHeight), extent.getH(screenWidth, screenHeight));
        } else if (!initialPositionSet) {
            setPosition(0, 0);
        }
    }

}

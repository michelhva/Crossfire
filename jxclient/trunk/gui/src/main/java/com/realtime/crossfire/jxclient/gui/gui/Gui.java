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

package com.realtime.crossfire.jxclient.gui.gui;

import com.realtime.crossfire.jxclient.gui.button.AbstractButton;
import com.realtime.crossfire.jxclient.gui.commands.GUICommandFactory;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.skin.skin.Expression;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.EnumSet;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Combines a list of {@link GUIElement GUIElements} to for a gui.
 * <p/>
 * A dialog can be modal. Such dialogs do not propagate key or mouse events to
 * lower dialogs.
 * @author Andreas Kirschbaum
 */
public class Gui extends JComponent {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

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
     * Whether this dialog retains its position across restarts.
     */
    private boolean saveDialog = false;

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
     * Whether an initial position has been set.
     */
    private boolean initialPositionSet = false;

    /**
     * If set, the auto-close listener to notify if this dialog looses the
     * active gui element.
     */
    @Nullable
    private GuiAutoCloseListener guiAutoCloseListener = null;

    /**
     * The default x-coordinate for this dialog. Set to <code>null</code> for
     * default.
     */
    @Nullable
    private Expression defaultX = null;

    /**
     * The default y-coordinate for this dialog. Set to <code>null</code> for
     * default.
     */
    @Nullable
    private Expression defaultY = null;

    /**
     * Creates a new instance.
     * @param guiCommandFactory the gui command factory for creating commands
     */
    public Gui(@NotNull final GUICommandFactory guiCommandFactory) {
        keyBindings = new KeyBindings(null, guiCommandFactory);
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
     * Returns whether this dialog is an auto-size dialog. Auto-size dialogs
     * cannot be moved or resized manually.
     * @return whether this dialog is auto-size
     */
    public boolean isAutoSize() {
        return autoSize != null;
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
     * @return the <code>GUIElement</code> at the given coordinate or
     *         <code>null</code> if none was found
     */
    @Nullable
    public AbstractGUIElement getElementFromPoint(final int x, final int y) {
        Component component = findComponentAt(x, y);
        while (component != null) {
            if (component instanceof AbstractGUIElement) {
                return (AbstractGUIElement)component;
            }
            component = component.getParent();
        }
        return null;
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
        if (activeElement != null && activeElement instanceof KeyPressedHandler && ((KeyPressedHandler)activeElement).keyPressed(e)) {
            return true;
        }

        if (activeElement != null && activeElement instanceof KeyPressedHandler) {
            final KeyPressedHandler keyListener = (KeyPressedHandler)activeElement;
            if (keyListener.keyPressed(e)) {
                return true;
            }
        }

        switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
        case KeyEvent.VK_SPACE:
            final GUIElement defaultElement = getDefaultElement();
            if (defaultElement != null && defaultElement instanceof AbstractButton) {
                ((AbstractButton)defaultElement).execute();
                return true;
            }
            break;
        }

        return keyBindings.handleKeyPress(e);
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
            final Dimension preferredSize = getPreferredSize();
            setBounds(extent.getX(screenWidth, screenHeight, preferredSize.width, preferredSize.height), extent.getY(screenWidth, screenHeight, preferredSize.width, preferredSize.height), extent.getW(screenWidth, screenHeight, preferredSize.width, preferredSize.height), extent.getH(screenWidth, screenHeight, preferredSize.width, preferredSize.height));
        } else if (!initialPositionSet) {
            final Dimension preferredSize = getPreferredSize();
            final int x;
            if (defaultX == null) {
                x = (screenWidth-preferredSize.width)/2;
            } else {
                x = defaultX.evaluate(screenWidth, screenHeight, preferredSize.width, preferredSize.height);
            }
            final int y;
            if (defaultY == null) {
                y = (screenHeight-preferredSize.height)/2;
            } else {
                y = defaultY.evaluate(screenWidth, screenHeight, preferredSize.width, preferredSize.height);
            }
            setSize(preferredSize.width, preferredSize.height);
            if (defaultX != null && defaultY != null) {
                setPosition(x-preferredSize.width/2, y-preferredSize.height/2);
            }
        }

        showDialog(getX(), getY(), screenWidth, screenHeight);
    }

    /**
     * Sets the default position for this dialog.
     * @param defaultX the default x-coordinate
     * @param defaultY the default y-coordinate
     */
    public void setDefaultPosition(@NotNull final Expression defaultX, @NotNull final Expression defaultY) {
        this.defaultX = defaultX;
        this.defaultY = defaultY;
    }

    /**
     * Returns whether this dialog retains its position across restarts.
     * @return whether this dialog retains its position across restarts
     */
    public boolean isSaveDialog() {
        return saveDialog;
    }

    /**
     * Makes this dialog retain its position across restarts.
     */
    public void setSaveDialog() {
        saveDialog = true;
    }

    /**
     * Sets the position of a dialog but makes sure the dialog is fully
     * visible.
     * @param x the dialog's x coordinate
     * @param y the dialog's y coordinate
     * @param windowWidth the main window's width
     * @param windowHeight the main window's height
     */
    public void showDialog(final int x, final int y, final int windowWidth, final int windowHeight) {
        final int newX;
        final int newY;
        if (isAutoSize()) {
            newX = x;
            newY = y;
        } else {
            newX = Math.max(Math.min(x, windowWidth-getWidth()), 0);
            newY = Math.max(Math.min(y, windowHeight-getHeight()), 0);
        }
        setPosition(newX, newY);
    }

}

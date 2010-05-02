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

import com.realtime.crossfire.jxclient.skin.skin.Expression;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for GUI elements to be shown in {@link Gui}s.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class GUIElement extends JComponent {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link Gui} this element is part of. Set to <code>null</code> if this
     * element is not part of any gui.
     */
    @Nullable
    private Gui gui = null;

    /**
     * The {@link GUIElementChangedListener} to be notified whenever the {@link
     * #changed} flag is set.
     */
    @Nullable
    private GUIElementChangedListener changedListener = null;

    /**
     * Object used to synchronize on {@link #bufferedImage} contents.
     */
    @NotNull
    protected final Object bufferedImageSync = new Object();

    /**
     * The backbuffer image of this element. It is updated in {@link #render()}
     * and {@link #render(Graphics2D)}. {@link #paintComponent(Graphics)}
     * copies the contents to screen.
     */
    @Nullable
    private BufferedImage bufferedImage;

    /**
     * Whether this element is visible.
     */
    private boolean visible = true;

    /**
     * Whether this element is the default element. The default element is
     * selected with the ENTER key.
     */
    private boolean isDefault = false;

    /**
     * Whether this gui element should be ignored for user interaction.
     */
    private boolean ignore = false;

    /**
     * The extent of this element.
     */
    @NotNull
    private final Extent extent;

    /**
     * The transparency for {@link #bufferedImage}.
     */
    private final int transparency;

    /**
     * The name of this element.
     */
    @NotNull
    private final String name;

    /**
     * Whether {@link #bufferedImage} has changed.
     */
    private boolean changed;

    /**
     * The {@link TooltipManager} to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to notify.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * The tooltip text to show when the mouse is inside this element. May be
     * <code>null</code> to show no tooltip.
     */
    @Nullable
    private TooltipText tooltipText = null;

    /**
     * Create a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name The name of this element.
     * @param extent the extent of this element
     * @param transparency The transparency value for the backing buffer
     */
    protected GUIElement(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final Extent extent, final int transparency) {
        setDoubleBuffered(false);
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.extent = extent;
        this.transparency = transparency;
        setOpaque(true);
        setSize(1, 1);
        createBuffer();
    }

    /**
     * Releases all allocated resources.
     */
    public void dispose() {
    }

    /**
     * Return the {@link Gui} this element is part of.
     * @return The gui, or <code>null</code>.
     */
    @Nullable
    public Gui getGui() {
        return gui;
    }

    /**
     * Set the {@link Gui} this element is part of.
     * @param gui The gui, or <code>null</code>.
     */
    public void setGui(@Nullable final Gui gui) {
        this.gui = gui;
        if (visible && gui != null) {
            gui.setChangedElements();
        }
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns the element's absolute screen coordinate.
     * @return the element's absolute x coordinate
     */
    public int getElementX() {
        return gui != null ? gui.getX()+getX() : getX();
    }

    /**
     * Returns the element's absolute screen coordinate.
     * @return the element's absolute y coordinate
     */
    public int getElementY() {
        return gui != null ? gui.getY()+getY() : getY();
    }

    /**
     * Returns whether this element is visible.
     * @return whether this element is visible
     */
    public boolean isElementVisible() {
        return visible;
    }

    /**
     * Sets whether this element is visible.
     * @param visible whether this element is visible
     */
    public void setElementVisible(final boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            setChanged();
            final Gui tmpGui = gui;
            if (tmpGui != null) {
                tmpGui.updateVisibleElement(this);
            }
        }
    }

    /**
     * Returns whether this element is the default element. The default element
     * is selected with the ENTER key.
     * @return whether this element is the default element
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Sets whether this element is the default element. The default element is
     * selected with the ENTER key.
     * @param isDefault whether this element is the default element
     */
    public void setDefault(final boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Return whether this gui element should be ignored for user interaction.
     * @return Whether this gui element should be ignored for user interaction.
     */
    public boolean isIgnore() {
        return ignore;
    }

    /**
     * Mark this gui element to be ignored for user interaction.
     */
    public void setIgnore() {
        ignore = true;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    /**
     * Will be called when the user has clicked (pressed+released) this element.
     * This event will be delivered after {@link #mouseReleased(MouseEvent)}.
     * @param e The mouse event relative to this element.
     */
    public void mouseClicked(@NotNull final MouseEvent e) {
        if (gui != null) {
            elementListener.mouseClicked(gui);
        }
    }

    /**
     * Will be called when the mouse has entered the bounding box of this
     * element.
     * @param e The mouse event relative to this element.
     */
    public void mouseEntered(@NotNull final MouseEvent e) {
        tooltipManager.setElement(this);
    }

    /**
     * Will be called when the mouse has left the bounding box of this element.
     * This function will not be called unless {@link #mouseEntered(MouseEvent)}
     * has been called before.
     * @param e The mouse event relative to this element.
     */
    public void mouseExited(@NotNull final MouseEvent e) {
        tooltipManager.unsetElement(this);
    }

    /**
     * Will be called when the user has pressed the mouse inside this element.
     * @param e The mouse event relative to this element.
     */
    public void mousePressed(@NotNull final MouseEvent e) {
    }

    /**
     * Will be called when the user has released the mouse. This event may be
     * deleivered even if no previous {@link #mousePressed(MouseEvent)} has been
     * delivered before.
     * @param e The mouse event relative to this element.
     */
    public void mouseReleased(@NotNull final MouseEvent e) {
    }

    /**
     * Will be called when the mouse moves within this component. before.
     * @param e The mouse event relative to this element.
     */
    public void mouseMoved(@NotNull final MouseEvent e) {
    }

    /**
     * Will be called when the mouse moves within this component while the
     * button is pressed. This event will be delivered after {@link
     * #mouseMoved(MouseEvent)}.
     * <p/>
     * <p>Note: if the mouse leaves this elements's bounding box while the mouse
     * button is still pressed, further <code>mouseDragged</code> (but no
     * <code>mouseMoved</code>) events will be generated.
     * @param e The mouse event relative to this element.
     */
    public void mouseDragged(@NotNull final MouseEvent e) {
    }

    /**
     * Record that {@link #bufferedImage} has changed and must be repainted.
     */
    public void setChanged() {
        if (changed) {
            return;
        }

        changed = true;
        if (visible) {
            if (gui != null) {
                gui.setChangedElements();
            }

            if (changedListener != null) {
                changedListener.notifyChanged(this);
            }
        }
    }

    /**
     * Record that {@link #bufferedImage} has changed and must be repainted.
     * Does not notify listeners.
     */
    protected void setChangedNoListeners() {
        changed = true;
    }

    /**
     * Returns the changed flag.
     * @return the changed flag
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Clears the changed flag.
     */
    public void resetChanged() {
        changed = false;
    }

    /**
     * Set the tooltip text to show when the mouse is inside this element.
     * @param tooltipText The text to show, or <code>null</cod> to disable the
     * tooltip for this element.
     */
    public void setTooltipText(@Nullable final String tooltipText) {
        final Gui tmpGui = gui;
        if (tmpGui != null) {
            setTooltipText(tooltipText, tmpGui.getX()+getX(), tmpGui.getY()+getY(), getWidth(), getHeight());
        }
    }

    /**
     * Set the tooltip text to show when the mouse is inside this element.
     * @param tooltipText The text to show, or <code>null</cod> to disable the
     * tooltip for this element.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the w coordinate
     * @param h the h coordinate
     */
    public void setTooltipText(@Nullable final String tooltipText, final int x, final int y, final int w, final int h) {
        final TooltipText oldTooltipText = this.tooltipText;
        if (oldTooltipText == null) {
            if (tooltipText == null) {
                return;
            }
        } else {
            if (tooltipText != null && tooltipText.equals(oldTooltipText.getText()) && x == oldTooltipText.getX() && y == oldTooltipText.getY() && w == oldTooltipText.getW() && h == oldTooltipText.getH()) {
                return;
            }
        }
        this.tooltipText = tooltipText == null ? null : new TooltipText(tooltipText, x, y, w, h);
        tooltipManager.updateElement(this);
    }

    /**
     * Return the tooltip text to show when the mouse is inside this element.
     * @return The text to show, or <code>null</cod> to disable the tooltip for
     *         this element.
     */
    @Nullable
    public TooltipText getTooltipText() {
        return tooltipText;
    }

    /**
     * Change the location of this gui element.
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
    public void setElementLocation(final int x, final int y) {
        extent.setLocation(new Expression(x, 0, 0), new Expression(y, 0, 0));
        if (getX() != x || getY() != y) {
            setLocation(x, y);
            setChanged();
        }
    }

    /**
     * Change the size of this gui element.
     * @param w The new width.
     * @param h The new height.
     */
    protected void setElementSize(final int w, final int h) {
        extent.setSize(new Expression(w, 0, 0), new Expression(h, 0, 0));
        if (getWidth() != w || getHeight() != h) {
            final Dimension size = new Dimension(w, h);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            createBuffer();
            setChanged();
        }
    }

    /**
     * Allocates {@link #bufferedImage} with the element's current size.
     */
    private void createBuffer() {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        bufferedImage = gconf.createCompatibleImage(getWidth(), getHeight(), transparency);
    }

    /**
     * Re-create the contents of {@link #bufferedImage}.
     */
    private void render() {
        synchronized (bufferedImageSync) {
            final Graphics2D g = createBufferGraphics();
            try {
                render(g);
            } finally {
                g.dispose();
            }
        }
    }

    /**
     * Paint the elements's contents into the passed graphics.
     * @param g The gaphics to paint to.
     */
    protected abstract void render(@NotNull final Graphics2D g);

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        if (bufferedImage == null) {
            throw new IllegalStateException();
        }

        synchronized (bufferedImageSync) {
            if (changed) {
                changed = false;
                render();
            }

            g.drawImage(bufferedImage, getElementX(), getElementY(), null);
        }
    }

    /**
     * Sets the {@link GUIElementChangedListener} to be notified. Note that at
     * most one such listener may be set per gui element.
     * @param changedListener the listener or <code>null</code> to unset
     */
    public void setChangedListener(@Nullable final GUIElementChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    /**
     * Calls {@link BufferedImage#createGraphics()} on {@link #bufferedImage}.
     * Checks that the calling thread holds the lock {@link
     * #bufferedImageSync}.
     * @return the graphics instance; must be released afterwards
     */
    @NotNull
    protected Graphics2D createBufferGraphics() {
        if (bufferedImage == null) {
            throw new IllegalStateException();
        }

        assert Thread.holdsLock(bufferedImageSync);
        assert bufferedImage != null;
        return bufferedImage.createGraphics();
    }

    /**
     * Returns whether the backbuffer for this element has been created.
     * @return whether the backbuffer has been created
     */
    protected boolean hasBufferedImage() {
        assert Thread.holdsLock(bufferedImageSync);
        return bufferedImage != null;
    }

    /**
     * Updates the location and size to a new screen resolution.
     * @param screenWidth the new screen width
     * @param screenHeight the new screen height
     */
    public void updateResolution(final int screenWidth, final int screenHeight) {
        final int width = extent.getW(screenWidth, screenHeight);
        final int height = extent.getH(screenWidth, screenHeight);
        if (bufferedImage == null || width != getWidth() || height != getHeight()) {
            final Dimension size = new Dimension(width, height);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            createBuffer();
            setChanged();
        }

        final int x = extent.getX(screenWidth, screenHeight);
        final int y = extent.getY(screenWidth, screenHeight);
        setLocation(x, y);
    }

    @Deprecated
    protected void updateResolutionConstant() {
        final int width = extent.getConstantW();
        final int height = extent.getConstantH();
        if (bufferedImage == null || width != getWidth() || height != getHeight()) {
            final Dimension size = new Dimension(width, height);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            createBuffer();
            setChanged();
        }
    }

}

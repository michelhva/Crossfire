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

import java.awt.Component;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for GUI elements to be shown in {@link Gui Guis}.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class AbstractGUIElement extends JComponent implements GUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link GUIElementChangedListener} to be notified whenever this
     * element has changed.
     */
    @Nullable
    private GUIElementChangedListener changedListener = null;

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
     * The name of this element.
     */
    @NotNull
    private final String name;

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
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param transparency the transparency value for the backing buffer
     */
    protected AbstractGUIElement(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int transparency) {
        setDoubleBuffered(false);
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        setOpaque(transparency != Transparency.TRANSLUCENT);
        setFocusable(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefault(final boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIgnore() {
        ignore = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIgnore() {
        return ignore;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        final Gui gui = GuiUtils.getGui(this);
        if (gui != null) {
            elementListener.raiseDialog(gui);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(@NotNull final MouseEvent e, final boolean debugGui) {
        if (tooltipText != null) {
            setTooltipText(tooltipText.getText());
        }
        tooltipManager.setElement(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
        tooltipManager.unsetElement(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        final Gui gui = GuiUtils.getGui(this);
        if (gui != null) {
            elementListener.raiseDialog(gui);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(@NotNull final MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(@NotNull final MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(@NotNull final MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChanged() {
        repaint();
        if (isVisible()) {
            if (changedListener != null) {
                changedListener.notifyChanged(this);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTooltipText(@Nullable final String tooltipText) {
        final Component gui = GuiUtils.getGui(this);
        if (gui != null) {
            setTooltipText(tooltipText, gui.getX()+getX(), gui.getY()+getY(), getWidth(), getHeight());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public TooltipText getTooltipText() {
        return tooltipText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChangedListener(@Nullable final GUIElementChangedListener changedListener) {
        this.changedListener = changedListener;
    }

}

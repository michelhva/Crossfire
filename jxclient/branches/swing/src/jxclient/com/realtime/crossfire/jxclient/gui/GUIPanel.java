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

package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementChangedListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.gui.TooltipText;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.JLayeredPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIElement} that is transparent but can contain other elements.
 * @author Andreas Kirschbaum
 */
public class GUIPanel extends JLayeredPane implements GUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link Gui} this element is part of. Set to <code>null</code> if this
     * element is not part of any gui.
     */
    @Nullable
    private Gui gui;

    /**
     * The {@link GUIElementChangedListener} to be notified whenever the {@link
     * #changed} flag is set.
     */
    @Nullable
    private GUIElementChangedListener changedListener = null;

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
     * The name of this element.
     */
    @NotNull
    private final String name;

    /**
     * Whether this element has changed.
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
     * Whether this component is active.
     */
    private boolean activeComponent = false;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     */
    public GUIPanel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name) {
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        setDoubleBuffered(false);
        setOpaque(false);
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
    @Nullable
    @Override
    public Gui getGui() {
        return gui;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGui(@Nullable final Gui gui) {
        this.gui = gui;
        if (visible && gui != null) {
            gui.setChangedElements();
        }
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
    public int getElementX() {
        return gui != null ? gui.getX()+getX() : getX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElementY() {
        return gui != null ? gui.getY()+getY() : getY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isElementVisible() {
        return visible;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public boolean isIgnore() {
        return ignore;
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
        if (gui != null) {
            elementListener.mouseClicked(gui);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(@NotNull final MouseEvent e) {
        tooltipManager.setElement(this);
        activeComponent = true;
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
        tooltipManager.unsetElement(this);
        activeComponent = false;
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
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
     * {@inheritDoc}
     */
    @Override
    public boolean isChanged() {
        return changed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetChanged() {
        changed = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTooltipText(@Nullable final String tooltipText) {
        final Component tmpGui = gui;
        if (tmpGui != null) {
            setTooltipText(tooltipText, tmpGui.getX()+getX(), tmpGui.getY()+getY(), getWidth(), getHeight());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isElementAtPoint(final int x, final int y) {
        return false; // XXX
    }

    /**
     * Called at the end of {@link #paintComponent(Graphics)}.
     * @param g the graphics
     */
    protected void finishPaintComponent(final Graphics g) {
        if (activeComponent) {
            g.setColor(Color.RED);
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        }
    }

}

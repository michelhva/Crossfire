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

import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.util.SwingUtilities2;
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
     * The global {@link GuiFactory} instance.
     */
    @NotNull
    private final GuiFactory guiFactory;

    /**
     * The {@link GUIElementChangedListener} to be notified whenever this
     * element has changed.
     */
    @Nullable
    private GUIElementChangedListener changedListener;

    /**
     * Whether this element is the default element. The default element is
     * selected with the ENTER key.
     */
    private boolean isDefault;

    /**
     * If set, change listeners will not be notified.
     */
    private boolean inhibitListeners;

    /**
     * Whether this gui element should be ignored for user interaction.
     */
    private boolean ignore;

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
     * Used to avoid refreshing items all the time.
     */
    private boolean pendingChange;

    /**
     * The {@link Runnable} that implements the code of {@link #setChanged()}
     * which must run on the EDT.
     */
    @NotNull
    private final Runnable setChangedRunnable = new Runnable() {

        @Override
        public void run() {
            synchronized (setChangedRunnable) {
                pendingChange = false;
                if (inhibitListeners) {
                    return;
                }
            }
            final Gui parent = guiFactory.getGui(AbstractGUIElement.this);
            if (parent != null) {
                parent.getComponent().repaint();
            }
            if (isVisible() && changedListener != null) {
                changedListener.notifyChanged();
            }
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param transparency the transparency value for the backing buffer
     * @param guiFactory the global GUI factory instance
     */
    protected AbstractGUIElement(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int transparency, @NotNull final GuiFactory guiFactory) {
        this.guiFactory = guiFactory;
        setDoubleBuffered(false);
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        setOpaque(transparency != Transparency.TRANSLUCENT);
        setFocusable(false);
    }

    @Override
    public void dispose() {
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setDefault(final boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public void setIgnore() {
        ignore = true;
    }

    @Override
    public boolean isIgnore() {
        return ignore;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        if (isEnabled()) {
            elementListener.raiseDialog(this);
        }
    }

    @Override
    public void mouseEntered(@NotNull final MouseEvent e, final boolean debugGui) {
        if (isEnabled()) {
            tooltipManager.setElement(this);
        }
    }

    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
        if (isEnabled()) {
            tooltipManager.unsetElement(this);
        }
    }

    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        if (isEnabled()) {
            elementListener.raiseDialog(this);
        }
    }

    @Override
    public void mouseReleased(@NotNull final MouseEvent e) {
    }

    @Override
    public void mouseMoved(@NotNull final MouseEvent e) {
    }

    @Override
    public void mouseDragged(@NotNull final MouseEvent e) {
    }

    @Override
    public void setChanged() {
        synchronized (setChangedRunnable) {
            if (!inhibitListeners && !pendingChange) {
                pendingChange = true;
                SwingUtilities2.invokeLater(setChangedRunnable);
            }
        }
    }

    /**
     * Returns the current tooltip text.
     * @return the tooltip text or {@code null} to not show a tooltip
     */
    @Nullable
    public abstract TooltipText getTooltip();

    /**
     * Creates a {@link TooltipText} instance relative to this instance.
     * @param tooltipText the text of the instance or {@code null} to return
     * {@code null}
     * @return the new instance or {@code null} if this component is not in a
     * GUI
     */
    @Nullable
    protected TooltipText newTooltipText(@Nullable final String tooltipText) {
        if (tooltipText == null) {
            return null;
        }

        final Gui gui = guiFactory.getGui(this);
        if (gui == null) {
            return null;
        }

        return new TooltipText(tooltipText, gui.getComponent().getX()+getX(), gui.getComponent().getY()+getY(), getWidth(), getHeight());

    }

    /**
     * Must be called whenever the {@link #getTooltip() tooltip} may have
     * changed.
     */
    protected void tooltipChanged() {
        if (isEnabled()) {
            tooltipManager.tooltipTextChanged(this);
        }
    }

    @Override
    public void setChangedListener(@Nullable final GUIElementChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    /**
     * Prevents change listeners to be notified.
     */
    public void inhibitListeners() {
        synchronized (setChangedRunnable) {
            inhibitListeners = true;
        }
    }

}

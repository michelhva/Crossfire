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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for GUI elements to be shown in {@link Gui}s.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class AbstractGUIElement extends JComponent implements GUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link GUIElementChangedListener} to be notified whenever the {@link
     * #changed} flag is set.
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
     * Whether the contents have changed.
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
    @Nullable
    @Override
    public Gui getGui() {
        for (Component component = this; component != null; component = component.getParent()) {
            if (component instanceof Gui) {
                return (Gui)component;
            }
        }
        return null;
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
        final Component gui = getGui();
        int x = gui != null ? gui.getX() : 0;
        for (Component component = this; component != null && !(component instanceof Gui); component = component.getParent()) {
            x += component.getX();
        }
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElementY() {
        final Component gui = getGui();
        int y = gui != null ? gui.getY() : 0;
        for (Component component = this; component != null && !(component instanceof Gui); component = component.getParent()) {
            y += component.getY();
        }
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isElementVisible() {
        return isVisible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setElementVisible(final boolean visible) {
        setVisible(visible);
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
        final Gui gui = getGui();
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
        if (!activeComponent) {
            activeComponent = true;
            setChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
        tooltipManager.unsetElement(this);
        if (activeComponent) {
            activeComponent = false;
            setChanged();
        }
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
        if (isVisible()) {
            final Gui gui = getGui();
            if (gui != null) {
                gui.setChangedElements();
            }

            if (changedListener != null) {
                changedListener.notifyChanged(this);
            }
        }
    }

    /**
     * Records that this element has changed and must be repainted. Does not
     * notify listeners.
     */
    protected void setChangedNoListeners() {
        changed = true;
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
        final Component gui = getGui();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isElementAtPoint(final int x, final int y) {
        final int elementX = getElementX();
        final int elementY = getElementY();
        return !ignore && elementX <= x && x < elementX+getWidth() && elementY <= y && y < elementY+getHeight();
    }

    /**
     * Returns the extents of a string when rendered in a given {@link Font} on
     * this component.
     * @param text the text
     * @param font the font
     * @return the extends
     */
    @NotNull
    public static Dimension getTextDimension(@NotNull final String text, @NotNull final Font font) {
        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final Graphics2D g = graphicsEnvironment.createGraphics(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)); // XXX
        try {
            final FontRenderContext fontRenderContext = g.getFontRenderContext();

            final TextLayout textLayout1 = new TextLayout(text.isEmpty() ? " " : text, font, fontRenderContext);
            final RectangularShape bounds1 = textLayout1.getBounds();

            final TextLayout textLayout2 = new TextLayout("Xg", font, fontRenderContext);
            final RectangularShape bounds2 = textLayout2.getBounds();

            return new Dimension((int)Math.ceil(bounds1.getWidth()), (int)Math.ceil(bounds2.getHeight()));
        } finally {
            g.dispose();
        }
    }

    /**
     * Called at the end of {@link #paintComponent(Graphics)}.
     * @param g the graphics
     */
    protected void finishPaintComponent(@NotNull final Graphics g) {
        if (activeComponent) {
            g.setColor(Color.RED);
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
            g.drawString(name, 2, 16);
        }
    }

}

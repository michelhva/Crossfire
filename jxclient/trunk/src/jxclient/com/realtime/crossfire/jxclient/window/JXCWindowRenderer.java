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

package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiAutoCloseListener;
import com.realtime.crossfire.jxclient.gui.log.Buffer;
import com.realtime.crossfire.jxclient.gui.log.GUILog;
import com.realtime.crossfire.jxclient.gui.log.GUIMessageLog;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.skin.Resolution;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Andreas Kirschbaum
 */
public class JXCWindowRenderer
{
    @NotNull
    private final JFrame frame;

    @NotNull
    private final MouseListener mouseTracker;

    /**
     * The semaphore used to synchronized map model updates and map view
     * redraws.
     */
    @NotNull
    private final Object redrawSemaphore;

    @Nullable
    private BufferStrategy bufferStrategy = null;

    @Nullable
    private DisplayMode oldDisplayMode = null;

    @Nullable
    private DisplayMode displayMode = null;

    /**
     * If set, the content of {@link #openDialogs} has changed.
     */
    private boolean openDialogsChanged = false;

    /**
     * Currently opened dialogs. The ordering is the painting order: the
     * topmost dialog is at the end.
     */
    @NotNull
    private final CopyOnWriteArrayList<Gui> openDialogs = new CopyOnWriteArrayList<Gui>();

    /**
     * Listeners to be notified about {@link #rendererGuiState} changes.
     */
    @NotNull
    private final Collection<RendererGuiStateListener> rendererGuiStateListeners = new CopyOnWriteArrayList<RendererGuiStateListener>();

    /**
     * If set, {@link #currentGui} has changed.
     */
    private boolean currentGuiChanged = false;

    @NotNull
    private Gui currentGui;

    /**
     * The tooltip to use, or <code>null</code> if no tooltips should be shown.
     */
    @Nullable
    private GUIElement tooltip = null;

    /**
     * If set, force a full repaint.
     */
    private volatile boolean forcePaint = false;

    /**
     * If set, do not repaint anything. It it set while a map update is in
     * progress.
     */
    private volatile boolean inhibitPaintMapUpdate = false;

    /**
     * If set, do not repaint anything. It it set while the main widnow is
     * iconified.
     */
    private volatile boolean inhibitPaintIconified = false;

    /**
     * If set, at least one call to {@link #redrawGUI()} has been dropped while
     * {@link #inhibitPaintMapUpdate} or {@link #inhibitPaintIconified} was
     * set.
     */
    private volatile boolean skippedPaint = false;

    /**
     * The x-offset of of the visible window.
     */
    private int offsetX = 0;

    /**
     * The y-offset of of the visible window.
     */
    private int offsetY = 0;

    /**
     * Receords whether full-screen has been activated.
     */
    private boolean isFullScreen = false;

    /**
     * The current gui state.
     */
    @NotNull
    private RendererGuiState rendererGuiState = RendererGuiState.START;

    /**
     * The {@link GuiAutoCloseListener} used to track auto-closing dialogs.
     */
    @NotNull
    private final GuiAutoCloseListener guiAutoCloseListener = new GuiAutoCloseListener()
    {
        /** {@inheritDoc} */
        @Override
        public void autoClosed(@NotNull final Gui gui)
        {
            closeDialog(gui);
        }
    };

    /**
     * The listener to detect map model changes.
     */
    @NotNull
    private final CrossfireUpdateMapListener crossfireUpdateMapListener = new CrossfireUpdateMapListener()
    {
        /** {@inheritDoc} */
        @Override
        public void newMap(final int mapWidth, final int mapHeight)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapBegin()
        {
            inhibitPaintMapUpdate = true;
            skippedPaint = false;
        }

        /** {@inheritDoc} */
        @Override
        public void mapClear(final int x, final int y)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapDarkness(final int x, final int y, final int darkness)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapFace(final int x, final int y, final int layer, final int faceNum)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapAnimation(final int x, final int y, final int layer, final int animationNum, final int animationType)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapAnimationSpeed(final int x, final int y, final int layer, final int animSpeed)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void scroll(final int dx, final int dy)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapEnd()
        {
            if (skippedPaint)
            {
                forcePaint = true;
            }
            inhibitPaintMapUpdate = false;
        }

        /** {@inheritDoc} */
        @Override
        public void addAnimation(final int animation, final int flags, @NotNull final int[] faces)
        {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param frame the associated window
     * @param mouseTracker the mouse tracker instance
     * @param redrawSemaphore the semaphore used to synchronized map model
     * updates and map view redraws
     * @param crossfireServerConnection the server connection to monitor
     */
    public JXCWindowRenderer(@NotNull final JFrame frame, @NotNull final MouseListener mouseTracker, @NotNull final Object redrawSemaphore, @NotNull final CrossfireServerConnection crossfireServerConnection)
    {
        this.frame = frame;
        this.mouseTracker = mouseTracker;
        this.redrawSemaphore = redrawSemaphore;
        crossfireServerConnection.addCrossfireUpdateMapListener(crossfireUpdateMapListener);
    }

    /**
     * Repaint the window.
     */
    public void repaint()
    {
        forcePaint = true;
    }

    public void initRendering(@NotNull final Resolution resolution, final boolean fullScreen)
    {
        displayMode = new DisplayMode(resolution.getWidth(), resolution.getHeight(), DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN);
        isFullScreen = false;
        oldDisplayMode = null;

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (fullScreen && gd.isFullScreenSupported())
        {
            frame.setUndecorated(true);
            gd.setFullScreenWindow(frame);
            isFullScreen = true;
            final DisplayMode currentDisplayMode = gd.getDisplayMode();
            if (currentDisplayMode.getWidth() == displayMode.getWidth() && currentDisplayMode.getHeight() == displayMode.getHeight())
            {
                // full-screen mode, no display mode change
            }
            else
            {
                if (!gd.isDisplayChangeSupported())
                {
                    isFullScreen = false;
                    gd.setFullScreenWindow(null);
//                    frame.setUndecorated(false); // XXX: cannot be called anymore
                    // windowed mode
                }
                else
                {
                    boolean ok = true;
                    try
                    {
                        gd.setDisplayMode(displayMode);
                    }
                    catch (final IllegalArgumentException ex)
                    {
                        ok = false;
                    }
                    if (ok)
                    {
                        oldDisplayMode = currentDisplayMode;
                        // full-screen mode, display mode change
                    }
                    else
                    {
                        isFullScreen = false;
                        gd.setFullScreenWindow(null);
//                        frame.setUndecorated(false); // XXX: cannot be called anymore
                        // windowed mode
                    }
                }
            }
        }
        else
        {
            // windowed mode
        }

        if (!isFullScreen)
        {
            if (fullScreen)
            {
                System.out.println("Warning ! True full-screen support is not available.");
            }

            final Dimension size = new Dimension(displayMode.getWidth(), displayMode.getHeight());
            frame.getRootPane().setPreferredSize(size);
            frame.pack();
            frame.setResizable(false);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        }
        frame.createBufferStrategy(2);
        bufferStrategy = frame.getBufferStrategy();

        final Insets insets = frame.getInsets();
        offsetX = insets.left;
        offsetY = insets.top;
    }

    public void endRendering()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (oldDisplayMode != null)
        {
            gd.setDisplayMode(oldDisplayMode);
            oldDisplayMode = null;
        }
        if (isFullScreen)
        {
            isFullScreen = false;
            gd.setFullScreenWindow(null);
        }
    }

    public void redrawGUI()
    {
        if (inhibitPaintMapUpdate || inhibitPaintIconified)
        {
            skippedPaint = true;
            return;
        }

        if (forcePaint)
        {
            forcePaint = false;
        }
        else if (!needRedraw())
        {
            return;
        }

        do
        {
            do
            {
                assert bufferStrategy != null;
                final Graphics g = bufferStrategy.getDrawGraphics();
                try
                {
                    g.translate(offsetX, offsetY);
                    assert bufferStrategy != null;
                    if (bufferStrategy.contentsRestored())
                    {
                        redrawBlack(g);
                    }
                    redraw(g);
                }
                finally
                {
                    g.dispose();
                }
                assert bufferStrategy != null;
            }
            while (bufferStrategy.contentsLost());
            assert bufferStrategy != null;
            bufferStrategy.show();
            assert bufferStrategy != null;
        }
        while (bufferStrategy.contentsLost());
    }

    /**
     * Paints the view into the given graphics instance.
     * @param g the graphics instance to paint to
     */
    public void redraw(@NotNull final Graphics g)
    {
        synchronized (redrawSemaphore)
        {
            redrawGUIBasic(g);
            redrawGUIDialog(g);
            redrawTooltip(g);
        }
    }

    public void clearGUI(@NotNull final Gui gui)
    {
        setCurrentGui(gui);
        for (int ig = 0; ig < 3; ig++)
        {
            assert bufferStrategy != null;
            final Graphics g = bufferStrategy.getDrawGraphics();
            g.translate(offsetX, offsetY);
            redrawBlack(g);
            g.dispose();
            assert bufferStrategy != null;
            bufferStrategy.show();
        }
    }

    private void redrawGUIBasic(@NotNull final Graphics g)
    {
        currentGuiChanged = false;
        currentGui.redraw(g);
    }

    private void redrawGUIDialog(@NotNull final Graphics g)
    {
        openDialogsChanged = false;
        for (final Gui dialog : openDialogs)
        {
            if (!dialog.isHidden(rendererGuiState))
            {
                dialog.redraw(g);
            }
        }
    }

    private void redrawTooltip(@NotNull final Graphics g)
    {
        final GUIElement tmpTooltip = tooltip;
        if (tmpTooltip != null)
        {
            if (tmpTooltip.isElementVisible())
            {
                tmpTooltip.paintComponent(g);
            }
            else
            {
                tmpTooltip.resetChanged();
            }
        }
    }

    private void redrawBlack(@NotNull final Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, currentGui.getWidth(), currentGui.getHeight());
    }

    /**
     * Open a dialog. Raises an already opened dialog.
     *
     * @param dialog The dialog to show.
     *
     * @param autoCloseOnDeactivate whether the dialog should auto-close when
     * it becomes inactive; ignored if the dialog is already open
     *
     * @return Whether the dialog was opened or raised; <code>false</code> if
     * the dialog already was opened as the topmost dialog.
     */
    public boolean openDialog(@NotNull final Gui dialog, final boolean autoCloseOnDeactivate)
    {
        if (dialog == currentGui)
        {
            return false;
        }

        if (!openDialogs.isEmpty() && openDialogs.get(openDialogs.size()-1) == dialog)
        {
            return false;
        }

        dialog.setStateChanged(true);
        if (!openDialogsRemove(dialog))
        {
            dialog.activateDefaultElement();
            dialog.setGuiAutoCloseListener(autoCloseOnDeactivate ? guiAutoCloseListener : null);
        }
        openDialogsAdd(dialog);
        openDialogsChanged = true;
        return true;
    }

    /**
     * Raise an already opened dialog.
     *
     * @param dialog The dialog to show.
     */
    public void raiseDialog(@NotNull final Gui dialog)
    {
        if (dialog == currentGui)
        {
            return;
        }

        if (!openDialogs.isEmpty() && openDialogs.get(openDialogs.size()-1) == dialog)
        {
            return;
        }

        if (!isDialogOpen(dialog))
        {
            return;
        }

        if (!openDialogsRemove(dialog))
        {
            assert false;
        }
        openDialogsAdd(dialog);
        openDialogsChanged = true;
    }

    /**
     * Return whether a given dialog is currently visible.
     *
     * @param dialog The dialog to check.
     *
     * @return Whether the dialog is visible.
     */
    public boolean isDialogOpen(@NotNull final Gui dialog)
    {
        return openDialogs.contains(dialog);
    }

    /**
     * Return all open dialogs in reverse painting order; the first element is
     * the top-most dialog.
     *
     * @return The open dialogs; client code must not modify this list.
     */
    @NotNull
    public Iterable<Gui> getOpenDialogs()
    {
        return new Iterable<Gui>()
        {
            /** {@inheritDoc} */
            @Override
            public Iterator<Gui> iterator()
            {
                return new OpenDialogsIterator();
            }
        };
    }

    public void setCurrentGui(@NotNull final Gui gui)
    {
        currentGui = gui;
        currentGuiChanged = true;
    }

    @NotNull
    public Gui getCurrentGui()
    {
        return currentGui;
    }

    /**
     * Check whether any gui element has changed and needs a redraw.
     *
     * @return whether any gui element has changed
     */
    private boolean needRedraw()
    {
        if (openDialogsChanged)
        {
            return true;
        }

        if (currentGuiChanged)
        {
            return true;
        }

        if (currentGui.needRedraw())
        {
                return true;
        }

        for (final Gui dialog : openDialogs)
        {
            if (!dialog.isHidden(rendererGuiState) && dialog.needRedraw())
            {
                return true;
            }
        }

        return tooltip != null && tooltip.isChanged();
    }

    /**
     * Return the x-offset of of the visible window.
     *
     * @return The x-offset of of the visible window.
     */
    public int getOffsetX()
    {
        return offsetX;
    }

    /**
     * Return the y-offset of of the visible window.
     *
     * @return The y-offset of of the visible window.
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    /**
     * Close a dialog. Does nothing if the given dialog is not open.
     *
     * @param dialog The dialog to close.
     */
    public void closeDialog(@NotNull final Gui dialog)
    {
        if (openDialogsRemove(dialog))
        {
            dialog.setStateChanged(true);
            final ActivatableGUIElement activeElement = dialog.getActiveElement();
            if (activeElement != null)
            {
                activeElement.setActive(false);
            }
            openDialogsChanged = true;
        }
    }

    /**
     * Toggle a dialog: if the dialog is not shown, show it; else hide it.
     *
     * @param dialog The dialog to toggle.
     *
     * @return Whether the dialog is shown.
     */
    public boolean toggleDialog(@NotNull final Gui dialog)
    {
        if (dialog == currentGui)
        {
            return true;
        }

        openDialogsChanged = true;
        dialog.setStateChanged(true);

        if (openDialogsRemove(dialog))
        {
            final ActivatableGUIElement activeElement = dialog.getActiveElement();
            if (activeElement != null)
            {
                activeElement.setActive(false);
            }
            return false;
        }

        dialog.setGuiAutoCloseListener(null);
        openDialogsAdd(dialog);
        dialog.activateDefaultElement();
        return true;
    }

    /**
     * Set the tooltip to use, or <code>null</code> if no tooltips should be
     * shown.
     *
     * @param tooltip The tooltip to use, or <code>null</code>.
     */
    public void setTooltip(@Nullable final GUIElement tooltip)
    {
        this.tooltip = tooltip;
    }

    /**
     * Set the current gui state.
     *
     * @param rendererGuiState The gui state.
     */
    public void setGuiState(@NotNull final RendererGuiState rendererGuiState)
    {
        if (this.rendererGuiState == rendererGuiState)
        {
            return;
        }

        this.rendererGuiState = rendererGuiState;
        forcePaint = true;
        for (final RendererGuiStateListener listener : rendererGuiStateListeners)
        {
            listener.guiStateChanged(rendererGuiState);
        }
    }

    /**
     * Return the current gui state.
     *
     * @return The gui state.
     */
    @NotNull
    public RendererGuiState getGuiState()
    {
        return rendererGuiState;
    }

    /**
     * Add a gui state listener to be notified about {@link #rendererGuiState} changes.
     *
     * @param listener The listener to add.
     */
    public void addGuiStateListener(@NotNull final RendererGuiStateListener listener)
    {
        rendererGuiStateListeners.add(listener);
    }

    /**
     * Adds a dialog to {@link #openDialogs}. Generates mouse events if
     * necessary.
     * @param dialog the dialog
     */
    private void openDialogsAdd(@NotNull final Gui dialog)
    {
        if (openDialogs.contains(dialog))
        {
            return;
        }

        final Point mouse = frame.getMousePosition(true);
        if (mouse == null)
        {
            openDialogs.add(dialog);
        }
        else
        {
            mouse.x -= offsetX;
            mouse.y -= offsetY;
            if (dialog.isWithinDrawingArea(mouse.x, mouse.y))
            {
                final MouseEvent mouseEvent = new MouseEvent(frame, 0, System.currentTimeMillis(), 0, mouse.x, mouse.y, 0, false);
                mouseTracker.mouseExited(mouseEvent);
                openDialogs.add(dialog);
                mouseTracker.mouseEntered(mouseEvent);
            }
            else
            {
                openDialogs.add(dialog);
            }
        }
    }

    /**
     * Removes a dialog to {@link #openDialogs}. Generates mouse events if
     * necessary.
     * @param dialog the dialog
     * @return whether the dialog was opened
     */
    private boolean openDialogsRemove(@NotNull final Gui dialog)
    {
        if (!openDialogs.contains(dialog))
        {
            return false;
        }

        final Point mouse = frame.getMousePosition(true);
        if (mouse == null)
        {
            openDialogs.remove(dialog);
        }
        else
        {
            mouse.x -= offsetX;
            mouse.y -= offsetY;
            if (dialog.isWithinDrawingArea(mouse.x, mouse.y))
            {
                final MouseEvent mouseEvent = new MouseEvent(frame, 0, System.currentTimeMillis(), 0, mouse.x, mouse.y, 0, false);
                mouseTracker.mouseExited(mouseEvent);
                openDialogs.remove(dialog);
                mouseTracker.mouseEntered(mouseEvent);
            }
            else
            {
                openDialogs.remove(dialog);
            }
        }

        return true;
    }

    /**
     * Deactivates the command input text field. Does nothing if the command
     * input text field is not active.
     * @return  whether the command input text field has been deactivated
     */
    public boolean deactivateCommandInput()
    {
        for (final Gui dialog : getOpenDialogs())
        {
            if (!dialog.isHidden(rendererGuiState))
            {
                if (dialog.deactivateCommandInput())
                {
                    return true;
                }
                if (dialog.isModal())
                {
                    return false;
                }
            }
        }

        return currentGui.deactivateCommandInput();
    }

    /**
     * Returns the active message buffer.
     * @return the active message buffer or <code>null</code> if none is active
     */
    @Nullable
    public Buffer getActiveMessageBuffer()
    {
        for (final Gui dialog : getOpenDialogs())
        {
            if (!dialog.isHidden(rendererGuiState))
            {
                final Buffer buffer = getActiveMessageBuffer(dialog);
                if (buffer != null)
                {
                    return buffer;
                }
                if (dialog.isModal())
                {
                    return null;
                }
            }
        }

        return getActiveMessageBuffer(currentGui);
    }

    /**
     * Returns the active message buffer for a {@link Gui} instance.
     * @param gui the gui instance
     * @return the active message buffer or <code>null</code>
     */
    @Nullable
    private Buffer getActiveMessageBuffer(@NotNull final Gui gui)
    {
        final GUILog buffer = gui.getFirstElement(GUIMessageLog.class);
        return buffer == null ? null : buffer.getBuffer();
    }

    /**
     * An {@link Iterator} that returns all open dialogs in painting order.
     */
    private class OpenDialogsIterator implements Iterator<Gui>
    {
        /**
         * The backing list iterator; it returns the elements in
         * reversed order.
         */
        @NotNull
        private final ListIterator<Gui> it = openDialogs.listIterator(openDialogs.size());

        /** {@inheritDoc} */
        @Override
        public boolean hasNext()
        {
            return it.hasPrevious();
        }

        /** {@inheritDoc} */
        @NotNull
        @Override
        public Gui next()
        {
            return it.previous();
        }

        /** {@inheritDoc} */
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Inhibits or allows painting while the main window is iconified.
     * @param inhibitPaintIconified whether the main window is iconified
     */
    public void setInhibitPaintIconified(final boolean inhibitPaintIconified)
    {
        this.inhibitPaintIconified = inhibitPaintIconified;
    }

    /**
     * Returns the width of the client area.
     * @return the width of the client area
     */
    public int getWindowWidth()
    {
        return currentGui.getWidth();
    }

    /**
     * Returns the height of the client area.
     * @return the height of the client area
     */
    public int getWindowHeight()
    {
        return currentGui.getHeight();
    }
}

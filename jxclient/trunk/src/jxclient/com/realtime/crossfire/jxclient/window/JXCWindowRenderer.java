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
package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.gui.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.Gui;
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
import java.awt.image.BufferStrategy;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Andreas Kirschbaum
 */
public class JXCWindowRenderer
{
    private final JXCWindow window;

    private final MouseTracker mouseTracker;

    /**
     * The semaphore used to synchronized map model updates and map view
     * redraws.
     */
    private final Object redrawSemaphore;

    private BufferStrategy bufferStrategy = null;

    private DisplayMode oldDisplayMode = null;

    private DisplayMode displayMode = null;

    /**
     * If set, the content of {@link #openDialogs} has changed.
     */
    private boolean openDialogsChanged = false;

    /**
     * Currently opened dialogs. The ordering is the painting order: the
     * topmost dialog is at the end.
     */
    private final CopyOnWriteArrayList<Gui> openDialogs = new CopyOnWriteArrayList<Gui>();

    /**
     * Listeners to be notified about {@link #guiState} changes.
     */
    private final CopyOnWriteArrayList<GuiStateListener> guiStateListeners = new CopyOnWriteArrayList<GuiStateListener>();

    /**
     * If set, {@link #currentGui} has changed.
     */
    private boolean currentGuiChanged = false;

    private Gui currentGui;

    /**
     * The tooltip to use, or <code>null</code> if no tooltips should be shown.
     */
    private AbstractLabel tooltip = null;

    /**
     * If set, force a full repaint.
     */
    private boolean forcePaint = false;

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
    private GuiState guiState = GuiState.START;

    /**
     * All gui states.
     */
    public enum GuiState
    {
        START,
        META,
        LOGIN,
        NEWCHAR,
        PLAYING,
    }

    /**
     * Creates a new instance.
     * @param window the associated window
     * @param mouseTracker the mouse tracker instance
     * @param redrawSemaphore the semaphore used to synchronized map model
     * updates and map view redraws
     */
    public JXCWindowRenderer(final JXCWindow window, final MouseTracker mouseTracker, final Object redrawSemaphore)
    {
        this.window = window;
        this.mouseTracker = mouseTracker;
        this.redrawSemaphore = redrawSemaphore;
        currentGui = new Gui(window, mouseTracker);
    }

    public void init(final Resolution resolution)
    {
        displayMode = new DisplayMode(resolution.getWidth(), resolution.getHeight(), DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN);
    }

    /**
     * Repaint the window.
     */
    public void repaint()
    {
        forcePaint = true;
    }

    public void initRendering(final boolean fullScreen)
    {
        isFullScreen = false;
        oldDisplayMode = null;

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (fullScreen && gd.isFullScreenSupported())
        {
            window.setUndecorated(true);
            gd.setFullScreenWindow(window);
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
//                    window.setUndecorated(false); // XXX: cannot be called anymore
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
//                        window.setUndecorated(false); // XXX: cannot be called anymore
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
            window.getRootPane().setPreferredSize(size);
            window.pack();
            window.setResizable(false);
            window.setVisible(true);
            window.setLocationRelativeTo(null);
        }
        window.createBufferStrategy(2);
        bufferStrategy = window.getBufferStrategy();

        final Insets insets = window.getInsets();
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
                final Graphics g = bufferStrategy.getDrawGraphics();
                g.translate(offsetX, offsetY);
                if (bufferStrategy.contentsRestored())
                {
                    redrawBlack(g);
                }
                redraw(g);
                g.dispose();
            }
            while (bufferStrategy.contentsLost());
            bufferStrategy.show();
        }
        while (bufferStrategy.contentsLost());
    }

    /**
     * Paints the view into the given graphics instance.
     * @param g the graphics instance to paint to
     */
    public void redraw(final Graphics g)
    {
        synchronized (redrawSemaphore)
        {
            redrawGUIBasic(g);
            redrawGUIDialog(g);
            redrawTooltip(g);
        }
    }

    public void clearGUI()
    {
        currentGui = new Gui(window, mouseTracker);
        currentGuiChanged = true;
        for (int ig = 0; ig < 3; ig++)
        {
            final Graphics g = bufferStrategy.getDrawGraphics();
            g.translate(offsetX, offsetY);
            redrawBlack(g);
            g.dispose();
            bufferStrategy.show();
        }
    }

    private void redrawGUIBasic(final Graphics g)
    {
        currentGuiChanged = false;
        currentGui.redraw(g, window);
    }

    private void redrawGUIDialog(final Graphics g)
    {
        openDialogsChanged = false;
        for (final Gui dialog : openDialogs)
        {
            if (!dialog.isHidden(guiState))
            {
                dialog.redraw(g, window);
            }
        }
    }

    private void redrawTooltip(final Graphics g)
    {
        if (tooltip != null)
        {
            if (tooltip.isVisible())
            {
                tooltip.drawImage(g);
            }
            else
            {
                tooltip.resetChanged();
            }
        }
    }

    private void redrawBlack(final Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, window.getWindowWidth(), window.getWindowHeight());
    }

    /**
     * Open a dialog. Raises an already opened dialog.
     *
     * @param dialog The dialog to show.
     *
     * @return Whether the dialog was opened or raised; <code>false</code> if
     * the dialog already was opened as the topmost dialog.
     */
    public boolean openDialog(final Gui dialog)
    {
        if (dialog == currentGui)
        {
            return false;
        }

        dialog.setAutoCloseOnDeactivate(false);
        if (openDialogs.size() > 0 && openDialogs.get(openDialogs.size()-1) == dialog)
        {
            return false;
        }

        dialog.setStateChanged(true);
        if (!openDialogsRemove(dialog))
        {
            dialog.activateDefaultElement();
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
    public void raiseDialog(final Gui dialog)
    {
        if (dialog == currentGui)
        {
            return;
        }

        if (openDialogs.size() > 0 && openDialogs.get(openDialogs.size()-1) == dialog)
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
    public boolean isDialogOpen(final Gui dialog)
    {
        return openDialogs.contains(dialog);
    }

    /**
     * Return all open dialogs in reverse painting order; the first element is
     * the top-most dialog.
     *
     * @return The open dialogs; client code must not modify this list.
     */
    public Iterable<Gui> getOpenDialogs()
    {
        return new Iterable<Gui>()
        {
            /** {@inheritDoc} */
            public Iterator<Gui> iterator()
            {
                return new Iterator<Gui>()
                {
                    /**
                     * The backing list iterator; it returns the elements in
                     * reversed order.
                     */
                    private final ListIterator<Gui> it = openDialogs.listIterator(openDialogs.size());

                    /** {@inheritDoc} */
                    public boolean hasNext()
                    {
                        return it.hasPrevious();
                    }

                    /** {@inheritDoc} */
                    public Gui next()
                    {
                        return it.previous();
                    }

                    /** {@inheritDoc} */
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public void setCurrentGui(final Gui gui)
    {
        currentGui = gui;
        currentGuiChanged = true;
    }

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
            if (!dialog.isHidden(guiState) && dialog.needRedraw())
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
    public void closeDialog(final Gui dialog)
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
    public boolean toggleDialog(final Gui dialog)
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

        dialog.setAutoCloseOnDeactivate(false);
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
    public void setTooltip(final AbstractLabel tooltip)
    {
        this.tooltip = tooltip;
    }

    /**
     * Set the current gui state.
     *
     * @param guiState The gui state.
     */
    public void setGuiState(final GuiState guiState)
    {
        if (this.guiState == guiState)
        {
            return;
        }

        this.guiState = guiState;
        forcePaint = true;
        for (final GuiStateListener listener : guiStateListeners)
        {
            listener.guiStateChanged(guiState);
        }
    }

    /**
     * Return the current gui state.
     *
     * @return The gui state.
     */
    public GuiState getGuiState()
    {
        return guiState;
    }

    /**
     * Add a gui state listener to be notified about {@link #guiState} changes.
     *
     * @param listener The listener to add.
     */
    public void addGuiStateListener(final GuiStateListener listener)
    {
        guiStateListeners.add(listener);
    }

    /**
     * Adds a dialog to {@link #openDialogs}. Generates mouse events if
     * necessary.
     * @param dialog the dialog
     */
    private void openDialogsAdd(final Gui dialog)
    {
        if (openDialogs.contains(dialog))
        {
            return;
        }

        final Point mouse = window.getMousePosition(true);
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
                final MouseEvent mouseEvent = new MouseEvent(window, 0, System.currentTimeMillis(), 0, mouse.x, mouse.y, 0, false);
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
    private boolean openDialogsRemove(final Gui dialog)
    {
        if (!openDialogs.contains(dialog))
        {
            return false;
        }

        final Point mouse = window.getMousePosition(true);
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
                final MouseEvent mouseEvent = new MouseEvent(window, 0, System.currentTimeMillis(), 0, mouse.x, mouse.y, 0, false);
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
}

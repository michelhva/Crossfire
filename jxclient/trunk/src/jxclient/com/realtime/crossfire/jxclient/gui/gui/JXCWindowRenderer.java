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

import com.realtime.crossfire.jxclient.gui.log.Buffer;
import com.realtime.crossfire.jxclient.gui.log.GUILog;
import com.realtime.crossfire.jxclient.gui.log.GUIMessageLog;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.util.Resolution;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renders a {@link Gui} instance into a {@link JFrame}.
 * @author Andreas Kirschbaum
 */
public class JXCWindowRenderer {

    /**
     * The associated window.
     */
    @NotNull
    private final JFrame frame;

    /**
     * The {@link MouseTracker} instance.
     */
    @NotNull
    private final MouseListener mouseTracker;

    /**
     * The semaphore used to synchronize map model updates and map view
     * redraws.
     */
    @NotNull
    private final Object redrawSemaphore;

    /**
     * The {@link Writer} to write screen debug to or <code>null</code>.
     */
    @Nullable
    private final Writer debugScreen;

    /**
     * The used {@link GraphicsEnvironment}.
     */
    @NotNull
    private final GraphicsEnvironment graphicsEnvironment;

    /**
     * The used {@link GraphicsDevice}.
     */
    @NotNull
    private final GraphicsDevice graphicsDevice;

    /**
     * The default screen mode that was active when the client did start.
     */
    @NotNull
    private final DisplayMode defaultDisplayMode;

    /**
     * The current {@link BufferStrategy}. Set to <code>null</code> until
     * {@link #setFullScreenMode(Resolution)} or {@link
     * #setWindowMode(Resolution, boolean)} has been called.
     */
    @Nullable
    private BufferStrategy bufferStrategy = null;

    /**
     * The width of the client area in pixels.
     */
    private int windowWidth = 0;

    /**
     * The height of the client area in pixels.
     */
    private int windowHeight = 0;

    /**
     * If set, the content of {@link #openDialogs} has changed.
     */
    private boolean openDialogsChanged = false;

    /**
     * Currently opened dialogs. The ordering is the painting order: the topmost
     * dialog is at the end.
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

    /**
     * The currently displayed {@link Gui}.
     */
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
     * The x-offset of the visible window.
     */
    private int offsetX = 0;

    /**
     * The y-offset of the visible window.
     */
    private int offsetY = 0;

    /**
     * The x-difference between the visible window to the real window.
     */
    private int offsetW = 0;

    /**
     * The y-difference between the visible window and the real window.
     */
    private int offsetH = 0;

    /**
     * Records whether full-screen mode is active.
     */
    private boolean isFullScreen = false;

    /**
     * Records whether the {@link #frame} has been displayed before.
     */
    private boolean wasDisplayed = false;

    /**
     * The current gui state.
     */
    @NotNull
    private RendererGuiState rendererGuiState = RendererGuiState.START;

    /**
     * A formatter for timestamps.
     */
    @NotNull
    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");

    /**
     * The {@link GuiAutoCloseListener} used to track auto-closing dialogs.
     */
    @NotNull
    private final GuiAutoCloseListener guiAutoCloseListener = new GuiAutoCloseListener() {
        /** {@inheritDoc} */
        @Override
        public void autoClosed(@NotNull final Gui gui) {
            closeDialog(gui);
        }
    };

    /**
     * The listener to detect map model changes.
     */
    @NotNull
    private final CrossfireUpdateMapListener crossfireUpdateMapListener = new CrossfireUpdateMapListener() {
        /** {@inheritDoc} */
        @Override
        public void newMap(final int mapWidth, final int mapHeight) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapBegin() {
            inhibitPaintMapUpdate = true;
            skippedPaint = false;
        }

        /** {@inheritDoc} */
        @Override
        public void mapClear(final int x, final int y) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapDarkness(final int x, final int y, final int darkness) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapFace(final int x, final int y, final int layer, final int faceNum) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapAnimation(final int x, final int y, final int layer, final int animationNum, final int animationType) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapAnimationSpeed(final int x, final int y, final int layer, final int animSpeed) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void scroll(final int dx, final int dy) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void mapEnd() {
            if (skippedPaint) {
                forcePaint = true;
            }
            inhibitPaintMapUpdate = false;
        }

        /** {@inheritDoc} */
        @Override
        public void addAnimation(final int animation, final int flags, @NotNull final int[] faces) {
            // ignore
        }
    };

    /**
     * The {@link GUIElementListener} attached to all {@link GUIElement}s.
     */
    @NotNull
    private final GUIElementListener elementListener = new GUIElementListener() {
        /** {@inheritDoc} */
        @Override
        public void mouseClicked(@NotNull final Gui gui) {
            raiseDialog(gui);
        }
    };

    /**
     * Creates a new instance.
     * @param frame the associated window
     * @param mouseTracker the mouse tracker instance
     * @param redrawSemaphore the semaphore used to synchronized map model
     * updates and map view redraws
     * @param crossfireServerConnection the server connection to monitor
     * @param debugScreen the writer to write screen debug to or
     * <code>null</code>
     */
    public JXCWindowRenderer(@NotNull final JFrame frame, @NotNull final MouseListener mouseTracker, @NotNull final Object redrawSemaphore, @NotNull final CrossfireServerConnection crossfireServerConnection, @Nullable final Writer debugScreen) {
        this.frame = frame;
        this.mouseTracker = mouseTracker;
        this.redrawSemaphore = redrawSemaphore;
        this.debugScreen = debugScreen;
        crossfireServerConnection.addCrossfireUpdateMapListener(crossfireUpdateMapListener);
        graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        defaultDisplayMode = graphicsDevice.getDisplayMode();
    }

    /**
     * Repaint the window.
     */
    public void repaint() {
        forcePaint = true;
    }

    /**
     * Tries to switch to the given resolution. If resolution switching fails,
     * the window might be invisible.
     * @param resolution the resolution to switch to
     * @return whether the resolution has been changed
     */
    public boolean setFullScreenMode(@NotNull final Resolution resolution) {
        debugScreenWrite("setFullScreenMode: resolution="+resolution);

        final DisplayMode currentDisplayMode = graphicsDevice.getDisplayMode();
        debugScreenWrite("setResolutionPre: current display mode="+currentDisplayMode.getWidth()+"x"+currentDisplayMode.getHeight());
        if (isFullScreen && resolution.getWidth() == windowWidth && resolution.getHeight() == windowHeight) {
            debugScreenWrite("setResolutionPre: no change needed");
            debugScreenWrite("setResolutionPre: success");
            return true;
        }

        if (!setResolutionPre()) {
            return false;
        }

        final Dimension dimension = new Dimension(resolution.getWidth(), resolution.getHeight());
        debugScreenWrite("setFullScreenMode: full-screen requested, dimension="+dimension);
        frame.setPreferredSize(dimension);
        frame.setResizable(false);
        frame.setUndecorated(true);

        // full-screen switch must happen before display mode change
        if (!graphicsDevice.isFullScreenSupported()) {
            debugScreenWrite("setFullScreenMode: full-screen mode is not supported");
            graphicsDevice.setFullScreenWindow(null);
            isFullScreen = false;
            debugScreenWrite("setFullScreenMode: failure");
            return false;
        }

        debugScreenWrite("setFullScreenMode: entering full-screen mode");
        graphicsDevice.setFullScreenWindow(frame);
        isFullScreen = true;

        if (resolution.equalsDisplayMode(currentDisplayMode)) {
            debugScreenWrite("setFullScreenMode: requested resolution matches screen resolution");
        } else {
            if (!graphicsDevice.isDisplayChangeSupported()) {
                debugScreenWrite("setFullScreenMode: screen resolution change is not supported");
                graphicsDevice.setFullScreenWindow(null);
                isFullScreen = false;
                debugScreenWrite("setFullScreenMode: failure");
                return false;
            }

            final DisplayMode newDisplayMode = new DisplayMode(resolution.getWidth(), resolution.getHeight(), DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN);
            try {
                debugScreenWrite("setFullScreenMode: setting screen resolution to "+newDisplayMode.getWidth()+"x"+newDisplayMode.getHeight());
                graphicsDevice.setDisplayMode(newDisplayMode);
            } catch (final IllegalArgumentException ex) {
                debugScreenWrite("setFullScreenMode: setting screen resolution failed: "+ex.getMessage());
                isFullScreen = false;
                debugScreenWrite("setFullScreenMode: resetting screen resolution to "+defaultDisplayMode.getWidth()+"x"+defaultDisplayMode.getHeight());
                graphicsDevice.setDisplayMode(defaultDisplayMode);
                debugScreenWrite("setFullScreenMode: leaving full-screen mode");
                graphicsDevice.setFullScreenWindow(null);
                debugScreenWrite("setFullScreenMode: failure");
                return false;
            }
        }

        setResolutionPost(resolution);
        return true;
    }

    /**
     * Tries to switch to the given resolution. If resolution switching fails,
     * the window might be invisible.
     * @param resolution the resolution to switch to
     * @param fixedSize whether the window should have fixed size
     * @return whether the resolution has been changed
     */
    public boolean setWindowMode(@NotNull final Resolution resolution, final boolean fixedSize) {
        debugScreenWrite("setWindowMode: resolution="+resolution+", fixedSize="+fixedSize);

        final DisplayMode currentDisplayMode = graphicsDevice.getDisplayMode();
        debugScreenWrite("setResolutionPre: current display mode="+currentDisplayMode.getWidth()+"x"+currentDisplayMode.getHeight());
        if (!isFullScreen && resolution.getWidth() == windowWidth && resolution.getHeight() == windowHeight) {
            debugScreenWrite("setResolutionPre: no change needed");
            debugScreenWrite("setResolutionPre: success");
            return true;
        }

        if (!setResolutionPre()) {
            return false;
        }

        debugScreenWrite("setResolutionPre: windowed mode requested");
        frame.setUndecorated(false);
        frame.setResizable(!fixedSize);
        final Point centerPoint = graphicsEnvironment.getCenterPoint();
        debugScreenWrite("setResolutionPre: screen center point is "+centerPoint);
        final Dimension dimension = resolution.asDimension();
        final int x = centerPoint.x-dimension.width/2;
        final int y = centerPoint.y-dimension.height/2;
        if (!wasDisplayed) {
            frame.setLocation(x, y); // try to minimize display movements
        }
        frame.setVisible(true);
        final Insets frameInsets = frame.getInsets();
        debugScreenWrite("setResolutionPre: frame insets="+frameInsets);

        final Dimension maxDimension = getMaxWindowDimension(frameInsets);
        debugScreenWrite("setResolutionPre: maximal window dimension="+maxDimension);
        if (resolution.getWidth() > maxDimension.width || resolution.getHeight() > maxDimension.height) {
            /*
            frame.dispose();
            debugScreenWrite("setResolutionPre: failure");
            return false;
            */
            debugScreenWrite("setResolutionPre: window size exceeds maximum allowed size, ignoring");
        }

        if (wasDisplayed) {
            debugScreenWrite("setResolutionPre: resizing window to "+dimension);
            frame.setPreferredSize(dimension);
            frame.setSize(dimension);
        } else {
            wasDisplayed = true;
            final int x2 = x-frameInsets.left;
            final int y2 = y-frameInsets.top;
            debugScreenWrite("setResolutionPre: moving window to "+x2+"/"+y2+" "+dimension.width+"x"+dimension.height);
            frame.setBounds(x2, y2, dimension.width, dimension.height);
        }

        setResolutionPost(resolution);
        return true;
    }

    /**
     * Tries to switch to the given resolution. If resolution switching fails,
     * the window might be invisible.
     * @return whether the resolution has been changed
     */
    private boolean setResolutionPre() {
        // disable full-screen since switching from full-screen to full-screen
        // does not work reliably
        if (isFullScreen) {
            debugScreenWrite("setResolutionPre: resetting screen resolution to "+defaultDisplayMode.getWidth()+"x"+defaultDisplayMode.getHeight());
            graphicsDevice.setDisplayMode(defaultDisplayMode);
        }
        debugScreenWrite("setResolutionPre: leaving full-screen mode");
        graphicsDevice.setFullScreenWindow(null);
        isFullScreen = false;

        debugScreenWrite("setResolutionPre: disposing frame");
        frame.dispose();
        return true;
    }

    /**
     * Tries to switch to the given resolution. If resolution switching fails,
     * the window might be invisible.
     * @param resolution the resolution to switch to
     */
    private void setResolutionPost(@NotNull final Resolution resolution) {
        debugScreenWrite("setResolutionPost: creating buffer strategy");
        frame.createBufferStrategy(2);
        bufferStrategy = frame.getBufferStrategy();

        final Insets insets = frame.getInsets();
        debugScreenWrite("setResolutionPost: offset="+offsetX+"x"+offsetY);
        offsetX = insets.left;
        offsetY = insets.top;
        offsetW = insets.left+insets.right;
        offsetH = insets.top+insets.bottom;
        debugScreenWrite("setResolutionPost: offset="+offsetX+"x"+offsetY+" "+offsetW+"x"+offsetH+" "+insets);

        debugScreenWrite("setResolutionPost: requesting focus");
        frame.requestFocusInWindow();

        updateWindowSize(resolution.getWidth()+offsetW, resolution.getHeight()+offsetH);

        debugScreenWrite("setResolutionPost: success");
    }

    /**
     * Updates the window size for rendering from the main window size.
     * @param windowWidth the window width including insets
     * @param windowHeight the window height including insets
     */
    public void updateWindowSize(final int windowWidth, final int windowHeight) {
        this.windowWidth = windowWidth-offsetW;
        this.windowHeight = windowHeight-offsetH;
        debugScreenWrite("updateWindowSize: gui size="+this.windowWidth+"x"+this.windowHeight);
    }

    /**
     * Auto-resizes a dialog (or does nothing for non-auto-size dialogs). Then
     * makes sure the dialog is fully visible.
     * @param dialog the dialog to show
     */
    public void showDialogAuto(@NotNull final Gui dialog) {
        dialog.autoSize(windowWidth, windowHeight);
        showDialog(dialog);
    }

    /**
     * Makes sure the dialog is fully visible.
     * @param dialog the dialog to show
     */
    private void showDialog(@NotNull final Gui dialog) {
        showDialog(dialog, dialog.getX(), dialog.getY());
    }

    /**
     * Sets the position of a dialog but makes sure the dialog is fully
     * visible.
     * @param dialog the dialog to show
     * @param x the dialog's x coordinate
     * @param y the dialog's y coordinate
     */
    public void showDialog(@NotNull final Gui dialog, final int x, final int y) {
        final int newX;
        final int newY;
        if (dialog.isAutoSize()) {
            newX = x;
            newY = y;
        } else {
            newX = Math.max(Math.min(x, windowWidth-dialog.getWidth()), 0);
            newY = Math.max(Math.min(y, windowHeight-dialog.getHeight()), 0);
        }
        dialog.setPosition(newX, newY);
    }

    /**
     * Returns the maximum dimension of a frame to fit on the screen.
     * @param frameInsets the frame's insets
     * @return the maximum dimension
     */
    @NotNull
    private Dimension getMaxWindowDimension(@NotNull final Insets frameInsets) {
        final Rectangle maximumWindowBounds = graphicsEnvironment.getMaximumWindowBounds();
        debugScreenWrite("getMaxWindowDimension: maximum window bounds="+maximumWindowBounds);

        final GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
        final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
        debugScreenWrite("getMaxWindowDimension: screen insets="+screenInsets);

        final int maxWidth = maximumWindowBounds.width-screenInsets.left-screenInsets.right-frameInsets.left-frameInsets.right;
        final int maxHeight = maximumWindowBounds.height-screenInsets.top-screenInsets.bottom-frameInsets.top-frameInsets.bottom;
        debugScreenWrite("getMaxWindowDimension: maximum window dimension="+maxWidth+"x"+maxHeight);
        return new Dimension(maxWidth, maxHeight);
    }

    /**
     * Ends rendering and reverts the display settings.
     */
    public void endRendering() {
        if (isFullScreen) {
            setWindowMode(new Resolution(true, defaultDisplayMode.getWidth(), defaultDisplayMode.getHeight()), false);
        }
    }

    /**
     * Redraws the current gui.
     */
    public void redrawGUI() {
        if (inhibitPaintMapUpdate || inhibitPaintIconified) {
            skippedPaint = true;
            return;
        }

        if (forcePaint) {
            forcePaint = false;
        } else if (!needRedraw()) {
            return;
        }

        do {
            do {
                assert bufferStrategy != null;
                final Graphics g = bufferStrategy.getDrawGraphics();
                try {
                    g.translate(offsetX, offsetY);
                    assert bufferStrategy != null;
                    if (bufferStrategy.contentsRestored()) {
                        redrawBlack(g);
                    }
                    redraw(g);
                } finally {
                    g.dispose();
                }
                assert bufferStrategy != null;
            } while (bufferStrategy.contentsLost());
            assert bufferStrategy != null;
            bufferStrategy.show();
            assert bufferStrategy != null;
        } while (bufferStrategy.contentsLost());
    }

    /**
     * Paints the view into the given graphics instance.
     * @param g the graphics instance to paint to
     */
    public void redraw(@NotNull final Graphics g) {
        synchronized (redrawSemaphore) {
            redrawGUIBasic(g);
            redrawGUIDialog(g);
            redrawTooltip(g);
        }
    }

    /**
     * Sets a gui to display and clears the display.
     * @param gui the gui to set
     */
    public void clearGUI(@NotNull final Gui gui) {
        setCurrentGui(gui);
        for (int ig = 0; ig < 3; ig++) {
            assert bufferStrategy != null;
            final Graphics g = bufferStrategy.getDrawGraphics();
            g.translate(offsetX, offsetY);
            redrawBlack(g);
            g.dispose();
            assert bufferStrategy != null;
            bufferStrategy.show();
        }
    }

    private void redrawGUIBasic(@NotNull final Graphics g) {
        currentGuiChanged = false;
        currentGui.redraw(g);
    }

    private void redrawGUIDialog(@NotNull final Graphics g) {
        openDialogsChanged = false;
        for (final Gui dialog : openDialogs) {
            if (!dialog.isHidden(rendererGuiState)) {
                dialog.redraw(g);
            }
        }
    }

    private void redrawTooltip(@NotNull final Graphics g) {
        final GUIElement tmpTooltip = tooltip;
        if (tmpTooltip != null) {
            if (tmpTooltip.isElementVisible()) {
                tmpTooltip.paintComponent(g);
            } else {
                tmpTooltip.resetChanged();
            }
        }
    }

    private void redrawBlack(@NotNull final Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, currentGui.getWidth(), currentGui.getHeight());
    }

    /**
     * Open a dialog. Raises an already opened dialog.
     * @param dialog The dialog to show.
     * @param autoCloseOnDeactivate whether the dialog should auto-close when it
     * becomes inactive; ignored if the dialog is already open
     * @return Whether the dialog was opened or raised; <code>false</code> if
     *         the dialog already was opened as the topmost dialog.
     */
    public boolean openDialog(@NotNull final Gui dialog, final boolean autoCloseOnDeactivate) {
        if (dialog == currentGui) {
            return false;
        }

        if (!openDialogs.isEmpty() && openDialogs.get(openDialogs.size()-1) == dialog) {
            return false;
        }

        dialog.setStateChanged(true);
        if (!openDialogsRemove(dialog)) {
            dialog.activateDefaultElement();
            dialog.setGuiAutoCloseListener(autoCloseOnDeactivate ? guiAutoCloseListener : null);
        }
        openDialogsAdd(dialog);
        openDialogsChanged = true;
        return true;
    }

    /**
     * Raise an already opened dialog.
     * @param dialog The dialog to show.
     */
    public void raiseDialog(@NotNull final Gui dialog) {
        if (dialog == currentGui) {
            return;
        }

        if (!openDialogs.isEmpty() && openDialogs.get(openDialogs.size()-1) == dialog) {
            return;
        }

        if (!isDialogOpen(dialog)) {
            return;
        }

        if (!openDialogsRemove(dialog)) {
            assert false;
        }
        openDialogsAdd(dialog);
        openDialogsChanged = true;
    }

    /**
     * Return whether a given dialog is currently visible.
     * @param dialog The dialog to check.
     * @return Whether the dialog is visible.
     */
    public boolean isDialogOpen(@NotNull final Gui dialog) {
        return openDialogs.contains(dialog);
    }

    /**
     * Return all open dialogs in reverse painting order; the first element is
     * the top-most dialog.
     * @return The open dialogs; client code must not modify this list.
     */
    @NotNull
    public Iterable<Gui> getOpenDialogs() {
        return new Iterable<Gui>() {
            /** {@inheritDoc} */
            @Override
            public Iterator<Gui> iterator() {
                return new OpenDialogsIterator();
            }
        };
    }

    /**
     * Sets the {@link Gui} to display.
     * @param gui the gui to display
     */
    public void setCurrentGui(@NotNull final Gui gui) {
        currentGui = gui;
        currentGuiChanged = true;
    }

    @NotNull
    public Gui getCurrentGui() {
        return currentGui;
    }

    /**
     * Check whether any gui element has changed and needs a redraw.
     * @return whether any gui element has changed
     */
    private boolean needRedraw() {
        if (openDialogsChanged) {
            return true;
        }

        if (currentGuiChanged) {
            return true;
        }

        if (currentGui.needRedraw()) {
            return true;
        }

        for (final Gui dialog : openDialogs) {
            if (!dialog.isHidden(rendererGuiState) && dialog.needRedraw()) {
                return true;
            }
        }

        return tooltip != null && tooltip.isChanged();
    }

    /**
     * Return the x-offset of the visible window.
     * @return The x-offset of the visible window.
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Return the y-offset of the visible window.
     * @return The y-offset of the visible window.
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Close a dialog. Does nothing if the given dialog is not open.
     * @param dialog The dialog to close.
     */
    public void closeDialog(@NotNull final Gui dialog) {
        if (openDialogsRemove(dialog)) {
            dialog.setStateChanged(true);
            final ActivatableGUIElement activeElement = dialog.getActiveElement();
            if (activeElement != null) {
                activeElement.setActive(false);
            }
            openDialogsChanged = true;
        }
    }

    /**
     * Toggle a dialog: if the dialog is not shown, show it; else hide it.
     * @param dialog The dialog to toggle.
     * @return Whether the dialog is shown.
     */
    public boolean toggleDialog(@NotNull final Gui dialog) {
        if (dialog == currentGui) {
            return true;
        }

        openDialogsChanged = true;
        dialog.setStateChanged(true);

        if (openDialogsRemove(dialog)) {
            final ActivatableGUIElement activeElement = dialog.getActiveElement();
            if (activeElement != null) {
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
     * @param tooltip The tooltip to use, or <code>null</code>.
     */
    public void setTooltip(@Nullable final GUIElement tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Set the current gui state.
     * @param rendererGuiState The gui state.
     */
    public void setGuiState(@NotNull final RendererGuiState rendererGuiState) {
        if (this.rendererGuiState == rendererGuiState) {
            return;
        }

        this.rendererGuiState = rendererGuiState;
        forcePaint = true;
        for (final RendererGuiStateListener listener : rendererGuiStateListeners) {
            listener.guiStateChanged(rendererGuiState);
        }
    }

    /**
     * Return the current gui state.
     * @return The gui state.
     */
    @NotNull
    public RendererGuiState getGuiState() {
        return rendererGuiState;
    }

    /**
     * Add a gui state listener to be notified about {@link #rendererGuiState}
     * changes.
     * @param listener The listener to add.
     */
    public void addGuiStateListener(@NotNull final RendererGuiStateListener listener) {
        rendererGuiStateListeners.add(listener);
    }

    /**
     * Adds a dialog to {@link #openDialogs}. Generates mouse events if
     * necessary.
     * @param dialog the dialog
     */
    private void openDialogsAdd(@NotNull final Gui dialog) {
        if (openDialogs.contains(dialog)) {
            return;
        }

        showDialogAuto(dialog);

        final Point mouse = frame.getMousePosition(true);
        if (mouse == null) {
            openDialogs.add(dialog);
        } else {
            mouse.x -= offsetX;
            mouse.y -= offsetY;
            if (dialog.isWithinDrawingArea(mouse.x, mouse.y)) {
                final MouseEvent mouseEvent = new MouseEvent(frame, 0, System.currentTimeMillis(), 0, mouse.x+offsetX, mouse.y+offsetY, 0, false);
                mouseTracker.mouseExited(mouseEvent);
                openDialogs.add(dialog);
                mouseTracker.mouseEntered(mouseEvent);
            } else {
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
    private boolean openDialogsRemove(@NotNull final Gui dialog) {
        if (!openDialogs.contains(dialog)) {
            return false;
        }

        final Point mouse = frame.getMousePosition(true);
        if (mouse == null) {
            openDialogs.remove(dialog);
        } else {
            mouse.x -= offsetX;
            mouse.y -= offsetY;
            if (dialog.isWithinDrawingArea(mouse.x, mouse.y)) {
                final MouseEvent mouseEvent = new MouseEvent(frame, 0, System.currentTimeMillis(), 0, mouse.x, mouse.y, 0, false);
                mouseTracker.mouseExited(mouseEvent);
                openDialogs.remove(dialog);
                mouseTracker.mouseEntered(mouseEvent);
            } else {
                openDialogs.remove(dialog);
            }
        }

        return true;
    }

    /**
     * Deactivates the command input text field. Does nothing if the command
     * input text field is not active.
     * @return whether the command input text field has been deactivated
     */
    public boolean deactivateCommandInput() {
        for (final Gui dialog : getOpenDialogs()) {
            if (!dialog.isHidden(rendererGuiState)) {
                if (dialog.deactivateCommandInput()) {
                    return true;
                }
                if (dialog.isModal()) {
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
    public Buffer getActiveMessageBuffer() {
        for (final Gui dialog : getOpenDialogs()) {
            if (!dialog.isHidden(rendererGuiState)) {
                final Buffer buffer = getActiveMessageBuffer(dialog);
                if (buffer != null) {
                    return buffer;
                }
                if (dialog.isModal()) {
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
    private static Buffer getActiveMessageBuffer(@NotNull final Gui gui) {
        final GUILog buffer = gui.getFirstElement(GUIMessageLog.class);
        return buffer == null ? null : buffer.getBuffer();
    }

    @Deprecated
    @NotNull
    public GUIElementListener getElementListener() {
        return elementListener;
    }

    /**
     * An {@link Iterator} that returns all open dialogs in painting order.
     */
    private class OpenDialogsIterator implements Iterator<Gui> {

        /**
         * The backing list iterator; it returns the elements in reversed
         * order.
         */
        @NotNull
        private final ListIterator<Gui> it = openDialogs.listIterator(openDialogs.size());

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return it.hasPrevious();
        }

        /**
         * {@inheritDoc}
         */
        @NotNull
        @Override
        public Gui next() {
            return it.previous();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Inhibits or allows painting while the main window is iconified.
     * @param inhibitPaintIconified whether the main window is iconified
     */
    public void setInhibitPaintIconified(final boolean inhibitPaintIconified) {
        this.inhibitPaintIconified = inhibitPaintIconified;
    }

    /**
     * Returns the width of the client area.
     * @return the width in pixels
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * Returns the height of the client area.
     * @return the height in pixels
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /**
     * Find the gui element for a given {@link MouseEvent}. If a gui element was
     * found, update the event mouse coordinates to be relative to the gui
     * element.
     * @param e The mouse event to process.
     * @return The gui element found, or <code>null</code> if none was found.
     */
    @Nullable
    public GUIElement findElement(@NotNull final MouseEvent e) {
        GUIElement elected = null;

        for (final Gui dialog : getOpenDialogs()) {
            if (!dialog.isHidden(rendererGuiState)) {
                elected = manageMouseEvents(dialog, e);
                if (elected != null) {
                    break;
                }
            }
            if (dialog.isModal()) {
                return null;
            }
        }

        if (elected == null) {
            elected = manageMouseEvents(currentGui, e);
        }

        if (elected != null) {
            e.translatePoint(-elected.getElementX()-offsetX, -elected.getElementY()-offsetY);
        }

        return elected;
    }

    @Nullable
    private GUIElement manageMouseEvents(@NotNull final Gui gui, @NotNull final MouseEvent e) {
        final int x = e.getX()-offsetX;
        final int y = e.getY()-offsetY;
        return gui.getElementFromPoint(x, y);
    }

    /**
     * Writes a message to the screen debug.
     * @param message the message to write
     */
    private void debugScreenWrite(@NotNull final CharSequence message) {
        if (debugScreen == null) {
            return;
        }

        try {
            debugScreen.append(simpleDateFormat.format(new Date()));
            debugScreen.append(message);
            debugScreen.append("\n");
            debugScreen.flush();
        } catch (final IOException ex) {
            System.err.println("Cannot write screen debug: "+ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

}

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

package com.realtime.crossfire.jxclient.gui.misc;

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiAutoCloseListener;
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiState;
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiStateListener;
import com.realtime.crossfire.jxclient.gui.list.GUIFloorList;
import com.realtime.crossfire.jxclient.gui.list.GUIItemList;
import com.realtime.crossfire.jxclient.gui.list.GUIMetaElementList;
import com.realtime.crossfire.jxclient.gui.log.Buffer;
import com.realtime.crossfire.jxclient.gui.log.GUILog;
import com.realtime.crossfire.jxclient.gui.log.GUIMessageLog;
import com.realtime.crossfire.jxclient.gui.map.GUIMap;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import com.realtime.crossfire.jxclient.util.Resolution;
import com.realtime.crossfire.jxclient.util.SwingUtilities2;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
import javax.swing.JLayeredPane;
import javax.swing.JViewport;
import javax.swing.RootPaneContainer;
import javax.swing.event.MouseInputListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renders a {@link Gui} instance into a {@link Frame}.
 * @author Andreas Kirschbaum
 */
public class JXCWindowRenderer {

    /**
     * The default number of ground view objects.
     */
    private static final int DEFAULT_NUM_LOOK_OBJECTS = 50;

    /**
     * The default map width to request from the server.
     */
    private static final int DEFAULT_MAP_WIDTH = 9;

    /**
     * The default map height to request from the server.
     */
    private static final int DEFAULT_MAP_HEIGHT = 9;

    /**
     * The associated {@link JFrame}. Set to <code>null</code> while not
     * visible.
     */
    @Nullable
    private JFrame frame = null;

    /**
     * The {@link JLayeredPane} added as the top-level component to {@link
     * #frame}.
     */
    @NotNull
    private final Container layeredPane = new JLayeredPane() {

        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            mouseTracker.paintActiveComponent(g);
        }

    };

    /**
     * The {@link MouseTracker} instance.
     */
    @NotNull
    private final MouseTracker mouseTracker;

    /**
     * The {@link CrossfireServerConnection} to monitor.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

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
     * A {@link MouseInputListener} that forwards to {@link #mouseTracker}.
     */
    @NotNull
    private final MouseInputListener mouseInputListener = new MouseInputListener() {

        @Override
        public void mouseClicked(final MouseEvent e) {
            // ignore
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            mouseTracker.mousePressed(findElement(e.getComponent(), e), e);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            mouseTracker.mouseReleased(findElement(e.getComponent(), e), e);
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            mouseTracker.mouseEntered(findElement(e.getComponent(), e), e);
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            mouseTracker.mouseExited(e);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            mouseTracker.mouseDragged(findElement(e.getComponent(), e), e);
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            mouseTracker.mouseMoved(findElement(e.getComponent(), e), e);
        }

    };

    /**
     * The current {@link BufferStrategy}. Set to <code>null</code> until {@link
     * #setFullScreenMode(JFrame, Resolution)} or {@link #setWindowMode(JFrame,
     * Resolution, Resolution, boolean)} has been called.
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
     * Currently opened dialogs. The ordering is the painting order: the topmost
     * dialog is at the end.
     */
    @NotNull
    private final CopyOnWriteArrayList<Gui> openDialogs = new CopyOnWriteArrayList<Gui>();

    /**
     * Listeners to be notified about {@link #rendererGuiState} changes.
     */
    @NotNull
    private final EventListenerList2<RendererGuiStateListener> rendererGuiStateListeners = new EventListenerList2<RendererGuiStateListener>(RendererGuiStateListener.class);

    /**
     * The currently displayed {@link Gui}.
     */
    @Nullable
    private Gui currentGui = null;

    /**
     * All {@link GUIMap} instances that {@link #currentGui} and {@link
     * #openDialogs} contain.
     */
    @NotNull
    private final Collection<GUIMap> maps = new CopyOnWriteArrayList<GUIMap>();

    /**
     * The {@link GUIItemList} instances that {@link #currentGui} and {@link
     * #openDialogs} contain and that display floor items.
     */
    @NotNull
    private final Collection<GUIFloorList> floorLists = new CopyOnWriteArrayList<GUIFloorList>();

    /**
     * The tooltip to use, or <code>null</code> if no tooltips should be shown.
     */
    @Nullable
    private Component tooltip = null;

    /**
     * The x-offset of the visible window.
     */
    private int offsetX = 0;

    /**
     * The y-offset of the visible window.
     */
    private int offsetY = 0;

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
     * The {@link ComponentListener} attached to {@link #frame}.
     */
    @NotNull
    private final ComponentListener componentListener = new ComponentListener() {

        @Override
        public void componentResized(final ComponentEvent e) {
            final RootPaneContainer tmpFrame = frame;
            assert tmpFrame != null;
            final int width = tmpFrame.getContentPane().getWidth();
            final int height = tmpFrame.getContentPane().getHeight();
            updateWindowSize(width, height);
            updateServerSettings();
        }

        @Override
        public void componentMoved(final ComponentEvent e) {
            // ignore
        }

        @Override
        public void componentShown(final ComponentEvent e) {
            updateServerSettings();
        }

        @Override
        public void componentHidden(final ComponentEvent e) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param mouseTracker the mouse tracker instance
     * @param crossfireServerConnection the server connection to monitor
     * @param debugScreen the writer to write screen debug to or
     * <code>null</code>
     */
    public JXCWindowRenderer(@NotNull final MouseTracker mouseTracker, @NotNull final CrossfireServerConnection crossfireServerConnection, @Nullable final Writer debugScreen) {
        this.mouseTracker = mouseTracker;
        this.crossfireServerConnection = crossfireServerConnection;
        this.debugScreen = debugScreen;
        graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        defaultDisplayMode = graphicsDevice.getDisplayMode();
    }

    /**
     * Tries to switch to the given resolution. If resolution switching fails,
     * the window might be invisible.
     * @param frame the associated frame
     * @param resolution the resolution to switch to; <code>null</code> to keep
     * current resolution
     * @return whether the resolution has been changed
     */
    public boolean setFullScreenMode(@NotNull final JFrame frame, @Nullable final Resolution resolution) {
        debugScreenWrite("setFullScreenMode: resolution="+(resolution == null ? "default" : resolution));

        final DisplayMode currentDisplayMode = graphicsDevice.getDisplayMode();
        debugScreenWrite("setResolutionPre: current display mode="+currentDisplayMode.getWidth()+"x"+currentDisplayMode.getHeight());
        if (frame == this.frame && isFullScreen && bufferStrategy != null && (resolution == null || resolution.getWidth() == windowWidth && resolution.getHeight() == windowHeight)) {
            debugScreenWrite("setResolutionPre: no change needed");
            debugScreenWrite("setResolutionPre: success");
            return true;
        }

        setResolutionPre(frame);

        final Dimension dimension;
        if (resolution == null) {
            dimension = new Dimension(currentDisplayMode.getWidth(), currentDisplayMode.getHeight());
        } else {
            dimension = new Dimension(resolution.getWidth(), resolution.getHeight());
        }
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

        if (resolution == null || resolution.equalsDisplayMode(currentDisplayMode)) {
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

        setResolutionPost(frame, dimension);
        if (this.frame != null) {
            this.frame.removeComponentListener(componentListener);
        }
        this.frame = frame;
        this.frame.addComponentListener(componentListener);
        addMouseTracker(frame);
        addMouseTrackerRecursively(frame);
        return true;
    }

    /**
     * Tries to switch to the given resolution. If resolution switching fails,
     * the window might be invisible.
     * @param frame the associated frame
     * @param resolution the resolution to switch to, <code>null</code> for
     * default
     * @param minResolution the minimal supported resolution
     * @param fixedSize whether the window should have fixed size
     */
    public void setWindowMode(@NotNull final JFrame frame, @Nullable final Resolution resolution, @NotNull final Resolution minResolution, final boolean fixedSize) {
        debugScreenWrite("setWindowMode: resolution="+(resolution == null ? "default" : resolution)+", fixedSize="+fixedSize);

        final DisplayMode currentDisplayMode = graphicsDevice.getDisplayMode();
        debugScreenWrite("setResolutionPre: current display mode="+currentDisplayMode.getWidth()+"x"+currentDisplayMode.getHeight());
        if (frame == this.frame && !isFullScreen && bufferStrategy != null && (resolution == null || resolution.getWidth() == windowWidth && resolution.getHeight() == windowHeight)) {
            debugScreenWrite("setResolutionPre: no change needed");
            debugScreenWrite("setResolutionPre: success");
            return;
        }

        setResolutionPre(frame);

        debugScreenWrite("setResolutionPre: windowed mode requested");
        frame.setUndecorated(false);
        frame.setResizable(!fixedSize);
        final Point centerPoint = graphicsEnvironment.getCenterPoint();
        debugScreenWrite("setResolutionPre: screen center point is "+centerPoint);
        final Dimension dimension;
        if (resolution == null) {
            dimension = new Dimension(currentDisplayMode.getWidth(), currentDisplayMode.getHeight());
        } else {
            dimension = resolution.asDimension();
        }
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
        if (dimension.width > maxDimension.width || dimension.height > maxDimension.height) {
            //noinspection VariableNotUsedInsideIf
            if (resolution == null) {
                dimension.width = Math.max(minResolution.getWidth()+frameInsets.left+frameInsets.right, maxDimension.width);
                dimension.height = Math.max(minResolution.getHeight()+frameInsets.top+frameInsets.bottom, maxDimension.height);
                debugScreenWrite("setResolutionPre: window size exceeds maximum allowed size, reducing window size to "+dimension.width+"x"+dimension.height);
            } else {
                debugScreenWrite("setResolutionPre: window size exceeds maximum allowed size, ignoring");
            }
        }

        if (wasDisplayed) {
            debugScreenWrite("setResolutionPre: resizing window to "+dimension);
            frame.setPreferredSize(dimension);
            frame.setSize(dimension);
        } else {
            wasDisplayed = true;
            final int x2 = centerPoint.x-dimension.width/2-frameInsets.left;
            final int y2 = centerPoint.y-dimension.height/2-frameInsets.top;
            debugScreenWrite("setResolutionPre: moving window to "+x2+"/"+y2+" "+dimension.width+"x"+dimension.height);
            frame.setBounds(x2, y2, dimension.width+frameInsets.left+frameInsets.right, dimension.height+frameInsets.top+frameInsets.bottom);
        }

        setResolutionPost(frame, dimension);
        if (this.frame != null) {
            this.frame.removeComponentListener(componentListener);
        }
        this.frame = frame;
        this.frame.addComponentListener(componentListener);
        addMouseTracker(frame);
        addMouseTrackerRecursively(frame);
    }

    /**
     * Tries to switch to the given resolution. If resolution switching fails,
     * the window might be invisible.
     * @param frame the associated frame
     */
    private void setResolutionPre(@NotNull final Window frame) {
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
    }

    /**
     * Tries to switch to the given resolution. If resolution switching fails,
     * the window might be invisible.
     * @param frame the associated frame
     * @param dimension the window size to switch to
     */
    private void setResolutionPost(@NotNull final Window frame, @NotNull final Dimension dimension) {
        debugScreenWrite("setResolutionPost: creating buffer strategy");
        frame.createBufferStrategy(2);
        bufferStrategy = frame.getBufferStrategy();

        final Insets insets = frame.getInsets();
        offsetX = insets.left;
        offsetY = insets.top;
        debugScreenWrite("setResolutionPost: offset="+offsetX+"x"+offsetY+" "+insets);

        debugScreenWrite("setResolutionPost: requesting focus");
        frame.requestFocusInWindow();

        updateWindowSize(dimension.width, dimension.height);

        frame.add(layeredPane);
        if (currentGui != null) {
            addToLayeredPane(currentGui, 0, -1);
            if (windowWidth > 0 && windowHeight > 0) {
                assert currentGui != null;
                currentGui.setSize(windowWidth, windowHeight);
            }

            frame.validate();
            updateServerSettings();
        } else {
            frame.validate();
        }

        debugScreenWrite("setResolutionPost: success");
    }

    /**
     * Updates the window size for rendering from the main window size.
     * @param windowWidth the window width including insets
     * @param windowHeight the window height including insets
     */
    private void updateWindowSize(final int windowWidth, final int windowHeight) {
        if (this.windowWidth == windowWidth && this.windowHeight == windowHeight) {
            return;
        }
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        debugScreenWrite("updateWindowSize: gui size="+this.windowWidth+"x"+this.windowHeight);
        if (currentGui != null) {
            currentGui.setSize(windowWidth, windowHeight);
        }
        if (frame != null) {
            frame.validate();
        }
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
        if (isFullScreen && frame != null) {
            if (currentGui != null) {
                removeFromLayeredPane(currentGui);
            }
            final Resolution minResolution = new Resolution(1, 1);
            assert frame != null;
            setWindowMode(frame, null, minResolution, false);
            assert frame != null;
            removeMouseTracker(frame);
            assert frame != null;
            removeMouseTrackerRecursively(frame);
            assert frame != null;
            frame.removeComponentListener(componentListener);
            frame = null;
        }
    }

    /**
     * Paints the view into the given graphics instance.
     * @param g the graphics instance to paint to
     */
    public void redraw(@NotNull final Graphics g) {
        layeredPane.paint(g);
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
            redrawBlack(g);
            g.dispose();
            assert bufferStrategy != null;
            bufferStrategy.show();
        }
    }

    /**
     * Repaints all to black.
     * @param g the graphics to paint into
     */
    private void redrawBlack(@NotNull final Graphics g) {
        g.setColor(Color.BLACK);
        assert frame != null;
        final int width = frame.getWidth();
        assert frame != null;
        final int height = frame.getHeight();
        g.fillRect(0, 0, width, height);
    }

    /**
     * Opens a dialog. Raises an already opened dialog.
     * @param dialog the dialog to show
     * @param autoCloseOnDeactivate whether the dialog should auto-close when it
     * becomes inactive; ignored if the dialog is already open
     * @return whether the dialog was opened or raised; <code>false</code> if
     *         the dialog already was opened as the topmost dialog
     */
    public boolean openDialog(@NotNull final Gui dialog, final boolean autoCloseOnDeactivate) {
        if (dialog == currentGui) {
            return false;
        }

        if (!openDialogs.isEmpty() && openDialogs.get(openDialogs.size()-1) == dialog) {
            return false;
        }

        if (!openDialogsRemove(dialog)) {
            dialog.activateDefaultElement();
            @Nullable final GuiAutoCloseListener guiAutoCloseListener;
            if (autoCloseOnDeactivate) {
                guiAutoCloseListener = new GuiAutoCloseListener() {

                    @Override
                    public void autoClosed() {
                        closeDialog(dialog);
                    }

                };

            } else {
                guiAutoCloseListener = null;
            }
            dialog.setGuiAutoCloseListener(guiAutoCloseListener);
        }
        openDialogsAdd(dialog);
        updateServerSettings();
        return true;
    }

    /**
     * Raises an already opened dialog.
     * @param dialog the dialog to show
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
        updateServerSettings();
    }

    /**
     * Returns whether a given dialog is currently visible.
     * @param dialog the dialog to check
     * @return whether the dialog is visible
     */
    public boolean isDialogOpen(@NotNull final Gui dialog) {
        return openDialogs.contains(dialog);
    }

    /**
     * Returns all open dialogs in reverse painting order; the first element is
     * the top-most dialog.
     * @return the open dialogs; client code must not modify this list
     */
    @NotNull
    public Iterable<Gui> getOpenDialogs() {
        return new Iterable<Gui>() {

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
    @SuppressWarnings("NullableProblems")
    public void setCurrentGui(@NotNull final Gui gui) {
        SwingUtilities2.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                if (frame != null && currentGui != null) {
                    removeFromLayeredPane(currentGui);
                }
                currentGui = gui;
                //noinspection VariableNotUsedInsideIf
                if (frame != null) {
                    addToLayeredPane(currentGui, 0, -1);
                }

                if (windowWidth > 0 && windowHeight > 0) {
                    assert currentGui != null;
                    currentGui.setSize(windowWidth, windowHeight);
                }
                if (frame != null) {
                    frame.validate();
                }
            }

        });
        updateServerSettings();
    }

    /**
     * Closes a dialog. Does nothing if the given dialog is not open.
     * @param dialog the dialog to close
     * @return whether the dialog has been closed; <code>false</code> if the
     *         dialog was not open
     */
    public boolean closeDialog(@NotNull final Gui dialog) {
        if (!openDialogsRemove(dialog)) {
            return false;
        }

        dialog.setActiveElementActive(false);
        updateServerSettings();
        return true;
    }

    /**
     * Toggles a dialog: if the dialog is not shown, show it; else hide it.
     * @param dialog the dialog to toggle
     * @return whether the dialog is shown
     */
    public boolean toggleDialog(@NotNull final Gui dialog) {
        if (dialog == currentGui) {
            return true;
        }

        if (openDialogsRemove(dialog)) {
            dialog.setActiveElementActive(false);
            updateServerSettings();
            return false;
        }

        dialog.setGuiAutoCloseListener(null);
        openDialogsAdd(dialog);
        dialog.activateDefaultElement();
        updateServerSettings();
        return true;
    }

    /**
     * Sets the tooltip to use, or <code>null</code> if no tooltips should be
     * shown.
     * @param tooltip the tooltip to use, or <code>null</code>
     */
    public void setTooltip(@Nullable final Component tooltip) {
        if (this.tooltip != null) {
            layeredPane.remove(this.tooltip);
        }
        this.tooltip = tooltip;
        if (this.tooltip != null) {
            layeredPane.add(this.tooltip, 2, -1);
        }
    }

    /**
     * Sets the current gui state.
     * @param rendererGuiState the gui state
     */
    public void setGuiState(@NotNull final RendererGuiState rendererGuiState) {
        if (this.rendererGuiState == rendererGuiState) {
            return;
        }

        this.rendererGuiState = rendererGuiState;
        SwingUtilities2.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                for (final Gui dialog : openDialogs) {
                    removeFromLayeredPane(dialog);
                    if (!dialog.isHidden(rendererGuiState)) {
                        addToLayeredPane(dialog, 1, 0);
                    }
                }
                if (frame != null) {
                    frame.validate();
                }
            }

        });
        updateServerSettings();
        for (final RendererGuiStateListener listener : rendererGuiStateListeners.getListeners()) {
            listener.guiStateChanged(rendererGuiState);
        }
    }

    /**
     * Returns the current gui state.
     * @return the gui state
     */
    @NotNull
    public RendererGuiState getGuiState() {
        return rendererGuiState;
    }

    /**
     * Adds a gui state listener to be notified about {@link #rendererGuiState}
     * changes.
     * @param listener the listener to add
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
            raiseDialog(dialog);
            return;
        }

        dialog.autoSize(windowWidth, windowHeight);

        final Point mouse = frame == null ? null : frame.getMousePosition(true);
        if (mouse == null || dialog.isHidden(rendererGuiState)) {
            openDialogs.add(dialog);
            if (!dialog.isHidden(rendererGuiState)) {
                openDialogInt(dialog);
            }
        } else {
            if (dialog.isWithinDrawingArea(mouse.x, mouse.y)) {
                final MouseEvent mouseEvent = new MouseEvent(frame, 0, System.currentTimeMillis(), 0, mouse.x, mouse.y, 0, false);
                mouseTracker.mouseExited(mouseEvent);
                openDialogs.add(dialog);
                assert !dialog.isHidden(rendererGuiState);
                openDialogInt(dialog);
                mouseTracker.mouseEntered(findElement(mouseEvent), mouseEvent);
            } else {
                openDialogs.add(dialog);
                assert !dialog.isHidden(rendererGuiState);
                openDialogInt(dialog);
            }
        }
    }

    /**
     * Opens a dialog. Resizes the dialog if necessary.
     * @param dialog the dialog to open
     */
    private void openDialogInt(@NotNull final Gui dialog) {
        addToLayeredPane(dialog, 1, 0);
        final Dimension preferredSize = dialog.getPreferredSize();
        final Dimension size;
        if (preferredSize == null) {
            size = new Dimension(320, 200);
        } else {
            size = new Dimension(Math.min(preferredSize.width, windowWidth), Math.min(preferredSize.height, windowHeight));
        }
        dialog.setSize(size);
        if (frame != null) {
            frame.validate();
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

        final Point mouse = frame == null ? null : frame.getMousePosition(true);
        if (mouse == null) {
            openDialogs.remove(dialog);
            removeFromLayeredPane(dialog);
            if (frame != null) {
                frame.validate();
                // @todo too aggressive?
                assert frame != null;
                frame.repaint();
            }
        } else {
            if (dialog.isWithinDrawingArea(mouse.x, mouse.y)) {
                final MouseEvent mouseEvent = new MouseEvent(frame, 0, System.currentTimeMillis(), 0, mouse.x, mouse.y, 0, false);
                mouseTracker.mouseExited(mouseEvent);
                openDialogs.remove(dialog);
                removeFromLayeredPane(dialog);
                if (frame != null) {
                    frame.validate();
                    // @todo too aggressive?
                    assert frame != null;
                    frame.repaint();
                }
                mouseTracker.mouseEntered(findElement(mouseEvent), mouseEvent);
            } else {
                openDialogs.remove(dialog);
                removeFromLayeredPane(dialog);
                if (frame != null) {
                    frame.validate();
                    // @todo too aggressive?
                    assert frame != null;
                    frame.repaint();
                }
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
        for (final Gui dialog : openDialogs) {
            if (!dialog.isHidden(rendererGuiState)) {
                if (dialog.deactivateCommandInput()) {
                    return true;
                }
                if (dialog.isModal()) {
                    return false;
                }
            }
        }

        assert currentGui != null;
        return currentGui.deactivateCommandInput();
    }

    /**
     * Returns the active message buffer.
     * @return the active message buffer or <code>null</code> if none is active
     */
    @Nullable
    public Buffer getActiveMessageBuffer() {
        for (final Gui dialog : openDialogs) {
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

        assert currentGui != null;
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

    /**
     * Selects a server entry.
     * @param serverName the server name to select
     */
    public void setSelectedHostname(@NotNull final String serverName) {
        assert currentGui != null;
        final GUIMetaElementList metaElementList = currentGui.getFirstElement(GUIMetaElementList.class);
        if (metaElementList != null) {
            metaElementList.setSelectedHostname(serverName);
        }
    }

    /**
     * Activates the command input text field. If more than one input field
     * exists, the first matching one is selected.
     * @return the command input text field or <code>null</code> if no command
     *         input text field exists
     */
    @Nullable
    public GUIText activateCommandInput() {
        // check main gui
        assert currentGui != null;
        final GUIText textArea1 = activateCommandInput(currentGui);
        if (textArea1 != null) {
            return textArea1;
        }

        // check visible dialogs
        for (final Gui dialog : openDialogs) {
            if (!dialog.isHidden(rendererGuiState)) {
                final GUIText textArea2 = activateCommandInput(dialog);
                if (textArea2 != null) {
                    openDialog(dialog, false); // raise dialog
                    return textArea2;
                }
            }
            if (dialog.isModal()) {
                return null;
            }
        }

        return null;
    }

    /**
     * Dispatches a key press {@link KeyEvent}.
     * @param e the event to dispatch
     * @return whether a gui element did handle the event
     */
    public boolean handleKeyPress(@NotNull final KeyEvent e) {
        assert currentGui != null;
        return currentGui.handleKeyPress(e);
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

        @Override
        public boolean hasNext() {
            return it.hasPrevious();
        }

        @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
        @NotNull
        @Override
        public Gui next() {
            return it.previous();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

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
     * Finds the gui element a given {@link Component} is part of.
     * @param component the component to search
     * @param mouseEvent the mouse event to update
     * @return the gui element found or <code>null</code> if none was found
     */
    @Nullable
    private static AbstractGUIElement findElement(@NotNull final Component component, @NotNull final MouseEvent mouseEvent) {
        for (Component result = component; result != null; result = result.getParent()) {
            if (result instanceof AbstractGUIElement) {
                return (AbstractGUIElement)result;
            }
            if (result instanceof JViewport) {
                final JViewport viewport = (JViewport)result;
                final Point position = viewport.getViewPosition();
                mouseEvent.translatePoint(-position.x, -position.y);
            }
        }
        return null;
    }

    /**
     * Finds the gui element for a given {@link MouseEvent}. If a gui element
     * was found, update the event mouse coordinates to be relative to the gui
     * element.
     * @param e the mouse event to process
     * @return the gui element found, or <code>null</code> if none was found
     */
    @Nullable
    private AbstractGUIElement findElement(@NotNull final MouseEvent e) {
        final MouseEvent ce = e;//convertEvent(e);
        AbstractGUIElement elected = null;

        final int eX = ce.getX();
        final int eY = ce.getY();
        for (final Gui dialog : openDialogs) {
            if (!dialog.isHidden(rendererGuiState)) {
                elected = getElementFromPoint(dialog, eX-dialog.getX(), eY-dialog.getY());
                if (elected != null) {
                    break;
                }
            }
            if (dialog.isModal()) {
                return null;
            }
        }

        if (elected == null) {
            assert currentGui != null;
            elected = getElementFromPoint(currentGui, eX, eY);
        }

        return elected;
    }

    /**
     * Determines the {@link GUIElement} for a given coordinate with a given
     * {@link Gui} instance.
     * @param gui the gui to search
     * @param eX the x-coordinate to check
     * @param eY the y-coordinate to check
     * @return the <code>GUIElement</code> at the given coordinate or
     *         <code>null</code> if none was found
     */
    @Nullable
    private AbstractGUIElement getElementFromPoint(@NotNull final Gui gui, final int eX, final int eY) {
        final int x = eX-offsetX;
        final int y = eY-offsetY;
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

    /**
     * Adds a component to {@link #layeredPane}.
     * @param component the component
     * @param layer the layer to add to
     * @param index the index within the layer to add to
     */
    private void addToLayeredPane(@NotNull final Component component, final int layer, final int index) {
        layeredPane.add(component, layer, index);
        addMouseTrackerRecursively(component);
        addComponent(component);
    }

    /**
     * Removes a component from {@link #layeredPane}.
     * @param component the component
     */
    private void removeFromLayeredPane(@NotNull final Component component) {
        layeredPane.remove(component);
        removeMouseTrackerRecursively(component);
        removeComponent(component);
    }

    /**
     * Adds {@link #mouseTracker} to a {@link Component}.
     * @param component the component to add to
     */
    private void addMouseTracker(@NotNull final Component component) {
        component.addMouseListener(mouseInputListener);
        component.addMouseMotionListener(mouseInputListener);
    }

    /**
     * Removes {@link #mouseTracker} from a {@link Component}.
     * @param component the component to remove from
     */
    private void removeMouseTracker(@NotNull final Component component) {
        component.removeMouseListener(mouseInputListener);
        component.removeMouseMotionListener(mouseInputListener);
    }

    /**
     * Adds {@link #mouseTracker} recursively to all children of a {@link
     * Component}.
     * @param component the component to add to
     */
    private void addMouseTrackerRecursively(@NotNull final Component component) {
        addMouseTracker(component);
        if (component instanceof Container) {
            final Container container = (Container)component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                addMouseTrackerRecursively(container.getComponent(i));
            }
        }
    }

    /**
     * Removes {@link #mouseTracker} recursively from all children of a {@link
     * Component}.
     * @param component the component to add to
     */
    private void removeMouseTrackerRecursively(@NotNull final Component component) {
        removeMouseTracker(component);
        if (component instanceof Container) {
            final Container container = (Container)component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                removeMouseTrackerRecursively(container.getComponent(i));
            }
        }
    }

    /**
     * Adds a {@link Component}.
     * @param component the component to add
     */
    private void addComponent(@NotNull final Component component) {
        if (component instanceof GUIMap) {
            final GUIMap map = (GUIMap)component;
            maps.add(map);
        }
        if (component instanceof GUIFloorList) {
            final GUIFloorList floorList = (GUIFloorList)component;
            floorLists.add(floorList);
        }
        if (component instanceof Container) {
            final Container container = (Container)component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                addComponent(container.getComponent(i));
            }
        }
    }

    /**
     * Removes a {@link Component}.
     * @param component the component to remove
     */
    private void removeComponent(@NotNull final Component component) {
        if (component instanceof GUIMap) {
            final GUIMap map = (GUIMap)component;
            maps.remove(map);
        }
        if (component instanceof GUIFloorList) {
            final GUIFloorList floorList = (GUIFloorList)component;
            floorLists.remove(floorList);
        }
        if (component instanceof Container) {
            final Container container = (Container)component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                removeComponent(container.getComponent(i));
            }
        }
    }

    /**
     * Updates server based settings to current screen size. Does nothing if the
     * main window is not visible.
     */
    public void updateServerSettings() {
        if (frame == null || !frame.isVisible()) {
            return;
        }

        final Dimension mapSize = getMapSize();
        crossfireServerConnection.setPreferredMapSize(mapSize.width, mapSize.height);
        crossfireServerConnection.setPreferredNumLookObjects(getNumLookObjects());
    }

    /**
     * Returns the map size in squares.
     * @return the map size
     */
    @NotNull
    private Dimension getMapSize() {
        int width = DEFAULT_MAP_WIDTH;
        int height = DEFAULT_MAP_HEIGHT;
        for (final GUIMap map : maps) {
            width = Math.max(width, map.getPreferredMapWidth());
            height = Math.max(height, map.getPreferredMapHeight());
        }
        return new Dimension(width, height);
    }

    /**
     * Returns the number of ground view objects to request from the server.
     * @return the number of ground view objects
     */
    private int getNumLookObjects() {
        int minNumLookObjects = Integer.MAX_VALUE;
        for (final GUIFloorList floorList : floorLists) {
            minNumLookObjects = Math.min(minNumLookObjects, floorList.getNumLookObjects());
        }
        if (minNumLookObjects < Integer.MAX_VALUE) {
            return minNumLookObjects;
        }

        return DEFAULT_NUM_LOOK_OBJECTS;
    }

    /**
     * Returns the first command text field of a gui and make it active.
     * @param gui the gui to check
     * @return the comment text field, or <code>null</code> if this gui does not
     *         contain any command text fields
     */
    @Nullable
    public static GUIText activateCommandInput(@NotNull final Gui gui) {
        final GUIText textArea = gui.getFirstElement(GUIText.class);
        if (textArea == null) {
            return null;
        }

        if (!textArea.getName().equals("command")) {
            return null;
        }

        textArea.setActive(true);
        return textArea;
    }

}

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

package com.realtime.crossfire.jxclient.main;

import com.realtime.crossfire.jxclient.account.CharacterInformation;
import com.realtime.crossfire.jxclient.account.CharacterModel;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.MouseTracker;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireAccountListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.server.crossfire.SentReplyListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.util.Resolution;
import com.realtime.crossfire.jxclient.util.ResourceUtils;
import com.realtime.crossfire.jxclient.window.DialogStateParser;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.KeyHandler;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main window.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class JXCWindow {

    /**
     * TODO: Remove when more options are implemented in the start screen gui.
     */
    public static final boolean DISABLE_START_GUI = true;

    /**
     * The {@link Exiter} to use.
     */
    @NotNull
    private final Exiter exiter;

    /**
     * The {@link GuiManager} for controlling the main GUI state.
     */
    @NotNull
    private final GuiManager guiManager;

    /**
     * The {@link CrossfireServerConnection} to use.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * The command queue instance for this window.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The {@link JXCWindowRenderer} for this window.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * The semaphore for drawing the window contents.
     */
    @NotNull
    private final Object semaphoreDrawing = new Object();

    /**
     * The {@link KeyHandler} for processing keyboard input.
     */
    @NotNull
    private final KeyHandler keyHandler;

    /**
     * The option manager for this window.
     */
    @NotNull
    private final OptionManager optionManager;

    @NotNull
    private final CharacterModel characterModel;

    /**
     * The main window.
     */
    @NotNull
    private final JFrame frame;

    /**
     * The {@link WindowFocusListener} registered for this window. It resets the
     * keyboard modifier state when the window loses the focus. The idea is to
     * prevent the following: user switches from JXClient to another window with
     * CTRL+ALT+direction key. This makes JXClient enter RUN mode since CTRL was
     * pressed. The following key release event is not received by JXClient
     * because it does not own the focus. Therefore JXClient's CTRL state is
     * still active when the user switches back to JXClient. A following
     * direction key then causes the character to run which is not what the
     * player wants.
     */
    @NotNull
    private final WindowFocusListener windowFocusListener = new WindowAdapter() {
        /** {@inheritDoc} */
        @Override
        public void windowLostFocus(final WindowEvent e) {
            keyHandler.reset();
            commandQueue.stopRunning();
        }
    };

    /**
     * The {@link CrossfireUpdateItemListener} to receive item updates.
     */
    @NotNull
    private final CrossfireUpdateItemListener crossfireUpdateItemListener = new CrossfireUpdateItemListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void delinvReceived(final int tag) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void delitemReceived(@NotNull final int[] tags) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addItemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void playerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name) {
            guiManager.playerReceived();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void upditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
            // ignore
        }

    };

    /**
     * The window listener attached to this frame.
     */
    @NotNull
    private final WindowListener windowListener = new WindowAdapter() {
        /** {@inheritDoc} */
        @Override
        public void windowClosing(@NotNull final WindowEvent e) {
            if (!guiManager.openQuitDialog()) {
                exiter.terminate();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void windowClosed(@NotNull final WindowEvent e) {
            if (!frame.isVisible()) {
                exiter.terminate();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void windowIconified(@NotNull final WindowEvent e) {
            windowRenderer.setInhibitPaintIconified(true);
        }

        /** {@inheritDoc} */
        @Override
        public void windowDeiconified(@NotNull final WindowEvent e) {
            windowRenderer.setInhibitPaintIconified(false);
        }
    };

    /**
     * The {@link KeyListener} attached to the main window.
     */
    @NotNull
    private final KeyListener keyListener = new KeyListener() {
        /** {@inheritDoc} */
        @Override
        public void keyTyped(@NotNull final KeyEvent e) {
            synchronized (semaphoreDrawing) {
                keyHandler.keyTyped(e);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void keyPressed(@NotNull final KeyEvent e) {
            synchronized (semaphoreDrawing) {
                keyHandler.keyPressed(e);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void keyReleased(@NotNull final KeyEvent e) {
            synchronized (semaphoreDrawing) {
                keyHandler.keyReleased(e);
            }
        }
    };

    /**
     * The {@link CrossfireQueryListener} attached to {@link #server}. It parses
     * query messages to open/close dialogs.
     */
    @NotNull
    private final CrossfireQueryListener crossfireQueryListener = new CrossfireQueryListener() {
        /** {@inheritDoc} */
        @Override
        public void commandQueryReceived(@NotNull final String prompt, final int queryType) {
            synchronized (semaphoreDrawing) {
                guiManager.openQueryDialog(prompt, queryType);
            }
        }
    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {
        /** {@inheritDoc} */
        @Override
        public void start() {
            server.removeCrossfireQueryListener(crossfireQueryListener);
            server.removeCrossfireUpdateItemListener(crossfireUpdateItemListener);
            if (DISABLE_START_GUI) {
                exiter.terminate();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver() {
            server.removeCrossfireQueryListener(crossfireQueryListener);
            server.removeCrossfireUpdateItemListener(crossfireUpdateItemListener);
        }

        /** {@inheritDoc} */
        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo) {
            server.addCrossfireQueryListener(crossfireQueryListener);
            server.addCrossfireUpdateItemListener(crossfireUpdateItemListener);
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected() {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }
    };

    /**
     * The {@link SentReplyListener} for detecting "reply" commands sent to the
     * server.
     */
    @NotNull
    private final SentReplyListener sentReplyListener = new SentReplyListener() {
        /** {@inheritDoc} */
        @Override
        public void replySent(@NotNull final String text) {
            guiManager.closeQueryDialog();
        }
    };

    @NotNull
    private final CrossfireAccountListener accountListener = new CrossfireAccountListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void manageAccount() {
            guiManager.manageAccount();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAccount(@NotNull final String name, @NotNull final String characterClass, @NotNull final String race, @NotNull final String face, @NotNull final String party, @NotNull final String map, final int level, final int faceNumber) {
            final CharacterInformation information = new CharacterInformation();
            information.setName(name);
            information.setRace(race);
            characterModel.add(information);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startAccountList(@NotNull final String accountName) {
            characterModel.begin();
            guiManager.setAccountName(accountName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endAccountList() {
            characterModel.commit();
            guiManager.showCharacters();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startPlaying() {
            guiManager.hideAccountWindows();
        }

    };

    /**
     * Creates a new instance.
     * @param exiter the exiter to use
     * @param server the crossfire server connection to use
     * @param optionManager the option manager instance to use
     * @param guiStateManager the gui state manager to use
     * @param windowRenderer the window renderer to use
     * @param commandQueue the command queue instance
     * @param guiManager the gui manager instance
     * @param keyHandler the key handler for keyboard input
     */
    public JXCWindow(@NotNull final Exiter exiter, @NotNull final CrossfireServerConnection server, @NotNull final OptionManager optionManager, @NotNull final GuiStateManager guiStateManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final CommandQueue commandQueue, @NotNull final GuiManager guiManager, @NotNull final KeyHandler keyHandler, @NotNull final CharacterModel characterModel) {
        this.exiter = exiter;
        this.server = server;
        this.optionManager = optionManager;
        this.windowRenderer = windowRenderer;
        this.commandQueue = commandQueue;
        this.guiManager = guiManager;
        this.keyHandler = keyHandler;
        this.characterModel = characterModel;
        frame = new JFrame("") {

            /**
             * The serial version UID.
             */
            private static final long serialVersionUID = 1L;

            /**
             * {@inheritDoc}
             */
            @Override
            public void paint(@NotNull final Graphics g) {
                windowRenderer.repaint();
            }

        };
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        try {
            frame.setIconImage(ResourceUtils.loadImage(ResourceUtils.APPLICATION_ICON).getImage());
        } catch (final IOException ex) {
            System.err.println("Cannot find application icon: "+ex.getMessage());
        }
        frame.setFocusTraversalKeysEnabled(false);
        frame.addWindowFocusListener(windowFocusListener);
        frame.addWindowListener(windowListener);
        frame.addComponentListener(new ComponentListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void componentResized(final ComponentEvent e) {
                windowRenderer.updateWindowSize(frame.getWidth(), frame.getHeight());
                guiManager.updateWindowSize(new Dimension(windowRenderer.getWindowWidth(), windowRenderer.getWindowHeight()));
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void componentMoved(final ComponentEvent e) {
                // ignore
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void componentShown(final ComponentEvent e) {
                // ignore
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void componentHidden(final ComponentEvent e) {
                // ignore
            }

        });
        server.addSentReplyListener(sentReplyListener);
        server.addCrossfireAccountListener(accountListener);
        guiStateManager.addGuiStateListener(guiStateListener);
        frame.addKeyListener(keyListener);

        windowRenderer.start();
    }

    /**
     * Initializes the instance: loads and displays the skin.
     * @param resolution the size of the client area, <code>null</code> for
     * default
     * @param mouseTracker the mouse tracker to use
     * @param skinName the skin to load
     * @param fullScreen whether full-screen mode should be enabled
     * @param skinLoader the skin loader instance
     */
    public void init(@Nullable final Resolution resolution, @NotNull final MouseTracker mouseTracker, @NotNull final String skinName, final boolean fullScreen, @NotNull final SkinLoader skinLoader) {
        JXCSkin skin;
        try {
            skin = skinLoader.loadSkin(skinName);
        } catch (final JXCSkinException ex) {
            if (skinName.equals(Options.DEFAULT_SKIN)) {
                System.err.println("cannot load skin "+skinName+": "+ex.getMessage());
                System.exit(1);
            }

            System.err.println("cannot load skin "+skinName+": "+ex.getMessage()+", trying default skin");
            try {
                skin = skinLoader.loadSkin(Options.DEFAULT_SKIN);
            } catch (final JXCSkinException ex2) {
                System.err.println("cannot load default skin "+Options.DEFAULT_SKIN+": "+ex2.getMessage());
                System.exit(1);
                throw new AssertionError();
            }
        }
        guiManager.unsetSkin();
        optionManager.loadOptions();
        keyHandler.setKeyBindings(skin.getDefaultKeyBindings());

        final Resolution minResolution = skin.getMinResolution();
        final Dimension minSize = minResolution.asDimension();
        final Dimension maxSize = skin.getMaxResolution().asDimension();
        final Insets insets = frame.getInsets();
        minSize.width += insets.left+insets.right;
        minSize.height += insets.top+insets.bottom;
        maxSize.width += insets.left+insets.right;
        maxSize.height += insets.top+insets.bottom;
        frame.setMinimumSize(minSize);
        frame.setMaximumSize(maxSize);

        if (!fullScreen || !windowRenderer.setFullScreenMode(frame, resolution)) {
            windowRenderer.setWindowMode(frame, resolution, minResolution, minSize.equals(maxSize));
        }

        guiManager.setSkin(skin);
        guiManager.updateWindowSize(frame.getSize());
        DialogStateParser.load(skin, windowRenderer);

        frame.addMouseListener(mouseTracker);
        frame.addMouseMotionListener(mouseTracker);
    }

    /**
     * Frees all resources. Should be called before the application terminates.
     */
    public void term() {
        windowRenderer.stop();
        guiManager.term();
        optionManager.saveOptions();
    }

    /**
     * Returns the main window.
     * @return the main window
     */
    @Deprecated
    @NotNull
    public Frame getFrame() {
        return frame;
    }

}

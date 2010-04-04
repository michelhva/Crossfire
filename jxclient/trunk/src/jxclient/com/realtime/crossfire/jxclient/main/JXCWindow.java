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

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.gui.GuiFactory;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.MouseTracker;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.guistate.GuiState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemSetListener;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.scripts.ScriptManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.server.crossfire.SentReplyListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.settings.options.Pickup;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.skin.io.JXCSkinLoader;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinClassSource;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinDirSource;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinSource;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.Resolution;
import com.realtime.crossfire.jxclient.util.ResourceUtils;
import com.realtime.crossfire.jxclient.window.DialogStateParser;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.JXCConnection;
import com.realtime.crossfire.jxclient.window.KeyHandler;
import com.realtime.crossfire.jxclient.window.KeyHandlerListener;
import com.realtime.crossfire.jxclient.window.ShortcutsManager;
import java.awt.Dimension;
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
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import javax.swing.JFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main window.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class JXCWindow extends JFrame {

    /**
     * TODO: Remove when more options are implemented in the start screen gui.
     */
    private static final boolean DISABLE_START_GUI = true;

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link GuiManager} for controlling the main GUI state.
     */
    @NotNull
    private final GuiManager guiManager;

    /**
     * The {@link GuiStateManager} instance.
     */
    @NotNull
    private final GuiStateManager guiStateManager;

    /**
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    /**
     * The {@link FacesManager} instance.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The {@link ItemSet} instance.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The inventory {@link ItemView} instance.
     */
    @NotNull
    private final ItemView inventoryView;

    /**
     * The {@link FloorView} instance.
     */
    @NotNull
    private final FloorView floorView;

    /**
     * The {@link SpellsManager} instance.
     */
    @NotNull
    private final SpellsManager spellsManager;

    /**
     * The {@link Stats} instance.
     */
    @NotNull
    private final Stats stats;

    /**
     * The {@link SkillSet} instance.
     */
    @NotNull
    private final SkillSet skillSet;

    /**
     * The {@link CfMapUpdater} instance.
     */
    @NotNull
    private final CfMapUpdater mapUpdater;

    /**
     * The global experience table.
     */
    @NotNull
    private final ExperienceTable experienceTable;

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
     * The {@link MouseTracker} for this window.
     */
    @NotNull
    private final MouseTracker mouseTracker;

    /**
     * The {@link JXCWindowRenderer} for this window.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    @NotNull
    private final Object semaphoreDrawing = new Object();

    /**
     * The {@link KeyHandler} for processing keyboard input.
     */
    @NotNull
    private final KeyHandler keyHandler;

    /**
     * The shortcuts manager for this window.
     */
    @NotNull
    private final ShortcutsManager shortcutsManager;

    /**
     * The current pickup mode.
     */
    @NotNull
    private final Pickup characterPickup;

    /**
     * The option manager for this window.
     */
    @NotNull
    private final OptionManager optionManager;

    /**
     * The current spell manager instance for this window.
     */
    @NotNull
    private final CurrentSpellManager currentSpellManager = new CurrentSpellManager();

    /**
     * The metaserver model instance for this window.
     */
    @NotNull
    private final MetaserverModel metaserverModel;

    /**
     * The connection.
     */
    @NotNull
    private final JXCConnection connection;

    /**
     * The size of the client area. Set to <code>null</code> for default.
     */
    @Nullable
    private final Resolution resolution;

    /**
     * Whether a server connection is active.
     */
    private boolean connected = false;

    /**
     * The synchronization object for accesses to {@link #connected}.
     */
    @NotNull
    private final Object semaphoreConnected = new Object();

    /**
     * The {@link Macros} instance.
     */
    @NotNull
    private final Macros macros;

    /**
     * The {@link WindowFocusListener} registered for this window. It resets the
     * keyboard modifier state when the window loses the focus. The idea is to
     * prevent the following: user switches from jxclient to another window with
     * CTRL+ALT+direction key. This makes jxclient enter RUN mode since CTRL was
     * pressed. The following key release event is not received by jxclient
     * because it does not own the focus. Therefore jxclient's CTRL state is
     * still active when the user switches back to jxclient. A following
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
        public void additemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void playerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name) {
            guiManager.playerReceived();
            commandQueue.sendNcom(true, 1, "output-count 1"); // to make message merging work reliably
            characterPickup.update();                         // reset pickup mode
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
     * The listener to detect a changed player name.
     */
    @NotNull
    private final ItemSetListener itemSetListener = new ItemSetListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void itemAdded(@NotNull final CfItem item) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void itemMoved(@NotNull final CfItem item) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void itemChanged(@NotNull final CfItem item) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void itemRemoved(@NotNull final CfItem item) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void playerChanged(@Nullable final CfItem player) {
            connection.setCharacter(player == null ? null : player.getName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void openContainerChanged(final int tag) {
            // ignore
        }

    };

    /**
     * The {@link ClientSocketListener} used to detect connection state
     * changes.
     */
    @NotNull
    private final ClientSocketListener clientSocketListener = new ClientSocketListener() {
        /** {@inheritDoc} */
        @Override
        public void connecting() {
            setConnected(true);
        }

        /** {@inheritDoc} */
        @Override
        public void connected() {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void packetReceived(@NotNull final byte[] buf, final int start, final int end) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void packetSent(@NotNull final byte[] buf, final int len) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void disconnecting(@NotNull final String reason, final boolean isError) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void disconnected(@NotNull final String reason) {
            setConnected(false);
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
                guiManager.terminate();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void windowClosed(@NotNull final WindowEvent e) {
            if (!isVisible()) {
                guiManager.terminate();
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
     * The {@link KeyHandlerListener} attached to {@link #keyHandler}.
     */
    @NotNull
    private final KeyHandlerListener keyHandlerListener = new KeyHandlerListener() {
        /** {@inheritDoc} */
        @Override
        public void escPressed() {
            if (guiStateManager.getGuiState() == GuiState.CONNECT_FAILED) {
                guiStateManager.disconnect();
                return;
            }

            switch (guiManager.escPressed(isConnected())) {
            case 0:
                break;

            case 1:
                guiStateManager.disconnect();
                break;

            case 2:
                guiManager.terminate();
                break;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void keyReleased() {
            guiManager.closeKeybindDialog();
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
            itemSet.removeItemSetListener(itemSetListener);
            server.removeCrossfireQueryListener(crossfireQueryListener);
            server.removeCrossfireUpdateItemListener(crossfireUpdateItemListener);
            if (DISABLE_START_GUI) {
                guiManager.terminate();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver() {
            itemSet.removeItemSetListener(itemSetListener);
            server.removeCrossfireQueryListener(crossfireQueryListener);
            server.removeCrossfireUpdateItemListener(crossfireUpdateItemListener);
        }

        /** {@inheritDoc} */
        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            connection.setHost(serverInfo);
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo) {
            facesManager.reset();
            itemSet.addItemSetListener(itemSetListener);
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

    /**
     * Creates a new instance.
     * @param terminateSync the object to be notified when the application
     * terminates
     * @param server the crossfire server connection to use
     * @param debugGui whether GUI elements should be highlighted
     * @param debugKeyboard if non-<code>null</code>, write all keyboard debug
     * to this writer
     * @param settings the settings instance to use
     * @param optionManager the option manager instance to use
     * @param metaserverModel the metaserver model to use
     * @param resolution the size of the client area, <code>null</code> for
     * default
     * @param guiStateManager the gui state manager to use
     * @param experienceTable the experience table to use
     * @param skillSet the skill set to use
     * @param stats the stats to use
     * @param facesManager the faces manager to use
     * @param itemSet the item set to use
     * @param inventoryView the inventory item view to use
     * @param floorView the floor view to use
     * @param mouseTracker the mouse tracker to use
     * @param windowRenderer the window renderer to use
     * @param skinName the skin to load
     * @param fullScreen whether full-screen mode should be enabled
     * @param serverInfo the server to connect to or <code>null</code>
     * @param macros the macros instance
     * @param mapUpdater the map updater instance
     * @param spellsManager the spells manager instance
     * @param commandQueue the command queue instance
     * @param scriptManager the script manager instance
     * @param shortcutsManager the shortcuts manager instance
     */
    public JXCWindow(@NotNull final Object terminateSync, @NotNull final CrossfireServerConnection server, final boolean debugGui, @Nullable final Writer debugKeyboard, @NotNull final Settings settings, @NotNull final OptionManager optionManager, @NotNull final MetaserverModel metaserverModel, @Nullable final Resolution resolution, @NotNull final GuiStateManager guiStateManager, @NotNull final ExperienceTable experienceTable, @NotNull final SkillSet skillSet, @NotNull final Stats stats, @NotNull final FacesManager facesManager, @NotNull final ItemSet itemSet, @NotNull final ItemView inventoryView, @NotNull final FloorView floorView, @NotNull final MouseTracker mouseTracker, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final String skinName, final boolean fullScreen, @Nullable final String serverInfo, @NotNull final Macros macros, @NotNull final CfMapUpdater mapUpdater, @NotNull final SpellsManager spellsManager, @NotNull final CommandQueue commandQueue, @NotNull final ScriptManager scriptManager, @NotNull final ShortcutsManager shortcutsManager) {
        super("");
        this.server = server;
        this.debugGui = debugGui;
        this.optionManager = optionManager;
        this.metaserverModel = metaserverModel;
        this.resolution = resolution;
        this.guiStateManager = guiStateManager;
        this.experienceTable = experienceTable;
        this.skillSet = skillSet;
        this.stats = stats;
        this.facesManager = facesManager;
        this.itemSet = itemSet;
        this.inventoryView = inventoryView;
        this.floorView = floorView;
        this.mouseTracker = mouseTracker;
        this.windowRenderer = windowRenderer;
        this.macros = macros;
        this.mapUpdater = mapUpdater;
        this.spellsManager = spellsManager;
        this.commandQueue = commandQueue;
        this.shortcutsManager = shortcutsManager;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        guiManager = new GuiManager(guiStateManager, semaphoreDrawing, terminateSync, new TooltipManager(windowRenderer), settings, server, macros, windowRenderer, scriptManager, commandQueue, optionManager, debugGui ? mouseTracker : null);
        keyHandler = new KeyHandler(debugKeyboard, guiManager.getKeybindingsManager(), commandQueue, windowRenderer, keyHandlerListener);
        try {
            characterPickup = new Pickup(commandQueue, optionManager);
        } catch (final OptionException ex) {
            throw new AssertionError();
        }
        try {
            setIconImage(ResourceUtils.loadImage(ResourceUtils.APPLICATION_ICON).getImage());
        } catch (final IOException ex) {
            System.err.println("Cannot find application icon: "+ex.getMessage());
        }
        setFocusTraversalKeysEnabled(false);
        addWindowFocusListener(windowFocusListener);
        addWindowListener(windowListener);
        addComponentListener(new ComponentListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void componentResized(final ComponentEvent e) {
                windowRenderer.updateWindowSize(getWidth(), getHeight());
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
        connection = new JXCConnection(guiManager.getKeybindingsManager(), shortcutsManager, settings, this, characterPickup, server, guiStateManager);
        server.addClientSocketListener(clientSocketListener);
        server.addSentReplyListener(sentReplyListener);
        guiManager.setConnection(connection);
        guiStateManager.addGuiStateListener(guiStateListener);
        addKeyListener(keyListener);
        JXCSkin skin;
        try {
            skin = loadSkin(skinName);
        } catch (final JXCSkinException ex) {
            if (skinName.equals(Options.DEFAULT_SKIN)) {
                System.err.println("cannot load skin "+skinName+": "+ex.getMessage());
                System.exit(1);
            }

            System.err.println("cannot load skin "+skinName+": "+ex.getMessage()+", trying default skin");
            try {
                skin = loadSkin(Options.DEFAULT_SKIN);
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
        final Insets insets = getInsets();
        minSize.width += insets.left+insets.right;
        minSize.height += insets.top+insets.bottom;
        maxSize.width += insets.left+insets.right;
        maxSize.height += insets.top+insets.bottom;
        setMinimumSize(minSize);
        setMaximumSize(maxSize);

        if (!fullScreen || !windowRenderer.setFullScreenMode(this, resolution)) {
            if (!windowRenderer.setWindowMode(this, resolution, minResolution, minSize.equals(maxSize))) {
                System.err.println("cannot create window with resolution "+resolution);
                System.exit(1);
                throw new AssertionError();
            }
        }

        guiManager.setSkin(skin);
        guiManager.updateWindowSize(getSize());

        DialogStateParser.load(skin, windowRenderer);
        guiManager.initRendering();

        if (serverInfo != null) {
            guiStateManager.connect(serverInfo);
        } else {
            guiStateManager.changeGUI(DISABLE_START_GUI ? GuiState.METASERVER : GuiState.START);
        }
        addMouseListener(mouseTracker);
        addMouseMotionListener(mouseTracker);
        guiManager.init3();
    }

    /**
     * Frees all resources. Should be called before the application terminates.
     */
    public void term() {
        guiManager.term();
        optionManager.saveOptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(@NotNull final Graphics g) {
        windowRenderer.repaint();
    }

    /**
     * Loads a skin file.
     * @param skinName the skin file name
     * @return the loaded skin
     * @throws JXCSkinException if the skin file cannot be loaded
     */
    @NotNull
    private JXCSkin loadSkin(@NotNull final String skinName) throws JXCSkinException {
        // check for skin in directory
        final File dir = new File(skinName);
        final KeyBindings defaultKeyBindings = new KeyBindings(null, guiManager.getCommands(), guiManager.getCommandCallback(), macros);
        final JXCSkinSource skinSource;
        if (dir.exists() && dir.isDirectory()) {
            skinSource = new JXCSkinDirSource(dir);
        } else {
            // fallback: built-in resource
            skinSource = new JXCSkinClassSource("com/realtime/crossfire/jxclient/skins/"+skinName);
        }
        final JXCSkinLoader newSkin = new JXCSkinLoader(itemSet, inventoryView, floorView, spellsManager, facesManager, stats, mapUpdater, defaultKeyBindings, optionManager, experienceTable, skillSet);
        final Commands commands = guiManager.getCommands();
        final GuiFactory guiFactory = new GuiFactory(debugGui ? mouseTracker : null, commands, guiManager.getCommandCallback(), macros);
        final CommandCallback commandCallback = guiManager.getCommandCallback();
        final JXCSkin skin = newSkin.load(skinSource, server, guiStateManager, guiManager.getTooltipManager(), windowRenderer, windowRenderer.getElementListener(), metaserverModel, commandQueue, shortcutsManager.getShortcuts(), commands, currentSpellManager, commandCallback, macros, guiFactory);
        if (resolution != null) {
            if (skin.getMinResolution().getWidth() > resolution.getWidth() || skin.getMinResolution().getHeight() > resolution.getHeight()) {
                throw new JXCSkinException("resolution "+resolution+" is not supported by this skin");
            }
            if (resolution.getWidth() > skin.getMaxResolution().getWidth() || resolution.getHeight() > skin.getMaxResolution().getHeight()) {
                throw new JXCSkinException("resolution "+resolution+" is not supported by this skin");
            }
        }

        return skin;
    }

    /**
     * Records whether a server connection is active.
     * @param connected whether a server connection is active
     */
    private void setConnected(final boolean connected) {
        synchronized (semaphoreConnected) {
            this.connected = connected;
        }
    }

    /**
     * Returns whether a server connection is active.
     * @return whether a server connection is active
     */
    private boolean isConnected() {
        synchronized (semaphoreConnected) {
            return connected;
        }
    }

}

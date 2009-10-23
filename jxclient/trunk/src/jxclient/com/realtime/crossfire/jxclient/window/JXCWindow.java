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

import com.realtime.crossfire.jxclient.animations.Animations;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.faces.FaceCache;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FileCache;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.items.PlayerListener;
import com.realtime.crossfire.jxclient.main.Options;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.scripts.ScriptManager;
import com.realtime.crossfire.jxclient.server.ClientSocketListener;
import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnectionListener;
import com.realtime.crossfire.jxclient.server.Pickup;
import com.realtime.crossfire.jxclient.server.UnknownCommandException;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.JXCSkinClassSource;
import com.realtime.crossfire.jxclient.skin.JXCSkinDirSource;
import com.realtime.crossfire.jxclient.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.skin.JXCSkinLoader;
import com.realtime.crossfire.jxclient.skin.JXCSkinSource;
import com.realtime.crossfire.jxclient.skin.Resolution;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.ActiveSkillWatcher;
import com.realtime.crossfire.jxclient.stats.PoisonWatcher;
import com.realtime.crossfire.jxclient.stats.Stats;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public class JXCWindow extends JFrame
{
    /** The serial version UID. */
    private static final long serialVersionUID = 1;

    /** TODO: Remove when more options are implemented in the start screen gui. */
    private static final boolean DISABLE_START_GUI = true;

    /**
     * The connection state listeners to notify.
     */
    private final List<GuiStateListener> guiStateListeners = new ArrayList<GuiStateListener>();

    /**
     * The {@link GuiManager} for controlling the main GUI state.
     */
    private final GuiManager guiManager;

    private GuiState guiState = null;

    /**
     * Terminate the application if set.
     */
    private final Object terminateSync;

    /**
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    /**
     * The {@link FacesManager} instance.
     */
    private final FacesManager facesManager;

    /**
     * The {@link ItemsManager} instance.
     */
    private final ItemsManager itemsManager;

    /**
     * The {@link SpellsManager} instance.
     */
    private final SpellsManager spellsManager;

    /**
     * The {@link Stats} instance.
     */
    private final Stats stats;

    /**
     * The {@link SkillSet} instance.
     */
    private final SkillSet skillSet;

    /**
     * The {@link CfMapUpdater} instance.
     */
    private final CfMapUpdater mapUpdater;

    /**
     * The global experience table.
     */
    private final ExperienceTable experienceTable;

    /**
     * The {@link CrossfireServerConnection} to use.
     */
    private final CrossfireServerConnection server;

    private final Animations animations = new Animations(this);

    /**
     * The command queue instance for this window.
     */
    private final CommandQueue commandQueue;

    private final MouseTracker mouseTracker;

    private final JXCWindowRenderer windowRenderer;

    private final Object semaphoreDrawing = new Object();

    private final Object semaphoreChangeGui = new Object();

    /**
     * The {@link KeyHandler} for processing keyboard input.
     */
    private final KeyHandler keyHandler;

    /**
     * The settings instance to use.
     */
    private final Settings settings;

    /**
     * The key bindings manager for this window.
     */
    private final KeybindingsManager keybindingsManager;

    /**
     * The shortcuts manager for this window.
     */
    private final ShortcutsManager shortcutsManager;

    /**
     * The current pickup mode. Set to <code>null</code> if no user is logged
     * in.
     */
    private final Pickup characterPickup;

    /**
     * The option manager for this window.
     */
    private final OptionManager optionManager;

    /**
     * The current spell manager instance for this window.
     */
    private final CurrentSpellManager currentSpellManager = new CurrentSpellManager();

    /**
     * The metaserver model instance for this window.
     */
    private final MetaserverModel metaserverModel;

    /**
     * The connection.
     */
    private final JXCConnection connection;

    /**
     * The size of the client area.
     */
    private final Resolution resolution;

    /**
     * Whether a server connection is active.
     */
    private boolean connected = false;

    /**
     * The synchronization object for accesses to {@link #connected}.
     */
    private final Object semaphoreConnected = new Object();

    /**
     * The {@link Macros} instance.
     */
    private final Macros macros;

    /**
     * The {@link WindowFocusListener} registered for this window. It resets
     * the keyboard modifier state when the window loses the focus. The idea is
     * to prevent the following: user switches from jxclient to another window
     * with CTRL+ALT+direction key. This makes jxclient enter RUN mode since
     * CTRL was pressed. The following key release event is not received by
     * jxclient because it does not own the focus. Therefore jxclient's CTRL
     * state is still active when the user switches back to jxclient. A
     * following direction key then causes the character to run which is not
     * what the player wants.
     */
    private final WindowFocusListener windowFocusListener = new WindowAdapter()
    {
        /** {@inheritDoc} */
        @Override
        public void windowLostFocus(final WindowEvent e)
        {
            keyHandler.reset();
            commandQueue.stopRunning();
        }
    };

    /**
     * The listener to detect a changed player name.
     */
    private final PlayerListener playerListener = new PlayerListener()
    {
        /** {@inheritDoc} */
        @Override
        public void playerReceived(final CfPlayer player)
        {
            guiManager.playerReceived();
            commandQueue.sendNcom(true, 1, "output-count 1"); // to make message merging work reliably
            characterPickup.update();                         // reset pickup mode
        }

        /** {@inheritDoc} */
        @Override
        public void playerAdded(final CfPlayer player)
        {
            connection.setCharacter(player.getName());
        }

        /** {@inheritDoc} */
        @Override
        public void playerRemoved(final CfPlayer player)
        {
            connection.setCharacter(null);
        }
    };

    /**
     * The {@link ClientSocketListener} used to detect connection state
     * changes.
     */
    private final ClientSocketListener clientSocketListener = new ClientSocketListener()
    {
        /** {@inheritDoc} */
        @Override
        public void connecting()
        {
            setConnected(true);
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void packetReceived(final byte[] buf, final int start, final int end) throws UnknownCommandException
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void packetSent(final byte[] buf, final int len)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void disconnecting(final String reason)
        {
            if (getGuiState() == GuiState.CONNECTING)
            {
                changeGUI(GuiState.CONNECT_FAILED, reason);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void disconnected(final String reason)
        {
            setConnected(false);
            if (getGuiState() != GuiState.CONNECT_FAILED)
            {
                changeGUI(GuiState.METASERVER);
            }
        }
    };

    /**
     * The {@link CrossfireServerConnectionListener} used to detect connection
     * progress changes.
     */
    private final CrossfireServerConnectionListener crossfireServerConnectionListener = new CrossfireServerConnectionListener()
    {
        /** {@inheritDoc} */
        @Override
        public void clientSocketStateChanged(final ClientSocketState clientSocketState)
        {
            for (final GuiStateListener listener : guiStateListeners)
            {
                listener.connecting(clientSocketState);
            }
            if (clientSocketState == ClientSocketState.CONNECTED)
            {
                changeGUI(GuiState.CONNECTED);
            }
        }
    };

    /**
     * The window listener attached to this frame.
     */
    private final WindowListener windowListener = new WindowAdapter()
    {
        /** {@inheritDoc} */
        @Override
        public void windowClosing(final WindowEvent e)
        {
            if (keybindingsManager.windowClosing())
            {
                guiManager.closeKeybindDialog();
            }

            if (!guiManager.openQuitDialog())
            {
                quitApplication();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void windowClosed(final WindowEvent e)
        {
            quitApplication();
        }
    };

    /**
     * The {@link KeyListener} attached to the main window.
     */
    private final KeyListener keyListener = new KeyListener()
    {
        /** {@inheritDoc} */
        @Override
        public void keyTyped(final KeyEvent e)
        {
            synchronized (semaphoreDrawing)
            {
                keyHandler.keyTyped(e);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void keyPressed(final KeyEvent e)
        {
            synchronized (semaphoreDrawing)
            {
                keyHandler.keyPressed(e);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void keyReleased(final KeyEvent e)
        {
            synchronized (semaphoreDrawing)
            {
                keyHandler.keyReleased(e);
            }
        }
    };

    /**
     * The {@link KeyHandlerListener} attached to {@link #keyHandler}.
     */
    private final KeyHandlerListener keyHandlerListener = new KeyHandlerListener()
    {
        /** {@inheritDoc} */
        @Override
        public void escPressed()
        {
            if (getGuiState() == GuiState.CONNECT_FAILED)
            {
                disconnect();
                return;
            }

            switch (guiManager.escPressed(keybindingsManager.escPressed(), isConnected()))
            {
            case 0:
                break;

            case 1:
                disconnect();
                break;

            case 2:
                quitApplication();
                break;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void keyReleased()
        {
            guiManager.closeKeybindDialog();
        }
    };

    /**
     * The {@link CrossfireQueryListener} attached to {@link #server}. It
     * parses query messages to open/close dialogs.
     */
    private final CrossfireQueryListener crossfireQueryListener = new CrossfireQueryListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandQueryReceived(final String prompt, final int queryType)
        {
            synchronized (semaphoreDrawing)
            {
                guiManager.openQueryDialog(prompt, queryType);
            }
        }
    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void start()
        {
            itemsManager.removeCrossfirePlayerListener(playerListener);
            server.removeCrossfireQueryListener(crossfireQueryListener);
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            itemsManager.removeCrossfirePlayerListener(playerListener);
            server.removeCrossfireQueryListener(crossfireQueryListener);
        }

        /** {@inheritDoc} */
        @Override
        public void connecting()
        {
            itemsManager.addCrossfirePlayerListener(playerListener);
            server.addCrossfireQueryListener(crossfireQueryListener);
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(final ClientSocketState clientSocketState)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(final String reason)
        {
            // ignore
        }
    };

    /**
     * Create a new instance.
     *
     * @param terminateSync Object to be notified when the application
     * terminates
     *
     * @param server the crossfire server connection to use
     *
     * @param semaphoreRedraw the semaphore used to synchronized map model
     * updates and map view redraws
     *
     * @param debugGui Whether GUI elements should be highlighted.
     *
     * @param debugKeyboard If non-<code>null</code>, write all keyboard debug
     * to this writer.
     *
     * @param settings The settings instance to use.
     *
     * @param optionManager the option manager instance to use
     *
     * @param metaserverModel the metaserver model to use
     *
     * @param resolution the size of the client area
     *
     * @throws IOException if a resource cannot be loaded
     */
    public JXCWindow(final Object terminateSync, final CrossfireServerConnection server, final Object semaphoreRedraw, final boolean debugGui, final Writer debugKeyboard, final Settings settings, final OptionManager optionManager, final MetaserverModel metaserverModel, final Resolution resolution) throws IOException
    {
        super("");
        this.terminateSync = terminateSync;
        this.server = server;
        this.debugGui = debugGui;
        this.settings = settings;
        this.optionManager = optionManager;
        this.metaserverModel = metaserverModel;
        this.resolution = resolution;
        macros = new Macros(server);
        final FaceCache faceCache = new FaceCache(server);
        experienceTable = new ExperienceTable(server);
        skillSet = new SkillSet(server, this);
        stats = new Stats(server, experienceTable, skillSet, this);
        itemsManager = new ItemsManager(server, faceCache, stats, skillSet, this);
        facesManager = new FacesManager(server, new FileCache(Filenames.getOriginalImageCacheDir()), new FileCache(Filenames.getScaledImageCacheDir()), new FileCache(Filenames.getMagicMapImageCacheDir()), faceCache, this);
        mapUpdater = new CfMapUpdater(server, facesManager, faceCache, animations, this);
        spellsManager = new SpellsManager(server, this);
        commandQueue = new CommandQueue(server, this);
        new PoisonWatcher(stats, server);
        new ActiveSkillWatcher(stats, server);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mouseTracker = new MouseTracker(debugGui);
        windowRenderer = new JXCWindowRenderer(this, mouseTracker, semaphoreRedraw, server);
        guiManager = new GuiManager(this, semaphoreDrawing, new TooltipManager(this), settings, server, macros, windowRenderer);
        mouseTracker.init(windowRenderer);
        final ScriptManager scriptManager = new ScriptManager(commandQueue, server, stats, itemsManager, spellsManager, mapUpdater, skillSet);
        guiManager.init(this, scriptManager, commandQueue, server, optionManager, debugGui ? mouseTracker : null);
        keybindingsManager = new KeybindingsManager(guiManager.getCommands(), guiManager, macros);
        shortcutsManager = new ShortcutsManager(commandQueue, spellsManager);
        keyHandler = new KeyHandler(debugKeyboard, keybindingsManager, commandQueue, windowRenderer, keyHandlerListener);
        try
        {
            characterPickup = new Pickup(commandQueue, optionManager);
        }
        catch (final OptionException ex)
        {
            throw new AssertionError();
        }
        setFocusTraversalKeysEnabled(false);
        addWindowFocusListener(windowFocusListener);
        addWindowListener(windowListener);
        connection = new JXCConnection(keybindingsManager, shortcutsManager, settings, this, characterPickup, server, guiManager);
        server.addClientSocketListener(clientSocketListener);
        server.addCrossfireServerConnectionListener(crossfireServerConnectionListener);
        guiManager.setConnection(connection);
        addConnectionStateListener(guiStateListener);
    }

    public boolean createKeyBinding(final boolean perCharacter, final GUICommandList cmdlist)
    {
        final boolean result = keybindingsManager.createKeyBinding(perCharacter, cmdlist);
        if (result)
        {
            guiManager.openKeybindDialog();
        }
        return result;
    }

    public boolean removeKeyBinding(final boolean perCharacter)
    {
        final boolean result = keybindingsManager.removeKeyBinding(perCharacter);
        if (result)
        {
            guiManager.openKeybindDialog();
        }
        return result;
    }

    public void quitApplication()
    {
        guiManager.terminate();
        synchronized (terminateSync)
        {
            terminateSync.notifyAll();
        }
    }

    public void changeGUI(final GuiState guiState)
    {
        changeGUI(guiState, null);
    }

    private void changeGUI(final GuiState guiState, final String param)
    {
        synchronized (semaphoreChangeGui)
        {
            if (this.guiState == guiState)
            {
                return;
            }

            guiManager.closeTransientDialogs();

            this.guiState = guiState;

            switch (guiState)
            {
            case START:
                for (final GuiStateListener listener : guiStateListeners)
                {
                    listener.start();
                }
                if (DISABLE_START_GUI)
                {
                    quitApplication();
                }
                break;

            case METASERVER:
                for (final GuiStateListener listener : guiStateListeners)
                {
                    listener.metaserver();
                }
                break;

            case CONNECTING:
                for (final GuiStateListener listener : guiStateListeners)
                {
                    listener.connecting();
                }
                break;

            case CONNECTED:
                for (final GuiStateListener listener : guiStateListeners)
                {
                    listener.connected();
                }
                break;

            case CONNECT_FAILED:
                for (final GuiStateListener listener : guiStateListeners)
                {
                    listener.connectFailed(param);
                }
                break;
            }
        }
    }

    /**
     * Returns the current {@link GuiState}.
     * @return the gui state
     */
    private GuiState getGuiState()
    {
        synchronized (semaphoreChangeGui)
        {
            return guiState;
        }
    }

    public void init(final String skinName, final boolean fullScreen, final String serverInfo)
    {
        addKeyListener(keyListener);
        if (!setSkin(skinName))
        {
            if (skinName.equals(Options.DEFAULT_SKIN))
            {
                System.exit(1);
            }

            System.err.println("trying to load default skin "+Options.DEFAULT_SKIN);
            if (!setSkin(Options.DEFAULT_SKIN))
            {
                System.exit(1);
                throw new AssertionError();
            }
        }
        guiManager.initRendering(fullScreen);
        keybindingsManager.loadKeybindings();

        if (serverInfo != null)
        {
            connect(serverInfo);
        }
        else
        {
            changeGUI(DISABLE_START_GUI ? GuiState.METASERVER : GuiState.START);
        }
        addMouseListener(mouseTracker);
        addMouseMotionListener(mouseTracker);
        guiManager.init3();
    }

    /**
     * Frees all resources. Should be called before the application terminates.
     */
    public void term()
    {
        guiManager.term();
        keybindingsManager.saveKeybindings();
        optionManager.saveOptions();
    }

    public void connect(final String serverInfo)
    {
        settings.putString("server", serverInfo);
        connection.setHost(serverInfo);
        changeGUI(GuiState.CONNECTING);
    }

    public void disconnect()
    {
        changeGUI(GuiState.METASERVER);
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final Graphics g)
    {
        windowRenderer.repaint();
    }

    /**
     * Set the skin to use.
     *
     * @param skinName The skin name to set.
     *
     * @return Whether loading was successful.
     */
    private boolean setSkin(final String skinName)
    {
        guiManager.unsetSkin();
        final JXCSkin skin;
        try
        {
            skin = loadSkin(skinName);
        }
        catch (final JXCSkinException ex)
        {
            System.err.println("cannot load skin "+skinName+": "+ex.getMessage());
            return false;
        }
        guiManager.setSkin(skin);
        optionManager.loadOptions();
        keyHandler.setKeyBindings(skin.getDefaultKeyBindings());
        return true;
    }

    /**
     * Loads a skin file.
     * @param skinName the skin file name
     * @return the loaded skin
     * @throws JXCSkinException if the skin file cannot be loaded
     */
    private JXCSkin loadSkin(final String skinName) throws JXCSkinException
    {
        // check for skin in directory
        final File dir = new File(skinName);
        final KeyBindings defaultKeyBindings = new KeyBindings(null, guiManager.getCommands(), guiManager, macros);
        final JXCSkinSource skinSource;
        if (dir.exists() && dir.isDirectory())
        {
            skinSource = new JXCSkinDirSource(dir);
        }
        else
        {
            // fallback: built-in resource
            skinSource = new JXCSkinClassSource("com/realtime/crossfire/jxclient/skins/"+skinName);
        }
        final JXCSkinLoader newSkin = new JXCSkinLoader(itemsManager, spellsManager, facesManager, stats, mapUpdater, defaultKeyBindings, optionManager, experienceTable, skillSet);
        return newSkin.load(skinSource, server, this, guiManager.getTooltipManager(), windowRenderer, mouseTracker, metaserverModel, commandQueue, resolution, shortcutsManager.getShortcuts(), guiManager.getCommands(), currentSpellManager, guiManager, debugGui, macros);
    }

    /**
     * Return the width of the client area.
     *
     * @return The width of the client area.
     */
    public int getWindowWidth()
    {
        return resolution.getWidth();
    }

    /**
     * Return the height of the client area.
     *
     * @return The height of the client area.
     */
    public int getWindowHeight()
    {
        return resolution.getHeight();
    }

    /**
     * Records whether a server connection is active.
     * @param connected whether a server connection is active
     */
    private void setConnected(final boolean connected)
    {
        synchronized (semaphoreConnected)
        {
            this.connected = connected;
        }
    }

    /**
     * Returns whether a server connection is active.
     * @return whether a server connection is active
     */
    private boolean isConnected()
    {
        synchronized (semaphoreConnected)
        {
            return connected;
        }
    }

    public void sendReply(final String reply)
    {
        server.sendReply(reply);
        guiManager.closeQueryDialog();
    }

    /**
     * Add a connection listener.
     *
     * @param listener The listener to add.
     */
    public void addConnectionStateListener(final GuiStateListener listener)
    {
        guiStateListeners.add(listener);
    }

    /**
     * Remove a connection listener.
     *
     * @param listener The listener to remove.
     */
    public void removeConnectionStateListener(final GuiStateListener listener)
    {
        guiStateListeners.remove(listener);
    }

    @Deprecated
    public Stats getStats()
    {
        return stats;
    }

    @Deprecated
    public GuiManager getGuiManager()
    {
        return guiManager;
    }

    @Deprecated
    public ItemsManager getItemsManager()
    {
        return itemsManager;
    }
}

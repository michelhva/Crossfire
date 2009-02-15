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
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.scripts.ScriptManager;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.ConnectionListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.DefaultCrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.Pickup;
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
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.sound.Sounds;
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

    private final CrossfireServerConnection server;

    private final Animations animations = new Animations(this);

    /**
     * The command queue instance for this window.
     */
    private final CommandQueue commandQueue;

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
     * The {@link SoundManager} instance.
     */
    private final SoundManager soundManager;

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
     * The semaphore used to synchronized map model updates and map view
     * redraws.
     */
    private final Object semaphoreRedraw = new Object();

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
    private final MetaserverModel metaserverModel = new MetaserverModel();

    /**
     * The metaserver instance for this window.
     */
    private final Metaserver metaserver = new Metaserver(Filenames.getMetaserverCacheFile(), metaserverModel, this);

    /**
     * The connection.
     */
    private final JXCConnection connection;

    /**
     * The size of the client area.
     */
    private Resolution resolution = null;

    private ConnectionStatus status = ConnectionStatus.UNCONNECTED;

    private final Object semaphoreStatus = new Object();

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

    private final ConnectionListener connectionListener = new ConnectionListener()
    {
        /** {@inheritDoc} */
        @Override
        public void connectionLost()
        {
            setStatus(ConnectionStatus.UNCONNECTED);
            changeGUI(GuiState.METASERVER);
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

            if(!guiManager.openQuitDialog())
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
                keyHandler.keyTyped(e, getStatus());
            }
        }

        /** {@inheritDoc} */
        @Override
        public void keyPressed(final KeyEvent e)
        {
            synchronized (semaphoreDrawing)
            {
                keyHandler.keyPressed(e, getStatus());
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
            switch (guiManager.escPressed(keybindingsManager.escPressed(), status))
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
                setStatus(ConnectionStatus.QUERY);
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
            server.removeCrossfireQueryListener(crossfireQueryListener);
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            server.removeCrossfireQueryListener(crossfireQueryListener);
        }

        /** {@inheritDoc} */
        @Override
        public void main()
        {
            server.addCrossfireQueryListener(crossfireQueryListener);
        }
    };

    /**
     * Create a new instance.
     *
     * @param terminateSync Object to be notified when the application
     * terminates
     *
     * @param debugGui Whether GUI elements should be highlighted.
     *
     * @param debugProtocol If non-<code>null</code>, write all protocol
     * commands to this appender.
     *
     * @param debugKeyboard If non-<code>null</code>, write all keyboard debug
     * to this writer.
     *
     * @param settings The settings instance to use.
     *
     * @param soundManager the sound manager instance to use
     *
     * @param optionManager the option manager instance to use
     *
     * @throws IOException if a resource cannot be loaded
     */
    public JXCWindow(final Object terminateSync, final boolean debugGui, final Writer debugProtocol, final Writer debugKeyboard, final Settings settings, final SoundManager soundManager, final OptionManager optionManager) throws IOException
    {
        super("");
        this.terminateSync = terminateSync;
        this.debugGui = debugGui;
        this.settings = settings;
        this.soundManager = soundManager;
        this.optionManager = optionManager;
        server = new DefaultCrossfireServerConnection(semaphoreRedraw, debugProtocol);
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
        guiManager = new GuiManager(this, debugGui, semaphoreDrawing, semaphoreRedraw, new TooltipManager(this), settings, server);
        final ScriptManager scriptManager = new ScriptManager(commandQueue, server, stats, itemsManager, spellsManager, mapUpdater, skillSet);
        guiManager.init(scriptManager, commandQueue, server, optionManager);
        keybindingsManager = new KeybindingsManager(guiManager.getCommands(), guiManager);
        shortcutsManager = new ShortcutsManager(commandQueue, spellsManager);
        keyHandler = new KeyHandler(debugKeyboard, keybindingsManager, commandQueue, guiManager.getWindowRenderer(), keyHandlerListener);
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
        connection = new JXCConnection(keybindingsManager, settings, this, characterPickup, server, connectionListener, guiManager);
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

    private void initRendering(final boolean fullScreen)
    {
        guiManager.initRendering(fullScreen);
        keybindingsManager.loadKeybindings();
        shortcutsManager.loadShortcuts();
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
                soundManager.muteMusic(true);
                soundManager.mute(Sounds.CHARACTER, true);
                itemsManager.removeCrossfirePlayerListener(playerListener);
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
                soundManager.muteMusic(true);
                soundManager.mute(Sounds.CHARACTER, true);
                itemsManager.removeCrossfirePlayerListener(playerListener);
                for (final GuiStateListener listener : guiStateListeners)
                {
                    listener.metaserver();
                }
                break;

            case MAIN:
                soundManager.muteMusic(false);
                soundManager.mute(Sounds.CHARACTER, false);
                itemsManager.addCrossfirePlayerListener(playerListener);
                for (final GuiStateListener listener : guiStateListeners)
                {
                    listener.main();
                }
                break;
            }
        }
    }

    public void init(final Resolution resolution, final String skinName, final boolean fullScreen, final String serverInfo)
    {
        this.resolution = resolution;
        addKeyListener(keyListener);
        guiManager.init2(this);
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
        initRendering(fullScreen);

        if (serverInfo != null)
        {
            connect(serverInfo);
        }
        else
        {
            changeGUI(DISABLE_START_GUI ? GuiState.METASERVER : GuiState.START);
        }
        guiManager.init3();
    }

    /**
     * Frees all resources. Should be called before the application terminates.
     */
    public void term()
    {
        guiManager.term();
        shortcutsManager.saveShortcuts();
        keybindingsManager.saveKeybindings();
        optionManager.saveOptions();
    }

    public void connect(final String serverInfo)
    {
        settings.putString("server", serverInfo);
        connection.setHost(serverInfo);
        changeGUI(GuiState.MAIN);
    }

    public void disconnect()
    {
        changeGUI(GuiState.METASERVER);
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final Graphics g)
    {
        guiManager.repaint();
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
        skin.attach(guiManager);
        guiManager.setSkin(skin);
        optionManager.loadOptions();
        keyHandler.setKeyBindings(guiManager.getSkin().getDefaultKeyBindings());
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
        final KeyBindings defaultKeyBindings = new KeyBindings(null, guiManager.getCommands(), guiManager);
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
        return newSkin.load(skinSource, server, this, guiManager.getTooltipManager(), guiManager.getWindowRenderer(), guiManager.mouseTracker, metaserverModel, commandQueue, resolution, shortcutsManager.getShortcuts(), guiManager.getCommands(), currentSpellManager, guiManager, debugGui);
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
     * Set the current player name. Does nothing if not currently in the
     * character name prompt.
     *
     * @param playerName The player name.
     */
    public void updatePlayerName(final String playerName)
    {
        guiManager.updatePlayerName(playerName);
    }

    /**
     * Sets the current status of the client to the given value. See the various
     * STATUS_ constants.
     * @param status The new status value.
     * @since 1.0
     */
    public void setStatus(final ConnectionStatus status)
    {
        synchronized (semaphoreStatus)
        {
            this.status = status;
        }
    }

    /**
     * Gets the current status of the client. See the STATUS_ constants.
     * @since 1.0
     * @return A value representing the current status.
     */
    public ConnectionStatus getStatus()
    {
        synchronized (semaphoreStatus)
        {
            return status;
        }
    }

    public void sendReply(final String reply)
    {
        setStatus(ConnectionStatus.PLAYING);
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
    public CrossfireServerConnection getCrossfireServerConnection()
    {
        return server;
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

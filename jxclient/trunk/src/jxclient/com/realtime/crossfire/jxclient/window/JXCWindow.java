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
import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.faces.FaceCache;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FileCache;
import com.realtime.crossfire.jxclient.gui.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.GUIOneLineLabel;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.list.GUIMetaElementList;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.items.PlayerListener;
import com.realtime.crossfire.jxclient.main.Options;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.ConnectionListener;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawextinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandQueryEvent;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import com.realtime.crossfire.jxclient.server.Pickup;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.JXCSkinClassLoader;
import com.realtime.crossfire.jxclient.skin.JXCSkinDirLoader;
import com.realtime.crossfire.jxclient.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.skin.Resolution;
import com.realtime.crossfire.jxclient.sound.MusicWatcher;
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.sound.SoundWatcher;
import com.realtime.crossfire.jxclient.sound.Sounds;
import com.realtime.crossfire.jxclient.sound.StatsWatcher;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.ActiveSkillWatcher;
import com.realtime.crossfire.jxclient.stats.PoisonWatcher;
import com.realtime.crossfire.jxclient.stats.Stats;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.Timer;

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

    public static final int GUI_START      = 0;
    public static final int GUI_METASERVER = 1;
    public static final int GUI_MAIN       = 2;

    /**
     * The connection state listeners to notify.
     */
    private final List<ConnectionStateListener> connectionStateListeners = new ArrayList<ConnectionStateListener>();

    private int guiId = -1;

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
     * The {@link CfMapUpdater} instance.
     */
    private final CfMapUpdater mapUpdater;

    /**
     * The global experience table.
     */
    private final ExperienceTable experienceTable;

    private final CrossfireServerConnection server;

    private final Animations animations = new Animations();

    /**
     * The command queue instance for this window.
     */
    private final CommandQueue commandQueue;

    private final Object semaphoreDrawing = new Object();

    private final Object semaphoreChangeGui = new Object();

    /**
     * The shortcuts for this window.
     */
    private final Shortcuts shortcuts;

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

    private Gui queryDialog;

    private Gui keybindDialog;

    /**
     * The "really quit?" dialog. Set to <code>null</code> if the skin does not
     * define this dialog.
     */
    private Gui dialogQuit = null;

    /**
     * The "really disconnect?" dialog. Set to <code>null</code> if the skin
     * does not define this dialog.
     */
    private Gui dialogDisconnect = null;

    private JXCSkin skin = null;

    /**
     * The key bindings manager for this window.
     */
    private final KeybindingsManager keybindingsManager;

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

    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link TooltipManager} for this window.
     */
    private final TooltipManager tooltipManager = new TooltipManager(this);

    /**
     * The option manager for this window.
     */
    private final OptionManager optionManager;

    /**
     * The commands instance for this window.
     */
    private final Commands commands;

    /**
     * The current spell manager instance for this window.
     */
    private final CurrentSpellManager currentSpellManager = new CurrentSpellManager();

    /**
     * The metaserver instance for this window.
     */
    private final Metaserver metaserver = new Metaserver(Filenames.getMetaserverCacheFile());

    /**
     * The mouse tracker.
     */
    private final MouseTracker mouseTracker;

    /**
     * The connection.
     */
    private final JXCConnection connection;

    /**
     * The size of the client area.
     */
    private Resolution resolution = null;

    /**
     * Whether the currently shown query dialog is the character name prompt.
     */
    private boolean currentQueryDialogIsNamePrompt = false;

    /**
     * Called periodically to update the display contents.
     */
    private final ActionListener actionListener = new ActionListener()
    {
        /** {@inheritDoc} */
        public void actionPerformed(final ActionEvent e)
        {
            synchronized (semaphoreDrawing)
            {
                windowRenderer.redrawGUI();
            }
        }
    };

    /**
     * The timer used to update the display contents.
     */
    private final Timer timer = new Timer(10, actionListener);

    public enum Status
    {
        /**
         * Represents the unconnected status of the client, which is the first to
         * happen during a normal gaming session.
         * @since 1.0
         */
        UNCONNECTED,

        /**
         * Represents the status of the client that is used during play.
         * @since 1.0
         */
        PLAYING,

        /**
         * Represents the status of the client that is displaying a Query dialog.
         * @since 1.0
         */
        QUERY;
    }

    private Status status = Status.UNCONNECTED;

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
        public void playerReceived(final CfPlayer player)
        {
            if (windowRenderer.getGuiState() == JXCWindowRenderer.GuiState.NEWCHAR)
            {
                openDialogByName("messages"); // hack for race selection
            }
            windowRenderer.setGuiState(JXCWindowRenderer.GuiState.PLAYING);
            commandQueue.sendNcom(true, 1, "output-count 1"); // to make message merging work reliably
            characterPickup.update();                         // reset pickup mode
        }

        /** {@inheritDoc} */
        public void playerAdded(final CfPlayer player)
        {
            connection.setCharacter(player.getName());
        }

        /** {@inheritDoc} */
        public void playerRemoved(final CfPlayer player)
        {
            connection.setCharacter(null);
        }
    };

    private final ConnectionListener connectionListener = new ConnectionListener()
    {
        /** {@inheritDoc} */
        public void connectionLost()
        {
            setStatus(Status.UNCONNECTED);
            changeGUI(GUI_METASERVER);
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
                closeKeybindDialog();
            }

            if (dialogQuit == null)
            {
                quitApplication();
            }
            else
            {
                windowRenderer.closeDialog(dialogDisconnect);
                windowRenderer.openDialog(dialogQuit);
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
        public void keyTyped(final KeyEvent e)
        {
            synchronized (semaphoreDrawing)
            {
                keyHandler.keyTyped(e, getStatus());
            }
        }

        /** {@inheritDoc} */
        public void keyPressed(final KeyEvent e)
        {
            synchronized (semaphoreDrawing)
            {
                keyHandler.keyPressed(e, getStatus());
            }
        }

        /** {@inheritDoc} */
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
        public void escPressed()
        {
            if (keybindingsManager.escPressed())
            {
                windowRenderer.closeDialog(keybindDialog);
            }
            else if (deactivateCommandInput())
            {
                // ignore
            }
            else if (status != JXCWindow.Status.UNCONNECTED)
            {
                if (dialogDisconnect == null)
                {
                    disconnect();
                }
                else if (windowRenderer.openDialog(dialogDisconnect))
                {
                    if (dialogQuit != null)
                    {
                        windowRenderer.closeDialog(dialogQuit);
                    }
                }
                else
                {
                    windowRenderer.closeDialog(dialogDisconnect);
                }
            }
            else
            {
                if (dialogQuit == null)
                {
                    quitApplication();
                }
                else if (windowRenderer.openDialog(dialogQuit))
                {
                    if (dialogDisconnect != null)
                    {
                        windowRenderer.closeDialog(dialogDisconnect);
                    }
                }
                else
                {
                    windowRenderer.closeDialog(dialogQuit);
                }
            }
        }

        /** {@inheritDoc} */
        public void keyReleased()
        {
            closeKeybindDialog();
        }
    };

    /**
     * The {@link CrossfireQueryListener} attached to {@link #server}. It
     * parses query messages to open/close dialogs.
     */
    private final CrossfireQueryListener crossfireQueryListener = new CrossfireQueryListener()
    {
        /** {@inheritDoc} */
        public void commandQueryReceived(final CrossfireCommandQueryEvent evt)
        {
            synchronized (semaphoreDrawing)
            {
                setStatus(Status.QUERY);
                windowRenderer.openDialog(queryDialog);
                queryDialog.setHideInput((evt.getQueryType()&CrossfireCommandQueryEvent.HIDEINPUT) != 0);

                currentQueryDialogIsNamePrompt = evt.getPrompt().startsWith("What is your name?");
                if (currentQueryDialogIsNamePrompt)
                {
                    final String playerName = settings.getString("player_"+connection.getHostname(), "");
                    if (playerName.length() > 0)
                    {
                        final GUIText textArea = queryDialog.getFirstTextArea();
                        if (textArea != null)
                        {
                            textArea.setText(playerName);
                        }
                    }
                }
                else if (evt.getPrompt().startsWith("[y] to roll new stats")
                || evt.getPrompt().startsWith("Welcome, Brave New Warrior!"))
                {
                    windowRenderer.setGuiState(JXCWindowRenderer.GuiState.NEWCHAR);
                    if (openDialogByName("newchar"))
                    {
                        closeDialogByName("messages");
                        closeDialogByName("status");
                    }
                    else
                    {
                        // fallback: open both message and status dialogs if this skin
                        // does not define a login dialog
                        openDialogByName("messages");
                        openDialogByName("status");
                    }
                    openDialog(queryDialog); // raise dialog
                }
            }
        }
    };

    /**
     * The {@link CrossfireDrawextinfoListener} attached to {@link #server}.
     */
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener()
    {
        /** {@inheritDoc} */
        public void commandDrawextinfoReceived(final CrossfireCommandDrawextinfoEvent evt)
        {
            String message = evt.getMessage();

            final Gui dialog;
            switch (evt.getType())
            {
            case MessageTypes.MSG_TYPE_BOOK:
                dialog = skin.getDialogBook(1);
                final GUIOneLineLabel title = dialog.getDialogTitle();
                if (title != null)
                {
                    final String[] tmp = message.split("\n", 2);
                    title.setText(tmp[0]);
                    message = tmp.length >= 2 ? tmp[1] : "";
                }
                break;

            case MessageTypes.MSG_TYPE_CARD:
            case MessageTypes.MSG_TYPE_PAPER:
            case MessageTypes.MSG_TYPE_SIGN:
            case MessageTypes.MSG_TYPE_MONUMENT:
            case MessageTypes.MSG_TYPE_DIALOG:
                dialog = null;
                break;

            case MessageTypes.MSG_TYPE_MOTD:
                /*
                 * We do not display a MOTD dialog, because it interferes with the
                 * query dialog that gets displayed just after it.
                 */
                dialog = null;
                break;

            case MessageTypes.MSG_TYPE_ADMIN:
            case MessageTypes.MSG_TYPE_SHOP:
            case MessageTypes.MSG_TYPE_COMMAND:
            case MessageTypes.MSG_TYPE_ATTRIBUTE:
            case MessageTypes.MSG_TYPE_SKILL:
            case MessageTypes.MSG_TYPE_APPLY:
            case MessageTypes.MSG_TYPE_ATTACK:
            case MessageTypes.MSG_TYPE_COMMUNICATION:
            case MessageTypes.MSG_TYPE_SPELL:
            case MessageTypes.MSG_TYPE_ITEM:
            case MessageTypes.MSG_TYPE_MISC:
            case MessageTypes.MSG_TYPE_VICTIM:
                dialog = null;
                break;

            default:
                dialog = null;
                break;
            }

            if (dialog == null)
            {
                return;
            }

            final AbstractLabel label = dialog.getFirstLabel();
            if (label != null)
            {
                label.setText(message);
            }
            else
            {
                final GUILabelLog log = dialog.getFirstLabelLog();
                if (log != null)
                {
                    log.updateText(message);
                }
            }
            windowRenderer.openDialog(dialog);
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
        server = new CrossfireServerConnection(semaphoreRedraw, debugProtocol);
        final FaceCache faceCache = new FaceCache(server);
        experienceTable = new ExperienceTable(server);
        stats = new Stats(server, experienceTable);
        itemsManager = new ItemsManager(server, faceCache, stats);
        facesManager = new FacesManager(server, new FileCache(Filenames.getOriginalImageCacheDir()), new FileCache(Filenames.getScaledImageCacheDir()), new FileCache(Filenames.getMagicMapImageCacheDir()), faceCache);
        mapUpdater = new CfMapUpdater(server, facesManager, faceCache, animations);
        spellsManager = new SpellsManager(server);
        commandQueue = new CommandQueue(server);
        shortcuts = new Shortcuts(commandQueue, spellsManager);
        new PoisonWatcher(stats, server);
        new ActiveSkillWatcher(stats, server);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mouseTracker = new MouseTracker(debugGui);
        windowRenderer = new JXCWindowRenderer(this, mouseTracker, semaphoreRedraw);
        mouseTracker.init(windowRenderer);
        commands = new Commands(this, windowRenderer, commandQueue, server, stats, optionManager);
        windowRenderer.init(commands);
        queryDialog = new Gui(this, mouseTracker, commands);
        keybindDialog = new Gui(this, mouseTracker, commands);
        keybindingsManager = new KeybindingsManager(commands, this);
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
        connection = new JXCConnection(keybindingsManager, settings, this, characterPickup, server);
    }

    public static boolean checkFire()
    {
        return false;
    }

    public boolean createKeyBinding(final boolean perCharacter, final GUICommandList cmdlist)
    {
        final boolean result = keybindingsManager.createKeyBinding(perCharacter, cmdlist);
        if (result)
        {
            openKeybindDialog();
        }
        return result;
    }

    public boolean removeKeyBinding(final boolean perCharacter)
    {
        final boolean result = keybindingsManager.removeKeyBinding(perCharacter);
        if (result)
        {
            openKeybindDialog();
        }
        return result;
    }

    /**
     * Load shortcut info from the backing file.
     */
    private void loadShortcuts()
    {
        final File file;
        try
        {
            file = Filenames.getShortcutsFile();
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read shortcuts file: "+ex.getMessage());
            return;
        }

        try
        {
            shortcuts.load(file);
        }
        catch (final FileNotFoundException ex)
        {
            return;
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read shortcuts file "+file+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Save all shortcut info to the backing file.
     */
    private void saveShortcuts()
    {
        final File file;
        try
        {
            file = Filenames.getShortcutsFile();
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write shortcuts file: "+ex.getMessage());
            return;
        }

        try
        {
            shortcuts.save(file);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write shortcuts file "+file+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Open a dialog. Raises an already opened dialog.
     *
     * @param dialog The dialog to show.
     */
    public void openDialog(final Gui dialog)
    {
        windowRenderer.openDialog(dialog);
        if (dialog == queryDialog)
        {
            dialog.setHideInput(false);
        }
    }

    /**
     * Toggle a dialog.
     *
     * @param dialog The dialog to toggle.
     */
    public void toggleDialog(final Gui dialog)
    {
        if (windowRenderer.toggleDialog(dialog))
        {
            if (dialog == queryDialog)
            {
                dialog.setHideInput(false);
            }
        }
    }

    /**
     * Close the "query" dialog. Does nothing if the dialog is not open.
     */
    public void closeQueryDialog()
    {
        windowRenderer.closeDialog(queryDialog);
    }

    private void initRendering(final boolean fullScreen)
    {
        windowRenderer.initRendering(fullScreen);
        DialogStateParser.load(skin, windowRenderer);
        keybindingsManager.loadKeybindings();
        loadShortcuts();
    }

    /**
     * Opens a dialog by name.
     * @param name the dialog name
     * @return whether the dialog exists
     */
    private boolean openDialogByName(final String name)
    {
        final Gui dialog = skin.getDialog(name);
        if (dialog == null)
        {
            return false;
        }

        openDialog(dialog);
        return true;
    }

    /**
     * Closes a dialog by name.
     * @param name the dialog name
     */
    private void closeDialogByName(final String name)
    {
        final Gui dialog = skin.getDialog(name);
        if (dialog != null)
        {
            windowRenderer.closeDialog(dialog);
        }
    }

    public void quitApplication()
    {
        timer.stop();
        synchronized (terminateSync)
        {
            terminateSync.notifyAll();
        }
    }

    public void changeGUI(final int guiId)
    {
        synchronized (semaphoreChangeGui)
        {
            if (this.guiId == guiId)
            {
                return;
            }

            if (this.guiId == GUI_MAIN)
            {
                connection.disconnect();
                itemsManager.removeCrossfirePlayerListener(playerListener);
                itemsManager.reset();
                mapUpdater.reset();
                for (final ConnectionStateListener listener : connectionStateListeners)
                {
                    listener.disconnect();
                }
            }

            this.guiId = guiId;

            if (this.guiId == GUI_MAIN)
            {
                soundManager.mute(Sounds.CHARACTER, false);
                itemsManager.addCrossfirePlayerListener(playerListener);
                stats.reset();
                SkillSet.clearNumberedSkills();
                connection.connect(connectionListener, crossfireQueryListener, crossfireDrawextinfoListener, skin.getMapWidth(), skin.getMapHeight(), skin.getNumLookObjects());
                facesManager.reset();
                commandQueue.clear();
                itemsManager.reset();
                spellsManager.reset();
                animations.reset();
                mapUpdater.reset();
                for (final ConnectionStateListener listener : connectionStateListeners)
                {
                    listener.connect();
                }
            }

            if (dialogDisconnect != null)
            {
                windowRenderer.closeDialog(dialogDisconnect);
            }
            if (dialogQuit != null)
            {
                windowRenderer.closeDialog(dialogQuit);
            }
            windowRenderer.closeDialog(queryDialog);
            windowRenderer.closeDialog(skin.getDialogBook(1));

            switch (guiId)
            {
            case GUI_START:
                soundManager.muteMusic(true);
                soundManager.mute(Sounds.CHARACTER, true);
                windowRenderer.setGuiState(JXCWindowRenderer.GuiState.START);
                if (DISABLE_START_GUI)
                {
                    quitApplication();
                }
                else
                {
                    showGUIStart();
                }
                break;

            case GUI_METASERVER:
                soundManager.muteMusic(true);
                soundManager.mute(Sounds.CHARACTER, true);
                windowRenderer.setGuiState(JXCWindowRenderer.GuiState.META);
                showGUIMeta();
                metaserver.query();

                final String serverName = settings.getString("server", "crossfire.metalforge.net");
                if (serverName.length() > 0)
                {
                    final GUIMetaElementList metaElementList = windowRenderer.getCurrentGui().getMetaElementList();
                    if (metaElementList != null)
                    {
                        metaElementList.setSelectedHostname(serverName);
                    }
                }
                break;

            case GUI_MAIN:
                soundManager.muteMusic(false);
                windowRenderer.setGuiState(JXCWindowRenderer.GuiState.LOGIN);
                showGUIMain();
                break;
            }
        }
    }

    public void init(final Resolution resolution, final String skinName, final boolean fullScreen, final String serverInfo)
    {
        new MusicWatcher(server, soundManager);
        new SoundWatcher(server, soundManager);
        new StatsWatcher(stats, windowRenderer, itemsManager, soundManager);
        this.resolution = resolution;
        addKeyListener(keyListener);
        addMouseListener(mouseTracker);
        addMouseMotionListener(mouseTracker);
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
        windowRenderer.init(skin.getResolution());
        initRendering(fullScreen);

        if (serverInfo != null)
        {
            connect(serverInfo);
        }
        else
        {
            changeGUI(DISABLE_START_GUI ? GUI_METASERVER : GUI_START);
        }
        timer.start();
    }

    /**
     * Frees all resources. Should be called before the application terminates.
     */
    public void term()
    {
        windowRenderer.endRendering();
        saveShortcuts();
        keybindingsManager.saveKeybindings();
        DialogStateParser.save(skin, windowRenderer);
        optionManager.saveOptions();
        soundManager.shutdown();
    }

    public void connect(final String serverInfo)
    {
        settings.putString("server", serverInfo);
        connection.setHost(serverInfo);
        changeGUI(GUI_MAIN);
    }

    public void disconnect()
    {
        changeGUI(GUI_METASERVER);
    }

    private void showGUIStart()
    {
        windowRenderer.clearGUI();
        windowRenderer.setCurrentGui(skin.getStartInterface());
        tooltipManager.reset();
    }

    private void showGUIMeta()
    {
        windowRenderer.clearGUI();
        final Gui newGui = skin.getMetaInterface();
        windowRenderer.setCurrentGui(newGui);
        newGui.activateDefaultElement();
        tooltipManager.reset();
    }

    private void showGUIMain()
    {
        windowRenderer.clearGUI();
        final Gui newGui = skin.getMainInterface();
        windowRenderer.setCurrentGui(newGui);
        tooltipManager.reset();
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
        try
        {
            skin = loadSkin(skinName);
        }
        catch (final JXCSkinException ex)
        {
            System.err.println("cannot load skin "+skinName+": "+ex.getMessage());
            return false;
        }

        skin.executeInitEvents();
        queryDialog = skin.getDialogQuery();
        keybindDialog = skin.getDialogKeyBind();
        dialogQuit = skin.getDialogQuit();
        dialogDisconnect = skin.getDialogDisconnect();
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
        final KeyBindings defaultKeyBindings = new KeyBindings(null, commands, this);
        final JXCSkin newSkin;
        if (dir.exists() && dir.isDirectory())
        {
            newSkin = new JXCSkinDirLoader(itemsManager, spellsManager, facesManager, stats, mapUpdater, dir, defaultKeyBindings);
        }
        else
        {
            // fallback: built-in resource
            newSkin = new JXCSkinClassLoader(itemsManager, spellsManager, facesManager, stats, mapUpdater, "com/realtime/crossfire/jxclient/skins/"+skinName, defaultKeyBindings);
        }
        newSkin.load(server, this, mouseTracker, metaserver, commandQueue, resolution, optionManager, experienceTable, shortcuts, commands, currentSpellManager);
        return newSkin;
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
     * Activate the command input text field. If the skin defined more than one
     * input field, the first matching one is selected.
     *
     * <p>If neither the main gui nor any visible dialog has an input text
     * field, invisible guis are checked as well. If one is found, it is made
     * visible.
     *
     * @param newText the new command text if non-<code>null</code>
     */
    public void activateCommandInput(final String newText)
    {
        final GUIText textArea = activateCommandInput();
        if (textArea != null && newText != null && newText.length() > 0)
        {
            textArea.setText(newText);
        }
    }

    /**
     * Activate the command input text field. If the skin defined more than one
     * input field, the first matching one is selected.
     *
     * <p>If neither the main gui nor any visible dialog has an input text
     * field, invisible guis are checked as well. If one is found, it is made
     * visible.
     *
     * @return The command input text field, or <code>null</code> if the skin
     * has no command input text field defined.
     */
    private GUIText activateCommandInput()
    {
        // check main gui
        final GUIText textArea1 = windowRenderer.getCurrentGui().activateCommandInput();
        if (textArea1 != null)
        {
            return textArea1;
        }

        // check visible dialogs
        for (final Gui dialog : windowRenderer.getOpenDialogs())
        {
            if (!dialog.isHidden(windowRenderer.getGuiState()))
            {
                final GUIText textArea2 = dialog.activateCommandInput();
                if (textArea2 != null)
                {
                    openDialog(dialog); // raise dialog
                    return textArea2;
                }
            }
            if (dialog.isModal())
            {
                return null;
            }
        }

        // check invisible dialogs
        for (final Gui dialog : skin)
        {
            final GUIText textArea3 = dialog.activateCommandInput();
            if (textArea3 != null)
            {
                openDialog(dialog);
                dialog.setAutoCloseOnDeactivate(true);
                return textArea3;
            }
        }

        return null;
    }

    /**
     * Deactivates the command input text field. Does nothing if the command
     * input text field is not active.
     * @return  whether the command input text field has been deactivated
     */
    private boolean deactivateCommandInput()
    {
        for (final Gui dialog : windowRenderer.getOpenDialogs())
        {
            if (!dialog.isHidden(windowRenderer.getGuiState()))
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

        return windowRenderer.getCurrentGui().deactivateCommandInput();
    }

    /**
     * Return the current skin.
     *
     * @return The skin.
     */
    public JXCSkin getSkin()
    {
        return skin;
    }

    /**
     * Return whether GUI elements should be highlighted.
     *
     * @return Whether GUI elements should be highlighted.
     */
    public boolean isDebugGui()
    {
        return debugGui;
    }

    /**
     * Return the window renderer instance for this window.
     *
     * @return The window renderer.
     */
    public JXCWindowRenderer getWindowRenderer()
    {
        return windowRenderer;
    }

    /**
     * Return the tooltip manager for this window.
     *
     * @return The tooltip manager for this window.
     */
    public TooltipManager getTooltipManager()
    {
        return tooltipManager;
    }

    /**
     * Set the current player name. Does nothing if not currently in the
     * character name prompt.
     *
     * @param playerName The player name.
     */
    public void updatePlayerName(final String playerName)
    {
        if (currentQueryDialogIsNamePrompt)
        {
            settings.putString("player_"+connection.getHostname(), playerName);
        }
    }

    /**
     * Sets the current status of the client to the given value. See the various
     * STATUS_ constants.
     * @param status The new status value.
     * @since 1.0
     */
    public void setStatus(final Status status)
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
    public Status getStatus()
    {
        synchronized (semaphoreStatus)
        {
            return status;
        }
    }

    public void sendReply(final String reply)
    {
        setStatus(JXCWindow.Status.PLAYING);
        server.sendReply(reply);
        closeQueryDialog();
    }

    /**
     * Add a connection listener.
     *
     * @param listener The listener to add.
     */
    public void addConnectionStateListener(final ConnectionStateListener listener)
    {
        connectionStateListeners.add(listener);
    }

    /**
     * Opens the keybinding dialog. Does nothing if the dialog is opened.
     */
    private void openKeybindDialog()
    {
        windowRenderer.openDialog(keybindDialog);
    }

    /**
     * Closes the keybinding dialog. Does nothing if the dialog is not opened.
     */
    private void closeKeybindDialog()
    {
        windowRenderer.closeDialog(keybindDialog);
    }

    /**
     * Returns the {@link KeyHandler}.
     * @return the key handler.
     */
    public KeyHandler getKeyHandler()
    {
        return keyHandler;
    }
}

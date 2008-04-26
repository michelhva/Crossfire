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
package com.realtime.crossfire.jxclient;

import com.realtime.crossfire.jxclient.animations.Animations;
import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.faces.Faces;
import com.realtime.crossfire.jxclient.gui.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.GUIMetaElement;
import com.realtime.crossfire.jxclient.gui.GUIOneLineLabel;
import com.realtime.crossfire.jxclient.gui.GUIText;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindingState;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.CrossfirePlayerListener;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.server.ConnectionListener;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawextinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandQueryEvent;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import com.realtime.crossfire.jxclient.server.Pickup;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.JXCSkinClassLoader;
import com.realtime.crossfire.jxclient.skin.JXCSkinDirLoader;
import com.realtime.crossfire.jxclient.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.sound.Sounds;
import com.realtime.crossfire.jxclient.stats.ActiveSkillWatcher;
import com.realtime.crossfire.jxclient.stats.PoisonWatcher;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.util.NumberParser;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public class JXCWindow extends JFrame implements KeyListener, CrossfireDrawextinfoListener, CrossfireQueryListener
{
    /**
     * The prefix for the window title.
     */
    private static final String TITLE_PREFIX = "jxclient";

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
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    /**
     * The global experience table.
     */
    private final ExperienceTable experienceTable = new ExperienceTable();

    private final CrossfireServerConnection server;

    private final Animations animations = new Animations();

    /**
     * The command queue instance for this window.
     */
    private final CommandQueue commandQueue;

    private final String semaphoreDrawing = "semaphore_drawing";

    private final String semaphoreChangeGui = "semaphore_change_gui";

    /**
     * The shortcuts for this window.
     */
    private final Shortcuts shortcuts = new Shortcuts(this);

    /**
     * The settings instance to use.
     */
    private final Settings settings;

    /**
     * Terminate the application if set.
     */
    private boolean terminated = false;

    private Gui queryDialog = new Gui(this);

    private Gui keybindDialog = new Gui(this);

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

    private final KeyBindings keyBindings = new KeyBindings(Filenames.getKeybindingsFile(null, null));

    /**
     * The key bindings for the current user. Set to <code>null</code> if no
     * user is logged in.
     */
    private KeyBindings characterKeyBindings = null;

    /**
     * The current pickup mode. Set to <code>null</code> if no user is logged
     * in.
     */
    private final Pickup characterPickup;

    private final boolean[] key_shift = new boolean[] { false, false, false, false };

    private KeyBindingState keyBindingState = null;

    public static final int KEY_SHIFT_SHIFT = 0;
    public static final int KEY_SHIFT_CTRL = 1;
    public static final int KEY_SHIFT_ALT = 2;
    public static final int KEY_SHIFT_ALTGR = 3;

    private boolean is_run_active = false;

    /**
     * The semaphore used to synchronized map model updates and map view
     * redraws.
     */
    private final Object semaphoreRedraw = "semaphore_redraw";

    private final JXCWindowRenderer jxcWindowRenderer = new JXCWindowRenderer(this, semaphoreRedraw);

    /**
     * The {@link TooltipManager} for this window.
     */
    private final TooltipManager tooltipManager = new TooltipManager(this);

    /**
     * The option manager for this window.
     */
    private final OptionManager optionManager;

    /**
     * The poison watcher to synthesize "poisoned" events.
     */
    private final PoisonWatcher poisonWatcher;

    /**
     * The active skill watcher to synthesize "active skill" events.
     */
    private final ActiveSkillWatcher activeSkillWatcher;

    /**
     * The commands instance for this window.
     */
    private final Commands commands = new Commands(this);

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
     * The width of the client area.
     */
    private int windowWidth = 0;

    /**
     * The height of the client area.
     */
    private int windowHeight = 0;

    /**
     * The currently connected server. Set to <code>null</code> if unconnected.
     */
    private String hostname = null;

    /**
     * The currently logged in character. Set to <code>null</code> if not
     * logged in.
     */
    private String character = null;

    /**
     * The currently connected port. Only valid if {@link #hostname} is set.
     */
    private int port = 0;

    /**
     * Whether the currently shown query dialog is the character name prompt.
     */
    private boolean currentQueryDialogIsNamePrompt = false;

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

    private final String semaphoreStatus = "semaphore_status";

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
        public void windowLostFocus(final WindowEvent e)
        {
            Arrays.fill(key_shift, false);
            stopRunning();
        }
    };

    /**
     * The listener to detect a changed player name.
     */
    private final CrossfirePlayerListener crossfirePlayerListener = new CrossfirePlayerListener()
    {
        /** {@inheritDoc} */
        public void playerReceived(final CfPlayer player)
        {
            if (jxcWindowRenderer.getGuiState() == JXCWindowRenderer.GuiState.NEWCHAR)
            {
                openDialogByName("messages"); // hack for race selection
            }
            jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.PLAYING);
            commandQueue.sendNcom(true, 1, "output-count 1"); // to make message merging work reliably
            characterPickup.update();                         // reset pickup mode
        }

        /** {@inheritDoc} */
        public void playerAdded(final CfPlayer player)
        {
            setCharacter(player.getName());
        }

        /** {@inheritDoc} */
        public void playerRemoved(final CfPlayer player)
        {
            setCharacter(null);
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
        public void windowClosing(final WindowEvent e)
        {
            if (keyBindingState != null)
            {
                keyBindingState = null;
                jxcWindowRenderer.closeDialog(keybindDialog);
            }

            if (dialogQuit == null)
            {
                endRendering();
            }
            else
            {
                jxcWindowRenderer.closeDialog(dialogDisconnect);
                jxcWindowRenderer.openDialog(dialogQuit);
            }
        }

        /** {@inheritDoc} */
        public void windowClosed(final WindowEvent e)
        {
            endRendering();
        }
    };

    /**
     * Create a new instance.
     *
     * @param debugGui Whether GUI elements should be highlighted.
     *
     * @param debugProtocol If non-<code>null</code>, write all protocol
     * commands to this appender.
     *
     * @param settings The settings instance to use.
     */
    public JXCWindow(final boolean debugGui, final Appendable debugProtocol, final Settings settings)
    {
        super(TITLE_PREFIX);
        this.debugGui = debugGui;
        this.settings = settings;
        server = new CrossfireServerConnection(semaphoreRedraw, experienceTable, animations, debugProtocol);
        CfMapUpdater.reset();
        commandQueue = new CommandQueue(server);
        poisonWatcher = new PoisonWatcher(ItemsList.getStats(), server);
        activeSkillWatcher = new ActiveSkillWatcher(ItemsList.getStats(), server);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        optionManager = new OptionManager(settings);
        try
        {
            characterPickup = new Pickup(commandQueue, optionManager);
        }
        catch (final OptionException ex)
        {
            throw new AssertionError();
        }
        mouseTracker = new MouseTracker(debugGui, jxcWindowRenderer);
        addWindowFocusListener(windowFocusListener);
        addWindowListener(windowListener);
        updateTitle();
    }

    public boolean checkRun()
    {
        return is_run_active;
    }

    public boolean checkFire()
    {
        return false;
    }

    public boolean createKeyBinding(final boolean perCharacter, final GUICommandList cmdlist)
    {
        final KeyBindings bindings = getKeyBindings(perCharacter);
        if (bindings == null)
        {
            return false;
        }

        keyBindingState = new KeyBindingState(bindings, null, cmdlist);
        jxcWindowRenderer.openDialog(keybindDialog);
        return true;
    }

    public boolean removeKeyBinding(final boolean perCharacter)
    {
        if (perCharacter && characterKeyBindings == null)
        {
            return false;
        }

        keyBindingState = new KeyBindingState(characterKeyBindings, perCharacter ? null : keyBindings, null);
        jxcWindowRenderer.openDialog(keybindDialog);
        return true;
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
        catch (final Exception ex)
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
        catch (final Exception ex)
        {
            System.err.println("Cannot write shortcuts file "+file+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Return the shortcuts of this window.
     *
     * @return The shortcuts.
     */
    public Shortcuts getShortcuts()
    {
        return shortcuts;
    }

    public boolean getKeyShift(final int keyid)
    {
        return key_shift[keyid];
    }

    private void setKeyShift(final int keyid, final boolean state)
    {
        key_shift[keyid] = state;
    }

    /**
     * Open a dialog. Raises an already opened dialog.
     *
     * @param dialog The dialog to show.
     */
    public void openDialog(final Gui dialog)
    {
        jxcWindowRenderer.openDialog(dialog);
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
        if (jxcWindowRenderer.toggleDialog(dialog))
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
        jxcWindowRenderer.closeDialog(queryDialog);
    }

    private void initRendering(final boolean fullScreen)
    {
        jxcWindowRenderer.initRendering(fullScreen);
        DialogStateParser.load(skin, jxcWindowRenderer);
        loadKeybindings();
        loadShortcuts();
    }

    /**
     * Opens a dialog by name.
     * @param dialog the name
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
     * @param dialog the name
     */
    private void closeDialogByName(final String name)
    {
        final Gui dialog = skin.getDialog(name);
        if (dialog != null)
        {
            jxcWindowRenderer.closeDialog(dialog);
        }
    }

    public void endRendering()
    {
        terminated = true;
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
                server.disconnect();
                setHost(null);
                ItemsList.getItemsManager().removeCrossfirePlayerListener(crossfirePlayerListener);
                server.removeCrossfireQueryListener(this);
                server.removeCrossfireDrawextinfoListener(this);
                setTitle(TITLE_PREFIX);
                ItemsList.getItemsManager().reset();
                for (final ConnectionStateListener listener : connectionStateListeners)
                {
                    listener.disconnect();
                }
            }

            this.guiId = guiId;

            if (this.guiId == GUI_MAIN)
            {
                SoundManager.instance.mute(Sounds.CHARACTER, false);
                server.addCrossfireDrawextinfoListener(this);
                server.addCrossfireQueryListener(this);
                setTitle(TITLE_PREFIX+" - "+hostname);
                ItemsList.getItemsManager().addCrossfirePlayerListener(crossfirePlayerListener);
                ItemsList.getStats().reset();
                server.setMapSize(skin.getMapWidth(), skin.getMapHeight());
                server.connect(hostname, port, connectionListener);
                Faces.reset();
                commandQueue.clear();
                ItemsList.getItemsManager().reset();
                ItemsList.getSpellsManager().reset();
                animations.reset();
                for (final ConnectionStateListener listener : connectionStateListeners)
                {
                    listener.connect();
                }
            }

            if (dialogDisconnect != null)
            {
                jxcWindowRenderer.closeDialog(dialogDisconnect);
            }
            if (dialogQuit != null)
            {
                jxcWindowRenderer.closeDialog(dialogQuit);
            }
            jxcWindowRenderer.closeDialog(queryDialog);
            jxcWindowRenderer.closeDialog(skin.getDialogBook(1));

            switch (guiId)
            {
            case GUI_START:
                SoundManager.instance.muteMusic(true);
                SoundManager.instance.mute(Sounds.CHARACTER, true);
                jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.START);
                if (DISABLE_START_GUI)
                {
                    endRendering();
                }
                else
                {
                    showGUIStart();
                }
                break;

            case GUI_METASERVER:
                SoundManager.instance.muteMusic(true);
                SoundManager.instance.mute(Sounds.CHARACTER, true);
                jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.META);
                showGUIMeta();
                metaserver.query();

                final String serverName = settings.getString("server", "crossfire.metalforge.net");
                if (serverName.length() > 0)
                {
                    final int metaIndex = metaserver.getServerIndex(serverName);
                    if (metaIndex != -1)
                    {
                        final GUIMetaElement metaElement = jxcWindowRenderer.getCurrentGui().getMetaElement(metaIndex);
                        if (metaElement != null)
                        {
                            metaElement.setActive(true);
                        }
                    }
                }
                break;

            case GUI_MAIN:
                SoundManager.instance.muteMusic(false);
                jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.LOGIN);
                showGUIMain();
                break;
            }
        }
    }

    public void init(final int w, final int h, final String skinName, final boolean fullScreen, final String serverInfo)
    {
        windowWidth = w;
        windowHeight = h;
        addKeyListener(this);
        addMouseListener(mouseTracker);
        addMouseMotionListener(mouseTracker);
        try
        {
            Faces.setFacesCallback(server);
        }
        catch (final IOException ex)
        {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
        jxcWindowRenderer.init(w, h);
        if (!setSkin(skinName))
        {
            if (skinName.equals(jxclient.DEFAULT_SKIN))
            {
                System.exit(1);
            }

            System.err.println("trying to load default skin "+jxclient.DEFAULT_SKIN);
            if (!setSkin(jxclient.DEFAULT_SKIN))
            {
                System.exit(1);
                throw new AssertionError();
            }
        }
        try
        {
            initRendering(fullScreen);
            try
            {
                if (serverInfo != null)
                {
                    connect(serverInfo);
                }
                else
                {
                    changeGUI(DISABLE_START_GUI ? GUI_METASERVER : GUI_START);
                }
                while (!terminated)
                {
                    synchronized (semaphoreDrawing)
                    {
                        jxcWindowRenderer.redrawGUI();
                    }
                    Thread.sleep(10);
                }
            }
            finally
            {
                jxcWindowRenderer.endRendering();
            }

            saveShortcuts();
            saveKeybindings();
            DialogStateParser.save(skin, jxcWindowRenderer);
            optionManager.saveOptions();
            SoundManager.instance.shutdown();
        }
        catch (final InterruptedException e)
        {
            // ignore
        }
    }

    public void connect(final String serverInfo)
    {
        settings.putString("server", serverInfo);
        setHost(serverInfo);
        changeGUI(GUI_MAIN);
    }

    public void disconnect()
    {
        changeGUI(GUI_METASERVER);
    }

    public CrossfireServerConnection getCrossfireServerConnection()
    {
        return server;
    }

    private void handleKeyPress(final KeyEvent e)
    {
        if (getStatus() != Status.PLAYING)
        {
            return;
        }

        if (characterKeyBindings != null && characterKeyBindings.handleKeyPress(e))
        {
            return;
        }

        if (keyBindings.handleKeyPress(e))
        {
            return;
        }

        switch (e.getKeyCode())
        {
        case KeyEvent.VK_KP_UP:
        case KeyEvent.VK_UP:
        case KeyEvent.VK_NUMPAD8:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                commandQueue.sendNcom(false, 0, "run 1");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "north f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "north");
            }
            break;

        case KeyEvent.VK_PAGE_UP:
        case KeyEvent.VK_NUMPAD9:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                commandQueue.sendNcom(false, 0, "run 2");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "northeast f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "northeast");
            }
            break;

        case KeyEvent.VK_KP_RIGHT:
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_NUMPAD6:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                commandQueue.sendNcom(false, 0, "run 3");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "east f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "east");
            }
            break;

        case KeyEvent.VK_PAGE_DOWN:
        case KeyEvent.VK_NUMPAD3:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                commandQueue.sendNcom(false, 0, "run 4");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "southeast f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "southeast");
            }
            break;

        case KeyEvent.VK_KP_DOWN:
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_NUMPAD2:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                commandQueue.sendNcom(false, 0, "run 5");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "south f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "south");
            }
            break;

        case KeyEvent.VK_END:
        case KeyEvent.VK_NUMPAD1:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                commandQueue.sendNcom(false, 0, "run 6");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "southwest f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "southwest");
            }
            break;

        case KeyEvent.VK_KP_LEFT:
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_NUMPAD4:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                commandQueue.sendNcom(false, 0, "run 7");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "west f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "west");
            }
            break;

        case KeyEvent.VK_HOME:
        case KeyEvent.VK_NUMPAD7:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                commandQueue.sendNcom(false, 0, "run 8");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "northwest f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "northwest");
            }
            break;

        case KeyEvent.VK_BEGIN:
        case KeyEvent.VK_NUMPAD5:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                // ignore
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                commandQueue.sendNcom(false, "stay f");
            }
            else
            {
                commandQueue.sendNcom(false, 0, "stay");
            }
            break;

        case KeyEvent.VK_0:
            commandQueue.addToRepeatCount(0);
            break;

        case KeyEvent.VK_1:
            commandQueue.addToRepeatCount(1);
            break;

        case KeyEvent.VK_2:
            commandQueue.addToRepeatCount(2);
            break;

        case KeyEvent.VK_3:
            commandQueue.addToRepeatCount(3);
            break;

        case KeyEvent.VK_4:
            commandQueue.addToRepeatCount(4);
            break;

        case KeyEvent.VK_5:
            commandQueue.addToRepeatCount(5);
            break;

        case KeyEvent.VK_6:
            commandQueue.addToRepeatCount(6);
            break;

        case KeyEvent.VK_7:
            commandQueue.addToRepeatCount(7);
            break;

        case KeyEvent.VK_8:
            commandQueue.addToRepeatCount(8);
            break;

        case KeyEvent.VK_9:
            commandQueue.addToRepeatCount(9);
            break;

        default:
            break;
        }
    }

    private void handleKeyTyped(final KeyEvent e)
    {
        if (getStatus() != Status.PLAYING)
        {
            return;
        }

        if (characterKeyBindings != null && characterKeyBindings.handleKeyTyped(e))
        {
            return;
        }

        if (keyBindings.handleKeyTyped(e))
        {
            return;
        }

        switch (e.getKeyChar())
        {
        case 'a':
            commandQueue.sendNcom(false, "apply");
            break;

        case 'd':
            commandQueue.sendNcom(false, "use_skill disarm traps");
            break;

        case 'e':
            commandQueue.sendNcom(false, "examine");
            break;

        case 'i':
            commandQueue.sendNcom(false, "mapinfo");
            break;

        case 'j':
            commandQueue.sendNcom(false, "use_skill jumping");
            break;

        case 'm':
            {
                final GUIText textArea = activateCommandInput();
                if (textArea != null)
                {
                    textArea.setText("maps ");
                }
            }
            break;

        case 's':
            commandQueue.sendNcom(false, "use_skill find traps");
            break;

        case 'p':
            commandQueue.sendNcom(false, "use_skill praying");
            break;

        case 't':
            commandQueue.sendNcom(false, "ready_skill throwing");
            break;

        case 'w':
            commandQueue.sendNcom(false, "who");
            break;

        case '?':
            commandQueue.sendNcom(false, "help");
            break;

        case ',':
            commandQueue.sendNcom(false, "get");
            break;

        case '/':
        case '\'':
            activateCommandInput();
            break;

        case '"':
            {
                final GUIText textArea = activateCommandInput();
                if (textArea != null)
                {
                    textArea.setText("say ");
                }
            }
            break;

        default:
            break;
        }
    }

    public void keyPressed(final KeyEvent e)
    {
        updateModifiers(e);
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_ALT:
        case KeyEvent.VK_ALT_GRAPH:
        case KeyEvent.VK_SHIFT:
        case KeyEvent.VK_CONTROL:
            break;

        default:
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                if (keyBindingState != null)
                {
                    keyBindingState = null;
                    jxcWindowRenderer.closeDialog(keybindDialog);
                }
                else if (getStatus() != Status.UNCONNECTED)
                {
                    if (dialogDisconnect == null)
                    {
                        disconnect();
                    }
                    else if (jxcWindowRenderer.openDialog(dialogDisconnect))
                    {
                        if (dialogQuit != null)
                        {
                            jxcWindowRenderer.closeDialog(dialogQuit);
                        }
                    }
                    else
                    {
                        jxcWindowRenderer.closeDialog(dialogDisconnect);
                    }
                }
                else
                {
                    if (dialogQuit == null)
                    {
                        endRendering();
                    }
                    else if (jxcWindowRenderer.openDialog(dialogQuit))
                    {
                        if (dialogDisconnect != null)
                        {
                            jxcWindowRenderer.closeDialog(dialogDisconnect);
                        }
                    }
                    else
                    {
                        jxcWindowRenderer.closeDialog(dialogQuit);
                    }
                }
            }
            else if (keyBindingState != null)
            {
                keyBindingState.keyPressed(e.getKeyCode(), e.getModifiers());
            }
            else
            {
                for (final Gui dialog : jxcWindowRenderer.getOpenDialogs())
                {
                    if (!dialog.isHidden(jxcWindowRenderer.getGuiState()))
                    {
                        if (dialog.handleKeyPress(e))
                        {
                            return;
                        }
                        if (dialog.isModal())
                        {
                            return;
                        }
                    }
                }
                if (jxcWindowRenderer.getCurrentGui().handleKeyPress(e))
                {
                    return;
                }
                handleKeyPress(e);
            }
            break;
        }
    }

    public void keyReleased(final KeyEvent e)
    {
        updateModifiers(e);
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_ALT:
        case KeyEvent.VK_ALT_GRAPH:
        case KeyEvent.VK_SHIFT:
        case KeyEvent.VK_CONTROL:
            break;

        default:
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                // ignore
            }
            else if (keyBindingState != null)
            {
                if (keyBindingState.keyReleased())
                {
                    keyBindingState = null;
                    jxcWindowRenderer.closeDialog(keybindDialog);
                }
            }
            break;
        }
    }

    public void keyTyped(final KeyEvent e)
    {
        if (e.getKeyChar() == 27) // ignore ESC key
        {
            return;
        }

        if (keyBindingState != null)
        {
            keyBindingState.keyTyped(e.getKeyChar());
            commandQueue.resetRepeatCount();
        }
        else
        {
            for (final Gui dialog : jxcWindowRenderer.getOpenDialogs())
            {
                if (!dialog.isHidden(jxcWindowRenderer.getGuiState()))
                {
                    if (dialog.handleKeyTyped(e))
                    {
                        return;
                    }
                    if (dialog.isModal())
                    {
                        return;
                    }
                }
            }
            if (jxcWindowRenderer.getCurrentGui().handleKeyTyped(e))
            {
                return;
            }
            handleKeyTyped(e);
        }
    }

    /**
     * Update the saved modifier state from a key event.
     *
     * @param keyEvent The key event to process.
     */
    private void updateModifiers(final KeyEvent keyEvent)
    {
        final int mask = keyEvent.getModifiersEx();
        setKeyShift(KEY_SHIFT_SHIFT, (mask&InputEvent.SHIFT_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_CTRL, (mask&InputEvent.CTRL_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_ALT, (mask&InputEvent.ALT_DOWN_MASK) != 0);
        setKeyShift(KEY_SHIFT_ALTGR, (mask&InputEvent.ALT_GRAPH_DOWN_MASK) != 0);
        if (!getKeyShift(KEY_SHIFT_CTRL))
        {
            stopRunning();
        }
    }

    /**
     * Tell the server to stop running. If the character is not running, do
     * nothing.
     */
    private void stopRunning()
    {
        if (!is_run_active)
        {
            return;
        }

        commandQueue.sendNcom(true, 0, "run_stop");
        is_run_active = false;
    }

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
        jxcWindowRenderer.openDialog(dialog);
    }

    public void commandQueryReceived(final CrossfireCommandQueryEvent evt)
    {
        setStatus(Status.QUERY);
        jxcWindowRenderer.openDialog(queryDialog);
        queryDialog.setHideInput((evt.getQueryType()&CrossfireCommandQueryEvent.HIDEINPUT) != 0);

        currentQueryDialogIsNamePrompt = evt.getPrompt().startsWith("What is your name?");
        if (currentQueryDialogIsNamePrompt)
        {
            final String playerName = settings.getString("player_"+hostname, "");
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
            jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.NEWCHAR);
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

    private void showGUIStart()
    {
        jxcWindowRenderer.clearGUI();
        jxcWindowRenderer.setCurrentGui(skin.getStartInterface());
        tooltipManager.reset();
    }

    private void showGUIMeta()
    {
        jxcWindowRenderer.clearGUI();
        final Gui newGui = skin.getMetaInterface();
        jxcWindowRenderer.setCurrentGui(newGui);
        newGui.activateDefaultElement();
        tooltipManager.reset();
    }

    private void showGUIMain()
    {
        jxcWindowRenderer.clearGUI();
        Gui newGui = skin.getMainInterface();
        jxcWindowRenderer.setCurrentGui(newGui);
        tooltipManager.reset();
    }

    /** {@inheritDoc} */
    public void paint(final Graphics g)
    {
        jxcWindowRenderer.repaint();
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
            // check for skin in directory
            final File dir = new File(skinName);
            if (dir.exists() && dir.isDirectory())
            {
                skin = new JXCSkinDirLoader(dir);
            }
            else
            {
                // fallback: built-in resource
                skin = new JXCSkinClassLoader("com/realtime/crossfire/jxclient/skins/"+skinName);
            }
            skin.load(server, this);
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
        return true;
    }

    /**
     * Return the width of the client area.
     *
     * @return The width of the client area.
     */
    public int getWindowWidth()
    {
        return windowWidth;
    }

    /**
     * Return the height of the client area.
     *
     * @return The height of the client area.
     */
    public int getWindowHeight()
    {
        return windowHeight;
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
        final GUIText textArea1 = jxcWindowRenderer.getCurrentGui().activateCommandInput();
        if (textArea1 != null)
        {
            return textArea1;
        }

        // check visible dialogs
        for (final Gui dialog : jxcWindowRenderer.getOpenDialogs())
        {
            if (!dialog.isHidden(jxcWindowRenderer.getGuiState()))
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
     * Return the global experience table.
     *
     * @return The experience table.
     */
    public ExperienceTable getExperienceTable()
    {
        return experienceTable;
    }

    /**
     * Load the keybindings from the backing file.
     */
    private void loadKeybindings()
    {
        try
        {
            keyBindings.loadKeyBindings(this);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read keybindings file "+keyBindings.getFile()+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Save the keybindings to the backing file.
     */
    private void saveKeybindings()
    {
        try
        {
            keyBindings.saveKeyBindings();
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write keybindings file "+keyBindings.getFile()+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Return the key bindings instance for this window.
     *
     * @param perCharacter If set, return the per-character key bindings; else
     * return the global bindings.
     *
     * @return The key bindings, or <code>null</code> if no per-character
     * bindings exist because no character is logged in.
     */
    public KeyBindings getKeyBindings(final boolean perCharacter)
    {
        return perCharacter ? characterKeyBindings : keyBindings;
    }

    /**
     * Execute a command or a list of commands. The commands may be a client-
     * or a server-sided command.
     *
     * @param commands The commands to execute.
     */
    public void executeCommand(final String commands)
    {

        String cmds = commands.trim();
        while (cmds.length() > 0)
        {
            final String[] cmd = cmds.split(" *; *", 2);
            if (executeSingleCommand(cmd[0], cmds))
            {
                break;
            }
            if (cmd.length <= 1)
            {
                break;
            }
            cmds = cmd[1];
        }
    }

    /**
     * Execute a command. The command may be a client- or a server-sided
     * command.
     *
     * @param command The command to execute.
     *
     * @param commandList The command and all remaining commands.
     *
     * @return <code>true</code> if all remaining commands have been consumed.
     */
    private boolean executeSingleCommand(final String command, final String commandList)
    {
        if (command.length() <= 0)
        {
            return false;
        }

        switch (commands.execute(command, commandList))
        {
        case 0:
            commandQueue.sendNcom(false, command);
            return false;

        case 1:
            return false;

        default:
            return true;
        }
    }

    /**
     * Return the option manager for this window.
     *
     * @return The option manager.
     */
    public OptionManager getOptionManager()
    {
        return optionManager;
    }

    /**
     * Return the mouse tracker.
     *
     * @return The mouse tracker.
     */
    public MouseTracker getMouseTracker()
    {
        return mouseTracker;
    }

    /**
     * Return the window renderer instance for this window.
     *
     * @return The window renderer.
     */
    public JXCWindowRenderer getWindowRenderer()
    {
        return jxcWindowRenderer;
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
     * Return the current spell manager instance for this window.
     *
     * @return The current spell manager instance for this window.
     */
    public final CurrentSpellManager getCurrentSpellManager()
    {
        return currentSpellManager;
    }

    /**
     * Return the metaserver instance for this window.
     *
     * @return The metaserver instance for this window.
     */
    public final Metaserver getMetaserver()
    {
        return metaserver;
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
            settings.putString("player_"+hostname, playerName);
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
     * Return the command queue instance for this window.
     *
     * @return The command queue instance.
     */
    public CommandQueue getCommandQueue()
    {
        return commandQueue;
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
     * Update the active character name.
     *
     * @param character The active character; <code>null</code> if not logged
     * in.
     */
    private void setCharacter(final String character)
    {
        if (this.character == null ? character == null : this.character.equals(character))
        {
            return;
        }

        if (characterKeyBindings != null)
        {
            try
            {
                characterKeyBindings.saveKeyBindings();
            }
            catch (final IOException ex)
            {
                System.err.println("Cannot write keybindings file "+characterKeyBindings.getFile()+": "+ex.getMessage());
            }
            characterKeyBindings = null;
        }

        if (hostname != null && this.character != null)
        {
            final long pickupMode = characterPickup.getPickupMode();
            if (pickupMode != Pickup.PU_NOTHING)
            {
                settings.putLong("pickup_"+hostname+"_"+this.character, pickupMode);
            }
            else
            {
                settings.remove("pickup_"+hostname+"_"+this.character);
            }
        }

        this.character = character;
        updateTitle();

        if (hostname != null && character != null)
        {
            characterKeyBindings = new KeyBindings(Filenames.getKeybindingsFile(hostname, character));
            try
            {
                characterKeyBindings.loadKeyBindings(this);
            }
            catch (final IOException ex)
            {
                System.err.println("Cannot read keybindings file "+characterKeyBindings.getFile()+": "+ex.getMessage());
            }

            characterPickup.setPickupMode(settings.getLong("pickup_"+hostname+"_"+character, Pickup.PU_NOTHING));
        }
    }

    /**
     * Update information about the connected host.
     *
     * @param serverInfo The hostname; <code>null</code> if not connected.
     */
    private void setHost(final String serverInfo)
    {
        final String newHostname;
        final int newPort;
        if (serverInfo == null)
        {
            newHostname = null;
            newPort = 0;
        }
        else
        {
            final String[] tmp = serverInfo.split(":", 2);
            newHostname = tmp[0];
            newPort = tmp.length < 2 ? 13327 : NumberParser.parseInt(tmp[1], 13327, 1, 65535);
        }

        if ((hostname == null ? newHostname == null : hostname.equals(newHostname))
        && port == newPort)
        {
            return;
        }

        setCharacter(null);
        hostname = newHostname;
        port = newPort;
        updateTitle();
    }

    /**
     * Update the window title to reflect the current connection state.
     */
    private void updateTitle()
    {
        if (hostname == null)
        {
            setTitle(TITLE_PREFIX);
        }
        else if (character == null)
        {
            setTitle(TITLE_PREFIX+" - "+hostname);
        }
        else
        {
            setTitle(TITLE_PREFIX+" - "+hostname+" - "+character);
        }
    }
}

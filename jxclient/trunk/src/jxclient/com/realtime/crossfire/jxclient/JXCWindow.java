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

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.faces.Faces;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.GUIText;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindingState;
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
import com.realtime.crossfire.jxclient.server.ServerConnection;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.JXCSkinClassLoader;
import com.realtime.crossfire.jxclient.skin.JXCSkinDirLoader;
import com.realtime.crossfire.jxclient.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.sound.Sounds;
import com.realtime.crossfire.jxclient.stats.PoisonWatcher;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
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
import java.util.Arrays;
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

    private int guiId = -1;

    /**
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    /**
     * The global experience table.
     */
    private final ExperienceTable experienceTable = new ExperienceTable();

    private final CrossfireServerConnection myserver;

    private final String semaphore_drawing = "semaphore_drawing";

    private final String semaphoseChangeGui = "semaphore_change_gui";

    /**
     * The shortcuts for this window.
     */
    private final Shortcuts shortcuts = new Shortcuts(this);

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

    private JXCSkin myskin = null;

    private final KeyBindings keyBindings = new KeyBindings();

    private final boolean[] key_shift = new boolean[] { false, false, false, false };

    private KeyBindingState keyBindingState = null;

    public static final int KEY_SHIFT_SHIFT = 0;
    public static final int KEY_SHIFT_CTRL = 1;
    public static final int KEY_SHIFT_ALT = 2;
    public static final int KEY_SHIFT_ALTGR = 3;

    private boolean is_run_active = false;

    private final JXCWindowRenderer jxcWindowRenderer = new JXCWindowRenderer(this);

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
     * The default repeat counter for "ncom" commands.
     */
    private int repeatCount = 0;

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
     * The currently connected port. Only valid if {@link #hostname} is set.
     */
    private int port = 0;

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
            jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.PLAYING);
        }

        /** {@inheritDoc} */
        public void playerAdded(final CfPlayer player)
        {
            setTitle(TITLE_PREFIX+" - "+hostname+" - "+player.getName());
        }

        /** {@inheritDoc} */
        public void playerRemoved(final CfPlayer player)
        {
            setTitle(TITLE_PREFIX+" - "+hostname);
        }
    };

    private final ConnectionListener connectionListener = new ConnectionListener()
    {
        /** {@inheritDoc} */
        public void connectionLost()
        {
            myserver.setStatus(ServerConnection.Status.UNCONNECTED);
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
        myserver = new CrossfireServerConnection(experienceTable, debugProtocol);
        poisonWatcher = new PoisonWatcher(ItemsList.getStats(), myserver);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        optionManager = new OptionManager(settings);
        mouseTracker = new MouseTracker(debugGui, jxcWindowRenderer);
        addWindowFocusListener(windowFocusListener);
        addWindowListener(windowListener);
    }

    public boolean checkRun()
    {
        return is_run_active;
    }

    public boolean checkFire()
    {
        return false;
    }

    public void createKeyBinding(final GUICommandList cmdlist)
    {
        keyBindingState = new KeyBindingState(cmdlist);
        jxcWindowRenderer.openDialog(keybindDialog);
    }

    public void removeKeyBinding()
    {
        keyBindingState = new KeyBindingState(null);
        jxcWindowRenderer.openDialog(keybindDialog);
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
        DialogStateParser.load(myskin, jxcWindowRenderer);
        loadKeybindings();
        loadShortcuts();
    }

    public void endRendering()
    {
        terminated = true;
    }

    public void changeGUI(final int guiId)
    {
        synchronized (semaphoseChangeGui)
        {
            if (this.guiId == guiId)
            {
                return;
            }

            if (this.guiId == GUI_MAIN)
            {
                myserver.disconnect();
                this.hostname = null;
                this.port = 0;
                ItemsList.getItemsManager().removeCrossfirePlayerListener(crossfirePlayerListener);
                myserver.removeCrossfireQueryListener(this);
                myserver.removeCrossfireDrawextinfoListener(this);
                setTitle(TITLE_PREFIX);
            }

            this.guiId = guiId;

            if (this.guiId == GUI_MAIN)
            {
                SoundManager.instance.mute(Sounds.CHARACTER, false);
                myserver.addCrossfireDrawextinfoListener(this);
                myserver.addCrossfireQueryListener(this);
                setTitle(TITLE_PREFIX+" - "+hostname);
                ItemsList.getItemsManager().addCrossfirePlayerListener(crossfirePlayerListener);
                ItemsList.getStats().reset();
                myserver.connect(hostname, port, connectionListener);
                Faces.setFacesCallback(myserver);
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
                break;

            case GUI_MAIN:
                SoundManager.instance.muteMusic(false);
                jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.LOGIN);
                showGUIMain();
                break;
            }
        }
    }

    public void init(final int w, final int h, final int b, final int f, final String skinName, final boolean fullScreen, final String server)
    {
        windowWidth = w;
        windowHeight = h;
        CfMapUpdater.processNewmap();
        addKeyListener(this);
        addMouseListener(mouseTracker);
        addMouseMotionListener(mouseTracker);
        jxcWindowRenderer.init(w, h, b, f);
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
                if (server != null)
                {
                    connect(server, 13327);
                }
                else
                {
                    changeGUI(DISABLE_START_GUI ? GUI_METASERVER : GUI_START);
                }
                while (!terminated)
                {
                    synchronized(semaphore_drawing)
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
            DialogStateParser.save(myskin, jxcWindowRenderer);
            optionManager.saveOptions();
            SoundManager.instance.shutdown();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void connect(final String hostname, final int port)
    {
        this.hostname = hostname;
        this.port = port;
        changeGUI(GUI_MAIN);
    }

    public void disconnect()
    {
        changeGUI(GUI_METASERVER);
    }

    /**
     * Send a "ncom" command to the server. This function uses the default
     * repeat count.
     *
     * @param command the command
     *
     * @return the packet id
     *
     * @see #sendNcom(int, String)
     */
    public int sendNcom(final String command)
    {
        return sendNcom(getRepeatCount(), command);
    }

    /**
     * Send a "ncom" command to the server.
     *
     * @param repeat the repeat count
     *
     * @param command the command
     *
     * @return the packet id
     *
     * @see #sendNcom(String)
     */
    public int sendNcom(final int repeat, final String command)
    {
        try
        {
            return myserver.sendNcom(repeat, command);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            endRendering();
            return 0;
        }
    }

    public CrossfireServerConnection getCrossfireServerConnection()
    {
        return myserver;
    }

    private void handleKeyPress(final KeyEvent e)
    {
        if (myserver == null || myserver.getStatus() != ServerConnection.Status.PLAYING)
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
                sendNcom(0, "run 1");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                sendNcom("north f");
            }
            else
            {
                sendNcom(0, "north");
            }
            break;

        case KeyEvent.VK_PAGE_UP:
        case KeyEvent.VK_NUMPAD9:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                sendNcom(0, "run 2");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                sendNcom("northeast f");
            }
            else
            {
                sendNcom(0, "northeast");
            }
            break;

        case KeyEvent.VK_KP_RIGHT:
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_NUMPAD6:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                sendNcom(0, "run 3");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                sendNcom("east f");
            }
            else
            {
                sendNcom(0, "east");
            }
            break;

        case KeyEvent.VK_PAGE_DOWN:
        case KeyEvent.VK_NUMPAD3:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                sendNcom(0, "run 4");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                sendNcom("southeast f");
            }
            else
            {
                sendNcom(0, "southeast");
            }
            break;

        case KeyEvent.VK_KP_DOWN:
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_NUMPAD2:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                sendNcom(0, "run 5");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                sendNcom("south f");
            }
            else
            {
                sendNcom(0, "south");
            }
            break;

        case KeyEvent.VK_END:
        case KeyEvent.VK_NUMPAD1:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                sendNcom(0, "run 6");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                sendNcom("southwest f");
            }
            else
            {
                sendNcom(0, "southwest");
            }
            break;

        case KeyEvent.VK_KP_LEFT:
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_NUMPAD4:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                sendNcom(0, "run 7");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                sendNcom("west f");
            }
            else
            {
                sendNcom(0, "west");
            }
            break;

        case KeyEvent.VK_HOME:
        case KeyEvent.VK_NUMPAD7:
            if (getKeyShift(KEY_SHIFT_CTRL))
            {
                sendNcom(0, "run 8");
                is_run_active = true;
            }
            else if (getKeyShift(KEY_SHIFT_SHIFT))
            {
                sendNcom("northwest f");
            }
            else
            {
                sendNcom(0, "northwest");
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
                sendNcom("stay f");
            }
            else
            {
                sendNcom(0, "stay");
            }
            break;

        case KeyEvent.VK_0:
            addToRepeatCount(0);
            break;

        case KeyEvent.VK_1:
            addToRepeatCount(1);
            break;

        case KeyEvent.VK_2:
            addToRepeatCount(2);
            break;

        case KeyEvent.VK_3:
            addToRepeatCount(3);
            break;

        case KeyEvent.VK_4:
            addToRepeatCount(4);
            break;

        case KeyEvent.VK_5:
            addToRepeatCount(5);
            break;

        case KeyEvent.VK_6:
            addToRepeatCount(6);
            break;

        case KeyEvent.VK_7:
            addToRepeatCount(7);
            break;

        case KeyEvent.VK_8:
            addToRepeatCount(8);
            break;

        case KeyEvent.VK_9:
            addToRepeatCount(9);
            break;

        default:
            break;
        }
    }

    private void handleKeyTyped(final KeyEvent e)
    {
        if (myserver == null || myserver.getStatus() != ServerConnection.Status.PLAYING)
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
            sendNcom("apply");
            break;

        case 'd':
            sendNcom("use_skill disarm traps");
            break;

        case 'e':
            sendNcom("examine");
            break;

        case 'i':
            sendNcom("mapinfo");
            break;

        case 'j':
            sendNcom("use_skill jumping");
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
            sendNcom("use_skill find traps");
            break;

        case 'p':
            sendNcom("use_skill praying");
            break;

        case 't':
            sendNcom("ready_skill throwing");
            break;

        case 'w':
            sendNcom("who");
            break;

        case '?':
            sendNcom("help");
            break;

        case ',':
            sendNcom("get");
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
                else if (myserver != null && myserver.getStatus() != ServerConnection.Status.UNCONNECTED)
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
                if (keyBindingState.keyReleased(keyBindings))
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
            resetRepeatCount();
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

        sendNcom(0, "run_stop");
        is_run_active = false;
    }

    public void commandDrawextinfoReceived(final CrossfireCommandDrawextinfoEvent evt)
    {
        switch (evt.getType())
        {
        case CrossfireServerConnection.MSG_TYPE_BOOK:
            jxcWindowRenderer.openDialog(myskin.getDialogBook(1));
            break;

        case CrossfireServerConnection.MSG_TYPE_CARD:
        case CrossfireServerConnection.MSG_TYPE_PAPER:
        case CrossfireServerConnection.MSG_TYPE_SIGN:
        case CrossfireServerConnection.MSG_TYPE_MONUMENT:
        case CrossfireServerConnection.MSG_TYPE_DIALOG:
            break;

        case CrossfireServerConnection.MSG_TYPE_MOTD:
            /*
             * We do not display a MOTD dialog, because it interferes with the
             * query dialog that gets displayed just after it.
             */
            break;

        case CrossfireServerConnection.MSG_TYPE_ADMIN:
        case CrossfireServerConnection.MSG_TYPE_SHOP:
        case CrossfireServerConnection.MSG_TYPE_COMMAND:
        case CrossfireServerConnection.MSG_TYPE_ATTRIBUTE:
        case CrossfireServerConnection.MSG_TYPE_SKILL:
        case CrossfireServerConnection.MSG_TYPE_APPLY:
        case CrossfireServerConnection.MSG_TYPE_ATTACK:
        case CrossfireServerConnection.MSG_TYPE_COMMUNICATION:
        case CrossfireServerConnection.MSG_TYPE_SPELL:
        case CrossfireServerConnection.MSG_TYPE_ITEM:
        case CrossfireServerConnection.MSG_TYPE_MISC:
        case CrossfireServerConnection.MSG_TYPE_VICTIM:
            break;

        default:
            break;
        }
    }

    public void commandQueryReceived(final CrossfireCommandQueryEvent evt)
    {
        myserver.setStatus(ServerConnection.Status.QUERY);
        jxcWindowRenderer.openDialog(queryDialog);
        queryDialog.setHideInput((evt.getQueryType()&CrossfireCommandQueryEvent.HIDEINPUT) != 0);
    }

    private void showGUIStart()
    {
        jxcWindowRenderer.clearGUI();
        jxcWindowRenderer.setCurrentGui(myskin.getStartInterface());
        tooltipManager.reset();
    }

    private void showGUIMeta()
    {
        jxcWindowRenderer.clearGUI();
        final Gui newGui = myskin.getMetaInterface();
        jxcWindowRenderer.setCurrentGui(newGui);
        newGui.activateDefaultElement();
        tooltipManager.reset();
    }

    private void showGUIMain()
    {
        jxcWindowRenderer.clearGUI();
        Gui newGui = myskin.getMainInterface();
        jxcWindowRenderer.setCurrentGui(newGui);
        tooltipManager.reset();
    }

    /** {@inheritDoc} */
    public void paint(final Graphics g)
    {
        jxcWindowRenderer.repaint();
    }

    /**
     * Return the current repeat count and reset it to zero.
     *
     * @return The current repeat count.
     */
    public int getRepeatCount()
    {
        final int oldRepeatCount = this.repeatCount;
        resetRepeatCount();
        return oldRepeatCount;
    }

    /**
     * Reset the current repeat count to zero.
     */
    private void resetRepeatCount()
    {
        this.repeatCount = 0;
    }

    /**
     * Add a digit to the current repeat count.
     *
     * @param digit The digit (0-9) to add.
     */
    private void addToRepeatCount(final int digit)
    {
        assert 0 <= digit && digit <= 9;
        this.repeatCount = (10*repeatCount+digit)%100000;
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
                myskin = new JXCSkinDirLoader(dir);
            }
            else
            {
                // fallback: built-in resource
                myskin = new JXCSkinClassLoader("com/realtime/crossfire/jxclient/skins/"+skinName);
            }
            myskin.load(myserver, this);
        }
        catch (final JXCSkinException ex)
        {
            System.err.println("cannot load skin "+skinName+": "+ex.getMessage());
            return false;
        }

        myskin.executeInitEvents();
        queryDialog = myskin.getDialogQuery();
        keybindDialog = myskin.getDialogKeyBind();
        dialogQuit = myskin.getDialogQuit();
        dialogDisconnect = myskin.getDialogDisconnect();
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
        for (final Gui dialog : myskin)
        {
            final GUIText textArea3 = dialog.activateCommandInput();
            if (textArea3 != null)
            {
                openDialog(dialog);
                dialog.setAutoCloseOnDeactivate(textArea3);
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
        return myskin;
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
        final File file;
        try
        {
            file = Filenames.getKeybindingsFile();
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read keybindings file: "+ex.getMessage());
            return;
        }

        try
        {
            keyBindings.loadKeyBindings(file, this);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot read keybindings file "+file+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Save the keybindings to the backing file.
     */
    private void saveKeybindings()
    {
        final File file;
        try
        {
            file = Filenames.getKeybindingsFile();
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write keybindings file: "+ex.getMessage());
            return;
        }

        try
        {
            keyBindings.saveKeyBindings(file);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write keybindings file "+file+": "+ex.getMessage());
            return;
        }
    }

    /**
     * Return the key bindings instance for this window.
     *
     * @return The key bindings.
     */
    public KeyBindings getKeyBindings()
    {
        return keyBindings;
    }

    /**
     * Execute a command or a list of commands. The commands may be a client-
     * or a server-sided command.
     *
     * @param commands The commands to execute.
     */
    public void executeCommand(final String commands)
    {
        for (final String command : commands.trim().split(" *; *"))
        {
            executeSingleCommand(command);
        }
    }

    /**
     * Execute a command. The command may be a client- or a server-sided
     * command.
     *
     * @param command The command to execute.
     */
    public void executeSingleCommand(final String command)
    {
        if (!commands.execute(command))
        {
            sendNcom(command);
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
}

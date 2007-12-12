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
import com.realtime.crossfire.jxclient.gui.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIText;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBinding;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindingState;
import com.realtime.crossfire.jxclient.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.CrossfirePlayerListener;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
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
import com.realtime.crossfire.jxclient.stats.PoisonWatcher;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellChangedEvent;
import com.realtime.crossfire.jxclient.spells.SpellListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.MouseInputListener;
import javax.swing.JFrame;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public class JXCWindow extends JFrame implements KeyListener, MouseInputListener, CrossfireDrawextinfoListener, CrossfireQueryListener
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
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    private long framecount = 0;

    /**
     * The global experience table.
     */
    private final ExperienceTable experienceTable = new ExperienceTable();

    private final CrossfireServerConnection myserver = new CrossfireServerConnection(experienceTable);

    private final String semaphore_drawing = "semaphore_drawing";

    private Spell mycurrentspell = null;

    /**
     * The shortcuts for this window.
     */
    private final Shortcuts shortcuts = new Shortcuts(this);

    private Gui mydialog_query = new Gui();
    private Gui mydialog_book = new Gui();
    private Gui mydialog_keybind = new Gui();

    /**
     * The "really quit?" dialog. Set to <code>null</code> if the skin does not
     * define this dialog.
     */
    private Gui dialogQuit = null;

    private JXCSkin myskin = null;

    private final KeyBindings keyBindings = new KeyBindings();

    private final boolean[] key_shift = new boolean[] { false, false, false, false };

    private KeyBindingState keyBindingState = null;

    private final List<SpellListener> myspelllisteners = new ArrayList<SpellListener>();

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
    private final PoisonWatcher poisonWatcher = new PoisonWatcher(ItemsList.getStats(), myserver);

    /**
     * The commands instance for this window.
     */
    private final Commands commands = new Commands(this);

    /**
     * The default repeat counter for "ncom" commands.
     */
    private int repeatCount = 0;

    /**
     * The gui element in which the mouse is.
     */
    private GUIElement mouseElement = null;

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

    /**
     * Create a new instance.
     *
     * @param boolean Whether GUI elements should be highlighted.
     *
     * @param settings The settings instance to use.
     */
    public JXCWindow(final boolean debugGui, final Settings settings)
    {
        super(TITLE_PREFIX);
        this.debugGui = debugGui;
        optionManager = new OptionManager(settings);
        addWindowFocusListener(windowFocusListener);
    }

    public boolean checkRun()
    {
        return is_run_active;
    }

    public boolean checkFire()
    {
        return false;
    }

    public void terminateScript(final ScriptProcess sp)
    {
        myserver.removeScriptMonitor(sp);
    }

    public void addSpellListener(final SpellListener s)
    {
        myspelllisteners.add(s);
    }

    public void removeSpellListener(final SpellListener s)
    {
        myspelllisteners.remove(s);
    }

    public void setCurrentSpell(final Spell s)
    {
        mycurrentspell = s;
        final SpellChangedEvent evt = new SpellChangedEvent(this, s);
        for (final SpellListener sl : myspelllisteners)
        {
            sl.spellChanged(evt);
        }
    }

    public Spell getCurrentSpell()
    {
        return mycurrentspell;
    }

    public void createKeyBinding(final GUICommandList cmdlist)
    {
        keyBindingState = new KeyBindingState(cmdlist);
        jxcWindowRenderer.openDialog(mydialog_keybind);
    }

    public void removeKeyBinding()
    {
        keyBindingState = new KeyBindingState(null);
        jxcWindowRenderer.openDialog(mydialog_keybind);
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
        if (dialog == mydialog_query)
        {
            jxcWindowRenderer.setHideInput(false);
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
            if (dialog == mydialog_query)
            {
                jxcWindowRenderer.setHideInput(false);
            }
        }
    }

    /**
     * Close a dialog. Does nothing if the given dialog is not open.
     *
     * @param dialog The dialog to close.
     */
    public void closeDialog(final Gui dialog)
    {
        jxcWindowRenderer.closeDialog(dialog);
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
        return jxcWindowRenderer.isDialogOpen(dialog);
    }

    /**
     * Close the "query" dialog. Does nothing if the dialog is not open.
     */
    public void closeQueryDialog()
    {
        closeDialog(mydialog_query);
    }

    private void initRendering(final boolean fullScreen)
    {
        jxcWindowRenderer.initRendering(fullScreen);
        framecount = 0;
        loadKeybindings();
        loadShortcuts();
    }

    public void endRendering()
    {
//        final long endtime = System.nanoTime();
//        final long totaltime = endtime-starttime;
//        System.out.println(framecount+" frames in "+totaltime/1000000+" ms - "+(framecount*1000/(totaltime/1000000))+" FPS");
        jxcWindowRenderer.endRendering();
        saveShortcuts();
        saveKeybindings();
        optionManager.saveOptions();
        System.exit(0);
    }

    private void initGUI(final int id)
    {
        switch (id)
        {
        case GUI_START:
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
            jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.META);
            showGUIMeta();
            break;

        case GUI_MAIN:
            jxcWindowRenderer.setGuiState(JXCWindowRenderer.GuiState.LOGIN);
            showGUIMain();
            break;
        }
    }

    public void changeGUI(final int id)
    {
        initGUI(id);
    }

    public void init(final int w, final int h, final int b, final int f, final String skinName, final boolean fullScreen, final String server)
    {
        windowWidth = w;
        windowHeight = h;
        CfMapUpdater.processNewmap();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
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
            if (server != null)
            {
                connect(server, 13327);
            }
            else
            {
                initGUI(DISABLE_START_GUI ? GUI_METASERVER : GUI_START);
            }
            for (;;)
            {
                synchronized(semaphore_drawing)
                {
                    jxcWindowRenderer.redrawGUI();
                }
                framecount++;
                Thread.sleep(10);
            }
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
        myserver.addCrossfireDrawextinfoListener(this);
        myserver.addCrossfireQueryListener(this);
        setTitle(TITLE_PREFIX+" - "+hostname);
        ItemsList.getItemsManager().addCrossfirePlayerListener(crossfirePlayerListener);
        initGUI(GUI_MAIN);
        myserver.connect(hostname, port);
        Faces.setFacesCallback(myserver);
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
                    jxcWindowRenderer.closeDialog(mydialog_keybind);
                }
                else if (dialogQuit == null)
                {
                    endRendering();
                }
                else if (!jxcWindowRenderer.openDialog(dialogQuit))
                {
                    jxcWindowRenderer.closeDialog(dialogQuit);
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
                    jxcWindowRenderer.closeDialog(mydialog_keybind);
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

    public void mouseClicked(final MouseEvent e)
    {
    }

    public void mouseEntered(final MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            final GUIElement elected = findElement(e, true);
            enterElement(elected, e);
        }
    }

    public void mouseExited(final MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            leaveElement(e);
        }
    }

    public void mousePressed(final MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            final GUIElement elected = findElement(e, false);
            if (elected != null)
            {
                elected.mousePressed(e);
                if (elected instanceof ActivatableGUIElement)
                {
                    if (elected.isActive())
                    {
                        elected.getGui().setActiveElement((ActivatableGUIElement)elected);
                    }
                    else
                    {
                        elected.getGui().setActiveElement(null);
                    }
                }
            }
        }
    }

    public void mouseReleased(final MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            final GUIElement elected = findElement(e, false);
            if (elected != null)
            {
                elected.mouseReleased(e);
            }
        }
    }

    /**
     * Find the gui element for a given {@link MouseEvent}. If a gui element
     * was found, update the event mouse coordinates to be relative to the gui
     * element.
     *
     * @param e The mouse event to process.
     *
     * @param ignoreButtons If set, match elements even if no mouse button is
     * pressed.
     *
     * @return The gui element found, or <code>null</code> if none was found.
     */
    private GUIElement findElement(final MouseEvent e, final boolean ignoreButtons)
    {
        GUIElement elected = null;

        for (final Gui dialog : jxcWindowRenderer.getOpenDialogs())
        {
            if (!dialog.isHidden(jxcWindowRenderer.getGuiState()))
            {
                elected = manageMouseEvents(dialog, e, ignoreButtons);
                if (elected != null)
                {
                    break;
                }
            }
            if (dialog.isModal())
            {
                return null;
            }
        }

        if (elected == null)
        {
            elected = manageMouseEvents(jxcWindowRenderer.getCurrentGui(), e, ignoreButtons);
        }

        if (elected != null)
        {
            e.translatePoint(-elected.getX()-jxcWindowRenderer.getOffsetX(), -elected.getY()-jxcWindowRenderer.getOffsetY());
        }

        return elected;
    }

    private GUIElement manageMouseEvents(final Gui gui, final MouseEvent e, final boolean ignoreButtons)
    {
        final int x = e.getX()-jxcWindowRenderer.getOffsetX();
        final int y = e.getY()-jxcWindowRenderer.getOffsetY();
        final int b = e.getButton();
        final GUIElement elected;
        switch (b)
        {
        case MouseEvent.BUTTON1:
        case MouseEvent.BUTTON2:
        case MouseEvent.BUTTON3:
            elected = gui.getElementFromPoint(x, y);
            break;

        default:
            elected = ignoreButtons ? gui.getElementFromPoint(x, y) : null;
            break;
        }
        return elected;
    }

    public void mouseDragged(final MouseEvent e)
    {
    }

    public void mouseMoved(final MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            final GUIElement elected = findElement(e, true);
            enterElement(elected, e);
        }
    }

    public void commandDrawextinfoReceived(final CrossfireCommandDrawextinfoEvent evt)
    {
        switch (evt.getType())
        {
        case CrossfireServerConnection.MSG_TYPE_BOOK:
            jxcWindowRenderer.openDialog(mydialog_book);
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
        jxcWindowRenderer.openDialog(mydialog_query);
        jxcWindowRenderer.setHideInput((evt.getQueryType()&CrossfireCommandQueryEvent.HIDEINPUT) != 0);
    }

    private void clearGUI()
    {
        jxcWindowRenderer.clearGUI();
    }

    private void showGUIStart()
    {
        clearGUI();
        Gui newGui;
        try
        {
            newGui = myskin.getStartInterface();
        }
        catch (final JXCSkinException e)
        {
            e.printStackTrace();
            endRendering();
            newGui = null;
        }
        jxcWindowRenderer.setCurrentGui(newGui);
        tooltipManager.reset();
    }

    private void showGUIMeta()
    {
        clearGUI();
        Gui newGui;
        try
        {
            newGui = myskin.getMetaInterface();
        }
        catch (final JXCSkinException e)
        {
            e.printStackTrace();
            endRendering();
            newGui = null;
        }
        jxcWindowRenderer.setCurrentGui(newGui);
        newGui.activateDefaultElement();
        tooltipManager.reset();
    }

    private void showGUIMain()
    {
        clearGUI();
        Gui newGui;
        try
        {
            newGui = myskin.getMainInterface();
            mydialog_query = myskin.getDialogQuery();
            mydialog_book = myskin.getDialogBook(1);

            mydialog_keybind = myskin.getDialogKeyBind();
            dialogQuit = myskin.getDialogQuit();
        }
        catch (final JXCSkinException e)
        {
            e.printStackTrace();
            endRendering();
            newGui = null;
        }
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
     * Display the tooltip for a gui element.
     *
     * @param guiElement The gui element to show the tooltip of.
     */
    public void setTooltipElement(final GUIElement guiElement)
    {
        tooltipManager.setElement(guiElement);
    }

    /**
     * Undisplay the tooltip for a gui element.
     *
     * @param guiElement The gui element to remove the tooltip of.
     */
    public void unsetTooltipElement(final GUIElement guiElement)
    {
        tooltipManager.unsetElement(guiElement);
    }

    /**
     * Update the tooltip text for a gui element.
     *
     * @param guiElement The gui element to process.
     */
    public void updateTooltipElement(final GUIElement guiElement)
    {
        tooltipManager.updateElement(guiElement);
    }

    /**
     * Called when the mouse enters an element or is moved within an element.
     * It checks whether the element has changed and generates {@link
     * GUIElement#mouseEntered(MouseEvent)} or {@link
     * GUIElement#mouseExited(MouseEvent)}.
     *
     * @param element The gui element the mouse is in.
     *
     * @param e The event that caused this call.
     */
    private void enterElement(final GUIElement element, final MouseEvent e)
    {
        if (element == null)
        {
            leaveElement(e);
            return;
        }

        if (mouseElement != null)
        {
            if (mouseElement == element)
            {
                return;
            }

            mouseElement.mouseExited(e);
        }

        mouseElement = element;
        mouseElement.mouseEntered(e);
        if (debugGui)
        {
            jxcWindowRenderer.repaint();
        }
    }

    /**
     * Called when the mouse has left an element. It generates {@link
     * GUIElement#mouseExited(MouseEvent)}.
     *
     * @param e The event that caused this call.
     */
    private void leaveElement(final MouseEvent e)
    {
        if (mouseElement != null)
        {
            mouseElement.mouseExited(e);
            mouseElement = null;
            if (debugGui)
            {
                jxcWindowRenderer.repaint();
            }
        }
    }

    /**
     * Set the tooltip to use, or <code>null</code> if no tooltips should be
     * shown.
     *
     * @param tooltip The tooltip to use, or <code>null</code>.
     */
    public void setTooltip(final AbstractLabel tooltip)
    {
        jxcWindowRenderer.setTooltip(tooltip);
    }

    /**
     * Return the tooltip {@link AbstractLabel} for this window.
     *
     * @return The tooltip label for this window.
     */
    public AbstractLabel getTooltip()
    {
        return jxcWindowRenderer.getTooltip();
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
     * Return the gui element in which the mouse is.
     *
     * @return The gui element in which the mouse is.
     */
    public GUIElement getMouseElement()
    {
        return mouseElement;
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
}

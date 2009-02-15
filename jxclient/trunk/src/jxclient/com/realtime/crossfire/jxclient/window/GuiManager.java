package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiFactory;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.label.GUIOneLineLabel;
import com.realtime.crossfire.jxclient.gui.list.GUIMetaElementList;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.scripts.ScriptManager;
import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.JXCSkinException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Maintains the application's main GUI state.
 * @author Andreas Kirschbaum
 */
public class GuiManager
{
    /**
     * Whether gui debugging is active.
     */
    private final boolean debugGui;

    /**
     * The semaphore used to synchronized drawing operations.
     */
    private final Object semaphoreDrawing;

    /**
     * The associated {@link JXCWindow} instance.
     */
    private final JXCWindow window;

    /**
     * The currently active skin. Set to <code>null</code> if no skin is set.
     */
    private JXCSkin skin = null;

    /**
     * The {@link JXCWindowRenderer} used to paint the gui.
     */
    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link GuiFactory} for creating {@link Gui} instances.
     */
    private GuiFactory guiFactory = null;

    /**
     * The query dialog.
     */
    private Gui queryDialog;

    /**
     * The keybindings dialog.
     */
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

    /**
     * The "connect in progress" dialog. Set to <code>null</code> if the skin
     * does not define this dialog.
     */
    private Gui dialogConnect = null;

    /**
     * The "message" field within {@link #dialogConnect}. Set to
     * <code>null</code> if the dialog does not define a "message" label.
     */
    private AbstractLabel dialogConnectLabel = null;

    /**
     * Whether the currently shown query dialog is the character name prompt.
     */
    private boolean currentQueryDialogIsNamePrompt = false;

    /**
     * The mouse tracker.
     */
    public final MouseTracker mouseTracker;

    /**
     * The commands instance for this window.
     */
    private Commands commands;

    /**
     * The {@link TooltipManager} for this window.
     */
    private final TooltipManager tooltipManager;

    /**
     * The {@link Settings} to use.
     */
    private final Settings settings;

    /**
     * The {@link JXCConnection} to use.
     */
    private JXCConnection connection;

    /**
     * The {@link CrossfireServerConnection} instance to monitor.
     */
    private final CrossfireServerConnection server;

    /**
     * Called periodically to update the display contents.
     */
    private final ActionListener actionListener = new ActionListener()
    {
        /** {@inheritDoc} */
        @Override
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

    /**
     * The {@link CrossfireDrawextinfoListener} attached to {@link #server}.
     */
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, String message)
        {
            final Gui dialog;
            switch (type)
            {
            case MessageTypes.MSG_TYPE_BOOK:
                dialog = skin.getDialogBook(1);
                final GUIOneLineLabel title = dialog.getFirstElementEndingWith(GUIOneLineLabel.class, "_title");
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

            final AbstractLabel label = dialog.getFirstElementNotEndingWith(AbstractLabel.class, "_title");
            if (label != null)
            {
                label.setText(message);
            }
            else
            {
                final GUILabelLog log = dialog.getFirstElement(GUILabelLog.class);
                if (log != null)
                {
                    log.updateText(message);
                }
            }
            windowRenderer.openDialog(dialog, false);
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
            server.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            setGuiState(RendererGuiState.START);
            showGUIStart();
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            server.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            setGuiState(RendererGuiState.META);
            showGUIMeta();
            activateMetaserverGui();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting()
        {
            server.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            setGuiState(RendererGuiState.LOGIN);
            showGUIMain();
            if (dialogConnect != null)
            {
                windowRenderer.openDialog(dialogConnect, false);
                updateConnectLabel(ClientSocketState.CONNECTING);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(final ClientSocketState clientSocketState)
        {
            updateConnectLabel(clientSocketState);
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            if (dialogConnect != null)
            {
                windowRenderer.closeDialog(dialogConnect);
            }
        }
    };

    /**
     * Creates a new instance.
     * @param window the associated window
     * @param debugGui whether gui debugging is active
     * @param semaphoreDrawing the semaphore to use for drawing operations
     * @param semaphoreRedraw the semaphore to use for redrawing operations
     * @param tooltipManager the tooltip manager to update
     * @param settings the settings to use
     * @param server the crossfire server connection to monitor
     */
    public GuiManager(final JXCWindow window, final boolean debugGui, final Object semaphoreDrawing, final Object semaphoreRedraw, final TooltipManager tooltipManager, final Settings settings, final CrossfireServerConnection server)
    {
        this.debugGui = debugGui;
        this.semaphoreDrawing = semaphoreDrawing;
        this.window = window;
        this.tooltipManager = tooltipManager;
        this.settings = settings;
        this.server = server;
        mouseTracker = new MouseTracker(debugGui);
        windowRenderer = new JXCWindowRenderer(window, mouseTracker, semaphoreRedraw);
        window.addConnectionStateListener(guiStateListener);
        mouseTracker.init(windowRenderer);
    }

    @Deprecated
    public void init(final ScriptManager scriptManager, final CommandQueue commandQueue, final CrossfireServerConnection server, final OptionManager optionManager)
    {
        commands = new Commands(window, windowRenderer, commandQueue, server, scriptManager, optionManager, this);
        guiFactory = new GuiFactory(debugGui ? mouseTracker : null, commands, this);
        windowRenderer.init(guiFactory.newGui());
        queryDialog = guiFactory.newGui();
        keybindDialog = guiFactory.newGui();
    }

    /**
     * A "player" protocol command has been received.
     */
    public void playerReceived()
    {
        if (windowRenderer.getGuiState() == RendererGuiState.NEWCHAR)
        {
            openDialogByName("messages"); // hack for race selection
        }
        windowRenderer.setGuiState(RendererGuiState.PLAYING);
    }

    /**
     * Operns the "quit" dialog. Does nothing if the dialog is open.
     * @return whether the dialog has been opened
     */
    public boolean openQuitDialog()
    {
        if (dialogQuit == null)
        {
            return false;
        }

        if (dialogDisconnect != null)
        {
            windowRenderer.closeDialog(dialogDisconnect);
        }
        windowRenderer.openDialog(dialogQuit, false);
        return true;
    }

    /**
     * The ESC key has been pressed.
     * @param closeKeybindDialog whether the keybindings dialog should be closed
     * @param status the current window status
     * @return whether how the key has been consumed: 0=ignore key,
     * 1=disconnect from server, quit=quit application
     */
    public int escPressed(final boolean closeKeybindDialog, final ConnectionStatus status)
    {
        if (closeKeybindDialog)
        {
            windowRenderer.closeDialog(keybindDialog);
        }
        else if (deactivateCommandInput())
        {
            // ignore
        }
        else if (status != ConnectionStatus.UNCONNECTED)
        {
            if (dialogDisconnect == null)
            {
                return 1;
            }
            else if (windowRenderer.openDialog(dialogDisconnect, false))
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
                return 2;
            }
            else if (windowRenderer.openDialog(dialogQuit, false))
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
        return 0;
    }

    /**
     * Opens the "query" dialog.
     * @param prompt the query prompt
     * @param queryType the query type
     */
    public void openQueryDialog(final String prompt, final int queryType)
    {
        windowRenderer.openDialog(queryDialog, false);
        queryDialog.setHideInput((queryType&CrossfireQueryListener.HIDEINPUT) != 0);
        currentQueryDialogIsNamePrompt = prompt.startsWith("What is your name?");
        if (currentQueryDialogIsNamePrompt)
        {
            final String playerName = settings.getString("player_"+connection.getHostname(), "");
            if (playerName.length() > 0)
            {
                final GUIText textArea = queryDialog.getFirstElement(GUIText.class);
                if (textArea != null)
                {
                    textArea.setText(playerName);
                }
            }
        }
        else if (prompt.startsWith("[y] to roll new stats")
        || prompt.startsWith("Welcome, Brave New Warrior!"))
        {
            windowRenderer.setGuiState(RendererGuiState.NEWCHAR);
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
            openDialog(queryDialog, false); // raise dialog
        }
    }

    /**
     * Opens a dialog. Raises the dialog if it is open.
     * @param dialog the dialog to show
     * @param autoCloseOnDeactivate whether the dialog should auto-close when
     * it becomes inactive; ignored if the dialog is already open
     */
    public void openDialog(final Gui dialog, final boolean autoCloseOnDeactivate)
    {
        windowRenderer.openDialog(dialog, autoCloseOnDeactivate);
        if (dialog == queryDialog)
        {
            dialog.setHideInput(false);
        }
    }

    /**
     * Toggles a dialog.
     * @param dialog the dialog to toggle
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
     * Closes the "query" dialog. Does nothing if the dialog is not open.
     */
    public void closeQueryDialog()
    {
        windowRenderer.closeDialog(queryDialog);
    }

    public void initRendering(final boolean fullScreen)
    {
        windowRenderer.init(skin.getResolution());
        windowRenderer.initRendering(fullScreen);
        DialogStateParser.load(skin, windowRenderer);
    }

    /**
     * Opens a dialog by name.
     * @param name the dialog name
     * @return whether the dialog exists
     */
    private boolean openDialogByName(final String name)
    {
        final Gui dialog;
        try
        {
            dialog = skin.getDialog(name);
        }
        catch (final JXCSkinException ex)
        {
            return false;
        }

        openDialog(dialog, false);
        return true;
    }

    /**
     * Closes a dialog by name.
     * @param name the dialog name
     */
    private void closeDialogByName(final String name)
    {
        final Gui dialog;
        try
        {
            dialog = skin.getDialog(name);
        }
        catch (final JXCSkinException ex)
        {
            // ignore
            return;
        }
        windowRenderer.closeDialog(dialog);
    }

    public void terminate()
    {
        timer.stop();
    }

    /**
     * Closes all transient dialogs: disconnect, quit, connect, query, and book
     * dialogs.
     */
    public void closeTransientDialogs()
    {
        if (dialogDisconnect != null)
        {
            windowRenderer.closeDialog(dialogDisconnect);
        }
        if (dialogQuit != null)
        {
            windowRenderer.closeDialog(dialogQuit);
        }
        if (dialogConnect != null)
        {
            windowRenderer.closeDialog(dialogConnect);
        }
        windowRenderer.closeDialog(queryDialog);
        windowRenderer.closeDialog(skin.getDialogBook(1));
    }

    /**
     * Updates the current gui state.
     * @param rendererGuiState the new gui state
     */
    private void setGuiState(final RendererGuiState rendererGuiState)
    {
        windowRenderer.setGuiState(rendererGuiState);
    }

    /**
     * Called when the server selection GUI becomes active. Selects the last
     * used server entry.
     */
    private void activateMetaserverGui()
    {
        final String serverName = settings.getString("server", "crossfire.metalforge.net");
        if (serverName.length() > 0)
        {
            final GUIMetaElementList metaElementList = windowRenderer.getCurrentGui().getFirstElement(GUIMetaElementList.class);
            if (metaElementList != null)
            {
                metaElementList.setSelectedHostname(serverName);
            }
        }
    }

    @Deprecated
    public void init3()
    {
        window.addMouseListener(mouseTracker);
        window.addMouseMotionListener(mouseTracker);
        timer.start();
    }

    /**
     * Opens the keybinding dialog. Does nothing if the dialog is opened.
     */
    public void openKeybindDialog()
    {
        windowRenderer.openDialog(keybindDialog, false);
    }

    /**
     * Closes the keybinding dialog. Does nothing if the dialog is not opened.
     */
    public void closeKeybindDialog()
    {
        windowRenderer.closeDialog(keybindDialog);
    }

    /**
     * Sets the current player name. Does nothing if not currently in the
     * character name prompt.
     * @param playerName the player name
     */
    public void updatePlayerName(final String playerName)
    {
        if (currentQueryDialogIsNamePrompt)
        {
            settings.putString("player_"+connection.getHostname(), playerName);
        }
    }

    /**
     * Returns the tooltip manager for this window.
     * @return the tooltip manager for this window
     */
    @Deprecated
    public TooltipManager getTooltipManager()
    {
        return tooltipManager;
    }

    /**
     * Returns the window renderer instance for this window.
     * @return the window renderer
     */
    @Deprecated
    public JXCWindowRenderer getWindowRenderer()
    {
        return windowRenderer;
    }

    /**
     * Returns the current skin.
     * @return the skin
     */
    @Deprecated
    public JXCSkin getSkin()
    {
        return skin;
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
     * Activates the command input text field. If the skin defined more than
     * one input field, the first matching one is selected.
     * <p>If neither the main gui nor any visible dialog has an input text
     * field, invisible guis are checked as well. If one is found, it is made
     * visible.
     * @return the command input text field, or <code>null</code> if the skin
     * has no command input text field defined
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
                    openDialog(dialog, false); // raise dialog
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
                openDialog(dialog, true);
                return textArea3;
            }
        }

        return null;
    }

    /**
     * Activates the command input text field. If the skin defined more than one
     * input field, the first matching one is selected.
     * <p>If neither the main gui nor any visible dialog has an input text
     * field, invisible guis are checked as well. If one is found, it is made
     * visible.
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
     * Unsets the current skin.
     */
    public void unsetSkin()
    {
        if (skin != null)
        {
            skin.detach();
            skin = null;
        }

        queryDialog = null;
        keybindDialog = null;
        dialogQuit = null;
        dialogDisconnect = null;
    }

    /**
     * Sets a new skin.
     * @param skin the new skin
     */
    public void setSkin(final JXCSkin skin)
    {
        this.skin = skin;
        skin.attach(this);
        queryDialog = skin.getDialogQuery();
        keybindDialog = skin.getDialogKeyBind();
        dialogQuit = skin.getDialogQuit();
        dialogDisconnect = skin.getDialogDisconnect();
        dialogConnect = skin.getDialogConnect();
        dialogConnectLabel = dialogConnect == null ? null : dialogConnect.getFirstElement(AbstractLabel.class, "message");
    }

    /**
     * Does a full repaint of the GUI.
     */
    public void repaint()
    {
        windowRenderer.repaint();
    }

    /**
     * Displays the "start" GUI.
     */
    private void showGUIStart()
    {
        windowRenderer.clearGUI(guiFactory.newGui());
        windowRenderer.setCurrentGui(skin.getStartInterface());
        tooltipManager.reset();
    }

    /**
     * Displays the "server selection" GUI.
     */
    private void showGUIMeta()
    {
        windowRenderer.clearGUI(guiFactory.newGui());
        final Gui newGui = skin.getMetaInterface();
        windowRenderer.setCurrentGui(newGui);
        newGui.activateDefaultElement();
        tooltipManager.reset();
    }

    /**
     * Displays the "main" GUI.
     */
    private void showGUIMain()
    {
        windowRenderer.clearGUI(guiFactory.newGui());
        final Gui newGui = skin.getMainInterface();
        windowRenderer.setCurrentGui(newGui);
        tooltipManager.reset();
    }

    public void term()
    {
        windowRenderer.endRendering();
        DialogStateParser.save(skin, windowRenderer);
    }

    @Deprecated
    public Commands getCommands()
    {
        return commands;
    }

    @Deprecated
    public void setConnection(final JXCConnection connection)
    {
        this.connection = connection;
    }

    /**
     * Updates the "message" field of the connect dialog. Does nothing if
     * the dialog is not open, does not exist, or if the dialog does not
     * define a "message" field.
     * @param clientSocketState the client socket state
     */
    private void updateConnectLabel(final ClientSocketState clientSocketState)
    {
        if (dialogConnectLabel != null)
        {
            String message = null;
            switch (clientSocketState)
            {
            case CONNECTING:
                message = "Connecting...";
                break;

            case VERSION:
                message = "Exchanging version...";
                break;

            case SETUP:
                message = "Exchanging configuration...";
                break;

            case REQUESTINFO:
                message = "Requesting information...";
                break;

            case ADDME:
                message = "Joining the game...";
                break;

            case CONNECTED:
                message = "Done.";
                break;
            }

            assert message != null;
            dialogConnectLabel.setText(message);
        }
    }
}

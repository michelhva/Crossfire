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

package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import com.realtime.crossfire.jxclient.gui.commands.NoSuchCommandException;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiFactory;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.MouseTracker;
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiState;
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
import com.realtime.crossfire.jxclient.server.GuiStateListener;
import com.realtime.crossfire.jxclient.server.GuiStateManager;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains the application's main GUI state.
 * @author Andreas Kirschbaum
 */
public class GuiManager
{
    /**
     * The semaphore used to synchronized drawing operations.
     */
    @NotNull
    private final Object semaphoreDrawing;

    /**
     * The object to be notified when the application terminates.
     */
    @NotNull
    private final Object terminateSync;

    /**
     * The currently active skin. Set to <code>null</code> if no skin is set.
     */
    @Nullable
    private JXCSkin skin = null;

    /**
     * The {@link JXCWindowRenderer} used to paint the gui.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link GuiFactory} for creating {@link Gui} instances.
     */
    @Nullable
    private GuiFactory guiFactory = null;

    /**
     * The query dialog.
     */
    @Nullable
    private Gui queryDialog;

    /**
     * The keybindings dialog.
     */
    @Nullable
    private Gui keybindDialog;

    /**
     * The key bindings manager for this window.
     */
    @NotNull
    private final KeybindingsManager keybindingsManager;

    /**
     * The "really quit?" dialog. Set to <code>null</code> if the skin does not
     * define this dialog.
     */
    @Nullable
    private Gui dialogQuit = null;

    /**
     * The "really disconnect?" dialog. Set to <code>null</code> if the skin
     * does not define this dialog.
     */
    @Nullable
    private Gui dialogDisconnect = null;

    /**
     * The "connect in progress" dialog. Set to <code>null</code> if the skin
     * does not define this dialog.
     */
    @Nullable
    private Gui dialogConnect = null;

    /**
     * The "message" field within {@link #dialogConnect}. Set to
     * <code>null</code> if the dialog does not define a "message" label.
     */
    @Nullable
    private AbstractLabel dialogConnectLabel = null;

    /**
     * Whether the currently shown query dialog is the character name prompt.
     */
    private boolean currentQueryDialogIsNamePrompt = false;

    /**
     * The commands instance for this window.
     */
    @NotNull
    private final Commands commands;

    /**
     * The {@link TooltipManager} for this window.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link Settings} to use.
     */
    @NotNull
    private final Settings settings;

    /**
     * The {@link JXCConnection} to use.
     */
    @NotNull
    private JXCConnection connection;

    /**
     * The {@link CrossfireServerConnection} instance to monitor.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * Called periodically to update the display contents.
     */
    @NotNull
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
    @NotNull
    private final Timer timer = new Timer(10, actionListener);

    /**
     * The {@link CrossfireDrawextinfoListener} attached to {@link #server}.
     */
    @NotNull
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, @NotNull String message)
        {
            @Nullable final Gui dialog;
            switch (type)
            {
            case MessageTypes.MSG_TYPE_BOOK:
                dialog = skin.getDialogBook(1);
                final AbstractLabel title = dialog.getFirstElementEndingWith(GUIOneLineLabel.class, "_title");
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
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void start()
        {
            server.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            windowRenderer.setGuiState(RendererGuiState.START);
            showGUIStart();
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            server.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            windowRenderer.setGuiState(RendererGuiState.META);
            showGUIMeta();
            activateMetaserverGui();
        }

        /** {@inheritDoc} */
        @Override
        public void preConnecting(@NotNull final String serverInfo)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo)
        {
            server.setMapSize(skin.getMapWidth(), skin.getMapHeight());
            server.setNumLookObjects(skin.getNumLookObjects());
            server.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            windowRenderer.setGuiState(RendererGuiState.LOGIN);
            showGUIMain();
            if (dialogConnect != null)
            {
                windowRenderer.openDialog(dialogConnect, false);
                updateConnectLabel(ClientSocketState.CONNECTING, null);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState)
        {
            updateConnectLabel(clientSocketState, null);
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            if (dialogConnect != null)
            {
                closeDialog(dialogConnect);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason)
        {
            if (dialogConnect != null)
            {
                windowRenderer.openDialog(dialogConnect, false);
                updateConnectLabel(ClientSocketState.CONNECT_FAILED, reason);
            }
        }
    };

    /**
     * The {@link CommandCallback}.
     */
    @NotNull
    private final CommandCallback commandCallback = new CommandCallback()
    {
        /** {@inheritDoc} */
        @Override
        public void quitApplication()
        {
            terminate();
        }

        /** {@inheritDoc} */
        @Override
        public void openDialog(@NotNull final Gui dialog)
        {
            GuiManager.this.openDialog(dialog, false);
        }

        /** {@inheritDoc} */
        @Override
        public void toggleDialog(@NotNull final Gui dialog)
        {
            GuiManager.this.toggleDialog(dialog);
        }

        /** {@inheritDoc} */
        @Override
        public void closeDialog(@NotNull final Gui dialog)
        {
            GuiManager.this.closeDialog(dialog);
        }

        /** {@inheritDoc} */
        @NotNull
        @Override
        public CommandList getCommandList(@NotNull final String args) throws NoSuchCommandException
        {
            try
            {
                return skin.getCommandList(args);
            }
            catch (final JXCSkinException ex)
            {
                throw new NoSuchCommandException(ex.getMessage());
            }
        }

        /** {@inheritDoc} */
        @Override
        public void updatePlayerName(@NotNull final String playerName)
        {
            GuiManager.this.updatePlayerName(playerName);
        }

        /** {@inheritDoc} */
        @Override
        public void activateCommandInput(@NotNull final String newText)
        {
            GuiManager.this.activateCommandInput(newText);
        }

        /** {@inheritDoc} */
        @Override
        public boolean createKeyBinding(final boolean perCharacter, @NotNull final CommandList commandList)
        {
            return GuiManager.this.createKeyBinding(perCharacter, commandList);
        }

        /** {@inheritDoc} */
        @Override
        public boolean removeKeyBinding(final boolean perCharacter)
        {
            return GuiManager.this.removeKeyBinding(perCharacter);
        }
    };

    /**
     * Creates a new instance.
     * @param guiStateManager the gui state manager to watch
     * @param semaphoreDrawing the semaphore to use for drawing operations
     * @param terminateSync the object to be notified when the application
     * terminates
     * @param tooltipManager the tooltip manager to update
     * @param settings the settings to use
     * @param server the crossfire server connection to monitor
     * @param macros the macros instance to use
     * @param windowRenderer the window renderer to use
     * @param scriptManager the script manager to use
     * @param commandQueue the command queue to use
     * @param optionManager the option manager to use
     * @param mouseTracker the mouse tracker to use
     */
    public GuiManager(@NotNull final GuiStateManager guiStateManager, @NotNull final Object semaphoreDrawing, @NotNull final Object terminateSync, @NotNull final TooltipManager tooltipManager, @NotNull final Settings settings, @NotNull final CrossfireServerConnection server, @NotNull final Macros macros, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final ScriptManager scriptManager, @NotNull final CommandQueue commandQueue, @NotNull final OptionManager optionManager, @Nullable final MouseTracker mouseTracker)
    {
        this.semaphoreDrawing = semaphoreDrawing;
        this.terminateSync = terminateSync;
        this.tooltipManager = tooltipManager;
        this.settings = settings;
        this.server = server;
        this.windowRenderer = windowRenderer;
        guiStateManager.addGuiStateListener(guiStateListener);
        commands = new Commands(windowRenderer, commandQueue, server, scriptManager, optionManager, commandCallback, macros);
        guiFactory = new GuiFactory(mouseTracker, commands, commandCallback, macros);
        windowRenderer.setCurrentGui(guiFactory.newGui());
        queryDialog = guiFactory.newGui();
        keybindDialog = guiFactory.newGui();
        keybindingsManager = new KeybindingsManager(commands, commandCallback, macros);
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
        if (keybindingsManager.windowClosing())
        {
            closeKeybindDialog();
        }

        if (dialogQuit == null)
        {
            return false;
        }

        if (dialogDisconnect != null)
        {
            closeDialog(dialogDisconnect);
        }
        windowRenderer.openDialog(dialogQuit, false);
        return true;
    }

    /**
     * The ESC key has been pressed.
     * @param connected whether a connection to the server is active
     * @return whether how the key has been consumed: 0=ignore key,
     * 1=disconnect from server, quit=quit application
     */
    public int escPressed(final boolean connected)
    {
        if (keybindingsManager.escPressed())
        {
            closeDialog(keybindDialog);
        }
        else if (windowRenderer.deactivateCommandInput())
        {
            // ignore
        }
        else if (connected)
        {
            if (dialogDisconnect == null)
            {
                return 1;
            }
            else if (windowRenderer.openDialog(dialogDisconnect, false))
            {
                if (dialogQuit != null)
                {
                    closeDialog(dialogQuit);
                }
            }
            else
            {
                closeDialog(dialogDisconnect);
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
                    closeDialog(dialogDisconnect);
                }
            }
            else
            {
                closeDialog(dialogQuit);
            }
        }
        return 0;
    }

    /**
     * Opens the "query" dialog.
     * @param prompt the query prompt
     * @param queryType the query type
     */
    public void openQueryDialog(@NotNull final String prompt, final int queryType)
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
    private void openDialog(@NotNull final Gui dialog, final boolean autoCloseOnDeactivate)
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
    private void toggleDialog(@NotNull final Gui dialog)
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
        closeDialog(queryDialog);
    }

    public void initRendering(final boolean fullScreen)
    {
        windowRenderer.initRendering(skin.getResolution(), fullScreen);
        DialogStateParser.load(skin, windowRenderer);
        keybindingsManager.loadKeybindings();
    }

    /**
     * Opens a dialog by name.
     * @param name the dialog name
     * @return whether the dialog exists
     */
    private boolean openDialogByName(@NotNull final String name)
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
    private void closeDialogByName(@NotNull final String name)
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
        closeDialog(dialog);
    }

    /**
     * Terminates the application.
     */
    public void terminate()
    {
        timer.stop();
        synchronized (terminateSync)
        {
            terminateSync.notifyAll();
        }
    }

    /**
     * Closes all transient dialogs: disconnect, quit, connect, query, and book
     * dialogs.
     */
    public void closeTransientDialogs()
    {
        if (dialogDisconnect != null)
        {
            closeDialog(dialogDisconnect);
        }
        if (dialogQuit != null)
        {
            closeDialog(dialogQuit);
        }
        if (dialogConnect != null)
        {
            closeDialog(dialogConnect);
        }
        closeDialog(queryDialog);
        closeDialog(skin.getDialogBook(1));
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
        closeDialog(keybindDialog);
    }

    /**
     * Closes the given dialog. Does nothing if the dialog is not opened.
     * @param dialog the dialog to close
     */
    private void closeDialog(@NotNull final Gui dialog)
    {
        windowRenderer.closeDialog(dialog);
    }

    /**
     * Sets the current player name. Does nothing if not currently in the
     * character name prompt.
     * @param playerName the player name
     */
    private void updatePlayerName(@NotNull final String playerName)
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
    @NotNull
    public TooltipManager getTooltipManager()
    {
        return tooltipManager;
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
    @Nullable
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
     * Activates the command input text field. If the skin defines more than
     * one input field, the first matching one is selected.
     * <p/>
     * If neither the main gui nor any visible dialog has an input text field,
     * invisible guis are checked as well. If one is found, it is made visible.
     * @param newText the new command text if non-<code>null</code>
     */
    private void activateCommandInput(@Nullable final String newText)
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
    public void setSkin(@NotNull final JXCSkin skin)
    {
        this.skin = skin;
        skin.attach(windowRenderer, tooltipManager);
        queryDialog = skin.getDialogQuery();
        keybindDialog = skin.getDialogKeyBind();
        dialogQuit = skin.getDialogQuit();
        dialogDisconnect = skin.getDialogDisconnect();
        dialogConnect = skin.getDialogConnect();
        dialogConnectLabel = dialogConnect == null ? null : dialogConnect.getFirstElement(AbstractLabel.class, "message");
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
        keybindingsManager.saveKeybindings();
    }

    @Deprecated
    @NotNull
    public Commands getCommands()
    {
        return commands;
    }

    @Deprecated
    public void setConnection(@NotNull final JXCConnection connection)
    {
        this.connection = connection;
    }

    /**
     * Updates the "message" field of the connect dialog. Does nothing if
     * the dialog is not open, does not exist, or if the dialog does not
     * define a "message" field.
     * @param clientSocketState the client socket state
     * @param param a parameter to display
     */
    private void updateConnectLabel(@NotNull final ClientSocketState clientSocketState, @Nullable final String param)
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

            case CONNECT_FAILED:
                message = "Cannot connect to Crossfire server:\n"+param;
                break;
            }

            assert message != null;
            dialogConnectLabel.setText(message);
        }
    }

    @Deprecated
    @NotNull
    public KeybindingsManager getKeybindingsManager()
    {
        return keybindingsManager;
    }

    /**
     * Adds a key binding.
     * @param perCharacter whether a per-character key binding should be added
     * @param cmdlist the command list to execute on key press
     * @return whether the key bindings dialog should be opened
     */
    private boolean createKeyBinding(final boolean perCharacter, @NotNull final CommandList cmdlist)
    {
        final boolean result = keybindingsManager.createKeyBinding(perCharacter, cmdlist);
        if (result)
        {
            openKeybindDialog();
        }
        return result;
    }

    /**
     * Removes a key binding.
     * @param perCharacter whether a per-character key binding should be
     * removed
     * @return whether the key bindings dialog should be opened
     */
    private boolean removeKeyBinding(final boolean perCharacter)
    {
        final boolean result = keybindingsManager.removeKeyBinding(perCharacter);
        if (result)
        {
            openKeybindDialog();
        }
        return result;
    }

    @Deprecated
    @NotNull
    public CommandCallback getCommandCallback()
    {
        return commandCallback;
    }
}

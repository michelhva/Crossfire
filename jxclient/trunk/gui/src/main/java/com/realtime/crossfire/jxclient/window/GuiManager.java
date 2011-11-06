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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiFactory;
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiState;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.label.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.label.TooltipManagerImpl;
import com.realtime.crossfire.jxclient.gui.list.GUICharacterList;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.gui.textinput.NoSuchCommandException;
import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireFailureListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.MessageTypes;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains the application's main GUI state.
 * @author Andreas Kirschbaum
 */
public class GuiManager {

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
    @NotNull
    private final GuiFactory guiFactory;

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
     * The {@link TooltipManager} for this window.
     */
    @NotNull
    private final TooltipManagerImpl tooltipManager;

    /**
     * The {@link Settings} to use.
     */
    @NotNull
    private final Settings settings;

    /**
     * The {@link JXCConnection} to use.
     */
    @NotNull
    private final JXCConnection connection;

    /**
     * The {@link CrossfireServerConnection} instance to monitor.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * The {@link CrossfireDrawextinfoListener} attached to {@link #server}.
     */
    @NotNull
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener() {

        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, @NotNull final String message) {
            if (skin == null) {
                throw new IllegalStateException();
            }

            @Nullable final Gui dialog;
            String effectiveMessage = message;
            switch (type) {
            case MessageTypes.MSG_TYPE_BOOK:
                dialog = skin.getDialogBook(1);
                final AbstractLabel title = dialog.getFirstElementEndingWith(AbstractLabel.class, "_title");
                if (title != null) {
                    final String[] tmp = message.split("\n", 2);
                    title.setText(tmp[0]);
                    effectiveMessage = tmp.length >= 2 ? tmp[1] : "";
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

            if (dialog == null) {
                return;
            }

            final AbstractLabel label = dialog.getFirstElementNotEndingWith(AbstractLabel.class, "_title");
            if (label != null) {
                label.setText(effectiveMessage);
            } else {
                final GUILabelLog log = dialog.getFirstElement(GUILabelLog.class);
                if (log != null) {
                    log.updateText(effectiveMessage);
                }
            }
            openDialog(dialog, false);
        }

        @Override
        public void setDebugMode(final boolean printMessageTypes) {
            // ignore
        }

    };

    /**
     * The {@link CrossfireFailureListener} registered to receive failure
     * messages.
     */
    @NotNull
    private final CrossfireFailureListener crossfireFailureListener = new CrossfireFailureListener() {

        @Override
        public void failure(@NotNull final String command, @NotNull final String arguments) {
            if (command.equals("accountlogin") && skin != null) {
                try {
                    final Gui dialog = skin.getDialog("account_login");
                    final GUIText passwordField = dialog.getFirstElement(GUIText.class, "account_password");
                    if (passwordField != null) {
                        passwordField.setText("");
                        passwordField.setActive(true);
                    }
                } catch (final JXCSkinException ignored) {
                    // ignore if dialog doesn't exist
                }
            }
        }

        @Override
        public void clearFailure() {
            // ignore
        }

    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        @Override
        public void start() {
            closeTransientDialogs();
            server.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            server.removeCrossfireFailureListener(crossfireFailureListener);
            windowRenderer.setGuiState(RendererGuiState.START);
            showGUIStart();
        }

        @Override
        public void metaserver() {
            closeTransientDialogs();
            server.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            server.removeCrossfireFailureListener(crossfireFailureListener);
            windowRenderer.setGuiState(RendererGuiState.META);
            showGUIMeta();
            activateMetaserverGui();
        }

        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        @Override
        public void connecting(@NotNull final String serverInfo) {
            if (skin == null) {
                throw new IllegalStateException();
            }

            closeTransientDialogs();
            windowRenderer.updateServerSettings();
            server.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
            server.addCrossfireFailureListener(crossfireFailureListener);
            windowRenderer.setGuiState(RendererGuiState.LOGIN);
            showGUIMain();
            if (dialogConnect != null) {
                openDialog(dialogConnect, false);
                updateConnectLabel(ClientSocketState.CONNECTING, null);
            }
        }

        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            updateConnectLabel(clientSocketState, null);
        }

        @Override
        public void connected() {
            closeTransientDialogs();
        }

        @Override
        public void connectFailed(@NotNull final String reason) {
            closeTransientDialogs();
            if (dialogConnect != null) {
                openDialog(dialogConnect, false);
                updateConnectLabel(ClientSocketState.CONNECT_FAILED, reason);
            }
        }

    };

    /**
     * Creates a new instance.
     * @param guiStateManager the gui state manager to watch
     * @param tooltipManager the tooltip manager to update
     * @param settings the settings to use
     * @param server the crossfire server connection to monitor
     * @param windowRenderer the window renderer to use
     * @param guiFactory the gui factory for creating gui instances
     * @param keybindingsManager the keybindings manager to use
     * @param connection the connection to use
     */
    public GuiManager(@NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManagerImpl tooltipManager, @NotNull final Settings settings, @NotNull final CrossfireServerConnection server, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GuiFactory guiFactory, @NotNull final KeybindingsManager keybindingsManager, @NotNull final JXCConnection connection) {
        this.tooltipManager = tooltipManager;
        this.settings = settings;
        this.server = server;
        this.windowRenderer = windowRenderer;
        this.guiFactory = guiFactory;
        this.keybindingsManager = keybindingsManager;
        this.connection = connection;
        guiStateManager.addGuiStateListener(guiStateListener);
        windowRenderer.setCurrentGui(guiFactory.newGui());
        queryDialog = guiFactory.newGui();
        keybindDialog = guiFactory.newGui();
    }

    /**
     * A "player" protocol command has been received.
     */
    public void playerReceived() {
        if (windowRenderer.getGuiState() == RendererGuiState.NEW_CHAR) {
            openDialogByName("messages"); // hack for race selection
        }
        windowRenderer.setGuiState(RendererGuiState.PLAYING);
    }

    /**
     * Displays the main account dialog, to let the player login or create a new
     * account.
     */
    public void manageAccount() {
        if (dialogConnect != null) {
            closeDialog(dialogConnect);
        }
        windowRenderer.setGuiState(RendererGuiState.ACCOUNT);
        hideAccountWindows();
        openDialogByName("account_main");
    }

    /**
     * Displays the window with the characters for an account.
     */
    public void showCharacters() {
        if (windowRenderer.getGuiState() != RendererGuiState.ACCOUNT) {
            windowRenderer.setGuiState(RendererGuiState.ACCOUNT);
        }
        hideAccountWindows();
        openDialogByName("account_characters");
    }

    /**
     * Hides all account-related windows.
     */
    public void hideAccountWindows() {
        closeDialogByName("account_login");
        closeDialogByName("account_create");
        closeDialogByName("account_characters");
        closeDialogByName("account_main");
        closeDialogByName("account_link");
        closeDialogByName("account_character_new");
        closeDialogByName("account_password");
    }

    /**
     * Opens the "quit" dialog. Does nothing if the dialog is open.
     * @return whether the dialog has been opened
     */
    public boolean openQuitDialog() {
        if (keybindingsManager.windowClosing()) {
            closeKeybindDialog();
        }

        if (dialogQuit == null) {
            return false;
        }

        if (dialogDisconnect != null) {
            closeDialog(dialogDisconnect);
        }
        assert dialogQuit != null;
        openDialog(dialogQuit, false);
        return true;
    }

    /**
     * The ESC key has been pressed.
     * @param connected whether a connection to the server is active
     * @return whether how the key has been consumed: 0=ignore key, 1=disconnect
     *         from server, quit=quit application
     */
    public EscAction escPressed(final boolean connected) {
        if (keybindingsManager.escPressed()) {
            assert keybindDialog != null;
            closeDialog(keybindDialog);
        } else if (windowRenderer.deactivateCommandInput()) {
            // ignore
        } else if (skin != null && closeDialog(skin.getDialogBook(1))) {
            // ignore
        } else if (connected) {
            if (dialogDisconnect == null) {
                return EscAction.DISCONNECT;
            } else if (openDialog(dialogDisconnect, false)) {
                if (dialogQuit != null) {
                    closeDialog(dialogQuit);
                }
            } else {
                assert dialogDisconnect != null;
                closeDialog(dialogDisconnect);
            }
        } else {
            if (dialogQuit == null) {
                return EscAction.QUIT;
            } else if (openDialog(dialogQuit, false)) {
                if (dialogDisconnect != null) {
                    closeDialog(dialogDisconnect);
                }
            } else {
                assert dialogQuit != null;
                closeDialog(dialogQuit);
            }
        }
        return EscAction.IGNORE;
    }

    /**
     * Opens the "query" dialog.
     * @param prompt the query prompt
     * @param queryType the query type
     */
    public void openQueryDialog(@NotNull final String prompt, final int queryType) {
        if (queryDialog == null) {
            throw new IllegalStateException();
        }

        openDialog(queryDialog, false);
        assert queryDialog != null;
        queryDialog.setHideInput((queryType&CrossfireQueryListener.HIDE_INPUT) != 0);
        currentQueryDialogIsNamePrompt = prompt.startsWith("What is your name?");
        if (currentQueryDialogIsNamePrompt) {
            final String playerName = settings.getString("player_"+connection.getHostname(), "");
            if (playerName.length() > 0) {
                assert queryDialog != null;
                final GUIText textArea = queryDialog.getFirstElement(GUIText.class);
                if (textArea != null) {
                    textArea.setText(playerName);
                }
            }
        } else if (prompt.startsWith("[y] to roll new stats") || prompt.startsWith("Welcome, Brave New Warrior!")) {
            windowRenderer.setGuiState(RendererGuiState.NEW_CHAR);
            if (openDialogByName("new_char")) {
                closeDialogByName("messages");
                closeDialogByName("status");
            } else {
                // fallback: open both message and status dialogs if this skin
                // does not define a login dialog
                openDialogByName("messages");
                openDialogByName("status");
            }
            assert queryDialog != null;
            openDialog(queryDialog, false); // raise dialog
        }
    }

    /**
     * Opens a dialog. Raises the dialog if it is open.
     * @param dialog the dialog to show
     * @param autoCloseOnDeactivate whether the dialog should auto-close when it
     * becomes inactive; ignored if the dialog is already open
     * @return whether the dialog was opened or raised; <code>false</code> if
     *         the dialog already was opened as the topmost dialog
     */
    public boolean openDialog(@NotNull final Gui dialog, final boolean autoCloseOnDeactivate) {
        final boolean result = windowRenderer.openDialog(dialog, autoCloseOnDeactivate);
        if (dialog == queryDialog) {
            dialog.setHideInput(false);
        } else {
            final String name = dialog.getName();
            if (name != null) {
                if (name.equals("account_login")) {
                    final GUIText loginField = dialog.getFirstElement(GUIText.class, "account_login");
                    if (loginField != null) {
                        final String accountName = settings.getString("login_account_"+connection.getHostname(), "");
                        if (accountName.length() > 0) {
                            loginField.setText(accountName);

                            final GUIText passwordField = dialog.getFirstElement(GUIText.class, "account_password");
                            if (passwordField != null) {
                                passwordField.setText("");
                                passwordField.setActive(true);
                            }
                        } else {
                            loginField.setText("");
                            loginField.setActive(true);

                            final GUIText passwordField = dialog.getFirstElement(GUIText.class, "account_password");
                            if (passwordField != null) {
                                passwordField.setText("");
                            }
                        }
                    } else {
                        final GUIText passwordField = dialog.getFirstElement(GUIText.class, "account_password");
                        if (passwordField != null) {
                            passwordField.setText("");
                        }
                    }
                } else if (name.equals("account_characters")) {
                    final GUICharacterList characterList = dialog.getFirstElement(GUICharacterList.class);
                    if (characterList != null) {
                        final String accountName = server.getAccountName();
                        if (accountName != null) {
                            final String characterName = settings.getString("login_account_"+connection.getHostname()+"_"+accountName, "");
                            if (characterName.length() > 0) {
                                characterList.setCharacter(characterName);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Toggles a dialog.
     * @param dialog the dialog to toggle
     */
    public void toggleDialog(@NotNull final Gui dialog) {
        if (windowRenderer.toggleDialog(dialog) && dialog == queryDialog) {
            dialog.setHideInput(false);
        }
    }

    /**
     * Closes the "query" dialog. Does nothing if the dialog is not open.
     */
    public void closeQueryDialog() {
        if (queryDialog == null) {
            throw new IllegalStateException();
        }

        closeDialog(queryDialog);
    }

    /**
     * Opens a dialog by name.
     * @param name the dialog name
     * @return whether the dialog exists
     */
    private boolean openDialogByName(@NotNull final String name) {
        if (skin == null) {
            throw new IllegalStateException();
        }

        final Gui dialog;
        try {
            dialog = skin.getDialog(name);
        } catch (final JXCSkinException ignored) {
            //System.err.println(ex.getLocalizedMessage());
            return false;
        }

        openDialog(dialog, false);
        return true;
    }

    /**
     * Closes a dialog by name.
     * @param name the dialog name
     */
    private void closeDialogByName(@NotNull final String name) {
        if (skin == null) {
            throw new IllegalStateException();
        }

        final Gui dialog;
        try {
            dialog = skin.getDialog(name);
        } catch (final JXCSkinException ignored) {
            // ignore
            return;
        }
        closeDialog(dialog);
    }

    /**
     * Closes all transient dialogs: disconnect, quit, connect, query, and book
     * dialogs.
     */
    private void closeTransientDialogs() {
        if (queryDialog == null || skin == null) {
            throw new IllegalStateException();
        }

        if (dialogDisconnect != null) {
            closeDialog(dialogDisconnect);
        }
        if (dialogQuit != null) {
            closeDialog(dialogQuit);
        }
        if (dialogConnect != null) {
            closeDialog(dialogConnect);
        }
        assert queryDialog != null;
        closeDialog(queryDialog);
        assert skin != null;
        closeDialog(skin.getDialogBook(1));
    }

    /**
     * Called when the server selection GUI becomes active. Selects the last
     * used server entry.
     */
    private void activateMetaserverGui() {
        final String serverName = settings.getString("server", "crossfire.metalforge.net");
        if (serverName.length() > 0) {
            windowRenderer.setSelectedHostname(serverName);
        }
    }

    /**
     * Opens the keybinding dialog. Does nothing if the dialog is opened.
     */
    private void openKeybindDialog() {
        if (keybindDialog == null) {
            throw new IllegalStateException();
        }

        openDialog(keybindDialog, false);
    }

    /**
     * Closes the keybinding dialog. Does nothing if the dialog is not opened.
     */
    public void closeKeybindDialog() {
        if (keybindDialog == null) {
            throw new IllegalStateException();
        }

        closeDialog(keybindDialog);
    }

    /**
     * Closes the given dialog. Does nothing if the dialog is not opened.
     * @param dialog the dialog to close
     * @return whether the dialog has been closed; <code>false</code> if the
     *         dialog was not open
     */
    public boolean closeDialog(@NotNull final Gui dialog) {
        return windowRenderer.closeDialog(dialog);
    }

    /**
     * Sets the current player name. Does nothing if not currently in the
     * character name prompt.
     * @param playerName the player name
     */
    public void updatePlayerName(@NotNull final String playerName) {
        if (currentQueryDialogIsNamePrompt) {
            settings.putString("player_"+connection.getHostname(), playerName, "The charactername last played on the server.");
        }
    }

    /**
     * Activates the command input text field. If the skin defined more than one
     * input field, the first matching one is selected. <p>If neither the main
     * gui nor any visible dialog has an input text field, invisible GUIs are
     * checked as well. If one is found, it is made visible.
     * @return the command input text field, or <code>null</code> if the skin
     *         has no command input text field defined
     */
    @Nullable
    private GUIText activateCommandInput() {
        // check visible dialogs
        final GUIText textArea1 = windowRenderer.activateCommandInput();
        if (textArea1 != null) {
            return textArea1;
        }

        // check invisible dialogs
        assert skin != null;
        for (final Gui dialog : skin) {
            final GUIText textArea3 = dialog.activateCommandInput();
            if (textArea3 != null) {
                openDialog(dialog, true);
                return textArea3;
            }
        }

        return null;
    }

    /**
     * Activates the command input text field. If the skin defines more than one
     * input field, the first matching one is selected.
     * <p/>
     * If neither the main gui nor any visible dialog has an input text field,
     * invisible GUIs are checked as well. If one is found, it is made visible.
     * @param newText the new command text if non-<code>null</code>
     */
    public void activateCommandInput(@Nullable final String newText) {
        final GUIText textArea = activateCommandInput();
        if (textArea != null && newText != null && newText.length() > 0) {
            textArea.setText(newText);
        }
    }

    /**
     * Unsets the current skin.
     */
    public void unsetSkin() {
        if (skin != null) {
            skin.detach();
            windowRenderer.setTooltip(null);
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
    @SuppressWarnings("NullableProblems")
    public void setSkin(@NotNull final JXCSkin skin) {
        this.skin = skin;
        skin.attach(tooltipManager);
        windowRenderer.setTooltip(skin.getTooltipLabel());
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
    private void showGUIStart() {
        windowRenderer.clearGUI(guiFactory.newGui());
        assert skin != null;
        windowRenderer.setCurrentGui(skin.getStartInterface());
        tooltipManager.reset();
    }

    /**
     * Displays the "server selection" GUI.
     */
    private void showGUIMeta() {
        windowRenderer.clearGUI(guiFactory.newGui());
        assert skin != null;
        final Gui newGui = skin.getMetaInterface();
        windowRenderer.setCurrentGui(newGui);
        newGui.activateDefaultElement();
        tooltipManager.reset();
    }

    /**
     * Displays the "main" GUI.
     */
    private void showGUIMain() {
        windowRenderer.clearGUI(guiFactory.newGui());
        assert skin != null;
        final Gui newGui = skin.getMainInterface();
        windowRenderer.setCurrentGui(newGui);
        tooltipManager.reset();
    }

    /**
     * Terminates the GUI. Stops display updates and saves the display state.
     */
    public void term() {
        windowRenderer.endRendering();
        if (skin != null) {
            DialogStateParser.save(skin, windowRenderer);
        }
        keybindingsManager.saveKeybindings();
    }

    /**
     * Updates the "message" field of the connect dialog. Does nothing if the
     * dialog is not open, does not exist, or if the dialog does not define a
     * "message" field.
     * @param clientSocketState the client socket state
     * @param param a parameter to display
     */
    private void updateConnectLabel(@NotNull final ClientSocketState clientSocketState, @Nullable final String param) {
        if (dialogConnectLabel != null) {
            String message = null;
            switch (clientSocketState) {
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

            case ACCOUNT_INFO:
                message = "Starting account session...";
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

    /**
     * Adds a key binding.
     * @param perCharacter whether a per-character key binding should be added
     * @param cmdList the command list to execute on key press
     * @return whether the key bindings dialog should be opened
     */
    public boolean createKeyBinding(final boolean perCharacter, @NotNull final CommandList cmdList) {
        final boolean result = keybindingsManager.createKeyBinding(perCharacter, cmdList);
        if (result) {
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
    public boolean removeKeyBinding(final boolean perCharacter) {
        final boolean result = keybindingsManager.removeKeyBinding(perCharacter);
        if (result) {
            openKeybindDialog();
        }
        return result;
    }

    /**
     * Sets a new window size.
     * @param width the new window width
     * @param height the new window height
     */
    public void updateWindowSize(final int width, final int height) {
        if (skin != null) {
            skin.setScreenSize(width, height);
            assert skin != null;
            for (final Gui dialog : skin) {
                dialog.autoSize(width, height);
            }
            tooltipManager.setScreenSize(width, height);
        }
    }

    /**
     * Returns a named command list.
     * @param args the name of the command list
     * @return the command list
     * @throws NoSuchCommandException if the command list does not exist
     */
    @NotNull
    public CommandList getCommandList(@NotNull final String args) throws NoSuchCommandException {
        if (skin == null) {
            throw new IllegalStateException();
        }

        try {
            return skin.getCommandList(args);
        } catch (final JXCSkinException ex) {
            throw new NoSuchCommandException(ex.getMessage());
        }
    }

    /**
     * Updates the current account name.
     * @param accountName the current account name
     */
    public void setAccountName(@NotNull final String accountName) {
        settings.putString("login_account_"+connection.getHostname(), accountName, "The account last logged in on the server.");
    }

    /**
     * Updates the selected character name in an account.
     * @param accountName the account name
     * @param characterName the character name
     */
    public void selectCharacter(@NotNull final String accountName, @NotNull final String characterName) {
        settings.putString("login_account_"+connection.getHostname()+"_"+accountName, characterName, "The character last selected on the account.");
    }

    /**
     * Action after ESC has been pressed.
     */
    public enum EscAction {

        /**
         * Ignore the key press.
         */
        IGNORE,

        /**
         * Disconnect from server.
         */
        DISCONNECT,

        /**
         * Quit the application.
         */
        QUIT

    }

}

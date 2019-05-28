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
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiState;
import com.realtime.crossfire.jxclient.gui.keybindings.KeybindingsManager;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.label.GUILabelFailure;
import com.realtime.crossfire.jxclient.gui.label.TooltipManagerImpl;
import com.realtime.crossfire.jxclient.gui.list.GUICharacterList;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.misc.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.gui.textinput.NoSuchCommandException;
import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.protocol.MessageTypes;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireFailureListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.SettingsEntries;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.util.SwingUtilities2;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains the application's main GUI state.
 * @author Andreas Kirschbaum
 */
public class GuiManager {

    /**
     * The currently active skin. Set to {@code null} if no skin is set.
     */
    @Nullable
    private JXCSkin skin;

    /**
     * The {@link JXCWindowRenderer} used to paint the gui.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

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
     * The "really quit?" dialog. Set to {@code null} if the skin does not
     * define this dialog.
     */
    @Nullable
    private Gui dialogQuit;

    /**
     * The "really disconnect?" dialog. Set to {@code null} if the skin does not
     * define this dialog.
     */
    @Nullable
    private Gui dialogDisconnect;

    /**
     * The "connect in progress" dialog. Set to {@code null} if the skin does
     * not define this dialog.
     */
    @Nullable
    private Gui dialogConnect;

    /**
     * The "message" field within {@link #dialogConnect}. Set to {@code null} if
     * the dialog does not define a "message" label.
     */
    @Nullable
    private AbstractLabel dialogConnectLabel;

    /**
     * Whether the currently shown query dialog is the character name prompt.
     */
    private boolean currentQueryDialogIsNamePrompt;

    /**
     * The {@link com.realtime.crossfire.jxclient.gui.gui.TooltipManager
     * TooltipManager} for this window.
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
     * Maps dialog name to dialog instance.
     */
    @NotNull
    private final Map<String, Gui> dialogs = new HashMap<>();

    /**
     * The {@link CrossfireDrawextinfoListener} attached to {@link #server}.
     */
    @NotNull
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener() {

        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, @NotNull final String message) {
            if (skin == null) {
                throw new IllegalStateException("no skin set");
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
            if (label == null) {
                final GUILabelLog log = dialog.getFirstElement(GUILabelLog.class);
                if (log != null) {
                    log.updateText(effectiveMessage);
                }
            } else {
                label.setText(effectiveMessage);
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
            } else if (command.equals("accountaddplayer") && skin != null) {
                try {
                    final Gui dialog = skin.getDialog("account_link");
                    final GUIText loginField = dialog.getFirstElement(GUIText.class, "character_login");
                    final GUIText passwordField = dialog.getFirstElement(GUIText.class, "character_password");
                    final String argumentsLower = arguments.toLowerCase();
                    if (argumentsLower.contains("password")) {
                        if (passwordField != null) {
                            passwordField.setText("");
                            passwordField.setActive(true);
                        }
                    } else if (argumentsLower.contains("character")) {
                        if (loginField != null) {
                            loginField.setActive(true);
                        }
                    } else {
                        if (passwordField != null) {
                            passwordField.setActive(true);
                        }
                    }
                } catch (final JXCSkinException ignored) {
                    // ignore if dialog doesn't exist
                }
            } else if (command.equals("accountnew") && skin != null) {
                try {
                    final Gui dialog = skin.getDialog("account_create");
                    final GUIText loginField = dialog.getFirstElement(GUIText.class, "account_login");
                    final GUIText passwordField = dialog.getFirstElement(GUIText.class, "account_password");
                    final GUIText passwordConfirmField = dialog.getFirstElement(GUIText.class, "account_password_confirm");
                    final String argumentsLower = arguments.toLowerCase();
                    if (argumentsLower.contains("password")) {
                        if (passwordField != null) {
                            passwordField.setText("");
                            passwordField.setActive(true);
                        }
                        if (passwordConfirmField != null) {
                            passwordConfirmField.setText("");
                        }
                    } else if (argumentsLower.contains("account")) {
                        if (loginField != null) {
                            loginField.setActive(true);
                        }
                    } else {
                        if (passwordField != null) {
                            passwordField.setActive(true);
                        }
                    }
                } catch (final JXCSkinException ignored) {
                    // ignore if dialog doesn't exist
                }
            } else if (command.equals("createplayer") && skin != null) {
                try {
                    final Gui dialog = skin.getDialog("account_character_new");
                    final GUIText loginField = dialog.getFirstElement(GUIText.class, "character_login");
                    if (loginField != null) {
                        loginField.setActive(true);
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
    @SuppressWarnings("FieldCanBeLocal")
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
                throw new IllegalStateException("no skin set");
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
        this.keybindingsManager = keybindingsManager;
        this.connection = connection;
        guiStateManager.addGuiStateListener(guiStateListener);
        windowRenderer.setCurrentGui(guiFactory.newGui());
        queryDialog = guiFactory.newGui();
        keybindDialog = guiFactory.newGui();
    }

    /**
     * Adds a dialog for name based lookup.
     * @param name the name of the dialog
     * @param dialog the dialog
     */
    public void addDialog(@NotNull final String name, @NotNull final Gui dialog) {
        dialogs.put(name, dialog);
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
        openDialogByName("account_login");
    }

    /**
     * Displays the window with the characters for an account.
     * @param count how many characters the account has.
     */
    public void showCharacters(final int count) {
        if (windowRenderer.getGuiState() != RendererGuiState.ACCOUNT) {
            windowRenderer.setGuiState(RendererGuiState.ACCOUNT);
        }
        hideAccountWindows();
        if (count == 0) {
            openDialogByName("account_character_new");
        } else {
            openDialogByName("account_characters");
        }
    }

    /**
     * Hides all account-related windows.
     */
    public void hideAccountWindows() {
        closeDialogByName("account_login");
        closeDialogByName("account_create");
        closeDialogByName("account_characters");
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
     * from server, quit=quit application
     */
    @SuppressWarnings("IfStatementWithIdenticalBranches")
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
            }
            if (openDialog(dialogDisconnect, false)) {
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
            }
            if (openDialog(dialogQuit, false)) {
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
            throw new IllegalStateException("query dialog not set");
        }

        openDialog(queryDialog, false);
        setHideInput((queryType&CrossfireQueryListener.HIDE_INPUT) != 0);
        currentQueryDialogIsNamePrompt = prompt.startsWith("What is your name?");
        if (currentQueryDialogIsNamePrompt) {
            final String hostname = connection.getHostname();
            if (hostname != null) {
                final String playerName = settings.getString(SettingsEntries.getPlayerSettingsEntry(hostname));
                if (!playerName.isEmpty()) {
                    assert queryDialog != null;
                    final GUIText textArea = queryDialog.getFirstElement(GUIText.class);
                    if (textArea != null) {
                        textArea.setText(playerName);
                    }
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
     * Opens a dialog by name. Raises the dialog if open.
     * @param name the name of the dialog
     */
    public void openDialog(@NotNull final String name) {
        final Gui dialog = dialogs.get(name);
        if (dialog != null) {
            openDialog(dialog, false);
        }
    }

    /**
     * Opens a dialog. Raises the dialog if it is open.
     * @param dialog the dialog to show
     * @param autoCloseOnDeactivate whether the dialog should auto-close when it
     * becomes inactive; ignored if the dialog is already open
     * @return whether the dialog was opened or raised; {@code false} if the
     * dialog already was opened as the topmost dialog
     */
    private boolean openDialog(@NotNull final Gui dialog, final boolean autoCloseOnDeactivate) {
        final boolean[] result = new boolean[1];
        SwingUtilities2.invokeAndWait(() -> {
            result[0] = windowRenderer.openDialog(dialog, autoCloseOnDeactivate);
            if (dialog == queryDialog) {
                setHideInput(false);
            } else {
                final AbstractLabel labelFailure = dialog.getFirstElement(GUILabelFailure.class);
                if (labelFailure != null) {
                    labelFailure.setText("");
                }

                final String name = dialog.getComponent().getName();
                if (name != null) {
                    switch (name) {
                    case "account_login":
                        final GUIText loginField = dialog.getFirstElement(GUIText.class, "account_login");
                        if (loginField == null) {
                            final GUIText passwordField = dialog.getFirstElement(GUIText.class, "account_password");
                            if (passwordField != null) {
                                passwordField.setText("");
                            }
                        } else {
                            final String hostname = connection.getHostname();
                            if (hostname != null) {
                                final String accountName = settings.getString(SettingsEntries.getLoginAccountSettingsEntry(hostname));
                                if (accountName.isEmpty()) {
                                    loginField.setText("");
                                    loginField.setActive(true);

                                    final GUIText passwordField = dialog.getFirstElement(GUIText.class, "account_password");
                                    if (passwordField != null) {
                                        passwordField.setText("");
                                    }
                                } else {
                                    loginField.setText(accountName);

                                    final GUIText passwordField = dialog.getFirstElement(GUIText.class, "account_password");
                                    if (passwordField != null) {
                                        passwordField.setText("");
                                        passwordField.setActive(true);
                                    }
                                }
                            }
                        }
                        break;

                    case "account_characters":
                        final GUICharacterList characterList = dialog.getFirstElement(GUICharacterList.class);
                        if (characterList != null) {
                            final String accountName = server.getAccountName();
                            if (accountName != null) {
                                final String hostname = connection.getHostname();
                                if (hostname != null) {
                                    final String characterName = settings.getString(SettingsEntries.getLoginAccountSettingsEntry(hostname, accountName));
                                    if (!characterName.isEmpty()) {
                                        characterList.setCharacter(characterName);
                                    }
                                }
                            }
                        }
                        break;

                    case "account_character_new":
                        final GUIText characterField = dialog.getFirstElement(GUIText.class, "character_login");
                        if (characterField != null) {
                            characterField.setText("");
                            characterField.setActive(true);
                        }
                        break;
                    }
                }

                dialog.notifyOpen();
            }
        });
        return result[0];
    }

    /**
     * Toggles a dialog.
     * @param name the name of the dialog
     */
    public void toggleDialog(@NotNull final String name) {
        final Gui dialog = dialogs.get(name);
        if (dialog != null && windowRenderer.toggleDialog(dialog) && dialog == queryDialog) {
            setHideInput(false);
        }
    }

    /**
     * Closes the "query" dialog. Does nothing if the dialog is not open.
     */
    public void closeQueryDialog() {
        if (queryDialog == null) {
            throw new IllegalStateException("query dialog not set");
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
            throw new IllegalStateException("skin not set");
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
            throw new IllegalStateException("skin not set");
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
        if (queryDialog == null) {
            throw new IllegalStateException("query dialog not set");
        }
        if (skin == null) {
            throw new IllegalStateException("skin not set");
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
        final String serverName = settings.getString(SettingsEntries.SERVER);
        if (!serverName.isEmpty()) {
            windowRenderer.setSelectedHostname(serverName);
        }
    }

    /**
     * Opens the keybinding dialog. Does nothing if the dialog is opened.
     */
    private void openKeybindDialog() {
        if (keybindDialog == null) {
            throw new IllegalStateException("keybinding dialog not set");
        }

        openDialog(keybindDialog, false);
    }

    /**
     * Closes the keybinding dialog. Does nothing if the dialog is not opened.
     */
    public void closeKeybindDialog() {
        if (keybindDialog == null) {
            throw new IllegalStateException("keybinding dialog not set");
        }

        closeDialog(keybindDialog);
    }

    /**
     * Closes the given dialog. Does nothing if the dialog is not opened.
     * @param name the name of the dialog
     */
    public void closeDialog(@NotNull final String name) {
        final Gui dialog = dialogs.get(name);
        if (dialog != null) {
            windowRenderer.closeDialog(dialog);
        }
    }

    /**
     * Closes the given dialog. Does nothing if the dialog is not opened.
     * @param dialog the dialog to close
     * @return whether the dialog has been closed; {@code false} if the dialog
     * was not open
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
            final String hostname = connection.getHostname();
            if (hostname != null) {
                settings.putString(SettingsEntries.getPlayerSettingsEntry(hostname), playerName);
            }
        }
    }

    /**
     * Activates the command input text field. If the skin defined more than one
     * input field, the first matching one is selected. <p>If neither the main
     * gui nor any visible dialog has an input text field, invisible GUIs are
     * checked as well. If one is found, it is made visible.
     * @return the command input text field, or {@code null} if the skin has no
     * command input text field defined
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
        for (Gui dialog : skin) {
            final GUIText textArea3 = JXCWindowRenderer.activateCommandInput(dialog);
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
     * <p>
     * If neither the main gui nor any visible dialog has an input text field,
     * invisible GUIs are checked as well. If one is found, it is made visible.
     * @param newText the new command text if non-{@code null}
     */
    public void activateCommandInput(@Nullable final String newText) {
        final GUIText textArea = activateCommandInput();
        if (textArea != null && newText != null && !newText.isEmpty()) {
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
        assert skin != null;
        windowRenderer.clearGUI(skin.getStartInterface());
        tooltipManager.reset();
    }

    /**
     * Displays the "server selection" GUI.
     */
    private void showGUIMeta() {
        assert skin != null;
        final Gui gui = skin.getMetaInterface();
        windowRenderer.clearGUI(gui);
        gui.activateDefaultElement();
        tooltipManager.reset();
    }

    /**
     * Displays the "main" GUI.
     */
    private void showGUIMain() {
        assert skin != null;
        windowRenderer.clearGUI(skin.getMainInterface());
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
            SwingUtilities2.invokeAndWait(() -> {
                for (Gui dialog : skin) {
                    dialog.autoSize(width, height);
                }
                tooltipManager.setScreenSize(width, height);
            });
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
            throw new IllegalStateException("skin not set");
        }

        try {
            return skin.getCommandList(args);
        } catch (final JXCSkinException ex) {
            throw new NoSuchCommandException(ex.getMessage(), ex);
        }
    }

    /**
     * Updates the current account name.
     * @param accountName the current account name
     */
    public void setAccountName(@NotNull final String accountName) {
        final String hostname = connection.getHostname();
        if (hostname != null) {
            settings.putString(SettingsEntries.getLoginAccountSettingsEntry(hostname), accountName);
        }
    }

    /**
     * Updates the selected character name in an account.
     * @param accountName the account name
     * @param characterName the character name
     */
    public void selectCharacter(@NotNull final String accountName, @NotNull final String characterName) {
        final String hostname = connection.getHostname();
        if (hostname != null) {
            settings.putString(SettingsEntries.getLoginAccountSettingsEntry(hostname, accountName), characterName);
        }
    }

    /**
     * Enables or disables hidden text in the first input field of the {@link
     * #queryDialog}.
     * @param hideInput if set, hide input; else show input
     */
    private void setHideInput(final boolean hideInput) {
        assert queryDialog != null;
        final GUIText textArea = queryDialog.getFirstElement(GUIText.class);
        if (textArea != null) {
            textArea.setHideInput(hideInput);
        }
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

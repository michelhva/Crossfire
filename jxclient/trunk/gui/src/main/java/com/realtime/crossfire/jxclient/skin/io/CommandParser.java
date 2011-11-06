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

package com.realtime.crossfire.jxclient.skin.io;

import com.realtime.crossfire.jxclient.gui.commandlist.GUICommand;
import com.realtime.crossfire.jxclient.gui.commands.AccountCreateCharacterCommand;
import com.realtime.crossfire.jxclient.gui.commands.AccountCreateCommand;
import com.realtime.crossfire.jxclient.gui.commands.AccountLinkCharacterCommand;
import com.realtime.crossfire.jxclient.gui.commands.AccountLoginCommand;
import com.realtime.crossfire.jxclient.gui.commands.AccountPasswordCommand;
import com.realtime.crossfire.jxclient.gui.commands.AccountPlayCharacterCommand;
import com.realtime.crossfire.jxclient.gui.commands.CommandType;
import com.realtime.crossfire.jxclient.gui.commands.ConnectCommand;
import com.realtime.crossfire.jxclient.gui.commands.DialogCloseCommand;
import com.realtime.crossfire.jxclient.gui.commands.DialogOpenCommand;
import com.realtime.crossfire.jxclient.gui.commands.DialogToggleCommand;
import com.realtime.crossfire.jxclient.gui.commands.DisconnectCommand;
import com.realtime.crossfire.jxclient.gui.commands.ExecSelectionCommand;
import com.realtime.crossfire.jxclient.gui.commands.ExecuteElementCommand;
import com.realtime.crossfire.jxclient.gui.commands.HideCommand;
import com.realtime.crossfire.jxclient.gui.commands.MetaCommand;
import com.realtime.crossfire.jxclient.gui.commands.MoveSelectionCommand;
import com.realtime.crossfire.jxclient.gui.commands.PrintCommand;
import com.realtime.crossfire.jxclient.gui.commands.QuitCommand;
import com.realtime.crossfire.jxclient.gui.commands.ScrollCommand;
import com.realtime.crossfire.jxclient.gui.commands.ScrollListCommand;
import com.realtime.crossfire.jxclient.gui.commands.ScrollNeverCommand;
import com.realtime.crossfire.jxclient.gui.commands.ScrollNextCommand;
import com.realtime.crossfire.jxclient.gui.commands.ScrollResetCommand;
import com.realtime.crossfire.jxclient.gui.commands.SelectCommand;
import com.realtime.crossfire.jxclient.gui.commands.ShowCommand;
import com.realtime.crossfire.jxclient.gui.commands.StartCommand;
import com.realtime.crossfire.jxclient.gui.commands.ToggleCommand;
import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUISelectable;
import com.realtime.crossfire.jxclient.gui.item.GUIItem;
import com.realtime.crossfire.jxclient.gui.list.GUIItemList;
import com.realtime.crossfire.jxclient.gui.list.GUIList;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable;
import com.realtime.crossfire.jxclient.gui.textinput.CommandCallback;
import com.realtime.crossfire.jxclient.gui.textinput.CommandExecutor;
import com.realtime.crossfire.jxclient.gui.textinput.ExecuteCommandCommand;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.Macros;
import com.realtime.crossfire.jxclient.skin.skin.Dialogs;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinCache;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.util.NumberParser;
import java.awt.Component;
import java.io.IOException;
import java.io.LineNumberReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for creating {@link GUICommand} instances from string
 * representations.
 * @author Andreas Kirschbaum
 */
public class CommandParser {

    /**
     * The {@link Dialogs} instance to use.
     */
    @NotNull
    private final Dialogs dialogs;

    /**
     * The {@link FloorView} to use.
     */
    @NotNull
    private final FloorView floorView;

    /**
     * The defined GUI elements.
     */
    @NotNull
    private final JXCSkinCache<AbstractGUIElement> definedGUIElements;

    /**
     * Creates a new instance.
     * @param dialogs the dialogs instance to use
     * @param floorView the floor view to use
     * @param definedGUIElements the defined GUI elements
     */
    public CommandParser(@NotNull final Dialogs dialogs, @NotNull final FloorView floorView, @NotNull final JXCSkinCache<AbstractGUIElement> definedGUIElements) {
        this.dialogs = dialogs;
        this.floorView = floorView;
        this.definedGUIElements = definedGUIElements;
    }

    /**
     * Parses and builds command arguments.
     * @param args the list of arguments
     * @param element the target element
     * @param command the command to parse the arguments of
     * @param guiStateManager the gui state manager instance
     * @param commandExecutor the command executor for executing commands
     * @param lnr the source to read more parameters from
     * @param commandQueue the command queue for executing commands
     * @param crossfireServerConnection the server connection to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     * @throws JXCSkinException if an element cannot be found
     */
    @NotNull
    public GUICommand parseCommandArgs(@NotNull final Args args, @Nullable final AbstractGUIElement element, @NotNull final String command, @NotNull final GuiStateManager guiStateManager, @NotNull final CommandExecutor commandExecutor, @NotNull final LineNumberReader lnr, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) throws IOException, JXCSkinException {
        if (command.equals("SHOW")) {
            return parseShow(element);
        } else if (command.equals("HIDE")) {
            return parseHide(element);
        } else if (command.equals("TOGGLE")) {
            return parseToggle(element);
        } else if (command.equals("PRINT")) {
            return parsePrint(element);
        } else if (command.equals("QUIT")) {
            return parseQuit(element, commandCallback);
        } else if (command.equals("CONNECT")) {
            return parseConnect(element, guiStateManager);
        } else if (command.equals("DISCONNECT")) {
            return parseDisconnect(element, guiStateManager);
        } else if (command.equals("GUI_META")) {
            return parseGuiMeta(element, guiStateManager);
        } else if (command.equals("GUI_START")) {
            return parseGuiStart(element, guiStateManager);
        } else if (command.equals("GUI_EXECUTE_ELEMENT")) {
            return parseGuiExecuteElement(element);
        } else if (command.equals("DIALOG_OPEN")) {
            return parseDialogOpen(args, element, commandCallback);
        } else if (command.equals("DIALOG_TOGGLE")) {
            return parseDialogToggle(args, element, commandCallback);
        } else if (command.equals("DIALOG_CLOSE")) {
            return parseDialogClose(args, element, commandCallback);
        } else if (command.equals("GUI_EXECUTE_COMMAND")) {
            return parseGuiExecuteCommand(args, element, commandExecutor, lnr, macros);
        } else if (command.equals("EXEC_SELECTION")) {
            return parseExecSelection(args, element, commandQueue, crossfireServerConnection);
        } else if (command.equals("MOVE_SELECTION")) {
            return parseMoveSelection(args, element);
        } else if (command.equals("SCROLL_LIST")) {
            return parseScrollList(args, element);
        } else if (command.equals("SCROLL") || command.equals("SCROLL_NEVER")) {
            return parseScroll(args, element, command.equals("SCROLL"));
        } else if (command.equals("SCROLL_RESET")) {
            return parseScrollReset(element);
        } else if (command.equals("MOVE_FOCUS")) {
            return parseMoveFocus(args, element);
        } else if (command.equals("ACCOUNT_LOGIN")) {
            return parseAccountLogin(element, commandCallback);
        } else if (command.equals("ACCOUNT_CREATE")) {
            return parseAccountCreate(element, commandCallback);
        } else if (command.equals("ACCOUNT_PLAY")) {
            return parseAccountPlay(element, commandCallback);
        } else if (command.equals("ACCOUNT_LINK")) {
            return parseAccountLink(element, commandCallback);
        } else if (command.equals("ACCOUNT_CREATE_CHARACTER")) {
            return parseAccountCreateCharacter(element, commandCallback);
        } else if (command.equals("ACCOUNT_PASSWORD")) {
            return parseAccountPassword(element, commandCallback);
        } else if (command.equals("SELECT")) {
            return parseSelect(args, element);
        } else {
            throw new JXCSkinException("unknown command '"+command+"'");
        }
    }

    /**
     * Parses and builds a "SHOW" command.
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseShow(@Nullable final Component element) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new ShowCommand(element);
    }

    /**
     * Parses and builds a "HIDE" command.
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseHide(@Nullable final Component element) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new HideCommand(element);
    }

    /**
     * Parses and builds a "TOGGLE" command.
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseToggle(@Nullable final Component element) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new ToggleCommand(element);
    }

    /**
     * Parses and builds a "PRINT" command.
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parsePrint(@Nullable final GUIElement element) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        return new PrintCommand();
    }

    /**
     * Parses and builds a "QUIT" command.
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseQuit(@Nullable final GUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        return new QuitCommand(commandCallback);
    }

    /**
     * Parses and builds a "CONNECT" command.
     * @param element the target element
     * @param guiStateManager the gui state manager instance
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseConnect(@Nullable final GUIElement element, @NotNull final GuiStateManager guiStateManager) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof GUIText)) {
            throw new IOException("'"+element+"' must be an input field");
        }

        return new ConnectCommand(guiStateManager, (GUIText)element);
    }

    /**
     * Parses and builds a "DISCONNECT" command.
     * @param element the target element
     * @param guiStateManager the gui state manager instance
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseDisconnect(@Nullable final GUIElement element, @NotNull final GuiStateManager guiStateManager) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        return new DisconnectCommand(guiStateManager);
    }

    /**
     * Parses and builds a "GUI_META" command.
     * @param element the target element
     * @param guiStateManager the gui state manager instance
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseGuiMeta(@Nullable final GUIElement element, @NotNull final GuiStateManager guiStateManager) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        return new MetaCommand(guiStateManager);
    }

    /**
     * Parses and builds a "GUI_START" command.
     * @param element the target element
     * @param guiStateManager the gui state manager instance
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseGuiStart(@Nullable final GUIElement element, @NotNull final GuiStateManager guiStateManager) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        return new StartCommand(guiStateManager);
    }

    /**
     * Parses and builds a "GUI_EXECUTE_ELEMENT" command.
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseGuiExecuteElement(@Nullable final GUIElement element) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof GUIItem)) {
            throw new IOException("'"+element+"' must be an item element");
        }

        return new ExecuteElementCommand((GUIItem)element);
    }

    /**
     * Parses and builds a "DIALOG_OPEN" command.
     * @param args the list of arguments
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private GUICommand parseDialogOpen(@NotNull final Args args, @Nullable final GUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        return new DialogOpenCommand(commandCallback, dialogs.addDialog(args.get()));
    }

    /**
     * Parses and builds a "DIALOG_TOGGLE" command.
     * @param args the list of arguments
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private GUICommand parseDialogToggle(@NotNull final Args args, @Nullable final GUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        return new DialogToggleCommand(commandCallback, dialogs.addDialog(args.get()));
    }

    /**
     * Parses and builds a "DIALOG_CLOSE" command.
     * @param args the list of arguments
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private GUICommand parseDialogClose(@NotNull final Args args, @Nullable final GUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        return new DialogCloseCommand(commandCallback, dialogs.addDialog(args.get()));
    }

    /**
     * Parses and builds a "GUI_EXECUTE_COMMAND" command.
     * @param args the list of arguments
     * @param element the target element
     * @param commandExecutor the command executor for executing commands
     * @param lnr the source to read more parameters from
     * @param macros the macros instance to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseGuiExecuteCommand(@NotNull final Args args, @Nullable final GUIElement element, @NotNull final CommandExecutor commandExecutor, @NotNull final LineNumberReader lnr, @NotNull final Macros macros) throws IOException {
        if (element != null) {
            throw new IOException("<element> is not allowed");
        }

        final String commandString = ParseUtils.parseText(args, lnr);
        return new ExecuteCommandCommand(commandExecutor, commandString, macros);
    }

    /**
     * Parses and builds a "" command.
     * @param args the list of arguments
     * @param element the target element
     * @param commandQueue the command queue for executing commands
     * @param crossfireServerConnection the server connection to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private GUICommand parseExecSelection(@NotNull final Args args, @Nullable final GUIElement element, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection crossfireServerConnection) throws IOException {
        final CommandType commandType = NumberParser.parseEnum(CommandType.class, args.get(), "command name");

        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof GUIItemList)) {
            throw new IOException("'"+element+"' must be an item list");
        }

        return new ExecSelectionCommand((GUIItemList)element, commandType, crossfireServerConnection, floorView, commandQueue);
    }

    /**
     * Parses and builds a "MOVE_SELECTION" command.
     * @param args the list of arguments
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseMoveSelection(@NotNull final Args args, @Nullable final GUIElement element) throws IOException {
        final int diffLines = ExpressionParser.parseInt(args.get());
        final int diffElements = ExpressionParser.parseInt(args.get());
        if (diffLines == 0 && diffElements == 0) {
            throw new IOException("Invalid zero scroll distance");
        }

        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof GUIList)) {
            throw new IOException("'"+element+"' must be a list");
        }

        return new MoveSelectionCommand((GUIList)element, diffLines, diffElements);
    }

    /**
     * Parses and builds a "SCROLL_LIST" command.
     * @param args the list of arguments
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseScrollList(@NotNull final Args args, @Nullable final GUIElement element) throws IOException {
        final int distance = ExpressionParser.parseInt(args.get());
        if (distance == 0) {
            throw new IOException("Invalid zero scroll distance");
        }

        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof GUIScrollable)) {
            throw new IOException("'"+element+"' must be a scrollable");
        }

        return new ScrollListCommand((GUIScrollable)element, distance);
    }

    /**
     * Parses and builds a "SCROLL" or "SCROLL_NEVER command.
     * @param args the list of arguments
     * @param element the target element
     * @param isScroll whether to parse a "SCROLL" (<code>true</code>) or a
     * "SCROLL_NEVER" (<code>false</code>) command
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseScroll(@NotNull final Args args, @Nullable final GUIElement element, final boolean isScroll) throws IOException {
        final int distance = ExpressionParser.parseInt(args.get());
        if (distance == 0) {
            throw new IOException("Invalid zero scroll distance");
        }

        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof GUIScrollable)) {
            throw new IOException("'"+element+"' must be a scrollable element");
        }

        return isScroll ? new ScrollCommand(distance, (GUIScrollable)element) : new ScrollNeverCommand(distance, (GUIScrollable)element);
    }

    /**
     * Parses and builds a "SCROLL_RESET" command.
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseScrollReset(@Nullable final GUIElement element) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof GUIScrollable)) {
            throw new IOException("'"+element+"' must be a scrollable element");
        }

        return new ScrollResetCommand((GUIScrollable)element);
    }

    /**
     * Parses and builds a "MOVE_FOCUS" command.
     * @param args the list of arguments
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     * @throws JXCSkinException if a syntax error occurs
     */
    @NotNull
    private GUICommand parseMoveFocus(@NotNull final Args args, @Nullable final GUIElement element) throws IOException, JXCSkinException {
        final Object nextElement = definedGUIElements.lookup(args.get());
        if (!(nextElement instanceof ActivatableGUIElement)) {
            throw new IOException("'"+args.getPrev()+"' cannot become active");
        }

        final int apply = args.hasMore() ? ExpressionParser.parseInt(args.get()) : 0;
        if (apply != 0 && apply != 1) {
            throw new IOException("<apply> must be 0 or 1");
        }

        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof ActivatableGUIElement)) {
            throw new IOException("'"+element+"' cannot become active");
        }

        return new ScrollNextCommand((ActivatableGUIElement)nextElement, (ActivatableGUIElement)element, apply != 0);
    }

    /**
     * Parses and builds an "ACCOUNT_LOGIN" command.
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseAccountLogin(@Nullable final AbstractGUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new AccountLoginCommand(commandCallback, element);
    }

    /**
     * Parses and builds an "ACCOUNT_CREATE" command.
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseAccountCreate(@Nullable final AbstractGUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new AccountCreateCommand(commandCallback, element);
    }

    /**
     * Parses and builds an "ACCOUNT_PLAY" command.
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseAccountPlay(@Nullable final AbstractGUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new AccountPlayCharacterCommand(commandCallback, element);
    }

    /**
     * Parses and builds an "ACCOUNT_LINK" command.
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseAccountLink(@Nullable final AbstractGUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new AccountLinkCharacterCommand(commandCallback, element);
    }

    /**
     * Parses and builds an "ACCOUNT_CREATE_CHARACTER" command.
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseAccountCreateCharacter(@Nullable final AbstractGUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new AccountCreateCharacterCommand(commandCallback, element);
    }

    /**
     * Parses and builds an "ACCOUNT_PASSWORD" command.
     * @param element the target element
     * @param commandCallback the command callback to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseAccountPassword(@Nullable final AbstractGUIElement element, @NotNull final CommandCallback commandCallback) throws IOException {
        if (element == null) {
            throw new IOException("<element> is required");
        }

        return new AccountPasswordCommand(commandCallback, element);
    }

    /**
     * Parses and builds a "SELECT" command.
     * @param args the list of arguments
     * @param element the target element
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     */
    @NotNull
    private static GUICommand parseSelect(@NotNull final Args args, @Nullable final GUIElement element) throws IOException {
        final boolean selected = NumberParser.parseBoolean(args.get());

        if (element == null) {
            throw new IOException("<element> is required");
        }

        if (!(element instanceof GUISelectable)) {
            throw new IOException("'"+element+"' must be a selectable element");
        }

        return new SelectCommand((GUISelectable)element, selected);
    }

}

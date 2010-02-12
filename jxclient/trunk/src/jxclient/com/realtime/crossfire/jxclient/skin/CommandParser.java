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

package com.realtime.crossfire.jxclient.skin;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.gui.commands.ConnectCommand;
import com.realtime.crossfire.jxclient.gui.commands.DialogCloseCommand;
import com.realtime.crossfire.jxclient.gui.commands.DialogOpenCommand;
import com.realtime.crossfire.jxclient.gui.commands.DialogToggleCommand;
import com.realtime.crossfire.jxclient.gui.commands.DisconnectCommand;
import com.realtime.crossfire.jxclient.gui.commands.ExecSelectionCommand;
import com.realtime.crossfire.jxclient.gui.commands.ExecuteCommandCommand;
import com.realtime.crossfire.jxclient.gui.commands.ExecuteElementCommand;
import com.realtime.crossfire.jxclient.gui.commands.GUICommand;
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
import com.realtime.crossfire.jxclient.gui.commands.ShowCommand;
import com.realtime.crossfire.jxclient.gui.commands.StartCommand;
import com.realtime.crossfire.jxclient.gui.commands.ToggleCommand;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.item.GUIItem;
import com.realtime.crossfire.jxclient.gui.list.GUIItemList;
import com.realtime.crossfire.jxclient.gui.list.GUIList;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.util.NumberParser;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.GuiStateManager;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.io.IOException;
import java.io.LineNumberReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for creating {@link GUICommand} instances from string
 * representations.
 * @author Andreas Kirschbaum
 */
public class CommandParser
{
    /**
     * The {@link Dialogs} instance to use.
     */
    @NotNull
    private final Dialogs dialogs;

    /**
     * The {@link ItemsManager} instance to use.
     */
    @NotNull
    private final ItemsManager itemsManager;

    /**
     * The {@link ExpressionParser} instance to use.
     */
    @NotNull
    private final ExpressionParser expressionParser;

    /**
     * The defined GUI elements.
     */
    @NotNull
    private final JXCSkinCache<GUIElement> definedGUIElements;

    /**
     * Creates a new instance.
     * @param dialogs the dialogs instance to use
     * @param itemsManager the items manager instance to use
     * @param expressionParser the expression parser instance to use
     * @param definedGUIElements the defined GUI elements
     */
    public CommandParser(@NotNull final Dialogs dialogs, @NotNull final ItemsManager itemsManager, @NotNull final ExpressionParser expressionParser, @NotNull final JXCSkinCache<GUIElement> definedGUIElements)
    {
        this.dialogs = dialogs;
        this.itemsManager = itemsManager;
        this.expressionParser = expressionParser;
        this.definedGUIElements = definedGUIElements;
    }

    /**
     * Parses and builds command arguments.
     * @param args the list of arguments
     * @param argc the start index for parsing
     * @param element the target element
     * @param command the command to parse the arguments of
     * @param window the window instance
     * @param guiStateManager the gui state manager instance
     * @param commands the commands instance for executing commands
     * @param lnr the source to read more parameters from
     * @param commandQueue the command queue for executing commands
     * @param crossfireServerConnection the server connection to use
     * @param guiManager the gui manager to use
     * @param macros the macros instance to use
     * @return the command arguments
     * @throws IOException if a syntax error occurs
     * @throws JXCSkinException if an element cannot be found
     */
    @NotNull
    public GUICommand parseCommandArgs(@NotNull final String[] args, final int argc, @Nullable final GUIElement element, @NotNull final String command, @NotNull final JXCWindow window, @NotNull final GuiStateManager guiStateManager, @NotNull final Commands commands, @NotNull final LineNumberReader lnr, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final GuiManager guiManager, @NotNull final Macros macros) throws IOException, JXCSkinException
    {
        if (command.equals("SHOW"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new ShowCommand(element);
        }
        else if (command.equals("HIDE"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new HideCommand(element);
        }
        else if (command.equals("TOGGLE"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new ToggleCommand(element);
        }
        else if (command.equals("PRINT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new PrintCommand();
        }
        else if (command.equals("QUIT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new QuitCommand(window);
        }
        else if (command.equals("CONNECT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            if (!(element instanceof GUIText))
            {
                throw new IOException("'"+element+"' must be an input field");
            }

            return new ConnectCommand(window, (GUIText)element);
        }
        else if (command.equals("DISCONNECT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new DisconnectCommand(window);
        }
        else if (command.equals("GUI_META"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new MetaCommand(guiStateManager);
        }
        else if (command.equals("GUI_START"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new StartCommand(guiStateManager);
        }
        else if (command.equals("GUI_EXECUTE_ELEMENT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            if (!(element instanceof GUIItem))
            {
                throw new IOException("'"+element+"' must be an item element");
            }

            return new ExecuteElementCommand((GUIItem)element);
        }
        else if (command.equals("DIALOG_OPEN"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            return new DialogOpenCommand(guiManager, dialogs.addDialog(args[argc]));
        }
        else if (command.equals("DIALOG_TOGGLE"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            return new DialogToggleCommand(guiManager, dialogs.addDialog(args[argc]));
        }
        else if (command.equals("DIALOG_CLOSE"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            return new DialogCloseCommand(guiManager, dialogs.addDialog(args[argc]));
        }
        else if (command.equals("GUI_EXECUTE_COMMAND"))
        {
            if (args.length < argc+1)
            {
                throw new IOException("syntax error");
            }

            final String commandString = ParseUtils.parseText(args, argc, lnr);
            return new ExecuteCommandCommand(commands, commandString, macros);
        }
        else if (command.equals("EXEC_SELECTION"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            final ExecSelectionCommand.CommandType commandType = NumberParser.parseEnum(ExecSelectionCommand.CommandType.class, args[argc], "command name");

            if (!(element instanceof GUIItemList))
            {
                throw new IOException("'"+element+"' must be an item list");
            }

            return new ExecSelectionCommand((GUIItemList)element, commandType, crossfireServerConnection, itemsManager.getCurrentFloorManager(), commandQueue);
        }
        else if (command.equals("MOVE_SELECTION"))
        {
            if (args.length != argc+2)
            {
                throw new IOException("syntax error");
            }

            final int diffLines = expressionParser.parseInt(args[argc]);
            final int diffElements = expressionParser.parseInt(args[argc+1]);
            if (diffLines == 0 && diffElements == 0)
            {
                throw new IOException("Invalid zero scroll distance");
            }

            if (!(element instanceof GUIList))
            {
                throw new IOException("'"+element+"' must be a list");
            }

            return new MoveSelectionCommand((GUIList)element, diffLines, diffElements);
        }
        else if (command.equals("SCROLL_LIST"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            final int distance = expressionParser.parseInt(args[argc]);
            if (distance == 0)
            {
                throw new IOException("Invalid zero scroll distance");
            }

            if (!(element instanceof GUIList))
            {
                throw new IOException("'"+element+"' must be a list");
            }

            return new ScrollListCommand((GUIList)element, distance);
        }
        else if (command.equals("SCROLL") || command.equals("SCROLL_NEVER"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            final int distance = expressionParser.parseInt(args[argc]);
            if (distance == 0)
            {
                throw new IOException("Invalid zero scroll distance");
            }

            if (!(element instanceof GUIScrollable))
            {
                throw new IOException("'"+element+"' must be a scrollable element");
            }

            return command.equals("SCROLL") ? new ScrollCommand(distance, (GUIScrollable)element) : new ScrollNeverCommand(distance, (GUIScrollable)element);
        }
        else if (command.equals("SCROLL_RESET"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            if (!(element instanceof GUIScrollable))
            {
                throw new IOException("'"+element+"' must be a scrollable element");
            }

            return new ScrollResetCommand((GUIScrollable)element);
        }
        else if (command.equals("SCROLLNEXT"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            final Object nextElement = definedGUIElements.lookup(args[argc]);
            if (!(nextElement instanceof ActivatableGUIElement))
            {
                throw new IOException("'"+args[argc]+"' cannot become active");
            }

            if (!(element instanceof ActivatableGUIElement))
            {
                throw new IOException("'"+element+"' cannot become active");
            }

            return new ScrollNextCommand((ActivatableGUIElement)nextElement, (ActivatableGUIElement)element);
        }
        else
        {
            throw new JXCSkinException("unknown command '"+command+"'");
        }
    }
}

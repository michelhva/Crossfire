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
package com.realtime.crossfire.jxclient.skin;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.gui.gauge.GaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.options.Option;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.skin.events.SkinEvent;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultJXCSkin implements JXCSkin
{
    /**
     * The default number of ground view objects.
     */
    private static final int DEFAULT_NUM_LOOK_OBJECTS = 50;

    /**
     * The skin name.
     */
    @NotNull
    private String skinName = "unknown";

    /**
     * The selected resolution.
     */
    @NotNull
    private final Resolution selectedResolution;

    /**
     * The map width in tiles; zero if unset.
     */
    private int mapWidth = 0;

    /**
     * The map height in tiles; zero if unset.
     */
    private int mapHeight = 0;

    /**
     * The maximum number of ground view objects.
     */
    private int numLookObjects = DEFAULT_NUM_LOOK_OBJECTS;

    /**
     * All "event init" commands in execution order.
     */
    @NotNull
    private final Collection<GUICommandList> initEvents = new ArrayList<GUICommandList>();

    /**
     * All defined command lists.
     */
    @NotNull
    private final JXCSkinCache<GUICommandList> definedCommandLists = new JXCSkinCache<GUICommandList>("command list");

    /**
     * All defined GUI elements.
     */
    @NotNull
    private final JXCSkinCache<GUIElement> definedGUIElements = new JXCSkinCache<GUIElement>("gui element");

    /**
     * All GUI elements.
     */
    @NotNull
    private final Collection<GUIElement> guiElements = new HashSet<GUIElement>();

    /**
     * All {@link SkinEvent}s attached to this instance.
     */
    @NotNull
    private final Collection<SkinEvent> skinEvents = new HashSet<SkinEvent>();

    /**
     * All defined dialogs.
     */
    @NotNull
    private final Dialogs dialogs;

    /**
     * The default key bindings.
     */
    @NotNull
    private final KeyBindings defaultKeyBindings;

    /**
     * The {@link OptionManager} to use.
     */
    @NotNull
    private final OptionManager optionManager;

    /**
     * The {@link ExperienceTable} to use.
     */
    @NotNull
    private final ExperienceTable experienceTable;

    /**
     * The {@link GaugeUpdaterParser} for parsing gauge specifications.
     */
    @NotNull
    private final GaugeUpdaterParser gaugeUpdaterParser;

    /**
     * The defined option names.
     */
    @NotNull
    private final Collection<String> optionNames = new HashSet<String>();

    /**
     * The defined {@link GaugeUpdater}s.
     */
    @NotNull
    private final Collection<GaugeUpdater> gaugeUpdaters = new ArrayList<GaugeUpdater>();

    /**
     * The {@link CommandParser} for parsing command specifications.
     */
    @NotNull
    private final CommandParser commandParser;

    /**
     * The tooltip label or <code>null</code>.
     */
    @Nullable
    private AbstractLabel tooltipLabel = null;

    /**
     * The {@link GuiManager} currently attached to or <code>null</code> if not
     * attached.
     */
    @Nullable
    private GuiManager guiManager = null;

    /**
     * Creates a new instance.
     * @param defaultKeyBindings the default key bindings
     * @param optionManager the option manager to use
     * @param stats the stats instance to use
     * @param itemsManager the items manager instance to use
     * @param experienceTable the experience table to use
     * @param skillSet the skill set for looking up skill names
     * @param expressionParser the expression parser to use
     * @param selectedResolution the resolution to use
     * @param dialogs the dialogs to use
     */
    public DefaultJXCSkin(@NotNull final KeyBindings defaultKeyBindings, @NotNull final OptionManager optionManager, @NotNull final Stats stats, @NotNull final ItemsManager itemsManager, @NotNull final ExperienceTable experienceTable, @NotNull final SkillSet skillSet, @NotNull final ExpressionParser expressionParser, @NotNull final Resolution selectedResolution, @NotNull final Dialogs dialogs)
    {
        this.defaultKeyBindings = defaultKeyBindings;
        this.optionManager = optionManager;
        this.experienceTable = experienceTable;
        this.selectedResolution = selectedResolution;
        gaugeUpdaterParser = new GaugeUpdaterParser(stats, itemsManager, skillSet);
        this.dialogs = dialogs;
        commandParser = new CommandParser(dialogs, itemsManager, expressionParser, definedGUIElements);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getSkinName()
    {
        return skinName+"@"+selectedResolution;
    }

    @NotNull
    public String getPlainSkinName()
    {
        return skinName;
    }

    public void setSkinName(@NotNull final String skinName)
    {
        this.skinName = skinName;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Resolution getResolution()
    {
        return selectedResolution;
    }

    /** {@inheritDoc} */
    @Override
    public int getMapWidth()
    {
        return mapWidth;
    }

    /** {@inheritDoc} */
    @Override
    public int getMapHeight()
    {
        return mapHeight;
    }

    /** {@inheritDoc} */
    @Override
    public int getNumLookObjects()
    {
        return numLookObjects;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Gui getDialogQuit()
    {
        try
        {
            return getDialog("quit");
        }
        catch (final JXCSkinException ex)
        {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Gui getDialogDisconnect()
    {
        try
        {
            return getDialog("disconnect");
        }
        catch (final JXCSkinException ex)
        {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Gui getDialogConnect()
    {
        try
        {
            return getDialog("connect");
        }
        catch (final JXCSkinException ex)
        {
            return null;
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Gui getDialogKeyBind()
    {
        try
        {
            return getDialog("keybind");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("keybind dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Gui getDialogQuery()
    {
        try
        {
            return getDialog("query");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("query dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Gui getDialogBook(final int booknr)
    {
        try
        {
            return getDialog("book");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("book dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Gui getMainInterface()
    {
        try
        {
            return getDialog("main");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("main dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Gui getMetaInterface()
    {
        try
        {
            return getDialog("meta");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("meta dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Gui getStartInterface()
    {
        try
        {
            return getDialog("start");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("start dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Gui getDialog(@NotNull final String name) throws JXCSkinException
    {
        return dialogs.lookup(name);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Iterator<Gui> iterator()
    {
        return dialogs.iterator();
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public GUICommandList getCommandList(@NotNull final String name) throws JXCSkinException
    {
        return definedCommandLists.lookup(name);
    }

    /**
     * Parses and builds command arguments.
     * @param listName the command list name to add to
     * @param args the list of arguments
     * @param argc the start index for parsing
     * @param element the target element
     * @param command the command to parse the arguments of
     * @param window the window instance
     * @param commands the commands instance for executing commands
     * @param lnr the source to read more parameters from
     * @param commandQueue the command queue for executing commands
     * @param crossfireServerConnection the server connection to use
     * @param guiManager the gui manager to use
     * @param macros the macros instance to use
     * @throws IOException if a syntax error occurs
     * @throws JXCSkinException if an element cannot be found
     */
    public void addCommand(@NotNull final String listName, @NotNull final String[] args, final int argc, @Nullable final GUIElement element, @NotNull final String command, @NotNull final JXCWindow window, @NotNull final Commands commands, @NotNull final LineNumberReader lnr, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final GuiManager guiManager, @NotNull final Macros macros) throws IOException, JXCSkinException
    {
        final GUICommandList commandList = getCommandList(listName);
        commandList.add(commandParser.parseCommandArgs(args, argc, element, command, window, commands, lnr, commandQueue, crossfireServerConnection, guiManager, macros));
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChangedDialog()
    {
        return dialogs.hasChangedDialog();
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public KeyBindings getDefaultKeyBindings()
    {
        return defaultKeyBindings;
    }

    /** {@inheritDoc} */
    @Override
    public void attach(@NotNull final GuiManager guiManager)
    {
        if (this.guiManager != null)
        {
            throw new IllegalStateException("skin is already attached");
        }

        this.guiManager = guiManager;
        guiManager.getWindowRenderer().setTooltip(tooltipLabel);
        guiManager.getTooltipManager().setTooltip(tooltipLabel);

        for (final GUICommandList commandList : initEvents)
        {
            commandList.execute();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void detach()
    {
        final GuiManager tmpGuiManager = guiManager;
        if (tmpGuiManager != null)
        {
            guiManager = null;
            tmpGuiManager.getWindowRenderer().setTooltip(null);
            tmpGuiManager.getTooltipManager().setTooltip(null);
        }

        for (final String optionName : optionNames)
        {
            optionManager.removeOption(optionName);
        }
        optionNames.clear();
        for (final GUIElement guiElement : guiElements)
        {
            guiElement.dispose();
        }
        guiElements.clear();
        definedGUIElements.clear();
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public GUIElement lookupGuiElement(@NotNull final String name) throws JXCSkinException
    {
        return definedGUIElements.lookup(name);
    }

    /**
     * Forgets about all named GUI elements. After this function has been
     * callled, {@link #lookupGuiElement(String)} and {@link
     * #guiElementIterator()} will not return anything.
     */
    public void clearDefinedGuiElements()
    {
        definedGUIElements.clear();
    }

    /**
     * Adds a new {@link GUIElement} to this skin.
     * @param guiElement the GUI element
     * @throws JXCSkinException if the name is not unique
     */
    public void insertGuiElement(@NotNull final GUIElement guiElement) throws JXCSkinException
    {
        definedGUIElements.insert(guiElement.getName(), guiElement);
        guiElements.add(guiElement);
    }

    @NotNull
    public Resolution getSelectedResolution()
    {
        return selectedResolution;
    }

    public void addDialog(@NotNull final String dialogName)
    {
        dialogs.addDialog(dialogName);
    }

    @Nullable
    public String getDialogToLoad()
    {
        return dialogs.getDialogToLoad();
    }

    public void addCommandList(@NotNull final String commandListName, @NotNull final GUICommandList.CommandType commandListCommandType) throws JXCSkinException
    {
        final GUICommandList commandList = new GUICommandList(commandListCommandType);
        definedCommandLists.insert(commandListName, commandList);
    }

    public void addInitEvent(@NotNull final GUICommandList commandList)
    {
        initEvents.add(commandList);
    }

    public void setMapSize(final int mapWidth, final int mapHeight)
    {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void setNumLookObjects(final int numLookObjects)
    {
        this.numLookObjects = numLookObjects;
    }

    public void addOption(@NotNull final String optionName, @NotNull final String documentation, @NotNull final Option commandCheckBoxOption) throws JXCSkinException
    {
        try
        {
            optionManager.addOption(optionName, documentation, commandCheckBoxOption);
        }
        catch (final OptionException ex)
        {
            throw new JXCSkinException(ex.getMessage());
        }
        optionNames.add(optionName);
    }

    public void setTooltipLabel(@Nullable final AbstractLabel tooltipLabel)
    {
        this.tooltipLabel = tooltipLabel;
    }

    /**
     * Returns an {@link Iterator} returning all named {@link GUIElement} of
     * this skin. The iterator does not support removal.
     * @return the iterator
     */
    @NotNull
    public Iterator<GUIElement> guiElementIterator()
    {
        return definedGUIElements.iterator();
    }

    /**
     * Creates a new {@link GaugeUpdater} instance from a string
     * representation.
     * @param name the gauge updater value to parse
     * @return the gauge updater
     * @throws IOException if the gauge updater value does not exist
     */
    @NotNull
    public GaugeUpdater newGaugeUpdater(@NotNull final String name) throws IOException
    {
        final GaugeUpdater gaugeUpdater = gaugeUpdaterParser.parseGaugeUpdater(name, experienceTable);
        gaugeUpdaters.add(gaugeUpdater);
        return gaugeUpdater;
    }

    /**
     * Records a {@link SkinEvent} attached to this instance.
     * @param skinEvent the skin event to add
     */
    public void addSkinEvent(@NotNull final SkinEvent skinEvent)
    {
        skinEvents.add(skinEvent);
    }
}

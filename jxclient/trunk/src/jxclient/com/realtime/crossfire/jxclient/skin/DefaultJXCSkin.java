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
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.settings.options.CommandCheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import com.realtime.crossfire.jxclient.window.MouseTracker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DefaultJXCSkin implements JXCSkin
{
    /**
     * The default number of ground view objects.
     */
    private static final int DEFAULT_NUM_LOOK_OBJECTS = 50;

    /**
     * The skin name.
     */
    private String skinName = "unknown";

    /**
     * The selected resolution.
     */
    private Resolution selectedResolution = new Resolution(true, 0, 0);

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
    private final List<GUICommandList> initEvents = new ArrayList<GUICommandList>();

    /**
     * All defined command lists.
     */
    private final JXCSkinCache<GUICommandList> definedCommandLists = new JXCSkinCache<GUICommandList>("command list");

    /**
     * All defined dialogs.
     */
    private final Dialogs dialogs = new Dialogs();

    /**
     * The default key bindings.
     */
    private final KeyBindings defaultKeyBindings;

    /**
     * The {@link OptionManager} to use.
     */
    private final OptionManager optionManager;

    /**
     * The defined option names.
     */
    private final Set<String> optionNames = new HashSet<String>();

    /**
     * Creates a new instance.
     * @param defaultKeyBindings the default key bindings
     * @param optionManager the option manager to use
     */
    public DefaultJXCSkin(final KeyBindings defaultKeyBindings, final OptionManager optionManager)
    {
        this.defaultKeyBindings = defaultKeyBindings;
        this.optionManager = optionManager;
    }

    public void reset()
    {
        unload();
        skinName = "unknown";
        mapWidth = 0;
        mapHeight = 0;
        numLookObjects = DEFAULT_NUM_LOOK_OBJECTS;
        dialogs.clear();
        definedCommandLists.clear();
    }

    /** {@inheritDoc} */
    @Override
    public String getSkinName()
    {
        return skinName+"@"+selectedResolution;
    }

    public String getPlainSkinName()
    {
        return skinName;
    }

    public void setSkinName(final String skinName)
    {
        this.skinName = skinName;
    }

    /** {@inheritDoc} */
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
    @Override
    public Gui getDialog(final String name) throws JXCSkinException
    {
        return dialogs.lookup(name);
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Gui> iterator()
    {
        return dialogs.iterator();
    }

    /** {@inheritDoc} */
    @Override
    public void executeInitEvents()
    {
        for (final GUICommandList commandList : initEvents)
        {
            commandList.execute();
        }
    }

    /** {@inheritDoc} */
    @Override
    public GUICommandList getCommandList(final String name) throws JXCSkinException
    {
        return definedCommandLists.lookup(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChangedDialog()
    {
        return dialogs.hasChangedDialog();
    }

    /** {@inheritDoc} */
    @Override
    public KeyBindings getDefaultKeyBindings()
    {
        return defaultKeyBindings;
    }

    /** {@inheritDoc} */
    @Override
    public void unload()
    {
        for (final String optionName : optionNames)
        {
            optionManager.removeOption(optionName);
        }
    }

    public CommandParser newCommandParser(final ItemsManager itemsManager, final ExpressionParser expressionParser, final JXCSkinCache<GUIElement> definedGUIElements)
    {
        return new CommandParser(dialogs, itemsManager, expressionParser, definedGUIElements);
    }

    public Resolution getSelectedResolution()
    {
        return selectedResolution;
    }

    public void setSelectedResolution(final Resolution selectedResolution)
    {
        this.selectedResolution = selectedResolution;
    }

    public void addDialog(final String dialogName, final JXCWindow window, final MouseTracker mouseTracker, final Commands commands)
    {
        dialogs.addDialog(dialogName, window, mouseTracker, commands);
    }

    public String getDialogToLoad()
    {
        return dialogs.getDialogToLoad();
    }

    public void addCommandList(final String commandListName, final GUICommandList commandList) throws JXCSkinException
    {
        definedCommandLists.insert(commandListName, commandList);
    }

    public void addInitEvent(final GUICommandList commandList)
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

    public void addOption(final String optionName, final String documentation, final CommandCheckBoxOption commandCheckBoxOption) throws JXCSkinException
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
}

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

package com.realtime.crossfire.jxclient.skin.io;

import com.realtime.crossfire.jxclient.gui.command.CommandType;
import com.realtime.crossfire.jxclient.gui.command.GUICommandList;
import com.realtime.crossfire.jxclient.gui.gauge.GaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.settings.options.Option;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.skin.events.SkinEvent;
import com.realtime.crossfire.jxclient.skin.skin.Dialogs;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinCache;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.util.Resolution;
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
     * The tooltip label or <code>null</code>.
     */
    @Nullable
    private AbstractLabel tooltipLabel = null;

    /**
     * The {@link JXCWindowRenderer} currently attached to or <code>null</code>
     * if not attached.
     */
    @Nullable
    private JXCWindowRenderer windowRenderer = null;

    /**
     * The {@link TooltipManager} currently attached to or <code>null</code> if
     * not attached.
     */
    @Nullable
    private TooltipManager tooltipManager = null;

    /**
     * Creates a new instance.
     * @param defaultKeyBindings the default key bindings
     * @param optionManager the option manager to use
     * @param selectedResolution the resolution to use
     * @param dialogs the dialogs to use
     */
    public DefaultJXCSkin(@NotNull final KeyBindings defaultKeyBindings, @NotNull final OptionManager optionManager, @NotNull final Resolution selectedResolution, @NotNull final Dialogs dialogs)
    {
        this.defaultKeyBindings = defaultKeyBindings;
        this.optionManager = optionManager;
        this.selectedResolution = selectedResolution;
        this.dialogs = dialogs;
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
    public void attach(@NotNull final JXCWindowRenderer windowRenderer, @NotNull final TooltipManager tooltipManager)
    {
        if (this.windowRenderer != null || this.tooltipManager != null)
        {
            throw new IllegalStateException("skin is already attached");
        }

        this.windowRenderer = windowRenderer;
        this.tooltipManager = tooltipManager;
        windowRenderer.setTooltip(tooltipLabel);
        tooltipManager.setTooltip(tooltipLabel);

        for (final GUICommandList commandList : initEvents)
        {
            commandList.execute();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void detach()
    {
        final JXCWindowRenderer tmpWindowRenderer = windowRenderer;
        final TooltipManager tmpTooltipManager = tooltipManager;
        windowRenderer = null;
        tooltipManager = null;
        if (tmpWindowRenderer != null)
        {
            tmpWindowRenderer.setTooltip(null);
        }
        if (tmpTooltipManager != null)
        {
            tmpTooltipManager.setTooltip(null);
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
    }

    /**
     * Adds a new {@link GUIElement} to this skin.
     * @param guiElement the GUI element
     * @throws JXCSkinException if the name is not unique
     */
    public void insertGuiElement(@NotNull final GUIElement guiElement) throws JXCSkinException
    {
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

    public void addCommandList(@NotNull final String commandListName, @NotNull final CommandType commandListCommandType) throws JXCSkinException
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
     * Adds a {@link GaugeUpdater} instance.
     * @param gaugeUpdater the gauge updater to add
     */
    public void addGaugeUpdater(@NotNull final GaugeUpdater gaugeUpdater)
    {
        gaugeUpdaters.add(gaugeUpdater);
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

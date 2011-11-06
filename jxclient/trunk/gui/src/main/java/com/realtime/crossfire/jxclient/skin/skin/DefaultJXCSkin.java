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

package com.realtime.crossfire.jxclient.skin.skin;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.commandlist.CommandListType;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.label.TooltipManagerImpl;
import com.realtime.crossfire.jxclient.settings.options.Option;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.skin.events.SkinEvent;
import com.realtime.crossfire.jxclient.util.Resolution;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default {@link JXCSkin} implementation.
 * @author Andreas Kirschbaum
 */
public class DefaultJXCSkin implements JXCSkin {

    /**
     * The skin name.
     */
    @NotNull
    private String skinName = "unknown";

    /**
     * The minimal resolution.
     */
    @NotNull
    private Resolution minResolution = new Resolution(1, 1);

    /**
     * The maximal resolution.
     */
    @NotNull
    private Resolution maxResolution = new Resolution(1, 1);

    /**
     * The current screen width.
     */
    private int currentScreenWidth = 0;

    /**
     * The current screen height.
     */
    private int currentScreenHeight = 0;

    /**
     * All "event init" commands in execution order.
     */
    @NotNull
    private final Collection<CommandList> initEvents = new ArrayList<CommandList>();

    /**
     * All defined command lists.
     */
    @NotNull
    private final JXCSkinCache<CommandList> definedCommandLists = new JXCSkinCache<CommandList>("command list");

    /**
     * All GUI elements.
     */
    @NotNull
    private final Collection<GUIElement> guiElements = new HashSet<GUIElement>();

    /**
     * All {@link SkinEvent SkinEvents} attached to this instance.
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
     * The tooltip label or <code>null</code>.
     */
    @Nullable
    private AbstractLabel tooltipLabel = null;

    /**
     * The {@link TooltipManagerImpl} currently attached to or <code>null</code>
     * if not attached.
     */
    @Nullable
    private TooltipManagerImpl tooltipManager = null;

    /**
     * Creates a new instance.
     * @param defaultKeyBindings the default key bindings
     * @param optionManager the option manager to use
     * @param dialogs the dialogs to use
     */
    public DefaultJXCSkin(@NotNull final KeyBindings defaultKeyBindings, @NotNull final OptionManager optionManager, @NotNull final Dialogs dialogs) {
        this.defaultKeyBindings = defaultKeyBindings;
        this.optionManager = optionManager;
        this.dialogs = dialogs;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getSkinName() {
        return skinName;
    }

    /**
     * Sets the skin name.
     * @param skinName the skin name
     * @param minResolution the minimal supported resolution
     * @param maxResolution the maximal supported resolution
     */
    public void setSkinName(@NotNull final String skinName, @NotNull final Resolution minResolution, @NotNull final Resolution maxResolution) {
        if (minResolution.getWidth() > maxResolution.getWidth()) {
            throw new IllegalArgumentException("minimum width must not exceed maximum width");
        }
        if (minResolution.getHeight() > maxResolution.getHeight()) {
            throw new IllegalArgumentException("minimum height must not exceed maximum height");
        }

        this.skinName = skinName;
        this.minResolution = minResolution;
        this.maxResolution = maxResolution;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Resolution getMinResolution() {
        return minResolution;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Resolution getMaxResolution() {
        return maxResolution;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Gui getDialogQuit() {
        try {
            return getDialog("quit");
        } catch (final JXCSkinException ignored) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Gui getDialogDisconnect() {
        try {
            return getDialog("disconnect");
        } catch (final JXCSkinException ignored) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Gui getDialogConnect() {
        try {
            return getDialog("connect");
        } catch (final JXCSkinException ignored) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Gui getDialogKeyBind() {
        try {
            return getDialog("keybind");
        } catch (final JXCSkinException ex) {
            final AssertionError error = new AssertionError("keybind dialog does not exist");
            error.initCause(ex);
            throw error;
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Gui getDialogQuery() {
        try {
            return getDialog("query");
        } catch (final JXCSkinException ex) {
            final AssertionError error = new AssertionError("query dialog does not exist");
            error.initCause(ex);
            throw error;
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Gui getDialogBook(final int bookNo) {
        try {
            return getDialog("book");
        } catch (final JXCSkinException ex) {
            final AssertionError error = new AssertionError("book dialog does not exist");
            error.initCause(ex);
            throw error;
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Gui getMainInterface() {
        try {
            return getDialog("main");
        } catch (final JXCSkinException ex) {
            final AssertionError error = new AssertionError("main dialog does not exist");
            error.initCause(ex);
            throw error;
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Gui getMetaInterface() {
        try {
            return getDialog("meta");
        } catch (final JXCSkinException ex) {
            final AssertionError error = new AssertionError("meta dialog does not exist");
            error.initCause(ex);
            throw error;
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Gui getStartInterface() {
        try {
            return getDialog("start");
        } catch (final JXCSkinException ex) {
            final AssertionError error = new AssertionError("start dialog does not exist");
            error.initCause(ex);
            throw error;
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Gui getDialog(@NotNull final String name) throws JXCSkinException {
        return dialogs.lookup(name);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Iterator<Gui> iterator() {
        return dialogs.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public CommandList getCommandList(@NotNull final String name) throws JXCSkinException {
        return definedCommandLists.lookup(name);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public KeyBindings getDefaultKeyBindings() {
        return defaultKeyBindings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attach(@NotNull final TooltipManagerImpl tooltipManager) {
        if (this.tooltipManager != null) {
            throw new IllegalStateException("skin is already attached");
        }

        this.tooltipManager = tooltipManager;
        tooltipManager.setTooltip(tooltipLabel);

        for (final CommandList commandList : initEvents) {
            commandList.execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach() {
        final TooltipManagerImpl tmpTooltipManager = tooltipManager;
        tooltipManager = null;
        if (tmpTooltipManager != null) {
            tmpTooltipManager.setTooltip(null);
        }

        for (final String optionName : optionNames) {
            optionManager.removeOption(optionName);
        }
        optionNames.clear();
        for (final GUIElement guiElement : guiElements) {
            guiElement.dispose();
        }
        for (final SkinEvent skinEvent : skinEvents) {
            skinEvent.dispose();
        }
        guiElements.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenSize(final int screenWidth, final int screenHeight) {
        final int newScreenWidth = Math.max(minResolution.getWidth(), Math.min(maxResolution.getWidth(), screenWidth));
        final int newScreenHeight = Math.max(minResolution.getHeight(), Math.min(maxResolution.getHeight(), screenHeight));
        if (currentScreenWidth == newScreenWidth && currentScreenHeight == newScreenHeight) {
            return;
        }
        currentScreenWidth = newScreenWidth;
        currentScreenHeight = newScreenHeight;
    }

    /**
     * Adds a new {@link GUIElement} to this skin.
     * @param guiElement the GUI element
     */
    public void insertGuiElement(@NotNull final GUIElement guiElement) {
        guiElements.add(guiElement);
    }

    /**
     * Defines a new dialog.
     * @param dialogName the dialog's name
     */
    public void addDialog(@NotNull final String dialogName) {
        dialogs.addDialog(dialogName);
    }

    /**
     * Returns one dialog pending loading. Each dialog is returned only once.
     * @return a dialog pending loading or <code>null</code>
     */
    @Nullable
    public String getDialogToLoad() {
        return dialogs.getDialogToLoad();
    }

    /**
     * Defines a new command list.
     * @param commandListName the command list's name
     * @param commandListType the command list's type
     * @throws JXCSkinException if the command list cannot be created
     */
    public void addCommandList(@NotNull final String commandListName, @NotNull final CommandListType commandListType) throws JXCSkinException {
        final CommandList commandList = new CommandList(commandListType);
        definedCommandLists.insert(commandListName, commandList);
    }

    /**
     * Adds a command list to be executed on "init" events.
     * @param commandList the command list
     */
    public void addInitEvent(@NotNull final CommandList commandList) {
        initEvents.add(commandList);
    }

    /**
     * Add a new option.
     * @param optionName the option name to add
     * @param documentation the documentation string for the settings
     * @param commandCheckBoxOption the option instance to forward to
     * @throws JXCSkinException if the option cannot be created
     */
    public void addOption(@NotNull final String optionName, @NotNull final String documentation, @NotNull final Option commandCheckBoxOption) throws JXCSkinException {
        try {
            optionManager.addOption(optionName, documentation, commandCheckBoxOption);
        } catch (final OptionException ex) {
            throw new JXCSkinException(ex.getMessage());
        }
        optionNames.add(optionName);
    }

    /**
     * Sets the {@link AbstractLabel} that is used to display tooltips.
     * @param tooltipLabel the label or <code>null</code> to disable tooltips
     */
    public void setTooltipLabel(@Nullable final AbstractLabel tooltipLabel) {
        this.tooltipLabel = tooltipLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public AbstractLabel getTooltipLabel() {
        return tooltipLabel;
    }

    /**
     * Records a {@link SkinEvent} attached to this instance.
     * @param skinEvent the skin event to add
     */
    public void addSkinEvent(@NotNull final SkinEvent skinEvent) {
        skinEvents.add(skinEvent);
    }

}

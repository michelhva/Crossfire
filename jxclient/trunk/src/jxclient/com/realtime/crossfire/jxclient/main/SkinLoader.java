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

package com.realtime.crossfire.jxclient.main;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiFactory;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.MouseTracker;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.skin.io.JXCSkinLoader;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinClassSource;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinDirSource;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinSource;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.Resolution;
import java.io.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Loader for {@link JXCSkin JXCSkins} and attaching them to the client.
 * @author Andreas Kirschbaum
 */
public class SkinLoader {

    /**
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    /**
     * The inventory {@link ItemView} instance.
     */
    @NotNull
    private final ItemView inventoryView;

    /**
     * The {@link FloorView} instance.
     */
    @NotNull
    private final FloorView floorView;

    /**
     * The {@link SpellsManager} instance.
     */
    @NotNull
    private final SpellsManager spellsManager;

    /**
     * The {@link Stats} instance.
     */
    @NotNull
    private final Stats stats;

    /**
     * The {@link SkillSet} instance.
     */
    @NotNull
    private final SkillSet skillSet;

    /**
     * The {@link CfMapUpdater} instance.
     */
    @NotNull
    private final CfMapUpdater mapUpdater;

    /**
     * The global experience table.
     */
    @NotNull
    private final ExperienceTable experienceTable;

    /**
     * The {@link MouseTracker} instance.
     */
    @NotNull
    private final MouseTracker mouseTracker;

    /**
     * The {@link CommandCallback} instance.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * The metaserver model instance for this window.
     */
    @NotNull
    private final MetaserverModel metaserverModel;

    /**
     * The current spell manager instance for this window.
     */
    @NotNull
    private final CurrentSpellManager currentSpellManager = new CurrentSpellManager();

    /**
     * The size of the client area. Set to <code>null</code> for default.
     */
    @Nullable
    private final Resolution resolution;

    /**
     * The {@link Macros} instance.
     */
    @NotNull
    private final Macros macros;

    /**
     * The {@link ItemSet} instance.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The {@link FacesManager} instance.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The option manager for this window.
     */
    @NotNull
    private final OptionManager optionManager;

    /**
     * The {@link JXCWindowRenderer} for this window.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link CrossfireServerConnection} to use.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * The {@link GuiStateManager} instance.
     */
    @NotNull
    private final GuiStateManager guiStateManager;

    /**
     * The {@link TooltipManager} instance.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link CommandQueue} instance.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * Creates a new instance.
     * @param debugGui whether GUI elements should be highlighted
     * @param experienceTable the experience table to use
     * @param skillSet the skill set to use
     * @param stats the stats to use
     * @param inventoryView the inventory item view to use
     * @param floorView the floor view to use
     * @param mapUpdater the map updater instance
     * @param spellsManager the spells manager instance
     * @param mouseTracker the mouse tracker to use
     * @param commandCallback the command callback to use
     * @param metaserverModel the metaserver model to use
     * @param resolution the size of the client area, <code>null</code> for
     * default
     * @param macros the macros instance
     * @param itemSet the item set to use
     * @param facesManager the faces manager to use
     * @param optionManager the option manager instance to use
     * @param windowRenderer the window renderer to use
     * @param server the crossfire server connection to use
     * @param guiStateManager the gui state manager to use
     * @param tooltipManager the tooltip manager to use
     * @param commandQueue the command queue to use
     */
    public SkinLoader(final boolean debugGui, @NotNull final ExperienceTable experienceTable, @NotNull final SkillSet skillSet, @NotNull final Stats stats, @NotNull final ItemView inventoryView, @NotNull final FloorView floorView, @NotNull final CfMapUpdater mapUpdater, @NotNull final SpellsManager spellsManager, @NotNull final MouseTracker mouseTracker, @NotNull final CommandCallback commandCallback, @NotNull final MetaserverModel metaserverModel, @Nullable final Resolution resolution, @NotNull final Macros macros, @NotNull final ItemSet itemSet, @NotNull final FacesManager facesManager, @NotNull final OptionManager optionManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final CrossfireServerConnection server, @NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManager tooltipManager, @NotNull final CommandQueue commandQueue) {
        this.debugGui = debugGui;
        this.experienceTable = experienceTable;
        this.skillSet = skillSet;
        this.stats = stats;
        this.inventoryView = inventoryView;
        this.floorView = floorView;
        this.mapUpdater = mapUpdater;
        this.spellsManager = spellsManager;
        this.mouseTracker = mouseTracker;
        this.commandCallback = commandCallback;
        this.metaserverModel = metaserverModel;
        this.resolution = resolution;
        this.macros = macros;
        this.itemSet = itemSet;
        this.facesManager = facesManager;
        this.optionManager = optionManager;
        this.windowRenderer = windowRenderer;
        this.server = server;
        this.guiStateManager = guiStateManager;
        this.tooltipManager = tooltipManager;
        this.commandQueue = commandQueue;
    }

    /**
     * Loads a skin file.
     * @param skinName the skin file name
     * @param commands the commands to use
     * @param shortcuts the shortcuts to use
     * @return the loaded skin
     * @throws JXCSkinException if the skin file cannot be loaded
     */
    @NotNull
    public JXCSkin loadSkin(@NotNull final String skinName, @NotNull final Commands commands, @NotNull final Shortcuts shortcuts) throws JXCSkinException {
        // check for skin in directory
        final File dir = new File(skinName);
        final KeyBindings defaultKeyBindings = new KeyBindings(null, commands, commandCallback, macros);
        final JXCSkinSource skinSource;
        if (dir.exists() && dir.isDirectory()) {
            skinSource = new JXCSkinDirSource(dir);
        } else {
            // fallback: built-in resource
            skinSource = new JXCSkinClassSource("com/realtime/crossfire/jxclient/skins/"+skinName);
        }
        final JXCSkinLoader newSkin = new JXCSkinLoader(itemSet, inventoryView, floorView, spellsManager, facesManager, stats, mapUpdater, defaultKeyBindings, optionManager, experienceTable, skillSet);
        final GuiFactory guiFactory = new GuiFactory(debugGui ? mouseTracker : null, commands, commandCallback, macros);
        final GUIElementListener elementListener = new GUIElementListener() {
            /** {@inheritDoc} */
            @Override
            public void mouseClicked(@NotNull final Gui gui) {
                windowRenderer.raiseDialog(gui);
            }
        };

        final JXCSkin skin = newSkin.load(skinSource, server, guiStateManager, tooltipManager, windowRenderer, elementListener, metaserverModel, commandQueue, shortcuts, commands, currentSpellManager, commandCallback, macros, guiFactory);
        if (resolution != null) {
            if (skin.getMinResolution().getWidth() > resolution.getWidth() || skin.getMinResolution().getHeight() > resolution.getHeight()) {
                throw new JXCSkinException("resolution "+resolution+" is not supported by this skin");
            }
            if (resolution.getWidth() > skin.getMaxResolution().getWidth() || resolution.getHeight() > skin.getMaxResolution().getHeight()) {
                throw new JXCSkinException("resolution "+resolution+" is not supported by this skin");
            }
        }

        return skin;
    }

}

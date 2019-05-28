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

package com.realtime.crossfire.jxclient.main;

import com.realtime.crossfire.jxclient.account.CharacterModel;
import com.realtime.crossfire.jxclient.faces.SmoothFaces;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.misc.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.textinput.CommandCallback;
import com.realtime.crossfire.jxclient.gui.textinput.CommandExecutor;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.Macros;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skin.io.JXCSkinLoader;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinClassSource;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinDirSource;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinSource;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.util.Resolution;
import java.awt.Component;
import java.io.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Loader for {@link JXCSkin JXCSkins} and attaching them to the client.
 * @author Andreas Kirschbaum
 */
public class SkinLoader {

    /**
     * The {@link CommandCallback} instance.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * The {@link MetaserverModel} instance for this window.
     */
    @NotNull
    private final MetaserverModel metaserverModel;

    /**
     * The {@link CharacterModel} instance for this window.
     */
    @NotNull
    private final CharacterModel characterModel;

    /**
     * The {@link SmoothFaces}.
     */
    @NotNull
    private final SmoothFaces smoothFaces;

    /**
     * The {@link GuiFactory} for creating {@link Gui} instances.
     */
    @NotNull
    private final GuiFactory guiFactory;

    /**
     * The {@link CurrentSpellManager} instance for this window.
     */
    @NotNull
    private final CurrentSpellManager currentSpellManager = new CurrentSpellManager();

    /**
     * The size of the client area. Set to {@code null} for default.
     */
    @Nullable
    private final Resolution resolution;

    /**
     * The {@link Macros} instance.
     */
    @NotNull
    private final Macros macros;

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
     * The {@link JXCSkinLoader} instance.
     */
    @NotNull
    private final JXCSkinLoader skinLoader;

    /**
     * The {@link CommandExecutor} instance.
     */
    @NotNull
    private final CommandExecutor commandExecutor;

    /**
     * The {@link Shortcuts} instance.
     */
    @NotNull
    private final Shortcuts shortcuts;

    /**
     * Creates a new instance.
     * @param commandCallback the command callback to use
     * @param metaserverModel the metaserver model to use
     * @param resolution the size of the client area, {@code null} for default
     * @param macros the macros instance
     * @param windowRenderer the window renderer to use
     * @param server the crossfire server connection to use
     * @param guiStateManager the gui state manager to use
     * @param tooltipManager the tooltip manager to use
     * @param commandQueue the command queue to use
     * @param skinLoader the skin loader instance
     * @param commandExecutor the command executor to use
     * @param shortcuts the shortcuts to use
     * @param characterModel the character model to use
     * @param smoothFaces the smooth faces
     * @param guiFactory the gui factory for creating GUI instances
     */
    public SkinLoader(@NotNull final CommandCallback commandCallback, @NotNull final MetaserverModel metaserverModel, @Nullable final Resolution resolution, @NotNull final Macros macros, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final CrossfireServerConnection server, @NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManager tooltipManager, @NotNull final CommandQueue commandQueue, @NotNull final JXCSkinLoader skinLoader, @NotNull final CommandExecutor commandExecutor, @NotNull final Shortcuts shortcuts, @NotNull final CharacterModel characterModel, @NotNull final SmoothFaces smoothFaces, @NotNull final GuiFactory guiFactory) {
        this.commandCallback = commandCallback;
        this.metaserverModel = metaserverModel;
        this.resolution = resolution;
        this.macros = macros;
        this.windowRenderer = windowRenderer;
        this.server = server;
        this.guiStateManager = guiStateManager;
        this.tooltipManager = tooltipManager;
        this.commandQueue = commandQueue;
        this.skinLoader = skinLoader;
        this.commandExecutor = commandExecutor;
        this.shortcuts = shortcuts;
        this.characterModel = characterModel;
        this.smoothFaces = smoothFaces;
        this.guiFactory = guiFactory;
    }

    /**
     * Loads a skin file.
     * @param skinName the skin file name
     * @return the loaded skin
     * @throws JXCSkinException if the skin file cannot be loaded
     */
    @NotNull
    public JXCSkin loadSkin(@NotNull final String skinName) throws JXCSkinException {
        // check for skin in directory
        final File dir = new File(skinName);
        final JXCSkinSource skinSource = dir.exists() && dir.isDirectory() ? new JXCSkinDirSource(dir) : new JXCSkinClassSource("com/realtime/crossfire/jxclient/skins/"+skinName);
        final GUIElementListener elementListener = new GUIElementListener() {

            @Override
            public void raiseDialog(@NotNull final Component component) {
                final Gui gui = GuiUtils.getGui(component);
                if (gui != null) {
                    windowRenderer.raiseDialog(gui);
                }
            }

            @Override
            public void activeChanged(@NotNull final ActivatableGUIElement element, final boolean active) {
                final Gui gui = GuiUtils.getGui(element);
                if (gui != null) {
                    gui.setActiveElement(element, active);
                }
            }

            @Override
            public boolean isActive(@NotNull final ActivatableGUIElement element) {
                final Gui gui = GuiUtils.getGui(element);
                return gui != null && gui.isActiveElement(element);
            }

        };

        final JXCSkin skin = skinLoader.load(skinSource, server, guiStateManager, tooltipManager, windowRenderer, elementListener, metaserverModel, characterModel, commandQueue, shortcuts, commandExecutor, currentSpellManager, commandCallback, macros, guiFactory, smoothFaces);
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

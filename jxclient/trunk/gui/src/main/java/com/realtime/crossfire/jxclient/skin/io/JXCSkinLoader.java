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

import com.realtime.crossfire.jxclient.account.CharacterModel;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesProvider;
import com.realtime.crossfire.jxclient.faces.FacesProviderFactory;
import com.realtime.crossfire.jxclient.faces.SmoothFaces;
import com.realtime.crossfire.jxclient.gui.button.ButtonImages;
import com.realtime.crossfire.jxclient.gui.button.GUIButton;
import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.commandlist.CommandListType;
import com.realtime.crossfire.jxclient.gui.commands.CommandCheckBoxOption;
import com.realtime.crossfire.jxclient.gui.gauge.GUIDupGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUIDupTextGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUIGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUITextGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.Orientation;
import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Expression;
import com.realtime.crossfire.jxclient.gui.gui.Extent;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiState;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemFloor;
import com.realtime.crossfire.jxclient.gui.item.GUIItemFloorFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventoryFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItemFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemQuestListFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemShortcut;
import com.realtime.crossfire.jxclient.gui.item.GUIItemSpell;
import com.realtime.crossfire.jxclient.gui.item.GUIItemSpellListFactory;
import com.realtime.crossfire.jxclient.gui.item.ItemPainter;
import com.realtime.crossfire.jxclient.gui.keybindings.InvalidKeyBindingException;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.keybindings.KeybindingsManager;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.label.Alignment;
import com.realtime.crossfire.jxclient.gui.label.GUIHTMLLabel;
import com.realtime.crossfire.jxclient.gui.label.GUILabelFailure;
import com.realtime.crossfire.jxclient.gui.label.GUILabelMessage;
import com.realtime.crossfire.jxclient.gui.label.GUILabelQuery;
import com.realtime.crossfire.jxclient.gui.label.GUILabelStats;
import com.realtime.crossfire.jxclient.gui.label.GUILabelStats2;
import com.realtime.crossfire.jxclient.gui.label.GUIMultiLineLabel;
import com.realtime.crossfire.jxclient.gui.label.GUIOneLineLabel;
import com.realtime.crossfire.jxclient.gui.label.GUISpellLabel;
import com.realtime.crossfire.jxclient.gui.label.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.label.Type;
import com.realtime.crossfire.jxclient.gui.list.GUICharacterList;
import com.realtime.crossfire.jxclient.gui.list.GUIFloorList;
import com.realtime.crossfire.jxclient.gui.list.GUIItemList;
import com.realtime.crossfire.jxclient.gui.list.GUIMetaElementList;
import com.realtime.crossfire.jxclient.gui.list.GUIQuestList;
import com.realtime.crossfire.jxclient.gui.list.GUISpellList;
import com.realtime.crossfire.jxclient.gui.log.Fonts;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.log.GUIMessageLog;
import com.realtime.crossfire.jxclient.gui.log.MessageBufferUpdater;
import com.realtime.crossfire.jxclient.gui.map.GUIMap;
import com.realtime.crossfire.jxclient.gui.map.GUIMiniMap;
import com.realtime.crossfire.jxclient.gui.misc.GUIFill;
import com.realtime.crossfire.jxclient.gui.misc.GUIPicture;
import com.realtime.crossfire.jxclient.gui.misc.GUIScrollBar;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable2;
import com.realtime.crossfire.jxclient.gui.textinput.CommandCallback;
import com.realtime.crossfire.jxclient.gui.textinput.CommandExecutor;
import com.realtime.crossfire.jxclient.gui.textinput.GUICommandText;
import com.realtime.crossfire.jxclient.gui.textinput.GUIQueryText;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.gui.textinput.GUITextField;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.items.QuestsView;
import com.realtime.crossfire.jxclient.items.SpellsView;
import com.realtime.crossfire.jxclient.map.MapUpdaterState;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.quests.QuestsManager;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.MessageTypes;
import com.realtime.crossfire.jxclient.server.socket.UnknownCommandException;
import com.realtime.crossfire.jxclient.settings.Macros;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.skin.events.ConnectionStateSkinEvent;
import com.realtime.crossfire.jxclient.skin.events.CrossfireMagicmapSkinEvent;
import com.realtime.crossfire.jxclient.skin.events.MapScrollSkinEvent;
import com.realtime.crossfire.jxclient.skin.events.SkillAddedSkinEvent;
import com.realtime.crossfire.jxclient.skin.events.SkillRemovedSkinEvent;
import com.realtime.crossfire.jxclient.skin.factory.CheckBoxFactory;
import com.realtime.crossfire.jxclient.skin.factory.DialogFactory;
import com.realtime.crossfire.jxclient.skin.factory.TextButtonFactory;
import com.realtime.crossfire.jxclient.skin.skin.DefaultJXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.Dialogs;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinCache;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.skin.source.JXCSkinSource;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.NumberParser;
import com.realtime.crossfire.jxclient.util.Resolution;
import com.realtime.crossfire.jxclient.util.ResourceUtils;
import com.realtime.crossfire.jxclient.util.StringUtils;
import com.realtime.crossfire.jxclient.util.UnterminatedTokenException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.swing.GroupLayout;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for loading {@link JXCSkin} instances from {@link JXCSkinSource
 * JXCSkinSources}.
 * @author Andreas Kirschbaum
 */
public class JXCSkinLoader {

    /**
     * The kind of list to create in {@link #parseList(Args, ListType,
     * TooltipManager, GUIElementListener, CommandQueue,
     * CrossfireServerConnection, CurrentSpellManager, Image, Image)}.
     */
    private enum ListType {

        /**
         * Create a {@link GUIItemList} instance.
         */
        INVENTORY,

        /**
         * Create a {@link GUIFloorList} instance.
         */
        GROUND,

        /**
         * Create a {@link GUISpellList} instance.
         */
        SPELL,

        /**
         * Create a {@link GUIQuestList} instance.
         */
        QUEST
    }

    /**
     * The border width of dialogs.
     */
    private static final int DIALOG_BORDER_WIDTH = 5;

    /**
     * The {@link ItemSet} instance to use.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The inventory {@link ItemView} to use.
     */
    @NotNull
    private final ItemView inventoryView;

    /**
     * The {@link FloorView} to use.
     */
    @NotNull
    private final FloorView floorView;

    /**
     * The {@link SpellsView} to use.
     */
    @NotNull
    private final SpellsView spellView;

    /**
     * The {@link QuestsView} to use.
     */
    @NotNull final QuestsView questView;

    /**
     * The {@link SpellsManager} instance to use.
     */
    @NotNull
    private final SpellsManager spellsManager;

    /**
     * The {@link QuestsManager} instance to use.
     */
    @NotNull
    private final QuestsManager questsManager;

    /**
     * The {@link FacesManager} instance to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The default tile size for the map view.
     */
    private final int defaultTileSize;

    /**
     * The {@link FacesProviderFactory} instance for creating faces provider
     * instances.
     */
    @NotNull
    private final FacesProviderFactory facesProviderFactory;

    /**
     * The {@link Stats} instance to use.
     */
    @NotNull
    private final Stats stats;

    /**
     * The {@link MapUpdaterState} instance to use.
     */
    @NotNull
    private final MapUpdaterState mapUpdaterState;

    /**
     * The default key bindings.
     */
    @NotNull
    private final KeyBindings defaultKeyBindings;

    /**
     * The {@link OptionManager} instance to use.
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
    private GaugeUpdaterParser gaugeUpdaterParser;

    /**
     * The {@link SkillSet} instance to use.
     */
    @NotNull
    private final SkillSet skillSet;

    /**
     * All defined fonts.
     */
    @NotNull
    private final JXCSkinCache<Font> definedFonts = new JXCSkinCache<Font>("font");

    /**
     * The text button factory. Set to <code>null</code> until defined.
     */
    @Nullable
    private TextButtonFactory textButtonFactory = null;

    /**
     * The dialog factory. Set to <code>null</code> until defined.
     */
    @Nullable
    private DialogFactory dialogFactory = null;

    /**
     * The checkbox factory. Set to <code>null</code> until defined.
     */
    @Nullable
    private CheckBoxFactory checkBoxFactory = null;

    /**
     * The {@link ItemPainter} for default item parameters. Set to
     * <code>null</code> until defined.
     */
    @Nullable
    private ItemPainter defaultItemPainter = null;

    /**
     * The {@link CommandParser} for parsing commands.
     */
    @NotNull
    private CommandParser commandParser;

    /**
     * The {@link ImageParser} for parsing image specifications.
     */
    @NotNull
    private ImageParser imageParser;

    /**
     * The {@link FontParser} for parsing font specifications.
     */
    @NotNull
    private FontParser fontParser;

    /**
     * The {@link GuiElementParser} for parsing gui element specifications.
     */
    @NotNull
    private GuiElementParser guiElementParser;

    /**
     * The defined {@link GUIElement GUIElements}.
     */
    @NotNull
    private final JXCSkinCache<AbstractGUIElement> definedGUIElements = new JXCSkinCache<AbstractGUIElement>("gui element");

    /**
     * The {@link JXCSkin} being loaded.
     */
    @NotNull
    private DefaultJXCSkin skin;

    /**
     * A {@link Comparator} that compares {@link GUIElement} instances by
     * element name.
     */
    @NotNull
    private static final Comparator<GUIElement> ELEMENT_COMPARATOR = new Comparator<GUIElement>() {

        @Override
        public int compare(@NotNull final GUIElement o1, @NotNull final GUIElement o2) {
            final Comparable<String> n1 = o1.getName();
            final String n2 = o2.getName();
            return n1.compareTo(n2);
        }

    };

    /**
     * The {@link KeybindingsManager} containing key bindings.
     */
    @NotNull
    private final KeybindingsManager keybindingsManager;

    /**
     * Creates a new instance.
     * @param itemSet the item set instance to use
     * @param inventoryView the inventory item view to use
     * @param floorView the floor view to use
     * @param spellView the spells view to use
     * @param spellsManager the spells manager instance to use
     * @param facesManager the faces manager instance to use
     * @param stats the stats instance to use
     * @param mapUpdaterState the map updater state instance to use
     * @param defaultKeyBindings the default key bindings
     * @param optionManager the option manager to use
     * @param experienceTable the experience table to use
     * @param skillSet the skill set to use
     * @param defaultTileSize the default tile size for the map view
     * @param keybindingsManager the keybindings manager to use
     * @param questView the quests view to use
     * @param questsManager the quests manager instance to use
     */
    public JXCSkinLoader(@NotNull final ItemSet itemSet, @NotNull final ItemView inventoryView, @NotNull final FloorView floorView, @NotNull final SpellsView spellView, @NotNull final SpellsManager spellsManager, @NotNull final FacesManager facesManager, @NotNull final Stats stats, @NotNull final MapUpdaterState mapUpdaterState, @NotNull final KeyBindings defaultKeyBindings, @NotNull final OptionManager optionManager, @NotNull final ExperienceTable experienceTable, @NotNull final SkillSet skillSet, final int defaultTileSize, @NotNull final KeybindingsManager keybindingsManager, @NotNull final QuestsManager questsManager, @NotNull final QuestsView questView) {
        this.itemSet = itemSet;
        this.inventoryView = inventoryView;
        this.floorView = floorView;
        this.spellView = spellView;
        this.spellsManager = spellsManager;
        this.facesManager = facesManager;
        this.defaultTileSize = defaultTileSize;
        facesProviderFactory = new FacesProviderFactory(facesManager);
        this.stats = stats;
        this.mapUpdaterState = mapUpdaterState;
        this.defaultKeyBindings = defaultKeyBindings;
        this.optionManager = optionManager;
        this.experienceTable = experienceTable;
        this.skillSet = skillSet;
        this.keybindingsManager = keybindingsManager;
        this.questsManager = questsManager;
        this.questView = questView;
    }

    /**
     * Loads the skin from its external representation.
     * @param skinSource the source to load from
     * @param crossfireServerConnection the server connection to attach to
     * @param guiStateManager the gui state manager instance
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer to add to
     * @param elementListener the element listener to notify
     * @param metaserverModel the metaserver mode to use
     * @param characterModel the character model to use
     * @param commandQueue the command queue to use
     * @param shortcuts the shortcuts to use
     * @param commandExecutor the command executor for executing commands
     * @param currentSpellManager the current spell manager to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @param guiFactory the gui factory for creating gui elements
     * @param smoothFaces the smooth faces
     * @return the loaded skin
     * @throws JXCSkinException if the skin cannot be loaded
     */
    @NotNull
    public JXCSkin load(@NotNull final JXCSkinSource skinSource, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel, @NotNull final CharacterModel characterModel, @NotNull final CommandQueue commandQueue, @NotNull final Shortcuts shortcuts, @NotNull final CommandExecutor commandExecutor, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros, @NotNull final GuiFactory guiFactory, @NotNull final SmoothFaces smoothFaces) throws JXCSkinException {
        imageParser = new ImageParser(skinSource);
        fontParser = new FontParser(skinSource);

        final Image nextGroupFace;
        try {
            nextGroupFace = ResourceUtils.loadImage(ResourceUtils.NEXT_GROUP_FACE).getImage();
        } catch (final IOException ex) {
            throw new JXCSkinException(ex.getMessage());
        }
        final Image prevGroupFace;
        try {
            prevGroupFace = ResourceUtils.loadImage(ResourceUtils.PREV_GROUP_FACE).getImage();
        } catch (final IOException ex) {
            throw new JXCSkinException(ex.getMessage());
        }

        final Dialogs dialogs = new Dialogs(guiFactory);
        gaugeUpdaterParser = new GaugeUpdaterParser(stats, itemSet, skillSet);
        commandParser = new CommandParser(dialogs, floorView, definedGUIElements);
        skin = new DefaultJXCSkin(defaultKeyBindings, optionManager, dialogs);
        @Nullable JXCSkin skinToDetach = skin;
        try {
            guiElementParser = new GuiElementParser(definedGUIElements);
            imageParser.clear();
            skin.addDialog("keybind");
            skin.addDialog("query");
            skin.addDialog("book");
            skin.addDialog("main");
            skin.addDialog("meta");
            skin.addDialog("quit");
            skin.addDialog("disconnect");
            skin.addDialog("connect");
            skin.addDialog("start");
            skin.addDialog("account_main");
            skin.addDialog("account_characters");
            definedFonts.clear();
            textButtonFactory = null;
            dialogFactory = null;
            checkBoxFactory = null;
            try {
                load(skinSource, "global", crossfireServerConnection, guiStateManager, tooltipManager, windowRenderer, elementListener, metaserverModel, characterModel, commandQueue, null, shortcuts, commandExecutor, currentSpellManager, commandCallback, macros, nextGroupFace, prevGroupFace, smoothFaces);
                while (true) {
                    final String name = skin.getDialogToLoad();
                    if (name == null) {
                        break;
                    }
                    final Gui gui = skin.getDialog(name);
                    load(skinSource, name, crossfireServerConnection, guiStateManager, tooltipManager, windowRenderer, elementListener, metaserverModel, characterModel, commandQueue, gui, shortcuts, commandExecutor, currentSpellManager, commandCallback, macros, nextGroupFace, prevGroupFace, smoothFaces);
                }
            } finally {
                definedFonts.clear();
                textButtonFactory = null;
                dialogFactory = null;
                checkBoxFactory = null;
                imageParser.clear();
            }

            skinToDetach = null;
        } finally {
            if (skinToDetach != null) {
                skinToDetach.detach();
                windowRenderer.setTooltip(null);
            }
        }

        return skin;
    }

    /**
     * Loads a skin file and add the entries to a {@link Gui} instance.
     * @param skinSource th source to load from
     * @param dialogName the key to identify this dialog
     * @param server the server connection to monitor
     * @param guiStateManager the gui state manager instance
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer to add to
     * @param elementListener the element listener to notify
     * @param metaserverModel the metaserver model to use
     * @param characterModel the character model to use
     * @param commandQueue the command queue for sending commands
     * @param gui the Gui representing the skin file
     * @param shortcuts the shortcuts instance
     * @param commandExecutor the command executor for executing commands
     * @param currentSpellManager the current spell manager to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     * @param smoothFaces the smooth faces
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(@NotNull final JXCSkinSource skinSource, @NotNull final String dialogName, @NotNull final CrossfireServerConnection server, @NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel, @NotNull final CharacterModel characterModel, @NotNull final CommandQueue commandQueue, @Nullable final Gui gui, @NotNull final Shortcuts shortcuts, @NotNull final CommandExecutor commandExecutor, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace, @NotNull final SmoothFaces smoothFaces) throws JXCSkinException {
        String resourceName = dialogName+".skin";

        definedGUIElements.clear();
        try {
            InputStream inputStream;
            try {
                inputStream = skinSource.getInputStream(resourceName);
            } catch (final IOException ignored) {
                resourceName = dialogName+".skin";
                inputStream = skinSource.getInputStream(resourceName);
            }
            try {
                load(skinSource, dialogName, resourceName, inputStream, server, guiStateManager, tooltipManager, windowRenderer, elementListener, metaserverModel, characterModel, commandQueue, gui, shortcuts, commandExecutor, currentSpellManager, commandCallback, macros, nextGroupFace, prevGroupFace, smoothFaces);
            } finally {
                inputStream.close();
            }
        } catch (final IOException ex) {
            throw new JXCSkinException(skinSource.getURI(resourceName)+": "+ex.getMessage());
        } catch (final JXCSkinException ex) {
            throw new JXCSkinException(skinSource.getURI(resourceName)+": "+ex.getMessage());
        } finally {
            definedGUIElements.clear();
        }
    }

    /**
     * Loads a skin file and add the entries to a {@link Gui} instance.
     * @param skinSource the source to load from
     * @param dialogName the key to identify this dialog
     * @param resourceName the name of the skin resource; used to construct
     * error messages
     * @param inputStream the input stream to load from
     * @param server the server connection to monitor
     * @param guiStateManager the gui state manager instance
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer to add to
     * @param elementListener the element listener to notify
     * @param metaserverModel the metaserver model to use
     * @param characterModel the character model to use
     * @param commandQueue the command queue for sending commands
     * @param gui the Gui representing the skin file
     * @param shortcuts the shortcuts instance
     * @param commandExecutor the command executor for executing commands
     * @param currentSpellManager the current spell manager to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     * @param smoothFaces the smooth faces
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(@NotNull final JXCSkinSource skinSource, @NotNull final String dialogName, @NotNull final String resourceName, @NotNull final InputStream inputStream, @NotNull final CrossfireServerConnection server, @NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel, @NotNull final CharacterModel characterModel, @NotNull final CommandQueue commandQueue, @Nullable final Gui gui, @NotNull final Shortcuts shortcuts, @NotNull final CommandExecutor commandExecutor, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace, @NotNull final SmoothFaces smoothFaces) throws JXCSkinException {
        try {
            final InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            try {
                final LineNumberReader lnr = new LineNumberReader(isr);
                try {
                    boolean isDialog = false;
                    while (true) {
                        final String line = lnr.readLine();
                        if (line == null) {
                            break;
                        }

                        if (line.startsWith("#") || line.length() == 0) {
                            continue;
                        }

                        final String[] argsTmp;
                        try {
                            argsTmp = StringUtils.splitFields(line);
                        } catch (final UnterminatedTokenException ex) {
                            throw new JXCSkinException(ex.getMessage());
                        }
                        final Args args = new Args(argsTmp);
                        final String cmd = args.get();
                        if (gui != null && cmd.equals("button")) {
                            parseButton(args, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("checkbox")) {
                            parseCheckbox(args, tooltipManager, elementListener, lnr);
                        } else if (cmd.equals("commandlist")) {
                            parseCommandList(args, guiStateManager, lnr, commandExecutor, commandQueue, server, commandCallback, macros);
                        } else if (cmd.equals("commandlist_add")) {
                            parseCommandListAdd(args, guiStateManager, lnr, commandExecutor, commandQueue, server, commandCallback, macros);
                        } else if (gui != null && cmd.equals("command_text")) {
                            parseCommandText(args, commandCallback, tooltipManager, elementListener, commandExecutor);
                        } else if (cmd.equals("def")) {
                            parseDef(args, lnr);
                        } else if (gui != null && cmd.equals("dialog")) {
                            if (isDialog) {
                                throw new JXCSkinException("'dialog' must not be used more than once per dialog");
                            }
                            parseDialog(args, tooltipManager, windowRenderer, elementListener, lnr, gui, dialogName);
                            isDialog = true;
                        } else if (gui != null && cmd.equals("dialog_hide")) {
                            parseDialogHide(args, gui);
                        } else if (gui != null && cmd.equals("dupgauge")) {
                            parseDupGauge(args, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("duptextgauge")) {
                            parseDupTextGauge(args, tooltipManager, elementListener, lnr);
                        } else if (cmd.equals("event")) {
                            parseEvent(args, guiStateManager, server);
                        } else if (gui != null && cmd.equals("fill")) {
                            parseFill(args, tooltipManager, elementListener);
                        } else if (cmd.equals("font")) {
                            parseFont(args);
                        } else if (gui != null && cmd.equals("gauge")) {
                            parseGauge(args, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("ignore")) {
                            parseIgnore(args);
                        } else if (gui != null && cmd.equals("inventory_list")) {
                            parseList(args, ListType.INVENTORY, tooltipManager, elementListener, commandQueue, server, currentSpellManager, nextGroupFace, prevGroupFace);
                        } else if (gui != null && cmd.equals("floor_list")) {
                            parseList(args, ListType.GROUND, tooltipManager, elementListener, commandQueue, server, currentSpellManager, nextGroupFace, prevGroupFace);
                        } else if (gui != null && cmd.equals("spells_list")) {
                            parseList(args, ListType.SPELL, tooltipManager, elementListener, commandQueue, server, currentSpellManager, nextGroupFace, prevGroupFace);
                        } else if (gui != null && cmd.equals("quests_list")) {
                            parseList(args, ListType.QUEST, tooltipManager, elementListener, commandQueue, server, currentSpellManager, nextGroupFace, prevGroupFace);
                        } else if (gui != null && cmd.equals("horizontal")) {
                            parseHorizontal(args, gui, lnr, isDialog);
                        } else if (gui != null && cmd.equals("item")) {
                            parseItem(args, tooltipManager, elementListener, commandQueue, server, shortcuts, currentSpellManager, nextGroupFace, prevGroupFace);
                        } else if (cmd.equals("key")) {
                            parseKey(args, gui, line);
                        } else if (gui != null && cmd.equals("label_html")) {
                            parseLabelHtml(args, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("label_multi")) {
                            parseLabelMulti(args, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("label_query")) {
                            parseLabelQuery(args, tooltipManager, elementListener, server);
                        } else if (gui != null && cmd.equals("label_failure")) {
                            parseLabelFailure(args, tooltipManager, elementListener, server);
                        } else if (gui != null && cmd.equals("label_message")) {
                            parseLabelMessage(args, tooltipManager, elementListener, server, windowRenderer);
                        } else if (gui != null && cmd.equals("label_text")) {
                            parseLabelText(args, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("label_stat")) {
                            parseLabelStat(args, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("label_stat2")) {
                            parseLabelStat2(args, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("label_spell")) {
                            parseLabelSpell(args, tooltipManager, elementListener, currentSpellManager);
                        } else if (gui != null && cmd.equals("link_size")) {
                            parseLinkSize(args, gui);
                        } else if (gui != null && cmd.equals("log_label")) {
                            parseLogLabel(args, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("log_message")) {
                            parseLogMessage(args, tooltipManager, elementListener, server);
                        } else if (gui != null && cmd.equals("log_color")) {
                            parseLogColor(args);
                        } else if (gui != null && cmd.equals("log_filter")) {
                            parseLogFilter(args);
                        } else if (gui != null && cmd.equals("minimap")) {
                            parseMinimap(args, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("map")) {
                            parseMap(args, tooltipManager, elementListener, server, smoothFaces);
                        } else if (gui != null && cmd.equals("meta_list")) {
                            parseMetaList(args, tooltipManager, elementListener, metaserverModel);
                        } else if (gui != null && cmd.equals("picture")) {
                            parsePicture(args, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("query_text")) {
                            parseQueryText(args, server, commandCallback, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("set_forced_active")) {
                            parseSetForcedActive(args, gui);
                        } else if (gui != null && cmd.equals("set_auto_size")) {
                            parseSetAutoSize(gui, args);
                        } else if (gui != null && cmd.equals("set_default")) {
                            parseSetDefault(args);
                        } else if (gui != null && cmd.equals("set_invisible")) {
                            parseSetInvisible(args);
                        } else if (gui != null && cmd.equals("set_modal")) {
                            parseSetModal(gui);
                        } else if (gui != null && cmd.equals("scrollbar")) {
                            parseScrollbar(args, tooltipManager, elementListener);
                        } else if (gui == null && cmd.equals("skin_name")) {
                            parseSkinName(args);
                        } else if (gui != null && cmd.equals("text")) {
                            parseText(args, commandCallback, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("textbutton")) {
                            parseTextButton(args, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("textgauge")) {
                            parseTextGauge(args, tooltipManager, elementListener, lnr);
                        } else if (cmd.equals("tooltip")) {
                            parseTooltip(args, tooltipManager, elementListener);
                        } else if (cmd.equals("vertical")) {
                            parseVertical(args, gui, lnr, isDialog);
                        } else if (cmd.equals("character_list")) {
                            parseCharacterList(args, tooltipManager, elementListener, characterModel);
                        } else if (cmd.equals("hide_input")) {
                            parseHideInput(args);
                        } else {
                            throw new IOException("unknown keyword '"+cmd+"'");
                        }

                        if (args.hasMore()) {
                            throw new IOException("excess arguments");
                        }
                    }
                } catch (final IOException ex) {
                    throw new IOException(ex.getMessage()+" in line "+lnr.getLineNumber());
                } catch (final JXCSkinException ex) {
                    throw new IOException(ex.getMessage()+" in line "+lnr.getLineNumber());
                } catch (final IllegalArgumentException ex) {
                    final Object msg = ex.getMessage();
                    if (msg != null) {
                        throw new IOException("invalid parameter ("+ex.getMessage()+") in line "+lnr.getLineNumber());
                    } else {
                        throw new IOException("invalid parameter in line "+lnr.getLineNumber());
                    }
                } finally {
                    lnr.close();
                }
            } finally {
                isr.close();
            }
        } catch (final IOException ex) {
            throw new JXCSkinException(skinSource.getURI(resourceName)+": "+ex.getMessage());
        }

        final Iterator<AbstractGUIElement> it = definedGUIElements.iterator();
        if (gui == null) {
            assert !it.hasNext();
        }

        //if (gui != null) {
        //    gui.validate();
        //    final JFrame frame = new JFrame(dialogName);
        //    frame.setLayout(new BorderLayout());
        //    frame.add(gui, BorderLayout.CENTER);
        //    frame.setSize(800, 600);
        //    frame.setBackground(new Color(192, 0, 0));
        //    frame.setVisible(true);
        //    //ComponentDumper.dump(gui);
        //}
    }

    /**
     * Parses a "button" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseButton(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final BufferedImage upImage = imageParser.getImage(args.get());
        final BufferedImage downImage = imageParser.getImage(args.get());
        final boolean autoRepeat = NumberParser.parseBoolean(args.get());
        final CommandList commandList = skin.getCommandList(args.get());
        @Nullable final String label;
        @Nullable final Font font;
        @Nullable final Color color;
        final int textX;
        final int textY;
        if (args.hasMore()) {
            font = definedFonts.lookup(args.get());
            color = ParseUtils.parseColor(args.get());
            textX = ExpressionParser.parseInt(args.get());
            textY = ExpressionParser.parseInt(args.get());
            label = ParseUtils.parseText(args, lnr);
        } else {
            label = null;
            font = null;
            color = null;
            textX = 0;
            textY = 0;
        }
        insertGuiElement(new GUIButton(tooltipManager, elementListener, name, upImage, downImage, label, font, color, textX, textY, autoRepeat, commandList));
    }

    /**
     * Parses a "checkbox" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCheckbox(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        if (checkBoxFactory == null) {
            throw new IOException("missing 'def checkbox' command");
        }

        final String name = args.get();
        final CheckBoxOption option = ParseUtils.parseCheckBoxOption(args.get(), optionManager);
        final String text = ParseUtils.parseText(args, lnr);
        assert checkBoxFactory != null;
        insertGuiElement(checkBoxFactory.newCheckBox(tooltipManager, elementListener, name, option, text));
    }

    /**
     * Parses a "commandlist" command.
     * @param args the command arguments
     * @param guiStateManager the gui state manager instance
     * @param lnr the line number reader for reading more lines
     * @param commandExecutor the command executor for executing the commands
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandList(@NotNull final Args args, @NotNull final GuiStateManager guiStateManager, @NotNull final LineNumberReader lnr, @NotNull final CommandExecutor commandExecutor, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection server, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) throws IOException, JXCSkinException {
        final String commandListName = args.get();
        final CommandListType commandListType = NumberParser.parseEnum(CommandListType.class, args.get(), "type");
        skin.addCommandList(commandListName, commandListType);
        if (args.hasMore()) {
            final AbstractGUIElement element = args.get().equals("null") ? null : definedGUIElements.lookup(args.getPrev());
            addCommand(commandListName, args, element, args.get(), guiStateManager, commandExecutor, lnr, commandQueue, server, commandCallback, macros);
        }
    }

    /**
     * Parses a "commandlist_add" command.
     * @param args the command arguments
     * @param guiStateManager the gui state manager instance
     * @param lnr the line number reader for reading more lines
     * @param commandExecutor the command executor for executing commands
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandListAdd(@NotNull final Args args, @NotNull final GuiStateManager guiStateManager, @NotNull final LineNumberReader lnr, @NotNull final CommandExecutor commandExecutor, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection server, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) throws IOException, JXCSkinException {
        final String name = args.get();
        final AbstractGUIElement element = args.get().equals("null") ? null : definedGUIElements.lookup(args.getPrev());
        addCommand(name, args, element, args.get(), guiStateManager, commandExecutor, lnr, commandQueue, server, commandCallback, macros);
    }

    /**
     * Parses a "command_text" command.
     * @param args the command arguments
     * @param commandCallback the command callback to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandExecutor the command executor for executing commands
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandText(@NotNull final Args args, @NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandExecutor commandExecutor) throws IOException, JXCSkinException {
        final String name = args.get();
        final Image activeImage = imageParser.getImage(args.get());
        final Image inactiveImage = imageParser.getImage(args.get());
        final Font font = definedFonts.lookup(args.get());
        final Color inactiveColor = ParseUtils.parseColor(args.get());
        final Color activeColor = ParseUtils.parseColor(args.get());
        final int margin = ExpressionParser.parseInt(args.get());
        final boolean enableHistory = NumberParser.parseBoolean(args.get());
        insertGuiElement(new GUICommandText(commandCallback, tooltipManager, elementListener, name, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", commandExecutor, enableHistory));
    }

    /**
     * Parses a "def" command.
     * @param args the command arguments
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDef(@NotNull final Args args, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String type = args.get();
        if (type.equals("checkbox")) {
            final BufferedImage checkedImage = imageParser.getImage(args.get());
            final BufferedImage uncheckedImage = imageParser.getImage(args.get());
            final Font font = definedFonts.lookup(args.get());
            final Color color = ParseUtils.parseColor(args.get());
            checkBoxFactory = new CheckBoxFactory(checkedImage, uncheckedImage, font, color);
        } else if (type.equals("checkbox_option")) {
            final String optionName = args.get();
            final CommandList commandOn = skin.getCommandList(args.get());
            final CommandList commandOff = skin.getCommandList(args.get());
            final String documentation = ParseUtils.parseText(args, lnr);
            skin.addOption(optionName, documentation, new CommandCheckBoxOption(commandOn, commandOff, documentation));
        } else if (type.equals("dialog")) {
            final String frame = args.get();
            final Image frameNW = imageParser.getImage(frame+"_nw");
            final Image frameN = imageParser.getImage(frame+"_n");
            final Image frameNE = imageParser.getImage(frame+"_ne");
            final Image frameW = imageParser.getImage(frame+"_w");
            final Image frameC = imageParser.getImage(frame+"_c");
            final Image frameE = imageParser.getImage(frame+"_e");
            final Image frameSW = imageParser.getImage(frame+"_sw");
            final Image frameS = imageParser.getImage(frame+"_s");
            final Image frameSE = imageParser.getImage(frame+"_se");
            final Font titleFont = definedFonts.lookup(args.get());
            final Color titleColor = ParseUtils.parseColor(args.get());
            final Color titleBackgroundColor = ParseUtils.parseColor(args.get());
            final float alpha = NumberParser.parseFloat(args.get());
            if (alpha < 0 || alpha > 1F) {
                throw new IOException("invalid alpha value: "+alpha);
            }
            dialogFactory = new DialogFactory(frameNW, frameN, frameNE, frameW, frameC, frameE, frameSW, frameS, frameSE, titleFont, titleColor, titleBackgroundColor, alpha);
        } else if (type.equals("item")) {
            final Color cursedColor = ParseUtils.parseColorNull(args.get());
            final Image cursedImage = imageParser.getImage(cursedColor, args.getPrev());
            final Color damnedColor = ParseUtils.parseColorNull(args.get());
            final Image damnedImage = imageParser.getImage(damnedColor, args.getPrev());
            final Color magicColor = ParseUtils.parseColorNull(args.get());
            final Image magicImage = imageParser.getImage(magicColor, args.getPrev());
            final Color blessedColor = ParseUtils.parseColorNull(args.get());
            final Image blessedImage = imageParser.getImage(blessedColor, args.getPrev());
            final Color appliedColor = ParseUtils.parseColorNull(args.get());
            final Image appliedImage = imageParser.getImage(appliedColor, args.getPrev());
            final Color unidentifiedColor = ParseUtils.parseColorNull(args.get());
            final Image unidentifiedImage = imageParser.getImage(unidentifiedColor, args.getPrev());
            final Color selectorColor = ParseUtils.parseColorNull(args.get());
            final Image selectorImage = imageParser.getImage(selectorColor, args.getPrev());
            final Color lockedColor = ParseUtils.parseColorNull(args.get());
            final Image lockedImage = imageParser.getImage(lockedColor, args.getPrev());
            final Color unpaidColor = ParseUtils.parseColorNull(args.get());
            final Image unpaidImage = imageParser.getImage(unpaidColor, args.getPrev());
            final Font font = definedFonts.lookup(args.get());
            final Color nrofColor = ParseUtils.parseColor(args.get());
            defaultItemPainter = new ItemPainter(cursedImage, damnedImage, magicImage, blessedImage, appliedImage, unidentifiedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, unidentifiedColor, selectorColor, lockedColor, unpaidColor, font, nrofColor);
        } else if (type.equals("textbutton")) {
            final String up = args.get();
            final String down = args.get();
            final Font font = definedFonts.lookup(args.get());
            final Color color = ParseUtils.parseColor(args.get());
            final Color colorSelected = ParseUtils.parseColor(args.get());
            final ButtonImages upImages = new ButtonImages(imageParser.getImage(up+"_w"), imageParser.getImage(up+"_c"), imageParser.getImage(up+"_e"));
            final ButtonImages downImages = new ButtonImages(imageParser.getImage(down+"_w"), imageParser.getImage(down+"_c"), imageParser.getImage(down+"_e"));
            textButtonFactory = new TextButtonFactory(upImages, downImages, font, color, colorSelected);
        } else {
            throw new IOException("unknown type '"+type+"'");
        }
    }

    /**
     * Parses a "dialog" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer the dialog belongs to
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @param gui the gui instance to add to
     * @param dialogName the dialog name
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDialog(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr, @NotNull final Gui gui, @NotNull final String dialogName) throws IOException, JXCSkinException {
        if (dialogFactory == null) {
            throw new IOException("missing 'def dialog' command");
        }

        final Expression defaultX = ExpressionParser.parseExpression(args.get());
        final Expression defaultY = ExpressionParser.parseExpression(args.get());
        final boolean saveDialog = NumberParser.parseBoolean(args.get());
        final String title = ParseUtils.parseText(args, lnr);
        assert dialogFactory != null;
        final Iterable<AbstractGUIElement> elements = dialogFactory.newDialog(tooltipManager, windowRenderer, elementListener, title);
        for (final AbstractGUIElement element : elements) {
            insertGuiElement(element);
        }
        gui.setName(dialogName);
        gui.setDefaultPosition(defaultX, defaultY);
        if (saveDialog) {
            gui.setSaveDialog();
        }
    }

    /**
     * Parses a "dialog_hide" command.
     * @param args the command arguments
     * @param gui the gui instance to use
     * @throws IOException if the command cannot be parsed
     */
    private static void parseDialogHide(@NotNull final Args args, @NotNull final Gui gui) throws IOException {
        do {
            gui.hideInState(NumberParser.parseEnum(RendererGuiState.class, args.get(), "gui state"));
        } while (args.hasMore());
    }

    /**
     * Parses a "dupgauge" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDupGauge(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Image positiveDivImage = imageParser.getImage(args.get());
        final Image positiveModImage = imageParser.getImage(args.get());
        final Image emptyImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final GaugeUpdater gaugeUpdater = newGaugeUpdater(args.get());
        final Orientation orientationDiv = ParseUtils.parseOrientation(args.get());
        final Orientation orientationMod = ParseUtils.parseOrientation(args.get());
        final String tooltipPrefix = ParseUtils.parseText(args, lnr);
        final GUIDupGauge element = new GUIDupGauge(tooltipManager, elementListener, name, positiveDivImage, positiveModImage, emptyImage, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
        insertGuiElement(element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses a "duptextgauge" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDupTextGauge(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Image positiveDivImage = imageParser.getImage(args.get());
        final Image positiveModImage = imageParser.getImage(args.get());
        final Image emptyImage = imageParser.getImage(args.get());
        final GaugeUpdater gaugeUpdater = newGaugeUpdater(args.get());
        final Orientation orientationDiv = ParseUtils.parseOrientation(args.get());
        final Orientation orientationMod = ParseUtils.parseOrientation(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final Font font = definedFonts.lookup(args.get());
        final String tooltipPrefix = ParseUtils.parseText(args, lnr);
        final GUIDupTextGauge element = new GUIDupTextGauge(tooltipManager, elementListener, name, positiveDivImage, positiveModImage, emptyImage, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
        insertGuiElement(element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses an "event" command.
     * @param args the command arguments
     * @param guiStateManager the gui state manager instance
     * @param server the server to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseEvent(@NotNull final Args args, @NotNull final GuiStateManager guiStateManager, @NotNull final CrossfireServerConnection server) throws IOException, JXCSkinException {
        final String type = args.get();
        if (type.equals("connect")) {
            final CommandList commandList = skin.getCommandList(args.get());
            skin.addSkinEvent(new ConnectionStateSkinEvent(commandList, guiStateManager));
        } else if (type.equals("init")) {
            skin.addInitEvent(skin.getCommandList(args.get()));
        } else if (type.equals("magicmap")) {
            final CommandList commandList = skin.getCommandList(args.get());
            skin.addSkinEvent(new CrossfireMagicmapSkinEvent(commandList, server));
        } else if (type.equals("mapscroll")) {
            final CommandList commandList = skin.getCommandList(args.get());
            skin.addSkinEvent(new MapScrollSkinEvent(commandList, mapUpdaterState));
        } else if (type.equals("skill")) {
            final String subtype = args.get();
            final Skill skill = skillSet.getNamedSkill(args.get().replaceAll("_", " "));
            final CommandList commandList = skin.getCommandList(args.get());
            if (subtype.equals("add")) {
                skin.addSkinEvent(new SkillAddedSkinEvent(commandList, skill));
            } else if (subtype.equals("del")) {
                skin.addSkinEvent(new SkillRemovedSkinEvent(commandList, skill));
            } else {
                throw new IOException("undefined event sub-type: "+subtype);
            }
        } else {
            throw new IOException("undefined event type: "+type);
        }
    }

    /**
     * Parses a "fill" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseFill(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Color color = ParseUtils.parseColor(args.get());
        final float alpha = NumberParser.parseFloat(args.get());
        if (alpha < 0 || alpha > 1F) {
            throw new IOException("invalid alpha value: "+alpha);
        }
        insertGuiElement(new GUIFill(tooltipManager, elementListener, name, color, alpha));
    }

    /**
     * Parses a "font" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseFont(@NotNull final Args args) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font fontNormal = fontParser.getFont(args.get());
        final Font font = fontNormal.deriveFont(NumberParser.parseFloat(args.get()));
        definedFonts.insert(name, font);
    }

    /**
     * Parses a "gauge" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseGauge(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        if (dialogFactory == null) {
            throw new IOException("missing 'def dialog' command");
        }

        final String name = args.get();
        final Image positiveImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Image negativeImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Image emptyImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final GaugeUpdater gaugeUpdater = newGaugeUpdater(args.get());
        final Orientation orientation = ParseUtils.parseOrientation(args.get());
        final String tooltipPrefix = ParseUtils.parseText(args, lnr);
        assert dialogFactory != null;
        final float alpha = dialogFactory.getFrameAlpha();
        final GUIGauge element = new GUIGauge(tooltipManager, elementListener, name, positiveImage, negativeImage, emptyImage, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null, alpha);
        insertGuiElement(element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses an "ignore" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseIgnore(@NotNull final Args args) throws IOException, JXCSkinException {
        final String name = args.get();
        definedGUIElements.lookup(name).setIgnore();
    }

    /**
     * Parses an "inventory_list", "floor_list", "spells_list" or "quests_list" command.
     * @param args the command arguments
     * @param type {@link ListType} list type to create
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param currentSpellManager the current spell manager to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseList(@NotNull final Args args, final ListType type, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection server, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) throws IOException, JXCSkinException {
        if (defaultItemPainter == null) {
            throw new IOException("cannot use '"+type.toString()+"_list"+"' without 'def item' command");
        }

        final String name = args.get();
        final int cellWidth = ExpressionParser.parseInt(args.get());
        final int cellHeight = ExpressionParser.parseInt(args.get());
        final AbstractLabel selectedItem = args.get().equals("null") ? null : guiElementParser.lookupLabelElement(args.getPrev());

        assert defaultItemPainter != null;
        final ItemPainter itemPainter = defaultItemPainter.newItemPainter();
        final AbstractGUIElement element;
        switch (type) {
        case INVENTORY:
            final GUIItemItemFactory inventoryItemFactory = new GUIItemInventoryFactory(tooltipManager, elementListener, commandQueue, name, itemPainter, server, facesManager, floorView, inventoryView);
            element = new GUIItemList(tooltipManager, elementListener, name, cellWidth, cellHeight, inventoryView, selectedItem, inventoryItemFactory);
            break;

        case GROUND:
            final GUIItemItemFactory groundItemFactory = new GUIItemFloorFactory(tooltipManager, elementListener, commandQueue, name, itemPainter, server, facesManager, floorView, itemSet, nextGroupFace, prevGroupFace);
            element = new GUIFloorList(tooltipManager, elementListener, name, cellWidth, cellHeight, floorView, selectedItem, groundItemFactory);
            break;

        case SPELL:
            final GUIItemItemFactory spellItemFactory = new GUIItemSpellListFactory(tooltipManager, elementListener, commandQueue, name, itemPainter, facesManager, spellsManager, currentSpellManager, spellView);
            element = new GUISpellList(tooltipManager, elementListener, name, cellWidth, cellHeight, spellView, selectedItem, spellItemFactory, spellsManager, keybindingsManager);
            break;

        case QUEST:
            final GUIItemItemFactory questItemFactory = new GUIItemQuestListFactory(tooltipManager, elementListener, name, itemPainter, facesManager, questsManager, questView);
            element = new GUIQuestList(tooltipManager, elementListener, name, cellWidth, cellHeight, questView, selectedItem, questItemFactory, questsManager);
            break;

        default:
            throw new AssertionError("unhandled type "+type);
        }
        insertGuiElement(element);
    }

    /**
     * Parses a "horizontal" command.
     * @param args the command arguments
     * @param gui the gui
     * @param lnr the line number reader to read more lines  @throws IOException
     * if the command cannot be parsed
     * @param isDialog whether a "dialog" command has been executed
     * @throws JXCSkinException if the command cannot be parsed
     * @throws IOException if the command cannot be parsed
     */
    private void parseHorizontal(@NotNull final Args args, @NotNull final Container gui, @NotNull final LineNumberReader lnr, final boolean isDialog) throws IOException, JXCSkinException {
        final GroupLayout layout = (GroupLayout)gui.getLayout();
        final String begin = args.get();
        if (!begin.equals("begin")) {
            throw new IOException("syntax error: expecting 'begin'");
        }
        final Collection<GUIElement> unreferencedElements = new TreeSet<GUIElement>(ELEMENT_COMPARATOR);
        for (final GUIElement element : definedGUIElements) {
            unreferencedElements.add(element);
        }
        if (!isDialog) {
            final GroupLayout.Group content = parseBegin(args, layout, lnr, unreferencedElements);
            if (!unreferencedElements.isEmpty()) {
                throw new IOException("layout doesn't define elements "+unreferencedElements);
            }
            layout.setHorizontalGroup(content);
            return;
        }
        final Component background = getUnreferencedElement("dialog_background", unreferencedElements);
        if (background == null) {
            throw new AssertionError("element 'dialog_background' is missing");
        }
        final GroupLayout.Group content = parseBegin(args, layout, lnr, unreferencedElements);
        final Component title = getUnreferencedElement("dialog_title", unreferencedElements);
        final Component close = getUnreferencedElement("dialog_close", unreferencedElements);
        final GroupLayout.Group group2 = layout.createSequentialGroup();
        group2.addGap(DIALOG_BORDER_WIDTH);
        if (title == null) {
            if (close == null) {
                final GroupLayout.Group group3 = layout.createParallelGroup();
                group3.addGroup(content);
                group2.addGroup(group3);
            } else {
                final GroupLayout.Group group3 = layout.createParallelGroup();
                group3.addGap(0, 0, Short.MAX_VALUE);
                group3.addComponent(close);
                group2.addGroup(group3);
                group2.addGroup(content);
            }
        } else {
            final GroupLayout.Group group3 = layout.createParallelGroup();
            if (close == null) {
                group3.addComponent(title);
                group3.addGroup(content);
            } else {
                final GroupLayout.Group group4 = layout.createSequentialGroup();
                group4.addComponent(title);
                group4.addComponent(close);
                group3.addGroup(group4);
                group3.addGroup(content);
            }
            group2.addGroup(group3);
        }
        group2.addGap(DIALOG_BORDER_WIDTH);
        if (!unreferencedElements.isEmpty()) {
            throw new IOException("layout doesn't define elements "+unreferencedElements);
        }

        final GroupLayout.Group group1 = layout.createParallelGroup();
        group1.addGroup(group2);
        group1.addComponent(background);

        layout.setHorizontalGroup(group1);
    }

    /**
     * Returns an unreferenced GUI element by name.
     * @param name the gui element's name
     * @param unreferencedElements the unreferenced gui elements
     * @return the named unreferenced gui element or <code>null</code>
     */
    @Nullable
    private Component getUnreferencedElement(@NotNull final String name, @NotNull final Collection<GUIElement> unreferencedElements) {
        final AbstractGUIElement component = definedGUIElements.lookupOptional(name);
        if (component == null) {
            return null;
        }

        return unreferencedElements.remove(component) ? component : null;
    }

    /**
     * Parses an "item" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param shortcuts the shortcuts to use
     * @param currentSpellManager the current spell manager to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseItem(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection server, @NotNull final Shortcuts shortcuts, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) throws IOException, JXCSkinException {
        final String type = args.get();
        final String name = args.get();
        final int index = ExpressionParser.parseInt(args.get());
        final AbstractGUIElement element;
        if (type.equals("floor")) {
            if (defaultItemPainter == null) {
                throw new IOException("cannot use 'item floor' without 'def item' command");
            }

            final ItemPainter itemPainter = defaultItemPainter.newItemPainter();
            element = new GUIItemFloor(tooltipManager, elementListener, commandQueue, name, itemPainter, index, server, floorView, itemSet, facesManager, nextGroupFace, prevGroupFace);
        } else if (type.equals("inventory")) {
            if (defaultItemPainter == null) {
                throw new IOException("cannot use 'item inventory' without 'def item' command");
            }

            final ItemPainter itemPainter = defaultItemPainter.newItemPainter();
            element = new GUIItemInventory(tooltipManager, elementListener, commandQueue, name, itemPainter, index, server, facesManager, floorView, inventoryView);
        } else if (type.equals("shortcut")) {
            final Color castColor = ParseUtils.parseColorNull(args.get());
            final Image castImage = imageParser.getImage(castColor, args.getPrev());
            final Color invokeColor = ParseUtils.parseColorNull(args.get());
            final Image invokeImage = imageParser.getImage(invokeColor, args.getPrev());
            final Font font = definedFonts.lookup(args.get());
            element = new GUIItemShortcut(tooltipManager, elementListener, name, castColor, castImage, invokeColor, invokeImage, index, facesManager, shortcuts, font, currentSpellManager);
        } else if (type.equals("spelllist")) {
            if (defaultItemPainter == null) {
                throw new IOException("cannot use 'item spelllist' without 'def item' command");
            }

            final ItemPainter itemPainter = defaultItemPainter.newItemPainter();
            element = new GUIItemSpell(tooltipManager, elementListener, commandQueue, name, itemPainter, index, facesManager, spellsManager, currentSpellManager, spellView);
        } else {
            throw new IOException("undefined item type: "+type);
        }
        insertGuiElement(element);
    }

    /**
     * Parses a "key" command.
     * @param args the command arguments
     * @param gui the gui to add to
     * @param line the unparsed command line
     * @throws IOException if the command cannot be parsed
     */
    private void parseKey(@NotNull final Args args, @Nullable final Gui gui, @NotNull final String line) throws IOException {
        final KeyBindings keyBindings = gui != null ? gui.getKeyBindings() : skin.getDefaultKeyBindings();
        try {
            keyBindings.parseKeyBinding(line.substring(4).trim(), true);
        } catch (final InvalidKeyBindingException ex) {
            throw new IOException("invalid key binding: "+ex.getMessage());
        }

        // consume all arguments to prevent syntax error
        while (args.hasMore()) {
            args.get();
        }
    }

    /**
     * Parses a "label_html" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelHtml(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final String text = ParseUtils.parseText(args, lnr);
        insertGuiElement(new GUIHTMLLabel(tooltipManager, elementListener, name, null, font, color, null, text));
    }

    /**
     * Parses a "label_multi" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelMulti(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final String text = ParseUtils.parseText(args, lnr);
        insertGuiElement(new GUIMultiLineLabel(tooltipManager, elementListener, name, null, font, color, null, Alignment.LEFT, text));
    }

    /**
     * Parses a "label_query" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server instance to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelQuery(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final AbstractGUIElement element = new GUILabelQuery(tooltipManager, elementListener, name, server, font, color, null);
        insertGuiElement(element);
    }

    /**
     * Parses a "label_message" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server instance to monitor
     * @param windowRenderer the window renderer to create the element for
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelMessage(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server, @NotNull final JXCWindowRenderer windowRenderer) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final AbstractGUIElement element = new GUILabelMessage(tooltipManager, elementListener, name, server, windowRenderer, font, color, null);
        insertGuiElement(element);
    }

    /**
     * Parses a "label_failure" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server instance to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelFailure(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final AbstractGUIElement element = new GUILabelFailure(tooltipManager, elementListener, name, server, font, color, null);
        insertGuiElement(element);
    }

    /**
     * Parses a "label_text" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelText(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final String text = ParseUtils.parseText(args, lnr);
        insertGuiElement(new GUIOneLineLabel(tooltipManager, elementListener, name, null, font, color, null, Alignment.LEFT, text));
    }

    /**
     * Parses a "label_stat" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelStat(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final int stat = ParseUtils.parseStat(args.get());
        final Alignment alignment = NumberParser.parseEnum(Alignment.class, args.get(), "text alignment");
        final AbstractGUIElement element = new GUILabelStats(tooltipManager, elementListener, name, font, color, null, stat, alignment, stats);
        insertGuiElement(element);
    }

    /**
     * Parses a "label_stat2" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelStat2(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Color colorNormal = ParseUtils.parseColor(args.get());
        final Color colorUpgradable = ParseUtils.parseColor(args.get());
        final Color colorDepleted = ParseUtils.parseColor(args.get());
        final Color colorBoosted = ParseUtils.parseColor(args.get());
        final Color colorBoostedUpgradable = ParseUtils.parseColor(args.get());
        final int statCurrent = ParseUtils.parseStat(args.get());
        final int statBase = ParseUtils.parseStat(args.get());
        final int statRace = ParseUtils.parseStat(args.get());
        final int statApplied = ParseUtils.parseStat(args.get());
        final Alignment alignment = NumberParser.parseEnum(Alignment.class, args.get(), "text alignment");
        final AbstractGUIElement element = new GUILabelStats2(tooltipManager, elementListener, name, font, colorNormal, colorUpgradable, colorDepleted, colorBoosted, colorBoostedUpgradable, null, statCurrent, statBase, statRace, statApplied, alignment, stats);
        insertGuiElement(element);
    }

    /**
     * Parses a "label_spell" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param currentSpellManager the current spell manager to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelSpell(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CurrentSpellManager currentSpellManager) throws IOException, JXCSkinException {
        final String name = args.get();
        final Font font = definedFonts.lookup(args.get());
        final Type type = NumberParser.parseEnum(Type.class, args.get(), "label type");
        final AbstractGUIElement element = new GUISpellLabel(tooltipManager, elementListener, name, null, facesManager, font, type, currentSpellManager);
        insertGuiElement(element);
    }

    /**
     * Parses a "link_size" command.
     * @param args the command arguments
     * @param gui the gui
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLinkSize(@NotNull final Args args, @NotNull final Container gui) throws IOException, JXCSkinException {
        final String type = args.get();
        final List<Component> elements = new ArrayList<Component>();
        while (args.hasMore()) {
            elements.add(definedGUIElements.lookup(args.get()));
        }
        if (elements.size() < 2) {
            throw new IOException("'link_size' needs at least two gui elements");
        }
        final Component[] array = elements.toArray(new Component[elements.size()]);
        final GroupLayout layout = (GroupLayout)gui.getLayout();
        if (type.equals("horizontal")) {
            layout.linkSize(SwingConstants.HORIZONTAL, array);
        } else if (type.equals("vertical")) {
            layout.linkSize(SwingConstants.VERTICAL, array);
        } else if (type.equals("both")) {
            layout.linkSize(SwingConstants.HORIZONTAL, array);
            layout.linkSize(SwingConstants.VERTICAL, array);
        } else {
            throw new IOException("undefined type '"+type+"'");
        }
    }

    /**
     * Parses a "log_label" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogLabel(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Image backgroundImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Font fontPrint = definedFonts.lookup(args.get());
        final Font fontFixed = definedFonts.lookup(args.get());
        final Font fontFixedBold = definedFonts.lookup(args.get());
        final Font fontArcane = definedFonts.lookup(args.get());
        final Color defaultColor = ParseUtils.parseColor(args.get());
        final Fonts fonts = new Fonts(fontPrint, fontFixed, fontFixedBold, fontArcane);
        final AbstractGUIElement element = new GUILabelLog(tooltipManager, elementListener, name, backgroundImage, fonts, defaultColor);
        insertGuiElement(element);
    }

    /**
     * Parses a "log_message" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogMessage(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server) throws IOException, JXCSkinException {
        final String name = args.get();
        final Image backgroundImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Font fontPrint = definedFonts.lookup(args.get());
        final Font fontFixed = definedFonts.lookup(args.get());
        final Font fontFixedBold = definedFonts.lookup(args.get());
        final Font fontArcane = definedFonts.lookup(args.get());
        final Color defaultColor = ParseUtils.parseColor(args.get());
        final Fonts fonts = new Fonts(fontPrint, fontFixed, fontFixedBold, fontArcane);
        final AbstractGUIElement element = new GUIMessageLog(tooltipManager, elementListener, name, server, backgroundImage, fonts, defaultColor);
        insertGuiElement(element);
    }

    /**
     * Parses a "log_color" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogColor(@NotNull final Args args) throws IOException, JXCSkinException {
        final String name = args.get();
        final int index = ExpressionParser.parseInt(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final Object element = definedGUIElements.lookup(name);
        if (!(element instanceof GUIMessageLog)) {
            throw new IOException("element '"+name+"' is not of type 'log'");
        }
        if (index < 0 || index >= MessageBufferUpdater.NUM_COLORS) {
            throw new IOException("invalid color index "+index);
        }
        ((GUIMessageLog)element).setColor(index, color);
    }

    /**
     * Parses a "log_filter" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogFilter(@NotNull final Args args) throws IOException, JXCSkinException {
        final String name = args.get();
        final String type = args.get();
        final boolean add;
        if (type.equals("only")) {
            add = true;
        } else if (type.equals("not")) {
            add = false;
        } else {
            throw new IOException("type '"+type+"' is invalid");
        }
        int types = 0;
        do {
            try {
                types |= 1<<MessageTypes.parseMessageType(args.get());
            } catch (final UnknownCommandException ex) {
                throw new IOException("undefined message type '"+args.getPrev()+"'", ex);
            }
        } while (args.hasMore());
        if (!add) {
            types = ~types;
        }
        final Object element = definedGUIElements.lookup(name);
        if (!(element instanceof GUIMessageLog)) {
            throw new IOException("element '"+name+"' is not of type 'log'");
        }
        ((GUIMessageLog)element).setTypes(types);
    }

    /**
     * Parses a "minimap" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMinimap(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final int w = ExpressionParser.parseInt(args.get());
        final int h = ExpressionParser.parseInt(args.get());
        final FacesProvider facesProvider = facesProviderFactory.getFacesProvider(4);
        if (facesProvider == null) {
            throw new IOException("cannot create faces with size 4");
        }
        final AbstractGUIElement element = new GUIMiniMap(tooltipManager, elementListener, name, mapUpdaterState, facesProvider, w, h);
        insertGuiElement(element);
    }

    /**
     * Parses a "map" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server to monitor
     * @param smoothFaces the smooth faces
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMap(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server, @NotNull final SmoothFaces smoothFaces) throws IOException, JXCSkinException {
        final String name = args.get();
        final FacesProvider facesProvider = facesProviderFactory.getFacesProvider(defaultTileSize);
        if (facesProvider == null) {
            throw new IOException("cannot create faces with size "+defaultTileSize);
        }
        insertGuiElement(new GUIMap(tooltipManager, elementListener, name, mapUpdaterState, facesProvider, server, smoothFaces));
    }

    /**
     * Parses a "meta_list" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param metaserverModel the metaserver model to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMetaList(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel) throws IOException, JXCSkinException {
        final String name = args.get();
        final int cellWidth = ExpressionParser.parseInt(args.get()); // XXX: should be derived from list's size
        final int cellHeight = ExpressionParser.parseInt(args.get()); // XXX: should be derived from list's size
        final Image image = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Font font = definedFonts.lookup(args.get());
        final GUIText text = args.get().equals("null") ? null : guiElementParser.lookupTextElement(args.getPrev());
        final AbstractLabel label = args.get().equals("null") ? null : guiElementParser.lookupLabelElement(args.getPrev());
        final CommandList connectCommandList = skin.getCommandList(args.get());
        final String format = args.get();
        final String tooltip = args.get();

        final AbstractGUIElement list = new GUIMetaElementList(tooltipManager, elementListener, name, cellWidth, cellHeight, metaserverModel, image, font, format, tooltip, text, label, connectCommandList);
        insertGuiElement(list);
    }

    /**
     * Parses a "picture" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parsePicture(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final BufferedImage image = imageParser.getImage(args.get());
        final float alpha = NumberParser.parseFloat(args.get());
        if (alpha < 0 || alpha > 1F) {
            throw new IOException("invalid alpha value: "+alpha);
        }
        insertGuiElement(new GUIPicture(tooltipManager, elementListener, name, image, alpha, image.getWidth(), image.getHeight()));
    }

    /**
     * Parses a "query_text" command.
     * @param args the command arguments
     * @param server the crossfire server connection for sending reply commands
     * @param commandCallback the command callback to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseQueryText(@NotNull final Args args, @NotNull final CrossfireServerConnection server, @NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Image activeImage = imageParser.getImage(args.get());
        final Image inactiveImage = imageParser.getImage(args.get());
        final Font font = definedFonts.lookup(args.get());
        final Color inactiveColor = ParseUtils.parseColor(args.get());
        final Color activeColor = ParseUtils.parseColor(args.get());
        final int margin = ExpressionParser.parseInt(args.get());
        final boolean enableHistory = NumberParser.parseBoolean(args.get());
        insertGuiElement(new GUIQueryText(server, commandCallback, tooltipManager, elementListener, name, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", enableHistory));
    }

    /**
     * Parses a "set_forced_active" command.
     * @param args the command arguments
     * @param gui the gui to modify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseSetForcedActive(@NotNull final Args args, @NotNull final Gui gui) throws IOException, JXCSkinException {
        final Object forcedActive = definedGUIElements.lookup(args.get());
        if (!(forcedActive instanceof ActivatableGUIElement)) {
            throw new IOException("argument to set_forced_active must be an activatable gui element");
        }
        gui.setForcedActive((ActivatableGUIElement)forcedActive);
    }

    /**
     * Parses a "set_auto_size" command.
     * @param args the command arguments
     * @param gui the gui to modify
     * @throws IOException if the command cannot be parsed
     */
    private static void parseSetAutoSize(@NotNull final Gui gui, @NotNull final Args args) throws IOException {
        final Expression x = ExpressionParser.parseExpression(args.get());
        final Expression y = ExpressionParser.parseExpression(args.get());
        final Expression w = ExpressionParser.parseExpression(args.get());
        final Expression h = ExpressionParser.parseExpression(args.get());
        gui.setAutoSize(new Extent(x, y, w, h));
    }

    /**
     * Parses a "set_default" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseSetDefault(@NotNull final Args args) throws IOException, JXCSkinException {
        definedGUIElements.lookup(args.get()).setDefault(true);
    }

    /**
     * Parses a "set_invisible" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseSetInvisible(@NotNull final Args args) throws IOException, JXCSkinException {
        definedGUIElements.lookup(args.get()).setVisible(false);
    }

    /**
     * Parses a "hide_input" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseHideInput(@NotNull final Args args) throws IOException, JXCSkinException {
        final GUIText element;
        try {
            element = (GUIText)definedGUIElements.lookup(args.get());
        } catch (final JXCSkinException ignored) {
            throw new JXCSkinException("can't use hide_input on a non text field "+args.getPrev());
        }
        element.setHideInput(true);
    }

    /**
     * Parses a "set_modal" command.
     * @param gui the gui to modify
     */
    private static void parseSetModal(@NotNull final Gui gui) {
        gui.setModal(true);
    }

    /**
     * Parses a "scrollbar" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseScrollbar(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final boolean proportionalSlider = NumberParser.parseBoolean(args.get());
        final Object element = definedGUIElements.lookup(args.get());
        final Color colorBackground = ParseUtils.parseColor(args.get());
        final Color colorForeground = ParseUtils.parseColor(args.get());
        if (!(element instanceof GUIScrollable2)) {
            throw new IOException("'"+element+"' is not a scrollable element");
        }
        insertGuiElement(new GUIScrollBar(tooltipManager, elementListener, name, proportionalSlider, (GUIScrollable2)element, colorBackground, colorForeground));
    }

    /**
     * Parses a "skin_name" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     */
    private void parseSkinName(@NotNull final Args args) throws IOException {
        final String newSkinName = args.get();
        final Resolution minResolution = parseResolution(args.get());
        final Resolution maxResolution = parseResolution(args.get());
        if (!newSkinName.matches("[-a-z_0-9]+")) {
            throw new IOException("invalid skin_name: "+newSkinName);
        }
        if (minResolution.getWidth() > maxResolution.getWidth() || minResolution.getHeight() > maxResolution.getHeight()) {
            throw new IOException("minimum resolution ("+minResolution+") must not exceed maximum resolution ("+maxResolution+")");
        }

        skin.setSkinName(newSkinName, minResolution, maxResolution);
    }

    /**
     * Parses a "text" command.
     * @param args the command arguments
     * @param commandCallback the command callback to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseText(@NotNull final Args args, @NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Image activeImage = imageParser.getImage(args.get());
        final Image inactiveImage = imageParser.getImage(args.get());
        final Font font = definedFonts.lookup(args.get());
        final Color activeColor = ParseUtils.parseColor(args.get());
        final Color inactiveColor = ParseUtils.parseColor(args.get());
        final int margin = ExpressionParser.parseInt(args.get());
        final CommandList commandList = skin.getCommandList(args.get());
        final boolean enableHistory = NumberParser.parseBoolean(args.get());
        insertGuiElement(new GUITextField(commandCallback, tooltipManager, elementListener, name, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", commandList, enableHistory));
    }

    /**
     * Parses a "textbutton" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseTextButton(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        if (textButtonFactory == null) {
            throw new IOException("missing 'def textbutton' command");
        }

        final String name = args.get();
        final boolean autoRepeat = NumberParser.parseBoolean(args.get());
        final CommandList commandList = skin.getCommandList(args.get());
        final String text = ParseUtils.parseText(args, lnr);
        assert textButtonFactory != null;
        insertGuiElement(textButtonFactory.newTextButton(tooltipManager, elementListener, name, text, autoRepeat, commandList));
    }

    /**
     * Parses a "textgauge" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseTextGauge(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        if (dialogFactory == null) {
            throw new IOException("missing 'def dialog' command");
        }

        final String name = args.get();
        final Image positiveImage = imageParser.getImage(args.get());
        final Image negativeImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Image emptyImage = imageParser.getImage(args.get());
        final GaugeUpdater gaugeUpdater = newGaugeUpdater(args.get());
        final Orientation orientation = ParseUtils.parseOrientation(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final Font font = definedFonts.lookup(args.get());
        final String tooltipPrefix = ParseUtils.parseText(args, lnr);
        assert dialogFactory != null;
        final float alpha = dialogFactory.getFrameAlpha();
        final GUITextGauge element = new GUITextGauge(tooltipManager, elementListener, name, positiveImage, negativeImage, emptyImage, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font, alpha);
        insertGuiElement(element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses a "tooltip" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseTooltip(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final Font font = definedFonts.lookup(args.get());
        final GUIHTMLLabel tooltipLabel = new GUIHTMLLabel(tooltipManager, elementListener, "tooltip", null, font, Color.BLACK, Color.WHITE, "");
        tooltipLabel.setAutoResize(true);
        skin.setTooltipLabel(tooltipLabel);
    }

    /**
     * Parses a "vertical" command.
     * @param args the command arguments
     * @param gui the gui
     * @param lnr the line number reader to read more lines  @throws IOException
     * if the command cannot be parsed
     * @param isDialog whether a "dialog" command has been executed
     * @throws JXCSkinException if the command cannot be parsed
     * @throws IOException if the command cannot be parsed
     */
    private void parseVertical(@NotNull final Args args, @NotNull final Container gui, @NotNull final LineNumberReader lnr, final boolean isDialog) throws IOException, JXCSkinException {
        final GroupLayout layout = (GroupLayout)gui.getLayout();
        final String begin = args.get();
        if (!begin.equals("begin")) {
            throw new IOException("syntax error: expecting 'begin'");
        }
        final Collection<GUIElement> unreferencedElements = new TreeSet<GUIElement>(ELEMENT_COMPARATOR);
        for (final GUIElement element : definedGUIElements) {
            unreferencedElements.add(element);
        }
        if (!isDialog) {
            final GroupLayout.Group content = parseBegin(args, layout, lnr, unreferencedElements);
            if (!unreferencedElements.isEmpty()) {
                throw new IOException("layout doesn't define elements "+unreferencedElements);
            }
            layout.setVerticalGroup(content);
            return;
        }
        final Component background = getUnreferencedElement("dialog_background", unreferencedElements);
        if (background == null) {
            throw new AssertionError("element 'dialog_background' is missing");
        }
        final GroupLayout.Group content = parseBegin(args, layout, lnr, unreferencedElements);
        final Component title = getUnreferencedElement("dialog_title", unreferencedElements);
        final Component close = getUnreferencedElement("dialog_close", unreferencedElements);
        final GroupLayout.Group group2 = layout.createSequentialGroup();
        group2.addGap(DIALOG_BORDER_WIDTH);
        if (title == null) {
            if (close != null) {
                final GroupLayout.Group group4 = layout.createParallelGroup();
                group4.addComponent(close);
                group4.addGap(0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
                final GroupLayout.Group group3 = layout.createParallelGroup();
                group3.addGroup(content);
                group3.addGroup(group4);
                group2.addGroup(group3);
            } else {
                group2.addGroup(content);
            }
        } else {
            if (close == null) {
                group2.addComponent(title);
                group2.addGroup(content);
            } else {
                final GroupLayout.Group group3 = layout.createParallelGroup();
                group3.addComponent(title);
                group3.addComponent(close);
                group2.addGroup(group3);
                group2.addGroup(content);
            }
        }
        group2.addGap(DIALOG_BORDER_WIDTH);
        if (!unreferencedElements.isEmpty()) {
            throw new IOException("layout doesn't define elements "+unreferencedElements);
        }

        final GroupLayout.Group group1 = layout.createParallelGroup();
        group1.addGroup(group2);
        group1.addComponent(background);

        layout.setVerticalGroup(group1);
    }

    /**
     * Parses a resolution specification.
     * @param text the resolution specification
     * @return the resolution
     * @throws IOException if the resolution specification cannot be parsed
     */
    @NotNull
    private static Resolution parseResolution(@NotNull final String text) throws IOException {
        final Resolution resolution = Resolution.parse(text);
        if (resolution == null) {
            throw new IOException("invalid resolution: "+resolution);
        }
        return resolution;
    }

    /**
     * Parses a "character_list" command.
     * @param args the command arguments
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param characterModel the character model to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCharacterList(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CharacterModel characterModel) throws IOException, JXCSkinException {
        final String name = args.get();
        final int cellWidth = ExpressionParser.parseInt(args.get());
        final int cellHeight = ExpressionParser.parseInt(args.get());
        final Font font = definedFonts.lookup(args.get());
        final AbstractGUIElement list = new GUICharacterList(tooltipManager, facesManager, elementListener, name, cellWidth, cellHeight, font, characterModel);
        insertGuiElement(list);
    }

    /**
     * Adds a new {@link GUIElement} to this skin.
     * @param guiElement the GUI element
     * @throws JXCSkinException if the name is not unique
     */
    private void insertGuiElement(@NotNull final AbstractGUIElement guiElement) throws JXCSkinException {
        definedGUIElements.insert(guiElement.getName(), guiElement);
        skin.insertGuiElement(guiElement);
    }

    /**
     * Parses and builds command arguments.
     * @param listName the command list name to add to
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
     * @throws IOException if a syntax error occurs
     * @throws JXCSkinException if an element cannot be found
     */
    private void addCommand(@NotNull final String listName, @NotNull final Args args, @Nullable final AbstractGUIElement element, @NotNull final String command, @NotNull final GuiStateManager guiStateManager, @NotNull final CommandExecutor commandExecutor, @NotNull final LineNumberReader lnr, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) throws IOException, JXCSkinException {
        final CommandList commandList = skin.getCommandList(listName);
        commandList.add(commandParser.parseCommandArgs(args, element, command, guiStateManager, commandExecutor, lnr, commandQueue, crossfireServerConnection, commandCallback, macros));
    }

    /**
     * Creates a new {@link GaugeUpdater} instance from a string
     * representation.
     * @param name the gauge updater value to parse
     * @return the gauge updater
     * @throws IOException if the gauge updater value does not exist
     */
    @NotNull
    private GaugeUpdater newGaugeUpdater(@NotNull final String name) throws IOException {
        return gaugeUpdaterParser.parseGaugeUpdater(name, experienceTable);
    }

    /**
     * Parses a "begin..end" block.
     * @param beginArgs the {@link Args} containing the "begin" line
     * @param layout the layout for the current gui
     * @param lnr the line number read to read from
     * @param unreferencedElements the unreferenced gui elements; will be
     * updated
     * @return the parsing result
     * @throws IOException if the block cannot be parsed
     * @throws JXCSkinException if the block cannot be parsed
     */
    @NotNull
    private GroupLayout.Group parseBegin(@NotNull final Args beginArgs, @NotNull final GroupLayout layout, @NotNull final LineNumberReader lnr, @NotNull final Collection<GUIElement> unreferencedElements) throws IOException, JXCSkinException {
        final String type = beginArgs.get();
        final GroupLayout.Group group;
        if (type.equals("seq")) {
            group = layout.createSequentialGroup();
        } else if (type.equals("par")) {
            group = layout.createParallelGroup();
        } else {
            throw new IOException("undefined type '"+type+"'");
        }

        while (true) {
            final String line = lnr.readLine();
            if (line == null) {
                throw new IOException("unterminated 'begin' block");
            }

            if (line.startsWith("#") || line.length() == 0) {
                continue;
            }

            final String[] argsTmp;
            try {
                argsTmp = StringUtils.splitFields(line);
            } catch (final UnterminatedTokenException ex) {
                throw new JXCSkinException(ex.getMessage());
            }
            final Args args = new Args(argsTmp);
            final String cmd = args.get();
            if (cmd.equals("end")) {
                break;
            }
            if (cmd.equals("begin")) {
                group.addGroup(parseBegin(args, layout, lnr, unreferencedElements));
            } else if (cmd.equals("border_gap")) {
                if (!(group instanceof GroupLayout.SequentialGroup)) {
                    throw new IOException("'border_gap' cannot be used outside 'seq' groups");
                }
                ((GroupLayout.SequentialGroup)group).addContainerGap();
            } else if (cmd.equals("gap")) {
                if (args.hasMore()) {
                    final int tmp = ExpressionParser.parseInt(args.get());
                    if (args.hasMore()) {
                        final int size = ExpressionParser.parseInt(args.get());
                        final int max = args.hasMore() ? ExpressionParser.parseInt(args.get()) : Short.MAX_VALUE;
                        group.addGap(tmp, size, max);
                    } else {
                        group.addGap(tmp);
                    }
                } else {
                    group.addGap(0, 1, Short.MAX_VALUE);
                }
            } else {
                final AbstractGUIElement element = definedGUIElements.lookup(cmd);
                if (!unreferencedElements.remove(element)) {
                    throw new IOException("layout defines element '"+element+"' more than once");
                }
                if (args.hasMore()) {
                    final int tmp = ExpressionParser.parseInt(args.get());
                    final int min;
                    final int pref;
                    final int max;
                    if (args.hasMore()) {
                        min = tmp;
                        pref = ExpressionParser.parseInt(args.get());
                        max = args.hasMore() ? ExpressionParser.parseInt(args.get()) : Short.MAX_VALUE;
                    } else {
                        min = tmp;
                        pref = tmp;
                        max = tmp;
                    }
                    group.addComponent(element, min, pref, max);
                } else {
                    group.addComponent(element);
                }
            }
            if (args.hasMore()) {
                throw new IOException("excess arguments");
            }
        }
        return group;
    }

}

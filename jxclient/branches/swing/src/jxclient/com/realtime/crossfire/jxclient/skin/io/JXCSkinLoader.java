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

import com.realtime.crossfire.jxclient.account.CharacterModel;
import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesProvider;
import com.realtime.crossfire.jxclient.faces.FacesProviderFactory;
import com.realtime.crossfire.jxclient.gui.GUIEmpty;
import com.realtime.crossfire.jxclient.gui.GUIFill;
import com.realtime.crossfire.jxclient.gui.GUIPanel;
import com.realtime.crossfire.jxclient.gui.GUIPicture;
import com.realtime.crossfire.jxclient.gui.button.ButtonImages;
import com.realtime.crossfire.jxclient.gui.button.GUIButton;
import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.commands.CommandCheckBoxOption;
import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import com.realtime.crossfire.jxclient.gui.commands.CommandListType;
import com.realtime.crossfire.jxclient.gui.gauge.GUIDupGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUIDupTextGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUIGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUITextGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.Orientation;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIScrollBar;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiFactory;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiState;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemFloor;
import com.realtime.crossfire.jxclient.gui.item.GUIItemFloorFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventoryFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItemFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemShortcut;
import com.realtime.crossfire.jxclient.gui.item.GUIItemSpellList;
import com.realtime.crossfire.jxclient.gui.item.ItemPainter;
import com.realtime.crossfire.jxclient.gui.keybindings.InvalidKeyBindingException;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
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
import com.realtime.crossfire.jxclient.gui.label.Type;
import com.realtime.crossfire.jxclient.gui.list.GUICharacterList;
import com.realtime.crossfire.jxclient.gui.list.GUIItemList;
import com.realtime.crossfire.jxclient.gui.list.GUIMetaElementList;
import com.realtime.crossfire.jxclient.gui.log.Fonts;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.log.GUIMessageLog;
import com.realtime.crossfire.jxclient.gui.log.MessageBufferUpdater;
import com.realtime.crossfire.jxclient.gui.map.GUIMap;
import com.realtime.crossfire.jxclient.gui.map.GUIMiniMap;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable2;
import com.realtime.crossfire.jxclient.gui.textinput.GUICommandText;
import com.realtime.crossfire.jxclient.gui.textinput.GUIQueryText;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.gui.textinput.GUITextField;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.MessageTypes;
import com.realtime.crossfire.jxclient.server.socket.UnknownCommandException;
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
import com.realtime.crossfire.jxclient.skin.skin.Expression;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
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
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for loading {@link JXCSkin} instances from {@link JXCSkinSource
 * JXCSkinSources}.
 * @author Andreas Kirschbaum
 */
public class JXCSkinLoader {

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
     * The {@link SpellsManager} instance to use.
     */
    @NotNull
    private final SpellsManager spellsManager;

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
     * The {@link CfMapUpdater} instance to use.
     */
    @NotNull
    private final CfMapUpdater mapUpdater;

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
     * The defined {@link GUIElement}s.
     */
    @NotNull
    private final JXCSkinCache<GUIElement> definedGUIElements = new JXCSkinCache<GUIElement>("gui element");

    /**
     * The {@link JXCSkin} being loaded.
     */
    @NotNull
    private DefaultJXCSkin skin;

    /**
     * The {@link GuiBuilder} instance for adding {@link GUIElement GUIElements}
     * to {@link Gui Guis}.
     */
    @NotNull
    private final GuiBuilder guiBuilder = new GuiBuilder();

    /**
     * Creates a new instance.
     * @param itemSet the item set instance to use
     * @param inventoryView the inventory item view to use
     * @param floorView the floor view to use
     * @param spellsManager the spells manager instance to use
     * @param facesManager the faces manager instance to use
     * @param stats the stats instance to use
     * @param mapUpdater the map updater instance to use
     * @param defaultKeyBindings the default key bindings
     * @param optionManager the option manager to use
     * @param experienceTable the experience table to use
     * @param skillSet the skill set to use
     * @param defaultTileSize the default tile size for the map view
     */
    public JXCSkinLoader(@NotNull final ItemSet itemSet, @NotNull final ItemView inventoryView, @NotNull final FloorView floorView, @NotNull final SpellsManager spellsManager, @NotNull final FacesManager facesManager, @NotNull final Stats stats, @NotNull final CfMapUpdater mapUpdater, @NotNull final KeyBindings defaultKeyBindings, @NotNull final OptionManager optionManager, @NotNull final ExperienceTable experienceTable, @NotNull final SkillSet skillSet, final int defaultTileSize) {
        this.itemSet = itemSet;
        this.inventoryView = inventoryView;
        this.floorView = floorView;
        this.spellsManager = spellsManager;
        this.facesManager = facesManager;
        this.defaultTileSize = defaultTileSize;
        facesProviderFactory = new FacesProviderFactory(facesManager);
        this.stats = stats;
        this.mapUpdater = mapUpdater;
        this.defaultKeyBindings = defaultKeyBindings;
        this.optionManager = optionManager;
        this.experienceTable = experienceTable;
        this.skillSet = skillSet;
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
     * @param commands the commands instance to use
     * @param currentSpellManager the current spell manager to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @param guiFactory the gui factory for creating gui elements
     * @return the loaded skin
     * @throws JXCSkinException if the skin cannot be loaded
     */
    @NotNull
    public JXCSkin load(@NotNull final JXCSkinSource skinSource, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel, @NotNull final CharacterModel characterModel, @NotNull final CommandQueue commandQueue, @NotNull final Shortcuts shortcuts, @NotNull final Commands commands, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros, @NotNull final GuiFactory guiFactory) throws JXCSkinException {
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
                load(skinSource, "global", crossfireServerConnection, guiStateManager, tooltipManager, windowRenderer, elementListener, metaserverModel, characterModel, commandQueue, null, shortcuts, commands, currentSpellManager, commandCallback, macros, nextGroupFace, prevGroupFace);
                for (; ;) {
                    final String name = skin.getDialogToLoad();
                    if (name == null) {
                        break;
                    }
                    final Gui gui = skin.getDialog(name);
                    load(skinSource, name, crossfireServerConnection, guiStateManager, tooltipManager, windowRenderer, elementListener, metaserverModel, characterModel, commandQueue, gui, shortcuts, commands, currentSpellManager, commandCallback, macros, nextGroupFace, prevGroupFace);
                    gui.setStateChanged(false);
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
     * @param commands the commands instance for executing commands
     * @param currentSpellManager the current spell manager to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(@NotNull final JXCSkinSource skinSource, @NotNull final String dialogName, @NotNull final CrossfireServerConnection server, @NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel, @NotNull final CharacterModel characterModel, @NotNull final CommandQueue commandQueue, @Nullable final Gui gui, @NotNull final Shortcuts shortcuts, @NotNull final Commands commands, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) throws JXCSkinException {
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
                load(skinSource, dialogName, resourceName, inputStream, server, guiStateManager, tooltipManager, windowRenderer, elementListener, metaserverModel, characterModel, commandQueue, gui, shortcuts, commands, currentSpellManager, commandCallback, macros, nextGroupFace, prevGroupFace);
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
     * @param commands the commands instance for executing commands
     * @param currentSpellManager the current spell manager to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(@NotNull final JXCSkinSource skinSource, @NotNull final String dialogName, @NotNull final String resourceName, @NotNull final InputStream inputStream, @NotNull final CrossfireServerConnection server, @NotNull final GuiStateManager guiStateManager, @NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel, @NotNull final CharacterModel characterModel, @NotNull final CommandQueue commandQueue, @Nullable final Gui gui, @NotNull final Shortcuts shortcuts, @NotNull final Commands commands, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) throws JXCSkinException {
        guiBuilder.clear();

        try {
            final InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            try {
                final LineNumberReader lnr = new LineNumberReader(isr);
                try {
                    final List<State> stack = new ArrayList<State>();
                    @Nullable ConstraintParser constraintParser = new GridBagConstraintParser(false); // XXX: is this correct?
                    @NotNull Container parent = gui;
                    for (; ;) {
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
                        if (gui != null && cmd.equals("add")) {
                            final String element = args.get();
                            if (element.equals("*")) {
                                if (args.hasMore()) {
                                    throw new IOException("parent component not allowed for '*'");
                                }

                                if (!guiBuilder.addWildcard()) {
                                    throw new IOException("duplicate '*'");
                                }
                            } else {
                                final GUIElement guiElement = definedGUIElements.lookup(element);
                                guiBuilder.addElement(guiElement);

                                if (args.hasMore()) {
                                    guiBuilder.setParent(guiElement, (Container/*XXX*/) definedGUIElements.lookup(args.get()));
                                }
                            }
                        } else if (gui != null && cmd.equals("begin")) {
                            if (constraintParser == null) {
                                throw new IOException("'begin' cannot be used outside a 'dialog'");
                            }
                            final State state = parseBegin(args, parent, constraintParser, tooltipManager, elementListener);
                            stack.add(new State(constraintParser, parent, state.getElement()));
                            constraintParser = state.getConstraintParser();
                            parent = state.getParent();
                        } else if (gui != null && cmd.equals("button")) {
                            parseButton(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("checkbox")) {
                            parseCheckbox(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (cmd.equals("commandlist")) {
                            parseCommandList(args, guiStateManager, lnr, commands, commandQueue, server, commandCallback, macros);
                        } else if (cmd.equals("commandlist_add")) {
                            parseCommandListAdd(args, guiStateManager, lnr, commands, commandQueue, server, commandCallback, macros);
                        } else if (gui != null && cmd.equals("command_text")) {
                            parseCommandText(args, parent, constraintParser, commandCallback, tooltipManager, elementListener, commands);
                        } else if (cmd.equals("def")) {
                            parseDef(args, lnr);
                        } else if (gui != null && cmd.equals("dialog")) {
                            if (!stack.isEmpty()) {
                                throw new IOException("cannot use multiple 'dialog' commands");
                            }
                            parent = parseDialog(args, tooltipManager, windowRenderer, elementListener, lnr, gui, dialogName);
                            constraintParser = new GridBagConstraintParser(false);
                        } else if (gui != null && cmd.equals("dialog_hide")) {
                            parseDialogHide(args, gui);
                        } else if (gui != null && cmd.equals("dupgauge")) {
                            parseDupGauge(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("duptextgauge")) {
                            parseDupTextGauge(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("empty")) {
                            parseEmpty(args, parent, constraintParser, tooltipManager, elementListener);
                        } else if (cmd.equals("end")) {
                            if (stack.isEmpty()) {
                                throw new IOException("'end' command without 'begin' command");
                            }

                            final State state = stack.remove(stack.size()-1);
                            parseEnd(args, state.getElement());
                            constraintParser = state.getConstraintParser();
                            parent = state.getParent();
                        } else if (cmd.equals("event")) {
                            parseEvent(args, guiStateManager, server);
                        } else if (gui != null && cmd.equals("fill")) {
                            parseFill(args, parent, constraintParser, tooltipManager, elementListener);
                        } else if (cmd.equals("font")) {
                            parseFont(args);
                        } else if (gui != null && cmd.equals("gauge")) {
                            parseGauge(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("ignore")) {
                            parseIgnore(args);
                        } else if (gui != null && cmd.equals("inventory_list")) {
                            parseList(args, parent, constraintParser, true, tooltipManager, elementListener, commandQueue, server, nextGroupFace, prevGroupFace);
                        } else if (gui != null && cmd.equals("floor_list")) {
                            parseList(args, parent, constraintParser, false, tooltipManager, elementListener, commandQueue, server, nextGroupFace, prevGroupFace);
                        } else if (gui != null && cmd.equals("item")) {
                            parseItem(args, parent, constraintParser, tooltipManager, elementListener, commandQueue, server, shortcuts, currentSpellManager, nextGroupFace, prevGroupFace);
                        } else if (cmd.equals("key")) {
                            parseKey(args, gui, line);
                        } else if (gui != null && cmd.equals("label_html")) {
                            parseLabelHtml(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("label_multi")) {
                            parseLabelMulti(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("label_query")) {
                            parseLabelQuery(args, parent, constraintParser, tooltipManager, elementListener, server);
                        } else if (gui != null && cmd.equals("label_failure")) {
                            parseLabelFailure(args, parent, constraintParser, tooltipManager, elementListener, server);
                        } else if (gui != null && cmd.equals("label_message")) {
                            parseLabelMessage(args, parent, constraintParser, tooltipManager, elementListener, server, windowRenderer);
                        } else if (gui != null && cmd.equals("label_text")) {
                            parseLabelText(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("label_stat")) {
                            parseLabelStat(args, parent, constraintParser, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("label_stat2")) {
                            parseLabelStat2(args, parent, constraintParser, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("label_spell")) {
                            parseLabelSpell(args, parent, constraintParser, tooltipManager, elementListener, currentSpellManager);
                        } else if (gui != null && cmd.equals("log_label")) {
                            parseLogLabel(args, parent, constraintParser, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("log_message")) {
                            parseLogMessage(args, parent, constraintParser, tooltipManager, elementListener, server);
                        } else if (gui != null && cmd.equals("log_color")) {
                            parseLogColor(args);
                        } else if (gui != null && cmd.equals("log_filter")) {
                            parseLogFilter(args);
                        } else if (gui != null && cmd.equals("minimap")) {
                            parseMinimap(args, parent, constraintParser, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("map")) {
                            parseMap(args, parent, constraintParser, tooltipManager, elementListener, server);
                        } else if (gui != null && cmd.equals("meta_list")) {
                            parseMetaList(args, parent, constraintParser, tooltipManager, elementListener, metaserverModel);
                        } else if (gui != null && cmd.equals("picture")) {
                            parsePicture(args, parent, constraintParser, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("query_text")) {
                            parseQueryText(args, parent, constraintParser, server, commandCallback, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("set_forced_active")) {
                            parseSetForcedActive(args, gui);
                        } else if (gui != null && cmd.equals("set_auto_size")) {
                            parseSetAutoSize(gui);
                        } else if (gui != null && cmd.equals("set_default")) {
                            parseSetDefault(args);
                        } else if (gui != null && cmd.equals("set_invisible")) {
                            parseSetInvisible(args);
                        } else if (gui != null && cmd.equals("set_modal")) {
                            parseSetModal(gui);
                        } else if (cmd.equals("set_num_look_objects")) {
                            parseSetNumLookObjects(args);
                        } else if (gui != null && cmd.equals("scrollbar")) {
                            parseScrollbar(args, parent, constraintParser, tooltipManager, elementListener);
                        } else if (gui == null && cmd.equals("skin_name")) {
                            parseSkinName(args);
                        } else if (gui != null && cmd.equals("text")) {
                            parseText(args, parent, constraintParser, commandCallback, tooltipManager, elementListener);
                        } else if (gui != null && cmd.equals("textbutton")) {
                            parseTextButton(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (gui != null && cmd.equals("textgauge")) {
                            parseTextGauge(args, parent, constraintParser, tooltipManager, elementListener, lnr);
                        } else if (cmd.equals("tooltip")) {
                            parseTooltip(args, tooltipManager, elementListener);
                        } else if (cmd.equals("character_list")) {
                            parseCharacterList(args, parent, constraintParser, tooltipManager, elementListener, characterModel);
                        } else if (cmd.equals("hide_input")) {
                            parseHideInput(args);
                        } else {
                            throw new IOException("unknown keyword '"+cmd+"'");
                        }

                        if (args.hasMore()) {
                            throw new IOException("excess arguments");
                        }
                    }

                    if (!stack.isEmpty()) {
                        final State state = stack.get(stack.size()-1);
                        throw new IOException("begin '"+state.getElement().getName()+"' not ended");
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

        final Iterator<GUIElement> it = definedGUIElements.iterator();
        if (gui == null) {
            assert !it.hasNext();
        } else {
            guiBuilder.finish(it, gui);
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
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     * @return the constraint parser to use for parsing child elements
     */
    @NotNull
    private State parseBegin(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        @Nullable final LayoutManager layout;
        final ConstraintParser constraintParser2;
        final String type = args.get();
        if (type.equals("HORIZONTAL")) {
            layout = new GridBagLayout();
            constraintParser2 = new GridBagConstraintParser(true);
        } else if (type.equals("VERTICAL")) {
            layout = new GridBagLayout();
            constraintParser2 = new GridBagConstraintParser(false);
        } else if (type.equals("LAYERED")) {
            layout = null;
            constraintParser2 = new LayeredConstraintParser();
        } else {
            throw new IOException("invalid orientation '"+type+"'");
        }
        final GUIPanel panel = new GUIPanel(tooltipManager, elementListener, name);
        panel.setLayout(layout);
        insertGuiElement(constraints, parent, panel);
        return new State(constraintParser2, panel, panel);
    }

    /**
     * Parses a "button" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseButton(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
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
        insertGuiElement(constraints, parent, new GUIButton(tooltipManager, elementListener, name, upImage, downImage, label, font, color, textX, textY, autoRepeat, commandList));
    }

    /**
     * Parses a "checkbox" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCheckbox(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        if (checkBoxFactory == null) {
            throw new IOException("missing 'def checkbox' command");
        }

        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final CheckBoxOption option = ParseUtils.parseCheckBoxOption(args.get(), optionManager);
        final String text = ParseUtils.parseText(args, lnr);
        assert checkBoxFactory != null;
        insertGuiElement(constraints, parent, checkBoxFactory.newCheckBox(tooltipManager, elementListener, name, option, text));
    }

    /**
     * Parses a "commandlist" command.
     * @param args the command arguments
     * @param guiStateManager the gui state manager instance
     * @param lnr the line number reader for reading more lines
     * @param commands the commands to add to
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandList(@NotNull final Args args, @NotNull final GuiStateManager guiStateManager, @NotNull final LineNumberReader lnr, @NotNull final Commands commands, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection server, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) throws IOException, JXCSkinException {
        final String commandListName = args.get();
        final CommandListType commandListType = NumberParser.parseEnum(CommandListType.class, args.get(), "type");
        skin.addCommandList(commandListName, commandListType);
        if (args.hasMore()) {
            final GUIElement element = args.get().equals("null") ? null : definedGUIElements.lookup(args.getPrev());
            addCommand(commandListName, args, element, args.get(), guiStateManager, commands, lnr, commandQueue, server, commandCallback, macros);
        }
    }

    /**
     * Parses a "commandlist_add" command.
     * @param args the command arguments
     * @param guiStateManager the gui state manager instance
     * @param lnr the line number reader for reading more lines
     * @param commands the commands to add to
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandListAdd(@NotNull final Args args, @NotNull final GuiStateManager guiStateManager, @NotNull final LineNumberReader lnr, @NotNull final Commands commands, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection server, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) throws IOException, JXCSkinException {
        final String name = args.get();
        final GUIElement element = args.get().equals("null") ? null : definedGUIElements.lookup(args.getPrev());
        addCommand(name, args, element, args.get(), guiStateManager, commands, lnr, commandQueue, server, commandCallback, macros);
    }

    /**
     * Parses a "command_text" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param commandCallback the command callback to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commands the commands to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandText(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final Commands commands) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage activeImage = imageParser.getImage(args.get());
        final BufferedImage inactiveImage = imageParser.getImage(args.get());
        final Font font = definedFonts.lookup(args.get());
        final Color inactiveColor = ParseUtils.parseColor(args.get());
        final Color activeColor = ParseUtils.parseColor(args.get());
        final int margin = ExpressionParser.parseInt(args.get());
        final boolean enableHistory = NumberParser.parseBoolean(args.get());
        insertGuiElement(constraints, parent, new GUICommandText(commandCallback, tooltipManager, elementListener, name, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", commands, enableHistory));
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
            final BufferedImage frameNW = imageParser.getImage(frame+"_nw");
            final BufferedImage frameN = imageParser.getImage(frame+"_n");
            final BufferedImage frameNE = imageParser.getImage(frame+"_ne");
            final BufferedImage frameW = imageParser.getImage(frame+"_w");
            final BufferedImage frameC = imageParser.getImage(frame+"_c");
            final BufferedImage frameE = imageParser.getImage(frame+"_e");
            final BufferedImage frameSW = imageParser.getImage(frame+"_sw");
            final BufferedImage frameS = imageParser.getImage(frame+"_s");
            final BufferedImage frameSE = imageParser.getImage(frame+"_se");
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
            final BufferedImage cursedImage = imageParser.getImage(cursedColor, args.getPrev());
            final Color damnedColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage damnedImage = imageParser.getImage(damnedColor, args.getPrev());
            final Color magicColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage magicImage = imageParser.getImage(magicColor, args.getPrev());
            final Color blessedColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage blessedImage = imageParser.getImage(blessedColor, args.getPrev());
            final Color appliedColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage appliedImage = imageParser.getImage(appliedColor, args.getPrev());
            final Color unidentifiedColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage unidentifiedImage = imageParser.getImage(appliedColor, args.getPrev());
            final Color selectorColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage selectorImage = imageParser.getImage(selectorColor, args.getPrev());
            final Color lockedColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage lockedImage = imageParser.getImage(lockedColor, args.getPrev());
            final Color unpaidColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage unpaidImage = imageParser.getImage(unpaidColor, args.getPrev());
            final Font font = definedFonts.lookup(args.get());
            final Color nrofColor = ParseUtils.parseColor(args.get());
            defaultItemPainter = new ItemPainter(cursedImage, damnedImage, magicImage, blessedImage, appliedImage, unidentifiedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, unidentifiedColor, selectorColor, lockedColor, unpaidColor, font, nrofColor);
        } else if (type.equals("textbutton")) {
            final String up = args.get();
            final String down = args.get();
            final Font font = definedFonts.lookup(args.get());
            final Color color = ParseUtils.parseColor(args.get());
            final ButtonImages upImages = new ButtonImages(imageParser.getImage(up+"_w"), imageParser.getImage(up+"_c"), imageParser.getImage(up+"_e"));
            final ButtonImages downImages = new ButtonImages(imageParser.getImage(down+"_w"), imageParser.getImage(down+"_c"), imageParser.getImage(down+"_e"));
            textButtonFactory = new TextButtonFactory(upImages, downImages, font, color);
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
     * @return the center component of the dialog
     */
    @NotNull
    private Container parseDialog(@NotNull final Args args, @NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr, @NotNull final Gui gui, @NotNull final String dialogName) throws IOException {
        if (dialogFactory == null) {
            throw new IOException("missing 'def dialog' command");
        }

        final String name = args.get();
        final Extent extent = parseExtent(args);
        final boolean saveDialog = NumberParser.parseBoolean(args.get());
        final String title = ParseUtils.parseText(args, lnr);
        final Expression w = extent.getWExpression();
        final Expression h = extent.getHExpression();
        assert dialogFactory != null;
        final Container result = dialogFactory.newDialog(tooltipManager, windowRenderer, elementListener, name, w, h, title, gui);
        result.setLayout(new GridBagLayout());
        if (saveDialog) {
            gui.setName(dialogName);
        }
        gui.setExtent(extent);
        return result;
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
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDupGauge(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints  = constraintParser.parseConstraints(args);
        final BufferedImage positiveDivImage = imageParser.getImage(args.get());
        final BufferedImage positiveModImage = imageParser.getImage(args.get());
        final BufferedImage emptyImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final GaugeUpdater gaugeUpdater = newGaugeUpdater(args.get());
        final Orientation orientationDiv = ParseUtils.parseOrientation(args.get());
        final Orientation orientationMod = ParseUtils.parseOrientation(args.get());
        final String tooltipPrefix = ParseUtils.parseText(args, lnr);
        final GUIDupGauge element = new GUIDupGauge(tooltipManager, elementListener, name, positiveDivImage, positiveModImage, emptyImage, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
        insertGuiElement(constraints, parent, element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses a "duptextgauge" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDupTextGauge(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage positiveDivImage = imageParser.getImage(args.get());
        final BufferedImage positiveModImage = imageParser.getImage(args.get());
        final BufferedImage emptyImage = imageParser.getImage(args.get());
        final GaugeUpdater gaugeUpdater = newGaugeUpdater(args.get());
        final Orientation orientationDiv = ParseUtils.parseOrientation(args.get());
        final Orientation orientationMod = ParseUtils.parseOrientation(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final Font font = definedFonts.lookup(args.get());
        final String tooltipPrefix = ParseUtils.parseText(args, lnr);
        final GUIDupTextGauge element = new GUIDupTextGauge(tooltipManager, elementListener, name, positiveDivImage, positiveModImage, emptyImage, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
        insertGuiElement(constraints, parent, element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses an "empty" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseEmpty(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(new Args(new String[] { "1", "BOTH", }));
        final GUIElement element = new GUIEmpty(tooltipManager, elementListener, name);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses an "end" command.
     * @param args the command arguments
     * @param element the element of the corresponding "begin" command
     * @throws IOException if the command cannot be parsed
     */
    private static void parseEnd(@NotNull final Args args, @NotNull final GUIElement element) throws IOException {
        final String elementName = args.get();

        if (!element.getName().equals(elementName)) {
            throw new IOException("end '"+elementName+"' does not match begin '"+element.getName()+"'");
        }
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
            skin.addSkinEvent(new MapScrollSkinEvent(commandList, mapUpdater));
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
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseFill(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Color color = ParseUtils.parseColor(args.get());
        final float alpha = NumberParser.parseFloat(args.get());
        if (alpha < 0 || alpha > 1F) {
            throw new IOException("invalid alpha value: "+alpha);
        }
        insertGuiElement(constraints, parent, new GUIFill(tooltipManager, elementListener, name, color, alpha));
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
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseGauge(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage positiveImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final BufferedImage negativeImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final BufferedImage emptyImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final GaugeUpdater gaugeUpdater = newGaugeUpdater(args.get());
        final Orientation orientation = ParseUtils.parseOrientation(args.get());
        final String tooltipPrefix = ParseUtils.parseText(args, lnr);
        final GUIGauge element = new GUIGauge(tooltipManager, elementListener, name, positiveImage, negativeImage, emptyImage, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
        insertGuiElement(constraints, parent, element);
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
     * Parses an "inventory_list" or a "floor_list" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param inventoryList <code>true</code> for "inventory_list" command,
     * <code>false</code> for "floor_list" command
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseList(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, final boolean inventoryList, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection server, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) throws IOException, JXCSkinException {
        if (defaultItemPainter == null) {
            throw new IOException("cannot use '"+(inventoryList ? "inventory_list" : "floor_list")+"' without 'def item' command");
        }

        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final int cellWidth = ExpressionParser.parseInt(args.get());
        final int cellHeight = ExpressionParser.parseInt(args.get());
        final AbstractLabel selectedItem = args.get().equals("null") ? null : guiElementParser.lookupLabelElement(args.getPrev());

        assert defaultItemPainter != null;
        final ItemPainter itemPainter = defaultItemPainter.newItemPainter();
        final GUIItemItemFactory itemFactory;
        if (inventoryList) {
            itemFactory = new GUIItemInventoryFactory(tooltipManager, elementListener, commandQueue, name, itemPainter, server, facesManager, floorView, inventoryView);
        } else {
            itemFactory = new GUIItemFloorFactory(tooltipManager, elementListener, commandQueue, name, itemPainter, server, facesManager, floorView, itemSet, nextGroupFace, prevGroupFace);
        }
        final GUIItemList element = new GUIItemList(tooltipManager, elementListener, commandQueue, name, cellWidth, cellHeight, server, inventoryList ? inventoryView : floorView, selectedItem, itemFactory);
        insertGuiElement(constraints, parent, element);

        if (!inventoryList) {
            skin.addFloorList(element);
        }
    }

    /**
     * Parses an "item" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
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
    private void parseItem(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection server, @NotNull final Shortcuts shortcuts, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) throws IOException, JXCSkinException {
        final String type = args.get();
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final int index = ExpressionParser.parseInt(args.get());
        final GUIElement element;
        if (type.equals("floor")) {
            if (defaultItemPainter == null) {
                throw new IOException("cannot use 'item floor' without 'def item' command");
            }

            final ItemPainter itemPainter = defaultItemPainter.newItemPainter();
            element = new GUIItemFloor(tooltipManager, elementListener, commandQueue, name, itemPainter, index, server, floorView, itemSet, facesManager, nextGroupFace, prevGroupFace);
        } else if (type.equals("inventory")) {
            if (defaultItemPainter == null) {
                throw new IOException("cannot use 'item floor' without 'def item' command");
            }

            final ItemPainter itemPainter = defaultItemPainter.newItemPainter();
            element = new GUIItemInventory(tooltipManager, elementListener, commandQueue, name, itemPainter, index, server, facesManager, floorView, inventoryView);
        } else if (type.equals("shortcut")) {
            final Color castColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage castImage = imageParser.getImage(castColor, args.getPrev());
            final Color invokeColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage invokeImage = imageParser.getImage(invokeColor, args.getPrev());
            final Font font = definedFonts.lookup(args.get());
            element = new GUIItemShortcut(tooltipManager, elementListener, name, castColor, castImage, invokeColor, invokeImage, index, facesManager, shortcuts, font, currentSpellManager);
        } else if (type.equals("spelllist")) {
            final Color selectorColor = ParseUtils.parseColorNull(args.get());
            final BufferedImage selectorImage = imageParser.getImage(selectorColor, args.getPrev());
            element = new GUIItemSpellList(tooltipManager, elementListener, commandQueue, name, selectorColor, selectorImage, index, facesManager, spellsManager, currentSpellManager);
        } else {
            throw new IOException("undefined item type: "+type);
        }
        insertGuiElement(constraints, parent, element);
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
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelHtml(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final String text = ParseUtils.parseText(args, lnr);
        insertGuiElement(constraints, parent, new GUIHTMLLabel(tooltipManager, elementListener, name, null, font, color, null, text));
    }

    /**
     * Parses a "label_multi" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelMulti(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final String text = ParseUtils.parseText(args, lnr);
        insertGuiElement(constraints, parent, new GUIMultiLineLabel(tooltipManager, elementListener, name, null, font, color, null, Alignment.LEFT, text));
    }

    /**
     * Parses a "label_query" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server instance to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelQuery(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final GUIElement element = new GUILabelQuery(tooltipManager, elementListener, name, server, font, color, null);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses a "label_message" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server instance to monitor
     * @param windowRenderer the window renderer to create the element for
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelMessage(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server, @NotNull final JXCWindowRenderer windowRenderer) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final GUIElement element = new GUILabelMessage(tooltipManager, elementListener, name, server, windowRenderer, font, color, null);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses a "label_failure" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server instance to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelFailure(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final GUIElement element = new GUILabelFailure(tooltipManager, elementListener, name, server, font, color, null);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses a "label_text" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelText(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final String text = ParseUtils.parseText(args, lnr);
        insertGuiElement(constraints, parent, new GUIOneLineLabel(tooltipManager, elementListener, name, null, font, color, null, Alignment.LEFT, text));
    }

    /**
     * Parses a "label_stat" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelStat(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Font font = definedFonts.lookup(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final int stat = ParseUtils.parseStat(args.get());
        final Alignment alignment = NumberParser.parseEnum(Alignment.class, args.get(), "text alignment");
        final GUIElement element = new GUILabelStats(tooltipManager, elementListener, name, font, color, null, stat, alignment, stats);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses a "label_stat2" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelStat2(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
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
        final GUIElement element = new GUILabelStats2(tooltipManager, elementListener, name, font, colorNormal, colorUpgradable, colorDepleted, colorBoosted, colorBoostedUpgradable, null, statCurrent, statBase, statRace, statApplied, alignment, stats);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses a "label_spell" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param currentSpellManager the current spell manager to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelSpell(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CurrentSpellManager currentSpellManager) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final Font font = definedFonts.lookup(args.get());
        final Type type = NumberParser.parseEnum(Type.class, args.get(), "label type");
        final GUIElement element = new GUISpellLabel(tooltipManager, elementListener, name, null, facesManager, font, type, currentSpellManager);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses a "log_label" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogLabel(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage backgroundImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Font fontPrint = definedFonts.lookup(args.get());
        final Font fontFixed = definedFonts.lookup(args.get());
        final Font fontFixedBold = definedFonts.lookup(args.get());
        final Font fontArcane = definedFonts.lookup(args.get());
        final Color defaultColor = ParseUtils.parseColor(args.get());
        final Fonts fonts = new Fonts(fontPrint, fontFixed, fontFixedBold, fontArcane);
        final GUIElement element = new GUILabelLog(tooltipManager, elementListener, name, backgroundImage, fonts, defaultColor);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses a "log_message" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogMessage(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage backgroundImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Font fontPrint = definedFonts.lookup(args.get());
        final Font fontFixed = definedFonts.lookup(args.get());
        final Font fontFixedBold = definedFonts.lookup(args.get());
        final Font fontArcane = definedFonts.lookup(args.get());
        final Color defaultColor = ParseUtils.parseColor(args.get());
        final Fonts fonts = new Fonts(fontPrint, fontFixed, fontFixedBold, fontArcane);
        final GUIElement element = new GUIMessageLog(tooltipManager, elementListener, name, server, backgroundImage, fonts, defaultColor);
        insertGuiElement(constraints, parent, element);
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
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMinimap(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final FacesProvider facesProvider = facesProviderFactory.getFacesProvider(4);
        if (facesProvider == null) {
            throw new IOException("cannot create faces with size 4");
        }
        final GUIElement element = new GUIMiniMap(tooltipManager, elementListener, name, mapUpdater, facesProvider);
        insertGuiElement(constraints, parent, element);
    }

    /**
     * Parses a "map" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param server the server to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMap(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CrossfireServerConnection server) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);

        final FacesProvider facesProvider = facesProviderFactory.getFacesProvider(defaultTileSize);
        if (facesProvider == null) {
            throw new IOException("cannot create faces with size "+defaultTileSize);
        }
        final GUIMap element = new GUIMap(tooltipManager, elementListener, name, mapUpdater, facesProvider, server);
        insertGuiElement(constraints, parent, element);

        skin.addMap(element);
    }

    /**
     * Parses a "meta_list" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param metaserverModel the metaserver model to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMetaList(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final int cellWidth = ExpressionParser.parseInt(args.get()); // XXX: should be derived from list's size
        final int cellHeight = ExpressionParser.parseInt(args.get()); // XXX: should be derived from list's size
        final BufferedImage image = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final Font font = definedFonts.lookup(args.get());
        final GUIText text = args.get().equals("null") ? null : guiElementParser.lookupTextElement(args.getPrev());
        final AbstractLabel label = args.get().equals("null") ? null : guiElementParser.lookupLabelElement(args.getPrev());
        final CommandList connectCommandList = skin.getCommandList(args.get());
        final String format = args.get();
        final String tooltip = args.get();

        final GUIElement list = new GUIMetaElementList(tooltipManager, elementListener, name, cellWidth, cellHeight, metaserverModel, image, font, format, tooltip, text, label, connectCommandList);
        insertGuiElement(constraints, parent, list);
    }

    /**
     * Parses a "picture" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parsePicture(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage image = imageParser.getImage(args.get());
        final float alpha = NumberParser.parseFloat(args.get());
        if (alpha < 0 || alpha > 1F) {
            throw new IOException("invalid alpha value: "+alpha);
        }
        insertGuiElement(constraints, parent, new GUIPicture(tooltipManager, elementListener, name, image, alpha, image.getWidth(), image.getHeight()));
    }

    /**
     * Parses a "query_text" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param server the crossfire server connection for sending reply commands
     * @param commandCallback the command callback to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseQueryText(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final CrossfireServerConnection server, @NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage activeImage = imageParser.getImage(args.get());
        final BufferedImage inactiveImage = imageParser.getImage(args.get());
        final Font font = definedFonts.lookup(args.get());
        final Color inactiveColor = ParseUtils.parseColor(args.get());
        final Color activeColor = ParseUtils.parseColor(args.get());
        final int margin = ExpressionParser.parseInt(args.get());
        final boolean enableHistory = NumberParser.parseBoolean(args.get());
        insertGuiElement(constraints, parent, new GUIQueryText(server, commandCallback, tooltipManager, elementListener, name, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", enableHistory));
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
     * @param gui the gui to modify
     */
    private static void parseSetAutoSize(@NotNull final Gui gui) {
        gui.setAutoSize(true);
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
        definedGUIElements.lookup(args.get()).setElementVisible(false);
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
     * Parses a "set_num_look_objects" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     */
    private void parseSetNumLookObjects(@NotNull final Args args) throws IOException {
        skin.setNumLookObjects(ExpressionParser.parseInt(args.get()));
    }

    /**
     * Parses a "scrollbar" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseScrollbar(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final boolean proportionalSlider = NumberParser.parseBoolean(args.get());
        final Object element = definedGUIElements.lookup(args.get());
        final Color colorBackground = ParseUtils.parseColor(args.get());
        final Color colorForeground = ParseUtils.parseColor(args.get());
        if (!(element instanceof GUIScrollable2)) {
            throw new IOException("'"+element+"' is not a scrollable element");
        }
        insertGuiElement(constraints, parent, new GUIScrollBar(tooltipManager, elementListener, name, proportionalSlider, (GUIScrollable2)element, colorBackground, colorForeground));
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
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param commandCallback the command callback to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseText(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final CommandCallback commandCallback, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage activeImage = imageParser.getImage(args.get());
        final BufferedImage inactiveImage = imageParser.getImage(args.get());
        final Font font = definedFonts.lookup(args.get());
        final Color activeColor = ParseUtils.parseColor(args.get());
        final Color inactiveColor = ParseUtils.parseColor(args.get());
        final int margin = ExpressionParser.parseInt(args.get());
        final CommandList commandList = skin.getCommandList(args.get());
        final boolean enableHistory = NumberParser.parseBoolean(args.get());
        insertGuiElement(constraints, parent, new GUITextField(commandCallback, tooltipManager, elementListener, name, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", commandList, enableHistory));
    }

    /**
     * Parses a "textbutton" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseTextButton(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        if (textButtonFactory == null) {
            throw new IOException("missing 'def textbutton' command");
        }

        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final boolean autoRepeat = NumberParser.parseBoolean(args.get());
        final CommandList commandList = skin.getCommandList(args.get());
        final String text = ParseUtils.parseText(args, lnr);
        assert textButtonFactory != null;
        insertGuiElement(constraints, parent, textButtonFactory.newTextButton(tooltipManager, elementListener, name, text, autoRepeat, commandList));
    }

    /**
     * Parses a "textgauge" command.
     * @param args the command arguments
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseTextGauge(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final LineNumberReader lnr) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final BufferedImage positiveImage = imageParser.getImage(args.get());
        final BufferedImage negativeImage = args.get().equals("null") ? null : imageParser.getImage(args.getPrev());
        final BufferedImage emptyImage = imageParser.getImage(args.get());
        final GaugeUpdater gaugeUpdater = newGaugeUpdater(args.get());
        final Orientation orientation = ParseUtils.parseOrientation(args.get());
        final Color color = ParseUtils.parseColor(args.get());
        final Font font = definedFonts.lookup(args.get());
        final String tooltipPrefix = ParseUtils.parseText(args, lnr);
        final GUITextGauge element = new GUITextGauge(tooltipManager, elementListener, name, positiveImage, negativeImage, emptyImage, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
        insertGuiElement(constraints, parent, element);
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
     * @param parent the GUI element's parent container
     * @param constraintParser the constraint parser to use
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param characterModel the character model to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCharacterList(@NotNull final Args args, @NotNull final Container parent, @NotNull final ConstraintParser constraintParser, @NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CharacterModel characterModel) throws IOException, JXCSkinException {
        final String name = args.get();
        final Object constraints = constraintParser.parseConstraints(args);
        final int cellWidth = ExpressionParser.parseInt(args.get());
        final int cellHeight = ExpressionParser.parseInt(args.get());
        final Font font = definedFonts.lookup(args.get());
        final GUIElement list = new GUICharacterList(tooltipManager, elementListener, name, cellWidth, cellHeight, font, characterModel);
        insertGuiElement(constraints, parent, list);
    }

    /**
     * Adds a new {@link GUIElement} to this skin.
     * @param constraints the constraints for the element
     * @param parent the GUI element's parent container
     * @param guiElement the GUI element
     * @throws JXCSkinException if the name is not unique
     */
    private void insertGuiElement(@NotNull final Object constraints, @NotNull final Container parent, @NotNull final GUIElement guiElement) throws JXCSkinException {
        definedGUIElements.insert(guiElement.getName(), guiElement);
        skin.insertGuiElement(guiElement);
        guiBuilder.defineElement(guiElement, parent, constraints);
    }

    /**
     * Parses and builds command arguments.
     * @param listName the command list name to add to
     * @param args the list of arguments
     * @param element the target element
     * @param command the command to parse the arguments of
     * @param guiStateManager the gui state manager instance
     * @param commands the commands instance for executing commands
     * @param lnr the source to read more parameters from
     * @param commandQueue the command queue for executing commands
     * @param crossfireServerConnection the server connection to use
     * @param commandCallback the command callback to use
     * @param macros the macros instance to use
     * @throws IOException if a syntax error occurs
     * @throws JXCSkinException if an element cannot be found
     */
    private void addCommand(@NotNull final String listName, @NotNull final Args args, @Nullable final GUIElement element, @NotNull final String command, @NotNull final GuiStateManager guiStateManager, @NotNull final Commands commands, @NotNull final LineNumberReader lnr, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final CommandCallback commandCallback, @NotNull final Macros macros) throws IOException, JXCSkinException {
        final CommandList commandList = skin.getCommandList(listName);
        commandList.add(commandParser.parseCommandArgs(args, element, command, guiStateManager, commands, lnr, commandQueue, crossfireServerConnection, commandCallback, macros));
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
     * Creates an {@link Extent} from four parameters.
     * @param args the parameters
     * @return the extent
     * @throws IOException if the extent cannot be parsed
     */
    @NotNull
    private static Extent parseExtent(@NotNull final Args args) throws IOException {
        final Expression x = ExpressionParser.parseExpression(args.get());
        final Expression y = ExpressionParser.parseExpression(args.get());
        final Expression w = ExpressionParser.parseExpression(args.get());
        final Expression h = ExpressionParser.parseExpression(args.get());
        return new Extent(x, y, w, h);
    }

    public static class State {

        @NotNull
        private final ConstraintParser constraintParser;

        @NotNull
        private final Container parent;

        @NotNull
        private final GUIElement element;

        public State(@NotNull final ConstraintParser constraintParser, @NotNull final Container parent, @NotNull final GUIElement element) {
            this.constraintParser = constraintParser;
            this.parent = parent;
            this.element = element;
        }

        @NotNull
        public ConstraintParser getConstraintParser() {
            return constraintParser;
        }

        @NotNull
        public Container getParent() {
            return parent;
        }

        @NotNull
        public GUIElement getElement() {
            return element;
        }

    }

}

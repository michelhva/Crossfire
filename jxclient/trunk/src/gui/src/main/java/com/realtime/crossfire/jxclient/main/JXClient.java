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
 * Copyright (C) 2006-2020 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.main;

import com.realtime.crossfire.jxclient.account.CharacterModel;
import com.realtime.crossfire.jxclient.commands.AgainCommand;
import com.realtime.crossfire.jxclient.commands.BindCommand;
import com.realtime.crossfire.jxclient.commands.BindingsCommand;
import com.realtime.crossfire.jxclient.commands.ClearCommand;
import com.realtime.crossfire.jxclient.commands.CommandExecutorImpl;
import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.DebugMessagesCommand;
import com.realtime.crossfire.jxclient.commands.ExecCommand;
import com.realtime.crossfire.jxclient.commands.ScreenshotCommand;
import com.realtime.crossfire.jxclient.commands.ScriptCommand;
import com.realtime.crossfire.jxclient.commands.ScriptkillCommand;
import com.realtime.crossfire.jxclient.commands.ScriptkillallCommand;
import com.realtime.crossfire.jxclient.commands.ScriptsCommand;
import com.realtime.crossfire.jxclient.commands.ScripttellCommand;
import com.realtime.crossfire.jxclient.commands.SetCommand;
import com.realtime.crossfire.jxclient.commands.UnbindCommand;
import com.realtime.crossfire.jxclient.faces.AskfaceFaceQueue;
import com.realtime.crossfire.jxclient.faces.DefaultFacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesQueue;
import com.realtime.crossfire.jxclient.faces.FileCache;
import com.realtime.crossfire.jxclient.gui.commandlist.GUICommandFactory;
import com.realtime.crossfire.jxclient.gui.commands.ScreenshotFiles;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.keybindings.KeybindingsManager;
import com.realtime.crossfire.jxclient.gui.label.TooltipManagerImpl;
import com.realtime.crossfire.jxclient.gui.misc.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.misc.MouseTracker;
import com.realtime.crossfire.jxclient.gui.textinput.CommandExecutor;
import com.realtime.crossfire.jxclient.gui.textinput.GUICommandFactoryImpl;
import com.realtime.crossfire.jxclient.guistate.GuiState;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.InventoryComparator;
import com.realtime.crossfire.jxclient.items.InventoryView;
import com.realtime.crossfire.jxclient.items.KnowledgeTypeView;
import com.realtime.crossfire.jxclient.items.KnowledgeView;
import com.realtime.crossfire.jxclient.items.QuestsView;
import com.realtime.crossfire.jxclient.items.SpellSkillView;
import com.realtime.crossfire.jxclient.items.SpellsView;
import com.realtime.crossfire.jxclient.map.MapUpdaterState;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.metaserver.MetaserverProcessor;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.scripts.ScriptManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.DefaultCrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.Model;
import com.realtime.crossfire.jxclient.settings.CommandHistoryFactory;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.Macros;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.SettingsEntries;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.settings.options.Pickup;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skin.io.JXCSkinLoader;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.sound.MusicWatcher;
import com.realtime.crossfire.jxclient.sound.SoundCheckBoxOption;
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.sound.SoundWatcher;
import com.realtime.crossfire.jxclient.sound.SoundStatsWatcher;
import com.realtime.crossfire.jxclient.stats.ActiveSkillWatcher;
import com.realtime.crossfire.jxclient.stats.PoisonWatcher;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.JXCConnection;
import com.realtime.crossfire.jxclient.window.KeyHandler;
import com.realtime.crossfire.jxclient.window.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is the entry point for JXClient. Note that this class doesn't do much by
 * itself - most of the work in done in JXCWindow or CrossfireServerConnection.
 * @author Lauwenmark
 * @version 1.0
 * @see JXCWindow
 * @see CrossfireServerConnection
 * @since 1.0
 */
public class JXClient {

    /**
     * The program entry point.
     * @param args the command line arguments
     */
    public static void main(@NotNull final String[] args) {
        Thread.currentThread().setName("JXClient:Main");
        final String buildNumber = getBuildNumber();
        System.out.println("JXClient "+buildNumber+" - Crossfire Java Client");
        System.out.println("(C)2005-2008 by Yann \"Lauwenmark\" Chachkoff.");
        System.out.println("(C)2006-2020 Andreas Kirschbaum.");
        System.out.println("This software is placed under the GPL License");
        final Options options = new Options();
        options.parse(args);
        //noinspection InstantiationOfUtilityClass
        new JXClient(options, buildNumber);
    }

    /**
     * Returns the build number as a string.
     * @return the build number
     */
    @NotNull
    private static String getBuildNumber() {
        try {
            return ResourceBundle.getBundle("build").getString("build.number");
        } catch (final MissingResourceException ignored) {
            return "unknown";
        }
    }

    /**
     * The constructor of the class. This is where the main window is created.
     * Initialization of a JXCWindow is the only task performed here.
     * @param options the options
     * @param buildNumber the client's build number
     */
    private JXClient(@NotNull final Options options, @NotNull final String buildNumber) {
        try {
            try (Writer debugProtocolOutputStreamWriter = openDebugStream(options.getDebugProtocolFilename())) {
                try (Writer debugKeyboardOutputStreamWriter = openDebugStream(options.getDebugKeyboardFilename())) {
                    try (Writer debugMouseOutputStreamWriter = openDebugStream(options.getDebugMouseFilename())) {
                        try (Writer debugScreenOutputStreamWriter = openDebugStream(options.getDebugScreenFilename())) {
                            try (Writer debugSoundOutputStreamWriter = openDebugStream(options.getDebugSoundFilename())) {
                                final Settings settings = new Settings(Filenames.getSettingsFile());
                                settings.remove("resolution"); // delete obsolete entry
                                settings.remove("width"); // delete obsolete entry
                                settings.remove("height"); // delete obsolete entry
                                settings.remove("skin"); // delete obsolete entry
                                final OptionManager optionManager = new OptionManager(settings);
                                final MetaserverModel metaserverModel = new MetaserverModel();
                                final CharacterModel characterModel = new CharacterModel();
                                final Model model = new Model();
                                final CrossfireServerConnection server = new DefaultCrossfireServerConnection(model, debugProtocolOutputStreamWriter == null ? null : new DebugWriter(debugProtocolOutputStreamWriter), "JXClient "+buildNumber);
                                server.start();
                                try {
                                    final AskfaceFaceQueue askfaceFaceQueue = new AskfaceFaceQueue(server);
                                    final FacesQueue facesQueue = new FacesQueue(askfaceFaceQueue, new FileCache(Filenames.getOriginalImageCacheDir()), new FileCache(Filenames.getScaledImageCacheDir()), new FileCache(Filenames.getMagicMapImageCacheDir()));
                                    final FacesManager facesManager = new DefaultFacesManager(model.getFaceCache(), facesQueue);
                                    model.setItemsManager(facesManager);
                                    final InventoryView inventoryView = new InventoryView(model.getItemSet(), new InventoryComparator());
                                    final FloorView floorView = new FloorView(model.getItemSet());
                                    final Metaserver metaserver = new Metaserver(Filenames.getMetaserverCacheFile(), metaserverModel);
                                    new MetaserverProcessor(metaserver, model.getGuiStateManager());
                                    final SoundManager soundManager = new SoundManager(model.getGuiStateManager(), debugSoundOutputStreamWriter == null ? null : new DebugWriter(debugSoundOutputStreamWriter));
                                    soundManager.start();
                                    try {
                                        optionManager.addOption("sound_enabled", "Whether sound is enabled.", new SoundCheckBoxOption(soundManager));
                                    } catch (final OptionException ex) {
                                        throw new AssertionError(ex);
                                    }

                                    new MusicWatcher(server, soundManager);
                                    new SoundWatcher(server, soundManager);
                                    new PoisonWatcher(model.getStats(), server);
                                    new ActiveSkillWatcher(model.getStats(), server);
                                    final Macros macros = new Macros(server);
                                    final MapUpdaterState mapUpdaterState = new MapUpdaterState(facesManager, model.getGuiStateManager());
                                    new CfMapUpdater(mapUpdaterState, server, facesManager, model.getGuiStateManager());
                                    final SpellsView spellsView = new SpellsView(model.getSpellsManager(), facesManager);
                                    final SpellSkillView spellSkillsView = new SpellSkillView(model.getSpellsManager(), facesManager);
                                    final QuestsView questsView = new QuestsView(model.getQuestsManager(), facesManager);
                                    final KnowledgeView knowledgeView = new KnowledgeView(facesManager, model.getKnowledgeManager());
                                    final KnowledgeTypeView knowledgeTypesView = new KnowledgeTypeView(facesManager, model.getKnowledgeManager());
                                    final CommandQueue commandQueue = new CommandQueue(server, model.getGuiStateManager());
                                    final ScriptManager scriptManager = new ScriptManager(commandQueue, server, model.getStats(), floorView, model.getItemSet(), model.getSpellsManager(), mapUpdaterState, model.getSkillSet());
                                    final Shortcuts shortcuts = new Shortcuts(commandQueue, model.getSpellsManager());
                                    final Logger logger = new Logger(server, null, settings.getBoolean(SettingsEntries.MESSAGE_LOG_SETTINGS_ENTRY));

                                    final Exiter exiter = new Exiter();
                                    final JXCWindow[] window = new JXCWindow[1];
                                    final GuiManagerCommandCallback commandCallback = new GuiManagerCommandCallback(exiter, server);
                                    final Commands commands = new Commands();
                                    final CommandExecutor commandExecutor = new CommandExecutorImpl(commandQueue, commands);
                                    final GUICommandFactory guiCommandFactory = new GUICommandFactoryImpl(commandCallback, commandExecutor, macros);
                                    final GuiFactory guiFactory = new GuiFactory(guiCommandFactory);
                                    final MouseTracker mouseTracker = new MouseTracker(options.isDebugGui(), debugMouseOutputStreamWriter, guiFactory);
                                    SwingUtilities.invokeAndWait(() -> {
                                        final JXCWindowRenderer windowRenderer = new JXCWindowRenderer(mouseTracker, server, debugScreenOutputStreamWriter);
                                        new SoundStatsWatcher(model.getStats(), windowRenderer, server, soundManager);
                                        final Pickup characterPickup;
                                        try {
                                            characterPickup = new Pickup(commandQueue, optionManager);
                                        } catch (final OptionException ex) {
                                            throw new AssertionError(ex);
                                        }
                                        final ScreenshotFiles screenshotFiles = new ScreenshotFiles();
                                        final CommandHistoryFactory commandHistoryFactory = new CommandHistoryFactory();
                                        commands.addCommand(new BindCommand(server, commandCallback, guiCommandFactory));
                                        commands.addCommand(new UnbindCommand(commandCallback, server));
                                        commands.addCommand(new ScreenshotCommand(windowRenderer, server, screenshotFiles));
                                        commands.addCommand(new ScriptCommand(scriptManager, server));
                                        commands.addCommand(new ScriptkillCommand(scriptManager, server));
                                        commands.addCommand(new ScriptkillallCommand(scriptManager, server));
                                        commands.addCommand(new ScriptsCommand(scriptManager, server));
                                        commands.addCommand(new ScripttellCommand(scriptManager, server));
                                        commands.addCommand(new ExecCommand(commandCallback, server));
                                        commands.addCommand(new SetCommand(server, optionManager));
                                        commands.addCommand(new ClearCommand(windowRenderer, server));
                                        commands.addCommand(new DebugMessagesCommand(server));
                                        commands.addCommand(new AgainCommand(server, commandExecutor, commandHistoryFactory.getCommandHistory("command")));
                                        final File keybindingsFile;
                                        try {
                                            keybindingsFile = Filenames.getKeybindingsFile(null, null);
                                        } catch (final IOException ex) {
                                            System.err.println("Cannot read keybindings file: "+ex.getMessage());
                                            exiter.terminate();
                                            return;
                                        }
                                        final KeybindingsManager keybindingsManager = new KeybindingsManager(keybindingsFile, guiCommandFactory);
                                        commands.addCommand(new BindingsCommand(server, keybindingsManager));
                                        final JXCConnection connection = new JXCConnection(keybindingsManager, shortcuts, settings, characterPickup, server, model.getGuiStateManager(), logger);
                                        final TooltipManagerImpl tooltipManager = new TooltipManagerImpl();
                                        final GuiManager guiManager = new GuiManager(model.getGuiStateManager(), tooltipManager, settings, server, windowRenderer, guiFactory, keybindingsManager, connection);
                                        commandCallback.init(guiManager);
                                        final KeyBindings defaultKeyBindings = new KeyBindings(null, guiCommandFactory);
                                        final JXCSkinLoader jxcSkinLoader = new JXCSkinLoader(model, inventoryView, floorView, spellsView, spellSkillsView, facesManager, mapUpdaterState, defaultKeyBindings, optionManager, options.getTileSize(), keybindingsManager, questsView, commandHistoryFactory, knowledgeView, knowledgeTypesView, options.isAvoidCopyArea(), guiManager, guiFactory);
                                        final SkinLoader skinLoader = new SkinLoader(commandCallback, metaserverModel, options.getResolution(), macros, windowRenderer, server, model.getGuiStateManager(), tooltipManager, commandQueue, jxcSkinLoader, commandExecutor, shortcuts, characterModel, model.getSmoothFaces(), guiFactory);
                                        new FacesTracker(model.getGuiStateManager(), facesManager);
                                        new PlayerNameTracker(model.getGuiStateManager(), connection, model.getItemSet());
                                        new OutputCountTracker(model.getGuiStateManager(), server, commandQueue);
                                        final DefaultKeyHandler defaultKeyHandler = new DefaultKeyHandler(exiter, guiManager, server, model.getGuiStateManager());
                                        final KeyHandler keyHandler = new KeyHandler(debugKeyboardOutputStreamWriter, keybindingsManager, commandQueue, windowRenderer, defaultKeyHandler);
                                        window[0] = new JXCWindow(exiter, server, optionManager, model.getGuiStateManager(), windowRenderer, commandQueue, guiManager, keyHandler, characterModel, connection);
                                        window[0].init(options.getResolution(), options.getSkin(), options.isFullScreen(), skinLoader);
                                        keybindingsManager.loadKeybindings();
                                        final String serverInfo = options.getServer();
                                        if (serverInfo == null) {
                                            model.getGuiStateManager().changeGUI(JXCWindow.DISABLE_START_GUI ? GuiState.METASERVER : GuiState.START);
                                        } else {
                                            model.getGuiStateManager().connect(serverInfo);
                                        }
                                    });
                                    exiter.waitForTermination();
                                    SwingUtilities.invokeAndWait(window[0]::term);
                                    soundManager.shutdown();
                                } finally {
                                    server.stop();
                                }
                            }
                        }
                    }
                }
            }
        } catch (final InterruptedException|InvocationTargetException|IOException ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
            System.exit(1);
            throw new AssertionError(ex);
        }

        System.exit(0);
    }

    /**
     * Opens an debug output stream.
     * @param filename the filename to write to or {@code null}
     * @return the output stream or {@code null}
     */
    @Nullable
    private static Writer openDebugStream(@Nullable final String filename) {
        if (filename == null) {
            return null;
        }

        Writer writer = null;
        try {
            final FileOutputStream outputStream = new FileOutputStream(filename);
            try {
                //noinspection IOResourceOpenedButNotSafelyClosed
                writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            } finally {
                if (writer == null) {
                    outputStream.close();
                }
            }
        } catch (final IOException ex) {
            System.err.println(filename+": cannot create output file: "+ex.getMessage());
            return null;
        }
        return writer;
    }

}

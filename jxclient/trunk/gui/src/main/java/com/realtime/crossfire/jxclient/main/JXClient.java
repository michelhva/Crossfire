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
import com.realtime.crossfire.jxclient.commands.BindCommand;
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
import com.realtime.crossfire.jxclient.gui.commands.ScreenshotFiles;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.keybindings.KeybindingsManager;
import com.realtime.crossfire.jxclient.gui.label.TooltipManagerImpl;
import com.realtime.crossfire.jxclient.gui.misc.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.misc.MouseTracker;
import com.realtime.crossfire.jxclient.gui.textinput.CommandExecutor;
import com.realtime.crossfire.jxclient.gui.textinput.GUICommandFactory;
import com.realtime.crossfire.jxclient.guistate.GuiState;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.InventoryComparator;
import com.realtime.crossfire.jxclient.items.InventoryView;
import com.realtime.crossfire.jxclient.items.QuestsView;
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
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.Macros;
import com.realtime.crossfire.jxclient.settings.Settings;
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
import com.realtime.crossfire.jxclient.sound.StatsWatcher;
import com.realtime.crossfire.jxclient.stats.ActiveSkillWatcher;
import com.realtime.crossfire.jxclient.stats.PoisonWatcher;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.JXCConnection;
import com.realtime.crossfire.jxclient.window.KeyHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
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
        System.out.println("(C)2005 by Lauwenmark.");
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
            final Writer debugProtocolOutputStreamWriter = openDebugStream(options.getDebugProtocolFilename());
            try {
                final Writer debugKeyboardOutputStreamWriter = openDebugStream(options.getDebugKeyboardFilename());
                try {
                    final Writer debugScreenOutputStreamWriter = openDebugStream(options.getDebugScreenFilename());
                    try {
                        final Writer debugSoundOutputStreamWriter = openDebugStream(options.getDebugSoundFilename());
                        try {
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
                                model.setAskfaceFaceQueue(askfaceFaceQueue);
                                final FacesQueue facesQueue = new FacesQueue(askfaceFaceQueue, new FileCache(Filenames.getOriginalImageCacheDir()), new FileCache(Filenames.getScaledImageCacheDir()), new FileCache(Filenames.getMagicMapImageCacheDir()));
                                final FacesManager facesManager = new DefaultFacesManager(model.getFaceCache(), facesQueue);
                                model.setItemsManager(facesManager);
                                final InventoryView inventoryView = new InventoryView(model.getItemSet(), new InventoryComparator());
                                final FloorView floorView = new FloorView(model.getItemSet());
                                final Metaserver metaserver = new Metaserver(Filenames.getMetaserverCacheFile(), metaserverModel);
                                new MetaserverProcessor(metaserver, model.getGuiStateManager());
                                final SoundManager soundManager = new SoundManager(model.getGuiStateManager(), debugSoundOutputStreamWriter == null ? null : new DebugWriter(debugSoundOutputStreamWriter));
                                try {
                                    optionManager.addOption("sound_enabled", "Whether sound is enabled.", new SoundCheckBoxOption(soundManager));
                                } catch (final OptionException ex) {
                                    throw new AssertionError(ex);
                                }

                                final MouseTracker mouseTracker = new MouseTracker(options.isDebugGui());
                                final JXCWindowRenderer windowRenderer = new JXCWindowRenderer(mouseTracker, server, debugScreenOutputStreamWriter);
                                new MusicWatcher(server, soundManager);
                                new SoundWatcher(server, soundManager);
                                new StatsWatcher(model.getStats(), windowRenderer, server, soundManager);
                                new PoisonWatcher(model.getStats(), server);
                                new ActiveSkillWatcher(model.getStats(), server);
                                final Macros macros = new Macros(server);
                                final MapUpdaterState mapUpdaterState = new MapUpdaterState(facesManager, model.getGuiStateManager());
                                new CfMapUpdater(mapUpdaterState, server, facesManager, model.getGuiStateManager());
                                final SpellsView spellsView = new SpellsView(model.getSpellsManager(), facesManager);
                                final QuestsView questsView = new QuestsView(model.getQuestsManager(), facesManager);
                                final CommandQueue commandQueue = new CommandQueue(server, model.getGuiStateManager());
                                final ScriptManager scriptManager = new ScriptManager(commandQueue, server, model.getStats(), floorView, model.getItemSet(), model.getSpellsManager(), mapUpdaterState, model.getSkillSet());
                                final Shortcuts shortcuts = new Shortcuts(commandQueue, model.getSpellsManager());

                                final Exiter exiter = new Exiter();
                                final JXCWindow[] window = new JXCWindow[1];
                                SwingUtilities.invokeAndWait(new Runnable() {

                                    @Override
                                    public void run() {
                                        final TooltipManagerImpl tooltipManager = new TooltipManagerImpl();
                                        final Pickup characterPickup;
                                        try {
                                            characterPickup = new Pickup(commandQueue, optionManager);
                                        } catch (final OptionException ex) {
                                            throw new AssertionError(ex);
                                        }
                                        final GuiManagerCommandCallback commandCallback = new GuiManagerCommandCallback(exiter, server);
                                        final ScreenshotFiles screenshotFiles = new ScreenshotFiles();
                                        final Commands commands = new Commands();
                                        final CommandExecutor commandExecutor = new CommandExecutorImpl(commandQueue, commands);
                                        final GUICommandFactory guiCommandFactory = new GUICommandFactory(commandCallback, commandExecutor, macros);
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
                                        final File keybindingsFile;
                                        try {
                                            keybindingsFile = Filenames.getKeybindingsFile(null, null);
                                        } catch (final IOException ex) {
                                            System.err.println("Cannot read keybindings file: "+ex.getMessage());
                                            exiter.terminate();
                                            return;
                                        }
                                        final KeybindingsManager keybindingsManager = new KeybindingsManager(keybindingsFile, guiCommandFactory);
                                        final JXCConnection connection = new JXCConnection(keybindingsManager, shortcuts, settings, characterPickup, server, model.getGuiStateManager());
                                        final GuiFactory guiFactory = new GuiFactory(guiCommandFactory);
                                        final GuiManager guiManager = new GuiManager(model.getGuiStateManager(), tooltipManager, settings, server, windowRenderer, guiFactory, keybindingsManager, connection);
                                        commandCallback.init(guiManager);
                                        final KeyBindings defaultKeyBindings = new KeyBindings(null, guiCommandFactory);
                                        final JXCSkinLoader jxcSkinLoader = new JXCSkinLoader(model.getItemSet(), inventoryView, floorView, spellsView, model.getSpellsManager(), facesManager, model.getStats(), mapUpdaterState, defaultKeyBindings, optionManager, model.getExperienceTable(), model.getSkillSet(), options.getTileSize(), keybindingsManager, model.getQuestsManager(), questsView);
                                        final SkinLoader skinLoader = new SkinLoader(commandCallback, metaserverModel, options.getResolution(), macros, windowRenderer, server, model.getGuiStateManager(), tooltipManager, commandQueue, jxcSkinLoader, commandExecutor, shortcuts, characterModel, model.getSmoothFaces(), guiCommandFactory);
                                        new FacesTracker(model.getGuiStateManager(), facesManager);
                                        new PlayerNameTracker(model.getGuiStateManager(), connection, model.getItemSet());
                                        new OutputCountTracker(model.getGuiStateManager(), server, commandQueue);
                                        final DefaultKeyHandler defaultKeyHandler = new DefaultKeyHandler(exiter, guiManager, server, model.getGuiStateManager());
                                        final KeyHandler keyHandler = new KeyHandler(debugKeyboardOutputStreamWriter, keybindingsManager, commandQueue, windowRenderer, defaultKeyHandler);
                                        window[0] = new JXCWindow(exiter, server, optionManager, model.getGuiStateManager(), windowRenderer, commandQueue, guiManager, keyHandler, characterModel, connection);
                                        window[0].init(options.getResolution(), options.getSkin(), options.isFullScreen(), skinLoader);
                                        keybindingsManager.loadKeybindings();
                                        final String serverInfo = options.getServer();
                                        if (serverInfo != null) {
                                            model.getGuiStateManager().connect(serverInfo);
                                        } else {
                                            model.getGuiStateManager().changeGUI(JXCWindow.DISABLE_START_GUI ? GuiState.METASERVER : GuiState.START);
                                        }
                                    }

                                });
                                exiter.waitForTermination();
                                SwingUtilities.invokeAndWait(new Runnable() {

                                    @Override
                                    public void run() {
                                        window[0].term();
                                        soundManager.shutdown();
                                    }

                                });
                            } finally {
                                server.stop();
                            }
                        } finally {
                            if (debugSoundOutputStreamWriter != null) {
                                debugSoundOutputStreamWriter.close();
                            }
                        }
                    } finally {
                        if (debugScreenOutputStreamWriter != null) {
                            debugScreenOutputStreamWriter.close();
                        }
                    }
                } finally {
                    if (debugKeyboardOutputStreamWriter != null) {
                        debugKeyboardOutputStreamWriter.close();
                    }
                }
            } finally {
                if (debugProtocolOutputStreamWriter != null) {
                    debugProtocolOutputStreamWriter.close();
                }
            }
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.exit(1);
            throw new AssertionError();
        } catch (final InterruptedException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.exit(1);
            throw new AssertionError();
        } catch (final InvocationTargetException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.exit(1);
            throw new AssertionError();
        }

        System.exit(0);
    }

    /**
     * Opens an debug output stream.
     * @param filename the filename to write to or <code>null</code>
     * @return the output stream or <code>null</code>
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
                writer = new OutputStreamWriter(outputStream, "UTF-8");
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

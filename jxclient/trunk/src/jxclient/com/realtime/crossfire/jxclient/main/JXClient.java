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
import com.realtime.crossfire.jxclient.faces.FaceCache;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesQueue;
import com.realtime.crossfire.jxclient.faces.FileCache;
import com.realtime.crossfire.jxclient.gui.gui.GuiFactory;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.MouseTracker;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.InventoryComparator;
import com.realtime.crossfire.jxclient.items.InventoryView;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.scripts.ScriptManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.DefaultCrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.settings.options.Pickup;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.sound.MusicWatcher;
import com.realtime.crossfire.jxclient.sound.SoundCheckBoxOption;
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.sound.SoundWatcher;
import com.realtime.crossfire.jxclient.sound.StatsWatcher;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.ActiveSkillWatcher;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.PoisonWatcher;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.JXCConnection;
import com.realtime.crossfire.jxclient.window.KeybindingsManager;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
     * @param args The command line arguments.
     */
    public static void main(@NotNull final String[] args) {
        final String buildNumber = getBuildNumber();
        System.out.println("JXClient " + buildNumber + " - Crossfire Java Client");
        System.out.println("(C)2005 by Lauwenmark.");
        System.out.println("This software is placed under the GPL License");
        final Options options = new Options();
        try {
            options.parse(args);
        } catch (final IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
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
        } catch (final MissingResourceException e) {
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
                        final OptionManager optionManager = new OptionManager(options.getPrefs());
                        final MetaserverModel metaserverModel = new MetaserverModel();
                        final Object semaphoreRedraw = new Object();
                        final CrossfireServerConnection server = new DefaultCrossfireServerConnection(semaphoreRedraw, debugProtocolOutputStreamWriter == null ? null : new DebugWriter(debugProtocolOutputStreamWriter), "JXClient " + buildNumber);
                        server.start();
                        try {
                            final GuiStateManager guiStateManager = new GuiStateManager(server);
                            final ExperienceTable experienceTable = new ExperienceTable(server);
                            final SkillSet skillSet = new SkillSet(server, guiStateManager);
                            final Stats stats = new Stats(server, experienceTable, skillSet, guiStateManager);
                            final FaceCache faceCache = new FaceCache(server);
                            final FacesQueue facesQueue = new FacesQueue(server, new FileCache(Filenames.getOriginalImageCacheDir()), new FileCache(Filenames.getScaledImageCacheDir()), new FileCache(Filenames.getMagicMapImageCacheDir()));
                            final FacesManager facesManager = new FacesManager(faceCache, facesQueue);
                            final ItemSet itemSet = new ItemSet();
                            final InventoryView inventoryView = new InventoryView(itemSet, new InventoryComparator());
                            final FloorView floorView = new FloorView(itemSet);
                            new ItemsManager(server, facesManager, stats, skillSet, guiStateManager, itemSet);
                            new Metaserver(Filenames.getMetaserverCacheFile(), metaserverModel, guiStateManager);
                            final SoundManager soundManager = new SoundManager(guiStateManager);
                            try {
                                optionManager.addOption("sound_enabled", "Whether sound is enabled.", new SoundCheckBoxOption(soundManager));
                            } catch (final OptionException ex) {
                                throw new AssertionError();
                            }

                            final MouseTracker mouseTracker = new MouseTracker(options.isDebugGui());
                            final JXCWindowRenderer windowRenderer = new JXCWindowRenderer(mouseTracker, semaphoreRedraw, server, debugScreenOutputStreamWriter);
                            mouseTracker.init(windowRenderer);
                            new MusicWatcher(server, soundManager);
                            new SoundWatcher(server, soundManager);
                            new StatsWatcher(stats, windowRenderer, server, soundManager);
                            new PoisonWatcher(stats, server);
                            new ActiveSkillWatcher(stats, server);
                            final Macros macros = new Macros(server);
                            final CfMapUpdater mapUpdater = new CfMapUpdater(server, facesManager, guiStateManager);
                            final SpellsManager spellsManager = new SpellsManager(server, guiStateManager);
                            final CommandQueue commandQueue = new CommandQueue(server, guiStateManager);
                            final ScriptManager scriptManager = new ScriptManager(commandQueue, server, stats, floorView, itemSet, spellsManager, mapUpdater, skillSet);
                            final Shortcuts shortcuts = new Shortcuts(commandQueue, spellsManager);

                            final JXCWindow[] window = new JXCWindow[1];
                            SwingUtilities.invokeAndWait(new Runnable() {
                                /** {@inheritDoc} */
                                @Override
                                public void run() {
                                    final Object semaphoreDrawing = new Object();
                                    final TooltipManager tooltipManager = new TooltipManager();
                                    final Pickup characterPickup;
                                    try {
                                        characterPickup = new Pickup(commandQueue, optionManager);
                                    } catch (final OptionException ex) {
                                        throw new AssertionError();
                                    }
                                    final GuiManagerCommandCallback commandCallback = new GuiManagerCommandCallback();
                                    final Commands commands = new Commands(windowRenderer, commandQueue, server, scriptManager, optionManager, commandCallback, macros);
                                    final KeybindingsManager keybindingsManager = new KeybindingsManager(commands, commandCallback, macros);
                                    final Settings settings = options.getPrefs();
                                    final JXCConnection connection = new JXCConnection(keybindingsManager, shortcuts, settings, characterPickup, server, guiStateManager);
                                    final GuiFactory guiFactory = new GuiFactory(options.isDebugGui() ? mouseTracker : null, commands, commandCallback, macros);
                                    final GuiManager guiManager = new GuiManager(guiStateManager, semaphoreDrawing, tooltipManager, settings, server, windowRenderer, guiFactory, keybindingsManager, connection);
                                    commandCallback.init(guiManager);
                                    final SkinLoader skinLoader = new SkinLoader(options.isDebugGui(), experienceTable, skillSet, stats, inventoryView, floorView, mapUpdater, spellsManager, mouseTracker, commandCallback, metaserverModel, options.getResolution(), macros, itemSet, facesManager, optionManager, windowRenderer, server, guiStateManager, tooltipManager, commandQueue);
                                    window[0] = new JXCWindow(server, debugKeyboardOutputStreamWriter, optionManager, guiStateManager, facesManager, itemSet, windowRenderer, commandQueue, semaphoreDrawing, characterPickup, keybindingsManager, connection, guiManager);
                                    connection.init(window[0]);
                                    window[0].init(options.getResolution(), mouseTracker, options.getSkin(), options.isFullScreen(), options.getServer(), shortcuts, skinLoader, commands);
                                }
                            });
                            window[0].waitForTermination();
                            SwingUtilities.invokeAndWait(new Runnable() {
                                /** {@inheritDoc} */
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
            e.printStackTrace();
            System.exit(1);
            throw new AssertionError();
        } catch (final InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
            throw new AssertionError();
        } catch (final InvocationTargetException e) {
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

        final FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(filename);
        } catch (final FileNotFoundException ex) {
            System.err.println(filename+": cannot create output file: "+ex.getMessage());
            return null;
        }

        final Writer writer;
        try {
            writer = new OutputStreamWriter(outputStream, "UTF-8");
        } catch (final UnsupportedEncodingException ex) {
            System.err.println("internal error: unsupported encoding 'UTF-8': "+ex.getMessage());
            return null;
        }

        return writer;
    }

}

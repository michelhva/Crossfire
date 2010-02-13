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

import com.realtime.crossfire.jxclient.faces.FaceCache;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesQueue;
import com.realtime.crossfire.jxclient.faces.FileCache;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.DefaultCrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.GuiStateManager;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.settings.options.SoundCheckBoxOption;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.sound.MusicWatcher;
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.sound.SoundWatcher;
import com.realtime.crossfire.jxclient.sound.StatsWatcher;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

/**
 * This is the entry point for JXClient. Note that this class doesn't do much by
 * itself - most of the work in done in JXCWindow or CrossfireServerConnection.
 * @author Lauwenmark
 * @version 1.0
 * @see JXCWindow
 * @see com.realtime.crossfire.jxclient.server.CrossfireServerConnection
 * @since 1.0
 */
public class JXClient
{
    /**
     * The program entry point.
     * @param args The command line arguments.
     */
    public static void main(@NotNull final String[] args)
    {
        System.out.println("JXClient - Crossfire Java Client");
        System.out.println("(C)2005 by Lauwenmark.");
        System.out.println("This software is placed under the GPL License");
        final Options options = new Options();
        try
        {
            options.parse(args);
        }
        catch (final IOException ex)
        {
            System.err.println(ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
        new JXClient(options);
    }

    /**
     * The constructor of the class. This is where the main window is created.
     * Initialization of a JXCWindow is the only task performed here.
     * @param options the options
     */
    private JXClient(@NotNull final Options options)
    {
        try
        {
            final FileOutputStream debugProtocolFileOutputStream = options.getDebugProtocolFilename() == null ? null : new FileOutputStream(options.getDebugProtocolFilename());
            try
            {
                final OutputStreamWriter debugProtocolOutputStreamWriter = debugProtocolFileOutputStream == null ? null : new OutputStreamWriter(debugProtocolFileOutputStream, "UTF-8");
                try
                {
                    final FileOutputStream debugKeyboardFileOutputStream = options.getDebugKeyboardFilename() == null ? null : new FileOutputStream(options.getDebugKeyboardFilename());
                    try
                    {
                        final OutputStreamWriter debugKeyboardOutputStreamWriter = debugKeyboardFileOutputStream == null ? null : new OutputStreamWriter(debugKeyboardFileOutputStream, "UTF-8");
                        try
                        {
                            final OptionManager optionManager = new OptionManager(options.getPrefs());
                            final Object terminateSync = new Object();
                            final MetaserverModel metaserverModel = new MetaserverModel();
                            final Object semaphoreRedraw = new Object();
                            final CrossfireServerConnection server = new DefaultCrossfireServerConnection(semaphoreRedraw, debugProtocolOutputStreamWriter == null ? null : new DebugWriter(debugProtocolOutputStreamWriter));
                            server.start();
                            try
                            {
                                final GuiStateManager guiStateManager = new GuiStateManager(server);
                                final ExperienceTable experienceTable = new ExperienceTable(server);
                                final SkillSet skillSet = new SkillSet(server, guiStateManager);
                                final Stats stats = new Stats(server, experienceTable, skillSet, guiStateManager);
                                final FaceCache faceCache = new FaceCache(server);
                                final FacesQueue facesQueue = new FacesQueue(server, new FileCache(Filenames.getOriginalImageCacheDir()), new FileCache(Filenames.getScaledImageCacheDir()), new FileCache(Filenames.getMagicMapImageCacheDir()));
                                final FacesManager facesManager = new FacesManager(faceCache, facesQueue);
                                final ItemsManager itemsManager = new ItemsManager(server, facesManager, stats, skillSet, guiStateManager);
                                final JXCWindow window = new JXCWindow(terminateSync, server, semaphoreRedraw, options.isDebugGui(), debugKeyboardOutputStreamWriter, options.getPrefs(), optionManager, metaserverModel, options.getResolution(), guiStateManager, experienceTable, skillSet, stats, facesManager, itemsManager);
                                new Metaserver(Filenames.getMetaserverCacheFile(), metaserverModel, guiStateManager);
                                final SoundManager soundManager = new SoundManager(guiStateManager);
                                try
                                {
                                    optionManager.addOption("sound_enabled", "Whether sound is enabled.", new SoundCheckBoxOption(soundManager));
                                }
                                catch (final OptionException ex)
                                {
                                    throw new AssertionError();
                                }

                                synchronized (terminateSync)
                                {
                                    SwingUtilities.invokeAndWait(new Runnable()
                                    {
                                        /** {@inheritDoc} */
                                        @Override
                                        public void run()
                                        {
                                            new MusicWatcher(server, soundManager);
                                            new SoundWatcher(server, soundManager);
                                            new StatsWatcher(stats, window.getWindowRenderer(), itemsManager, soundManager);
                                            window.init(options.getSkin(), options.isFullScreen(), options.getServer());
                                        }
                                    });
                                    terminateSync.wait();
                                }
                                SwingUtilities.invokeAndWait(new Runnable()
                                {
                                    /** {@inheritDoc} */
                                    @Override
                                    public void run()
                                    {
                                        window.term();
                                        soundManager.shutdown();
                                    }
                                });
                            }
                            finally
                            {
                                server.stop();
                            }
                        }
                        finally
                        {
                            if (debugKeyboardOutputStreamWriter != null)
                            {
                                debugKeyboardOutputStreamWriter.close();
                            }
                        }
                    }
                    finally
                    {
                        if (debugKeyboardFileOutputStream != null)
                        {
                            debugKeyboardFileOutputStream.close();
                        }
                    }
                }
                finally
                {
                    if (debugProtocolOutputStreamWriter != null)
                    {
                        debugProtocolOutputStreamWriter.close();
                    }
                }
            }
            finally
            {
                if (debugProtocolFileOutputStream != null)
                {
                    debugProtocolFileOutputStream.close();
                }
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            System.exit(1);
            throw new AssertionError();
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
            System.exit(1);
            throw new AssertionError();
        }
        catch (final InvocationTargetException e)
        {
            e.printStackTrace();
            System.exit(1);
            throw new AssertionError();
        }

        System.exit(0);
    }
}

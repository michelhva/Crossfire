//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient;

import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.SoundCheckBoxOption;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.sound.MusicWatcher;
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.sound.SoundWatcher;
import com.realtime.crossfire.jxclient.sound.StatsWatcher;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * This is the entry point for JXClient. Note that this class doesn't do much
 * by itself - most of the work in done in JXCWindow or CrossfireServerConnection.
 * @see com.realtime.crossfire.jxclient.JXCWindow
 * @see com.realtime.crossfire.jxclient.server.CrossfireServerConnection
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class jxclient
{
    /**
     * The default skin name.
     */
    public static final String DEFAULT_SKIN = "ragnorok";

    /**
     * The program entry point.
     * @param args The command line arguments.
     */
    public static void main(final String args[])
    {
        System.out.println("JXClient - Crossfire Java Client");
        System.out.println("(C)2005 by Lauwenmark.");
        System.out.println("This software is placed under the GPL License");
        new jxclient(args);
    }

    /**
     * The constructor of the class. This is where the main window is created.
     * Initialization of a JXCWindow is the only task performed here.
     * @param args The command line arguments.
     */
    public jxclient(final String args[])
    {
        try
        {
            final Settings prefs = new Settings(Filenames.getSettingsFile());
            int width = prefs.getInt("width", 1024);
            int height = prefs.getInt("height", 768);
            String skin = prefs.getString("skin", "default");
            boolean fullScreen = true;
            String server = null;
            boolean debugGui = false;
            String debugProtocolFilename = null;

            // fix changed default skin name
            if (skin.equals("com.realtime.crossfire.jxclient.JXCSkinPrelude"))
            {
                skin = "default";
            }

            for (int i = 0; i < args.length; i++)
            {
                if (args[i].equals("-W") && i+1 < args.length)
                {
                    width = Integer.parseInt(args[i+1]);
                    i++;
                }
                else if (args[i].equals("-H") && i+1 < args.length)
                {
                    height = Integer.parseInt(args[i+1]);
                    i++;
                }
                else if (args[i].equals("-S") && i+1 < args.length)
                {
                    skin = args[i+1];
                    i++;
                }
                else if (args[i].equals("-N"))
                {
                    fullScreen = false;
                }
                else if (args[i].equals("--opengl"))
                {
                    System.setProperty("sun.java2d.opengl", "True");
                }
                else if (args[i].equals("--server") && i+1 < args.length)
                {
                    server = args[i+1];
                    i++;
                }
                else if (args[i].equals("--debug-gui"))
                {
                    debugGui = true;
                }
                else if (args[i].equals("--debug-protocol") && i+1 < args.length)
                {
                    debugProtocolFilename = args[i+1];
                    i++;
                }
                else
                {
                    System.out.println("");
                    System.out.println("Available options:");
                    System.out.println(" -N             : Disable full-screen mode;");
                    System.out.println(" -W <size>      : Width of the screen, in pixels;");
                    System.out.println(" -H <size>      : Height of the screen, in pixels;");
                    System.out.println(" -S <skin>      : Skin name to use. [default, prelude, ragnorok]");
                    System.out.println(" --opengl       : Enable the OpenGL rendering pipeline.");
                    System.out.println(" --server <host>: Select a server to connect to; skips main and metaserver");
                    System.out.println("                  windows.");
                    System.out.println(" --debug-gui    : Enable debugging of GUI elements.");
                    System.out.println(" --debug-protocol <log-file>");
                    System.out.println("                : Log messages exchanged with the server.");
                    System.exit(1);
                }
            }
            prefs.putInt("width", width);
            prefs.putInt("height", height);
            prefs.putString("skin", skin);

            // Map "default to actual skin name; must be after skin name has
            // been written to preferences.
            if (skin.equals("default"))
            {
                skin = DEFAULT_SKIN;
            }

            final FileOutputStream debugProtocolFileOutputStream = debugProtocolFilename == null ? null : new FileOutputStream(debugProtocolFilename);
            try
            {
                final OutputStreamWriter debugProtocolOutputStreamWriter = debugProtocolFileOutputStream == null ? null : new OutputStreamWriter(debugProtocolFileOutputStream, "UTF-8");
                try
                {
                    final BufferedWriter debugProtocolBufferedWriter = debugProtocolOutputStreamWriter == null ? null : new BufferedWriter(debugProtocolOutputStreamWriter);
                    try
                    {
                        final JXCWindow jxwin = new JXCWindow(debugGui, debugProtocolBufferedWriter, prefs);
                        try
                        {
                            jxwin.getOptionManager().addOption("sound_enabled", "Whether sound is enabled.", new SoundCheckBoxOption());
                        }
                        catch (final OptionException ex)
                        {
                            throw new AssertionError();
                        }

                        SoundManager.instance = new SoundManager();
                        new StatsWatcher(ItemsList.getStats(), jxwin.getWindowRenderer());
                        new MusicWatcher(jxwin.getCrossfireServerConnection());
                        new SoundWatcher(jxwin.getCrossfireServerConnection());

                        jxwin.init(width, height, skin, fullScreen, server);
                    }
                    finally
                    {
                        if (debugProtocolBufferedWriter != null)
                        {
                            debugProtocolBufferedWriter.close();
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
        }

        System.exit(0);
    }
}

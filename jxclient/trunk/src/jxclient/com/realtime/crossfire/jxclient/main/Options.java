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

import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.util.Resolution;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command line argument parser.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class Options
{
    /**
     * The {@link Settings} for saving/restoring defaults.
     */
    @Nullable
    private Settings prefs = null;

    /**
     * Whether full-screen mode should be enabled.
     */
    private boolean fullScreen = true;

    /**
     * The Crossfire server to connect to or <code>null</code> to show the
     * server selections screen.
     */
    @Nullable
    private String server = null;

    /**
     * Enable debugging of GUI elements.
     */
    private boolean debugGui = false;

    /**
     * The filename for Crossfire protocol message logs or <code>null</code> to
     * not log protocol messages.
     */
    @Nullable
    private String debugProtocolFilename = null;

    /**
     * The filename for keyboard debug logs or <code>null</code> to not log
     * keyboard input.
     */
    @Nullable
    private String debugKeyboardFilename = null;

    /**
     * The resolution to use or <code>null</code> for default.
     */
    @Nullable
    private Resolution resolution = null;

    /**
     * The skin name to load or <code>null</code> for the default skin.
     */
    @Nullable
    private String skin = null;

    /**
     * The default skin name.
     */
    @NotNull
    public static final String DEFAULT_SKIN = "ragnorok";

    /**
     * Parse command line arguments.
     * @param args the command line arguments
     * @throws IOException if an I/O error occurs
     */
    public void parse(@NotNull final String[] args) throws IOException
    {
        prefs = new Settings(Filenames.getSettingsFile());
        resolution = Resolution.parse(false, prefs.getString("resolution", getScreenResolution()));
        skin = prefs.getString("skin", "default");

        // fix changed default skin name
        if (skin.equals("com.realtime.crossfire.jxclient.JXCSkinPrelude"))
        {
            skin = "default";
        }

        int i = 0;
        while (i < args.length)
        {
            if ((args[i].equals("-r") || args[i].equals("--resolution")) && i+1 < args.length)
            {
                resolution = Resolution.parse(true, args[++i]);
                if (resolution == null)
                {
                    System.err.println("Invalid resolution: "+args[i]);
                    System.exit(1);
                }
            }
            else if (args[i].equals("-S") && i+1 < args.length)
            {
                skin = args[++i];
                if (skin.indexOf('@') != -1)
                {
                    System.err.println("Invalid skin name: "+skin);
                    System.exit(1);
                }
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
                server = args[++i];
            }
            else if (args[i].equals("--debug-gui"))
            {
                debugGui = true;
            }
            else if (args[i].equals("--debug-protocol") && i+1 < args.length)
            {
                debugProtocolFilename = args[++i];
            }
            else if (args[i].equals("--debug-keyboard") && i+1 < args.length)
            {
                debugKeyboardFilename = args[++i];
            }
            else
            {
                System.out.println("");
                System.out.println("Available options:");
                System.out.println(" -N             : Disable full-screen mode.");
                System.out.println(" --resolution <width>x<height>");
                System.out.println(" -r <width>x<height>");
                System.out.println("                : Resolution to use. [default is maximum not exceeding screen]");
                System.out.println(" -S <skin>      : Skin name to use.");
                System.out.println(" --opengl       : Enable the OpenGL rendering pipeline.");
                System.out.println(" --server <host>: Select a server to connect to; skips main and metaserver");
                System.out.println("                  windows.");
                System.out.println(" --debug-gui    : Enable debugging of GUI elements.");
                System.out.println(" --debug-keyboard <log-file>");
                System.out.println("                : Log keyboard input.");
                System.out.println(" --debug-protocol <log-file>");
                System.out.println("                : Log messages exchanged with the server.");
                System.out.println("");
                System.out.println("Available skins: default, prelude, ragnorok.");
                System.exit(1);
            }
            i++;
        }
        prefs.putString("resolution", resolution.toString());
        prefs.remove("width"); // delete obsolete entry
        prefs.remove("height"); // delete obsolete entry
        prefs.putString("skin", skin);

        // Map "default to actual skin name; must be after skin name has
        // been written to preferences.
        if (skin.equals("default"))
        {
            skin = DEFAULT_SKIN;
        }
    }

    /**
     * Returns the current screen's resolution.
     * @return the screen's resolution.
     */
    @NotNull
    private static String getScreenResolution()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final DisplayMode displayMode = gd.getDisplayMode();
        return new Resolution(true, displayMode.getWidth(), displayMode.getHeight()).toString();
    }

    /**
     * Returns the filename for Crossfire protocol debug logs.
     * @return the filename or <code>null</code> to not log Crossfire protocol
     * messages
     */
    @Nullable
    public String getDebugProtocolFilename()
    {
        return debugProtocolFilename;
    }

    /**
     * Returns the filename for keyboard debug logs.
     * @return the filename or <code>null</code> to not log keyboard input
     */
    @Nullable
    public String getDebugKeyboardFilename()
    {
        return debugKeyboardFilename;
    }

    /**
     * Returns the {@link Settings} for restoring/saving settings.
     * @return the settings
     */
    @Nullable
    public Settings getPrefs()
    {
        return prefs;
    }

    /**
     * Returns whether debugging of GUI elements is enabled.
     * @return whether debugging of GUI elements is enabled
     */
    public boolean isDebugGui()
    {
        return debugGui;
    }

    /**
     * Returns the resolution.
     * @return the resolution or <code>null</code> for default
     */
    @Nullable
    public Resolution getResolution()
    {
        return resolution;
    }

    /**
     * Returns the skin name.
     * @return the skin name or <code>null</code> for the default skin
     */
    @Nullable
    public String getSkin()
    {
        return skin;
    }

    /**
     * Returns whether full-screen mode should be enabled.
     * @return whether full-screen mode should be enabled
     */
    public boolean isFullScreen()
    {
        return fullScreen;
    }

    /**
     * Returns the Crossfire server to connect to.
     * @return the server or <code>null</code> for interactive server selection
     */
    @Nullable
    public String getServer()
    {
        return server;
    }
}

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
package com.realtime.crossfire.jxclient.main;

import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.skin.Resolution;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

/**
 * Command line argument parser.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class Options
{
    private Settings prefs = null;

    private boolean fullScreen = true;

    private String server = null;

    private boolean debugGui = false;

    private String debugProtocolFilename = null;

    private Resolution resolution = null;

    private String skin = null;

    /**
     * The default skin name.
     */
    public static final String DEFAULT_SKIN = "ragnorok";

    /**
     * Parse command line arguments.
     * @param args the command line arguments
     * @throws IOException if an I/O error occurs
     */
    public void parse(final String[] args) throws IOException
    {
        prefs = new Settings(Filenames.getSettingsFile());
        resolution = Resolution.parse(false, prefs.getString("resolution", getScreenResolution()));
        skin = prefs.getString("skin", "default");

        // fix changed default skin name
        if (skin.equals("com.realtime.crossfire.jxclient.JXCSkinPrelude"))
        {
            skin = "default";
        }

        for (int i = 0; i < args.length; i++)
        {
            if ((args[i].equals("-r") || args[i].equals("--resolution")) && i+1 < args.length)
            {
                resolution = Resolution.parse(true, args[i+1]);
                if (resolution == null)
                {
                    System.err.println("Invalid resolution: "+args[i+1]);
                    System.exit(1);
                }
                i++;
            }
            else if (args[i].equals("-S") && i+1 < args.length)
            {
                skin = args[i+1];
                if (skin.indexOf('@') != -1)
                {
                    System.err.println("Invalid skin name: "+skin);
                    System.exit(1);
                }
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
                System.out.println(" --resolution <width>x<height>");
                System.out.println(" -r <width>x<height>");
                System.out.println("                : Resolution to use [default is maximum not exceeding current]");
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
    public static String getScreenResolution()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final DisplayMode displayMode = gd.getDisplayMode();
        return new Resolution(true, displayMode.getWidth(), displayMode.getHeight()).toString();
    }

    public String getDebugProtocolFilename()
    {
        return debugProtocolFilename;
    }

    public Settings getPrefs()
    {
        return prefs;
    }

    public boolean isDebugGui()
    {
        return debugGui;
    }

    public Resolution getResolution()
    {
        return resolution;
    }

    public String getSkin()
    {
        return skin;
    }

    public boolean isFullScreen()
    {
        return fullScreen;
    }

    public String getServer()
    {
        return server;
    }
}

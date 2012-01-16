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

import com.realtime.crossfire.jxclient.settings.Settings;
import com.realtime.crossfire.jxclient.util.Resolution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command line argument parser.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class Options {

    /**
     * The default size of tiles in the map view in pixels.
     */
    private static final int DEFAULT_TILE_SIZE = 64;

    /**
     * The {@link Settings} for saving/restoring defaults.
     */

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
     * The filename for screen debug logs or <code>null</code> to not log screen
     * logs.
     */
    @Nullable
    private String debugScreenFilename = null;

    /**
     * The filename for sound debug logs or <code>null</code> to not log sound
     * logs.
     */
    @Nullable
    private String debugSoundFilename = null;

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
     * The size of tiles in the map view in pixels.
     */
    private int tileSize = DEFAULT_TILE_SIZE;

    /**
     * The default skin name.
     */
    @NotNull
    public static final String DEFAULT_SKIN = "ragnorok";

    /**
     * Whether map scrolling is done by copying pixel areas. If unset, always
     * repaint all map squares.
     */
    private boolean avoidCopyArea;

    /**
     * Parse command line arguments.
     * @param args the command line arguments
     */
    public void parse(@NotNull final String[] args) {
        resolution = null;
        skin = "default";

        // fix changed default skin name
        if (skin.equals("com.realtime.crossfire.jxclient.JXCSkinPrelude")) {
            skin = "default";
        }

        int i = 0;
        while (i < args.length) {
            if ((args[i].equals("-r") || args[i].equals("--resolution")) && i+1 < args.length) {
                resolution = Resolution.parse(args[++i]);
                if (resolution == null) {
                    System.err.println("Invalid resolution: "+args[i]);
                    System.exit(1);
                }
            } else if ((args[i].equals("-S") || args[i].equals("-s") || args[i].equals("--skin")) && i+1 < args.length) {
                skin = args[++i];
                if (skin.indexOf('@') != -1) {
                    System.err.println("Invalid skin name: "+skin);
                    System.exit(1);
                }
            } else if (args[i].equals("-N") || args[i].equals("-n") || args[i].equals("--no-full-screen")) {
                fullScreen = false;
            } else if (args[i].equals("--opengl")) {
                System.setProperty("sun.java2d.opengl", "True");
            } else if (args[i].equals("--server") && i+1 < args.length) {
                server = args[++i];
            } else if (args[i].equals("--debug-gui")) {
                debugGui = true;
            } else if (args[i].equals("--debug-protocol") && i+1 < args.length) {
                debugProtocolFilename = args[++i];
            } else if (args[i].equals("--debug-keyboard") && i+1 < args.length) {
                debugKeyboardFilename = args[++i];
            } else if (args[i].equals("--debug-screen") && i+1 < args.length) {
                debugScreenFilename = args[++i];
            } else if (args[i].equals("--debug-sound") && i+1 < args.length) {
                debugSoundFilename = args[++i];
            } else if (args[i].equals("--tile-size") && i+1 < args.length) {
                final String tmp = args[++i];
                try {
                    tileSize = Integer.parseInt(tmp);
                } catch (final NumberFormatException ignored) {
                    System.err.println("Invalid tile size: "+tmp);
                    System.exit(1);
                }
                if (tileSize < 1) {
                    System.err.println("Invalid tile size: "+tileSize);
                    System.exit(1);
                }
            } else if (args[i].equals("--avoid-copy-area")) {
                avoidCopyArea = true;
            } else {
                System.out.println("");
                System.out.println("Available options:");
                System.out.println(" --no-full-screen");
                //System.out.println(" -N"); // not advertised as it is considered deprecated
                System.out.println(" -n             : Disable full-screen mode.");
                System.out.println(" --resolution <width>x<height>");
                System.out.println(" -r <width>x<height>");
                System.out.println("                : Resolution to use. [default is maximum not exceeding screen]");
                System.out.println(" --skin <skin>");
                //System.out.println(" -S <skin>"); // not advertised as it is considered deprecated
                System.out.println(" -s <skin>      : Skin name to use.");
                System.out.println(" --tile-size <n>: The size of map view tiles in pixels.");
                System.out.println(" --avoid-copy-area: Do not copy pixel areas when scrolling the map view.");
                System.out.println("                  Instead always repaint all map squares.");
                System.out.println(" --opengl       : Enable the OpenGL rendering pipeline.");
                System.out.println(" --server <host>: Select a server to connect to; skips main and metaserver");
                System.out.println("                  windows.");
                System.out.println(" --debug-gui    : Enable debugging of GUI elements.");
                System.out.println(" --debug-keyboard <log-file>");
                System.out.println("                : Log keyboard input.");
                System.out.println(" --debug-protocol <log-file>");
                System.out.println("                : Log messages exchanged with the server.");
                System.out.println(" --debug-screen <log-file>");
                System.out.println("                : Log messages related to screen resolution.");
                System.out.println(" --debug-sound <log-file>");
                System.out.println("                : Log messages related to sound.");
                System.out.println("");
                System.out.println("Available skins: default, ragnorok.");
                System.exit(1);
            }
            i++;
        }

        // Map "default to actual skin name; must be after skin name has
        // been written to preferences.
        if (skin.equals("default")) {
            skin = DEFAULT_SKIN;
        }
    }

    /**
     * Returns the filename for Crossfire protocol debug logs.
     * @return the filename or <code>null</code> to not log Crossfire protocol
     *         messages
     */
    @Nullable
    public String getDebugProtocolFilename() {
        return debugProtocolFilename;
    }

    /**
     * Returns the filename for keyboard debug logs.
     * @return the filename or <code>null</code> to not log keyboard input
     */
    @Nullable
    public String getDebugKeyboardFilename() {
        return debugKeyboardFilename;
    }

    /**
     * Returns the filename for screen debug logs.
     * @return the filename or <code>null</code> to not log screen logs
     */
    @Nullable
    public String getDebugScreenFilename() {
        return debugScreenFilename;
    }

    /**
     * Returns the filename for sound debug logs.
     * @return the filename or <code>null</code> to not log sound logs
     */
    @Nullable
    public String getDebugSoundFilename() {
        return debugSoundFilename;
    }

    /**
     * Returns whether debugging of GUI elements is enabled.
     * @return whether debugging of GUI elements is enabled
     */
    public boolean isDebugGui() {
        return debugGui;
    }

    /**
     * Returns the resolution.
     * @return the resolution or <code>null</code> for default
     */
    @Nullable
    public Resolution getResolution() {
        return resolution;
    }

    /**
     * Returns the skin name.
     * @return the skin name or <code>null</code> for the default skin
     */
    @Nullable
    public String getSkin() {
        return skin;
    }

    /**
     * Returns the size of a tile in the map view.
     * @return the tile size in pixels
     */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Returns whether full-screen mode should be enabled.
     * @return whether full-screen mode should be enabled
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * Returns the Crossfire server to connect to.
     * @return the server or <code>null</code> for interactive server selection
     */
    @Nullable
    public String getServer() {
        return server;
    }

    /**
     * Returns whether map scrolling is done by copying pixel areas. If unset,
     * always repaint all map squares.
     * @return whether copying pixel areas is disallowed
     */
    public boolean isAvoidCopyArea() {
        return avoidCopyArea;
    }

}

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

package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.gui.commands.ScreenshotFiles;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the "screenshot" command. It write the current window contents
 * into a .png file.
 * @author Andreas Kirschbaum
 */
public class ScreenshotCommand extends AbstractCommand {

    /**
     * The renderer to use.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link ScreenshotFiles} instance for creating screenshot file names.
     */
    @NotNull
    private final ScreenshotFiles screenshotFiles;

    /**
     * Creates a new instance.
     * @param windowRenderer the renderer to use
     * @param crossfireServerConnection the connection instance
     * @param screenshotFiles the screenshot files instance for creating
     * screenshot file names
     */
    public ScreenshotCommand(@NotNull final JXCWindowRenderer windowRenderer, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final ScreenshotFiles screenshotFiles) {
        super("screenshot", crossfireServerConnection);
        this.windowRenderer = windowRenderer;
        this.screenshotFiles = screenshotFiles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allArguments() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NotNull final String args) {
        final File file;
        if (args.length() == 0) {
            try {
                file = screenshotFiles.getFile();
            } catch (final IOException ex) {
                drawInfoError("Failed to create screenshot filename: "+ex.getMessage());
                return;
            }
        } else {
            file = new File(args);
        }

        final BufferedImage image = new BufferedImage(windowRenderer.getWindowWidth(), windowRenderer.getWindowHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics g = image.createGraphics();
        try {
            g.setColor(Color.black);
            g.fillRect(0, 0, windowRenderer.getWindowWidth(), windowRenderer.getWindowHeight());
            windowRenderer.redraw(g);
        } finally {
            g.dispose();
        }
        try {
            ImageIO.write(image, "png", file);
        } catch (final IOException ex) {
            drawInfoError("Cannot write screenshot "+file.getPath()+": "+ex.getMessage());
            return;
        } catch (final NullPointerException ignored) { // ImageIO.write() crashes if the destination cannot be written to
            drawInfoError("Cannot write screenshot "+file.getPath());
            return;
        }

        drawInfo("Saved screenshot to "+file.getPath());
    }

}

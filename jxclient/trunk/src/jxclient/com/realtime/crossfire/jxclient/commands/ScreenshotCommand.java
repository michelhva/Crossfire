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
package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.Filenames;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Implements the "screenshot" command. It write the current window contents
 * into a .png file.
 * @author Andreas Kirschbaum
 */
public class ScreenshotCommand extends AbstractCommand
{
    private static int screenshotId = 0;

    /**
     * The window to execute in.
     */
    private JXCWindow window;

    /**
     * Creates a new instance.
     * @param window The window to execute in.
     * @param crossfireServerConnection the connection instance
     */
    protected ScreenshotCommand(final JXCWindow window, final CrossfireServerConnection crossfireServerConnection)
    {
        super(crossfireServerConnection);
        this.window = window;
    }

    /** {@inheritDoc} */
    public boolean allArguments()
    {
        return true;
    }

    /** {@inheritDoc} */
    public void execute(final String args)
    {
        final File file;
        if (args.length() == 0)
        {
            try
            {
                file = Filenames.getSettingsFile("screenshot"+screenshotId+".png");
            }
            catch (final IOException ex)
            {
                drawInfoError("Failed to create screenshot filename: "+ex.getMessage());
                return;
            }
            screenshotId = (screenshotId+1)%10;
        }
        else
        {
            file = new File(args);
        }

        final BufferedImage image = new BufferedImage(window.getWindowWidth(), window.getWindowHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics2D grfx = image.createGraphics();
        try
        {
            grfx.setColor(Color.black);
            grfx.fillRect(0, 0, window.getWindowWidth(), window.getWindowHeight());
            window.getWindowRenderer().redraw(grfx);
        }
        finally
        {
            grfx.dispose();
        }
        try
        {
            ImageIO.write(image, "png", file);
        }
        catch (final IOException ex)
        {
            drawInfoError("Cannot write screenshot "+file.getPath()+": "+ex.getMessage());
            return;
        }
        catch (final NullPointerException ex) // ImageIO.write() crashes if the destination cannot be written to
        {
            drawInfoError("Cannot write screenshot "+file.getPath());
            return;
        }

        drawInfo("Saved screenshot to "+file.getPath());
    }
}

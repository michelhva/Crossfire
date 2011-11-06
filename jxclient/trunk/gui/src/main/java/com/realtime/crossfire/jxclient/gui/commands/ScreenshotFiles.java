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

package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.settings.Filenames;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * Helper class for creating file names for screenshot files.
 * @author Andreas Kirschbaum
 */
public class ScreenshotFiles {

    /**
     * The number of auto-created screenshot filenames. If more than this number
     * of screenshots are created, old files will be recycled.
     */
    private static final int SCREENSHOT_FILENAMES = 10;

    /**
     * A number for creating screenshot file names. It is incremented for each
     * screenshot.
     */
    private int screenshotId = 0;

    /**
     * Returns a {@link File} for the next screenshot file.
     * @return the file
     * @throws IOException if the file cannot be determined
     */
    @NotNull
    public File getFile() throws IOException {
        final File file = Filenames.getSettingsFile("screenshot"+screenshotId+".png");
        screenshotId = (screenshotId+1)%SCREENSHOT_FILENAMES;
        return file;
    }

}

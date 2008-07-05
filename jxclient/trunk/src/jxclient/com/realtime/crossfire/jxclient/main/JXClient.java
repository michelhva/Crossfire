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

import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.settings.options.SoundCheckBoxOption;
import com.realtime.crossfire.jxclient.sound.SoundManager;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * This is the entry point for JXClient. Note that this class doesn't do much
 * by itself - most of the work in done in JXCWindow or CrossfireServerConnection.
 * @see JXCWindow
 * @see com.realtime.crossfire.jxclient.server.CrossfireServerConnection
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class JXClient
{
    /**
     * The program entry point.
     * @param args The command line arguments.
     */
    public static void main(final String[] args)
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
    public JXClient(final Options options)
    {
        try
        {
            final SoundManager soundManager = new SoundManager();

            final FileOutputStream debugProtocolFileOutputStream = options.getDebugProtocolFilename() == null ? null : new FileOutputStream(options.getDebugProtocolFilename());
            try
            {
                final OutputStreamWriter debugProtocolOutputStreamWriter = debugProtocolFileOutputStream == null ? null : new OutputStreamWriter(debugProtocolFileOutputStream, "UTF-8");
                try
                {
                    final BufferedWriter debugProtocolBufferedWriter = debugProtocolOutputStreamWriter == null ? null : new BufferedWriter(debugProtocolOutputStreamWriter);
                    try
                    {
                        final OptionManager optionManager = new OptionManager(options.getPrefs());
                        final JXCWindow window = new JXCWindow(options.isDebugGui(), debugProtocolBufferedWriter, options.getPrefs(), soundManager, optionManager);
                        try
                        {
                            optionManager.addOption("sound_enabled", "Whether sound is enabled.", new SoundCheckBoxOption(soundManager));
                        }
                        catch (final OptionException ex)
                        {
                            throw new AssertionError();
                        }

                        window.init(options.getResolution(), options.getSkin(), options.isFullScreen(), options.getServer());
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

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
package com.realtime.crossfire.jxclient.sound;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Plays a sound clip.
 *
 * @author Andreas Kirschbaum
 */
public class SoundClip extends Thread
{
    /**
     * The sound to play.
     */
    private final String name;

    /**
     * Create a new instance.
     *
     * @param file The soundfile to play.
     */
    public SoundClip(final String name)
    {
        this.name = name;
        start();
    }

    /** {@inheritDoc} */
    public void run()
    {

        final String resource = "resource/sounds/"+name+".wav";
        final InputStream inputStream = SoundClip.class.getClassLoader().getResourceAsStream(resource);
        if (inputStream == null)
        {
            System.err.println(name+": resource "+resource+" does not exist");
            return;
        }

        final Clip clip;
        try
        {
            final AudioInputStream stream;
            try
            {
                stream = AudioSystem.getAudioInputStream(inputStream);
            }
            catch(final UnsupportedAudioFileException ex)
            {
                System.err.println(name+": "+ex.getMessage());
                return;
            }
            catch(final IOException ex)
            {
                System.err.println(name+": "+ex.getMessage());
                return;
            }

            final DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
            try
            {
                clip = (Clip)AudioSystem.getLine(info);
            }
            catch(final LineUnavailableException ex)
            {
                System.err.println(name+": "+ex.getMessage());
                return;
            }

            // This method does not return until the audio file is completely
            // loaded
            try
            {
                clip.open(stream);
            }
            catch(final LineUnavailableException ex)
            {
                System.err.println(name+": "+ex.getMessage());
                    return;
            }
            catch(final IOException ex)
            {
                System.err.println(name+": "+ex.getMessage());
                return;
            }

            clip.start();
            try
            {
                clip.drain();

                // sleep 100ms; else the clip is cut off at the end
                try
                {
                    Thread.sleep(100);
                }
                catch (final InterruptedException ex)
                {
                    // ignore
                }
            }
            finally
            {
                clip.close();
            }
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (final IOException ex)
            {
                // ignore
            }
        }
    }
}

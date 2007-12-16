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
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Plays background music. At most one background music can be concurrently
 * active (except for fading in/out effects).
 *
 * @author Andreas Kirschbaum
 */
public class MusicManager
{
    /**
     * The currently running processor, or <code>null</code> if none is active
     * or if music is disabled.
     */
    private Processor processor = null;

    /**
     * Whether background music is enabled. (User setting)
     */
    private boolean enabled = false;

    /**
     * Whether background music is muted. (Depends on connection state)
     */
    private boolean muted = true;

    /**
     * The currently playing music name. Set to <code>null</code> if no music
     * is playing.
     */
    private String name = null;

    /**
     * Play the given music. If the new music name is unchanged, continue
     * playing.
     *
     * @param name The music name, or <code>null</code> to stop playing music.
     */
    public void play(final String name)
    {
        if (this.name == null ? name == null : this.name.equals(name))
        {
            return;
        }

        this.name = name;
        restart();
    }

    /**
     * Set whether background music is enabled.
     *
     * @param enabled Whether background music is enabled.
     */
    public void setEnabled(final boolean enabled)
    {
        if (this.enabled == enabled)
        {
            return;
        }

        this.enabled = enabled;
        restart();
    }

    /**
     * Set whether background music is muted.
     *
     * @param muted Whether background music is muted.
     */
    public void setMuted(final boolean muted)
    {
        if (this.muted == muted)
        {
            return;
        }

        this.muted = muted;
        restart();
    }

    /**
     * Restart the current music. Take into account {@link #enabled} and {@link
     * #muted} settings.
     */
    private void restart()
    {
        if (processor != null)
        {
            processor.terminate(enabled, false);
            processor = null;
        }

        if (enabled && !muted && name != null)
        {
            processor = new Processor(name);
            processor.start();
        }
    }

    /**
     * Terminate a playing background music and free resources.
     */
    public void shutdown()
    {
        if (processor != null)
        {
            processor.terminate(false, true);
            processor = null;
        }
    }

    /**
     * A thread that plays a music file over and over until terminated.
     */
    private static class Processor extends Thread
    {
        /**
         * The minimum factor for fading in/out effects.
         */
        private float MIN_VALUE = 1E-3F;

        /**
         * The step for the fading in/out factor. It is multiplied to the
         * current value for each sample.
         */
        private float VOLUME_STEP_PER_SAMPLE = 1.00005F;

        /**
         * The name of the music to play.
         */
        private final String name;

        /**
         * The current state: 0=fade in, 1=playing, 2=fade out, 3=terminate,
         * 4=terminate immediately.
         */
        private int state = 0;

        /**
         * The current volume. It is a factor that is multiplied to all sample
         * values while fading in/out.
         */
        private float volume = MIN_VALUE;

        /**
         * Create a new instance.
         *
         * @param name The music name to play.
         */
        public Processor(final String name)
        {
            this.name = name;
        }

        /**
         * Stop playing music. The music is faded out rather than cut off.
         *
         * @param fadeOut Whether tp fade out the music (<code>true</code>) or
         * to cut it off (<code>false</code>).
         *
         * @param join Whether to wait for thread termination.
         */
        public void terminate(final boolean fadeOut, final boolean join)
        {
            state = fadeOut ? 2 : 4;

            if (join)
            {
                try
                {
                    join();
                }
                catch (final InterruptedException ex)
                {
                    throw new AssertionError();
                }
            }
        }

        /** {@inheritDoc} */
        public void run()
        {
            try
            {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(AudioFileLoader.getInputStream(null, name));
                try
                {
                    final SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioInputStream.getFormat());
                    final AudioFormat audioFormat = sourceDataLine.getFormat();

                    if (audioFormat.getChannels() > 2)
                    {
                        System.err.println("music "+name+": cannot handle more than two channels");
                        return;
                    }
                    if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
                    {
                        System.err.println("music "+name+": encoding must be PCM_SIGNED");
                        return;
                    }
                    if (audioFormat.getSampleSizeInBits() != 16)
                    {
                        System.err.println("music "+name+": sample size must be 16 bits");
                        return;
                    }
                    if (audioFormat.isBigEndian())
                    {
                        System.err.println("music "+name+": cannot handle little endian encoding");
                        return;
                    }

                    sourceDataLine.open(audioInputStream.getFormat());
                    try
                    {
                        sourceDataLine.start();
                        try
                        {
                            final byte[] buf = new byte[8192];
                            while (state < 3 && !isInterrupted())
                            {
                                int len = audioInputStream.read(buf, 0, buf.length);
                                if (len == -1)
                                {
                                    final AudioInputStream newAudioInputStream = AudioSystem.getAudioInputStream(AudioFileLoader.getInputStream(null, name));
                                    if (!newAudioInputStream.getFormat().matches(audioInputStream.getFormat()))
                                    {
                                        newAudioInputStream.close();
                                        System.err.println("music "+name+": file format has changed");
                                        break;
                                    }
                                    final AudioInputStream oldAudioInputStream = audioInputStream;
                                    audioInputStream = newAudioInputStream;
                                    oldAudioInputStream.close();
                                    len = audioInputStream.read(buf, 0, buf.length);
                                    if (len == -1)
                                    {
                                        System.err.println("music "+name+": cannot re-read file");
                                        break;
                                    }
                                }

                                switch (state)
                                {
                                case 0: // fade in
                                    for (int i = 0; i+3 < len; i += 4)
                                    {
                                        volume *= VOLUME_STEP_PER_SAMPLE;
                                        if (volume >= 1F)
                                        {
                                            state = 1;
                                            volume = 1F;
                                            break;
                                        }

                                        convertSample(buf, i);
                                        convertSample(buf, i+2);
                                    }
                                    break;

                                case 1: // play
                                    break;

                                case 2: // fade out
                                    for (int i = 0; i+3 < len; i += 4)
                                    {
                                        volume /= VOLUME_STEP_PER_SAMPLE;
                                        if (volume <= MIN_VALUE)
                                        {
                                            state = 3;
                                            len = i;
                                            break;
                                        }

                                        convertSample(buf, i);
                                        convertSample(buf, i+2);
                                    }
                                    break;

                                default:
                                    throw new AssertionError();
                                }

                                sourceDataLine.write(buf, 0, len);
                            }
                            if (state != 4)
                            {
                                sourceDataLine.drain();
                            }
                        }
                        finally
                        {
                            sourceDataLine.stop();
                        }
                    }
                    finally
                    {
                        sourceDataLine.close();
                    }
                }
                finally
                {
                    audioInputStream.close();
                }
            }
            catch (final IOException ex)
            {
                System.err.println("music "+name+": "+ex.getMessage());
            }
            catch (final LineUnavailableException ex)
            {
                System.err.println("music "+name+": "+ex.getMessage());
            }
            catch (final UnsupportedAudioFileException ex)
            {
                System.err.println("music "+name+": "+ex.getMessage());
            }
        }

        /**
         * Convert one audio sample according to the current {@link #factor}.
         *
         * @param buf The buffer holding the sample.
         *
         * @param i The sample offset.
         */
        private void convertSample(final byte[] buf, final int i)
        {
            final float value = (short)((buf[i]&0xFF)+(buf[i+1]&0xFF)*0x100)*volume;
            final short s = (short)value;
            if (s >= 0)
            {
                buf[i] = (byte)s;
                buf[i+1] = (byte)(s/0x100);
            }
            else
            {
                buf[i] = (byte)s;
                buf[i+1] = (byte)((s+0x10000)/0x100);
            }
        }
    }
}

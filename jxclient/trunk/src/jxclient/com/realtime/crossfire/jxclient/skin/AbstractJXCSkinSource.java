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

package com.realtime.crossfire.jxclient.skin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for {@link JXCSkinSource} implementations.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractJXCSkinSource implements JXCSkinSource
{
    /**
     * Available resolutions for this skin.
     */
    @NotNull
    private final Set<Resolution> resolutions = new HashSet<Resolution>();

    /**
     * Checks that the skin exists and can be accessed.
     * @throws JXCSkinException if the skin does not exist or cannot be loaded
     */
    protected void checkAccess() throws JXCSkinException
    {
        try
        {
            final InputStream is = getInputStream("resolutions");
            try
            {
                final InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                try
                {
                    final BufferedReader br = new BufferedReader(isr);
                    try
                    {
                        for (;;)
                        {
                            final String line = br.readLine();
                            if (line == null)
                            {
                                break;
                            }

                            final Resolution resolution = Resolution.parse(true, line);
                            if (resolution == null)
                            {
                                throw new JXCSkinException(getURI("resolutions")+": invalid resolution '"+line+"' in resolutions file");
                            }
                            resolutions.add(resolution);
                        }
                    }
                    finally
                    {
                        br.close();
                    }
                }
                finally
                {
                    isr.close();
                }
            }
            finally
            {
                is.close();
            }
        }
        catch (final IOException ex)
        {
            throw new JXCSkinException(getURI("resolutions")+": "+ex.getMessage());
        }

        if (resolutions.isEmpty())
        {
            throw new JXCSkinException(getURI("resolutions")+": empty file");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsResolution(@NotNull final Resolution resolution)
    {
        return resolutions.contains(resolution);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Iterator<Resolution> iterator()
    {
        return Collections.unmodifiableSet(resolutions).iterator();
    }
}

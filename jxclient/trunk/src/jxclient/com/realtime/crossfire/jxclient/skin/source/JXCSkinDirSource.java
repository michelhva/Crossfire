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

package com.realtime.crossfire.jxclient.skin.source;

import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link JXCSkinSource} that loads from files.
 *
 * @author Andreas Kirschbaum
 */
public class JXCSkinDirSource extends AbstractJXCSkinSource
{
    /**
     * The base directory.
     */
    @NotNull
    private final File dir;

    /**
     * Create a new instance.
     *
     * @param dir The base directory.
     *
     * @throws JXCSkinException if the skin cannot be loaded
     */
    public JXCSkinDirSource(@NotNull final File dir) throws JXCSkinException
    {
        this.dir = dir;
        checkAccess();
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public InputStream getInputStream(@NotNull final String name) throws IOException
    {
        return new FileInputStream(new File(dir, name));
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getURI(@NotNull final String name)
    {
        return "file:"+new File(dir, name);
    }
}

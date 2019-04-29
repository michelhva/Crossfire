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

package com.realtime.crossfire.jxclient.skin.source;

import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link JXCSkinSource} that loads via the class loader.
 * @author Andreas Kirschbaum
 */
public class JXCSkinClassSource extends AbstractJXCSkinSource {

    /**
     * The base resource name to prepend to all resource names.
     */
    @NotNull
    private final String baseName;

    /**
     * Creates a new instance.
     * @param baseName the base resource name to prepend to all resource names
     * @throws JXCSkinException if the skin cannot be loaded
     */
    public JXCSkinClassSource(@NotNull final String baseName) throws JXCSkinException {
        this.baseName = baseName;
        checkAccess();
    }

    @NotNull
    @Override
    public InputStream getInputStream(@NotNull final String name) throws IOException {
        final InputStream inputStream = getClassLoader().getResourceAsStream(baseName+"/"+name);
        if (inputStream == null) {
            throw new IOException("resource '"+baseName+"/"+name+"' not found");
        }
        return inputStream;
    }

    @NotNull
    @Override
    public String getURI(@NotNull final String name) {
        return "resource:"+baseName+"/"+name;
    }

    /**
     * Returns the {@link ClassLoader} to use.
     * @return the class loader
     */
    @NotNull
    private ClassLoader getClassLoader() {
        final ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader != null) {
            return classLoader;
        }

        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (systemClassLoader != null) {
            return systemClassLoader;
        }

        throw new InternalError("cannot find class loader");
    }

}

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
package com.realtime.crossfire.jxclient.skin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * A {@link JXCSkinLoader} that loads from files.
 *
 * @author Andreas Kirschbaum
 */
public class JXCSkinDirLoader extends JXCSkinLoader
{
    /**
     * The base directory.
     */
    private final File dir;

    /**
     * Create a new instance.
     *
     * @param dir The base directory.
     *
     * @throws JXCSkinException if the skin cannot be loaded
     */
    public JXCSkinDirLoader(final File dir) throws JXCSkinException
    {
        if (dir == null) throw new IllegalArgumentException();
        this.dir = dir;
        checkAccess();
    }

    /** {@inheritDoc} */
    protected InputStream getInputStream(final String name) throws IOException
    {
        return new FileInputStream(new File(dir, name));
    }

    /** {@inheritDoc} */
    protected String getURI(final String name)
    {
        return "file:"+new File(dir, name);
    }
}

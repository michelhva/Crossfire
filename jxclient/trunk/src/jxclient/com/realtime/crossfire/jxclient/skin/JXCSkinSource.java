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

import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for providers of {@link JXCSkin} sources.
 * @author Andreas Kirschbaum
 */
public interface JXCSkinSource extends Iterable<Resolution>
{
    /**
     * Returns an {@link InputStream} for a resource name.
     * @param name the resource name
     * @return the input stream for the resource
     * @throws IOException if the resource cannot be loaded
     */
    @NotNull
    InputStream getInputStream(@NotNull String name) throws IOException;

    /**
     * Returns a description of the location of a resource name.
     * @param name the resource name
     * @return the description of the resource
     */
    @NotNull
    String getURI(@NotNull String name);

    /**
     * Returns whether this source defines a given resolution.
     * @param resolution the resolution
     * @return whether this source defines the resolution
     */
    boolean containsResolution(@NotNull Resolution resolution);
}

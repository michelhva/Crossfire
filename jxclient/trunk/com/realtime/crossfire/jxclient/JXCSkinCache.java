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
package com.realtime.crossfire.jxclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a cache for elements identified by name.
 *
 * @author Andreas Kirschbaum
 */
public class JXCSkinCache<T>
{
    /**
     * The description of this cache used for creating error messages.
     */
    private final String ident;

    /**
     * The cached elements. Maps element name to element. Mapped elements are
     * never <code>null</code>.
     */
    private final Map<String, T> cache = new HashMap<String, T>();

    /**
     * Create a new instance.
     *
     * @param ident The description of this cache used for creating error messages.
     */
    public JXCSkinCache(final String ident)
    {
        if (ident == null) throw new IllegalArgumentException();
        this.ident = ident;
    }

    /**
     * Forget all cached elements.
     */
    public void clear()
    {
        cache.clear();
    }

    /**
     * Add a new element to the cache.
     *
     * @param name The element name to add.
     *
     * @param t The element to add.
     *
     * @throws IOException if the element name is not unique
     */
    public void insert(final String name, final T t) throws IOException
    {
        if (name == null) throw new IllegalArgumentException();
        if (t == null) throw new IllegalArgumentException();

        if (cache.containsKey(name))
        {
            throw new IOException("duplicate "+ident+" name: "+name);
        }

        cache.put(name, t);
    }

    /**
     * Lookup an element by name.
     *
     * @param name The name of the element.
     *
     * @return The element.
     *
     * @throws IOException if no such element exists
     */
    public T lookup(final String name) throws IOException
    {
        final T t = cache.get(name);
        if (t == null)
        {
            throw new IOException("undefined "+ident+" name: "+name);
        }

        return t;
    }
}

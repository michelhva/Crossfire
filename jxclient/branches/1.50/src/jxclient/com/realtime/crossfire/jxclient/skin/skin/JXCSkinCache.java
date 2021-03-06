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

package com.realtime.crossfire.jxclient.skin.skin;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Implements a cache for elements identified by name.
 * @author Andreas Kirschbaum
 */
public class JXCSkinCache<T> implements Iterable<T> {

    /**
     * The description of this cache used for creating error messages.
     */
    @NotNull
    private final String ident;

    /**
     * The cached elements. Maps element name to element. Mapped elements are
     * never <code>null</code>.
     */
    @NotNull
    private final Map<String, T> cache = new LinkedHashMap<String, T>();

    /**
     * Create a new instance.
     * @param ident The description of this cache used for creating error
     * messages.
     */
    public JXCSkinCache(@NotNull final String ident) {
        this.ident = ident;
    }

    /**
     * Forget all cached elements.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Add a new element to the cache.
     * @param name The element name to add.
     * @param t The element to add.
     * @throws JXCSkinException if the element name is not unique
     */
    public void insert(@NotNull final String name, @NotNull final T t) throws JXCSkinException {
        if (cache.containsKey(name)) {
            throw new JXCSkinException("duplicate "+ident+" name: "+name);
        }

        cache.put(name, t);
    }

    /**
     * Lookup an element by name.
     * @param name The name of the element.
     * @return The element.
     * @throws JXCSkinException if no such element exists
     */
    @NotNull
    public T lookup(@NotNull final String name) throws JXCSkinException {
        final T t = cache.get(name);
        if (t == null) {
            throw new JXCSkinException("undefined "+ident+" name: "+name);
        }

        return t;
    }

    /**
     * Return all stored values.
     * @return An iterator returning all stored values.
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableCollection(cache.values()).iterator();
    }

}

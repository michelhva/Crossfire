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

/**
 * Abstract base class for {@link JXCSkinSource} implementations.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractJXCSkinSource implements JXCSkinSource {

    /**
     * Checks that the skin exists and can be accessed.
     * @throws JXCSkinException if the skin does not exist or cannot be loaded
     */
    protected void checkAccess() throws JXCSkinException {
        try {
            getInputStream("global.skin").close();
        } catch (final IOException ex) {
            throw new JXCSkinException(getURI("global.skin")+": "+ex.getMessage());
        }
    }

}

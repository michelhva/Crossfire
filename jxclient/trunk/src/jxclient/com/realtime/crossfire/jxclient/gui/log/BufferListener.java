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

package com.realtime.crossfire.jxclient.gui.log;

import java.util.EventListener;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners for changes of {@link Buffer} contents.
 * @author Andreas Kirschbaum
 */
public interface BufferListener extends EventListener {

    /**
     * A line has been added to the buffer.
     */
    void lineAdded();

    /**
     * A line has been replaced at the end of the buffer.
     */
    void lineReplaced();

    /**
     * Some lines have been removed from the buffer.
     * @param lines the lines that have been removed from the beginning of the
     * buffer
     */
    void linesRemoved(@NotNull List<Line> lines);

}

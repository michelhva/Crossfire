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

package com.realtime.crossfire.jxclient.gui;

import java.awt.event.InputEvent;

/**
 * Helper functions for keyboard modifiers.
 * @author Andreas Kirschbaum
 */
public class Modifiers {

    /**
     * The mask for "no modifier".
     */
    public static final int NONE = 0;

    /**
     * The mask for "shift".
     */
    public static final int SHIFT = InputEvent.SHIFT_DOWN_MASK;

    /**
     * The mask for "ctrl".
     */
    public static final int CTRL = InputEvent.CTRL_DOWN_MASK;

    /**
     * The mask for "alt_graph".
     */
    public static final int ALT_GRAPH = InputEvent.ALT_GRAPH_DOWN_MASK;

    /**
     * The mask for all used modifiers.
     */
    public static final int MASK = SHIFT|CTRL|ALT_GRAPH;

    /**
     * Private constructor to prevent instantiation.
     */
    private Modifiers() {
    }

}

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

package com.realtime.crossfire.jxclient.gui.gui;

/**
 * All gui states of {@link JXCWindowRenderer}.
 * @author Andreas Kirschbaum
 */
public enum RendererGuiState {

    /**
     * The start screen is active.
     */
    START,

    /**
     * The server selection screen is active.
     */
    META,

    /**
     * The login screen is active.
     */
    LOGIN,

    /**
     * The new character creation screen is active.
     */
    NEW_CHAR,

    /**
     * The account management screen is active.
     */
    ACCOUNT,

    /**
     * The playing screen is active.
     */
    PLAYING

}

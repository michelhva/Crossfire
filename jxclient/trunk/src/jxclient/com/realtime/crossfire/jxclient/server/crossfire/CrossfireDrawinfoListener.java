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

package com.realtime.crossfire.jxclient.server.crossfire;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public interface CrossfireDrawinfoListener
{
    int NDI_BLACK = 0;

    int NDI_WHITE = 1;

    int NDI_NAVY = 2;

    int NDI_RED = 3;

    int NDI_ORANGE = 4;

    int NDI_BLUE = 5;

    int NDI_DK_ORANGE = 6;

    int NDI_GREEN = 7;

    int NDI_LT_GREEN = 8;

    int NDI_GREY = 9;

    int NDI_BROWN = 10;

    int NDI_GOLD = 11;

    int NDI_TAN = 12;

    void commandDrawinfoReceived(@NotNull String text, int type);
}

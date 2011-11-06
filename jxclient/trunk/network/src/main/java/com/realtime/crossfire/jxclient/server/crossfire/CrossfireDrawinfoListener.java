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

package com.realtime.crossfire.jxclient.server.crossfire;

import java.util.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners interested in drawinfo messages received from the
 * Crossfire server.
 * @author Lauwenmark
 */
public interface CrossfireDrawinfoListener extends EventListener {

    /**
     * A message color.
     */
    int NDI_BLACK = 0;

    /**
     * A message color.
     */
    int NDI_WHITE = 1;

    /**
     * A message color.
     */
    int NDI_NAVY = 2;

    /**
     * A message color.
     */
    int NDI_RED = 3;

    /**
     * A message color.
     */
    int NDI_ORANGE = 4;

    /**
     * A message color.
     */
    int NDI_BLUE = 5;

    /**
     * A message color.
     */
    int NDI_DK_ORANGE = 6;

    /**
     * A message color.
     */
    int NDI_GREEN = 7;

    /**
     * A message color.
     */
    int NDI_LT_GREEN = 8;

    /**
     * A message color.
     */
    int NDI_GREY = 9;

    /**
     * A message color.
     */
    int NDI_BROWN = 10;

    /**
     * A message color.
     */
    int NDI_GOLD = 11;

    /**
     * A message color.
     */
    int NDI_TAN = 12;

    /**
     * A drawinfo message has been received.
     * @param text the message text
     * @param type the message type
     */
    void commandDrawinfoReceived(@NotNull String text, int type);

}

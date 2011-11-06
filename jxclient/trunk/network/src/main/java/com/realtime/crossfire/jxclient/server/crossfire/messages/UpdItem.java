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

package com.realtime.crossfire.jxclient.server.crossfire.messages;

/**
 * Interface defining constants for the "upditem" Crossfire protocol message.
 * @author Andreas Kirschbaum
 */
public interface UpdItem {

    /**
     * The update flags value for location updates.
     */
    int UPD_LOCATION = 0x01;

    /**
     * The update flags value for flags updates.
     */
    int UPD_FLAGS = 0x02;

    /**
     * The update flags value for weight updates.
     */
    int UPD_WEIGHT = 0x04;

    /**
     * The update flags value for face updates.
     */
    int UPD_FACE = 0x08;

    /**
     * The update flags value for name updates.
     */
    int UPD_NAME = 0x10;

    /**
     * The update flags value for animation updates.
     */
    int UPD_ANIM = 0x20;

    /**
     * The update flags value for animation speed updates.
     */
    int UPD_ANIMSPEED = 0x40;

    /**
     * The update flags value for nrof updates.
     */
    int UPD_NROF = 0x80;

}

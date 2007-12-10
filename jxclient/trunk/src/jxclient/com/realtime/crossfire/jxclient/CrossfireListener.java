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

import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawextinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandQueryEvent;
import com.realtime.crossfire.jxclient.stats.CrossfireCommandStatsEvent;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public interface CrossfireListener
{
    void commandImageReceived(CrossfireCommandImageEvent evt);

    void commandGoodbyeReceived(CrossfireCommandGoodbyeEvent evt);

    void commandAddmeSuccessReceived(CrossfireCommandAddmeSuccessEvent evt);

    void commandFaceReceived(CrossfireCommandFaceEvent evt);

    void commandItemReceived(CrossfireCommandItemEvent evt);

    void commandMapReceived(CrossfireCommandMapEvent evt);

    void commandNewmapReceived(CrossfireCommandNewmapEvent evt);

    void commandPlayerReceived(CrossfireCommandPlayerEvent evt);

    void commandStatsReceived(CrossfireCommandStatsEvent evt);

    void commandMapscrollReceived(CrossfireCommandMapscrollEvent evt);

    void commandDrawinfoReceived(CrossfireCommandDrawinfoEvent evt);

    void commandDrawextinfoReceived(CrossfireCommandDrawinfoEvent evt);

    void commandAnimReceived(CrossfireCommandAnimEvent evt);

    void commandVersionReceived(CrossfireCommandVersionEvent evt);

    void commandMagicmapReceived(CrossfireCommandMagicmapEvent evt);

    void commandAddmeFailedReceived(CrossfireCommandAddmeFailedEvent evt);

    void commandSetupReceived(CrossfireCommandSetupEvent evt);

    void commandQueryReceived(CrossfireCommandQueryEvent evt);
}

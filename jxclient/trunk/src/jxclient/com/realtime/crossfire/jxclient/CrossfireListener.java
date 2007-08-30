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

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public interface CrossfireListener
{
    public void commandImageReceived(CrossfireCommandImageEvent evt);

    public void commandGoodbyeReceived(CrossfireCommandGoodbyeEvent evt);

    public void commandAddmeSuccessReceived(CrossfireCommandAddmeSuccessEvent evt);

    public void commandFaceReceived(CrossfireCommandFaceEvent evt);

    public void commandItemReceived(CrossfireCommandItemEvent evt);

    public void commandMapReceived(CrossfireCommandMapEvent evt);

    public void commandNewmapReceived(CrossfireCommandNewmapEvent evt);

    public void commandPlayerReceived(CrossfireCommandPlayerEvent evt);

    public void commandStatsReceived(CrossfireCommandStatsEvent evt);

    public void commandMapscrollReceived(CrossfireCommandMapscrollEvent evt);

    public void commandDrawinfoReceived(CrossfireCommandDrawinfoEvent evt);

    public void commandDrawextinfoReceived(CrossfireCommandDrawinfoEvent evt);

    public void commandAnimReceived(CrossfireCommandAnimEvent evt);

    public void commandVersionReceived(CrossfireCommandVersionEvent evt);

    public void commandMagicmapReceived(CrossfireCommandMagicmapEvent evt);

    public void commandAddmeFailedReceived(CrossfireCommandAddmeFailedEvent evt);

    public void commandSetupReceived(CrossfireCommandSetupEvent evt);

    public void commandQueryReceived(CrossfireCommandQueryEvent evt);
}

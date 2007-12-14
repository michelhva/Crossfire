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
package com.realtime.crossfire.jxclient.skin;

import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.util.Iterator;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public interface JXCSkin extends Iterable<Gui>
{
    void load(CrossfireServerConnection s, JXCWindow p) throws JXCSkinException;

    /**
     * Return all gui instances of this skin. The instances has no defined
     * order.
     *
     * @return An iterator returning all gui instances.
     */
    Iterator<Gui> iterator();

    /**
     * The "really quit?" dialog. It is opened when the user presses ESCAPE.
     *
     * @return The dialog, or <code>null</code> if the skin does not define
     * this dialog.
     */
    Gui getDialogQuit();

    /**
     * The "disconnect from server?" dialog. It is opened when the user presses
     * ESCAPE.
     *
     * @return The dialog, or <code>null</code> if the skin does not define
     * this dialog.
     */
    Gui getDialogDisconnect();

    Gui getDialogKeyBind();
    Gui getDialogQuery();
    Gui getDialogBook(int booknr);
    Gui getMainInterface();
    Gui getMetaInterface();
    Gui getStartInterface();

    /**
     * Execute the "event init" commands.
     */
    void executeInitEvents();

    /**
     * Return a named command list.
     *
     * @param name The name of the command list.
     *
     * @return The command list.
     *
     * @throws JXCSkinException If the command list does not exist.
     */
    GUICommandList getCommandList(String name) throws JXCSkinException;
}

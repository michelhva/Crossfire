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
package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.skin.JXCSkin;
import com.realtime.crossfire.jxclient.skin.JXCSkinException;

/**
 * Implements a "script" command. It runs a new script.
 *
 * @author Andreas Kirschbaum
 */
public class ExecCommand implements Command
{
    /** {@inheritDoc} */
    public void execute(final String args, final JXCWindow window)
    {
        if (args.length() == 0)
        {
            window.getCrossfireServerConnection().drawInfo("Which command to you want to run?", 3);
            return;
        }

        final JXCSkin skin = window.getSkin();
        final GUICommandList commandList;
        try
        {
            commandList = skin.getCommandList(args);
        }
        catch (final JXCSkinException ex)
        {
            window.getCrossfireServerConnection().drawInfo(ex.getMessage(), 3);
            return;
        }
        commandList.execute();
    }
}

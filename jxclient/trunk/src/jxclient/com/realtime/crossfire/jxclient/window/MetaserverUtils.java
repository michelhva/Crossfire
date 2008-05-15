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
package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.gui.AbstractButton;
import com.realtime.crossfire.jxclient.gui.GUIMetaElement;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import java.util.Set;

/**
 * Utility class for metaserver dialog.
 * @author Andreas Kirschbaum
 */
public class MetaserverUtils
{
    /**
     * Private constructor to prevent instantiation.
     */
    private MetaserverUtils()
    {
    }

    public static void selectEntry(final Gui gui, final Metaserver metaserver, final String serverName)
    {
        final int metaIndex = metaserver.getServerIndex(serverName);
        if (metaIndex == -1)
        {
            return;
        }

        boolean scrollUp = false;
        boolean scrollDown = false;
        for (int i = 0; i < 100; i++)
        {
            final Set<GUIMetaElement> metaElements = gui.getMetaElements();
            for (final GUIMetaElement metaElement : metaElements)
            {
                final int thisIndex = metaElement.getIndex();
                if (thisIndex == metaIndex)
                {
                    metaElement.setActive(true);
                    return;
                }

                if (thisIndex < metaIndex)
                {
                    scrollDown = true;
                }
                else
                {
                    scrollUp = true;
                }
            }

            final String buttonName;
            if (scrollUp && !scrollDown)
            {
                buttonName = "metaup";
            }
            else if (scrollDown && !scrollUp)
            {
                buttonName = "metadown";
            }
            else
            {
                return;
            }

            final AbstractButton button = gui.getButton(buttonName);
            if (button == null || !button.canExecute())
            {
                return;
            }

            button.execute();
        }
    }
}

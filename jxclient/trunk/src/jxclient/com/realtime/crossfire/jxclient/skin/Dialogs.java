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

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import com.realtime.crossfire.jxclient.window.MouseTracker;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Maintains a set of {@link Gui} instances representing dialog windows. These
 * dialogs can be looked up by name. Also maintains a set of dialogs pending
 * loading.
 * @author Andreas Kirschbaum
 */
public class Dialogs implements Iterable<Gui>
{
    /**
     * The existing dialogs.
     */
    private final JXCSkinCache<Gui> dialogs = new JXCSkinCache<Gui>("dialog");

    /**
     * Names of pending skin files.
     */
    private final Set<String> dialogsToLoad = new HashSet<String>();

    /**
     * Forgets about all dialogs.
     */
    public void clear()
    {
        dialogs.clear();
        dialogsToLoad.clear();
    }

    /**
     * Returns a dialog instance by dialog name.
     * @param name the dialog name
     * @return the dialog instance
     * @throws JXCSkinException if the dialog does not exist
     */
    public Gui lookup(final String name) throws JXCSkinException
    {
        return dialogs.lookup(name);
    }

    /**
     * Creates a new dialog instance.
     * @param name the dialog's name
     * @param window the window to attach to
     * @param mouseTracker the mouse tracker to attach
     * @param commands the commands instance to use
     * @return the new dialog instance
     */
    public Gui addDialog(final String name, final JXCWindow window, final MouseTracker mouseTracker, final Commands commands)
    {
        try
        {
            return dialogs.lookup(name);
        }
        catch (final JXCSkinException ex)
        {
            final Gui gui = new Gui(window, mouseTracker, commands);
            try
            {
                dialogs.insert(name, gui);
            }
            catch (final JXCSkinException ex2)
            {
                throw new AssertionError();
            }
            dialogsToLoad.add(name);
            return gui;
        }
    }

    /**
     * Returns one dialog pending loading. Each dialog is returned only once.
     * @return a dialog pending loading or <code>null</code>
     */
    public String getDialogToLoad()
    {
        final Iterator<String> it = dialogsToLoad.iterator();
        if (!it.hasNext())
        {
            return null;
        }

        final String result = it.next();
        it.remove();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Gui> iterator()
    {
        return dialogs.iterator();
    }
}

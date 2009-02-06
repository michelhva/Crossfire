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
import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import com.realtime.crossfire.jxclient.window.MouseTracker;
import java.util.Iterator;

/**
 * Defines a JXClient skin consisting of a main {@link Gui} and zero or more
 * dialog {@link Gui}s.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public interface JXCSkin extends Iterable<Gui>
{
    /**
     * Loads the skin from its external representation.
     * @param skinSource the source to load from
     * @param crossfireServerConnection the server connection to attach to
     * @param window the window to use
     * @param mouseTracker the mouse tracker to use
     * @param metaserverModel the metaserver mode to use
     * @param commandQueue the command queue to use
     * @param resolution the preferred screen resolution
     * @param optionManager the option manager to use
     * @param experienceTable the experience table to use
     * @param shortcuts the shortcuts to use
     * @param commands the commands instance to use
     * @param currentSpellManager the current spell manager to use
     * @throws JXCSkinException if the skin cannot be loaded
     */
    void load(JXCSkinSource skinSource, CrossfireServerConnection crossfireServerConnection, JXCWindow window, MouseTracker mouseTracker, MetaserverModel metaserverModel, CommandQueue commandQueue, Resolution resolution, OptionManager optionManager, ExperienceTable experienceTable, Shortcuts shortcuts, Commands commands, CurrentSpellManager currentSpellManager) throws JXCSkinException;

    /**
     * Returns a short name for the skin. It is used to construct path or file
     * names.
     * @return the skin name
     */
    String getSkinName();

    /**
     * Returns the resolution of this skin.
     * @return the resolution
     */
    Resolution getResolution();

    /**
     * Returns the map width in tiles.
     * @return the map width in tiles
     */
    int getMapWidth();

    /**
     * Returns the map height in tiles.
     * @return the map height in tiles
     */
    int getMapHeight();

    /**
     * The number of ground view objects to request from the server.
     * @return the number of ground view objects
     */
    int getNumLookObjects();

    /**
     * Returns all gui instances of this skin. The instances has no defined
     * order.
     * @return an iterator returning all gui instances
     */
    @Override
    Iterator<Gui> iterator();

    /**
     * Returns the "really quit?" dialog. It is opened when the user presses
     * ESCAPE.
     * @return the dialog or <code>null</code> if the dialog does not exist
     */
    Gui getDialogQuit();

    /**
     * Returns the "disconnect from server?" dialog. It is opened when the user
     * presses ESCAPE.
     * @return the dialog or <code>null</code> if the dialog does not exist
     */
    Gui getDialogDisconnect();

    /**
     * Returns the key bindings dialog.
     * @return the dialog
     */
    Gui getDialogKeyBind();

    /**
     * Returns the dialog for query text input.
     * @return the dialog
     */
    Gui getDialogQuery();

    /**
     * Returns the popup dialog for readables.
     * @param booknr the book ID
     * @return the dialog
     */
    Gui getDialogBook(int booknr);

    /**
     * Returns the main window.
     * @return the dialog
     */
    Gui getMainInterface();

    /**
     * Returns the server selection window.
     * @return the dialog
     */
    Gui getMetaInterface();

    /**
     * Returns the start window.
     * @return the dialog
     */
    Gui getStartInterface();

    /**
     * Returns a dialog by name.
     * @param name the dialog's name
     * @return the dialog
     * @throws JXCSkinException if the dialog does not exist
     */
    Gui getDialog(final String name) throws JXCSkinException;

    /**
     * Executes the "event init" commands.
     */
    void executeInitEvents();

    /**
     * Returns a named command list.
     * @param name the name of the command list
     * @return the command list
     * @throws JXCSkinException if the command list does not exist
     */
    GUICommandList getCommandList(String name) throws JXCSkinException;

    /**
     * Returns whether the dialog state should be saved.
     * @return whether the dialog state should be saved
     */
    boolean hasChangedDialog();

    /**
     * Returns the default key bindings for this skin.
     * @return the default key bindings
     */
    KeyBindings getDefaultKeyBindings();
}

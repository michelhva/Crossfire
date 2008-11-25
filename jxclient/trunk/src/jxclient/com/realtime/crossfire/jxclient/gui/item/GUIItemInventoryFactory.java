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

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 * A factory for creating {@link GUIItemInventory} instances.
 * @author Andreas Kirschbaum
 */
public class GUIItemInventoryFactory
{
    private final JXCWindow window;

    private final CommandQueue commandQueue;

    private final String name;

    private final BufferedImage cursedImage;

    private final BufferedImage damnedImage;

    private final BufferedImage magicImage;

    private final BufferedImage blessedImage;

    private final BufferedImage appliedImage;

    private final BufferedImage selectorImage;

    private final BufferedImage lockedImage;

    private final BufferedImage unpaidImage;

    private final Color cursedColor;

    private final Color damnedColor;

    private final Color magicColor;

    private final Color blessedColor;

    private final Color appliedColor;

    private final Color selectorColor;

    private final Color lockedColor;

    private final Color unpaidColor;

    private final CrossfireServerConnection crossfireServerConnection;

    private final FacesManager facesManager;

    private final ItemsManager itemsManager;

    private final Font font;

    private final Color nrofColor;

    /**
     * Creates a new instance.
     */
    public GUIItemInventoryFactory(final JXCWindow window, final CommandQueue commandQueue, final String name, final BufferedImage cursedImage, final BufferedImage damnedImage, final BufferedImage magicImage, final BufferedImage blessedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final BufferedImage unpaidImage, final Color cursedColor, final Color damnedColor, final Color magicColor, final Color blessedColor, final Color appliedColor, final Color selectorColor, final Color lockedColor, final Color unpaidColor, final CrossfireServerConnection crossfireServerConnection, final FacesManager facesManager, final ItemsManager itemsManager, final Font font, final Color nrofColor)
    {
        this.window = window;
        this.commandQueue = commandQueue;
        this.name = name;
        this.cursedImage = cursedImage;
        this.damnedImage = damnedImage;
        this.magicImage = magicImage;
        this.blessedImage = blessedImage;
        this.appliedImage = appliedImage;
        this.selectorImage = selectorImage;
        this.lockedImage = lockedImage;
        this.unpaidImage = unpaidImage;
        this.cursedColor = cursedColor;
        this.damnedColor = damnedColor;
        this.magicColor = magicColor;
        this.blessedColor = blessedColor;
        this.appliedColor = appliedColor;
        this.selectorColor = selectorColor;
        this.lockedColor = lockedColor;
        this.unpaidColor = unpaidColor;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.itemsManager = itemsManager;
        this.font = font;
        this.nrofColor = nrofColor;
    }

    /**
     * Creates a new {@link GUIItemInventory} instance.
     * @param index the item inventory's index
     * @return the new instance
     */
    public GUIItemInventory newItemInventory(final int index)
    {
        return new GUIItemInventory(window, commandQueue, name+index, 0, 0, 1, 1, cursedImage, damnedImage, magicImage, blessedImage, appliedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, selectorColor, lockedColor, unpaidColor, index, crossfireServerConnection, facesManager, itemsManager, font, nrofColor);
    }

    /**
     * Creates a new {@link GUIItemInventory} instance.
     * @param cellHeight the cell size
     * @return the new instance
     */
    public GUIItemInventory newTemplateItemInventory(final int cellHeight)
    {
        return new GUIItemInventory(window, commandQueue, name+"_template", 0, 0, cellHeight, cellHeight, cursedImage, damnedImage, magicImage, blessedImage, appliedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, selectorColor, lockedColor, unpaidColor, -1, crossfireServerConnection, facesManager, itemsManager, font, nrofColor);
    }
}

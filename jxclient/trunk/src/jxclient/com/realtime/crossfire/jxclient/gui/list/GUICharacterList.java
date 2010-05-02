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
 * Copyright (C) 2010 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.account.CharacterInformation;
import com.realtime.crossfire.jxclient.account.CharacterInformationListener;
import com.realtime.crossfire.jxclient.account.CharacterListener;
import com.realtime.crossfire.jxclient.account.CharacterModel;
import com.realtime.crossfire.jxclient.gui.GUICharacter;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import java.awt.Font;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIList} display characters of an account.
 * @author Nicolas Weeger
 */
public class GUICharacterList extends GUIList {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The model to display characters from.
     */
    @NotNull
    private final CharacterModel characterModel;

    /**
     * The {@link TooltipManager} to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to use.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * The name of this element.
     */
    @NotNull
    private final String name;

    /**
     * The image for drawing list entries.
     */
    @Nullable
    private final BufferedImage image;

    /**
     * The font for drawing list entries.
     */
    @NotNull
    private final Font font;

    /**
     * The tooltip format for drawing list entries.
     */
    @NotNull
    private final String tooltip;

    private int selectedIndex;

    /**
     * The {@link CharacterInformationListener} attached to all tracked
     * character models. It detects changed contents and updates the list
     * accordingly.
     */
    @NotNull
    private final CharacterInformationListener characterInformationListener = new CharacterInformationListener() {
        /** {@inheritDoc} */
        @Override
        public void informationChanged() {
            setChanged();
        }
    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param extent the extent of this element
     * @param cellWidth the width of cells
     * @param cellHeight the height of cells
     * @param image picture to display for the item
     * @param font font to display with
     * @param tooltip how to format the tooltip
     * @param characterModel what to list characters of
     */
    public GUICharacterList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Extent extent, final int cellWidth, final int cellHeight, @Nullable final BufferedImage image, @NotNull final Font font, @NotNull final String tooltip, @NotNull final CharacterModel characterModel) {
        super(tooltipManager, elementListener, name, extent, cellWidth, cellHeight, new CharacterCellRenderer(new GUICharacter(tooltipManager, elementListener, name + "_template", 50, 20, image, font, cellWidth, "", tooltip, characterModel)), null);
        this.characterModel = characterModel;
        this.characterModel.addMetaserverListener(new CharacterListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void numberOfItemsChanged() {
                rebuildList();
            }

        });
        this.tooltip = tooltip;
        this.tooltipManager = tooltipManager;
        this.font = font;
        this.image = image;
        this.elementListener = elementListener;
        this.name = name;
    }

    @Override
    protected void selectionChanged(final int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    protected void updateTooltip(final int index, final int x, final int y, final int w, final int h) {

    }

    @Override
    protected void activeChanged() {

    }

    /**
     * Rebuild the list cells.
     */
    private void rebuildList() {
        synchronized (getTreeLock()) {
            final int newSize = characterModel.size();
            final int oldSize = resizeElements(newSize);
            if (oldSize < newSize) {
                for (int i = oldSize; i < newSize; i++) {
                    final GUIElement metaElement = new GUICharacter(tooltipManager, elementListener, name+i, 1, 1, image, font, i, "", tooltip, characterModel);
                    addElement(metaElement);
                    characterModel.addMetaserverEntryListener(i, characterInformationListener);
                }
            } else {
                for (int i = newSize; i < oldSize; i++) {
                    characterModel.removeMetaserverEntryListener(i, characterInformationListener);
                }
            }
        }
        setChanged();
    }

    /**
     * Get the currently selected character in the list.
     * @return null if invalid index, else the character information.
     */
    @Nullable
    public CharacterInformation getCurrentCharacter() {
        return characterModel.getEntry(selectedIndex);
    }
}

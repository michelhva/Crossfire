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
import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.GUICharacter;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Font;
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
     * The {@link FacesManager} to use to display faces.
     */
    @NotNull
    private final FacesManager facesManager;

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
     * The font for drawing list entries.
     */
    @NotNull
    private final Font font;

    /**
     * The currently selected list index.
     */
    private int selectedIndex;

    /**
     * The {@link CharacterInformationListener} attached to all tracked
     * character models. It detects changed contents and updates the list
     * accordingly.
     */
    @NotNull
    private final CharacterInformationListener characterInformationListener = new CharacterInformationListener() {

        @Override
        public void informationChanged() {
            setChanged();
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param facesManager the faces to use to display
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of cells
     * @param cellHeight the height of cells
     * @param font font to display with
     * @param characterModel what to list characters of
     */
    public GUICharacterList(@NotNull final TooltipManager tooltipManager, @NotNull final FacesManager facesManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final Font font, @NotNull final CharacterModel characterModel) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, new CharacterCellRenderer(new GUICharacter(tooltipManager, facesManager, elementListener, name+"_template", 50, 20, font, 0, characterModel)), null);
        this.characterModel = characterModel;
        this.facesManager = facesManager;
        this.facesManager.addFacesManagerListener(new FacesManagerListener() {

            @Override
            public void faceUpdated(@NotNull final Face face) {
                if (characterModel.displaysFace(face.getFaceNum())) {
                    repaint();
                }
            }

        });
        this.characterModel.addCharacterListener(new CharacterListener() {

            @Override
            public void numberOfItemsChanged() {
                rebuildList();
            }

        });
        this.tooltipManager = tooltipManager;
        this.font = font;
        this.elementListener = elementListener;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void selectionChanged(final int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateTooltip(final int index, final int x, final int y, final int w, final int h) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void activeChanged() {
    }

    /**
     * Rebuilds the list cells.
     */
    private void rebuildList() {
        synchronized (getTreeLock()) {
            final int newSize = characterModel.size();
            final int oldSize = resizeElements(newSize);
            if (oldSize < newSize) {
                for (int i = oldSize; i < newSize; i++) {
                    final GUIElement metaElement = new GUICharacter(tooltipManager, facesManager, elementListener, name+i, 1, 1, font, i, characterModel);
                    addElement(metaElement);
                    characterModel.addCharacterInformationListener(i, characterInformationListener);
                }
            } else {
                for (int i = newSize; i < oldSize; i++) {
                    characterModel.removeCharacterInformationListener(i, characterInformationListener);
                }
            }
        }
        setChanged();
    }

    /**
     * Returns the currently selected character in the list.
     * @return <code>null</code> if invalid index, else the character
     *         information
     */
    @Nullable
    public CharacterInformation getCurrentCharacter() {
        return characterModel.getEntry(selectedIndex);
    }

    /**
     * Selects an entry by character name.
     * @param characterName the character name
     */
    public void setCharacter(@NotNull final String characterName) {
        setSelectedIndex(characterModel.getCharacterIndex(characterName));
    }

}

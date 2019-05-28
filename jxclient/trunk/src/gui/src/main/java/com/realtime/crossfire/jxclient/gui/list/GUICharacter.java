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
import com.realtime.crossfire.jxclient.account.CharacterModel;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Transparency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A character displaying GUI element.
 * @author Nicolas Weeger
 */
public class GUICharacter extends ActivatableGUIElement implements GUIScrollable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link FacesManager} to use to display faces.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * Character model to display items from.
     */
    private final CharacterModel characterModel;

    /**
     * Index of the item to display in {@link #characterModel}.
     */
    private int index;

    /**
     * The font to use.
     */
    @NotNull
    private final Font font;

    /**
     * If set, paint the element in "selected" state.
     */
    private boolean selected;

    /**
     * All listeners to the entry itself.
     */
    @NotNull
    private final CharacterInformationListener characterInformationListener = () -> {
        setChanged();
        updateTooltip();
    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param facesManager the faces to use to display
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param font the font to use
     * @param defaultIndex the initial metaserver index
     * @param characterModel the character model to monitor
     * @param guiFactory the global GUI factory instance
     */
    public GUICharacter(@NotNull final TooltipManager tooltipManager, @NotNull final FacesManager facesManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int w, final int h, @NotNull final Font font, final int defaultIndex, final CharacterModel characterModel, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT, guiFactory);
        setSize(w, h);
        this.facesManager = facesManager;
        this.characterModel = characterModel;
        this.font = font;
        index = defaultIndex;
    }

    @Override
    protected void activeChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 0.0f));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(font);
        g.setColor(isActive() || selected ? Color.RED : Color.GRAY);
        final CharacterInformation character = characterModel.getEntry(index);
        if (character == null) {
            return;
        }

        /**
         * @todo improve, make skin-based ; also getOriginalImageIcon can
         * spit warnings about undefined face, fix somehow.
         */
        final int y = (getHeight()+font.getSize())/2;
        int x = 0;
        g.drawImage(facesManager.getOriginalImageIcon(character.getFaceNumber(), null).getImage(), x, (getHeight()-facesManager.getOriginalImageIcon(character.getFaceNumber(), null).getImage().getHeight(null))/2, this);
        x += 40;
        g.drawString(character.getName(), x, y);
        x += 150;
        g.drawString(character.getRace(), x, y);
        x += 80;
        g.drawString(character.getParty(), x, y);
        x += 100;
        g.drawString(character.getMap(), x, y);
    }

    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return getMinimumSizeInt();
    }

    /**
     * Returns the minimal size needed to display this component.
     * @return the minimal size
     */
    @NotNull
    private Dimension getMinimumSizeInt() {
        final CharacterInformation character = characterModel.getEntry(index);
        return GuiUtils.getTextDimension(character == null ? "" : character.getName(), getFontMetrics(font));
    }

    @Override
    public boolean canScroll(final int distance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void scroll(final int distance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetScroll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns the index of this element.
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index of this element.
     * @param index the index
     */
    public void setIndex(final int index) {
        if (this.index == index) {
            return;
        }

        characterModel.removeCharacterInformationListener(index, characterInformationListener);
        this.index = index;
        characterModel.addCharacterInformationListener(index, characterInformationListener);
        setChanged();
        updateTooltip();
    }

    /**
     * Updates the tooltip text.
     */
    private void updateTooltip() {
        final CharacterInformation characterInformation = characterModel.getEntry(index);
        setTooltipText(characterInformation == null ? null : characterInformation.getName());
    }

    /**
     * Sets the selected state.
     * @param selected whether this element should drawn as "selected"
     */
    public void setSelected(final boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        setChanged();
    }

    @Override
    public void execute() {
        // ignore
    }

    @Override
    public void notifyOpen() {
    }

}

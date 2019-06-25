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
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FaceImages;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.list.GUISpellSkillList;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.spells.SpellsManagerListener;
import java.awt.Dimension;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Nicolas Weeger
 */
public class GUIItemSpellSkill extends GUIItemItem {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The object used for synchronization on {@link #index}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The {@link FacesManager} for looking up faces.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The {@link SpellsManager} instance to watch.
     */
    @NotNull
    private final SpellsManager spellsManager;

    /**
     * The currently selected spell or {@code null} if none is selected.
     * Corresponds to index {@link #index}.
     */
    @Nullable
    private Skill skill;

    /**
     * The currently selected spell or {@code -1} if none is selected.
     * Corresponds to {@link #skill}.
     */
    private int index = -1;

    /**
     * Whether this element is selected in its {@link GUISpellSkillList}.
     */
    private boolean selected;

    /**
     * The spells view to use.
     */
    @NotNull
    private final ItemView itemView;

    /**
     * The {@link SpellsManagerListener} used to detect spell changes.
     */
    @NotNull
    private final SpellsManagerListener spellsManagerListener = new SpellsManagerListener() {

        @Override
        public void spellAdded(final int index) {
            if (GUIItemSpellSkill.this.index >= index) {
                setSkill();
            }
        }

        @Override
        public void spellRemoved(final int index) {
            if (GUIItemSpellSkill.this.index >= index) {
                setSkill();
            }
        }

    };

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener() {

        @Override
        public void faceUpdated(@NotNull final Face face) {
            if (skill != null) { // && skill.getFaceNum() == face.getFaceNum()) {
                setChanged();
            }
        }

    };

    @NotNull
    private final FaceImages defaultSkillIcon;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param itemPainter the item painter for painting the icon
     * @param defaultIndex the default scroll index
     * @param facesManager the faces manager for looking up faces
     * @param spellsManager the spells manager instance to watch
     * @param itemView the spells view to use
     * @param defaultSkillIcon the default icon to use the skills if not
     * defined
     * @param size the size of the component or {@code 0} for undefined
     * @param guiFactory the global GUI factory instance
     */
    public GUIItemSpellSkill(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final ItemPainter itemPainter, final int defaultIndex, @NotNull final FacesManager facesManager, @NotNull final SpellsManager spellsManager, @NotNull final ItemView itemView, @NotNull final FaceImages defaultSkillIcon, final int size, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, itemPainter, facesManager, guiFactory);
        this.facesManager = facesManager;
        this.defaultIndex = defaultIndex;
        this.spellsManager = spellsManager;
        setIndex(defaultIndex);
        this.spellsManager.addCrossfireSpellChangedListener(spellsManagerListener);
        this.facesManager.addFacesManagerListener(facesManagerListener);
        this.itemView = itemView;
        this.defaultSkillIcon = defaultSkillIcon;
        if (size != 0) {
            setSize(size, size);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        spellsManager.removeCrossfireSpellChangedListener(spellsManagerListener);
        facesManager.removeFacesManagerListener(facesManagerListener);
    }

    @Override
    public void notifyOpen() {
    }

    @Override
    public boolean canScroll(final int distance) {
        if (distance < 0) {
            return index >= -distance;
        }
        //noinspection SimplifiableIfStatement
        if (distance > 0) {
            return index+distance < spellsManager.getSpells();
        }
        return false;
    }

    @Override
    public void scroll(final int distance) {
        setIndex(index+distance);
        setChanged();
    }

    @Override
    public void resetScroll() {
        setIndex(defaultIndex);
    }

    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

    @NotNull
    @Override
    public Dimension getMinimumSize() {
        return getMinimumSizeInt();
    }

    /**
     * Returns the minimal size to display this component.
     * @return the minimal size
     */
    @NotNull
    private static Dimension getMinimumSizeInt() {
        return new Dimension(32, 32);
    }

    /**
     * Sets the currently selected {@link Skill}.
     */
    private void setSkill() {
        final Skill newSkill = spellsManager.getSpellSkill(index);
        if (skill == newSkill) {
            return;
        }

        skill = newSkill;

        setChanged();
        tooltipChanged();
    }

    /**
     * Sets the {@link #index} of the currently selected {@link #skill}. Updates
     * the currently selected spell.
     * @param index the index to set
     */
    private void setIndex(final int index) {
        if (this.index == index) {
            return;
        }
        this.index = index;

        setSkill();
    }

    @NotNull
    @Override
    protected Image getFace(@NotNull final CfItem item) {
        if (item.getFace().getFaceNum() == 0) {
            return defaultSkillIcon.getOriginalImageIcon().getImage();
        }
        return facesManager.getOriginalImageIcon(item.getFace().getFaceNum(), null).getImage();
    }

    @Override
    public void setSelected(final boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        setChanged();
    }

    @Override
    protected boolean isSelected() {
        return selected || isActive();
    }

    @Override
    public int getIndex() {
        synchronized (sync) {
            return index;
        }
    }

    @Override
    public void setIndexNoListeners(final int index) {
        synchronized (sync) {
            this.index = index;
        }

        setItemNoListeners(itemView.getItem(this.index));
    }

    @Override
    public void button1Clicked(final int modifiers) {
    }

    @Override
    public void button2Clicked(final int modifiers) {
    }

    @Override
    public void button3Clicked(final int modifiers) {
    }

}

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

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.faces.FaceImages;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemSpellSkill;
import com.realtime.crossfire.jxclient.gui.item.ItemPainter;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIItemList} displaying spell skills.
 * @author Nicolas Weeger
 */
public class GUISpellSkillList extends GUIItemList {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link TooltipManager} to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to notify.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * The base name for created elements.
     */
    @NotNull
    private final String name;

    /**
     * The {@link ItemView} to use.
     */
    @NotNull
    private final ItemView itemView;

    /**
     * The spells to display.
     */
    @NotNull
    private final SpellsManager spellsManager;

    /**
     * The {@link ItemPainter} for painting the icon.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The {@link FacesManager} to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The default {@link FaceImages} to use for the skills if not defined.
     */
    @NotNull
    private final FaceImages defaultSkillIcon;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of cells
     * @param cellHeight the height of cells
     * @param itemView the item view to monitor
     * @param currentItem the label to update with information about the
     * selected item.
     * @param spellsManager the skills to display
     * @param itemPainter the item painter for painting the icon
     * @param facesManager the faces manager to use
     * @param defaultSkillIcon the default icon to use the skills if not
     * defined
     */
    public GUISpellSkillList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final SpellsManager spellsManager, @NotNull final ItemPainter itemPainter, @NotNull final FacesManager facesManager, @NotNull final FaceImages defaultSkillIcon) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, itemView, currentItem, new GUIItemSpellSkill(tooltipManager, elementListener, name+"_template", itemPainter, -1, facesManager, spellsManager, itemView, defaultSkillIcon, cellHeight));
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.itemView = itemView;
        this.spellsManager = spellsManager;
        this.itemPainter = itemPainter;
        this.facesManager = facesManager;
        this.defaultSkillIcon = defaultSkillIcon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateTooltip(final int index, final int x, final int y, final int w, final int h) {
        final Skill skill = spellsManager.getSpellSkill(index);
        final String text;
        if (skill == null) {
            text = "";
        } else if (skill.getLevel() > 0) {
            text = skill.toString() + " (level " + String.valueOf(skill.getLevel()) + ")";
        } else {
            text = skill.toString();
        }
        setTooltipText(text, x, y, w, h);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected GUIElement newItem(final int index) {
        return new GUIItemSpellSkill(tooltipManager, elementListener, name+index, itemPainter, index, facesManager, spellsManager, itemView, defaultSkillIcon, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void selectionChanged(int selectedIndex) {
        if (spellsManager == null) {
            return;
        }
        spellsManager.filterSkill(selectedIndex);
        super.selectionChanged(selectedIndex);
    }

}

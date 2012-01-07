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
 * Copyright (C) 2006-2012 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItemFactory;
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
     * The spells to display.
     */
    @NotNull
    private final SpellsManager spellsManager;

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
     * @param itemItemFactory the factory for creating item instances
     * @param spellsManager the skills to display
     */
    public GUISpellSkillList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final GUIItemItemFactory itemItemFactory, @NotNull final SpellsManager spellsManager) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, itemView, currentItem, itemItemFactory);
        this.spellsManager = spellsManager;
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
    @Override
    protected void selectionChanged(int selectedIndex) {
        if (spellsManager == null) {
            return;
        }
        spellsManager.filterSkill(selectedIndex);
        super.selectionChanged(selectedIndex);
    }
}

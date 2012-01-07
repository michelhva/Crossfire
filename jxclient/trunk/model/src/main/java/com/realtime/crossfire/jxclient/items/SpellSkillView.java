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

package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.spells.SpellsManagerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A list to display spell skills.
 * @author Nicolas Weeger
 */
public class SpellSkillView extends AbstractItemView {

    /**
     * The spells to display.
     */
    @NotNull
    private final SpellsManager spellsManager;

    /**
     * The {@link FacesManager} for retrieving face information.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * Creates a new instance.
     * @param spellsManager the spells to display
     * @param facesManager the faces manager for retrieving face information
     */
    public SpellSkillView(@NotNull final SpellsManager spellsManager, @NotNull final FacesManager facesManager) {
        this.spellsManager = spellsManager;
        this.facesManager = facesManager;
        spellsManager.addCrossfireSpellChangedListener(new SpellsManagerListener() {

            @Override
            public void spellAdded(final int index) {
                addModifiedRange(0, spellsManager.getSpellSkills());
            }

            @Override
            public void spellRemoved(final int index) {
                addModifiedRange(0, spellsManager.getSpellSkills());
            }

        });
        facesManager.addFacesManagerListener(new FacesManagerListener() {

            @Override
            public void faceUpdated(@NotNull final Face face) {
                if (spellsManager.displaysFace(face.getFaceNum())) {
                    addModifiedRange(0, spellsManager.getSpellSkills());
                }
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return spellsManager.getSpellSkills();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public CfItem getItem(final int index) {
        final Skill skill = spellsManager.getSpellSkill(index);
        if (skill == null) {
            return new CfItem(0, 0, 0, 0, null, "All skills", "All skills", 0, 0, 0, 0);
        }
        return new CfItem(0, 0, 0, 0, null, skill.toString(), skill.toString(), 0, 0, 0, 0);
    }

}

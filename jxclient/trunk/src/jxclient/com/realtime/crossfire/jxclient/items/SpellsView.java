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

package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.spells.SpellsManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author nicolas
 */
public class SpellsView extends AbstractItemView {

    @NotNull
    private final SpellsManager spellsManager;

    @NotNull
    private final FacesManager facesManager;

    public SpellsView(@NotNull final SpellsManager spellsManager, @NotNull final FacesManager facesManager) {
        super();
        this.spellsManager = spellsManager;
        this.facesManager = facesManager;
        spellsManager.addCrossfireSpellChangedListener(new SpellsManagerListener() {
            public void spellAdded(Spell spell, int index) {
                addModifiedRange(index, spellsManager.getSpells());
            }

            public void spellRemoved(Spell spell, int index) {
                addModifiedRange(index, spellsManager.getSpells());
            }
        });
        facesManager.addFacesManagerListener(new FacesManagerListener() {
            public void faceUpdated(Face face) {
                addModifiedRange(0, spellsManager.getSpells());
            }
        });
    }

    public int getSize() {
        return spellsManager.getSpells();
    }

    public CfItem getItem(int index) {
        final Spell spell = spellsManager.getSpell(index);
        if (spell == null) {
            return null;
        }
        final Face face = facesManager.getFace(spell.getFaceNum());
        CfItem item = new CfItem(0, spell.getTag(), 0, 0, face, spell.getName(), spell.getName(), 0, 0, 0, 0);
        return item;
    }

}

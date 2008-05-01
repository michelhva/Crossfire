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

package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.spells.CrossfireSpellChangedListener;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateFaceListener;
import com.realtime.crossfire.jxclient.spells.Spell;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class GUIItemSpelllist extends GUIItem
{
    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    private Spell spell = null;

    private int index = -1;

    /**
     * The {@link CrossfireSpellChangedListener} used to detect spell changes.
     */
    private final CrossfireSpellChangedListener crossfireSpellChangedListener = new CrossfireSpellChangedListener()
    {
        /** {@inheritDoc} */
        public void spellAdded(final Spell spell, final int index)
        {
            if (GUIItemSpelllist.this.index >= index)
            {
                setSpell();
            }
        }

        /** {@inheritDoc} */
        public void spellRemoved(final Spell spell, final int index)
        {
            if (GUIItemSpelllist.this.index >= index)
            {
                setSpell();
            }
        }

        /** {@inheritDoc} */
        public void spellModified(final Spell spell, final int index)
        {
            if (GUIItemSpelllist.this.index == index)
            {
                setSpell();
            }
        }
    };

    /**
     * The {@link CrossfireUpdateFaceListener} registered to detect updated
     * faces.
     */
    private final CrossfireUpdateFaceListener crossfireUpdateFaceListener = new CrossfireUpdateFaceListener()
    {
        /** {@inheritDoc} */
        public void updateFace(final int faceID)
        {
            if (spell == null)
            {
                return;
            }

            final Face face = spell.getFace();
            if (face == null || face.getID() != faceID)
            {
                return;
            }

            setChanged();
        }
    };

    public GUIItemSpelllist(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final int defaultIndex, final CrossfireServerConnection crossfireServerConnection, final Font font)
    {
        super(jxcWindow, name, x, y, w, h, cursedImage, appliedImage, selectorImage, lockedImage, crossfireServerConnection, font);
        this.defaultIndex = defaultIndex;
        setIndex(defaultIndex);
        ItemsList.getSpellsManager().addCrossfireSpellChangedListener(crossfireSpellChangedListener);
        jxcWindow.getCrossfireServerConnection().addCrossfireUpdateFaceListener(crossfireUpdateFaceListener);
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            return index >= -distance;
        }
        else if (distance > 0)
        {
            final List<Spell> list = ItemsList.getSpellsManager().getSpellList();
            return index+distance < list.size();
        }
        else
        {
            return false;
        }
    }

    /* {@inheritDoc} */
    public void scroll(final int distance)
    {
        setIndex(index+distance);
        setChanged();
    }

    /* {@inheritDoc} */
    public void resetScroll()
    {
        setIndex(defaultIndex);
    }

    /* {@inheritDoc} */
    @Override public void button1Clicked(final JXCWindow jxcw)
    {
        if (spell == null)
        {
            return;
        }

        jxcw.getCommandQueue().sendNcom(false, "cast "+spell.getInternalName());
        jxcw.getCurrentSpellManager().setCurrentSpell(spell);
    }

    /* {@inheritDoc} */
    @Override public void button2Clicked(final JXCWindow jxcw)
    {
    }

    /* {@inheritDoc} */
    @Override public void button3Clicked(final JXCWindow jxcw)
    {
    }

    /* {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        if (spell == null)
        {
            return;
        }

        g.drawImage(spell.getImageIcon().getImage(), 0, 0, null);
        if (isActive())
        {
            g.drawImage(selectorImage, 0, 0, null);
        }
    }

    private void setSpell()
    {
        final List<Spell> list = ItemsList.getSpellsManager().getSpellList();
        final Spell spell = 0 <= index && index < list.size() ? list.get(index) : null;

        if (this.spell == spell)
        {
            return;
        }

        this.spell = spell;
        setChanged();

        setTooltipText(spell == null ? null : spell.getTooltipText());
    }

    private void setIndex(final int index)
    {
        if (this.index == index)
        {
            return;
        }
        this.index = index;

        setSpell();
    }
}

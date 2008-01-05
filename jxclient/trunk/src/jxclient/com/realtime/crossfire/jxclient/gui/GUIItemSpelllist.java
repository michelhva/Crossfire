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

import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.spells.CrossfireSpellChangedListener;
import com.realtime.crossfire.jxclient.spells.Spell;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class GUIItemSpelllist extends GUIItem
{
    private Spell myspell = null;

    private int myindex = -1;

    /**
     * The {@link CrossfireSpellChangedListener} used to detect spell changes.
     */
    private final CrossfireSpellChangedListener crossfireSpellChangedListener = new CrossfireSpellChangedListener()
    {
        /** {@inheritDoc} */
        public void spellAdded(final Spell spell, final int index)
        {
            if (myindex >= index)
            {
                setSpell();
            }
        }

        /** {@inheritDoc} */
        public void spellRemoved(final Spell spell, final int index)
        {
            if (myindex >= index)
            {
                setSpell();
            }
        }

        /** {@inheritDoc} */
        public void spellModified(final Spell spell, final int index)
        {
            if (myindex == index)
            {
                setSpell();
            }
        }
    };

    public GUIItemSpelllist(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage image, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final int index, final CrossfireServerConnection crossfireServerConnection, final Font font)
    {
        super(jxcWindow, name, x, y, w, h, image, cursedImage, appliedImage, selectorImage, lockedImage, crossfireServerConnection, font);
        setIndex(index);
        ItemsList.getSpellsManager().addCrossfireSpellChangedListener(crossfireSpellChangedListener);
        render();
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            return myindex >= -distance;
        }
        else if (distance > 0)
        {
            final List<Spell> list = ItemsList.getSpellsManager().getSpellList();
            return myindex+distance < list.size();
        }
        else
        {
            return false;
        }
    }

    /* {@inheritDoc} */
    @Override public void scroll(final int distance)
    {
        setIndex(myindex+distance);
        render();
    }

    /* {@inheritDoc} */
    @Override protected void button1Clicked(final JXCWindow jxcw)
    {
        if (myspell == null)
        {
            return;
        }

        jxcw.getCommandQueue().sendNcom(false, "cast "+myspell.getInternalName());
        jxcw.getCurrentSpellManager().setCurrentSpell(myspell);
    }

    /* {@inheritDoc} */
    @Override protected void button2Clicked(final JXCWindow jxcw)
    {
    }

    /* {@inheritDoc} */
    @Override protected void button3Clicked(final JXCWindow jxcw)
    {
    }

    /* {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        if (myspell == null)
        {
            return;
        }

        g.drawImage(myspell.getImageIcon().getImage(), 0, 0, null);
        if (isActive())
        {
            g.drawImage(selectorImage, 0, 0, null);
        }
    }

    private void setSpell()
    {
        final List<Spell> list = ItemsList.getSpellsManager().getSpellList();
        final Spell spell = 0 <= myindex && myindex < list.size() ? list.get(myindex) : null;

        if (myspell == spell)
        {
            return;
        }

        myspell = spell;
        render();

        setTooltipText(spell == null ? null : spell.getTooltipText());
    }

    private void setIndex(final int index)
    {
        if (myindex == index)
        {
            return;
        }
        myindex = index;

        setSpell();
    }
}

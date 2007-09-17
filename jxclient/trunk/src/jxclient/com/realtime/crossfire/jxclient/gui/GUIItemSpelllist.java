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

import com.realtime.crossfire.jxclient.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.CrossfireSpellChangedListener;
import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.Spell;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    public GUIItemSpelllist(final JXCWindow jxcWindow, final String nn, final int nx, final int ny, final int nw, final int nh, final BufferedImage picture, final BufferedImage pic_cursed, final BufferedImage pic_applied, final BufferedImage pic_selector, final BufferedImage pic_locked, final int index, final CrossfireServerConnection msc, final Font mft)
    {
        super(jxcWindow, nn, nx, ny, nw, nh, picture, pic_cursed, pic_applied, pic_selector, pic_locked, msc, mft);
        setIndex(index);
        ItemsList.getSpellsManager().addCrossfireSpellChangedListener(crossfireSpellChangedListener);
        render();
    }

    public int getIndex()
    {
        return myindex;
    }

    /** {@inheritDoc} */
    public boolean canScrollUp()
    {
        return myindex > 0;
    }

    public void scrollUp()
    {
        setIndex(myindex-1);
        render();
    }

    /** {@inheritDoc} */
    public boolean canScrollDown()
    {
        final List<Spell> list = ItemsList.getSpellsManager().getSpellList();
        return myindex+1 < list.size();
    }

    public void scrollDown()
    {
        setIndex(myindex+1);
        render();
    }

    protected void button1Clicked(final JXCWindow jxcw)
    {
        if (myspell == null)
        {
            return;
        }

        try
        {
            jxcw.sendNcom("cast "+myspell.getInternalName());
            jxcw.setCurrentSpell(myspell);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    protected void button2Clicked(final JXCWindow jxcw)
    {
    }

    protected void button3Clicked(final JXCWindow jxcw)
    {
    }

    protected void render(final Graphics g)
    {
        if (myspell == null)
        {
            return;
        }

        g.drawImage(myspell.getImageIcon().getImage(), 0, 0, null);
        if (active)
        {
            g.drawImage(mypicselector, 0, 0, null);
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

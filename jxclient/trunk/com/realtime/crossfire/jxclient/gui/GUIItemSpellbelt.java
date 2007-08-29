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
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.Spell;
import com.realtime.crossfire.jxclient.SpellBeltItem;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GUIItemSpellbelt extends GUIItem
{
    private SpellBeltItem myspellbelt = null;

    private int myindex = -1;

    public GUIItemSpellbelt(final JXCWindow jxcWindow, String nn, int nx, int ny, int nw, int nh, BufferedImage picture, BufferedImage pic_cursed, BufferedImage pic_applied, BufferedImage pic_selector, BufferedImage pic_locked, int index, CrossfireServerConnection msc, Font mft) throws IOException
    {
        super(jxcWindow, nn, nx, ny, nw, nh, picture, pic_cursed, pic_applied, pic_selector, pic_locked, msc, mft);
        setIndex(index);
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
    }

    /** {@inheritDoc} */
    public boolean canScrollDown()
    {
        return myindex+1 < 12;
    }

    public void scrollDown()
    {
        setIndex(myindex+1);
    }

    protected void button1Clicked(JXCWindow jxcw)
    {
        if (myspellbelt == null || myspellbelt.getSpell() == null)
        {
            return;
        }

        final int status = myspellbelt.getStatus();
        try
        {
            if (status == SpellBeltItem.STATUS_CAST)
            {
                jxcw.sendNcom("cast "+myspellbelt.getSpell().getInternalName());
            }
            else
            {
                jxcw.sendNcom("invoke "+myspellbelt.getSpell().getInternalName());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    protected void button2Clicked(JXCWindow jxcw)
    {
        if (myspellbelt == null || myspellbelt.getSpell() == null)
        {
            return;
        }

        final int status = myspellbelt.getStatus();
        myspellbelt.setStatus(status == SpellBeltItem.STATUS_CAST ? SpellBeltItem.STATUS_INVOKE : SpellBeltItem.STATUS_CAST);
        render();
    }

    protected void button3Clicked(JXCWindow jxcw)
    {
        final Spell myspell = jxcw.getCurrentSpell();
        if (myspellbelt == null)
        {
            if (myindex >= 0 && myindex < 12)
            {
                final SpellBeltItem spellBeltItem = new SpellBeltItem(myspell, SpellBeltItem.STATUS_CAST);
                JXCWindow.getSpellBelt()[myindex] = spellBeltItem;
                setSpellbelt(spellBeltItem);
            }
        }
        else
        {
            myspellbelt.setSpell(myspell);
        }
        render();
    }

    protected void render(Graphics g)
    {
        if (myspellbelt == null || myspellbelt.getSpell() == null)
        {
            return;
        }

        g.drawImage(myspellbelt.getSpell().getImageIcon().getImage(), 0, 0, null);
        g.setFont(myfont);
        g.setColor(Color.YELLOW);
        g.drawString("F"+(myindex+1), 1, 1+myfont.getSize());
        g.drawImage(myspellbelt.getStatus() == SpellBeltItem.STATUS_CAST ? mypiccursed : mypicapplied, 0, 0, null);
    }

    private void setSpellbelt(final SpellBeltItem spellBeltItem)
    {
        if (myspellbelt == spellBeltItem)
        {
            return;
        }

        myspellbelt = spellBeltItem;
        render();

        if (myspellbelt == null)
        {
            setTooltipText(null);
        }
        else
        {
            final Spell spell = myspellbelt.getSpell();
            setTooltipText(spell == null ? null : spell.getTooltipText());
        }
    }

    private void setIndex(final int index)
    {
        if (myindex == index)
        {
            return;
        }
        myindex = index;

        setSpellbelt(0 <= myindex && myindex < 12 ? JXCWindow.getSpellBelt()[myindex] : null);
    }
}

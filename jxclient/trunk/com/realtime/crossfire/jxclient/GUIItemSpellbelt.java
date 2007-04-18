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

package com.realtime.crossfire.jxclient;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.util.List;

public class GUIItemSpellbelt extends GUIItem
{
    private SpellBeltItem myspellbelt = null;

    private int myindex = -1;

    public GUIItemSpellbelt(String nn, int nx, int ny, int nw, int nh, String picture, String pic_cursed, String pic_applied, String pic_selector, String pic_locked, int index, ServerConnection msc, Font mft) throws IOException
    {
        super(nn, nx, ny, nw, nh, picture, pic_cursed, pic_applied, pic_selector, pic_locked, msc, mft);
        setIndex(index);
        render();
    }

    public int getIndex()
    {
        return myindex;
    }

    public void scrollUp()
    {
        setIndex(myindex-1);
        render();
    }

    public void scrollDown()
    {
        setIndex(myindex+1);
        render();
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
                jxcw.getServerConnection().writePacket("command 0 cast "+myspellbelt.getSpell().getInternalName());
            }
            else
            {
                jxcw.getServerConnection().writePacket("command 0 invoke "+myspellbelt.getSpell().getInternalName());
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
        if (status == SpellBeltItem.STATUS_CAST)
        {
            myspellbelt.setStatus(SpellBeltItem.STATUS_INVOKE);
        }
        else
        {
            myspellbelt.setStatus(SpellBeltItem.STATUS_CAST);
        }
        setModified();
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
            setModified();
        }
        render();
    }

    protected void render(Graphics g)
    {
        if (myspellbelt == null || myspellbelt.getSpell() == null)
        {
            return;
        }

        g.drawImage(myspellbelt.getSpell().getPicture(), 0, 0, null);
        g.setFont(myfont);
        g.setColor(Color.YELLOW);
        g.drawString("F"+(myindex+1), 1, 1+myfont.getSize());
        g.drawImage(myspellbelt.getStatus() == SpellBeltItem.STATUS_CAST ? mypiccursed : mypicapplied, 0, 0, null);
    }

    public void CommandUpditemReceived(CrossfireCommandUpditemEvent evt)
    {
    }

    public void CommandItem1Received(CrossfireCommandItem1Event evt)
    {
    }

    public void CommandItem2Received(CrossfireCommandItem2Event evt)
    {
    }

    private void setSpellbelt(final SpellBeltItem spellBeltItem)
    {
        if (myspellbelt == spellBeltItem)
        {
            return;
        }

        myspellbelt = spellBeltItem;
        setModified();
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

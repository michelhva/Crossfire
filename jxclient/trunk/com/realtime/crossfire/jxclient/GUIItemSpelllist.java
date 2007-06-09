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

public class GUIItemSpelllist extends GUIItem
{
    private Spell myspell = null;

    private int myindex = -1;

    public GUIItemSpelllist(String nn, int nx, int ny, int nw, int nh, String picture, String pic_cursed, String pic_applied, String pic_selector, String pic_locked, int index, ServerConnection msc, Font mft) throws IOException
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
        if (myspell == null)
        {
            return;
        }

        try
        {
            jxcw.getServerConnection().writePacket("command 0 cast "+myspell.getInternalName());
            jxcw.setCurrentSpell(myspell);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    protected void button2Clicked(JXCWindow jxcw)
    {
    }

    protected void button3Clicked(JXCWindow jxcw)
    {
    }

    protected void render(Graphics g)
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

    private void setSpell(final Spell spell)
    {
        if (myspell == spell)
        {
            return;
        }

        myspell = spell;
        render();
    }

    private void setIndex(final int index)
    {
        if (myindex == index)
        {
            return;
        }
        myindex = index;

        final List<Spell> list = ItemsList.getSpellList();
        setSpell(0 <= myindex && myindex < list.size() ? list.get(myindex) : null);
    }
}

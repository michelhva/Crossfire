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

import java.awt.Font;
import java.io.IOException;
import java.util.List;

public class GUIItemFloor extends GUIItemItem
{
    private int myindex;

    public GUIItemFloor(String nn, int nx, int ny, int nw, int nh, String picture, String pic_cursed, String pic_applied, String pic_selector, String pic_locked, int index, ServerConnection msc, Font mft) throws IOException
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
    }

    public void scrollDown()
    {
        setIndex(myindex+1);
    }

    protected void button1Clicked(JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }
        try
        {
            jxcw.getServerConnection().writePacket("examine "+item.getTag());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    protected void button3Clicked(JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }
        try
        {
            if (ItemsList.getPlayer() != null)
            {
                jxcw.getServerConnection().writePacket("move "+ItemsList.getPlayer().getTag()+" "+item.getTag()+" 0");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public void CommandUpditemReceived(CrossfireCommandUpditemEvent evt)
    {
        final CfItem updItem = evt.getItem();
        final CfItem item = getItem();
        if (item == updItem)
        {
            if (item.getLocation() != ItemsList.getCurrentFloor())
            {
                setItem(null);
            }
        }
        else if (item != null)
        {
            if (item.getLocation() != ItemsList.getCurrentFloor())
            {
                setIndex(myindex);
            }
        }
        render();
    }

    public void CommandItem1Received(CrossfireCommandItem1Event evt)
    {
        final CfItem item = evt.getItem();
        if (item.getLocation() == ItemsList.getCurrentFloor())
        {
            setIndex(myindex);
        }
        render();
    }

    public void CommandItem2Received(CrossfireCommandItem2Event evt)
    {
        final CfItem item = evt.getItem();
        if (item.getLocation() == ItemsList.getCurrentFloor())
        {
            setIndex(myindex);
        }
        render();
    }

    private void setIndex(final int index)
    {
        myindex = index;

        final List<CfItem> list = ItemsList.getItems(ItemsList.getCurrentFloor());
        setItem(0 <= myindex && myindex < list.size() ? list.get(myindex) : null);
    }
}

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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class GUIItemInventory extends GUIItemItem
{
    private int myindex = -1;

    public GUIItemInventory(String nn, int nx, int ny, int nw, int nh, String picture, String pic_cursed, String pic_applied, String pic_selector, String pic_locked, int index, ServerConnection msc, Font mft) throws IOException
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
        final CfItem item = getItem();
        if(item == null)
        {
            return;
        }

        try
        {
            if (jxcw.getKeyShift(JXCWindow.KEY_SHIFT_SHIFT))
            {
                final ByteArrayOutputStream bout = new ByteArrayOutputStream();
                final DataOutputStream out = new DataOutputStream(bout);
                out.writeBytes("mark ");
                out.writeInt(item.getTag());
                jxcw.getServerConnection().writePacket(bout.toString());
            }
            else if (jxcw.getKeyShift(JXCWindow.KEY_SHIFT_CTRL))
            {
                final ByteArrayOutputStream bout = new ByteArrayOutputStream();
                final DataOutputStream out = new DataOutputStream(bout);
                out.writeBytes("lock ");
                out.writeByte(item.isLocked() ? 0 : 1);
                out.writeInt(item.getTag());
                jxcw.getServerConnection().writePacket(bout.toString());
            }
            else
            {
                jxcw.getServerConnection().writePacket("examine "+item.getTag());
            }
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
        if(item == null)
        {
            return;
        }

        try
        {
            jxcw.getServerConnection().writePacket("move 0 "+item.getTag()+" 0");
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
            if (ItemsList.getPlayer() != null && item.getLocation() != ItemsList.getPlayer().getTag())
            {
                setItem(null);
            }
        }
        render();
    }

    public void CommandItem1Received(CrossfireCommandItem1Event evt)
    {
        setIndex(myindex);
        render();
    }

    public void CommandItem2Received(CrossfireCommandItem2Event evt)
    {
        setIndex(myindex);
        render();
    }

    private void setIndex(final int index)
    {
        myindex = index;

        final CfPlayer player = ItemsList.getPlayer();
        if (player == null)
        {
            return;
        }

        final List<CfItem> list = ItemsList.getItems(player.getTag());
        setItem(0 <= myindex && myindex < list.size() ? list.get(myindex) : null);
    }
}

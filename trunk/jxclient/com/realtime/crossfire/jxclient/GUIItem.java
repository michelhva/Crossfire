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
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIItem extends GUIElement implements GUIScrollable, CrossfireItem1Listener,
                                        CrossfireUpditemListener, CrossfireDelitemListener,
                                        CrossfireItem2Listener
{
    protected int myindex = 0;
    protected BufferedImage mypiccursed;
    protected BufferedImage mypicapplied;
    protected BufferedImage mypicselector;
    protected BufferedImage mypicbackground;
    protected BufferedImage mypiclocked;

    protected Spell myspell = null;
    protected SpellBeltItem myspellbelt = null;
    protected CfItem myitem = null;
    protected int mytype = 0;
    protected Font myfont;

    public static final int ITEM_FLOOR     = 0;
    public static final int ITEM_INVENTORY = 1;
    public static final int ITEM_SPELLBELT = 2;
    public static final int ITEM_SPELLLIST = 3;
    protected boolean spell_invoke = false;

    public GUIItem
            (String nn, int nx, int ny, int nw, int nh, String picture,
             String pic_cursed, String pic_applied, String pic_selector,
             String pic_locked, int index, int type, ServerConnection msc,
             Font mft)
            throws IOException
    {
        mypicbackground = javax.imageio.ImageIO.read(new File(picture));
        mypiccursed   = javax.imageio.ImageIO.read(new File(pic_cursed));
        mypicapplied  = javax.imageio.ImageIO.read(new File(pic_applied));
        mypicselector = javax.imageio.ImageIO.read(new File(pic_selector));
        mypiclocked   = javax.imageio.ImageIO.read(new File(pic_locked));
        myindex = index;
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myname = nn;
        active=false;
        mytype = type;
        myfont = mft;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        switch(type)
        {
            case ITEM_SPELLBELT:
                if ((myindex >= 0)&&(myindex<12))
                    myspellbelt = (JXCWindow.getSpellBelt())[myindex];
                break;
            case ITEM_SPELLLIST:
                if ((myindex>=0)&&(myindex<ItemsList.getSpellList().size()))
                     myspell = (Spell)ItemsList.getSpellList().get(myindex);
                break;
            default:
                break;
        }
        render();
    }
    public int getIndex()
    {
        return myindex;
    }
    public void scrollUp()
    {
        myindex--;
        java.util.List list;
        switch (mytype)
        {
            case ITEM_FLOOR:
                list = ItemsList.getItems(ItemsList.getCurrentFloor());
                if ((list.size()> myindex)&&(myindex>0))
                    myitem = (CfItem)list.get(myindex);
                else
                    myitem = null;
                break;
            case ITEM_INVENTORY:
                if (ItemsList.getPlayer()!=null)
                {
                    list = ItemsList.getItems(ItemsList.getPlayer().getTag());
                    if ((list.size()> myindex)&&(myindex>0))
                        myitem = (CfItem)list.get(myindex);
                    else
                        myitem = null;
                }
                break;
            case ITEM_SPELLBELT:
                if ((myindex >=0 )&&(myindex < 12))
                    myspellbelt = (JXCWindow.getSpellBelt())[myindex];
                else
                    myspellbelt = null;
                break;
            case ITEM_SPELLLIST:
                if ((myindex >= 0)&&(myindex<ItemsList.getSpellList().size()))
                {
                    myspell = (Spell)ItemsList.getSpellList().get(myindex);
                }
                break;
        }
        //CommandItem1Received(new CrossfireCommandItem1Event(myserverconnection, myitem));
        render();
    }
    public void scrollDown()
    {
        myindex++;
        java.util.List list;
        switch (mytype)
        {
            case ITEM_FLOOR:
                list = ItemsList.getItems(ItemsList.getCurrentFloor());
                if ((list.size()> myindex)&&(myindex>0))
                    myitem = (CfItem)list.get(myindex);
                else
                    myitem = null;
                break;
            case ITEM_INVENTORY:
                if (ItemsList.getPlayer()!=null)
                {
                    list = ItemsList.getItems(ItemsList.getPlayer().getTag());
                    if ((list.size()> myindex)&&(myindex>0))
                    {
                        myitem = (CfItem)list.get(myindex);
                    }
                    else
                        myitem = null;
                }
                break;
            case ITEM_SPELLBELT:
                if ((myindex >=0 )&&(myindex < 12))
                    myspellbelt = (JXCWindow.getSpellBelt())[myindex];
                else
                    myspellbelt = null;
                break;
            case ITEM_SPELLLIST:
                if ((myindex >= 0)&&(myindex<ItemsList.getSpellList().size()))
                {
                    myspell = (Spell)ItemsList.getSpellList().get(myindex);
                }
                break;
        }
        //    CommandItem1Received(new CrossfireCommandItem1Event(myserverconnection, myitem));
        render();
    }
    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        int b = e.getButton();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        JXCWindow jxcw = (JXCWindow)(e.getSource());

        switch(b)
        {
            case MouseEvent.BUTTON1:
                active = true;
                switch (mytype)
                {
                    case ITEM_INVENTORY:
                        if (myitem != null)
                        {
                            try
                            {
                                if (jxcw.getKeyShift(JXCWindow.KEY_SHIFT_SHIFT)==true)
                                {
                                    out.writeBytes("mark ");
                                    out.writeInt(myitem.getTag());
                                    jxcw.getServerConnection().writePacket(bout.toString());
                                }
                                else if (jxcw.getKeyShift(JXCWindow.KEY_SHIFT_CTRL)==true)
                                {
                                    out.writeBytes("lock ");
                                    if (myitem.isLocked())
                                        out.writeByte(0);
                                    else
                                        out.writeByte(1);
                                    out.writeInt(myitem.getTag());
                                    jxcw.getServerConnection().writePacket(bout.toString());
                                }
                                else
                                    jxcw.getServerConnection().writePacket("examine "+myitem.getTag());
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                System.exit(0);
                            }
                        }
                        break;
                    case ITEM_FLOOR:
                        if (myitem != null)
                        {
                            try
                            {
                                jxcw.getServerConnection().writePacket("examine "+myitem.getTag());
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                System.exit(0);
                            }
                        }
                        break;
                    case ITEM_SPELLLIST:
                        if (myspell != null)
                        {
                            try
                            {
                                jxcw.getServerConnection().writePacket("command 0 cast "+
                                        myspell.getInternalName());
                                jxcw.setCurrentSpell(myspell);
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                System.exit(0);
                            }
                        }
                        break;
                    case ITEM_SPELLBELT:
                        System.out.println("Spellbelt entry btn 1:"+myindex);
                        if ((myspellbelt != null)&&(myspellbelt.getSpell()!=null))
                        {
                            int status = myspellbelt.getStatus();
                            try
                            {
                                if (status==SpellBeltItem.STATUS_CAST)
                                    jxcw.getServerConnection().writePacket("command 0 cast "+
                                        myspellbelt.getSpell().getInternalName());
                                else
                                    jxcw.getServerConnection().writePacket("command 0 invoke "+
                                        myspellbelt.getSpell().getInternalName());
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                System.exit(0);
                            }
                        }
                        break;
                }
                render();
                break;
            case MouseEvent.BUTTON2:
                switch(mytype)
                {
                    case ITEM_FLOOR:
                    case ITEM_INVENTORY:
                        if (myitem != null)
                        {
                            try
                            {
                                if (mytype == ITEM_INVENTORY)
                                    jxcw.getServerConnection().writePacket("apply "+myitem.getTag());
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                System.exit(0);
                            }
                        }
                        break;
                    case ITEM_SPELLBELT:
                        if (myspellbelt == null)
                            myspellbelt = (jxcw.getSpellBelt())[myindex];
                        if (myspellbelt != null)
                        {
                            int status = myspellbelt.getStatus();
                            if (status==SpellBeltItem.STATUS_CAST)
                                myspellbelt.setStatus(SpellBeltItem.STATUS_INVOKE);
                            else
                                myspellbelt.setStatus(SpellBeltItem.STATUS_CAST);
                        }
                        render();
                        break;
                    case ITEM_SPELLLIST:
                        break;
                }
                break;
            case MouseEvent.BUTTON3:
                switch(mytype)
                {
                    case ITEM_FLOOR:
                        if (myitem != null)
                        {
                            try
                            {
                                if (ItemsList.getPlayer()!=null)
                                {
                                    jxcw.getServerConnection().writePacket("move "+
                                            ItemsList.getPlayer().getTag()+" "+
                                            myitem.getTag()+" 0");
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                System.exit(0);
                            }
                        }
                        break;
                    case ITEM_INVENTORY:
                        if (myitem != null)
                        {
                            try
                            {
                                jxcw.getServerConnection().writePacket("move 0 "+
                                        myitem.getTag()+" 0");
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                System.exit(0);
                            }
                        }
                        break;
                    case ITEM_SPELLLIST:
                        break;
                    case ITEM_SPELLBELT:
                        myspell = jxcw.getCurrentSpell();
                        if (myspellbelt == null)
                        {
                            if ((myindex>=0)&&(myindex<12))
                                (JXCWindow.getSpellBelt())[myindex] = new SpellBeltItem(myspell,
                                    SpellBeltItem.STATUS_CAST);
                            myspellbelt =(JXCWindow.getSpellBelt())[myindex];
                        }
                        else
                        {
                            myspellbelt.setSpell(myspell);
                        }
                        render();
                        break;
                }
                break;
        }
    }
    public void setActive(boolean act)
    {
        active = act;
        render();
    }
    protected void render()
    {
        Graphics2D g = mybuffer.createGraphics();
        g.drawImage(mypicbackground, 0, 0, null);

        g.setBackground(new Color(0,0,0,0.0f));
        g.clearRect(0,0,w,h);
        switch (mytype)
        {
            case ITEM_FLOOR:
            case ITEM_INVENTORY:
                if (myitem != null)
                {
                    g.drawImage(myitem.getFace().getOriginalPicture(),0,0,null);
                    if (myitem.isApplied())
                        g.drawImage(mypicapplied, 0, 0, null);
                    if (myitem.isCursed())
                        g.drawImage(mypiccursed, 0, 0, null);
                    if (myitem.isLocked())
                        g.drawImage(mypiclocked, 0, 0, null);
                    if (active)
                        g.drawImage(mypicselector, 0, 0, null);
                    if (myitem.getNrOf() > 0)
                    {
                        g.setFont(myfont);
                        g.setColor(Color.WHITE);
                        g.drawString(String.valueOf(myitem.getNrOf()), 1,1+myfont.getSize());
                    }
                }
                break;
            case ITEM_SPELLBELT:
                if ((myspellbelt !=null)&&(myspellbelt.getSpell()!=null))
                {
                    g.drawImage(myspellbelt.getSpell().getPicture(),0,0,null);
                    g.setFont(myfont);
                    g.setColor(Color.YELLOW);
                    g.drawString("F"+(myindex+1), 1,1+myfont.getSize());
                    if (myspellbelt.getStatus() == SpellBeltItem.STATUS_CAST)
                        g.drawImage(mypiccursed, 0, 0, null);
                    else
                        g.drawImage(mypicapplied, 0, 0, null);
                }
                break;
            case ITEM_SPELLLIST:
                if (myspell !=null)
                {
                    g.drawImage(myspell.getPicture(),0,0,null);
                    if (active)
                        g.drawImage(mypicselector, 0, 0, null);
                }
                break;
        }
        g.dispose();
    }
    public void CommandUpditemReceived(CrossfireCommandUpditemEvent evt)
    {
        CfItem item = evt.getItem();
        switch (mytype)
        {
            case ITEM_FLOOR:
                if (myitem == item)
                {
                    if (myitem.getLocation()!=ItemsList.getCurrentFloor())
                        myitem = null;
                    render();
                }
                else if (myitem != null)
                {
                    if (myitem.getLocation()!=ItemsList.getCurrentFloor())
                    {
                        java.util.List list = ItemsList.getItems(ItemsList.getCurrentFloor());
                        if ((list.size()> myindex)&&(myindex>=0))
                            myitem = (CfItem)list.get(myindex);
                        else
                            myitem = null;
                    }
                    render();
                }
                break;
            case ITEM_INVENTORY:
                if (myitem == item)
                {
                    if ((ItemsList.getPlayer()!=null) && (myitem.getLocation()!=ItemsList.getPlayer().getTag()))
                        myitem=null;
                    render();
                }
                break;
            case ITEM_SPELLBELT:
                break;
            case ITEM_SPELLLIST:
                break;
        }
    }
    public void CommandItem1Received(CrossfireCommandItem1Event evt)
    {
        CfItem item = evt.getItem();
        //myitem = null;
        java.util.List list;
        switch (mytype)
        {
            case ITEM_FLOOR:
                if (item.getLocation()==ItemsList.getCurrentFloor())
                {
                    list = ItemsList.getItems(ItemsList.getCurrentFloor());
                    if (list.size()> myindex)
                        myitem = (CfItem)list.get(myindex);
                    else
                        myitem = null;
                    render();
                }
                break;
            case ITEM_INVENTORY:
                if (ItemsList.getPlayer()!=null)
                {
                    list = ItemsList.getItems(ItemsList.getPlayer().getTag());
                    if ((list.size()> myindex)&&(myindex>0))
                        myitem = (CfItem)list.get(myindex);
                    else
                        myitem = null;
                }
                break;
            case ITEM_SPELLBELT:
                break;
            case ITEM_SPELLLIST:
                break;
        }
        //CommandItem1Received(new CrossfireCommandItem1Event(myserverconnection, myitem));
        render();

    }
    public void CommandItem2Received(CrossfireCommandItem2Event evt)
    {
        CfItem item = evt.getItem();
        //myitem = null;
        java.util.List list;
        switch (mytype)
        {
            case ITEM_FLOOR:
                if (item.getLocation()==ItemsList.getCurrentFloor())
                {
                    list = ItemsList.getItems(ItemsList.getCurrentFloor());

                    if (list.size()> myindex)
                        myitem = (CfItem)list.get(myindex);
                    else
                        myitem = null;
                    render();
                }
                break;
            case ITEM_INVENTORY:
                if (ItemsList.getPlayer()!=null)
                {
                    list = ItemsList.getItems(ItemsList.getPlayer().getTag());
                    if ((list.size()> myindex)&&(myindex>0))
                        myitem = (CfItem)list.get(myindex);
                    else
                        myitem = null;
                }
                break;
            case ITEM_SPELLBELT:
                break;
            case ITEM_SPELLLIST:
                break;
        }
        //CommandItem1Received(new CrossfireCommandItem1Event(myserverconnection, myitem));
        render();

    }
    public void CommandDelitemReceived(CrossfireCommandDelitemEvent evt)
    {
        CfItem item = evt.getItem();
        if (myitem == item)
        {
            myitem = null;
            render();
        }
    }
}

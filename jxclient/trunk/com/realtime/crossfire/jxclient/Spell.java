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
import java.awt.image.*;
import java.io.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Spell
{
    private BufferedImage myspellpic;
    private String myspellname;
    private String myid;

    private int     mytag;
    private int     mylevel;
    private int     mycastingtime;
    private int     mymana;
    private int     mygrace;
    private int     mydamage;
    private int     myskill;
    private int     mypath;
    private Face    myface;
    private String  myname;
    private String  mymessage;

    public int getTag()
    {
        return mytag;
    }
    public int getLevel()
    {
        return mylevel;
    }
    public int getCastingTime()
    {
        return mycastingtime;
    }
    public int getMana()
    {
        return mymana;
    }
    public int getGrace()
    {
        return mygrace;
    }
    public int getDamage()
    {
        return mydamage;
    }
    public int getSkill()
    {
        return myskill;
    }
    public int getPath()
    {
        return mypath;
    }
    public String getName()
    {
        return myname;
    }
    public String getMessage()
    {
        return mymessage;
    }
    public Face getFace()
    {
        return myface;
    }
    public void setLevel(int nv)
    {
        mylevel = nv;
    }
    public void setCastingTime(int nv)
    {
        mycastingtime = nv;
    }
    public void setMana(int nv)
    {
        mymana = nv;
    }
    public void setGrace(int nv)
    {
        mygrace = nv;
    }
    public void setDamage(int nv)
    {
        mydamage = nv;
    }
    public void setSkill(int nv)
    {
        myskill = nv;
    }
    public void setPath(int nv)
    {
        mypath = nv;
    }

    public Spell(Face f, int tag, String spellname, String spellmessage)
    {
        myspellpic = null;
        myface = f;
        mytag = tag;
        myname = spellname;
        mymessage = spellmessage;
        myid = null;
    }

    public Spell(String filename, String spellname, String id) throws IOException
    {
        myspellpic   =
            javax.imageio.ImageIO.read(this.getClass().getClassLoader().getResource(filename));
        if (myspellpic == null)
        {
            throw new IllegalArgumentException("resource '"+filename+"' does not exist");
        }
        myface = null;
        mytag = 0;
        myname = spellname;
        mymessage = "";
        myid = id;
    }
    public String getInternalName()
    {
        return myid;
    }
    public BufferedImage getPicture()
    {
        if (myspellpic != null)
            return myspellpic;
        else
        {
            return myface.getOriginalPicture();
        }
    }
    public String toString()
    {
        String str = new String("Name:"+myname+" ID:"+mytag+" Level:"+mylevel);
        str = str+" Time:"+mycastingtime+" Mana:"+mymana+" Grace:"+mygrace;
        str = str+" Damage:"+mydamage+" Skill:"+myskill+" Path:"+mypath;
        return str;
    }
}

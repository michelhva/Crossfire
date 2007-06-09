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

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Skill
{
    private int myindex;
    private String myname;
    private long myexperience = 0;
    private int mylevel = 1;

    public Skill(int index, String name)
    {
        myindex = index;
        myname = name;
    }
    public void setLevel(int nv)
    {
//        System.out.println("Skill "+myname+": Setting level to "+nv);
        mylevel = nv;
    }
    public void setExperience(long exp)
    {
//        System.out.println("Skill "+myname+": Setting experience to "+exp);
        myexperience = exp;
    }
    public long getExperience()
    {
        return myexperience;
    }
    public int getLevel()
    {
        return mylevel;
    }
    public String toString()
    {
        return myname;
    }
}

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
public class SpellBeltItem 
{
    public static final int STATUS_CAST = 0;
    public static final int STATUS_INVOKE = 1;

    private int mystatus = 0;
    private Spell myspell = null;

    public SpellBeltItem(int idx, int status)
    {
        myspell = (Spell)(ItemsList.getSpellList().get(idx));
        mystatus = status;
    }
    public SpellBeltItem(Spell sp, int status)
    {
        myspell = sp;
        mystatus = status;
    }
    public void setStatus(int st)
    {
        mystatus = st;
    }
    public int getStatus()
    {
        return mystatus;
    }
    public int getSpellIndex()
    {
        return ItemsList.getSpellList().indexOf(myspell);
    }
    public Spell getSpell()
    {
        return myspell;
    }
    public void setSpell(Spell sp)
    {
        myspell = sp;
    }
    public String toString()
    {
        String sp = new String("none");
        if (myspell != null)
            sp = myspell.getName();
        String ssp = "Spellbelt - Spell is "+sp+" Status:"+mystatus;
        return ssp;
    }
}
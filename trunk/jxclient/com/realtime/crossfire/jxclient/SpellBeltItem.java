package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;

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
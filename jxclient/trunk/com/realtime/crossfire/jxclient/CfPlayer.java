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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class CfPlayer extends CfItem
{
    private static java.util.List<CrossfireStatsListener> mylisteners_stats =
            new ArrayList<CrossfireStatsListener>();
    private static java.util.List<CrossfirePlayerListener> mylisteners_player =
            new ArrayList<CrossfirePlayerListener>();

    private static Stats mystats = new Stats();

    public CfPlayer(int tag, int weight, Face face, String name)
    {
        super(0,tag,0,weight,face,name,name,1);
    }
    public static Stats getStats()
    {
        return mystats;
    }
    public static java.util.List<CrossfirePlayerListener> getCrossfirePlayerListeners()
    {
        return mylisteners_player;
    }
    public static java.util.List<CrossfireStatsListener> getCrossfireStatsListeners()
    {
        return mylisteners_stats;
    }
    public static void setStats(DataInputStream dis) throws IOException
    {
        int pos = 0;
        int len = dis.available();
        while(pos<len)
        {
            int stat = dis.readUnsignedByte();
            pos+=1;
            switch (stat)
            {
                case Stats.CS_STAT_EXP:
                    long experi = dis.readUnsignedShort();
                    mystats.setExperience(experi);
                    pos+=2;
                    break;
                case Stats.CS_STAT_SPEED:
                    mystats.setStat(stat, dis.readInt());
                    pos+=4;
                    break;
                case Stats.CS_STAT_WEAP_SP:
                    mystats.setStat(stat, dis.readInt());
                    pos+=4;
                    break;
                case Stats.CS_STAT_RANGE:
                {
                    int length = dis.readUnsignedByte();
                    pos+=1;
                    byte buf[] = new byte[length];
                    dis.readFully(buf);
                    String str = new String(buf);
                    pos+=length;
                    mystats.setRange(str);
                }
                    break;
                case Stats.CS_STAT_TITLE:
                {
                    int length = dis.readUnsignedByte();
                    pos+=1;
                    byte buf[] = new byte[length];

                    dis.readFully(buf);
                    String str = new String(buf);
                    pos+=length;
                    mystats.setTitle(str);
                }
                    break;
                case Stats.CS_STAT_WEIGHT_LIM:
                    mystats.setStat(Stats.CS_STAT_WEIGHT_LIM, dis.readInt());
                    pos+=4;
                    break;
                case Stats.CS_STAT_EXP64:
                    experi = dis.readLong();
                    mystats.setExperience(experi);
                    pos+=8;
                    break;
                case Stats.CS_STAT_HP:
                case Stats.CS_STAT_MAXHP:
                case Stats.CS_STAT_SP:
                case Stats.CS_STAT_MAXSP:
                case Stats.CS_STAT_STR:
                case Stats.CS_STAT_INT:
                case Stats.CS_STAT_WIS:
                case Stats.CS_STAT_DEX:
                case Stats.CS_STAT_CON:
                case Stats.CS_STAT_CHA:
                case Stats.CS_STAT_LEVEL:
                case Stats.CS_STAT_WC:
                case Stats.CS_STAT_AC:
                case Stats.CS_STAT_DAM:
                case Stats.CS_STAT_ARMOUR:
                case Stats.CS_STAT_FOOD:
                case Stats.CS_STAT_POW:
                case Stats.CS_STAT_GRACE:
                case Stats.CS_STAT_MAXGRACE:
                case Stats.CS_STAT_FLAGS:
                    mystats.setStat(stat, dis.readShort());
                    pos+=2;
                    break;
                case Stats.CS_STAT_SPELL_ATTUNE:
                case Stats.CS_STAT_SPELL_REPEL:
                case Stats.CS_STAT_SPELL_DENY:
                    dis.readInt(); // TODO: set spell paths
                    pos+=4;
                    break;
                default:
                    if ((stat >=100) && (stat < (100+Stats.RESIST_TYPES)))
                    {
                        mystats.setStat(stat, dis.readShort());
                        pos+=2;
                    }
                    else if ((stat>=Stats.CS_STAT_SKILLINFO)&&(stat<Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS))
                    {
                        final Skill sk = Stats.getSkill(stat);
                        if (sk == null)
                        {
                            System.err.println("ignoring skill value for unknown skill "+stat);
                        }
                        else
                        {
                            sk.setLevel(dis.readUnsignedByte());
                            sk.setExperience(dis.readLong());
                        }
                        pos+=9;
                    }
                    else
                    {
                        throw new IOException("unknown stat value: "+stat);
                    }
                    break;
            }
        }
        CrossfireCommandStatsEvent evt = new CrossfireCommandStatsEvent(new Object(),mystats);
        Iterator<CrossfireStatsListener> it = mylisteners_stats.iterator();
        while (it.hasNext())
        {
            it.next().CommandStatsReceived(evt);
        }
    }
}

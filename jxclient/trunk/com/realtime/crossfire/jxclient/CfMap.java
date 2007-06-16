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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class CfMap
{
    private static Faces myfaces;

    private static List<CrossfireMap1Listener> mylisteners_map1 = new ArrayList<CrossfireMap1Listener>();

    private static List<CrossfireNewmapListener> mylisteners_newmap = new ArrayList<CrossfireNewmapListener>();

    private static List<CrossfireMapscrollListener> mylisteners_mapscroll = new ArrayList<CrossfireMapscrollListener>();

    private static List<CrossfireMagicmapListener> mylisteners_magicmap = new ArrayList<CrossfireMagicmapListener>();

    private static CfMapSquare[][] map = new CfMapSquare[CrossfireServerConnection.MAP_WIDTH+20][CrossfireServerConnection.MAP_HEIGHT+20];

    public static List<CrossfireMap1Listener> getCrossfireMap1Listeners()
    {
        return mylisteners_map1;
    }

    public static List<CrossfireNewmapListener> getCrossfireNewmapListeners()
    {
        return mylisteners_newmap;
    }

    public static List<CrossfireMapscrollListener> getCrossfireMapscrollListeners()
    {
        return mylisteners_mapscroll;
    }

    public static List<CrossfireMagicmapListener> getCrossfireMagicmapListeners()
    {
        return mylisteners_magicmap;
    }

    static
    {
        for (int x = 0; x < CrossfireServerConnection.MAP_WIDTH+20; x++)
        {
            for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT+20; y++)
            {
                map[x][y] = new CfMapSquare(x, y);
            }
        }
    }

    public static void magicmap(DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        System.out.println("**************** MAGIC MAPPING ********************");
        dis.readFully(buf);

        String str = new String(buf);
        String packs[] = str.split(" ", 5);
        CrossfireCommandMagicmapEvent evt = new CrossfireCommandMagicmapEvent(new Object(),
            Integer.parseInt(packs[0]),
            Integer.parseInt(packs[1]),
            Integer.parseInt(packs[2]),
            Integer.parseInt(packs[3]),
            packs[4].getBytes());
        Iterator<CrossfireMagicmapListener> it = mylisteners_magicmap.iterator();
        while (it.hasNext())
        {
            it.next().commandMagicmapReceived(evt);
        }
    }

    public static void newMap(CrossfireServerConnection crossfireServerConnection) throws IOException
    {
//        long stime = System.nanoTime();

        for (int x = 0; x < CrossfireServerConnection.MAP_WIDTH+20; x++)
        {
            for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT+20; y++)
            {
                map[x][y].clear();
            }
        }

        crossfireServerConnection.sendMapredraw();
        CrossfireCommandNewmapEvent evt = new CrossfireCommandNewmapEvent(new Object());
        Iterator<CrossfireNewmapListener> it = mylisteners_newmap.iterator();
        while (it.hasNext())
        {
            it.next().commandNewmapReceived(evt);
        }

//        long etime = System.nanoTime();
//        System.out.println("Free Memory before Newmap GC:"+Runtime.getRuntime().freeMemory()/1024+" KB");
        System.gc();
//        long egtime = System.nanoTime();
//        System.out.println("Free Memory after Newmap GC:"+Runtime.getRuntime().freeMemory()/1024+" KB");
//        System.out.println("Cleaning complete, Cleaning time:"+(etime-stime)/1000000+"ms, GC:"+(egtime-etime)/1000000+"ms.");
    }

    public static void scroll(final int dx, final int dy) throws IOException
    {
        //System.out.println("--------------------------------------------------");
        int mx = CrossfireServerConnection.MAP_WIDTH+20;
        int my = CrossfireServerConnection.MAP_HEIGHT+20;

        if (dx >= 0)
        {
            if (dy >= 0)
            {
                /*for (int i = dx; i < mx; i++)
                {
                    for (int j = dy; j < my; j++)
                    {
                        map[i][j].copy(map[i-dx][j-dy]);
                        map[i][j].clear();
                    }
                }*/

                for (int i = dx; i < mx; i++)
                {
                    for (int j = dy; j < my; j++)
                    {
                        map[i-dx][j-dy] = map[i][j];
                        map[i-dx][j-dy].setPos(i-dx, j-dy);
                        map[i][j] = null;
                    }
                }

                for(int i = 0; i < mx; i++)
                {
                    for (int j = 0; j < my; j++)
                    {
                        if (map[i][j] == null)
                            map[i][j] = new CfMapSquare(i, j);
                        map[i][j].dirty();
                    }
                }
            }
            else
            {
                /*for (int i = dx; i < mx; i++)
                {
                    for (int j = my+dy-1; j >= 0; j--)
                    {
                        map[i][j].copy(map[i-dx][j-dy]);
                        map[i][j].clear();
                    }
                }*/

                for (int i = dx; i < mx; i++)
                {
                    for (int j = my+dy-1; j >= 0; j--)
                    {
                        map[i-dx][j-dy] = map[i][j];
                        map[i-dx][j-dy].setPos(i-dx, j-dy);
                        map[i][j] = null;
                    }
                }

                for (int i = 0; i < mx; i++)
                {
                    for (int j = 0; j < my; j++)
                    {
                        if (map[i][j] == null)
                            map[i][j] = new CfMapSquare(i, j);
                        map[i][j].dirty();
                    }
                }
            }
        }
        else
        {
            if (dy >= 0)
            {
                /*for (int i = mx+dx-1; i >= 0; i--)
                {
                    for (int j = dy; j < my; j++)
                    {
                        map[i][j].copy(map[i-dx][j-dy]);
                        map[i][j].clear();
                    }
                }
                */

                for (int i = mx+dx-1; i >= 0; i--)
                {
                    for (int j = dy; j < my; j++)
                    {
                        map[i-dx][j-dy] = map[i][j];
                        map[i-dx][j-dy].setPos(i-dx, j-dy);
                        map[i][j] = null;
                    }
                }

                for (int i = 0; i < mx; i++)
                {
                    for (int j = 0; j < my; j++)
                    {
                        if (map[i][j] == null)
                            map[i][j] = new CfMapSquare(i, j);
                        map[i][j].dirty();
                    }
                }
            }
            else
            {
                /*for (int i = mx+dx-1; i >= 0; i--)
                {
                    for (int j = my+dy-1; j >= 0; j--)
                    {
                        map[i][j].copy(map[i-dx][j-dy]);
                        map[i][j].clear();
                    }
                }
                */

                for (int i = mx+dx-1; i >= 0; i--)
                {
                    for (int j = my+dy-1; j >= 0; j--)
                    {
                        map[i-dx][j-dy] = map[i][j];
                        map[i-dx][j-dy].setPos(i-dx, j-dy);
                        map[i][j] = null;
                    }
                }

                for (int i = 0; i < mx; i++)
                {
                    for (int j = 0; j < my; j++)
                    {
                        if (map[i][j] == null)
                            map[i][j] = new CfMapSquare(i, j);
                        map[i][j].dirty();
                    }
                }
            }
        }

        CrossfireCommandMapscrollEvent evt = new CrossfireCommandMapscrollEvent(new Object(), dx, dy);

        Iterator<CrossfireMapscrollListener> it = mylisteners_mapscroll.iterator();
        while (it.hasNext())
        {
            it.next().commandMapscrollReceived(evt);
        }
    }

    public static void changeSquare(int x, int y, int z, int darkness, Face face)
    {
        map[x][y].setDarkness(darkness);
        map[x][y].setFace(face, z);
        map[x][y].dirty();
    }

    public static void map1(DataInputStream dis) throws IOException
    {
        int len = dis.available();
        int pos = 0;
        List<CfMapSquare> l = new LinkedList<CfMapSquare>();
        int[] faces = new int[CrossfireServerConnection.NUM_LAYERS];
        while (pos < len)
        {
            int coord = dis.readUnsignedShort();
            int x = 10+(coord>>10)&0x3f;
            int y = 10+(coord>>4)&0x3f;
            int isclear = coord&0xf;
            int isdark = coord&0x8;
            int mask = coord&0x7;
            int darkness = -1;
            pos += 2;
            /*System.out.println("------------------------------------------");
            System.out.println("Packet map received");
            System.out.println("X:"+x+" Y:"+y);
            System.out.println("Clear:"+isclear+" Dark:"+isdark);*/
            if (isclear == 0)
            {
                continue;
            }

            if (isdark != 0)
            {
                darkness = dis.readUnsignedByte();
                //System.out.println("Darkness:"+darkness+ "("+x+";"+y+")");
                pos++;
            }

            for (int layer = CrossfireServerConnection.NUM_LAYERS-1; layer >= 0; layer--)
            {
                if ((mask&(1<<layer)) != 0)
                {
                    faces[(CrossfireServerConnection.NUM_LAYERS-1)-layer] = dis.readUnsignedShort();
                    Faces.ensureFaceExists(faces[(CrossfireServerConnection.NUM_LAYERS-1)-layer]);
                    Face ff = Faces.getFace(faces[(CrossfireServerConnection.NUM_LAYERS-1)-layer]);
                    /*if (ff != null)
                        System.out.println("Layer face :"+ff.getName());*/
                    pos += 2;
                }
                else
                {
                    faces[(CrossfireServerConnection.NUM_LAYERS-1)-layer] = -1;
                    //System.out.println("Empty face on the square");
                }
            }
            for (int layer = 0; layer < CrossfireServerConnection.NUM_LAYERS; layer++)
            {
                if (faces[layer] < 0)
                {
                    continue;
                }
                Faces.ensureFaceExists(faces[layer]);
                Face f = Faces.getFace(faces[layer]);
                changeSquare(x, y, layer, darkness, f);
                l.add(map[x][y]);
            }
            CrossfireCommandMap1Event evt = new CrossfireCommandMap1Event(new Object(), l);
            Iterator<CrossfireMap1Listener> it = mylisteners_map1.iterator();
            while (it.hasNext())
            {
                it.next().commandMap1Received(evt);
            }
        }
    }

    public static CfMapSquare[][] getMap()
    {
        return map;
    }

    public static void invalidate()
    {
        for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT+20; y++)
        {
            for (int x = 0; x < CrossfireServerConnection.MAP_WIDTH+20; x++)
            {
                map[x][y].dirty();
            }
        }
        CrossfireCommandNewmapEvent evt = new CrossfireCommandNewmapEvent(new Object());
        Iterator<CrossfireNewmapListener> it = mylisteners_newmap.iterator();
        while (it.hasNext())
        {
            it.next().commandNewmapReceived(evt);
        }
    }

    public static void updateFace(int pixnum)
    {
        List<CfMapSquare> l = new LinkedList<CfMapSquare>();

        //System.out.println("Face update: "+pixnum);
        for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT+20; y++)
        {
            for (int x = 0; x < CrossfireServerConnection.MAP_WIDTH+20; x++)
            {
                for (int z = 0; z < CrossfireServerConnection.NUM_LAYERS; z++)
                {
                    if (map[x][y].getFace(z) != null)
                    if (map[x][y].getFace(z).getID() == pixnum)
                    {
                        map[x][y].dirty();
                        //l.add(new CfMapSquare(x, y, z, 0, myfaces.getFace(pixnum)));
                    }
                }
            }
        }
        CrossfireCommandMap1Event evt = new CrossfireCommandMap1Event(new Object(), l);
        Iterator<CrossfireMap1Listener> it = mylisteners_map1.iterator();
        while (it.hasNext())
        {
            it.next().commandMap1Received(evt);
        }
    }
}

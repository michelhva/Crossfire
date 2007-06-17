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

    private static final List<CrossfireMap1Listener> mylisteners_map1 = new ArrayList<CrossfireMap1Listener>();

    private static final List<CrossfireNewmapListener> mylisteners_newmap = new ArrayList<CrossfireNewmapListener>();

    private static final List<CrossfireMapscrollListener> mylisteners_mapscroll = new ArrayList<CrossfireMapscrollListener>();

    private static final List<CrossfireMagicmapListener> mylisteners_magicmap = new ArrayList<CrossfireMagicmapListener>();

    private static final CfMapSquare[][] map = new CfMapSquare[CrossfireServerConnection.MAP_WIDTH+20][CrossfireServerConnection.MAP_HEIGHT+20];

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

    public static void magicmap(final DataInputStream dis) throws IOException
    {
        final int len = dis.available();
        final byte buf[] = new byte[len];

        System.out.println("**************** MAGIC MAPPING ********************");
        dis.readFully(buf);

        final String str = new String(buf);
        final String packs[] = str.split(" ", 5);
        final CrossfireCommandMagicmapEvent evt = new CrossfireCommandMagicmapEvent(new Object(),
            Integer.parseInt(packs[0]),
            Integer.parseInt(packs[1]),
            Integer.parseInt(packs[2]),
            Integer.parseInt(packs[3]),
            packs[4].getBytes());
        final Iterator<CrossfireMagicmapListener> it = mylisteners_magicmap.iterator();
        while (it.hasNext())
        {
            it.next().commandMagicmapReceived(evt);
        }
    }

    public static void newMap(final CrossfireServerConnection crossfireServerConnection) throws IOException
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
        final CrossfireCommandNewmapEvent evt = new CrossfireCommandNewmapEvent(new Object());
        final Iterator<CrossfireNewmapListener> it = mylisteners_newmap.iterator();
        while (it.hasNext())
        {
            it.next().commandNewmapReceived(evt);
        }

//        final long etime = System.nanoTime();
//        System.out.println("Free Memory before Newmap GC:"+Runtime.getRuntime().freeMemory()/1024+" KB");
        System.gc();
//        final long egtime = System.nanoTime();
//        System.out.println("Free Memory after Newmap GC:"+Runtime.getRuntime().freeMemory()/1024+" KB");
//        System.out.println("Cleaning complete, Cleaning time:"+(etime-stime)/1000000+"ms, GC:"+(egtime-etime)/1000000+"ms.");
    }

    public static void scroll(final int dx, final int dy) throws IOException
    {
        //System.out.println("--------------------------------------------------");
        final int mx = CrossfireServerConnection.MAP_WIDTH+20;
        final int my = CrossfireServerConnection.MAP_HEIGHT+20;

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

        final CrossfireCommandMapscrollEvent evt = new CrossfireCommandMapscrollEvent(new Object(), dx, dy);

        final Iterator<CrossfireMapscrollListener> it = mylisteners_mapscroll.iterator();
        while (it.hasNext())
        {
            it.next().commandMapscrollReceived(evt);
        }
    }

    public static void changeSquare(final int x, final int y, final int z, final int darkness, final Face face)
    {
        map[x][y].setDarkness(darkness);
        map[x][y].setFace(face, z);
        map[x][y].dirty();
    }

    public static void map1(final DataInputStream dis) throws IOException
    {
        final int len = dis.available();
        int pos = 0;
        final List<CfMapSquare> l = new LinkedList<CfMapSquare>();
        final int[] faces = new int[CrossfireServerConnection.NUM_LAYERS];
        while (pos < len)
        {
            final int coord = dis.readUnsignedShort();
            final int x = 10+(coord>>10)&0x3f;
            final int y = 10+(coord>>4)&0x3f;
            final int isclear = coord&0xf;
            final int isdark = coord&0x8;
            final int mask = coord&0x7;
            final int darkness;
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
            else
            {
                darkness = -1;
            }

            for (int layer = CrossfireServerConnection.NUM_LAYERS-1; layer >= 0; layer--)
            {
                if ((mask&(1<<layer)) != 0)
                {
                    faces[(CrossfireServerConnection.NUM_LAYERS-1)-layer] = dis.readUnsignedShort();
                    Faces.ensureFaceExists(faces[(CrossfireServerConnection.NUM_LAYERS-1)-layer]);
                    final Face ff = Faces.getFace(faces[(CrossfireServerConnection.NUM_LAYERS-1)-layer]);
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
            final CrossfireCommandMap1Event evt = new CrossfireCommandMap1Event(new Object(), l);
            final Iterator<CrossfireMap1Listener> it = mylisteners_map1.iterator();
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
        final CrossfireCommandNewmapEvent evt = new CrossfireCommandNewmapEvent(new Object());
        final Iterator<CrossfireNewmapListener> it = mylisteners_newmap.iterator();
        while (it.hasNext())
        {
            it.next().commandNewmapReceived(evt);
        }
    }

    public static void updateFace(final int pixnum)
    {
        final List<CfMapSquare> l = new LinkedList<CfMapSquare>();

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
        final CrossfireCommandMap1Event evt = new CrossfireCommandMap1Event(new Object(), l);
        final Iterator<CrossfireMap1Listener> it = mylisteners_map1.iterator();
        while (it.hasNext())
        {
            it.next().commandMap1Received(evt);
        }
    }
}

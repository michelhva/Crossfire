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

import java.net.*;
import java.util.*;
import java.io.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Metaserver
{
    private static String metaserver_name = "crossfire.real-time.com";
    private static int    metaserver_port = 13326;
    //private static Socket                  mysocket;
    //private static DataInputStream         in;
    private static List<MetaserverEntry> metalist = null;
    public static synchronized List<MetaserverEntry> query()
    {
        if (metalist != null)
        {
            return metalist;
        }
        metalist = new ArrayList<MetaserverEntry>();
        try
        {
            Socket mysocket = new Socket(metaserver_name, metaserver_port);
            DataInputStream in = new DataInputStream(mysocket.getInputStream());
            BufferedReader bin
                    = new BufferedReader(new InputStreamReader(in));

            String entry = "127.0.0.1|0|localhost|0|1.8.0|localhost|0|0|0";
            while(entry!=null)
            {
                entry = bin.readLine();
                if (entry != null)
                {
                    String[] entries = entry.split("\\|");
                    MetaserverEntry me = new MetaserverEntry(
                            entries[0], entries[2], entries[5], entries[4],
                            Integer.parseInt(entries[3]),
                            Integer.parseInt(entries[1]));
                    metalist.add(me);
                }
            }
            bin.close();
            in.close();
            mysocket.close();
        }
        catch (java.net.UnknownHostException e)
        {
            MetaserverEntry me = new MetaserverEntry("127.0.0.1", "localhost", "Localhost",
                    "unknown", 0,0);
            metalist.add(me);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        return metalist;
    }
}

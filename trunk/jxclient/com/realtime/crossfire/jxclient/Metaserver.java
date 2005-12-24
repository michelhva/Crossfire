package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;

import java.net.*;
import java.util.*;
import java.io.*;

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

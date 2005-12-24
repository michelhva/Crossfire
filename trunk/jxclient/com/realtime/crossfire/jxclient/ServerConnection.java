package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
//import com.sixlegs.png.*;

public class ServerConnection extends Thread
{

    private static Socket                  mysocket;
    private static DataInputStream         in;
    private static DataOutputStream        out;
    private static DataOutputStream        bout;
    private static ByteArrayOutputStream   bos;
    private static byte[]                  buf = null;

    private static java.util.List<CrossfireGoodbyeListener> mylisteners_goodbye =
            new ArrayList<CrossfireGoodbyeListener>();
    private static java.util.List<CrossfireAddmeSuccessListener> mylisteners_addme_success =
            new ArrayList<CrossfireAddmeSuccessListener>();
    private static java.util.List<CrossfireDrawinfoListener> mylisteners_drawinfo =
            new ArrayList<CrossfireDrawinfoListener>();
    private static java.util.List<CrossfireDrawextinfoListener> mylisteners_drawextinfo =
            new ArrayList<CrossfireDrawextinfoListener>();
    private static java.util.List<CrossfireAddmeFailedListener> mylisteners_addme_failed =
            new ArrayList<CrossfireAddmeFailedListener>();
    private static java.util.List<CrossfireQueryListener> mylisteners_query =
            new ArrayList<CrossfireQueryListener>();

    private static String                  myhost = new String("localhost");
    private static int                     myport = 13327;

    public static final int         STATUS_UNCONNECTED = 0;
    public static final int         STATUS_PLAYING     = 1;
    public static final int         STATUS_QUERY       = 2;
    /*public static final int         MAP_WIDTH=25;
    public static final int         MAP_HEIGHT=19;*/
    public static final int         MAP_WIDTH  =17;
    public static final int         MAP_HEIGHT =13;
    public static final int         NUM_LAYERS =3;
    public static final int         SQUARE_SIZE = 64;

    public static final int MSG_TYPE_BOOK_CLASP_1 = 1;
    public static final int MSG_TYPE_BOOK_CLASP_2 = 2;
    public static final int MSG_TYPE_BOOK_ELEGANT_1 = 3;
    public static final int MSG_TYPE_BOOK_ELEGANT_2 = 4;
    public static final int MSG_TYPE_BOOK_QUARTO_1 = 5;
    public static final int MSG_TYPE_BOOK_QUARTO_2 = 6;
    public static final int MSG_TYPE_BOOK_SPELL_EVOKER = 8;
    public static final int MSG_TYPE_BOOK_SPELL_PRAYER = 9;
    public static final int MSG_TYPE_BOOK_SPELL_PYRO = 10;
    public static final int MSG_TYPE_BOOK_SPELL_SORCERER = 11;
    public static final int MSG_TYPE_BOOK_SPELL_SUMMONER = 12;

    private static int                     mystatus = STATUS_UNCONNECTED;
    private static String mystatus_sem = new String("mystatus_sem");
    public void run()
    {
        setStatus(STATUS_PLAYING);
        try
        {
            for(;;)
            {
                readPacket();
            }
        }
        catch (Exception e)
        {
            setStatus(STATUS_UNCONNECTED);
            e.printStackTrace();
            System.exit(0);
        }
    }
    public synchronized boolean waitForData() throws IOException
    {
        return (in.available() > 0) ? true : false;
    }
    public synchronized void readPacket() throws IOException, UnknownCommandException
    {
        int len = 0;
        int off = 0;
        int i = 0;

        len = in.readUnsignedShort();
        byte[] data = new byte[len];
        byte[] cmd = null;

        in.readFully(data);
        for (i=0;i<len;i++)
        {
            if (data[i]==0x20)
            {
                cmd = new byte[i+1];
                for(int j=0;j<i;j++)
                    cmd[j]=data[j];
                break;
            }
        }
        String cmdstr = (cmd != null) ? new String(cmd) : new String(data);
        DataInputStream dis = null;
        if (i<len)
        {
            dis = new DataInputStream(new ByteArrayInputStream(data));
            dis.skipBytes(i+1);
        }
        command(cmdstr, dis);
    }
    public static void writePacket(String str) throws IOException
    {
        synchronized(bout)
        {
            bout.writeShort((short)str.length());
            bout.writeBytes(str);
            synchronized(out)
            {
                bos.writeTo(out);
                out.flush();
                bos.reset();
            }
        }
    }
    public ServerConnection(String hostname, int port)
    {
        buf = new byte[2];
        myhost = hostname;
        myport = port;
    }
    public void connect()
    {
        try
        {
            mysocket = new Socket(myhost, myport);
            in = new DataInputStream(mysocket.getInputStream());
            out = new DataOutputStream(mysocket.getOutputStream());
            bos = new ByteArrayOutputStream(0);
            bout = new DataOutputStream(bos);
            start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }

    }
    public synchronized void addCrossfireGoodbyeListener(CrossfireGoodbyeListener listener)
    {
        mylisteners_goodbye.add(listener);
    }
    public synchronized void removeCrossfireGoodbyeListener(CrossfireGoodbyeListener listener)
    {
        mylisteners_goodbye.remove(listener);
    }
    public synchronized void addCrossfireAddmeSuccessListener(CrossfireAddmeSuccessListener listener)
    {
        mylisteners_addme_success.add(listener);
    }
    public synchronized void removeCrossfireAddmeSuccessListener(CrossfireAddmeSuccessListener listener)
    {
        mylisteners_addme_success.remove(listener);
    }
    public synchronized void addCrossfireDelitemListener(CrossfireDelitemListener listener)
    {
        ItemsList.getCrossfireDelitemListeners().add(listener);
    }
    public synchronized void removeCrossfireDelitemListener(CrossfireDelitemListener listener)
    {
        ItemsList.getCrossfireDelitemListeners().remove(listener);
    }
    public synchronized void addCrossfireItem1Listener(CrossfireItem1Listener listener)
    {
        ItemsList.getCrossfireItem1Listeners().add(listener);
    }
    public synchronized void removeCrossfireItem1Listener(CrossfireItem1Listener listener)
    {
        ItemsList.getCrossfireItem1Listeners().remove(listener);
    }
    public synchronized void addCrossfireItem2Listener(CrossfireItem2Listener listener)
    {
        ItemsList.getCrossfireItem2Listeners().add(listener);
    }
    public synchronized void removeCrossfireItem2Listener(CrossfireItem2Listener listener)
    {
        ItemsList.getCrossfireItem2Listeners().remove(listener);
    }
    public synchronized void addCrossfireMap1Listener(CrossfireMap1Listener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMap1Listeners().add(listener);
    }
    public synchronized void removeCrossfireMap1Listener(CrossfireMap1Listener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMap1Listeners().remove(listener);
    }
    public synchronized void addCrossfireNewmapListener(CrossfireNewmapListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireNewmapListeners().add(listener);
    }
    public synchronized void removeCrossfireNewmapListener(CrossfireNewmapListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireNewmapListeners().remove(listener);
    }
    public synchronized void addCrossfirePlayerListener(CrossfirePlayerListener listener)
    {
        CfPlayer.getCrossfirePlayerListeners().add(listener);
    }
    public synchronized void removeCrossfirePlayerListener(CrossfirePlayerListener listener)
    {
        CfPlayer.getCrossfirePlayerListeners().remove(listener);
    }
    public synchronized void addCrossfireStatsListener(CrossfireStatsListener listener)
    {
        CfPlayer.getCrossfireStatsListeners().add(listener);
    }
    public synchronized void removeCrossfireStatsListener(CrossfireStatsListener listener)
    {
        CfPlayer.getCrossfireStatsListeners().remove(listener);
    }
    public synchronized void addCrossfireUpditemListener(CrossfireUpditemListener listener)
    {
        ItemsList.getCrossfireUpditemListeners().add(listener);
    }
    public synchronized void removeCrossfireUpditemListener(CrossfireUpditemListener listener)
    {
        ItemsList.getCrossfireUpditemListeners().remove(listener);
    }
    public synchronized void addCrossfireMapscrollListener(CrossfireMapscrollListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMapscrollListeners().add(listener);
    }
    public synchronized void removeCrossfireMapscrollListener(CrossfireMapscrollListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMapscrollListeners().remove(listener);
    }
    public synchronized void addCrossfireDelinvListener(CrossfireDelinvListener listener)
    {
        ItemsList.getCrossfireDelinvListeners().add(listener);
    }
    public synchronized void removeCrossfireDelinvListener(CrossfireDelinvListener listener)
    {
        ItemsList.getCrossfireDelinvListeners().remove(listener);
    }
    public synchronized void addCrossfireDrawinfoListener(CrossfireDrawinfoListener listener)
    {
        mylisteners_drawinfo.add(listener);
    }
    public synchronized void removeCrossfireDrawinfoListener(CrossfireDrawinfoListener listener)
    {
        mylisteners_drawinfo.remove(listener);
    }
    public synchronized void addCrossfireDrawextinfoListener(CrossfireDrawextinfoListener listener)
    {
        mylisteners_drawextinfo.add(listener);
    }
    public synchronized void removeCrossfireDrawextinfoListener(CrossfireDrawextinfoListener listener)
    {
        mylisteners_drawextinfo.remove(listener);
    }
    public synchronized void addCrossfireMagicmapListener(CrossfireMagicmapListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMagicmapListeners().add(listener);
    }
    public synchronized void removeCrossfireMagicmapListener(CrossfireMagicmapListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMagicmapListeners().remove(listener);
    }
    public synchronized void addCrossfireAddmeFailedListener(CrossfireAddmeFailedListener listener)
    {
        mylisteners_addme_failed.add(listener);
    }
    public synchronized void removeCrossfireAddmeFailedListener(CrossfireAddmeFailedListener listener)
    {
        mylisteners_addme_failed.remove(listener);
    }
    public synchronized void addCrossfireQueryListener(CrossfireQueryListener listener)
    {
        mylisteners_query.add(listener);
    }
    public synchronized void removeCrossfireQueryListener(CrossfireQueryListener listener)
    {
        mylisteners_query.remove(listener);
    }
    public void setStatus(int nstatus)
    {
        synchronized(mystatus_sem)
        {
            mystatus = nstatus;
        }
    }
    public int getStatus()
    {
        synchronized(mystatus_sem)
        {
            return mystatus;
        }
    }

    private void command(String cmd, DataInputStream dis) throws IOException, UnknownCommandException
    {
        if (cmd.startsWith("image"))
            cmd_image(cmd, dis);
        else if (cmd.startsWith("goodbye"))
            cmd_goodbye(cmd, dis);
        else if (cmd.startsWith("addme_success"))
            cmd_addme_success(cmd, dis);
        else if (cmd.startsWith("delitem"))
            ItemsList.removeItem(dis);
        else if (cmd.startsWith("delinv"))
            ItemsList.cleanInventory(dis);
        else if (cmd.startsWith("face1"))
            Faces.setFace1(dis);
        else if (cmd.startsWith("item1"))
            ItemsList.addItems(dis);
        else if (cmd.startsWith("item2"))
            ItemsList.addItems2(dis);
        else if (cmd.startsWith("item"))
            cmd_item(cmd, dis);
        else if (cmd.startsWith("map1"))
            com.realtime.crossfire.jxclient.Map.map1(dis);
        else if (cmd.startsWith("newmap"))
            com.realtime.crossfire.jxclient.Map.newMap(dis);
        else if (cmd.startsWith("player"))
            ItemsList.createPlayer(dis);
        else if (cmd.startsWith("stats"))
        {
            //if (ItemsList.getPlayer()!=null)
                CfPlayer.setStats(dis);
        }
        else if (cmd.startsWith("upditem"))
            ItemsList.updateItem(dis);
        else if (cmd.startsWith("map_scroll"))
            com.realtime.crossfire.jxclient.Map.scroll(dis);
        else if (cmd.startsWith("drawinfo"))
        {
            cmd_drawinfo(cmd, dis);
        }
        else if (cmd.startsWith("anim"))
        {
            cmd_anim(cmd, dis);
        }
        else if (cmd.startsWith("version"))
        {
            cmd_version(cmd, dis);
        }
        else if (cmd.startsWith("magicmap"))
            com.realtime.crossfire.jxclient.Map.magicmap(dis);
        else if (cmd.startsWith("addme_failed"))
        {
            cmd_addme_failed(cmd, dis);
        }
        else if (cmd.startsWith("setup"))
        {
            cmd_setup(cmd, dis);
        }
        else if (cmd.startsWith("query"))
        {
            cmd_query(cmd, dis);
        }
        else if (cmd.startsWith("ExtendedTextSet"))
        {
            cmd_ExtendedTextSet(cmd, dis);
        }
        else if (cmd.startsWith("ExtendedInfoSet"))
        {
            cmd_ExtendedInfoSet(cmd, dis);
        }
        else if (cmd.startsWith("drawextinfo"))
        {
            cmd_drawextinfo(cmd, dis);
        }
        else if (cmd.startsWith("smooth"))
        {
            cmd_smooth(cmd, dis);
        }
        else if (cmd.startsWith("mapextended"))
        {
            cmd_mapextended(cmd, dis);
        }
        else if (cmd.startsWith("sound"))
        {
            cmd_sound(cmd, dis);
        }
        else if (cmd.startsWith("replyinfo"))
        {
            cmd_replyinfo(cmd, dis);
        }
        else
        {
            throw new UnknownCommandException("Unknown command: "+cmd);
        }
    }
    void cmd_image(String cmd, DataInputStream dis) throws IOException
    {
        int pixmap = Faces.setImage(dis);
        com.realtime.crossfire.jxclient.Map.updateFace(pixmap);
    }
    void cmd_goodbye(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }
    void cmd_addme_success(String cmd, DataInputStream dis) throws IOException
    {
        //System.out.println("Command: "+cmd);
    }
    void cmd_item(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }
    void cmd_sound(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }
    void cmd_drawinfo(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);
        String[] datas = (new String(buf)).split(" ",2);

        CrossfireCommandDrawinfoEvent evt = new
                CrossfireCommandDrawinfoEvent(this,datas[1], Integer.parseInt(datas[0]));
        Iterator it = mylisteners_drawinfo.iterator();
        while (it.hasNext())
        {
            ((CrossfireDrawinfoListener)it.next()).CommandDrawinfoReceived(evt);
        }
    }
    void cmd_anim(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }
    void cmd_version(String cmd, DataInputStream dis) throws IOException
    {
        writePacket("version 1023 1027 JXClient Java Client Pegasus 0.1");
        writePacket("toggleextendedtext 1 2 3 4 5 6 7");
        writePacket("setup sound 0 exp64 1 map1cmd 1 darkness 1 newmapcmd 1 facecache 1 extendedMapInfos 1 extendedTextInfos 1 itemcmd 2 mapsize "+MAP_WIDTH+"x"+MAP_HEIGHT);
        writePacket("requestinfo image_info");
        writePacket("requestinfo skill_info");
        writePacket("toggleextendedtext 1");
    }
    void cmd_replyinfo(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        //byte buf[] = new byte[len];
        BufferedReader d
                = new BufferedReader(new InputStreamReader(dis));
        String replytype = d.readLine();
        if (replytype.equals("image_info"))
        {
            int nrpics = Integer.parseInt(d.readLine());
            System.out.println("Number of pics:"+nrpics);
            writePacket("addme");
        }
        else if (replytype.equals("skill_info"))
        {
            String r = new String("");
            while(r!=null)
            {
                r = d.readLine();
                if (r!=null)
                {
                    String[] sk = r.split(":");
                    Stats.addSkill(Integer.parseInt(sk[0])-Stats.CS_STAT_SKILLINFO,sk[1]);
                }
            }
        }
    }
    void cmd_addme_failed(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }
    void cmd_setup(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);

        String str = new String(buf);
    }
    void cmd_query(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);

        String[] datas = (new String(buf)).split(" ",2);

        CrossfireCommandQueryEvent evt = new
                CrossfireCommandQueryEvent(this,datas[1], Integer.parseInt(datas[0]));
        Iterator it = mylisteners_query.iterator();
        setStatus(STATUS_QUERY);
        while (it.hasNext())
        {
            ((CrossfireQueryListener)it.next()).CommandQueryReceived(evt);
        }
    }
    void cmd_ExtendedTextSet(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);

        String str = new String(buf);
    }
    void cmd_ExtendedInfoSet(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }
    void cmd_drawextinfo(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);

        String[] datas = (new String(buf)).split(" ",4);

        CrossfireCommandDrawextinfoEvent evt = new
                CrossfireCommandDrawextinfoEvent(this,
                    Integer.parseInt(datas[0]), Integer.parseInt(datas[1]),
                    Integer.parseInt(datas[2]), datas[3]);
        Iterator it = mylisteners_drawextinfo.iterator();
        while (it.hasNext())
        {
            ((CrossfireDrawextinfoListener)it.next()).CommandDrawextinfoReceived(evt);
        }
    }
    void cmd_smooth(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }
    void cmd_mapextended(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }
    public java.util.List getItems(int location)
    {
        return ItemsList.getItems(location);
    }
    public CfPlayer getPlayer()
    {
        return ItemsList.getPlayer();
    }
}

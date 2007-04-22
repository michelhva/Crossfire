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

/**
 * One of the two most important classes, ServerConnection performs most of the
 * network-related work. It either decodes commands sent by the server itself,
 * or delegates their processing to other classes, like Map or Faces.
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
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

    private static java.util.List<CrossfireScriptMonitorListener> scripts_monitor =
            new ArrayList<CrossfireScriptMonitorListener>();

    private static String                  myhost = new String("localhost");
    private static int                     myport = 13327;

    /**
     * Represents the unconnected status of the client, which is the first to
     * happen during a normal gaming session.
     * @since 1.0
     */
    public static final int         STATUS_UNCONNECTED = 0;

    /**
     * Represents the status of the client that is used during play.
     * @since 1.0
     */
    public static final int         STATUS_PLAYING     = 1;

    /**
     * Represents the status of the client that is displaying a Query dialog.
     * @since 1.0
     */
    public static final int         STATUS_QUERY       = 2;

    /**
     * The width of the map displayed, in squares.
     * @since 1.0
     */
    public static final int         MAP_WIDTH  =17;

    /**
     * The height of the map displayed, in squares.
     * @since 1.0
     */
    public static final int         MAP_HEIGHT =13;

    /**
     * The total number of map layers to display.
     * @since 1.0
     */
    public static final int         NUM_LAYERS =3;

    /**
     * The pixel size of the gaming squares. Notice that they are supposed to
     * be *squares*, so only a single value is needed :)
     * @since 1.0
     */
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

    /**
     * The Thread Main loop. ServerConnection contains its own Thread, so it
     * can monitor the socket content in parallel with the GUI handling loop.
     * @since 1.0
     */
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

    /**
     * Tests if there are some data waiting on the socket. Just a simple wrapper
     * for an InputStream.available() check.
     * @return True if there is some data waiting, else false.
     * @since 1.0
     */
    public synchronized boolean waitForData() throws IOException
    {
        return (in.available() > 0) ? true : false;
    }

    /**
     * Reads the next available packet sent by the Crossfire server on the
     * network.
     * @since 1.0
     */
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

    /**
     * Writes a Crossfire Message on the socket, so it is sent to the server.
     * @param str The message to sent.
     * @since 1.0
     */
    public static void writePacket(String str) throws IOException
    {
        synchronized(bout)
        {
            Iterator<CrossfireScriptMonitorListener> it = scripts_monitor.iterator();
            while (it.hasNext())
            {
                it.next().commandSent(str);
            }

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

    /**
     * Creates a new ServerConnection that will be used to communicate with the
     * server located at the given hostname:port address.
     * Note that the connection is not performed by the constructor - you need
     * to call the connect() method.
     * @param hostname The hostname (or IP address) of the server.
     * @param port The TCP port on which the server is listening.
     * @since 1.0
     */
    public ServerConnection(String hostname, int port)
    {
        buf = new byte[2];
        myhost = hostname;
        myport = port;
    }

    /**
     * Attempts to connect the client to the server using the previously defined
     * hostname:port address.
     * @since 1.0
     */
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

    /**
     * Adds a new listener monitoring the
     * goodbye S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireGoodbyeListener(CrossfireGoodbyeListener listener)
    {
        mylisteners_goodbye.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * goodbye S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireGoodbyeListener(CrossfireGoodbyeListener listener)
    {
        mylisteners_goodbye.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * addme_success S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireAddmeSuccessListener(CrossfireAddmeSuccessListener listener)
    {
        mylisteners_addme_success.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * addme_success S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireAddmeSuccessListener(CrossfireAddmeSuccessListener listener)
    {
        mylisteners_addme_success.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * map1 S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireMap1Listener(CrossfireMap1Listener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMap1Listeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * map1 S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireMap1Listener(CrossfireMap1Listener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMap1Listeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * newmap S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireNewmapListener(CrossfireNewmapListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireNewmapListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * newmap S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireNewmapListener(CrossfireNewmapListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireNewmapListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * player S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfirePlayerListener(CrossfirePlayerListener listener)
    {
        CfPlayer.getCrossfirePlayerListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * player S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfirePlayerListener(CrossfirePlayerListener listener)
    {
        CfPlayer.getCrossfirePlayerListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * stats S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireStatsListener(CrossfireStatsListener listener)
    {
        CfPlayer.getCrossfireStatsListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * stats S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireStatsListener(CrossfireStatsListener listener)
    {
        CfPlayer.getCrossfireStatsListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * mapscroll S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireMapscrollListener(CrossfireMapscrollListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMapscrollListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * mapscroll S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireMapscrollListener(CrossfireMapscrollListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMapscrollListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * drawinfo S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireDrawinfoListener(CrossfireDrawinfoListener listener)
    {
        mylisteners_drawinfo.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * drawinfo S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireDrawinfoListener(CrossfireDrawinfoListener listener)
    {
        mylisteners_drawinfo.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * drawextinfo S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireDrawextinfoListener(CrossfireDrawextinfoListener listener)
    {
        mylisteners_drawextinfo.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * drawextinfo S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireDrawextinfoListener(CrossfireDrawextinfoListener listener)
    {
        mylisteners_drawextinfo.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * magicmap S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireMagicmapListener(CrossfireMagicmapListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMagicmapListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * magicmap S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireMagicmapListener(CrossfireMagicmapListener listener)
    {
        com.realtime.crossfire.jxclient.Map.getCrossfireMagicmapListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * addme_failed S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireAddmeFailedListener(CrossfireAddmeFailedListener listener)
    {
        mylisteners_addme_failed.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * addme_failed S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireAddmeFailedListener(CrossfireAddmeFailedListener listener)
    {
        mylisteners_addme_failed.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * query S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireQueryListener(CrossfireQueryListener listener)
    {
        mylisteners_query.add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * query S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireQueryListener(CrossfireQueryListener listener)
    {
        mylisteners_query.remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * addspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireSpellAddedListener(CrossfireSpellAddedListener listener)
    {
        ItemsList.getCrossfireSpellAddedListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * addspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireSpellAddedListener(CrossfireSpellAddedListener listener)
    {
        ItemsList.getCrossfireSpellAddedListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * updspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireSpellUpdatedListener(CrossfireSpellUpdatedListener listener)
    {
        ItemsList.getCrossfireSpellUpdatedListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * updspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireSpellUpdatedListener(CrossfireSpellUpdatedListener listener)
    {
        ItemsList.getCrossfireSpellUpdatedListeners().remove(listener);
    }

    /**
     * Adds a new listener monitoring the
     * delspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void addCrossfireSpellRemovedListener(CrossfireSpellRemovedListener listener)
    {
        ItemsList.getCrossfireSpellRemovedListeners().add(listener);
    }

    /**
     * Removes the given listener from the list of objects listening to the
     * delspell S->C messages.
     * @param listener The listener to remove.
     * @since 1.0
     */
    public synchronized void removeCrossfireSpellRemovedListener(CrossfireSpellRemovedListener listener)
    {
        ItemsList.getCrossfireSpellRemovedListeners().remove(listener);
    }

    /**
     * Sets the current status of the client to the given value. See the various
     * STATUS_ constants.
     * @param nstatus The new status value.
     * @since 1.0
     */
    public void setStatus(int nstatus)
    {
        synchronized(mystatus_sem)
        {
            mystatus = nstatus;
        }
    }

    /**
     * Gets the current status of the client. See the STATUS_ constants.
     * @since 1.0
     * @return A value representing the current status.
     */
    public int getStatus()
    {
        synchronized(mystatus_sem)
        {
            return mystatus;
        }
    }

    /**
     * This is the main command handler, in which the command received is
     * decoded, and the appropriate method called.
     * @param cmd The S->C command received.
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
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
        else if (cmd.startsWith("addspell"))
        {
            cmd_addspell(cmd, dis);
        }
        else if (cmd.startsWith("updspell"))
        {
            cmd_updspell(cmd, dis);
        }
        else if (cmd.startsWith("delspell"))
        {
            cmd_delspell(cmd, dis);
        }
        else
        {
            throw new UnknownCommandException("Unknown command: "+cmd);
        }
    }

    /**
     * Handles the image server to client command.
     * @param cmd The S->C command, in this case "addspell".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_addspell(String cmd, DataInputStream dis) throws IOException
    {
        ItemsList.addSpell(dis);
    }

    /**
     * Handles the image server to client command.
     * @param cmd The S->C command, in this case "updspell".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.3
     */
    void cmd_updspell(String cmd, DataInputStream dis) throws IOException
    {
        ItemsList.updateSpell(dis);
    }

    /**
     * Handles the image server to client command.
     * @param cmd The S->C command, in this case "delspell".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.3
     */
    void cmd_delspell(String cmd, DataInputStream dis) throws IOException
    {
        ItemsList.deleteSpell(dis);
    }

    /**
     * Handles the image server to client command.
     * @param cmd The S->C command, in this case "image".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.3
     */
    void cmd_image(String cmd, DataInputStream dis) throws IOException
    {
        int pixmap = Faces.setImage(dis);
        com.realtime.crossfire.jxclient.Map.updateFace(pixmap);
    }

    /**
     * Handles the goodbye server to client command.
     * @param cmd The S->C command, in this case "goodbye".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_goodbye(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }

    /**
     * Handles the addme_success server to client command.
     * @param cmd The S->C command, in this case "addme_success".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_addme_success(String cmd, DataInputStream dis) throws IOException
    {
        //System.out.println("Command: "+cmd);
    }

    /**
     * Handles the item server to client command.
     * @param cmd The S->C command, in this case "item".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_item(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }

    /**
     * Handles the sound server to client command
     * @param cmd The S->C command, in this case "sound".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_sound(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }

    /**
     * Handles the drawinfo server to client command.
     * @param cmd The S->C command, in this case "drawinfo".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_drawinfo(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);
        String[] datas = (new String(buf)).split(" ",2);

        CrossfireCommandDrawinfoEvent evt = new
                CrossfireCommandDrawinfoEvent(this,datas[1], Integer.parseInt(datas[0]));
        Iterator<CrossfireDrawinfoListener> it = mylisteners_drawinfo.iterator();
        while (it.hasNext())
        {
            it.next().CommandDrawinfoReceived(evt);
        }
    }

    /**
     * Handles the anim server to client command.
     * @param cmd The S->C command, in this case "anim".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_anim(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }

    /**
     * Handles the version server to client command.
     * @param cmd The S->C command, in this case "version".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_version(String cmd, DataInputStream dis) throws IOException
    {
        writePacket("version 1023 1027 JXClient Java Client Pegasus 0.1");
        writePacket("toggleextendedtext 1 2 3 4 5 6 7");
        writePacket("setup sound 0 exp64 1 map1cmd 1 darkness 1 newmapcmd 1 facecache 1 extendedMapInfos 1 extendedTextInfos 1 itemcmd 2 spellmon 1 mapsize "+MAP_WIDTH+"x"+MAP_HEIGHT);
        writePacket("requestinfo image_info");
        writePacket("requestinfo skill_info");
        writePacket("toggleextendedtext 1");
    }

    /**
     * Handles the replyinfo server to client command.
     * @param cmd The S->C command, in this case "replyinfo".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
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

    /**
     * Handles the addme_failed server to client command.
     * @param cmd The S->C command, in this case "addme_failed".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_addme_failed(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }

    /**
     * Handles the setup server to client command.
     * @param cmd The S->C command, in this case "setup".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_setup(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);

        String str = new String(buf);
        String[] datas = str.split(" ");
        for(int i=0; i<datas.length; i++)
        {
            if (datas[i].equals("spellmon"))
            {
                if (datas[i+1].equals("1"))
                {
                    //System.out.println("New Spell Mode activated.");
                    ItemsList.setSpellMode(ItemsList.SPELLMODE_SENT);
                }
                else
                    ItemsList.setSpellMode(ItemsList.SPELLMODE_LOCAL);
            }
        }
        //System.out.println("Received setup command:"+str);
    }

    /**
     * Handles the query server to client command.
     * @param cmd The S->C command, in this case "query".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_query(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);

        String[] datas = (new String(buf)).split(" ",2);

        CrossfireCommandQueryEvent evt = new
                CrossfireCommandQueryEvent(this,datas[1], Integer.parseInt(datas[0]));
        Iterator<CrossfireQueryListener> it = mylisteners_query.iterator();
        setStatus(STATUS_QUERY);
        while (it.hasNext())
        {
            it.next().CommandQueryReceived(evt);
        }
    }

    /**
     * Handles the extendedtextset server to client command.
     * @param cmd The S->C command, in this case "extendedtextset".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_ExtendedTextSet(String cmd, DataInputStream dis) throws IOException
    {
        int len = dis.available();
        byte buf[] = new byte[len];

        dis.readFully(buf);

        String str = new String(buf);
    }

    /**
     * Handles the extendedinfoset server to client command.
     * @param cmd The S->C command, in this case "extendedinfoset".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_ExtendedInfoSet(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }

    /**
     * Handles the drawextinfo server to client command.
     * @param cmd The S->C command, in this case "drawextinfo".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
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
        Iterator<CrossfireDrawextinfoListener> it = mylisteners_drawextinfo.iterator();
        while (it.hasNext())
        {
            it.next().CommandDrawextinfoReceived(evt);
        }
    }

    /**
     * Handles the Smooth server to client command.
     * @param cmd The S->C command, in this case "smooth".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_smooth(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }

    /**
     * Handles the MapExtended server to client command.
     * @param cmd The S->C command, in this case "MapExtended".
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    void cmd_mapextended(String cmd, DataInputStream dis) throws IOException
    {
        System.out.println("Command: "+cmd);
    }

    /**
     * Returns the list of all items at the given location.
     * Usually, this is either an inventory content, or the list of objects on
     * the floor.
     * @param location The object tag identifier of the location to get items from.
     * @return Known items, as a List object.
     * @since 1.0
     */
    public java.util.List<CfItem> getItems(int location)
    {
        return ItemsList.getItems(location);
    }

    /**
     * Returns the current player.
     * @return The current player, as a CfPlayer object.
     * @since 1.0
     */
    public CfPlayer getPlayer()
    {
        return ItemsList.getPlayer();
    }
    public void addScriptMonitor(CrossfireScriptMonitorListener listener)
    {
        scripts_monitor.add(listener);
    }
    public void removeScriptMonitor(CrossfireScriptMonitorListener listener)
    {
        scripts_monitor.remove(listener);
    }
    public void drawInfo(String msg, int col)
    {
        CrossfireCommandDrawinfoEvent evt = new
                CrossfireCommandDrawinfoEvent(this,msg,col);
        Iterator<CrossfireDrawinfoListener> it = mylisteners_drawinfo.iterator();
        while (it.hasNext())
        {
            it.next().CommandDrawinfoReceived(evt);
        }
    }
}

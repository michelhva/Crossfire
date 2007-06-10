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
import java.io.*;

public class ScriptProcess extends Thread implements CrossfireScriptMonitorListener
{
    private String mycmd;
    private JXCWindow mywindow;
    private InputStream in;
    private OutputStream out;
    private OutputStreamWriter osw;

    public ScriptProcess(String cmdline, JXCWindow win) throws IOException
    {
        mycmd = cmdline;
        mywindow = win;
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(mycmd);
        in = proc.getInputStream();
        out = proc.getOutputStream();
        osw = new OutputStreamWriter(out);
        start();
    }
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine())!= null)
            {
                runScriptCommand(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mywindow.terminateScript(this);
    }
    public OutputStream getOutputStream()
    {
        return out;
    }
    public void commandSent(final byte[] packet)
    {
        final String cmd;
        try
        {
            cmd = new String(packet, "ISO-8859-1");
        }
        catch(final UnsupportedEncodingException ex)
        {
            throw new AssertionError(); // will never happen: every JVM must implement ISO-8859-1
        }
        commandSent(cmd);
    }
    public void commandSent(String cmd)
    {
        try
        {
            osw.write(cmd+"\n");
            osw.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public String toString()
    {
        return mycmd;
    }
    private void cmd_watch(String cmdline)
    {
        String parms = cmdline.substring(6);
        System.out.println(" - Watch   :"+parms);
    }
    private void cmd_unwatch(String cmdline)
    {
        String parms = cmdline.substring(8);
        System.out.println(" - Unwatch :"+parms);
    }
    private void cmd_request(String cmdline)
    {
        String parms = cmdline.substring(8);
        System.out.println(" - Request :"+parms);
        Stats st = mywindow.getCrossfireServerConnection().getPlayer().getStats();

        if (parms.equals("range"))
        {
            commandSent(st.getRange());
        }
        else if (parms.equals("stat stats"))
        {
            commandSent(st.getStat(Stats.CS_STAT_STR)+","+
                        st.getStat(Stats.CS_STAT_CON)+","+
                        st.getStat(Stats.CS_STAT_DEX)+","+
                        st.getStat(Stats.CS_STAT_INT)+","+
                        st.getStat(Stats.CS_STAT_WIS)+","+
                        st.getStat(Stats.CS_STAT_POW)+","+
                        st.getStat(Stats.CS_STAT_CHA));
        }
        else if (parms.equals("stat cmbt"))
        {
            commandSent(st.getStat(Stats.CS_STAT_WC)+","+
                        st.getStat(Stats.CS_STAT_AC)+","+
                        st.getStat(Stats.CS_STAT_DAM)+","+
                        st.getStat(Stats.CS_STAT_SPEED)+","+
                        st.getStat(Stats.CS_STAT_WEAP_SP));
        }
        else if (parms.equals("stat hp"))
        {
            commandSent(st.getStat(Stats.CS_STAT_HP)+","+
                    st.getStat(Stats.CS_STAT_MAXHP)+","+
                    st.getStat(Stats.CS_STAT_SP)+","+
                    st.getStat(Stats.CS_STAT_MAXSP)+","+
                    st.getStat(Stats.CS_STAT_GRACE)+","+
                    st.getStat(Stats.CS_STAT_MAXGRACE)+","+
                    st.getStat(Stats.CS_STAT_FOOD));
        }
        else if (parms.equals("stat xp"))
        {
            String str = st.getStat(Stats.CS_STAT_LEVEL)+","+st.getExperience();
            for (int i=0; i<200; i++)
            {
                try
                {
                    if (Stats.getSkill(i) != null)
                    {
                        str = str+ ","+Stats.getSkill(i).getLevel()+","+Stats.getSkill(i).getExperience();
                    }
                }
                catch (Exception e)
                {
                    i=201;
                }
            }
            commandSent(str);
        }
        else if (parms.equals("stat resists"))
        {
            String str = "";
            for(int i= Stats.CS_STAT_RESIST_START; i<=Stats.CS_STAT_RESIST_END;i++)
            {
                str = str+st.getStat(i);
                if (i<Stats.CS_STAT_RESIST_END)
                    str+=",";
            }
            commandSent(str);
        }
        else if (parms.equals("weight"))
        {
            //mmm... I've lost the location of the weight...
            //commandSent(st.getStat(CS_STAT_MAXWEIGHT)+","+st.getStat(CS_STAT_WEIGHT));
        }
        else if (parms.equals("flags"))
        {
            String str = "";
            if (mywindow.checkFire())
                str = "1,";
            else
                str = "0,";
            if (mywindow.checkRun())
                str += "1";
            else
                str += "0";
            commandSent(str);
        }
        else if (parms.equals("items inv"))
        {
        }
        else if (parms.equals("items actv"))
        {
        }
        else if (parms.equals("items on"))
        {
        }
        else if (parms.equals("items cont"))
        {
        }
        else if (parms.equals("map pos"))
        {
        }
        else if (parms.equals("map near"))
        {
        }
        else if (parms.equals("map all"))
        {
        }
        else if (parms.startsWith("map "))
        {
        }
        else if (parms.startsWith("stat "))
        {
        }
    }
    public void runScriptCommand(String cmdline)
    {
        System.out.println("Script Command:"+cmdline);
        if (cmdline.startsWith("watch "))
        {
            cmd_watch(cmdline);
        }
        else if (cmdline.startsWith("unwatch "))
        {
            cmd_unwatch(cmdline);
        }
        else if (cmdline.startsWith("request "))
        {
            cmd_request(cmdline);
        }
        else if (cmdline.startsWith("issue mark "))
        {
            String parms = cmdline.substring(11);
            System.out.println(" - Issue M :"+parms);
        }
        else if (cmdline.startsWith("issue lock "))
        {
            String parms = cmdline.substring(11);
            System.out.println(" - Issue L :"+parms);
        }
        else if (cmdline.startsWith("issue "))
        {
            String parms = cmdline.substring(6);
            String[] pps = parms.split(" ",3);
            for (int i=0; i<Integer.parseInt(pps[0]); i++)
                mywindow.send("command 0 "+pps[2]);
        }
        else if (cmdline.startsWith("draw "))
        {
            String parms = cmdline.substring(5);
            String[] pps = parms.split(" ");
            mywindow.getCrossfireServerConnection().drawInfo(pps[1], Integer.parseInt(pps[0]));
        }
        else if (cmdline.startsWith("monitor"))
        {
            mywindow.getCrossfireServerConnection().addScriptMonitor(this);
        }
        else if (cmdline.startsWith("unmonitor"))
        {
            mywindow.getCrossfireServerConnection().removeScriptMonitor(this);
        }
    }
}

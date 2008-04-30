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
package com.realtime.crossfire.jxclient.scripts;

import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireScriptMonitorListener;
import com.realtime.crossfire.jxclient.stats.Stats;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class ScriptProcess extends Thread
{
    private final String filename;

    private final JXCWindow window;

    private final InputStream in;

    private final OutputStream out;

    private final OutputStreamWriter osw;

    private final CrossfireScriptMonitorListener crossfireScriptMonitorListener = new CrossfireScriptMonitorListener()
    {
        /** {@inheritDoc} */
        public void commandSent(final byte[] packet, final int length)
        {
            final String cmd;
            try
            {
                cmd = new String(packet, 0, length, "ISO-8859-1");
            }
            catch (final UnsupportedEncodingException ex)
            {
                throw new AssertionError(); // will never happen: every JVM must implement ISO-8859-1
            }
            ScriptProcess.this.commandSent(cmd);
        }
    };

    public ScriptProcess(final String filename, final JXCWindow window) throws IOException
    {
        this.filename = filename;
        this.window = window;
        final Runtime rt = Runtime.getRuntime();
        final Process proc = rt.exec(filename);
        in = proc.getInputStream();
        out = proc.getOutputStream();
        osw = new OutputStreamWriter(out);
        start();
    }

    public void run()
    {
        try
        {
            final InputStreamReader isr = new InputStreamReader(in);
            try
            {
                final BufferedReader br = new BufferedReader(isr);
                try
                {
                    for (;;)
                    {
                        final String line = br.readLine();
                        if (line == null)
                        {
                            break;
                        }

                        runScriptCommand(line);
                    }
                }
                finally
                {
                    br.close();
                }
            }
            finally
            {
                isr.close();
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        window.getCrossfireServerConnection().getScriptMonitorListeners().removeScriptMonitor(crossfireScriptMonitorListener);
    }

    public void commandSent(final String cmd)
    {
        try
        {
            osw.write(cmd+"\n");
            osw.flush();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    public String toString()
    {
        return filename;
    }

    private void cmd_watch(final String cmdline)
    {
        final String parms = cmdline.substring(6);
        System.out.println(" - Watch   :"+parms);
    }

    private void cmd_unwatch(final String cmdline)
    {
        final String parms = cmdline.substring(8);
        System.out.println(" - Unwatch :"+parms);
    }

    private void cmd_request(final String cmdline)
    {
        final String parms = cmdline.substring(8);
        System.out.println(" - Request :"+parms);
        final Stats st = ItemsList.getStats();

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
            final StringBuilder sb = new StringBuilder();
            sb.append(st.getStat(Stats.CS_STAT_LEVEL));
            sb.append(',').append(st.getExperience());
            for (int i = Stats.CS_STAT_SKILLINFO; i < Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS; i++)
            {
                if (Stats.getSkill(i) != null)
                {
                    sb.append(',').append(Stats.getSkill(i).getLevel());
                    sb.append(',').append(Stats.getSkill(i).getExperience());
                }
            }
            commandSent(sb.toString());
        }
        else if (parms.equals("stat resists"))
        {
            final StringBuilder sb = new StringBuilder();
            for (int i = Stats.CS_STAT_RESIST_START; i <= Stats.CS_STAT_RESIST_END; i++)
            {
                sb.append(st.getStat(i));
                if (i < Stats.CS_STAT_RESIST_END)
                {
                    sb.append(',');
                }
            }
            commandSent(sb.toString());
        }
        else if (parms.equals("weight"))
        {
            //mmm... I've lost the location of the weight...
            //commandSent(st.getStat(CS_STAT_MAXWEIGHT)+","+st.getStat(CS_STAT_WEIGHT));
        }
        else if (parms.equals("flags"))
        {
            commandSent((window.checkFire() ? "1" : "0")+","+(window.checkRun() ? "1" : "0"));
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

    private void runScriptCommand(String cmdline)
    {
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
            final String parms = cmdline.substring(11);
            System.out.println(" - Issue M :"+parms);
        }
        else if (cmdline.startsWith("issue lock "))
        {
            final String parms = cmdline.substring(11);
            System.out.println(" - Issue L :"+parms);
        }
        else if (cmdline.startsWith("issue "))
        {
            final String parms = cmdline.substring(6);
            final String[] pps = parms.split(" ", 3);
            for (int i = 0; i < Integer.parseInt(pps[0]); i++)
            {
                window.getCommandQueue().sendNcom(pps[1].equals("1"), 0, pps[2]);
            }
        }
        else if (cmdline.startsWith("draw "))
        {
            final String parms = cmdline.substring(5);
            final String[] pps = parms.split(" ");
            window.getCrossfireServerConnection().drawInfo(pps[1], Integer.parseInt(pps[0]));
        }
        else if (cmdline.startsWith("monitor"))
        {
            window.getCrossfireServerConnection().getScriptMonitorListeners().addScriptMonitor(crossfireScriptMonitorListener);
        }
        else if (cmdline.startsWith("unmonitor"))
        {
            window.getCrossfireServerConnection().getScriptMonitorListeners().removeScriptMonitor(crossfireScriptMonitorListener);
        }
    }
}

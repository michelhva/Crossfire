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

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireScriptMonitorListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * An external command executed as a client-sided script.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class ScriptProcess extends Thread implements Comparable<ScriptProcess>
{
    /**
     * The script ID identifying this script instance.
     */
    private final int scriptId;

    /**
     * The script command including arguments.
     */
    private final String filename;

    /**
     * The associated {@link JXCWindow} instance.
     */
    private final JXCWindow window;

    /**
     * The {@link CommandQueue} for sending commands.
     */
    private final CommandQueue commandQueue;

    /**
     * The connection instance.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link Stats} instance to watch.
     */
    private final Stats stats;

    /**
     * The {@link ItemsManager} instance to use.
     */
    private final ItemsManager itemsManager;

    /**
     * The {@link SpellsManager} instance to use.
     */
    private final SpellsManager spellsManager;

    /**
     * The {@link CfMapUpdater} instance to use.
     */
    private final CfMapUpdater mapUpdater;

    /**
     * The {@link Process} instance for the executed child process.
     */
    private final Process proc;

    /**
     * The {@link InputStream} of {@link #proc}.
     */
    private final InputStream in;

    /**
     * The {@link OutputStreamWriter} associated with {@link #proc}.
     */
    private final OutputStreamWriter osw;

    /**
     * The {@link ScriptProcessListener}s to notify.
     */
    private final List<ScriptProcessListener> scriptProcessListeners = new ArrayList<ScriptProcessListener>(1);

    /**
     * The {@link CrossfireScriptMonitorListener} attached to {@link
     * #crossfireServerConnection} to track commands sent to the server.
     */
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

    /**
     * Creates a new instance.
     * @param scriptId the script ID identifying the new script
     * @param filename the command including arguments to execute
     * @param window the associated window instance
     * @param commandQueue the command queue for sending commands
     * @param crossfireServerConnection the server connection
     * @param stats the stats instance to watch
     * @param itemsManager the items manager instance to use
     * @param spellsManager the spells manager instance to use
     * @param mapUpdater the map updater instance to use
     * @throws IOException if the script cannot be created
     */
    public ScriptProcess(final int scriptId, final String filename, final JXCWindow window, final CommandQueue commandQueue, final CrossfireServerConnection crossfireServerConnection, final Stats stats, final ItemsManager itemsManager, final SpellsManager spellsManager, final CfMapUpdater mapUpdater) throws IOException
    {
        this.scriptId = scriptId;
        this.filename = filename;
        this.window = window;
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.stats = stats;
        this.itemsManager = itemsManager;
        this.spellsManager = spellsManager;
        this.mapUpdater = mapUpdater;
        final Runtime rt = Runtime.getRuntime();
        proc = rt.exec(filename);
        in = proc.getInputStream();
        osw = new OutputStreamWriter(proc.getOutputStream());
    }

    /**
     * Returns the script ID identifying this script instance.
     * @return the script ID
     */
    public int getScriptId()
    {
        return scriptId;
    }

    /**
     * Returns the script's filename.
     * @return the script's filename
     */
    public String getFilename()
    {
        return filename;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        String result = "unexpected";
        try
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
                try
                {
                    final int exitStatus = proc.waitFor();
                    result = exitStatus == 0 ? null : "exit "+exitStatus;
                }
                catch (final InterruptedException ex)
                {
                    result = ex.getMessage();
                }
            }
            catch (final IOException ex)
            {
                result = ex.getMessage();
            }
            crossfireServerConnection.getScriptMonitorListeners().removeScriptMonitor(crossfireScriptMonitorListener);
        }
        finally
        {
            for(final ScriptProcessListener scriptProcessListener : scriptProcessListeners)
            {
                scriptProcessListener.scriptTerminated(result);
            }
        }
    }

    /**
     * Sends a message to the script process.
     * @param cmd the message to send
     */
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

    /**
     * Sends an item info message to the script process.
     * @param cmd the message to send
     * @param item the item to send
     */
    public void commandSentItem(final String cmd, final CfItem item)
    {
        int flags = 0;
        if(item.isMagic()) flags |= 0x100;
        if(item.isCursed()) flags |= 0x80;
        if(item.isDamned()) flags |= 0x40;
        if(item.isUnpaid()) flags |= 0x20;
        if(item.isLocked()) flags |= 0x10;
        if(item.isApplied()) flags |= 0x08;
        if(item.isOpen()) flags |= 0x04;
        final int nrof = Math.max(1, item.getNrOf());
        final String name = nrof <= 1 ? item.getName() : nrof+" "+item.getName();
        commandSent(cmd+" "+item.getTag()+" "+nrof+" "+Math.max(0, item.getWeight())+" "+flags+" "+item.getType()+" "+name);
    }

    /**
     * Sends info about one map cell to the script process.
     * @param map the map instance to use
     * @param x the cell's x-coordinate relative to the view area
     * @param y the cell's y-coordinate relative to the view area
     */
    public void commandSentMap(final CfMap map, final int x, final int y)
    {
        final CfMapSquare square = map.getMapSquare(x, y);
        final StringBuilder sb = new StringBuilder("request map ");
        sb.append(x);
        sb.append(' ');
        sb.append(y);
        sb.append(' ');
        sb.append(square.getDarkness());
        sb.append(" n y n "); // XXX: smoothing
        sb.append(square.isFogOfWar() ? 'y' : 'n');
        sb.append(" smooth 0 0 0 heads"); // XXX: smoothing
        for (int i = 0; i < 3; i++)
        {
            sb.append(' ');
            final Face face = square.getFace(i);
            sb.append(face == CfMapSquare.DEFAULT_FACE ? 0 : face.getFaceNum());
        }
        sb.append(" tails");
        for (int i = 0; i < 3; i++)
        {
            final CfMapSquare headSquare = square.getHeadMapSquare(i);
            if (headSquare == null)
            {
                sb.append(" 0");
            }
            else
            {
                sb.append(' ');
                final Face face = headSquare.getFace(i);
                sb.append(face == CfMapSquare.DEFAULT_FACE ? 0 : face.getFaceNum());
            }
        }
        commandSent(sb.toString());
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return scriptId+" "+filename;
    }

    /**
     * Processes a "watch" command from the script process.
     * @param parms the command arguments
     */
    private static void cmdWatch(final String parms)
    {
        System.out.println(" - Watch   :"+parms);
    }

    /**
     * Processes an "unwatch" command from the script process.
     * @param parms the command arguments
     */
    private static void cmdUnwatch(final String parms)
    {
        System.out.println(" - Unwatch :"+parms);
    }

    /**
     * Processes a "request" command from the script process.
     * @param parms the command arguments
     */
    private void cmdRequest(final String parms)
    {
        if (parms.equals("player"))
        {
            commandSent("request player "+itemsManager.getPlayer().getTag()+" "+stats.getTitle());
        }
        else if (parms.equals("range"))
        {
            commandSent("request range "+stats.getRange());
        }
        else if (parms.equals("weight"))
        {
            commandSent("request weight "+stats.getStat(CrossfireStatsListener.CS_STAT_WEIGHT_LIM)+" "+itemsManager.getPlayer().getWeight());
        }
        else if (parms.equals("stat stats"))
        {
            commandSent("request stat stats "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_STR)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_CON)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_DEX)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_INT)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_WIS)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_POW)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_CHA));
        }
        else if (parms.equals("stat cmbt"))
        {
            commandSent("request stat cmbt "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_WC)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_AC)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_DAM)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_SPEED)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_WEAP_SP));
        }
        else if (parms.equals("stat hp"))
        {
            commandSent("request stat hp "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_HP)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_MAXHP)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_SP)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_MAXSP)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_GRACE)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_MAXGRACE)+" "+
                        stats.getStat(CrossfireStatsListener.CS_STAT_FOOD));
        }
        else if (parms.equals("stat xp"))
        {
            final StringBuilder sb = new StringBuilder("request stat xp ");
            sb.append(stats.getStat(CrossfireStatsListener.CS_STAT_LEVEL));
            sb.append(' ').append(stats.getExperience());
            for (int i = CrossfireStatsListener.CS_STAT_SKILLINFO; i < CrossfireStatsListener.CS_STAT_SKILLINFO+CrossfireStatsListener.CS_NUM_SKILLS; i++)
            {
                final Skill skill = SkillSet.getSkill(i);
                if (skill != null)
                {
                    sb.append(' ').append(skill.getLevel());
                    sb.append(' ').append(skill.getExperience());
                }
                else
                {
                    sb.append(" 0 0");
                }
            }
            commandSent(sb.toString());
        }
        else if (parms.equals("stat resists"))
        {
            final StringBuilder sb = new StringBuilder("request stat resists");
            for (int i = CrossfireStatsListener.CS_STAT_RESIST_START; i <= CrossfireStatsListener.CS_STAT_RESIST_END; i++)
            {
                sb.append(' ');
                sb.append(stats.getStat(i));
            }
            // add dummy values for GTK client compatibility
            for (int i = CrossfireStatsListener.CS_STAT_RESIST_END+1-CrossfireStatsListener.CS_STAT_RESIST_START; i < 30; i++)
            {
                sb.append(" 0");
            }
            commandSent(sb.toString());
        }
        else if (parms.equals("stat paths"))
        {
            commandSent("request stat paths "+stats.getStat(CrossfireStatsListener.CS_STAT_SPELL_ATTUNE)+" "+stats.getStat(CrossfireStatsListener.CS_STAT_SPELL_REPEL)+" "+stats.getStat(CrossfireStatsListener.CS_STAT_SPELL_DENY));
        }
        else if (parms.equals("flags"))
        {
            commandSent("request flags "+stats.getStat(CrossfireStatsListener.CS_STAT_FLAGS)+" "+(window.checkFire() ? "1" : "0")+" "+(commandQueue.checkRun() ? "1" : "0")+" 0");
        }
        else if (parms.equals("items inv"))
        {
            for (final CfItem item : itemsManager.getInventory())
            {
                commandSentItem("request items inv", item);
            }
            commandSent("request items inv end");
        }
        else if (parms.equals("items actv"))
        {
            for (final CfItem item : itemsManager.getInventory())
            {
                if (item.isApplied())
                {
                    commandSentItem("request items actv", item);
                }
            }
            commandSent("request items actv end");
        }
        else if (parms.equals("items on"))
        {
            for (final CfItem item : itemsManager.getItems(0))
            {
                commandSentItem("request items on", item);
            }
            commandSent("request items on end");
        }
        else if (parms.equals("items cont"))
        {
            final int containerTag = itemsManager.getCurrentFloorManager().getCurrentFloor();
            if (containerTag != 0)
            {
                for (final CfItem item : itemsManager.getItems(containerTag))
                {
                    commandSentItem("request items cont", item);
                }
            }
            commandSent("request items cont end");
        }
        else if (parms.equals("map pos"))
        {
            commandSent("request map pos "+mapUpdater.getWidth()/2+" "+mapUpdater.getHeight()/2);
        }
        else if (parms.equals("map near"))
        {
            final CfMap map = mapUpdater.getMap();
            final int centerX = mapUpdater.getWidth()/2;
            final int centerY = mapUpdater.getHeight()/2;
            for (int y = -1; y <= +1; y++)
            {
                for (int x = -1; x <= +1; x++)
                {
                    commandSentMap(map, centerX+x, centerY+y);
                }
            }
        }
        else if (parms.equals("map all"))
        {
            final CfMap map = mapUpdater.getMap();
            final int width = mapUpdater.getWidth()/2;
            final int height = mapUpdater.getHeight()/2;
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    commandSentMap(map, x, y);
                }
            }
        }
        else if (parms.startsWith("map "))
        {
            final String[] tmp = parms.split(" +");
            if(tmp.length != 3)
            {
                reportError("syntax error: request "+parms);
                return;
            }

            final int x;
            try
            {
                x = Integer.parseInt(tmp[1]);
            }
            catch (final NumberFormatException ex)
            {
                reportError("syntax error: request "+parms);
                return;
            }

            final int y;
            try
            {
                y = Integer.parseInt(tmp[2]);
            }
            catch (final NumberFormatException ex)
            {
                reportError("syntax error: request "+parms);
                return;
            }

            commandSentMap(mapUpdater.getMap(), x, y);
        }
        else if (parms.equals("skills"))
        {
            for (int i = CrossfireStatsListener.CS_STAT_SKILLINFO; i < CrossfireStatsListener.CS_STAT_SKILLINFO+CrossfireStatsListener.CS_NUM_SKILLS; i++)
            {
                final Skill skill = SkillSet.getSkill(i);
                if (skill != null)
                {
                    commandSent("request skills "+i+" "+skill);
                }
            }
            commandSent("request skills end");
        }
        else if (parms.equals("spells"))
        {
            for (final Spell spell : spellsManager.getSpellList())
            {
                commandSent("request spells "+spell.getTag()+" "+spell.getLevel()+" "+spell.getMana()+" "+spell.getGrace()+" "+spell.getSkill()+" "+spell.getPath()+" "+spell.getCastingTime()+" "+spell.getDamage()+" "+spell.getName());
            }
            commandSent("request spells end");
        }
        else
        {
            reportError("syntax error: request "+parms);
        }
    }

    /**
     * Processes a "issue mark" command from the script process.
     * @param parms the command arguments
     */
    private void cmdIssueMark(final String parms)
    {
        System.out.println(" - Issue M :"+parms);
    }

    /**
     * Processes a "issue lock" command from the script process.
     * @param parms the command arguments
     */
    private void cmdIssueLock(final String parms)
    {
        System.out.println(" - Issue L :"+parms);
    }

    /**
     * Processes a regular "issue" command from the script process.
     * @param parms the command arguments
     */
    private void cmdIssue(final String parms)
    {
        final String[] pps = parms.split(" ", 3);
        for (int i = 0; i < Integer.parseInt(pps[0]); i++)
        {
            commandQueue.sendNcom(pps[1].equals("1"), 0, pps[2]);
        }
    }

    /**
     * Processes a "draw" command from the script process.
     * @param parms the command arguments
     */
    private void cmdDraw(final String parms)
    {
        final String[] pps = parms.split(" ", 2);
        crossfireServerConnection.drawInfo(pps[1], Integer.parseInt(pps[0]));
    }

    /**
     * Processes a "monitor" command from the script process.
     * @param parms the command arguments
     */
    private void cmdMonitor(final String parms)
    {
        if(!parms.isEmpty())
        {
            reportError("The 'monitor' command does not take arguments.");
            return;
        }

        crossfireServerConnection.getScriptMonitorListeners().addScriptMonitor(crossfireScriptMonitorListener);
    }

    /**
     * Processes an "unmonitor" command from the script process.
     * @param parms the command arguments
     */
    private void cmdUnmonitor(final String parms)
    {
        if(!parms.isEmpty())
        {
            reportError("The 'unmonitor' command does not take arguments.");
            return;
        }

        crossfireServerConnection.getScriptMonitorListeners().removeScriptMonitor(crossfireScriptMonitorListener);
    }

    /**
     * Processes a line received from the script process.
     * @param cmdline the line
     */
    private void runScriptCommand(final String cmdline)
    {
        final String[] tmp = cmdline.split(" +", 2);
        if (tmp[0].equals("watch"))
        {
            cmdWatch(tmp[1]);
        }
        else if (tmp[0].equals("unwatch"))
        {
            cmdUnwatch(tmp[1]);
        }
        else if (tmp[0].equals("request"))
        {
            cmdRequest(tmp[1]);
        }
        else if (tmp[0].equals("issue"))
        {
            if (tmp[1].startsWith("mark "))
            {
                cmdIssueMark(tmp[1].substring(5));
            }
            else if (tmp[1].startsWith("lock "))
            {
                cmdIssueLock(tmp[1].substring(5));
            }
            else
            {
                cmdIssue(tmp[1]);
            }
        }
        else if (tmp[0].equals("draw"))
        {
            cmdDraw(tmp[1]);
        }
        else if (tmp[0].equals("monitor"))
        {
            cmdMonitor(tmp[1]);
        }
        else if (tmp[0].equals("unmonitor"))
        {
            cmdUnmonitor(tmp[1]);
        }
        else
        {
            reportError("unrecognized command from script: "+cmdline);
        }
    }

    /**
     * Reports an error while executing client commands.
     * @param string the error message
     */
    private void reportError(final String string)
    {
        crossfireServerConnection.drawInfo(string, CrossfireCommandDrawinfoEvent.NDI_RED);
    }

    /**
     * Adds a {@link ScriptProcessListener} to be notified.
     * @param scriptProcessListener the listener to add
     */
    public void addScriptProcessListener(final ScriptProcessListener scriptProcessListener)
    {
        scriptProcessListeners.add(scriptProcessListener);
    }

    /**
     * Kills the script process. Does nothing if the process is not running.
     */
    public void killScript()
    {
        proc.destroy();
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final ScriptProcess o)
    {
        if(scriptId < o.scriptId)
        {
            return -1;
        }
        else if(scriptId > o.scriptId)
        {
            return +1;
        }
        else
        {
            return 0;
        }
    }
}

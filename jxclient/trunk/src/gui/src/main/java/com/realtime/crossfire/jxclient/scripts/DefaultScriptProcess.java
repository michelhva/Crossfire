/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.scripts;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.map.MapUpdaterState;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketListener;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation for {@link ScriptProcess}es.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class DefaultScriptProcess implements Runnable, ScriptProcess {

    /**
     * The script ID identifying this script instance.
     */
    private final int scriptId;

    /**
     * The script command including arguments.
     */
    @NotNull
    private final String filename;

    /**
     * The {@link CommandQueue} for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The connection instance.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link Stats} instance to watch.
     */
    @NotNull
    private final Stats stats;

    /**
     * The {@link FloorView} to use.
     */
    @NotNull
    private final FloorView floorView;

    /**
     * The {@link ItemSet} instance to use.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The {@link SpellsManager} instance to use.
     */
    @NotNull
    private final Iterable<Spell> spellsManager;

    /**
     * The {@link MapUpdaterState} instance to use.
     */
    @NotNull
    private final MapUpdaterState mapUpdaterState;

    /**
     * The {@link SkillSet} for looking up skill names.
     */
    @NotNull
    private final SkillSet skillSet;

    /**
     * The {@link Process} instance for the executed child process.
     */
    @NotNull
    private final Process process;

    /**
     * The {@link InputStream} of {@link #process}.
     */
    @NotNull
    private final InputStream in;

    /**
     * The {@link OutputStreamWriter} associated with {@link #process}.
     */
    @NotNull
    private final OutputStreamWriter osw;

    /**
     * The {@link ScriptProcessListener ScriptProcessListeners} to notify.
     */
    @NotNull
    private final EventListenerList2<ScriptProcessListener> scriptProcessListeners = new EventListenerList2<>();

    /**
     * The {@link PacketWatcher} to process "watch" commands.
     */
    @NotNull
    private final PacketWatcher packetWatcher;

    /**
     * Whether a "monitor" command is active.
     */
    private boolean isMonitoring;

    /**
     * Whether this script has been killed.
     */
    private boolean killed;

    /**
     * The {@link ClientSocketListener} attached to {@link
     * #crossfireServerConnection} to track commands sent to the server.
     */
    @NotNull
    private final ClientSocketListener clientSocketListener = new ClientSocketListener() {

        @Override
        public void connecting() {
            // ignore
        }

        @Override
        public void connected() {
            // ignore
        }

        @Override
        public void packetReceived(@NotNull final ByteBuffer packet) {
            // ignore
        }

        @Override
        public void packetSent(@NotNull final byte[] buf, final int len) {
            commandSent(new String(buf, 0, len, StandardCharsets.ISO_8859_1));
        }

        @Override
        public void disconnecting(@NotNull final String reason, final boolean isError) {
            // ignore
        }

        @Override
        public void disconnected(@NotNull final String reason) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param scriptId the script ID identifying the new script
     * @param filename the command including arguments to execute
     * @param commandQueue the command queue for sending commands
     * @param crossfireServerConnection the server connection
     * @param stats the stats instance to watch
     * @param floorView the floor view to use
     * @param itemSet the item set instance to use
     * @param spellsManager the spells manager instance to use
     * @param mapUpdaterState the map updater state instance to use
     * @param skillSet the skill set for looking up skill names
     * @throws IOException if the script cannot be created
     */
    public DefaultScriptProcess(final int scriptId, @NotNull final String filename, @NotNull final CommandQueue commandQueue, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final Stats stats, @NotNull final FloorView floorView, @NotNull final ItemSet itemSet, @NotNull final Iterable<Spell> spellsManager, @NotNull final MapUpdaterState mapUpdaterState, @NotNull final SkillSet skillSet) throws IOException {
        this.scriptId = scriptId;
        this.filename = filename;
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.stats = stats;
        this.floorView = floorView;
        this.itemSet = itemSet;
        this.spellsManager = spellsManager;
        this.mapUpdaterState = mapUpdaterState;
        this.skillSet = skillSet;
        packetWatcher = new PacketWatcher(crossfireServerConnection, this);
        final Runtime rt = Runtime.getRuntime();
        process = rt.exec(filename);
        in = process.getInputStream();
        //noinspection IOResourceOpenedButNotSafelyClosed
        osw = new OutputStreamWriter(process.getOutputStream());
    }

    @Override
    public int getScriptId() {
        return scriptId;
    }

    @NotNull
    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void run() {
        @Nullable String result = "unexpected";
        try {
            try {
                try (InputStreamReader isr = new InputStreamReader(in)) {
                    try (BufferedReader br = new BufferedReader(isr)) {
                        while (true) {
                            final String line = br.readLine();
                            if (line == null) {
                                break;
                            }

                            runScriptCommand(line);
                        }
                    }
                }
                try {
                    final int exitStatus = process.waitFor();
                    result = exitStatus == 0 ? null : "exit "+exitStatus;
                } catch (final InterruptedException ex) {
                    result = ex.getMessage();
                }
            } catch (final IOException ex) {
                result = ex.getMessage();
            }
            crossfireServerConnection.removeClientSocketListener(clientSocketListener);
        } finally {
            if (isMonitoring) {
                crossfireServerConnection.removeClientSocketListener(clientSocketListener);
            }
            packetWatcher.destroy();
            for (ScriptProcessListener scriptProcessListener : scriptProcessListeners) {
                scriptProcessListener.scriptTerminated(result);
            }
        }
    }

    @Override
    public void commandSent(@NotNull final String cmd) {
        if (killed) {
            return;
        }

        try {
            osw.write(cmd+"\n");
            osw.flush();
        } catch (final IOException ex) {
            reportError(ex.getMessage());
            killScript();
        }
    }

    /**
     * Sends an item info message to the script process.
     * @param cmd the message to send
     * @param item the item to send
     */
    private void commandSentItem(@NotNull final String cmd, @NotNull final CfItem item) {
        int flags = 0;
        if (item.isMagic()) {
            flags |= 0x100;
        }
        if (item.isCursed()) {
            flags |= 0x80;
        }
        if (item.isDamned()) {
            flags |= 0x40;
        }
        if (item.isUnpaid()) {
            flags |= 0x20;
        }
        if (item.isLocked()) {
            flags |= 0x10;
        }
        if (item.isApplied()) {
            flags |= 0x08;
        }
        if (item.isOpen()) {
            flags |= 0x04;
        }
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
    private void commandSentMap(@NotNull final CfMap map, final int x, final int y) {
        final StringBuilder sb = new StringBuilder("request map ");
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (map) {
            final CfMapSquare square = map.getMapSquare(x, y);
            sb.append(x);
            sb.append(' ');
            sb.append(y);
            sb.append(' ');
            sb.append(square.getDarkness());
            sb.append(" n y n "); // XXX: smoothing
            sb.append(square.isFogOfWar() ? 'y' : 'n');
            sb.append(" smooth 0 0 0 heads"); // XXX: smoothing
            for (int i = 0; i < 3; i++) {
                sb.append(' ');
                final Face face = square.getFace(i);
                //noinspection ConstantConditions
                sb.append(face == CfMapSquare.DEFAULT_FACE ? 0 : face.getFaceNum());
            }
            sb.append(" tails");
            for (int i = 0; i < 3; i++) {
                final CfMapSquare headSquare = square.getHeadMapSquare(i);
                if (headSquare == null) {
                    sb.append(" 0");
                } else {
                    sb.append(' ');
                    final Face face = headSquare.getFace(i);
                    //noinspection ConstantConditions
                    sb.append(face == CfMapSquare.DEFAULT_FACE ? 0 : face.getFaceNum());
                }
            }
        }
        commandSent(sb.toString());
    }

    @NotNull
    @Override
    public String toString() {
        return scriptId+" "+filename;
    }

    /**
     * Processes a "request" command from the script process.
     * @param params the command arguments
     */
    private void cmdRequest(@NotNull final String params) {
        if (params.equals("player")) {
            commandSent("request player "+itemSet.getPlayer().getTag()+" "+stats.getTitle());
        } else if (params.equals("range")) {
            commandSent("request range "+stats.getRange());
        } else if (params.equals("weight")) {
            commandSent("request weight "+stats.getStat(Stats.CS_STAT_WEIGHT_LIM)+" "+itemSet.getPlayer().getWeight());
        } else if (params.equals("stat stats")) {
            commandSent("request stat stats "+stats.getStat(Stats.CS_STAT_STR)+" "+stats.getStat(Stats.CS_STAT_CON)+" "+stats.getStat(Stats.CS_STAT_DEX)+" "+stats.getStat(Stats.CS_STAT_INT)+" "+stats.getStat(Stats.CS_STAT_WIS)+" "+stats.getStat(Stats.CS_STAT_POW)+" "+stats.getStat(Stats.CS_STAT_CHA));
        } else if (params.equals("stat stats_race")) {
            commandSent("request stat stats_race "+stats.getStat(Stats.CS_STAT_RACE_STR)+" "+stats.getStat(Stats.CS_STAT_RACE_CON)+" "+stats.getStat(Stats.CS_STAT_RACE_DEX)+" "+stats.getStat(Stats.CS_STAT_RACE_INT)+" "+stats.getStat(Stats.CS_STAT_RACE_WIS)+" "+stats.getStat(Stats.CS_STAT_RACE_POW)+" "+stats.getStat(Stats.CS_STAT_RACE_CHA));
        } else if (params.equals("stat stats_base")) {
            commandSent("request stat stats_base "+stats.getStat(Stats.CS_STAT_BASE_STR)+" "+stats.getStat(Stats.CS_STAT_BASE_CON)+" "+stats.getStat(Stats.CS_STAT_BASE_DEX)+" "+stats.getStat(Stats.CS_STAT_BASE_INT)+" "+stats.getStat(Stats.CS_STAT_BASE_WIS)+" "+stats.getStat(Stats.CS_STAT_BASE_POW)+" "+stats.getStat(Stats.CS_STAT_BASE_CHA));
        } else if (params.equals("stat stats_applied")) {
            commandSent("request stat stats_applied "+stats.getStat(Stats.CS_STAT_APPLIED_STR)+" "+stats.getStat(Stats.CS_STAT_APPLIED_CON)+" "+stats.getStat(Stats.CS_STAT_APPLIED_DEX)+" "+stats.getStat(Stats.CS_STAT_APPLIED_INT)+" "+stats.getStat(Stats.CS_STAT_APPLIED_WIS)+" "+stats.getStat(Stats.CS_STAT_APPLIED_POW)+" "+stats.getStat(Stats.CS_STAT_APPLIED_CHA));
        } else if (params.equals("stat cmbt")) {
            commandSent("request stat cmbt "+stats.getStat(Stats.CS_STAT_WC)+" "+stats.getStat(Stats.CS_STAT_AC)+" "+stats.getStat(Stats.CS_STAT_DAM)+" "+stats.getStat(Stats.CS_STAT_SPEED)+" "+stats.getStat(Stats.CS_STAT_WEAP_SP));
        } else if (params.equals("stat hp")) {
            commandSent("request stat hp "+stats.getStat(Stats.CS_STAT_HP)+" "+stats.getStat(Stats.CS_STAT_MAXHP)+" "+stats.getStat(Stats.CS_STAT_SP)+" "+stats.getStat(Stats.CS_STAT_MAXSP)+" "+stats.getStat(Stats.CS_STAT_GRACE)+" "+stats.getStat(Stats.CS_STAT_MAXGRACE)+" "+stats.getStat(Stats.CS_STAT_FOOD));
        } else if (params.equals("stat xp")) {
            final StringBuilder sb = new StringBuilder("request stat xp ");
            sb.append(stats.getStat(Stats.CS_STAT_LEVEL));
            sb.append(' ').append(stats.getExperience());
            for (int i = Stats.CS_STAT_SKILLINFO; i < Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS; i++) {
                final Skill skill = skillSet.getSkill(i);
                if (skill == null) {
                    sb.append(" 0 0");
                } else {
                    sb.append(' ').append(skill.getLevel());
                    sb.append(' ').append(skill.getExperience());
                }
            }
            commandSent(sb.toString());
        } else if (params.equals("stat resists")) {
            final StringBuilder sb = new StringBuilder("request stat resists");
            for (int i = Stats.CS_STAT_RESIST_START; i <= Stats.CS_STAT_RESIST_END; i++) {
                sb.append(' ');
                sb.append(stats.getStat(i));
            }
            // add dummy values for GTK client compatibility
            for (int i = Stats.CS_STAT_RESIST_END+1-Stats.CS_STAT_RESIST_START; i < 30; i++) {
                sb.append(" 0");
            }
            commandSent(sb.toString());
        } else if (params.equals("stat paths")) {
            commandSent("request stat paths "+stats.getStat(Stats.CS_STAT_SPELL_ATTUNE)+" "+stats.getStat(Stats.CS_STAT_SPELL_REPEL)+" "+stats.getStat(Stats.CS_STAT_SPELL_DENY));
        } else if (params.equals("flags")) {
            commandSent("request flags "+stats.getStat(Stats.CS_STAT_FLAGS)+" "+(commandQueue.checkFire() ? "1" : "0")+" "+(commandQueue.checkRun() ? "1" : "0")+" 0");
        } else if (params.equals("items inv")) {
            for (CfItem item : itemSet.getPlayerInventory()) {
                commandSentItem("request items inv", item);
            }
            commandSent("request items inv end");
        } else if (params.equals("items actv")) {
            for (CfItem item : itemSet.getPlayerInventory()) {
                if (item.isApplied()) {
                    commandSentItem("request items actv", item);
                }
            }
            commandSent("request items actv end");
        } else if (params.equals("items on")) {
            for (CfItem item : itemSet.getItemsByLocation(0)) {
                commandSentItem("request items on", item);
            }
            commandSent("request items on end");
        } else if (params.equals("items cont")) {
            final int containerTag = floorView.getCurrentFloor();
            if (containerTag != 0) {
                for (CfItem item : itemSet.getItemsByLocation(containerTag)) {
                    commandSentItem("request items cont", item);
                }
            }
            commandSent("request items cont end");
        } else if (params.equals("map pos")) {
            commandSent("request map pos "+mapUpdaterState.getMapWidth()/2+" "+mapUpdaterState.getMapHeight()/2);
        } else if (params.equals("map near")) {
            final CfMap map = mapUpdaterState.getMap();
            final int centerX = mapUpdaterState.getMapWidth()/2;
            final int centerY = mapUpdaterState.getMapHeight()/2;
            for (int y = -1; y <= +1; y++) {
                for (int x = -1; x <= +1; x++) {
                    commandSentMap(map, centerX+x, centerY+y);
                }
            }
        } else if (params.equals("map all")) {
            final CfMap map = mapUpdaterState.getMap();
            final int width = mapUpdaterState.getMapWidth()/2;
            final int height = mapUpdaterState.getMapHeight()/2;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    commandSentMap(map, x, y);
                }
            }
        } else if (params.startsWith("map ")) {
            final String[] tmp = params.split(" +");
            if (tmp.length != 3) {
                reportError("syntax error: request "+params);
                return;
            }

            final int x;
            try {
                x = Integer.parseInt(tmp[1]);
            } catch (final NumberFormatException ignored) {
                reportError("syntax error: request "+params);
                return;
            }

            final int y;
            try {
                y = Integer.parseInt(tmp[2]);
            } catch (final NumberFormatException ignored) {
                reportError("syntax error: request "+params);
                return;
            }

            commandSentMap(mapUpdaterState.getMap(), x, y);
        } else if (params.equals("skills")) {
            for (int i = Stats.CS_STAT_SKILLINFO; i < Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS; i++) {
                final Object skill = skillSet.getSkill(i);
                if (skill != null) {
                    commandSent("request skills "+i+" "+skill);
                }
            }
            commandSent("request skills end");
        } else if (params.equals("spells")) {
            for (Spell spell : spellsManager) {
                commandSent("request spells "+spell.getTag()+" "+spell.getLevel()+" "+spell.getMana()+" "+spell.getGrace()+" "+spell.getSkill()+" "+spell.getPath()+" "+spell.getCastingTime()+" "+spell.getDamage()+" "+spell.getName());
            }
            commandSent("request spells end");
        } else {
            reportError("syntax error: request "+params);
        }
    }

    /**
     * Processes a "issue mark" command from the script process.
     * @param params the command arguments
     */
    private void cmdIssueMark(@NotNull final String params) {
        final int tag;
        try {
            tag = Integer.parseInt(params);
        } catch (final NumberFormatException ignored) {
            reportError("syntax error: issue mark "+params);
            return;
        }
        crossfireServerConnection.sendMark(tag);
    }

    /**
     * Processes a "issue lock" command from the script process.
     * @param params the command arguments
     */
    private void cmdIssueLock(@NotNull final String params) {
        final String[] tmp = params.split(" +", 2);
        if (tmp.length != 2) {
            reportError("syntax error: issue lock "+params);
            return;
        }
        final int val;
        final int tag;
        try {
            val = Integer.parseInt(tmp[0]);
            tag = Integer.parseInt(tmp[1]);
        } catch (final NumberFormatException ignored) {
            reportError("syntax error: issue lock "+params);
            return;
        }
        if (val < 0 || val > 1) {
            reportError("syntax error: issue lock "+params);
            return;
        }
        crossfireServerConnection.sendLock(val != 0, tag);
    }

    /**
     * Processes a regular "issue" command from the script process.
     * @param params the command arguments
     */
    private void cmdIssue(@NotNull final String params) {
        final String[] pps = params.split(" +", 3);
        if (pps.length != 3) {
            reportError("syntax error: issue "+params);
            return;
        }
        final int repeat;
        final int tmp;
        try {
            repeat = Integer.parseInt(pps[0]);
            tmp = Integer.parseInt(pps[1]);
        } catch (final NumberFormatException ignored) {
            reportError("syntax error: issue "+params);
            return;
        }
        if (tmp < 0 || tmp > 1) {
            reportError("syntax error: issue "+params);
            return;
        }
        final boolean mustSend = tmp != 0;
        final String command = pps[2];
        commandQueue.sendNcom(mustSend, repeat, command);
    }

    /**
     * Processes a "draw" command from the script process.
     * @param params the command arguments
     */
    private void cmdDraw(@NotNull final String params) {
        final String[] pps = params.split(" +", 2);
        if (pps.length != 2) {
            reportError("syntax error: draw "+params);
            return;
        }
        final int color;
        try {
            color = Integer.parseInt(pps[0]);
        } catch (final NumberFormatException ignored) {
            reportError("syntax error: draw "+params);
            return;
        }
        crossfireServerConnection.drawInfo(pps[1], color);
    }

    /**
     * Processes a "monitor" command from the script process.
     */
    private void cmdMonitor() {
        if (!isMonitoring) {
            isMonitoring = true;
            crossfireServerConnection.addClientSocketListener(clientSocketListener);
        }
    }

    /**
     * Processes an "unmonitor" command from the script process.
     */
    private void cmdUnmonitor() {
        if (isMonitoring) {
            isMonitoring = false;
            crossfireServerConnection.removeClientSocketListener(clientSocketListener);
        }
    }

    /**
     * Processes a line received from the script process.
     * @param cmdLine the line
     */
    private void runScriptCommand(@NotNull final String cmdLine) {
        final String[] tmp = cmdLine.split(" +", 2);
        switch (tmp[0]) {
        case "watch":
            if (tmp.length == 1) {
                packetWatcher.addCommand("");
            } else if (tmp[1].indexOf(' ') != -1) {
                reportError("syntax error: "+cmdLine);
            } else {
                packetWatcher.addCommand(tmp[1]);
            }
            break;

        case "unwatch":
            packetWatcher.removeCommand(tmp.length >= 2 ? tmp[1] : "");
            break;

        case "request":
            if (tmp.length == 2) {
                cmdRequest(tmp[1]);
            } else {
                reportError("syntax error: "+cmdLine);
            }
            break;

        case "issue":
            if (tmp.length != 2) {
                reportError("syntax error: "+cmdLine);
            } else if (tmp[1].startsWith("mark ")) {
                cmdIssueMark(tmp[1].substring(5));
            } else if (tmp[1].startsWith("lock ")) {
                cmdIssueLock(tmp[1].substring(5));
            } else {
                cmdIssue(tmp[1]);
            }
            break;

        case "draw":
            if (tmp.length == 2) {
                cmdDraw(tmp[1]);
            } else {
                reportError("syntax error: "+cmdLine);
            }
            break;

        case "monitor":
            if (tmp.length == 1) {
                cmdMonitor();
            } else {
                reportError("The 'monitor' command does not take arguments.");
            }
            break;

        case "unmonitor":
            if (tmp.length == 1) {
                cmdUnmonitor();
            } else {
                reportError("The 'unmonitor' command does not take arguments.");
            }
            break;

        default:
            reportError("unrecognized command from script: "+cmdLine);
            break;
        }
    }

    /**
     * Reports an error while executing client commands.
     * @param string the error message
     */
    private void reportError(@NotNull final String string) {
        crossfireServerConnection.drawInfo(string, CrossfireDrawinfoListener.NDI_RED);
    }

    @Override
    public void addScriptProcessListener(@NotNull final ScriptProcessListener scriptProcessListener) {
        scriptProcessListeners.add(scriptProcessListener);
    }

    @Override
    public void killScript() {
        killed = true;
        process.destroy();
    }

    @Override
    public int compareTo(@NotNull final ScriptProcess o) {
        if (scriptId < o.getScriptId()) {
            return -1;
        }
        if (scriptId > o.getScriptId()) {
            return +1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return scriptId;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof ScriptProcess)) {
            return false;
        }

        final ScriptProcess scriptProcess = (ScriptProcess)obj;
        return scriptProcess.getScriptId() == scriptId;
    }

}

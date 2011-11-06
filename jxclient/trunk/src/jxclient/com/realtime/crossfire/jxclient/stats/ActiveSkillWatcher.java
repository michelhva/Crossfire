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

package com.realtime.crossfire.jxclient.stats;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * Helper class to synthesize an "active skill" stat value. The Crossfire server
 * currently does not send this information, therefore range stat messages are
 * tracked.
 * @author Andreas Kirschbaum
 */
public class ActiveSkillWatcher {

    /**
     * Prefix string when searching for the currently active skill.
     */
    @NotNull
    private static final String READIED_SKILLS = "Readied skill: ";

    /**
     * The object used for synchronization.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The stats instance to notify.
     */
    @NotNull
    private final Stats stats;

    /**
     * The last known active skill name.
     */
    @NotNull
    private String activeSkill = "";

    /**
     * The stats listener to detect the range stat.
     */
    @NotNull
    private final StatsListener statsListener = new StatsListener() {

        @Override
        public void reset() {
            // ignore
        }

        @Override
        public void statChanged(final int statNo, final int value) {
            // ignore
        }

        @Override
        public void simpleWeaponSpeedChanged(final boolean simpleWeaponSpeed) {
            // ignore
        }

        @Override
        public void titleChanged(@NotNull final String title) {
            // ignore
        }

        @Override
        public void rangeChanged(@NotNull final String range) {
            checkRange(range);
        }

        @Override
        public void activeSkillChanged(@NotNull final String activeSkill) {
            // ignore
        }

        @Override
        public void experienceChanged(final long exp) {
            // ignore
        }

        @Override
        public void experienceNextLevelChanged(final long expNextLevel) {
            // ignore
        }

    };

    /**
     * The drawinfo listener to receive drawinfo messages.
     */
    @NotNull
    private final CrossfireDrawinfoListener drawinfoListener = new CrossfireDrawinfoListener() {

        @Override
        public void commandDrawinfoReceived(@NotNull final String text, final int type) {
            checkMessage(text);
        }

    };

    /**
     * The drawextinfo listener to receive drawextinfo messages.
     */
    @NotNull
    private final CrossfireDrawextinfoListener drawextinfoListener = new CrossfireDrawextinfoListener() {

        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, @NotNull final String message) {
            checkMessage(message);
        }

        @Override
        public void setDebugMode(final boolean printMessageTypes) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param stats the stats instance to notify/watch
     * @param crossfireServerConnection the connection to watch
     */
    public ActiveSkillWatcher(@NotNull final Stats stats, @NotNull final CrossfireServerConnection crossfireServerConnection) {
        this.stats = stats;
        stats.addCrossfireStatsListener(statsListener);
        crossfireServerConnection.addCrossfireDrawinfoListener(drawinfoListener);
        crossfireServerConnection.addCrossfireDrawextinfoListener(drawextinfoListener);
        setActive("");
    }

    /**
     * Checks whether the range attribute has changed.
     * @param range the new range attribute
     */
    private void checkRange(@NotNull final String range) {
        if (range.startsWith("Skill: ")) {
            setActive(range.substring(7));
        }
    }

    /**
     * Checks whether a drawinfo message is skill related.
     * @param message the message
     */
    private void checkMessage(@NotNull final String message) {
        if (message.startsWith(READIED_SKILLS)) {
            final String tmp = message.substring(READIED_SKILLS.length());
            setActive(tmp.endsWith(".") ? tmp.substring(0, tmp.length()-1) : tmp);
        }
    }

    /**
     * Sets the active skill name.
     * @param activeSkill the active skill name
     */
    private void setActive(@NotNull final String activeSkill) {
        // Normalize skill name: the Crossfire server sometimes sends "Skill:
        // <skill item name>" rather than "Skill: <skill name>".
        final String normalizedActiveSkill;
        if (activeSkill.equals("lockpicks")) {
            normalizedActiveSkill = "lockpicking";
        } else if (activeSkill.equals("writing pen")) {
            normalizedActiveSkill = "inscription";
        } else {
            normalizedActiveSkill = activeSkill;
        }

        synchronized (sync) {
            if (this.activeSkill.equals(normalizedActiveSkill)) {
                return;
            }

            this.activeSkill = normalizedActiveSkill;
            stats.setActiveSkill(this.activeSkill);
        }
    }

}

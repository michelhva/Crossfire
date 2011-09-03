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

package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.TestCrossfireServerConnection;
import java.util.Arrays;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression test for {@link CommandExpander}.
 * @author Andreas Kirschbaum
 */
public class CommandExpanderTest {

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)} works
     * as expected.
     */
    @Test
    public void testExpandSingle() {
        final Commands commands = new Commands();
        check("xyz", commands, new CommandExec(null, "xyz"));
    }

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)} works
     * as expected.
     */
    @Test
    public void testExpandMultiple() {
        final Commands commands = new Commands();
        check("xyz;abc;abc", commands, new CommandExec(null, "xyz"), new CommandExec(null, "abc"), new CommandExec(null, "abc"));
    }

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)} allows
     * whitespace before or after the command separator.
     */
    @Test
    public void testExpandIgnoreWhitespace() {
        final Commands commands = new Commands();
        check("xyz;abc", commands, new CommandExec(null, "xyz"), new CommandExec(null, "abc"));
        check("xyz ;abc", commands, new CommandExec(null, "xyz"), new CommandExec(null, "abc"));
        check("xyz; abc", commands, new CommandExec(null, "xyz"), new CommandExec(null, "abc"));
        check("xyz   ;   abc", commands, new CommandExec(null, "xyz"), new CommandExec(null, "abc"));
    }

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)}
     * ignores empty commands.
     */
    @Test
    public void testExpandIgnoreEmpty() {
        final Commands commands = new Commands();
        check("", commands);
        check(";;;", commands);
        check("   ;   xyz   ;   abc   ;   ", commands, new CommandExec(null, "xyz"), new CommandExec(null, "abc"));
    }

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)}
     * detects predefined commands.
     */
    @Test
    public void testExpandPredefined() {
        final CrossfireServerConnection crossfireServerConnection = new TestCrossfireServerConnection();
        final Commands commands = new Commands();
        final TestCommand cmd = new TestCommand("cmd", false, crossfireServerConnection);
        commands.addCommand(cmd);
        check("cmd", commands, new CommandExec(cmd, ""));
        check("cmd abc", commands, new CommandExec(cmd, "abc"));
        check("cm", commands, new CommandExec(null, "cm"));
        check("cmd2", commands, new CommandExec(null, "cmd2"));
        check("cmd;cmd;cm;cmd;cmd2", commands, new CommandExec(cmd, ""), new CommandExec(cmd, ""), new CommandExec(null, "cm"), new CommandExec(cmd, ""), new CommandExec(null, "cmd2"));
    }

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)}
     * correctly parses command arguments.
     */
    @Test
    public void testExpandArguments() {
        final CrossfireServerConnection crossfireServerConnection = new TestCrossfireServerConnection();
        final Commands commands = new Commands();
        final TestCommand cmd = new TestCommand("cmd", false, crossfireServerConnection);
        commands.addCommand(cmd);
        check("cmd", commands, new CommandExec(cmd, ""));
        check("cmd abc", commands, new CommandExec(cmd, "abc"));
        check("cmd    abc", commands, new CommandExec(cmd, "abc"));
        check("cm", commands, new CommandExec(null, "cm"));
        check("cm abc", commands, new CommandExec(null, "cm abc"));
        check("cm   abc", commands, new CommandExec(null, "cm   abc"));
    }

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)}
     * matches command names case-insensitive. This is what the server does,
     * too.
     */
    @Test
    public void testExpandCaseInsensitiveCommandNames() {
        final CrossfireServerConnection crossfireServerConnection = new TestCrossfireServerConnection();
        final Commands commands = new Commands();
        final TestCommand cmd = new TestCommand("cmd", false, crossfireServerConnection);
        commands.addCommand(cmd);
        check("cmd", commands, new CommandExec(cmd, ""));
        check("Cmd", commands, new CommandExec(cmd, ""));
        check("cmD", commands, new CommandExec(cmd, ""));
        check("CMD", commands, new CommandExec(cmd, ""));
    }

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)}
     * correctly parses commands that consume all remaining arguments.
     */
    @Test
    public void testExpandAllArguments() {
        final CrossfireServerConnection crossfireServerConnection = new TestCrossfireServerConnection();
        final Commands commands = new Commands();
        final TestCommand cmd = new TestCommand("cmd", false, crossfireServerConnection);
        final TestCommand bind = new TestCommand("bind", true, crossfireServerConnection);
        commands.addCommand(cmd);
        commands.addCommand(bind);
        check("cmd bind", commands, new CommandExec(cmd, "bind"));
        check("bind cmd", commands, new CommandExec(bind, "cmd"));
        check("cmd bind;bind cmd", commands, new CommandExec(cmd, "bind"), new CommandExec(bind, "cmd"));
        check("bind cmd;cmd bind", commands, new CommandExec(bind, "cmd;cmd bind"));
        check("cmd bind;bind cmd;cmd bind", commands, new CommandExec(cmd, "bind"), new CommandExec(bind, "cmd;cmd bind"));
        check("bind cmd;cmd bind;bind cmd", commands, new CommandExec(bind, "cmd;cmd bind;bind cmd"));
        check("cmd bind ; bind cmd ; cmd bind", commands, new CommandExec(cmd, "bind"), new CommandExec(bind, "cmd ; cmd bind"));
        check("bind cmd ; cmd bind ; bind cmd", commands, new CommandExec(bind, "cmd ; cmd bind ; bind cmd"));
    }

    /**
     * Checks that {@link CommandExpander#expand(CharSequence, Commands)} works
     * as expected.
     * @param command the command to split
     * @param commands the commands instance to use
     * @param commandList the expected result
     */
    private static void check(@NotNull final CharSequence command, @NotNull final Commands commands, @NotNull final CommandExec... commandList) {
        final Collection<CommandExec> expandedCommands = CommandExpander.expand(command, commands);
        Assert.assertEquals(Arrays.asList(commandList), expandedCommands);
    }

}

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

package com.realtime.crossfire.jxclient.server.crossfire;

import com.realtime.crossfire.jxclient.server.socket.ClientSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;

/**
 * Implements {@link CrossfireServerConnection} for regression tests. All
 * functions do call {@link Assert#fail()}. Sub-classes may override some
 * functions.
 * @author Andreas Kirschbaum
 */
public class TestCrossfireServerConnection extends AbstractCrossfireServerConnection {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAddme() {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendApply(final int tag) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAskface(final int num) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendExamine(final int tag) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendLock(final boolean val, final int tag) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendLookat(final int dx, final int dy) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMark(final int tag) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMove(final int to, final int tag, final int nrof) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int sendNcom(final int repeat, @NotNull final String command) {
        Assert.fail();
        throw new AssertionError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendReply(@NotNull final String text) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRequestinfo(@NotNull final String infoType) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSetup(@NotNull final String... options) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendToggleextendedtext(@NotNull final int... types) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendVersion(final int csval, final int scval, @NotNull final String vinfo) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPreferredMapSize(final int preferredMapWidth, final int preferredMapHeight) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPreferredNumLookObjects(final int preferredNumLookObjects) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public String getAccountName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addClientSocketListener(@NotNull final ClientSocketListener clientSocketListener) {
        throw new AssertionError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeClientSocketListener(@NotNull final ClientSocketListener clientSocketListener) {
        throw new AssertionError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(@NotNull final String hostname, final int port) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(@NotNull final String reason) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAccountLogin(@NotNull final String login, @NotNull final String password) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAccountPlay(@NotNull final String name) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAccountLink(final int force, @NotNull final String login, @NotNull final String password) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAccountCreate(@NotNull final String login, @NotNull final String password) {
        Assert.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAccountCharacterCreate(@NotNull final String login, @NotNull final String password) {
        Assert.fail();
    }

    @Override
    public void sendAccountPassword(@NotNull final String currentPassword, @NotNull final String newPassword) {
        Assert.fail();
    }

}

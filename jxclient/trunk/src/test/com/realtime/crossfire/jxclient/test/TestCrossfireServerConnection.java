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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.test;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireComcListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireExpTableListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireFaceListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireMusicListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfirePickupListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnectionListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireSkillInfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireSoundListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireSpellListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireTickListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateFaceListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.server.crossfire.MapSizeListener;
import com.realtime.crossfire.jxclient.server.crossfire.SentReplyListener;
import com.realtime.crossfire.jxclient.server.server.ReceivedPacketListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketListener;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

/**
 * Implements {@link CrossfireServerConnection} for regression tests. All
 * functions do call {@link Assert#fail()}. Sub-classes may override some
 * functions.
 * @author Andreas Kirschbaum
 */
public class TestCrossfireServerConnection implements CrossfireServerConnection
{
    /** {@inheritDoc} */
    @Override
    public void start()
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void stop() throws InterruptedException
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireServerConnectionListener(@NotNull final CrossfireServerConnectionListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addMapSizeListener(@NotNull final MapSizeListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeMapSizeListener(@NotNull final MapSizeListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireDrawinfoListener(@NotNull final CrossfireDrawinfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireDrawinfoListener(@NotNull final CrossfireDrawinfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireDrawextinfoListener(@NotNull final CrossfireDrawextinfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireDrawextinfoListener(@NotNull final CrossfireDrawextinfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireQueryListener(@NotNull final CrossfireQueryListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireQueryListener(@NotNull final CrossfireQueryListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireMagicmapListener(@NotNull final CrossfireMagicmapListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireMagicmapListener(@NotNull final CrossfireMagicmapListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateFaceListener(@NotNull final CrossfireUpdateFaceListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireStatsListener(@NotNull final CrossfireStatsListener crossfireStatsListener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateItemListener(@NotNull final CrossfireUpdateItemListener crossfireUpdateItemListener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateMapListener(@NotNull final CrossfireUpdateMapListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireTickListener(@NotNull final CrossfireTickListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireSoundListener(@NotNull final CrossfireSoundListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireMusicListener(@NotNull final CrossfireMusicListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireComcListener(@NotNull final CrossfireComcListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireFaceListener(@NotNull final CrossfireFaceListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireSpellListener(@NotNull final CrossfireSpellListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireExpTableListener(@NotNull final CrossfireExpTableListener crossfireExpTableListener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireSkillInfoListener(@NotNull final CrossfireSkillInfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireSkillInfoListener(@NotNull final CrossfireSkillInfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfirePickupListener(@NotNull final CrossfirePickupListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfirePickupListener(@NotNull final CrossfirePickupListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addPacketWatcherListener(@NotNull final ReceivedPacketListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removePacketWatcherListener(@NotNull final ReceivedPacketListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addSentReplyListener(@NotNull final SentReplyListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeSentReplyListener(@NotNull final SentReplyListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void drawInfo(@NotNull final String message, final int color)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendAddme()
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendApply(final int tag)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendAskface(final int num)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendExamine(final int tag)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendLock(final boolean val, final int tag)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendLookat(final int dx, final int dy)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendMark(final int tag)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendMove(final int to, final int tag, final int nrof)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public int sendNcom(final int repeat, @NotNull final String command)
    {
        Assert.fail();
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public void sendReply(@NotNull final String text)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendRequestinfo(@NotNull final String infoType)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendSetup(@NotNull final String... options)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendToggleextendedtext(@NotNull final int... types)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendVersion(final int csval, final int scval, @NotNull final String vinfo)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void setMapSize(final int mapWidth, final int mapHeight)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void setNumLookObjects(final int numLookObjects)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public int getMapWidth()
    {
        Assert.fail();
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public int getMapHeight()
    {
        Assert.fail();
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public void addClientSocketListener(@NotNull final ClientSocketListener clientSocketListener)
    {
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public void removeClientSocketListener(@NotNull final ClientSocketListener clientSocketListener)
    {
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public void connect(@NotNull final String hostname, final int port)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect()
    {
        Assert.fail();
    }
}

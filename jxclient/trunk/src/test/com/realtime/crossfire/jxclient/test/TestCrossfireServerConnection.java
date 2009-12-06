package com.realtime.crossfire.jxclient.test;

import com.realtime.crossfire.jxclient.server.ClientSocketListener;
import com.realtime.crossfire.jxclient.server.CrossfireComcListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireExpTableListener;
import com.realtime.crossfire.jxclient.server.CrossfireFaceListener;
import com.realtime.crossfire.jxclient.server.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireMusicListener;
import com.realtime.crossfire.jxclient.server.CrossfirePickupListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnectionListener;
import com.realtime.crossfire.jxclient.server.CrossfireSkillInfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireSoundListener;
import com.realtime.crossfire.jxclient.server.CrossfireSpellListener;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.server.CrossfireTickListener;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateFaceListener;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.server.MapSizeListener;
import com.realtime.crossfire.jxclient.server.ReceivedPacketListener;
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

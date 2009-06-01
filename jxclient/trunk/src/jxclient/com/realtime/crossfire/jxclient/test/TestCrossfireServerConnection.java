package com.realtime.crossfire.jxclient.test;

import com.realtime.crossfire.jxclient.server.ConnectionListener;
import com.realtime.crossfire.jxclient.server.CrossfireComcListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireExpTableListener;
import com.realtime.crossfire.jxclient.server.CrossfireFaceListener;
import com.realtime.crossfire.jxclient.server.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireMusicListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
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
import com.realtime.crossfire.jxclient.server.ScriptMonitorListeners;
import junit.framework.Assert;

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
    public void addMapSizeListener(final MapSizeListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeMapSizeListener(final MapSizeListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireDrawinfoListener(final CrossfireDrawinfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireDrawinfoListener(final CrossfireDrawinfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireDrawextinfoListener(final CrossfireDrawextinfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireDrawextinfoListener(final CrossfireDrawextinfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireQueryListener(final CrossfireQueryListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireQueryListener(final CrossfireQueryListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireMagicmapListener(final CrossfireMagicmapListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireMagicmapListener(final CrossfireMagicmapListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateFaceListener(final CrossfireUpdateFaceListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireStatsListener(final CrossfireStatsListener crossfireStatsListener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateItemListener(final CrossfireUpdateItemListener crossfireUpdateItemListener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireUpdateMapListener(final CrossfireUpdateMapListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireTickListener(final CrossfireTickListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireSoundListener(final CrossfireSoundListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireMusicListener(final CrossfireMusicListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireComcListener(final CrossfireComcListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireFaceListener(final CrossfireFaceListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireSpellListener(final CrossfireSpellListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireExpTableListener(final CrossfireExpTableListener crossfireExpTableListener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addCrossfireSkillInfoListener(final CrossfireSkillInfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removeCrossfireSkillInfoListener(final CrossfireSkillInfoListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void addPacketWatcherListener(final ReceivedPacketListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void removePacketWatcherListener(final ReceivedPacketListener listener)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void drawInfo(final String message, final int color)
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
    public int sendNcom(final int repeat, final String command)
    {
        Assert.fail();
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public void sendReply(final String text)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendRequestinfo(final String infoType)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendSetup(final String... options)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendToggleextendedtext(final int... types)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void sendVersion(final int csval, final int scval, final String vinfo)
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
    public void addConnectionListener(final ConnectionListener connectionListener)
    {
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override
    public void connect(final String hostname, final int port)
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect()
    {
        Assert.fail();
    }

    /** {@inheritDoc} */
    @Override
    public ScriptMonitorListeners getScriptMonitorListeners()
    {
        Assert.fail();
        throw new AssertionError();
    }
}

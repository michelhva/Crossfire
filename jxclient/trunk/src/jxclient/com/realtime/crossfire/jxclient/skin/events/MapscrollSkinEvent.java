package com.realtime.crossfire.jxclient.skin.events;

import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.mapupdater.MapscrollListener;
import com.realtime.crossfire.jxclient.window.GUICommandList;

/**
 * A {@link SkinEvent} that executes a {@link GUICommandList} whenever the
 * map scroll protocol command is received.
 * @author Andreas Kirschbaum
 */
public class MapscrollSkinEvent implements SkinEvent
{
    /**
     * The {@link GUICommandList} to execute.
     */
    private final GUICommandList commandList;

    /**
     * The {@link CfMapUpdater} to attach to.
     */
    private final CfMapUpdater mapUpdater;

    /**
     * The {@link MapscrollListener} attached to {@link #mapUpdater}.
     */
    private final MapscrollListener mapscrollListener = new MapscrollListener()
    {
        /** {@inheritDoc} */
        @Override
        public void mapScrolled(final int dx, final int dy)
        {
            commandList.execute();
        }
    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute
     * @param mapUpdater the map updater to attach to
     */
    public MapscrollSkinEvent(final GUICommandList commandList, final CfMapUpdater mapUpdater)
    {
        this.commandList = commandList;
        this.mapUpdater = mapUpdater;
        mapUpdater.addCrossfireMapscrollListener(mapscrollListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        mapUpdater.removeCrossfireMapscrollListener(mapscrollListener);
    }
}

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
 * Copyright (C) 2010 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireFailureListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import java.awt.Color;
import java.awt.Font;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIHTMLLabel} that displays the last received "failure" message.
 * @author Nicolas Weeger
 */
public class GUILabelFailure extends GUIMultiLineLabel {

    /**
     * The {@link CrossfireServerConnection} to monitor.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link CrossfireFailureListener} registered to receive failure messages.
     */
    @NotNull
    private final CrossfireFailureListener crossfireFailureListener = new CrossfireFailureListener() {
        public void failure(String command, String arguments) {
            setText(arguments);
        }
    };

    /**
     * Create a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name The name of this element.
     * @param extent the extent of this element
     * @param crossfireServerConnection the connection instance
     * @param windowRenderer the window renderer this element belongs to
     * @param font The font to use.
     * @param color The color to use.
     * @param backgroundColor The background color.
     */
    public GUILabelFailure(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Extent extent, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final Font font, @NotNull final Color color, @NotNull final Color backgroundColor) {
        super(tooltipManager, elementListener, name, extent, null, font, color, backgroundColor, Alignment.LEFT, "");
        this.crossfireServerConnection = crossfireServerConnection;
        this.crossfireServerConnection.addCrossfireFailureListener(crossfireFailureListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        crossfireServerConnection.removeCrossfireFailureListener(crossfireFailureListener);
    }

}

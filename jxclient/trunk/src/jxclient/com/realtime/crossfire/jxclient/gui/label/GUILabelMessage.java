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

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Font;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIHTMLLabel} that displays the last received "drawinfo" message.
 * @author Andreas Kirschbaum
 */
public class GUILabelMessage extends GUIMultiLineLabel {

    /**
     * The maximum line length in characters.
     */
    private static final int MAX_LINE_LENGTH = 75;

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link CrossfireServerConnection} to monitor.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link JXCWindowRenderer} this element belongs to.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link CrossfireDrawinfoListener} registered to receive drawinfo
     * messages.
     */
    @NotNull
    private final CrossfireDrawinfoListener crossfireDrawinfoListener = new CrossfireDrawinfoListener() {

        @Override
        public void commandDrawinfoReceived(@NotNull final String text, final int type) {
            final Gui gui = GuiUtils.getGui(GUILabelMessage.this);
            if (gui == null || !windowRenderer.isDialogOpen(gui)) {
                setText(text);
            }
        }

    };

    /**
     * The {@link CrossfireDrawextinfoListener} registered to receive
     * drawextinfo messages.
     */
    @NotNull
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener() {

        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, @NotNull final String message) {
            final Gui gui = GuiUtils.getGui(GUILabelMessage.this);
            if (gui == null || !windowRenderer.isDialogOpen(gui)) {
                setText(message);
            }
        }

        @Override
        public void setDebugMode(final boolean printMessageTypes) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param crossfireServerConnection the connection instance
     * @param windowRenderer the window renderer this element belongs to
     * @param font the font to use
     * @param color the color to use
     * @param backgroundColor the background color
     */
    public GUILabelMessage(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final Font font, @NotNull final Color color, @Nullable final Color backgroundColor) {
        super(tooltipManager, elementListener, name, null, font, color, backgroundColor, Alignment.LEFT, "");
        this.crossfireServerConnection = crossfireServerConnection;
        this.windowRenderer = windowRenderer;
        this.crossfireServerConnection.addCrossfireDrawinfoListener(crossfireDrawinfoListener);
        this.crossfireServerConnection.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        crossfireServerConnection.removeCrossfireDrawinfoListener(crossfireDrawinfoListener);
        crossfireServerConnection.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setText(@NotNull final String text) {
        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        while (pos < text.length()) {
            sb.append('\n');

            int endPos = pos;
            while (endPos < pos+MAX_LINE_LENGTH && endPos < text.length() && text.charAt(endPos) != '\n') {
                endPos++;
            }

            if (endPos >= pos+MAX_LINE_LENGTH) {
                // whitespace split
                int endPos2 = endPos;
                while (endPos2 > pos) {
                    endPos2--;
                    final int ch = text.charAt(endPos2);
                    if (ch == ' ') {
                        break;
                    }
                }
                if (endPos2 > pos) {
                    sb.append(text.substring(pos, endPos2));
                    pos = endPos2+1;
                } else {
                    sb.append(text.substring(pos, endPos));
                    pos = endPos;
                }
            } else if (endPos >= text.length()) {
                // append all
                sb.append(text.substring(pos));
                pos = endPos;
            } else {
                assert text.charAt(endPos) == '\n';
                // append segment
                sb.append(text.substring(pos, endPos));
                pos = endPos+1;
            }
        }
        super.setText(sb.length() > 0 ? sb.substring(1) : "");
    }

}

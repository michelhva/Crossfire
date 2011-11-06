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

package com.realtime.crossfire.jxclient.gui.log;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.MessageTypes;
import java.awt.Color;
import org.jetbrains.annotations.NotNull;

/**
 * Adds drawinfo, drawextinfo, and query messages to a {@link Buffer} instance.
 * @author Andreas Kirschbaum
 */
public class MessageBufferUpdater {

    /**
     * The number of supported colors.
     */
    public static final int NUM_COLORS = 13;

    /**
     * Maps color index to color.
     */
    @NotNull
    private final Color[] colors = {
        // black
        new Color(0x000000),
        // white
        new Color(0x860F9F),
        // navy blue
        new Color(0x362589),
        // red
        new Color(0xD80C1B),
        // orange
        new Color(0xAF0093),
        // dodger blue
        new Color(0x2E00B7),
        // dark orange
        new Color(0xCF4507),
        // sea green
        new Color(0x275A0F),
        // dark sea green
        new Color(0x346B1A),
        // grey
        new Color(0x353535),
        // brown sienna
        new Color(0x775716),
        // gold
        new Color(0x874200),
        // khaki
        new Color(0x6C6E20),
    };

    /**
     * The colors names corresponding to {@link #colors}.
     */
    @NotNull
    private static final String[] COLOR_NAMES = {
        "black",
        "white",
        "navy blue",
        "red",
        "orange",
        "dodger blue",
        "dark orange",
        "sea green",
        "dark sea green",
        "grey",
        "brown sienna",
        "gold",
        "khaki",
    };

    /**
     * The {@link CrossfireServerConnection} to monitor.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link Parser} instance for parsing drawextinfo messages.
     */
    @NotNull
    private final Parser parser = new Parser();

    /**
     * The color to use for invalid colors indices.
     */
    @NotNull
    private final Color defaultColor;

    /**
     * The buffer to update.
     */
    @NotNull
    private final Buffer buffer;

    /**
     * The types to show.
     */
    private int types = ~0;

    /**
     * Whether message types are included in the buffer output.
     */
    private boolean printMessageTypes = false;

    /**
     * The {@link CrossfireQueryListener} registered to receive query commands.
     */
    @NotNull
    private final CrossfireQueryListener crossfireQueryListener = new CrossfireQueryListener() {

        @Override
        public void commandQueryReceived(@NotNull final String prompt, final int queryType) {
            if (isTypeShown(MessageTypes.MSG_TYPE_QUERY)) {
                parser.parseWithoutMediaTags(addMessageTypePrefix(MessageTypes.MSG_TYPE_QUERY, 0, CrossfireDrawinfoListener.NDI_RED, prompt), findColor(CrossfireDrawinfoListener.NDI_RED), buffer);
            }
        }

    };

    /**
     * The {@link CrossfireDrawextinfoListener} registered to receive
     * drawextinfo commands.
     */
    @NotNull
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener() {

        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, @NotNull final String message) {
            if (type == MessageTypes.MSG_TYPE_QUERY // should not happen; but if it happens just display it
                || isTypeShown(type)) {
                final CharSequence messageWithPrefix = addMessageTypePrefix(type, subtype, color, message);
                if (type == MessageTypes.MSG_TYPE_COMMUNICATION) {
                    parser.parseWithoutMediaTags(messageWithPrefix, findColor(color), buffer);
                } else {
                    parser.parse(messageWithPrefix, findColor(color), buffer);
                }
            }
        }

        @Override
        public void setDebugMode(final boolean printMessageTypes) {
            MessageBufferUpdater.this.printMessageTypes = printMessageTypes;
        }

    };

    /**
     * The {@link CrossfireDrawinfoListener} registered to receive drawinfo
     * commands.
     */
    @NotNull
    private final CrossfireDrawinfoListener crossfireDrawinfoListener = new CrossfireDrawinfoListener() {

        @Override
        public void commandDrawinfoReceived(@NotNull final String text, final int type) {
            // guess category from message color
            final int messageType;
            switch (type) {
            case NDI_WHITE:
            case NDI_ORANGE:
            case NDI_BLUE:
            case NDI_RED:
                messageType = MessageTypes.MSG_TYPE_COMMUNICATION;
                break;

            default:
                messageType = MessageTypes.MSG_TYPE_MISC;
                break;
            }

            if (isTypeShown(messageType)) {
                parser.parseWithoutMediaTags(addMessageTypePrefix(messageType, 0, type, text), findColor(type), buffer);
            }
        }

    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection instance
     * @param buffer the buffer to update
     * @param defaultColor the default color to use for undefined colors
     * indices
     */
    public MessageBufferUpdater(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final Buffer buffer, @NotNull final Color defaultColor) {
        this.crossfireServerConnection = crossfireServerConnection;
        this.buffer = buffer;
        this.defaultColor = defaultColor;
        this.crossfireServerConnection.addCrossfireQueryListener(crossfireQueryListener);
        this.crossfireServerConnection.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        this.crossfireServerConnection.addCrossfireDrawinfoListener(crossfireDrawinfoListener);
    }

    /**
     * Releases resources.
     */
    public void dispose() {
        crossfireServerConnection.removeCrossfireQueryListener(crossfireQueryListener);
        crossfireServerConnection.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        crossfireServerConnection.removeCrossfireDrawinfoListener(crossfireDrawinfoListener);
    }

    /**
     * Converts a Crossfire color index to a {@link Color} instance.
     * @param index the color index to look up
     * @return the color
     */
    @NotNull
    private Color findColor(final int index) {
        try {
            return colors[index];
        } catch (final ArrayIndexOutOfBoundsException ignored) {
            return defaultColor;
        }
    }

    /**
     * Returns the name of a color index.
     * @param index the color index
     * @return the color name
     */
    @NotNull
    public static String getColorName(final int index) {
        try {
            return COLOR_NAMES[index];
        } catch (final ArrayIndexOutOfBoundsException ignored) {
            return "undefined";
        }
    }

    /**
     * Sets a color mapping.
     * @param index the color index to change
     * @param color the color to map to
     */
    public void setColor(final int index, @NotNull final Color color) {
        colors[index] = color;
    }

    /**
     * Sets the message types to show.
     * @param types the types to show
     */
    public void setTypes(final int types) {
        this.types = types;
    }

    /**
     * Returns whether a message type should be shown.
     * @param type the message type
     * @return whether the message type should be shown
     */
    private boolean isTypeShown(final int type) {
        return type < 0 || type > 31 || (types&(1<<type)) != 0;
    }

    /**
     * Adds a message type prefix to a message if {@link #printMessageTypes} is
     * set.
     * @param type the message type
     * @param subtype the message subtype
     * @param color the message color
     * @param message the message
     * @return the message with prefix
     */
    private CharSequence addMessageTypePrefix(final int type, final int subtype, final int color, @NotNull final String message) {
        return printMessageTypes ? "(t="+MessageTypes.toString(type)+"/"+subtype+",c="+color+")"+message : message;
    }

}

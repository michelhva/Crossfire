//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.gui.log;

import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import java.awt.Color;

/**
 * Adds drawinfo, drawextinfo, and query messages to a {@link Buffer} instance.
 * @author Andreas Kirschbaum
 */
public class MessageBufferUpdater
{
    /**
     * The number of supported colors.
     */
    public static final int NUM_COLORS = 13;

    /**
     * Maps color index to color.
     */
    private final Color[] colors =
    {
        Color.BLACK,            // black
        Color.WHITE,            // white
        Color.BLUE,             // navy blue
        Color.RED,              // red
        Color.ORANGE,           // orange
        Color.CYAN,             // dodger blue
        new Color(0xFFC000),    // dark orange
        Color.GREEN,            // sea green
        new Color(0x008000),    // dark sea green
        Color.GRAY,             // grey
        new Color(0x806000),    // brown sienna
        Color.YELLOW,           // gold
        new Color(0xBDB76B),    // khaki
    };

    /**
     * The colors names corresponding to {@link #colors}.
     */
    private static final String[] COLOR_NAMES =
    {
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
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link Parser} instance for parsing drawextinfo messages.
     */
    private final Parser parser = new Parser();

    /**
     * The color to use for invalid colors indices.
     */
    private final Color defaultColor;

    /**
     * The buffer to update.
     */
    private final Buffer buffer;

    /**
     * The types to show.
     */
    private int types = ~0;

    /**
     * The {@link CrossfireQueryListener} registered to receive query commands.
     */
    private final CrossfireQueryListener crossfireQueryListener = new CrossfireQueryListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandQueryReceived(final String prompt, final int queryType)
        {
            if (isTypeShown(MessageTypes.MSG_TYPE_QUERY))
            {
                parser.parseWithoutMediaTags(prompt, Color.RED, buffer);
            }
        }
    };

    /**
     * The {@link CrossfireDrawextinfoListener} registered to receive
     * drawextinfo commands.
     */
    private final CrossfireDrawextinfoListener crossfireDrawextinfoListener = new CrossfireDrawextinfoListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandDrawextinfoReceived(final int color, final int type, final int subtype, final String message)
        {
            if (type == MessageTypes.MSG_TYPE_QUERY // should not happen; but if it happens just display it
            || isTypeShown(type))
            {
                if (type == MessageTypes.MSG_TYPE_COMMUNICATION)
                {
                    parser.parseWithoutMediaTags(message, findColor(color), buffer);
                }
                else
                {
                    parser.parse(message, findColor(color), buffer);
                }
            }
        }
    };

    /**
     * The {@link CrossfireDrawinfoListener} registered to receive drawinfo
     * commands.
     */
    private final CrossfireDrawinfoListener crossfireDrawinfoListener = new CrossfireDrawinfoListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandDrawinfoReceived(final String text, final int type)
        {
            // guess category from message color
            final int messageType;
            switch (type)
            {
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

            if (isTypeShown(messageType))
            {
                parser.parseWithoutMediaTags(text, findColor(type), buffer);
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
    public MessageBufferUpdater(final CrossfireServerConnection crossfireServerConnection, final Buffer buffer, final Color defaultColor)
    {
        this.crossfireServerConnection = crossfireServerConnection;
        this.buffer = buffer;
        this.defaultColor = defaultColor;
        this.crossfireServerConnection.addCrossfireQueryListener(crossfireQueryListener);
        this.crossfireServerConnection.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        this.crossfireServerConnection.addCrossfireDrawinfoListener(crossfireDrawinfoListener);
    }

    /** {@inheritDoc} */
    public void dispose()
    {
        crossfireServerConnection.removeCrossfireQueryListener(crossfireQueryListener);
        crossfireServerConnection.removeCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        crossfireServerConnection.removeCrossfireDrawinfoListener(crossfireDrawinfoListener);
    }

    /**
     * Converts a Crossfire color index to a {@link Color} instance.
     * @param index the color index to look up
     * @return the color
     */
    private Color findColor(final int index)
    {
        try
        {
            return colors[index];
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            return defaultColor;
        }
    }

    /**
     * Returns the name of a color index.
     * @param index the color index
     * @return the color name
     */
    public static String getColorName(final int index)
    {
        try
        {
            return COLOR_NAMES[index];
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            return "undefined";
        }
    }

    /**
     * Sets a color mapping.
     * @param index the color index to change
     * @param color the color to map to
     */
    public void setColor(final int index, final Color color)
    {
        colors[index] = color;
    }

    /**
     * Sets the message types to show.
     * @param types the types to show
     */
    public void setTypes(final int types)
    {
        this.types = types;
    }

    /**
     * Returns whether a message type should be shown.
     * @param type the message type
     * @return whether the message type should be shown
     */
    private boolean isTypeShown(final int type)
    {
        return type < 0 || type > 31 || (types&(1<<type)) != 0;
    }
}

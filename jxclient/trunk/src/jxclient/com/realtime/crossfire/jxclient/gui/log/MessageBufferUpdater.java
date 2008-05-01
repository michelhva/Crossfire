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

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawextinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandQueryEvent;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireQueryListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import java.awt.Color;

/**
 * Adds drawinfo, drawextinfo, and query messages to a {@link Buffer} instance.
 *
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
    private final Color[] colors = new Color[]
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
        public void commandQueryReceived(final CrossfireCommandQueryEvent evt)
        {
            if (isTypeShown(MessageTypes.MSG_TYPE_QUERY))
            {
                parser.parseWithoutMediaTags(evt.getPrompt(), Color.RED, buffer);
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
        public void commandDrawextinfoReceived(final CrossfireCommandDrawextinfoEvent evt)
        {
            if (evt.getType() == MessageTypes.MSG_TYPE_QUERY // should not happen; but if it happens just display it
            || isTypeShown(evt.getType()))
            {
                parser.parse(evt.getMessage(), findColor(evt.getColor()), buffer);
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
        public void commandDrawinfoReceived(final CrossfireCommandDrawinfoEvent evt)
        {
            // guess category from message color
            final int type;
            switch(evt.getTextType())
            {
            case CrossfireCommandDrawinfoEvent.NDI_WHITE:
            case CrossfireCommandDrawinfoEvent.NDI_ORANGE:
            case CrossfireCommandDrawinfoEvent.NDI_BLUE:
            case CrossfireCommandDrawinfoEvent.NDI_RED:
                type = MessageTypes.MSG_TYPE_COMMUNICATION;
                break;

            default:
                type = MessageTypes.MSG_TYPE_MISC;
                break;
            }

            if (isTypeShown(type))
            {
                parser.parseWithoutMediaTags(evt.getText(), findColor(evt.getTextType()), buffer);
            }
        }
    };

    /**
     * Create a new instance.
     *
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
     *
     * @param crossfireServerConnection the connection instance
     *
     * @param buffer The buffer to update.
     *
     * @param defaultColor The default color to use for undefined colors
     * indices.
     */
    public MessageBufferUpdater(final JXCWindow jxcWindow, final CrossfireServerConnection crossfireServerConnection, final Buffer buffer, final Color defaultColor)
    {
        this.buffer = buffer;
        this.defaultColor = defaultColor;
        crossfireServerConnection.addCrossfireQueryListener(crossfireQueryListener);
        crossfireServerConnection.addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        crossfireServerConnection.addCrossfireDrawinfoListener(crossfireDrawinfoListener);
    }

    /**
     * Convert a Crossfire color index to a {@link Color} instance.
     *
     * @param index The color index to look up.
     *
     * @return The color.
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
     * Set a color mapping.
     *
     * @param index The color index to change.
     *
     * @param color The color to map to.
     */
    public void setColor(final int index, final Color color)
    {
        colors[index] = color;
    }

    /**
     * Set the message types to show.
     *
     * @param types The types to show.
     */
    public void setTypes(final int types)
    {
        this.types = types;
    }

    /**
     * Return whether a message type should be shown.
     *
     * @param type The message type.
     *
     * @return Whether the message type should be shown.
     */
    private boolean isTypeShown(final int type)
    {
        return type < 0 || type > 31 || (types&(1<<type)) != 0;
    }
}

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
import com.realtime.crossfire.jxclient.server.MessageTypes;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Adds drawinfo, drawextinfo, and query messages to a {@link #Buffer}
 * instance.
 *
 * @author Andreas Kirschbaum
 */
public class MessageBufferUpdater
{
    /**
     * Maps color index to color.
     */
    private final Map<Integer, Color> colors = new HashMap<Integer, Color>();
    {
        colors.put(0, Color.BLACK); //black
        colors.put(1, Color.WHITE); //white
        colors.put(2, Color.BLUE); //navy blue
        colors.put(3, Color.RED); //red
        colors.put(4, Color.ORANGE); //orange
        colors.put(5, Color.CYAN); //dodger blue
        colors.put(6, new Color(0xFFC000)); //dark orange
        colors.put(7, Color.GREEN); //sea green
        colors.put(8, new Color(0x008000)); //dark sea green
        colors.put(9, Color.GRAY); //grey
        colors.put(10, new Color(0x806000)); //brown sienna
        colors.put(11, Color.YELLOW); //gold
        colors.put(12, new Color(0xBDB76B)); //khaki
    }

    /**
     * The {@link Parser} instance for parsing drawextinfo messages.
     */
    private final Parser parser = new Parser();

    /**
     * The buffer to update.
     */
    private final Buffer buffer;

    /**
     * The default color to use for text message not specifying a color.
     */
    private final Color defaultColor;

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
            if (isTypeShown(MessageTypes.MSG_TYPE_MISC))
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
     * @param buffer The buffer to update.
     *
     * @param defaultColor The default color to use for text message not
     * specifying a color.
     */
    public MessageBufferUpdater(final JXCWindow jxcWindow, final Buffer buffer, final Color defaultColor)
    {
        this.buffer = buffer;
        this.defaultColor = defaultColor;
        jxcWindow.getCrossfireServerConnection().addCrossfireQueryListener(crossfireQueryListener);
        jxcWindow.getCrossfireServerConnection().addCrossfireDrawextinfoListener(crossfireDrawextinfoListener);
        jxcWindow.getCrossfireServerConnection().addCrossfireDrawinfoListener(crossfireDrawinfoListener);
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
        final Color color = colors.get(index);
        return color == null ? defaultColor : color;
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
        colors.put(index, color);
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

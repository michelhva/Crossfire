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
package com.realtime.crossfire.jxclient.server;

import java.util.EventObject;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class CrossfireCommandQueryEvent extends EventObject
{
    /** The serial version UID. */
    private static final long serialVersionUID = 1;

    /** Query type: yes/no question. */
    public static final int YESNO = 1;

    /** Query type: single character response expected. */
    public static final int SINGLECHAR = 2;

    /** Query type: hide input being entered. */
    public static final int HIDEINPUT = 4;

    private final String prompt;

    private final int queryType;

    public CrossfireCommandQueryEvent(final Object src, final String prompt, final int queryType)
    {
        super(src);
        this.prompt = prompt;
        this.queryType = queryType;
    }

    public String getPrompt()
    {
        return prompt;
    }

    /**
     * Return the query type as a bitmask of {@link #YESNO}, {@link
     * #SINGLECHAR} and {@link #HIDEINPUT}.
     *
     * @return The query type bitmask.
     */
    public int getQueryType()
    {
        return queryType;
    }
}

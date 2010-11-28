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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.skin.io;

import com.realtime.crossfire.jxclient.util.NumberParser;
import java.awt.GridBagConstraints;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class GridBagConstraintParser implements ConstraintParser {

    /**
     * Whether to run in horizontal mode (<code>true</code>) or vertical mode
     * (<code>false</code>).
     */
    private final boolean horizontal;

    /**
     * Creates a new instance.
     * @param horizontal whether to run in horizontal mode (<code>true</code>)
     * or vertical mode (<code>false</code>)
     */
    public GridBagConstraintParser(final boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public GridBagConstraints parseConstraints(@NotNull final Args args) throws IOException {
        final String weightStr = args.get();
        final float weight = NumberParser.parseFloat(weightStr);
        if (weight < 0 || weight > 1F) {
            throw new IOException("invalid weight '"+weightStr+"'");
        }

        final String constraint = args.get();
        final int fill;
        if (constraint.equals("CENTER")) {
            fill = GridBagConstraints.BOTH; // XXX
        } else if (constraint.equals("NONE")) {
            fill = GridBagConstraints.NONE;
        } else if (constraint.equals("HORIZONTAL")) {
            fill = GridBagConstraints.HORIZONTAL;
        } else if (constraint.equals("VERTICAL")) {
            fill = GridBagConstraints.VERTICAL;
        } else if (constraint.equals("BOTH")) {
            fill = GridBagConstraints.BOTH;
        } else {
            throw new IOException("invalid constraint '"+constraint+"'");
        }

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = horizontal ? 1 : 0;
        gbc.gridy = horizontal ? 0 : 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = horizontal ? weight : 1F;
        gbc.weighty = horizontal ? 1F : weight;
        gbc.fill = fill;
        return gbc;
    }

}

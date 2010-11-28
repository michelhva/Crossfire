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

import com.realtime.crossfire.jxclient.skin.skin.Expression;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import com.realtime.crossfire.jxclient.util.NumberParser;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class LayeredConstraintParser implements ConstraintParser {

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public LayeredConstraint parseConstraints(@NotNull final Args args) throws IOException {
        final String layerStr = args.get();
        final int layer = NumberParser.parseInt(layerStr, 0);
        if (layer < 0 || layer > 1000) {
            throw new IOException("invalid layer '"+layerStr+"'");
        }

        final Expression x = ExpressionParser.parseExpression(args.get());
        final Expression y = ExpressionParser.parseExpression(args.get());
        final Expression w = ExpressionParser.parseExpression(args.get());
        final Expression h = ExpressionParser.parseExpression(args.get());
        return new LayeredConstraint(new Extent(x, y, w, h), layer);
    }

}

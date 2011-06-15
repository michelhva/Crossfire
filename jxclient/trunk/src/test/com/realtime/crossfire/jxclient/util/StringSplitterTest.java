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

package com.realtime.crossfire.jxclient.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Regression tests for class {@link StringSplitter}.
 * @author Andreas Kirschbaum
 */
public class StringSplitterTest {

    /**
     * Checks that {@link StringSplitter#splitAsHtml(String)} does work.
     */
    @Test
    public void testSplit() {
        Assert.assertEquals("", StringSplitter.splitAsHtml(""));
        Assert.assertEquals("a", StringSplitter.splitAsHtml("a"));
        Assert.assertEquals("abc", StringSplitter.splitAsHtml("abc"));
        Assert.assertEquals("a b c", StringSplitter.splitAsHtml("a b c"));
        Assert.assertEquals("a  b  c", StringSplitter.splitAsHtml("  a  b  c  "));

        Assert.assertEquals("a b c d e f g h i j k l m n o p q r s t u v w x y<br>"+"A B C D E", StringSplitter.splitAsHtml("a b c d e f g h i j k l m n o p q r s t u v w x y A B C D E"));
        Assert.assertEquals("a b c d e f g h i j k l m n o p q r s t u v w x y<br>"+"A B C D E F G H I J K L M N O P Q R S T U V W X Y<br>"+"a b c d e f g h i j k l m n o p q r s t u v w x y<br>"+"A B C D E", StringSplitter.splitAsHtml("a b c d e f g h i j k l m n o p q r s t u v w x y A B C D E F G H I J K L M N O P Q R S T U V W X Y a b c d e f g h i j k l m n o p q r s t u v w x y A B C D E"));
        Assert.assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa<br>"+"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb<br>"+"cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc<br>"+"cccccccccccccccccccc ddddddddddddddddddddddddddddd<br>"+"eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee<br>"+"ffffffffffffffffffffffffffffff<br>"+"gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg<br>"+"gggggggggggggggggggg", StringSplitter.splitAsHtml("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc ddddddddddddddddddddddddddddd eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ffffffffffffffffffffffffffffff gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"));
    }

}

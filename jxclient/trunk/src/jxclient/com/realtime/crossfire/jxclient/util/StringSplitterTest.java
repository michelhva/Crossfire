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
package com.realtime.crossfire.jxclient.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Regression tests for class {@link StringSplitter}.
 * @author Andreas Kirschbaum
 */
public class StringSplitterTest extends TestCase
{
    /**
     * Creates a new instance.
     * @param name the test case name
     */
    public StringSplitterTest(final String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(StringSplitterTest.class);
    }

    /**
     * Runs the regression tests.
     * @param args the command line arguments (ignored)
     */
    public static void main(final String[] args)
    {
        TestRunner.run(suite());
    }

    public void testSplit()
    {
	assertEquals("", StringSplitter.splitAsHtml(""));
	assertEquals("a", StringSplitter.splitAsHtml("a"));
	assertEquals("abc", StringSplitter.splitAsHtml("abc"));
	assertEquals("a b c", StringSplitter.splitAsHtml("a b c"));
	assertEquals("a  b  c", StringSplitter.splitAsHtml("  a  b  c  "));

	assertEquals(
	    "a b c d e f g h i j k l m n o p q r s t u v w x y<br>"+
	    "A B C D E",
	    StringSplitter.splitAsHtml("a b c d e f g h i j k l m n o p q r s t u v w x y A B C D E"));
	assertEquals(
	    "a b c d e f g h i j k l m n o p q r s t u v w x y<br>"+
	    "A B C D E F G H I J K L M N O P Q R S T U V W X Y<br>"+
	    "a b c d e f g h i j k l m n o p q r s t u v w x y<br>"+
	    "A B C D E",
	    StringSplitter.splitAsHtml("a b c d e f g h i j k l m n o p q r s t u v w x y A B C D E F G H I J K L M N O P Q R S T U V W X Y a b c d e f g h i j k l m n o p q r s t u v w x y A B C D E"));
	assertEquals(
	    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa<br>"+
	    "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb<br>"+
	    "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc<br>"+
	    "cccccccccccccccccccc ddddddddddddddddddddddddddddd<br>"+
	    "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee<br>"+
	    "ffffffffffffffffffffffffffffff<br>"+
	    "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg<br>"+
	    "gggggggggggggggggggg",
	    StringSplitter.splitAsHtml("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc ddddddddddddddddddddddddddddd eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ffffffffffffffffffffffffffffff gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"));
    }
}

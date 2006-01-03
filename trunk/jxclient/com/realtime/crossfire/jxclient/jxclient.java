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
package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This is the entry point for JXClient. Note that this class doesn't do much
 * by itself - most of the work in done in JXCWindow or ServerConnection.
 * @see com.realtime.crossfire.jxclient.JXCWindow
 * @see com.realtime.crossfire.jxclient.ServerConnection
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class jxclient
{
    /**
     * The program entry point.
     * @since 1.0
     */
    public static void main(String args[])
    {
        System.out.println("JXClient - Crossfire Java Client");
        System.out.println("(C)2005 by Lauwenmark.");
        System.out.println("This software is placed under the GPL License");
        jxclient game = new jxclient(args);
    }

    /**
     * The constructor of the class. This is where the main window is created.
     * Initialization of a JXCWindow is the only task performed here.
     * @since 1.0
     */
    public jxclient(String args[])
    {
        try
        {
           JXCWindow jxwin = new JXCWindow();
           jxwin.init(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                      Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
}

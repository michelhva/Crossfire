package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class jxclient
{
    public static void main(String args[])
    {
        System.out.println("JXClient - Crossfire Java Client");
        System.out.println("(C)2005 by Lauwenmark.");
        System.out.println("This software is placed under the GPL License");
        jxclient game = new jxclient(args);
    }
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

package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUICommand
{
    private GUIElement mytarget;
    private int myorder;
    private Object myparams; //Often a String, but not always - see CMD_QUIT or CMD_CONNECT

    public static final int CMD_SHOW       = 0;
    public static final int CMD_HIDE       = 1;
    public static final int CMD_TOGGLE     = 2;
    public static final int CMD_PRINT      = 3;
    public static final int CMD_QUIT       = 4;
    public static final int CMD_SCROLLUP   = 5;
    public static final int CMD_SCROLLDOWN = 6;
    public static final int CMD_CONNECT    = 7;
    public static final int CMD_GUI_META   = 8;
    public static final int CMD_GUI_START  = 9;
    public static final int CMD_GUI_LEAVE_DIALOG  = 10;
    public static final int CMD_GUI_SEND_COMMAND = 11;

    public GUICommand(GUIElement element, int order, Object params)
    {
        mytarget = element;
        myorder = order;
        myparams = params;
    }
    public void execute()
    {
        //System.out.println("Executing command "+myorder+ " on "+mytarget.getName());
        switch (myorder)
        {
            case CMD_SHOW:
                if (mytarget.isVisible()==false)
                    mytarget.setVisible(true);
                break;
            case CMD_HIDE:
                if (mytarget.isVisible()==true)
                    mytarget.setVisible(false);
                break;
            case CMD_TOGGLE:
                if (mytarget.isVisible()==true)
                    mytarget.setVisible(false);
                else
                    mytarget.setVisible(true);
                break;
            case CMD_PRINT:
                break;
            case CMD_QUIT:
                ((JXCWindow)myparams).endRendering();
                break;
            case CMD_SCROLLUP:
                if (mytarget instanceof GUIScrollable)
                    ((GUIScrollable)mytarget).scrollUp();
                break;
            case CMD_SCROLLDOWN:
                if (mytarget instanceof GUIScrollable)
                    ((GUIScrollable)mytarget).scrollDown();
                break;
            case CMD_CONNECT:
                ((JXCWindow)myparams).connect(((GUIText)mytarget).getText(), 13327);
                break;
            case CMD_GUI_META:
                ((JXCWindow)myparams).changeGUI(JXCWindow.GUI_METASERVER);
                break;
            case CMD_GUI_START:
                ((JXCWindow)myparams).changeGUI(JXCWindow.GUI_START);
                break;
            case CMD_GUI_LEAVE_DIALOG:
                ((JXCWindow)myparams).setDialogStatus(JXCWindow.DLG_NONE);
                break;
            case CMD_GUI_SEND_COMMAND:
                java.util.List lp = (java.util.List)myparams;
                JXCWindow jxcw = (JXCWindow)lp.get(0);
                String cmd = (String)lp.get(1);
                jxcw.send("command 0 "+cmd);
                break;
        }
    }
    public Object getParams()
    {
        return myparams;
    }
}

package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class GUICommandText extends GUIText implements KeyListener
{
    public GUICommandText
            (String nn, int nx, int ny, int nw, int nh, String picactive,
             String picinactive, Font nf, String txt)  throws IOException
    {
        super(nn,nx,ny,nw,nh,picactive,picinactive,nf,txt);
    }
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_BACK_SPACE:
                if (mytext.length()>0)
                {
                    mytext = mytext.substring(0,mytext.length()-1);
                    render();
                }
                break;
            case KeyEvent.VK_DELETE:
                if (mytext.length()>0)
                {
                    mytext = "";
                    render();
                }
                break;
            case KeyEvent.VK_ENTER:
                switch(((JXCWindow)e.getSource()).getServerConnection().getStatus())
                {
                    case ServerConnection.STATUS_PLAYING:
                        System.out.println("Command:"+mytext);
                        if (mytext.startsWith("bind "))
                        {
                            String cmdl = mytext.substring(5);
                            String[] cmds = cmdl.split(";");
                            java.util.List list_parms;
                            GUICommand guicmd;
                            java.util.List <GUICommand> list_commands =
                                    new ArrayList<GUICommand>();

                            for(int i=0; i<cmds.length;i++)
                            {
                                list_parms = new ArrayList();
                                list_parms.add(e.getSource());
                                list_parms.add(cmds[i]);
                                guicmd = new GUICommand(null, GUICommand.CMD_GUI_SEND_COMMAND,
                                        list_parms);
                                list_commands.add(guicmd);
                            }
                            ((JXCWindow)e.getSource()).createKeyBinding(list_commands);
                        }
                        else if (mytext.startsWith("unbind"))
                        {
                            ((JXCWindow)e.getSource()).removeKeyBinding();
                        }
                        else
                        {
                            ((JXCWindow)e.getSource()).send("command 0 "+mytext);
                        }
                        mytext="";
                        setActive(false);
                        break;
                    case ServerConnection.STATUS_QUERY:
                        ((JXCWindow)e.getSource()).getServerConnection().setStatus(
                                ServerConnection.STATUS_PLAYING);
                        ((JXCWindow)e.getSource()).send("reply "+mytext);
                        ((JXCWindow)e.getSource()).setDialogStatus(JXCWindow.DLG_NONE);
                        mytext="";
                        setActive(false);
                        break;
                    default:
                        mytext="";
                        setActive(false);
                        break;
                }
                break;
            case KeyEvent.VK_SHIFT:
                break;
            default:
                char chr = e.getKeyChar();
                mytext = mytext+chr;
                render();
                break;
        }
    }
}

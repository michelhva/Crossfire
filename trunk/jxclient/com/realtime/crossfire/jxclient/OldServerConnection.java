package com.real-time.crossfire.jxclient;
import  com.real-time.crossfire.jxclient.*;

import java.net.*;
import java.util.*;
import java.io.*;

public class OldServerConnection
{
    private Socket                  mysocket;
    private DataInputStream         in;
    private DataOutputStream        out;
    private DataOutputStream        bout;
    private ByteArrayOutputStream   bos;
    private byte[]                  buf = null;
    private List                    mylisteners = new ArrayList();

    public synchronized boolean waitForData() throws IOException
    {
        return (in.available() > 0) ? true : false;
    }
    public synchronized void readPacket() throws IOException
    {
        int len = 0;
        int off = 0;

        len = in.readUnsignedShort();
        byte[] data = new byte[len];
        byte[] cmd;

        in.readFully(data);
        for (int i=0;i<len;i++)
        {
            if (data[i]==0x20)
            {
                cmd = new byte[i+1];
                for(int j=0;j<i;j++)
                    cmd[j]=data[j];
                break;
            }
        }
        String cmdstr = (cmd != null) ? new String(cmd) : new String(data);
        DataInputStream dis = null;
        if (i<len)
        {
            dis = new DataInputStream(new ByteArrayInputStream(data));
            dis.skipBytes(i+1);
        }
        command(cmdstr, dis);
    }
    public synchronized void writePacket(String str) throws IOException
    {
        bout.writeShort((short)str.length());
        bout.writeBytes(str);
        bos.writeTo(out);
        out.flush();
        bos.reset();
    }
    public OldServerConnection(String hostname, int port)
    {
        buf = new byte[2];
        try
        {
            mysocket = new Socket(hostname, port);
            in = new DataInputStream(mysocket.getInputStream());
            out = new DataOutputStream(mysocket.getOutputStream());
            bos = new ByteArrayOutputStream(0);
            bout = new DataOutputStream(bos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
    public synchronized void addCrossfireListener(CrossfireListener listener)
    {
        mylisteners.add(listener);
    }
    public synchronized void removeCrossfireListener(CrossfireListener listener)
    {
        mylisteners.remove(listener);
    }
    private void command(String cmd, DataInputStream dis) throws IOException
    {
    }
}
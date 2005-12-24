package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;

public class MetaserverEntry
{
    private String myip;
    private String myhost;
    private String mycomment;
    private String myversion;
    private int mynrplayers;
    private int myping;

    public MetaserverEntry(String ip, String host, String comment, String version,
                           int nrplayers, int ping)
    {
        myip = ip;
        myhost = host;
        mycomment = comment;
        myversion = version;
        mynrplayers = nrplayers;
        myping = ping;
    }
    public String toString()
    {
        String str = new String("IP:"+myip+" Host:"+myhost+" Version:"+myversion+
                " Players:"+mynrplayers+" Ping:"+myping+" Comment:"+mycomment);
        return str;
    }
    public String getIP()
    {
        return myip;
    }
    public String getHost()
    {
        return myhost;
    }
    public String getComment()
    {
        return mycomment;
    }
    public String getVersion()
    {
        return myversion;
    }
    public int getNrPlayers()
    {
        return mynrplayers;
    }
    public int getPing()
    {
        return myping;
    }
}
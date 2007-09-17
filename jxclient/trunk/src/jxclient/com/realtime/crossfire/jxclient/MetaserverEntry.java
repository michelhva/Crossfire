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

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class MetaserverEntry implements Comparable<MetaserverEntry>
{
    private String myip;

    private String myhost;

    private String mycomment;

    private String myversion;

    private int mynrplayers;

    private int myping;

    public MetaserverEntry(String ip, String host, String comment, String version, int nrplayers, int ping)
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
        return "IP:"+myip+" Host:"+myhost+" Version:"+myversion+" Players:"+mynrplayers+" Ping:"+myping+" Comment:"+mycomment;
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

    /** {@inheritDoc} */
    public int compareTo(final MetaserverEntry o)
    {
        return myhost.compareTo(o.myhost);
    }
}

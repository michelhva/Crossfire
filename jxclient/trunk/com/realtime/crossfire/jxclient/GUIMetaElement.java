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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIMetaElement extends GUIElement implements GUIScrollable
{
    private BufferedImage mypicture_tcp = null;
    private BufferedImage mypicture_udp = null;
    private Font myfont;
    private GUIText mytext;
    private GUILabel mylabel;
    private int myindex;

    public GUIMetaElement
            (String nn, int nx, int ny, int nw, int nh, String pic_tcp,
                          String pic_udp, Font nf, GUIText txt, GUILabel comment,
                          int meta_id) throws IOException
    {
        super(nn, nx, ny, nw, nh);
        mypicture_tcp =
            ImageIO.read(this.getClass().getClassLoader().getResource(pic_tcp));
        mypicture_udp =
            ImageIO.read(this.getClass().getClassLoader().getResource(pic_udp));
        myfont = nf;
        mytext = txt;
        mylabel = comment;
        myindex = meta_id;
        createBuffer();
        render();
    }
    protected void render()
    {
        List<MetaserverEntry> l = Metaserver.query();
        try
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice      gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gconf = gd.getDefaultConfiguration();
            mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);

            if ((myindex < 0)||(myindex>=l.size()))
                return;

            MetaserverEntry mentry = l.get(myindex);
            Graphics2D g = mybuffer.createGraphics();
            g.setFont(myfont);
            if (active)
                g.setColor(Color.RED);
            else
                g.setColor(Color.GRAY);
            g.drawImage(mypicture_tcp, 0, 0, null);
            g.drawString("P:"+mentry.getNrPlayers()+" L:"+mentry.getPing()+" - "
                    + mentry.getHost()+" - " + mentry.getComment(), 16,
                    myfont.getSize()+1);
            g.dispose();
            if ((mylabel != null)&&active)
                mylabel.setText(mentry.getComment());
            if ((mytext != null)&&active)
                mytext.setText(mentry.getHost());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        setChanged();
    }
    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        int b = e.getButton();
        switch(b)
        {
            case MouseEvent.BUTTON1:
                active = true;
                render();
                break;
            case MouseEvent.BUTTON2:
                break;
            case MouseEvent.BUTTON3:
                break;
        }
    }
    public void setActive(boolean act)
    {
        if (active && (!act))
            if (mylabel != null)
                mylabel.setText("");
        active = act;
        render();
    }
    public void scrollUp()
    {
            myindex--;
            render();
    }
    public void scrollDown()
    {
        myindex++;
        render();
    }
    public int getIndex()
    {
        return myindex;
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        setChanged();
    }
}

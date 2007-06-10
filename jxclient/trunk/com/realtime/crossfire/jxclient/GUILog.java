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
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUILog extends GUIElement implements CrossfireQueryListener,
    GUIScrollable, CrossfireDrawinfoListener
{
    private BufferedImage mybackground = null;
    private Font myfont;
    private java.util.List<String> mytext=new LinkedList<String>();
    private java.util.List<Color>  mytextcolor=new LinkedList<Color>();
    private int mynrlines;
    private int mylogtype;
    private int myindex;
    private int mynrchars = 40;

    public GUILog
            (String nn, int nx, int ny,int  nw,int  nh, String picture, Font nf, int nnr, int nt)
            throws IOException
    {
        if (picture != null)
            mybackground =
                javax.imageio.ImageIO.read(getClass().getClassLoader().getResourceAsStream(picture));
        else
            mybackground = null;
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myfont = nf;
        mynrlines = nnr;
        myindex = 0;
        mylogtype = nt;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        Graphics2D g = mybuffer.createGraphics();
        if (mybackground != null)
            g.drawImage(mybackground, x, y, null);
        g.dispose();
        myname = nn;
        setChanged();
    }
    public GUILog
          (String nn, int nx, int ny,int  nw,int  nh, String picture, Font nf, int nnr, int nnw, int nt)
            throws IOException
    {
        if (picture != null)
            mybackground =
                    javax.imageio.ImageIO.read(getClass().getClassLoader().getResourceAsStream(picture));
        else
            mybackground = null;
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myfont = nf;
        mynrlines = nnr;
        mynrchars = nnw;
        myindex = 0;
        mylogtype = nt;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        Graphics2D g = mybuffer.createGraphics();
        if (mybackground != null)
            g.drawImage(mybackground, x, y, null);
        g.dispose();
        myname = nn;
        setChanged();
    }
    protected void render()
    {
        try
        {
            Graphics2D g = mybuffer.createGraphics();
            g.setBackground(new Color(0,0,0,0.0f));
            g.clearRect(0,0,w,h);
            if (mybackground != null)
            {
                g.drawImage(mybackground, x, y, null);
            }
            g.setFont(myfont);

            try
            {
                for(int i=myindex; i<myindex+mynrlines;i++)
                {
                    g.setColor(mytextcolor.get(i));
                    if (mytext.get(i) != null)
                        g.drawString(mytext.get(i), 0, (myfont.getSize()+1)*(i-myindex));
                    else
                        g.drawString("", 0, (myfont.getSize()+1)*(i-myindex));
                }
            }
            catch (Exception e)
            {};
            g.dispose();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        setChanged();
    }

    public void CommandQueryReceived(CrossfireCommandQueryEvent evt)
    {
        mytext.add(evt.getPrompt());
        mytextcolor.add(Color.RED);
        myindex++;
        render();
    }
    private void addTextLine(String txt, int txttype)
    {
        mytext.add(txt);
        switch(txttype)
        {
            case 0: //black
                mytextcolor.add(Color.WHITE);
                break;
            case 1: //white
                mytextcolor.add(Color.WHITE);
                break;
            case 2: //navy blue
                mytextcolor.add(Color.BLUE);
                break;
            case 3: //red
                mytextcolor.add(Color.RED);
                break;
            case 4: //orange
                mytextcolor.add(Color.ORANGE);
                break;
            case 5: //dodger blue
                mytextcolor.add(Color.CYAN);
                break;
            case 6: //dark orange
                mytextcolor.add(Color.MAGENTA);
                break;
            case 7: //sea green
                mytextcolor.add(Color.GREEN);
                break;
            case 8: //dark sea green
                mytextcolor.add(Color.GREEN);
                break;
            case 9: //grey
                mytextcolor.add(Color.GRAY);
                break;
            case 10://brown sienna
                mytextcolor.add(Color.PINK);
                break;
            case 11://gold
                mytextcolor.add(Color.YELLOW);
                break;
            case 12://khaki
                mytextcolor.add(Color.WHITE);
                break;
            default:
                mytextcolor.add(Color.WHITE);
                break;
        }
        scrollDown();
    }

    public void CommandDrawinfoReceived(CrossfireCommandDrawinfoEvent evt)
    {
        String[] txtlines = evt.getText().split("\n");
        for(int i=0; i<txtlines.length;i++)
        {
            if (txtlines[i].length()>mynrchars)
            {
                int k=0;
                for(k=mynrchars; k<txtlines[i].length();k+=mynrchars)
                {
                    String str = txtlines[i].substring(k-mynrchars,k);
                    addTextLine(str, evt.getTextType());
                }
                String strf = txtlines[i].substring(k-mynrchars,txtlines[i].length());
                addTextLine(strf, evt.getTextType());
            }
            else
            {
                addTextLine(txtlines[i], evt.getTextType());
            }
        }
        render();
    }
    public void scrollUp()
    {
        myindex--;
        if (myindex < 0) myindex = 0;
        render();
    }
    public void scrollDown()
    {
        myindex++;
        if ((myindex + mynrlines)>= mytext.size()) myindex = mytext.size()-mynrlines;
        render();
    }
    public int getIndex()
    {
        return myindex;
    }
}

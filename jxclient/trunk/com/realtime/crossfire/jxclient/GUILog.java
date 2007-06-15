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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUILog extends GUIElement implements CrossfireQueryListener, GUIScrollable, CrossfireDrawinfoListener
{
    private final BufferedImage mybackground;

    private final Font myfont;

    private final List<String> mytext = new LinkedList<String>();

    private final List<Color> mytextcolor = new LinkedList<Color>();

    private final int mynrlines;

    private final int mylogtype;

    private int myindex;

    private final int mynrchars;

    public GUILog(final String nn, final int nx, final int ny, final int nw, final int nh, final String picture, final Font nf, final int nnr, final int nt) throws IOException
    {
        super(nn, nx, ny, nw, nh);
        if (picture != null)
            mybackground = ImageIO.read(getClass().getClassLoader().getResourceAsStream(picture));
        else
            mybackground = null;
        myfont = nf;
        mynrlines = nnr;
        mynrchars = 40;
        myindex = 0;
        mylogtype = nt;

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        final Graphics2D g = mybuffer.createGraphics();
        if (mybackground != null)
            g.drawImage(mybackground, x, y, null);
        g.dispose();
        setChanged();
    }

    public GUILog(final String nn, final int nx, final int ny, final int nw, final int nh, final String picture, final Font nf, final int nnr, final int nnw, final int nt) throws IOException
    {
        super(nn, nx, ny, nw, nh);
        if (picture != null)
            mybackground = ImageIO.read(getClass().getClassLoader().getResourceAsStream(picture));
        else
            mybackground = null;
        myfont = nf;
        mynrlines = nnr;
        mynrchars = nnw;
        myindex = 0;
        mylogtype = nt;

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        final Graphics2D g = mybuffer.createGraphics();
        if (mybackground != null)
            g.drawImage(mybackground, x, y, null);
        g.dispose();
        setChanged();
    }

    protected void render()
    {
        try
        {
            final Graphics2D g = mybuffer.createGraphics();
            g.setBackground(new Color(0, 0, 0, 0.0f));
            g.clearRect(0, 0, w, h);
            if (mybackground != null)
            {
                g.drawImage(mybackground, x, y, null);
            }
            g.setFont(myfont);

            try
            {
                for(int i = myindex; i < myindex+mynrlines; i++)
                {
                    g.setColor(mytextcolor.get(i));
                    if (mytext.get(i) != null)
                        g.drawString(mytext.get(i), 0, (myfont.getSize()+1)*(i-myindex));
                    else
                        g.drawString("", 0, (myfont.getSize()+1)*(i-myindex));
                }
            }
            catch (final Exception e)
            {};
            g.dispose();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        setChanged();
    }

    public void commandQueryReceived(final CrossfireCommandQueryEvent evt)
    {
        mytext.add(evt.getPrompt());
        mytextcolor.add(Color.RED);
        myindex++;
        render();
    }

    private void addTextLine(final String txt, final int txttype)
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

        case 10: //brown sienna
            mytextcolor.add(Color.PINK);
            break;

        case 11: //gold
            mytextcolor.add(Color.YELLOW);
            break;

        case 12: //khaki
            mytextcolor.add(Color.WHITE);
            break;

        default:
            mytextcolor.add(Color.WHITE);
            break;
        }
        scrollDown();
    }

    public void commandDrawinfoReceived(final CrossfireCommandDrawinfoEvent evt)
    {
        final String[] txtlines = evt.getText().split("\n");
        for (final String txtline : txtlines)
        {
            if (txtline.length() > mynrchars)
            {
                int k = 0;
                for (k = mynrchars; k < txtline.length(); k += mynrchars)
                {
                    final String str = txtline.substring(k-mynrchars, k);
                    addTextLine(str, evt.getTextType());
                }
                final String strf = txtline.substring(k-mynrchars, txtline.length());
                addTextLine(strf, evt.getTextType());
            }
            else
            {
                addTextLine(txtline, evt.getTextType());
            }
        }
        render();
    }

    public void scrollUp()
    {
        myindex--;
        if (myindex < 0)
            myindex = 0;
        render();
    }

    public void scrollDown()
    {
        myindex++;
        if ((myindex+mynrlines) >= mytext.size())
            myindex = mytext.size()-mynrlines;
        render();
    }

    public int getIndex()
    {
        return myindex;
    }
}

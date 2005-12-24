package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

public class GUILabel extends GUIElement implements CrossfireStatsListener,
                                                    CrossfireQueryListener,
                                                    CrossfireDrawextinfoListener
{
    private BufferedImage mybackground = null;
    private Font myfont;
    private String mycaption=new String("");
    private int mystat=0;
    private boolean stat_based = false;
    private Color mycolor = Color.WHITE;

    private void commonInit(String nn, int nx, int ny, int nw, int nh, String picture, Font nf)
            throws IOException
    {
        if (picture != null)
            mybackground = javax.imageio.ImageIO.read(new File(picture));
        else
            mybackground = null;
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myfont = nf;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
        Graphics2D g = mybuffer.createGraphics();
        if (mybackground != null)
            g.drawImage(mybackground, x, y, null);
        g.dispose();
    }

    public GUILabel
            (String nn, int nx, int ny,int  nw,int  nh, String picture, Font nf,
             Color clr, String cap)
            throws IOException
    {
        commonInit(nn,nx,ny,nw,nh,picture,nf);
        mycolor = clr;
        mycaption = cap;
        myname = nn;
        render();
    }
    public GUILabel
            (String nn, int nx, int ny,int  nw,int  nh, String picture, Font nf, String cap)
            throws IOException
    {
        commonInit(nn,nx,ny,nw,nh,picture,nf);
        mycaption = cap;
        myname = nn;
        render();
    }
    public GUILabel
            (String nn, int nx, int ny,int  nw,int  nh, String picture, Font nf, int stat)
            throws IOException
    {
        commonInit(nn,nx,ny,nw,nh,picture,nf);
        mystat = stat;
        stat_based = true;
        myname = nn;
        render();
    }
    public GUILabel
            (String nn, int nx, int ny,int  nw,int  nh, String picture, Font nf,
             Color clr, int stat)
            throws IOException
    {
        commonInit(nn,nx,ny,nw,nh,picture,nf);
        mystat = stat;
        stat_based = true;
        mycolor = clr;
        myname = nn;
        render();
    }
    public void setText(String ntxt)
    {
        mycaption = ntxt;
        render();
    }
    private void render()
    {
        try
        {
            String myfinalcaption="";
            Graphics2D g = mybuffer.createGraphics();
            g.setBackground(new Color(0,0,0,0.0f));
            g.clearRect(0,0,w,h);
            if (mybackground != null)
            {
                g.drawImage(mybackground, x, y, null);
            }
            g.setFont(myfont);
            g.setColor(mycolor);

            mycaption = mycaption.replaceAll("\n","<br>");
            Reader reader = new StringReader(mycaption);
            try
            {
                new ParserDelegator().parse(reader,
                new InternalHTMLRenderer(myfont, mycolor,g,0,myfont.getSize()),false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            g.dispose();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void CommandStatsReceived(CrossfireCommandStatsEvent evt)
    {
        Stats s = evt.getStats();
        switch (mystat)
        {
            case Stats.CS_STAT_SPEED:
            case Stats.CS_STAT_WEAP_SP:
                mycaption = String.valueOf(s.getStat(mystat)/1000)+"."+
                        String.valueOf(s.getStat(mystat)%1000);
                break;
            case Stats.CS_STAT_RANGE:
                mycaption = s.getRange();
                break;
            case Stats.CS_STAT_TITLE:
                mycaption = s.getTitle();
                break;
            case Stats.CS_STAT_EXP64:
                mycaption = String.valueOf(s.getStat(mystat));
                break;
            default:
                mycaption = String.valueOf(s.getStat(mystat));
                break;
        }
        render();
    }
    public void CommandQueryReceived(CrossfireCommandQueryEvent evt)
    {
        mycaption = evt.getPrompt();
        render();
    }
    public void CommandDrawextinfoReceived(CrossfireCommandDrawextinfoEvent evt)
    {
        mycaption = evt.getMessage();
        render();
    }
    class InternalHTMLRenderer extends HTMLEditorKit.ParserCallback
    {
        private java.util.Stack<Font> myfonts = new java.util.Stack<Font>();
        private java.util.Stack<Color> mycolors = new java.util.Stack<Color>();

        private Graphics2D mygc;
        private int myx = 0;
        private int myy = 0;
        private int myorigx = 0;
        public InternalHTMLRenderer(Font fd, Color fdc, Graphics2D g, int x, int y)
        {
            myfonts.push(fd);
            mycolors.push(fdc);
            mygc = g;
            myx = x;
            myy = y;
            myorigx = myx;
        }
        public void handleText(char[] data, int pos)
        {
            mygc.setFont(myfonts.peek());
            mygc.setColor(mycolors.peek());
            FontMetrics m = mygc.getFontMetrics();
            String str = new String(data);
            int w = m.stringWidth(str);
            mygc.drawString(str, myx, myy);
            myx+=w;
        }
        public void handleStartTag(HTML.Tag tag,
                                   MutableAttributeSet attrSet, int pos)
        {
            if (tag.equals(HTML.Tag.A))
            {
                myfonts.push(myfonts.peek());
                mycolors.push(Color.YELLOW);
                //myy += mydefaultfont.getSize()+1;
            }
            else if (tag.equals(HTML.Tag.B))
            {
                myfonts.push(myfonts.peek().deriveFont(Font.BOLD));
                mycolors.push(mycolors.peek());
            }
            else if (tag.equals(HTML.Tag.I))
            {
                myfonts.push(myfonts.peek().deriveFont(Font.ITALIC));
                mycolors.push(mycolors.peek());
            }
            else if (tag.equals(HTML.Tag.LI))
            {
                myfonts.push(myfonts.peek());
                mycolors.push(mycolors.peek());
                mygc.setFont(myfonts.peek());
                mygc.setColor(mycolors.peek());
                FontMetrics m = mygc.getFontMetrics();
                myx = myorigx;
                myy += myfonts.peek().getSize()+1;
                String str = " - ";
                int w = m.stringWidth(str);
                mygc.drawString(str, myx, myy);
                myx+=w;
            }
            else
            {
                myfonts.push(myfonts.peek());
                mycolors.push(mycolors.peek());
            }
        }
        public void handleSimpleTag(HTML.Tag tag,
                                   MutableAttributeSet attrSet, int pos)
        {
            if (tag.equals(HTML.Tag.BR))
            {
                myy += myfonts.peek().getSize()+1;
                myx = myorigx;
            }
            /*else
            {
                System.out.println("Tag:"+tag);
        }*/
        }
        public void handleEndTag(HTML.Tag tag, int pos)
        {
            myfonts.pop();
            mycolors.pop();
            //System.out.println("End Tag:"+tag);
        }
    }
}

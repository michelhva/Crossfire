/* $Id$ */

package com.realtime.crossfire.jxclient;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.util.List;

/**
 * @author Andreas Kirschbaum
 */
public class JXCWindowRenderer
{
    private final JXCWindow jxcWindow;

    private BufferStrategy mybufferstrategy;

    private boolean isfullscreen = false;

    private DisplayMode oldDisplayMode=null;

    private DisplayMode mymode = null;

    private List<GUIElement> mydialog_current = null;

    public JXCWindowRenderer(final JXCWindow jxcWindow)
    {
        this.jxcWindow = jxcWindow;
    }

    public void init(final int w, final int h, final int b, final int f)
    {
        mymode = new DisplayMode(w, h, b, f);
    }

    public void initRendering()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice      gd = ge.getDefaultScreenDevice();
        isfullscreen = gd.isFullScreenSupported();
        if(gd.isFullScreenSupported()==false)
        {
            System.out.println("Warning ! True full-screen support is not available.");
            jxcWindow.setUndecorated(true);
            jxcWindow.setIgnoreRepaint(true);
            oldDisplayMode = gd.getDisplayMode();

            jxcWindow.setSize(mymode.getWidth(),mymode.getHeight());
            jxcWindow.setVisible(true);
        }
        else
        {
            jxcWindow.setUndecorated(true);
            jxcWindow.setIgnoreRepaint(true);
            oldDisplayMode = gd.getDisplayMode();

            final DisplayMode ndm = mymode;
            gd.setFullScreenWindow(jxcWindow);
            gd.setDisplayMode(ndm);
            System.out.println("Graphic Device:"+gd.getIDstring());
            System.out.println("Accelerated memory available:"+gd.getAvailableAcceleratedMemory());
        }
        jxcWindow.createBufferStrategy(2);
        mybufferstrategy = jxcWindow.getBufferStrategy();
    }

    public void endRendering()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        if(gd.isFullScreenSupported()==true)
        {
            gd.setDisplayMode(oldDisplayMode);
            gd.setFullScreenWindow(null);
        }
    }

    public void showGUIStart()
    {
        mybufferstrategy.show();
    }

    public void redrawGUI()
    {
        final Graphics g = mybufferstrategy.getDrawGraphics();
        if (mybufferstrategy.contentsRestored())
        {
            // Surface was recreated and reset, may require redrawing.
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, jxcWindow.getWidth(), jxcWindow.getHeight());
        }
        for (final GUIElement element : jxcWindow.getGui())
        {
            if (element.isVisible())
            {
                if (element instanceof GUIMap)
                {
                    final GUIMap mel = (GUIMap)element;
                    mel.redraw(g);
                }
                else
                {
                    g.drawImage(element.getBuffer(), element.getX(), element.getY(), jxcWindow);
                }
            }
        }
        redrawGUIDialog(g);
        g.dispose();
        mybufferstrategy.show();
        if (mybufferstrategy.contentsLost())
        {
        // The surface was lost since last call to getDrawGraphics, you
        // may need to redraw.
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, jxcWindow.getWidth(), jxcWindow.getHeight());
        }
    }

    public void clearGUI()
    {
        for(int ig=0;ig<3;ig++)
        {
            final Graphics gd = mybufferstrategy.getDrawGraphics();
            gd.setColor(Color.BLACK);
            gd.fillRect(0, 0, jxcWindow.getWidth(), jxcWindow.getHeight());
            gd.dispose();
            mybufferstrategy.show();
        }
    }

    private void redrawGUIDialog(final Graphics g)
    {
        if (jxcWindow.getDialogStatus()!=JXCWindow.DLG_NONE)
        {
            for (final GUIElement element : mydialog_current)
            {
                if (element.isVisible())
                {
                    if (element instanceof GUIMap)
                    {
                        final GUIMap mel = (GUIMap)element;
                        final Graphics gg = element.getBuffer().createGraphics();
                        mel.redraw(gg);
                        gg.dispose();
                    }
                    g.drawImage(element.getBuffer(), element.getX(), element.getY(), jxcWindow);
                }
            }
        }
    }

    public void setCurrentDialog(final List<GUIElement> dialog)
    {
        mydialog_current = dialog;
    }

    public List<GUIElement> getCurrentDialog()
    {
        return mydialog_current;
    }
}

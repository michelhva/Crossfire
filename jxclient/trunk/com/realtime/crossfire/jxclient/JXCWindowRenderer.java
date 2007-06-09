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

    private BufferStrategy bufferStrategy;

    private boolean isfullscreen = false;

    private DisplayMode oldDisplayMode=null;

    private DisplayMode displayMode = null;

    private List<GUIElement> currentDialog = null;

    public JXCWindowRenderer(final JXCWindow jxcWindow)
    {
        this.jxcWindow = jxcWindow;
    }

    public void init(final int w, final int h, final int b, final int f)
    {
        displayMode = new DisplayMode(w, h, b, f);
    }

    public void initRendering()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice      gd = ge.getDefaultScreenDevice();
        isfullscreen = gd.isFullScreenSupported();
        if (gd.isFullScreenSupported() == false)
        {
            System.out.println("Warning ! True full-screen support is not available.");
            jxcWindow.setUndecorated(true);
            jxcWindow.setIgnoreRepaint(true);
            oldDisplayMode = gd.getDisplayMode();

            jxcWindow.setSize(displayMode.getWidth(), displayMode.getHeight());
            jxcWindow.setVisible(true);
        }
        else
        {
            jxcWindow.setUndecorated(true);
            jxcWindow.setIgnoreRepaint(true);
            oldDisplayMode = gd.getDisplayMode();

            final DisplayMode ndm = displayMode;
            gd.setFullScreenWindow(jxcWindow);
            gd.setDisplayMode(ndm);
            System.out.println("Graphic Device:"+gd.getIDstring());
            System.out.println("Accelerated memory available:"+gd.getAvailableAcceleratedMemory());
        }
        jxcWindow.createBufferStrategy(2);
        bufferStrategy = jxcWindow.getBufferStrategy();
    }

    public void endRendering()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isFullScreenSupported() == true)
        {
            gd.setDisplayMode(oldDisplayMode);
            gd.setFullScreenWindow(null);
        }
    }

    public void redrawGUI()
    {
        do
        {
            do
            {
                final Graphics g = bufferStrategy.getDrawGraphics();
                if (bufferStrategy.contentsRestored())
                {
                    redrawBlack(g);
                }
                redrawGUIBasic(g);
                redrawGUIDialog(g);
                g.dispose();
            }
            while (bufferStrategy.contentsLost());
            bufferStrategy.show();
        }
        while (bufferStrategy.contentsLost());
    }

    public void clearGUI()
    {
        for(int ig = 0; ig < 3; ig++)
        {
            final Graphics g = bufferStrategy.getDrawGraphics();
            redrawBlack(g);
            g.dispose();
            bufferStrategy.show();
        }
    }

    private void redrawGUIBasic(final Graphics g)
    {
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
    }

    private void redrawGUIDialog(final Graphics g)
    {
        if (jxcWindow.getDialogStatus() != JXCWindow.DLG_NONE)
        {
            for (final GUIElement element : currentDialog)
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

    private void redrawBlack(final Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, jxcWindow.getWidth(), jxcWindow.getHeight());
    }

    public void setCurrentDialog(final List<GUIElement> dialog)
    {
        currentDialog = dialog;
    }

    public List<GUIElement> getCurrentDialog()
    {
        return currentDialog;
    }
}

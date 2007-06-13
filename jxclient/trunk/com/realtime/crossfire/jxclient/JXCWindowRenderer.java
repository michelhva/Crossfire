/* $Id$ */

package com.realtime.crossfire.jxclient;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;

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

    /**
     * If set, {@link #currentDialog} has changed.
     */
    private boolean currentDialogChanged = false;

    private Gui currentDialog = null;

    /**
     * If set, {@link #currentGui} has changed.
     */
    private boolean currentGuiChanged = false;

    private Gui currentGui = new Gui();

    /**
     * If set, force a full repaint.
     */
    private boolean forcePaint = false;

    public JXCWindowRenderer(final JXCWindow jxcWindow)
    {
        this.jxcWindow = jxcWindow;
    }

    public void init(final int w, final int h, final int b, final int f)
    {
        displayMode = new DisplayMode(w, h, b, f);
    }

    /**
     * Repaint the window.
     */
    public void repaint()
    {
        forcePaint = true;
    }

    public void initRendering()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice      gd = ge.getDefaultScreenDevice();
        isfullscreen = gd.isFullScreenSupported();
        if (!gd.isFullScreenSupported())
        {
            System.out.println("Warning ! True full-screen support is not available.");
            jxcWindow.setUndecorated(true);
            oldDisplayMode = gd.getDisplayMode();

            jxcWindow.setSize(displayMode.getWidth(), displayMode.getHeight());
            jxcWindow.setVisible(true);
        }
        else
        {
            jxcWindow.setUndecorated(true);
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
        if (gd.isFullScreenSupported())
        {
            gd.setDisplayMode(oldDisplayMode);
            gd.setFullScreenWindow(null);
        }
    }

    public void redrawGUI()
    {
        if (forcePaint)
        {
            forcePaint = false;
        }
        else if (!needRedraw())
        {
            return;
        }

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
        currentGui.clear();
        currentGuiChanged = true;
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
        currentGuiChanged = false;
        currentGui.redraw(g, jxcWindow);
    }

    private void redrawGUIDialog(final Graphics g)
    {
        currentDialogChanged = false;
        if (currentDialog != null)
        {
            currentDialog.redrawElements(g, jxcWindow);
        }
    }

    private void redrawBlack(final Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, jxcWindow.getWidth(), jxcWindow.getHeight());
    }

    public void setCurrentDialog(final Gui dialog)
    {
        currentDialog = dialog;
        currentDialogChanged = true;
    }

    public Gui getCurrentDialog()
    {
        return currentDialog;
    }

    public void setCurrentGui(final Gui gui)
    {
        currentGui = gui;
        currentGuiChanged = true;
    }

    public Gui getCurrentGui()
    {
        return currentGui;
    }

    /**
     * Check whether any gui element has changed and needs a redraw.
     *
     * @return whether any gui element has changed
     */
    private boolean needRedraw()
    {
        if (currentDialogChanged)
        {
            return true;
        }

        if (currentGuiChanged)
        {
            return true;
        }

        if (currentGui.needRedraw())
        {
                return true;
        }

        if (currentDialog != null && currentDialog.needRedraw())
        {
            return true;
        }

        return false;
    }
}

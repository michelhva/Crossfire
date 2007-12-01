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

import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.GUILabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.Insets;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * @author Andreas Kirschbaum
 */
public class JXCWindowRenderer
{
    private final JXCWindow jxcWindow;

    private BufferStrategy bufferStrategy;

    private DisplayMode oldDisplayMode=null;

    private DisplayMode displayMode = null;

    /**
     * If set, the content of {@link #openDialogs} has changed.
     */
    private boolean openDialogsChanged = false;

    /**
     * Currently opened dialogs. The ordering is the painting order: the
     * topmost dialog is at the end.
     */
    private CopyOnWriteArrayList<Gui> openDialogs = new CopyOnWriteArrayList<Gui>();

    /**
     * If set, {@link #currentGui} has changed.
     */
    private boolean currentGuiChanged = false;

    private Gui currentGui = new Gui();

    /**
     * The tooltip to use, or <code>null</code> if no tooltips should be shown.
     */
    private GUILabel tooltip = null;

    /**
     * If set, force a full repaint.
     */
    private boolean forcePaint = false;

    /**
     * The x-offset of of the visible window.
     */
    private int offsetX = 0;

    /**
     * The y-offset of of the visible window.
     */
    private int offsetY = 0;

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

    public void initRendering(boolean fullScreen)
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (!fullScreen || !gd.isFullScreenSupported())
        {
            if (fullScreen)
            {
                System.out.println("Warning ! True full-screen support is not available.");
            }
            oldDisplayMode = gd.getDisplayMode();

            final Dimension size = new Dimension(displayMode.getWidth(), displayMode.getHeight());
            jxcWindow.getRootPane().setPreferredSize(size);
            jxcWindow.pack();
            jxcWindow.setResizable(false);
            jxcWindow.setVisible(true);
            jxcWindow.setLocationRelativeTo(null);
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

        final Insets insets = jxcWindow.getInsets();
        offsetX = insets.left;
        offsetY = insets.top;
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
                g.translate(offsetX, offsetY);
                if (bufferStrategy.contentsRestored())
                {
                    redrawBlack(g);
                }
                redrawGUIBasic(g);
                redrawGUIDialog(g);
                redrawTooltip(g);
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
        for (int ig = 0; ig < 3; ig++)
        {
            final Graphics g = bufferStrategy.getDrawGraphics();
            g.translate(offsetX, offsetY);
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
        openDialogsChanged = false;
        for (final Gui dialog : openDialogs)
        {
            dialog.redraw(g, jxcWindow);
        }
    }

    private void redrawTooltip(final Graphics g)
    {
        if (tooltip != null && tooltip.isVisible())
        {
            final BufferedImage bufferedImage = tooltip.getBuffer();
            synchronized(bufferedImage)
            {
                g.drawImage(bufferedImage, tooltip.getX(), tooltip.getY(), jxcWindow);
                tooltip.resetChanged();
            }
        }
    }

    private void redrawBlack(final Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, jxcWindow.getWindowWidth(), jxcWindow.getWindowHeight());
    }

    /**
     * Open a dialog. Raises an already opened dialog.
     *
     * @param dialog The dialog to show.
     */
    public void openDialog(final Gui dialog)
    {
        if (openDialogs.size() > 0 && openDialogs.get(openDialogs.size()-1) == dialog)
        {
            return;
        }

        openDialogs.remove(dialog);
        openDialogs.add(dialog);
        openDialogsChanged = true;
    }

    /**
     * Return all open dialogs in reverse painting order; the first element is
     * the top-most dialog.
     *
     * @return The open dialogs; client code must not modify this list.
     */
    public Iterable<Gui> getOpenDialogs()
    {
        return new Iterable<Gui>()
        {
            /** {@inheritDoc} */
            public Iterator<Gui> iterator()
            {
                return new Iterator<Gui>()
                {
                    /**
                     * The backing list iterator; it returns the elements in
                     * reversed order.
                     */
                    private final ListIterator<Gui> it = openDialogs.listIterator(openDialogs.size());

                    /** {@inheritDoc} */
                    public boolean hasNext()
                    {
                        return it.hasPrevious();
                    }

                    /** {@inheritDoc} */
                    public Gui next()
                    {
                        return it.previous();
                    }

                    /** {@inheritDoc} */
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
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
        if (openDialogsChanged)
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

        for (final Gui dialog : openDialogs)
        {
            if (dialog.needRedraw())
            {
                return true;
            }
        }

        return tooltip != null && tooltip.isVisible() && tooltip.hasChanged();
    }

    /**
     * Enable or disable hidden text in the first input field of the top-most
     * dialog.
     *
     * @param hideInput If set, hide input; else show input.
     */
    public void setHideInput(final boolean hideInput)
    {
        if (openDialogs.size() > 0)
        {
            openDialogs.get(openDialogs.size()-1).getFirstTextArea().setHideInput(hideInput);
        }
    }

    /**
     * Return the x-offset of of the visible window.
     *
     * @return The x-offset of of the visible window.
     */
    public int getOffsetX()
    {
        return offsetX;
    }

    /**
     * Return the y-offset of of the visible window.
     *
     * @return The y-offset of of the visible window.
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    /**
     * Close a dialog. Does nothing if the given dialog is not open.
     *
     * @param dialog The dialog to close.
     */
    public void closeDialog(final Gui dialog)
    {
        if (openDialogs.remove(dialog))
        {
            openDialogsChanged = true;
        }
    }

    /**
     * Toggle a dialog: if the dialog is not shown, show it; else hide it.
     *
     * @param dialog The dialog to toggle.
     *
     * @return Whether the dialog is shown.
     */
    public boolean toggleDialog(final Gui dialog)
    {
        openDialogsChanged = true;

        if (openDialogs.remove(dialog))
        {
            return false;
        }

        openDialogs.add(dialog);
        return true;
    }

    /**
     * Set the tooltip to use, or <code>null</code> if no tooltips should be
     * shown.
     *
     * @param tooltip The tooltip to use, or <code>null</code>.
     */
    public void setTooltip(final GUILabel tooltip)
    {
        this.tooltip = tooltip;
    }

    /**
     * Return the tooltip to use, or <code>null</code> if no tooltips should be
     * shown.
     *
     * @return The tooltip, or <code>null</code>.
     */
    public GUILabel getTooltip()
    {
        return tooltip;
    }
}

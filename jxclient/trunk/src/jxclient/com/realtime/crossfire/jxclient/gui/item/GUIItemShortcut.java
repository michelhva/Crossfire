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

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.shortcuts.Shortcut;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutCommand;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutListener;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutSpell;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutsListener;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GUIItemShortcut extends GUIItem
{
    /**
     * The background color of this item.
     */
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0.0f);

    private final Shortcuts shortcuts;

    /**
     * The {@link FacesManager} instance for looking up faces.
     */
    private final FacesManager facesManager;
    
    private final BufferedImage cursedImage;

    private final BufferedImage appliedImage;

    private final Font font;

    private final int index;

    private final ShortcutsListener shortcutsListener = new ShortcutsListener()
    {
        /** {@inheritDoc} */
        public void shortcutAdded(final int index, final Shortcut shortcut)
        {
            if (index == GUIItemShortcut.this.index)
            {
                shortcut.addShortcutListener(shortcutListener);
                setChanged();
            }
        }

        /** {@inheritDoc} */
        public void shortcutRemoved(final int index, final Shortcut shortcut)
        {
            if (index == GUIItemShortcut.this.index)
            {
                shortcut.removeShortcutListener(shortcutListener);
                setChanged();
            }
        }
    };

    private final ShortcutListener shortcutListener = new ShortcutListener()
    {
        /** {@inheritDoc} */
        public void shortcutModified()
        {
            setChanged();
        }
    };

    public GUIItemShortcut(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final BufferedImage cursedImage, final BufferedImage appliedImage, final int index, final FacesManager facesManager, final Shortcuts shortcuts, final Font font)
    {
        super(window, name, x, y, w, h);
        this.shortcuts = shortcuts;
        this.facesManager = facesManager;
        this.cursedImage = cursedImage;
        this.appliedImage = appliedImage;
        this.font = font;
        this.index = index;
        shortcuts.addShortcutsListener(shortcutsListener);
    }

    /* {@inheritDoc} */
    @Override public void button1Clicked(final JXCWindow window)
    {
        final Shortcut shortcut = shortcuts.getShortcut(index);
        if (shortcut != null)
        {
            shortcut.execute();
        }
    }

    /* {@inheritDoc} */
    @Override public void button2Clicked(final JXCWindow window)
    {
        final Shortcut shortcut = shortcuts.getShortcut(index);
        if (shortcut != null)
        {
            if (shortcut instanceof ShortcutSpell)
            {
                final ShortcutSpell shortcutSpell = (ShortcutSpell)shortcut;
                shortcutSpell.setCast(!shortcutSpell.isCast());
            }
        }
    }

    /* {@inheritDoc} */
    @Override public void button3Clicked(final JXCWindow window)
    {
        final Spell spell = window.getCurrentSpellManager().getCurrentSpell();
        if (spell == null)
        {
           return;
        }

        shortcuts.setSpellShortcut(index, spell, true);
    }

    /* {@inheritDoc} */
    @Override public void paintComponent(final Graphics g)
    {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(BACKGROUND_COLOR);
        g.clearRect(0, 0, w, h);

        final Shortcut shortcut = shortcuts.getShortcut(index);
        if (shortcut == null)
        {
            return;
        }

        if (shortcut instanceof ShortcutSpell)
        {
            final ShortcutSpell shortcutSpell = (ShortcutSpell)shortcut;
            g.drawImage(facesManager.getOriginalImageIcon(shortcutSpell.getSpell().getFaceNum()).getImage(), 0, 0, null);
            g.drawImage(shortcutSpell.isCast() ? cursedImage : appliedImage, 0, 0, null);
        }
        else if (shortcut instanceof ShortcutCommand)
        {
            // XXX: todo
        }
        else
        {
            throw new AssertionError();
        }
        g.setFont(font);
        g.setColor(Color.YELLOW);
        g.drawString("F"+(index+1), 1, 1+font.getSize()); // XXX: define in skin
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance)
    {
        return false;
    }

    /** {@inheritDoc} */
    public void scroll(final int distance)
    {
    }

    /** {@inheritDoc} */
    public void resetScroll()
    {
    }
}

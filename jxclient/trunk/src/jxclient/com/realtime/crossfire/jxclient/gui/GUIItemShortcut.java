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

package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.shortcuts.Shortcut;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutCommand;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutListener;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutsListener;
import com.realtime.crossfire.jxclient.shortcuts.ShortcutSpell;
import com.realtime.crossfire.jxclient.spells.Spell;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GUIItemShortcut extends GUIItem
{
    private final int index;

    private final Font font;

    private final ShortcutsListener shortcutsListener = new ShortcutsListener()
    {
        /** {@inheritDoc} */
        public void shortcutAdded(final int index, final Shortcut shortcut)
        {
            if (index == GUIItemShortcut.this.index)
            {
                shortcut.addShortcutListener(shortcutListener);
                render();
            }
        }

        /** {@inheritDoc} */
        public void shortcutRemoved(final int index, final Shortcut shortcut)
        {
            if (index == GUIItemShortcut.this.index)
            {
                shortcut.removeShortcutListener(shortcutListener);
                render();
            }
        }
    };

    private final ShortcutListener shortcutListener = new ShortcutListener()
    {
        /** {@inheritDoc} */
        public void shortcutModified(final Shortcut shortcut)
        {
            render();
        }
    };

    public GUIItemShortcut(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage image, final BufferedImage imageCursed, final BufferedImage imageApplied, final BufferedImage imageSelector, final BufferedImage imageLocked, final int index, final CrossfireServerConnection crossfireServerConnection, final Font font)
    {
        super(jxcWindow, name, x, y, w, h, image, imageCursed, imageApplied, imageSelector, imageLocked, crossfireServerConnection, font);
        this.index = index;
        this.font = font;
        jxcWindow.getShortcuts().addShortcutsListener(shortcutsListener);
        render();
    }

    /* {@inheritDoc} */
    @Override protected void button1Clicked(final JXCWindow jxcWindow)
    {
        final Shortcut shortcut = getJXCWindow().getShortcuts().getShortcut(index);
        if (shortcut != null)
        {
            shortcut.execute();
        }
    }

    /* {@inheritDoc} */
    @Override protected void button2Clicked(final JXCWindow jxcWindow)
    {
        final Shortcut shortcut = getJXCWindow().getShortcuts().getShortcut(index);
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
    @Override protected void button3Clicked(final JXCWindow jxcWindow)
    {
        final Spell spell = jxcWindow.getCurrentSpellManager().getCurrentSpell();
        if (spell == null)
        {
           return;
        }

        getJXCWindow().getShortcuts().setSpellShortcut(index, spell, true);
    }

    /* {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        final Shortcut shortcut = getJXCWindow().getShortcuts().getShortcut(index);
        if (shortcut == null)
        {
            return;
        }

        if (shortcut instanceof ShortcutSpell)
        {
            final ShortcutSpell shortcutSpell = (ShortcutSpell)shortcut;
            g.drawImage(shortcutSpell.getSpell().getImageIcon().getImage(), 0, 0, null);
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

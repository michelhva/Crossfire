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

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.spells.SpellsManagerListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class GUIItemSpelllist extends GUIItem
{
    /**
     * The command queue for sending commands.
     */
    private final CommandQueue commandQueue;

    /**
     * The instance for looking up faces.
     */
    private final FacesManager facesManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The {@link SpellsManager} instance to watch.
     */
    private final SpellsManager spellsManager;

    private Spell spell = null;

    private int index = -1;

    /**
     * The {@link SpellsManagerListener} used to detect spell changes.
     */
    private final SpellsManagerListener spellsManagerListener = new SpellsManagerListener()
    {
        /** {@inheritDoc} */
        public void spellAdded(final Spell spell, final int index)
        {
            if (GUIItemSpelllist.this.index >= index)
            {
                setSpell();
            }
        }

        /** {@inheritDoc} */
        public void spellRemoved(final Spell spell, final int index)
        {
            if (GUIItemSpelllist.this.index >= index)
            {
                setSpell();
            }
        }

        /** {@inheritDoc} */
        public void spellModified(final Spell spell, final int index)
        {
            if (GUIItemSpelllist.this.index == index)
            {
                setSpell();
            }
        }
    };

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    private final FacesManagerListener facesManagerListener = new FacesManagerListener()
    {
        /** {@inheritDoc} */
        public void faceUpdated(final Face face)
        {
            if (spell != null && spell.getFaceNum() == face.getFaceNum())
            {
                setChanged();
            }
        }
    };

    public GUIItemSpelllist(final JXCWindow window, final CommandQueue commandQueue, final String name, final int x, final int y, final int w, final int h, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final int defaultIndex, final FacesManager facesManager, final SpellsManager spellsManager, final Font font)
    {
        super(window, name, x, y, w, h, cursedImage, appliedImage, selectorImage, lockedImage, font);
        this.commandQueue = commandQueue;
        this.facesManager = facesManager;
        this.defaultIndex = defaultIndex;
        this.spellsManager = spellsManager;
        setIndex(defaultIndex);
        spellsManager.addCrossfireSpellChangedListener(spellsManagerListener);
        facesManager.addFacesManagerListener(facesManagerListener);
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            return index >= -distance;
        }
        else if (distance > 0)
        {
            final List<Spell> list = spellsManager.getSpellList();
            return index+distance < list.size();
        }
        else
        {
            return false;
        }
    }

    /* {@inheritDoc} */
    public void scroll(final int distance)
    {
        setIndex(index+distance);
        setChanged();
    }

    /* {@inheritDoc} */
    public void resetScroll()
    {
        setIndex(defaultIndex);
    }

    /* {@inheritDoc} */
    @Override public void button1Clicked(final JXCWindow window)
    {
        if (spell == null)
        {
            return;
        }

        commandQueue.sendNcom(false, "cast "+spell.getInternalName());
        window.getCurrentSpellManager().setCurrentSpell(spell);
    }

    /* {@inheritDoc} */
    @Override public void button2Clicked(final JXCWindow window)
    {
    }

    /* {@inheritDoc} */
    @Override public void button3Clicked(final JXCWindow window)
    {
    }

    /* {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        if (spell == null)
        {
            return;
        }

        g.drawImage(facesManager.getOriginalImageIcon(spell.getFaceNum()).getImage(), 0, 0, null);
        if (isActive())
        {
            g.drawImage(selectorImage, 0, 0, null);
        }
    }

    private void setSpell()
    {
        final List<Spell> list = spellsManager.getSpellList();
        final Spell newSpell = 0 <= index && index < list.size() ? list.get(index) : null;

        if (spell == newSpell)
        {
            return;
        }

        spell = newSpell;
        setChanged();

        setTooltipText(newSpell == null ? null : newSpell.getTooltipText());
    }

    private void setIndex(final int index)
    {
        if (this.index == index)
        {
            return;
        }
        this.index = index;

        setSpell();
    }
}

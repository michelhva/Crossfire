/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */


package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellListener;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.spells.SpellsManagerListener;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GUIItemSpelllist extends GUIItem
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The background color of this item.
     */
    @NotNull
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0.0f);

    /**
     * The command queue for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The instance for looking up faces.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The {@link SpellsManager} instance to watch.
     */
    @NotNull
    private final SpellsManager spellsManager;

    @Nullable
    private final Color selectorColor;

    @Nullable
    private final Image selectorImage;

    @NotNull
    private final CurrentSpellManager currentSpellManager;

    /**
     * The spelllist's width in pixel.
     */
    private final int w;

    /**
     * The spelllist's height in pixel.
     */
    private final int h;

    @Nullable
    private Spell spell = null;

    private int index = -1;

    /**
     * The {@link SpellsManagerListener} used to detect spell changes.
     */
    @NotNull
    private final SpellsManagerListener spellsManagerListener = new SpellsManagerListener()
    {
        /** {@inheritDoc} */
        @Override
        public void spellAdded(@NotNull final Spell spell, final int index)
        {
            if (GUIItemSpelllist.this.index >= index)
            {
                setSpell();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void spellRemoved(@NotNull final Spell spell, final int index)
        {
            if (GUIItemSpelllist.this.index >= index)
            {
                setSpell();
            }
        }
    };

    /**
     * The {@link SpellListener} attached to {@link #spell}.
     */
    @NotNull
    private final SpellListener spellListener = new SpellListener()
    {
        /** {@inheritDoc} */
        @Override
        public void spellChanged()
        {
            setSpell();
        }
    };

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener()
    {
        /** {@inheritDoc} */
        @Override
        public void faceUpdated(@NotNull final Face face)
        {
            if (spell != null && spell.getFaceNum() == face.getFaceNum())
            {
                setChanged();
            }
        }
    };

    public GUIItemSpelllist(@NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final CommandQueue commandQueue, @NotNull final String name, final int x, final int y, final int w, final int h, @Nullable final Color selectorColor, @Nullable final Image selectorImage, final int defaultIndex, @NotNull final FacesManager facesManager, @NotNull final SpellsManager spellsManager, @NotNull final CurrentSpellManager currentSpellManager)
    {
        super(tooltipManager, windowRenderer, name, x, y, w, h);
        this.commandQueue = commandQueue;
        this.facesManager = facesManager;
        this.defaultIndex = defaultIndex;
        this.spellsManager = spellsManager;
        this.selectorColor = selectorColor;
        this.selectorImage = selectorImage;
        this.currentSpellManager = currentSpellManager;
        setIndex(defaultIndex);
        this.spellsManager.addCrossfireSpellChangedListener(spellsManagerListener);
        this.facesManager.addFacesManagerListener(facesManagerListener);
        this.w = w;
        this.h = h;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
        spellsManager.removeCrossfireSpellChangedListener(spellsManagerListener);
        facesManager.removeFacesManagerListener(facesManagerListener);
        if (spell != null)
        {
            spell.removeSpellListener(spellListener);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            return index >= -distance;
        }
        else if (distance > 0)
        {
            final Collection<Spell> list = spellsManager.getSpellList();
            return index+distance < list.size();
        }
        else
        {
            return false;
        }
    }

    /* {@inheritDoc} */
    @Override
    public void scroll(final int distance)
    {
        setIndex(index+distance);
        setChanged();
    }

    /* {@inheritDoc} */
    @Override
    public void resetScroll()
    {
        setIndex(defaultIndex);
    }

    /* {@inheritDoc} */
    @Override
    public void button1Clicked(final int modifiers)
    {
        if (spell == null)
        {
            return;
        }

        commandQueue.sendNcom(false, "cast "+spell.getTag());
        currentSpellManager.setCurrentSpell(spell);
    }

    /* {@inheritDoc} */
    @Override
    public void button2Clicked(final int modifiers)
    {
    }

    /* {@inheritDoc} */
    @Override
    public void button3Clicked(final int modifiers)
    {
    }

    /* {@inheritDoc} */
    @Override
    protected void render(@NotNull final Graphics g)
    {
        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(BACKGROUND_COLOR);
        g.clearRect(0, 0, getWidth(), getHeight());

        if (spell == null)
        {
            return;
        }

        if (isActive() && selectorColor != null)
        {
            g.setColor(selectorColor);
            g.fillRect(0, 0, w, h);
        }
        g.drawImage(facesManager.getOriginalImageIcon(spell.getFaceNum()).getImage(), 0, 0, null);
        if (isActive() && selectorImage != null)
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

        if (spell != null)
        {
            spell.removeSpellListener(spellListener);
        }

        spell = newSpell;

        if (spell != null)
        {
            spell.addSpellListener(spellListener);
        }

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

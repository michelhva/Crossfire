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
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellListener;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.spells.SpellsManagerListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GUIItemSpellList extends GUIItem {

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
     * The {@link CommandQueue} for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The {@link FacesManager} for looking up faces.
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

    @Nullable
    private Spell spell = null;

    private int index = -1;

    /**
     * The {@link SpellsManagerListener} used to detect spell changes.
     */
    @NotNull
    private final SpellsManagerListener spellsManagerListener = new SpellsManagerListener() {

        @Override
        public void spellAdded(@NotNull final Spell spell, final int index) {
            if (GUIItemSpellList.this.index >= index) {
                setSpell();
            }
        }

        @Override
        public void spellRemoved(@NotNull final Spell spell, final int index) {
            if (GUIItemSpellList.this.index >= index) {
                setSpell();
            }
        }

    };

    /**
     * The {@link SpellListener} attached to {@link #spell}.
     */
    @NotNull
    private final SpellListener spellListener = new SpellListener() {

        @Override
        public void spellChanged() {
            setSpell();
        }

    };

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener() {

        @Override
        public void faceUpdated(@NotNull final Face face) {
            if (spell != null && spell.getFaceNum() == face.getFaceNum()) {
                setChanged();
            }
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandQueue the command queue for sending commands
     * @param name the name of this element
     * @param defaultIndex the default scroll index
     * @param facesManager the faces manager for looking up faces
     * @param spellsManager the spells manager instance to watch
     */
    public GUIItemSpellList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final String name, @Nullable final Color selectorColor, @Nullable final Image selectorImage, final int defaultIndex, @NotNull final FacesManager facesManager, @NotNull final SpellsManager spellsManager, @NotNull final CurrentSpellManager currentSpellManager) {
        super(tooltipManager, elementListener, name);
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        spellsManager.removeCrossfireSpellChangedListener(spellsManagerListener);
        facesManager.removeFacesManagerListener(facesManagerListener);
        if (spell != null) {
            spell.removeSpellListener(spellListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScroll(final int distance) {
        if (distance < 0) {
            return index >= -distance;
        } else if (distance > 0) {
            return index+distance < spellsManager.getSpells();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scroll(final int distance) {
        setIndex(index+distance);
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetScroll() {
        setIndex(defaultIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button1Clicked(final int modifiers) {
        if (spell == null) {
            return;
        }

        commandQueue.sendNcom(false, "cast "+spell.getTag());
        currentSpellManager.setCurrentSpell(spell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button2Clicked(final int modifiers) {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button3Clicked(final int modifiers) {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);

        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (spell == null) {
            return;
        }

        if (isActive() && selectorColor != null) {
            g.setColor(selectorColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        assert spell != null;
        g.drawImage(facesManager.getOriginalImageIcon(spell.getFaceNum()).getImage(), 0, 0, null);
        if (isActive() && selectorImage != null) {
            g.drawImage(selectorImage, 0, 0, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getMinimumSize() {
        return getMinimumSizeInt();
    }

    /**
     * Returns the minimal size to display this component.
     * @return the minimal size
     */
    @NotNull
    private static Dimension getMinimumSizeInt() {
        return new Dimension(32, 32);
    }

    private void setSpell() {
        final Spell newSpell = spellsManager.getSpell(index);
        if (spell == newSpell) {
            return;
        }

        if (spell != null) {
            spell.removeSpellListener(spellListener);
        }

        spell = newSpell;

        if (spell != null) {
            spell.addSpellListener(spellListener);
        }

        setChanged();

        setTooltipText(newSpell == null ? null : newSpell.getTooltipText());
    }

    private void setIndex(final int index) {
        if (this.index == index) {
            return;
        }
        this.index = index;

        setSpell();
    }

}

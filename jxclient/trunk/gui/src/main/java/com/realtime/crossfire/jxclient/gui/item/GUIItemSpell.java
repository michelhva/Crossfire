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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.list.GUISpellList;
import com.realtime.crossfire.jxclient.gui.misc.Modifiers;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellListener;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.spells.SpellsManagerListener;
import java.awt.Dimension;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIItemItem} that represents an entry in a {@link GUISpellList}.
 * @author Andreas Kirschbaum
 */
public class GUIItemSpell extends GUIItemItem {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The object used for synchronization on {@link #index}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The command queue for sending commands.
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

    /**
     * The {@link CurrentSpellManager} to update when a spell is selected.
     */
    @NotNull
    private final CurrentSpellManager currentSpellManager;

    /**
     * The currently selected spell or <code>null</code> if none is selected. It
     * has {@link #spellListener} attached. Corresponds to index {@link
     * #index}.
     */
    @Nullable
    private Spell spell = null;

    /**
     * The currently selected spell or <code>-1</code> if none is selected.
     * Corresponds to {@link #spell}.
     */
    private int index = -1;

    /**
     * Whether this element is selected in its {@link GUISpellList}.
     */
    private boolean selected;

    /**
     * The spells view to use.
     */
    @NotNull
    private final ItemView spellsView;

    /**
     * The {@link SpellsManagerListener} used to detect spell changes.
     */
    @NotNull
    private final SpellsManagerListener spellsManagerListener = new SpellsManagerListener() {

        @Override
        public void spellAdded(final int index) {
            if (GUIItemSpell.this.index >= index) {
                setSpell();
            }
        }

        @Override
        public void spellRemoved(final int index) {
            if (GUIItemSpell.this.index >= index) {
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
     * @param itemPainter the item painter for painting the icon
     * @param defaultIndex the default scroll index
     * @param facesManager the faces manager for looking up faces
     * @param spellsManager the spells manager instance to watch
     * @param currentSpellManager the current spell manager to update when a
     * spell is selected
     * @param spellsView the spells view to use
     */
    public GUIItemSpell(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final String name, @NotNull final ItemPainter itemPainter, final int defaultIndex, @NotNull final FacesManager facesManager, @NotNull final SpellsManager spellsManager, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final ItemView spellsView) {
        super(tooltipManager, elementListener, name, itemPainter, facesManager);
        this.commandQueue = commandQueue;
        this.facesManager = facesManager;
        this.defaultIndex = defaultIndex;
        this.spellsManager = spellsManager;
        this.currentSpellManager = currentSpellManager;
        setIndex(defaultIndex);
        this.spellsManager.addCrossfireSpellChangedListener(spellsManagerListener);
        this.facesManager.addFacesManagerListener(facesManagerListener);
        this.spellsView = spellsView;
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

        switch (modifiers&Modifiers.MASK) {
        case Modifiers.NONE:
            commandQueue.sendNcom(false, "cast "+spell.getName());
            currentSpellManager.setCurrentSpell(spell);
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button2Clicked(final int modifiers) {
        if (spell == null) {
            return;
        }

        switch (modifiers&Modifiers.MASK) {
        case Modifiers.NONE:
            commandQueue.sendNcom(false, "invoke "+spell.getName());
            currentSpellManager.setCurrentSpell(spell);
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button3Clicked(final int modifiers) {
        if (spell == null) {
            return;
        }

        switch (modifiers&Modifiers.MASK) {
        case Modifiers.SHIFT:
            commandQueue.sendNcom(false, "invoke "+spell.getName());
            currentSpellManager.setCurrentSpell(spell);
            break;
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

    /**
     * Sets the currently selected {@link #spell}. Attaches or detaches {@link
     * #spellListener} as needed.
     */
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

    /**
     * Sets the {@link #index} of the currently selected {@link #spell}. Updates
     * the currently selected spell.
     * @param index the index to set
     */
    private void setIndex(final int index) {
        if (this.index == index) {
            return;
        }
        this.index = index;

        setSpell();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected Image getFace(@NotNull final CfItem item) {
        return facesManager.getOriginalImageIcon(item.getFace().getFaceNum(), null).getImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelected(final boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSelected() {
        return selected || isActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        synchronized (sync) {
            return index;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIndexNoListeners(final int index) {
        synchronized (sync) {
            this.index = index;
        }

        setItemNoListeners(spellsView.getItem(this.index));
    }
}

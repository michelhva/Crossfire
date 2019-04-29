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

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemSpell;
import com.realtime.crossfire.jxclient.gui.item.ItemPainter;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBinding;
import com.realtime.crossfire.jxclient.gui.keybindings.KeybindingsManager;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIList} to display spells.
 * @author Nicolas Weeger
 */
public class GUISpellList extends GUIItemList<GUIItemSpell> {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link TooltipManager} to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to notify.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * The base name for created elements.
     */
    @NotNull
    private final String name;

    /**
     * The {@link ItemView} to use.
     */
    @NotNull
    private final ItemView itemView;

    /**
     * The spells to display.
     */
    @NotNull
    private final SpellsManager spellsManager;

    /**
     * The keybinding for displaying shortcuts.
     */
    @NotNull
    private final KeybindingsManager keybindingsManager;

    /**
     * The {@link CommandQueue} for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The {@link ItemPainter} for painting the icon.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The {@link FacesManager} to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The {@link CurrentSpellManager} to update when a spell is selected.
     */
    @NotNull
    private final CurrentSpellManager currentSpellManager;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of cells
     * @param cellHeight the height of cells
     * @param itemView the item view to monitor
     * @param currentItem the label to update with information about the
     * selected item.
     * @param spellsManager the spells to display
     * @param keybindingsManager the bindings for displaying shortcuts
     * @param commandQueue the command queue for sending commands
     * @param itemPainter the item painter for painting the icon
     * @param facesManager the faces manager to use
     * @param currentSpellManager the current spell manager to update when a
     * spell is selected
     */
    public GUISpellList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final SpellsManager spellsManager, @NotNull final KeybindingsManager keybindingsManager, @NotNull final CommandQueue commandQueue, @NotNull final ItemPainter itemPainter, @NotNull final FacesManager facesManager, @NotNull final CurrentSpellManager currentSpellManager) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, itemView, currentItem, new GUIItemSpell(tooltipManager, elementListener, commandQueue, name+"_template", itemPainter, -1, facesManager, spellsManager, currentSpellManager, itemView, cellHeight));
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.itemView = itemView;
        this.spellsManager = spellsManager;
        this.keybindingsManager = keybindingsManager;
        this.commandQueue = commandQueue;
        this.itemPainter = itemPainter;
        this.facesManager = facesManager;
        this.currentSpellManager = currentSpellManager;
    }

    /**
     * Return a text with the keybindings for the spell.
     * @param spell what to search bindings for
     * @param prefix invocation prefix, "cast " or "invoke ",  to search for
     * @param legend the text before the binding(s)
     * @return empty string if no matching bindings, else text in the form
     * "legend (binding 1) ; (binding 2) ; ..."
     */
    @NotNull
    private String getBindings(@NotNull final Spell spell, @NotNull final String prefix, @NotNull final String legend) {
        final String search = prefix+spell.getName().charAt(0);
        final String match = prefix+spell.getName();

        // because key bindings can specify partial names, we search in 2 steps:
        // - search all bindings with the first spell letter
        // - from those bindings only keep the ones the spell's command matches
        final Iterable<KeyBinding> bindings = keybindingsManager.getBindingsForPartialCommand(search, true);
        boolean first = true;
        final StringBuilder sb = new StringBuilder();

        for (final KeyBinding binding : bindings) {
            if (match.startsWith(binding.getCommandString())) {
                if (first) {
                    sb.append(legend);
                    first = false;
                } else {
                    sb.append(" ; ");
                }

                sb.append(binding.getBindingDescription());
            }
        }

        return sb.toString();
    }

    @Override
    protected void updateTooltip(final int index, final int x, final int y, final int w, final int h) {
        final Spell spell = spellsManager.getSpell(index);
        if (spell == null) {
            setTooltipText(null, x, y, w, h);
            return;
        }

        //noinspection StringBufferReplaceableByString
        final StringBuilder sb = new StringBuilder(spell.getTooltipText());

        // find bindings to cast or invoke the spell
        sb.append(getBindings(spell, "cast ", "<br>Cast shortcut: "));
        sb.append(getBindings(spell, "invoke ", "<br>Invoke shortcut:"));

        setTooltipText(sb.toString(), x, y, w, h);
    }

    @NotNull
    @Override
    protected GUIItemSpell newItem(final int index) {
        return new GUIItemSpell(tooltipManager, elementListener, commandQueue, name+index, itemPainter, index, facesManager, spellsManager, currentSpellManager, itemView, 0);
    }

}

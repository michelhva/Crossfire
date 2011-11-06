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

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItemFactory;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBinding;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.window.KeybindingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIList} to display spells.
 * @author Nicolas Weeger
 */
public class GUISpellList extends GUIItemList {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

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
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of cells
     * @param cellHeight the height of cells
     * @param itemView the item view to monitor
     * @param currentItem the label to update with information about the
     * selected item.
     * @param itemItemFactory the factory for creating item instances
     * @param spellsManager the spells to display
     * @param keybindingsManager the bindings for displaying shortcuts
     */
    public GUISpellList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final GUIItemItemFactory itemItemFactory, @NotNull final SpellsManager spellsManager, @NotNull final KeybindingsManager keybindingsManager) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, itemView, currentItem, itemItemFactory);
        this.spellsManager = spellsManager;
        this.keybindingsManager = keybindingsManager;
    }

    /**
     * Return a text with the keybindings for the spell.
     * @param spell what to search bindings for
     * @param prefix invocation prefix, "cast " or "invoke ",  to search for
     * @param legend the text before the binding(s)
     * @return empty string if no matching bindings, else text in the form
     *         "legend (binding 1) ; (binding 2) ; ..."
     */
    private String getBindings(@NotNull final Spell spell, @NotNull final String prefix, @NotNull final String legend) {
        final String search = prefix+spell.getName().charAt(0);
        final String match = prefix+spell.getName();

        // because key bindings can specify partial names, we search in 2 steps:
        // - search all bindings with the first spell letter
        // - from those bindings only keep the ones the spell's command matches
        final Iterable<KeyBinding> bindings = keybindingsManager.getBindingsForPartialCommand(search);
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

        final StringBuilder sb = new StringBuilder(spell.getTooltipText());

        // find bindings to cast or invoke the spell
        sb.append(getBindings(spell, "cast ", "<br>Cast shortcut: "));
        sb.append(getBindings(spell, "invoke ", "<br>Invoke shortcut:"));

        setTooltipText(sb.toString(), x, y, w, h);
    }
}

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

package com.realtime.crossfire.jxclient.shortcuts;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellListener;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Shortcut} that casts a spell.
 * @author Andreas Kirschbaum
 */
public class ShortcutSpell extends Shortcut {

    /**
     * Command prefix to "cast" a spell.
     */
    @NotNull
    private static final String CAST = "cast ";

    /**
     * Command prefix to "invoke" a spell.
     */
    @NotNull
    private static final String INVOKE = "invoke ";

    /**
     * The command queue for executing commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The spell to cast.
     */
    @NotNull
    private final Spell spell;

    /**
     * The command for casting the spell.
     */
    @NotNull
    private String command = CAST;

    /**
     * The {@link SpellListener} attached to {@link #spell}.
     */
    @NotNull
    private final SpellListener spellListener = this::fireModifiedEvent;

    /**
     * Creates a new instance.
     * @param commandQueue the command queue for executing commands
     * @param spell the spell to cast
     */
    public ShortcutSpell(@NotNull final CommandQueue commandQueue, @NotNull final Spell spell) {
        this.commandQueue = commandQueue;
        this.spell = spell;
        spell.addSpellListener(spellListener);
    }

    /**
     * Returns the spell to cast.
     * @return the spell
     */
    @NotNull
    public Spell getSpell() {
        return spell;
    }

    /**
     * Returns whether the spell should be "cast" or "invoked".
     * @return {@code true} for "cast", or {@code false} for "invoke"
     */
    public boolean isCast() {
        return command == CAST;
    }

    /**
     * Sets whether the spell should be "cast" or "invoked".
     * @param cast {@code true} for "cast", or {@code false} for "invoke"
     */
    public void setCast(final boolean cast) {
        final String newCommand = cast ? CAST : INVOKE;
        if (command == newCommand) {
            return;
        }

        command = newCommand;
        fireModifiedEvent();
    }

    @Override
    public void dispose() {
        spell.removeSpellListener(spellListener);
    }

    @Override
    public void execute() {
        if (!spell.isUnknown()) {
            commandQueue.sendNcom(false, command+spell.getTag());
        }
    }

    @NotNull
    @Override
    public String getTooltipText() {
        return command+spell.getTooltipText();
    }

    @Override
    public void visit(@NotNull final ShortcutVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean displaysFace(final Face face) {
        return face.getFaceNum() == spell.getFaceNum();
    }

}

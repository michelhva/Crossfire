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

package com.realtime.crossfire.jxclient.sound;

import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link CheckBoxOption} that enables/disables sound support.
 * @author Andreas Kirschbaum
 */
public class SoundCheckBoxOption extends CheckBoxOption {

    /**
     * The {@link SoundManager} instance to affect.
     */
    @NotNull
    private final SoundManager soundManager;

    /**
     * Creates a new instance.
     * @param soundManager the sound manager instance to affect
     */
    public SoundCheckBoxOption(@NotNull final SoundManager soundManager) {
        super("<html>Plays sound effects and background music if checked.");
        this.soundManager = soundManager;
    }

    @Override
    protected void execute(final boolean checked) {
        soundManager.setEnabled(checked);
    }

    @Override
    public boolean isDefaultChecked() {
        return true;
    }

}

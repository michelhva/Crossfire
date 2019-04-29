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

package com.realtime.crossfire.jxclient.settings.options;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link CheckBoxOption} that toggles a pickup setting.
 * @author Andreas Kirschbaum
 */
public class PickupOption extends CheckBoxOption {

    /**
     * The pickup instance for modifying the pickup mode.
     */
    @NotNull
    private final Pickup pickup;

    /**
     * The affected pickup mode.
     */
    private final long option;

    /**
     * Creates a new instance.
     * @param pickup the pickup instance for modifying the pickup mode
     * @param option the affected pickup mode
     * @param tooltipText the tooltip text to explain this option
     */
    public PickupOption(@NotNull final Pickup pickup, final long option, @NotNull final String tooltipText) {
        super(tooltipText);
        this.pickup = pickup;
        this.option = option;
    }

    @Override
    protected void execute(final boolean checked) {
        pickup.setPickupMode(option, checked);
    }

    @Override
    public boolean isDefaultChecked() {
        return (Pickup.DEFAULT_PICKUP_MODE&option) == option;
    }

    @Override
    public boolean inhibitSave() {
        return true;
    }

    /**
     * Notifies this instance that the pickup mode has changed.
     * @param pickupMode the new pickup mode
     */
    public void setPickupMode(final long pickupMode) {
        setChecked((pickupMode&option) == option);
    }

}

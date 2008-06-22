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
package com.realtime.crossfire.jxclient.settings.options;

/**
 * The base class for all check box options. It manages the checked/unchecked
 * state and notifies listeners about changes.
 *
 * @author Andreas Kirschbaum
 */
public abstract class CheckBoxOption extends Option
{
    /**
     * The current state.
     */
    private boolean checked = false;

    /**
     * Return the current state.
     *
     * @return The current state.
     */
    public boolean isChecked()
    {
        return checked;
    }

    /**
     * Set the current state.
     *
     * @param checked The new state.
     */
    public void setChecked(final boolean checked)
    {
        if (this.checked == checked)
        {
            return;
        }

        this.checked = checked;
        fireStateChangedEvent();
    }

    /**
     * Toggle the checked state.
     */
    public void toggleChecked()
    {
        setChecked(!checked);
    }

    /**
     * Execute the action associated with this check box option. Must be
     * implemented in subsclasses.
     * @param checked whether the check box option is checked
     */
    protected abstract void execute(final boolean checked);

    /** {@inheritDoc} */
    @Override
    protected void fireStateChangedEvent()
    {
        execute(checked);
        super.fireStateChangedEvent();
    }

    /**
     * Return the default value of {@link #isChecked()}.
     *
     * @return The default value.
     */
    public abstract boolean isDefaultChecked();
}

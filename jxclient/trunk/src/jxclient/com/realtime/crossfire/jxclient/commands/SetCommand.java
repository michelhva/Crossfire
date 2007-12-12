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
package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionException;

/**
 * Implements a "script" command. It runs a new script.
 *
 * @author Andreas Kirschbaum
 */
public class SetCommand extends AbstractCommand
{
    /**
     * Create a new instance.
     *
     * @param window The window to execute in.
     */
    protected SetCommand(final JXCWindow window)
    {
        super(window);
    }

    /** {@inheritDoc} */
    public void execute(final String args)
    {
        final String[] tmp = Commands.patternWhitespace.split(args, 2);
        if (tmp.length != 2)
        {
            drawInfoError("The set command needs two arguments: set <option> <value>");
            return;
        }

        final String optionName = tmp[0];
        final String optionArgs = tmp[1];
        final CheckBoxOption option;
        try
        {
            option = getWindow().getOptionManager().getCheckBoxOption(optionName);
        }
        catch (final OptionException ex)
        {
            drawInfoError("Unknown option '"+optionName+"'");
            return;
        }

        final boolean checked;
        if (optionArgs.equals("on"))
        {
            checked = true;
        }
        else if (optionArgs.equals("off"))
        {
            checked = false;
        }
        else
        {
            drawInfoError("The '"+optionArgs+"' for option '"+optionName+"'is invalid. Valid arguments are 'on' or 'off'.");
            return;
        }

        option.setChecked(checked);
    }
}

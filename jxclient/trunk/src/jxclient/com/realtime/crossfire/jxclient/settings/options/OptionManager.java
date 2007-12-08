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

import com.realtime.crossfire.jxclient.settings.Settings;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a set of named options.
 *
 * @author Andreas Kirschbaum
 */
public class OptionManager
{
    /**
     * Maps option name to option instance.
     */
    private final Map<String, Option> options = new HashMap<String, Option>();

    /**
     * The settings instance for loading/saving option values.
     */
    private final Settings settings;

    /**
     * Create a new instance.
     *
     * @param settings The settings instance for loading/saving option values.
     */
    public OptionManager(final Settings settings)
    {
        this.settings = settings;
    }

    /**
     * Add a new option.
     *
     * @param optionName The option name to add.
     *
     * @param documentation The documentation string for the settings.
     *
     * @param option The option to add.
     *
     * @throws OptionException If the option name is not unique.
     */
    public void addOption(final String optionName, final String documentation, final Option option) throws OptionException
    {
        if (optionName == null) throw new IllegalArgumentException();
        if (documentation == null) throw new IllegalArgumentException();
        if (option == null) throw new IllegalArgumentException();

        if (options.containsKey(optionName))
        {
            throw new OptionException("duplicate option name: "+optionName);
        }

        options.put(optionName, option);
    }

    /**
     * Return a check box option.
     *
     * @param optionName The option name to look up.
     *
     * @return The option.
     *
     * @throws OptionException If the option name does not exist.
     */
    public CheckBoxOption getCheckBoxOption(final String optionName) throws OptionException
    {
        final Option option = options.get(optionName);
        if (option == null || !(option instanceof CheckBoxOption))
        {
            throw new OptionException("undefined option: "+optionName);
        }

        return (CheckBoxOption)option;
    }

    /**
     * Load all options' states from the backing settings instance.
     */
    public void loadOptions()
    {
        for (final Map.Entry<String, Option> e : options.entrySet())
        {
            final String optionName = e.getKey();
            final Option option = e.getValue();
            if (option instanceof CheckBoxOption)
            {
                final CheckBoxOption checkBoxOption = (CheckBoxOption)option;
                final boolean checked = settings.getBoolean(optionName, checkBoxOption.isDefaultChecked());
                if (checkBoxOption.isChecked() != checked)
                {
                    checkBoxOption.setChecked(checked);
                }
                else
                {
                    // make sure the appropriate option command is executed
                    checkBoxOption.fireStateChangedEvent();
                }
            }
            else if (option instanceof DialogStatusOption)
            {
                final DialogStatusOption dialogStatusOption = (DialogStatusOption)option;
                final boolean open = settings.getBoolean(optionName, dialogStatusOption.isDefaultOpen());
                if (dialogStatusOption.isOpen() != open)
                {
                    dialogStatusOption.setOpen(open);
                }
                else
                {
                    // make sure the appropriate option command is executed
                    dialogStatusOption.fireStateChangedEvent();
                }
            }
            else
            {
                throw new AssertionError();
            }
        }
    }

    /**
     * Save all options' states to the backing settings instance.
     */
    public void saveOptions()
    {
        for (final Map.Entry<String, Option> e : options.entrySet())
        {
            final String optionName = e.getKey();
            final Option option = e.getValue();
            if (option instanceof CheckBoxOption)
            {
                final CheckBoxOption checkBoxOption = (CheckBoxOption)option;
                settings.putBoolean(optionName, checkBoxOption.isChecked());
            }
            else if (option instanceof DialogStatusOption)
            {
                final DialogStatusOption dialogStatusOption = (DialogStatusOption)option;
                settings.putBoolean(optionName, dialogStatusOption.isOpen());
            }
            else
            {
                throw new AssertionError();
            }
        }
    }
}

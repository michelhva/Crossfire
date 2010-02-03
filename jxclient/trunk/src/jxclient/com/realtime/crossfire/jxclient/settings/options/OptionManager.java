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

package com.realtime.crossfire.jxclient.settings.options;

import com.realtime.crossfire.jxclient.settings.Settings;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    private final Map<String, Option> options = new HashMap<String, Option>();

    /**
     * The settings instance for loading/saving option values.
     */
    @NotNull
    private final Settings settings;

    /**
     * Create a new instance.
     *
     * @param settings The settings instance for loading/saving option values.
     */
    public OptionManager(@NotNull final Settings settings)
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
    public void addOption(@NotNull final String optionName, @NotNull final String documentation, @NotNull final Option option) throws OptionException
    {
        if (options.containsKey(optionName))
        {
            throw new OptionException("duplicate option name: "+optionName);
        }

        options.put(optionName, option);
    }

    /**
     * Removes an option by name. Does nothing if the option does not exist.
     * @param optionName the option name to remove
     */
    public void removeOption(@NotNull final String optionName)
    {
        options.remove(optionName);
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
    @NotNull
    public CheckBoxOption getCheckBoxOption(@NotNull final String optionName) throws OptionException
    {
        final Object option = options.get(optionName);
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
            final Object option = e.getValue();
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
            if (!option.inhibitSave())
            {
                if (option instanceof CheckBoxOption)
                {
                    final CheckBoxOption checkBoxOption = (CheckBoxOption)option;
                    settings.putBoolean(optionName, checkBoxOption.isChecked());
                }
                else
                {
                    throw new AssertionError();
                }
            }
        }
    }
}

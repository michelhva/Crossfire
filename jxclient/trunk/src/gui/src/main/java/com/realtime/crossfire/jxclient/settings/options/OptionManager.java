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

import com.realtime.crossfire.jxclient.settings.Settings;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Maintains a set of named options.
 * @author Andreas Kirschbaum
 */
public class OptionManager {

    /**
     * Maps option name to option instance.
     */
    @NotNull
    private final Map<String, Entry> options = new HashMap<String, Entry>();

    /**
     * The settings instance for loading/saving option values.
     */
    @NotNull
    private final Settings settings;

    /**
     * Creates a new instance.
     * @param settings the settings instance for loading/saving option values
     */
    public OptionManager(@NotNull final Settings settings) {
        this.settings = settings;
    }

    /**
     * Adds a new option.
     * @param optionName the option name to add
     * @param documentation the documentation string for the settings
     * @param option the option to add
     * @throws OptionException if the option name is not unique
     */
    public void addOption(@NotNull final String optionName, @NotNull final String documentation, @NotNull final Option option) throws OptionException {
        if (options.containsKey(optionName)) {
            throw new OptionException("duplicate option name: "+optionName);
        }

        options.put(optionName, new Entry(option, documentation));
    }

    /**
     * Removes an option by name. Does nothing if the option does not exist.
     * @param optionName the option name to remove
     */
    public void removeOption(@NotNull final String optionName) {
        options.remove(optionName);
    }

    /**
     * Returns a check box option.
     * @param optionName the option name to look up
     * @return the option
     * @throws OptionException if the option name does not exist
     */
    @NotNull
    public CheckBoxOption getCheckBoxOption(@NotNull final String optionName) throws OptionException {
        final Entry entry = options.get(optionName);
        if (entry == null || !(entry.getOption() instanceof CheckBoxOption)) {
            throw new OptionException("Unknown option '"+optionName+"'");
        }

        return (CheckBoxOption)entry.getOption();
    }

    /**
     * Loads all options' states from the backing settings instance.
     */
    public void loadOptions() {
        for (final Map.Entry<String, Entry> e : options.entrySet()) {
            final String optionName = e.getKey();
            final Object option = e.getValue().getOption();
            if (option instanceof CheckBoxOption) {
                final CheckBoxOption checkBoxOption = (CheckBoxOption)option;
                final boolean checked = settings.getBoolean(optionName, checkBoxOption.isDefaultChecked());
                if (checkBoxOption.isChecked() == checked) {
                    // make sure the appropriate option command is executed
                    checkBoxOption.fireStateChangedEvent();
                } else {
                    checkBoxOption.setChecked(checked);
                }
            } else {
                throw new AssertionError();
            }
        }
    }

    /**
     * Saves all options' states to the backing settings instance.
     */
    public void saveOptions() {
        for (final Map.Entry<String, Entry> e : options.entrySet()) {
            final String optionName = e.getKey();
            final Entry entry = e.getValue();
            final Option option = entry.getOption();
            if (!option.inhibitSave()) {
                if (option instanceof CheckBoxOption) {
                    final CheckBoxOption checkBoxOption = (CheckBoxOption)option;
                    settings.putBoolean(optionName, checkBoxOption.isChecked(), entry.getDocumentation());
                } else {
                    throw new AssertionError();
                }
            }
        }
    }

    /**
     * Pair of {@link Option} and corresponding documentation string.
     * @author Andreas Kirschbaum
     */
    private static class Entry {

        /**
         * The {@link Option} instance.
         */
        @NotNull
        private final Option option;

        /**
         * The corresponding documentation string.
         */
        @NotNull
        private final String documentation;

        /**
         * Creates a new instance.
         * @param option the option instance
         * @param documentation the corresponding documentation string
         */
        private Entry(@NotNull final Option option, @NotNull final String documentation) {
            this.option = option;
            this.documentation = documentation;
        }

        /**
         * Returns the {@link Option} instance.
         * @return the option instance
         */
        @NotNull
        public Option getOption() {
            return option;
        }

        /**
         * Returns the corresponding documentation string.
         * @return the corresponding documentation string
         */
        @NotNull
        public String getDocumentation() {
            return documentation;
        }

    }

}

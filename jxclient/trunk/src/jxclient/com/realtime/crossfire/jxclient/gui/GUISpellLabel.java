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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.Spell;
import com.realtime.crossfire.jxclient.SpellChangedEvent;
import com.realtime.crossfire.jxclient.SpellListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A {@link GUILabel} that displays the currently selected spell.
 *
 * @author Andreas Kirschbaum
 */
public class GUISpellLabel extends GUILabel
{
    /**
     * The display type.
     */
    public enum Type
    {
        /**
         * Display the spell name.
         */
        SPELL_NAME,

        /**
         * Display the spell icon.
         */
        SPELL_ICON,

        /**
         * Display the spell cost (mana/grace).
         */
        SPELL_COST,

        /**
         * Display the spell level.
         */
        SPELL_LEVEL,

        /**
         * Display the spell description.
         */
        SPELL_DESCRIPTION,
    }

    /**
     * The spell type to display.
     */
    private final Type type;

    /**
     * The {@link SpellListener} registered to be notified about changed spell
     * parameters.
     */
    private final SpellListener spellListener = new SpellListener()
    {
        /** {@inheritDoc} */
        public void spellChanged(final SpellChangedEvent evt)
        {
            final Spell sp = evt.getSpell();
            if (sp == null)
            {
                setText("");
                return;
            }

            switch (type)
            {
            case SPELL_NAME:
                setText(sp.getName());
                break;

            case SPELL_ICON:
                setText("");
                setBackground(sp.getImageIcon());
                break;

            case SPELL_COST:
                final int mana = sp.getMana();
                final int grace = sp.getGrace();
                if (grace == 0)
                {
                    setText("M:"+mana);
                }
                else if (mana == 0)
                {
                    setText("G:"+grace);
                }
                else
                {
                    setText("M:"+mana+" G:"+grace);
                }
                break;

            case SPELL_LEVEL:
                setText(Integer.toString(sp.getLevel()));
                break;

            case SPELL_DESCRIPTION:
                setText(sp.getMessage());
                break;
            }
        }
    };

    /**
     * Create a new instance.
     *
     * @param jxcWindow The window this gui element is part of.
     *
     * @param name The gui element name.
     *
     * @param x The x-coordinate to display at.
     *
     * @param y The y-coordinate to display at.
     *
     * @param w The width of the label.
     *
     * @param h The height of the label.
     *
     * @param picture The background picture; may be <code>null</code>. It is
     * ignored for type <code>SPELL_ICON</code>.
     *
     * @param font The font to use.
     *
     * @param type The display type.
     *
     * @throws IOException If an I/O error occurs.
     */
    public GUISpellLabel(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final Font font, final Type type) throws IOException
    {
        super(jxcWindow, name, x, y, w, h, picture, font, Color.WHITE, "");
        this.type = type;
        jxcWindow.addSpellListener(spellListener);
    }
}

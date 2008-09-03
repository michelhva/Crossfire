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

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.spells.Spell;
import com.realtime.crossfire.jxclient.spells.SpellListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 * A {@link GUIHTMLLabel} that displays the currently selected spell.
 *
 * @author Andreas Kirschbaum
 */
public class GUISpellLabel extends GUIHTMLLabel
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

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
     * The instance for looking up faces.
     */
    private final FacesManager facesManager;

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
        public void spellChanged(final Spell spell)
        {
            if (spell == null)
            {
                setText("");
                return;
            }

            switch (type)
            {
            case SPELL_NAME:
                setText(spell.getName());
                break;

            case SPELL_ICON:
                setText("");
                setBackgroundImage(facesManager.getOriginalImageIcon(spell.getFaceNum()));
                break;

            case SPELL_COST:
                final int mana = spell.getMana();
                final int grace = spell.getGrace();
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
                setText(Integer.toString(spell.getLevel()));
                break;

            case SPELL_DESCRIPTION:
                setText(spell.getMessage());
                break;
            }
        }
    };

    /**
     * Create a new instance.
     *
     * @param window The window this gui element is part of.
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
     * @param facesManager the instance for looking up faces
     *
     * @param font The font to use.
     *
     * @param type The display type.
     */
    public GUISpellLabel(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final FacesManager facesManager, final Font font, final Type type)
    {
        super(window, name, x, y, w, h, picture, font, Color.WHITE, new Color(0, 0, 0, 0F), "");
        this.facesManager = facesManager;
        this.type = type;
        window.getCurrentSpellManager().addSpellListener(spellListener);
    }
}

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

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManagerListener;
import com.realtime.crossfire.jxclient.spells.Spell;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIHTMLLabel} that displays the currently selected spell.
 * @author Andreas Kirschbaum
 */
public class GUISpellLabel extends GUIHTMLLabel {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The instance for looking up faces.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The spell type to display.
     */
    @NotNull
    private final Type type;

    /**
     * The {@link CurrentSpellManager} to monitor.
     */
    @NotNull
    private final CurrentSpellManager currentSpellManager;

    /**
     * The {@link CurrentSpellManagerListener} registered to be notified about
     * changed spell parameters.
     */
    @NotNull
    private final CurrentSpellManagerListener currentSpellManagerListener = new CurrentSpellManagerListener() {

        @Override
        public void spellChanged(@Nullable final Spell spell) {
            if (spell == null) {
                setText("");
                return;
            }

            switch (type) {
            case SPELL_NAME:
                setText(spell.getName());
                break;

            case SPELL_ICON:
                setText("");
                setBackgroundImage(facesManager.getOriginalImageIcon(spell.getFaceNum(), null));
                break;

            case SPELL_COST:
                final int mana = spell.getMana();
                final int grace = spell.getGrace();
                if (grace == 0) {
                    setText("M:"+mana);
                } else if (mana == 0) {
                    setText("G:"+grace);
                } else {
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
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the gui element name
     * @param picture the background picture; may be {@code null}; it is ignored
     * for type {@code SPELL_ICON}
     * @param facesManager the instance for looking up faces
     * @param font the font to use
     * @param type the display type
     * @param currentSpellManager the current spell manager to track
     * @param guiFactory the global GUI factory instance
     */
    public GUISpellLabel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @Nullable final BufferedImage picture, @NotNull final FacesManager facesManager, @NotNull final Font font, @NotNull final Type type, @NotNull final CurrentSpellManager currentSpellManager, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, picture, font, Color.WHITE, null, "", guiFactory);
        this.facesManager = facesManager;
        this.type = type;
        this.currentSpellManager = currentSpellManager;
        this.currentSpellManager.addSpellListener(currentSpellManagerListener);
    }

    @Override
    public void dispose() {
        super.dispose();
        currentSpellManager.removeSpellListener(currentSpellManagerListener);
    }

}

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

package com.realtime.crossfire.jxclient.skin.io;

import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinCache;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import org.jetbrains.annotations.NotNull;

/**
 * Creates gui element instances from string representations.
 * @author Andreas Kirschbaum
 */
public class GuiElementParser
{
    /**
     * The defined {@link GUIElement}s.
     */
    @NotNull
    private final JXCSkinCache<GUIElement> definedGUIElements;

    /**
     * Creates a new instance.
     * @param definedGUIElements the defined gui elements
     */
    public GuiElementParser(@NotNull final JXCSkinCache<GUIElement> definedGUIElements)
    {
        this.definedGUIElements = definedGUIElements;
    }

    /**
     * Returns a {@link GUIText} by element name.
     * @param name the element name
     * @return the <code>GUIText</code> element
     * @throws JXCSkinException if the element name is undefined
     */
    @NotNull
    public GUIText lookupTextElement(@NotNull final String name) throws JXCSkinException
    {
        final Object element = definedGUIElements.lookup(name);
        if (!(element instanceof GUIText))
        {
            throw new JXCSkinException("element "+name+" is not a text field");
        }

        return (GUIText)element;
    }

    /**
     * Returns a {@link AbstractLabel} by element name.
     * @param name the element name
     * @return the <code>AbstractLabel</code> element
     * @throws JXCSkinException if the element name is undefined
     */
    @NotNull
    public AbstractLabel lookupLabelElement(@NotNull final String name) throws JXCSkinException
    {
        final Object element = definedGUIElements.lookup(name);
        if (!(element instanceof AbstractLabel))
        {
            throw new JXCSkinException("element "+name+" is not a label");
        }

        return (AbstractLabel)element;
    }
}

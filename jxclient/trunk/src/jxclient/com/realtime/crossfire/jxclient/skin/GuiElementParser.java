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
package com.realtime.crossfire.jxclient.skin;

import com.realtime.crossfire.jxclient.gui.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;

/**
 * Creates gui element instances from string representations.
 * @author Andreas Kirschbaum
 */
public class GuiElementParser
{
    /**
     * The skin for lookin up defined GUI elements.
     */
    private final JXCSkin skin;

    /**
     * Creates a new instance.
     * @param skin the skin for looking up defined GUI elements
     */
    public GuiElementParser(final JXCSkin skin)
    {
        this.skin = skin;
    }

    /**
     * Returns a {@link GUIText} by element name.
     * @param name the element name
     * @return the <code>GUIText</code> element
     * @throws JXCSkinException if the element name is undefined
     */
    public GUIText lookupTextElement(final String name) throws JXCSkinException
    {
        final GUIElement element = skin.lookupGuiElement(name);
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
    public AbstractLabel lookupLabelElement(final String name) throws JXCSkinException
    {
        final GUIElement element = skin.lookupGuiElement(name);
        if (!(element instanceof AbstractLabel))
        {
            throw new JXCSkinException("element "+name+" is not a label");
        }

        return (AbstractLabel)element;
    }
}

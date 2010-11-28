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
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import com.realtime.crossfire.jxclient.skin.skin.Extent;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class GuiBuilder {

    private boolean addedElementsContainsWildcard = false;

    @NotNull
    private final List<GUIElement> addedElements = new ArrayList<GUIElement>();

    /**
     * Maps {@link GUIElement} instance to associated {@link
     * GridBagConstraints}.
     */
    @NotNull
    private final Map<GUIElement, Object> gridBagConstraints = new HashMap<GUIElement, Object>();

    /**
     * Maps {@link GUIElement} instance to parent container.
     */
    @NotNull
    private final Map<GUIElement, Container> parents = new HashMap<GUIElement, Container>();

    public void clear() {
        addedElementsContainsWildcard = false;
        addedElements.clear();
        gridBagConstraints.clear();
        parents.clear();
    }

    public boolean addWildcard() {
        if (addedElementsContainsWildcard) {
            return false;
        }

        addedElementsContainsWildcard = true;
        addedElements.add(null);
        return true;
    }

    public void addElement(@NotNull final GUIElement guiElement) {
        addedElements.add(guiElement);
    }

    public void setParent(@NotNull final GUIElement guiElement, final Container parent) {
        parents.put(guiElement, parent);
    }

    public void finish(@NotNull final Iterator<GUIElement> it, @NotNull final Gui gui) {
        final Map<GUIElement, GUIElement> wildcardElements = new LinkedHashMap<GUIElement, GUIElement>();
        while (it.hasNext()) {
            final GUIElement element = it.next();
            wildcardElements.put(element, element);
        }
        for (final GUIElement element : addedElements) {
            wildcardElements.remove(element);
        }

        int i = 0;

        while (i < addedElements.size()) {
            final GUIElement element = addedElements.get(i);
            if (element == null) {
                i++;
                break;
            }

            insertIntoParent(element, gui);
            i++;
        }

        for (final GUIElement element : wildcardElements.keySet()) {
            insertIntoParent(element, gui);
        }

        while (i < addedElements.size()) {
            final GUIElement element = addedElements.get(i);
            if (element != null) {
                insertIntoParent(element, gui);
            }

            i++;
        }
    }

    private void insertIntoParent(@NotNull final GUIElement element, @NotNull final Gui gui) {
        final Container parent = parents.get(element);
        if (parent == null) {
            throw new IllegalArgumentException("element '"+element.getName()+"' has no defied parent");
        }
        final Object constraints = gridBagConstraints.get(element);
        assert constraints != null;
        if (constraints instanceof GridBagConstraints) {
            final GridBagConstraints gbc = (GridBagConstraints)constraints;
            if (gbc.gridx == 1 && gbc.gridy == 0) {
                gbc.gridx = parent.getComponentCount();
            } else if (gbc.gridx == 0 && gbc.gridy == 1) {
                gbc.gridy = parent.getComponentCount();
            } else {
                throw new AssertionError();
            }
            //System.out.println("element="+element.getName()+", parent="+parent.getName()+", gbc="+gbc.gridwidth+"x"+gbc.gridheight+"+"+gbc.gridx+"+"+gbc.gridy+" "+gbc.weightx+"/"+gbc.weighty+" fill="+gbc.fill);
            parent.add((Component/*XXX*/) element, constraints);
            gui.addElement(element);
        } else if (constraints instanceof LayeredConstraint) {
            final LayeredConstraint layeredConstraint = (LayeredConstraint) constraints;
            final Extent extent = layeredConstraint.getExtent();
            final int x = extent.getX(1024, 768); // XXX
            final int y = extent.getY(1024, 768); // XXX
            final int w = extent.getW(1024, 768); // XXX
            final int h = extent.getH(1024, 768); // XXX
            element.setBounds(x, y, w, h);
            parent.add((Component/*XXX*/) element, Integer.valueOf(layeredConstraint.getLayer()));
            gui.addElement(element);
        } else {
            //System.out.println("element="+element.getName()+", parent="+parent.getName()+", constraints="+constraints);
            throw new AssertionError();
        }
    }

    public void defineElement(@NotNull final GUIElement guiElement, @NotNull final Container parent, @NotNull final Object constraints) {
        parents.put(guiElement, parent);
        gridBagConstraints.put(guiElement, constraints);
    }

}

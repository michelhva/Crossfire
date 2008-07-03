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
package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.gui.GUIMetaElement;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * A {@link ListCellRenderer} that renders {@link GUIMetaElement} instances.
 * @author Andreas Kirschbaum
 */
public class MetaElementCellRenderer extends JPanel implements ListCellRenderer
{
    /**
     * The template used for painting.
     */
    private final GUIMetaElement template;

    /**
     * Creates a new instance.
     * @param template the template used for painting
     */
    public MetaElementCellRenderer(final GUIMetaElement template)
    {
        super(new BorderLayout());
        setOpaque(false);
        this.template = template;
        add(template, BorderLayout.NORTH);
    }

    /** {@inheritDoc} */
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        template.setIndex(((GUIMetaElement)value).getIndex());
        template.setSelected(isSelected);
        return this;
    }
}

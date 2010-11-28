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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import org.jetbrains.annotations.NotNull;

public class ComponentDumper {

    private ComponentDumper() {
    }

    public static void dump(@NotNull final Component component) {
        dump(component, "", "");
    }

    public static void dump(@NotNull final Component component, @NotNull final String prefix1, @NotNull final String prefix2) {
        final String name = component.getName();
        System.out.print(prefix1);
        System.out.print(name);
        System.out.print(" [");
        System.out.print(component.getWidth());
        System.out.print("x");
        System.out.print(component.getHeight());
        System.out.print("+");
        System.out.print(component.getX());
        System.out.print("+");
        System.out.print(component.getY());
        System.out.print("] ");
        System.out.print("pref=");
        final Dimension preferredSize = component.getPreferredSize();
        if (preferredSize == null) {
            System.out.print("null");
        } else {
            System.out.print(preferredSize.width+"x"+preferredSize.height);
        }
        System.out.print(" ");
        System.out.print(component.getClass().getName());

        if (!(component instanceof Container)) {
            System.out.println();
            return;
        }
        final Container container = (Container)component;

        final LayoutManager layout = container.getLayout();
        if (layout != null) {
            System.out.print(" layout=");
            System.out.print(layout.getClass().getName());
        }
        System.out.println();

        final int components = container.getComponentCount();
        for (int i = 0; i < components; i++) {
            dump(container.getComponent(i), prefix2+"+-", i+1 < components ? prefix2+"| " : prefix2+"   ");
        }
    }

}

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

package com.realtime.crossfire.jxclient.util;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for Swing related functions.
 * @author Andreas Kirschbaum
 */
public class SwingUtilities2 {

    /**
     * Private constructor to prevent instantiation.
     */
    private SwingUtilities2() {
    }

    /**
     * Calls {@link SwingUtilities#invokeAndWait(Runnable)} if not on the EDT or
     * calls the {@link Runnable} directly if on the EDT.
     * @param runnable the runnable to call
     */
    public static void invokeAndWait(@NotNull final Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (final InterruptedException ignored) {
                Thread.currentThread().interrupt();
            } catch (final InvocationTargetException ex) {
                throw new AssertionError(ex);
            }
        }
    }

    /**
     * Calls {@link SwingUtilities#invokeLater(Runnable)} if not on the EDT or
     * calls the {@link Runnable} directly if on the EDT.
     * @param runnable the runnable to call
     */
    public static void invokeLater(final Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

}

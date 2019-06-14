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

import com.realtime.crossfire.jxclient.character.Choice;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireFailureListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Color;
import java.awt.Font;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIHTMLLabel} that displays stat information in the character
 * creation dialog.
 * @author Andreas Kirschbaum
 */
public class GUINewcharLabel extends GUIOneLineLabel {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * A {@link Pattern} that matches an optional digit prefix.
     */
    @NotNull
    private static final Pattern PATTERN_DIGITS = Pattern.compile("^([0-9]+ )?");

    /**
     * The stat value to display.
     */
    @NotNull
    private final NewcharStat stat;

    /**
     * The stat type to display.
     */
    @NotNull
    private final NewcharType type;

    /**
     * The global {@link NewCharModel} instance.
     */
    @NotNull
    private final NewCharModel newCharModel;

    /**
     * The font for invalid values.
     */
    @NotNull
    private final Font fontError;

    /**
     * The color for invalid values.
     */
    @NotNull
    private final Color colorError;

    /**
     * The {@link CrossfireServerConnection} to monitor.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The listener attached to {@link #newCharModel}.
     */
    @NotNull
    private final NewCharModelListener listener = this::updateContents;

    /**
     * The {@link CrossfireFailureListener} registered to receive failure
     * messages.
     */
    @Nullable
    private final CrossfireFailureListener crossfireFailureListener;

    /**
     * Whether a valid value is currently shown.
     */
    private boolean validValue = true;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the gui element name
     * @param crossfireServerConnection the connection instance
     * @param font the font to use
     * @param fontError the font for invalid values
     * @param stat the stat value to display
     * @param type the stat type to display
     * @param newCharModel the global new char model instance
     * @param color the default text color
     * @param colorError the text color for invalid values
     * @param guiFactory the global GUI factory instance
     */
    public GUINewcharLabel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final Font font, @NotNull final Font fontError, @NotNull final NewcharStat stat, @NotNull final NewcharType type, @NotNull final NewCharModel newCharModel, @NotNull final Color color, @NotNull final Color colorError, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, null, font, color, null, type == NewcharType.ERROR || type == NewcharType.CHAR_OPTION ? Alignment.LEFT : Alignment.RIGHT, "", guiFactory);
        this.stat = stat;
        this.type = type;
        this.newCharModel = newCharModel;
        this.fontError = fontError;
        this.colorError = colorError;

        newCharModel.addListener(listener);
        updateContents();

        this.crossfireServerConnection = crossfireServerConnection;
        crossfireFailureListener = type == NewcharType.ERROR ? new CrossfireFailureListener() {

            @Override
            public void failure(@NotNull final String command, @NotNull final String arguments) {
                GUINewcharLabel.this.newCharModel.setErrorText(NewCharModel.PRIORITY_SERVER_FAILURE, PATTERN_DIGITS.matcher(arguments).replaceFirst(""));
            }

            @Override
            public void clearFailure() {
                GUINewcharLabel.this.newCharModel.setErrorText(NewCharModel.PRIORITY_SERVER_FAILURE, null);
            }

        } : null;
        if (crossfireFailureListener != null) {
            this.crossfireServerConnection.addCrossfireFailureListener(crossfireFailureListener);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        newCharModel.removeListener(listener);
        if (crossfireFailureListener != null) {
            crossfireServerConnection.removeCrossfireFailureListener(crossfireFailureListener);
        }
    }

    @Override
    public void notifyOpen() {
        super.notifyOpen();
        if (type == NewcharType.ERROR) {
            newCharModel.setErrorText(NewCharModel.PRIORITY_SERVER_FAILURE, null);
        }
        updateContents();
    }

    @NotNull
    @Override
    protected Color getTextColor() {
        return validValue ? super.getTextColor() : colorError;
    }

    @NotNull
    @Override
    protected Font getTextFont() {
        return validValue ? super.getTextFont() : fontError;
    }

    /**
     * Updates the displayed information to show the current value.
     */
    private void updateContents() {
        switch (type) {
        case RACE:
            setText(Integer.toString(newCharModel.getRaceStatAdjustment(stat)));
            break;

        case CLASS:
            setText(Integer.toString(newCharModel.getClassStatAdjustment(stat)));
            break;

        case TOTAL:
            final int total = newCharModel.getTotal(stat);
            validValue = total >= 1;
            setText(Integer.toString(total));
            newCharModel.setErrorText(stat.getPriority(), validValue ? null : "The "+stat.getName()+" is invalid: it must be at least 1.");
            break;

        case ERROR:
            setText(newCharModel.getErrorText());
            break;

        case UNUSED:
            final int unusedPoints = newCharModel.getUnusedPoints();
            validValue = unusedPoints == 0;
            setText(Integer.toString(unusedPoints));
            newCharModel.setErrorText(NewCharModel.PRIORITY_UNUSED_POINTS, unusedPoints < 0 ? "You have used more than your allotted total attribute points." : unusedPoints > 0 ? "You have not used all your allotted attribute points." : null);
            break;

        case CHAR_OPTION:
            final Choice option = newCharModel.getOption();
            setText(option == null ? "" : option.getChoiceDescription());
            break;
        }
    }

}

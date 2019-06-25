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

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.metaserver.MetaserverEntry;
import com.realtime.crossfire.jxclient.metaserver.MetaserverEntryListener;
import com.realtime.crossfire.jxclient.metaserver.MetaserverListener;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Font;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIList} that tracks a {@link Metaserver} instance.
 * @author Andreas Kirschbaum
 */
public class GUIMetaElementList extends GUIList<GUIMetaElement> {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The metaserver model to track.
     */
    @NotNull
    private final MetaserverModel metaserverModel;

    /**
     * The {@link TooltipManager} to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to use.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * The name of this element.
     */
    @NotNull
    private final String name;

    /**
     * The image for drawing list entries.
     */
    @Nullable
    private final Image image;

    /**
     * The font for drawing list entries.
     */
    @NotNull
    private final Font font;

    /**
     * The format for drawing list entries.
     */
    @NotNull
    private final String format;

    /**
     * The tooltip format for drawing list entries.
     */
    @NotNull
    private final String tooltip;

    /**
     * The hostname input field to update; may be {@code null}.
     */
    @Nullable
    private final GUIText hostname;

    /**
     * The comment field to update; may be {@code null}.
     */
    @Nullable
    private final AbstractLabel comment;

    /**
     * The global {@link GuiFactory} instance.
     */
    @NotNull
    private final GuiFactory guiFactory;

    /**
     * The {@link MetaserverListener} attached to {@link #metaserverModel}. It
     * detects added or removed entries and updates the list accordingly.
     */
    @NotNull
    private final MetaserverListener metaserverListener = this::rebuildList;

    /**
     * The {@link MetaserverEntryListener} attached to all tracked metaserver
     * entries. It detects changed contents and updates the list accordingly.
     */
    @NotNull
    private final MetaserverEntryListener metaserverEntryListener = this::setChanged;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of each cell
     * @param cellHeight the height of each cell
     * @param metaserverModel the metaserver model to track
     * @param image the image for drawing list entries
     * @param font the font for drawing list entries
     * @param format the format for drawing list entries
     * @param tooltip the tooltip format for drawing list entries
     * @param hostname the hostname input field to update; may be {@code null}
     * @param comment the comment field to update; may be {@code null}
     * @param connectCommandList the command list to connect to the server
     * @param guiFactory the global GUI factory instance
     */
    public GUIMetaElementList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final MetaserverModel metaserverModel, @Nullable final Image image, @NotNull final Font font, @NotNull final String format, @NotNull final String tooltip, @Nullable final GUIText hostname, @Nullable final AbstractLabel comment, @NotNull final CommandList connectCommandList, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, new MetaElementCellRenderer(new GUIMetaElement(tooltipManager, elementListener, metaserverModel, name+"_template", image, font, 0, format, tooltip, guiFactory)), connectCommandList, guiFactory);
        this.metaserverModel = metaserverModel;
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.image = image;
        this.font = font;
        this.format = format;
        this.tooltip = tooltip;
        this.hostname = hostname;
        this.comment = comment;
        this.guiFactory = guiFactory;
        this.metaserverModel.addMetaserverListener(metaserverListener);
        rebuildList();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (int i = 0; i < metaserverModel.size(); i++) {
            metaserverModel.removeMetaserverEntryListener(i, metaserverEntryListener);
        }
        metaserverModel.removeMetaserverListener(metaserverListener);
    }

    @Override
    public void notifyOpen() {
    }

    /**
     * Rebuild the list cells.
     */
    private void rebuildList() {
        synchronized (getTreeLock()) {
            final int newSize = metaserverModel.size();
            final int oldSize = resizeElements(newSize);
            if (oldSize < newSize) {
                for (int i = oldSize; i < newSize; i++) {
                    final GUIMetaElement metaElement = new GUIMetaElement(tooltipManager, elementListener, metaserverModel, name+i, image, font, i, format, tooltip, guiFactory);
                    addElement(metaElement);
                    metaserverModel.addMetaserverEntryListener(i, metaserverEntryListener);
                }
            } else {
                for (int i = newSize; i < oldSize; i++) {
                    metaserverModel.removeMetaserverEntryListener(i, metaserverEntryListener);
                }
            }
        }
        setChanged();
    }

    @Override
    protected void activeChanged() {
    }

    @Override
    protected void selectionChanged(final int selectedIndex) {
        if (selectedIndex == -1) {
            // do not update hostname
            if (comment != null) {
                comment.setText("");
            }
        } else {
            final MetaserverEntry metaEntry = metaserverModel.getEntry(selectedIndex);
            if (hostname != null) {
                hostname.setText(metaEntry == null ? "" : metaEntry.getHostname());
            }
            if (comment != null) {
                comment.setText(metaEntry == null ? "" : metaEntry.getComment());
            }
        }
    }

    @Nullable
    @Override
    protected String getTooltip(final int index) {
        final MetaserverEntry metaEntry = metaserverModel.getEntry(index);
        return metaEntry == null ? null : metaEntry.format(tooltip);
    }

    /**
     * Select an entry by server name.
     * @param serverName the server name
     */
    public void setSelectedHostname(@NotNull final String serverName) {
        final int index = metaserverModel.getServerIndex(serverName);
        setSelectedIndex(index);
        if (index == -1 && hostname != null) {
            hostname.setText(serverName);
        }
    }

}

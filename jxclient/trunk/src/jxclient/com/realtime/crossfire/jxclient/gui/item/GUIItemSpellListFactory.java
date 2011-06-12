package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.SpellsView;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author nicolas
 */
public class GUIItemSpellListFactory implements GUIItemItemFactory {

    /**
     * The tooltip manager to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to notify.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * The {@link CommandQueue} for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The base name.
     */
    @NotNull
    private final String name;

    /**
     * The {@link ItemPainter} to use.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The {@link CrossfireServerConnection} to use.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    private final SpellsManager spellsManager;
    private final CurrentSpellManager currentSpellManager;
    private final SpellsView spellsView;

    /**
     * The {@link FacesManager} to use.
     */
    @NotNull
    private final FacesManager facesManager;

    public GUIItemSpellListFactory(TooltipManager tooltipManager, GUIElementListener elementListener, CommandQueue commandQueue, String name, CrossfireServerConnection crossfireServerConnection, ItemPainter itemPainter, FacesManager facesManager, SpellsManager spellsManager, CurrentSpellManager currentSpellManager, @NotNull final SpellsView spellsView) {
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.commandQueue = commandQueue;
        this.name = name;
        this.itemPainter = itemPainter;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.spellsManager = spellsManager;
        this.currentSpellManager = currentSpellManager;
        this.spellsView = spellsView;
    }

    public GUIElement newItem(int index) {
        return new GUIItemSpellList(tooltipManager, elementListener, crossfireServerConnection, name + index, itemPainter, index, facesManager, spellsManager, currentSpellManager, spellsView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public GUIItemItem newTemplateItem(final int cellHeight) {
        final GUIItemItem result = new GUIItemSpellList(tooltipManager, elementListener, crossfireServerConnection, name + "_template", itemPainter, -1, facesManager, spellsManager, currentSpellManager, spellsView);
        //noinspection SuspiciousNameCombination
        result.setSize(cellHeight, cellHeight);
        return result;
    }

    public int getMoveLocation() {
        return 0;
    }

}

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItemFactory;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author nicolas
 */
public class GUIKnowledgeTypeList extends GUIItemList {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    @NotNull
    private final KnowledgeManager knowledgeManager;
    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of cells
     * @param cellHeight the height of cells
     * @param itemView the item view to monitor
     * @param currentItem the label to update with information about the
     * selected item.
     * @param itemItemFactory the factory for creating item instances
     */
    public GUIKnowledgeTypeList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final GUIItemItemFactory itemItemFactory, @NotNull final KnowledgeManager knowledgeManager) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, itemView, currentItem, itemItemFactory);
        this.knowledgeManager = knowledgeManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void selectionChanged(int selectedIndex) {
        if (knowledgeManager == null) {
            return;
        }
        knowledgeManager.filterType(selectedIndex);
        super.selectionChanged(selectedIndex);
    }

}

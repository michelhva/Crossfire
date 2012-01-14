package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeManager;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author nicolas
 */
public class GUIItemKnowledgeTypeFactory implements GUIItemItemFactory {

    /**
     * The {@link TooltipManager} to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to notify.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * The base name for created elements.
     */
    @NotNull
    private final String name;

    /**
     * The {@link ItemPainter} for painting the icon.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The {@link FacesManager} to use.
     */
    @NotNull
    private final FacesManager facesManager;

    @NotNull
    private final KnowledgeManager knowledgeManager;

    @NotNull
    private final ItemView view;
    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the base name for created elements
     * @param itemPainter the item painter for painting the icon
     * @param facesManager the faces manager to use
     * @param spellsManager the spells manager instance to watch
     * @param spellsView the spells view to use
     */
    public GUIItemKnowledgeTypeFactory(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final ItemPainter itemPainter, @NotNull final FacesManager facesManager, @NotNull final KnowledgeManager knowledgeManager, @NotNull final ItemView view) {
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.itemPainter = itemPainter;
        this.facesManager = facesManager;
        this.knowledgeManager = knowledgeManager;
        this.view = view;
    }

    public GUIElement newItem(int index) {
        return new GUIItemKnowledgeType(tooltipManager, elementListener, name + index, itemPainter, index, facesManager, knowledgeManager, view);
    }

    public GUIItemItem newTemplateItem(int cellHeight) {
        final GUIItemItem result = new GUIItemKnowledgeType(tooltipManager, elementListener, name + "_template", itemPainter, -1, facesManager, knowledgeManager, view);
        //noinspection SuspiciousNameCombination
        result.setSize(cellHeight, cellHeight);
        return result;
    }
}

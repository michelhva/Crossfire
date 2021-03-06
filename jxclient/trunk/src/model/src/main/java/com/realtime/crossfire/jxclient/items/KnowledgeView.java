package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeItem;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeListener;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Displays knowledge items the player knows.
 * @author Nicolas Weeger
 */
public class KnowledgeView extends AbstractItemView {

    @NotNull
    private final KnowledgeManager knowledgeManager;

    @NotNull
    private final FacesManager facesManager;

    public KnowledgeView(@NotNull final FacesManager facesManager, @NotNull final KnowledgeManager knowledgeManager) {
        this.facesManager = facesManager;
        this.knowledgeManager = knowledgeManager;
        knowledgeManager.addKnowledgeListener(new KnowledgeListener() {
            @Override
            public void typeAdded(final int index) {
            }

            @Override
            public void knowledgeAdded(final int index) {
                addModifiedRange(0, knowledgeManager.getKnowledgeCount());
            }
        });
        facesManager.addFacesManagerListener(face -> addModifiedRange(0, knowledgeManager.getKnowledgeCount()));
    }

    @Override
    public int getSize() {
        return knowledgeManager.getKnowledgeCount();
    }

    @Nullable
    @Override
    public CfItem getItem(final int index) {
        final KnowledgeItem item = knowledgeManager.getKnowledge(index);
        if (item == null) {
            return null;
        }
        final Face face = facesManager.getFace(item.getFaceNum());
        return new CfItem(0, 0, 0, 0, face, item.getKnowledgeTitle(), item.getKnowledgeTitle(), 0, 0, 0, 0);
    }

}

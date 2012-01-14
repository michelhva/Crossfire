package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author nicolas
 */
public class KnowledgeTypeView extends AbstractItemView {

    @NotNull
    private final KnowledgeManager knowledgeManager;

    @NotNull
    private final FacesManager facesManager;

    public KnowledgeTypeView(@NotNull final FacesManager facesManager, @NotNull final KnowledgeManager knowledgeManager) {
        this.facesManager = facesManager;
        this.knowledgeManager = knowledgeManager;
        knowledgeManager.addKnowledgeListener(new KnowledgeManager.KnowledgeListener() {
            @Override
            public void typeAdded(final int index) {
                addModifiedRange(0, knowledgeManager.getTypes());
            }

            @Override
            public void knowledgeAdded(final int index) {
            }
        });
        facesManager.addFacesManagerListener(new FacesManagerListener() {
            @Override
            public void faceUpdated(@NotNull final Face face) {
                addModifiedRange(0, knowledgeManager.getTypes());
            }
        });

    }

    @Override
    public int getSize() {
        return knowledgeManager.getTypes();
    }

    @Nullable
    @Override
    public CfItem getItem(final int index) {
        final Face face = facesManager.getFace(knowledgeManager.getTypeFace(index));
        return new CfItem(0, 0, 0, 0, face, knowledgeManager.getTypeName(index), knowledgeManager.getTypeName(index), 0, 0, 0, 0);
    }
}

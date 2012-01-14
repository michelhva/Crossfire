package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeManager;
import java.awt.Dimension;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author nicolas
 */
public class GUIItemKnowledgeType extends GUIItemItem {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The object used for synchronization on {@link #index}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The {@link FacesManager} for looking up faces.
     */
    @NotNull
    private final FacesManager facesManager;

    @NotNull
    private final KnowledgeManager knowledgeManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The currently selected spell or <code>-1</code> if none is selected.
     * Corresponds to {@link #skill}.
     */
    private int index = -1;

    /**
     * Whether this element is selected in its {@link GUISpellSkillList}.
     */
    private boolean selected;
    private int currentFace = -1;
    @NotNull
    private final ItemView view;

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener() {

        @Override
        public void faceUpdated(@NotNull final Face face) {
            if (currentFace == face.getFaceNum()) {
                setChanged();
            }
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param itemPainter the item painter for painting the icon
     * @param defaultIndex the default scroll index
     * @param facesManager the faces manager for looking up faces
     * @param spellsManager the spells manager instance to watch
     * @param spellsView the spells view to use
     * @param defaultSkillIcon the default icon to use the skills if not defined
     */
    public GUIItemKnowledgeType(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final ItemPainter itemPainter, final int defaultIndex, @NotNull final FacesManager facesManager, @NotNull final KnowledgeManager knowledgeManager, @NotNull final ItemView view) {
        super(tooltipManager, elementListener, name, itemPainter, facesManager);
        this.defaultIndex = defaultIndex;
        this.facesManager = facesManager;
        this.knowledgeManager = knowledgeManager;
        this.view = view;
        setIndex(defaultIndex);

        knowledgeManager.addKnowledgeListener(new KnowledgeManager.KnowledgeListener() {
            public void typeAdded(int index) {
               if (GUIItemKnowledgeType.this.index >= index) {
                setSkill();
                }
            }

            public void knowledgeAdded(int index) {
            }
        });
        facesManager.addFacesManagerListener(facesManagerListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScroll(final int distance) {
        if (distance < 0) {
            return index >= -distance;
        } else if (distance > 0) {
            return index+distance < knowledgeManager.getTypes();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scroll(final int distance) {
        setIndex(index+distance);
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetScroll() {
        setIndex(defaultIndex);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getMinimumSize() {
        return getMinimumSizeInt();
    }

    /**
     * Returns the minimal size to display this component.
     * @return the minimal size
     */
    @NotNull
    private static Dimension getMinimumSizeInt() {
        return new Dimension(32, 32);
    }

    /**
     * Sets the currently selected {@link Skill}.
     */
    private void setSkill() {
        currentFace = knowledgeManager.getTypeFace(index);
        setChanged();
/*
        setTooltipText(newSkill == null ? null : newSkill.toString());*/
    }

    /**
     * Sets the {@link #index} of the currently selected {@link #skill}. Updates
     * the currently selected spell.
     * @param index the index to set
     */
    private void setIndex(final int index) {
        if (this.index == index) {
            return;
        }
        this.index = index;

        setSkill();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected Image getFace(@NotNull final CfItem item) {
 //      if (item.getFace() == null) {
    //        return defaultSkillIcon.getOriginalImageIcon().getImage();
      //  }
        return facesManager.getOriginalImageIcon(item.getFace().getFaceNum(), null).getImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelected(final boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSelected() {
        return selected || isActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        synchronized (sync) {
            return index;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIndexNoListeners(final int index) {
        synchronized (sync) {
            this.index = index;
        }

        setItemNoListeners(view.getItem(this.index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button1Clicked(int modifiers) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button2Clicked(int modifiers) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button3Clicked(int modifiers) {
    }
}

package com.realtime.crossfire.jxclient.knowledge;

import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all knowledge the player knows.
 * @author Nicolas Weeger
 */
public class KnowledgeManager {

    public interface KnowledgeListener extends EventListener {

        void typeAdded(int index);

        void knowledgeAdded(int index);
    }

    /**
     * Compare two knowledge items.
     */
    private final Comparator<KnowledgeItem> knowledgeComparator = new Comparator<KnowledgeItem>() {

        @Override
        public int compare(final KnowledgeItem o1, final KnowledgeItem o2) {
            return o1.getKnowledgeTitle().compareTo(o2.getKnowledgeTitle());
        }
    };

    /**
     * The {@link SpellsManagerListener SpellsManagerListeners} to notify about
     * changes.
     */
    @NotNull
    private final EventListenerList2<KnowledgeListener> listeners = new EventListenerList2<KnowledgeListener>(KnowledgeListener.class);

    private final List<String> types = new ArrayList<String>();

    private final List<String> names = new ArrayList<String>();

    private final List<Integer> faces = new ArrayList<Integer>();

    private final List<Boolean> attempt = new ArrayList<Boolean>();

    private final Collection<KnowledgeItem> items = new ArrayList<KnowledgeItem>();

    private final List<KnowledgeItem> filteredItems = new ArrayList<KnowledgeItem>();

    private String typeFilter = "";

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        @Override
        public void start() {
            // ignore
        }

        @Override
        public void metaserver() {
            // ignore
        }

        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        @Override
        public void connecting(@NotNull final String serverInfo) {
            types.clear();
            faces.clear();
            names.clear();
            attempt.clear();
            items.clear();
            filteredItems.clear();
        }

        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        @Override
        public void connected() {
            // ignore
        }

        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }

    };

    public KnowledgeManager(@NotNull final GuiStateManager guiStateManager) {
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /**
     * A character name was sent to the server.
     */
    public void selectCharacter() {
        items.clear();
        filteredItems.clear();
    }

    /**
     * Adds a {@link KnowledgeListener} to notify about changes.
     * @param listener the listener to add
     */
    public void addKnowledgeListener(@NotNull final KnowledgeListener listener) {
        listeners.add(listener);
    }

    public void addKnowledgeType(final String type, final String name, final int face, final boolean canAttempt) {
        types.add(type);
        if (type.equals("")) {
            names.add("All types");
        } else {
            names.add(name);
        }
        faces.add(face);
        attempt.add(canAttempt);
        for (final KnowledgeListener listener : listeners.getListeners()) {
            listener.typeAdded(0);
        }
    }

    public void clearTypes() {
        types.clear();
        names.clear();
        faces.clear();
        attempt.clear();
    }

    public int getTypes() {
        return types.size();
    }

    public String getTypeName(final int index) {
        if (index < 0 || index >= faces.size()) {
            return "";
        }
        return names.get(index);
    }

    public int getTypeFace(final int index) {
        if (index < 0 || index >= faces.size()) {
            return 0;
        }
        return faces.get(index);
    }

    public boolean canAttemptType(@NotNull final String type) {
        for (int i = 0; i < types.size(); i++) {
            if (type.equals(types.get(i))) {
                return attempt.get(i);
            }
        }

        return false;
    }

    public void addKnowledge(final int index, @NotNull final String type, @NotNull final String title, final int face) {
        items.add(new KnowledgeItem(index, type, title, face));
        filterKnowledge();
        for (final KnowledgeListener listener : listeners.getListeners()) {
            listener.knowledgeAdded(0);
        }
    }

    public int getKnowledgeCount() {
        return filteredItems.size();
    }

    @Nullable
    public KnowledgeItem getKnowledge(final int index) {
        if (index < 0 || index >= filteredItems.size()) {
            return null;
        }
        return filteredItems.get(index);
    }

    public void filterType(final int index) {
        if (index < 0 || index >= types.size()) {
            return;
        }

        final String filter = types.get(index);

        if (typeFilter.equals(filter)) {
            return;
        }

        typeFilter = filter;
        filterKnowledge();

        for (final KnowledgeListener listener : listeners.getListeners()) {
            listener.knowledgeAdded(0);
        }
    }

    protected void filterKnowledge() {
        filteredItems.clear();
        for (final KnowledgeItem item : items) {
            if ("".equals(typeFilter) || item.getType().equals(typeFilter)) {
                filteredItems.add(item);
            }
        }
        Collections.sort(filteredItems, knowledgeComparator);
    }
}

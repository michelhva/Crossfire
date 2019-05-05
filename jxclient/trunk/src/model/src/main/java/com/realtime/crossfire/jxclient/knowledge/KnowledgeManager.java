package com.realtime.crossfire.jxclient.knowledge;

import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all knowledge the player knows.
 * @author Nicolas Weeger
 */
public class KnowledgeManager {

    /**
     * Compare two knowledge items.
     */
    @NotNull
    private static final Comparator<KnowledgeItem> KNOWLEDGE_COMPARATOR = (o1, o2) -> o1.getKnowledgeTitle().compareTo(o2.getKnowledgeTitle());

    /**
     * The {@link KnowledgeListener KnowledgeListeners} to notify about
     * changes.
     */
    @NotNull
    private final EventListenerList2<KnowledgeListener> listeners = new EventListenerList2<>();

    @NotNull
    private final List<String> types = new ArrayList<>();

    @NotNull
    private final List<String> names = new ArrayList<>();

    @NotNull
    private final List<Integer> faces = new ArrayList<>();

    @NotNull
    private final List<Boolean> attempt = new ArrayList<>();

    @NotNull
    private final Collection<KnowledgeItem> items = new ArrayList<>();

    @NotNull
    private final List<KnowledgeItem> filteredItems = new ArrayList<>();

    @NotNull
    private String typeFilter = "";

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    @SuppressWarnings("FieldCanBeLocal")
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

    public void addKnowledgeType(@NotNull final String type, @NotNull final String name, final int face, final boolean canAttempt) {
        types.add(type);
        if (type.isEmpty()) {
            names.add("All types");
        } else {
            names.add(name);
        }
        faces.add(face);
        attempt.add(canAttempt);
        for (KnowledgeListener listener : listeners) {
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

    @NotNull
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
        for (KnowledgeListener listener : listeners) {
            listener.knowledgeAdded(0);
        }
    }

    public int getKnowledgeCount() {
        return filteredItems.size();
    }

    /**
     * Returns a {@link KnowledgeItem} instance by index.
     * @param index the index
     * @return the item or {@code null} if the index is invalid
     */
    @Nullable
    public KnowledgeItem getKnowledge(final int index) {
        try {
            return filteredItems.get(index);
        } catch (final IndexOutOfBoundsException ignored) {
            return null;
        }
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

        for (KnowledgeListener listener : listeners) {
            listener.knowledgeAdded(0);
        }
    }

    private void filterKnowledge() {
        filteredItems.clear();
        filteredItems.addAll(items.stream().filter(item -> typeFilter.isEmpty() || item.getType().equals(typeFilter)).collect(Collectors.toList()));
        Collections.sort(filteredItems, KNOWLEDGE_COMPARATOR);
    }

}

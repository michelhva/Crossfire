package com.realtime.crossfire.jxclient.gui.label;

import java.util.EventListener;

/**
 * Interface for listeners interested in {@link NewCharModel} related changes.
 */
public interface NewCharModelListener extends EventListener {

    /**
     * Called if any attribute has changed.
     */
    void changed();

}

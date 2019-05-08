package com.realtime.crossfire.jxclient.faces;

import java.nio.ByteBuffer;
import java.util.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for classes interested in faces received from the Crossfire
 * server.
 */
public interface AskfaceFaceQueueListener extends EventListener {

    /**
     * Called for each received face.
     * @param faceNum the face ID
     * @param faceSetNum the face set
     * @param packet the face data
     */
    void faceReceived(int faceNum, int faceSetNum, @NotNull ByteBuffer packet);

}

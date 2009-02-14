package com.realtime.crossfire.jxclient.skin.events;

/**
 * Interface for events attached to skins.
 * @author Andreas Kirschbaum
 */
public interface SkinEvent
{
    /**
     * Will be called when the skin is disposed. Should release resources.
     */
    void dispose();
}

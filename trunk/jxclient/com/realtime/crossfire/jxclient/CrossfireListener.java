package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;

public interface CrossfireListener
{
    public void CommandImageReceived(CrossfireCommandImageEvent evt);
    public void CommandGoodbyeReceived(CrossfireCommandGoodbyeEvent evt);
    public void CommandAddmeSuccessReceived(CrossfireCommandAddmeSuccessEvent evt);
    public void CommandDelitemReceived(CrossfireCommandDelitemEvent evt);
    public void CommandFaceReceived(CrossfireCommandFaceEvent evt);
    public void CommandItemReceived(CrossfireCommandItemEvent evt);
    public void CommandItem1Received(CrossfireCommandItem1Event evt);
    public void CommandMap1Received(CrossfireCommandMap1Event evt);
    public void CommandNewmapReceived(CrossfireCommandNewmapEvent evt);
    public void CommandPlayerReceived(CrossfireCommandPlayerEvent evt);
    public void CommandStatsReceived(CrossfireCommandStatsEvent evt);
    public void CommandUpditemReceived(CrossfireCommandUpditemEvent evt);
    public void CommandMapscrollReceived(CrossfireCommandMapscrollEvent evt);
    public void CommandDelinvReceived(CrossfireCommandDelinvEvent evt);
    public void CommandDrawinfoReceived(CrossfireCommandDrawinfoEvent evt);
    public void CommandDrawextinfoReceived(CrossfireCommandDrawinfoEvent evt);
    public void CommandAnimReceived(CrossfireCommandAnimEvent evt);
    public void CommandVersionReceived(CrossfireCommandVersionEvent evt);
    public void CommandMagicmapReceived(CrossfireCommandMagicmapEvent evt);
    public void CommandAddmeFailedReceived(CrossfireCommandAddmeFailedEvent evt);
    public void CommandSetupReceived(CrossfireCommandSetupEvent evt);
    public void CommandQueryReceived(CrossfireCommandQueryEvent evt);
}
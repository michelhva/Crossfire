package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.io.*;

public interface JXCSkin
{
    public java.util.List<GUIElement> getDialogKeyBind(ServerConnection s, JXCWindow p)
            throws JXCSkinException;
    public java.util.List<GUIElement> getDialogQuery(ServerConnection s, JXCWindow p)
            throws JXCSkinException;
    public java.util.List<GUIElement> getDialogBook(ServerConnection s, JXCWindow p, int booknr)
            throws JXCSkinException;
    public java.util.List<GUIElement> getMainInterface(ServerConnection s, JXCWindow p)
            throws JXCSkinException;
    public java.util.List<GUIElement> getMetaInterface(ServerConnection s, JXCWindow p)
            throws JXCSkinException;
    public java.util.List<GUIElement> getStartInterface(ServerConnection s, JXCWindow p)
            throws JXCSkinException;
}

//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.MouseInputListener;
import javax.swing.JFrame;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class JXCWindow extends JFrame implements KeyListener, MouseInputListener,
                                                 CrossfireDrawextinfoListener,
                                                 CrossfireQueryListener
{
    /** The serial version UID. */
    private static final long serialVersionUID = 1;

    /** TODO: Remove when more options are implemented in the start screen gui. */
    private static final boolean DISABLE_START_GUI = true;

    public final static int GUI_START      = 0;
    public final static int GUI_METASERVER = 1;
    public final static int GUI_MAIN       = 2;

    public final static int DLG_NONE       = 0;
    public final static int DLG_BOOK       = 1;
    public final static int DLG_QUERY      = 2;
    public final static int DLG_KEYBIND    = 3;
    public final static int DLG_CARD       = 4;
    public final static int DLG_PAPER      = 5;
    public final static int DLG_SIGN       = 6;
    public final static int DLG_MONUMENT   = 7;
    public final static int DLG_SCRIPTED_DIALOG = 8;
    public final static int DLG_MOTD       = 9;

    private long            framecount = 0;
    private long            starttime = 0;
    private int             mycurrentgui = GUI_START;
    private int             mydialogstatus = DLG_NONE;
    private CrossfireServerConnection myserver = null;
    private GUIElement      myactive_element = null;
    private final String    semaphore_drawing = "semaphore_drawing";
    private final String    mydialogstatus_sem = "mydialogstatus_sem";
    private Spell           mycurrentspell = null;
    private static final SpellBeltItem[] myspellbelt = new SpellBeltItem[12];

    private List<GUIElement> mydialog_query   = new ArrayList<GUIElement>();
    private List<GUIElement> mydialog_book    = new ArrayList<GUIElement>();
    private List<GUIElement> mydialog_keybind = new ArrayList<GUIElement>();
    private List<GUIElement> mydialog_card    = new ArrayList<GUIElement>();
    private List<GUIElement> mydialog_paper   = new ArrayList<GUIElement>();
    private List<GUIElement> mydialog_sign    = new ArrayList<GUIElement>();
    private List<GUIElement> mydialog_monument= new ArrayList<GUIElement>();
    private List<GUIElement> mydialog_scripted_dialog = new ArrayList<GUIElement>();
    private List<GUIElement> mydialog_motd    = new ArrayList<GUIElement>();

    private JXCSkin myskin = null;

    private final KeyBindings keyBindings = new KeyBindings();
    private final boolean[] key_shift = new boolean[]{false, false, false, false};
    private List<GUICommand> mycurrentkeybinding = null;

    private final List<SpellListener> myspelllisteners = new ArrayList<SpellListener>();

    public final static int KEY_SHIFT_SHIFT = 0;
    public final static int KEY_SHIFT_CTRL  = 1;
    public final static int KEY_SHIFT_ALT   = 2;
    public final static int KEY_SHIFT_ALTGR = 3;
    private boolean is_run_active = false;
    private boolean is_fire_active = false;

    private final JXCWindowRenderer jxcWindowRenderer = new JXCWindowRenderer(this);

    public boolean checkRun()
    {
        return is_run_active;
    }
    public boolean checkFire()
    {
        return is_fire_active;
    }
    public void terminateScript(final ScriptProcess sp)
    {
        System.out.println("Script "+sp+" terminated");
        myserver.removeScriptMonitor((CrossfireScriptMonitorListener)sp);
    }
    public void runScript(final String cmdline)
    {
        System.out.println("Script to run: "+cmdline);
        try
        {
            final ScriptProcess sp = new ScriptProcess(cmdline, this);
        }
        catch (final IOException e)
        {
            System.out.println("Unable to run script: "+cmdline);
        }
    }
    public void addSpellListener(final SpellListener s)
    {
        myspelllisteners.add(s);
    }
    public void removeSpellListener(final SpellListener s)
    {
        myspelllisteners.remove(s);
    }
    public void setCurrentSpell(final Spell s)
    {
        mycurrentspell = s;
        final SpellChangedEvent evt = new SpellChangedEvent(this, s);
        for (final SpellListener sl : myspelllisteners)
        {
            sl.SpellChanged(evt);
        }
    }
    public Spell getCurrentSpell()
    {
        return mycurrentspell;
    }
    public void createKeyBinding(final List<GUICommand> cmdlist)
    {
        mycurrentkeybinding = cmdlist;
        setDialogStatus(DLG_KEYBIND);
    }
    public void removeKeyBinding()
    {
        mycurrentkeybinding = null;
        setDialogStatus(DLG_KEYBIND);
    }
    private void loadSpellBelt(final String filename)
    {
        try
        {
            final FileInputStream fis = new FileInputStream(filename);
            try
            {
                final ObjectInputStream ois = new ObjectInputStream(fis);
                try
                {
                    for(int i=0;i<12;i++)
                    {
                        myspellbelt[i] = null;
                        int sp = ois.readInt();
                        int st = ois.readInt();
                        if (sp > -1)
                            myspellbelt[i] = new SpellBeltItem(sp, st);
                    }
                }
                finally
                {
                    ois.close();
                }
            }
            finally
            {
                fis.close();
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
    private void saveSpellBelt(String filename)
    {
        try
        {
            final FileOutputStream fos = new FileOutputStream(filename);
            try
            {
                final ObjectOutputStream oos = new ObjectOutputStream(fos);
                try
                {
                    for(int i=0; i<12;i++)
                    {
                        if (myspellbelt[i]==null)
                        {
                            oos.writeInt(-1);
                            oos.writeInt(-1);
                        }
                        else
                        {
                            oos.writeInt(myspellbelt[i].getSpellIndex());
                            oos.writeInt(myspellbelt[i].getStatus());
                        }
                    }
                }
                finally
                {
                    oos.close();
                }
            }
            finally
            {
                fos.close();
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
    public static SpellBeltItem[] getSpellBelt()
    {
        return myspellbelt;
    }
    public boolean getKeyShift(final int keyid)
    {
        return key_shift[keyid];
    }
    public void setKeyShift(final int keyid, final boolean state)
    {
        key_shift[keyid] = state;
    }
    public void setDialogStatus(final int nv)
    {
        synchronized(mydialogstatus_sem)
        {
            mydialogstatus = nv;
            switch (nv)
            {
                case DLG_NONE:
                    jxcWindowRenderer.setCurrentDialog(null);
                    CfMap.invalidate();
                    break;
                case DLG_BOOK:
                    jxcWindowRenderer.setCurrentDialog(mydialog_book);
                    break;
                case DLG_QUERY:
                    jxcWindowRenderer.setCurrentDialog(mydialog_query);
                    break;
                case DLG_KEYBIND:
                    jxcWindowRenderer.setCurrentDialog(mydialog_keybind);
                    break;
                case DLG_CARD:
                    jxcWindowRenderer.setCurrentDialog(mydialog_card);
                    break;
                case DLG_PAPER:
                    jxcWindowRenderer.setCurrentDialog(mydialog_paper);
                    break;
                case DLG_SIGN:
                    jxcWindowRenderer.setCurrentDialog(mydialog_sign);
                    break;
                case DLG_MONUMENT:
                    jxcWindowRenderer.setCurrentDialog(mydialog_monument);
                    break;
                case DLG_SCRIPTED_DIALOG:
                    jxcWindowRenderer.setCurrentDialog(mydialog_scripted_dialog);
                    break;
                case DLG_MOTD:
                    jxcWindowRenderer.setCurrentDialog(mydialog_motd);
                    break;
            }
            if (nv != DLG_NONE)
            {
                activateFirstTextArea(jxcWindowRenderer.getCurrentDialog());
            }
        }
    }
    private void activateFirstTextArea(final List<GUIElement> list)
    {
        for (final GUIElement e : list)
        {
            if (e instanceof com.realtime.crossfire.jxclient.GUIText)
            {
                if (e.isVisible())
                {
                    deactivateCurrentElement();
                    e.setActive(true);
                    myactive_element = e;
                }
            }
        }
    }
    private void initRendering()
    {
        jxcWindowRenderer.initRendering();
        framecount = 0;
        keyBindings.loadKeyBindings("keybindings.data");
        loadSpellBelt("spellbelt.data");
        starttime = System.nanoTime();
    }
    public void endRendering()
    {
        final long endtime = System.nanoTime();
        final long totaltime = endtime - starttime;
        System.out.println(framecount+" frames in "+totaltime/1000000 +
                " ms - "+(framecount*1000/(totaltime/1000000))+" FPS");
        jxcWindowRenderer.endRendering();
        keyBindings.saveKeyBindings("keybindings.data");
        saveSpellBelt("spellbelt.data");
        System.exit(0);
    }
    private void initGUI(final int id)
    {
        switch (id)
        {
            case GUI_START:
                if (DISABLE_START_GUI)
                {
                    endRendering();
                }
                else
                {
                    showGUIStart();
                }
                break;
            case GUI_METASERVER:
                showGUIMeta();
                break;
            case GUI_MAIN:
                showGUIMain();
                break;
        }
    }
    public void changeGUI(final int id)
    {
        initGUI(id);
    }
    public void init(final int w, final int h, final int b, final int f, final String skinclass)
    {
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        jxcWindowRenderer.init(w, h, b, f);
        try
        {
            myskin = (JXCSkin)(Class.forName(skinclass).newInstance());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        try
        {
            initRendering();
            initGUI(DISABLE_START_GUI ? GUI_METASERVER : GUI_START);
            for(;;)
            {
                synchronized(semaphore_drawing)
                {
                    jxcWindowRenderer.redrawGUI();
                }
                framecount++;
                Thread.sleep(10);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void connect(final String hostname, final int port)
    {
        myserver = new CrossfireServerConnection(hostname, port);
        myserver.addCrossfireDrawextinfoListener(this);
        myserver.addCrossfireQueryListener(this);
        initGUI(GUI_MAIN);
        myserver.connect();
        Faces.setCrossfireServerConnection(myserver);
    }

    /**
     * Send a "ncom" command to the server.
     *
     * @param repeat the repeat count
     *
     * @param command the command
     *
     * @return the packet id
     */
    public int sendNcom(final int repeat, final String command)
    {
        try
        {
            return myserver.sendNcom(repeat, command);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            endRendering();
            return 0;
        }
    }

    public CrossfireServerConnection getCrossfireServerConnection()
    {
        return myserver;
    }
    private void launchSpellFromBelt(final int idx)
    {
        final GUICommand fcmd = new GUICommand(null, GUICommand.CMD_GUI_SPELLBELT, new GUICommand.SpellBeltParameter(this, myspellbelt[idx]));
        fcmd.execute();
    }
    private void handleKeyPress(final KeyEvent e)
    {
        if((myserver == null)||(myserver.getStatus() != CrossfireServerConnection.STATUS_PLAYING))
            return;
        for (final KeyBinding kb : keyBindings)
        {
            if ((kb.getKeyCode()==e.getKeyCode())&&(kb.getKeyModifiers()==e.getModifiers()))
            {
                for (final GUICommand cmd : kb.getCommands())
                {
                    cmd.execute();
                }
                return;
            }
        }
        switch(e.getKeyCode())
        {
            case KeyEvent.VK_F1:
                launchSpellFromBelt(0);
                break;
            case KeyEvent.VK_F2:
                launchSpellFromBelt(1);
                break;
            case KeyEvent.VK_F3:
                launchSpellFromBelt(2);
                break;
            case KeyEvent.VK_F4:
                launchSpellFromBelt(3);
                break;
            case KeyEvent.VK_F5:
                launchSpellFromBelt(4);
                break;
            case KeyEvent.VK_F6:
                launchSpellFromBelt(5);
                break;
            case KeyEvent.VK_F7:
                launchSpellFromBelt(6);
                break;
            case KeyEvent.VK_F8:
                launchSpellFromBelt(7);
                break;
            case KeyEvent.VK_F9:
                launchSpellFromBelt(8);
                break;
            case KeyEvent.VK_F10:
                launchSpellFromBelt(9);
                break;
            case KeyEvent.VK_F11:
                launchSpellFromBelt(10);
                break;
            case KeyEvent.VK_F12:
                launchSpellFromBelt(11);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_NUMPAD8:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    sendNcom(0, "run 1");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    sendNcom(0, "fire 1");
                    is_fire_active = true;
                }
                else
                    sendNcom(0, "north");
                break;
            case KeyEvent.VK_NUMPAD9:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    sendNcom(0, "run 2");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    sendNcom(0, "fire 2");
                    is_fire_active = true;
                }
                else
                    sendNcom(0, "northeast");
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_NUMPAD6:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    sendNcom(0, "run 3");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    sendNcom(0, "fire 3");
                    is_fire_active = true;
                }
                else
                    sendNcom(0, "east");
                break;
            case KeyEvent.VK_NUMPAD3:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    sendNcom(0, "run 4");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    sendNcom(0, "fire 4");
                    is_fire_active = true;
                }
                else
                    sendNcom(0, "southeast");
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_NUMPAD2:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    sendNcom(0, "run 5");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    sendNcom(0, "fire 5");
                    is_fire_active = true;
                }
                else
                    sendNcom(0, "south");
                break;
            case KeyEvent.VK_NUMPAD1:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    sendNcom(0, "run 6");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    sendNcom(0, "fire 6");
                    is_fire_active = true;
                }
                else
                    sendNcom(0, "southwest");
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_NUMPAD4:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    sendNcom(0, "run 7");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    sendNcom(0, "fire 7");
                    is_fire_active = true;
                }
                else
                    sendNcom(0, "west");
                break;
            case KeyEvent.VK_NUMPAD7:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    sendNcom(0, "run 8");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    sendNcom(0, "fire 8");
                    is_fire_active = true;
                }
                else
                    sendNcom(0, "northwest");
                break;
            case KeyEvent.VK_A:
                sendNcom(0, "apply");
                break;
            case KeyEvent.VK_S:
                sendNcom(0, "save");
                break;
            case KeyEvent.VK_W:
                sendNcom(0, "who");
                break;
            case KeyEvent.VK_QUOTE:
                activateFirstTextArea(jxcWindowRenderer.getCurrentGui());
                break;
            case KeyEvent.VK_QUOTEDBL:
                activateFirstTextArea(jxcWindowRenderer.getCurrentGui());
                if (myactive_element != null)
                {
                    ((GUIText)myactive_element).setText("say ");
                }
                break;
            default:
                break;
        }
    }
    public void keyPressed(final KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_ALT:
                setKeyShift(KEY_SHIFT_ALT, true);
                break;
            case KeyEvent.VK_ALT_GRAPH:
                setKeyShift(KEY_SHIFT_ALTGR, true);
                break;
            case KeyEvent.VK_SHIFT:
                setKeyShift(KEY_SHIFT_SHIFT, true);
                break;
            case KeyEvent.VK_CONTROL:
                setKeyShift(KEY_SHIFT_CTRL, true);
                break;
            default:
                if (mydialogstatus == DLG_KEYBIND)
                {
                    if (mycurrentkeybinding!=null)
                    {
                        keyBindings.addKeyBinding(e.getKeyCode(), e.getModifiers(), mycurrentkeybinding);
                    }
                    else
                    {
                        keyBindings.deleteKeyBinding(e.getKeyCode(), e.getModifiers());
                        keyBindings.addKeyBinding(e.getKeyCode(), e.getModifiers(), mycurrentkeybinding);
                    }
                    mycurrentkeybinding = null;
                    setDialogStatus(DLG_NONE);
                }
                else if (myactive_element != null)
                {
                    if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
                    {
                        myactive_element.setActive(false);
                        myactive_element = null;
                    }
                    else if (myactive_element instanceof KeyListener)
                    {
                        ((KeyListener)myactive_element).keyPressed(e);
                        if (myactive_element.isActive()==false)
                            myactive_element = null;
                    }
                    else
                    {
                        handleKeyPress(e);
                    }
                }
                else if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
                {
                    endRendering();
                }
                else
                {
                    handleKeyPress(e);
                }
                break;
        }
    }
    public void keyReleased(final KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_ALT:
                setKeyShift(KEY_SHIFT_ALT, false);
                break;
            case KeyEvent.VK_ALT_GRAPH:
                setKeyShift(KEY_SHIFT_ALTGR, false);
                break;
            case KeyEvent.VK_SHIFT:
                setKeyShift(KEY_SHIFT_SHIFT, false);
                if (is_fire_active == true)
                {
                    sendNcom(0, "fire_stop");
                    is_fire_active = false;
                }
                break;
            case KeyEvent.VK_CONTROL:
                setKeyShift(KEY_SHIFT_CTRL, false);
                if (is_run_active == true)
                {
                    sendNcom(0, "run_stop");
                    is_run_active = false;
                }
                break;
        }
    }
    public void keyTyped(final KeyEvent e)
    {
        /*if (myactive_element != null)
            if (myactive_element instanceof KeyListener)
        ((KeyListener)myactive_element).keyTyped(e);*/
    }
    public void mouseClicked(final MouseEvent e)
    {
    }
    public void mouseEntered(final MouseEvent e)
    {
    }
    public void mouseExited(final MouseEvent e)
    {
    }
    public void mousePressed(final MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            GUIElement elected = manageMouseEvents(jxcWindowRenderer.getCurrentGui(), e);
            if (elected == null) elected = jxcWindowRenderer.getCurrentGui().get(0);

            GUIElement myother = null;

            final List<GUIElement> currentDialog = jxcWindowRenderer.getCurrentDialog();
            if (currentDialog != null)
            {
                myother = manageMouseEvents(currentDialog, e);
                if (myother != null)
                    elected = myother;
            }
            e.translatePoint(-elected.x, -elected.y);
            deactivateCurrentElement();
            elected.mousePressed(e);
            if (elected.isActive())
                myactive_element = elected;
        }
    }
    public void mouseReleased(final MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            GUIElement elected = manageMouseEvents(jxcWindowRenderer.getCurrentGui(), e);
            if (elected == null) elected = jxcWindowRenderer.getCurrentGui().get(0);

            GUIElement myother = null;

            final List<GUIElement> currentDialog = jxcWindowRenderer.getCurrentDialog();
            if (currentDialog != null)
            {
                myother = manageMouseEvents(currentDialog, e);
                if (myother != null)
                    elected = myother;
            }
            e.translatePoint(-elected.x, -elected.y);
            if (myactive_element!=elected)
                deactivateCurrentElement();
            elected.mouseReleased(e);
        }
    }
    private GUIElement manageMouseEvents(final List<GUIElement> guilist, final MouseEvent e)
    {
        final int x = e.getX();
        final int y = e.getY();
        final int b = e.getButton();
        GUIElement elected = null;
        switch(b)
        {
            case MouseEvent.BUTTON1:
                for (final GUIElement element : guilist)
                {
                    if (element.isVisible())
                        if ((x>=element.getX())&&(x<=element.getX()+element.getWidth()))
                            if ((y>=element.getY())&&(y<=element.getY()+element.getHeight()))
                                elected = element;
                 }
                 break;
            case MouseEvent.BUTTON2:
                for (final GUIElement element : guilist)
                {
                    if (element.isVisible())
                        if ((x>=element.getX())&&(x<=element.getX()+element.getWidth()))
                            if ((y>=element.getY())&&(y<=element.getY()+element.getHeight()))
                                elected = element;
                }
                break;
            case MouseEvent.BUTTON3:
                for (final GUIElement element : guilist)
                {
                    if (element.isVisible())
                        if ((x>=element.getX())&&(x<=element.getX()+element.getWidth()))
                            if ((y>=element.getY())&&(y<=element.getY()+element.getHeight()))
                                elected = element;
                }
                break;
        }
        return elected;
    }

    public void deactivateCurrentElement()
    {
        if (myactive_element != null)
            myactive_element.setActive(false);
        myactive_element = null;
    }
    public void mouseDragged(final MouseEvent e)
    {
    }
    public void mouseMoved(final MouseEvent e)
    {
    }

    public void CommandDrawextinfoReceived(final CrossfireCommandDrawextinfoEvent evt)
    {
        switch (evt.getType())
        {
            case 1: //Books
                //System.out.println("Message is:"+evt.getMessage());
                //System.out.println("Subtype is:"+evt.getSubType());
                setDialogStatus(DLG_BOOK);
                break;
            case 2: //Cards
                //System.out.println("Message is:"+evt.getMessage());
                //System.out.println("Subtype is:"+evt.getSubType());
                setDialogStatus(DLG_CARD);
                break;
            case 3: //Papers
                //System.out.println("Message is:"+evt.getMessage());
                //System.out.println("Subtype is:"+evt.getSubType());
                setDialogStatus(DLG_PAPER);
                break;
            case 4: //Signs
                //System.out.println("Message is:"+evt.getMessage());
                //System.out.println("Subtype is:"+evt.getSubType());
                setDialogStatus(DLG_SIGN);
                break;
            case 5: //Monuments
                //System.out.println("Message is:"+evt.getMessage());
                //System.out.println("Subtype is:"+evt.getSubType());
                setDialogStatus(DLG_MONUMENT);
                break;
            case 6: //Scripted Dialogs
                //System.out.println("Message is:"+evt.getMessage());
                //System.out.println("Subtype is:"+evt.getSubType());
                setDialogStatus(DLG_SCRIPTED_DIALOG);
                break;
            case 7: // Message of the Day
                //System.out.println("Message is:"+evt.getMessage());
                //System.out.println("Subtype is:"+evt.getSubType());
                //setDialogStatus(DLG_MOTD);
                /*
                 * We do not display a MOTD dialog, because it interferes with
                 * the query dialog that gets displayed just after it.
                */
                break;
            default: //Let's consider those as books for now, k ?
                System.out.println("Message is:"+evt.getMessage());
                System.out.println("Subtype is:"+evt.getSubType());
                setDialogStatus(DLG_BOOK);
                break;
        }
    }
    public void CommandQueryReceived(final CrossfireCommandQueryEvent evt)
    {
        setDialogStatus(DLG_QUERY);
    }
    private void clearGUI()
    {
        jxcWindowRenderer.clearGUI();
    }
    private void showGUIStart()
    {
        clearGUI();
        List<GUIElement> newGui;
        try
        {
            newGui = myskin.getStartInterface(myserver,this);
        }
        catch (final JXCSkinException e)
        {
            endRendering();
            newGui = null;
        }
        jxcWindowRenderer.setCurrentGui(newGui);
    }
    private void showGUIMeta()
    {
        clearGUI();
        List<GUIElement> newGui;
        try
        {
            newGui = myskin.getMetaInterface(myserver,this);
        }
        catch (final JXCSkinException e)
        {
            endRendering();
            newGui = null;
        }
        jxcWindowRenderer.setCurrentGui(newGui);
    }
    private void showGUIMain()
    {
        clearGUI();
        List<GUIElement> newGui;
        try
        {
            newGui = myskin.getMainInterface(myserver,this);
            mydialog_query   = myskin.getDialogQuery(myserver,this);
            mydialog_book    = myskin.getDialogBook(myserver, this, 1);
            mydialog_card    = myskin.getDialogBook(myserver, this, 2);
            mydialog_paper   = myskin.getDialogBook(myserver, this, 3);
            mydialog_sign    = myskin.getDialogBook(myserver, this, 4);
            mydialog_monument    = myskin.getDialogBook(myserver, this, 5);
            mydialog_scripted_dialog    = myskin.getDialogBook(myserver, this, 6);
            mydialog_motd    = myskin.getDialogBook(myserver, this, 7);

            mydialog_keybind = myskin.getDialogKeyBind(myserver, this);
        }
        catch (final JXCSkinException e)
        {
            endRendering();
            newGui = null;
        }
        jxcWindowRenderer.setCurrentGui(newGui);
    }

    /** {@inheritDoc} */
    public void paint(final Graphics g)
    {
        jxcWindowRenderer.repaint();
    }
}

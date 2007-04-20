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
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.io.*;

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

    private DisplayMode     oldDisplayMode=null;
    private long            framecount = 0;
    private long            starttime = 0;
    private BufferStrategy  mybufferstrategy;
    private int             mycurrentgui = GUI_START;
    private int             mydialogstatus = DLG_NONE;
    private ServerConnection myserver = null;
    private GUIElement      myactive_element = null;
    private java.util.List<GUIElement> mygui = new ArrayList<GUIElement>();
    private String          semaphore_drawing = new String("semaphore_drawing");;
    private String          mydialogstatus_sem = new String("mydialogstatus_sem");
    private DisplayMode     mymode = null;
    private Spell           mycurrentspell = null;
    private static SpellBeltItem[]         myspellbelt = new SpellBeltItem[12];
    private boolean isfullscreen = false;

    private java.util.List<GUIElement> mydialog_query   = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_book    = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_keybind = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_card    = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_paper   = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_sign    = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_monument= new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_scripted_dialog = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_motd    = new ArrayList<GUIElement>();

    private java.util.List<GUIElement> mydialog_current = null;

    private JXCSkin myskin = null;

    private java.util.List<KeyBinding> mykeybindings = new ArrayList<KeyBinding>();
    private boolean[] key_shift = new boolean[]{false, false, false, false};
    private java.util.List<GUICommand> mycurrentkeybinding = null;

    private java.util.List<SpellListener> myspelllisteners = new ArrayList<SpellListener>();

    public final static int KEY_SHIFT_SHIFT = 0;
    public final static int KEY_SHIFT_CTRL  = 1;
    public final static int KEY_SHIFT_ALT   = 2;
    public final static int KEY_SHIFT_ALTGR = 3;
    private boolean is_run_active = false;
    private boolean is_fire_active = false;

    public boolean checkRun()
    {
        return is_run_active;
    }
    public boolean checkFire()
    {
        return is_fire_active;
    }
    public void terminateScript(ScriptProcess sp)
    {
        System.out.println("Script "+sp+" terminated");
        myserver.removeScriptMonitor((CrossfireScriptMonitorListener)sp);
    }
    public void runScript(String cmdline)
    {
        System.out.println("Script to run: "+cmdline);
        try
        {
            ScriptProcess sp = new ScriptProcess(cmdline, this);
        }
        catch (IOException e)
        {
            System.out.println("Unable to run script: "+cmdline);
        }
    }
    public void addSpellListener(SpellListener s)
    {
        myspelllisteners.add(s);
    }
    public void removeSpellListener(SpellListener s)
    {
        myspelllisteners.remove(s);
    }
    public void setCurrentSpell(Spell s)
    {
        mycurrentspell = s;
        Iterator<SpellListener> it = myspelllisteners.iterator();
        SpellChangedEvent evt = new SpellChangedEvent(this, s);
        while (it.hasNext())
        {
            SpellListener sl = it.next();
            sl.SpellChanged(evt);
        }
    }
    public Spell getCurrentSpell()
    {
        return mycurrentspell;
    }
    public void addKeyBinding(int keycode, int keymod, java.util.List<GUICommand> cmdlist)
    {
        KeyBinding kb = new KeyBinding(keycode, keymod, cmdlist);
        KeyBinding ok;
        Iterator<KeyBinding> it = mykeybindings.iterator();
        KeyBinding elected = null;
        while(it.hasNext())
        {
            ok = it.next();
            if (ok.equals(kb))
            {
                elected = ok;
                continue;
            }
        }
        if (elected != null)
            mykeybindings.remove(elected);
        mykeybindings.add(kb);
    }
    public void createKeyBinding(java.util.List<GUICommand> cmdlist)
    {
        mycurrentkeybinding = cmdlist;
        setDialogStatus(DLG_KEYBIND);
    }
    public void removeKeyBinding()
    {
        mycurrentkeybinding = null;
        setDialogStatus(DLG_KEYBIND);
    }
    private void deleteKeyBinding(int keycode, int keymod)
    {
        Iterator<KeyBinding> it = mykeybindings.iterator();
        while(it.hasNext())
        {
            KeyBinding kb = it.next();
            if ((kb.getKeyCode()==keycode)&&(kb.getKeyModifiers()==keymod))
            {
                mykeybindings.remove(kb);
                return;
            }
        }
    }
    private void loadSpellBelt(String filename)
    {
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);

            for(int i=0;i<12;i++)
            {
                myspellbelt[i] = null;
                int sp = ois.readInt();
                int st = ois.readInt();
                if (sp > -1)
                    myspellbelt[i] = new SpellBeltItem(sp, st);
            }
            ois.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void saveSpellBelt(String filename)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
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
            oos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static SpellBeltItem[] getSpellBelt()
    {
        return myspellbelt;
    }
    private void loadKeyBindings(String filename)
    {
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mykeybindings.clear();
            int sz = ois.readInt();
            for(int i=0;i<sz;i++)
            {
                int kc = ois.readInt();
                int km = ois.readInt();
                int lsz= ois.readInt();
                java.util.List<GUICommand> guil = new java.util.ArrayList<GUICommand>();
                for(int j=0; j<lsz; j++)
                {
                    java.util.List list_parms = new ArrayList();
                    list_parms.add(this);
                    list_parms.add((String)ois.readObject());
                    GUICommand guic = new GUICommand(null, GUICommand.CMD_GUI_SEND_COMMAND,
                                        list_parms);
                    guil.add(guic);
                }
                KeyBinding kb = new KeyBinding(kc, km, guil);
                mykeybindings.add(kb);
            }
            ois.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void saveKeyBindings(String filename)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeInt(mykeybindings.size());
            Iterator<KeyBinding> it = mykeybindings.iterator();
            while(it.hasNext())
            {
                KeyBinding kb = it.next();
                oos.writeInt(kb.getKeyCode());
                oos.writeInt(kb.getKeyModifiers());
                oos.writeInt(kb.getCommands().size());
                Iterator<GUICommand> ic = kb.getCommands().iterator();
                while (ic.hasNext())
                {
                    GUICommand guic = ic.next();
                    java.util.List guil = (java.util.List)guic.getParams();
                    oos.writeObject((String)guil.get(1));
                }
            }
            oos.close();
        }
        catch (Exception e)
        {
            System.err.println("Warning: the key bindings file does not exist or is unavailable.");
            System.err.println("It should be created when you leave the client.");
        }
    }
    public boolean getKeyShift(int keyid)
    {
        return key_shift[keyid];
    }
    public void setKeyShift(int keyid, boolean state)
    {
        key_shift[keyid] = state;
    }
    public int getDialogStatus()
    {
        return mydialogstatus;
    }
    public void setDialogStatus(int nv)
    {
        synchronized(mydialogstatus_sem)
        {
            mydialogstatus = nv;
            switch (nv)
            {
                case DLG_NONE:
                    mydialog_current = null;
                    com.realtime.crossfire.jxclient.Map.invalidate();
                    break;
                case DLG_BOOK:
                    mydialog_current = mydialog_book;
                    break;
                case DLG_QUERY:
                    mydialog_current = mydialog_query;
                    break;
                case DLG_KEYBIND:
                    mydialog_current = mydialog_keybind;
                    break;
                case DLG_CARD:
                    mydialog_current = mydialog_card;
                    break;
                case DLG_PAPER:
                    mydialog_current = mydialog_paper;
                    break;
                case DLG_SIGN:
                    mydialog_current = mydialog_sign;
                    break;
                case DLG_MONUMENT:
                    mydialog_current = mydialog_monument;
                    break;
                case DLG_SCRIPTED_DIALOG:
                    mydialog_current = mydialog_scripted_dialog;
                    break;
                case DLG_MOTD:
                    mydialog_current = mydialog_motd;
                    break;
            }
            if (nv != DLG_NONE)
            {
                activateFirstTextArea(mydialog_current);
            }
        }
    }
    private void activateFirstTextArea(java.util.List<GUIElement> list)
    {
        Iterator<GUIElement> it = list.iterator();
        while (it.hasNext())
        {
            GUIElement e = it.next();
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
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        isfullscreen = gd.isFullScreenSupported();
        if(gd.isFullScreenSupported()==false)
        {
            System.out.println("Warning ! True full-screen support is not available.");
            setUndecorated(true);
            setIgnoreRepaint(true);
            oldDisplayMode = gd.getDisplayMode();

            DisplayMode ndm = mymode;
            this.setSize(mymode.getWidth(),mymode.getHeight());
            this.setVisible(true);
        }
        else
        {
            setUndecorated(true);
            setIgnoreRepaint(true);
            oldDisplayMode = gd.getDisplayMode();

            DisplayMode ndm = mymode;
            gd.setFullScreenWindow(this);
            gd.setDisplayMode(ndm);
            System.out.println("Graphic Device:"+gd.getIDstring());
            System.out.println("Accelerated memory available:"+gd.getAvailableAcceleratedMemory());
        }
        createBufferStrategy(2);
        mybufferstrategy = getBufferStrategy();

        framecount = 0;
        loadKeyBindings("keybindings.data");
        loadSpellBelt("spellbelt.data");
        starttime = System.nanoTime();
    }
    public void endRendering()
    {
        long endtime = System.nanoTime();
        long totaltime = endtime - starttime;
        System.out.println(framecount+" frames in "+totaltime/1000000 +
                " ms - "+(framecount*1000/(totaltime/1000000))+" FPS");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if(gd.isFullScreenSupported()==true)
        {
            gd.setDisplayMode(oldDisplayMode);
            gd.setFullScreenWindow(null);
        }
        saveKeyBindings("keybindings.data");
        saveSpellBelt("spellbelt.data");
        System.exit(0);
    }
    private void initGUI(int id)
    {
        switch (id)
        {
            case GUI_START:
                showGUIStart();
                break;
            case GUI_METASERVER:
                showGUIMeta();
                break;
            case GUI_MAIN:
                showGUIMain();
                break;
        }
    }
    public void changeGUI(int id)
    {
        initGUI(id);
    }
    private void redrawGUI()
    {
        Graphics g = mybufferstrategy.getDrawGraphics();
        if (mybufferstrategy.contentsRestored())
        {
            // Surface was recreated and reset, may require redrawing.
            g.setColor(Color.BLACK);
            g.fillRect(0,0,getWidth(),getHeight());
        }
        Iterator<GUIElement> it = mygui.iterator();
        while (it.hasNext())
        {
            GUIElement element = it.next();
            if (element.isVisible())
            {
                if (element instanceof GUIMap)
                {
                    GUIMap mel = (GUIMap)element;
                    mel.redraw(g);
                }
                else
                {
                    g.drawImage(
                        element.getBuffer(), element.getX(), element.getY(), this);
                }
            }
        }
        redrawGUIDialog(g);
        g.dispose();
        mybufferstrategy.show();
        if (mybufferstrategy.contentsLost())
        {
        // The surface was lost since last call to getDrawGraphics, you
        // may need to redraw.
            g.setColor(Color.BLACK);
            g.fillRect(0,0,getWidth(),getHeight());
        }
    }
    private void redrawGUIDialog(Graphics g)
    {
        Iterator<GUIElement> it;
        if (getDialogStatus()!=DLG_NONE)
        {
            it = mydialog_current.iterator();
            while (it.hasNext())
            {
                GUIElement element = it.next();

                if (element.isVisible())
                {
                    if (element instanceof GUIMap)
                    {
                        GUIMap mel = (GUIMap)element;
                        Graphics gg = element.getBuffer().createGraphics();
                        mel.redraw(gg);
                        gg.dispose();
                        g.drawImage(
                                element.getBuffer(), element.getX(), element.getY(), this);
                    }
                    else
                    {
                        g.drawImage(
                                element.getBuffer(), element.getX(), element.getY(), this);
                    }
                }
            }
        }
    }
    public void init(int w, int h, int b, int f, String skinclass)
    {
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        mymode = new DisplayMode(w,h,b,f);
        try
        {
            myskin = (JXCSkin)(Class.forName(skinclass).newInstance());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        try
        {
            initRendering();
            initGUI(GUI_START);
            for(;;)
            {
                synchronized(semaphore_drawing)
                {
                    redrawGUI();
                }
                framecount++;
                Thread.sleep(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void connect(String hostname, int port)
    {
        myserver = new ServerConnection(hostname, port);
        myserver.addCrossfireDrawextinfoListener(this);
        myserver.addCrossfireQueryListener(this);
        initGUI(GUI_MAIN);
        myserver.connect();
    }
    public void send(String packet)
    {
        try
        {
            myserver.writePacket(packet);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            endRendering();
        }
    }
    public ServerConnection getServerConnection()
    {
        return myserver;
    }
    private void launchSpellFromBelt(int idx)
    {
        java.util.List lp = new java.util.ArrayList();
        lp.add(this);
        lp.add(myspellbelt[idx]);
        GUICommand fcmd = new GUICommand(null, GUICommand.CMD_GUI_SPELLBELT, lp);
        fcmd.execute();
    }
    public void handleKeyPress(KeyEvent e)
    {
        if((myserver == null)||(myserver.getStatus() != ServerConnection.STATUS_PLAYING))
            return;
        Iterator<KeyBinding> it = mykeybindings.iterator();
        while(it.hasNext())
        {
            KeyBinding kb = it.next();
            if ((kb.getKeyCode()==e.getKeyCode())&&(kb.getKeyModifiers()==e.getModifiers()))
            {
                Iterator<GUICommand> cit = kb.getCommands().iterator();
                while (cit.hasNext())
                {
                    GUICommand cmd = cit.next();
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
                    send("command 0 run 1");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    send("command 0 fire 1");
                    is_fire_active = true;
                }
                else
                    send("command 0 north");
                break;
            case KeyEvent.VK_NUMPAD9:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    send("command 0 run 2");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    send("command 0 fire 2");
                    is_fire_active = true;
                }
                else
                    send("command 0 northeast");
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_NUMPAD6:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    send("command 0 run 3");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    send("command 0 fire 3");
                    is_fire_active = true;
                }
                else
                    send("command 0 east");
                break;
            case KeyEvent.VK_NUMPAD3:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    send("command 0 run 4");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    send("command 0 fire 4");
                    is_fire_active = true;
                }
                else
                    send("command 0 southeast");
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_NUMPAD2:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    send("command 0 run 5");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    send("command 0 fire 5");
                    is_fire_active = true;
                }
                else
                    send("command 0 south");
                break;
            case KeyEvent.VK_NUMPAD1:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    send("command 0 run 6");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    send("command 0 fire 6");
                    is_fire_active = true;
                }
                else
                    send("command 0 southwest");
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_NUMPAD4:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    send("command 0 run 7");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    send("command 0 fire 7");
                    is_fire_active = true;
                }
                else
                    send("command 0 west");
                break;
            case KeyEvent.VK_NUMPAD7:
                if (getKeyShift(KEY_SHIFT_CTRL)==true)
                {
                    send("command 0 run 8");
                    is_run_active = true;
                }
                else if (getKeyShift(KEY_SHIFT_SHIFT)==true)
                {
                    send("command 0 fire 8");
                    is_fire_active = true;
                }
                else
                    send("command 0 northwest");
                break;
            case KeyEvent.VK_A:
                send("command 0 apply");
                break;
            case KeyEvent.VK_S:
                send("command 0 save");
                break;
            case KeyEvent.VK_W:
                send("command 0 who");
                break;
            case KeyEvent.VK_QUOTE:
                activateFirstTextArea(mygui);
                break;
            case KeyEvent.VK_QUOTEDBL:
                activateFirstTextArea(mygui);
                if (myactive_element != null)
                {
                    ((GUIText)myactive_element).setText("say ");
                }
                break;
            default:
                break;
        }
    }
    public void keyPressed(KeyEvent e)
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
                if (getDialogStatus()==DLG_KEYBIND)
                {
                    if (mycurrentkeybinding!=null)
                    {
                        addKeyBinding(e.getKeyCode(), e.getModifiers(), mycurrentkeybinding);
                    }
                    else
                    {
                        deleteKeyBinding(e.getKeyCode(), e.getModifiers());
                        addKeyBinding(e.getKeyCode(), e.getModifiers(), mycurrentkeybinding);
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
    public void keyReleased(KeyEvent e)
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
                    send("command 0 fire_stop");
                    is_fire_active = false;
                }
                break;
            case KeyEvent.VK_CONTROL:
                setKeyShift(KEY_SHIFT_CTRL, false);
                if (is_run_active == true)
                {
                    send("command 0 run_stop");
                    is_run_active = false;
                }
                break;
        }
    }
    public void keyTyped(KeyEvent e)
    {
        /*if (myactive_element != null)
            if (myactive_element instanceof KeyListener)
        ((KeyListener)myactive_element).keyTyped(e);*/
    }
    public void mouseClicked(MouseEvent e)
    {
    }
    public void mouseEntered(MouseEvent e)
    {
    }
    public void mouseExited(MouseEvent e)
    {
    }
    public void mousePressed(MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            GUIElement elected = manageMouseEvents(mygui, e);
            if (elected == null) elected = mygui.get(0);

            GUIElement myother = null;

            if (getDialogStatus()!=DLG_NONE)
            {
                myother = manageMouseEvents(mydialog_current, e);
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
    public void mouseReleased(MouseEvent e)
    {
        synchronized(semaphore_drawing)
        {
            GUIElement elected = manageMouseEvents(mygui, e);
            if (elected == null) elected = mygui.get(0);

            GUIElement myother = null;

            if (getDialogStatus() != DLG_NONE)
            {
                myother = manageMouseEvents(mydialog_current, e);
                if (myother != null)
                    elected = myother;
            }
            e.translatePoint(-elected.x, -elected.y);
            if (myactive_element!=elected)
                deactivateCurrentElement();
            elected.mouseReleased(e);
        }
    }
    public GUIElement manageMouseEvents(java.util.List<GUIElement> guilist, MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        int b = e.getButton();
        GUIElement elected = null;
        switch(b)
        {
            case MouseEvent.BUTTON1:
                Iterator<GUIElement> it = guilist.iterator();
                while (it.hasNext())
                {
                    GUIElement element = it.next();
                    if (element.isVisible())
                        if ((x>=element.getX())&&(x<=element.getX()+element.getWidth()))
                            if ((y>=element.getY())&&(y<=element.getY()+element.getHeight()))
                                elected = element;
                 }
                 break;
            case MouseEvent.BUTTON2:
                it = guilist.iterator();
                while (it.hasNext())
                {
                    GUIElement element = (GUIElement)it.next();
                    if (element.isVisible())
                        if ((x>=element.getX())&&(x<=element.getX()+element.getWidth()))
                            if ((y>=element.getY())&&(y<=element.getY()+element.getHeight()))
                                elected = element;
                }
                break;
            case MouseEvent.BUTTON3:
                it = guilist.iterator();
                while (it.hasNext())
                {
                    GUIElement element = (GUIElement)it.next();
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
    public void mouseDragged(MouseEvent e)
    {
    }
    public void mouseMoved(MouseEvent e)
    {
    }

    public void CommandDrawextinfoReceived(CrossfireCommandDrawextinfoEvent evt)
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
    public void CommandQueryReceived(CrossfireCommandQueryEvent evt)
    {
        setDialogStatus(DLG_QUERY);
    }
    private void clearGUI()
    {
        mygui.clear();
        Graphics gd;
        for(int ig=0;ig<3;ig++)
        {
            gd = mybufferstrategy.getDrawGraphics();
            gd.setColor(Color.BLACK);
            gd.fillRect(0,0,getWidth(),getHeight());
            gd.dispose();
            mybufferstrategy.show();
        }
    }
    private void showGUIStart()
    {
        clearGUI();
        mybufferstrategy.show();
        mygui = null;
        try
        {
            mygui = myskin.getStartInterface(myserver,this);
        }
        catch (JXCSkinException e)
        {
            endRendering();
        }
    }
    private void showGUIMeta()
    {
        clearGUI();
        mygui = null;
        try
        {
            mygui = myskin.getMetaInterface(myserver,this);
        }
        catch (JXCSkinException e)
        {
            endRendering();
        }
    }
    private void showGUIMain()
    {
        clearGUI();
        mygui = null;
        try
        {
            mygui = myskin.getMainInterface(myserver,this);
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
        catch (JXCSkinException e)
        {
            endRendering();
        }
    }
}

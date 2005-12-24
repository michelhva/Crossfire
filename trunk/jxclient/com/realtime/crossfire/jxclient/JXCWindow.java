package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.io.*;

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

    private java.util.List<GUIElement> mydialog_query   = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_book    = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_keybind = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_current = null;
    private JXCSkin myskin = new JXCSkinPrelude();

    private java.util.List<KeyBinding> mykeybindings = new ArrayList<KeyBinding>();
    private boolean[] key_shift = new boolean[]{false, false, false, false};
    private java.util.List<GUICommand> mycurrentkeybinding = null;

    public final static int KEY_SHIFT_SHIFT = 0;
    public final static int KEY_SHIFT_CTRL  = 1;
    public final static int KEY_SHIFT_ALT   = 2;
    public final static int KEY_SHIFT_ALTGR = 3;
    private boolean is_run_active = false;
    private boolean is_fire_active = false;

    public void setCurrentSpell(Spell s)
    {
        mycurrentspell = s;
    }
    public Spell getCurrentSpell()
    {
        return mycurrentspell;
    }
    public void addKeyBinding(int keycode, int keymod, java.util.List<GUICommand> cmdlist)
    {
        KeyBinding kb = new KeyBinding(keycode, keymod, cmdlist);
        KeyBinding ok;
        Iterator it = mykeybindings.iterator();
        KeyBinding elected = null;
        while(it.hasNext())
        {
            ok = (KeyBinding)it.next();
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
        Iterator it = mykeybindings.iterator();
        while(it.hasNext())
        {
            KeyBinding kb = (KeyBinding)it.next();
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
            Iterator it = mykeybindings.iterator();
            while(it.hasNext())
            {
                KeyBinding kb = (KeyBinding)it.next();
                oos.writeInt(kb.getKeyCode());
                oos.writeInt(kb.getKeyModifiers());
                oos.writeInt(kb.getCommands().size());
                Iterator ic = kb.getCommands().iterator();
                while (ic.hasNext())
                {
                    GUICommand guic = (GUICommand)ic.next();
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
            }
        }
    }
    private void initRendering()
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        if(gd.isFullScreenSupported()==false)
        {
            System.out.println("Warning ! True full-screen support is not available.");
            setUndecorated(true);
            setIgnoreRepaint(true);
            oldDisplayMode = gd.getDisplayMode();

            DisplayMode ndm = mymode;
            this.setSize(1024,768);
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
        if (mybufferstrategy.contentsRestored()) {
        // Surface was recreated and reset, may require redrawing.
            g.setColor(Color.BLACK);
            g.fillRect(0,0,getWidth(),getHeight());
        }
        Iterator it = mygui.iterator();
        while (it.hasNext())
        {
            GUIElement element = (GUIElement)it.next();

            if (element.isVisible())
            {
                if (element instanceof GUIMap)
                {
                    GUIMap mel = (GUIMap)element;
                    mel.redraw(g);
                    //g.drawImage(
                    //    element.getBuffer(), element.getX(), element.getY(), this);
                }
                else
                {
                    g.drawImage(
                        element.getBuffer(), element.getX(), element.getY(), this);
                }
            }
        }
        if (getDialogStatus()!=DLG_NONE)
        {
            it = mydialog_current.iterator();
            while (it.hasNext())
            {
                GUIElement element = (GUIElement)it.next();

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
        g.dispose();
        mybufferstrategy.show();
        if (mybufferstrategy.contentsLost()) {
        // The surface was lost since last call to getDrawGraphics, you
        // may need to redraw.
            g.setColor(Color.BLACK);
            g.fillRect(0,0,getWidth(),getHeight());
        }
    }
    public void init(int w, int h, int b, int f)
    {
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        mymode = new DisplayMode(w,h,b,f);
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
    public void handleKeyPress(KeyEvent e)
    {
        if((myserver == null)||(myserver.getStatus() != ServerConnection.STATUS_PLAYING))
            return;
        Iterator it = mykeybindings.iterator();
        while(it.hasNext())
        {
            KeyBinding kb = (KeyBinding)it.next();
            if ((kb.getKeyCode()==e.getKeyCode())&&(kb.getKeyModifiers()==e.getModifiers()))
            {
                Iterator cit = kb.getCommands().iterator();
                while (cit.hasNext())
                {
                    GUICommand cmd = (GUICommand)cit.next();
                    cmd.execute();
                }
                return;
            }
        }
        switch(e.getKeyCode())
        {
            case KeyEvent.VK_UP:
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
            case KeyEvent.VK_DOWN:
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
            case KeyEvent.VK_LEFT:
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
            case KeyEvent.VK_RIGHT:
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
            case KeyEvent.VK_A:
                send("command 0 apply");
                break;
            case KeyEvent.VK_S:
                send("command 0 save");
                break;
            case KeyEvent.VK_W:
                send("command 0 who");
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
                Iterator it = guilist.iterator();
                while (it.hasNext())
                {
                    GUIElement element = (GUIElement)it.next();
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
            mydialog_keybind = myskin.getDialogKeyBind(myserver, this);
        }
        catch (JXCSkinException e)
        {
            endRendering();
        }
    }
}

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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a set of key bindings.
 *
 * @author Andreas Kirschbaum
 */
public final class KeyBindings implements Iterable<KeyBinding>
{
    private final List<KeyBinding> keybindings = new ArrayList<KeyBinding>();

    public int size()
    {
        return keybindings.size();
    }

    public void addKeyBinding(final int keycode, final int keymod, final List<GUICommand> cmdlist)
    {
        final KeyBinding kb = new KeyBinding(keycode, keymod, cmdlist);
        KeyBinding elected = null;
        for (final KeyBinding ok : keybindings)
        {
            if (ok.equals(kb))
            {
                elected = ok;
                continue;
            }
        }
        if (elected != null)
        {
            keybindings.remove(elected);
        }
        keybindings.add(kb);
    }

    public void deleteKeyBinding(final int keycode, final int keymod)
    {
        for (final KeyBinding kb : keybindings)
        {
            if (kb.getKeyCode() == keycode && kb.getKeyModifiers() == keymod)
            {
                keybindings.remove(kb);
                return;
            }
        }
    }

    public Iterator<KeyBinding> iterator()
    {
        return Collections.unmodifiableList(keybindings).iterator();
    }

    public void loadKeyBindings(final String filename)
    {
        try
        {
            final FileInputStream fis = new FileInputStream(filename);
            try
            {
                final ObjectInputStream ois = new ObjectInputStream(fis);
                try
                {
                    keybindings.clear();
                    final int sz = ois.readInt();
                    for(int i=0;i<sz;i++)
                    {
                        final int kc = ois.readInt();
                        final int km = ois.readInt();
                        final int lsz= ois.readInt();
                        final List<GUICommand> guil = new ArrayList<GUICommand>();
                        for(int j=0; j<lsz; j++)
                        {
/*XXX: type mismatch
                        final GUICommand guic = new GUICommand(null, GUICommand.CMD_GUI_SEND_COMMAND,
                            new GUICommand.SendCommandParameter(this, (String)ois.readObject()));
                        guil.add(guic);
*/
                        }
                        keybindings.add(new KeyBinding(kc, km, guil));
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

    public void saveKeyBindings(final String filename)
    {
        try
        {
            final FileOutputStream fos = new FileOutputStream(filename);
            try
            {
                final ObjectOutputStream oos = new ObjectOutputStream(fos);
                try
                {
                    oos.writeInt(keybindings.size());
                    for (final KeyBinding kb : keybindings)
                    {
                        oos.writeInt(kb.getKeyCode());
                        oos.writeInt(kb.getKeyModifiers());
                        oos.writeInt(kb.getCommands().size());
                        for (final GUICommand guic : kb.getCommands())
                        {
                            final List guil = (List)guic.getParams();
                            oos.writeObject((String)guil.get(1));
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
            System.err.println("Warning: the key bindings file does not exist or is unavailable.");
            System.err.println("It should be created when you leave the client.");
        }
    }
}

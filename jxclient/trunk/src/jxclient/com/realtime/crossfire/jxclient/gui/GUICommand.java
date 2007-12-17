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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.JXCWindow;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUICommand
{
    private final GUIElement mytarget;

    private final Command myorder;

    private final Object myparams; //Often a String, but not always - see QUIT or CONNECT

    public enum Command
    {
        SHOW,
        HIDE,
        TOGGLE,
        PRINT,
        QUIT,
        SCROLLUP,
        SCROLLDOWN,
        SCROLLNEXT,
        CONNECT,
        DISCONNECT,
        GUI_META,
        GUI_START,
        GUI_EXECUTE_COMMAND,
        GUI_EXECUTE_ELEMENT,
        DIALOG_OPEN,
        DIALOG_TOGGLE,
        DIALOG_CLOSE,
    }

    public GUICommand(final GUIElement element, final Command order, final Object params)
    {
        mytarget = element;
        myorder = order;
        myparams = params;
    }

    public Command getOrder()
    {
        return myorder;
    }

    public boolean canExecute()
    {
        switch (myorder)
        {
        case SHOW:
        case HIDE:
        case TOGGLE:
        case PRINT:
        case QUIT:
            break;

        case SCROLLUP:
            if (mytarget instanceof GUIScrollable)
            {
                return ((GUIScrollable)mytarget).canScrollUp();
            }
            break;

        case SCROLLDOWN:
            if (mytarget instanceof GUIScrollable)
            {
                return ((GUIScrollable)mytarget).canScrollDown();
            }
            break;

        case SCROLLNEXT:
        case CONNECT:
        case DISCONNECT:
        case GUI_META:
        case GUI_START:
        case GUI_EXECUTE_COMMAND:
        case GUI_EXECUTE_ELEMENT:
        case DIALOG_OPEN:
        case DIALOG_TOGGLE:
        case DIALOG_CLOSE:
            break;
        }

        return true;
    }

    public void execute()
    {
        switch (myorder)
        {
        case SHOW:
            mytarget.setVisible(true);
            break;

        case HIDE:
            mytarget.setVisible(false);
            break;

        case TOGGLE:
            mytarget.setVisible(!mytarget.isVisible());
            break;

        case PRINT:
            break;

        case QUIT:
            ((JXCWindow)myparams).endRendering();
            break;

        case SCROLLUP:
            if (mytarget instanceof GUIScrollable)
            {
                ((GUIScrollable)mytarget).scrollUp();
            }
            break;

        case SCROLLDOWN:
            if (mytarget instanceof GUIScrollable)
            {
                ((GUIScrollable)mytarget).scrollDown();
            }
            break;

        case SCROLLNEXT:
            if (mytarget.isActive())
            {
                ((ScrollNextParameter)myparams).nextElement.setActive(true);
            }
            break;

        case CONNECT:
            ((JXCWindow)myparams).connect(((GUIText)mytarget).getText(), 13327);
            break;

        case DISCONNECT:
            ((JXCWindow)myparams).disconnect();
            break;

        case GUI_META:
            ((JXCWindow)myparams).changeGUI(JXCWindow.GUI_METASERVER);
            break;

        case GUI_START:
            ((JXCWindow)myparams).changeGUI(JXCWindow.GUI_START);
            break;

        case GUI_EXECUTE_COMMAND:
            {
                final ExecuteCommandParameter param = (ExecuteCommandParameter)myparams;
                param.window.executeCommand(param.command);
            }
            break;

        case GUI_EXECUTE_ELEMENT:
            if (mytarget instanceof GUIItem)
            {
                ((GUIItem)mytarget).button1Clicked((JXCWindow)myparams);
            }
            break;

        case DIALOG_OPEN:
            {
                final DialogOpenParameter param = (DialogOpenParameter)myparams;
                param.window.openDialog(param.dialog);
            }
            break;

        case DIALOG_TOGGLE:
            {
                final DialogToggleParameter param = (DialogToggleParameter)myparams;
                param.window.toggleDialog(param.dialog);
            }
            break;

        case DIALOG_CLOSE:
            {
                final DialogCloseParameter param = (DialogCloseParameter)myparams;
                param.window.getWindowRenderer().closeDialog(param.dialog);
            }
            break;
        }
    }

    public Object getParams()
    {
        return myparams;
    }

    /**
     * A parameter object for the {@link Command#SCROLLNEXT} command.
     */
    public static class ScrollNextParameter
    {
        /** The dialog to close. */
        private final ActivatableGUIElement nextElement;

        /**
         * Create a new instance.
         *
         * @param nextElement The element to activate.
         */
        public ScrollNextParameter(final ActivatableGUIElement nextElement)
        {
            this.nextElement = nextElement;
        }
    }

    /**
     * A parameter object for the {@link Command#GUI_EXECUTE_COMMAND} command.
     */
    public static class ExecuteCommandParameter
    {
        /** The window to operate on. */
        private final JXCWindow window;

        /** The command to execute. */
        private final String command;

        /**
         * Create a new instance.
         *
         * @param window The window to operate on.
         *
         * @param command The command to execute.
         */
        public ExecuteCommandParameter(final JXCWindow window, final String command)
        {
            this.window = window;
            this.command = command;
        }

        /**
         * Return the command to execute.
         *
         * @return The command to execute.
         */
        public String getCommand()
        {
            return command;
        }
    }

    /**
     * A parameter object for the {@link Command#DIALOG_OPEN} command.
     */
    public static class DialogOpenParameter
    {
        /** The window to operate on. */
        private final JXCWindow window;

        /** The dialog to open. */
        private final Gui dialog;

        /**
         * Create a new instance.
         *
         * @param window The window to operate on.
         *
         * @param dialog The dialog to open.
         */
        public DialogOpenParameter(final JXCWindow window, final Gui dialog)
        {
            this.window = window;
            this.dialog = dialog;
        }
    }

    /**
     * A parameter object for the {@link Command#DIALOG_TOGGLE} command.
     */
    public static class DialogToggleParameter
    {
        /** The window to operate on. */
        private final JXCWindow window;

        /** The dialog to toggle. */
        private final Gui dialog;

        /**
         * Create a new instance.
         *
         * @param window The window to operate on.
         *
         * @param dialog The dialog to toggle.
         */
        public DialogToggleParameter(final JXCWindow window, final Gui dialog)
        {
            this.window = window;
            this.dialog = dialog;
        }
    }

    /**
     * A parameter object for the {@link Command#DIALOG_CLOSE} command.
     */
    public static class DialogCloseParameter
    {
        /** The window to operate on. */
        private final JXCWindow window;

        /** The dialog to close. */
        private final Gui dialog;

        /**
         * Create a new instance.
         *
         * @param window The window to operate on.
         *
         * @param dialog The dialog to close.
         */
        public DialogCloseParameter(final JXCWindow window, final Gui dialog)
        {
            this.window = window;
            this.dialog = dialog;
        }
    }
}

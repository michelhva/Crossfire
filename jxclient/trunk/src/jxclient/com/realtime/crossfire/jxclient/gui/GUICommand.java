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
import com.realtime.crossfire.jxclient.util.NumberParser;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUICommand
{
    private final GUIElement target;

    private final Command order;

    private final Parameter params;

    public enum Command
    {
        SHOW,
        HIDE,
        TOGGLE,
        PRINT,
        QUIT,
        SCROLL,
        SCROLL_NEVER,
        SCROLLNEXT,
        SCROLL_RESET,
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

    public GUICommand(final GUIElement target, final Command order, final Parameter params)
    {
        this.target = target;
        this.order = order;
        this.params = params;
    }

    public Command getOrder()
    {
        return order;
    }

    public boolean canExecute()
    {
        switch (order)
        {
        case SHOW:
        case HIDE:
        case TOGGLE:
        case PRINT:
        case QUIT:
            break;

        case SCROLL:
            if (target instanceof GUIScrollable)
            {
                return ((GUIScrollable)target).canScroll(((ScrollParameter)params).distance);
            }
            break;

        case SCROLL_NEVER:
            return false;

        case SCROLLNEXT:
        case SCROLL_RESET:
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
        switch (order)
        {
        case SHOW:
            target.setVisible(true);
            break;

        case HIDE:
            target.setVisible(false);
            break;

        case TOGGLE:
            target.setVisible(!target.isVisible());
            break;

        case PRINT:
            break;

        case QUIT:
            ((QuitParameter)params).window.endRendering();
            break;

        case SCROLL:
        case SCROLL_NEVER:
            if (target instanceof GUIScrollable)
            {
                ((GUIScrollable)target).scroll(((ScrollParameter)params).distance);
            }
            break;

        case SCROLLNEXT:
            if (target.isActive())
            {
                ((ScrollNextParameter)params).nextElement.setActive(true);
            }
            break;

        case SCROLL_RESET:
            if (target instanceof GUIScrollable)
            {
                ((GUIScrollable)target).resetScroll();
            }
            break;

        case CONNECT:
            ((ConnectParameter)params).window.connect(((GUIText)target).getText());
            break;

        case DISCONNECT:
            ((DisconnectParameter)params).window.disconnect();
            break;

        case GUI_META:
            ((MetaParameter)params).window.changeGUI(JXCWindow.GUI_METASERVER);
            break;

        case GUI_START:
            ((StartParameter)params).window.changeGUI(JXCWindow.GUI_START);
            break;

        case GUI_EXECUTE_COMMAND:
            {
                final ExecuteCommandParameter param = (ExecuteCommandParameter)params;
                param.window.executeCommand(param.command);
            }
            break;

        case GUI_EXECUTE_ELEMENT:
            if (target instanceof GUIItem)
            {
                ((GUIItem)target).button1Clicked(((ExecuteElementParameter)params).window);
            }
            break;

        case DIALOG_OPEN:
            {
                final DialogOpenParameter param = (DialogOpenParameter)params;
                param.window.openDialog(param.dialog);
            }
            break;

        case DIALOG_TOGGLE:
            {
                final DialogToggleParameter param = (DialogToggleParameter)params;
                param.window.toggleDialog(param.dialog);
            }
            break;

        case DIALOG_CLOSE:
            {
                final DialogCloseParameter param = (DialogCloseParameter)params;
                param.window.getWindowRenderer().closeDialog(param.dialog);
            }
            break;
        }
    }

    public Parameter getParams()
    {
        return params;
    }

    /**
     * A parameter for an {@link GUICommand}.
     */
    public interface Parameter
    {
    }

    /**
     * A parameter object for the {@link Command#QUIT} command.
     */
    public static class QuitParameter implements Parameter
    {
        /** The window. */
        private final JXCWindow window;

        /**
         * Create a new instance.
         *
         * @param window The window.
         */
        public QuitParameter(final JXCWindow window)
        {
            this.window = window;
        }
    }

    /**
     * A parameter object for the {@link Command#CONNECT} command.
     */
    public static class ConnectParameter implements Parameter
    {
        /** The window. */
        private final JXCWindow window;

        /**
         * Create a new instance.
         *
         * @param window The window.
         */
        public ConnectParameter(final JXCWindow window)
        {
            this.window = window;
        }
    }

    /**
     * A parameter object for the {@link Command#DISCONNECT} command.
     */
    public static class DisconnectParameter implements Parameter
    {
        /** The window. */
        private final JXCWindow window;

        /**
         * Create a new instance.
         *
         * @param window The window.
         */
        public DisconnectParameter(final JXCWindow window)
        {
            this.window = window;
        }
    }

    /**
     * A parameter object for the {@link Command#GUI_META} command.
     */
    public static class MetaParameter implements Parameter
    {
        /** The window. */
        private final JXCWindow window;

        /**
         * Create a new instance.
         *
         * @param window The window.
         */
        public MetaParameter(final JXCWindow window)
        {
            this.window = window;
        }
    }

    /**
     * A parameter object for the {@link Command#GUI_START} command.
     */
    public static class StartParameter implements Parameter
    {
        /** The window. */
        private final JXCWindow window;

        /**
         * Create a new instance.
         *
         * @param window The window.
         */
        public StartParameter(final JXCWindow window)
        {
            this.window = window;
        }
    }

    /**
     * A parameter object for the {@link Command#GUI_EXECUTE_ELEMENT} command.
     */
    public static class ExecuteElementParameter implements Parameter
    {
        /** The window. */
        private final JXCWindow window;

        /**
         * Create a new instance.
         *
         * @param window The window.
         */
        public ExecuteElementParameter(final JXCWindow window)
        {
            this.window = window;
        }
    }

    /**
     * A parameter object for the {@link Command#SCROLL} and {@link
     * Command#SCROLL_NEVER} commands.
     */
    public static class ScrollParameter implements Parameter
    {
        /** The distance to scroll. */
        private final int distance;

        /**
         * Create a new instance.
         *
         * @param distance The distance to scroll.
         */
        public ScrollParameter(final int distance)
        {
            this.distance = distance;
        }
    }

    /**
     * A parameter object for the {@link Command#SCROLLNEXT} command.
     */
    public static class ScrollNextParameter implements Parameter
    {
        /** The element to activate. */
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
    public static class ExecuteCommandParameter implements Parameter
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
    public static class DialogOpenParameter implements Parameter
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
    public static class DialogToggleParameter implements Parameter
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
    public static class DialogCloseParameter implements Parameter
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

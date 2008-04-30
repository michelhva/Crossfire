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
package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIItem;
import com.realtime.crossfire.jxclient.gui.GUIScrollable;
import com.realtime.crossfire.jxclient.gui.GUIText;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUICommand
{
    private final Parameter params;

    public GUICommand(final Parameter params)
    {
        this.params = params;
    }

    public boolean canExecute()
    {
        return params.canExecute();
    }

    public void execute()
    {
        params.execute();
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
        boolean canExecute();

        void execute();
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

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.endRendering();
        }
    }

    /**
     * A parameter object for the {@link Command#CONNECT} command.
     */
    public static class ConnectParameter implements Parameter
    {
        /** The window. */
        private final JXCWindow window;

        /** The host name input field. */
        private final GUIText hostNameInputField;

        /**
         * Create a new instance.
         *
         * @param window The window.
         *
         * @param hostNameInputField The host name input field.
         */
        public ConnectParameter(final JXCWindow window, final GUIText hostNameInputField)
        {
            this.window = window;
            this.hostNameInputField = hostNameInputField;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.connect(hostNameInputField.getText());
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

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.disconnect();
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

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.changeGUI(JXCWindow.GUI_METASERVER);
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

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.changeGUI(JXCWindow.GUI_START);
        }
    }

    /**
     * A parameter object for the {@link Command#GUI_EXECUTE_ELEMENT} command.
     */
    public static class ExecuteElementParameter implements Parameter
    {
        /** The window. */
        private final JXCWindow window;

        /** The item element to execute. */
        private final GUIItem item;

        /**
         * Create a new instance.
         *
         * @param window The window.
         *
         * @param item The item element to execute.
         */
        public ExecuteElementParameter(final JXCWindow window, final GUIItem item)
        {
            this.window = window;
            this.item = item;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            item.button1Clicked(window);
        }
    }

    /**
     * A parameter object for the {@link Command#SCROLL} command.
     */
    public static class ScrollParameter implements Parameter
    {
        /** The distance to scroll. */
        private final int distance;

        /** The scrollable element. */
        private final GUIScrollable scrollable;

        /**
         * Create a new instance.
         *
         * @param distance The distance to scroll.
         *
         * @param scrollable The scrollable element.
         */
        public ScrollParameter(final int distance, final GUIScrollable scrollable)
        {
            this.distance = distance;
            this.scrollable = scrollable;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return scrollable.canScroll(distance);
        }

        /** {@inheritDoc} */
        public void execute()
        {
            scrollable.scroll(distance);
        }
    }

    /**
     * A parameter object for the {@link Command#SCROLL_NEVER} command.
     */
    public static class ScrollNeverParameter implements Parameter
    {
        /** The distance to scroll. */
        private final int distance;

        /** The scrollable element. */
        private final GUIScrollable scrollable;

        /**
         * Create a new instance.
         *
         * @param distance The distance to scroll.
         *
         * @param scrollable The scrollable element.
         */
        public ScrollNeverParameter(final int distance, final GUIScrollable scrollable)
        {
            this.distance = distance;
            this.scrollable = scrollable;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return false;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            scrollable.scroll(distance);
        }
    }

    /**
     * A parameter object for the {@link Command#SCROLLNEXT} command.
     */
    public static class ScrollNextParameter implements Parameter
    {
        /** The element to activate. */
        private final ActivatableGUIElement nextElement;

        /** The element to deactivate. */
        private final ActivatableGUIElement prevElement;

        /**
         * Create a new instance.
         *
         * @param nextElement The element to activate.
         *
         * @param prevElement The element to deactivate.
         */
        public ScrollNextParameter(final ActivatableGUIElement nextElement, final ActivatableGUIElement prevElement)
        {
            this.nextElement = nextElement;
            this.prevElement = prevElement;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            if (prevElement.isActive())
            {
                nextElement.setActive(true);
            }
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

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.executeCommand(command);
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

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.openDialog(dialog);
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

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.toggleDialog(dialog);
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

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            window.getWindowRenderer().closeDialog(dialog);
        }
    }

    /**
     * A parameter object for the {@link Command#SHOW} command.
     */
    public static class ShowParameter implements Parameter
    {
        /**
         * The gui element to show.
         */
        private final GUIElement target;

        /**
         * Create a new instance.
         *
         * @param target The gui element to show.
         */
        public ShowParameter(final GUIElement target)
        {
            this.target = target;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            target.setVisible(true);
        }
    }

    /**
     * A parameter object for the {@link Command#HIDE} command.
     */
    public static class HideParameter implements Parameter
    {
        /**
         * The gui element to hide.
         */
        private final GUIElement target;

        /**
         * Create a new instance.
         *
         * @param target The gui element to hide.
         */
        public HideParameter(final GUIElement target)
        {
            this.target = target;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            target.setVisible(false);
        }
    }

    /**
     * A parameter object for the {@link Command#TOGGLE} command.
     */
    public static class ToggleParameter implements Parameter
    {
        /**
         * The gui element to toggle.
         */
        private final GUIElement target;

        /**
         * Create a new instance.
         *
         * @param target The gui element to toggle.
         */
        public ToggleParameter(final GUIElement target)
        {
            this.target = target;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            target.setVisible(!target.isVisible());
        }
    }

    /**
     * A parameter object for the {@link Command#PRINT} command.
     */
    public static class PrintParameter implements Parameter
    {
        /**
         * Create a new instance.
         */
        public PrintParameter()
        {
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
        }
    }

    /**
     * A parameter object for the {@link Command#SCROLL_RESET} command.
     */
    public static class ScrollResetParameter implements Parameter
    {
        /**
         * The scrollable gui element to reset.
         */
        private final GUIScrollable scrollable;

        /**
         * Create a new instance.
         *
         * @param scrollable The scrollable gui element to reset.
         */
        public ScrollResetParameter(final GUIScrollable scrollable)
        {
            this.scrollable = scrollable;
        }

        /** {@inheritDoc} */
        public boolean canExecute()
        {
            return true;
        }

        /** {@inheritDoc} */
        public void execute()
        {
            scrollable.resetScroll();
        }
    }
}

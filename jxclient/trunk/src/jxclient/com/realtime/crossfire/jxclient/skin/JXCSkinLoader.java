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
package com.realtime.crossfire.jxclient.skin;

import com.realtime.crossfire.jxclient.ConnectionStateListener;
import com.realtime.crossfire.jxclient.ExperienceTable;
import com.realtime.crossfire.jxclient.gui.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.commands.GUICommand;
import com.realtime.crossfire.jxclient.gui.gauge.ActiveSkillGaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.GaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.GUIDupGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUIDupTextGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUIGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUITextGauge;
import com.realtime.crossfire.jxclient.gui.gauge.Orientation;
import com.realtime.crossfire.jxclient.gui.gauge.OrientationParser;
import com.realtime.crossfire.jxclient.gui.gauge.SkillGaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.StatGaugeUpdater;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.GUIButton;
import com.realtime.crossfire.jxclient.gui.GUICommandText;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIHTMLLabel;
import com.realtime.crossfire.jxclient.gui.GUIItem;
import com.realtime.crossfire.jxclient.gui.GUIItemFloor;
import com.realtime.crossfire.jxclient.gui.GUIItemInventory;
import com.realtime.crossfire.jxclient.gui.GUIItemShortcut;
import com.realtime.crossfire.jxclient.gui.GUIItemSpelllist;
import com.realtime.crossfire.jxclient.gui.GUILabel;
import com.realtime.crossfire.jxclient.gui.GUILabelQuery;
import com.realtime.crossfire.jxclient.gui.GUILabelStats;
import com.realtime.crossfire.jxclient.gui.GUIMagicMap;
import com.realtime.crossfire.jxclient.gui.GUIMap;
import com.realtime.crossfire.jxclient.gui.GUIMetaElement;
import com.realtime.crossfire.jxclient.gui.GUIMultiLineLabel;
import com.realtime.crossfire.jxclient.gui.GUIOneLineLabel;
import com.realtime.crossfire.jxclient.gui.GUIPicture;
import com.realtime.crossfire.jxclient.gui.GUIQueryText;
import com.realtime.crossfire.jxclient.gui.GUIScrollable;
import com.realtime.crossfire.jxclient.gui.GUIScrollable2;
import com.realtime.crossfire.jxclient.gui.GUIScrollBar;
import com.realtime.crossfire.jxclient.gui.GUISpellLabel;
import com.realtime.crossfire.jxclient.gui.GUIText;
import com.realtime.crossfire.jxclient.gui.GUITextButton;
import com.realtime.crossfire.jxclient.gui.GUITextField;
import com.realtime.crossfire.jxclient.gui.keybindings.InvalidKeyBinding;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.log.Fonts;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.log.GUIMessageLog;
import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireCommandMapscrollEvent;
import com.realtime.crossfire.jxclient.mapupdater.CrossfireMapscrollListener;
import com.realtime.crossfire.jxclient.Resolution;
import com.realtime.crossfire.jxclient.server.CrossfireCommandMagicmapEvent;
import com.realtime.crossfire.jxclient.server.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import com.realtime.crossfire.jxclient.server.UnknownCommandException;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.CommandCheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillListener;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsParser;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/**
 * Creates a {@link JXCSkin} instance from a file.
 *
 * @author Andreas Kirschbaum
 */
public abstract class JXCSkinLoader implements JXCSkin
{
    /**
     * Pattern to parse integer constants.
     */
    private static final Pattern patternExpr = Pattern.compile("([0-9]+|WIDTH|HEIGHT|WIDTH/2|HEIGHT/2)([-+])(.+)");

    /**
     * Available resolutions for this skin.
     */
    private final Set<Resolution> resolutions = new HashSet<Resolution>();

    /**
     * All defined gui elements.
     */
    private final JXCSkinCache<GUIElement> elements = new JXCSkinCache<GUIElement>("gui element");

    /**
     * All defined command lists.
     */
    private final JXCSkinCache<GUICommandList> commandLists = new JXCSkinCache<GUICommandList>("command list");

    /**
     * All defined dialogs.
     */
    private final JXCSkinCache<Gui> dialogs = new JXCSkinCache<Gui>("dialog");

    /**
     * All defined fonts.
     */
    private final JXCSkinCache<Font> fonts = new JXCSkinCache<Font>("font");

    /**
     * All defined images.
     */
    private final JXCSkinCache<BufferedImage> images = new JXCSkinCache<BufferedImage>("image");

    /**
     * The skin name.
     */
    private String skinName = "unknown";

    /**
     * The selected resolution.
     */
    private Resolution selectedResolution = new Resolution(true, 0, 0);

    /**
     * The map width in tiles; zero if unset.
     */
    private int mapWidth = 0;

    /**
     * The map height in tiles; zero if unset.
     */
    private int mapHeight = 0;

    /**
     * The text button factory. Set to <code>null</code> until defined.
     */
    private TextButtonFactory textButtonFactory = null;

    /**
     * The dialog factory. Set to <code>null</code> until defined.
     */
    private DialogFactory dialogFactory = null;

    /**
     * The checkbox factory. Set to <code>null</code> until defined.
     */
    private CheckBoxFactory checkBoxFactory = null;

    /**
     * Names of pending skin files.
     */
    private final Set<String> dialogsToLoad = new HashSet<String>();

    /**
     * All "event init" commands in execution order.
     */
    private final List<GUICommandList> initEvents = new ArrayList<GUICommandList>();

    /**
     * Check that the skin exists and can be accessed.
     *
     * @throws JXCSkinException if the skin does not exist or cannot be loaded
     */
    protected void checkAccess() throws JXCSkinException
    {
        try
        {
            final InputStream is = getInputStream("resolutions");
            try
            {
                final InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                try
                {
                    final BufferedReader br = new BufferedReader(isr);
                    try
                    {
                        for (;;)
                        {
                            final String line = br.readLine();
                            if (line == null)
                            {
                                break;
                            }

                            final Resolution resolution = Resolution.parse(true, line);
                            if (resolution == null)
                            {
                                throw new JXCSkinException(getURI("resolutions")+": invalid resolution '"+line+"' in resolutions file");
                            }
                            resolutions.add(resolution);
                        }
                    }
                    finally
                    {
                        br.close();
                    }
                }
                finally
                {
                    isr.close();
                }
            }
            finally
            {
                is.close();
            }
        }
        catch (final IOException ex)
        {
            throw new JXCSkinException(getURI("resolutions")+": "+ex.getMessage());
        }

        if (resolutions.isEmpty())
        {
            throw new JXCSkinException(getURI("resolutions")+": empty file");
        }
    }

    /** {@inheritDoc} */
    public void load(final CrossfireServerConnection s, final JXCWindow p, final Resolution resolution) throws JXCSkinException
    {
        if (resolution.isExact())
        {
            if (!resolutions.contains(resolution))
            {
                throw new JXCSkinException("resolution "+resolution+" is not supported by the skin "+skinName);
            }

            selectedResolution = resolution;
        }
        else
        {
            if (resolutions.contains(resolution))
            {
                selectedResolution = resolution;
            }
            else
            {
                Resolution selectedCandidate = null;
                // select maximum <= requested
                for (final Resolution candidate: resolutions)
                {
                    if (candidate.getWidth() <= resolution.getWidth() && candidate.getHeight() <= resolution.getHeight())
                    {
                        if (selectedCandidate == null || selectedCandidate.getArea() < candidate.getArea())
                        {
                            selectedCandidate = candidate;
                        }
                    }
                }
                if (selectedCandidate == null)
                {
                    // select minimum > requested
                    for (final Resolution candidate: resolutions)
                    {
                        if (selectedCandidate == null || selectedCandidate.getArea() > candidate.getArea())
                        {
                            selectedCandidate = candidate;
                        }
                    }
                    assert selectedCandidate != null; // at least one resolution exists
                }
                selectedResolution = selectedCandidate;
            }
        }

        skinName = "unknown";
        mapWidth = 0;
        mapHeight = 0;
        dialogs.clear();
        images.clear();
        addDialog("keybind", p);
        addDialog("query", p);
        addDialog("book", p);
        addDialog("main", p);
        addDialog("meta", p);
        addDialog("quit", p);
        addDialog("disconnect", p);
        addDialog("start", p);
        commandLists.clear();
        fonts.clear();
        textButtonFactory = null;
        dialogFactory = null;
        checkBoxFactory = null;
        try
        {
            load("global", selectedResolution, s, p, null);
            while (!dialogsToLoad.isEmpty())
            {
                final Iterator<String> it = dialogsToLoad.iterator();
                final String name = it.next();
                it.remove();
                final Gui gui = dialogs.lookup(name);
                load(name, selectedResolution, s, p, gui);
                gui.setStateChanged(false);
            }
        }
        finally
        {
            fonts.clear();
            textButtonFactory = null;
            dialogFactory = null;
            checkBoxFactory = null;
            images.clear();
        }

        if (mapWidth == 0 || mapHeight == 0)
        {
            throw new JXCSkinException("Missing map command");
        }
    }

    /** {@inheritDoc} */
    public String getSkinName()
    {
        return skinName+"@"+selectedResolution;
    }

    /** {@inheritDoc} */
    public Resolution getResolution()
    {
        return selectedResolution;
    }

    /** {@inheritDoc} */
    public int getMapWidth()
    {
        return mapWidth;
    }

    /** {@inheritDoc} */
    public int getMapHeight()
    {
        return mapHeight;
    }

    private Gui addDialog(final String name, final JXCWindow window)
    {
        try
        {
            return dialogs.lookup(name);
        }
        catch (final JXCSkinException ex)
        {
            final Gui gui = new Gui(window);
            try
            {
                dialogs.insert(name, gui);
            }
            catch (final JXCSkinException ex2)
            {
                throw new AssertionError();
            }
            dialogsToLoad.add(name);
            return gui;
        }
    }

    /** {@inheritDoc} */
    public Gui getDialogQuit()
    {
        return getDialog("quit");
    }

    /** {@inheritDoc} */
    public Gui getDialogDisconnect()
    {
        return getDialog("disconnect");
    }

    /** {@inheritDoc} */
    public Gui getDialogKeyBind()
    {
        final Gui result = getDialog("keybind");
        assert result != null;
        return result;
    }

    /** {@inheritDoc} */
    public Gui getDialogQuery()
    {
        final Gui result = getDialog("query");
        assert result != null;
        return result;
    }

    /** {@inheritDoc} */
    public Gui getDialogBook(int booknr)
    {
        final Gui result = getDialog("book");
        assert result != null;
        return result;
    }

    /** {@inheritDoc} */
    public Gui getMainInterface()
    {
        final Gui result = getDialog("main");
        assert result != null;
        return result;
    }

    /** {@inheritDoc} */
    public Gui getMetaInterface()
    {
        final Gui result = getDialog("meta");
        assert result != null;
        return result;
    }

    /** {@inheritDoc} */
    public Gui getStartInterface()
    {
        final Gui result = getDialog("start");
        assert result != null;
        return result;
    }

    /** {@inheritDoc} */
    public Gui getDialog(final String name)
    {
        try
        {
            return dialogs.lookup(name);
        }
        catch (final JXCSkinException ex)
        {
            return null;
        }
    }

    /**
     * Load a skin file and add the entries to a {@link Gui} instance.
     *
     * @param dialogName The key to identify this dialog.
     *
     * @param resolution The preferred resolution.
     *
     * @param server The server connection to monitor.
     *
     * @param window The main window.
     *
     * @param gui The Gui representing the skin file.
     *
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(final String dialogName, final Resolution resolution, final CrossfireServerConnection server, final JXCWindow window, final Gui gui) throws JXCSkinException
    {
        String resourceName = dialogName+"@"+resolution+".skin";

        elements.clear();
        try
        {
            InputStream inputStream;
            try
            {
                inputStream = getInputStream(resourceName);
            }
            catch (final IOException ex)
            {
                resourceName = dialogName+".skin";
                inputStream = getInputStream(resourceName);
            }
            try
            {
                load(dialogName, resourceName, inputStream, server, window, gui);
            }
            finally
            {
                inputStream.close();
            }
        }
        catch (final IOException ex)
        {
            throw new JXCSkinException(getURI(resourceName)+": "+ex.getMessage());
        }
        catch (final JXCSkinException ex)
        {
            throw new JXCSkinException(getURI(resourceName)+": "+ex.getMessage());
        }
        finally
        {
            elements.clear();
        }
    }

    /**
     * Return an {@link InputStream} for a resource name.
     *
     * @param name The resource name.
     *
     * @return The input stream for the resource.
     *
     * @throws IOException if the resource cannot be loaded
     */
    protected abstract InputStream getInputStream(final String name) throws IOException;

    /**
     * Return a description of the location of a resource name.
     *
     * @param name The resource name.
     *
     * @return The description of the resource.
     */
    protected abstract String getURI(final String name);

    /**
     * Load a skin file and add the entries to a {@link Gui} instance.
     *
     * @param dialogName The key to identify this dialog.
     *
     * @param resourceName The name of the skin resource; used to construct
     * error messages.
     *
     * @param inputStream The input stream to load from.
     *
     * @param server The server connection to monitor.
     *
     * @param window The main window.
     *
     * @param gui The Gui representing the skin file.
     *
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(final String dialogName, final String resourceName, final InputStream inputStream, final CrossfireServerConnection server, final JXCWindow window, final Gui gui) throws JXCSkinException
    {
        final List<GUIElement> addedElements = new ArrayList<GUIElement>();
        boolean addedElementsContainsWildcard = false;

        try
        {
            final InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            try
            {
                final LineNumberReader lnr = new LineNumberReader(isr);
                try
                {
                    for (;;)
                    {
                        final String line = lnr.readLine();
                        if (line == null)
                        {
                            break;
                        }

                        if (line.startsWith("#") || line.length() == 0)
                        {
                            continue;
                        }

                        final String[] args = split(line);
                        if (gui != null && args[0].equals("add"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            if (args[1].equals("*"))
                            {
                                addedElementsContainsWildcard = true;
                                addedElements.add(null);
                            }
                            else
                            {
                                addedElements.add(elements.lookup(args[1]));
                            }
                        }
                        else if (gui != null && args[0].equals("button"))
                        {
                            if (args.length != 10 && args.length < 14)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage pictureUp = getPicture(args[6]);
                            final BufferedImage pictureDown = getPicture(args[7]);
                            final boolean autoRepeat = parseBoolean(args[8]);
                            final GUICommandList commandList = getCommandList(args[9]);
                            final String label;
                            final Font font;
                            final Color color;
                            final int textX;
                            final int textY;
                            if (args.length == 10)
                            {
                                label = null;
                                font = null;
                                color = null;
                                textX = 0;
                                textY = 0;
                            }
                            else
                            {
                                assert args.length >= 14;
                                font = fonts.lookup(args[10]);
                                color = parseColor(args[11]);
                                textX = parseInt(args[12]);
                                textY = parseInt(args[13]);
                                label = parseText(args, 14, lnr);
                            }
                            elements.insert(name, new GUIButton(window, name, x, y, w, h, pictureUp, pictureDown, label, font, color, textX, textY, autoRepeat, commandList));
                        }
                        else if (gui != null && args[0].equals("checkbox"))
                        {
                            if (args.length < 7)
                            {
                                throw new IOException("syntax error");
                            }

                            if (checkBoxFactory == null)
                            {
                                throw new IOException("missing 'def checkbox' command");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final CheckBoxOption option = parseCheckBoxOption(args[6], window);
                            final String text = parseText(args, 7, lnr);
                            elements.insert(name, checkBoxFactory.newCheckBox(window, name, x, y, w, h, option, text));
                        }
                        else if (args[0].equals("commandlist"))
                        {
                            if (args.length != 3 && args.length < 5)
                            {
                                throw new IOException("syntax error");
                            }

                            final String commandListName = args[1];
                            final GUICommandList.Type commandListType = parseEnum(GUICommandList.Type.class, args[2], "type");
                            final GUICommandList commandList = new GUICommandList(commandListType);
                            commandLists.insert(commandListName, commandList);
                            if (args.length >= 5)
                            {
                                final GUIElement element = args[3].equals("null") ? null : elements.lookup(args[3]);
                                final String command = args[4];
                                final GUICommand.Parameter params = parseCommandArgs(args, 5, element, command, window, lnr);
                                commandList.add(new GUICommand(params));
                            }
                        }
                        else if (args[0].equals("commandlist_add"))
                        {
                            if (args.length < 4)
                            {
                                throw new IOException("syntax error");
                            }

                            final GUICommandList commandList = getCommandList(args[1]);
                            final GUIElement element = args[2].equals("null") ? null : elements.lookup(args[2]);
                            final String command = args[3];
                            final GUICommand.Parameter params = parseCommandArgs(args, 4, element, command, window, lnr);
                            commandList.add(new GUICommand(params));
                        }
                        else if (gui != null && args[0].equals("command_text"))
                        {
                            if (args.length != 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage activePicture = getPicture(args[6]);
                            final BufferedImage inactivePicture = getPicture(args[7]);
                            final Font font = fonts.lookup(args[8]);
                            final Color inactiveColor = parseColor(args[9]);
                            final Color activeColor = parseColor(args[10]);
                            final int margin = parseInt(args[11]);
                            elements.insert(name, new GUICommandText(window, name, x, y, w, h, activePicture, inactivePicture, font, inactiveColor, activeColor, margin, ""));
                        }
                        else if (args[0].equals("def"))
                        {
                            if (args.length < 2)
                            {
                                throw new IOException("syntax error");
                            }

                            if (args[1].equals("checkbox"))
                            {
                                if (args.length != 6)
                                {
                                    throw new IOException("syntax error");
                                }

                                final BufferedImage checked = getPicture(args[2]);
                                final BufferedImage unchecked = getPicture(args[3]);
                                final Font font = fonts.lookup(args[4]);
                                final Color color = parseColor(args[5]);
                                checkBoxFactory = new CheckBoxFactory(checked, unchecked, font, color);
                            }
                            else if (args[1].equals("checkbox_option"))
                            {
                                if (args.length < 5)
                                {
                                    throw new IOException("syntax error");
                                }

                                final String optionName = args[2];
                                final GUICommandList commandOn = getCommandList(args[3]);
                                final GUICommandList commandOff = getCommandList(args[4]);
                                final String documentation = parseText(args, 5, lnr);
                                try
                                {
                                    window.getOptionManager().addOption(optionName, documentation, new CommandCheckBoxOption(commandOn, commandOff));
                                }
                                catch (final OptionException ex)
                                {
                                    throw new IOException(ex.getMessage());
                                }
                            }
                            else if (args[1].equals("dialog"))
                            {
                                if (args.length != 7)
                                {
                                    throw new IOException("syntax error");
                                }

                                final String frame = args[2];
                                final BufferedImage frameNW = getPicture(frame+"_nw");
                                final BufferedImage frameN = getPicture(frame+"_n");
                                final BufferedImage frameNE = getPicture(frame+"_ne");
                                final BufferedImage frameW = getPicture(frame+"_w");
                                final BufferedImage frameC = getPicture(frame+"_c");
                                final BufferedImage frameE = getPicture(frame+"_e");
                                final BufferedImage frameSW = getPicture(frame+"_sw");
                                final BufferedImage frameS = getPicture(frame+"_s");
                                final BufferedImage frameSE = getPicture(frame+"_se");
                                final Font titleFont = fonts.lookup(args[3]);
                                final Color titleColor = parseColor(args[4]);
                                final Color titleBackgroundColor = parseColor(args[5]);
                                final float alpha = parseFloat(args[6]);
                                if (alpha < 0 || alpha > 1F) throw new IOException("invalid alpha value: "+alpha);
                                dialogFactory = new DialogFactory(frameNW, frameN, frameNE, frameW, frameC, frameE, frameSW, frameS, frameSE, titleFont, titleColor, titleBackgroundColor, alpha);
                            }
                            else if (args[1].equals("textbutton"))
                            {
                                if (args.length != 6)
                                {
                                    throw new IOException("syntax error");
                                }

                                final String up = args[2];
                                final String down = args[3];
                                final Font font = fonts.lookup(args[4]);
                                final Color color = parseColor(args[5]);
                                final GUITextButton.ButtonImages upImages = new GUITextButton.ButtonImages(getPicture(up+"_w"), getPicture(up+"_c"), getPicture(up+"_e"));
                                final GUITextButton.ButtonImages downImages = new GUITextButton.ButtonImages(getPicture(down+"_w"), getPicture(down+"_c"), getPicture(down+"_e"));
                                textButtonFactory = new TextButtonFactory(upImages, downImages, font, color);
                            }
                            else
                            {
                                throw new IOException("unknown type '"+args[1]+"'");
                            }
                        }
                        else if (gui != null && args[0].equals("dialog"))
                        {
                            if (args.length < 7)
                            {
                                throw new IOException("syntax error");
                            }

                            if (dialogFactory == null)
                            {
                                throw new IOException("missing 'def dialog' command");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final boolean saveDialog = parseBoolean(args[6]);
                            final String title = parseText(args, 7, lnr);
                            for (final GUIElement element : dialogFactory.newDialog(window, name, w, h, title))
                            {
                                elements.insert(element.getName(), element);
                            }
                            if (saveDialog)
                            {
                                gui.setName(dialogName);
                            }
                            gui.setSize(w, h);
                            gui.setPosition(x, y);
                        }
                        else if (gui != null && args[0].equals("dialog_hide"))
                        {
                            if (args.length < 2)
                            {
                                throw new IOException("syntax error");
                            }

                            for (int i = 1; i < args.length; i++)
                            {
                                gui.hideInState(parseEnum(JXCWindowRenderer.GuiState.class, args[i], "gui state"));
                            }
                        }
                        else if (gui != null && args[0].equals("dupgauge"))
                        {
                            if (args.length < 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage picturePositiveDiv = getPicture(args[6]);
                            final BufferedImage picturePositiveMod = getPicture(args[7]);
                            final BufferedImage pictureEmpty = args[8].equals("null") ? null : getPicture(args[8]);
                            final GaugeUpdater gaugeUpdater = parseGaugeUpdater(args[9], window.getExperienceTable());
                            final Orientation orientationDiv = parseOrientation(args[10]);
                            final Orientation orientationMod = parseOrientation(args[11]);
                            final String tooltipPrefix = parseText(args, 12, lnr);
                            final GUIDupGauge element = new GUIDupGauge(window, name, x, y, w, h, picturePositiveDiv, picturePositiveMod, pictureEmpty, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
                            elements.insert(name, element);
                            gaugeUpdater.setGauge(element);
                        }
                        else if (gui != null && args[0].equals("duptextgauge"))
                        {
                            if (args.length < 14)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage picturePositiveDiv = getPicture(args[6]);
                            final BufferedImage picturePositiveMod = getPicture(args[7]);
                            final BufferedImage pictureEmpty = getPicture(args[8]);
                            final GaugeUpdater gaugeUpdater = parseGaugeUpdater(args[9], window.getExperienceTable());
                            final Orientation orientationDiv = parseOrientation(args[10]);
                            final Orientation orientationMod = parseOrientation(args[11]);
                            final Color color = parseColor(args[12]);
                            final Font font = fonts.lookup(args[13]);
                            final String tooltipPrefix = parseText(args, 14, lnr);
                            final GUIDupTextGauge element = new GUIDupTextGauge(window, name, x, y, w, h, picturePositiveDiv, picturePositiveMod, pictureEmpty, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
                            elements.insert(name, element);
                            gaugeUpdater.setGauge(element);
                        }
                        else if (args[0].equals("event"))
                        {
                            if (args.length < 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final String type = args[1];
                            if (type.equals("connect"))
                            {
                                if (args.length != 3)
                                {
                                    throw new IOException("syntax error");
                                }

                                final GUICommandList commandList = getCommandList(args[2]);
                                window.addConnectionStateListener(new ConnectionStateListener()
                                    {
                                        /** {@inheritDoc} */
                                        public void connect()
                                        {
                                            commandList.execute();
                                        }

                                        /** {@inheritDoc} */
                                        public void disconnect()
                                        {
                                            // ignore
                                        }
                                    });
                            }
                            else if (type.equals("init"))
                            {
                                if (args.length != 3)
                                {
                                    throw new IOException("syntax error");
                                }

                                initEvents.add(getCommandList(args[2]));
                            }
                            else if (type.equals("magicmap"))
                            {
                                if (args.length != 3)
                                {
                                    throw new IOException("syntax error");
                                }

                                final GUICommandList commandList = getCommandList(args[2]);
                                server.addCrossfireMagicmapListener(new CrossfireMagicmapListener()
                                    {
                                        /** {@inheritDoc} */
                                        public void commandMagicmapReceived(final CrossfireCommandMagicmapEvent evt)
                                        {
                                            commandList.execute();
                                        }
                                    });
                            }
                            else if (type.equals("mapscroll"))
                            {
                                if (args.length != 3)
                                {
                                    throw new IOException("syntax error");
                                }

                                final GUICommandList commandList = getCommandList(args[2]);
                                CfMapUpdater.addCrossfireMapscrollListener(new CrossfireMapscrollListener()
                                    {
                                        /** {@inheritDoc} */
                                        public void commandMapscrollReceived(final CrossfireCommandMapscrollEvent evt)
                                        {
                                            commandList.execute();
                                        }
                                    });
                            }
                            else if (type.equals("skill"))
                            {
                                if (args.length != 5)
                                {
                                    throw new IOException("syntax error");
                                }

                                final String subtype = args[2];
                                final Skill skill = Stats.getNamedSkill(args[3].replaceAll("_", " "));
                                final GUICommandList commandList = getCommandList(args[4]);
                                if (subtype.equals("add"))
                                {
                                    skill.addSkillListener(new SkillListener()
                                        {
                                            /** {@inheritDoc} */
                                            public void addSkill(final Skill skill)
                                            {
                                                commandList.execute();
                                            }

                                            /** {@inheritDoc} */
                                            public void delSkill(final Skill skill)
                                            {
                                                // ignore
                                            }

                                            /** {@inheritDoc} */
                                            public void updSkill(final Skill skill)
                                            {
                                                // ignore
                                            }
                                        });
                                }
                                else if (subtype.equals("del"))
                                {
                                    skill.addSkillListener(new SkillListener()
                                        {
                                            /** {@inheritDoc} */
                                            public void addSkill(final Skill skill)
                                            {
                                                // ignore
                                            }

                                            /** {@inheritDoc} */
                                            public void delSkill(final Skill skill)
                                            {
                                                commandList.execute();
                                            }

                                            /** {@inheritDoc} */
                                            public void updSkill(final Skill skill)
                                            {
                                                // ignore
                                            }
                                        });
                                }
                                else
                                {
                                    throw new IOException("undefined event sub-type: "+subtype);
                                }
                            }
                            else
                            {
                                throw new IOException("undefined event type: "+type);
                            }
                        }
                        else if (args[0].equals("font"))
                        {
                            if (args.length != 4)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final Font fontNormal = getFont(args[2]);
                            final Font font = fontNormal.deriveFont(parseFloat(args[3]));
                            fonts.insert(name, font);
                        }
                        else if (gui != null && args[0].equals("gauge"))
                        {
                            if (args.length < 11)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage picturePositive = args[6].equals("null") ? null : getPicture(args[6]);
                            final BufferedImage pictureNegative = args[7].equals("null") ? null : getPicture(args[7]);
                            final BufferedImage pictureEmpty = args[8].equals("null") ? null : getPicture(args[8]);
                            final GaugeUpdater gaugeUpdater = parseGaugeUpdater(args[9], window.getExperienceTable());
                            final Orientation orientation = parseOrientation(args[10]);
                            final String tooltipPrefix = parseText(args, 11, lnr);
                            final GUIGauge element = new GUIGauge(window, name, x, y, w, h, picturePositive, pictureNegative, pictureEmpty, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
                            elements.insert(name, element);
                            gaugeUpdater.setGauge(element);
                        }
                        else if (gui != null && args[0].equals("ignore"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            elements.lookup(name).setIgnore();
                        }
                        else if (gui != null && args[0].equals("item"))
                        {
                            if (args.length < 13)
                            {
                                throw new IOException("syntax error");
                            }

                            final String type = args[1];
                            final String name = args[2];
                            final int x = parseInt(args[3]);
                            final int y = parseInt(args[4]);
                            final int w = parseInt(args[5]);
                            final int h = parseInt(args[6]);
                            final BufferedImage pictureCursed = getPicture(args[7]);
                            final BufferedImage pictureApplied = getPicture(args[8]);
                            final BufferedImage pictureSelector = getPicture(args[9]);
                            final BufferedImage pictureLocked = getPicture(args[10]);
                            final int index = parseInt(args[11]);
                            final Font font = fonts.lookup(args[12]);
                            final GUIItem element;
                            if (type.equals("floor"))
                            {
                                if (args.length != 14)
                                {
                                    throw new IOException("syntax error");
                                }

                                final Color nrofColor = parseColor(args[13]);
                                element = new GUIItemFloor(window, name, x, y, w, h, pictureCursed, pictureApplied, pictureSelector, pictureLocked, index, server, font, nrofColor);
                            }
                            else if (type.equals("inventory"))
                            {
                                if (args.length != 14)
                                {
                                    throw new IOException("syntax error");
                                }

                                final Color nrofColor = parseColor(args[13]);
                                element = new GUIItemInventory(window, name, x, y, w, h, pictureCursed, pictureApplied, pictureSelector, pictureLocked, index, server, font, nrofColor);
                            }
                            else if (type.equals("shortcut"))
                            {
                                if (args.length != 13)
                                {
                                    throw new IOException("syntax error");
                                }

                                element = new GUIItemShortcut(window, name, x, y, w, h, pictureCursed, pictureApplied, pictureSelector, pictureLocked, index, server, font);
                            }
                            else if (type.equals("spelllist"))
                            {
                                if (args.length != 13)
                                {
                                    throw new IOException("syntax error");
                                }

                                element = new GUIItemSpelllist(window, name, x, y, w, h, pictureCursed, pictureApplied, pictureSelector, pictureLocked, index, server, font);
                            }
                            else
                            {
                                throw new IOException("undefined item type: "+type);
                            }
                            elements.insert(name, element);
                        }
                        else if (args[0].equals("key"))
                        {
                            if (args.length < 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final KeyBindings keyBindings = gui != null ? gui.getKeyBindings() : window.getKeyBindings(false);
                            try
                            {
                                keyBindings.parseKeyBinding(line.substring(4).trim(), window, true);
                            }
                            catch (final InvalidKeyBinding ex)
                            {
                                throw new IOException("invalid key binding: "+ex.getMessage());
                            }
                        }
                        else if (gui != null && args[0].equals("label_html"))
                        {
                            if (args.length < 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final Font font = fonts.lookup(args[6]);
                            final Color color = parseColor(args[7]);
                            final String text = parseText(args, 8, lnr);
                            elements.insert(name, new GUIHTMLLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), text));
                        }
                        else if (gui != null && args[0].equals("label_multi"))
                        {
                            if (args.length < 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final Font font = fonts.lookup(args[6]);
                            final Color color = parseColor(args[7]);
                            final String text = parseText(args, 8, lnr);
                            elements.insert(name, new GUIMultiLineLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), GUILabel.Alignment.LEFT, text));
                        }
                        else if (gui != null && args[0].equals("label_query"))
                        {
                            if (args.length != 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final Font font = fonts.lookup(args[6]);
                            final Color color = parseColor(args[7]);
                            final GUILabelQuery element = new GUILabelQuery(window, name, x, y, w, h, font, color, new Color(0, 0, 0, 0F));
                            elements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("label_text"))
                        {
                            if (args.length < 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final Font font = fonts.lookup(args[6]);
                            final Color color = parseColor(args[7]);
                            final String text = parseText(args, 8, lnr);
                            elements.insert(name, new GUIOneLineLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), GUILabel.Alignment.LEFT, text));
                        }
                        else if (gui != null && args[0].equals("label_stat"))
                        {
                            if (args.length != 10)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final Font font = fonts.lookup(args[6]);
                            final Color color = parseColor(args[7]);
                            final int stat = parseStat(args[8]);
                            final GUILabel.Alignment alignment = parseEnum(GUILabel.Alignment.class, args[9], "text alignment");
                            final GUILabelStats element = new GUILabelStats(window, name, x, y, w, h, font, color, new Color(0, 0, 0, 0F), stat, alignment);
                            elements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("label_spell"))
                        {
                            if (args.length != 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final Font font = fonts.lookup(args[6]);
                            final GUISpellLabel.Type type = parseEnum(GUISpellLabel.Type.class, args[7], "label type");
                            final GUISpellLabel element = new GUISpellLabel(window, name, x, y, w, h, null, font, type);
                            elements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("log_label"))
                        {
                            if (args.length != 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage pictureEmpty = getPicture(args[6]);
                            final Font fontPrint = fonts.lookup(args[7]);
                            final Font fontFixed = fonts.lookup(args[8]);
                            final Font fontFixedBold = fonts.lookup(args[9]);
                            final Font fontArcane = fonts.lookup(args[10]);
                            final Color defaultColor = parseColor(args[11]);
                            final Fonts fonts = new Fonts(fontPrint, fontFixed, fontFixedBold, fontArcane);
                            final GUILabelLog element = new GUILabelLog(window, name, x, y, w, h, pictureEmpty, fonts, defaultColor);
                            elements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("log_message"))
                        {
                            if (args.length != 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage pictureEmpty = getPicture(args[6]);
                            final Font fontPrint = fonts.lookup(args[7]);
                            final Font fontFixed = fonts.lookup(args[8]);
                            final Font fontFixedBold = fonts.lookup(args[9]);
                            final Font fontArcane = fonts.lookup(args[10]);
                            final Color defaultColor = parseColor(args[11]);
                            final Fonts fonts = new Fonts(fontPrint, fontFixed, fontFixedBold, fontArcane);
                            final GUIMessageLog element = new GUIMessageLog(window, name, x, y, w, h, pictureEmpty, fonts, defaultColor);
                            elements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("log_color"))
                        {
                            if (args.length != 4)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int index = parseInt(args[2]);
                            final Color color = parseColor(args[3]);
                            final GUIElement element = elements.lookup(name);
                            if (!(element instanceof GUIMessageLog))
                            {
                                    throw new IOException("element '"+name+"' is not of type 'log'");
                            }
                            ((GUIMessageLog)element).setColor(index, color);
                        }
                        else if (gui != null && args[0].equals("log_filter"))
                        {
                            if (args.length < 4)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final String type = args[2];
                            final boolean add;
                            if (type.equals("only"))
                            {
                                add = true;
                            }
                            else if (type.equals("not"))
                            {
                                add = false;
                            }
                            else
                            {
                                throw new IOException("type '"+type+"' is invalid");
                            }
                            int types = 0;
                            for (int i = 3; i < args.length; i++)
                            {
                                try
                                {
                                    types |= 1<<MessageTypes.parseMessageType(args[i]);
                                }
                                catch (final UnknownCommandException ex)
                                {
                                    throw new IOException("undefined message type '"+args[i]+"'");
                                }
                            }
                            if (!add)
                            {
                                types = ~types;
                            }
                            final GUIElement element = elements.lookup(name);
                            if (!(element instanceof GUIMessageLog))
                            {
                                throw new IOException("element '"+name+"' is not of type 'log'");
                            }
                            ((GUIMessageLog)element).setTypes(types);
                        }
                        else if (gui != null && args[0].equals("magicmap"))
                        {
                            if (args.length != 6)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final GUIMagicMap element = new GUIMagicMap(window, name, x, y, w, h);
                            elements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("map"))
                        {
                            if (args.length != 7)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int tileSize = parseInt(args[2]);
                            final int x = parseInt(args[3]);
                            final int y = parseInt(args[4]);
                            final int w = parseInt(args[5]);
                            final int h = parseInt(args[6]);

                            if (tileSize <= 0) throw new IOException("invalid tile size "+tileSize);
                            if (w%tileSize != 0) throw new IOException("map width "+w+" is not a multiple of the tile size "+tileSize);
                            if (h%tileSize != 0) throw new IOException("map height "+h+" is not a multiple of the tile size "+tileSize);
                            final int tmpW = w/tileSize;
                            final int tmpH = h/tileSize;
                            CrossfireServerConnection.validateMapSize(tmpW, tmpH);
                            mapWidth = tmpW;
                            mapHeight = tmpH;

                            final GUIMap element = new GUIMap(window, name, tileSize, x, y, w, h);
                            elements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("meta_element"))
                        {
                            if (args.length != 13)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage pictureTcp = args[6].equals("null") ? null : getPicture(args[6]);
                            final Font font = fonts.lookup(args[7]);
                            final GUIText text = lookupTextElement(args[8]);
                            final AbstractLabel label = lookupLabelElement(args[9]);
                            final int id = parseInt(args[10]);
                            final String format = args[11];
                            final String tooltip = args[12];
                            elements.insert(name, new GUIMetaElement(window, name, x, y, w, h, pictureTcp, font, text, label, id, format, tooltip));
                        }
                        else if (gui != null && args[0].equals("picture"))
                        {
                            if (args.length != 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage picture = getPicture(args[6]);
                            final float alpha = parseFloat(args[7]);
                            if (alpha < 0 || alpha > 1F) throw new IOException("invalid alpha value: "+alpha);
                            elements.insert(name, new GUIPicture(window, name, x, y, w, h, picture, alpha));
                        }
                        else if (gui != null && args[0].equals("query_text"))
                        {
                            if (args.length != 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage activePicture = getPicture(args[6]);
                            final BufferedImage inactivePicture = getPicture(args[7]);
                            final Font font = fonts.lookup(args[8]);
                            final Color inactiveColor = parseColor(args[9]);
                            final Color activeColor = parseColor(args[10]);
                            final int margin = parseInt(args[11]);
                            elements.insert(name, new GUIQueryText(window, name, x, y, w, h, activePicture, inactivePicture, font, inactiveColor, activeColor, margin, ""));
                        }
                        else if (gui != null && args[0].equals("set_default"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            elements.lookup(args[1]).setDefault(true);
                        }
                        else if (gui != null && args[0].equals("set_invisible"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            elements.lookup(args[1]).setVisible(false);
                        }
                        else if (gui != null && args[0].equals("set_modal"))
                        {
                            if (args.length != 1)
                            {
                                throw new IOException("syntax error");
                            }

                            gui.setModal(true);
                        }
                        else if (gui != null && args[0].equals("scrollbar"))
                        {
                            if (args.length != 10)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final boolean proportionalSlider = parseBoolean(args[6]);
                            final GUIElement element = elements.lookup(args[7]);
                            final Color colorBackground = parseColor(args[8]);
                            final Color colorForeground = parseColor(args[9]);
                            if (!(element instanceof GUIScrollable2))
                            {
                                throw new IOException("'"+element+"' is not a scrollable element");
                            }
                            elements.insert(name, new GUIScrollBar(window, name, x, y, w, h, proportionalSlider, (GUIScrollable2)element, colorBackground, colorForeground));
                        }
                        else if (gui == null && args[0].equals("skin_name"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final String skinName = args[1];
                            if (!skinName.matches("[-a-z_0-9]+"))
                            {
                                throw new IOException("invalid skin_name: "+skinName);
                            }

                            this.skinName = skinName;
                        }
                        else if (gui != null && args[0].equals("text"))
                        {
                            if (args.length != 13)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage activePicture = getPicture(args[6]);
                            final BufferedImage inactivePicture = getPicture(args[7]);
                            final Font font = fonts.lookup(args[8]);
                            final Color inactiveColor = parseColor(args[9]);
                            final Color activeColor = parseColor(args[10]);
                            final int margin = parseInt(args[11]);
                            final GUICommandList commandList = getCommandList(args[12]);
                            elements.insert(name, new GUITextField(window, name, x, y, w, h, activePicture, inactivePicture, font, inactiveColor, activeColor, margin, "", commandList));
                        }
                        else if (gui != null && args[0].equals("textbutton"))
                        {
                            if (args.length < 7)
                            {
                                throw new IOException("syntax error");
                            }

                            if (textButtonFactory == null)
                            {
                                throw new IOException("missing 'def textbutton' command");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final boolean autoRepeat = parseBoolean(args[6]);
                            final GUICommandList commandList = getCommandList(args[7]);
                            final String text = parseText(args, 8, lnr);
                            elements.insert(name, textButtonFactory.newTextButton(window, name, x, y, w, h, text, autoRepeat, commandList));
                        }
                        else if (gui != null && args[0].equals("textgauge"))
                        {
                            if (args.length < 13)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage picturePositive = getPicture(args[6]);
                            final BufferedImage pictureNegative = args[7].equals("null") ? null : getPicture(args[7]);
                            final BufferedImage pictureEmpty = getPicture(args[8]);
                            final GaugeUpdater gaugeUpdater = parseGaugeUpdater(args[9], window.getExperienceTable());
                            final Orientation orientation = parseOrientation(args[10]);
                            final Color color = parseColor(args[11]);
                            final Font font = fonts.lookup(args[12]);
                            final String tooltipPrefix = parseText(args, 13, lnr);
                            final GUITextGauge element = new GUITextGauge(window, name, x, y, w, h, picturePositive, pictureNegative, pictureEmpty, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
                            elements.insert(name, element);
                            gaugeUpdater.setGauge(element);
                        }
                        else if (args[0].equals("tooltip"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final Font font = fonts.lookup(args[1]);
                            final GUIHTMLLabel tooltipLabel = new GUIHTMLLabel(window, "tooltip", 0, 0, 1, 1, null, font, Color.BLACK, Color.WHITE, "");
                            tooltipLabel.setAutoResize(true);
                            window.getWindowRenderer().setTooltip(tooltipLabel);
                        }
                        else
                        {
                            throw new IOException("unknown keyword '"+args[0]+"'");
                        }
                    }
                }
                catch (final IOException ex)
                {
                    throw new IOException(ex.getMessage()+" in line "+lnr.getLineNumber());
                }
                catch (final JXCSkinException ex)
                {
                    throw new IOException(ex.getMessage()+" in line "+lnr.getLineNumber());
                }
                catch (final IllegalArgumentException ex)
                {
                    final String msg = ex.getMessage();
                    if (msg != null)
                    {
                        throw new IOException("invalid parameter ("+ex.getMessage()+") in line "+lnr.getLineNumber());
                    }
                    else
                    {
                        throw new IOException("invalid parameter in line "+lnr.getLineNumber());
                    }
                }
                finally
                {
                    lnr.close();
                }
            }
            finally
            {
                isr.close();
            }
        }
        catch (final IOException ex)
        {
            throw new JXCSkinException(getURI(resourceName)+": "+ex.getMessage());
        }

        assert gui != null || !elements.iterator().hasNext();

        final Map<GUIElement, GUIElement> wildcardElements = new LinkedHashMap<GUIElement, GUIElement>();
        for (final GUIElement element : elements)
        {
            wildcardElements.put(element, element);
        }
        for (final GUIElement element : addedElements)
        {
            wildcardElements.remove(element);
        }

        int i = 0;
        if (addedElementsContainsWildcard)
        {
            while (i < addedElements.size())
            {
                final GUIElement element = addedElements.get(i);
                if (element == null)
                {
                    break;
                }
                gui.add(element);
                i++;
            }
            assert i < addedElements.size();
            i++;
        }

        for (final GUIElement element : wildcardElements.keySet())
        {
            gui.add(element);
        }

        while (i < addedElements.size())
        {
            final GUIElement element = addedElements.get(i);
            if (element != null)
            {
                gui.add(element);
            }
            i++;
        }
    }

    /**
     * Parse an integer constant. Valid constants are "3", "3+4", and "1+2-3+4".
     *
     * @param str The integer constant string to parse.
     *
     * @return The integer value.
     *
     * @throws IOException if a parsing error occurs
     */
    private int parseInt(final String str) throws IOException
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (final NumberFormatException ex)
        {
            // ignore
        }

        Matcher matcher = patternExpr.matcher(str);
        if (!matcher.matches())
        {
            throw new IOException("invalid number: "+str);
        }
        int value;
        try
        {
            value = parseIntegerConstant(matcher.group(1));
            for (;;)
            {
                final boolean negative = matcher.group(2).equals("-");
                final String rest = matcher.group(3);

                matcher = patternExpr.matcher(rest);
                if (!matcher.matches())
                {
                    final int valueRest = Integer.parseInt(rest);
                    if (negative)
                    {
                        value -= valueRest;
                    }
                    else
                    {
                        value += valueRest;
                    }
                    break;
                }

                final int valueRest = parseIntegerConstant(matcher.group(1));
                if (negative)
                {
                    value -= valueRest;
                }
                else
                {
                    value += valueRest;
                }
            }
        }
        catch (final NumberFormatException ex)
        {
            throw new IOException("invalud number: "+str);
        }

        return value;
    }

    private int parseIntegerConstant(final String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (final NumberFormatException ex)
        {
            if (str.equals("WIDTH"))
            {
                return selectedResolution.getWidth();
            }

            if (str.equals("HEIGHT"))
            {
                return selectedResolution.getHeight();
            }

            if (str.equals("WIDTH/2"))
            {
                return selectedResolution.getWidth()/2;
            }

            if (str.equals("HEIGHT/2"))
            {
                return selectedResolution.getHeight()/2;
            }

            throw ex;
        }
    }

    /**
     * Parse a float constant.
     *
     * @param str The floating constant string to parse.
     *
     * @return The floating value.
     *
     * @throws IOException if a parsing error occurs
     */
    private static float parseFloat(final String str) throws IOException
    {
        try
        {
            return Float.parseFloat(str);
        }
        catch (final NumberFormatException ex)
        {
            throw new IOException("invalid number: "+str);
        }
    }

    /**
     * Parse a boolean constant.
     *
     * @param str The boolean constant string to parse.
     *
     * @return The boolean value.
     *
     * @throws IOException If a parsing error occurs.
     */
    private static boolean parseBoolean(final String str) throws IOException
    {
        try
        {
            return Boolean.parseBoolean(str);
        }
        catch (final NumberFormatException ex)
        {
            throw new IOException("invalid boolean: "+str);
        }
    }

    /**
     * Parse an enum constant.
     *
     * @param class_ The enum class the enum constant belongs to.
     *
     * @param name The enum constant to parse.
     *
     * @param ident The description of the enum class for building error
     * messages.
     *
     * @return The enum constant.
     *
     * @throws IOException if the enum constant does not exist
     */
    private static <T extends Enum<T>> T parseEnum(final Class<T> class_, final String name, final String ident) throws IOException
    {
        try
        {
            return Enum.valueOf(class_, name);
        }
        catch (final IllegalArgumentException ex)
        {
            throw new IOException("no such "+ident+" type: "+name);
        }
    }

    /**
     * Parse a stat value.
     *
     * @param name The stat value to parse.
     *
     * @return The stat value.
     *
     * @throws IOException if the stat value does not exist.
     */
    private static int parseStat(final String name) throws IOException
    {
        try
        {
            return StatsParser.parseStat(name);
        }
        catch (final IllegalArgumentException ex)
        {
            // ignore
        }

        throw new IOException("invalid stat name: "+name);
    }

    /**
     * Parse a orientation value.
     *
     * @param name The orientation value to parse.
     *
     * @return The orientation.
     *
     * @throws IOException if the orientation value does not exist.
     */
    private static Orientation parseOrientation(final String name) throws IOException
    {
        try
        {
            return OrientationParser.parseOrientation(name);
        }
        catch (final IllegalArgumentException ex)
        {
            // ignore
        }

        throw new IOException("invalid orientation: "+name);
    }

    /**
     * Parse a gauge updater value.
     *
     * @param name The gauge updater value to parse.
     *
     * @param experienceTable The experience table to query.
     *
     * @return The gauge updater.
     *
     * @throws IOException if the gauge updater value does not exist.
     */
    private static GaugeUpdater parseGaugeUpdater(final String name, final ExperienceTable experienceTable) throws IOException
    {
        try
        {
            return new StatGaugeUpdater(experienceTable, StatsParser.parseStat(name));
        }
        catch (final IllegalArgumentException ex)
        {
            // ignore
        }

        if (name.startsWith("SKILL_"))
        {
            return new SkillGaugeUpdater(experienceTable, Stats.getNamedSkill(name.substring(6).replaceAll("_", " ")));
        }

        if (name.startsWith("ACTIVE_SKILL_"))
        {
            return new ActiveSkillGaugeUpdater(experienceTable, name.substring(13).replaceAll("_", " "));
        }

        throw new IOException("invalid stat name: "+name);
    }

    /**
     * Parse a color name.
     *
     * @param name The color name to parse.
     *
     * @return The color.
     *
     * @throws IOException if the color name does not exist.
     */
    private Color parseColor(final String name) throws IOException
    {
        if (name.equals("BLACK")) return Color.BLACK;
        if (name.equals("DARK_GRAY")) return Color.DARK_GRAY;
        if (name.equals("GRAY")) return Color.GRAY;
        if (name.equals("WHITE")) return Color.WHITE;
        if (name.length() == 7 && name.charAt(0) == '#' && name.charAt(1) != '-')
        {
            try
            {
                return new Color(Integer.parseInt(name.substring(1), 16));
            }
            catch (final NumberFormatException ex)
            {
                ; // ignore
            }
        }
        throw new IOException("unknown color name "+name);
    }

    /**
     * Concatenate trailing arguments into a string. If the first line is
     * "<<EOF", all text up to the next line containing only "EOF" is appended.
     * Comments starting with "#" are dropped.
     *
     * @param args The args to concatenate.
     *
     * @param startIndex The first index to concatenate.
     *
     * @param lnr Where to read additional lines from.
     *
     * @return The concatenated string.
     *
     * @throws IOException If reading from <code>lnr</lnr> fails.
     */
    private String parseText(final String[] args, final int startIndex, final LineNumberReader lnr) throws IOException
    {
        final StringBuilder text = new StringBuilder();
        for (int i = startIndex; i < args.length; i++)
        {
            if (i > startIndex)
            {
                text.append(' ');
            }
            text.append(args[i]);
        }
        if (text.toString().equals("<<EOF"))
        {
            text.setLength(0);
            for (;;)
            {
                final String line = lnr.readLine();
                if (line == null)
                {
                    throw new IOException();
                }
                if (line.equals("EOF"))
                {
                    break;
                }
                if (line.startsWith("#"))
                {
                    continue;
                }

                text.append(line);
                text.append('\n');
            }
            if (text.length() > 0)
            {
                text.setLength(text.length()-1);
            }
        }

        return text.toString();
    }

    /**
     * Parse a check box option name.
     *
     * @param name The check box option name to parse.
     *
     * @param window The current window.
     *
     * @return The check box option.
     *
     * @throws IOException If the check box option name does not exist.
     */
    private CheckBoxOption parseCheckBoxOption(final String name, final JXCWindow window) throws IOException
    {
        try
        {
            return window.getOptionManager().getCheckBoxOption(name);
        }
        catch (final OptionException ex)
        {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Parse and build command arguments.
     *
     * @param args The list of arguments.
     *
     * @param argc The start index for parsing.
     *
     * @param element The target element.
     *
     * @param command The command to parse the arguments of.
     *
     * @param window The window instance.
     *
     * @param lnr The source to read more parameters from.
     *
     * @return The command arguments.
     *
     * @throws IOException If a syntax error occurs.
     *
     * @throws JXCSkinException If an element cannot be found.
     */
    private GUICommand.Parameter parseCommandArgs(final String[] args, final int argc, final GUIElement element, final String command, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
    {
        if (command.equals("SHOW"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.ShowParameter(element);
        }
        else if (command.equals("HIDE"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.HideParameter(element);
        }
        else if (command.equals("TOGGLE"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.ToggleParameter(element);
        }
        else if (command.equals("PRINT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.PrintParameter();
        }
        else if (command.equals("QUIT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.QuitParameter(window);
        }
        else if (command.equals("CONNECT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            if (!(element instanceof GUIText))
            {
                throw new IOException("'"+element+"' must be an input field");
            }

            return new GUICommand.ConnectParameter(window, (GUIText)element);
        }
        else if (command.equals("DISCONNECT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.DisconnectParameter(window);
        }
        else if (command.equals("GUI_META"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.MetaParameter(window);
        }
        else if (command.equals("GUI_START"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.StartParameter(window);
        }
        else if (command.equals("GUI_EXECUTE_ELEMENT"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            if (!(element instanceof GUIItem))
            {
                throw new IOException("'"+element+"' must be an item element");
            }

            return new GUICommand.ExecuteElementParameter(window, (GUIItem)element);
        }
        else if (command.equals("DIALOG_OPEN"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.DialogOpenParameter(window, addDialog(args[argc], window));
        }
        else if (command.equals("DIALOG_TOGGLE"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.DialogToggleParameter(window, addDialog(args[argc], window));
        }
        else if (command.equals("DIALOG_CLOSE"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            return new GUICommand.DialogCloseParameter(window, addDialog(args[argc], window));
        }
        else if (command.equals("GUI_EXECUTE_COMMAND"))
        {
            if (args.length < argc+1)
            {
                throw new IOException("syntax error");
            }

            final String commandString = parseText(args, argc, lnr);
            return new GUICommand.ExecuteCommandParameter(window, commandString);
        }
        else if (command.equals("SCROLL") || command.equals("SCROLL_NEVER"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            final int distance = parseInt(args[argc]);
            if (distance == 0)
            {
                throw new IOException("Invalid zero scroll distance");
            }

            if (!(element instanceof GUIScrollable))
            {
                throw new IOException("'"+element+"' must be a scrollable element");
            }

            return command.equals("SCROLL") ? new GUICommand.ScrollParameter(distance, (GUIScrollable)element) : new GUICommand.ScrollNeverParameter(distance, (GUIScrollable)element);
        }
        else if (command.equals("SCROLL_RESET"))
        {
            if (args.length != argc)
            {
                throw new IOException("syntax error");
            }

            if (!(element instanceof GUIScrollable))
            {
                throw new IOException("'"+element+"' must be a scrollable element");
            }

            return new GUICommand.ScrollResetParameter((GUIScrollable)element);
        }
        else if (command.equals("SCROLLNEXT"))
        {
            if (args.length != argc+1)
            {
                throw new IOException("syntax error");
            }

            final GUIElement nextElement = elements.lookup(args[argc]);
            if (!(nextElement instanceof ActivatableGUIElement))
            {
                throw new IOException("'"+args[argc]+"' cannot become active");
            }

            if (!(element instanceof ActivatableGUIElement))
            {
                throw new IOException("'"+element+"' cannot become active");
            }

            return new GUICommand.ScrollNextParameter((ActivatableGUIElement)nextElement, (ActivatableGUIElement)element);
        }
        else
        {
            throw new JXCSkinException("unknown command '"+command+"'");
        }
    }

    /**
     * Return a font by font file base name.
     *
     * @param name The file base name of the font file to load.
     *
     * @return The font.
     *
     * @throws IOException if the font cannot be loaded.
     */
    private Font getFont(final String name) throws IOException
    {
        final String filename = "fonts/"+name+".ttf";

        final Font font;
        try
        {
            final InputStream ttf = getInputStream(filename);
            try
            {
                try
                {
                    font = Font.createFont(Font.TRUETYPE_FONT, ttf);
                }
                catch (final FontFormatException ex)
                {
                    throw new IOException(filename+": invalid font format: "+ex.getMessage());
                }
            }
            finally
            {
                ttf.close();
            }
        }
        catch (final IOException ex)
        {
            throw new IOException(getURI(filename)+": i/o error: "+ex.getMessage());
        }
        return font;
    }

    /**
     * Load an picture by base file name.
     *
     * @param name The base file name.
     *
     * @return The image.
     *
     * @throws IOException if the picture cannot be loaded
     */
    private BufferedImage getPicture(final String name) throws IOException
    {
        try
        {
            return images.lookup(name);
        }
        catch (final JXCSkinException ex)
        {
            // ignore
        }

        final String filename = "pictures/"+name+".png";
        final BufferedImage picture;
        final InputStream inputStream = getInputStream(filename);
        try
        {
            picture = ImageIO.read(inputStream);
        }
        finally
        {
            inputStream.close();
        }
        if (picture == null)
        {
            throw new IOException("picture '"+getURI(filename)+"' does not exist");
        }
        try
        {
            images.insert(name, picture);
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError();
        }
        return picture;
    }

    /**
     * Return a {@link GUIText} by element name.
     *
     * @param name The element name.
     *
     * @return The <code>GUIText</code> element.
     *
     * @throws JXCSkinException if the element name is undefined
     */
    private GUIText lookupTextElement(final String name) throws JXCSkinException
    {
        final GUIElement element = elements.lookup(name);
        if (!(element instanceof GUIText))
        {
            throw new JXCSkinException("element "+name+" is not a text field");
        }

        return (GUIText)element;
    }

    /**
     * Return a {@link AbstractLabel} by element name.
     *
     * @param name The element name.
     *
     * @return The <code>AbstractLabel</code> element.
     *
     * @throws JXCSkinException if the element name is undefined
     */
    private AbstractLabel lookupLabelElement(final String name) throws JXCSkinException
    {
        final GUIElement element = elements.lookup(name);
        if (!(element instanceof AbstractLabel))
        {
            throw new JXCSkinException("element "+name+" is not a label");
        }

        return (AbstractLabel)element;
    }

    /** {@inheritDoc} */
    public Iterator<Gui> iterator()
    {
        return dialogs.iterator();
    }

    /** {@inheritDoc} */
    public void executeInitEvents()
    {
        for (final GUICommandList commandList : initEvents)
        {
            commandList.execute();
        }
    }

    /** {@inheritDoc} */
    public GUICommandList getCommandList(final String name) throws JXCSkinException
    {
        return commandLists.lookup(name);
    }

    /**
     * Splits a line into tokens. Handles quoting ("...").
     * @param line the line
     * @return the tokens
     */
    private String[] split(final String line) throws JXCSkinException
    {
        final List<String> tokens = new ArrayList<String>(64);

        final char[] chars = line.toCharArray();

        int i = 0;
        while (i < chars.length)
        {
            while (i < chars.length && (chars[i] == ' ' || chars[i] == '\t'))
            {
                i++;
            }
            final int start;
            final int end;
            if (i < chars.length && (chars[i] == '"' || chars[i] == '\''))
            {
                // quoted token
                final char quoteChar = chars[i++];
                start = i;
                while (i < chars.length && chars[i] != quoteChar)
                {
                    i++;
                }
                if (i >= chars.length)
                {
                    throw new JXCSkinException("unterminated token: "+line.substring(start-1));
                }
                end = i;
                i++;
            }
            else
            {
                // unquoted token
                start = i;
                while (i < chars.length && (chars[i] != ' ' && chars[i] != '\t'))
                {
                    i++;
                }
                end = i;
            }
            tokens.add(line.substring(start, end));
        }

        return tokens.toArray(new String[tokens.size()]);
    }
}

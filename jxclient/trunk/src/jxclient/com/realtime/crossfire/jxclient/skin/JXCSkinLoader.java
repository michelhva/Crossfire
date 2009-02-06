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

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.GUIButton;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIHTMLLabel;
import com.realtime.crossfire.jxclient.gui.GUILabel;
import com.realtime.crossfire.jxclient.gui.GUILabelQuery;
import com.realtime.crossfire.jxclient.gui.GUILabelStats;
import com.realtime.crossfire.jxclient.gui.GUIMagicMap;
import com.realtime.crossfire.jxclient.gui.GUIMap;
import com.realtime.crossfire.jxclient.gui.GUIMultiLineLabel;
import com.realtime.crossfire.jxclient.gui.GUIOneLineLabel;
import com.realtime.crossfire.jxclient.gui.GUIPicture;
import com.realtime.crossfire.jxclient.gui.GUIScrollBar;
import com.realtime.crossfire.jxclient.gui.GUIScrollable2;
import com.realtime.crossfire.jxclient.gui.GUISpellLabel;
import com.realtime.crossfire.jxclient.gui.GUITextButton;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.commands.GUICommand;
import com.realtime.crossfire.jxclient.gui.gauge.GUIDupGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUIDupTextGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUIGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GUITextGauge;
import com.realtime.crossfire.jxclient.gui.gauge.GaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.Orientation;
import com.realtime.crossfire.jxclient.gui.item.GUIItem;
import com.realtime.crossfire.jxclient.gui.item.GUIItemFloor;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventoryFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemShortcut;
import com.realtime.crossfire.jxclient.gui.item.GUIItemSpelllist;
import com.realtime.crossfire.jxclient.gui.item.ItemPainter;
import com.realtime.crossfire.jxclient.gui.keybindings.InvalidKeyBindingException;
import com.realtime.crossfire.jxclient.gui.keybindings.KeyBindings;
import com.realtime.crossfire.jxclient.gui.list.GUIItemInventoryList;
import com.realtime.crossfire.jxclient.gui.list.GUIMetaElementList;
import com.realtime.crossfire.jxclient.gui.log.Fonts;
import com.realtime.crossfire.jxclient.gui.log.GUILabelLog;
import com.realtime.crossfire.jxclient.gui.log.GUIMessageLog;
import com.realtime.crossfire.jxclient.gui.log.MessageBufferUpdater;
import com.realtime.crossfire.jxclient.gui.textinput.GUICommandText;
import com.realtime.crossfire.jxclient.gui.textinput.GUIQueryText;
import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.gui.textinput.GUITextField;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.mapupdater.MapscrollListener;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireCommandMagicmapEvent;
import com.realtime.crossfire.jxclient.server.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.DefaultCrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import com.realtime.crossfire.jxclient.server.UnknownCommandException;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.CommandCheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillListener;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.NumberParser;
import com.realtime.crossfire.jxclient.util.StringUtils;
import com.realtime.crossfire.jxclient.window.ConnectionStateListener;
import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.window.MouseTracker;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates a {@link JXCSkin} instance from a file.
 * @author Andreas Kirschbaum
 */
public abstract class JXCSkinLoader implements JXCSkin
{
    /**
     * The default number of ground view objects.
     */
    private static final int DEFAULT_NUM_LOOK_OBJECTS = 50;

    /**
     * The {@link ItemsManager} instance to use.
     */
    private final ItemsManager itemsManager;

    /**
     * The {@link SpellsManager} instance to use.
     */
    private final SpellsManager spellsManager;

    /**
     * The {@link FacesManager} instance to use.
     */
    private final FacesManager facesManager;

    /**
     * The {@link Stats} instance to use.
     */
    private final Stats stats;

    /**
     * The {@link CfMapUpdater} instance to use.
     */
    private final CfMapUpdater mapUpdater;

    /**
     * Available resolutions for this skin.
     */
    private final Set<Resolution> resolutions = new HashSet<Resolution>();

    /**
     * All defined gui elements.
     */
    private final JXCSkinCache<GUIElement> definedGUIElements = new JXCSkinCache<GUIElement>("gui element");

    /**
     * All defined command lists.
     */
    private final JXCSkinCache<GUICommandList> definedCommandLists = new JXCSkinCache<GUICommandList>("command list");

    /**
     * All defined dialogs.
     */
    private final Dialogs dialogs = new Dialogs();

    /**
     * All defined fonts.
     */
    private final JXCSkinCache<Font> definedFonts = new JXCSkinCache<Font>("font");

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
     * The maximum number of ground view objects.
     */
    private int numLookObjects = DEFAULT_NUM_LOOK_OBJECTS;

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
     * All "event init" commands in execution order.
     */
    private final List<GUICommandList> initEvents = new ArrayList<GUICommandList>();

    /**
     * The default key bindings.
     */
    private final KeyBindings defaultKeyBindings;

    /**
     * The {@link ExpressionParser} for parsing integer constant expressions.
     */
    private ExpressionParser expressionParser = new ExpressionParser(selectedResolution);

    /**
     * The {@link CommandParser} for parsing command specifications.
     */
    private final CommandParser commandParser;

    /**
     * The {@link ImageParser} for parsing image specifications.
     */
    private final ImageParser imageParser = new ImageParser(this);

    /**
     * The {@link FontParser} for parsing font specifications.
     */
    private final FontParser fontParser = new FontParser(this);

    /**
     * The {@link GaugeUpdaterParser} for parsing gauge specifications.
     */
    private final GaugeUpdaterParser gaugeUpdaterParser;

    /**
     * Creates a new instance.
     * @param itemsManager the items manager instance to use
     * @param spellsManager the spells manager instance to use
     * @param facesManager the faces manager instance to use
     * @param stats the stats instance to use
     * @param mapUpdater the map updater instance to use
     * @param defaultKeyBindings the default key bindings
     */
    protected JXCSkinLoader(final ItemsManager itemsManager, final SpellsManager spellsManager, final FacesManager facesManager, final Stats stats, final CfMapUpdater mapUpdater, final KeyBindings defaultKeyBindings)
    {
        this.itemsManager = itemsManager;
        this.spellsManager = spellsManager;
        this.facesManager = facesManager;
        this.stats = stats;
        this.mapUpdater = mapUpdater;
        this.defaultKeyBindings = defaultKeyBindings;
        commandParser = new CommandParser(dialogs, itemsManager, expressionParser, definedGUIElements);
        gaugeUpdaterParser = new GaugeUpdaterParser(stats, itemsManager);
    }

    /**
     * Checks that the skin exists and can be accessed.
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
    @Override
    public void load(final CrossfireServerConnection crossfireServerConnection, final JXCWindow window, final MouseTracker mouseTracker, final MetaserverModel metaserverModel, final CommandQueue commandQueue, final Resolution resolution, final OptionManager optionManager, final ExperienceTable experienceTable, final Shortcuts shortcuts, final Commands commands, final CurrentSpellManager currentSpellManager) throws JXCSkinException
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

        expressionParser = new ExpressionParser(selectedResolution);
        skinName = "unknown";
        mapWidth = 0;
        mapHeight = 0;
        numLookObjects = DEFAULT_NUM_LOOK_OBJECTS;
        dialogs.clear();
        imageParser.clear();
        dialogs.addDialog("keybind", window, mouseTracker, commands);
        dialogs.addDialog("query", window, mouseTracker, commands);
        dialogs.addDialog("book", window, mouseTracker, commands);
        dialogs.addDialog("main", window, mouseTracker, commands);
        dialogs.addDialog("meta", window, mouseTracker, commands);
        dialogs.addDialog("quit", window, mouseTracker, commands);
        dialogs.addDialog("disconnect", window, mouseTracker, commands);
        dialogs.addDialog("start", window, mouseTracker, commands);
        definedCommandLists.clear();
        definedFonts.clear();
        textButtonFactory = null;
        dialogFactory = null;
        checkBoxFactory = null;
        try
        {
            load("global", selectedResolution, crossfireServerConnection, window, mouseTracker, metaserverModel, commandQueue, null, optionManager, experienceTable, shortcuts, commands, currentSpellManager);
            for (;;)
            {
                final String name = dialogs.getDialogToLoad();
                if (name == null)
                {
                    break;
                }
                final Gui gui = dialogs.lookup(name);
                load(name, selectedResolution, crossfireServerConnection, window, mouseTracker, metaserverModel, commandQueue, gui, optionManager, experienceTable, shortcuts, commands, currentSpellManager);
                gui.setStateChanged(false);
            }
        }
        finally
        {
            definedFonts.clear();
            textButtonFactory = null;
            dialogFactory = null;
            checkBoxFactory = null;
            imageParser.clear();
        }

        if (mapWidth == 0 || mapHeight == 0)
        {
            throw new JXCSkinException("Missing map command");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getSkinName()
    {
        return skinName+"@"+selectedResolution;
    }

    /** {@inheritDoc} */
    @Override
    public Resolution getResolution()
    {
        return selectedResolution;
    }

    /** {@inheritDoc} */
    @Override
    public int getMapWidth()
    {
        return mapWidth;
    }

    /** {@inheritDoc} */
    @Override
    public int getMapHeight()
    {
        return mapHeight;
    }

    /** {@inheritDoc} */
    @Override
    public int getNumLookObjects()
    {
        return numLookObjects;
    }

    /** {@inheritDoc} */
    @Override
    public Gui getDialogQuit()
    {
        try
        {
            return getDialog("quit");
        }
        catch (final JXCSkinException ex)
        {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Gui getDialogDisconnect()
    {
        try
        {
            return getDialog("disconnect");
        }
        catch (final JXCSkinException ex)
        {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Gui getDialogKeyBind()
    {
        try
        {
            return getDialog("keybind");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("keybind dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Gui getDialogQuery()
    {
        try
        {
            return getDialog("query");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("query dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Gui getDialogBook(final int booknr)
    {
        try
        {
            return getDialog("book");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("book dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Gui getMainInterface()
    {
        try
        {
            return getDialog("main");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("main dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Gui getMetaInterface()
    {
        try
        {
            return getDialog("meta");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("meta dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Gui getStartInterface()
    {
        try
        {
            return getDialog("start");
        }
        catch (final JXCSkinException ex)
        {
            throw new AssertionError("start dialog does not exist");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Gui getDialog(final String name) throws JXCSkinException
    {
        return dialogs.lookup(name);
    }

    /**
     * Loads a skin file and add the entries to a {@link Gui} instance.
     * @param dialogName the key to identify this dialog
     * @param resolution the preferred resolution
     * @param server the server connection to monitor
     * @param window the main window
     * @param mouseTracker the mouse tracker instance
     * @param metaserverModel the metaserver model to use
     * @param commandQueue the command queue for sending commands
     * @param gui the Gui representing the skin file
     * @param optionManager the option manager instance to use
     * @param experienceTable the experience table to use
     * @param shortcuts the shortcuts instance
     * @param commands the commands instance for executing commands
     * @param currentSpellManager the current spell manager to use
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(final String dialogName, final Resolution resolution, final CrossfireServerConnection server, final JXCWindow window, final MouseTracker mouseTracker, final MetaserverModel metaserverModel, final CommandQueue commandQueue, final Gui gui, final OptionManager optionManager, final ExperienceTable experienceTable, final Shortcuts shortcuts, final Commands commands, final CurrentSpellManager currentSpellManager) throws JXCSkinException
    {
        String resourceName = dialogName+"@"+resolution+".skin";

        definedGUIElements.clear();
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
                load(dialogName, resourceName, inputStream, server, window, mouseTracker, metaserverModel, commandQueue, gui, optionManager, experienceTable, shortcuts, commands, currentSpellManager);
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
            definedGUIElements.clear();
        }
    }

    /**
     * Returns an {@link InputStream} for a resource name.
     * @param name the resource name
     * @return the input stream for the resource
     * @throws IOException if the resource cannot be loaded
     */
    protected abstract InputStream getInputStream(final String name) throws IOException;

    /**
     * Returns a description of the location of a resource name.
     * @param name the resource name
     * @return the description of the resource
     */
    protected abstract String getURI(final String name);

    /**
     * Loads a skin file and add the entries to a {@link Gui} instance.
     * @param dialogName the key to identify this dialog
     * @param resourceName the name of the skin resource; used to construct
     * error messages
     * @param inputStream the input stream to load from
     * @param server the server connection to monitor
     * @param window the main window
     * @param mouseTracker the mouse tracker instance
     * @param metaserverModel the metaserver model to use
     * @param commandQueue the command queue for sending commands
     * @param gui the Gui representing the skin file
     * @param optionManager the option manager instance to use
     * @param experienceTable the experience table to use
     * @param shortcuts the shortcuts instance
     * @param commands the commands instance for executing commands
     * @param currentSpellManager the current spell manager to use
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(final String dialogName, final String resourceName, final InputStream inputStream, final CrossfireServerConnection server, final JXCWindow window, final MouseTracker mouseTracker, final MetaserverModel metaserverModel, final CommandQueue commandQueue, final Gui gui, final OptionManager optionManager, final ExperienceTable experienceTable, final Shortcuts shortcuts, final Commands commands, final CurrentSpellManager currentSpellManager) throws JXCSkinException
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

                        final String[] args = StringUtils.splitFields(line);
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
                                addedElements.add(definedGUIElements.lookup(args[1]));
                            }
                        }
                        else if (gui != null && args[0].equals("button"))
                        {
                            if (args.length != 10 && args.length < 14)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage upImage = imageParser.getImage(args[6]);
                            final BufferedImage downImage = imageParser.getImage(args[7]);
                            final boolean autoRepeat = NumberParser.parseBoolean(args[8]);
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
                                font = definedFonts.lookup(args[10]);
                                color = ParseUtils.parseColor(args[11]);
                                textX = expressionParser.parseInt(args[12]);
                                textY = expressionParser.parseInt(args[13]);
                                label = ParseUtils.parseText(args, 14, lnr);
                            }
                            definedGUIElements.insert(name, new GUIButton(window, name, x, y, w, h, upImage, downImage, label, font, color, textX, textY, autoRepeat, commandList));
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
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final CheckBoxOption option = ParseUtils.parseCheckBoxOption(args[6], optionManager);
                            final String text = ParseUtils.parseText(args, 7, lnr);
                            definedGUIElements.insert(name, checkBoxFactory.newCheckBox(window, name, x, y, w, h, option, text));
                        }
                        else if (args[0].equals("commandlist"))
                        {
                            if (args.length != 3 && args.length < 5)
                            {
                                throw new IOException("syntax error");
                            }

                            final String commandListName = args[1];
                            final GUICommandList.CommandType commandListCommandType = NumberParser.parseEnum(GUICommandList.CommandType.class, args[2], "type");
                            final GUICommandList commandList = new GUICommandList(commandListCommandType);
                            definedCommandLists.insert(commandListName, commandList);
                            if (args.length >= 5)
                            {
                                final GUIElement element = args[3].equals("null") ? null : definedGUIElements.lookup(args[3]);
                                final GUICommand command = commandParser.parseCommandArgs(args, 5, element, args[4], window, mouseTracker, commands, lnr, commandQueue, server);
                                commandList.add(command);
                            }
                        }
                        else if (args[0].equals("commandlist_add"))
                        {
                            if (args.length < 4)
                            {
                                throw new IOException("syntax error");
                            }

                            final GUICommandList commandList = getCommandList(args[1]);
                            final GUIElement element = args[2].equals("null") ? null : definedGUIElements.lookup(args[2]);
                            final GUICommand command = commandParser.parseCommandArgs(args, 4, element, args[3], window, mouseTracker, commands, lnr, commandQueue, server);
                            commandList.add(command);
                        }
                        else if (gui != null && args[0].equals("command_text"))
                        {
                            if (args.length != 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage activeImage = imageParser.getImage(args[6]);
                            final BufferedImage inactiveImage = imageParser.getImage(args[7]);
                            final Font font = definedFonts.lookup(args[8]);
                            final Color inactiveColor = ParseUtils.parseColor(args[9]);
                            final Color activeColor = ParseUtils.parseColor(args[10]);
                            final int margin = expressionParser.parseInt(args[11]);
                            definedGUIElements.insert(name, new GUICommandText(window, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", commands, false));
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

                                final BufferedImage checkedImage = imageParser.getImage(args[2]);
                                final BufferedImage uncheckedImage = imageParser.getImage(args[3]);
                                final Font font = definedFonts.lookup(args[4]);
                                final Color color = ParseUtils.parseColor(args[5]);
                                checkBoxFactory = new CheckBoxFactory(checkedImage, uncheckedImage, font, color);
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
                                final String documentation = ParseUtils.parseText(args, 5, lnr);
                                try
                                {
                                    optionManager.addOption(optionName, documentation, new CommandCheckBoxOption(commandOn, commandOff));
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
                                final BufferedImage frameNW = imageParser.getImage(frame+"_nw");
                                final BufferedImage frameN = imageParser.getImage(frame+"_n");
                                final BufferedImage frameNE = imageParser.getImage(frame+"_ne");
                                final BufferedImage frameW = imageParser.getImage(frame+"_w");
                                final BufferedImage frameC = imageParser.getImage(frame+"_c");
                                final BufferedImage frameE = imageParser.getImage(frame+"_e");
                                final BufferedImage frameSW = imageParser.getImage(frame+"_sw");
                                final BufferedImage frameS = imageParser.getImage(frame+"_s");
                                final BufferedImage frameSE = imageParser.getImage(frame+"_se");
                                final Font titleFont = definedFonts.lookup(args[3]);
                                final Color titleColor = ParseUtils.parseColor(args[4]);
                                final Color titleBackgroundColor = ParseUtils.parseColor(args[5]);
                                final float alpha = NumberParser.parseFloat(args[6]);
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
                                final Font font = definedFonts.lookup(args[4]);
                                final Color color = ParseUtils.parseColor(args[5]);
                                final GUITextButton.ButtonImages upImages = new GUITextButton.ButtonImages(imageParser.getImage(up+"_w"), imageParser.getImage(up+"_c"), imageParser.getImage(up+"_e"));
                                final GUITextButton.ButtonImages downImages = new GUITextButton.ButtonImages(imageParser.getImage(down+"_w"), imageParser.getImage(down+"_c"), imageParser.getImage(down+"_e"));
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
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final boolean saveDialog = NumberParser.parseBoolean(args[6]);
                            final String title = ParseUtils.parseText(args, 7, lnr);
                            for (final GUIElement element : dialogFactory.newDialog(window, name, w, h, title))
                            {
                                definedGUIElements.insert(element.getName(), element);
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
                                gui.hideInState(NumberParser.parseEnum(JXCWindowRenderer.GuiState.class, args[i], "gui state"));
                            }
                        }
                        else if (gui != null && args[0].equals("dupgauge"))
                        {
                            if (args.length < 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage positiveDivImage = imageParser.getImage(args[6]);
                            final BufferedImage positiveModImage = imageParser.getImage(args[7]);
                            final BufferedImage emptyImage = args[8].equals("null") ? null : imageParser.getImage(args[8]);
                            final GaugeUpdater gaugeUpdater = gaugeUpdaterParser.parseGaugeUpdater(args[9], experienceTable);
                            final Orientation orientationDiv = ParseUtils.parseOrientation(args[10]);
                            final Orientation orientationMod = ParseUtils.parseOrientation(args[11]);
                            final String tooltipPrefix = ParseUtils.parseText(args, 12, lnr);
                            final GUIDupGauge element = new GUIDupGauge(window, name, x, y, w, h, positiveDivImage, positiveModImage, emptyImage, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
                            definedGUIElements.insert(name, element);
                            gaugeUpdater.setGauge(element);
                        }
                        else if (gui != null && args[0].equals("duptextgauge"))
                        {
                            if (args.length < 14)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage positiveDivImage = imageParser.getImage(args[6]);
                            final BufferedImage positiveModImage = imageParser.getImage(args[7]);
                            final BufferedImage emptyImage = imageParser.getImage(args[8]);
                            final GaugeUpdater gaugeUpdater = gaugeUpdaterParser.parseGaugeUpdater(args[9], experienceTable);
                            final Orientation orientationDiv = ParseUtils.parseOrientation(args[10]);
                            final Orientation orientationMod = ParseUtils.parseOrientation(args[11]);
                            final Color color = ParseUtils.parseColor(args[12]);
                            final Font font = definedFonts.lookup(args[13]);
                            final String tooltipPrefix = ParseUtils.parseText(args, 14, lnr);
                            final GUIDupTextGauge element = new GUIDupTextGauge(window, name, x, y, w, h, positiveDivImage, positiveModImage, emptyImage, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
                            definedGUIElements.insert(name, element);
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
                                        @Override
                                        public void connect()
                                        {
                                            commandList.execute();
                                        }

                                        /** {@inheritDoc} */
                                        @Override
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
                                        @Override
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
                                mapUpdater.addCrossfireMapscrollListener(new MapscrollListener()
                                    {
                                        /** {@inheritDoc} */
                                        @Override
                                        public void mapScrolled(final int dx, final int dy)
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
                                final Skill skill = SkillSet.getNamedSkill(args[3].replaceAll("_", " "));
                                final GUICommandList commandList = getCommandList(args[4]);
                                if (subtype.equals("add"))
                                {
                                    skill.addSkillListener(new SkillListener()
                                        {
                                            /** {@inheritDoc} */
                                            @Override
                                            public void gainedSkill()
                                            {
                                                commandList.execute();
                                            }

                                            /** {@inheritDoc} */
                                            @Override
                                            public void lostSkill()
                                            {
                                                // ignore
                                            }

                                            /** {@inheritDoc} */
                                            @Override
                                            public void changedSkill()
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
                                            @Override
                                            public void gainedSkill()
                                            {
                                                // ignore
                                            }

                                            /** {@inheritDoc} */
                                            @Override
                                            public void lostSkill()
                                            {
                                                commandList.execute();
                                            }

                                            /** {@inheritDoc} */
                                            @Override
                                            public void changedSkill()
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
                            final Font fontNormal = fontParser.getFont(args[2]);
                            final Font font = fontNormal.deriveFont(NumberParser.parseFloat(args[3]));
                            definedFonts.insert(name, font);
                        }
                        else if (gui != null && args[0].equals("gauge"))
                        {
                            if (args.length < 11)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage positiveImage = args[6].equals("null") ? null : imageParser.getImage(args[6]);
                            final BufferedImage negativeImage = args[7].equals("null") ? null : imageParser.getImage(args[7]);
                            final BufferedImage emptyImage = args[8].equals("null") ? null : imageParser.getImage(args[8]);
                            final GaugeUpdater gaugeUpdater = gaugeUpdaterParser.parseGaugeUpdater(args[9], experienceTable);
                            final Orientation orientation = ParseUtils.parseOrientation(args[10]);
                            final String tooltipPrefix = ParseUtils.parseText(args, 11, lnr);
                            final GUIGauge element = new GUIGauge(window, name, x, y, w, h, positiveImage, negativeImage, emptyImage, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
                            definedGUIElements.insert(name, element);
                            gaugeUpdater.setGauge(element);
                        }
                        else if (gui != null && args[0].equals("ignore"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            definedGUIElements.lookup(name).setIgnore();
                        }
                        else if (gui != null && args[0].equals("inventory_list"))
                        {
                            if (args.length != 18)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final int cellHeight = expressionParser.parseInt(args[6]);
                            final Color cursedColor = ParseUtils.parseColorNull(args[7]);
                            final BufferedImage cursedImage = imageParser.getImage(cursedColor, args[7]);
                            final Color damnedColor = ParseUtils.parseColorNull(args[8]);
                            final BufferedImage damnedImage = imageParser.getImage(damnedColor, args[8]);
                            final Color magicColor = ParseUtils.parseColorNull(args[9]);
                            final BufferedImage magicImage = imageParser.getImage(magicColor, args[9]);
                            final Color blessedColor = ParseUtils.parseColorNull(args[10]);
                            final BufferedImage blessedImage = imageParser.getImage(blessedColor, args[10]);
                            final Color appliedColor = ParseUtils.parseColorNull(args[11]);
                            final BufferedImage appliedImage = imageParser.getImage(appliedColor, args[11]);
                            final Color selectorColor = ParseUtils.parseColorNull(args[12]);
                            final BufferedImage selectorImage = imageParser.getImage(selectorColor, args[12]);
                            final Color lockedColor = ParseUtils.parseColorNull(args[13]);
                            final BufferedImage lockedImage = imageParser.getImage(lockedColor, args[13]);
                            final Color unpaidColor = ParseUtils.parseColorNull(args[14]);
                            final BufferedImage unpaidImage = imageParser.getImage(unpaidColor, args[14]);
                            final Font font = definedFonts.lookup(args[15]);
                            final Color nrofColor = ParseUtils.parseColor(args[16]);
                            final AbstractLabel selectedItem = args[17].equals("null") ? null : lookupLabelElement(args[17]);

                            final ItemPainter itemPainter = new ItemPainter(cursedImage, damnedImage, magicImage, blessedImage, appliedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, selectorColor, lockedColor, unpaidColor, font, nrofColor, cellHeight, cellHeight);
                            final GUIItemInventoryFactory itemInventoryFactory = new GUIItemInventoryFactory(window, commandQueue, name, itemPainter, server, facesManager, itemsManager);
                            final GUIItemInventoryList element = new GUIItemInventoryList(window, commandQueue, name, x, y, w, h, cellHeight, server, itemsManager, selectedItem, itemInventoryFactory);
                            definedGUIElements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("item"))
                        {
                            if (args.length < 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String type = args[1];
                            final String name = args[2];
                            final int x = expressionParser.parseInt(args[3]);
                            final int y = expressionParser.parseInt(args[4]);
                            final int w = expressionParser.parseInt(args[5]);
                            final int h = expressionParser.parseInt(args[6]);
                            final int index = expressionParser.parseInt(args[7]);
                            final GUIItem element;
                            if (type.equals("floor"))
                            {
                                if (args.length != 18)
                                {
                                    throw new IOException("syntax error");
                                }

                                final Color cursedColor = ParseUtils.parseColorNull(args[8]);
                                final BufferedImage cursedImage = imageParser.getImage(cursedColor, args[8]);
                                final Color damnedColor = ParseUtils.parseColorNull(args[9]);
                                final BufferedImage damnedImage = imageParser.getImage(damnedColor, args[9]);
                                final Color magicColor = ParseUtils.parseColorNull(args[10]);
                                final BufferedImage magicImage = imageParser.getImage(magicColor, args[10]);
                                final Color blessedColor = ParseUtils.parseColorNull(args[11]);
                                final BufferedImage blessedImage = imageParser.getImage(blessedColor, args[11]);
                                final Color appliedColor = ParseUtils.parseColorNull(args[12]);
                                final BufferedImage appliedImage = imageParser.getImage(appliedColor, args[12]);
                                final Color selectorColor = ParseUtils.parseColorNull(args[13]);
                                final BufferedImage selectorImage = imageParser.getImage(selectorColor, args[13]);
                                final Color lockedColor = ParseUtils.parseColorNull(args[14]);
                                final BufferedImage lockedImage = imageParser.getImage(lockedColor, args[14]);
                                final Color unpaidColor = ParseUtils.parseColorNull(args[15]);
                                final BufferedImage unpaidImage = imageParser.getImage(unpaidColor, args[15]);
                                final Font font = definedFonts.lookup(args[16]);
                                final Color nrofColor = ParseUtils.parseColor(args[17]);
                                final ItemPainter itemPainter = new ItemPainter(cursedImage, damnedImage, magicImage, blessedImage, appliedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, selectorColor, lockedColor, unpaidColor, font, nrofColor, w, h);
                                element = new GUIItemFloor(window, commandQueue, name, x, y, w, h, itemPainter, index, server, itemsManager, facesManager);
                            }
                            else if (type.equals("inventory"))
                            {
                                if (args.length != 18)
                                {
                                    throw new IOException("syntax error");
                                }

                                final Color cursedColor = ParseUtils.parseColorNull(args[8]);
                                final BufferedImage cursedImage = imageParser.getImage(cursedColor, args[8]);
                                final Color damnedColor = ParseUtils.parseColorNull(args[9]);
                                final BufferedImage damnedImage = imageParser.getImage(damnedColor, args[9]);
                                final Color magicColor = ParseUtils.parseColorNull(args[10]);
                                final BufferedImage magicImage = imageParser.getImage(magicColor, args[10]);
                                final Color blessedColor = ParseUtils.parseColorNull(args[11]);
                                final BufferedImage blessedImage = imageParser.getImage(blessedColor, args[11]);
                                final Color appliedColor = ParseUtils.parseColorNull(args[12]);
                                final BufferedImage appliedImage = imageParser.getImage(appliedColor, args[12]);
                                final Color selectorColor = ParseUtils.parseColorNull(args[13]);
                                final BufferedImage selectorImage = imageParser.getImage(selectorColor, args[13]);
                                final Color lockedColor = ParseUtils.parseColorNull(args[14]);
                                final BufferedImage lockedImage = imageParser.getImage(lockedColor, args[14]);
                                final Color unpaidColor = ParseUtils.parseColorNull(args[15]);
                                final BufferedImage unpaidImage = imageParser.getImage(unpaidColor, args[15]);
                                final Font font = definedFonts.lookup(args[16]);
                                final Color nrofColor = ParseUtils.parseColor(args[17]);
                                final ItemPainter itemPainter = new ItemPainter(cursedImage, damnedImage, magicImage, blessedImage, appliedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, selectorColor, lockedColor, unpaidColor, font, nrofColor, w, h);
                                element = new GUIItemInventory(window, commandQueue, name, x, y, w, h, itemPainter, index, server, facesManager, itemsManager);
                            }
                            else if (type.equals("shortcut"))
                            {
                                if (args.length != 11)
                                {
                                    throw new IOException("syntax error");
                                }

                                final BufferedImage cursedImage = imageParser.getImage(args[8]);
                                final BufferedImage appliedImage = imageParser.getImage(args[9]);
                                final Font font = definedFonts.lookup(args[10]);
                                element = new GUIItemShortcut(window, name, x, y, w, h, cursedImage, appliedImage, index, facesManager, shortcuts, font, currentSpellManager);
                            }
                            else if (type.equals("spelllist"))
                            {
                                if (args.length != 9)
                                {
                                    throw new IOException("syntax error");
                                }

                                final BufferedImage selectorImage = imageParser.getImage(args[8]);
                                element = new GUIItemSpelllist(window, commandQueue, name, x, y, w, h, selectorImage, index, facesManager, spellsManager, currentSpellManager);
                            }
                            else
                            {
                                throw new IOException("undefined item type: "+type);
                            }
                            definedGUIElements.insert(name, element);
                        }
                        else if (args[0].equals("key"))
                        {
                            if (args.length < 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final KeyBindings keyBindings = gui != null ? gui.getKeyBindings() : defaultKeyBindings;
                            try
                            {
                                keyBindings.parseKeyBinding(line.substring(4).trim(), true);
                            }
                            catch (final InvalidKeyBindingException ex)
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
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final Font font = definedFonts.lookup(args[6]);
                            final Color color = ParseUtils.parseColor(args[7]);
                            final String text = ParseUtils.parseText(args, 8, lnr);
                            definedGUIElements.insert(name, new GUIHTMLLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), text));
                        }
                        else if (gui != null && args[0].equals("label_multi"))
                        {
                            if (args.length < 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final Font font = definedFonts.lookup(args[6]);
                            final Color color = ParseUtils.parseColor(args[7]);
                            final String text = ParseUtils.parseText(args, 8, lnr);
                            definedGUIElements.insert(name, new GUIMultiLineLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), GUILabel.Alignment.LEFT, text));
                        }
                        else if (gui != null && args[0].equals("label_query"))
                        {
                            if (args.length != 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final Font font = definedFonts.lookup(args[6]);
                            final Color color = ParseUtils.parseColor(args[7]);
                            final GUILabelQuery element = new GUILabelQuery(window, name, x, y, w, h, server, font, color, new Color(0, 0, 0, 0F));
                            definedGUIElements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("label_text"))
                        {
                            if (args.length < 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final Font font = definedFonts.lookup(args[6]);
                            final Color color = ParseUtils.parseColor(args[7]);
                            final String text = ParseUtils.parseText(args, 8, lnr);
                            definedGUIElements.insert(name, new GUIOneLineLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), GUILabel.Alignment.LEFT, text));
                        }
                        else if (gui != null && args[0].equals("label_stat"))
                        {
                            if (args.length != 10)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final Font font = definedFonts.lookup(args[6]);
                            final Color color = ParseUtils.parseColor(args[7]);
                            final int stat = ParseUtils.parseStat(args[8]);
                            final GUILabel.Alignment alignment = NumberParser.parseEnum(GUILabel.Alignment.class, args[9], "text alignment");
                            final GUILabelStats element = new GUILabelStats(window, name, x, y, w, h, font, color, new Color(0, 0, 0, 0F), stat, alignment, stats);
                            definedGUIElements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("label_spell"))
                        {
                            if (args.length != 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final Font font = definedFonts.lookup(args[6]);
                            final GUISpellLabel.Type type = NumberParser.parseEnum(GUISpellLabel.Type.class, args[7], "label type");
                            final GUISpellLabel element = new GUISpellLabel(window, name, x, y, w, h, null, facesManager, font, type, currentSpellManager);
                            definedGUIElements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("log_label"))
                        {
                            if (args.length != 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage emptyImage = imageParser.getImage(args[6]);
                            final Font fontPrint = definedFonts.lookup(args[7]);
                            final Font fontFixed = definedFonts.lookup(args[8]);
                            final Font fontFixedBold = definedFonts.lookup(args[9]);
                            final Font fontArcane = definedFonts.lookup(args[10]);
                            final Color defaultColor = ParseUtils.parseColor(args[11]);
                            final Fonts fonts = new Fonts(fontPrint, fontFixed, fontFixedBold, fontArcane);
                            final GUILabelLog element = new GUILabelLog(window, name, x, y, w, h, emptyImage, fonts, defaultColor);
                            definedGUIElements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("log_message"))
                        {
                            if (args.length != 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage emptyImage = imageParser.getImage(args[6]);
                            final Font fontPrint = definedFonts.lookup(args[7]);
                            final Font fontFixed = definedFonts.lookup(args[8]);
                            final Font fontFixedBold = definedFonts.lookup(args[9]);
                            final Font fontArcane = definedFonts.lookup(args[10]);
                            final Color defaultColor = ParseUtils.parseColor(args[11]);
                            final Fonts fonts = new Fonts(fontPrint, fontFixed, fontFixedBold, fontArcane);
                            final GUIMessageLog element = new GUIMessageLog(window, name, x, y, w, h, server, emptyImage, fonts, defaultColor);
                            definedGUIElements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("log_color"))
                        {
                            if (args.length != 4)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int index = expressionParser.parseInt(args[2]);
                            final Color color = ParseUtils.parseColor(args[3]);
                            final GUIElement element = definedGUIElements.lookup(name);
                            if (!(element instanceof GUIMessageLog))
                            {
                                 throw new IOException("element '"+name+"' is not of type 'log'");
                            }
                            if (index < 0 || index >= MessageBufferUpdater.NUM_COLORS)
                            {
                                throw new IOException("invalid color index "+index);
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
                            final GUIElement element = definedGUIElements.lookup(name);
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
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final GUIMagicMap element = new GUIMagicMap(window, name, x, y, w, h, server, mapUpdater, facesManager);
                            definedGUIElements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("map"))
                        {
                            if (args.length != 7)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int tileSize = expressionParser.parseInt(args[2]);
                            final int x = expressionParser.parseInt(args[3]);
                            final int y = expressionParser.parseInt(args[4]);
                            final int w = expressionParser.parseInt(args[5]);
                            final int h = expressionParser.parseInt(args[6]);

                            if (tileSize <= 0) throw new IOException("invalid tile size "+tileSize);
                            if (w%tileSize != 0) throw new IOException("map width "+w+" is not a multiple of the tile size "+tileSize);
                            if (h%tileSize != 0) throw new IOException("map height "+h+" is not a multiple of the tile size "+tileSize);
                            final int tmpW = w/tileSize;
                            final int tmpH = h/tileSize;
                            DefaultCrossfireServerConnection.validateMapSize(tmpW, tmpH);
                            mapWidth = tmpW;
                            mapHeight = tmpH;

                            final GUIMap element = new GUIMap(window, name, tileSize, x, y, w, h, server, facesManager, mapUpdater);
                            definedGUIElements.insert(name, element);
                        }
                        else if (gui != null && args[0].equals("meta_list"))
                        {
                            if (args.length != 13)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final int cellHeight = expressionParser.parseInt(args[6]);
                            final BufferedImage tcpImage = args[7].equals("null") ? null : imageParser.getImage(args[7]);
                            final Font font = definedFonts.lookup(args[8]);
                            final GUIText text = args[9].equals("null") ? null : lookupTextElement(args[9]);
                            final AbstractLabel label = args[10].equals("null") ? null : lookupLabelElement(args[10]);
                            final String format = args[11];
                            final String tooltip = args[12];

                            final GUIMetaElementList list = new GUIMetaElementList(window, name, x, y, w, h, cellHeight, metaserverModel, tcpImage, font, format, tooltip, text, label);
                            definedGUIElements.insert(name, list);
                        }
                        else if (gui != null && args[0].equals("picture"))
                        {
                            if (args.length != 8)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage image = imageParser.getImage(args[6]);
                            final float alpha = NumberParser.parseFloat(args[7]);
                            if (alpha < 0 || alpha > 1F) throw new IOException("invalid alpha value: "+alpha);
                            definedGUIElements.insert(name, new GUIPicture(window, name, x, y, w, h, image, alpha));
                        }
                        else if (gui != null && args[0].equals("query_text"))
                        {
                            if (args.length != 12)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage activeImage = imageParser.getImage(args[6]);
                            final BufferedImage inactiveImage = imageParser.getImage(args[7]);
                            final Font font = definedFonts.lookup(args[8]);
                            final Color inactiveColor = ParseUtils.parseColor(args[9]);
                            final Color activeColor = ParseUtils.parseColor(args[10]);
                            final int margin = expressionParser.parseInt(args[11]);
                            definedGUIElements.insert(name, new GUIQueryText(window, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", false));
                        }
                        else if (gui != null && args[0].equals("set_forced_active"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final GUIElement forcedActive = definedGUIElements.lookup(args[1]);
                            if (!(forcedActive instanceof ActivatableGUIElement))
                            {
                                throw new IOException("argument to set_forced_active must be an activatable gui element");
                            }
                            gui.setForcedActive((ActivatableGUIElement)forcedActive);
                        }
                        else if (gui != null && args[0].equals("set_default"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            definedGUIElements.lookup(args[1]).setDefault(true);
                        }
                        else if (gui != null && args[0].equals("set_invisible"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            definedGUIElements.lookup(args[1]).setElementVisible(false);
                        }
                        else if (gui != null && args[0].equals("set_modal"))
                        {
                            if (args.length != 1)
                            {
                                throw new IOException("syntax error");
                            }

                            gui.setModal(true);
                        }
                        else if (gui != null && args[0].equals("set_num_look_objects"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            numLookObjects = expressionParser.parseInt(args[1]);
                        }
                        else if (gui != null && args[0].equals("scrollbar"))
                        {
                            if (args.length != 10)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final boolean proportionalSlider = NumberParser.parseBoolean(args[6]);
                            final GUIElement element = definedGUIElements.lookup(args[7]);
                            final Color colorBackground = ParseUtils.parseColor(args[8]);
                            final Color colorForeground = ParseUtils.parseColor(args[9]);
                            if (!(element instanceof GUIScrollable2))
                            {
                                throw new IOException("'"+element+"' is not a scrollable element");
                            }
                            definedGUIElements.insert(name, new GUIScrollBar(window, name, x, y, w, h, proportionalSlider, (GUIScrollable2)element, colorBackground, colorForeground));
                        }
                        else if (gui == null && args[0].equals("skin_name"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final String newSkinName = args[1];
                            if (!newSkinName.matches("[-a-z_0-9]+"))
                            {
                                throw new IOException("invalid skin_name: "+newSkinName);
                            }

                            skinName = newSkinName;
                        }
                        else if (gui != null && args[0].equals("text"))
                        {
                            if (args.length != 14)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage activeImage = imageParser.getImage(args[6]);
                            final BufferedImage inactiveImage = imageParser.getImage(args[7]);
                            final Font font = definedFonts.lookup(args[8]);
                            final Color inactiveColor = ParseUtils.parseColor(args[9]);
                            final Color activeColor = ParseUtils.parseColor(args[10]);
                            final int margin = expressionParser.parseInt(args[11]);
                            final GUICommandList commandList = getCommandList(args[12]);
                            final boolean ignoreUpDown = NumberParser.parseBoolean(args[13]);
                            definedGUIElements.insert(name, new GUITextField(window, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", commandList, ignoreUpDown));
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
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final boolean autoRepeat = NumberParser.parseBoolean(args[6]);
                            final GUICommandList commandList = getCommandList(args[7]);
                            final String text = ParseUtils.parseText(args, 8, lnr);
                            definedGUIElements.insert(name, textButtonFactory.newTextButton(window, name, x, y, w, h, text, autoRepeat, commandList));
                        }
                        else if (gui != null && args[0].equals("textgauge"))
                        {
                            if (args.length < 13)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = expressionParser.parseInt(args[2]);
                            final int y = expressionParser.parseInt(args[3]);
                            final int w = expressionParser.parseInt(args[4]);
                            final int h = expressionParser.parseInt(args[5]);
                            final BufferedImage positiveImage = imageParser.getImage(args[6]);
                            final BufferedImage negativeImage = args[7].equals("null") ? null : imageParser.getImage(args[7]);
                            final BufferedImage emptyImage = imageParser.getImage(args[8]);
                            final GaugeUpdater gaugeUpdater = gaugeUpdaterParser.parseGaugeUpdater(args[9], experienceTable);
                            final Orientation orientation = ParseUtils.parseOrientation(args[10]);
                            final Color color = ParseUtils.parseColor(args[11]);
                            final Font font = definedFonts.lookup(args[12]);
                            final String tooltipPrefix = ParseUtils.parseText(args, 13, lnr);
                            final GUITextGauge element = new GUITextGauge(window, name, x, y, w, h, positiveImage, negativeImage, emptyImage, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
                            definedGUIElements.insert(name, element);
                            gaugeUpdater.setGauge(element);
                        }
                        else if (args[0].equals("tooltip"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final Font font = definedFonts.lookup(args[1]);
                            final GUIHTMLLabel tooltipLabel = new GUIHTMLLabel(window, "tooltip", 0, 0, 1, 1, null, font, Color.BLACK, Color.WHITE, "");
                            tooltipLabel.setAutoResize(true);
                            window.getWindowRenderer().setTooltip(tooltipLabel);
                            window.getTooltipManager().setTooltip(tooltipLabel);
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

        assert gui != null || !definedGUIElements.iterator().hasNext();

        final Map<GUIElement, GUIElement> wildcardElements = new LinkedHashMap<GUIElement, GUIElement>();
        for (final GUIElement element : definedGUIElements)
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
     * Returns a {@link GUIText} by element name.
     * @param name the element name
     * @return the <code>GUIText</code> element
     * @throws JXCSkinException if the element name is undefined
     */
    private GUIText lookupTextElement(final String name) throws JXCSkinException
    {
        final GUIElement element = definedGUIElements.lookup(name);
        if (!(element instanceof GUIText))
        {
            throw new JXCSkinException("element "+name+" is not a text field");
        }

        return (GUIText)element;
    }

    /**
     * Returns a {@link AbstractLabel} by element name.
     * @param name the element name
     * @return the <code>AbstractLabel</code> element
     * @throws JXCSkinException if the element name is undefined
     */
    private AbstractLabel lookupLabelElement(final String name) throws JXCSkinException
    {
        final GUIElement element = definedGUIElements.lookup(name);
        if (!(element instanceof AbstractLabel))
        {
            throw new JXCSkinException("element "+name+" is not a label");
        }

        return (AbstractLabel)element;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Gui> iterator()
    {
        return dialogs.iterator();
    }

    /** {@inheritDoc} */
    @Override
    public void executeInitEvents()
    {
        for (final GUICommandList commandList : initEvents)
        {
            commandList.execute();
        }
    }

    /** {@inheritDoc} */
    @Override
    public GUICommandList getCommandList(final String name) throws JXCSkinException
    {
        return definedCommandLists.lookup(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChangedDialog()
    {
        return dialogs.hasChangedDialog();
    }

    /** {@inheritDoc} */
    @Override
    public KeyBindings getDefaultKeyBindings()
    {
        return defaultKeyBindings;
    }
}

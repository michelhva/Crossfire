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
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.DefaultCrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.MessageTypes;
import com.realtime.crossfire.jxclient.server.UnknownCommandException;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.CommandCheckBoxOption;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import com.realtime.crossfire.jxclient.shortcuts.Shortcuts;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.skin.events.ConnectionStateSkinEvent;
import com.realtime.crossfire.jxclient.skin.events.CrossfireMagicmapSkinEvent;
import com.realtime.crossfire.jxclient.skin.events.MapscrollSkinEvent;
import com.realtime.crossfire.jxclient.skin.events.SkillAddedSkinEvent;
import com.realtime.crossfire.jxclient.skin.events.SkillRemovedSkinEvent;
import com.realtime.crossfire.jxclient.spells.CurrentSpellManager;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.NumberParser;
import com.realtime.crossfire.jxclient.util.StringUtils;
import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.window.MouseTracker;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for loading {@link JXCSkin} instances from {@link JXCSkinSource}s.
 * @author Andreas Kirschbaum
 */
public class JXCSkinLoader
{
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
     * The default key bindings.
     */
    private final KeyBindings defaultKeyBindings;

    /**
     * The {@link OptionManager} instance to use.
     */
    private final OptionManager optionManager;

    /**
     * The {@link ExperienceTable} to use.
     */
    private final ExperienceTable experienceTable;

    /**
     * The {@link SkillSet} instance to use.
     */
    private final SkillSet skillSet;

    /**
     * All defined fonts.
     */
    private final JXCSkinCache<Font> definedFonts = new JXCSkinCache<Font>("font");

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
     * The {@link ExpressionParser} for parsing integer constant expressions.
     */
    private ExpressionParser expressionParser;

    /**
     * The {@link ImageParser} for parsing image specifications.
     */
    private ImageParser imageParser;

    /**
     * The {@link FontParser} for parsing font specifications.
     */
    private FontParser fontParser;

    /**
     * The {@link GuiElementParser} for parsing gui element specifications.
     */
    private GuiElementParser guiElementParser;

    /**
     * The {@link JXCSkin} being loaded.
     */
    private DefaultJXCSkin skin;

    /**
     * Creates a new instance.
     * @param itemsManager the items manager instance to use
     * @param spellsManager the spells manager instance to use
     * @param facesManager the faces manager instance to use
     * @param stats the stats instance to use
     * @param mapUpdater the map updater instance to use
     * @param defaultKeyBindings the default key bindings
     * @param optionManager the option manager to use
     * @param experienceTable the experience table to use
     * @param skillSet the skill set to use
     */
    public JXCSkinLoader(final ItemsManager itemsManager, final SpellsManager spellsManager, final FacesManager facesManager, final Stats stats, final CfMapUpdater mapUpdater, final KeyBindings defaultKeyBindings, final OptionManager optionManager, final ExperienceTable experienceTable, final SkillSet skillSet)
    {
        this.itemsManager = itemsManager;
        this.spellsManager = spellsManager;
        this.facesManager = facesManager;
        this.stats = stats;
        this.mapUpdater = mapUpdater;
        this.defaultKeyBindings = defaultKeyBindings;
        this.optionManager = optionManager;
        this.experienceTable = experienceTable;
        this.skillSet = skillSet;
    }

    /**
     * Loads the skin from its external representation.
     * @param skinSource the source to load from
     * @param crossfireServerConnection the server connection to attach to
     * @param window the window to use
     * @param mouseTracker the mouse tracker to use
     * @param metaserverModel the metaserver mode to use
     * @param commandQueue the command queue to use
     * @param resolution the preferred screen resolution
     * @param shortcuts the shortcuts to use
     * @param commands the commands instance to use
     * @param currentSpellManager the current spell manager to use
     * @param guiManager the gui manager to use
     * @return the loaded skin
     * @throws JXCSkinException if the skin cannot be loaded
     */
    public JXCSkin load(final JXCSkinSource skinSource, final CrossfireServerConnection crossfireServerConnection, final JXCWindow window, final MouseTracker mouseTracker, final MetaserverModel metaserverModel, final CommandQueue commandQueue, final Resolution resolution, final Shortcuts shortcuts, final Commands commands, final CurrentSpellManager currentSpellManager, final GuiManager guiManager) throws JXCSkinException
    {
        imageParser = new ImageParser(skinSource);
        fontParser = new FontParser(skinSource);
        final Resolution selectedResolution;
        if (resolution.isExact())
        {
            if (!skinSource.containsResolution(resolution))
            {
                throw new JXCSkinException("resolution "+resolution+" is not supported by the skin "+skin.getPlainSkinName());
            }

            selectedResolution = resolution;
        }
        else
        {
            if (skinSource.containsResolution(resolution))
            {
                selectedResolution = resolution;
            }
            else
            {
                Resolution selectedCandidate = null;
                // select maximum <= requested
                for (final Resolution candidate: skinSource)
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
                    for (final Resolution candidate: skinSource)
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
        skin = new DefaultJXCSkin(defaultKeyBindings, optionManager, stats, itemsManager, experienceTable, skillSet, expressionParser, selectedResolution);
        guiElementParser = new GuiElementParser(skin);
        skin.reset();
        imageParser.clear();
        skin.addDialog("keybind", window, mouseTracker, commands, guiManager);
        skin.addDialog("query", window, mouseTracker, commands, guiManager);
        skin.addDialog("book", window, mouseTracker, commands, guiManager);
        skin.addDialog("main", window, mouseTracker, commands, guiManager);
        skin.addDialog("meta", window, mouseTracker, commands, guiManager);
        skin.addDialog("quit", window, mouseTracker, commands, guiManager);
        skin.addDialog("disconnect", window, mouseTracker, commands, guiManager);
        skin.addDialog("start", window, mouseTracker, commands, guiManager);
        definedFonts.clear();
        textButtonFactory = null;
        dialogFactory = null;
        checkBoxFactory = null;
        try
        {
            load(skinSource, "global", crossfireServerConnection, window, mouseTracker, metaserverModel, commandQueue, null, shortcuts, commands, currentSpellManager, guiManager);
            for (;;)
            {
                final String name = skin.getDialogToLoad();
                if (name == null)
                {
                    break;
                }
                final Gui gui = skin.getDialog(name);
                load(skinSource, name, crossfireServerConnection, window, mouseTracker, metaserverModel, commandQueue, gui, shortcuts, commands, currentSpellManager, guiManager);
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

        if (skin.getMapWidth() == 0 || skin.getMapHeight() == 0)
        {
            throw new JXCSkinException("Missing map command");
        }

        return skin;
    }

    /**
     * Loads a skin file and add the entries to a {@link Gui} instance.
     * @param skinSource th source to load from
     * @param dialogName the key to identify this dialog
     * @param server the server connection to monitor
     * @param window the main window
     * @param mouseTracker the mouse tracker instance
     * @param metaserverModel the metaserver model to use
     * @param commandQueue the command queue for sending commands
     * @param gui the Gui representing the skin file
     * @param shortcuts the shortcuts instance
     * @param commands the commands instance for executing commands
     * @param currentSpellManager the current spell manager to use
     * @param guiManager the gui manager to use
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(final JXCSkinSource skinSource, final String dialogName, final CrossfireServerConnection server, final JXCWindow window, final MouseTracker mouseTracker, final MetaserverModel metaserverModel, final CommandQueue commandQueue, final Gui gui, final Shortcuts shortcuts, final Commands commands, final CurrentSpellManager currentSpellManager, final GuiManager guiManager) throws JXCSkinException
    {
        String resourceName = dialogName+"@"+skin.getSelectedResolution()+".skin";

        skin.clearDefinedGuiElements();
        try
        {
            InputStream inputStream;
            try
            {
                inputStream = skinSource.getInputStream(resourceName);
            }
            catch (final IOException ex)
            {
                resourceName = dialogName+".skin";
                inputStream = skinSource.getInputStream(resourceName);
            }
            try
            {
                load(skinSource, dialogName, resourceName, inputStream, server, window, mouseTracker, metaserverModel, commandQueue, gui, shortcuts, commands, currentSpellManager, guiManager);
            }
            finally
            {
                inputStream.close();
            }
        }
        catch (final IOException ex)
        {
            throw new JXCSkinException(skinSource.getURI(resourceName)+": "+ex.getMessage());
        }
        catch (final JXCSkinException ex)
        {
            throw new JXCSkinException(skinSource.getURI(resourceName)+": "+ex.getMessage());
        }
        finally
        {
            skin.clearDefinedGuiElements();
        }
    }

    /**
     * Loads a skin file and add the entries to a {@link Gui} instance.
     * @param skinSource the source to load from
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
     * @param shortcuts the shortcuts instance
     * @param commands the commands instance for executing commands
     * @param currentSpellManager the current spell manager to use
     * @param guiManager the gui manager to use
     * @throws JXCSkinException if the file cannot be loaded
     */
    private void load(final JXCSkinSource skinSource, final String dialogName, final String resourceName, final InputStream inputStream, final CrossfireServerConnection server, final JXCWindow window, final MouseTracker mouseTracker, final MetaserverModel metaserverModel, final CommandQueue commandQueue, final Gui gui, final Shortcuts shortcuts, final Commands commands, final CurrentSpellManager currentSpellManager, final GuiManager guiManager) throws JXCSkinException
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
                                addedElements.add(skin.lookupGuiElement(args[1]));
                            }
                        }
                        else if (gui != null && args[0].equals("button"))
                        {
                            parseButton(args, window, lnr);
                        }
                        else if (gui != null && args[0].equals("checkbox"))
                        {
                            parseCheckbox(args, window, lnr);
                        }
                        else if (args[0].equals("commandlist"))
                        {
                            parseCommandList(args, window, lnr, mouseTracker, commands, commandQueue, server, guiManager);
                        }
                        else if (args[0].equals("commandlist_add"))
                        {
                            parseCommandListAdd(args, window, lnr, mouseTracker, commands, commandQueue, server, guiManager);
                        }
                        else if (gui != null && args[0].equals("command_text"))
                        {
                            parseCommandText(args, window, commands);
                        }
                        else if (args[0].equals("def"))
                        {
                            parseDef(args, lnr);
                        }
                        else if (gui != null && args[0].equals("dialog"))
                        {
                            parseDialog(args, window, lnr, gui, dialogName);
                        }
                        else if (gui != null && args[0].equals("dialog_hide"))
                        {
                            parseDialogHide(args, gui);
                        }
                        else if (gui != null && args[0].equals("dupgauge"))
                        {
                            parseDupGauge(args, window, lnr);
                        }
                        else if (gui != null && args[0].equals("duptextgauge"))
                        {
                            parseDupTextGauge(args, window, lnr);
                        }
                        else if (args[0].equals("event"))
                        {
                            parseEvent(args, window, server);
                        }
                        else if (args[0].equals("font"))
                        {
                            parseFont(args);
                        }
                        else if (gui != null && args[0].equals("gauge"))
                        {
                            parseGauge(args, window, lnr);
                        }
                        else if (gui != null && args[0].equals("ignore"))
                        {
                            parseIgnore(args);
                        }
                        else if (gui != null && args[0].equals("inventory_list"))
                        {
                            parseInventoryList(args, window, commandQueue, server);
                        }
                        else if (gui != null && args[0].equals("item"))
                        {
                            parseItem(args, window, commandQueue, server, shortcuts, currentSpellManager);
                        }
                        else if (args[0].equals("key"))
                        {
                            parseKey(args, gui, line);
                        }
                        else if (gui != null && args[0].equals("label_html"))
                        {
                            parseLabelHtml(args, window, lnr);
                        }
                        else if (gui != null && args[0].equals("label_multi"))
                        {
                            parseLabelMulti(args, window, lnr);
                        }
                        else if (gui != null && args[0].equals("label_query"))
                        {
                            parseLabelQuery(args, window, server);
                        }
                        else if (gui != null && args[0].equals("label_text"))
                        {
                            parseLabelText(args, window, lnr);
                        }
                        else if (gui != null && args[0].equals("label_stat"))
                        {
                            parseLabelStat(args, window);
                        }
                        else if (gui != null && args[0].equals("label_spell"))
                        {
                            parseLabelSpell(args, window, currentSpellManager);
                        }
                        else if (gui != null && args[0].equals("log_label"))
                        {
                            parseLogLabel(args, window);
                        }
                        else if (gui != null && args[0].equals("log_message"))
                        {
                            parseLogMessage(args, window, server);
                        }
                        else if (gui != null && args[0].equals("log_color"))
                        {
                            parseLogColor(args);
                        }
                        else if (gui != null && args[0].equals("log_filter"))
                        {
                            parseLogFilter(args);
                        }
                        else if (gui != null && args[0].equals("magicmap"))
                        {
                            parseMagicmap(args, window, server);
                        }
                        else if (gui != null && args[0].equals("map"))
                        {
                            parseMap(args, window, server);
                        }
                        else if (gui != null && args[0].equals("meta_list"))
                        {
                            parseMetaList(args, window, metaserverModel);
                        }
                        else if (gui != null && args[0].equals("picture"))
                        {
                            parsePicture(args, window);
                        }
                        else if (gui != null && args[0].equals("query_text"))
                        {
                            parseQueryText(args, window);
                        }
                        else if (gui != null && args[0].equals("set_forced_active"))
                        {
                            parseSetForcedActive(args, gui);
                        }
                        else if (gui != null && args[0].equals("set_default"))
                        {
                            parseSetDefault(args);
                        }
                        else if (gui != null && args[0].equals("set_invisible"))
                        {
                            parseSetInvisible(args);
                        }
                        else if (gui != null && args[0].equals("set_modal"))
                        {
                            parseSetModal(args, gui);
                        }
                        else if (gui != null && args[0].equals("set_num_look_objects"))
                        {
                            parseSetNumLookObjects(args);
                        }
                        else if (gui != null && args[0].equals("scrollbar"))
                        {
                            parseScrollbar(args, window);
                        }
                        else if (gui == null && args[0].equals("skin_name"))
                        {
                            parseSkinName(args);
                        }
                        else if (gui != null && args[0].equals("text"))
                        {
                            parseText(args, window);
                        }
                        else if (gui != null && args[0].equals("textbutton"))
                        {
                            parseTextButton(args, window, lnr);
                        }
                        else if (gui != null && args[0].equals("textgauge"))
                        {
                            parseTextGauge(args, window, lnr);
                        }
                        else if (args[0].equals("tooltip"))
                        {
                            parseTooltip(args, window);
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
            throw new JXCSkinException(skinSource.getURI(resourceName)+": "+ex.getMessage());
        }

        final Iterator<GUIElement> it = skin.guiElementIterator();
        assert gui != null || !it.hasNext();

        final Map<GUIElement, GUIElement> wildcardElements = new LinkedHashMap<GUIElement, GUIElement>();
        while (it.hasNext())
        {
            final GUIElement element = it.next();
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
     * Parses a "button" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseButton(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        final GUICommandList commandList = skin.getCommandList(args[9]);
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
        skin.insertGuiElement(new GUIButton(window, name, x, y, w, h, upImage, downImage, label, font, color, textX, textY, autoRepeat, commandList));
    }

    /**
     * Parses a "checkbox" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCheckbox(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        skin.insertGuiElement(checkBoxFactory.newCheckBox(window, name, x, y, w, h, option, text));
    }

    /**
     * Parses a "commandlist" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @param mouseTracker the mouse tracker to use
     * @param commands the commands to add to
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param guiManager the gui manager to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandList(final String[] args, final JXCWindow window, final LineNumberReader lnr, final MouseTracker mouseTracker, final Commands commands, final CommandQueue commandQueue, final CrossfireServerConnection server, final GuiManager guiManager) throws IOException, JXCSkinException
    {
        if (args.length != 3 && args.length < 5)
        {
            throw new IOException("syntax error");
        }

        final String commandListName = args[1];
        final GUICommandList.CommandType commandListCommandType = NumberParser.parseEnum(GUICommandList.CommandType.class, args[2], "type");
        skin.addCommandList(commandListName, commandListCommandType);
        if (args.length >= 5)
        {
            final GUIElement element = args[3].equals("null") ? null : skin.lookupGuiElement(args[3]);
            skin.addCommand(commandListName, args, 5, element, args[4], window, mouseTracker, commands, lnr, commandQueue, server, guiManager);
        }
    }

    /**
     * Parses a "commandlist_add" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @param mouseTracker the mouse tracker to use
     * @param commands the commands to add to
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param guiManager the gui manager to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandListAdd(final String[] args, final JXCWindow window, final LineNumberReader lnr, final MouseTracker mouseTracker, final Commands commands, final CommandQueue commandQueue, final CrossfireServerConnection server, final GuiManager guiManager) throws IOException, JXCSkinException
    {
        if (args.length < 4)
        {
            throw new IOException("syntax error");
        }

        final GUIElement element = args[2].equals("null") ? null : skin.lookupGuiElement(args[2]);
        skin.addCommand(args[1], args, 4, element, args[3], window, mouseTracker, commands, lnr, commandQueue, server, guiManager);
    }

    /**
     * Parses a "command_text" command.
     * @param args the command arguments
     * @param window the window to use
     * @param commands the commands to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseCommandText(final String[] args, final JXCWindow window, final Commands commands) throws IOException, JXCSkinException
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
        skin.insertGuiElement(new GUICommandText(window, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", commands, false));
    }

    /**
     * Parses a "def" command.
     * @param args the command arguments
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDef(final String[] args, final LineNumberReader lnr) throws IOException, JXCSkinException
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
            final GUICommandList commandOn = skin.getCommandList(args[3]);
            final GUICommandList commandOff = skin.getCommandList(args[4]);
            final String documentation = ParseUtils.parseText(args, 5, lnr);
            skin.addOption(optionName, documentation, new CommandCheckBoxOption(commandOn, commandOff));
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

    /**
     * Parses a "dialog" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @param gui the gui instance to add to
     * @param dialogName the dialog name
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDialog(final String[] args, final JXCWindow window, final LineNumberReader lnr, final Gui gui, final String dialogName) throws IOException, JXCSkinException
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
            skin.insertGuiElement(element);
        }
        if (saveDialog)
        {
            gui.setName(dialogName);
        }
        gui.setSize(w, h);
        gui.setPosition(x, y);
    }

    /**
     * Parses a "dialog_hide" command.
     * @param args the command arguments
     * @param gui the gui instance to use
     * @throws IOException if the command cannot be parsed
     */
    private static void parseDialogHide(final String[] args, final Gui gui) throws IOException
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

    /**
     * Parses a "dupgauge" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDupGauge(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        final GaugeUpdater gaugeUpdater = skin.newGaugeUpdater(args[9]);
        final Orientation orientationDiv = ParseUtils.parseOrientation(args[10]);
        final Orientation orientationMod = ParseUtils.parseOrientation(args[11]);
        final String tooltipPrefix = ParseUtils.parseText(args, 12, lnr);
        final GUIDupGauge element = new GUIDupGauge(window, name, x, y, w, h, positiveDivImage, positiveModImage, emptyImage, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
        skin.insertGuiElement(element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses a "duptextgauge" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseDupTextGauge(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        final GaugeUpdater gaugeUpdater = skin.newGaugeUpdater(args[9]);
        final Orientation orientationDiv = ParseUtils.parseOrientation(args[10]);
        final Orientation orientationMod = ParseUtils.parseOrientation(args[11]);
        final Color color = ParseUtils.parseColor(args[12]);
        final Font font = definedFonts.lookup(args[13]);
        final String tooltipPrefix = ParseUtils.parseText(args, 14, lnr);
        final GUIDupTextGauge element = new GUIDupTextGauge(window, name, x, y, w, h, positiveDivImage, positiveModImage, emptyImage, orientationDiv, orientationMod, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
        skin.insertGuiElement(element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses an "event" command.
     * @param args the command arguments
     * @param window the window to use
     * @param server the server to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseEvent(final String[] args, final JXCWindow window, final CrossfireServerConnection server) throws IOException, JXCSkinException
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

            final GUICommandList commandList = skin.getCommandList(args[2]);
            skin.addSkinEvent(new ConnectionStateSkinEvent(commandList, window));
        }
        else if (type.equals("init"))
        {
            if (args.length != 3)
            {
                throw new IOException("syntax error");
            }

            skin.addInitEvent(skin.getCommandList(args[2]));
        }
        else if (type.equals("magicmap"))
        {
            if (args.length != 3)
            {
                throw new IOException("syntax error");
            }

            final GUICommandList commandList = skin.getCommandList(args[2]);
            skin.addSkinEvent(new CrossfireMagicmapSkinEvent(commandList, server));
        }
        else if (type.equals("mapscroll"))
        {
            if (args.length != 3)
            {
                throw new IOException("syntax error");
            }

            final GUICommandList commandList = skin.getCommandList(args[2]);
            skin.addSkinEvent(new MapscrollSkinEvent(commandList, mapUpdater));
        }
        else if (type.equals("skill"))
        {
            if (args.length != 5)
            {
                throw new IOException("syntax error");
            }

            final String subtype = args[2];
            final Skill skill = skillSet.getNamedSkill(args[3].replaceAll("_", " "));
            final GUICommandList commandList = skin.getCommandList(args[4]);
            if (subtype.equals("add"))
            {
                skin.addSkinEvent(new SkillAddedSkinEvent(commandList, skill));
            }
            else if (subtype.equals("del"))
            {
                skin.addSkinEvent(new SkillRemovedSkinEvent(commandList, skill));
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

    /**
     * Parses a "font" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseFont(final String[] args) throws IOException, JXCSkinException
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

    /**
     * Parses a "gauge" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseGauge(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        final GaugeUpdater gaugeUpdater = skin.newGaugeUpdater(args[9]);
        final Orientation orientation = ParseUtils.parseOrientation(args[10]);
        final String tooltipPrefix = ParseUtils.parseText(args, 11, lnr);
        final GUIGauge element = new GUIGauge(window, name, x, y, w, h, positiveImage, negativeImage, emptyImage, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
        skin.insertGuiElement(element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses an "ignore" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseIgnore(final String[] args) throws IOException, JXCSkinException
    {
        if (args.length != 2)
        {
            throw new IOException("syntax error");
        }

        final String name = args[1];
        skin.lookupGuiElement(name).setIgnore();
    }

    /**
     * Parses an "inventory_list" command.
     * @param args the command arguments
     * @param window the window to use
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseInventoryList(final String[] args, final JXCWindow window, final CommandQueue commandQueue, final CrossfireServerConnection server) throws IOException, JXCSkinException
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
        final AbstractLabel selectedItem = args[17].equals("null") ? null : guiElementParser.lookupLabelElement(args[17]);

        final ItemPainter itemPainter = new ItemPainter(cursedImage, damnedImage, magicImage, blessedImage, appliedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, selectorColor, lockedColor, unpaidColor, font, nrofColor, cellHeight, cellHeight);
        final GUIItemInventoryFactory itemInventoryFactory = new GUIItemInventoryFactory(window, commandQueue, name, itemPainter, server, facesManager, itemsManager);
        final GUIItemInventoryList element = new GUIItemInventoryList(window, commandQueue, name, x, y, w, h, cellHeight, server, itemsManager, selectedItem, itemInventoryFactory);
        skin.insertGuiElement(element);
    }

    /**
     * Parses an "item" command.
     * @param args the command arguments
     * @param window the window to use
     * @param commandQueue the command queue to use
     * @param server the server to use
     * @param shortcuts the shortcuts to use
     * @param currentSpellManager the current spell manager to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseItem(final String[] args, final JXCWindow window, final CommandQueue commandQueue, final CrossfireServerConnection server, final Shortcuts shortcuts, final CurrentSpellManager currentSpellManager) throws IOException, JXCSkinException
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
        skin.insertGuiElement(element);
    }

    /**
     * Parses a "key" command.
     * @param args the command arguments
     * @param gui the gui to add to
     * @param line the unparsed command line
     * @throws IOException if the command cannot be parsed
     */
    private void parseKey(final String[] args, final Gui gui, final String line) throws IOException
    {
        if (args.length < 2)
        {
            throw new IOException("syntax error");
        }

        final KeyBindings keyBindings = gui != null ? gui.getKeyBindings() : skin.getDefaultKeyBindings();
        try
        {
            keyBindings.parseKeyBinding(line.substring(4).trim(), true);
        }
        catch (final InvalidKeyBindingException ex)
        {
            throw new IOException("invalid key binding: "+ex.getMessage());
        }
    }

    /**
     * Parses a "label_html" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelHtml(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        skin.insertGuiElement(new GUIHTMLLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), text));
    }

    /**
     * Parses a "label_multi" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelMulti(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        skin.insertGuiElement(new GUIMultiLineLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), GUILabel.Alignment.LEFT, text));
    }

    /**
     * Parses a "label_query" command.
     * @param args the command arguments
     * @param window the window to use
     * @param server the server instance to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelQuery(final String[] args, final JXCWindow window, final CrossfireServerConnection server) throws IOException, JXCSkinException
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
        skin.insertGuiElement(element);
    }

    /**
     * Parses a "label_text" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelText(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        skin.insertGuiElement(new GUIOneLineLabel(window, name, x, y, w, h, null, font, color, new Color(0, 0, 0, 0F), GUILabel.Alignment.LEFT, text));
    }

    /**
     * Parses a "label_stat" command.
     * @param args the command arguments
     * @param window the window to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelStat(final String[] args, final JXCWindow window) throws IOException, JXCSkinException
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
        skin.insertGuiElement(element);
    }

    /**
     * Parses a "label_spell" command.
     * @param args the command arguments
     * @param window the window to use
     * @param currentSpellManager the current spell manager to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLabelSpell(final String[] args, final JXCWindow window, final CurrentSpellManager currentSpellManager) throws IOException, JXCSkinException
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
        skin.insertGuiElement(element);
    }

    /**
     * Parses a "log_label" command.
     * @param args the command arguments
     * @param window the window to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogLabel(final String[] args, final JXCWindow window) throws IOException, JXCSkinException
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
        skin.insertGuiElement(element);
    }

    /**
     * Parses a "log_message" command.
     * @param args the command arguments
     * @param window the window to use
     * @param server the server to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogMessage(final String[] args, final JXCWindow window, final CrossfireServerConnection server) throws IOException, JXCSkinException
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
        skin.insertGuiElement(element);
    }

    /**
     * Parses a "log_color" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogColor(final String[] args) throws IOException, JXCSkinException
    {
        if (args.length != 4)
        {
            throw new IOException("syntax error");
        }

        final String name = args[1];
        final int index = expressionParser.parseInt(args[2]);
        final Color color = ParseUtils.parseColor(args[3]);
        final GUIElement element = skin.lookupGuiElement(name);
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

    /**
     * Parses a "log_filter" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseLogFilter(final String[] args) throws IOException, JXCSkinException
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
        final GUIElement element = skin.lookupGuiElement(name);
        if (!(element instanceof GUIMessageLog))
        {
            throw new IOException("element '"+name+"' is not of type 'log'");
        }
        ((GUIMessageLog)element).setTypes(types);
    }

    /**
     * Parses a "magicmap" command.
     * @param args the command arguments
     * @param window the window to use
     * @param server the server to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMagicmap(final String[] args, final JXCWindow window, final CrossfireServerConnection server) throws IOException, JXCSkinException
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
        skin.insertGuiElement(element);
    }

    /**
     * Parses a "map" command.
     * @param args the command arguments
     * @param window the window to use
     * @param server the server to monitor
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMap(final String[] args, final JXCWindow window, final CrossfireServerConnection server) throws IOException, JXCSkinException
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
        skin.setMapSize(tmpW, tmpH);

        final GUIMap element = new GUIMap(window, name, tileSize, x, y, w, h, server, facesManager, mapUpdater);
        skin.insertGuiElement(element);
    }

    /**
     * Parses a "meta_list" command.
     * @param args the command arguments
     * @param window the window to use
     * @param metaserverModel the metaserver model to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseMetaList(final String[] args, final JXCWindow window, final MetaserverModel metaserverModel) throws IOException, JXCSkinException
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
        final GUIText text = args[9].equals("null") ? null : guiElementParser.lookupTextElement(args[9]);
        final AbstractLabel label = args[10].equals("null") ? null : guiElementParser.lookupLabelElement(args[10]);
        final String format = args[11];
        final String tooltip = args[12];

        final GUIMetaElementList list = new GUIMetaElementList(window, name, x, y, w, h, cellHeight, metaserverModel, tcpImage, font, format, tooltip, text, label);
        skin.insertGuiElement(list);
    }

    /**
     * Parses a "picture" command.
     * @param args the command arguments
     * @param window the window to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parsePicture(final String[] args, final JXCWindow window) throws IOException, JXCSkinException
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
        skin.insertGuiElement(new GUIPicture(window, name, x, y, w, h, image, alpha));
    }

    /**
     * Parses a "query_text" command.
     * @param args the command arguments
     * @param window the window to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseQueryText(final String[] args, final JXCWindow window) throws IOException, JXCSkinException
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
        skin.insertGuiElement(new GUIQueryText(window, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", false));
    }

    /**
     * Parses a "set_forced_active" command.
     * @param args the command arguments
     * @param gui the gui to modify
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseSetForcedActive(final String[] args, final Gui gui) throws IOException, JXCSkinException
    {
        if (args.length != 2)
        {
            throw new IOException("syntax error");
        }

        final GUIElement forcedActive = skin.lookupGuiElement(args[1]);
        if (!(forcedActive instanceof ActivatableGUIElement))
        {
            throw new IOException("argument to set_forced_active must be an activatable gui element");
        }
        gui.setForcedActive((ActivatableGUIElement)forcedActive);
    }

    /**
     * Parses a "set_default" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseSetDefault(final String[] args) throws IOException, JXCSkinException
    {
        if (args.length != 2)
        {
            throw new IOException("syntax error");
        }

        skin.lookupGuiElement(args[1]).setDefault(true);
    }

    /**
     * Parses a "set_invisible" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseSetInvisible(final String[] args) throws IOException, JXCSkinException
    {
        if (args.length != 2)
        {
            throw new IOException("syntax error");
        }

        skin.lookupGuiElement(args[1]).setElementVisible(false);
    }

    /**
     * Parses a "set_modal" command.
     * @param args the command arguments
     * @param gui the gui to modify
     * @throws IOException if the command cannot be parsed
     */
    private static void parseSetModal(final String[] args, final Gui gui) throws IOException
    {
        if (args.length != 1)
        {
            throw new IOException("syntax error");
        }

        gui.setModal(true);
    }

    /**
     * Parses a "set_num_look_objects" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     */
    private void parseSetNumLookObjects(final String[] args) throws IOException
    {
        if (args.length != 2)
        {
            throw new IOException("syntax error");
        }

        skin.setNumLookObjects(expressionParser.parseInt(args[1]));
    }

    /**
     * Parses a "scrollbar" command.
     * @param args the command arguments
     * @param window the window to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseScrollbar(final String[] args, final JXCWindow window) throws IOException, JXCSkinException
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
        final GUIElement element = skin.lookupGuiElement(args[7]);
        final Color colorBackground = ParseUtils.parseColor(args[8]);
        final Color colorForeground = ParseUtils.parseColor(args[9]);
        if (!(element instanceof GUIScrollable2))
        {
            throw new IOException("'"+element+"' is not a scrollable element");
        }
        skin.insertGuiElement(new GUIScrollBar(window, name, x, y, w, h, proportionalSlider, (GUIScrollable2)element, colorBackground, colorForeground));
    }

    /**
     * Parses a "skin_name" command.
     * @param args the command arguments
     * @throws IOException if the command cannot be parsed
     */
    private void parseSkinName(final String[] args) throws IOException
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

        skin.setSkinName(newSkinName);
    }

    /**
     * Parses a "text" command.
     * @param args the command arguments
     * @param window the window to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseText(final String[] args, final JXCWindow window) throws IOException, JXCSkinException
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
        final GUICommandList commandList = skin.getCommandList(args[12]);
        final boolean ignoreUpDown = NumberParser.parseBoolean(args[13]);
        skin.insertGuiElement(new GUITextField(window, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, "", commandList, ignoreUpDown));
    }

    /**
     * Parses a "textbutton" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseTextButton(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        final GUICommandList commandList = skin.getCommandList(args[7]);
        final String text = ParseUtils.parseText(args, 8, lnr);
        skin.insertGuiElement(textButtonFactory.newTextButton(window, name, x, y, w, h, text, autoRepeat, commandList));
    }

    /**
     * Parses a "textgauge" command.
     * @param args the command arguments
     * @param window the window to use
     * @param lnr the line number reader for reading more lines
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseTextGauge(final String[] args, final JXCWindow window, final LineNumberReader lnr) throws IOException, JXCSkinException
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
        final GaugeUpdater gaugeUpdater = skin.newGaugeUpdater(args[9]);
        final Orientation orientation = ParseUtils.parseOrientation(args[10]);
        final Color color = ParseUtils.parseColor(args[11]);
        final Font font = definedFonts.lookup(args[12]);
        final String tooltipPrefix = ParseUtils.parseText(args, 13, lnr);
        final GUITextGauge element = new GUITextGauge(window, name, x, y, w, h, positiveImage, negativeImage, emptyImage, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null, color, font);
        skin.insertGuiElement(element);
        gaugeUpdater.setGauge(element);
    }

    /**
     * Parses a "tooltip" command.
     * @param args the command arguments
     * @param window the window to use
     * @throws IOException if the command cannot be parsed
     * @throws JXCSkinException if the command cannot be parsed
     */
    private void parseTooltip(final String[] args, final JXCWindow window) throws IOException, JXCSkinException
    {
        if (args.length != 2)
        {
            throw new IOException("syntax error");
        }

        final Font font = definedFonts.lookup(args[1]);
        final GUIHTMLLabel tooltipLabel = new GUIHTMLLabel(window, "tooltip", 0, 0, 1, 1, null, font, Color.BLACK, Color.WHITE, "");
        tooltipLabel.setAutoResize(true);
        skin.setTooltipLabel(tooltipLabel);
    }
}

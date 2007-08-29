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

import com.realtime.crossfire.jxclient.CfMapUpdater;
import com.realtime.crossfire.jxclient.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.gui.Gui;
import com.realtime.crossfire.jxclient.gui.GUIButton;
import com.realtime.crossfire.jxclient.gui.GUICommand;
import com.realtime.crossfire.jxclient.gui.GUICommandText;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIGauge;
import com.realtime.crossfire.jxclient.gui.GUIItem;
import com.realtime.crossfire.jxclient.gui.GUIItemFloor;
import com.realtime.crossfire.jxclient.gui.GUIItemInventory;
import com.realtime.crossfire.jxclient.gui.GUIItemSpellbelt;
import com.realtime.crossfire.jxclient.gui.GUIItemSpelllist;
import com.realtime.crossfire.jxclient.gui.GUILabel;
import com.realtime.crossfire.jxclient.gui.GUILabelDrawextinfo;
import com.realtime.crossfire.jxclient.gui.GUILabelQuery;
import com.realtime.crossfire.jxclient.gui.GUILabelStats;
import com.realtime.crossfire.jxclient.gui.GUIMagicMap;
import com.realtime.crossfire.jxclient.gui.GUIMap;
import com.realtime.crossfire.jxclient.gui.GUIMetaElement;
import com.realtime.crossfire.jxclient.gui.GUIPicture;
import com.realtime.crossfire.jxclient.gui.GUISpellLabel;
import com.realtime.crossfire.jxclient.gui.GUIText;
import com.realtime.crossfire.jxclient.gui.log.GUILog;
import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.Stats;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Creates a {@link JXCSkin} instance from a file.
 *
 * @author Andreas Kirschbaum
 */
public abstract class JXCSkinLoader implements JXCSkin
{
    /**
     * Pattern to split a command into tokens.
     */
    private static final Pattern patternTokens = Pattern.compile("[ \t]+");

    /**
     * Pattern to parse integer constants.
     */
    private static final Pattern patternExpr = Pattern.compile("([0-9]+)([-+])(.+)");

    /**
     * All defined gui elements.
     */
    private final JXCSkinCache<GUIElement> elements = new JXCSkinCache<GUIElement>("gui element");

    /**
     * All defined command lists.
     */
    private final JXCSkinCache<GUICommandList> commandLists = new JXCSkinCache<GUICommandList>("command list");

    /**
     * All defined fonts.
     */
    private final JXCSkinCache<Font> fonts = new JXCSkinCache<Font>("font");

    /**
     * Check that the skin exists and can be accessed.
     *
     * @throws JXCSkinException if the skin does not exist or cannot be loaded
     */
    protected void checkAccess() throws JXCSkinException
    {
        try
        {
            final InputStream inputStream = getInputStream("start.skin");
            inputStream.close();
        }
        catch (final IOException ex)
        {
            throw new JXCSkinException(getURI("start.skin")+": "+ex.getMessage());
        }
    }

    /** {@inheritDoc} */
    public Gui getDialogKeyBind(final CrossfireServerConnection s, final JXCWindow p) throws JXCSkinException
    {
        return load("keybind.skin", s, p);
    }

    /** {@inheritDoc} */
    public Gui getDialogQuery(final CrossfireServerConnection s, final JXCWindow p) throws JXCSkinException
    {
        return load("query.skin", s, p);
    }

    /** {@inheritDoc} */
    public Gui getDialogBook(final CrossfireServerConnection s, final JXCWindow p, int booknr) throws JXCSkinException
    {
        return load("book.skin", s, p);
    }

    /** {@inheritDoc} */
    public Gui getMainInterface(final CrossfireServerConnection s, final JXCWindow p) throws JXCSkinException
    {
        return load("main.skin", s, p);
    }

    /** {@inheritDoc} */
    public Gui getMetaInterface(final CrossfireServerConnection s, final JXCWindow p) throws JXCSkinException
    {
        return load("meta.skin", s, p);
    }

    /** {@inheritDoc} */
    public Gui getStartInterface(final CrossfireServerConnection s, final JXCWindow p) throws JXCSkinException
    {
        return load("start.skin", s, p);
    }

    private Gui load(final String name, final CrossfireServerConnection s, final JXCWindow p) throws JXCSkinException
    {
        try
        {
            final InputStream inputStream = getInputStream(name);
            try
            {
                return load(name, inputStream, s, p);
            }
            finally
            {
                inputStream.close();
            }
        }
        catch (final IOException ex)
        {
            throw new JXCSkinException(getURI(name)+": "+ex.getMessage());
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
     * Load a skin file and create a {@link Gui} instance.
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
     * @throws IOException if the file cannot be loaded
     */
    private Gui load(final String resourceName, final InputStream inputStream, final CrossfireServerConnection server, final JXCWindow window) throws JXCSkinException
    {
        elements.clear();
        commandLists.clear();
        fonts.clear();

        final Gui gui = new Gui();
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

                        final String[] args = patternTokens.split(line);
                        if (args[0].equals("add"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            gui.add(elements.lookup(args[1]));
                        }
                        else if (args[0].equals("button"))
                        {
                            if (args.length != 9 && args.length < 13)
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
                            final GUICommandList commandList = commandLists.lookup(args[8]);
                            if (args.length == 9)
                            {
                                elements.insert(name, new GUIButton(window, name, x, y, w, h, pictureUp, pictureDown, commandList));
                            }
                            else
                            {
                                assert args.length >= 13;
                                final Font font = fonts.lookup(args[9]);
                                final Color color = parseColor(args[10]);
                                final int textX = parseInt(args[11]);
                                final int textY = parseInt(args[12]);
                                final String label = parseText(args, 13);
                                elements.insert(name, new GUIButton(window, name, x, y, w, h, pictureUp, pictureDown, label, font, color, textX, textY, commandList));
                            }
                        }
                        else if (args[0].equals("commandlist"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            commandLists.insert(args[1], new GUICommandList());
                        }
                        else if (args[0].equals("commandlist_add"))
                        {
                            if (args.length != 4)
                            {
                                throw new IOException("syntax error");
                            }

                            final GUICommandList commandList = commandLists.lookup(args[1]);
                            final GUIElement element = args[2].equals("null") ? null : elements.lookup(args[2]);
                            final GUICommand.Command command = parseEnum(GUICommand.Command.class, args[3], "command");
                            final Object params;
                            if (command == GUICommand.Command.CONNECT
                            || command == GUICommand.Command.GUI_LEAVE_DIALOG
                            || command == GUICommand.Command.GUI_META
                            || command == GUICommand.Command.GUI_START
                            || command == GUICommand.Command.QUIT)
                            {
                                params = window;
                            }
                            else
                            {
                                params = "";
                            }
                            commandList.add(new GUICommand(element, command, params));
                        }
                        else if (args[0].equals("command_text"))
                        {
                            if (args.length != 9)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage pictureActive = getPicture(args[6]);
                            final BufferedImage pictureInactive = getPicture(args[7]);
                            final Font font = fonts.lookup(args[8]);
                            elements.insert(name, new GUICommandText(window, name, x, y, w, h, pictureActive, pictureInactive, font, ""));
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
                        else if (args[0].equals("gauge"))
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
                            final BufferedImage picturePositive = getPicture(args[6]);
                            final BufferedImage pictureNegative = args[7].equals("null") ? null : getPicture(args[7]);
                            final BufferedImage pictureEmpty = getPicture(args[8]);
                            final int stat = parseStat(args[9]);
                            final GUIGauge.Orientation orientation = parseEnum(GUIGauge.Orientation.class, args[10], "orientation");
                            final String tooltipPrefix = parseText(args, 11);
                            final GUIGauge element = new GUIGauge(window, name, x, y, w, h, picturePositive, pictureNegative, pictureEmpty, stat, orientation, tooltipPrefix.length() > 0 ? tooltipPrefix : null);
                            elements.insert(name, element);
                        }
                        else if (args[0].equals("ignore"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            elements.lookup(args[1]).setIgnore();
                        }
                        else if (args[0].equals("item"))
                        {
                            if (args.length != 14)
                            {
                                throw new IOException("syntax error");
                            }

                            final String type = args[1];
                            final String name = args[2];
                            final int x = parseInt(args[3]);
                            final int y = parseInt(args[4]);
                            final int w = parseInt(args[5]);
                            final int h = parseInt(args[6]);
                            final BufferedImage pictureEmpty = getPicture(args[7]);
                            final BufferedImage pictureCursed = getPicture(args[8]);
                            final BufferedImage pictureApplied = getPicture(args[9]);
                            final BufferedImage pictureSelector = getPicture(args[10]);
                            final BufferedImage pictureLocked = getPicture(args[11]);
                            final int index = parseInt(args[12]);
                            final Font font = fonts.lookup(args[13]);
                            final GUIItem element;
                            if(type.equals("floor"))
                            {
                                element = new GUIItemFloor(window, name, x, y, w, h, pictureEmpty, pictureCursed, pictureApplied, pictureSelector, pictureLocked, index, server, font);
                            }
                            else if (type.equals("inventory"))
                            {
                                element = new GUIItemInventory(window, name, x, y, w, h, pictureEmpty, pictureCursed, pictureApplied, pictureSelector, pictureLocked, index, server, font);
                            }
                            else if (type.equals("spellbelt"))
                            {
                                element = new GUIItemSpellbelt(window, name, x, y, w, h, pictureEmpty, pictureCursed, pictureApplied, pictureSelector, pictureLocked, index, server, font);
                            }
                            else if (type.equals("spelllist"))
                            {
                                element = new GUIItemSpelllist(window, name, x, y, w, h, pictureEmpty, pictureCursed, pictureApplied, pictureSelector, pictureLocked, index, server, font);
                            }
                            else
                            {
                                throw new IOException("undefined item type: "+type);
                            }
                            elements.insert(name, element);
                        }
                        else if (args[0].equals("label_drawextinfo"))
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
                            final GUILabelDrawextinfo element = new GUILabelDrawextinfo(window, name, x, y, w, h, font, color);
                            elements.insert(name, element);
                        }
                        else if (args[0].equals("label_query"))
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
                            final GUILabelQuery element = new GUILabelQuery(window, name, x, y, w, h, font, color);
                            elements.insert(name, element);
                        }
                        else if (args[0].equals("label_text"))
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
                            final String text = parseText(args, 8);
                            elements.insert(name, new GUILabel(window, name, x, y, w, h, null, font, color, text));
                        }
                        else if (args[0].equals("label_picture"))
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
                            final BufferedImage pictureEmpty = getPicture(args[6]);
                            final Font font = fonts.lookup(args[7]);
                            elements.insert(name, new GUILabel(window, name, x, y, w, h, pictureEmpty, font, Color.WHITE, ""));
                        }
                        else if (args[0].equals("label_stat"))
                        {
                            if (args.length != 9)
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
                            final GUILabelStats element = new GUILabelStats(window, name, x, y, w, h, font, color, stat);
                            elements.insert(name, element);
                        }
                        else if (args[0].equals("label_spell"))
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
                        else if (args[0].equals("log"))
                        {
                            if (args.length != 11)
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
                            final GUILog element = new GUILog(window, name, x, y, w, h, pictureEmpty, fontPrint, fontFixed, fontFixedBold, fontArcane);
                            elements.insert(name, element);
                        }
                        else if (args[0].equals("magicmap"))
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
                        else if (args[0].equals("map"))
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
                            final GUIMap element = new GUIMap(window, name, tileSize, x, y, w, h);
                            elements.insert(name, element);
                            CfMapUpdater.addCrossfireMapscrollListener(element);
                        }
                        else if (args[0].equals("meta_element"))
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
                            final BufferedImage pictureTcp = getPicture(args[6]);
                            final BufferedImage pictureUdp = getPicture(args[7]);
                            final Font font = fonts.lookup(args[8]);
                            final GUIText text = lookupTextElement(args[9]);
                            final GUILabel label = lookupLabelElement(args[10]);
                            final int id = parseInt(args[11]);
                            elements.insert(name, new GUIMetaElement(window, name, x, y, w, h, pictureTcp, pictureUdp, font, text, label, id));
                        }
                        else if (args[0].equals("picture"))
                        {
                            if (args.length != 7)
                            {
                                throw new IOException("syntax error");
                            }

                            final String name = args[1];
                            final int x = parseInt(args[2]);
                            final int y = parseInt(args[3]);
                            final int w = parseInt(args[4]);
                            final int h = parseInt(args[5]);
                            final BufferedImage picture = getPicture(args[6]);
                            elements.insert(name, new GUIPicture(window, name, x, y, w, h, picture));
                        }
                        else if (args[0].equals("set_invisible"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            elements.lookup(args[1]).setVisible(false);
                        }
                        else if (args[0].equals("text"))
                        {
                            if (args.length != 9)
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
                            elements.insert(name, new GUIText(window, name, x, y, w, h, activePicture, inactivePicture, font, ""));
                        }
                        else if (args[0].equals("tooltip"))
                        {
                            if (args.length != 2)
                            {
                                throw new IOException("syntax error");
                            }

                            final Font font = fonts.lookup(args[1]);
                            final GUILabel tooltipLabel = new GUILabel(window, "tooltip", 0, 0, 1, 1, null, font, Color.BLACK, "");
                            tooltipLabel.setAutoResize(true);
                            tooltipLabel.setBackgroundColor(Color.WHITE);
                            gui.setTooltip(tooltipLabel);
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
                catch (final IllegalArgumentException ex)
                {
                        throw new IOException("invalid parameter in line "+lnr.getLineNumber());
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
        finally
        {
            elements.clear();
            commandLists.clear();
            fonts.clear();
        }

        return gui;
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
    private static int parseInt(final String str) throws IOException
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
            value = Integer.parseInt(matcher.group(1));
            for (;;)
            {
                final boolean negative = matcher.group(2).equals("-");
                final String rest = matcher.group(3);

                matcher = patternExpr.matcher(rest);
                if (!matcher.matches())
                {
                    final int valueRest = Integer.parseInt(rest);
                    if(negative)
                    {
                        value -= valueRest;
                    }
                    else
                    {
                        value += valueRest;
                    }
                    break;
                }

                final int valueRest = Integer.parseInt(matcher.group(1));
                if(negative)
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
            return Stats.parseStat(name);
        }
        catch (final IllegalArgumentException ex)
        {
            // ignore
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
        if (name.equals("WHITE")) return Color.WHITE;
        throw new IOException("unknown color name "+name);
    }

    /**
     * Concatenate trailing arguments into a string.
     *
     * @param args The args to concatenate.
     *
     * @param startIndex The first index to concatenate.
     *
     * @return The concatenated string.
     */
    private String parseText(final String[] args, final int startIndex)
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
        return text.toString();
    }

    /**
     * Return a font by font file base name.
     *
     * @param name The file base name of the font file to load.
     *
     * @return The font.
     *
     * @throws JXCSkinException if the font cannot be loaded.
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
        final String filename = "pictures/"+name+".png";
        final BufferedImage picture = ImageIO.read(getInputStream(filename));
        if (picture == null)
        {
            throw new IOException("picture '"+getURI(filename)+"' does not exist");
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
     * @throws IOException if the element name is undefined
     */
    private GUIText lookupTextElement(final String name) throws IOException
    {
        final GUIElement element = elements.lookup(name);
        if (!(element instanceof GUIText))
        {
            throw new IOException("element "+name+" is not a text field");
        }

        return (GUIText)element;
    }

    /**
     * Return a {@link GUILabel} by element name.
     *
     * @param name The element name.
     *
     * @return The <code>GUILabel</code> element.
     *
     * @throws IOException if the element name is undefined
     */
    private GUILabel lookupLabelElement(final String name) throws IOException
    {
        final GUIElement element = elements.lookup(name);
        if (!(element instanceof GUILabel))
        {
            throw new IOException("element "+name+" is not a label");
        }

        return (GUILabel)element;
    }
}

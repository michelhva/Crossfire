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
package com.realtime.crossfire.jxclient.server;

import com.realtime.crossfire.jxclient.CommandQueue;
import com.realtime.crossfire.jxclient.settings.options.OptionException;
import com.realtime.crossfire.jxclient.settings.options.OptionManager;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines constants for pickup mode.
 * @author Andreas Kirschbaum
 */
public class Pickup
{
    /**
     * The default pickup mode.
     */
    public static final long DEFAULT_PICKUP_MODE = 0;

    /** Pickup mode: nothing. */
    public static final long PU_NOTHING = 0x00000000L;

    /** Pickup mode: mask for value/weight ratio. */
    public static final long PU_RATIO = 0x0000000FL;

    /** Pickup mode: food. */
    public static final long PU_FOOD = 0x00000010L;

    /** Pickup mode: drinks. */
    public static final long PU_DRINK = 0x00000020L;

    /** Pickup mode: rings and amulets. */
    public static final long PU_VALUABLES = 0x00000040L;

    /** Pickup mode: bows. */
    public static final long PU_BOW = 0x00000080L;

    /** Pickup mode: arrows. */
    public static final long PU_ARROW = 0x00000100L;

    /** Pickup mode: helmets. */
    public static final long PU_HELMET = 0x00000200L;

    /** Pickup mode: shields. */
    public static final long PU_SHIELD = 0x00000400L;

    /** Pickup mode: armors. */
    public static final long PU_ARMOUR = 0x00000800L;

    /** Pickup mode: boots. */
    public static final long PU_BOOTS = 0x00001000L;

    /** Pickup mode: gloves. */
    public static final long PU_GLOVES = 0x00002000L;

    /** Pickup mode: cloaks. */
    public static final long PU_CLOAK = 0x00004000L;

    /** Pickup mode: keys. */
    public static final long PU_KEY = 0x00008000L;

    /** Pickup mode: missile weapons. */
    public static final long PU_MISSILEWEAPON = 0x00010000L;

    /** Pickup mode: all weapons. */
    public static final long PU_ALLWEAPON = 0x00020000L;

    /** Pickup mode: magical items. */
    public static final long PU_MAGICAL = 0x00040000L;

    /** Pickup mode: potions. */
    public static final long PU_POTION = 0x00080000L;

    /** Pickup mode: spellbooks. */
    public static final long PU_SPELLBOOK = 0x00100000L;

    /** Pickup mode: skillscrolls. */
    public static final long PU_SKILLSCROLL = 0x00200000L;

    /** Pickup mode: normal books and scrolls. */
    public static final long PU_READABLES = 0x00400000L;

    /** Pickup mode: magic devices. */
    public static final long PU_MAGIC_DEVICE = 0x00800000L;

    /** Pickup mode: ignore cursed items. */
    public static final long PU_NOT_CURSED = 0x01000000L;

    /** Pickup mode: jewels and money. */
    public static final long PU_JEWELS = 0x02000000L;

    /** Pickup mode: flesh. */
    public static final long PU_FLESH = 0x04000000L;

    /** Pickup mode: debug. */
    public static final long PU_DEBUG = 0x10000000L;

    /** Pickup mode: disable pickup. */
    public static final long PU_INHIBIT = 0x20000000L;

    /** Pickup mode: stop before pickup. */
    public static final long PU_STOP = 0x40000000L;

    /** Pickup mode: enable newpickup mode. */
    public static final long PU_NEWMODE = 0x80000000L;

    /**
     * All pickup options.
     */
    private final List<PickupOption> pickupOptions = new LinkedList<PickupOption>();

    /**
     * The command queue for sending pickup commands.
     */
    private final CommandQueue commandQueue;

    /**
     * The pickup mode.
     */
    private long pickupMode = DEFAULT_PICKUP_MODE;

    /**
     * Creates a new instance. Registers pickup related options.
     * @param commandQueue the command queue for sending pickup commands
     * @param optionManager the option manager to use
     * @throws OptionException if an option cannot be registered
     */
    public Pickup(final CommandQueue commandQueue, final OptionManager optionManager) throws OptionException
    {
        this.commandQueue = commandQueue;
//      optionManager.addOption("pickup_ratio0", "Pickup mode: weight/value off", newPickupOption(PU_RATIO, 0));
//      optionManager.addOption("pickup_ratio5", "Pickup mode: weight/value >= 5", newPickupOption(PU_RATIO, 1));
//      optionManager.addOption("pickup_ratio10", "Pickup mode: weight/value >= 10", newPickupOption(PU_RATIO, 2));
//      optionManager.addOption("pickup_ratio15", "Pickup mode: weight/value >= 15", newPickupOption(PU_RATIO, 3));
//      optionManager.addOption("pickup_ratio20", "Pickup mode: weight/value >= 20", newPickupOption(PU_RATIO, 4));
//      optionManager.addOption("pickup_ratio25", "Pickup mode: weight/value >= 25", newPickupOption(PU_RATIO, 5));
//      optionManager.addOption("pickup_ratio30", "Pickup mode: weight/value >= 30", newPickupOption(PU_RATIO, 6));
//      optionManager.addOption("pickup_ratio35", "Pickup mode: weight/value >= 35", newPickupOption(PU_RATIO, 7));
//      optionManager.addOption("pickup_ratio40", "Pickup mode: weight/value >= 40", newPickupOption(PU_RATIO, 8));
//      optionManager.addOption("pickup_ratio45", "Pickup mode: weight/value >= 45", newPickupOption(PU_RATIO, 9));
//      optionManager.addOption("pickup_ratio50", "Pickup mode: weight/value >= 50", newPickupOption(PU_RATIO, 10));
//      optionManager.addOption("pickup_ratio55", "Pickup mode: weight/value >= 55", newPickupOption(PU_RATIO, 11));
//      optionManager.addOption("pickup_ratio60", "Pickup mode: weight/value >= 60", newPickupOption(PU_RATIO, 12));
//      optionManager.addOption("pickup_ratio65", "Pickup mode: weight/value >= 65", newPickupOption(PU_RATIO, 13));
//      optionManager.addOption("pickup_ratio70", "Pickup mode: weight/value >= 70", newPickupOption(PU_RATIO, 14));
//      optionManager.addOption("pickup_ratio75", "Pickup mode: weight/value >= 75", newPickupOption(PU_RATIO, 15));
        optionManager.addOption("pickup_food", "Pickup mode: food", newPickupOption(PU_FOOD));
        optionManager.addOption("pickup_drink", "Pickup mode: drinks", newPickupOption(PU_DRINK));
        optionManager.addOption("pickup_valuables", "Pickup mode: valuables", newPickupOption(PU_VALUABLES));
        optionManager.addOption("pickup_bow", "Pickup mode: bows", newPickupOption(PU_BOW));
        optionManager.addOption("pickup_arrow", "Pickup mode: arrows", newPickupOption(PU_ARROW));
        optionManager.addOption("pickup_helmet", "Pickup mode: helmets", newPickupOption(PU_HELMET));
        optionManager.addOption("pickup_shield", "Pickup mode: shields", newPickupOption(PU_SHIELD));
        optionManager.addOption("pickup_armour", "Pickup mode: armors", newPickupOption(PU_ARMOUR));
        optionManager.addOption("pickup_boots", "Pickup mode: boots", newPickupOption(PU_BOOTS));
        optionManager.addOption("pickup_gloves", "Pickup mode: gloves", newPickupOption(PU_GLOVES));
        optionManager.addOption("pickup_cloak", "Pickup mode: cloaks", newPickupOption(PU_CLOAK));
        optionManager.addOption("pickup_key", "Pickup mode: keys", newPickupOption(PU_KEY));
        optionManager.addOption("pickup_missileweapon", "Pickup mode: missile weapons", newPickupOption(PU_MISSILEWEAPON));
        optionManager.addOption("pickup_allweapon", "Pickup mode: all weapons", newPickupOption(PU_ALLWEAPON));
        optionManager.addOption("pickup_magical", "Pickup mode: magical items", newPickupOption(PU_MAGICAL));
        optionManager.addOption("pickup_potion", "Pickup mode: potions", newPickupOption(PU_POTION));
        optionManager.addOption("pickup_spellbook", "Pickup mode: spellbooks", newPickupOption(PU_SPELLBOOK));
        optionManager.addOption("pickup_skillscroll", "Pickup mode: skillscrolls", newPickupOption(PU_SKILLSCROLL));
        optionManager.addOption("pickup_readables", "Pickup mode: readables", newPickupOption(PU_READABLES));
        optionManager.addOption("pickup_magic_device", "Pickup mode: magic devices", newPickupOption(PU_MAGIC_DEVICE));
        optionManager.addOption("pickup_not_cursed", "Pickup mode: not cursed items", newPickupOption(PU_NOT_CURSED));
        optionManager.addOption("pickup_jewels", "Pickup mode: jewels", newPickupOption(PU_JEWELS));
        optionManager.addOption("pickup_flesh", "Pickup mode: flesh", newPickupOption(PU_FLESH));
        optionManager.addOption("pickup_inhibit", "Pickup mode: inhibit pickup", newPickupOption(PU_INHIBIT));
        optionManager.addOption("pickup_stop", "Pickup mode: stop before pickup", newPickupOption(PU_STOP));
    }

    /**
     * Creates a new {@link PickupOption}.
     * @param option the pickup value
     * @return the pickup option
     */
    private PickupOption newPickupOption(final long option)
    {
        final PickupOption pickupOption = new PickupOption(this, option);
        pickupOptions.add(pickupOption);
        return pickupOption;
    }

    /**
     * Sets the pickup mode. This function does <em>not</code> send a 'pickup
     * command to the server.
     * @param pickupMode the pickup mode
     */
    public void setPickupMode(final long pickupMode)
    {
        this.pickupMode = pickupMode;
    }

    /**
     * Returns the pickup mode.
     * @return the pickup mode
     */
    public long getPickupMode()
    {
        return pickupMode;
    }

    /**
     * Sets or unsets the pickup mode.
     * @param pickupMode the pickup mode(s) to affect
     * @param set <code>true</code>=set, <code>false</code>=unset
     */
    public void setPickupMode(final long pickupMode, final boolean set)
    {
        if (set)
        {
            this.pickupMode |= pickupMode;
        }
        else
        {
            this.pickupMode &= ~pickupMode;
        }
        update();
    }

    /**
     * Notifies the Crossfire server and all pickup options that the pickup
     * mode has changed.
     */
    public void update()
    {
        commandQueue.sendNcom(true, 1, "pickup "+(pickupMode == PU_NOTHING ? 0 : (pickupMode|PU_NEWMODE)));
        for (final PickupOption pickupOption : pickupOptions)
        {
            pickupOption.setPickupMode(pickupMode);
        }
    }
}

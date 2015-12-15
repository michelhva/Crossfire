/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.settings.options;

import com.realtime.crossfire.jxclient.queue.CommandQueue;
import java.util.Collection;
import java.util.LinkedList;
import org.jetbrains.annotations.NotNull;

/**
 * Defines constants for pickup mode.
 * @author Andreas Kirschbaum
 */
public class Pickup {

    /**
     * The default pickup mode.
     */
    public static final long DEFAULT_PICKUP_MODE = 0;

    /**
     * Pickup mode: nothing.
     */
    public static final long PU_NOTHING = 0x00000000L;

    /**
     * Pickup mode: mask for value/weight ratio.
     */
    public static final long PU_RATIO = 0x0000000FL;

    /**
     * Pickup mode: debug.
     */
    private static final long PU_DEBUG = 0x10000000L;

    /**
     * Pickup mode: enable newpickup mode.
     */
    private static final long PU_NEW_MODE = 0x80000000L;

    /**
     * All pickup options.
     */
    @NotNull
    private final Collection<PickupOption> pickupOptions = new LinkedList<>();

    /**
     * Pickup mode: food.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption food = newPickupOption(0x00000010L, "<html>Picks up food items.<br>Flesh items are not included.");

    /**
     * Pickup mode: drinks.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption drink = newPickupOption(0x00000020L, "<html>Picks up drinkable items.");

    /**
     * Pickup mode: money and gems.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption valuables = newPickupOption(0x00000040L, "<html>Picks up money and gems.");

    /**
     * Pickup mode: bows.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption bow = newPickupOption(0x00000080L, "<html>Picks up bows and crossbows.");

    /**
     * Pickup mode: arrows.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption arrow = newPickupOption(0x00000100L, "<html>Picks up arrows and bolts.");

    /**
     * Pickup mode: helmets.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption helmet = newPickupOption(0x00000200L, "<html>Picks up helmets.");

    /**
     * Pickup mode: shields.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption shield = newPickupOption(0x00000400L, "<html>Picks up shields.");

    /**
     * Pickup mode: armors.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption armor = newPickupOption(0x00000800L, "<html>Picks up armors.");

    /**
     * Pickup mode: boots.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption boot = newPickupOption(0x00001000L, "<html>Picks up boots.");

    /**
     * Pickup mode: gloves.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption glove = newPickupOption(0x00002000L, "<html>Picks up gloves.");

    /**
     * Pickup mode: cloaks.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption cloak = newPickupOption(0x00004000L, "<html>Picks up cloaks.");

    /**
     * Pickup mode: keys.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption key = newPickupOption(0x00008000L, "<html>Picks up keys.");

    /**
     * Pickup mode: missile weapons.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption missileWeapon = newPickupOption(0x00010000L, "<html>Picks up missile weapons.");

    /**
     * Pickup mode: all weapons.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption meleeWeapon = newPickupOption(0x00020000L, "<html>Picks up melee weapons.");

    /**
     * Pickup mode: magical items.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption magicalItem = newPickupOption(0x00040000L, "<html>Picks up magical items.");

    /**
     * Pickup mode: potions.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption potion = newPickupOption(0x00080000L, "<html>Picks up potions.");

    /**
     * Pickup mode: spellbooks.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption spellbook = newPickupOption(0x00100000L, "<html>Picks up spellbooks and prayer books.");

    /**
     * Pickup mode: skillscrolls.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption skillscroll = newPickupOption(0x00200000L, "<html>Picks up skillscrolls.");

    /**
     * Pickup mode: normal books and scrolls.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption readable = newPickupOption(0x00400000L, "<html>Picks up readables.");

    /**
     * Pickup mode: magic devices.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption magicDevice = newPickupOption(0x00800000L, "<html>Picks up magic devices.");

    /**
     * Pickup mode: ignore cursed items.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption notCursed = newPickupOption(0x01000000L, "<html>Ignores cursed items.");

    /**
     * Pickup mode: rings and amulets.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption jewel = newPickupOption(0x02000000L, "<html>Picks up rings and amulets.");

    /**
     * Pickup mode: flesh.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption flesh = newPickupOption(0x04000000L, "<html>Picks up flesh items.");

    /**
     * Pickup mode: container.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption container = newPickupOption(0x08000000L, "<html>Picks up containers.");

    /**
     * Pickup mode: disable pickup.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption inhibit = newPickupOption(0x20000000L, "<html>Disables pickup mode.");

    /**
     * Pickup mode: stop before pickup.
     */
    @NotNull
    @SuppressWarnings("PublicField")
    public final PickupOption stop = newPickupOption(0x40000000L, "<html>Stops running when picking up items.");

    /**
     * The command queue for sending pickup commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The pickup mode.
     */
    @SuppressWarnings("RedundantFieldInitialization")
    private long pickupMode = DEFAULT_PICKUP_MODE;

    /**
     * Creates a new instance. Registers pickup related options.
     * @param commandQueue the command queue for sending pickup commands
     * @param optionManager the option manager to use
     * @throws OptionException if an option cannot be registered
     */
    public Pickup(@NotNull final CommandQueue commandQueue, @NotNull final OptionManager optionManager) throws OptionException {
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
        optionManager.addOption("pickup_food", "Pickup mode: food.", food);
        optionManager.addOption("pickup_drink", "Pickup mode: drinks.", drink);
        optionManager.addOption("pickup_valuables", "Pickup mode: valuables.", valuables);
        optionManager.addOption("pickup_bow", "Pickup mode: bows.", bow);
        optionManager.addOption("pickup_arrow", "Pickup mode: arrows.", arrow);
        optionManager.addOption("pickup_helmet", "Pickup mode: helmets.", helmet);
        optionManager.addOption("pickup_shield", "Pickup mode: shields.", shield);
        optionManager.addOption("pickup_armour", "Pickup mode: armors.", armor);
        optionManager.addOption("pickup_boots", "Pickup mode: boots.", boot);
        optionManager.addOption("pickup_gloves", "Pickup mode: gloves.", glove);
        optionManager.addOption("pickup_cloak", "Pickup mode: cloaks.", cloak);
        optionManager.addOption("pickup_key", "Pickup mode: keys.", key);
        optionManager.addOption("pickup_missile_weapon", "Pickup mode: missile weapons.", missileWeapon);
        optionManager.addOption("pickup_melee_weapon", "Pickup mode: melee weapons.", meleeWeapon);
        optionManager.addOption("pickup_magical", "Pickup mode: magical items.", magicalItem);
        optionManager.addOption("pickup_potion", "Pickup mode: potions.", potion);
        optionManager.addOption("pickup_spellbook", "Pickup mode: spellbooks.", spellbook);
        optionManager.addOption("pickup_skillscroll", "Pickup mode: skillscrolls.", skillscroll);
        optionManager.addOption("pickup_readables", "Pickup mode: readables.", readable);
        optionManager.addOption("pickup_magic_device", "Pickup mode: magic devices.", magicDevice);
        optionManager.addOption("pickup_not_cursed", "Pickup mode: not cursed items.", notCursed);
        optionManager.addOption("pickup_jewels", "Pickup mode: jewels.", jewel);
        optionManager.addOption("pickup_flesh", "Pickup mode: flesh.", flesh);
        optionManager.addOption("pickup_containers", "Pickup mode: container.", container);
        optionManager.addOption("pickup_inhibit", "Pickup mode: inhibit pickup.", inhibit);
        optionManager.addOption("pickup_stop", "Pickup mode: stop before pickup.", stop);
    }

    /**
     * Creates a new {@link PickupOption}.
     * @param option the pickup value
     * @param tooltipText the tooltip text to explain this option
     * @return the pickup option
     */
    @NotNull
    private PickupOption newPickupOption(final long option, @NotNull final String tooltipText) {
        final PickupOption pickupOption = new PickupOption(this, option, tooltipText);
        pickupOptions.add(pickupOption);
        return pickupOption;
    }

    /**
     * Sets the pickup mode.
     * @param pickupMode the pickup mode
     * @param sendToServer whether a /pickup command should be sent to the
     * server
     */
    public void updatePickupMode(final long pickupMode, final boolean sendToServer) {
        if (this.pickupMode == pickupMode) {
            return;
        }
        this.pickupMode = pickupMode;
        if (sendToServer) {
            commandQueue.sendNcom(true, 1, "pickup "+((pickupMode == PU_NOTHING ? 0 : pickupMode|PU_NEW_MODE)&0xFFFFFFFFL));
        }
        for (final PickupOption pickupOption : pickupOptions) {
            pickupOption.setPickupMode(pickupMode);
        }
    }

    /**
     * Returns the pickup mode.
     * @return the pickup mode
     */
    public long getPickupMode() {
        return pickupMode;
    }

    /**
     * Sets or unsets the pickup mode.
     * @param pickupMode the pickup mode(s) to affect
     * @param set {@code true}=set, {@code false}=unset
     */
    public void setPickupMode(final long pickupMode, final boolean set) {
        final long newPickupMode;
        //noinspection IfMayBeConditional
        if (set) {
            newPickupMode = this.pickupMode|pickupMode;
        } else {
            newPickupMode = this.pickupMode&~pickupMode;
        }
        updatePickupMode(newPickupMode, true);
    }

}

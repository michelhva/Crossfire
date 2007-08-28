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

import com.realtime.crossfire.jxclient.faces.Faces;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class ItemsList
{
    public static final int SPELLMODE_LOCAL = 0;

    public static final int SPELLMODE_SENT = 1;

    private static int spellmode = SPELLMODE_LOCAL;

    private static final ItemsManager itemsManager = new ItemsManager();

    private static final List<Spell> spells = new ArrayList<Spell>();

    private static final List<CrossfireSpellAddedListener> addspellListeners = new ArrayList<CrossfireSpellAddedListener>();

    private static final List<CrossfireSpellUpdatedListener> updspellListeners = new ArrayList<CrossfireSpellUpdatedListener>();

    private static final List<CrossfireSpellRemovedListener> delspellListeners = new ArrayList<CrossfireSpellRemovedListener>();

    static
    {
        try
        {
            initSpells();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void addCrossfireSpellAddedListener(final CrossfireSpellAddedListener listener)
    {
        addspellListeners.add(listener);
    }

    public static void removeCrossfireSpellAddedListener(final CrossfireSpellAddedListener listener)
    {
        addspellListeners.remove(listener);
    }

    public static void addCrossfireSpellUpdatedListener(final CrossfireSpellUpdatedListener listener)
    {
        updspellListeners.add(listener);
    }

    public static void removeCrossfireSpellUpdatedListener(final CrossfireSpellUpdatedListener listener)
    {
        updspellListeners.remove(listener);
    }

    public static void addCrossfireSpellRemovedListener(final CrossfireSpellRemovedListener listener)
    {
        delspellListeners.add(listener);
    }

    public static void removeCrossfireSpellRemovedListener(final CrossfireSpellRemovedListener listener)
    {
        delspellListeners.remove(listener);
    }

    public static void setSpellMode(final int spellmode)
    {
        ItemsList.spellmode = spellmode;
        if (spellmode == SPELLMODE_LOCAL)
        {
            spells.clear();
            initSpells();
        }
        else
        {
            spells.clear();
        }
    }

    public static void updateItem(final int flags, final int tag, final int valFlags, final int valWeight, final int valFace, final String valName, final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof)
    {
        final CfItem item = itemsManager.getItemOrPlayer(tag);
        if (item == null)
        {
            System.err.println("updateItem: undefined item "+tag);
            return;
        }

        final boolean wasopen = (flags&CfItem.UPD_FLAGS) != 0 && itemsManager.getCurrentFloor() == item.getTag() && item.isOpen();
        item.update(flags, tag, valFlags, valWeight, Faces.getFace(valFace), valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        if ((flags&CfItem.UPD_FLAGS) != 0)
        {
            if (item.isOpen())
            {
                itemsManager.setCurrentFloor(item.getTag());
            }
            else if (wasopen)
            {
                itemsManager.setCurrentFloor(0);
            }
        }
    }

    public static List<Spell> getSpellList()
    {
        return spells;
    }

    private static void initSpells()
    {
        if (spellmode != SPELLMODE_LOCAL)
        {
            spells.clear();
            return;
        }

        spells.add(new Spell("default.theme/pictures/spells/si_001.png", "Small Lightning", "small lightning"));
        spells.add(new Spell("default.theme/pictures/spells/si_002.png", "Large Lightning", "large lightning"));
        spells.add(new Spell("default.theme/pictures/spells/si_003.png", "Create Bomb", "create bomb"));
        spells.add(new Spell("default.theme/pictures/spells/si_004.png", "Comet", "comet"));
        spells.add(new Spell("default.theme/pictures/spells/si_005.png", "Small Snowstorm", "small snowstorm"));
        spells.add(new Spell("default.theme/pictures/spells/si_006.png", "Medium Snowstorm", "medium snowstorm"));
        spells.add(new Spell("default.theme/pictures/spells/si_007.png", "Large Snowstorm", "large snowstorm"));
        spells.add(new Spell("default.theme/pictures/spells/si_008.png", "Small Fireball", "small fireball"));
        spells.add(new Spell("default.theme/pictures/spells/si_009.png", "Medium Fireball", "medium fireball"));
        spells.add(new Spell("default.theme/pictures/spells/si_010.png", "Large Fireball", "large fireball"));
        spells.add(new Spell("default.theme/pictures/spells/si_011.png", "Create Earth Wall", "create earth wall"));
        spells.add(new Spell("default.theme/pictures/spells/si_013.png", "Call Holy Servant", "call holy servant"));
        spells.add(new Spell("default.theme/pictures/spells/si_014.png", "Summon Avatar", "summon avatar"));
        spells.add(new Spell("default.theme/pictures/spells/si_015.png", "Cause Rabies", "cause rabies"));
        spells.add(new Spell("default.theme/pictures/spells/si_016.png", "Create Food", "create food"));
        spells.add(new Spell("default.theme/pictures/spells/si_017.png", "Magic Bullet", "magic bullet"));
        spells.add(new Spell("default.theme/pictures/spells/si_018.png", "Burning Hands", "burning hands"));
        spells.add(new Spell("default.theme/pictures/spells/si_019.png", "Dragon Breath", "dragon breath"));
        spells.add(new Spell("default.theme/pictures/spells/si_020.png", "Firebolt", "firebolt"));
        spells.add(new Spell("default.theme/pictures/spells/si_021.png", "Rune of Fire", "rune of fire"));
        spells.add(new Spell("default.theme/pictures/spells/si_022.png", "Steambolt", "steambolt"));
        spells.add(new Spell("default.theme/pictures/spells/si_023.png", "Hellfire", "hellfire"));
        spells.add(new Spell("default.theme/pictures/spells/si_024.png", "Icestorm", "icestorm"));
        spells.add(new Spell("default.theme/pictures/spells/si_025.png", "Large Icestorm", "large icestorm"));
        spells.add(new Spell("default.theme/pictures/spells/si_026.png", "Frostbolt", "frostbolt"));
        spells.add(new Spell("default.theme/pictures/spells/si_027.png", "Rune of Frost", "rune of frost"));
        spells.add(new Spell("default.theme/pictures/spells/si_028.png", "Ball Lightning", "ball lightning"));
        spells.add(new Spell("default.theme/pictures/spells/si_029.png", "Rune of Shocking", "rune of shocking"));
        spells.add(new Spell("default.theme/pictures/spells/si_030.png", "Forked Lightning", "forked lightning"));
        spells.add(new Spell("default.theme/pictures/spells/si_031.png", "Poison Cloud", "poison cloud"));
        spells.add(new Spell("default.theme/pictures/spells/si_032.png", "Large Bullet", "large bullet"));
        spells.add(new Spell("default.theme/pictures/spells/si_033.png", "Bullet Swarm", "bullet swarm"));
        spells.add(new Spell("default.theme/pictures/spells/si_034.png", "Bullet Storm", "bullet storm"));
        spells.add(new Spell("default.theme/pictures/spells/si_035.png", "Small Speedball", "small speedball"));
        spells.add(new Spell("default.theme/pictures/spells/si_036.png", "Large Speedball", "large speedball"));
        spells.add(new Spell("default.theme/pictures/spells/si_037.png", "Magic Missile", "magic missile"));
        spells.add(new Spell("default.theme/pictures/spells/si_038.png", "Summon Golem", "summon golem"));
        spells.add(new Spell("default.theme/pictures/spells/si_039.png", "Summon Fire Elemental", "summon fire elemental"));
        spells.add(new Spell("default.theme/pictures/spells/si_040.png", "Summon Earth Elemental", "summon earth elemental"));
        spells.add(new Spell("default.theme/pictures/spells/si_041.png", "Summon Water Elemental", "summon water elemental"));
        spells.add(new Spell("default.theme/pictures/spells/si_042.png", "Summon Air Elemental", "summon air elemental"));
        spells.add(new Spell("default.theme/pictures/spells/si_043.png", "Summon Pet Monster", "summon pet monster"));
        spells.add(new Spell("default.theme/pictures/spells/si_044.png", "Mystic Fist", "mystic fist"));
        spells.add(new Spell("default.theme/pictures/spells/si_045.png", "Summon Evil Monster", "summon evil monster"));
        spells.add(new Spell("default.theme/pictures/spells/si_046.png", "Summon Cult Monsters", "summon cult monsters"));
        spells.add(new Spell("default.theme/pictures/spells/si_047.png", "Insect Plague", "insect plague"));
        spells.add(new Spell("default.theme/pictures/spells/si_048.png", "Dancing Sword", "dancing sword"));
        spells.add(new Spell("default.theme/pictures/spells/si_049.png", "Build Director", "build director"));
        spells.add(new Spell("default.theme/pictures/spells/si_050.png", "Create Fire Wall", "create fire wall"));
        spells.add(new Spell("default.theme/pictures/spells/si_051.png", "Create Frost Wall", "create frost wall"));
        spells.add(new Spell("default.theme/pictures/spells/si_052.png", "Build Bullet Wall", "build bullet wall"));
        spells.add(new Spell("default.theme/pictures/spells/si_053.png", "Build Lightning Wall", "build lightning wall"));
        spells.add(new Spell("default.theme/pictures/spells/si_054.png", "Build Fireball Wall", "build fireball wall"));
        spells.add(new Spell("default.theme/pictures/spells/si_055.png", "Create Pool of Chaos", "create pool of chaos"));
        spells.add(new Spell("default.theme/pictures/spells/si_056.png", "Magic Rune", "magic rune"));
        spells.add(new Spell("default.theme/pictures/spells/si_057.png", "Create Missile", "create missile"));
        spells.add(new Spell("default.theme/pictures/spells/si_058.png", "Summon Fog", "summon fog"));
        spells.add(new Spell("default.theme/pictures/spells/si_059.png", "Wall of Thorns", "wall of thorns"));
        spells.add(new Spell("default.theme/pictures/spells/si_060.png", "Staff to Snake", "staff to snake"));
        spells.add(new Spell("default.theme/pictures/spells/si_061.png", "Spiderweb", "spiderweb"));
        spells.add(new Spell("default.theme/pictures/spells/si_062.png", "Armour", "armour"));
        spells.add(new Spell("default.theme/pictures/spells/si_063.png", "Strength", "strength"));
        spells.add(new Spell("default.theme/pictures/spells/si_064.png", "Dexterity", "dexterity"));
        spells.add(new Spell("default.theme/pictures/spells/si_065.png", "Constitution", "constitution"));
        spells.add(new Spell("default.theme/pictures/spells/si_066.png", "Charisma", "charisma"));
        spells.add(new Spell("default.theme/pictures/spells/si_067.png", "Heroism", "heroism"));
        spells.add(new Spell("default.theme/pictures/spells/si_068.png", "Haste", "haste"));
        spells.add(new Spell("default.theme/pictures/spells/si_069.png", "Ironwood Skin", "ironwood skin"));
        spells.add(new Spell("default.theme/pictures/spells/si_070.png", "Wrathful Eye", "wrathful eye"));
        spells.add(new Spell("default.theme/pictures/spells/si_071.png", "Minor Healing", "minor healing"));
        spells.add(new Spell("default.theme/pictures/spells/si_072.png", "Medium Healing", "medium healing"));
        spells.add(new Spell("default.theme/pictures/spells/si_073.png", "Major Healing", "major healing"));
        spells.add(new Spell("default.theme/pictures/spells/si_074.png", "Heal", "heal"));
        spells.add(new Spell("default.theme/pictures/spells/si_075.png", "Restoration", "restoration"));
        spells.add(new Spell("default.theme/pictures/spells/si_076.png", "Regenerate Spellpoints", "regenerate spellpoints"));
        spells.add(new Spell("default.theme/pictures/spells/si_077.png", "Counterwall", "counterwall"));
        spells.add(new Spell("default.theme/pictures/spells/si_078.png", "Cure Poison", "cure poison"));
        spells.add(new Spell("default.theme/pictures/spells/si_079.png", "Cure Confusion", "cure confusion"));
        spells.add(new Spell("default.theme/pictures/spells/si_080.png", "Cure Blindness", "cure blindness"));
        spells.add(new Spell("default.theme/pictures/spells/si_081.png", "Cure Disease", "cure disease"));
        spells.add(new Spell("default.theme/pictures/spells/si_082.png", "Remove Curse", "remove curse"));
        spells.add(new Spell("default.theme/pictures/spells/si_083.png", "Remove Damnation", "remove damnation"));
        spells.add(new Spell("default.theme/pictures/spells/si_084.png", "Raise Dead", "raise dead"));
        spells.add(new Spell("default.theme/pictures/spells/si_085.png", "Resurrection", "resurrection"));
        spells.add(new Spell("default.theme/pictures/spells/si_086.png", "Reincarnation", "reincarnation"));
        spells.add(new Spell("default.theme/pictures/spells/si_087.png", "Cancellation", "cancellation"));
        spells.add(new Spell("default.theme/pictures/spells/si_088.png", "Counterspell", "counterspell"));
        spells.add(new Spell("default.theme/pictures/spells/si_089.png", "Antimagic Rune", "antimagic rune"));
        spells.add(new Spell("default.theme/pictures/spells/si_090.png", "Disarm", "disarm"));
        spells.add(new Spell("default.theme/pictures/spells/si_091.png", "Bless", "bless"));
        spells.add(new Spell("default.theme/pictures/spells/si_092.png", "Curse", "curse"));
        spells.add(new Spell("default.theme/pictures/spells/si_093.png", "Regeneration", "regeneration"));
        spells.add(new Spell("default.theme/pictures/spells/si_094.png", "Holy Possession", "holy possession"));
        spells.add(new Spell("default.theme/pictures/spells/si_095.png", "Consecrate", "consecrate"));
        spells.add(new Spell("default.theme/pictures/spells/si_096.png", "Magic Mapping", "magic mapping"));
        spells.add(new Spell("default.theme/pictures/spells/si_097.png", "Probe", "probe"));
        spells.add(new Spell("default.theme/pictures/spells/si_098.png", "Identify", "identify"));
        spells.add(new Spell("default.theme/pictures/spells/si_099.png", "Detect Magic", "detect magic"));
        spells.add(new Spell("default.theme/pictures/spells/si_100.png", "Detect Monster", "detect monster"));
        spells.add(new Spell("default.theme/pictures/spells/si_101.png", "Xray", "xray"));
        spells.add(new Spell("default.theme/pictures/spells/si_102.png", "Dark Vision", "dark vision"));
        spells.add(new Spell("default.theme/pictures/spells/si_103.png", "Perceive Self", "perceive self"));
        spells.add(new Spell("default.theme/pictures/spells/si_104.png", "Detect Evil", "detect evil"));
        spells.add(new Spell("default.theme/pictures/spells/si_105.png", "Detect Curse", "detect curse"));
        spells.add(new Spell("default.theme/pictures/spells/si_106.png", "Show Invisible", "show invisible"));
        spells.add(new Spell("default.theme/pictures/spells/si_107.png", "Animate Weapon", "animate weapon"));
        spells.add(new Spell("default.theme/pictures/spells/si_108.png", "Fear", "fear"));
        spells.add(new Spell("default.theme/pictures/spells/si_109.png", "Confusion", "confusion"));
        spells.add(new Spell("default.theme/pictures/spells/si_110.png", "Mass Confusion", "mass confusion"));
        spells.add(new Spell("default.theme/pictures/spells/si_111.png", "Charm Monsters", "charm monsters"));
        spells.add(new Spell("default.theme/pictures/spells/si_112.png", "Dimension Door", "dimension door"));
        spells.add(new Spell("default.theme/pictures/spells/si_113.png", "Faery Fire", "faery fire"));
        spells.add(new Spell("default.theme/pictures/spells/si_114.png", "Pacify", "pacify"));
        spells.add(new Spell("default.theme/pictures/spells/si_115.png", "Command Undead", "command undead"));
        spells.add(new Spell("default.theme/pictures/spells/si_116.png", "Conflict", "conflict"));
        spells.add(new Spell("default.theme/pictures/spells/si_117.png", "Word of Recall", "word of recall"));
        spells.add(new Spell("default.theme/pictures/spells/si_118.png", "Light", "light"));
        spells.add(new Spell("default.theme/pictures/spells/si_119.png", "Darkness", "darkness"));
        spells.add(new Spell("default.theme/pictures/spells/si_120.png", "Nightfall", "nightfall"));
        spells.add(new Spell("default.theme/pictures/spells/si_121.png", "Daylight", "daylight"));
        spells.add(new Spell("default.theme/pictures/spells/si_122.png", "Sunspear", "sunspear"));
        spells.add(new Spell("default.theme/pictures/spells/si_123.png", "Rune of Blasting", "rune of blasting"));
        spells.add(new Spell("default.theme/pictures/spells/si_124.png", "Rune of Death", "rune of death"));
        spells.add(new Spell("default.theme/pictures/spells/si_125.png", "Flaming Aura", "flaming aura"));
        spells.add(new Spell("default.theme/pictures/spells/si_126.png", "Vitriol", "vitriol"));
        spells.add(new Spell("default.theme/pictures/spells/si_127.png", "Vitriol Splash", "vitriol splash"));
        spells.add(new Spell("default.theme/pictures/spells/si_128.png", "Face of Death", "face of death"));
        spells.add(new Spell("default.theme/pictures/spells/si_129.png", "Finger of Death", "finger of death"));
        spells.add(new Spell("default.theme/pictures/spells/si_130.png", "Turn Undead", "turn undead"));
        spells.add(new Spell("default.theme/pictures/spells/si_131.png", "Holy Word", "holy word"));
        spells.add(new Spell("default.theme/pictures/spells/si_132.png", "Banishment", "banishment"));
        spells.add(new Spell("default.theme/pictures/spells/si_133.png", "Holy Orb", "holy orb"));
        spells.add(new Spell("default.theme/pictures/spells/si_134.png", "Holy Wrath", "holy wrath"));
        spells.add(new Spell("default.theme/pictures/spells/si_135.png", "Wonder", "wonder"));
        spells.add(new Spell("default.theme/pictures/spells/si_136.png", "Polymorph", "polymorph"));
        spells.add(new Spell("default.theme/pictures/spells/si_137.png", "Alchemy", "alchemy"));
        spells.add(new Spell("default.theme/pictures/spells/si_138.png", "Charging", "charging"));
        spells.add(new Spell("default.theme/pictures/spells/si_139.png", "Rune of Magic Drain", "rune of magic drain"));
        spells.add(new Spell("default.theme/pictures/spells/si_140.png", "Rune of Transferrence", "rune of transferrence"));
        spells.add(new Spell("default.theme/pictures/spells/si_141.png", "Magic Drain", "magic drain"));
        spells.add(new Spell("default.theme/pictures/spells/si_142.png", "Transferrence", "transferrence"));
        spells.add(new Spell("default.theme/pictures/spells/si_143.png", "Mana Blast", "mana blast"));
        spells.add(new Spell("default.theme/pictures/spells/si_144.png", "Small Manaball", "small manaball"));
        spells.add(new Spell("default.theme/pictures/spells/si_145.png", "Medium Manaball", "medium manaball"));
        spells.add(new Spell("default.theme/pictures/spells/si_146.png", "Large Manaball", "large manaball"));
        spells.add(new Spell("default.theme/pictures/spells/si_147.png", "Mana Bolt", "mana bolt"));
        spells.add(new Spell("default.theme/pictures/spells/si_148.png", "Cause Light Wounds", "cause light wounds"));
        spells.add(new Spell("default.theme/pictures/spells/si_149.png", "Cause Medium Wounds", "cause medium wounds"));
        spells.add(new Spell("default.theme/pictures/spells/si_150.png", "Cause Serious Wounds", "cause serious wounds"));
        spells.add(new Spell("default.theme/pictures/spells/si_151.png", "Cause Many Wounds", "cause many wounds"));
        spells.add(new Spell("default.theme/pictures/spells/si_152.png", "Cause Critical Wounds", "cause critical wounds"));
        spells.add(new Spell("default.theme/pictures/spells/si_153.png", "Cause Cold", "cause cold"));
        spells.add(new Spell("default.theme/pictures/spells/si_154.png", "Cause Leprosy", "cause leprosy"));
        spells.add(new Spell("default.theme/pictures/spells/si_155.png", "Cause Smallpox", "cause smallpox"));
        spells.add(new Spell("default.theme/pictures/spells/si_156.png", "Cause White Death", "cause white death"));
        spells.add(new Spell("default.theme/pictures/spells/si_157.png", "Cause Anthrax", "cause anthrax"));
        spells.add(new Spell("default.theme/pictures/spells/si_158.png", "Cause Typhoid", "cause typhoid"));
        spells.add(new Spell("default.theme/pictures/spells/si_159.png", "Cause Flu", "cause flu"));
        spells.add(new Spell("default.theme/pictures/spells/si_160.png", "Cause Red Death", "cause raise death"));
        spells.add(new Spell("default.theme/pictures/spells/si_161.png", "Poison Fog", "poison fog"));
        spells.add(new Spell("default.theme/pictures/spells/si_162.png", "Rage", "rage"));
        spells.add(new Spell("default.theme/pictures/spells/si_163.png", "Divine Shock", "divine shock"));
        spells.add(new Spell("default.theme/pictures/spells/si_164.png", "Retributive Strike", "retributive strike"));
        spells.add(new Spell("default.theme/pictures/spells/si_165.png", "Protection from Cold", "protection from cold"));
        spells.add(new Spell("default.theme/pictures/spells/si_166.png", "Protection from Electricity", "protection from electricity"));
        spells.add(new Spell("default.theme/pictures/spells/si_167.png", "Protection from Fire", "protection from fire"));
        spells.add(new Spell("default.theme/pictures/spells/si_168.png", "Protection from Poison", "protection from poison"));
        spells.add(new Spell("default.theme/pictures/spells/si_169.png", "Protection from Slow", "protection from slow"));
        spells.add(new Spell("default.theme/pictures/spells/si_170.png", "Protection from Paralysis", "protection from paralysis"));
        spells.add(new Spell("default.theme/pictures/spells/si_171.png", "Protection from Draining", "protection from draining"));
        spells.add(new Spell("default.theme/pictures/spells/si_172.png", "Protection from Magic", "protection from magic"));
        spells.add(new Spell("default.theme/pictures/spells/si_173.png", "Protection from Attack", "protection from attack"));
        spells.add(new Spell("default.theme/pictures/spells/si_174.png", "Protection from Confusion", "protection from confusion"));
        spells.add(new Spell("default.theme/pictures/spells/si_175.png", "Protection from Cancellation", "protection from cancellation"));
        spells.add(new Spell("default.theme/pictures/spells/si_176.png", "Protection from Depletion", "protection from depletion"));
        spells.add(new Spell("default.theme/pictures/spells/si_177.png", "Defense", "defense"));
        spells.add(new Spell("default.theme/pictures/spells/si_178.png", "Sanctuary", "sanctuary"));
        spells.add(new Spell("default.theme/pictures/spells/si_179.png", "Peace", "peace"));
        spells.add(new Spell("default.theme/pictures/spells/si_180.png", "Paralyze", "paralyze"));
        spells.add(new Spell("default.theme/pictures/spells/si_181.png", "Destruction", "destruction"));
        spells.add(new Spell("default.theme/pictures/spells/si_182.png", "Invisible", "invisible"));
        spells.add(new Spell("default.theme/pictures/spells/si_183.png", "Improved Invisibility", "improved invisibility"));
        spells.add(new Spell("default.theme/pictures/spells/si_184.png", "Earth to Dust", "earth to dust"));
        spells.add(new Spell("default.theme/pictures/spells/si_185.png", "Levitate", "levitate"));
        spells.add(new Spell("default.theme/pictures/spells/si_186.png", "Slow", "slow"));
        spells.add(new Spell("default.theme/pictures/spells/si_187.png", "Aggravation", "aggravation"));
        spells.add(new Spell("default.theme/pictures/spells/si_188.png", "Color Spray", "color spray"));
        spells.add(new Spell("default.theme/pictures/spells/si_189.png", "Shockwave", "shockwave"));
        spells.add(new Spell("default.theme/pictures/spells/si_190.png", "Marking Rune", "marking rune"));
        spells.add(new Spell("default.theme/pictures/spells/si_191.png", "Invisible to Undead", "invisible to undead"));
        spells.add(new Spell("default.theme/pictures/spells/si_192.png", "Cause Black Death", "cause black death"));
        spells.add(new Spell("default.theme/pictures/spells/si_193.png", "Windstorm", "windstorm"));
        spells.add(new Spell("default.theme/pictures/spells/si_194.png", "Meteor Swarm", "meteor swarm"));
        spells.add(new Spell("default.theme/pictures/spells/si_195.png", "Town Portal", "town portal"));
        spells.add(new Spell("default.theme/pictures/spells/si_196.png", "Missile Swarm", "missile swarm"));
    }

    public static void addSpell(final int tag, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path, final int face, final String name, final String message)
    {
        Faces.getFace(face).setName("spell_"+tag);
        try
        {
            Faces.askface(face);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Spell sp = new Spell(Faces.getFace(face), tag, name, message);
        sp.setLevel(level);
        sp.setCastingTime(castingTime);
        sp.setMana(mana);
        sp.setGrace(grace);
        sp.setDamage(damage);
        sp.setSkill(skill);
        spells.add(sp);

        final CrossfireCommandAddSpellEvent evt = new CrossfireCommandAddSpellEvent(new Object(), sp);
        for (final CrossfireSpellAddedListener listener : addspellListeners)
        {
            listener.commandAddSpellReceived(evt);
        }
    }

    public static void updateSpell(final int flags, final int tag, final int mana, final int grace, final int damage)
    {
        // XXX: updateSpell() not yet implemented
    }

    public static void deleteSpell(final int tag)
    {
        // XXX: deleteSpell() not yet implemented
    }

    public static ItemsManager getItemsManager()
    {
        return itemsManager;
    }
}

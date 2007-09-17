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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Manages all known spells.
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class SpellsManager
{
    public static final int SPELLMODE_LOCAL = 0;

    public static final int SPELLMODE_SENT = 1;

    private int spellmode = SPELLMODE_LOCAL;

    /**
     * All known spells.
     */
    private final List<Spell> spells = new ArrayList<Spell>();

    private final List<CrossfireSpellChangedListener> listeners = new ArrayList<CrossfireSpellChangedListener>();

    /**
     * A {@link Comparator} to compare {@link Spell} instances by spell path
     * and name.
     */
    private final Comparator<Spell> spellNameComparator = new Comparator<Spell>()
    {
        /** {@inheritDoc} */
        public int compare(final Spell spell1, final Spell spell2)
        {
            final int path1 = spell1.getPath();
            final int path2 = spell2.getPath();
            if (path1 < path2) return -1;
            if (path1 > path2) return +1;
            return String.CASE_INSENSITIVE_ORDER.compare(spell1.getName(), spell2.getName());
        }
    };

    /**
     * Create a new instance.
     */
    public SpellsManager()
    {
        initSpells();
    }

    public void setSpellMode(final int spellmode)
    {
        this.spellmode = spellmode;
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

    public void addCrossfireSpellChangedListener(final CrossfireSpellChangedListener listener)
    {
        listeners.add(listener);
    }

    public void removeCrossfireSpellChangedListener(final CrossfireSpellChangedListener listener)
    {
        listeners.remove(listener);
    }

    public List<Spell> getSpellList()
    {
        return spells;
    }

    private void initSpells()
    {
        for (int i = spells.size()-1; i >= 0; i--)
        {
            final Spell spell = spells.remove(i);
            for (final CrossfireSpellChangedListener listener : listeners)
            {
                listener.spellRemoved(spell, i);
            }
        }

        if (spellmode != SPELLMODE_LOCAL)
        {
            return;
        }

        addSpell(new Spell("resource/spells/si_001.png", "Small Lightning", "small lightning"));
        addSpell(new Spell("resource/spells/si_002.png", "Large Lightning", "large lightning"));
        addSpell(new Spell("resource/spells/si_003.png", "Create Bomb", "create bomb"));
        addSpell(new Spell("resource/spells/si_004.png", "Comet", "comet"));
        addSpell(new Spell("resource/spells/si_005.png", "Small Snowstorm", "small snowstorm"));
        addSpell(new Spell("resource/spells/si_006.png", "Medium Snowstorm", "medium snowstorm"));
        addSpell(new Spell("resource/spells/si_007.png", "Large Snowstorm", "large snowstorm"));
        addSpell(new Spell("resource/spells/si_008.png", "Small Fireball", "small fireball"));
        addSpell(new Spell("resource/spells/si_009.png", "Medium Fireball", "medium fireball"));
        addSpell(new Spell("resource/spells/si_010.png", "Large Fireball", "large fireball"));
        addSpell(new Spell("resource/spells/si_011.png", "Create Earth Wall", "create earth wall"));
        addSpell(new Spell("resource/spells/si_013.png", "Call Holy Servant", "call holy servant"));
        addSpell(new Spell("resource/spells/si_014.png", "Summon Avatar", "summon avatar"));
        addSpell(new Spell("resource/spells/si_015.png", "Cause Rabies", "cause rabies"));
        addSpell(new Spell("resource/spells/si_016.png", "Create Food", "create food"));
        addSpell(new Spell("resource/spells/si_017.png", "Magic Bullet", "magic bullet"));
        addSpell(new Spell("resource/spells/si_018.png", "Burning Hands", "burning hands"));
        addSpell(new Spell("resource/spells/si_019.png", "Dragon Breath", "dragon breath"));
        addSpell(new Spell("resource/spells/si_020.png", "Firebolt", "firebolt"));
        addSpell(new Spell("resource/spells/si_021.png", "Rune of Fire", "rune of fire"));
        addSpell(new Spell("resource/spells/si_022.png", "Steambolt", "steambolt"));
        addSpell(new Spell("resource/spells/si_023.png", "Hellfire", "hellfire"));
        addSpell(new Spell("resource/spells/si_024.png", "Icestorm", "icestorm"));
        addSpell(new Spell("resource/spells/si_025.png", "Large Icestorm", "large icestorm"));
        addSpell(new Spell("resource/spells/si_026.png", "Frostbolt", "frostbolt"));
        addSpell(new Spell("resource/spells/si_027.png", "Rune of Frost", "rune of frost"));
        addSpell(new Spell("resource/spells/si_028.png", "Ball Lightning", "ball lightning"));
        addSpell(new Spell("resource/spells/si_029.png", "Rune of Shocking", "rune of shocking"));
        addSpell(new Spell("resource/spells/si_030.png", "Forked Lightning", "forked lightning"));
        addSpell(new Spell("resource/spells/si_031.png", "Poison Cloud", "poison cloud"));
        addSpell(new Spell("resource/spells/si_032.png", "Large Bullet", "large bullet"));
        addSpell(new Spell("resource/spells/si_033.png", "Bullet Swarm", "bullet swarm"));
        addSpell(new Spell("resource/spells/si_034.png", "Bullet Storm", "bullet storm"));
        addSpell(new Spell("resource/spells/si_035.png", "Small Speedball", "small speedball"));
        addSpell(new Spell("resource/spells/si_036.png", "Large Speedball", "large speedball"));
        addSpell(new Spell("resource/spells/si_037.png", "Magic Missile", "magic missile"));
        addSpell(new Spell("resource/spells/si_038.png", "Summon Golem", "summon golem"));
        addSpell(new Spell("resource/spells/si_039.png", "Summon Fire Elemental", "summon fire elemental"));
        addSpell(new Spell("resource/spells/si_040.png", "Summon Earth Elemental", "summon earth elemental"));
        addSpell(new Spell("resource/spells/si_041.png", "Summon Water Elemental", "summon water elemental"));
        addSpell(new Spell("resource/spells/si_042.png", "Summon Air Elemental", "summon air elemental"));
        addSpell(new Spell("resource/spells/si_043.png", "Summon Pet Monster", "summon pet monster"));
        addSpell(new Spell("resource/spells/si_044.png", "Mystic Fist", "mystic fist"));
        addSpell(new Spell("resource/spells/si_045.png", "Summon Evil Monster", "summon evil monster"));
        addSpell(new Spell("resource/spells/si_046.png", "Summon Cult Monsters", "summon cult monsters"));
        addSpell(new Spell("resource/spells/si_047.png", "Insect Plague", "insect plague"));
        addSpell(new Spell("resource/spells/si_048.png", "Dancing Sword", "dancing sword"));
        addSpell(new Spell("resource/spells/si_049.png", "Build Director", "build director"));
        addSpell(new Spell("resource/spells/si_050.png", "Create Fire Wall", "create fire wall"));
        addSpell(new Spell("resource/spells/si_051.png", "Create Frost Wall", "create frost wall"));
        addSpell(new Spell("resource/spells/si_052.png", "Build Bullet Wall", "build bullet wall"));
        addSpell(new Spell("resource/spells/si_053.png", "Build Lightning Wall", "build lightning wall"));
        addSpell(new Spell("resource/spells/si_054.png", "Build Fireball Wall", "build fireball wall"));
        addSpell(new Spell("resource/spells/si_055.png", "Create Pool of Chaos", "create pool of chaos"));
        addSpell(new Spell("resource/spells/si_056.png", "Magic Rune", "magic rune"));
        addSpell(new Spell("resource/spells/si_057.png", "Create Missile", "create missile"));
        addSpell(new Spell("resource/spells/si_058.png", "Summon Fog", "summon fog"));
        addSpell(new Spell("resource/spells/si_059.png", "Wall of Thorns", "wall of thorns"));
        addSpell(new Spell("resource/spells/si_060.png", "Staff to Snake", "staff to snake"));
        addSpell(new Spell("resource/spells/si_061.png", "Spiderweb", "spiderweb"));
        addSpell(new Spell("resource/spells/si_062.png", "Armour", "armour"));
        addSpell(new Spell("resource/spells/si_063.png", "Strength", "strength"));
        addSpell(new Spell("resource/spells/si_064.png", "Dexterity", "dexterity"));
        addSpell(new Spell("resource/spells/si_065.png", "Constitution", "constitution"));
        addSpell(new Spell("resource/spells/si_066.png", "Charisma", "charisma"));
        addSpell(new Spell("resource/spells/si_067.png", "Heroism", "heroism"));
        addSpell(new Spell("resource/spells/si_068.png", "Haste", "haste"));
        addSpell(new Spell("resource/spells/si_069.png", "Ironwood Skin", "ironwood skin"));
        addSpell(new Spell("resource/spells/si_070.png", "Wrathful Eye", "wrathful eye"));
        addSpell(new Spell("resource/spells/si_071.png", "Minor Healing", "minor healing"));
        addSpell(new Spell("resource/spells/si_072.png", "Medium Healing", "medium healing"));
        addSpell(new Spell("resource/spells/si_073.png", "Major Healing", "major healing"));
        addSpell(new Spell("resource/spells/si_074.png", "Heal", "heal"));
        addSpell(new Spell("resource/spells/si_075.png", "Restoration", "restoration"));
        addSpell(new Spell("resource/spells/si_076.png", "Regenerate Spellpoints", "regenerate spellpoints"));
        addSpell(new Spell("resource/spells/si_077.png", "Counterwall", "counterwall"));
        addSpell(new Spell("resource/spells/si_078.png", "Cure Poison", "cure poison"));
        addSpell(new Spell("resource/spells/si_079.png", "Cure Confusion", "cure confusion"));
        addSpell(new Spell("resource/spells/si_080.png", "Cure Blindness", "cure blindness"));
        addSpell(new Spell("resource/spells/si_081.png", "Cure Disease", "cure disease"));
        addSpell(new Spell("resource/spells/si_082.png", "Remove Curse", "remove curse"));
        addSpell(new Spell("resource/spells/si_083.png", "Remove Damnation", "remove damnation"));
        addSpell(new Spell("resource/spells/si_084.png", "Raise Dead", "raise dead"));
        addSpell(new Spell("resource/spells/si_085.png", "Resurrection", "resurrection"));
        addSpell(new Spell("resource/spells/si_086.png", "Reincarnation", "reincarnation"));
        addSpell(new Spell("resource/spells/si_087.png", "Cancellation", "cancellation"));
        addSpell(new Spell("resource/spells/si_088.png", "Counterspell", "counterspell"));
        addSpell(new Spell("resource/spells/si_089.png", "Antimagic Rune", "antimagic rune"));
        addSpell(new Spell("resource/spells/si_090.png", "Disarm", "disarm"));
        addSpell(new Spell("resource/spells/si_091.png", "Bless", "bless"));
        addSpell(new Spell("resource/spells/si_092.png", "Curse", "curse"));
        addSpell(new Spell("resource/spells/si_093.png", "Regeneration", "regeneration"));
        addSpell(new Spell("resource/spells/si_094.png", "Holy Possession", "holy possession"));
        addSpell(new Spell("resource/spells/si_095.png", "Consecrate", "consecrate"));
        addSpell(new Spell("resource/spells/si_096.png", "Magic Mapping", "magic mapping"));
        addSpell(new Spell("resource/spells/si_097.png", "Probe", "probe"));
        addSpell(new Spell("resource/spells/si_098.png", "Identify", "identify"));
        addSpell(new Spell("resource/spells/si_099.png", "Detect Magic", "detect magic"));
        addSpell(new Spell("resource/spells/si_100.png", "Detect Monster", "detect monster"));
        addSpell(new Spell("resource/spells/si_101.png", "Xray", "xray"));
        addSpell(new Spell("resource/spells/si_102.png", "Dark Vision", "dark vision"));
        addSpell(new Spell("resource/spells/si_103.png", "Perceive Self", "perceive self"));
        addSpell(new Spell("resource/spells/si_104.png", "Detect Evil", "detect evil"));
        addSpell(new Spell("resource/spells/si_105.png", "Detect Curse", "detect curse"));
        addSpell(new Spell("resource/spells/si_106.png", "Show Invisible", "show invisible"));
        addSpell(new Spell("resource/spells/si_107.png", "Animate Weapon", "animate weapon"));
        addSpell(new Spell("resource/spells/si_108.png", "Fear", "fear"));
        addSpell(new Spell("resource/spells/si_109.png", "Confusion", "confusion"));
        addSpell(new Spell("resource/spells/si_110.png", "Mass Confusion", "mass confusion"));
        addSpell(new Spell("resource/spells/si_111.png", "Charm Monsters", "charm monsters"));
        addSpell(new Spell("resource/spells/si_112.png", "Dimension Door", "dimension door"));
        addSpell(new Spell("resource/spells/si_113.png", "Faery Fire", "faery fire"));
        addSpell(new Spell("resource/spells/si_114.png", "Pacify", "pacify"));
        addSpell(new Spell("resource/spells/si_115.png", "Command Undead", "command undead"));
        addSpell(new Spell("resource/spells/si_116.png", "Conflict", "conflict"));
        addSpell(new Spell("resource/spells/si_117.png", "Word of Recall", "word of recall"));
        addSpell(new Spell("resource/spells/si_118.png", "Light", "light"));
        addSpell(new Spell("resource/spells/si_119.png", "Darkness", "darkness"));
        addSpell(new Spell("resource/spells/si_120.png", "Nightfall", "nightfall"));
        addSpell(new Spell("resource/spells/si_121.png", "Daylight", "daylight"));
        addSpell(new Spell("resource/spells/si_122.png", "Sunspear", "sunspear"));
        addSpell(new Spell("resource/spells/si_123.png", "Rune of Blasting", "rune of blasting"));
        addSpell(new Spell("resource/spells/si_124.png", "Rune of Death", "rune of death"));
        addSpell(new Spell("resource/spells/si_125.png", "Flaming Aura", "flaming aura"));
        addSpell(new Spell("resource/spells/si_126.png", "Vitriol", "vitriol"));
        addSpell(new Spell("resource/spells/si_127.png", "Vitriol Splash", "vitriol splash"));
        addSpell(new Spell("resource/spells/si_128.png", "Face of Death", "face of death"));
        addSpell(new Spell("resource/spells/si_129.png", "Finger of Death", "finger of death"));
        addSpell(new Spell("resource/spells/si_130.png", "Turn Undead", "turn undead"));
        addSpell(new Spell("resource/spells/si_131.png", "Holy Word", "holy word"));
        addSpell(new Spell("resource/spells/si_132.png", "Banishment", "banishment"));
        addSpell(new Spell("resource/spells/si_133.png", "Holy Orb", "holy orb"));
        addSpell(new Spell("resource/spells/si_134.png", "Holy Wrath", "holy wrath"));
        addSpell(new Spell("resource/spells/si_135.png", "Wonder", "wonder"));
        addSpell(new Spell("resource/spells/si_136.png", "Polymorph", "polymorph"));
        addSpell(new Spell("resource/spells/si_137.png", "Alchemy", "alchemy"));
        addSpell(new Spell("resource/spells/si_138.png", "Charging", "charging"));
        addSpell(new Spell("resource/spells/si_139.png", "Rune of Magic Drain", "rune of magic drain"));
        addSpell(new Spell("resource/spells/si_140.png", "Rune of Transferrence", "rune of transferrence"));
        addSpell(new Spell("resource/spells/si_141.png", "Magic Drain", "magic drain"));
        addSpell(new Spell("resource/spells/si_142.png", "Transferrence", "transferrence"));
        addSpell(new Spell("resource/spells/si_143.png", "Mana Blast", "mana blast"));
        addSpell(new Spell("resource/spells/si_144.png", "Small Manaball", "small manaball"));
        addSpell(new Spell("resource/spells/si_145.png", "Medium Manaball", "medium manaball"));
        addSpell(new Spell("resource/spells/si_146.png", "Large Manaball", "large manaball"));
        addSpell(new Spell("resource/spells/si_147.png", "Mana Bolt", "mana bolt"));
        addSpell(new Spell("resource/spells/si_148.png", "Cause Light Wounds", "cause light wounds"));
        addSpell(new Spell("resource/spells/si_149.png", "Cause Medium Wounds", "cause medium wounds"));
        addSpell(new Spell("resource/spells/si_150.png", "Cause Serious Wounds", "cause serious wounds"));
        addSpell(new Spell("resource/spells/si_151.png", "Cause Many Wounds", "cause many wounds"));
        addSpell(new Spell("resource/spells/si_152.png", "Cause Critical Wounds", "cause critical wounds"));
        addSpell(new Spell("resource/spells/si_153.png", "Cause Cold", "cause cold"));
        addSpell(new Spell("resource/spells/si_154.png", "Cause Leprosy", "cause leprosy"));
        addSpell(new Spell("resource/spells/si_155.png", "Cause Smallpox", "cause smallpox"));
        addSpell(new Spell("resource/spells/si_156.png", "Cause White Death", "cause white death"));
        addSpell(new Spell("resource/spells/si_157.png", "Cause Anthrax", "cause anthrax"));
        addSpell(new Spell("resource/spells/si_158.png", "Cause Typhoid", "cause typhoid"));
        addSpell(new Spell("resource/spells/si_159.png", "Cause Flu", "cause flu"));
        addSpell(new Spell("resource/spells/si_160.png", "Cause Red Death", "cause raise death"));
        addSpell(new Spell("resource/spells/si_161.png", "Poison Fog", "poison fog"));
        addSpell(new Spell("resource/spells/si_162.png", "Rage", "rage"));
        addSpell(new Spell("resource/spells/si_163.png", "Divine Shock", "divine shock"));
        addSpell(new Spell("resource/spells/si_164.png", "Retributive Strike", "retributive strike"));
        addSpell(new Spell("resource/spells/si_165.png", "Protection from Cold", "protection from cold"));
        addSpell(new Spell("resource/spells/si_166.png", "Protection from Electricity", "protection from electricity"));
        addSpell(new Spell("resource/spells/si_167.png", "Protection from Fire", "protection from fire"));
        addSpell(new Spell("resource/spells/si_168.png", "Protection from Poison", "protection from poison"));
        addSpell(new Spell("resource/spells/si_169.png", "Protection from Slow", "protection from slow"));
        addSpell(new Spell("resource/spells/si_170.png", "Protection from Paralysis", "protection from paralysis"));
        addSpell(new Spell("resource/spells/si_171.png", "Protection from Draining", "protection from draining"));
        addSpell(new Spell("resource/spells/si_172.png", "Protection from Magic", "protection from magic"));
        addSpell(new Spell("resource/spells/si_173.png", "Protection from Attack", "protection from attack"));
        addSpell(new Spell("resource/spells/si_174.png", "Protection from Confusion", "protection from confusion"));
        addSpell(new Spell("resource/spells/si_175.png", "Protection from Cancellation", "protection from cancellation"));
        addSpell(new Spell("resource/spells/si_176.png", "Protection from Depletion", "protection from depletion"));
        addSpell(new Spell("resource/spells/si_177.png", "Defense", "defense"));
        addSpell(new Spell("resource/spells/si_178.png", "Sanctuary", "sanctuary"));
        addSpell(new Spell("resource/spells/si_179.png", "Peace", "peace"));
        addSpell(new Spell("resource/spells/si_180.png", "Paralyze", "paralyze"));
        addSpell(new Spell("resource/spells/si_181.png", "Destruction", "destruction"));
        addSpell(new Spell("resource/spells/si_182.png", "Invisible", "invisible"));
        addSpell(new Spell("resource/spells/si_183.png", "Improved Invisibility", "improved invisibility"));
        addSpell(new Spell("resource/spells/si_184.png", "Earth to Dust", "earth to dust"));
        addSpell(new Spell("resource/spells/si_185.png", "Levitate", "levitate"));
        addSpell(new Spell("resource/spells/si_186.png", "Slow", "slow"));
        addSpell(new Spell("resource/spells/si_187.png", "Aggravation", "aggravation"));
        addSpell(new Spell("resource/spells/si_188.png", "Color Spray", "color spray"));
        addSpell(new Spell("resource/spells/si_189.png", "Shockwave", "shockwave"));
        addSpell(new Spell("resource/spells/si_190.png", "Marking Rune", "marking rune"));
        addSpell(new Spell("resource/spells/si_191.png", "Invisible to Undead", "invisible to undead"));
        addSpell(new Spell("resource/spells/si_192.png", "Cause Black Death", "cause black death"));
        addSpell(new Spell("resource/spells/si_193.png", "Windstorm", "windstorm"));
        addSpell(new Spell("resource/spells/si_194.png", "Meteor Swarm", "meteor swarm"));
        addSpell(new Spell("resource/spells/si_195.png", "Town Portal", "town portal"));
        addSpell(new Spell("resource/spells/si_196.png", "Missile Swarm", "missile swarm"));
    }

    public void addSpell(final int tag, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path, final int face, final String name, final String message)
    {
        Faces.getFace(face).setName("spell_"+tag);
        Faces.askface(face);

        final Spell sp = new Spell(Faces.getFace(face), tag, name, message);
        sp.setLevel(level);
        sp.setCastingTime(castingTime);
        sp.setMana(mana);
        sp.setGrace(grace);
        sp.setDamage(damage);
        sp.setSkill(skill);
        addSpell(sp);
    }

    private void addSpell(final Spell sp)
    {
        final int index = Collections.binarySearch(spells, sp, spellNameComparator);
        if (index < 0)
        {
            spells.add(-index-1, sp);
        }
        else
        {
            spells.set(index, sp);
        }

        for (final CrossfireSpellChangedListener listener : listeners)
        {
            listener.spellAdded(sp, index);
        }
    }

    public void updateSpell(final int flags, final int tag, final int mana, final int grace, final int damage)
    {
        int index = 0;
        for (final Spell spell : spells)
        {
            if (spell.getTag() == tag)
            {
                if ((flags&CrossfireServerConnection.UPD_SP_MANA) != 0)
                {
                    spell.setMana(mana);
                }

                if ((flags&CrossfireServerConnection.UPD_SP_GRACE) != 0)
                {
                    spell.setGrace(mana);
                }

                if ((flags&CrossfireServerConnection.UPD_SP_DAMAGE) != 0)
                {
                    spell.setDamage(mana);
                }

                for (final CrossfireSpellChangedListener listener : listeners)
                {
                    listener.spellModified(spell, index);
                }
                break;
            }
            index++;
        }
    }

    public void deleteSpell(final int tag)
    {
        int index = 0;
        for (final Spell spell : spells)
        {
            if (spell.getTag() == tag)
            {
                spells.remove(index);

                for (final CrossfireSpellChangedListener listener : listeners)
                {
                    listener.spellRemoved(spell, index);
                }
                break;
            }
            index++;
        }
    }
}

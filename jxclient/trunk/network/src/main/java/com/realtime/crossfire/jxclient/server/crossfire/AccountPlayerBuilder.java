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

package com.realtime.crossfire.jxclient.server.crossfire;

import com.realtime.crossfire.jxclient.account.CharacterInformation;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builder for {@link CharacterInformation} instances while parsing an
 * "accountplayers" packet.
 * @author Nicolas Weeger
 * @author Andreas Kirschbaum
 */
public class AccountPlayerBuilder {

    /**
     * The appender to write protocol commands to. May be <code>null</code> to
     * not write anything.
     */
    @Nullable
    private final DebugWriter debugProtocol;

    /**
     * The character's name.
     */
    @NotNull
    private String name = "";

    /**
     * The character's class.
     */
    @NotNull
    private String cClass = "";

    /**
     * The character's race.
     */
    @NotNull
    private String race = "";

    /**
     * The character's level.
     */
    private int level;

    /**
     * The character's face.
     */
    @NotNull
    private String face = "";

    /**
     * The character's party.
     */
    @NotNull
    private String party = "";

    /**
     * The character's map.
     */
    @NotNull
    private String map = "";

    /**
     * The character's face number.
     */
    private int faceNumber;

    /**
     * Creates a new instance.
     * @param debugProtocol the appender to write protocol commands to or
     * <code>null</code> to not write anything
     */
    public AccountPlayerBuilder(@Nullable final DebugWriter debugProtocol) {
        this.debugProtocol = debugProtocol;
    }

    /**
     * Finishes parsing an entry an returns the {@link CharacterInformation} for the entry.
     * @return the character information for the parsed entry
     */
    @NotNull
    public CharacterInformation finish() {
        final CharacterInformation characterInformation = new CharacterInformation(name, cClass, race, face, party, map, level, faceNumber);
        name = "";
        cClass = "";
        race = "";
        level = 0;
        face = "";
        party = "";
        map = "";
        faceNumber = 0;
        return characterInformation;
    }

    /**
     * Sets the character's name.
     * @param name the character's name
     */
    public void setName(@NotNull final String name) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv accountplayers name="+name);
        }
        this.name = name;
    }

    /**
     * Sets the character's class.
     * @param cClass the character's class
     */
    public void setClass(@NotNull final String cClass) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv accountplayers class="+cClass);
        }
        this.cClass = cClass;
    }

    /**
     * Sets the character's race.
     * @param race the character's race
     */
    public void setRace(@NotNull final String race) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv accountplayers race="+race);
        }
        this.race = race;
    }

    /**
     * Sets the character's level.
     * @param level the character's level
     */
    public void setLevel(final int level) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv accountplayers level="+this.level);
        }
        this.level = level;
    }

    /**
     * Sets the character's face.
     * @param face the character's face
     */
    public void setFace(@NotNull final String face) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv accountplayers face="+face);
        }
        this.face = face;
    }

    /**
     * Sets the character's party.
     * @param party the character's party
     */
    public void setParty(@NotNull final String party) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv accountplayers party="+party);
        }
        this.party = party;
    }

    /**
     * Sets the character's map.
     * @param map the character's map
     */
    public void setMap(@NotNull final String map) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv accountplayers map="+map);
        }
        this.map = map;
    }

    /**
     * Sets the character's face number.
     * @param faceNumber the character's face number
     */
    public void setFaceNumber(final int faceNumber) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv accountplayers face="+faceNumber);
        }
        this.faceNumber = faceNumber;
    }

}

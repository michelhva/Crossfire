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

package com.realtime.crossfire.jxclient.protocol;

import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates the message type numbers for drawextinfo messages.
 * @author Andreas Kirschbaum
 */
public class MessageTypes {

    /**
     * Internally used drawextinfo message type: a query message has been
     * received.
     */
    public static final int MSG_TYPE_QUERY = 30;

    /**
     * drawextinfo message type: character did read a book.
     */
    public static final int MSG_TYPE_BOOK = 1;

    /**
     * drawextinfo message type: character did read a card.
     */
    public static final int MSG_TYPE_CARD = 2;

    /**
     * drawextinfo message type: character did read a paper.
     */
    public static final int MSG_TYPE_PAPER = 3;

    /**
     * drawextinfo message type: character did read a sign.
     */
    public static final int MSG_TYPE_SIGN = 4;

    /**
     * drawextinfo message type: character did read a monument.
     */
    public static final int MSG_TYPE_MONUMENT = 5;

    /**
     * drawextinfo message type: a NPC/magic mouth/altar/etc. talks.
     */
    public static final int MSG_TYPE_DIALOG = 6;

    /**
     * drawextinfo message type: motd text.
     */
    public static final int MSG_TYPE_MOTD = 7;

    /**
     * drawextinfo message type: general server message.
     */
    public static final int MSG_TYPE_ADMIN = 8;

    /**
     * drawextinfo message type: shop related message.
     */
    public static final int MSG_TYPE_SHOP = 9;

    /**
     * drawextinfo message type: response to command processing.
     */
    public static final int MSG_TYPE_COMMAND = 10;

    /**
     * drawextinfo message type: attribute (stats, resistances, etc.) change
     * message.
     */
    public static final int MSG_TYPE_ATTRIBUTE = 11;

    /**
     * drawextinfo message type: message related to using skills.
     */
    public static final int MSG_TYPE_SKILL = 12;

    /**
     * drawextinfo message type: an object was applied.
     */
    public static final int MSG_TYPE_APPLY = 13;

    /**
     * drawextinfo message type: attack related message.
     */
    public static final int MSG_TYPE_ATTACK = 14;

    /**
     * drawextinfo message type: communication between players.
     */
    public static final int MSG_TYPE_COMMUNICATION = 15;

    /**
     * drawextinfo message type: spell related information.
     */
    public static final int MSG_TYPE_SPELL = 16;

    /**
     * drawextinfo message type: item related information.
     */
    public static final int MSG_TYPE_ITEM = 17;

    /**
     * drawextinfo message type: message that does not fit in any other
     * category.
     */
    public static final int MSG_TYPE_MISC = 18;

    /**
     * drawextinfo message type: something bad is happening to the player.
     */
    public static final int MSG_TYPE_VICTIM = 19;

    public static final int MSG_TYPE_BOOK_CLASP_1 = 1;

    public static final int MSG_TYPE_BOOK_CLASP_2 = 2;

    public static final int MSG_TYPE_BOOK_ELEGANT_1 = 3;

    public static final int MSG_TYPE_BOOK_ELEGANT_2 = 4;

    public static final int MSG_TYPE_BOOK_QUARTO_1 = 5;

    public static final int MSG_TYPE_BOOK_QUARTO_2 = 6;

    public static final int MSG_TYPE_BOOK_SPELL_EVOKER = 8;

    public static final int MSG_TYPE_BOOK_SPELL_PRAYER = 9;

    public static final int MSG_TYPE_BOOK_SPELL_PYRO = 10;

    public static final int MSG_TYPE_BOOK_SPELL_SORCERER = 11;

    public static final int MSG_TYPE_BOOK_SPELL_SUMMONER = 12;

    public static final int MSG_TYPE_COMMUNICATION_RANDOM = 1; // random event (coin toss)

    public static final int MSG_TYPE_COMMUNICATION_SAY = 2; // player says something

    public static final int MSG_TYPE_COMMUNICATION_ME = 3; // player me's a message

    public static final int MSG_TYPE_COMMUNICATION_TELL = 4; // player tells something

    public static final int MSG_TYPE_COMMUNICATION_EMOTE = 5; // player emotes

    public static final int MSG_TYPE_COMMUNICATION_PARTY = 6; // party message

    public static final int MSG_TYPE_COMMUNICATION_SHOUT = 7; // shout message

    public static final int MSG_TYPE_COMMUNICATION_CHAT = 8; // chat message

    /**
     * Private constructor to prevent instantiation.
     */
    private MessageTypes() {
    }

    /**
     * Returns all defined message types.
     * @return all defined message types
     */
    @NotNull
    public static int[] getAllTypes() {
        return new int[] {
            MSG_TYPE_BOOK,
            MSG_TYPE_CARD,
            MSG_TYPE_PAPER,
            MSG_TYPE_SIGN,
            MSG_TYPE_MONUMENT,
            MSG_TYPE_DIALOG,
            MSG_TYPE_MOTD,
            MSG_TYPE_ADMIN,
            MSG_TYPE_SHOP,
            MSG_TYPE_COMMAND,
            MSG_TYPE_ATTRIBUTE,
            MSG_TYPE_SKILL,
            MSG_TYPE_APPLY,
            MSG_TYPE_ATTACK,
            MSG_TYPE_COMMUNICATION,
            MSG_TYPE_SPELL,
            MSG_TYPE_ITEM,
            MSG_TYPE_MISC,
            MSG_TYPE_VICTIM,
        };
    }

    /**
     * Parses a string into a message type.
     * @param str the string to parse
     * @return the message type
     * @throws UnknownMessageTypeException if the string is unknown
     */
    public static int parseMessageType(@NotNull final String str) throws UnknownMessageTypeException {
        switch (str) {
        case "BOOK":
            return MSG_TYPE_BOOK;

        case "CARD":
            return MSG_TYPE_CARD;

        case "PAPER":
            return MSG_TYPE_PAPER;

        case "SIGN":
            return MSG_TYPE_SIGN;

        case "MONUMENT":
            return MSG_TYPE_MONUMENT;

        case "DIALOG":
            return MSG_TYPE_DIALOG;

        case "MOTD":
            return MSG_TYPE_MOTD;

        case "ADMIN":
            return MSG_TYPE_ADMIN;

        case "SHOP":
            return MSG_TYPE_SHOP;

        case "COMMAND":
            return MSG_TYPE_COMMAND;

        case "ATTRIBUTE":
            return MSG_TYPE_ATTRIBUTE;

        case "SKILL":
            return MSG_TYPE_SKILL;

        case "APPLY":
            return MSG_TYPE_APPLY;

        case "ATTACK":
            return MSG_TYPE_ATTACK;

        case "COMMUNICATION":
            return MSG_TYPE_COMMUNICATION;

        case "SPELL":
            return MSG_TYPE_SPELL;

        case "ITEM":
            return MSG_TYPE_ITEM;

        case "MISC":
            return MSG_TYPE_MISC;

        case "VICTIM":
            return MSG_TYPE_VICTIM;

        case "QUERY":
            return MSG_TYPE_QUERY;
        }

        throw new UnknownMessageTypeException(str);
    }

    /**
     * Returns a string representation of a message type.
     * @param type the message type
     * @return the string representation
     */
    @NotNull
    public static String toString(final int type) {
        switch (type) {
        case MSG_TYPE_BOOK:
            return "BOOK";

        case MSG_TYPE_CARD:
            return "CARD";

        case MSG_TYPE_PAPER:
            return "PAPER";

        case MSG_TYPE_SIGN:
            return "SIGN";

        case MSG_TYPE_MONUMENT:
            return "MONUMENT";

        case MSG_TYPE_DIALOG:
            return "DIALOG";

        case MSG_TYPE_MOTD:
            return "MOTD";

        case MSG_TYPE_ADMIN:
            return "ADMIN";

        case MSG_TYPE_SHOP:
            return "SHOP";

        case MSG_TYPE_COMMAND:
            return "COMMAND";

        case MSG_TYPE_ATTRIBUTE:
            return "ATTRIBUTE";

        case MSG_TYPE_SKILL:
            return "SKILL";

        case MSG_TYPE_APPLY:
            return "APPLY";

        case MSG_TYPE_ATTACK:
            return "ATTACK";

        case MSG_TYPE_COMMUNICATION:
            return "COMMUNICATION";

        case MSG_TYPE_SPELL:
            return "SPELL";

        case MSG_TYPE_ITEM:
            return "ITEM";

        case MSG_TYPE_MISC:
            return "MISC";

        case MSG_TYPE_VICTIM:
            return "VICTIM";
        }

        return Integer.toString(type);
    }

}
